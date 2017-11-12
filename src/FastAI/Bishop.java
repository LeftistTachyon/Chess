package FastAI;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import java.util.ArrayList;
import java.util.List;

public class Bishop {
    
    private Bishop() {
        
    }

    //return the positions where this bishop can attack at in the board.
    public static List<Integer> generateAttack(Board board, int currentIndex, boolean color) {
        List<Integer> positions = new ArrayList<>(4);
        int currentRow = ChessConstants.getRow(currentIndex);
        int currentColumn = ChessConstants.getColumn(currentIndex);
        if (color) {
            for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                int index = ChessConstants.getLocation(nextRow, nextColumn);
                if ((board.blackPieces & (1L << index)) != 0) {
                    positions.add(index); 
                    break;
                }
            }
            for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                int index = ChessConstants.getLocation(nextRow, nextColumn);
                if ((board.blackPieces & (1L << index)) != 0) {
                    positions.add(index); 
                    break;
                }
            }
            for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                int index = ChessConstants.getLocation(nextRow, nextColumn);
                if ((board.blackPieces & (1L << index)) != 0) {
                    positions.add(index); 
                    break;
                }
            }
            for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                int index = ChessConstants.getLocation(nextRow, nextColumn);
                if ((board.blackPieces & (1L << index)) != 0) {
                    positions.add(index); 
                    break;
                }
            }
        }
        else {
            for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                int index = ChessConstants.getLocation(nextRow, nextColumn);
                if ((board.whitePieces & (1L << index)) != 0) {
                    positions.add(index); 
                    break;
                }
            }
            for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                int index = ChessConstants.getLocation(nextRow, nextColumn);
                if ((board.whitePieces & (1L << index)) != 0) {
                    positions.add(index); 
                    break;
                }
            }
            for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                int index = ChessConstants.getLocation(nextRow, nextColumn);
                if ((board.whitePieces & (1L << index)) != 0) {
                    positions.add(index); 
                    break;
                }
            }
            for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                int index = ChessConstants.getLocation(nextRow, nextColumn);
                if ((board.whitePieces & (1L << index)) != 0) {
                    positions.add(index); 
                    break;
                }
            }
        }
        return positions;
    }
}
