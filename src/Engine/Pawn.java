package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("EqualsAndHashcode")
public final class Pawn extends Piece {
    
    private boolean canEnPassant = true;

    public Pawn(int row, int column, boolean color) {
        super(row, column, color);
    }
    
    public Pawn(int row, int column, int moveCount, boolean color) {
        super(row, column, moveCount, color);
    }
    
    @Override
    public char getSymbol() {
        return isWhite() ? ChessConstants.WHITE_PAWN : ChessConstants.BLACK_PAWN;
    }
    
    @Override
    public boolean isPawn() {
        return true;
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Pawn clone() {
        Pawn copy = new Pawn(getRow(), getColumn(), getMoveCount(), isWhite());
        copy.canEnPassant = canEnPassant;
        return copy;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (!(obj instanceof Pawn)) ? false : (super.equals(obj) && (canEnPassant == ((Pawn) obj).canEnPassant));
    }
    
    @Override
    public int getValue() {
        return ChessConstants.PAWN_VALUE;
    }

    @Override
    public String getType() {
        return ChessConstants.PAWN;
    }

    @Override
    public List<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_PAWN_PROTECTED_TILES);
        if (isBlack()) {
            int nextRow = getRow() + 1;
            if (!hasMoved()) {
                final int currentColumn = getColumn();
                Tile nextTileDown = grid.getTile(nextRow, currentColumn);
                if (!nextTileDown.isOccupied()) {
                    list.add(nextTileDown);
                    Tile nextNextTileDown = grid.getTile(nextRow + 1, currentColumn);
                    if (!nextNextTileDown.isOccupied()) {
                        list.add(nextNextTileDown);
                    }
                }
                return list;
            }
            if (nextRow < LENGTH) {
                Tile nextTileDown = grid.getTile(nextRow, getColumn());
                if (!nextTileDown.isOccupied()) {
                    list.add(nextTileDown);
                }
            }
            return list;
        }
        int nextRow = getRow() - 1;
        if (!hasMoved()) {
            final int currentColumn = getColumn();
            Tile nextTileUp = grid.getTile(nextRow, currentColumn);
            if (!nextTileUp.isOccupied()) {
                list.add(nextTileUp);
                Tile nextNextTileUp = grid.getTile(nextRow - 1, currentColumn);
                if (!nextNextTileUp.isOccupied()) {
                    list.add(nextNextTileUp);
                }
            }
            return list;
        }
        if (nextRow >= 0) {
            Tile nextTileUp = grid.getTile(nextRow, getColumn());
            if (!nextTileUp.isOccupied()) {
                list.add(nextTileUp);
            }
        }
        return list;
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_PAWN_PROTECTED_TILES);
        if (isBlack()) {
            int nextRow = getRow() + 1;
            if (nextRow < LENGTH) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    Tile tile = grid.getTile(nextRow, nextColumn);
                    if (tile.isOccupiedByWhite()) {
                        list.add(tile);
                    }
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    Tile tile = grid.getTile(nextRow, nextColumn);
                    if (tile.isOccupiedByWhite()) {
                        list.add(tile);
                    }
                }
            }
            return list;
        }
        int nextRow = getRow() - 1;
        if (nextRow >= 0) {
            final int currentColumn = getColumn();
            int nextColumn = currentColumn - 1;
            if (nextColumn >= 0) {
                Tile tile = grid.getTile(nextRow, nextColumn);
                if (tile.isOccupiedByBlack()) {
                    list.add(tile);
                }
            }
            if ((nextColumn = currentColumn + 1) < LENGTH) {
                Tile tile = grid.getTile(nextRow, nextColumn);
                if (tile.isOccupiedByBlack()) {
                    list.add(tile);
                }
            }
        }
        return list;
    }

    @Override
    public List<Tile> getProtectedTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_PAWN_PROTECTED_TILES);
        if (isBlack()) {
            final int nextRow = getRow() + 1;
            if (nextRow < LENGTH) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
            }
        }
        else {
            final int nextRow = getRow() - 1;
            if (nextRow >= 0) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
            }
        }
        return list;
    }
    
    @Override
    public void setProtectedTiles(Grid grid) {
        if (isBlack()) {
            final int nextRow = getRow() + 1;
            if (nextRow < LENGTH) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    grid.getTile(nextRow, nextColumn).setProtectedBy(this);
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    grid.getTile(nextRow, nextColumn).setProtectedBy(this);
                }
            }
        }
        else {
            final int nextRow = getRow() - 1;
            if (nextRow >= 0) {
                final int currentColumn = getColumn();
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    grid.getTile(nextRow, nextColumn).setProtectedBy(this);
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    grid.getTile(nextRow, nextColumn).setProtectedBy(this);
                }
            }
        }
    }
    
    @Override
    public int getNumberOfProtectedTiles(Grid grid) {
        int count = 0;
        if (isBlack()) {
            if ((getRow() + 1) < LENGTH) {
                final int currentColumn = getColumn();
                if ((currentColumn - 1) >= 0) {
                    ++count;
                }
                if ((currentColumn + 1) < LENGTH) {
                    return ++count;
                }
            }
            return count;
        }
        else {
            if ((getRow() - 1) >= 0) {
                final int currentColumn = getColumn();
                if ((currentColumn - 1) >= 0) {
                    ++count;
                }
                if ((currentColumn + 1) < LENGTH) {
                    return ++count;
                }
            }
            return count;
        }
    }
    
    public boolean canEnPassant() {
        return canEnPassant;
    }
    
    public void setEnPassantPermission(boolean permission) {
        canEnPassant = permission;
    }
    
    //should be psesdo-legal, generate positions even if the pawn had already lost its 
    //right to enpassant.
    public List<Tile> getEnPassantTiles(Grid grid) {
        List<Tile> enPassantTiles = new ArrayList<>(ChessConstants.NUMBER_OF_PAWN_PROTECTED_TILES);
        Tile enPassantTile = getLeftEnPassantTile(grid);
        if (enPassantTile != null) {
            enPassantTiles.add(enPassantTile);
        }
        if ((enPassantTile = getRightEnPassantTile(grid)) != null) {
            enPassantTiles.add(enPassantTile);
        }
        return enPassantTiles;
    }
    
    public Tile getLeftEnPassantTile(Grid grid) {
        final int currentRow = getRow();
        if (isWhite()) {
            if (currentRow != ChessConstants.WHITE_ENPASSANT_ROW) {
                return null;
            }
            final int leftColumn = getColumn() - 1;
            if (leftColumn >= 0) {
                Tile leftEnPassantTile = grid.getTile(currentRow - 1, leftColumn);
                Tile blackPawnTile = grid.getTile(currentRow, leftColumn);
                if (blackPawnTile.isOccupied()) {
                    Piece blackPawn = blackPawnTile.getOccupant();
                    if (blackPawn.isPawn() && blackPawn.isBlack() && !leftEnPassantTile.isOccupied()) {
                        return leftEnPassantTile;
                    }
                }
            }
        }
        else {
            if (currentRow != ChessConstants.BLACK_ENPASSANT_ROW) {
                return null;
            }
            final int leftColumn = getColumn() - 1;
            if (leftColumn >= 0) {
                Tile leftEnPassantTile = grid.getTile(currentRow + 1, leftColumn);
                Tile whitePawnTile = grid.getTile(currentRow, leftColumn);
                if (whitePawnTile.isOccupied()) {
                    Piece whitePawn = whitePawnTile.getOccupant();
                    if (whitePawn.isPawn() && whitePawn.isWhite() && !leftEnPassantTile.isOccupied()) {
                        return leftEnPassantTile;
                    }
                }
            }
        }
        return null;
    }
    
    public Tile getRightEnPassantTile(Grid grid) {
        final int currentRow = getRow();
        if (isWhite()) {
            if (currentRow != ChessConstants.WHITE_ENPASSANT_ROW) {
                return null;
            }
            final int rightColumn = getColumn() + 1;
            if (rightColumn < ChessConstants.LENGTH) {
                Tile rightEnPassantTile = grid.getTile(currentRow - 1, rightColumn);
                Tile blackPawnTile = grid.getTile(currentRow, rightColumn);
                if (blackPawnTile.isOccupied()) {
                    Piece blackPawn = blackPawnTile.getOccupant();
                    if (blackPawn.isPawn() && blackPawn.isBlack() && !rightEnPassantTile.isOccupied()) {
                        return rightEnPassantTile;
                    }
                }
            }
        }
        else {
            if (currentRow != ChessConstants.BLACK_ENPASSANT_ROW) {
                return null;
            }
            final int rightColumn = getColumn() + 1;
            if (rightColumn < ChessConstants.LENGTH) {
                Tile rightEnPassantTile = grid.getTile(currentRow + 1, rightColumn);
                Tile whitePawnTile = grid.getTile(currentRow, rightColumn);
                if (whitePawnTile.isOccupied()) {
                    Piece whitePawn = whitePawnTile.getOccupant();
                    if (whitePawn.isPawn() && whitePawn.isWhite() && !rightEnPassantTile.isOccupied()) {
                        return rightEnPassantTile;
                    }
                }
            }
        }
        return null;
    }

    public boolean canPuesdoEnPassant(Grid grid) {
        return !getEnPassantTiles(grid).isEmpty();
    }
    
    public static Queen promote(Piece pawn) {
        return new Queen(pawn.getRow(), pawn.getColumn(), pawn.getMoveCount(), pawn.isWhite());
    }
}