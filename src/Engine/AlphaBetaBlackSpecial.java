package Engine;

import Util.ChessConstants;
import static Util.ChessConstants.CHECKMATE_VALUE;
import static Util.ChessConstants.LENGTH;
import static Util.Constants.NEGATIVE_INFINITY;
import static Util.Constants.POSITIVE_INFINITY;
import java.util.List;

//special is designed to avoid making redudant temporary move lists on the fly
//which is a big problem hindering search speed.
public class AlphaBetaBlackSpecial {

    private AlphaBetaBlackSpecial() {

    }

    /**
     * Minimizing component of the Alpha-Beta search function seeking to reduce
     * Black's score as much as possible. This component implements White's
     * moves.
     *
     * @param board The chess board.
     * @param depth Number of ply to search ahead.
     * @return The least possible score to reduce White's score as much as
     * possible. This score may be extremely high, indicating that White is
     * losing or is about to be checkmated.
     */
    static int min(final Board board, int depth, final int alpha, int beta) {
        if (depth == 0 || AI.TIMER.timeOver()) {
            return EvaluatorSpecial.evaluateInBlackPerspective(board);
        }

        --depth;
        int value = POSITIVE_INFINITY;

        final Grid grid = board.grid;
        final List<Piece> whites = board.whites;
        final List<Piece> blacks = board.blacks;
        final King whiteKing = board.whiteKing;

        final int numberOfWhiteQueens = board.whiteQueens.size();
        final int numberOfWhiteRooks = board.whiteRooks.size();
        final int numberOfWhiteBishops = board.whiteBishops.size();
        final int numberOfWhiteKnights = board.whiteKnights.size();
        final int numberOfWhitePawns = board.whitePawns.size();

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
                    int result = max(board, depth, alpha, beta);
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
                    int result = max(board, depth, alpha, beta);
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

        {
            for (int index = 0; index < numberOfWhiteQueens; ++index) {
                Queen piece = board.whiteQueens.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
            }

            for (int index = 0; index < numberOfWhiteRooks; ++index) {
                Rook piece = board.whiteRooks.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
            }

            for (int index = 0; index < numberOfWhiteBishops; ++index) {
                Bishop piece = board.whiteBishops.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
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
                        if (!piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                        break;
                    }
                }
            }

            for (int index = 0; index < numberOfWhiteKnights; ++index) {
                Knight piece = board.whiteKnights.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);

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
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(up, right2);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
                if (down < LENGTH) {
                    if (left2 >= 0) {
                        Tile tile = grid.getTile(down, left2);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(down, right2);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
                if (left >= 0) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, left);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, left);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
                if (right < LENGTH) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, right);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, right);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            attackTile.setOccupant(piece);
                            int removeIndex = Pieces.remove(blacks, enemy);
                            grid.setProtections(whites, blacks);
                            if (!whiteKing.inCheck(grid)) {
                                piece.increaseMoveCount();
                                int result = max(board, depth, alpha, beta);
                                if (result < value) {
                                    value = result;
                                }
                                if (value < beta) {
                                    beta = value;
                                }
                                piece.decreaseMoveCount();
                                if (beta <= alpha) {
                                    previousTile.setOccupant(piece);
                                    attackTile.setOccupant(enemy);
                                    blacks.add(removeIndex, enemy);
                                    grid.setProtections(whites, blacks);
                                    return beta;
                                }
                            }
                            previousTile.setOccupant(piece);
                            attackTile.setOccupant(enemy);
                            blacks.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                        }
                    }
                }
            }

            for (int index = 0; index < numberOfWhitePawns; ++index) {
                Pawn piece = board.whitePawns.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                int nextRow = currentRow - 1;
                if (nextRow >= 0) {
                    Tile previousTile = grid.getTile(currentRow, currentColumn);
                    int nextColumn = currentColumn - 1;
                    if (nextColumn >= 0) {
                        Tile tile = grid.getTile(nextRow, nextColumn);
                        if (tile.isOccupiedByBlack()) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            if (currentRow == 1) {
                                Queen replace = Pawn.promote(piece);
                                attackTile.setOccupant(replace);
                                int pawnIndex = whites.indexOf(piece);
                                whites.set(pawnIndex, replace);
                                int removeIndex = Pieces.remove(blacks, enemy);
                                grid.setProtections(whites, blacks);
                                if (!whiteKing.inCheck(grid)) {
                                    replace.increaseMoveCount();
                                    int result = max(board, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                    if (beta <= alpha) {
                                        previousTile.setOccupant(piece);
                                        attackTile.setOccupant(enemy);
                                        whites.set(pawnIndex, piece);
                                        blacks.add(removeIndex, enemy);
                                        grid.setProtections(whites, blacks);
                                        return beta;
                                    }
                                }
                                previousTile.setOccupant(piece);
                                attackTile.setOccupant(enemy);
                                whites.set(pawnIndex, piece);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                            }
                            else {
                                attackTile.setOccupant(piece);
                                int removeIndex = Pieces.remove(blacks, enemy);
                                grid.setProtections(whites, blacks);
                                if (!whiteKing.inCheck(grid)) {
                                    piece.increaseMoveCount();
                                    int result = max(board, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                    piece.decreaseMoveCount();
                                    if (beta <= alpha) {
                                        previousTile.setOccupant(piece);
                                        attackTile.setOccupant(enemy);
                                        blacks.add(removeIndex, enemy);
                                        grid.setProtections(whites, blacks);
                                        return beta;
                                    }
                                }
                                previousTile.setOccupant(piece);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                            }
                        }
                    }
                    if ((nextColumn = currentColumn + 1) < LENGTH) {
                        Tile tile = grid.getTile(nextRow, nextColumn);
                        if (tile.isOccupiedByBlack()) {
                            Tile attackTile = tile;
                            Piece enemy = attackTile.getOccupant();
                            if (enemy.isKing()) {
                                continue;
                            }
                            previousTile.removeOccupant();
                            if (currentRow == 1) {
                                Queen replace = Pawn.promote(piece);
                                attackTile.setOccupant(replace);
                                int pawnIndex = whites.indexOf(piece);
                                whites.set(pawnIndex, replace);
                                int removeIndex = Pieces.remove(blacks, enemy);
                                grid.setProtections(whites, blacks);
                                if (!whiteKing.inCheck(grid)) {
                                    replace.increaseMoveCount();
                                    int result = max(board, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                    if (beta <= alpha) {
                                        previousTile.setOccupant(piece);
                                        attackTile.setOccupant(enemy);
                                        whites.set(pawnIndex, piece);
                                        blacks.add(removeIndex, enemy);
                                        grid.setProtections(whites, blacks);
                                        return beta;
                                    }
                                }
                                previousTile.setOccupant(piece);
                                attackTile.setOccupant(enemy);
                                whites.set(pawnIndex, piece);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                            }
                            else {
                                attackTile.setOccupant(piece);
                                int removeIndex = Pieces.remove(blacks, enemy);
                                grid.setProtections(whites, blacks);
                                if (!whiteKing.inCheck(grid)) {
                                    piece.increaseMoveCount();
                                    int result = max(board, depth, alpha, beta);
                                    if (result < value) {
                                        value = result;
                                    }
                                    if (value < beta) {
                                        beta = value;
                                    }
                                    piece.decreaseMoveCount();
                                    if (beta <= alpha) {
                                        previousTile.setOccupant(piece);
                                        attackTile.setOccupant(enemy);
                                        blacks.add(removeIndex, enemy);
                                        grid.setProtections(whites, blacks);
                                        return beta;
                                    }
                                }
                                previousTile.setOccupant(piece);
                                attackTile.setOccupant(enemy);
                                blacks.add(removeIndex, enemy);
                                grid.setProtections(whites, blacks);
                            }
                        }
                    }
                }
            }
        }
        {
            for (int index = 0; index < numberOfWhiteQueens; ++index) {
                Queen piece = board.whiteQueens.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn++);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn--);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn++);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
            }

            for (int index = 0; index < numberOfWhiteRooks; ++index) {
                Rook piece = board.whiteRooks.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
            }

            for (int index = 0; index < numberOfWhiteBishops; ++index) {
                Bishop piece = board.whiteBishops.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn++);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn--);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn++);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
            }

            for (int index = 0; index < numberOfWhiteKnights; ++index) {
                Knight piece = board.whiteKnights.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
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
                        if (!tile.isOccupied()) {

                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(up, right2);
                        if (!tile.isOccupied()) {

                        }
                    }
                }
                if (down < LENGTH) {
                    if (left2 >= 0) {
                        Tile tile = grid.getTile(down, left2);
                        if (!tile.isOccupied()) {

                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(down, right2);
                        if (!tile.isOccupied()) {

                        }
                    }
                }
                if (left >= 0) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, left);
                        if (!tile.isOccupied()) {

                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, left);
                        if (!tile.isOccupied()) {

                        }
                    }
                }
                if (right < LENGTH) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, right);
                        if (!tile.isOccupied()) {

                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, right);
                        if (!tile.isOccupied()) {

                        }
                    }
                }
            }

            for (int index = 0; index < numberOfWhitePawns; ++index) {
                Pawn piece = board.whitePawns.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                int nextRow = currentRow - 1;
                if (!piece.hasMoved()) {
                    Tile nextTileUp = grid.getTile(nextRow, currentColumn);
                    if (!nextTileUp.isOccupied()) {
                        //list.add(nextTileUp);
                        Tile nextNextTileUp = grid.getTile(nextRow - 1, currentColumn);
                        if (!nextNextTileUp.isOccupied()) {
                            //list.add(nextNextTileUp);
                        }
                    }
                    continue;
                }
                if (nextRow >= 0) {
                    Tile nextTileUp = grid.getTile(nextRow, currentColumn);
                    if (!nextTileUp.isOccupied()) {
                        //list.add(nextTileUp);
                    }
                }
            }
        }

        final int numberOfWhitePieces = whites.size();

        for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
            final Piece white = whites.get(pieceIndex);
            final int previousRow = white.getRow();
            final int previousColumn = white.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> attackTiles = white.getAttackTiles(grid);
            for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                Tile attackTile = attackTiles.get(index);
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
                        int result = max(board, depth, alpha, beta);
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
                        int result = max(board, depth, alpha, beta);
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

        for (int pieceIndex = 0; pieceIndex != numberOfWhitePieces; ++pieceIndex) {
            final Piece white = whites.get(pieceIndex);
            final int previousRow = white.getRow();
            final int previousColumn = white.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> moveTiles = white.getMoveTiles(grid);
            for (int index = (moveTiles.size() - 1); index >= 0; --index) {
                Tile moveTile = moveTiles.get(index);
                previousTile.removeOccupant();
                if (white.isPawn() && previousRow == 1) {
                    Queen replace = Pawn.promote(white);
                    moveTile.setOccupant(replace);
                    int pawnIndex = whites.indexOf(white);
                    whites.set(pawnIndex, replace);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        int result = max(board, depth, alpha, beta);
                        if (result < value) {
                            value = result;
                        }
                        if (value < beta) {
                            beta = value;
                        }
                        if (beta <= alpha) {
                            previousTile.setOccupant(white);
                            moveTile.removeOccupant();
                            whites.set(pawnIndex, white);
                            grid.setProtections(whites, blacks);
                            return beta;
                        }
                    }
                    previousTile.setOccupant(white);
                    moveTile.removeOccupant();
                    whites.set(pawnIndex, white);
                    grid.setProtections(whites, blacks);
                }
                else {
                    moveTile.setOccupant(white);
                    grid.setProtections(whites, blacks);
                    if (!whiteKing.inCheck(grid)) {
                        white.increaseMoveCount();
                        int result = max(board, depth, alpha, beta);
                        if (result < value) {
                            value = result;
                        }
                        if (value < beta) {
                            beta = value;
                        }
                        white.decreaseMoveCount();
                        if (beta <= alpha) {
                            previousTile.setOccupant(white);
                            moveTile.removeOccupant();
                            grid.setProtections(whites, blacks);
                            return beta;
                        }
                    }
                    previousTile.setOccupant(white);
                    moveTile.removeOccupant();
                    grid.setProtections(whites, blacks);
                }
            }
        }

        return (value == POSITIVE_INFINITY) ? checkWhiteEndGame(grid, whiteKing, depth + 1) : beta;
    }

    /**
     * Maximizing component of the Alpha-Beta search function. This component
     * seeks to increase Black's score as much as possible.
     *
     * @param board The chess board.
     * @param depth Number of ply to search ahead.
     * @return The greatest possible score to increase White's score as much as
     * possible. This score may be extremely low, indicating that Black is
     * losing or is about to be checkmated.
     */
    static int max(final Board board, int depth, int alpha, final int beta) {
        if (depth == 0 || AI.TIMER.timeOver()) {
            return EvaluatorSpecial.evaluateInBlackPerspective(board);
        }

        --depth;
        int value = NEGATIVE_INFINITY;

        final Grid grid = board.grid;
        final List<Piece> whites = board.whites;
        final List<Piece> blacks = board.blacks;
        final King blackKing = board.whiteKing;

        final int numberOfBlackQueens = board.blackQueens.size();
        final int numberOfBlackRooks = board.blackRooks.size();
        final int numberOfBlackBishops = board.blackBishops.size();
        final int numberOfBlackKnights = board.blackKnights.size();
        final int numberOfBlackPawns = board.blackPawns.size();

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
                    int result = min(board, depth, alpha, beta);
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
                    int result = min(board, depth, alpha, beta);
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

        {
            for (int index = 0; index < numberOfBlackQueens; ++index) {
                Queen piece = board.blackQueens.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn++);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn++);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
            }

            for (int index = 0; index < numberOfBlackRooks; ++index) {
                Rook piece = board.blackRooks.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
            }

            for (int index = 0; index < numberOfBlackBishops; ++index) {
                Bishop piece = board.blackBishops.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn++);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn--);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn++);
                    if (tile.isOccupied()) {
                        if (!piece.isAlly(tile.getOccupant())) {

                        }
                        break;
                    }
                }
            }

            for (int index = 0; index < numberOfBlackKnights; ++index) {
                Knight piece = board.blackKnights.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);

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
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {

                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(up, right2);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {

                        }
                    }
                }
                if (down < LENGTH) {
                    if (left2 >= 0) {
                        Tile tile = grid.getTile(down, left2);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {

                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(down, right2);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {

                        }
                    }
                }
                if (left >= 0) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, left);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {

                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, left);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {

                        }
                    }
                }
                if (right < LENGTH) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, right);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {

                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, right);
                        if (tile.isOccupied() && !piece.isAlly(tile.getOccupant())) {

                        }
                    }
                }
            }

            for (int index = 0; index < numberOfBlackPawns; ++index) {
                Pawn piece = board.blackPawns.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                int nextRow = currentRow + 1;
                if (nextRow < LENGTH) {
                    int nextColumn = currentColumn - 1;
                    if (nextColumn >= 0) {
                        Tile tile = grid.getTile(nextRow, nextColumn);
                        if (tile.isOccupiedByWhite()) {

                        }
                    }
                    if ((nextColumn = currentColumn + 1) < LENGTH) {
                        Tile tile = grid.getTile(nextRow, nextColumn);
                        if (tile.isOccupiedByWhite()) {

                        }
                    }
                }
            }
        }
        {
            for (int index = 0; index < numberOfBlackQueens; ++index) {
                Queen piece = board.blackQueens.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn++);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn--);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn++);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
            }

            for (int index = 0; index < numberOfBlackRooks; ++index) {
                Rook piece = board.blackRooks.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1; nextRow >= 0; nextRow--) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1; nextRow < LENGTH; nextRow++) {
                    Tile tile = grid.getTile(nextRow, currentColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextColumn = currentColumn - 1; nextColumn >= 0; nextColumn--) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextColumn = currentColumn + 1; nextColumn < LENGTH; nextColumn++) {
                    Tile tile = grid.getTile(currentRow, nextColumn);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
            }

            for (int index = 0; index < numberOfBlackBishops; ++index) {
                Bishop piece = board.blackBishops.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn--);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow--, nextColumn++);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn--);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
                for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH;) {
                    Tile tile = grid.getTile(nextRow++, nextColumn++);
                    if (tile.isOccupied()) {
                        break;
                    }

                }
            }

            for (int index = 0; index < numberOfBlackKnights; ++index) {
                Knight piece = board.blackKnights.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
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
                        if (!tile.isOccupied()) {

                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(up, right2);
                        if (!tile.isOccupied()) {

                        }
                    }
                }
                if (down < LENGTH) {
                    if (left2 >= 0) {
                        Tile tile = grid.getTile(down, left2);
                        if (!tile.isOccupied()) {

                        }
                    }
                    if (right2 < LENGTH) {
                        Tile tile = grid.getTile(down, right2);
                        if (!tile.isOccupied()) {

                        }
                    }
                }
                if (left >= 0) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, left);
                        if (!tile.isOccupied()) {

                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, left);
                        if (!tile.isOccupied()) {

                        }
                    }
                }
                if (right < LENGTH) {
                    if (up2 >= 0) {
                        Tile tile = grid.getTile(up2, right);
                        if (!tile.isOccupied()) {

                        }
                    }
                    if (down2 < LENGTH) {
                        Tile tile = grid.getTile(down2, right);
                        if (!tile.isOccupied()) {

                        }
                    }
                }
            }

            for (int index = 0; index < numberOfBlackPawns; ++index) {
                Pawn piece = board.blackPawns.get(index);
                final int currentRow = piece.getRow();
                final int currentColumn = piece.getColumn();
                Tile previousTile = grid.getTile(currentRow, currentColumn);
                int nextRow = currentRow + 1;
                if (!piece.hasMoved()) {
                    Tile nextTileDown = grid.getTile(nextRow, currentColumn);
                    if (!nextTileDown.isOccupied()) {
                        //list.add(nextTileDown);
                        Tile nextNextTileDown = grid.getTile(nextRow + 1, currentColumn);
                        if (!nextNextTileDown.isOccupied()) {
                            //list.add(nextNextTileDown);
                        }
                    }
                    continue;
                }
                if (nextRow < LENGTH) {
                    Tile nextTileDown = grid.getTile(nextRow, currentColumn);
                    if (!nextTileDown.isOccupied()) {
                        //list.add(nextTileDown);
                    }
                }
            }
        }

        final int numberOfBlackPieces = blacks.size();

        for (int pieceIndex = 0; pieceIndex != numberOfBlackPieces; ++pieceIndex) {
            final Piece black = blacks.get(pieceIndex);
            final int previousRow = black.getRow();
            final int previousColumn = black.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> attackTiles = black.getAttackTiles(grid);
            for (int index = (attackTiles.size() - 1); index >= 0; --index) {
                Tile attackTile = attackTiles.get(index);
                Piece enemy = attackTile.getOccupant();
                if (enemy.isKing()) {
                    continue;
                }
                previousTile.removeOccupant();
                if (black.isPawn() && previousRow == 6) {
                    Queen replace = Pawn.promote(black);
                    attackTile.setOccupant(replace);
                    int pawnIndex = blacks.indexOf(black);
                    blacks.set(pawnIndex, replace);
                    int removeIndex = Pieces.remove(whites, enemy);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        int result = min(board, depth, alpha, beta);
                        if (result > value) {
                            value = result;
                        }
                        if (value > alpha) {
                            alpha = value;
                        }
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            attackTile.setOccupant(enemy);
                            blacks.set(pawnIndex, black);
                            whites.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(black);
                    attackTile.setOccupant(enemy);
                    blacks.set(pawnIndex, black);
                    whites.add(removeIndex, enemy);
                    grid.setProtections(whites, blacks);
                }
                else {
                    attackTile.setOccupant(black);
                    int removeIndex = Pieces.remove(whites, enemy);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        black.increaseMoveCount();
                        int result = min(board, depth, alpha, beta);
                        if (result > value) {
                            value = result;
                        }
                        if (value > alpha) {
                            alpha = value;
                        }
                        black.decreaseMoveCount();
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            attackTile.setOccupant(enemy);
                            whites.add(removeIndex, enemy);
                            grid.setProtections(whites, blacks);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(black);
                    attackTile.setOccupant(enemy);
                    whites.add(removeIndex, enemy);
                    grid.setProtections(whites, blacks);
                }
            }
        }

        for (int pieceIndex = 0; pieceIndex != numberOfBlackPieces; ++pieceIndex) {
            final Piece black = blacks.get(pieceIndex);
            final int previousRow = black.getRow();
            final int previousColumn = black.getColumn();
            final Tile previousTile = grid.getTile(previousRow, previousColumn);
            final List<Tile> moveTiles = black.getMoveTiles(grid);
            for (int index = (moveTiles.size() - 1); index >= 0; --index) {
                Tile moveTile = moveTiles.get(index);
                previousTile.removeOccupant();
                if (black.isPawn() && previousRow == 6) {
                    Queen replace = Pawn.promote(black);
                    moveTile.setOccupant(replace);
                    int pawnIndex = blacks.indexOf(black);
                    blacks.set(pawnIndex, replace);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        replace.increaseMoveCount();
                        int result = min(board, depth, alpha, beta);
                        if (result > value) {
                            value = result;
                        }
                        if (value > alpha) {
                            alpha = value;
                        }
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            moveTile.removeOccupant();
                            blacks.set(pawnIndex, black);
                            grid.setProtections(whites, blacks);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(black);
                    moveTile.removeOccupant();
                    blacks.set(pawnIndex, black);
                    grid.setProtections(whites, blacks);
                }
                else {
                    moveTile.setOccupant(black);
                    grid.setProtections(whites, blacks);
                    if (!blackKing.inCheck(grid)) {
                        black.increaseMoveCount();
                        int result = min(board, depth, alpha, beta);
                        if (result > value) {
                            value = result;
                        }
                        if (value > alpha) {
                            alpha = value;
                        }
                        black.decreaseMoveCount();
                        if (beta <= alpha) {
                            previousTile.setOccupant(black);
                            moveTile.removeOccupant();
                            grid.setProtections(whites, blacks);
                            return alpha;
                        }
                    }
                    previousTile.setOccupant(black);
                    moveTile.removeOccupant();
                    grid.setProtections(whites, blacks);
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