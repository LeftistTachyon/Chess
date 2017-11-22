package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.BLACK;
import static Util.ChessConstants.WHITE;
import Util.Constants;
import static Util.Constants.SPACE;
import java.util.List;

@SuppressWarnings("EqualsAndHashcode")
public abstract class Piece implements Cloneable, Comparable<Piece>, Locatable {

    private int row;
    private int column;
    private int moveCount;
    private final boolean color;
    
    protected Piece(int row, int column, boolean color) {
        this.row = row;
        this.column = column;
        this.color = color;
    }
    
    protected Piece(int row, int column, int moveCount, boolean color) {
        this.row = row;
        this.column = column;
        this.moveCount = moveCount;
        this.color = color;
    }

    @Override
    public final int getRow() {
        return row;
    }

    @Override
    public final int getColumn() {
        return column;
    }

    @Override
    public final void setRow(int row) {
        this.row = row;
    }

    @Override
    public final void setColumn(int column) {
        this.column = column;
    }

    @Override
    public final void setLocation(int row, int column) {
        this.row = row;
        this.column = column;
    }
    
    /**
     * Returns true if this piece is white, false otherwise. This
     * is an convince method.
     * @see isBlack()
     * @return {@code true} if this piece is white, false otherwise.
     */
    public final boolean isWhite() {
        return color;
    }
    
    /**
     * Returns true if this piece is black, false otherwise. This
     * is an convince method.
     * @see isWhite()
     * @return {@code true} if this piece is black, false otherwise.
     */
    public final boolean isBlack() {
        return !color;
    }

    /**
     * Determines whether or not the given piece is an ally or
     * foe of this piece.
     * @param other Another piece object.
     * @return {@code true} if the other piece object is an ally, false otherwise.
     */
    public final boolean isAlly(Piece other) {
        return color == other.color;
    }

    /**
     * Returns true if this piece has moved, false otherwise.
     * @return {@code true} if this piece has moved, false otherwise.
     */
    public final boolean hasMoved() {
        return moveCount != 0;
    }
    
    /**
     * Get the number of times this piece has moved.
     * @see setMoveCount(int)
     * @see increaseMoveCount()
     * @see decreaseMoveCount()
     * @return The number of times this piece has moved.
     */
    public final int getMoveCount() {
        return moveCount;
    }
    
    /**
     * Sets the number of times this piece has moved.
     * @param moveCount 
     */
    public final void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }
    
    /**
     * Increases the number of times this piece has moved by 1.
     * @see setMoveCount(int)
     * @see decreaseMoveCount()
     */
    public final void increaseMoveCount() {
        ++moveCount;
    }

    /**
     * Decreases the number of times this piece has moved by 1.
     * @see setMoveCount(int)
     * @see increaseMoveCount()
     */
    public final void decreaseMoveCount() {
        --moveCount;
    }

    //less comparison is faster.
    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        
        //return (obj == this); 

        //we could compare references, since no 
        //objects are copied and created,
        //we use references and heavily 
        //modify them and change and un-change
        //their states. Each object is unique.
        
        
        /* No need to check type, bugs should fail when attempting to cast
        if (!(obj instanceof Piece)) {
            return false;
        }
         */
      
        Piece other = (Piece) obj;
        return (row == other.row)
                && (column == other.column)
                && (color == other.color)
                && (moveCount == other.moveCount)
                && (Constants.equals(getType(), other.getType()));
    }

    /**
     * Returns an exact deep copy of this piece that is
     * independent of this piece. This policy overrides
     * the original policy of {@link java.lang.Object#clone()}.
     * @return An exact deep copy of this piece.
     */
    @Override
    public abstract Piece clone();

    /**
     * Gets the value of this piece (always positive).
     * @return The value of this piece.
     */
    public abstract int getValue();
    
    /**
     * Gets the text symbol of this piece. This symbol
     * depends on the color of this piece.
     * @return The text symbol of this piece.
     */
    public abstract char getSymbol();
    
    /**
     * Gets the type of this piece. 
     * @see ChessConstants.PAWN
     * @see ChessConstants.KNIGHT
     * @see ChessConstants.BISHOP
     * @see ChessConstants.ROOK
     * @see ChessConstants.QUEEN
     * @see ChessConstants.KING
     * @return The type of this piece.
     */
    public abstract String getType();
    
    /**
     * Gets the full name of this piece which is its color (White or Black)
     * followed by a space and its type.
     * 
     * @see getType()
     * @see isWhite()
     * @see isBlack()
     * @return The full name of this piece.
     */
    public final String getName() {
        return (color) ? WHITE + SPACE + getType() : BLACK + SPACE + getType();
    }

    /**
     * Gets the various tiles where this piece can move to.
     * @param grid The given board of pieces.
     * @return The various tiles where this piece can move to.
     */
    public abstract List<Tile> getMoveTiles(Grid grid);
    
    /**
     * Gets the various tiles where this piece can capture enemy pieces.
     * @param grid The given board of pieces.
     * @return The various tiles where this piece can capture enemy pieces.
     */
    public abstract List<Tile> getAttackTiles(Grid grid);

    /**
     * Gets the various tiles where this piece affa
     * @param grid
     * @return 
     */
    public abstract List<Tile> getProtectedTiles(Grid grid);

    public abstract void setProtectedTiles(Grid grid);

    public abstract int getNumberOfProtectedTiles(Grid grid);

    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public boolean justMadeDoubleJump() {
        throw new UnsupportedOperationException();
    }

    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public void setJustMadeDoubleJump(boolean doubleJumpJustPerformed) {
        throw new UnsupportedOperationException();
    }
    
    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public Tile getLeftEnPassantTile(Grid grid) {
        throw new UnsupportedOperationException();
    }
    
    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public Tile getRightEnPassantTile(Grid grid) {
        throw new UnsupportedOperationException();
    }
    
    //method which only applies to Pawn, defined here
    //to avoid the need for casting
    public List<Tile> getEnPassantTiles(Grid grid) {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines whether of not this piece is a {@link Pawn}.
     * @return {@code true} if this piece is a pawn.
     */
    public boolean isPawn() {
        return false;
    }
    
    /**
     * Determines whether of not this piece is a {@link Knight}.
     * @return {@code true} if this piece is a knight.
     */
    public boolean isKnight() {
        return false;
    }
    
    /**
     * Determines whether of not this piece is a {@link Pawn}.
     *
     * @return {@code true} if this piece is a pawn.
     */
    public boolean isBishop() {
        return false;
    }

    /**
     * Determines whether of not this piece is a {@link Rook}.
     *
     * @return {@code true} if this piece is a rook.
     */
    public boolean isRook() {
        return false;
    }

    /**
     * Determines whether of not this piece is a {@link Queen}.
     *
     * @return {@code true} if this piece is a queen.
     */
    public boolean isQueen() {
        return false;
    }
    
    /**
     * Determines whether of not this piece is a {@link King}.
     * @return {@code true} if this piece is a king.
     */
    public boolean isKing() {
        return false;
    }
    
    /**
     * Encodes the all the properties of this piece except the number of times it
     * has moved. This method is used by {@see AI} in order to avoid threefold 
     * repetition.
     * @return 
     */
    public String encode() {
        return "(" + color + "," + getType() + "," + row + "," + column + ")";
    }
    
    /**
     * Compares this piece vs the given piece by their 
     * exact value.
     * @param other Another piece object.
     * @return 
     */
    @Override
    public final int compareTo(Piece other) {
        return Integer.compare(getValue(), other.getValue());
    }
}