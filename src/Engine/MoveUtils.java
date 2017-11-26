package Engine;

import java.util.List;

//class designed to simplify move testing and searching
public final class MoveUtils {
    
    private MoveUtils() {
        
    }
    
    //move functions
    public static void doMove(Grid grid, List<Piece> whites, List<Piece> blacks, Tile moveTile, Tile previousTile) {
        Piece piece = previousTile.getOccupant();
        previousTile.removeOccupant();
        moveTile.setOccupant(piece);
        grid.setProtections(whites, blacks);
    }

    public static void undoMove(Grid grid, List<Piece> whites, List<Piece> blacks, Tile moveTile, Tile previousTile) {
        Piece piece = moveTile.getOccupant();
        moveTile.removeOccupant();
        previousTile.setOccupant(piece);
        grid.setProtections(whites, blacks);
    }
    
    public static void undoWhiteMovePromotion(Grid grid, List<Piece> whites, List<Piece> blacks, Piece piece, int pawnIndex, Tile moveTile, Tile previousTile) {
        previousTile.setOccupant(piece);
        moveTile.removeOccupant();
        whites.set(pawnIndex, piece);
        grid.setProtections(whites, blacks);
    }
    
    public static void undoBlackMovePromotion(Grid grid, List<Piece> whites, List<Piece> blacks, Piece piece, int pawnIndex, Tile moveTile, Tile previousTile) {
        previousTile.setOccupant(piece);
        moveTile.removeOccupant();
        blacks.set(pawnIndex, piece);
        grid.setProtections(whites, blacks);
    }
    
    //white capture functions
    public static int doWhiteCapture(Grid grid, List<Piece> whites, List<Piece> blacks, Tile attackTile, Tile previousTile) {
        Piece piece = previousTile.getOccupant();
        previousTile.removeOccupant();
        int removeIndex = Pieces.remove(blacks, attackTile.getOccupant());
        attackTile.setOccupant(piece);
        grid.setProtections(whites, blacks);
        return removeIndex;
    }

    public static void undoWhiteCapture(Grid grid, List<Piece> whites, List<Piece> blacks, Piece enemy, int removeIndex, Tile attackTile, Tile previousTile) {
        Piece piece = attackTile.getOccupant();
        attackTile.setOccupant(enemy);
        previousTile.setOccupant(piece);
        blacks.add(removeIndex, enemy);
        grid.setProtections(whites, blacks);
    }

    public static void undoWhiteCapturePromotion(Grid grid, List<Piece> whites, List<Piece> blacks, Piece piece, int pawnIndex, Piece enemy, int removeIndex, Tile attackTile, Tile previousTile) {
        previousTile.setOccupant(piece);
        attackTile.setOccupant(enemy);
        whites.set(pawnIndex, piece);
        blacks.add(removeIndex, enemy);
        grid.setProtections(whites, blacks);
    }
    
    //black capture functions
    public static int doBlackCapture(Grid grid, List<Piece> whites, List<Piece> blacks, Tile attackTile, Tile previousTile) {
        Piece piece = previousTile.getOccupant();
        previousTile.removeOccupant();
        int removeIndex = Pieces.remove(whites, attackTile.getOccupant());
        attackTile.setOccupant(piece);
        grid.setProtections(whites, blacks);
        return removeIndex;
    }

    public static void undoBlackCapture(Grid grid, List<Piece> whites, List<Piece> blacks, Piece enemy, int removeIndex, Tile attackTile, Tile previousTile) {
        Piece piece = attackTile.getOccupant();
        attackTile.setOccupant(enemy);
        previousTile.setOccupant(piece);
        whites.add(removeIndex, enemy);
        grid.setProtections(whites, blacks);
    }

    public static void undoBlackCapturePromotion(Grid grid, List<Piece> whites, List<Piece> blacks, Piece piece, int pawnIndex, Piece enemy, int removeIndex, Tile attackTile, Tile previousTile) {
        previousTile.setOccupant(piece);
        attackTile.setOccupant(enemy);
        blacks.set(pawnIndex, piece);
        whites.add(removeIndex, enemy);
        grid.setProtections(whites, blacks);
    }
}