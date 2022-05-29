package com.sustech.components;

import com.sustech.chess.ChessPiece;
import com.sustech.chess.PieceTypes;

import java.util.ArrayList;

public class KingComponent implements MoveComponents {
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
            if (Math.abs(x - x1) == 1 && Math.abs(y - y1) == 1 || Math.abs(x - x1) == 1 && y == y1 || x == x1 && Math.abs(y - y1) == 1) {
                return true;
            } else if (Math.abs(x - x1) == 2 && y == y1) {
                if ((currentChessPiece.getSide().equals("White") && x1 == 4 && y1 == 7)) {
                    for (ChessPiece i : (ArrayList<ChessPiece>) currentChessPiece.getBoardPane().getWhiteChessList()) {
                        if (i.getPieceType() == PieceTypes.ROOK && i.getX() == 7 && i.getY() == 7 && isEmpty(5, 7, chess) && isEmpty(6, 7, chess)) {
                            return true;
                        } else if (i.getPieceType() == PieceTypes.ROOK && i.getX() == 0 && i.getY() == 7 && isEmpty(1, 7, chess) && isEmpty(2, 7, chess) && isEmpty(3, 7, chess)) {
                            return true;
                        }
                    }
                } else if (currentChessPiece.getSide().equals("Black") && x1 == 4 && y1 == 0) {
                    for (ChessPiece i : (ArrayList<ChessPiece>) currentChessPiece.getBoardPane().getBlackChessList()) {
                        if (i.getPieceType() == PieceTypes.ROOK && i.getX() == 7 && i.getY() == 0 && isEmpty(5, 0, chess) && isEmpty(6, 0, chess)) {
                            return true;
                        } else if (i.getPieceType() == PieceTypes.ROOK && i.getX() == 0 && i.getY() == 0 && isEmpty(1, 0, chess) && isEmpty(2, 0, chess) && isEmpty(3, 0, chess)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isEmpty(int x, int y, int[][] chess) {
        if (chess[y][x] != 0) {
            return false;
        }
        return true;
    }
}