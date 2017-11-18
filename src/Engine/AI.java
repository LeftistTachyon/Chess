package Engine;

import static Engine.PieceConverter.PIECE_CONVERTER;
import GUI.Chess;
import Util.ChessConstants;
import Util.Constants;
import static Util.Constants.NEGATIVE_INFINITY;
import static Util.Constants.POSITIVE_INFINITY;
import Util.ImageReader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * NOTE: ENPASSANT IS DISABLED
 * I may change my logic to only consider search results only if they all finish
 * a particular depth. If some searches at a depth do not finish, they WILL
 * adversely affect the selection of moves from the AI, thus this cannot be
 * countered by the argument that such deeper searches yield more data. This
 * scenario pops up often after using parallel processing.
 *
 * Bug, Non-parallel processing with depth extension (aka default mode) having
 * trouble finding checkmate, most likely located in this class, as parallel
 * processing does it just fine.
 *
 * This bug seems to be resolved as of 4/30/17 by just sorting the possible
 * positions and selecting the one at the top.
 *
 * Could make parallel search better by conducting a single threaded search to
 * depth 3, then sorting and starting parallel searching. However, there may be
 * no need since each position is searched at the same time.
 * 
 * Although making sure all results are of the same depth is critical,
 * if a branch detects a checkmate, we should return that branch immediately
 */
public final class AI {

    //reference to main board in gui
    //static Board GUI_BOARD;
    //used for parallel processing
    private static final LinkedBlockingDeque<Runnable> DEQUE = new LinkedBlockingDeque<Runnable>() {

        private static final long serialVersionUID = -6903933921423432194L;

        @Override
        public final boolean offer(Runnable runnable) {
            //http://stackoverflow.com/questions/19528304/how-to-get-the-threadpoolexecutor-to-increase-threads-to-max-before-queueing?rq=1
            return (size() <= 1) ? super.offer(runnable) : false;
        }
    };

    //stores positions and their values, in both white and black's persepctive.
    private static final Database STORE = new Database();

    //test mode flag
    private static final boolean CHECK_MODE = true;

    //non-private for Pieces and Grid to allocate ArrayList memory
    static int NUMBER_OF_WHITE_PIECES;
    static int NUMBER_OF_BLACK_PIECES;
    static int NUMBER_OF_PIECES;

    //difficulty
    public static final String[] DIFFICULTY = {"Novice", "Student", "Good", "Strong", "Master", "Grand Master"};
    public static final int MIN_DIFFICULTY = 0;
    public static final int MAX_DIFFICULTY = DIFFICULTY.length - 1;

    //maximum time allocated (in seconds) to search per difficulty level
    public static final int[] SEARCH_TIMES = {2, 5, 10, 30, 45, 60};

    //how far to search, depending on the difficulty
    private static final int[] SEARCH_DEPTHS = {1, 2, 3, 4, POSITIVE_INFINITY, POSITIVE_INFINITY};

    //test depth used for check mode
    private static final int TEST_DEPTH = 1;

    //images corresponding to the AI difficulty
    private static final BufferedImage[] AI_IMAGES = new BufferedImage[DIFFICULTY.length];

    //load images in static initalizer, invoked by classloader
    static {
        for (int index = MIN_DIFFICULTY; index <= MAX_DIFFICULTY; ++index) {
            AI_IMAGES[index] = ImageReader.readImage(DIFFICULTY[index], ImageReader.PNG);
        }
    }

    //current AI image rendering in the status dialog
    //updated per call to makeMove
    private static BufferedImage IMAGE;

    //global search timer used by the status dialog
    //and by static search classes for timing
    //updated per call to makeMove
    static SearchTimer TIMER;

    //used when CHECK_MODE = true
    private static final SearchTimer TEST_TIMER = new SearchTimer(POSITIVE_INFINITY, "Test Timer");

    //show AI status data to user
    static StatusDialog DIALOG;

    //only 2 instances allowed
    private static final AI WHITE_AI = new AI(true, SEARCH_TIMES[0]);
    private static final AI BLACK_AI = new AI(false, SEARCH_TIMES[0]);

    public static final AI getWhiteAI() {
        return WHITE_AI;
    }

    public static final AI getBlackAI() {
        return BLACK_AI;
    }

    //AI image of the current AI
    private BufferedImage image;

    //search timer of the current AI
    private final SearchTimer timer;

    //color, true indicates white and false indicates black
    private final boolean side;

    //input chess board and pieces
    private final Grid grid = new Grid();
    private final List<Piece> pieces = new ArrayList<>(ChessConstants.MAX_NUMBER_OF_PIECES);

    //possible positions to select from
    private final List<PositionHolder> possiblePositions = new ArrayList<>(ChessConstants.INITAL_NUMBER_OF_POSITIONS);

    //table of chosen positions and the number of times each position was chosen
    //used to avoid threefold repetition
    private final Map<String, Integer> selectedPositions = new HashMap<>();

    //functionality to consider results from incomplete searches.
    private boolean depthLimited = true;

    //parallel processing capability, disabled by default
    private boolean parallel = false;

    private int maxDepth;

    AI(boolean color, int searchTime) {
        for (int index = MIN_DIFFICULTY; index <= MAX_DIFFICULTY; ++index) {
            if (searchTime == SEARCH_TIMES[index]) {
                timer = new SearchTimer(searchTime, (side = color) ? "White AI Timer" : "Black AI Timer");
                image = AI_IMAGES[index];
                maxDepth = SEARCH_DEPTHS[index];
                return;
            }
        }
        throw new IllegalArgumentException("Invalid Search Time: " + searchTime);
    }

    public SearchTimer getTimer() {
        return timer;
    }

    public static final void resetInfoDialog() {
        if (DIALOG != null) {
            DIALOG.setVisible(false);
        }
        //whenever a game is being restarted while an AI is actively
        //searching, the following code will terminate the AI search instantly
        //since this method is called every time the user restarts
        WHITE_AI.timer.stopTiming();
        BLACK_AI.timer.stopTiming();
    }

    public void setSearchTime(int searchTime) {
        for (int index = MIN_DIFFICULTY; index <= MAX_DIFFICULTY; ++index) {
            if (searchTime == SEARCH_TIMES[index]) {
                timer.setCountdownTime(searchTime);
                image = AI_IMAGES[index];
                maxDepth = SEARCH_DEPTHS[index];
                return;
            }
        }
        throw new IllegalArgumentException("Invalid Search Time: " + searchTime);
    }

