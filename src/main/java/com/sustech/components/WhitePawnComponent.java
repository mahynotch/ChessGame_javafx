package com.sustech.components;

import com.sustech.chess.ChessPiece;

import java.util.List;

public class WhitePawnComponent implements MoveComponents {
    public boolean justMoved;
    private boolean hasMoved;
    private List<ChessPiece> enemyList;

    @Override
    public void setJustMoved(boolean justMoved) {
        this.justMoved = justMoved;
    }

    @Override
    public boolean canMove(ChessPiece currentChessPiece, int[][] chess, int x, int y) {
        int x1 = currentChessPiece.getX();
        int y1 = currentChessPiece.getY();
        if (!currentChessPiece.getSide().equals(chess[y][x] == 1 ? "White" : "Black")) {
            if (y == y1 - 1 && x == x1) {
                return isEmpty(x, y, x1, y1, chess);
            } else if (currentChessPiece.notMoved() && y == y1 - 2 && x == x1) {
                return isEmpty(x, y, x1, y1, chess);
            } else if ((y == y1 - 1 && (x == x1 + 1 || x == x1 - 1)) && chess[y][x] == 2) {
                return true;
            } else if ((y == y1 - 1 && (x == x1 + 1 || x == x1 - 1)) && chess[y1][x] == 2) {
                ChessPiece i = currentChessPiece.getBoardPane().getBlackPiece(x, y1);
                try {
                    if (i.justMoved && i.getMoveComponent() instanceof BlackPawnComponent) {
                        return true;
                    }
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    public boolean isEmpty(int x, int y, int x1, int y1, int[][] chess) {
        return chess[y][x] == 0;
    }

    @Override
    public boolean getHasMoved() {
        return hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
