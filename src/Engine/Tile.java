package Engine;

import Util.Constants;
import Util.EmptyList;

@SuppressWarnings("EqualsAndHashcode")
public final class Tile implements Locatable {
    
    static final EmptyList<Tile> EMPTY_LIST = new EmptyList<>();
    
    private final int row;
    private final int column;
 
    private boolean protectedByWhite;
    private boolean protectedByBlack;
    
    private Piece occupant;
    
    public Tile(int row, int column) {
        this.row = row;
        this.column = column;
        protectedByWhite = protectedByBlack = false;
    }
    
    public Tile(Tile tile) {
        row = tile.row;
        column = tile.column;
        protectedByWhite = tile.protectedByWhite;
        protectedByBlack = tile.protectedByBlack;
        if (tile.occupant != null) {
            occupant = tile.occupant.clone();
        }
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
    @Deprecated
    public final void setRow(int row) {
        throw new UnsupportedOperationException("Cannot change row in Tile.");
    }

    @Override
    @Deprecated
    public final void setColumn(int column) {
        throw new UnsupportedOperationException("Cannot change column in Tile.");
    }

    @Override
    @Deprecated
    public final void setLocation(int row, int column) {
        throw new UnsupportedOperationException("Cannot change location in Tile.");
    }
    
    public boolean protectedByWhite() {
        return protectedByWhite;
    }

    public boolean protectedByBlack() {
        return protectedByBlack;
    }
    
    public boolean protectedByEnemy(Piece piece) {
        return piece.isWhite() ? protectedByBlack : protectedByWhite;
    }
    
    public boolean protectedByAlly(Piece piece) {
        return piece.isWhite() ? protectedByWhite : protectedByBlack;
    }
    
    public void setProtectedBy(Piece piece) { 
        if (piece.isWhite()) {
            protectedByWhite = true;
        }
        else {
            protectedByBlack = true;
        }
    }
    
    public void removeProtections() {
        protectedByWhite = protectedByBlack = false;
    }

    public boolean isOccupied() {
        return occupant != null;
    }
    
    public boolean isOccupiedByWhite() {
        return occupant != null && occupant.isWhite();
    }
    
    public boolean isOccupiedByBlack() {
        return occupant != null && occupant.isBlack();
    }
    
    public Piece getOccupant() {
        return occupant;
    }
    
    public Piece popOccupant() {
        Piece piece = occupant;
        occupant = null;
        return piece;
    }
    
    public void setOccupant(Piece newOccupant) {
        occupant = null;
        (occupant = newOccupant).setLocation(row, column);
    }

    public void removeOccupant() {
        occupant = null;
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        Tile other = (Tile) obj;
        return (row == other.row) 
                && (column == other.column)
                && (protectedByWhite == other.protectedByWhite)
                && (protectedByBlack == other.protectedByBlack)
                && Constants.equals(occupant, other.occupant);
    }

    @Override
    @Deprecated
    public String toString() {
        String result = "";
        if (occupant != null) {
            result += "Occupied by: " + occupant.getName() + "\n";
        }
        result += "Location: [" + row + ", " + column + "]\n";
        result += protectedByWhite ? "Protected By White\n" : "Not Protected By White\n";
        result += protectedByBlack ? "Protected By Black" : "Not Protected By Black";
        return result;
    }
}