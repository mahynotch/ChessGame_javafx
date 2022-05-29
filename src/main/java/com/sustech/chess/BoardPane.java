package com.sustech.chess;

import com.sustech.components.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardPane extends Pane {
    public static Color deeperColor = Color.DARKGRAY;
    public static Color lighterColor = Color.WHITESMOKE;
    public static String color;

    public static String backGroundID = "3";
    private final Stage primaryStage;
    //棋子列表
    private final ArrayList whiteChessList = new ArrayList<ChessPiece>();
    private final ArrayList blackChessList = new ArrayList<ChessPiece>();
    public List<ChessPiece> loadTaken = new ArrayList<>();
    public List<String> loadStep = new ArrayList<>();
    public Button reverseB;
    public Button settings;
    public Button back;
    public Label l;
    public double cellL;
    public int round;
    public AudioClip ac;
    public Label rnd;
    public Rectangle[][] MarkerList = new Rectangle[8][8];
    public HBox hBox;
    public boolean isSuccess = true;
    private String currentSide = "W";


    public BoardPane(Stage primaryStage) {
        cellL = MainGUI.cellL;
        this.primaryStage = primaryStage;
        initChess();
    }

    public BoardPane(Stage primaryStage, JSONObject save) {
        cellL = MainGUI.cellL;
        this.primaryStage = primaryStage;
        initChess(save);
    }

    public void drawBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rectangle r = new Rectangle(cellL + cellL * i, cellL + cellL * j, cellL, cellL);
                r.setStroke(Color.BLACK);
                Color color;
                r.setFill(color = i % 2 == 0 ^ j % 2 == 0 ? deeperColor : lighterColor);
                r.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        r.setFill(Color.GRAY);
                    }
                });
                r.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        r.setFill(color);
                    }
                });
                getChildren().add(r);
            }
        }
    }

    public void spawnMarkers() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Rectangle r = new Rectangle(x * cellL + cellL, y * cellL + cellL, cellL, cellL);
                r.setFill(Color.YELLOW);
                r.setStroke(Color.BLACK);
                r.setVisible(false);
                getChildren().add(r);
                MarkerList[y][x] = r;
            }
        }
    }

    public void initChess() {
        drawBoard();
        spawnMarkers();
        spawnUI();
        initChessPiece();
        initMusic();
    }

    public void initChess(JSONObject save) {
        drawBoard();
        spawnMarkers();
        initChessPiece(save);
        spawnUI();
        initMusic();
    }

    public void initMusic() {
        URL url = this.getClass().getResource("/assets/sound/mov.wav");
        ac = new AudioClip(url.toExternalForm());
        MediaLoader.mediaLoader().play();
    }

    public void spawnUI() {
        hBox = new HBox();
        reverseB = new Button("Reverse");
        l = new Label(currentSide);
        l.setLayoutX(5 * cellL);
        l.setPrefSize(cellL, cellL);
        rnd = new Label(String.valueOf(round));
        rnd.setLayoutX(9.5 * cellL);
        rnd.setPrefSize(cellL, cellL);
        getChildren().add(reverseB);
        reverseB.setPrefSize(2 * cellL, cellL);
        reverseB.setLayoutX(9.5 * cellL);
        reverseB.setLayoutY(2 * cellL);
        Button pauseOrStartMusic = new Button("⏯");
        pauseOrStartMusic.setFont(new Font("宋体", 30));
        pauseOrStartMusic.setLayoutX(2 * cellL);
        pauseOrStartMusic.setLayoutY(cellL / 10);
        pauseOrStartMusic.setPrefSize(cellL, cellL / 2);
        pauseOrStartMusic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (MediaLoader.music.getStatus() == MediaPlayer.Status.PLAYING) {
                    MediaLoader.music.pause();
                } else {
                    MediaLoader.music.play();
                }
            }
        });
        getChildren().add(pauseOrStartMusic);
        back = new Button("\uD83E\uDC14");
        back.setPrefSize(cellL, cellL / 2);
        back.setLayoutX(cellL / 10);
        back.setLayoutY(cellL / 10);
        back.setFont(new Font("宋体", 30));
        settings = new Button("Settings");
        settings.setPrefSize(2 * cellL, cellL);
        settings.setLayoutX(9.5 * cellL);
        settings.setLayoutY(6 * cellL);
        getChildren().add(back);
        getChildren().add(l);
        getChildren().add(rnd);
        getChildren().add(settings);
        Button reset = new Button("Reset");
        reset.setLayoutX(2 * cellL);
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                (new MainGUI()).startSinglePlayerNew();
            }
        });
        reset.setPrefSize(2 * cellL, cellL);
        reset.setLayoutX(9.5 * cellL);
        reset.setLayoutY(4 * cellL);
        getChildren().add(reset);
        URL url = this.getClass().getResource("/assets/backgrounds/" + backGroundID + ".jpg");
        Image img = new Image(url.toExternalForm());
        BackgroundImage bImg = new BackgroundImage(img,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background bGround = new Background(bImg);
        setBackground(bGround);
    }

    public void initChessPiece(JSONObject save) {
        save = new JSONObject(save.toString());
        if (save == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String content = "No input!";
            alert.setContentText(content);
            alert.setHeaderText("Error");
            alert.showAndWait();
            MainGUI.singlePlayerMenu();
            return;
        }
        JSONArray chessList = save.getJSONArray("chess");
        JSONArray takenList = save.getJSONArray("taken");
        for (Object i : chessList) {
            JSONObject j = (JSONObject) i;
            String Type = (String) j.get("pieceType");
            String side = (String) j.get("side");
            int x = (int) j.get("x");
            int y = (int) j.get("y");
            if (x > 7 || x < 0 || y > 7 || y < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                String content = "Out of boundary";
                alert.setContentText(content);
                alert.setHeaderText("File Reading Error 101");
                alert.showAndWait();
                MainGUI.singlePlayerMenu();
                isSuccess = false;
                return;
            }
            if (side.equals("Black")) {
                if (Type.equals("PAWN")) {
                    blackPawn(x, y);
                } else if (Type.equals("ROOK")) {
                    blackRook(x, y);
                } else if (Type.equals("KNIGHT")) {
                    blackKnight(x, y);
                } else if (Type.equals("BISHOP")) {
                    blackBishop(x, y);
                } else if (Type.equals("KING")) {
                    blackKing(x, y);
                } else if (Type.equals("QUEEN")) {
                    blackQueen(x, y);
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    String content = "Wrong piece type!";
                    alert.setContentText(content);
                    alert.setHeaderText("File Reading Error 102");
                    alert.showAndWait();
                    MainGUI.singlePlayerMenu();
                    isSuccess = false;
                    return;
                }
            } else if (side.equals("White")) {
                if (Type.equals("PAWN")) {
                    whitePawn(x, y);
                } else if (Type.equals("ROOK")) {
                    whiteRook(x, y);
                } else if (Type.equals("KNIGHT")) {
                    whiteKnight(x, y);
                } else if (Type.equals("BISHOP")) {
                    whiteBishop(x, y);
                } else if (Type.equals("KING")) {
                    whiteKing(x, y);
                } else if (Type.equals("QUEEN")) {
                    whiteQueen(x, y);
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    String content = "Wrong piece type!";
                    alert.setContentText(content);
                    alert.setHeaderText("File Reading Error 102");
                    alert.showAndWait();
                    MainGUI.singlePlayerMenu();
                    isSuccess = false;
                    return;
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                String content = "Wrong piece side!";
                alert.setContentText(content);
                alert.setHeaderText("File Reading Error 102");
                alert.showAndWait();
                MainGUI.singlePlayerMenu();
                isSuccess = false;
                return;
            }
        }
        for (Object i : takenList) {
            JSONObject j = (JSONObject) i;
            String Type = (String) j.get("pieceType");
            String side = (String) j.get("side");
            int x = (int) j.get("x");
            int y = (int) j.get("y");
            if (x > 7 || x < 0 || y > 7 || y < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                String content = "Out of boundary";
                alert.setContentText(content);
                alert.setHeaderText("File Reading Error 101");
                alert.showAndWait();
                MainGUI.singlePlayerMenu();
                isSuccess = false;
                return;
            }
            if (side.equals("Black")) {
                if (Type.equals("PAWN")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.PAWN, "Black", new BlackPawnComponent(), this)));
                } else if (Type.equals("ROOK")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.ROOK, "Black", new RookComponent(), this)));
                } else if (Type.equals("KNIGHT")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.KNIGHT, "Black", new KnightComponent(), this)));
                } else if (Type.equals("BISHOP")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.BISHOP, "Black", new BishopComponent(), this)));
                } else if (Type.equals("KING")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.KING, "Black", new KingComponent(), this)));
                } else if (Type.equals("QUEEN")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.QUEEN, "Black", new BishopComponent(), this)));
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    String content = "Wrong piece type!";
                    alert.setContentText(content);
                    alert.setHeaderText("File Reading Error 102");
                    alert.showAndWait();
                    MainGUI.singlePlayerMenu();
                    isSuccess = false;
                    return;
                }
            } else if (side.equals("White")) {
                if (Type.equals("PAWN")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.PAWN, "White", new WhitePawnComponent(), this)));
                } else if (Type.equals("ROOK")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.ROOK, "White", new RookComponent(), this)));
                } else if (Type.equals("KNIGHT")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.KNIGHT, "White", new KnightComponent(), this)));
                } else if (Type.equals("BISHOP")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.BISHOP, "White", new BishopComponent(), this)));
                } else if (Type.equals("KING")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.KING, "White", new KingComponent(), this)));
                } else if (Type.equals("QUEEN")) {
                    loadTaken.add(reshapeTakenImageView(new ChessPiece(x, y, PieceTypes.QUEEN, "White", new BishopComponent(), this)));
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    String content = "Wrong piece type!";
                    alert.setContentText(content);
                    alert.setHeaderText("File Reading Error 102");
                    alert.showAndWait();
                    MainGUI.singlePlayerMenu();
                    isSuccess = false;
                    return;
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                String content = "Wrong piece side!";
                alert.setContentText(content);
                alert.setHeaderText("File Reading Error 102");
                alert.showAndWait();
                MainGUI.singlePlayerMenu();
                isSuccess = false;
                return;
            }
        }
        for (Object i : save.getJSONArray("step")) {
            loadStep.add((String) i);
        }
        try {
            currentSide = (String) save.get("currentPlayer");
            if (!currentSide.equals("B") && !currentSide.equals("W")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                String content = "Wrong currentplayer!";
                alert.setContentText(content);
                alert.setHeaderText("File Reading Error 103");
                alert.showAndWait();
                MainGUI.singlePlayerMenu();
                isSuccess = false;
                return;
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String content = "No currentplayer!";
            alert.setContentText(content);
            alert.setHeaderText("File Reading Error 103");
            alert.showAndWait();
            MainGUI.singlePlayerMenu();
            isSuccess = false;
            return;
        }
        round = (loadStep.size() + 1) / 2;
    }

    public void initChessPiece() {
        JSONArray injs = SaveLoad.readJsonArrayFile("save.json");
        for (Object i : injs) {
            JSONObject j = (JSONObject) i;
            String Type = (String) j.get("pieceType");
            String side = (String) j.get("side");
            int x = (int) j.get("x");
            int y = (int) j.get("y");
            if (side.equals("Black")) {
                if (Type.equals("PAWN")) {
                    blackPawn(x, y);
                } else if (Type.equals("ROOK")) {
                    blackRook(x, y);
                } else if (Type.equals("KNIGHT")) {
                    blackKnight(x, y);
                } else if (Type.equals("BISHOP")) {
                    blackBishop(x, y);
                } else if (Type.equals("KING")) {
                    blackKing(x, y);
                } else if (Type.equals("QUEEN")) {
                    blackQueen(x, y);
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    String content = "File Reading Error!";
                    alert.setContentText(content);
                    alert.setHeaderText("Error");
                    alert.showAndWait();
                }
            } else if (side.equals("White")) {
                if (Type.equals("PAWN")) {
                    whitePawn(x, y);
                } else if (Type.equals("ROOK")) {
                    whiteRook(x, y);
                } else if (Type.equals("KNIGHT")) {
                    whiteKnight(x, y);
                } else if (Type.equals("BISHOP")) {
                    whiteBishop(x, y);
                } else if (Type.equals("KING")) {
                    whiteKing(x, y);
                } else if (Type.equals("QUEEN")) {
                    whiteQueen(x, y);
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    String content = "File Reading Error!";
                    alert.setContentText(content);
                    alert.setHeaderText("Error");
                    alert.showAndWait();
                }
            }
        }
    }

    public void reshapeImageView(int x, int y, ChessPiece piece) {
        piece.getImageView().setX(cellL + x * cellL);
        piece.getImageView().setY(cellL + y * cellL);
        piece.getImageView().setScaleX(cellL / 100);
        piece.getImageView().setScaleY(cellL / 100);
        piece.getImageView().setFitHeight(cellL);
        piece.getImageView().setFitWidth(cellL);
    }

    public ChessPiece reshapeTakenImageView(ChessPiece piece) {
        int x = piece.getX();
        int y = piece.getY();
        reshapeImageView(x, y, piece);
        piece.getImageView().setVisible(false);
        getChildren().add(piece.getImageView());
        return piece;
    }

    public void blackPawn(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.PAWN, "Black", new BlackPawnComponent(), this);
        blackChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void blackKing(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.KING, "Black", new KingComponent(), this);
        blackChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void blackQueen(int x, int y) {
        ChessPiece piece = new ChessPiece(3, 0, PieceTypes.QUEEN, "Black", new QueenComponent(), this);
        blackChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void blackKnight(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.KNIGHT, "Black", new KnightComponent(), this);
        blackChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void blackBishop(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.BISHOP, "Black", new BishopComponent(), this);
        blackChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void blackRook(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.ROOK, "Black", new RookComponent(), this);
        blackChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void whitePawn(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.PAWN, "White", new WhitePawnComponent(), this);
        whiteChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void whiteKing(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.KING, "White", new KingComponent(), this);
        whiteChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void whiteQueen(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.QUEEN, "White", new QueenComponent(), this);
        whiteChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void whiteKnight(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.KNIGHT, "White", new KnightComponent(), this);
        whiteChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void whiteBishop(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.BISHOP, "White", new BishopComponent(), this);
        whiteChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public void whiteRook(int x, int y) {
        ChessPiece piece = new ChessPiece(x, y, PieceTypes.ROOK, "White", new RookComponent(), this);
        whiteChessList.add(piece);
        reshapeImageView(x, y, piece);
        getChildren().add(piece.getImageView());
    }

    public ChessPiece getPiece(int x, int y) {
        for (int i = 0; i < getWhiteChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) getWhiteChessList().get(i);
            if (piece.getX() == x && piece.getY() == y)
                return piece;
        }
        for (int i = 0; i < getBlackChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) getBlackChessList().get(i);
            if (piece.getX() == x && piece.getY() == y)
                return piece;
        }
        return null;
    }

    public ChessPiece getWhitePiece(int x, int y) {
        for (int i = 0; i < getWhiteChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) getWhiteChessList().get(i);
            if (piece.getX() == x && piece.getY() == y)
                return piece;
        }
        return null;
    }

    public ChessPiece getBlackPiece(int x, int y) {
        for (int i = 0; i < getBlackChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) getBlackChessList().get(i);
            if (piece.getX() == x && piece.getY() == y)
                return piece;
        }
        return null;
    }


    public ArrayList getWhiteChessList() {
        return whiteChessList;
    }

    public ArrayList getBlackChessList() {
        return blackChessList;
    }

    public String getCurrentSide() {
        return currentSide;
    }

    public void setCurrentSide(String currentSide) {
        this.currentSide = currentSide;
    }


}