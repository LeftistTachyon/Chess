package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.MAX_NUMBER_OF_ROOK_MOVE_TILES;
import java.util.ArrayList;
import java.util.List;

public final class Rook extends Piece {

    public Rook(int row, int column, boolean color) {
        super(row, column, color);
    }
    
    public Rook(int row, int column, int moveCount, boolean color) {
        super(row, column, moveCount, color);
    }
    
    @Override
    public char getSymbol() {
        return isWhite() ? ChessConstants.WHITE_ROOK : ChessConstants.BLACK_ROOK;
    }
    
    @Override
    public boolean isRook() {
        return true;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Rook clone() {
        return new Rook(getRow(), getColumn(), getMoveCount(), isWhite());
    }

    @Override
    public int getValue() {
        return ChessConstants.ROOK_VALUE;
    }

    @Override
    public String getType() {
        return ChessConstants.ROOK;
    }
    
    @Override
    public List<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(MAX_NUMBER_OF_ROOK_MOVE_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        return list;
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
            Tile tile = grid.getTile(nextRow, currentColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
            Tile tile = grid.getTile(currentRow, nextColumn);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        return list;
    }

    @Override
    public List<Tile> getProtectedTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>();
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0;) {
            Tile tile = grid.getTile(nextRow--, currentColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, currentColumn);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0;) {
            Tile tile = grid.getTile(currentRow, nextColumn--);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH;) {
            Tile tile = grid.getTile(currentRow, nextColumn++);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        return list;
    }

    @Override
    public void setProtectedTiles(Grid grid) {
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0;) {
            Tile tile = grid.getTile(nextRow--, currentColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, currentColumn);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, currentColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0;) {
            Tile tile = grid.getTile(currentRow, nextColumn--);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH;) {
            Tile tile = grid.getTile(currentRow, nextColumn++);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(currentRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public int getNumberOfProtectedTiles(Grid grid) {
        int count = 0;
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1; nextRow >= 0;) {
            Tile tile = grid.getTile(nextRow--, currentColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0) {
                        if (!grid.getTile(nextRow, currentColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1; nextRow < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, currentColumn);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH) {
                        if (!grid.getTile(nextRow, currentColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn - 1; nextColumn >= 0;) {
            Tile tile = grid.getTile(currentRow, nextColumn--);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn >= 0) {
                        if (!grid.getTile(currentRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextColumn = currentColumn + 1; nextColumn < LENGTH;) {
            Tile tile = grid.getTile(currentRow, nextColumn++);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextColumn < LENGTH) {
                        if (!grid.getTile(currentRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        return count;
    }

    public int getNumberOfTilesTowardsEnemyKing(Grid grid) {
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        {
            int count = 0;
            for (int nextRow = currentRow - 1; nextRow >= 0;) {
                Tile tile = grid.getTile(nextRow--, currentColumn);
                ++count;
                if (tile.isOccupied()) {
                    Piece occupant = tile.getOccupant();
                    if (occupant.isKing() && !isAlly(occupant)) {
                        if (nextRow >= 0) {
                            if (!grid.getTile(nextRow, currentColumn).isOccupied()) {
                                return ++count;
                            }
                        }
                    }
                    break;
                }
            }
        }
        {
            int count = 0;
            for (int nextRow = currentRow + 1; nextRow < LENGTH;) {
                Tile tile = grid.getTile(nextRow++, currentColumn);
                ++count;
                if (tile.isOccupied()) {
                    Piece occupant = tile.getOccupant();
                    if (occupant.isKing() && !isAlly(occupant)) {
                        if (nextRow < LENGTH) {
                            if (!grid.getTile(nextRow, currentColumn).isOccupied()) {
                                return ++count;
                            }
                        }
                    }
                    break;
                }
            }
        }
        {

            int count = 0;
            for (int nextColumn = currentColumn - 1; nextColumn >= 0;) {
                Tile tile = grid.getTile(currentRow, nextColumn--);
                ++count;
                if (tile.isOccupied()) {
                    Piece occupant = tile.getOccupant();
                    if (occupant.isKing() && !isAlly(occupant)) {
                        if (nextColumn >= 0) {
                            if (!grid.getTile(currentRow, nextColumn).isOccupied()) {
                                return ++count;
                            }
                        }
                    }
                    break;
                }
            }
        }
        {
            int count = 0;
            for (int nextColumn = currentColumn + 1; nextColumn < LENGTH;) {
                Tile tile = grid.getTile(currentRow, nextColumn++);
                ++count;
                if (tile.isOccupied()) {
                    Piece occupant = tile.getOccupant();
                    if (occupant.isKing() && !isAlly(occupant)) {
                        if (nextColumn < LENGTH) {
                            if (!grid.getTile(currentRow, nextColumn).isOccupied()) {
                                return ++count;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return 0;
    }
}