    public int getSearchTime() {
        return timer.getCountdownTime();
    }

    public String getDifficulty() {
        int searchTime = timer.getCountdownTime();
        for (int index = MIN_DIFFICULTY; index <= MAX_DIFFICULTY; ++index) {
            if (searchTime == SEARCH_TIMES[index]) {
                return DIFFICULTY[index];
            }
        }
        throw new IllegalStateException();
    }

    public void setSelectedPositions(Map<String, Integer> savedSelectedPositions) {
        selectedPositions.clear();
        selectedPositions.putAll(savedSelectedPositions);
    }

    public Map<String, Integer> getSelectedPositions() {
        return selectedPositions;
    }

    public boolean isParallelProcessing() {
        return parallel;
    }

    public void useParallelProcessing(boolean enable) {
        parallel = enable;
    }

    private static final class PositionHolder implements Comparable<PositionHolder> {

        private final Grid grid;
        private final List<Piece> whites;
        private final List<Piece> blacks;

        private final Grid clonedGrid;
        private final List<Piece> clonedWhites;
        private final List<Piece> clonedBlacks;

        private final String description;
        private int value = NEGATIVE_INFINITY;

        private PositionHolder(Grid copiedGrid, String moveInfo) {
            List<Piece> pieces = (grid = copiedGrid).getPieces();
            Pieces.sort(pieces);
            clonedGrid = new Grid(grid);
            clonedWhites = Pieces.getDeepCopy(whites = Pieces.getWhite(pieces));
            clonedBlacks = Pieces.getDeepCopy(blacks = Pieces.getBlack(pieces));
            description = moveInfo;
        }

        @Override
        public final int compareTo(PositionHolder other) {
            return Integer.compare(value, other.value);
        }
    }

    @SuppressWarnings("Convert2Lambda")
    private static final Comparator<PositionHolder> HIGHEST_VALUE_POSITION_FIRST = new Comparator<PositionHolder>() {
        @Override
        public final int compare(PositionHolder first, PositionHolder next) {
            return next.compareTo(first);
        }
    };

    private static final Comparator<PositionHolder> HIGHEST_VALUE_POSITION_LAST = HIGHEST_VALUE_POSITION_FIRST.reversed();

    private final class ParallelSearch implements Callable<Integer> {

        private final PositionHolder position;
        private final List<Integer> results = new ArrayList<>(6);

        private ParallelSearch(PositionHolder position) {
            this.position = position;
        }

