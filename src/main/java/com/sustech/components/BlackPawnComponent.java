package com.sustech.components;

import com.sustech.chess.ChessPiece;

public class BlackPawnComponent implements MoveComponents {
    public boolean justMoved;
    private boolean hasMoved;

    @Override
    public void setJustMoved(boolean justMoved) {
        this.justMoved = justMoved;
    }

    @Override
    public boolean canMove(ChessPiece currentChessPiece, int[][] chess, int x, int y) {
        int x1 = currentChessPiece.getX();
        int y1 = currentChessPiece.getY();
        if (y == y1 + 1 && x == x1) {
            return isEmpty(x, y, x1, y1, chess);
        } else if (currentChessPiece.notMoved() && y == y1 + 2 && x == x1) {
            return isEmpty(x, y, x1, y1, chess);
        } else if ((y == y1 + 1 && (x == x1 + 1 || x == x1 - 1)) && chess[y][x] == 1) {
            return true;
        } else if ((y == y1 + 1 && (x == x1 + 1 || x == x1 - 1)) && chess[y1][x] == 1) {
            ChessPiece i = currentChessPiece.getBoardPane().getWhitePiece(x, y1);
            try {
                if (i.justMoved && i.getMoveComponent() instanceof WhitePawnComponent) {
                    return true;
                }
            } catch (Exception e) {
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
