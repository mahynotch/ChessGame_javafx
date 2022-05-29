package com.sustech.components;

import com.sustech.chess.ChessPiece;

public class KnightComponent implements MoveComponents {
    public boolean justMoved;
    private boolean hasMoved = false;

    @Override
    public void setJustMoved(boolean justMoved) {
        this.justMoved = justMoved;
    }

    @Override
    public boolean getHasMoved() {
        return hasMoved;
    }

    @Override
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public boolean canMove(ChessPiece currentChessPiece, int[][] chess, int x, int y) {
        int x1 = currentChessPiece.getX();
        int y1 = currentChessPiece.getY();
        if ((currentChessPiece.getSide().equals("White") && chess[y][x] != 1) || (currentChessPiece.getSide().equals("Black") && chess[y][x] != 2)) {
            if ((Math.abs(x - x1) == 1 && Math.abs(y - y1) == 2) || (Math.abs(x - x1) == 2 && Math.abs(y - y1) == 1)) {
                if (isEmpty(x, y, x1, y1, chess)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEmpty(int x, int y, int x1, int y1, int[][] chess) {
        return true;
    }
}
