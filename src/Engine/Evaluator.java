package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.BISHOP_BONUS;
import static Util.ChessConstants.BISHOP_VALUE;
import static Util.ChessConstants.CASTLE_VALUE;
import static Util.ChessConstants.CHECKED_VALUE;
import static Util.ChessConstants.KING_MOVED_VALUE;
import static Util.ChessConstants.KNIGHT_VALUE;
import static Util.ChessConstants.PAWN_VALUE;
import static Util.ChessConstants.QUEEN_VALUE;
import static Util.ChessConstants.ROOK_VALUE;
import java.math.BigInteger;
import java.util.List;

public final class Evaluator {

    private Evaluator() {

    }

    private static final int[] PAWN_POSITION_WHITE = {
        0, 0, 0, 0, 0, 0, 0, 0,
        5, 10, 15, 20, 20, 15, 10, 5,
        4, 8, 12, 16, 16, 12, 8, 4,
        3, 6, 9, 12, 12, 9, 6, 3,
        2, 4, 6, 8, 8, 6, 4, 2,
        1, 2, 3, -10, -10, 3, 2, 1,
        0, 0, 0, -40, -40, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
    };

    private static final int[] KNIGHT_POSITION_WHITE = {
        -10, -10, -10, -10, -10, -10, -10, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, -30, -10, -10, -10, -10, -30, -10
    };

    private static final int[] BISHOP_POSITION_WHITE = {
        -10, -10, -10, -10, -10, -10, -10, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, -10, -20, -10, -10, -20, -10, -10
    };

    private static final int[] ROOK_POSITION_WHITE = {
        5, 5, 5, 5, 5, 5, 5, 5,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 2, 3, 3, 2, 1, 0,
        1, 2, 3, 6, 6, 3, 2, 1,
        1, 2, 3, 6, 6, 3, 2, 1,
        0, 1, 2, 3, 3, 2, 1, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        -10, 0, 0, 10, 10, 0, 0, -10
    };

    private static final int[] QUEEN_POSITION_WHITE = {
        -10, -10, -10, -10, -10, -10, -10, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, -10, -20, -10, -10, -20, -10, -10
    };

    private static final int[] KING_POSITION_MIDDLE_WHITE = {
        -40, -40, -40, -40, -40, -40, -40, -40,
        -40, -40, -40, -40, -40, -40, -40, -40,
        -40, -40, -40, -40, -40, -40, -40, -40,
        -40, -40, -40, -40, -40, -40, -40, -40,
        -40, -40, -40, -40, -40, -40, -40, -40,
        -40, -40, -40, -40, -40, -40, -40, -40,
        -20, -20, -20, -20, -20, -20, -20, -20,
        0, 20, 40, -20, 0, -20, 40, 20
    };

    private static final int[] KING_POSITION_END_WHITE = {
        0, 10, 20, 30, 30, 20, 10, 0,
        10, 20, 30, 40, 40, 30, 20, 10,
        20, 30, 40, 50, 50, 40, 30, 20,
        30, 40, 50, 60, 60, 50, 40, 30,
        30, 40, 50, 60, 60, 50, 40, 30,
        20, 30, 40, 50, 50, 40, 30, 20,
        10, 20, 30, 40, 40, 30, 20, 10,
        0, 10, 20, 30, 30, 20, 10, 0
    };

    /*
    private static final int[] NORMAL = {
        0, 1, 2, 3, 4, 5, 6, 7, 
        8, 9, 10, 11, 12, 13, 14, 15, 
        16, 17, 18, 19, 20, 21, 22, 23, 
        24, 25, 26, 27, 28, 29, 30, 31, 
        32, 33, 34, 35, 36, 37, 38, 39, 
        40, 41, 42, 43, 44, 45, 46, 47, 
        48, 49, 50, 51, 52, 53, 54, 55, 
        56, 57, 58, 59, 60, 61, 62, 63
    };
     */
    
