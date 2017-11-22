package Engine;

import Util.ChessConstants;
import static Util.Constants.SPACE;
import Util.Converter;

/**
 * Utility class to convert a GUI.Piece format text to 
 * a Engine.Piece object and vise versa.
 */
public final class PieceConverter extends Converter<String, Piece> {
    
    public static final PieceConverter PIECE_CONVERTER = new PieceConverter();
    
    private PieceConverter() {
        
    }

    @Override
    public final Piece convertForward(String str) {
        if (str == null) {
            throw new NullPointerException();
        }
        try {
            String[] read = str.split(SPACE);
            switch (read[4]) {
                case ChessConstants.PAWN: {
                    Pawn pawn = new Pawn(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
                    pawn.setJustMadeDoubleJump(Boolean.parseBoolean(read[5]));
                    return pawn;
                }
                case ChessConstants.KNIGHT: {
                    return new Knight(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
                }
                case ChessConstants.BISHOP: {
                    return new Bishop(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
                }
                case ChessConstants.ROOK: {
                    return new Rook(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
                }
                case ChessConstants.QUEEN: {
                    return new Queen(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
                }
                case ChessConstants.KING: {
                    return new King(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
                }
            }
        }
        catch (RuntimeException ex) {

        }
        return null;
    }

    @Override
    public final String convertBackward(Piece piece) {
        if (piece == null) {
            throw new NullPointerException();
        }
        String result = piece.getRow() + SPACE + piece.getColumn() + SPACE + piece.getMoveCount() + SPACE + piece.isWhite() + SPACE + piece.getType();
        if (piece.isPawn()) {
            return result + SPACE + piece.justMadeDoubleJump();
        }
        return result;
    }
}