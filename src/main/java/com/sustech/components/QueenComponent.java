package com.sustech.components;

import com.sustech.chess.ChessPiece;

public class QueenComponent implements MoveComponents {
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
            if (x == x1 || y == y1 || Math.abs(x - x1) == Math.abs(y - y1)) {
                if (isEmpty(x, y, x1, y1, chess)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEmpty(int x, int y, int x1, int y1, int[][] chess) {
        if (x < x1 && y < y1) {
            for (int i = 1; i < (x1 - x); i++)
                if (chess[y + i][x + i] != 0)
                    return false;
        } else if (x < x1 && y > y1) {
            for (int i = 1; i < (x1 - x); i++)
                if (chess[y - i][x + i] != 0)
                    return false;
        } else if (x > x1 && y < y1) {
            for (int i = 1; i < (x - x1); i++)
                if (chess[y + i][x - i] != 0)
                    return false;
        } else if (x > x1 && y > y1) {
            for (int i = 1; i < (x - x1); i++)
                if (chess[y - i][x - i] != 0)
                    return false;
        } else if (x == x1 && y < y1) {
            for (int i = 1; i < (y1 - y); i++)
                if (chess[y + i][x] != 0)
                    return false;
        } else if (x == x1 && y > y1) {
            for (int i = 1; i < y - y1; i++)
                if (chess[y - i][x] != 0)
                    return false;
        } else if (x > x1 && y == y1) {
            for (int i = 1; i < (x - x1); i++)
                if (chess[y][x - i] != 0)
                    return false;
        } else if (x < x1 && y == y1) {
            for (int i = 1; i < (x1 - x); i++)
                if (chess[y][x + i] != 0)
                    return false;
        }
        return true;
    }
}

