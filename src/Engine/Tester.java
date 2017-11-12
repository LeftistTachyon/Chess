package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.LINEAR_LENGTH;
import Util.Constants;
import static Util.Constants.NEGATIVE_INFINITY;
import static Util.Constants.POSITIVE_INFINITY;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class Tester {

    @SuppressWarnings("Convert2Lambda")
    private static final Comparator<Tile> TILE_SORTER = new Comparator<Tile>() {
        @Override
        public int compare(Tile first, Tile next) {
            return Integer.compare(first.getIndex(), next.getIndex());
        }
    };

    private Tester() {

    }

    static final void check(Grid grid1, Grid grid2, List<Piece> list1, List<Piece> list2) {
        grid1.equals(grid2);
        if (!list1.equals(list2)) {
            throw new Error();
        }
    }

    public static final void main(String... args) {
        String s = "{\n" +
"            for (int index = 0; index < numberOfWhiteQueens; ++index) {\n" +
"                Queen piece = board.whiteQueens.get(index);\n" +
"                final int currentRow = piece.getRow();\n" +
"                final int currentColumn = piece.getColumn();\n" +
"                Tile previousTile = grid.getTile(currentRow, currentColumn);\n" +
"                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {\n" +
"                    Tile tile = grid.getTile(nextRow, currentColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"                            \n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {\n" +
"                    Tile tile = grid.getTile(nextRow, currentColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {\n" +
"                    Tile tile = grid.getTile(currentRow, nextColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {\n" +
"                    Tile tile = grid.getTile(currentRow, nextColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {\n" +
"                    Tile tile = grid.getTile(nextRow--, nextColumn--);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {\n" +
"                    Tile tile = grid.getTile(nextRow--, nextColumn++);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {\n" +
"                    Tile tile = grid.getTile(nextRow++, nextColumn--);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {\n" +
"                    Tile tile = grid.getTile(nextRow++, nextColumn++);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"            }\n" +
"\n" +
"            for (int index = 0; index < numberOfWhiteRooks; ++index) {\n" +
"                Rook piece = board.whiteRooks.get(index);\n" +
"                final int currentRow = piece.getRow();\n" +
"                final int currentColumn = piece.getColumn();\n" +
"                Tile previousTile = grid.getTile(currentRow, currentColumn);\n" +
"                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {\n" +
"                    Tile tile = grid.getTile(nextRow, currentColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {\n" +
"                    Tile tile = grid.getTile(nextRow, currentColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {\n" +
"                    Tile tile = grid.getTile(currentRow, nextColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {\n" +
"                    Tile tile = grid.getTile(currentRow, nextColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"            }\n" +
"\n" +
"            for (int index = 0; index < numberOfWhiteBishops; ++index) {\n" +
"                Bishop piece = board.whiteBishops.get(index);\n" +
"                final int currentRow = piece.getRow();\n" +
"                final int currentColumn = piece.getColumn();\n" +
"                Tile previousTile = grid.getTile(currentRow, currentColumn);\n" +
"                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {\n" +
"                    Tile tile = grid.getTile(nextRow--, nextColumn--);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {\n" +
"                    Tile tile = grid.getTile(nextRow--, nextColumn++);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {\n" +
"                    Tile tile = grid.getTile(nextRow++, nextColumn--);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {\n" +
"                    Tile tile = grid.getTile(nextRow++, nextColumn++);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        if (!piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                        break;\n" +
"                    }\n" +
"                }\n" +
"            }\n" +
"\n" +
"            for (int index = 0; index < numberOfWhiteKnights; ++index) {\n" +
"                Knight piece = board.whiteKnights.get(index);\n" +
"                final int currentRow = piece.getRow();\n" +
"                final int currentColumn = piece.getColumn();\n" +
"\n" +
"                final int up = currentRow - 1;\n" +
"                final int down = currentRow + 1;\n" +
"                final int left = currentColumn - 1;\n" +
"                final int right = currentColumn + 1;\n" +
"\n" +
"                final int up2 = currentRow - 2;\n" +
"                final int down2 = currentRow + 2;\n" +
"                final int left2 = currentColumn - 2;\n" +
"                final int right2 = currentColumn + 2;\n" +
"\n" +
"                if (up >= 0) {\n" +
"                    if (left2 >= 0) {\n" +
"                        Tile tile = grid.getTile(up, left2);\n" +
"                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if (right2 < LENGTH) {\n" +
"                        Tile tile = grid.getTile(up, right2);\n" +
"                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"                if (down < LENGTH) {\n" +
"                    if (left2 >= 0) {\n" +
"                        Tile tile = grid.getTile(down, left2);\n" +
"                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if (right2 < LENGTH) {\n" +
"                        Tile tile = grid.getTile(down, right2);\n" +
"                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"                if (left >= 0) {\n" +
"                    if (up2 >= 0) {\n" +
"                        Tile tile = grid.getTile(up2, left);\n" +
"                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if (down2 < LENGTH) {\n" +
"                        Tile tile = grid.getTile(down2, left);\n" +
"                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"                if (right < LENGTH) {\n" +
"                    if (up2 >= 0) {\n" +
"                        Tile tile = grid.getTile(up2, right);\n" +
"                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if (down2 < LENGTH) {\n" +
"                        Tile tile = grid.getTile(down2, right);\n" +
"                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"            }\n" +
"\n" +
"            for (int index = 0; index < numberOfWhitePawns; ++index) {\n" +
"                Pawn piece = board.whitePawns.get(index);\n" +
"                int nextRow = piece.getRow() - 1;\n" +
"                if (nextRow >= 0) {\n" +
"                    final int currentColumn = piece.getColumn();\n" +
"                    int nextColumn = currentColumn - 1;\n" +
"                    if (nextColumn >= 0) {\n" +
"                        Tile tile = grid.getTile(nextRow, nextColumn);\n" +
"                        if (tile.isOccupiedByBlack()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if ((nextColumn = currentColumn + 1) < LENGTH) {\n" +
"                        Tile tile = grid.getTile(nextRow, nextColumn);\n" +
"                        if (tile.isOccupiedByBlack()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"            }\n" +
"        }\n" +
"        {\n" +
"            for (int index = 0; index < numberOfWhiteQueens; ++index) {\n" +
"                Queen piece = board.whiteQueens.get(index);\n" +
"                final int currentRow = piece.getRow();\n" +
"                final int currentColumn = piece.getColumn();\n" +
"                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {\n" +
"                    Tile tile = grid.getTile(nextRow, currentColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {\n" +
"                    Tile tile = grid.getTile(nextRow, currentColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {\n" +
"                    Tile tile = grid.getTile(currentRow, nextColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {\n" +
"                    Tile tile = grid.getTile(currentRow, nextColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {\n" +
"                    Tile tile = grid.getTile(nextRow--, nextColumn--);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {\n" +
"                    Tile tile = grid.getTile(nextRow--, nextColumn++);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {\n" +
"                    Tile tile = grid.getTile(nextRow++, nextColumn--);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {\n" +
"                    Tile tile = grid.getTile(nextRow++, nextColumn++);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"            }\n" +
"\n" +
"            for (int index = 0; index < numberOfWhiteRooks; ++index) {\n" +
"                Rook piece = board.whiteRooks.get(index);\n" +
"                final int currentRow = piece.getRow();\n" +
"                final int currentColumn = piece.getColumn();\n" +
"                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {\n" +
"                    Tile tile = grid.getTile(nextRow, currentColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {\n" +
"                    Tile tile = grid.getTile(nextRow, currentColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {\n" +
"                    Tile tile = grid.getTile(currentRow, nextColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {\n" +
"                    Tile tile = grid.getTile(currentRow, nextColumn);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"            }\n" +
"\n" +
"            for (int index = 0; index < numberOfWhiteBishops; ++index) {\n" +
"                Bishop piece = board.whiteBishops.get(index);\n" +
"                final int currentRow = piece.getRow();\n" +
"                final int currentColumn = piece.getColumn();\n" +
"                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {\n" +
"                    Tile tile = grid.getTile(nextRow--, nextColumn--);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {\n" +
"                    Tile tile = grid.getTile(nextRow--, nextColumn++);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {\n" +
"                    Tile tile = grid.getTile(nextRow++, nextColumn--);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {\n" +
"                    Tile tile = grid.getTile(nextRow++, nextColumn++);\n" +
"                    if (tile.isOccupied()) {\n" +
"                        break;\n" +
"                    }\n" +
"\n" +
"                }\n" +
"            }\n" +
"\n" +
"            for (int index = 0; index < numberOfWhiteKnights; ++index) {\n" +
"                Knight piece = board.whiteKnights.get(index);\n" +
"                final int currentRow = piece.getRow();\n" +
"                final int currentColumn = piece.getColumn();\n" +
"                final int up = currentRow - 1;\n" +
"                final int down = currentRow + 1;\n" +
"                final int left = currentColumn - 1;\n" +
"                final int right = currentColumn + 1;\n" +
"\n" +
"                final int up2 = currentRow - 2;\n" +
"                final int down2 = currentRow + 2;\n" +
"                final int left2 = currentColumn - 2;\n" +
"                final int right2 = currentColumn + 2;\n" +
"\n" +
"                if (up >= 0) {\n" +
"                    if (left2 >= 0) {\n" +
"                        Tile tile = grid.getTile(up, left2);\n" +
"                        if (!tile.isOccupied()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if (right2 < LENGTH) {\n" +
"                        Tile tile = grid.getTile(up, right2);\n" +
"                        if (!tile.isOccupied()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"                if (down < LENGTH) {\n" +
"                    if (left2 >= 0) {\n" +
"                        Tile tile = grid.getTile(down, left2);\n" +
"                        if (!tile.isOccupied()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if (right2 < LENGTH) {\n" +
"                        Tile tile = grid.getTile(down, right2);\n" +
"                        if (!tile.isOccupied()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"                if (left >= 0) {\n" +
"                    if (up2 >= 0) {\n" +
"                        Tile tile = grid.getTile(up2, left);\n" +
"                        if (!tile.isOccupied()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if (down2 < LENGTH) {\n" +
"                        Tile tile = grid.getTile(down2, left);\n" +
"                        if (!tile.isOccupied()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"                if (right < LENGTH) {\n" +
"                    if (up2 >= 0) {\n" +
"                        Tile tile = grid.getTile(up2, right);\n" +
"                        if (!tile.isOccupied()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                    if (down2 < LENGTH) {\n" +
"                        Tile tile = grid.getTile(down2, right);\n" +
"                        if (!tile.isOccupied()) {\n" +
"\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"            }\n" +
"\n" +
"            for (int index = 0; index < numberOfWhitePawns; ++index) {\n" +
"                Pawn piece = board.whitePawns.get(index);\n" +
"                int nextRow = piece.getRow() - 1;\n" +
"                if (!piece.hasMoved()) {\n" +
"                    final int currentColumn = piece.getColumn();\n" +
"                    Tile nextTileUp = grid.getTile(nextRow, currentColumn);\n" +
"                    if (!nextTileUp.isOccupied()) {\n" +
"                        //list.add(nextTileUp);\n" +
"                        Tile nextNextTileUp = grid.getTile(nextRow - 1, currentColumn);\n" +
"                        if (!nextNextTileUp.isOccupied()) {\n" +
"                            //list.add(nextNextTileUp);\n" +
"                        }\n" +
"                    }\n" +
"                    continue;\n" +
"                }\n" +
"                if (nextRow >= 0) {\n" +
"                    Tile nextTileUp = grid.getTile(nextRow, piece.getColumn());\n" +
"                    if (!nextTileUp.isOccupied()) {\n" +
"                        //list.add(nextTileUp);\n" +
"                    }\n" +
"                }\n" +
"            }\n" +
"        }";
        s = s.replace("white", "black");
        s = s.replace("WHITE", "BLACK");
        s = s.replace("White", "Black");
        System.out.println(s);
        /*
        Piece piece = new King(0, 0, false);
        int c = 1;
        while (1 == c) {
            long s = System.nanoTime();
            boolean method = piece.isKing();
            System.out.println(System.nanoTime() - s);
            s = System.nanoTime();
            boolean type = piece.getClass() == King.class;
            System.out.println(System.nanoTime() - s);
            
            System.out.println();
        }
         */
        System.out.println(Constants.RUNTIME.availableProcessors());
        testStartPosition();
        testErrorPosition();
        int k = 0;
        while (k++ < 10) {
            int[] nums = new int[10000000];
            int[] nums2 = new int[10000000];
            long start = System.nanoTime();
            for (int index = 0; index < nums.length; ++index) {
                nums[index] = index << 3;
            }
            System.out.println(System.nanoTime() - start);
            start = System.nanoTime();
            for (int index = 0; index < nums2.length; ++index) {
                nums2[index] = index * ChessConstants.LENGTH;
            }
            System.out.println(System.nanoTime() - start);
            System.out.println();
        }
    }

    private static void testStartPosition() {
        System.out.println("Testing Starting Position");
        AI.TIMER = new SearchTimer(10000, "Test");
        new AI(true, 60).useTestDialog();
        List<Piece> pieces = new ArrayList<>();

        pieces.add(new King(7, 4, true));
        pieces.add(new King(0, 4, false));

        pieces.add(new Rook(7, 0, true));
        pieces.add(new Knight(7, 1, true));
        pieces.add(new Bishop(7, 2, true));
        pieces.add(new Queen(7, 3, true));
        pieces.add(new Bishop(7, 5, true));
        pieces.add(new Knight(7, 6, true));
        pieces.add(new Rook(7, 7, true));

        pieces.add(new Pawn(6, 0, true));
        pieces.add(new Pawn(6, 1, true));
        pieces.add(new Pawn(6, 2, true));
        pieces.add(new Pawn(6, 3, true));
        pieces.add(new Pawn(6, 4, true));
        pieces.add(new Pawn(6, 5, true));
        pieces.add(new Pawn(6, 6, true));
        pieces.add(new Pawn(6, 7, true));

        pieces.add(new Rook(0, 0, false));
        pieces.add(new Knight(0, 1, false));
        pieces.add(new Bishop(0, 2, false));
        pieces.add(new Queen(0, 3, false));
        pieces.add(new Bishop(0, 5, false));
        pieces.add(new Knight(0, 6, false));
        pieces.add(new Rook(0, 7, false));

        pieces.add(new Pawn(1, 0, false));
        pieces.add(new Pawn(1, 1, false));
        pieces.add(new Pawn(1, 2, false));
        pieces.add(new Pawn(1, 3, false));
        pieces.add(new Pawn(1, 4, false));
        pieces.add(new Pawn(1, 5, false));
        pieces.add(new Pawn(1, 6, false));
        pieces.add(new Pawn(1, 7, false));

        //Pieces.sort(pieces);
        testPosition(pieces, false);
        Pieces.sort(pieces);
        Grid grid = new Grid();
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }
        grid.setProtections(pieces);
        List<Piece> whites = Pieces.getWhite(pieces);
        List<Piece> blacks = Pieces.getBlack(pieces);
        for (int depth = 1; depth <= 5; ++depth) {
            int alphaBetaScore = AlphaBetaWhite.min(grid, whites, blacks, depth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
            int alphaBetaPositionCount = Evaluator.getNumberOfPositionsEvaluatedInWhitePersepective();
            System.out.println("AlphaBeta Score: " + alphaBetaScore);
            System.out.println("AlphaBeta Position Count: " + alphaBetaPositionCount);
            Evaluator.setNumberOfPositionEvaluatedInWhitePersepective(0);
            int minMaxScore = MinMaxWhite.min(grid, whites, blacks, depth);
            int minMaxPositionCount = Evaluator.getNumberOfPositionsEvaluatedInWhitePersepective();
            Evaluator.setNumberOfPositionEvaluatedInWhitePersepective(0);
            System.out.println("MinMax Score: " + minMaxScore);
            System.out.println("MinMax Position Count: " + minMaxPositionCount);
            System.out.println();
        }
    }

    private static void testErrorPosition() {
        System.out.println("Testing Error Position");
        List<Piece> pieces = new ArrayList<>();

        pieces.add(new Rook(0, 0, false));
        pieces.add(new Bishop(0, 2, false));
        pieces.add(new Queen(0, 3, false));
        pieces.add(new King(0, 4, false));
        pieces.add(new Bishop(0, 5, false));
        pieces.add(new Knight(0, 6, false));
        pieces.add(new Rook(0, 7, false));
        pieces.add(new Pawn(1, 0, false));
        pieces.add(new Pawn(1, 1, false));
        pieces.add(new Pawn(1, 2, false));
        pieces.add(new Pawn(1, 3, false));
        pieces.add(new Pawn(1, 5, false));
        pieces.add(new Pawn(1, 6, false));
        pieces.add(new Pawn(1, 7, false));
        pieces.add(new Knight(2, 2, false));
        pieces.add(new Pawn(3, 4, false));
        pieces.add(new Pawn(4, 4, true));
        pieces.add(new Pawn(5, 3, true));
        pieces.add(new Pawn(6, 0, true));
        pieces.add(new Pawn(6, 1, true));
        pieces.add(new Pawn(6, 2, true));
        pieces.add(new King(6, 4, true));
        pieces.add(new Pawn(6, 5, true));
        pieces.add(new Pawn(6, 6, true));
        pieces.add(new Pawn(6, 7, true));
        pieces.add(new Rook(7, 0, true));
        pieces.add(new Knight(7, 1, true));
        pieces.add(new Bishop(7, 2, true));
        pieces.add(new Queen(7, 3, true));
        pieces.add(new Knight(7, 6, true));
        pieces.add(new Rook(7, 7, true));

        testPosition(pieces, false);
        Pieces.sort(pieces);
        Grid grid = new Grid();
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }
        grid.setProtections(pieces);
        List<Piece> whites = Pieces.getWhite(pieces);
        List<Piece> blacks = Pieces.getBlack(pieces);
        for (int depth = 1; depth <= 7; ++depth) {
            int alphaBetaScore = AlphaBetaWhite.min(grid, whites, blacks, depth, NEGATIVE_INFINITY, POSITIVE_INFINITY);
            int alphaBetaPositionCount = Evaluator.getNumberOfPositionsEvaluatedInWhitePersepective();
            System.out.println("AlphaBeta Score: " + alphaBetaScore);
            System.out.println("AlphaBeta Position Count: " + alphaBetaPositionCount);
            Evaluator.setNumberOfPositionEvaluatedInWhitePersepective(0);
            /*
            int minMaxScore = MinMaxWhite.min(grid, whites, blacks, depth);
            int minMaxPositionCount = Evaluator.getNumberOfPositionsEvaluatedInWhitePersepective();
            Evaluator.setNumberOfPositionEvaluatedInWhitePersepective(0);
            System.out.println("MinMax Score: " + minMaxScore);
            System.out.println("MinMax Position Count: " + minMaxPositionCount);
             */
            System.out.println();
        }
    }
    
    static void value(Grid grid) {
        System.out.println("Comparing Evaluator with EvaluatorSpecial");
        List<Piece> pieces = grid.getPieces();
        Pieces.sort(pieces);
        List<Piece> whites = Pieces.getWhite(pieces);
        List<Piece> blacks = Pieces.getBlack(pieces);
        System.out.println(Evaluator.evaluateInBlackPerspective(grid, whites, blacks));
        Board board = new Board(grid);
        System.out.println(EvaluatorSpecial.evaluateInBlackPerspective(board));
    }

    static final void testPosition(final List<Piece> pieces, final boolean color) {
        final Grid grid = new Grid();
        final int numberOfPieces = pieces.size();

        //put pieces onto the grid
        for (int index = 0; index != numberOfPieces; ++index) {
            Piece piece = pieces.get(index);
            grid.getTile(piece.getRow(), piece.getColumn()).setOccupant(piece);
        }
        

        //for each piece, test their protected tiles
        for (int pieceIndex = 0; pieceIndex != numberOfPieces; ++pieceIndex) {
            final Piece piece = pieces.get(pieceIndex);
            final List<Tile> protectedTiles = piece.getProtectedTiles(grid);
            final int numberOfProtectedTiles = piece.getNumberOfProtectedTiles(grid);
            piece.setProtectedTiles(grid);
            final List<Tile> protectedTilesOnGrid = new ArrayList<>(numberOfProtectedTiles);
            for (int index = 0; index < LINEAR_LENGTH; ++index) {
                Tile tile = grid.getTile(index);
                if (tile.protectedByAlly(piece)) {
                    protectedTilesOnGrid.add(tile);
                }
            }
            if (protectedTiles.size() != numberOfProtectedTiles) {
                throw new Error();
            }
            protectedTiles.sort(TILE_SORTER);
            protectedTilesOnGrid.sort(TILE_SORTER);
            if (!protectedTiles.equals(protectedTilesOnGrid)) {
                throw new Error();
            }
            for (int index = 0; index < LINEAR_LENGTH; ++index) {
                grid.getTile(index).removeProtections();
            }
        }
        grid.setProtections(pieces);
        
        Grid copiedGrid = new Grid(grid);
        List<Piece> copiedPieces = Pieces.getDeepCopy(pieces);
        for (int depth = 1; depth <= 5; ++depth) {
            System.out.println("Perft (" + depth + "): " + perft(grid, depth, color));
        }
        check(grid, copiedGrid, pieces, copiedPieces);
        //System.out.println("Board Evaluation: " + Evaluator.analyze(grid, pieces));
        System.out.println();
    }

    static List<Pawn> checkEnPassant(final Grid grid, final List<Piece> pieces) {
        List<Pawn> list = new ArrayList<>(8);
        for (int index = 0, size = pieces.size(); index != size; ++index) {
            Piece piece = pieces.get(index);
            if (piece.isPawn()) {
                Pawn pawn = (Pawn) piece;
                //if the pawn can "technically" perform a puesdo-en passant
                if (pawn.canEnPassant() && pawn.canPuesdoEnPassant(grid)) {
                    pawn.setEnPassantPermission(false); //don't allow it
                    list.add(pawn);
                }
            }
        }
        return list;
    }

    static void uncheckEnPassant(final List<Pawn> pawns) {
        for (Pawn pawn : pawns) {
            pawn.setEnPassantPermission(true);
        }
    }

    static final int perft(final Grid grid, int depth, final boolean color) {
        if (depth == 0) {
            return 1;
        }
        --depth;
        Grid clone = new Grid(grid);
        int moves = 0;
        final List<Piece> pieces = grid.getPieces();
        Pieces.sort(pieces);
        //must sort pieces per iteration, since the
        //pieces from the grid are not sorted.
        if (color) {
            final List<Piece> blacks = Pieces.getBlack(pieces);
            final King blackKing = Pieces.getBlackKing(blacks);
            for (int lastIndex = (blacks.size() - 1); lastIndex >= 0; --lastIndex) {
                Piece black = blacks.get(lastIndex);
                int previousRow = black.getRow();
                int previousColumn = black.getColumn();
                Tile previousTile = grid.getTile(previousRow, previousColumn);
                if (black.isKing()) {
                    Tile leftKingCastleTile = grid.getTile(0, 2);
                    Tile rightKingCastleTile = grid.getTile(0, 6);
                    List<Tile> castleTiles = blackKing.getCastleTiles(grid);
                    for (int index = 0, size = castleTiles.size(); index != size; ++index) {
                        if (castleTiles.get(index).sameLocation(leftKingCastleTile)) {
                            List<Pawn> enPassantPawns = checkEnPassant(grid, blacks);
                            Tile leftRookTile = grid.getTile(0, 0);
                            Piece leftRook = leftRookTile.getOccupant();
                            Tile leftRookCastleTile = grid.getTile(0, 3);
                            previousTile.removeOccupant();
                            leftRookTile.removeOccupant();
                            leftKingCastleTile.setOccupant(black);
                            leftRookCastleTile.setOccupant(leftRook);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                black.increaseMoveCount();
                                leftRook.increaseMoveCount();
                                moves += perft(grid, depth, false);
                                black.decreaseMoveCount();
                                leftRook.decreaseMoveCount();
                            }
                            previousTile.setOccupant(black);
                            leftRookTile.setOccupant(leftRook);
                            leftKingCastleTile.removeOccupant();
                            leftRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                            uncheckEnPassant(enPassantPawns);
                        }
                        else {
                            List<Pawn> enPassantPawns = checkEnPassant(grid, blacks);
                            Tile rightRookTile = grid.getTile(0, 7);
                            Piece rightRook = rightRookTile.getOccupant();
                            Tile rightRookCastleTile = grid.getTile(0, 5);
                            previousTile.removeOccupant();
                            rightRookTile.removeOccupant();
                            rightKingCastleTile.setOccupant(black);
                            rightRookCastleTile.setOccupant(rightRook);
                            grid.setProtections(pieces);
                            if (!blackKing.inCheck(grid)) {
                                black.increaseMoveCount();
                                rightRook.increaseMoveCount();
                                moves += perft(grid, depth, false);
                                black.decreaseMoveCount();
                                rightRook.decreaseMoveCount();
                            }
                            previousTile.setOccupant(black);
                            rightRookTile.setOccupant(rightRook);
                            rightKingCastleTile.removeOccupant();
                            rightRookCastleTile.removeOccupant();
                            grid.setProtections(pieces);
                            uncheckEnPassant(enPassantPawns);
                        }
                        grid.equals(clone);
                    }
                }
                /*
                else if (black.isPawn()) {
                    Pawn pawn = (Pawn) black;
                    if (pawn.canEnPassant()) {
                        {
                            Tile enPassantTile = pawn.getLeftEnPassantTile(grid);
                            if (enPassantTile != null) {
                                List<Pawn> enPassantPawns = checkEnPassant(grid, blacks);
                                Tile whitePawnTile = grid.getTile(previousRow, previousColumn - 1);
                                Piece whitePawn = whitePawnTile.getOccupant();
                                previousTile.removeOccupant();
                                whitePawnTile.removeOccupant();
                                enPassantTile.setOccupant(black);
                                int removeIndex = pieces.indexOf(whitePawn);
                                pieces.remove(removeIndex);
                                grid.setProtections(pieces);
                                if (!blackKing.inCheck(grid)) {
                                    black.increaseMoveCount();
                                    moves += perft(grid, depth, false);
                                    black.decreaseMoveCount();
                                }
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, whitePawn);
                                grid.setProtections(pieces);
                                uncheckEnPassant(enPassantPawns);
                            }
                        }
                        {
                            Tile enPassantTile = pawn.getRightEnPassantTile(grid);
                            if (enPassantTile != null) {
                                List<Pawn> enPassantPawns = checkEnPassant(grid, blacks);
                                Tile whitePawnTile = grid.getTile(previousRow, previousColumn + 1);
                                Piece whitePawn = whitePawnTile.getOccupant();
                                previousTile.removeOccupant();
                                whitePawnTile.removeOccupant();
                                enPassantTile.setOccupant(black);
                                int removeIndex = pieces.indexOf(whitePawn);
                                pieces.remove(removeIndex);
                                grid.setProtections(pieces);
                                if (!blackKing.inCheck(grid)) {
                                    black.increaseMoveCount();
                                    moves += perft(grid, depth, false);
                                    black.decreaseMoveCount();
                                }
                                previousTile.setOccupant(black);
                                whitePawnTile.setOccupant(whitePawn);
                                enPassantTile.removeOccupant();
                                pieces.add(removeIndex, whitePawn);
                                grid.setProtections(pieces);
                                uncheckEnPassant(enPassantPawns);
                            }
                        }
                        grid.equals(clone);
                    }
                }
                 */
                List<Tile> moveTiles = black.getMoveTiles(grid);
                for (int index = 0, size = moveTiles.size(); index != size; ++index) {
                    List<Pawn> enPassantPawns = checkEnPassant(grid, blacks);
                    Tile moveTile = moveTiles.get(index);
                    previousTile.removeOccupant();
                    if (black.isPawn() && previousRow == 6) {
                        Queen replace = Pawn.promote(black);
                        moveTile.setOccupant(replace);
                        int pawnIndex = pieces.indexOf(black);
                        pieces.set(pawnIndex, replace);
                        grid.setProtections(pieces);
                        if (!blackKing.inCheck(grid)) {
                            replace.increaseMoveCount();
                            moves += perft(grid, depth, false);
                        }
                        previousTile.setOccupant(black);
                        moveTile.removeOccupant();
                        pieces.set(pawnIndex, black);
                        grid.setProtections(pieces);
                    }
                    else {
                        moveTile.setOccupant(black);
                        grid.setProtections(pieces);
                        if (!blackKing.inCheck(grid)) {
                            black.increaseMoveCount();
                            moves += perft(grid, depth, false);
                            black.decreaseMoveCount();
                        }
                        previousTile.setOccupant(black);
                        moveTile.removeOccupant();
                        grid.setProtections(pieces);
                    }
                    uncheckEnPassant(enPassantPawns);
                    grid.equals(clone);
                }
                List<Tile> attackTiles = black.getAttackTiles(grid);
                for (int index = 0, size = attackTiles.size(); index != size; ++index) {
                    List<Pawn> enPassantPawns = checkEnPassant(grid, blacks);
                    Tile attackTile = attackTiles.get(index);
                    Piece enemy = attackTile.getOccupant();
                    if (enemy.isKing()) {
                        continue;
                    }
                    previousTile.removeOccupant();
                    if (black.isPawn() && previousRow == 6) {
                        Queen replace = Pawn.promote(black);
                        attackTile.setOccupant(replace);
                        int pawnIndex = pieces.indexOf(black);
                        pieces.set(pawnIndex, replace);
                        int removeIndex = pieces.indexOf(enemy);
                        pieces.remove(removeIndex);
                        grid.setProtections(pieces);
                        if (!blackKing.inCheck(grid)) {
                            replace.increaseMoveCount();
                            moves += perft(grid, depth, false);
                        }
                        previousTile.setOccupant(black);
                        attackTile.setOccupant(enemy);
                        pieces.add(removeIndex, enemy);
                        pieces.set(pawnIndex, black);
                        grid.setProtections(pieces);
                    }
                    else {
                        attackTile.setOccupant(black);
                        int removeIndex = pieces.indexOf(enemy);
                        pieces.remove(removeIndex);
                        grid.setProtections(pieces);
                        if (!blackKing.inCheck(grid)) {
                            black.increaseMoveCount();
                            moves += perft(grid, depth, false);
                            black.decreaseMoveCount();
                        }
                        previousTile.setOccupant(black);
                        attackTile.setOccupant(enemy);
                        pieces.add(removeIndex, enemy);
                        grid.setProtections(pieces);
                    }
                    uncheckEnPassant(enPassantPawns);
                    grid.equals(clone);
                }
            }
            grid.equals(clone);
            //no change, no legal moves, then max still is MIN_VALUE
            return moves;
        }
        final List<Piece> whites = Pieces.getWhite(pieces);
        final King whiteKing = Pieces.getWhiteKing(whites);
        for (int lastIndex = (whites.size() - 1); lastIndex >= 0; --lastIndex) {
            Piece white = whites.get(lastIndex);
            int previousRow = white.getRow();
            int previousColumn = white.getColumn();
            Tile previousTile = grid.getTile(previousRow, previousColumn);
            if (white.isKing()) {
                Tile leftKingCastleTile = grid.getTile(7, 2);
                Tile rightKingCastleTile = grid.getTile(7, 6);
                List<Tile> castleTiles = whiteKing.getCastleTiles(grid);
                for (int index = 0, size = castleTiles.size(); index != size; ++index) {
                    if (castleTiles.get(index).sameLocation(leftKingCastleTile)) {
                        List<Pawn> enPassantPawns = checkEnPassant(grid, whites);
                        Tile leftRookTile = grid.getTile(7, 0);
                        Piece leftRook = leftRookTile.getOccupant();
                        Tile leftRookCastleTile = grid.getTile(7, 3);
                        previousTile.removeOccupant();
                        leftRookTile.removeOccupant();
                        leftKingCastleTile.setOccupant(white);
                        leftRookCastleTile.setOccupant(leftRook);
                        grid.setProtections(pieces);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            leftRook.increaseMoveCount();
                            moves += perft(grid, depth, true);
                            white.decreaseMoveCount();
                            leftRook.decreaseMoveCount();
                        }
                        previousTile.setOccupant(white);
                        leftRookTile.setOccupant(leftRook);
                        leftKingCastleTile.removeOccupant();
                        leftRookCastleTile.removeOccupant();
                        grid.setProtections(pieces);
                        uncheckEnPassant(enPassantPawns);
                    }
                    else {
                        List<Pawn> enPassantPawns = checkEnPassant(grid, whites);
                        Tile rightRookTile = grid.getTile(7, 7);
                        Piece rightRook = rightRookTile.getOccupant();
                        Tile rightRookCastleTile = grid.getTile(7, 5);
                        previousTile.removeOccupant();
                        rightRookTile.removeOccupant();
                        rightKingCastleTile.setOccupant(white);
                        rightRookCastleTile.setOccupant(rightRook);
                        grid.setProtections(pieces);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            rightRook.increaseMoveCount();
                            moves += perft(grid, depth, true);
                            white.decreaseMoveCount();
                            rightRook.decreaseMoveCount();
                        }
                        previousTile.setOccupant(white);
                        rightRookTile.setOccupant(rightRook);
                        rightKingCastleTile.removeOccupant();
                        rightRookCastleTile.removeOccupant();
                        grid.setProtections(pieces);
                        uncheckEnPassant(enPassantPawns);
                    }
                    grid.equals(clone);
                }
            }
            /*
            else if (white.isPawn()) {
                Pawn pawn = (Pawn) white;
                if (pawn.canEnPassant()) {
                    {
                        Tile enPassantTile = pawn.getLeftEnPassantTile(grid);
                        if (enPassantTile != null) {
                            List<Pawn> enPassantPawns = checkEnPassant(grid, whites);
                            Tile blackPawnTile = grid.getTile(previousRow, previousColumn - 1);
                            Piece blackPawn = blackPawnTile.getOccupant();
                            previousTile.removeOccupant();
                            blackPawnTile.removeOccupant();
                            enPassantTile.setOccupant(white);
                            int removeIndex = pieces.indexOf(blackPawn);
                            pieces.remove(removeIndex);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                moves += perft(grid, depth, true);
                                white.decreaseMoveCount();
                            }
                            previousTile.setOccupant(white);
                            blackPawnTile.setOccupant(blackPawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, blackPawn);
                            grid.setProtections(pieces);
                            uncheckEnPassant(enPassantPawns);
                        }
                    }
                    {
                        Tile enPassantTile = pawn.getRightEnPassantTile(grid);
                        if (enPassantTile != null) {
                            List<Pawn> enPassantPawns = checkEnPassant(grid, whites);
                            Tile blackPawnTile = grid.getTile(previousRow, previousColumn + 1);
                            Piece blackPawn = blackPawnTile.getOccupant();
                            previousTile.removeOccupant();
                            blackPawnTile.removeOccupant();
                            enPassantTile.setOccupant(white);
                            int removeIndex = pieces.indexOf(blackPawn);
                            pieces.remove(removeIndex);
                            grid.setProtections(pieces);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                moves += perft(grid, depth, true);
                                white.decreaseMoveCount();
                            }
                            previousTile.setOccupant(white);
                            blackPawnTile.setOccupant(blackPawn);
                            enPassantTile.removeOccupant();
                            pieces.add(removeIndex, blackPawn);
                            grid.setProtections(pieces);
                            uncheckEnPassant(enPassantPawns);
                        }
                    }
                    grid.equals(clone);
                }
            }
             */
            List<Tile> moveTiles = white.getMoveTiles(grid);
            for (int index = 0, size = moveTiles.size(); index != size; ++index) {
                List<Pawn> enPassantPawns = checkEnPassant(grid, whites);
                Tile moveTile = moveTiles.get(index);
                previousTile.removeOccupant();
                if (white.isPawn() && previousRow == 1) {
                    Queen replace = Pawn.promote(white);
                    moveTile.setOccupant(replace);
                    int pawnIndex = pieces.indexOf(white);
                    pieces.set(pawnIndex, replace);
                    grid.setProtections(pieces);
                    if (!whiteKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        moves += perft(grid, depth, true);
                    }
                    previousTile.setOccupant(white);
                    moveTile.removeOccupant();
                    pieces.set(pawnIndex, white);
                    grid.setProtections(pieces);
                }
                else {
                    moveTile.setOccupant(white);
                    grid.setProtections(pieces);
                    if (!whiteKing.inCheck(grid)) {
                        white.increaseMoveCount();
                        moves += perft(grid, depth, true);
                        white.decreaseMoveCount();
                    }
                    previousTile.setOccupant(white);
                    moveTile.removeOccupant();
                    grid.setProtections(pieces);
                }
                uncheckEnPassant(enPassantPawns);
                grid.equals(clone);
            }
            List<Tile> attackTiles = white.getAttackTiles(grid);
            for (int index = 0, size = attackTiles.size(); index != size; ++index) {
                List<Pawn> enPassantPawns = checkEnPassant(grid, whites);
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (enemy.isKing()) {
                    continue;
                }
                previousTile.removeOccupant();
                if (white.isPawn() && previousRow == 1) {
                    Queen replace = Pawn.promote(white);
                    attackTile.setOccupant(replace);
                    int pawnIndex = pieces.indexOf(white);
                    pieces.set(pawnIndex, replace);
                    int removeIndex = pieces.indexOf(enemy);
                    pieces.remove(removeIndex);
                    grid.setProtections(pieces);
                    if (!whiteKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        moves += perft(grid, depth, true);
                    }
                    previousTile.setOccupant(white);
                    attackTile.setOccupant(enemy);
                    pieces.add(removeIndex, enemy);
                    pieces.set(pawnIndex, white);
                    grid.setProtections(pieces);
                }
                else {
                    attackTile.setOccupant(white);
                    int removeIndex = pieces.indexOf(enemy);
                    pieces.remove(removeIndex);
                    grid.setProtections(pieces);
                    if (!whiteKing.inCheck(grid)) {
                        white.increaseMoveCount();
                        moves += perft(grid, depth, true);
                        white.decreaseMoveCount();
                    }
                    previousTile.setOccupant(white);
                    attackTile.setOccupant(enemy);
                    pieces.add(removeIndex, enemy);
                    grid.setProtections(pieces);
                }
                uncheckEnPassant(enPassantPawns);
                grid.equals(clone);
            }
        }
        grid.equals(clone);
        //no change, no legal moves, then min still is MAX_VALUE
        return moves;
    }
}