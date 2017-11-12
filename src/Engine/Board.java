package Engine;

import Util.ChessConstants;
import java.util.ArrayList;
import java.util.List;

public final class Board {

    final Grid grid;

    final List<Piece> whites = new ArrayList<>(16);
    final King whiteKing;
    final List<Pawn> whitePawns = new ArrayList<>(8);
    final List<Knight> whiteKnights = new ArrayList<>(2);
    final List<Bishop> whiteBishops = new ArrayList<>(2);
    final List<Rook> whiteRooks = new ArrayList<>(2);
    final List<Queen> whiteQueens = new ArrayList<>(1);

    final List<Piece> blacks = new ArrayList<>(16);
    final King blackKing;
    final List<Pawn> blackPawns = new ArrayList<>(8);
    final List<Knight> blackKnights = new ArrayList<>(2);
    final List<Bishop> blackBishops = new ArrayList<>(2);
    final List<Rook> blackRooks = new ArrayList<>(2);
    final List<Queen> blackQueens = new ArrayList<>(1);

    public Board(Grid grid) {
        this.grid = grid;
        King white = null, black = null;
        for (int index = 0; index != ChessConstants.LINEAR_LENGTH; ++index) {
            Piece piece = grid.getTile(index).getOccupant();
            if (piece != null) {
                if (piece.isWhite()) {
                    whites.add(piece);
                    if (piece.isKing()) {
                        white = (King) piece;
                    }
                    else if (piece.isPawn()) {
                        whitePawns.add((Pawn) piece);
                    }
                    else if (piece.isKnight()) {
                        whiteKnights.add((Knight) piece);
                    }
                    else if (piece.isBishop()) {
                        whiteBishops.add((Bishop) piece);
                    }
                    else if (piece.isRook()) {
                        whiteRooks.add((Rook) piece);
                    }
                    else {
                        whiteQueens.add((Queen) piece);
                    }
                }
                else {
                    blacks.add(piece);
                    if (piece.isKing()) {
                        black = (King) piece;
                    }
                    else if (piece.isPawn()) {
                        blackPawns.add((Pawn) piece);
                    }
                    else if (piece.isKnight()) {
                        blackKnights.add((Knight) piece);
                    }
                    else if (piece.isBishop()) {
                        blackBishops.add((Bishop) piece);
                    }
                    else if (piece.isRook()) {
                        blackRooks.add((Rook) piece);
                    }
                    else {
                        blackQueens.add((Queen) piece);
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