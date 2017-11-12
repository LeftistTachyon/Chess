package GUI;

import java.util.ArrayList;
import java.util.List;
import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;

//Status: Done, note enPassant is disabled
@SuppressWarnings("EqualsAndHashcode")
public final class Pawn extends Piece {
    
    private boolean canEnPassant = true;
    
    public Pawn(int x, int y, int width, int height, int row, int column, boolean color) {
        super(x, y, width, height, row, column, color, ChessConstants.PAWN);
    }
    
    public Pawn(int row, int column, boolean color) {
        super(row, column, color, ChessConstants.PAWN);
    }
    
    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Pawn clone() {
        Pawn copy = new Pawn(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
        copy.setSelected(isSelected());
        copy.setMoveCount(getMoveCount());
        copy.canEnPassant = canEnPassant;
        return copy;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (!(obj instanceof Pawn)) ? false : (super.equals(obj) && (canEnPassant == ((Pawn) obj).canEnPassant));
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
    public List<Tile> getMoveTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_PAWN_PROTECTED_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        if (isBlack()) {
            int nextRow = currentRow + 1;
            if (!hasMoved()) {
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
                Tile nextTileDown = grid.getTile(nextRow, currentColumn);
                if (!nextTileDown.isOccupied()) {
                    list.add(nextTileDown);
                }
            }
            return list;
        }
        int nextRow = currentRow - 1;
        if (!hasMoved()) {
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
            Tile nextTileUp = grid.getTile(nextRow, currentColumn);
            if (!nextTileUp.isOccupied()) {
                list.add(nextTileUp);
            }
        }
        return list;
    }

    @Override
    public List<Tile> getAttackTiles(Grid grid) {
        final List<Tile> list = new ArrayList<>(ChessConstants.NUMBER_OF_PAWN_PROTECTED_TILES);
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        if (isBlack()) {
            int nextRow = currentRow + 1;
            if (nextRow < LENGTH) {
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
        int nextRow = currentRow - 1;
        if (nextRow >= 0) {
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
        final int currentRow = getRow();
        final int currentColumn = getColumn();
        if (isBlack()) {
            final int nextRow = currentRow + 1;
            if (nextRow < LENGTH) {
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
                nextColumn = currentColumn + 1;
                if (nextColumn < LENGTH) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
            }
        }
        else {
            final int nextRow = currentRow - 1;
            if (nextRow >= 0) {
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    list.add(grid.getTile(nextRow, nextColumn));
                }
                nextColumn = currentColumn + 1;
                if (nextColumn < LENGTH) {
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

    public void setEnPassantPermission(boolean enPassantRight) {
        canEnPassant = enPassantRight;
    }

    public boolean enPassantAuthorized() {
        return canEnPassant;
    }

    public List<Tile> getEnPassantTiles(Grid grid) {
        List<Tile> enPassantTiles = new ArrayList<>(ChessConstants.NUMBER_OF_PAWN_PROTECTED_TILES);
        Tile leftEnPassantTile = getLeftEnPassantTile(grid);
        if (leftEnPassantTile != null) {
            enPassantTiles.add(leftEnPassantTile);
        }
        Tile rightEnPassantTile = getRightEnPassantTile(grid);
        if (rightEnPassantTile != null) {
            enPassantTiles.add(rightEnPassantTile);
        }
        return enPassantTiles;
    }
    
    public Tile getLeftEnPassantTile(Grid grid) {
        final int currentRow = getRow();
        if (isWhite()) {
            if (currentRow != ChessConstants.WHITE_ENPASSANT_ROW) {
                return null;
            }
            final int currentColumn = getColumn();
            final int leftColumn = currentColumn - 1;
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
            final int currentColumn = getColumn();
            final int leftColumn = currentColumn - 1;
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
            final int currentColumn = getColumn();
            final int rightColumn = currentColumn + 1;
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
            final int currentColumn = getColumn();
            final int rightColumn = currentColumn + 1;
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

    @Override
    public List<Tile> getLegalMoves(Grid grid) {
        List<Tile> legalMoves = super.getLegalMoves(grid);
        /*
        Tile leftEnPassantTile = getLeftEnPassantTile(grid);
        if (leftEnPassantTile != null) {
            legalMoves.add(leftEnPassantTile);
        }
        Tile rightEnPassantTile = getRightEnPassantTile(grid);
        if (rightEnPassantTile != null) {
            legalMoves.add(rightEnPassantTile);
        }
         */
        return legalMoves;
    }

    public Piece promote(String name) {
        switch (name) {
            case ChessConstants.QUEEN: {
                Queen promote = new Queen(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
                promote.setSelected(isSelected());
                promote.setMoveCount(getMoveCount());
                return promote;
            }
            case ChessConstants.ROOK: {
                Rook promote = new Rook(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
                promote.setSelected(isSelected());
                promote.setMoveCount(getMoveCount());
                return promote;
            }
            case ChessConstants.BISHOP: {
                Bishop promote = new Bishop(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
                promote.setSelected(isSelected());
                promote.setMoveCount(getMoveCount());
                return promote;   
            }
            case ChessConstants.KNIGHT: {
                Knight promote = new Knight(getX(), getY(), getWidth(), getHeight(), getRow(), getColumn(), isWhite());
                promote.setSelected(isSelected());
                promote.setMoveCount(getMoveCount());
                return promote;
            }
        }
        return null;
    }
}