package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.CHECKMATE_VALUE;
import static Util.Constants.NEGATIVE_INFINITY;
import static Util.Constants.POSITIVE_INFINITY;
import java.util.List;

/**
 * Fail-hard Alpha-Beta search algorithm that searches through the chess game
 * tree in Black's perspective. Larger values indicate better positions for
 * Black and vice versa.
 *
 * @author Will
 * @see Evaluator
 */
final class AlphaBetaWhite {

    private AlphaBetaWhite() {

    }

    /**
     * Minimizing component of the Alpha-Beta search function seeking to reduce
     * White's score as much as possible. This component implements Black's
     * moves.
     *
     * @param grid The chess board.
     * @param whites The white pieces on the chess board.
     * @param blacks The black pieces on the chess board.
     * @param depth Number of ply to search ahead.
     * @return The least possible score to reduce White's score as much as
     * possible. This score may be extremely high, indicating that Black is
     * losing or is about to be checkmated.
     */
    static int min(final Grid grid, final List<Piece> whites, final List<Piece> blacks, int depth, final int alpha, int beta) {
        if (depth == 0 || AI.TIMER.timeOver()) {
            return Evaluator.evaluateInWhitePerspective(grid, whites, blacks);
        }

        --depth;
        int value = POSITIVE_INFINITY;

        final King blackKing = Pieces.getBlackKing(blacks);

        {
            final Tile previousTile = grid.getTile(blackKing.getRow(), blackKing.getColumn());
            final List<Tile> castleTiles = blackKing.getCastleTiles(grid);
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
                    grid.setProtections(whites, blacks);

                    blackKing.increaseMoveCount();
                    leftRook.increaseMoveCount();
                    int result = max(grid, whites, blacks, depth, alpha, beta);
                    if (result < value) {
                        value = result;
                    }
                    if (value < beta) {
                        beta = value;
                    }
                    blackKing.decreaseMoveCount();
                    leftRook.decreaseMoveCount();

                    previousTile.setOccupant(blackKing);
                    leftRookTile.setOccupant(leftRook);
                    kingCastleTile.removeOccupant();
                    leftRookCastleTile.removeOccupant();
                    grid.setProtections(whites, blacks);

                    if (beta <= alpha) {
                        return beta;
                    }
                }
                else {
                    Tile rightRookTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, 7);
                    Tile rightRookCastleTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, ChessConstants.RIGHT_ROOK_CASTLE_COLUMN);
                    Piece rightRook = rightRookTile.getOccupant();

                    previousTile.removeOccupant();
                    rightRookTile.removeOccupant();
                    kingCastleTile.setOccupant(blackKing);
                    rightRookCastleTile.setOccupant(rightRook);
                    grid.setProtections(whites, blacks);

                    blackKing.increaseMoveCount();
                    rightRook.increaseMoveCount();
                    int result = max(grid, whites, blacks, depth, alpha, beta);
                    if (result < value) {
                        value = result;
                    }
                    if (value < beta) {
                        beta = value;
                    }
                    blackKing.decreaseMoveCount();
                    rightRook.decreaseMoveCount();

                    previousTile.setOccupant(blackKing);
                    rightRookTile.setOccupant(rightRook);
                    kingCastleTile.removeOccupant();
                    rightRookCastleTile.removeOccupant();
                    grid.setProtections(whites, blacks);

