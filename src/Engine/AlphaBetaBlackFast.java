/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import static Engine.AlphaBetaBlack.max;
import Util.ChessConstants;
import static Util.ChessConstants.CHECKMATE_VALUE;
import static Util.ChessConstants.LENGTH;
import static Util.Constants.NEGATIVE_INFINITY;
import static Util.Constants.POSITIVE_INFINITY;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zwill
 */
public class AlphaBetaBlackFast {

    /**
     * Minimizing component of the Alpha-Beta search function seeking to reduce
     * Black's score as much as possible. This component implements White's
     * moves.
     *
     * @param grid The chess board.
     * @param whites The white pieces on the chess board.
     * @param blacks The black pieces on the chess board.
     * @param depth Number of ply to search ahead.
     * @return The least possible score to reduce White's score as much as
     * possible. This score may be extremely high, indicating that White is
     * losing or is about to be checkmated.
     */
    static int min(final Grid grid, final List<Piece> whites, final List<Piece> blacks, int depth, final int alpha, int beta) {
        if (depth == 0 || AI.TIMER.timeOver()) {
            return Evaluator.evaluateInBlackPerspective(grid, whites, blacks);
        }

        --depth;
        int value = POSITIVE_INFINITY;

        final King whiteKing = Pieces.getWhiteKing(whites);

        CASTLE:
        {
            final Tile previousTile = grid.getTile(whiteKing.getRow(), whiteKing.getColumn());
            final List<Tile> castleTiles = whiteKing.getCastleTiles(grid);
            for (int index = (castleTiles.size() - 1); index >= 0; --index) {
                Tile kingCastleTile = castleTiles.get(index);
                if (kingCastleTile.getColumn() == ChessConstants.LEFT_KING_CASTLE_COLUMN) {
                    Tile leftRookTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, 0);
                    Tile leftRookCastleTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, ChessConstants.LEFT_ROOK_CASTLE_COLUMN);
                    Piece leftRook = leftRookTile.getOccupant();

                    previousTile.removeOccupant();
                    leftRookTile.removeOccupant();
                    kingCastleTile.setOccupant(whiteKing);
                    leftRookCastleTile.setOccupant(leftRook);
                    grid.setProtections(whites, blacks);

                    whiteKing.increaseMoveCount();
                    leftRook.increaseMoveCount();
                    int result = max(grid, whites, blacks, depth, alpha, beta);
                    if (result < value) {
                        value = result;
                    }
                    if (value < beta) {
                        beta = value;
                    }
                    whiteKing.decreaseMoveCount();
                    leftRook.decreaseMoveCount();

                    previousTile.setOccupant(whiteKing);
                    leftRookTile.setOccupant(leftRook);
                    kingCastleTile.removeOccupant();
                    leftRookCastleTile.removeOccupant();
                    grid.setProtections(whites, blacks);

                    if (beta <= alpha) {
                        return beta;
                    }
                }
                else {
                    Tile rightRookTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, 7);
                    Tile rightRookCastleTile = grid.getTile(ChessConstants.WHITE_PIECE_ROW, ChessConstants.RIGHT_ROOK_CASTLE_COLUMN);
                    Piece rightRook = rightRookTile.getOccupant();

                    previousTile.removeOccupant();
                    rightRookTile.removeOccupant();
                    kingCastleTile.setOccupant(whiteKing);
                    rightRookCastleTile.setOccupant(rightRook);
                    grid.setProtections(whites, blacks);

                    whiteKing.increaseMoveCount();
                    rightRook.increaseMoveCount();
                    int result = max(grid, whites, blacks, depth, alpha, beta);
                    if (result < value) {
                        value = result;
                    }
                    if (value < beta) {
                        beta = value;
                    }
                    whiteKing.decreaseMoveCount();
                    rightRook.decreaseMoveCount();

                    previousTile.setOccupant(whiteKing);
                    rightRookTile.setOccupant(rightRook);
                    kingCastleTile.removeOccupant();
                    rightRookCastleTile.removeOccupant();
                    grid.setProtections(whites, blacks);

                    if (beta <= alpha) {
                        return beta;
                    }
                }
            }
        }

        final int numberOfWhitePieces = whites.size();

        ATTACK_LOOP:
        for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
            final Piece white = whites.get(pieceIndex);
            final int previousRow = white.getRow();
            final int previousColumn = white.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            if (white.isKing()) {
                final int currentRow = previousRow;
                final int currentColumn = previousColumn;
                final int up = currentRow - 1;
                final int down = currentRow + 1;
                final int left = currentColumn - 1;
                final int right = currentColumn + 1;
                if (up >= 0) {
                    if (left >= 0) {
                        Tile tile = grid.getTile(up, left);
                        if (!tile.protectedByEnemy(white) && tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    Tile tile = grid.getTile(up, currentColumn);
                    if (!tile.protectedByEnemy(white) && tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                        Tile attackTile = tile;
                        Piece enemy = attackTile.getOccupant();
                        if (enemy.isKing()) {
                            continue;
                        }
                        previousTile.removeOccupant();
                        attackTile.setOccupant(white);
                        int removeIndex = Pieces.remove(blacks, enemy);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            int result = max(grid, whites, blacks, depth, alpha, beta);
                            if (result < value) {
                                value = result;
                            }
                            if (value < beta) {
                                beta = value;
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(white);
                        attackTile.setOccupant(enemy);
                        blacks.add(removeIndex, enemy);
                        grid.setProtections(whites, blacks);
                    }
                    if (right < LENGTH) {
                        tile = grid.getTile(up, right);
                        if (!tile.protectedByEnemy(white) && tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
                if (left >= 0) {
                    Tile tile = grid.getTile(currentRow, left);
                    if (!tile.protectedByEnemy(white) && tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                        Tile attackTile = tile;
                        Piece enemy = attackTile.getOccupant();
                        if (enemy.isKing()) {
                            continue;
                        }
                        previousTile.removeOccupant();
                        attackTile.setOccupant(white);
                        int removeIndex = Pieces.remove(blacks, enemy);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            int result = max(grid, whites, blacks, depth, alpha, beta);
                            if (result < value) {
                                value = result;
                            }
                            if (value < beta) {
                                beta = value;
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(white);
                        attackTile.setOccupant(enemy);
                        blacks.add(removeIndex, enemy);
                        grid.setProtections(whites, blacks);
                    }
                }
                if (right < LENGTH) {
                    Tile tile = grid.getTile(currentRow, right);
                    if (!tile.protectedByEnemy(white) && tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                        Tile attackTile = tile;
                        Piece enemy = attackTile.getOccupant();
                        if (enemy.isKing()) {
                            continue;
                        }
                        previousTile.removeOccupant();
                        attackTile.setOccupant(white);
                        int removeIndex = Pieces.remove(blacks, enemy);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            int result = max(grid, whites, blacks, depth, alpha, beta);
                            if (result < value) {
                                value = result;
                            }
                            if (value < beta) {
                                beta = value;
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(white);
                        attackTile.setOccupant(enemy);
                        blacks.add(removeIndex, enemy);
                        grid.setProtections(whites, blacks);
                    }
                }
                if (down < LENGTH) {
                    if (left >= 0) {
                        Tile tile = grid.getTile(down, left);
                        if (!tile.protectedByEnemy(white) && tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    Tile tile = grid.getTile(down, currentColumn);
                    if (!tile.protectedByEnemy(white) && tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                        Tile attackTile = tile;
                        Piece enemy = attackTile.getOccupant();
                        if (enemy.isKing()) {
                            continue;
                        }
                        previousTile.removeOccupant();
                        attackTile.setOccupant(white);
                        int removeIndex = Pieces.remove(blacks, enemy);
                        grid.setProtections(whites, blacks);
                        if (!whiteKing.inCheck(grid)) {
                            white.increaseMoveCount();
                            int result = max(grid, whites, blacks, depth, alpha, beta);
                            if (result < value) {
                                value = result;
                            }
                            if (value < beta) {
                                beta = value;
                            }
                            white.decreaseMoveCount();
                            if (beta <= alpha) {
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                                return beta;
                            }
                        }
                        previousTile.setOccupant(white);
                        attackTile.setOccupant(enemy);
                        blacks.add(removeIndex, enemy);
                        grid.setProtections(whites, blacks);
                    }
                    if (right < LENGTH) {
                        tile = grid.getTile(down, right);
                        if (!tile.protectedByEnemy(white) && tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
            }
            else if (white.isQueen()) {
                final int currentRow = previousRow;
                final int currentColumn = previousColumn;
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn++);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn++);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
            }
            else if (white.isRook()) {
                final int currentRow = previousRow;
                final int currentColumn = previousColumn;
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
            }
            else if (white.isBishop()) {
                final int currentRow = previousRow;
                final int currentColumn = previousColumn;
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn++);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn++);
                    if (tile.isOccupied()) {
                        if (!white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                break;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
            }
            else if (white.isKnight()) {
                final int currentRow = previousRow;
                final int currentColumn = previousColumn;

                final int up = currentRow - 1;
                final int down = currentRow + 1;
                final int left = currentColumn - 1;
                final int right = currentColumn + 1;

                final int up2 = currentRow - 2;
                final int down2 = currentRow + 2;
                final int left2 = currentColumn - 2;
                final int right2 = currentColumn + 2;

                if (up >= 0) {
                    if (left2 >= 0) {
                        Tile tile = grid.getTile(up, left2);
                        if (tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(up, right2);
                        if (tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
                if (down < LENGTH) {
                    if (left2 >= 0) {
                        Tile tile = grid.getTile(down, left2);
                        if (tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(down, right2);
                        if (tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
                if (left >= 0) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, left);
                        if (tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, left);
                        if (tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
                if (right < LENGTH) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, right);
                        if (tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, right);
                        if (tile.isOccupied() && !white.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(white);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                white.increaseMoveCount();
                                int result = max(grid, whites, blacks, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                white.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(white);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(white);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
            }
            else {
                int nextRow = previousRow - 1;
                if (nextRow >= 0) {
                    int nextColumn = previousColumn - 1;
                    if (nextColumn >= 0) {
                        Tile tile = grid.getTile(nextRow, nextColumn);
                        if (tile.isOccupiedByBlack()) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            if (white.isPawn() && previousRow == 1) {
                                Queen replace = Pawn.promote(white);
                                attackTile.setOccupant(replace);
                                int pawnIndex = whites.indexOf(white);
                                whites.set(pawnIndex, replace);
                                int removeIndex = Pieces.remove(blacks, enemy);
                                grid.setProtections(whites, blacks);
                                if (!whiteKing.inCheck(grid)) {
                                    replace.increaseMoveCount();
                                    int result = max(grid, whites, blacks, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                    if (beta <= alpha) {
                                        previousTile.setOccupant(white);
                                        attackTile.setOccupant(enemy);
                                        whites.set(pawnIndex, white);
                                        blacks.add(removeIndex, enemy);
                                        grid.setProtections(whites, blacks);
                                        return beta;
                                    }
                                }
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                whites.set(pawnIndex, white);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                            }
                            else {
                                attackTile.setOccupant(white);
                                int removeIndex = Pieces.remove(blacks, enemy);
                                grid.setProtections(whites, blacks);
                                if (!whiteKing.inCheck(grid)) {
                                    white.increaseMoveCount();
                                    int result = max(grid, whites, blacks, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                    white.decreaseMoveCount();
                                    if (beta <= alpha) {
                                        previousTile.setOccupant(white);
                                        attackTile.setOccupant(enemy);
                                        blacks.add(removeIndex, enemy);
                                        grid.setProtections(whites, blacks);
                                        return beta;
                                    }
                                }
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                            }
                        }
                    }
                    if ((nextColumn = previousColumn + 1) < LENGTH) {
                        Tile tile = grid.getTile(nextRow, nextColumn);
                        if (tile.isOccupiedByBlack()) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            if (white.isPawn() && previousRow == 1) {
                                Queen replace = Pawn.promote(white);
                                attackTile.setOccupant(replace);
                                int pawnIndex = whites.indexOf(white);
                                whites.set(pawnIndex, replace);
                                int removeIndex = Pieces.remove(blacks, enemy);
                                grid.setProtections(whites, blacks);
                                if (!whiteKing.inCheck(grid)) {
                                    replace.increaseMoveCount();
                                    int result = max(grid, whites, blacks, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                    if (beta <= alpha) {
                                        previousTile.setOccupant(white);
                                        attackTile.setOccupant(enemy);
                                        whites.set(pawnIndex, white);
                                        blacks.add(removeIndex, enemy);
                                        grid.setProtections(whites, blacks);
                                        return beta;
                                    }
                                }
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                whites.set(pawnIndex, white);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                            }
                            else {
                                attackTile.setOccupant(white);
                                int removeIndex = Pieces.remove(blacks, enemy);
                                grid.setProtections(whites, blacks);
                                if (!whiteKing.inCheck(grid)) {
                                    white.increaseMoveCount();
                                    int result = max(grid, whites, blacks, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                    white.decreaseMoveCount();
                                    if (beta <= alpha) {
                                        previousTile.setOccupant(white);
                                        attackTile.setOccupant(enemy);
                                        blacks.add(removeIndex, enemy);
                                        grid.setProtections(whites, blacks);
                                        return beta;
                                    }
                                }
                                previousTile.setOccupant(white);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                            }
                        }
                    }
                }
            }
        }

        MOVE_LOOP:
        for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
            final Piece white = whites.get(pieceIndex);
            final int previousRow = white.getRow();
            final int previousColumn = white.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            if (white.isKing()) {
                final int currentRow = previousRow;
                final int currentColumn = previousColumn;
                final int up = currentRow - 1;
                final int down = currentRow + 1;
                final int left = currentColumn - 1;
                final int right = currentColumn + 1;
                if (up >= 0) {
                    if (left >= 0) {
                        Tile tile = grid.getTile(up, left);
                        if (!tile.protectedByEnemy(white) && !tile.isOccupied()) {
                     
                        }
                    }
                    Tile tile = grid.getTile(up, currentColumn);
                    if (!tile.protectedByEnemy(white) && !tile.isOccupied()) {
                   
                    }
                    if (right < LENGTH) {
                        tile = grid.getTile(up, right);
                        if (!tile.protectedByEnemy(white) && !tile.isOccupied()) {
                            
                        }
                    }
                }
                if (left >= 0) {
                    Tile tile = grid.getTile(currentRow, left);
                    if (!tile.protectedByEnemy(white) && !tile.isOccupied()) {
                      
                    }
                }
                if (right < LENGTH) {
                    Tile tile = grid.getTile(currentRow, right);
                    if (!tile.protectedByEnemy(white) && !tile.isOccupied()) {
                        
                    }
                }
                if (down < LENGTH) {
                    if (left >= 0) {
                        Tile tile = grid.getTile(down, left);
                        if (!tile.protectedByEnemy(white) && !tile.isOccupied()) {
                          
                        }
                    }
                    Tile tile = grid.getTile(down, currentColumn);
                    if (!tile.protectedByEnemy(white) && !tile.isOccupied()) {
                     
                    }
                    if (right < LENGTH) {
                        tile = grid.getTile(down, right);
                        if (!tile.protectedByEnemy(white) && !tile.isOccupied()) {
                           
                        }
                    }
                }
            }
            else if (white.isQueen()) {

            }
            else if (white.isRook()) {

            }
            else if (white.isBishop()) {

            }
            else if (white.isKnight()) {

            }
            else {

            }
        }

        return (value == POSITIVE_INFINITY) ? checkWhiteEndGame(grid, whiteKing, depth + 1) : beta;
    }

    /**
     * Maximizing component of the Alpha-Beta search function. This component
     * seeks to increase Black's score as much as possible.
     *
     * @param grid The chess board.
     * @param whites The white pieces on the chess board.
     * @param blacks The black pieces on the chess board.
     * @param depth Number of ply to search ahead.
     * @return The greatest possible score to increase White's score as much as
     * possible. This score may be extremely low, indicating that Black is
     * losing or is about to be checkmated.
     */
    static int max(final Grid grid, final List<Piece> whites, final List<Piece> blacks, int depth, int alpha, final int beta) {
        if (depth == 0 || AI.TIMER.timeOver()) {
            return Evaluator.evaluateInBlackPerspective(grid, whites, blacks);
        }

        --depth;
        int value = NEGATIVE_INFINITY;

        final King blackKing = Pieces.getBlackKing(blacks);

        {
            final Tile previousTile = grid.getTile(blackKing.getRow(), blackKing.getColumn());
            final List<Tile> castleTiles = blackKing.getCastleTiles(grid);
            for (int index = (castleTiles.size() - 1); index >= 0; --index) {
                Tile kingCastleTile = castleTiles.get(index);
                if (kingCastleTile.getColumn() == ChessConstants.LEFT_KING_CASTLE_COLUMN) {
                    Tile leftRookTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, 0);
                    Tile leftRookCastleTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, ChessConstants.LEFT_ROOK_CASTLE_COLUMN);
                    Piece leftRook = leftRookTile.getOccupant();

                    previousTile.removeOccupant();
                    leftRookTile.removeOccupant();
                    kingCastleTile.setOccupant(blackKing);
                    leftRookCastleTile.setOccupant(leftRook);
                    grid.setProtections(whites, blacks);

                    blackKing.increaseMoveCount();
                    leftRook.increaseMoveCount();
                    int result = min(grid, whites, blacks, depth, alpha, beta);
                    if (result > value) {
                        value = result;
                    }
                    if (value > alpha) {
                        alpha = value;
                    }
                    blackKing.decreaseMoveCount();
                    leftRook.decreaseMoveCount();

                    previousTile.setOccupant(blackKing);
                    leftRookTile.setOccupant(leftRook);
                    kingCastleTile.removeOccupant();
                    leftRookCastleTile.removeOccupant();
                    grid.setProtections(whites, blacks);

                    if (beta <= alpha) {
                        return alpha;
                    }
                }
                else {
                    Tile rightRookTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, 7);
                    Tile rightRookCastleTile = grid.getTile(ChessConstants.BLACK_PIECE_ROW, ChessConstants.RIGHT_ROOK_CASTLE_COLUMN);
                    Piece rightRook = rightRookTile.getOccupant();

                    previousTile.removeOccupant();
                    rightRookTile.removeOccupant();
                    kingCastleTile.setOccupant(blackKing);
                    rightRookCastleTile.setOccupant(rightRook);
                    grid.setProtections(whites, blacks);

                    blackKing.increaseMoveCount();
                    rightRook.increaseMoveCount();
                    int result = min(grid, whites, blacks, depth, alpha, beta);
                    if (result > value) {
                        value = result;
                    }
                    if (value > alpha) {
                        alpha = value;
                    }
                    blackKing.decreaseMoveCount();
                    rightRook.decreaseMoveCount();

                    previousTile.setOccupant(blackKing);
                    rightRookTile.setOccupant(rightRook);
                    kingCastleTile.removeOccupant();
                    rightRookCastleTile.removeOccupant();
                    grid.setProtections(whites, blacks);

                    if (beta <= alpha) {
                        return alpha;
                    }
                }
            }
        }

        return (value == NEGATIVE_INFINITY) ? checkBlackEndGame(grid, blackKing, depth + 1) : alpha;
    }

    /**
     * This function should only be called when the White player cannot make any
     * legal moves. The function then determines whether such a position is a
     * Checkmate against White or a Stalemate.
     *
     * @param grid The current chess board.
     * @param whiteKing The White player's King.
     * @param depth How close this position is from the base node. The bigger
     * this value, the closer this position is from the base node. This value
     * cannot be negative.
     * @return An extremely high value (greater than or equal to 200,000,000) if
     * this position is a Checkmate against White or 0 if this position is a
     * Stalemate.
     */
    private static int checkWhiteEndGame(final Grid grid, final King whiteKing, final int depth) {
        AI.DIALOG.increasePositionsScanned();
        //a bigger "depth", is actually shallower in the tree
        return whiteKing.inCheck(grid) ? (CHECKMATE_VALUE + depth) : 0;
    }

    /**
     * This function should only be called when the Black player cannot make any
     * legal moves. The function then determines whether such a position is a
     * Checkmate against Black or a Stalemate.
     *
     * @param grid The current chess board.
     * @param blackKing The Black player's King.
     * @param depth How close this position is from the base node. The bigger
     * this value, the closer this position is from the base node. This value
     * cannot be negative.
     * @return An extremely low value (less than or equal to -200,000,000) if
     * this position is a Checkmate against Black or 0 if this position is a
     * Stalemate.
     */
    private static int checkBlackEndGame(final Grid grid, final King blackKing, final int depth) {
        AI.DIALOG.increasePositionsScanned();
        //a lower "depth", is actually deeper in the tree
        return blackKing.inCheck(grid) ? (-CHECKMATE_VALUE - depth) : 0;
    }
}