    private static final int[] MIRROR = {
        56, 57, 58, 59, 60, 61, 62, 63,
        48, 49, 50, 51, 52, 53, 54, 55,
        40, 41, 42, 43, 44, 45, 46, 47,
        32, 33, 34, 35, 36, 37, 38, 39,
        24, 25, 26, 27, 28, 29, 30, 31,
        16, 17, 18, 19, 20, 21, 22, 23,
        8, 9, 10, 11, 12, 13, 14, 15,
        0, 1, 2, 3, 4, 5, 6, 7
    };

    private static final int[] PAWN_POSITION_BLACK = new int[ChessConstants.LINEAR_LENGTH];

    private static final int[] KNIGHT_POSITION_BLACK = new int[ChessConstants.LINEAR_LENGTH];

    private static final int[] BISHOP_POSITION_BLACK = new int[ChessConstants.LINEAR_LENGTH];

    private static final int[] ROOK_POSITION_BLACK = new int[ChessConstants.LINEAR_LENGTH];

    private static final int[] QUEEN_POSITION_BLACK = new int[ChessConstants.LINEAR_LENGTH];

    private static final int[] KING_POSITION_MIDDLE_BLACK = new int[ChessConstants.LINEAR_LENGTH];

    private static final int[] KING_POSITION_END_BLACK = new int[ChessConstants.LINEAR_LENGTH];