        @Override
        public final Integer call() {
            if (side) {
                int value = AlphaBetaWhite.min(position.grid, position.whites, position.blacks, 1, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                results.add(value);
                for (int currentDepth = 2; currentDepth <= maxDepth; ++currentDepth) {
                    int result = AlphaBetaWhite.min(position.grid, position.whites, position.blacks, currentDepth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                    if (timer.timeOver()) {
                        return value;
                    }
                    results.add(value = result);
                }
                return value;
            }
            else {
                int value = AlphaBetaBlack.min(position.grid, position.whites, position.blacks, 1, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                results.add(value);
                for (int currentDepth = 2; currentDepth <= maxDepth; ++currentDepth) {
                    int result = AlphaBetaBlack.min(position.grid, position.whites, position.blacks, currentDepth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                    if (timer.timeOver()) {
                        return value;
                    }
                    results.add(value = result);
                }
                return value;
            }
        }
    }

    @SuppressWarnings({"Convert2Lambda", "UnusedAssignment"})
    public synchronized void makeMove(List<String> list) {
        IMAGE = image;
        if (DIALOG == null) {
            //Chess chess = Chess.getInstance();
            //GUI_BOARD = chess.getChild();
            //DIALOG = new StatusDialog(chess);
            DIALOG = new StatusDialog(Chess.getInstance());
            //must init dialog here to avoid circular initalization in chess -> board -> computer -> chess
        }
        if (parallel) {
            DIALOG.setTitle(side ? "White AI - (Parallel)" : "Black AI - (Parallel)");
        }
        else {
            DIALOG.setTitle(side ? "White AI - (Normal)" : "Black AI - (Normal)");
        }

        NUMBER_OF_WHITE_PIECES = NUMBER_OF_BLACK_PIECES = 0;
        NUMBER_OF_PIECES = list.size();

        //read pieces
        for (int index = 0; index != NUMBER_OF_PIECES; ++index) {
            Piece piece = PIECE_CONVERTER.convertForward(list.get(index));
            if (piece.isWhite()) {
                ++NUMBER_OF_WHITE_PIECES;
                Pieces.WHITES.add(piece);
            }
            else {
                ++NUMBER_OF_BLACK_PIECES;
                Pieces.BLACKS.add(piece);
            }
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }

        Pieces.WHITES.sort(Pieces.BEST_PIECES_FIRST);
        Pieces.BLACKS.sort(Pieces.BEST_PIECES_LAST);
        for (int index = 0; index != NUMBER_OF_WHITE_PIECES; ++index) {
            Piece white = Pieces.WHITES.get(index);
            white.setProtectedTiles(grid);
            pieces.add(white);
        }
        for (int index = 0; index != NUMBER_OF_BLACK_PIECES; ++index) {
            Piece black = Pieces.BLACKS.get(index);
            black.setProtectedTiles(grid);
            pieces.add(black);
        }
        Pieces.WHITES.clear();
        Pieces.BLACKS.clear();

        //For Debugging:
        final List<Piece> clonedPieces = Pieces.getDeepCopy(pieces);
        final Grid clonedGrid = new Grid(grid);
        
        Tester.value(grid);

        if (side) {
            System.out.println("White AI Playing");
            {
                final List<Piece> whites = Pieces.getWhite(pieces);
                final King whiteKing = Pieces.getWhiteKing(whites);
                {
                    Tile previousTile = grid.getTile(whiteKing.getRow(), whiteKing.getColumn());
                    List<Tile> castleTiles = whiteKing.getCastleTiles(grid);
                    for (int index = (castleTiles.size() - 1); index >= 0; --index) {
                        Tile kingCastleTile = castleTiles.get(index);
                        if (kingCastleTile.getColumn() == ChessConstants.LEFT_KING_CASTLE_COLUMN) {
                            Tile leftRookTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, 0);
                            Tile leftRookCastleTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, ChessConstants.LEFT_ROOK_CASTLE_COLUMN);
                            Piece leftRook = leftRookTile.getOccupant();

                            previousTile.removeOccupant();
                            leftRookTile.removeOccupant();
                            kingCastleTile.setOccupant(whiteKing);
                            leftRookCastleTile.setOccupant(leftRook);
                            grid.setProtections(pieces);

                            whiteKing.increaseMoveCount();
                            leftRook.increaseMoveCount();
                            possiblePositions.add(new PositionHolder(new Grid(grid), "White King castles left from " + previousTile.getNotationLocation() + " to " + whiteKing.getNotationLocation() + " and the White Rook at [1,A] has moved to [1,D]."));
                            whiteKing.decreaseMoveCount();
                            leftRook.decreaseMoveCount();

                            previousTile.setOccupant(whiteKing);
                            leftRookTile.setOccupant(leftRook);
                            kingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        else {
                            Tile rightRookTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, 7);
                            Tile rightRookCastleTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, ChessConstants.RIGHT_ROOK_CASTLE_COLUMN);
                            Piece rightRook = rightRookTile.getOccupant();

                            previousTile.removeOccupant();
                            rightRookTile.removeOccupant();
                            kingCastleTile.setOccupant(whiteKing);
                            rightRookCastleTile.setOccupant(rightRook);
                            grid.setProtections(pieces);

                            whiteKing.increaseMoveCount();
                            rightRook.increaseMoveCount();
                            possiblePositions.add(new PositionHolder(new Grid(grid), "White King castles right from " + previousTile.getNotationLocation() + " to " + whiteKing.getNotationLocation() + " and the White Rook at [1,H] has moved to [1,F]."));
                            whiteKing.decreaseMoveCount();
                            rightRook.decreaseMoveCount();

                            previousTile.setOccupant(whiteKing);
                            rightRookTile.setOccupant(rightRook);
                            kingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }

                for (int pieceIndex = 0; pieceIndex != NUMBER_OF_WHITE_PIECES; ++pieceIndex) {
                    final Piece white = whites.get(pieceIndex);
                    final int previousRow = white.getRow();
                    final int previousColumn = white.getColumn();
                    final Tile previousTile = grid.getTile(previousRow, previousColumn);
                    List<Tile> attackTiles = white.getAttackTiles(grid);
                    for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                        Tile attackTile = attackTiles.get(index);
                        Piece enemy = attackTile.getOccupant();
                        previousTile.removeOccupant();
                        if (white.isPawn() && previousRow == 1) {
                            Queen replace = Pawn.promote(white);
                            attackTile.setOccupant(replace);
                            int pawnIndex = pieces.indexOf(white);
                            pieces.set(pawnIndex, replace);
                            int removeIndex = Pieces.remove(pieces, enemy);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                replace.increaseMoveCount();
                                possiblePositions.add(new PositionHolder(new Grid(grid), "White Pawn at " + previousTile.getNotationLocation() + " has captured " + enemy.getName() + " at " + replace.getNotationLocation() + " and has been promoted to a White Queen."));
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            pieces.add(removeIndex, enemy);
                            pieces.set(pawnIndex, white);
                            grid.setProtections(pieces);
                        }
                        else {
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(pieces, enemy);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                possiblePositions.add(new PositionHolder(new Grid(grid), white.getName() + " at " + previousTile.getNotationLocation() + " has captured " + enemy.getName() + " at " + white.getNotationLocation()));
                                white.decreaseMoveCount();
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            pieces.add(removeIndex, enemy);
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                    /*
                    if (white.isPawn()) {
                        Pawn pawn = (Pawn) white;
                        {
                            Tile enPassantTile = pawn.getLeftEnPassantTile(grid);
                            if (enPassantTile != null) {
                                Tile blackPawnTile = grid.getTile(previousRow, previousColumn - 1);
                                Piece blackPawn = blackPawnTile.getOccupant();
                                previousTile.removeOccupant();
                                blackPawnTile.removeOccupant();
                                enPassantTile.setOccupant(white);
                                int removeIndex = Pieces.remove(pieces, blackPawn);
                                grid.setProtections(pieces);
                                if (!whiteKing.inCheck(grid)) {
                                    white.increaseMoveCount();
                                    possiblePositions.add(new PositionHolder(new Grid(grid), "White Pawn at " + previousTile.getNotationLocation() + " performed enpassant capture on the Black Pawn at " + blackPawn.getNotationLocation() + " and has moved from " + previousTile.getNotationLocation() + " to " + white.getNotationLocation()));
                                    white.decreaseMoveCount();
                                }
                                previousTile.setOccupant(white);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, blackPawn);
                                grid.setProtections(pieces);
                            }
                        }
                        {
                            Tile enPassantTile = pawn.getRightEnPassantTile(grid);
                            if (enPassantTile != null) {
                                Tile blackPawnTile = grid.getTile(previousRow, previousColumn + 1);
                                Piece blackPawn = blackPawnTile.getOccupant();
                                previousTile.removeOccupant();
                                blackPawnTile.removeOccupant();
                                enPassantTile.setOccupant(white);
                                int removeIndex = Pieces.remove(pieces, blackPawn);
                                grid.setProtections(pieces);
                                if (!whiteKing.inCheck(grid)) {
                                    white.increaseMoveCount();
                                    possiblePositions.add(new PositionHolder(new Grid(grid), "White Pawn at " + previousTile.getNotationLocation() + " performed enpassant capture on the Black Pawn at " + blackPawn.getNotationLocation() + " and has moved from " + previousTile.getNotationLocation() + " to " + white.getNotationLocation()));
                                    white.decreaseMoveCount();
                                }
                                previousTile.setOccupant(white);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, blackPawn);
                                grid.setProtections(pieces);
                            }
                        }
                    }
                     */
                }

                for (int pieceIndex = 0; pieceIndex != NUMBER_OF_WHITE_PIECES; ++pieceIndex) {
                    final Piece white = whites.get(pieceIndex);
                    final int previousRow = white.getRow();
                    final int previousColumn = white.getColumn();
                    final Tile previousTile = grid.getTile(previousRow, previousColumn);
                    List<Tile> moveTiles = white.getMoveTiles(grid);
                    for (int index = (moveTiles.size() - 1); index >= 0; --index) {
                        Tile moveTile = moveTiles.get(index);
                        previousTile.removeOccupant();
                        if (white.isPawn() && previousRow == 1) {
                            Queen replace = Pawn.promote(white);
                            moveTile.setOccupant(replace);
                            int pawnIndex = pieces.indexOf(white);
                            pieces.set(pawnIndex, replace);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                replace.increaseMoveCount();
                                possiblePositions.add(new PositionHolder(new Grid(grid), "White Pawn at " + previousTile.getNotationLocation() + " has moved to " + replace.getNotationLocation() + " and has been promoted to a White Queen."));
                            }
                            previousTile.setOccupant(white);
                            moveTile.removeOccupant();
                            pieces.set(pawnIndex, white);
                            grid.setProtections(pieces);
                        }
                        else {
                            moveTile.setOccupant(white);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                possiblePositions.add(new PositionHolder(new Grid(grid), white.getName() + " at " + previousTile.getNotationLocation() + " has moved to " + white.getNotationLocation()));
                                white.decreaseMoveCount();
                            }
                            previousTile.setOccupant(white);
                            moveTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }
                Tester.check(grid, clonedGrid, pieces, clonedPieces);
            }

            //show info dialog
            DIALOG.setVisible(true);

            //start timing
            (TIMER = timer).startTiming();

            list.clear();

            final int numberOfPositions = possiblePositions.size();

            if (numberOfPositions != Tester.perft(grid, 1, !side)) {
                throw new InternalError();
            }

            if (numberOfPositions == 0) {
                DIALOG.disableUpdate();
                DIALOG.setTitle(side ? "White AI - (Done)" : "Black AI - (Done)");
                timer.stopTiming();

                //clear previous grids and pieces
                grid.clear();
                pieces.clear();
                return;
            }
            
            //update the dialog if not game over
            DIALOG.reset();

            PositionHolder bestPosition = possiblePositions.get(0);

            if (parallel) {
                ParallelSearch[] parallelSearches = new ParallelSearch[numberOfPositions];
                Future[] futures = new Future[numberOfPositions];
                ThreadPoolExecutor executor = new ThreadPoolExecutor(numberOfPositions,
                        numberOfPositions, 0L, TimeUnit.MILLISECONDS, DEQUE);
                for (int index = 0; index != numberOfPositions; ++index) {
                    futures[index] = executor.submit(parallelSearches[index] = new ParallelSearch(possiblePositions.get(index)));
                    //start all parallel tasks at the same time
                }
                executor.shutdown();
                try {
                    for (int index = 0; index != numberOfPositions; ++index) {
                        futures[index].get(); //wait for all parallel tasks to finish.
                        futures[index] = null;
                    }
                }
                catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
                int shallowestDepth = parallelSearches[0].results.size();
                for (int index = 1; index != numberOfPositions; ++index) {
                    int depthsCovered = parallelSearches[index].results.size();
                    if (depthsCovered < shallowestDepth) {
                        shallowestDepth = depthsCovered;
                    }
                }
                DIALOG.setFinishedDepth(shallowestDepth); //this is the size of the list, we need the last index
                if (depthLimited) {
                    DIALOG.setMaxPositionValue(bestPosition.value = parallelSearches[0].results.get(--shallowestDepth));
                    parallelSearches[0] = null;
                    for (int index = 1; index != numberOfPositions; ++index) {
                        PositionHolder currentPosition = possiblePositions.get(index);
                        if ((currentPosition.value = parallelSearches[index].results.get(shallowestDepth)) > bestPosition.value) {
                            DIALOG.setMaxPositionValue((bestPosition = currentPosition).value);
                        }
                        parallelSearches[index] = null;
                    }
                }
                else {
                    DIALOG.setMaxPositionValue(bestPosition.value = parallelSearches[0].results.get(parallelSearches[0].results.size() - 1));
                    parallelSearches[0] = null;
                    for (int index = 1; index != numberOfPositions; ++index) {
                        PositionHolder currentPosition = possiblePositions.get(index);
                        if ((currentPosition.value = parallelSearches[index].results.get(parallelSearches[index].results.size() - 1)) > bestPosition.value) {
                            DIALOG.setMaxPositionValue((bestPosition = currentPosition).value);
                        }
                        parallelSearches[index] = null;
                    }
                }
                parallelSearches = null;
                futures = null;
                executor = null;
                DEQUE.clear();
            }
            else if (depthLimited) {
                int previousIterationTime = NEGATIVE_INFINITY;
                //array of previous values
                int[] previousValues = new int[numberOfPositions];
                Iterative_Deepening:
                for (int searchDepth = 1; searchDepth <= maxDepth; ++searchDepth) {
                    //before attempting to search at this particular depth, check time 
                    final int depthStartTime = timer.timeElapsed();
                    if (previousIterationTime != NEGATIVE_INFINITY && (timer.getCountdownTime() - depthStartTime) <= previousIterationTime) {
                        //it is pretty much impossible to search to a greater depth
                        //in the remaining amount of time so terminate search
                        break;
                    }
                    for (int index = 0; index != numberOfPositions; ++index) {
                        previousValues[index] = possiblePositions.get(index).value;
                    }
                    for (int positionIndex = 0; positionIndex != numberOfPositions; ++positionIndex) {
                        PositionHolder position = possiblePositions.get(positionIndex);
                        int result = AlphaBetaWhite.min(position.grid, position.whites, position.blacks, searchDepth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                        if (timer.timeOver()) {
                            //should time run out before all searches are completed
                            //at the current depth, reset all positional values to
                            //the previous depth iteration
                            for (int index = 0; index != numberOfPositions; ++index) {
                                possiblePositions.get(index).value = previousValues[index];
                            }
                            break Iterative_Deepening;
                        }
                        position.value = result;
                    }
                    //at this point the current depth has been fully searched
                    //so now we sort the higher scoring positions to look at first
                    //and find the best position, which is the first after sorting
                    possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                    DIALOG.setMaxPositionValue((bestPosition = possiblePositions.get(0)).value);
                    DIALOG.setFinishedDepth(searchDepth);
                    previousIterationTime = timer.timeElapsed() - depthStartTime;
                    System.out.println("Depth: " + searchDepth + " Time Taken: " + previousIterationTime);
                }
                System.out.println();
                previousValues = null;
            }
            else {
                Iterative_Deepening:
                for (int searchDepth = 1; searchDepth <= maxDepth; ++searchDepth) {
                    for (int index = 0; index != numberOfPositions; ++index) {
                        PositionHolder position = possiblePositions.get(index);
                        int result = AlphaBetaWhite.min(position.grid, position.whites, position.blacks, searchDepth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                        if (timer.timeOver()) {
                            break Iterative_Deepening;
                        }
                        position.value = result;
                    }
                    possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                    DIALOG.setMaxPositionValue(possiblePositions.get(0).value);
                    DIALOG.setFinishedDepth(searchDepth);
                }
                possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                DIALOG.setMaxPositionValue((bestPosition = possiblePositions.get(0)).value);
            }

            {
                //maybe search for a better position only when losing
                final String encoded = Pieces.encode(bestPosition.whites, bestPosition.blacks);
                if (!selectedPositions.containsKey(encoded)) {
                    selectedPositions.put(encoded, 1);
                }
                else {
                    final int timesSelected = selectedPositions.get(encoded);
                    if (timesSelected >= 2) {
                        if (numberOfPositions >= 2) {
                            possiblePositions.sort(HIGHEST_VALUE_POSITION_LAST);
                            boolean successorFound = false;
                            for (int index = (numberOfPositions - 2); index >= 0; --index) {
                                PositionHolder nextBestPosition = possiblePositions.get(index);
                                String nextBestPositionEncoded = Pieces.encode(nextBestPosition.whites, nextBestPosition.blacks);
                                if (!selectedPositions.containsKey(nextBestPositionEncoded)) {
                                    bestPosition = nextBestPosition;
                                    selectedPositions.put(nextBestPositionEncoded, 1);
                                    successorFound = true;
                                    break;
                                }
                                if (selectedPositions.get(nextBestPositionEncoded) == 1) {
                                    bestPosition = nextBestPosition;
                                    selectedPositions.put(nextBestPositionEncoded, 2);
                                    successorFound = true;
                                    break;
                                }
                            }
                            if (!successorFound) {
                                selectedPositions.put(encoded, timesSelected + 1);
                            }
                        }
                        else {
                            selectedPositions.put(encoded, timesSelected + 1);
                        }
                    }
                    else {
                        selectedPositions.put(encoded, timesSelected + 1);
                    }
                }
            }

            if (CHECK_MODE) {
                (TIMER = TEST_TIMER).startTiming();
                Tester.checkProtections(pieces, side);
                for (int index = 0; index != numberOfPositions; ++index) {
                    PositionHolder current = possiblePositions.get(index);
                    int alphaBeta = AlphaBetaWhite.min(current.grid, current.whites, current.blacks, TEST_DEPTH, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                    int minMax = MinMaxWhite.min(current.grid, current.whites, current.blacks, TEST_DEPTH);
                    if (alphaBeta != minMax) {
                        System.out.println("Algorithm Mismatch");
                        System.out.println("AlphaBeta: " + alphaBeta);
                        System.out.println("MinMax: " + minMax);
                    }
                    Tester.check(current.grid, current.clonedGrid, current.whites, current.clonedWhites);
                    Tester.check(current.grid, current.clonedGrid, current.blacks, current.clonedBlacks);
                }
            }

            list.add(bestPosition.description);
            for (int index = 0, size = bestPosition.whites.size(); index != size; ++index) {
                list.add(PIECE_CONVERTER.convertBackward(bestPosition.whites.get(index)));
            }
            for (int index = 0, size = bestPosition.blacks.size(); index != size; ++index) {
                list.add(PIECE_CONVERTER.convertBackward(bestPosition.blacks.get(index)));
            }
        }
        else {
            System.out.println("Black AI Playing");
            {
                final List<Piece> blacks = Pieces.getBlack(pieces);
                final King blackKing = Pieces.getBlackKing(blacks);
                {
                    Tile previousTile = grid.getTile(blackKing.getRow(), blackKing.getColumn());
                    List<Tile> castleTiles = blackKing.getCastleTiles(grid);
                    for (int index = (castleTiles.size() - 1); index >= 0; --index) {
                        Tile kingCastleTile = castleTiles.get(index);
                        if (kingCastleTile.getColumn() == ChessConstants.LEFT_KING_CASTLE_COLUMN) {
                            Tile leftRookTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, 0);
                            Tile leftRookCastleTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, ChessConstants.LEFT_ROOK_CASTLE_COLUMN);
                            Piece leftRook = leftRookTile.getOccupant();

                            previousTile.removeOccupant();
                            leftRookTile.removeOccupant();
                            kingCastleTile.setOccupant(blackKing);
                            leftRookCastleTile.setOccupant(leftRook);
                            grid.setProtections(pieces);

                            blackKing.increaseMoveCount();
                            leftRook.increaseMoveCount();
                            possiblePositions.add(new PositionHolder(new Grid(grid), "Black King castles left from " + previousTile.getNotationLocation() + " to " + blackKing.getNotationLocation() + " and the Black Rook at [8,A] has moved to [8,D]."));
                            blackKing.decreaseMoveCount();
                            leftRook.decreaseMoveCount();

                            previousTile.setOccupant(blackKing);
                            leftRookTile.setOccupant(leftRook);
                            kingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        else {
                            Tile rightRookTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, 7);
                            Tile rightRookCastleTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, ChessConstants.RIGHT_ROOK_CASTLE_COLUMN);
                            Piece rightRook = rightRookTile.getOccupant();

                            previousTile.removeOccupant();
                            rightRookTile.removeOccupant();
                            kingCastleTile.setOccupant(blackKing);
                            rightRookCastleTile.setOccupant(rightRook);
                            grid.setProtections(pieces);

                            blackKing.increaseMoveCount();
                            rightRook.increaseMoveCount();
                            possiblePositions.add(new PositionHolder(new Grid(grid), "Black King castles right from " + previousTile.getNotationLocation() + " to " + blackKing.getNotationLocation() + " and the Black Rook at [8,H] has moved to [8,F]."));
                            blackKing.decreaseMoveCount();
                            rightRook.decreaseMoveCount();

                            previousTile.setOccupant(blackKing);
                            rightRookTile.setOccupant(rightRook);
                            kingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }

                for (int pieceIndex = 0; pieceIndex != NUMBER_OF_BLACK_PIECES; ++pieceIndex) {
                    final Piece black = blacks.get(pieceIndex);
                    final int previousRow = black.getRow();
                    final int previousColumn = black.getColumn();
                    final Tile previousTile = grid.getTile(previousRow, previousColumn);
                    List<Tile> attackTiles = black.getAttackTiles(grid);
                    for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                        Tile attackTile = attackTiles.get(index);
                        Piece enemy = attackTile.getOccupant();
                        previousTile.removeOccupant();
                        if (black.isPawn() && previousRow == 6) {
                            Queen replace = Pawn.promote(black);
                            attackTile.setOccupant(replace);
                            int pawnIndex = pieces.indexOf(black);
                            pieces.set(pawnIndex, replace);
                            int removeIndex = Pieces.remove(pieces, enemy);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                replace.increaseMoveCount();
                                possiblePositions.add(new PositionHolder(new Grid(grid), "Black Pawn at " + previousTile.getNotationLocation() + " has captured " + enemy.getName() + " at " + replace.getNotationLocation() + " and has been promoted to a Black Queen."));
                            }
                            previousTile.setOccupant(black);
                            attackTile.setOccupant(enemy);
                            pieces.add(removeIndex, enemy);
                            pieces.set(pawnIndex, black);
                            grid.setProtections(pieces);
                        }
                        else {
                            attackTile.setOccupant(black);
                            int removeIndex = Pieces.remove(pieces, enemy);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                black.increaseMoveCount();
                                possiblePositions.add(new PositionHolder(new Grid(grid), black.getName() + " at " + previousTile.getNotationLocation() + " has captured " + enemy.getName() + " at " + black.getNotationLocation()));
                                black.decreaseMoveCount();
                            }
                            previousTile.setOccupant(black);
                            attackTile.setOccupant(enemy);
                            pieces.add(removeIndex, enemy);
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                    /*
                    if (black.isPawn()) {
                        Pawn pawn = (Pawn) black;
                        {
                            Tile enPassantTile = pawn.getLeftEnPassantTile(grid);
                            if (enPassantTile != null) {
                                Tile whitePawnTile = grid.getTile(previousRow, previousColumn - 1);
                                Piece whitePawn = whitePawnTile.getOccupant();
                                previousTile.removeOccupant();
                                whitePawnTile.removeOccupant();
                                enPassantTile.setOccupant(black);
                                int removeIndex = Pieces.remove(pieces, whitePawn);
                                grid.setProtections(pieces);
                                if (!blackKing.inCheck(grid)) {
                                    black.increaseMoveCount();
                                    possiblePositions.add(new PositionHolder(new Grid(grid), "Black Pawn at " + previousTile.getNotationLocation() + " performed enpassant capture on the Black Pawn at " + whitePawn.getNotationLocation() + " and has moved from " + previousTile.getNotationLocation() + " to " + black.getNotationLocation()));
                                    black.decreaseMoveCount();
                                }
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, whitePawn);
                                grid.setProtections(pieces);
                            }
                        }
                        {
                            Tile enPassantTile = pawn.getRightEnPassantTile(grid);
                            if (enPassantTile != null) {
                                Tile whitePawnTile = grid.getTile(previousRow, previousColumn + 1);
                                Piece whitePawn = whitePawnTile.getOccupant();
                                previousTile.removeOccupant();
                                whitePawnTile.removeOccupant();
                                enPassantTile.setOccupant(black);
                                int removeIndex = Pieces.remove(pieces, whitePawn);
                                grid.setProtections(pieces);
                                if (!blackKing.inCheck(grid)) {
                                    black.increaseMoveCount();
                                    possiblePositions.add(new PositionHolder(new Grid(grid), "Black Pawn at " + previousTile.getNotationLocation() + " performed enpassant capture on the Black Pawn at " + whitePawn.getNotationLocation() + " and has moved from " + previousTile.getNotationLocation() + " to " + black.getNotationLocation()));
                                    black.decreaseMoveCount();
                                }
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, whitePawn);
                                grid.setProtections(pieces);
                            }
                        }

                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                     */
                }

                for (int pieceIndex = 0; pieceIndex != NUMBER_OF_BLACK_PIECES; ++pieceIndex) {
                    final Piece black = blacks.get(pieceIndex);
                    final int previousRow = black.getRow();
                    final int previousColumn = black.getColumn();
                    final Tile previousTile = grid.getTile(previousRow, previousColumn);
                    List<Tile> moveTiles = black.getMoveTiles(grid);
                    for (int index = (moveTiles.size() - 1); index >= 0; --index) {
                        Tile moveTile = moveTiles.get(index);
                        previousTile.removeOccupant();
                        if (black.isPawn() && previousRow == 6) {
                            Queen replace = Pawn.promote(black);
                            moveTile.setOccupant(replace);
                            int pawnIndex = pieces.indexOf(black);
                            pieces.set(pawnIndex, replace);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                replace.increaseMoveCount();
                                possiblePositions.add(new PositionHolder(new Grid(grid), "Black Pawn at " + previousTile.getNotationLocation() + " has moved to " + replace.getNotationLocation() + " and has been promoted to a Black Queen."));
                            }
                            previousTile.setOccupant(black);
                            moveTile.removeOccupant();
                            pieces.set(pawnIndex, black);
                            grid.setProtections(pieces);
                        }
                        else {
                            moveTile.setOccupant(black);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                black.increaseMoveCount();
                                possiblePositions.add(new PositionHolder(new Grid(grid), black.getName() + " at " + previousTile.getNotationLocation() + " has moved to " + black.getNotationLocation()));
                                black.decreaseMoveCount();
                            }
                            previousTile.setOccupant(black);
                            moveTile.removeOccupant();
                            grid.setProtections(pieces);
                        }
                        Tester.check(grid, clonedGrid, pieces, clonedPieces);
                    }
                }
                Tester.check(grid, clonedGrid, pieces, clonedPieces);
            }

            //show info dialog
            DIALOG.setVisible(true);

            //start timing
            (TIMER = timer).startTiming();

            list.clear();

            final int numberOfPositions = possiblePositions.size();
            if (numberOfPositions != Tester.perft(grid, 1, !side)) {
                throw new InternalError();
            }

            if (numberOfPositions == 0) {
                DIALOG.disableUpdate();
                DIALOG.setTitle(side ? "White AI - (Done)" : "Black AI - (Done)");
                timer.stopTiming();

                //clear previous grids and pieces
                grid.clear();
                pieces.clear();
                return;
            }
            
            //update the dialog if not game over
            DIALOG.reset();

            PositionHolder bestPosition = possiblePositions.get(0);

            if (parallel) {
                //magic thomas gold
                //can't wait
                //again- arminvan buuren remix
                ParallelSearch[] parallelSearches = new ParallelSearch[numberOfPositions];
                Future[] futures = new Future[numberOfPositions];
                ThreadPoolExecutor executor = new ThreadPoolExecutor(numberOfPositions,
                        numberOfPositions, 0L, TimeUnit.MILLISECONDS, DEQUE);
                for (int index = 0; index != numberOfPositions; ++index) {
                    futures[index] = executor.submit(parallelSearches[index] = new ParallelSearch(possiblePositions.get(index)));
                }
                executor.shutdown();
                try {
                    for (int index = 0; index != numberOfPositions; ++index) {
                        futures[index].get();
                        futures[index] = null;
                    }
                }
                catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }
                int shallowestDepth = parallelSearches[0].results.size();
                for (int index = 1; index != numberOfPositions; ++index) {
                    int depthsCovered = parallelSearches[index].results.size();
                    if (depthsCovered < shallowestDepth) {
                        shallowestDepth = depthsCovered;
                    }
                }
                DIALOG.setFinishedDepth(shallowestDepth); //this is the size of the list, we need the last index
                if (depthLimited) {
                    DIALOG.setMaxPositionValue(bestPosition.value = parallelSearches[0].results.get(--shallowestDepth));
                    parallelSearches[0] = null;
                    for (int index = 1; index != numberOfPositions; ++index) {
                        PositionHolder currentPosition = possiblePositions.get(index);
                        if ((currentPosition.value = parallelSearches[index].results.get(shallowestDepth)) > bestPosition.value) {
                            DIALOG.setMaxPositionValue((bestPosition = currentPosition).value);
                        }
                        parallelSearches[index] = null;
                    }
                }
                else {
                    DIALOG.setMaxPositionValue(bestPosition.value = parallelSearches[0].results.get(parallelSearches[0].results.size() - 1));
                    parallelSearches[0] = null;
                    for (int index = 1; index != numberOfPositions; ++index) {
                        PositionHolder currentPosition = possiblePositions.get(index);
                        if ((currentPosition.value = parallelSearches[index].results.get(parallelSearches[index].results.size() - 1)) > bestPosition.value) {
                            DIALOG.setMaxPositionValue((bestPosition = currentPosition).value);
                        }
                        parallelSearches[index] = null;
                    }
                }
                parallelSearches = null;
                futures = null;
                executor = null;
                DEQUE.clear();
            }
            else if (depthLimited) {
                int previousIterationTime = NEGATIVE_INFINITY;
                //array of previous values
                int[] previousValues = new int[numberOfPositions];
                Iterative_Deepening:
                for (int searchDepth = 1; searchDepth <= maxDepth; ++searchDepth) {
                    //before attempting to search at this particular depth, check time 
                    final int depthStartTime = timer.timeElapsed();
                    if (previousIterationTime != NEGATIVE_INFINITY && (timer.getCountdownTime() - depthStartTime) <= previousIterationTime) {
                        //it is pretty much impossible to search to a greater depth
                        //in the remaining amount of time so terminate search
                        break;
                    }
                    for (int index = 0; index != numberOfPositions; ++index) {
                        previousValues[index] = possiblePositions.get(index).value;
                    }
                    for (int positionIndex = 0; positionIndex != numberOfPositions; ++positionIndex) {
                        PositionHolder position = possiblePositions.get(positionIndex);
                        int result = AlphaBetaBlack.min(position.grid, position.whites, position.blacks, searchDepth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                        if (timer.timeOver()) {
                            //should time run out before all searches are completed
                            //at the current depth, reset all positional values to
                            //the previous depth iteration
                            for (int index = 0; index != numberOfPositions; ++index) {
                                possiblePositions.get(index).value = previousValues[index];
                            }
                            break Iterative_Deepening;
                        }
                        position.value = result;
                    }
                    //at this point the current depth has been fully searched
                    //so now we sort the higher scoring positions to look at first
                    //and find the best position, which is the first after sorting
                    possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                    DIALOG.setMaxPositionValue((bestPosition = possiblePositions.get(0)).value);
                    DIALOG.setFinishedDepth(searchDepth);
                    previousIterationTime = timer.timeElapsed() - depthStartTime;
                    System.out.println("Depth: " + searchDepth + " Time Taken: " + previousIterationTime);
                }
                System.out.println();
                previousValues = null;
            }
            else {
                Iterative_Deepening:
                for (int searchDepth = 1; searchDepth <= maxDepth; ++searchDepth) {
                    for (int index = 0; index != numberOfPositions; ++index) {
                        PositionHolder position = possiblePositions.get(index);
                        int result = AlphaBetaBlack.min(position.grid, position.whites, position.blacks, searchDepth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                        if (timer.timeOver()) {
                            break Iterative_Deepening;
                        }
                        position.value = result;
                    }
                    possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                    DIALOG.setMaxPositionValue(possiblePositions.get(0).value);
                    DIALOG.setFinishedDepth(searchDepth);
                }
                possiblePositions.sort(HIGHEST_VALUE_POSITION_FIRST);
                DIALOG.setMaxPositionValue((bestPosition = possiblePositions.get(0)).value);
            }

            {
                //maybe search for a better position only when losing
                final String encoded = Pieces.encode(bestPosition.whites, bestPosition.blacks);
                if (!selectedPositions.containsKey(encoded)) {
                    selectedPositions.put(encoded, 1);
                }
                else {
                    final int timesSelected = selectedPositions.get(encoded);
                    if (timesSelected >= 2) {
                        if (numberOfPositions >= 2) {
                            possiblePositions.sort(HIGHEST_VALUE_POSITION_LAST);
                            boolean successorFound = false;
                            for (int index = (numberOfPositions - 2); index >= 0; --index) {
                                PositionHolder nextBestPosition = possiblePositions.get(index);
                                String nextBestPositionEncoded = Pieces.encode(nextBestPosition.whites, nextBestPosition.blacks);
                                if (!selectedPositions.containsKey(nextBestPositionEncoded)) {
                                    bestPosition = nextBestPosition;
                                    selectedPositions.put(nextBestPositionEncoded, 1);
                                    successorFound = true;
                                    break;
                                }
                                if (selectedPositions.get(nextBestPositionEncoded) == 1) {
                                    bestPosition = nextBestPosition;
                                    selectedPositions.put(nextBestPositionEncoded, 2);
                                    successorFound = true;
                                    break;
                                }
                            }
                            if (!successorFound) {
                                selectedPositions.put(encoded, timesSelected + 1);
                            }
                        }
                        else {
                            selectedPositions.put(encoded, timesSelected + 1);
                        }
                    }
                    else {
                        selectedPositions.put(encoded, timesSelected + 1);
                    }
                }
            }

            if (CHECK_MODE) {
                (TIMER = TEST_TIMER).startTiming();
                Tester.checkProtections(pieces, side);
                for (int index = 0; index != numberOfPositions; ++index) {
                    PositionHolder current = possiblePositions.get(index);
                    int alphaBeta = AlphaBetaBlack.min(current.grid, current.whites, current.blacks, TEST_DEPTH, NEGATIVE_INFINITY, POSITIVE_INFINITY);
                    int minMax = MinMaxBlack.min(current.grid, current.whites, current.blacks, TEST_DEPTH);
                    if (alphaBeta != minMax) {
                        System.out.println("Algorithm Mismatch");
                        System.out.println("AlphaBeta: " + alphaBeta);
                        System.out.println("MinMax: " + minMax);
                    }
                    Tester.check(current.grid, current.clonedGrid, current.whites, current.clonedWhites);
                    Tester.check(current.grid, current.clonedGrid, current.blacks, current.clonedBlacks);
                }
            }

            list.add(bestPosition.description);
            for (int index = 0, size = bestPosition.whites.size(); index != size; ++index) {
                list.add(PIECE_CONVERTER.convertBackward(bestPosition.whites.get(index)));
            }
            for (int index = 0, size = bestPosition.blacks.size(); index != size; ++index) {
                list.add(PIECE_CONVERTER.convertBackward(bestPosition.blacks.get(index)));
            }
        }

        DIALOG.disableUpdate();
        DIALOG.setTitle(side ? "White AI - (Done)" : "Black AI - (Done)");
        timer.stopTiming();

        //clear previous grids and pieces
        grid.clear();
        pieces.clear();
        possiblePositions.clear();
    }
    
    void useTestDialog() {
        DIALOG = new AI.StatusDialog(new JFrame());
    }

    static final class StatusDialog extends JDialog {

        private static final String TITLE = "AI Status";
        private final StatusView view;

        StatusDialog(JFrame parent) {
            super(parent, TITLE, false);
            super.setIconImage(parent.getIconImage());

            Dimension windowSize = new Dimension(parent.getWidth() / 2, Constants.APP_HEIGHT + parent.getHeight() / 2);

            super.setSize(windowSize);
            super.setMinimumSize(windowSize);
            super.setMaximumSize(windowSize);
            super.setPreferredSize(windowSize);

            super.getContentPane().add(view = new StatusView());

            super.setLocationRelativeTo(parent);
            super.setResizable(false);
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            if (!visible) {
                view.update = false;
            }
        }

        void setStateTitle(String state) {
            super.setTitle(TITLE + state);
        }

        void disableUpdate() {
            view.update = false;
        }

        void increasePositionsScanned() {
            ++view.nodesScanned;
        }

        void setMaxPositionValue(int num) {
            view.data[3] = ("Best Position Value: " + (view.maxNodeValue = num));
        }

        void setFinishedDepth(int num) {
            view.depth = num;
        }

        void reset() {
            view.update = true;
            view.nodesScanned = view.depth = 0;
            view.maxNodeValue = NEGATIVE_INFINITY;
        }

        private static final class StatusView extends JPanel implements Runnable {

            //could use atomicinteger
            private volatile boolean update;
            private volatile int nodesScanned;
            //private int nodesIgnored;
            private volatile int maxNodeValue;
            private volatile int depth;
            private final String[] data = new String[5];

            @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
            private StatusView() {
                super(null);
                data[0] = data[1] = data[2] = data[3] = data[4] = "";
                Thread statusViewThread = new Thread(this, "Status View Thread");
                statusViewThread.setPriority(Thread.MAX_PRIORITY - 1);
                statusViewThread.start();
            }

            private int width;
            private int height;
            private BufferedImage offscreenBuffer;
            private Graphics2D offscreenGraphics;
            private FontRenderContext offscreenFontRenderContext;
            private Font font;

            @Override
            @SuppressWarnings("UnusedAssignment")
            protected final void paintComponent(Graphics window) {
                super.paintComponent(window);
                if (offscreenBuffer == null) {
                    offscreenFontRenderContext = (offscreenGraphics = (offscreenBuffer = (BufferedImage) createImage(width = getWidth(), height = getHeight())).createGraphics()).getFontRenderContext();
                    font = getFont();
                }
                offscreenGraphics.setColor(Color.BLACK);
                offscreenGraphics.fillRect(0, 0, width, height);
                offscreenGraphics.setColor(Color.WHITE);
                if (update) {
                    int secondsElapsed = AI.TIMER.timeElapsed();
                    data[0] = ("Seconds Elapsed: " + secondsElapsed);
                    data[1] = ("Positions Scanned: " + nodesScanned);
                    data[2] = ("Positions Scanned per Second: " + ((secondsElapsed == 0) ? nodesScanned : ((int) (nodesScanned / secondsElapsed))));
                    //data.add("Nodes Ignored: " + nodesIgnored);
                    data[3] = ("Max Position Value: " + maxNodeValue);
                    data[4] = ("Search Depths Completed: " + depth);
                    //data[5] = ("Current Search Depth: " + searchDepth);
                    //data.add("Unused Memory: " + RUNTIME.freeMemory() + " bytes");
                    //data.add("Used Memory: " + RUNTIME.totalMemory() + " bytes");
                    //data.add("Maximum Memory: " + RUNTIME.maxMemory() + " bytes");
                }
                float textHeight = 0.0f;
                for (int index = 0; index != data.length; ++index) {
                    String line = data[index];
                    offscreenGraphics.drawString(line, 0, textHeight += Constants.getStringHeight(line, font, offscreenFontRenderContext));
                }
                int approximateTextHeight = (int) Math.ceil(textHeight);
                //offscreenGraphics.drawLine(0, approximateTextHeight, width, approximateTextHeight);
                offscreenGraphics.drawImage(IMAGE, 0, approximateTextHeight, width, height - approximateTextHeight, this);
                window.drawImage(offscreenBuffer, 0, 0, this);
            }

            @Override
            public final void run() {
                try {
                    for (;;) {
                        repaint();
                        //helps to reduce painting overload and stress
                        //especially during parallel processing
                        //reduce the frames per second
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                }
                catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}