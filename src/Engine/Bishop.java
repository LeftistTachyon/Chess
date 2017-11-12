package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.MAX_NUMBER_OF_BISHOP_MOVE_TILES;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a bishop in chess.
 * @author Will
 */
public final class Bishop extends Piece {

    public Bishop(int row, int column, boolean color) {
        super(row, column, color);
    }

    public Bishop(int row, int column, int moveCount, boolean color) {
        super(row, column, moveCount, color);
    }
    
    @Override
    public char getSymbol() {
        return isWhite() ? ChessConstants.WHITE_BISHOP : ChessConstants.BLACK_BISHOP;
    }
    
    @Override
    public boolean isBishop() {
        return true;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Bishop clone() {
        return new Bishop(getRow(), getColumn(), getMoveCount(), isWhite());
    }

    @Override
    public int getValue() {
        return ChessConstants.BISHOP_VALUE;
    }

    @Override
    public String getType() {
        return ChessConstants.BISHOP;
    }

    @Override
    public List<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(MAX_NUMBER_OF_BISHOP_MOVE_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            if (tile.isOccupied()) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
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
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        return list;
    }
    
    private static final int jump = 9;
    
    //this wont work, as diagonals need row and column indexing, and your direction is off
    private List<Tile> getAttackTilesHelper(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES);
        final int index = getIndex();
        //0,0 -> 0 
        //1,1 -> 8
        //jump by 9's
        
        //up-left diagonal
        for (int upLeftIndex = index - jump; upLeftIndex >= 0; upLeftIndex -= jump) {
            Tile tile = grid.getTile(upLeftIndex);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        
        //up-right diagonal
        for (int upRightIndex = index - 7; upRightIndex >= 7; upRightIndex -= 7) {
            Tile tile = grid.getTile(upRightIndex);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        
        //down-right diagonal
        for (int downRightIndex = index + jump; downRightIndex <= 63; downRightIndex += jump) {
            Tile tile = grid.getTile(downRightIndex);
            if (tile.isOccupied()) {
                if (!isAlly(tile.getOccupant())) {
                    list.add(tile);
                }
                break;
            }
        }
        
        //down-left
        for (int downLeftIndex = index + 7; downLeftIndex <= 56 ; downLeftIndex += 7) {
            Tile tile = grid.getTile(downLeftIndex);
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
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            list.add(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
            list.add(tile);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
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
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn >= 0) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
                        if (!pierceTile.isOccupied()) {
                            pierceTile.setProtectedBy(this);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
            tile.setProtectedBy(this);
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn < LENGTH) {
                        Tile pierceTile = grid.getTile(nextRow, nextColumn);
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
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow--, nextColumn--);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn >= 0) {
                        if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow--, nextColumn++);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow >= 0 && nextColumn < LENGTH) {
                        if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
            Tile tile = grid.getTile(nextRow++, nextColumn--);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn >= 0) {
                        if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
                            ++count;
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
            Tile tile = grid.getTile(nextRow++, nextColumn++);
            ++count;
            if (tile.isOccupied()) {
                Piece occupant = tile.getOccupant();
                if (occupant.isKing() && !isAlly(occupant)) {
                    if (nextRow < LENGTH && nextColumn < LENGTH) {
                        if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
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
            for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                Tile tile = grid.getTile(nextRow--, nextColumn--);
                ++count;
                if (tile.isOccupied()) {
                    Piece occupant = tile.getOccupant();
                    if (occupant.isKing() && !isAlly(occupant)) {
                        if (nextRow >= 0 && nextColumn >= 0) {
                            if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
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
            for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                Tile tile = grid.getTile(nextRow--, nextColumn++);
                ++count;
                if (tile.isOccupied()) {
                    Piece occupant = tile.getOccupant();
                    if (occupant.isKing() && !isAlly(occupant)) {
                        if (nextRow >= 0 && nextColumn < LENGTH) {
                            if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
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
            for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                Tile tile = grid.getTile(nextRow++, nextColumn--);
                ++count;
                if (tile.isOccupied()) {
                    Piece occupant = tile.getOccupant();
                    if (occupant.isKing() && !isAlly(occupant)) {
                        if (nextRow < LENGTH && nextColumn >= 0) {
                            if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
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
            for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                Tile tile = grid.getTile(nextRow++, nextColumn++);
                ++count;
                if (tile.isOccupied()) {
                    Piece occupant = tile.getOccupant();
                    if (occupant.isKing() && !isAlly(occupant)) {
                        if (nextRow < LENGTH && nextColumn < LENGTH) {
                            if (!grid.getTile(nextRow, nextColumn).isOccupied()) {
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