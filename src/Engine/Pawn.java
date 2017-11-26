package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.LENGTH;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("EqualsAndHashcode")
public final class Pawn extends Piece {

    private boolean justMadeDoubleJump = false;

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
        copy.justMadeDoubleJump = justMadeDoubleJump;
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (!(obj instanceof Pawn)) ? false : (super.equals(obj) && (justMadeDoubleJump == ((Pawn) obj).justMadeDoubleJump));
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
    public boolean justMadeDoubleJump() {
        return justMadeDoubleJump && (getMoveCount() == 1) && (isWhite() ? (getRow() == 4) : (getRow() == 3));
    }

    @Override
    public void setJustMadeDoubleJump(boolean doubleJumpJustPerformed) {
        justMadeDoubleJump = doubleJumpJustPerformed;
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

    //should be psesdo-legal, generate positions even if the pawn had already lost its 
    //right to enpassant.
    @Override
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

    @Override
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
                    if (blackPawn.isPawn() && blackPawn.isBlack() && blackPawn.justMadeDoubleJump() && !leftEnPassantTile.isOccupied()) {
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
                    if (whitePawn.isPawn() && whitePawn.isWhite() && whitePawn.justMadeDoubleJump() && !leftEnPassantTile.isOccupied()) {
                        return leftEnPassantTile;
                    }
                }
            }
        }
        return null;
    }

    @Override
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
                    if (blackPawn.isPawn() && blackPawn.isBlack() && blackPawn.justMadeDoubleJump() && !rightEnPassantTile.isOccupied()) {
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
                    if (whitePawn.isPawn() && whitePawn.isWhite() && whitePawn.justMadeDoubleJump() && !rightEnPassantTile.isOccupied()) {
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

    public static Piece[] getPromoted(Piece pawn) {
        final int row = pawn.getRow();
        final int column = pawn.getColumn();
        final int moveCount = pawn.getMoveCount();
        final boolean color = pawn.isWhite();
        return new Piece[]{
            new Queen(row, column, moveCount, color), 
            new Rook(row, column, moveCount, color), 
            new Bishop(row, column, moveCount, color), 
            new Knight(row, column, moveCount, color)
        };
    }

    public static Queen promote(Piece pawn) {
        return new Queen(pawn.getRow(), pawn.getColumn(), pawn.getMoveCount(), pawn.isWhite());
    }

    @Override
    public String encode() {
        return "(" + isWhite() + "," + getType() + "," + getRow() + "," + getColumn() + "," + justMadeDoubleJump + ")";
    }
}