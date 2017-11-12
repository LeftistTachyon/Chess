package Engine;

import Util.ChessConstants;
import Util.Constants;
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
            String[] read = str.split(Constants.SPACE);
            switch (read[4]) {
                case ChessConstants.PAWN: {
                    return new Pawn(Integer.parseInt(read[0]), Integer.parseInt(read[1]), Integer.parseInt(read[2]), Boolean.parseBoolean(read[3]));
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
        return piece.getRow() + Constants.SPACE + piece.getColumn() + Constants.SPACE + piece.getMoveCount() + Constants.SPACE + piece.isWhite() + Constants.SPACE + piece.getType();
    }
}