                    if (beta <= alpha) {
                        return beta;
                    }
                }
            }
        }

        final int numberOfBlackPieces = blacks.size();

        for (int pieceIndex = 0; pieceIndex != numberOfBlackPieces; ++pieceIndex) {
            final Piece black = blacks.get(pieceIndex);
            final int previousRow = black.getRow();
            final int previousColumn = black.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> attackTiles = black.getAttackTiles(grid);
            for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (enemy.isKing()) {
                    continue;
                }
                previousTile.removeOccupant();
                if (black.isPawn() && previousRow == 6) {
                    Queen replace = Pawn.promote(black);
                    attackTile.setOccupant(replace);
                    int pawnIndex = blacks.indexOf(black);
                    blacks.set(pawnIndex, replace);
                    int removeIndex = Pieces.remove(whites, enemy);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        int result = max(grid, whites, blacks, depth, alpha, beta);
                        if (result < value) {
                            value = result;
                        }
                        if (value < beta) {
                            beta = value;
                        }
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            attackTile.setOccupant(enemy);
                            blacks.set(pawnIndex, black);
                            whites.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                            return beta;
                        }
                    }
                    previousTile.setOccupant(black);
                    attackTile.setOccupant(enemy);
                    blacks.set(pawnIndex, black);
                    whites.add(removeIndex, enemy);
                    grid.setProtections(whites, blacks);
                }
                else {
                    attackTile.setOccupant(black);
                    int removeIndex = Pieces.remove(whites, enemy);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        black.increaseMoveCount();
                        int result = max(grid, whites, blacks, depth, alpha, beta);
                        if (result < value) {
                            value = result;
                        }
                        if (value < beta) {
                            beta = value;
                        }
                        black.decreaseMoveCount();
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            attackTile.setOccupant(enemy);
                            whites.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                            return beta;
                        }
                    }
                    previousTile.setOccupant(black);
                    attackTile.setOccupant(enemy);
                    whites.add(removeIndex, enemy);
                    grid.setProtections(whites, blacks);
                }
            }
            /*
            if (black.isPawn()) {
                final List<Tile> enPassantTiles = ((Pawn) black).getEnPassantTiles(grid);
                for (int index = (enPassantTiles.size() - 1); index >= 0; --index) {
                    Tile enPassantTile = enPassantTiles.get(index);
                    if (enPassantTile.getColumn() < previousColumn) {
                        Tile whitePawnTile = grid.getTile(previousRow, previousColumn - 1);
                        Piece whitePawn = whitePawnTile.getOccupant();
                        previousTile.removeOccupant();
                        whitePawnTile.removeOccupant();
                        enPassantTile.setOccupant(black);
                        int removeIndex = Pieces.remove(whites, whitePawn);
                        grid.setProtections(whites, blacks);
                        if (!blackKing.inCheck(grid)) {
                            black.increaseMoveCount();
                            int result = max(grid, whites, blacks, depth, alpha, beta);
                            if (result < value) {
                                value = result;
                            }
                            if (value < beta) {
                                beta = value;
                            }
                            black.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                whites.add(removeIndex, whitePawn);
                                grid.setProtections(whites, blacks);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(black);
                        whitePawnTile.setOccupant(whitePawn);
                        enPassantTile.removeOccupant();
                        whites.add(removeIndex, whitePawn);
                        grid.setProtections(whites, blacks);
                    }
                    else {
                        Tile whitePawnTile = grid.getTile(previousRow, previousColumn + 1);
                        Piece whitePawn = whitePawnTile.getOccupant();
                        previousTile.removeOccupant();
                        whitePawnTile.removeOccupant();
                        enPassantTile.setOccupant(black);
                        int removeIndex = Pieces.remove(whites, whitePawn);
                        grid.setProtections(whites, blacks);
                        if (!blackKing.inCheck(grid)) {
                            black.increaseMoveCount();
                            int result = max(grid, whites, blacks, depth, alpha, beta);
                            if (result < value) {
                                value = result;
                            }
                            if (value < beta) {
                                beta = value;
                            }
                            black.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                whites.add(removeIndex, whitePawn);
                                grid.setProtections(whites, blacks);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(black);
                        whitePawnTile.setOccupant(whitePawn);
                        enPassantTile.removeOccupant();
                        whites.add(removeIndex, whitePawn);
                        grid.setProtections(whites, blacks);
                    }
                }
            }
            */
        }

        for (int pieceIndex = 0; pieceIndex != numberOfBlackPieces; ++pieceIndex) {
            final Piece black = blacks.get(pieceIndex);
            final int previousRow = black.getRow();
            final int previousColumn = black.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> moveTiles = black.getMoveTiles(grid);
            for (int index = (moveTiles.size() - 1); index >= 0; --index) {
                Tile moveTile = moveTiles.get(index);
                previousTile.removeOccupant();
                if (black.isPawn() && previousRow == 6) {
                    Queen replace = Pawn.promote(black);
                    moveTile.setOccupant(replace);
                    int pawnIndex = blacks.indexOf(black);
                    blacks.set(pawnIndex, replace);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        int result = max(grid, whites, blacks, depth, alpha, beta);
                        if (result < value) {
                            value = result;
                        }
                        if (value < beta) {
                            beta = value;
                        }
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            moveTile.removeOccupant();
                            blacks.set(pawnIndex, black);
                            grid.setProtections(whites, blacks);
                            return beta;
                        }
                    }
                    previousTile.setOccupant(black);
                    moveTile.removeOccupant();
                    blacks.set(pawnIndex, black);
                    grid.setProtections(whites, blacks);
                }
                else {
                    moveTile.setOccupant(black);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        black.increaseMoveCount();
                        int result = max(grid, whites, blacks, depth, alpha, beta);
                        if (result < value) {
                            value = result;
                        }
                        if (value < beta) {
                            beta = value;
                        }
                        black.decreaseMoveCount();
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            moveTile.removeOccupant();
                            grid.setProtections(whites, blacks);
                            return beta;
                        }
                    }
                    previousTile.setOccupant(black);
                    moveTile.removeOccupant();
                    grid.setProtections(whites, blacks);
                }
            }
        }

        return (value == POSITIVE_INFINITY) ? checkBlackEndGame(grid, blackKing, depth + 1) : beta;
    }

    /**
     * Maximizing component of the Alpha-Beta search function. This component
     * seeks to increase White's score as much as possible.
     *
     * @param grid The chess board.
     * @param whites The white pieces on the chess board.
     * @param blacks The black pieces on the chess board.
     * @param depth Number of ply to search ahead.
     * @return The greatest possible score to increase White's score as much as
     * possible. This score may be extremely low, indicating that White is
     * losing or is about to be checkmated.
     */
    static int max(final Grid grid, final List<Piece> whites, final List<Piece> blacks, int depth, int alpha, final int beta) {
        if (depth == 0 || AI.TIMER.timeOver()) {
            return Evaluator.evaluateInWhitePerspective(grid, whites, blacks);
        }

        --depth;
        int value = NEGATIVE_INFINITY;

        final King whiteKing = Pieces.getWhiteKing(whites);

        {
            final Tile previousTile = grid.getTile(whiteKing.getRow(), whiteKing.getColumn());
            final List<Tile> castleTiles = whiteKing.getCastleTiles(grid);
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
                    grid.setProtections(whites, blacks);

                    whiteKing.increaseMoveCount();
                    leftRook.increaseMoveCount();
                    int result = min(grid, whites, blacks, depth, alpha, beta);
                    if (result > value) {
                        value = result;
                    }
                    if (value > alpha) {
                        alpha = value;
                    }
                    whiteKing.decreaseMoveCount();
                    leftRook.decreaseMoveCount();

                    previousTile.setOccupant(whiteKing);
                    leftRookTile.setOccupant(leftRook);
                    kingCastleTile.removeOccupant();
                    leftRookCastleTile.removeOccupant();
                    grid.setProtections(whites, blacks);
                    
                    if (beta <= alpha) {
                        return alpha;
                    }
                }
                else {
                    Tile rightRookTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, 7);
                    Tile rightRookCastleTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, ChessConstants.RIGHT_ROOK_CASTLE_COLUMN);
                    Piece rightRook = rightRookTile.getOccupant();

                    previousTile.removeOccupant();
                    rightRookTile.removeOccupant();
                    kingCastleTile.setOccupant(whiteKing);
                    rightRookCastleTile.setOccupant(rightRook);
                    grid.setProtections(whites, blacks);

                    whiteKing.increaseMoveCount();
                    rightRook.increaseMoveCount();
                    int result = min(grid, whites, blacks, depth, alpha, beta);
                    if (result > value) {
                        value = result;
                    }
                    if (value > alpha) {
                        alpha = value;
                    }
                    whiteKing.decreaseMoveCount();
                    rightRook.decreaseMoveCount();

                    previousTile.setOccupant(whiteKing);
                    rightRookTile.setOccupant(rightRook);
                    kingCastleTile.removeOccupant();
                    rightRookCastleTile.removeOccupant();
                    grid.setProtections(whites, blacks);
                    
                    if (beta <= alpha) {
                        return alpha;
                    }
                }
            }
        }

        final int numberOfWhitePieces = whites.size();

        for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
            final Piece white = whites.get(pieceIndex);
            final int previousRow = white.getRow();
            final int previousColumn = white.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> attackTiles = white.getAttackTiles(grid);
            for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (enemy.isKing()) {
                    continue;
                }
                previousTile.removeOccupant();
                if (white.isPawn() && previousRow == 1) {
                    Queen replace = Pawn.promote(white);
                    attackTile.setOccupant(replace);
                    int pawnIndex = whites.indexOf(white);
                    whites.set(pawnIndex, replace);
                    int removeIndex = Pieces.remove(blacks, enemy);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        int result = min(grid, whites, blacks, depth, alpha, beta);
                        if (result > value) {
                            value = result;
                        }
                        if (value > alpha) {
                            alpha = value;
                        }
                        if (beta <= alpha) {
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            whites.set(pawnIndex, white);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(white);
                    attackTile.setOccupant(enemy);
                    whites.set(pawnIndex, white);
                    blacks.add(removeIndex, enemy);
                    grid.setProtections(whites, blacks);
                }
                else {
                    attackTile.setOccupant(white);
                    int removeIndex = Pieces.remove(blacks, enemy);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        white.increaseMoveCount();
                        int result = min(grid, whites, blacks, depth, alpha, beta);
                        if (result > value) {
                            value = result;
                        }
                        if (value > alpha) {
                            alpha = value;
                        }
                        white.decreaseMoveCount();
                        if (beta <= alpha) {
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(white);
                    attackTile.setOccupant(enemy);
                    blacks.add(removeIndex, enemy);
                    grid.setProtections(whites, blacks);
                }
            }
            /*
            if (white.isPawn()) {
                final List<Tile> enPassantTiles = ((Pawn) white).getEnPassantTiles(grid);
                for (int index = (enPassantTiles.size() - 1); index >= 0; --index) {
                    Tile enPassantTile = enPassantTiles.get(index);
                    if (enPassantTile.getColumn() < previousColumn) {
                        Tile blackPawnTile = grid.getTile(previousRow, previousColumn - 1);
                        Piece blackPawn = blackPawnTile.getOccupant();
                        previousTile.removeOccupant();
                        blackPawnTile.removeOccupant();
                        enPassantTile.setOccupant(white);
                        int removeIndex = Pieces.remove(blacks, blackPawn);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            int result = min(grid, whites, blacks, depth, alpha, beta);
                            if (result > value) {
                                value = result;
                            }
                            if (value > alpha) {
                                alpha = value;
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                blacks.add(removeIndex, blackPawn);
                                grid.setProtections(whites, blacks);
                                return alpha;
                            }
                        }
                        previousTile.setOccupant(white);
                        blackPawnTile.setOccupant(blackPawn);
                        enPassantTile.removeOccupant();
                        blacks.add(removeIndex, blackPawn);
                        grid.setProtections(whites, blacks);
                    }
                    else {
                        Tile blackPawnTile = grid.getTile(previousRow, previousColumn + 1);
                        Piece blackPawn = blackPawnTile.getOccupant();
                        previousTile.removeOccupant();
                        blackPawnTile.removeOccupant();
                        enPassantTile.setOccupant(white);
                        int removeIndex = Pieces.remove(blacks, blackPawn);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            int result = min(grid, whites, blacks, depth, alpha, beta);
                            if (result > value) {
                                value = result;
                            }
                            if (value > alpha) {
                                alpha = value;
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                blackPawnTile.setOccupant(blackPawn);
                                enPassantTile.removeOccupant();
                                blacks.add(removeIndex, blackPawn);
                                grid.setProtections(whites, blacks);
                                return alpha;
                            }
                        }
                        previousTile.setOccupant(white);
                        blackPawnTile.setOccupant(blackPawn);
                        enPassantTile.removeOccupant();
                        blacks.add(removeIndex, blackPawn);
                        grid.setProtections(whites, blacks);
                    }
                }
            }
            */
        }

        for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
            final Piece white = whites.get(pieceIndex);
            final int previousRow = white.getRow();
            final int previousColumn = white.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> moveTiles = white.getMoveTiles(grid);
            for (int index = (moveTiles.size() - 1); index >= 0; --index) {
                Tile moveTile = moveTiles.get(index);
                previousTile.removeOccupant();
                if (white.isPawn() && previousRow == 1) {
                    Queen replace = Pawn.promote(white);
                    moveTile.setOccupant(replace);
                    int pawnIndex = whites.indexOf(white);
                    whites.set(pawnIndex, replace);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        int result = min(grid, whites, blacks, depth, alpha, beta);
                        if (result > value) {
                            value = result;
                        }
                        if (value > alpha) {
                            alpha = value;
                        }
                        if (beta <= alpha) {
                            previousTile.setOccupant(white);
                            moveTile.removeOccupant();
                            whites.set(pawnIndex, white);
                            grid.setProtections(whites, blacks);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(white);
                    moveTile.removeOccupant();
                    whites.set(pawnIndex, white);
                    grid.setProtections(whites, blacks);
                }
                else {
                    moveTile.setOccupant(white);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        white.increaseMoveCount();
                        int result = min(grid, whites, blacks, depth, alpha, beta);
                        if (result > value) {
                            value = result;
                        }
                        if (value > alpha) {
                            alpha = value;
                        }
                        white.decreaseMoveCount();
                        if (beta <= alpha) {
                            previousTile.setOccupant(white);
                            moveTile.removeOccupant();
                            grid.setProtections(whites, blacks);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(white);
                    moveTile.removeOccupant();
                    grid.setProtections(whites, blacks);
                }
            }
        }

        return (value == NEGATIVE_INFINITY) ? checkWhiteEndGame(grid, whiteKing, depth + 1) : alpha;
    }

    /**
     * This function should only be called when the White player cannot make any
     * legal moves. The function then determines whether such a position is a
     * Checkmate against White or a Stalemate.
     *
     * @param grid The current chess board.
     * @param whiteKing The White player's King.
     * @param depth How close this position is from the base node. The bigger
     * this value, the closer this position is from the base node. This value
     * cannot be negative.
     * @return An extremely low value (less than or equal to 200,000,000) if
     * this position is a Checkmate against White or 0 if this position is a
     * Stalemate.
     */
    private static int checkWhiteEndGame(final Grid grid, final King whiteKing, final int depth) {
        AI.DIALOG.increasePositionsScanned();
        //a bigger "depth", is actually shallower in the tree
        return whiteKing.inCheck(grid) ? (-CHECKMATE_VALUE - depth) : 0;
    }

    /**
     * This function should only be called when the Black player cannot make any
     * legal moves. The function then determines whether such a position is a
     * Checkmate against Black or a Stalemate.
     *
     * @param grid The current chess board.
     * @param blackKing The Black player's King.
     * @param depth How close this position is from the base node. The bigger
     * this value, the closer this position is from the base node. This value
     * cannot be negative.
     * @return An extremely high value (greater than or equal to 200,000,000) if
     * this position is a Checkmate against Black or 0 if this position is a
     * Stalemate.
     */
    private static int checkBlackEndGame(final Grid grid, final King blackKing, final int depth) {
        AI.DIALOG.increasePositionsScanned();
        //a lower "depth", is actually deeper in the tree
        return blackKing.inCheck(grid) ? (CHECKMATE_VALUE + depth) : 0;
    }
}