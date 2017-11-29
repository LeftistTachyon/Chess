package Engine;

import Util.ChessConstants;
import java.util.ArrayList;
import java.util.List;

//BE CAREFUL WHEN REMOVING PIECES FROM 1 LIST
//IT WILL STILL BE IN ANOTHER LIST
//FOR EXAMPLE: IF YOU REMOVE A BLACK PAWN FROM 
//THE BLACK PIECES LIST IT WILL STILL
//BE IN THE BLACK PAWN LIST
public final class Board {

    final Grid grid;

    final List<Piece> whites = new ArrayList<>(16);
    final King whiteKing;
    
    final List<Piece> blacks = new ArrayList<>(16);
    final King blackKing;

    public Board(Grid board) {
        grid = board;
        King white = null, black = null;
        for (int index = 0; index != ChessConstants.LINEAR_LENGTH; ++index) {
            Piece piece = board.getTile(index).getOccupant();
            if (piece != null) {
                if (piece.isWhite()) {
                    whites.add(piece);
                    if (piece.isKing()) {
                        white = (King) piece;
                    }
                }
                else {
                    blacks.add(piece);
                    if (piece.isKing()) {
                        black = (King) piece;
                    }
                }
            }
        }
        whiteKing = white;
        blackKing = black;
    }
    
    public Board(Board board) {
        this(new Grid(board.grid));
    }
}