    static {
        for (int index = 0; index < ChessConstants.LINEAR_LENGTH; ++index) {
            final int mirrorIndex = MIRROR[index];
            PAWN_POSITION_BLACK[mirrorIndex] = PAWN_POSITION_WHITE[index];
            KNIGHT_POSITION_BLACK[mirrorIndex] = KNIGHT_POSITION_WHITE[index];
            BISHOP_POSITION_BLACK[mirrorIndex] = BISHOP_POSITION_WHITE[index];
            ROOK_POSITION_BLACK[mirrorIndex] = ROOK_POSITION_WHITE[index];
            QUEEN_POSITION_BLACK[mirrorIndex] = QUEEN_POSITION_WHITE[index];
            KING_POSITION_MIDDLE_BLACK[mirrorIndex] = KING_POSITION_MIDDLE_WHITE[index];
            KING_POSITION_END_BLACK[mirrorIndex] = KING_POSITION_END_WHITE[index];
        }
        mirroredColumnsEqual(toMatrix(PAWN_POSITION_WHITE), toMatrix(PAWN_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(BISHOP_POSITION_WHITE), toMatrix(BISHOP_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(KNIGHT_POSITION_WHITE), toMatrix(KNIGHT_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(ROOK_POSITION_WHITE), toMatrix(ROOK_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(QUEEN_POSITION_WHITE), toMatrix(QUEEN_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(KING_POSITION_MIDDLE_WHITE), toMatrix(KING_POSITION_MIDDLE_BLACK));
        mirroredColumnsEqual(toMatrix(KING_POSITION_END_WHITE), toMatrix(KING_POSITION_END_BLACK));
    }
    //4:05 woman coughing in defcon game

    private static int[][] toMatrix(int[] nums) {
        int[][] matrix = new int[ChessConstants.LENGTH][ChessConstants.LENGTH];
        int row = 0;
        int column = 0;
        for (int n : nums) {
            if (column == ChessConstants.LENGTH) {
                row++;
                column = 0;
            }
            matrix[row][column++] = n;
        }
        return matrix;
    }

    private static void mirroredColumnsEqual(int[][] mat1, int[][] mat2) {
        int column = 0;
        for (int times = 0; times < ChessConstants.LENGTH; ++times) {
            List<Integer> vertical = new java.util.ArrayList<>(ChessConstants.LENGTH);
            List<Integer> otherVertical = new java.util.ArrayList<>(ChessConstants.LENGTH);
            for (int row = 0; row < ChessConstants.LENGTH; ++row) {
                vertical.add(mat1[row][column]);
                otherVertical.add(mat2[row][column]);
            }
            java.util.Collections.reverse(otherVertical);
            if (!vertical.equals(otherVertical)) {
                throw new Error();
            }
            column++;
        }
    }

    //used for testing purposes
    //to count perft in in alphabeta vs minmax evaluations
    private static int POSITIONS_EVALUATED_IN_WHITE_PERSEPECTIVE;
    private static int POSITIONS_EVALUATED_IN_BLACK_PERSEPECTIVE;

    public static void setNumberOfPositionEvaluatedInWhitePersepective(int n) {
        POSITIONS_EVALUATED_IN_WHITE_PERSEPECTIVE = n;
    }

    public static void setNumberOfPositionEvaluatedInBlackPersepective(int n) {
        POSITIONS_EVALUATED_IN_BLACK_PERSEPECTIVE = n;
    }

    public static int getNumberOfPositionsEvaluatedInWhitePersepective() {
        return POSITIONS_EVALUATED_IN_WHITE_PERSEPECTIVE;
    }

    public static int getNumberOfPositionsEvaluatedInBlackPersepective() {
        return POSITIONS_EVALUATED_IN_BLACK_PERSEPECTIVE;
    }
    
    static BigInteger MAP_OPERATIONS_WHITE = BigInteger.ZERO;
    static BigInteger MAP_OPERATIONS_BLACK = BigInteger.ZERO;
    
    static final int evaluateInWhitePerspective(final Grid grid, final List<Piece> whites, final List<Piece> blacks) {
        //++POSITIONS_EVALUATED_IN_WHITE_PERSEPECTIVE;
        AI.DIALOG.increasePositionsScanned();
       
        if (AI.WHITE_STORE.containsEntry(grid)) {
            MAP_OPERATIONS_WHITE = MAP_OPERATIONS_WHITE.add(BigInteger.ONE);
            return AI.WHITE_STORE.getValue(grid);
        }

        final int numberOfWhitePieces = whites.size();
        final int numberOfBlackPieces = blacks.size();

        int whiteScore = 0;
        int whiteKnights = 0;
        int whiteBishops = 0;
        int whiteRooks = 0;
        int whiteQueens = 0;
        int whiteKingLocation;

        int blackScore = 0;
        int blackKnights = 0;
        int blackBishops = 0;
        int blackRooks = 0;
        int blackQueens = 0;
        int blackKingLocation;

        {
            {
                Piece whiteKing = whites.get(0);
                int moveCount = whiteKing.getMoveCount();
                if (grid.getTile(whiteKingLocation = ((whiteKing.getRow() * ChessConstants.LENGTH) + whiteKing.getColumn())).protectedByBlack()) {
                    whiteScore -= CHECKED_VALUE;
                    if (moveCount > 1) {
                        whiteScore -= KING_MOVED_VALUE;
                    }
                }
                else if (moveCount > 1) {
                    whiteScore -= KING_MOVED_VALUE;
                }
                else if (moveCount == 1) {
                    switch (whiteKing.getColumn()) {
                        case ChessConstants.LEFT_KING_CASTLE_COLUMN: {
                            whiteScore += CASTLE_VALUE;
                            break;
                        }
                        case ChessConstants.RIGHT_KING_CASTLE_COLUMN: {
                            whiteScore += CASTLE_VALUE;
                        }
                    }
                }
            }

            int index = 1;

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isQueen()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += QUEEN_VALUE;
                    whiteScore += QUEEN_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++whiteQueens;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isRook()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += ROOK_VALUE;
                    whiteScore += ROOK_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++whiteRooks;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isBishop()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += BISHOP_VALUE;
                    whiteScore += BISHOP_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++whiteBishops;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isKnight()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += KNIGHT_VALUE;
                    whiteScore += KNIGHT_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++whiteKnights;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isPawn()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += PAWN_VALUE;
                    whiteScore += PAWN_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++index;
                }
                else {
                    break;
                }
            }
        }
        
        {
            {
                Piece blackKing = blacks.get(0);
                int moveCount = blackKing.getMoveCount();
                if (grid.getTile(blackKingLocation = ((blackKing.getRow() * ChessConstants.LENGTH) + blackKing.getColumn())).protectedByWhite()) {
                    blackScore -= CHECKED_VALUE;
                    if (moveCount > 1) {
                        blackScore -= KING_MOVED_VALUE;
                    }
                }
                else if (moveCount > 1) {
                    blackScore -= KING_MOVED_VALUE;
                }
                else if (moveCount == 1) {
                    switch (blackKing.getColumn()) {
                        case ChessConstants.LEFT_KING_CASTLE_COLUMN: {
                            blackScore += CASTLE_VALUE;
                            break;
                        }
                        case ChessConstants.RIGHT_KING_CASTLE_COLUMN: {
                            blackScore += CASTLE_VALUE;
                        }
                    }
                }
            }

            int index = 1;

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isQueen()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += QUEEN_VALUE;
                    blackScore += QUEEN_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++blackQueens;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isRook()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += ROOK_VALUE;
                    blackScore += ROOK_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++blackRooks;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isBishop()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += BISHOP_VALUE;
                    blackScore += BISHOP_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++blackBishops;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isKnight()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += KNIGHT_VALUE;
                    blackScore += KNIGHT_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++blackKnights;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isPawn()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += PAWN_VALUE;
                    blackScore += PAWN_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++index;
                }
                else {
                    break;
                }
            }
        }

        if (((blackQueens == 0 && blackRooks <= 1)
                || ((blackQueens == 1 && blackKnights == 1 && blackBishops == 0 && blackRooks == 0) || (blackQueens == 1 && blackKnights == 0
                && blackBishops == 1 && blackRooks == 0))) && ((whiteQueens == 0 && whiteRooks <= 1)
                || ((whiteQueens == 1 && whiteKnights == 1 && whiteBishops == 0 && whiteRooks == 0) || (whiteQueens == 1 && whiteKnights == 0
                && whiteBishops == 1 && whiteRooks == 0)))) {
            blackScore += KING_POSITION_END_BLACK[blackKingLocation];
            whiteScore += KING_POSITION_END_WHITE[whiteKingLocation];
        }
        else {
            blackScore += KING_POSITION_MIDDLE_BLACK[blackKingLocation];
            whiteScore += KING_POSITION_MIDDLE_WHITE[whiteKingLocation];
            if (blackBishops >= 2) {
                blackScore += BISHOP_BONUS;
            }
            if (whiteBishops >= 2) {
                whiteScore += BISHOP_BONUS;
            }
        }
        int score = whiteScore - blackScore;
        AI.WHITE_STORE.putEntry(grid, score);
        return score;
    }

    static final int evaluateInBlackPerspective(final Grid grid, final List<Piece> whites, final List<Piece> blacks) {
        //++POSITIONS_EVALUATED_IN_BLACK_PERSEPECTIVE;
        AI.DIALOG.increasePositionsScanned();
        
        if (AI.BLACK_STORE.containsEntry(grid)) {
            MAP_OPERATIONS_BLACK = MAP_OPERATIONS_BLACK.add(BigInteger.ONE);
            return AI.BLACK_STORE.getValue(grid);
        }

        final int numberOfWhitePieces = whites.size();
        final int numberOfBlackPieces = blacks.size();

        int whiteScore = 0;
        int whiteKnights = 0;
        int whiteBishops = 0;
        int whiteRooks = 0;
        int whiteQueens = 0;
        int whiteKingLocation;

        int blackScore = 0;
        int blackKnights = 0;
        int blackBishops = 0;
        int blackRooks = 0;
        int blackQueens = 0;
        int blackKingLocation;

        {
            {
                Piece whiteKing = whites.get(0);
                int moveCount = whiteKing.getMoveCount();
                if (grid.getTile(whiteKingLocation = ((whiteKing.getRow() * ChessConstants.LENGTH) + whiteKing.getColumn())).protectedByBlack()) {
                    whiteScore -= CHECKED_VALUE;
                    if (moveCount > 1) {
                        whiteScore -= KING_MOVED_VALUE;
                    }
                }
                else if (moveCount > 1) {
                    whiteScore -= KING_MOVED_VALUE;
                }
                else if (moveCount == 1) {
                    switch (whiteKing.getColumn()) {
                        case ChessConstants.LEFT_KING_CASTLE_COLUMN: {
                            whiteScore += CASTLE_VALUE;
                            break;
                        }
                        case ChessConstants.RIGHT_KING_CASTLE_COLUMN: {
                            whiteScore += CASTLE_VALUE;
                        }
                    }
                }
            }

            int index = 1;

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isQueen()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += QUEEN_VALUE;
                    whiteScore += QUEEN_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++whiteQueens;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isRook()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += ROOK_VALUE;
                    whiteScore += ROOK_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++whiteRooks;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isBishop()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += BISHOP_VALUE;
                    whiteScore += BISHOP_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++whiteBishops;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isKnight()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += KNIGHT_VALUE;
                    whiteScore += KNIGHT_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++whiteKnights;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfWhitePieces) {
                Piece piece = whites.get(index);
                if (piece.isPawn()) {
                    whiteScore += piece.getNumberOfProtectedTiles(grid);
                    whiteScore += PAWN_VALUE;
                    whiteScore += PAWN_POSITION_WHITE[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++index;
                }
                else {
                    break;
                }
            }
        }
        
        {
            {
                Piece blackKing = blacks.get(0);
                int moveCount = blackKing.getMoveCount();
                if (grid.getTile(blackKingLocation = ((blackKing.getRow() * ChessConstants.LENGTH) + blackKing.getColumn())).protectedByWhite()) {
                    blackScore -= CHECKED_VALUE;
                    if (moveCount > 1) {
                        blackScore -= KING_MOVED_VALUE;
                    }
                }
                else if (moveCount > 1) {
                    blackScore -= KING_MOVED_VALUE;
                }
                else if (moveCount == 1) {
                    switch (blackKing.getColumn()) {
                        case ChessConstants.LEFT_KING_CASTLE_COLUMN: {
                            blackScore += CASTLE_VALUE;
                            break;
                        }
                        case ChessConstants.RIGHT_KING_CASTLE_COLUMN: {
                            blackScore += CASTLE_VALUE;
                        }
                    }
                }
            }

            int index = 1;

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isQueen()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += QUEEN_VALUE;
                    blackScore += QUEEN_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++blackQueens;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isRook()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += ROOK_VALUE;
                    blackScore += ROOK_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++blackRooks;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isBishop()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += BISHOP_VALUE;
                    blackScore += BISHOP_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++blackBishops;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isKnight()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += KNIGHT_VALUE;
                    blackScore += KNIGHT_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++blackKnights;
                    ++index;
                }
                else {
                    break;
                }
            }

            while (index < numberOfBlackPieces) {
                Piece piece = blacks.get(index);
                if (piece.isPawn()) {
                    blackScore += piece.getNumberOfProtectedTiles(grid);
                    blackScore += PAWN_VALUE;
                    blackScore += PAWN_POSITION_BLACK[(piece.getRow() * ChessConstants.LENGTH) + piece.getColumn()];
                    ++index;
                }
                else {
                    break;
                }
            }
        }

        if (((blackQueens == 0 && blackRooks <= 1)
                || ((blackQueens == 1 && blackKnights == 1 && blackBishops == 0 && blackRooks == 0) || (blackQueens == 1 && blackKnights == 0
                && blackBishops == 1 && blackRooks == 0))) && ((whiteQueens == 0 && whiteRooks <= 1)
                || ((whiteQueens == 1 && whiteKnights == 1 && whiteBishops == 0 && whiteRooks == 0) || (whiteQueens == 1 && whiteKnights == 0
                && whiteBishops == 1 && whiteRooks == 0)))) {
            blackScore += KING_POSITION_END_BLACK[blackKingLocation];
            whiteScore += KING_POSITION_END_WHITE[whiteKingLocation];
        }
        else {
            blackScore += KING_POSITION_MIDDLE_BLACK[blackKingLocation];
            whiteScore += KING_POSITION_MIDDLE_WHITE[whiteKingLocation];
            if (blackBishops >= 2) {
                blackScore += BISHOP_BONUS;
            }
            if (whiteBishops >= 2) {
                whiteScore += BISHOP_BONUS;
            }
        }

        int score = blackScore - whiteScore;
        AI.BLACK_STORE.putEntry(grid, score);
        return score;
    }
}