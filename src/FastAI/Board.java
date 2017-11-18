package FastAI;

import Util.ChessConstants;
import java.util.Arrays;

public class Board {
    
    public static final long L = 1L;

    long whitePawns;
    long whiteKnights;
    long whiteBishops;
    long whiteRooks;
    long whiteQueens;
    long whiteKings;
    
    long whitePieces;

    long blackPawns;
    long blackKnights;
    long blackBishops;
    long blackRooks;
    long blackQueens;
    long blackKings;
    
    long blackPieces; //positions of black pieces on chess board.

    long moveCounts; //default to all zero, no bits set.

    long whiteProtections; //must intialize manually
    long blackProtections; //must initialize manually

    public Board() {
        whitePawns =   0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        whiteKnights = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_01000010L;
        whiteBishops = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00100100L;
        whiteRooks =   0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_10000001L;
        whiteQueens =  0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00010000L;
        whiteKings =   0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00001000L;

        //to loop from [0][0] to [7][7] start at 63 and stop at 0
        //could hard code white pieces, but this initialization code shows the process
        blackPawns =   0b00000000_11111111_00000000_00000000_00000000_00000000_00000000_00000000L;
        blackKnights = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        blackBishops = 0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        blackRooks =   0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        blackQueens =  0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
        blackKings =   0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;

        for (int index = 63; index >= 0; --index) {
            long shift = L << index;
            if ((whitePawns & shift) != 0) {
                whitePieces |= shift;
            }
            else if ((whiteKnights & shift) != 0) {
                whitePieces |= shift;
            }
            else if ((whiteBishops & shift) != 0) {
                whitePieces |= shift;
            }
            else if ((whiteRooks & shift) != 0) {
                whitePieces |= shift;
            }
            else if ((whiteQueens & shift) != 0) {
                whitePieces |= shift;
            }
            else if ((whiteKings & shift) != 0) {
                whitePieces |= shift;
            }

            else if ((blackPawns & shift) != 0) {
                blackPieces |= shift;
            }
            else if ((blackKnights & shift) != 0) {
                blackPieces |= shift;
            }
            else if ((blackBishops & shift) != 0) {
                blackPieces |= shift;
            }
            else if ((blackRooks & shift) != 0) {
                blackPieces |= shift;
            }
            else if ((blackQueens & shift) != 0) {
                blackPieces |= shift;
            }
            else if ((blackKings & shift) != 0) {
                blackPieces |= shift;
            }
        }
    }

    public Board(Board grid) {
        whitePawns = grid.whitePawns;
        whiteKnights = grid.whiteKnights;
        whiteBishops = grid.whiteBishops;
        whiteRooks = grid.whiteRooks;
        whiteQueens = grid.whiteQueens;
        whiteKings = grid.whiteKings;

        blackPawns = grid.blackPawns;
        blackKnights = grid.blackKnights;
        blackBishops = grid.blackBishops;
        blackRooks = grid.blackRooks;
        blackQueens = grid.blackQueens;
        blackKings = grid.blackKings;
    }

    @Override
    public String toString() {
        char[][] board = new char[ChessConstants.LENGTH][ChessConstants.LENGTH];
        for (int row = 0, column = 0, index = (ChessConstants.LINEAR_LENGTH - 1); index >= 0; --index) {
            if (column == ChessConstants.LENGTH) {
                ++row;
                column = 0;
            }
            long shift = L << index;
            if ((whitePawns & shift) != 0) {
                board[row][column] = ChessConstants.WHITE_PAWN;
            }
            else if ((whiteKnights & shift) != 0) {
                board[row][column] = ChessConstants.WHITE_KNIGHT;
            }
            else if ((whiteBishops & shift) != 0) {
                board[row][column] = ChessConstants.WHITE_BISHOP;
            }
            else if ((whiteRooks & shift) != 0) {
                board[row][column] = ChessConstants.WHITE_ROOK;
            }
            else if ((whiteQueens & shift) != 0) {
                board[row][column] = ChessConstants.WHITE_QUEEN;
            }
            else if ((whiteKings & shift) != 0) {
                board[row][column] = ChessConstants.WHITE_KING;
            }

            else if ((blackPawns & shift) != 0) {
                board[row][column] = ChessConstants.BLACK_PAWN;
            }
            else if ((blackKnights & shift) != 0) {
                board[row][column] = ChessConstants.BLACK_KNIGHT;
            }
            else if ((blackBishops & shift) != 0) {
                board[row][column] = ChessConstants.BLACK_BISHOP;
            }
            else if ((blackRooks & shift) != 0) {
                board[row][column] = ChessConstants.BLACK_ROOK;
            }
            else if ((blackQueens & shift) != 0) {
                board[row][column] = ChessConstants.BLACK_QUEEN;
            }
            else if ((blackKings & shift) != 0) {
                board[row][column] = ChessConstants.BLACK_KING;
            }
            else {
                board[row][column] = '.';
            }
            ++column;
        }
        StringBuilder result = new StringBuilder(ChessConstants.LINEAR_LENGTH);
        for (int index = 0, lastIndex = (ChessConstants.LENGTH - 1); index <= lastIndex; ++index) {
            result.append(Arrays.toString(board[index]));
            if (index == lastIndex) {
                break;
            }
            result.append("\n");
        }
        return result.toString();
    }
    
    public static String bitBoard(long l) {
        String result = "";
        
        for (int index = (ChessConstants.LINEAR_LENGTH - 1); index >= 0; --index) {
            if ((l & (L << index)) != 0) {
                result += "1";
            }
            else {
                result += "0";
            }
            if (index % ChessConstants.LENGTH == 0) {
                result += "\n";
            }
        }
        
        return result;
    }

    public static void main(String[] args) {
        Board board = new Board();
        long l = 10;
        l |= 4; // sets 3rd bit, from bottom of [8][8]
        l &= ~4; // unset 3rd bit
        long a = board.whitePawns;
        System.out.println(bitBoard(a));
        a = 0;
        a |= (L << 0);//zero based index, zero at the [8][8] 63 at [0][0]
        System.out.println();
        System.out.println(bitBoard(a));
        System.out.println();
        System.out.println(bitBoard(board.whitePieces));
        System.out.println(bitBoard(board.blackPieces));
        System.out.println(board);
        System.out.println(L);
        System.out.println(Bishop.generateAttack(board, 58, true));
    }
}
