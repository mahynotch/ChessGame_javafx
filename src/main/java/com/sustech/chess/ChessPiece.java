package com.sustech.chess;

import com.sustech.components.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class ChessPiece {
    public boolean justMoved = false;
    public boolean justJustMoved = false;
    public boolean canShortCastle;
    public boolean canLongCastle;
    public byte canEnPassant = 0;
    private int x;
    private int y;
    private PieceTypes pieceType;
    private boolean selected;
    private MoveComponents moveComponent;
    private final ImageView imageView;
    private final String side;
    public static double cellL;
    private final BoardPane boardPane;

    public ChessPiece(int x, int y, PieceTypes pieceType, String side, MoveComponents moveComponent, BoardPane boardPane) {
        this.x = x;
        this.y = y;
        this.pieceType = pieceType;
        this.side = side;
        this.moveComponent = moveComponent;
        imageView = Utils.imageViewLoader(side + pieceType.location);
        this.boardPane = boardPane;
    }

    public void move(int x, int y) {
        int tempX = this.x;
        if (pieceType == PieceTypes.PAWN) {
            if (notMoved()) {
                justMoved = true;
            } else if (justMoved) {
                justJustMoved = true;
                justMoved = false;
            } else if (justJustMoved) {
                justJustMoved = false;
            }
            if (y == 7 || y == 0) {
                promotion();
            }
        }
        this.x = x;
        this.y = y;
//        TranslateTransition tt = new TranslateTransition();
//        tt.setDuration(Duration.seconds(1));
//        tt.setNode(imageView);
//        tt.setToX(100 + 100 * x);
//        tt.setToY(100 + 100 * y);
//        tt.play();
        imageView.setX(cellL + cellL * x);
        imageView.setY(cellL + cellL * y);
        if (pieceType == PieceTypes.KING && x - tempX == 2) {
            shortCastle();
        } else if (pieceType == PieceTypes.KING && tempX - x == 2) {
            longCastle();
        }
        this.setSelected(false);
    }

    public BoardPane getBoardPane() {
        return boardPane;
    }

    public void reverseMove(int x, int y) {
        int tempX = this.x;
        this.x = x;
        this.y = y;
        imageView.setX(cellL + cellL * x);
        imageView.setY(cellL + cellL * y);
        if (justJustMoved) {
            justMoved = true;
            justJustMoved = false;
        }
    }

    public void promotion() {
        Group translate = new Group();
        Button toQueen = new Button("", Utils.imageViewLoader(side + PieceTypes.QUEEN.location));
        Button toRook = new Button("", Utils.imageViewLoader(side + PieceTypes.ROOK.location));
        Button toKnight = new Button("", Utils.imageViewLoader(side + PieceTypes.KNIGHT.location));
        Button toBishop = new Button("", Utils.imageViewLoader(side + PieceTypes.BISHOP.location));
        translate.getChildren().add(toBishop);
        translate.getChildren().add(toKnight);
        translate.getChildren().add(toRook);
        translate.getChildren().add(toQueen);
        Stage upgrade = new Stage();
        upgrade.setScene(new Scene(translate, 8 * MainGUI.cellL, 2 * MainGUI.cellL));
        toQueen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveComponent = new QueenComponent();
                imageView.setImage(Utils.pieceImageLoader(side + PieceTypes.QUEEN.location));
                pieceType = PieceTypes.QUEEN;
                upgrade.close();
            }
        });
        toRook.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveComponent = new RookComponent();
                imageView.setImage(Utils.pieceImageLoader(side + PieceTypes.ROOK.location));
                pieceType = PieceTypes.ROOK;
                upgrade.close();
            }
        });
        toKnight.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveComponent = new KnightComponent();
                imageView.setImage(Utils.pieceImageLoader(side + PieceTypes.KNIGHT.location));
                pieceType = PieceTypes.KNIGHT;
                upgrade.close();
            }
        });
        toBishop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                moveComponent = new BishopComponent();
                imageView.setImage(Utils.pieceImageLoader(side + PieceTypes.BISHOP.location));
                pieceType = PieceTypes.BISHOP;
                upgrade.close();
            }
        });
        toBishop.setPrefSize(2 * cellL, 2 * cellL);
        toBishop.setLayoutX(2 * cellL);
        toQueen.setPrefSize(2 * cellL, 2 * cellL);
        toKnight.setPrefSize(2 * cellL, 2 * cellL);
        toKnight.setLayoutX(4 * cellL);
        toRook.setPrefSize(2 * cellL, 2 * cellL);
        toRook.setLayoutX(6 * cellL);
        upgrade.show();
    }

    public void reversePromo(){
        moveComponent = side.equals("White") ? new WhitePawnComponent() : new BlackPawnComponent();
        imageView.setImage(Utils.pieceImageLoader(side + PieceTypes.PAWN.location));
        pieceType = PieceTypes.PAWN;
    }

    public void shortCastle() {
        if (side.equals("White")) {
            for (ChessPiece i : (ArrayList<ChessPiece>) getBoardPane().getWhiteChessList()) {
                if (i.getPieceType() == PieceTypes.ROOK && i.getX() == 7 && i.getY() == 7) {
                    i.move(5, 7);
                }
            }
        } else if (side.equals("Black")) {
            for (ChessPiece i : (ArrayList<ChessPiece>) getBoardPane().getBlackChessList()) {
                if (i.getPieceType() == PieceTypes.ROOK && i.getX() == 7 && i.getY() == 0) {
                    i.move(5, 0);
                }
            }
        }
    }

    public void longCastle() {
        if (side.equals("White")) {
            for (ChessPiece i : (ArrayList<ChessPiece>) getBoardPane().getWhiteChessList()) {
                if (i.getPieceType() == PieceTypes.ROOK && i.getX() == 0 && i.getY() == 7) {
                    i.move(3, 7);
                }
            }
        } else if (side.equals("Black")) {
            for (ChessPiece i : (ArrayList<ChessPiece>) getBoardPane().getBlackChessList()) {
                if (i.getPieceType() == PieceTypes.ROOK && i.getX() == 0 && i.getY() == 0) {
                    i.move(3, 0);
                }
            }
        }
    }

    public boolean notMoved() {
        return (y == 1 && moveComponent instanceof BlackPawnComponent) || (y == 6 && moveComponent instanceof WhitePawnComponent);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getSide() {
        return side;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public PieceTypes getPieceType() {
        return pieceType;
    }

    public MoveComponents getMoveComponent() {
        return moveComponent;
    }

    public void setMoveComponent(MoveComponents moveComponent) {
        this.moveComponent = moveComponent;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> out = new HashMap<>();
        out.put("pieceType", pieceType.toString());
        out.put("side", side);
        out.put("x", x);
        out.put("y", y);
        return out;
    }
}