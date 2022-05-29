package com.sustech.components;

import com.sustech.chess.ChessPiece;

public interface MoveComponents {
    public boolean hasMoved = false;
    public boolean justMoved = false;

    public void setJustMoved(boolean justMoved);

    public boolean canMove(ChessPiece chessPiece, int[][] board, int x, int y);

    public void setHasMoved(boolean hasMoved);

    public boolean getHasMoved();
}
