package com.sustech.chess;

public enum PieceTypes {
    QUEEN("_queen.png"), KING("_king.png"), ROOK("_rook.png"), BISHOP("_bishop.png"), PAWN("_pawn.png"), KNIGHT("_knight.png");
    public String location;

    PieceTypes(String location) {
        this.location = location;
    }
}
