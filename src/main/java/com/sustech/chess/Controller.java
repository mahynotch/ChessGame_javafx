package com.sustech.chess;

import com.sustech.components.KingComponent;
import com.sustech.components.MoveComponents;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller implements EventHandler<MouseEvent> {
    public static double cellL;
    private final BoardPane boardPane;
    private final int[][] chess = new int[8][8];
    public Stage stage1 = new Stage();
    private int blackCheckedCnt = 0;
    private List<ChessPiece> taken = new ArrayList<>();
    private List<String> step = new ArrayList<>();
    private Timeline animation;
    private int whiteCheckedCnt = 0;
    private int tmp = 30;
    private Label cntDwnLabel;

    public Controller(BoardPane boardPane) {
        this.boardPane = boardPane;
        taken = boardPane.loadTaken;
        step = boardPane.loadStep;
        initCountdown();
        Controller controller = this;
        boardPane.settings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage1 = new Stage();
                stage1.setScene(new Scene(new Settings(controller), cellL * 5, cellL * 3));
                stage1.show();
            }
        });
        boardPane.reverseB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reverse();
            }
        });
        boardPane.back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Group group = new Group();
                Button toMainMenu = new Button("return to main menu");
                Button saveGame = new Button("save game");
                group.getChildren().add(saveGame);
                group.getChildren().add(toMainMenu);
                Stage stage = new Stage();
                stage.setScene(new Scene(group, 8 * cellL, 2 * cellL));
                toMainMenu.setPrefSize(2 * cellL, cellL);
                toMainMenu.setLayoutX(cellL / 2);
                toMainMenu.setLayoutY(cellL / 2);
                saveGame.setPrefSize(2 * cellL, cellL);
                saveGame.setLayoutX(3.5 * cellL);
                saveGame.setLayoutY(cellL / 2);
                stage.show();
                saveGame.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        FileChooser fileChooser1 = new FileChooser();
                        fileChooser1.setTitle("Save");
                        File path = new File("./");
                        fileChooser1.setInitialDirectory(path);
                        fileChooser1.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON File", "*.json"));
                        File file = fileChooser1.showSaveDialog(MainGUI.primaryStage);
                        try {
                            SaveLoad.writeJsonFile(save(), file.getPath());
                        } catch (IOException e) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            String content = "Write Fail!";
                            alert.setContentText(content);
                            alert.setHeaderText("File Writing Error");
                            alert.showAndWait();
                        } finally {
                            MediaLoader.music.stop();
                            MainGUI mainGUI = new MainGUI();
                            mainGUI.start(MainGUI.primaryStage);
                            stage.close();
                        }
                    }
                });
                toMainMenu.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        MainGUI mainGUI = new MainGUI();
                        mainGUI.start(MainGUI.primaryStage);
                        MediaLoader.music.stop();
                        stage.close();
                    }
                });
            }
        });
    }

    public void initCountdown() {
        cntDwnLabel = new Label("30");
        cntDwnLabel.setFont(javafx.scene.text.Font.font(20));
        cntDwnLabel.setLayoutX(cellL * 8);
        cntDwnLabel.setLayoutY(cellL / 5);
        boardPane.getChildren().add(cntDwnLabel);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), e -> timelabel());
        animation = new Timeline(keyFrame);
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    public void timelabel() {
        tmp--;
        cntDwnLabel.setText(String.valueOf(tmp));
        if (tmp == 0) {
            addStep(0, 0, 0, 0, false);
            tmp = 30;
            boardPane.setCurrentSide(boardPane.getCurrentSide().equals("W") ? "B" : "W");
            boardPane.l.setText(boardPane.getCurrentSide());
        }
    }

    public void restartCntDwn() {
        tmp = 30;
        cntDwnLabel.setText(String.valueOf(tmp));
    }

    public void reload() throws IOException {
        MainGUI.startSinglePlayerLoad(save());
    }

    public void isBlackCheckMate(int x, int y, ChessPiece foe) {
        int x1 = foe.getX(), y1 = foe.getY();
        boolean cantHide = true;
        if (foe.getPieceType() == PieceTypes.BISHOP) {
            if (x < x1 && y < y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x < x1 && y > y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y < y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y > y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            }
        } else if (foe.getPieceType() == PieceTypes.ROOK) {
            if (x == x1 && y < y1) {
                for (int i = 1; i < (y1 - y); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x == x1 && y > y1) {
                for (int i = 1; i < y - y1; i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y == y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x < x1 && y == y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y)) {
                            cantHide = false;
                        }
                    }
                }
            }
        } else if (foe.getPieceType() == PieceTypes.QUEEN) {
            if (x < x1 && y < y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x < x1 && y > y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y < y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y > y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x == x1 && y < y1) {
                for (int i = 1; i < (y1 - y); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x == x1 && y > y1) {
                for (int i = 1; i < y - y1; i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y == y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x < x1 && y == y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y)) {
                            cantHide = false;
                        }
                    }
                }
            }
        }
        ChessPiece king = getPiece(x, y);
        int total = 0, cantmove = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (king.getMoveComponent().canMove(king, chess, i, j)) {
                    total++;
                    for (ChessPiece k : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (k.getMoveComponent().canMove(k, chess, i, j)) {
                            cantmove++;
                        }
                    }
                }
            }
        }
//        boolean aimed = false;
//        for (ChessPiece k : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
//            if (k.getMoveComponent().canMove(k, chess, x1, y1)) {
//                aimed = true;
//                chess[foe.getX()][foe.getY()] = 0;
//                for (ChessPiece l : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
//                    if (l.getMoveComponent().canMove(l, chess, foe.getX(), foe.getY()) && k.getPieceType() == PieceTypes.KING) {
//                        aimed = false;
//                    }
//                }
//                chess[foe.getX()][foe.getY()] = 2;
//            }
//        }
        if (cantHide && total == cantmove) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String content = "Winner is White";
            alert.setContentText(content);
            alert.setHeaderText("Winner");
            alert.showAndWait();
            new MainGUI().start(MainGUI.primaryStage);
        }
    }

    public void isWhiteCheckMate(int x, int y, ChessPiece foe) {
        int x1 = foe.getX(), y1 = foe.getY();
        boolean cantHide = true;
        if (foe.getPieceType() == PieceTypes.BISHOP) {
            if (x < x1 && y < y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x < x1 && y > y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y < y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y > y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            }
        } else if (foe.getPieceType() == PieceTypes.ROOK) {
            if (x == x1 && y < y1) {
                for (int i = 1; i < (y1 - y); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x == x1 && y > y1) {
                for (int i = 1; i < y - y1; i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y == y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x < x1 && y == y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y)) {
                            cantHide = false;
                        }
                    }
                }
            }
        } else if (foe.getPieceType() == PieceTypes.QUEEN) {
            if (x < x1 && y < y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x < x1 && y > y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y < y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y > y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x == x1 && y < y1) {
                for (int i = 1; i < (y1 - y); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x, y + 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x == x1 && y > y1) {
                for (int i = 1; i < y - y1; i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x, y - 1)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x > x1 && y == y1) {
                for (int i = 1; i < (x - x1); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x - 1, y)) {
                            cantHide = false;
                        }
                    }
                }
            } else if (x < x1 && y == y1) {
                for (int i = 1; i < (x1 - x); i++) {
                    for (ChessPiece j : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
                        if (j.getMoveComponent().canMove(j, chess, x + 1, y)) {
                            cantHide = false;
                        }
                    }
                }
            }
        }
        ChessPiece king = getPiece(x, y);
        int total = 0, cantmove = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (king.getMoveComponent().canMove(king, chess, i, j)) {
                    total++;
                    for (ChessPiece k : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
                        if (k.getMoveComponent().canMove(k, chess, i, j)) {
                            cantmove++;
                        }
                    }
                }
            }
        }
//        boolean aimed = false;
//        for (ChessPiece k : (ArrayList<ChessPiece>) boardPane.getBlackChessList()) {
//            if (k.getMoveComponent().canMove(k, chess, x1, y1)) {
//                aimed = true;
//                chess[foe.getX()][foe.getY()] = 0;
//                for (ChessPiece l : (ArrayList<ChessPiece>) boardPane.getWhiteChessList()) {
//                    if(l == foe) continue;
//                    if (l.getMoveComponent().canMove(l, chess, foe.getX(), foe.getY()) && k.getPieceType() == PieceTypes.KING) {
//                        aimed = false;
//                    }
//                }
//                chess[foe.getX()][foe.getY()] = 1;
//            }
//        }
        if (cantHide && total == cantmove) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String content = "Winner is Black";
            alert.setContentText(content);
            alert.setHeaderText("Winner");
            alert.showAndWait();
            new MainGUI().start(MainGUI.primaryStage);
        }
    }


    public int[][] getChess() {
        return chess;
    }

    @Override
    public void handle(MouseEvent event) {
        refresh();
        hideMovable();
        int x = (int) ((event.getX() - cellL) / cellL);
        int y = (int) ((event.getY() - cellL) / cellL);

        if (x > 7 || y > 7 || x < 0 || y < 0) return;

        if (isSelected() == null) {
            ChessPiece piece = isEmpty(x, y);
            if (piece == null)
                return;
            piece.setSelected(true);
            markMovable(piece);
        } else {
            ChessPiece currentPiece = isSelected();
            ChessPiece newPiece = isEmpty(x, y);
            if (newPiece == null) {
                ChessPiece nextPiece = getPiece(x, y);
                MoveComponents moveComponent = currentPiece.getMoveComponent();
                if (moveComponent.canMove(currentPiece, chess, x, y)) {
                    if (currentPiece.getPieceType() == PieceTypes.PAWN && nextPiece == null && Math.abs(currentPiece.getX() - x) == 1 && Math.abs(currentPiece.getY() - y) == 1) {
                        remove(getPiece(x, currentPiece.getY()));
                        taken.add(nextPiece);
                        addStep(currentPiece.getX(), currentPiece.getY(), x, y, true);
                    } else {
                        addStep(currentPiece.getX(), currentPiece.getY(), x, y, chess[y][x] != 0);
                    }
                    currentPiece.move(x, y);
                    boardPane.ac.play();
                    refresh();
                    if (currentPiece.getPieceType() == PieceTypes.PAWN && (y == 0 || y == 7))
                        step.set(step.size() - 1, step.get(step.size() - 1) + "t");
                    if (nextPiece != null) {
                        if (nextPiece.getPieceType() == PieceTypes.KING) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            String content = "Winner is " + (currentPiece.equals("W") ? "Black" : "White");
                            alert.setContentText(content);
                            alert.setHeaderText("Winner");
                            alert.showAndWait();
                            new MainGUI().start(MainGUI.primaryStage);
                        }
                        remove(nextPiece);
                        refresh();
                    }
                    if (boardPane.getCurrentSide().equals("W")) {
                        boardPane.setCurrentSide("B");
                        if (isWhiteChecked()) {
                            boardPane.MarkerList[y][x].setVisible(false);
//                            reverse();
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                            alert.setHeaderText("Wrong move");
//                            alert.setContentText("White king is under attack!");
//                            alert.showAndWait();
                        }
                        isBlackChecked();
                        boardPane.l.setText(boardPane.getCurrentSide());
                    } else {
                        boardPane.setCurrentSide("W");
                        if (isBlackChecked()) {
                            boardPane.MarkerList[y][x].setVisible(false);
//                            reverse();
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                            alert.setHeaderText("Wrong move");
//                            alert.setContentText("Black king is under attack!");
//                            alert.showAndWait();
                        }
                        isWhiteChecked();
                        boardPane.l.setText(boardPane.getCurrentSide());
                    }
                    boardPane.rnd.setText(String.valueOf((step.size() + 1) / 2));
                } else {
                    isSelected().setSelected(false);
                    hideMovable();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Wrong move");
                    alert.setContentText("This piece cannot move like this.");
                    alert.showAndWait();
                }
            } else {
                currentPiece.setSelected(false);
                newPiece.setSelected(true);
                hideMovable();
                markMovable(newPiece);
            }
        }
        refresh();
    }


    public void markMovable(ChessPiece currentPiece) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (currentPiece.getMoveComponent().canMove(currentPiece, chess, x, y)) {
                    boardPane.MarkerList[y][x].setVisible(true);
                    boardPane.MarkerList[y][x].setFill(Color.YELLOW);
                }
            }
        }
    }

    public void hideMovable() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                boardPane.MarkerList[y][x].setVisible(false);
            }
        }
    }

    public boolean isWhiteChecked() {
        int x = 0, y = 0;
        for (Object i : boardPane.getWhiteChessList()) {
            if (((ChessPiece) i).getMoveComponent() instanceof KingComponent) {
                x = ((ChessPiece) i).getX();
                y = ((ChessPiece) i).getY();
                break;
            }
        }
        for (Object i : boardPane.getBlackChessList()) {
            if (((ChessPiece) i).getMoveComponent().canMove((ChessPiece) i, chess, x, y)) {
                Rectangle marker = boardPane.MarkerList[y][x];
                marker.setFill(Color.CRIMSON);
                marker.setVisible(true);
                whiteCheckedCnt++;
                if (whiteCheckedCnt == 3) assertDraw();
                isWhiteCheckMate(x, y, (ChessPiece) i);
                return true;
            }
        }
        whiteCheckedCnt = 0;
        return false;
    }

    public boolean isBlackChecked() {
        int x = 0, y = 0;
        for (Object i : boardPane.getBlackChessList()) {
            if (((ChessPiece) i).getMoveComponent() instanceof KingComponent) {
                x = ((ChessPiece) i).getX();
                y = ((ChessPiece) i).getY();
                break;
            }
        }
        for (Object i : boardPane.getWhiteChessList()) {
            if (((ChessPiece) i).getMoveComponent().canMove((ChessPiece) i, chess, x, y)) {
                Rectangle marker = boardPane.MarkerList[y][x];
                marker.setFill(Color.CRIMSON);
                marker.setVisible(true);
                blackCheckedCnt++;
                if (blackCheckedCnt == 3) assertDraw();
                isBlackCheckMate(x, y, (ChessPiece) i);
                return true;
            }
        }
        blackCheckedCnt = 0;
        return false;
    }

    public ChessPiece isSelected() {
        if (boardPane.getCurrentSide().equals("W")) {
            for (int i = 0; i < boardPane.getWhiteChessList().size(); i++) {
                ChessPiece piece = (ChessPiece) boardPane.getWhiteChessList().get(i);
                if (piece.isSelected())
                    return piece;
            }
        } else {
            for (int i = 0; i < boardPane.getBlackChessList().size(); i++) {
                ChessPiece piece = (ChessPiece) boardPane.getBlackChessList().get(i);
                if (piece.isSelected())
                    return piece;
            }
        }
        return null;
    }

    public List<String> getStep() {
        return step;
    }


    public void addStep(int x1, int y1, int x, int y, boolean eat) {
        restartCntDwn();
        if (x1 == 0 && y1 == 0 && x == 0 && y == 0) {
            step.add("");
        } else {
            step.add(String.format(eat ? "%d%d%d%dx" : "%d%d%d%d", x1, y1, x, y));
        }
        if (step.size() >= 10)
            if (step.get(step.size() - 1).equals(step.get(step.size() - 5)) && step.get(step.size() - 5).equals(step.get(step.size() - 9)) && step.get(step.size() - 2).equals(step.get(step.size() - 6)) && step.get(step.size() - 6).equals(step.get(step.size() - 10)))
                assertDraw();
    }

    public void assertDraw() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String content = "You've Reached a Draw!";
        alert.setContentText(content);
        alert.setHeaderText("Draw");
        alert.show();
        MediaLoader.music.stop();
        new MainGUI().start(MainGUI.primaryStage);
    }

    public void reverse() {
        String lastStep = step.get(step.size() - 1);
        restartCntDwn();
        try {
            step.remove(step.size() - 1);
            int i = Integer.parseInt(String.valueOf(lastStep.charAt(2)));
            ChessPiece piece = step.size() % 2 == 0 ? getWhitePiece(i, Integer.parseInt(String.valueOf(lastStep.charAt(3)))) : getBlackPiece(i, Integer.parseInt(String.valueOf(lastStep.charAt(3))));
            int parseInt = Integer.parseInt(String.valueOf(lastStep.charAt(0)));
            if (piece.getPieceType() == PieceTypes.KING && i - parseInt == 2) {
                ChessPiece rook = getPiece(5, Integer.parseInt(String.valueOf(lastStep.charAt(1))));
                rook.reverseMove(7, Integer.parseInt(String.valueOf(lastStep.charAt(1))));
            } else if (piece.getPieceType() == PieceTypes.KING && parseInt - i == 2) {
                ChessPiece rook = getPiece(3, Integer.parseInt(String.valueOf(lastStep.charAt(1))));
                rook.reverseMove(0, Integer.parseInt(String.valueOf(lastStep.charAt(1))));
            }
            piece.reverseMove(parseInt, Integer.parseInt(String.valueOf(lastStep.charAt(1))));
            if (lastStep.contains("t")) piece.reversePromo();
            refresh();
        } catch (Exception e) {
        }
        if (lastStep.contains("x")) {
            ChessPiece revive = taken.get(taken.size() - 1);
            taken.remove(taken.size() - 1);
            revive.getImageView().setX(cellL + cellL * revive.getX());
            revive.getImageView().setY(cellL + cellL * revive.getY());
            revive.getImageView().setScaleX(cellL / 100);
            revive.getImageView().setScaleY(cellL / 100);
            revive.getImageView().setVisible(true);
            revive.getImageView().setDisable(false);
            if (revive.getSide().equals("White")) {
                boardPane.getWhiteChessList().add(revive);
            } else {
                boardPane.getBlackChessList().add(revive);
            }
        }
        boardPane.rnd.setText(String.valueOf((step.size() + 1) / 2));
        boardPane.setCurrentSide(boardPane.getCurrentSide().equals("W") ? "B" : "W");
        boardPane.l.setText(boardPane.getCurrentSide());
    }


    public ChessPiece isEmpty(int x, int y) {
        if (boardPane.getCurrentSide().equals("W")) {
            for (int i = 0; i < boardPane.getWhiteChessList().size(); i++) {
                ChessPiece piece = (ChessPiece) boardPane.getWhiteChessList().get(i);
                if (piece.getX() == x && piece.getY() == y)
                    return piece;
            }
        } else {
            for (int i = 0; i < boardPane.getBlackChessList().size(); i++) {
                ChessPiece piece = (ChessPiece) boardPane.getBlackChessList().get(i);
                if (piece.getX() == x && piece.getY() == y)
                    return piece;
            }
        }
        return null;
    }

    public ChessPiece getPiece(int x, int y) {
        for (int i = 0; i < boardPane.getWhiteChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) boardPane.getWhiteChessList().get(i);
            if (piece.getX() == x && piece.getY() == y)
                return piece;
        }
        for (int i = 0; i < boardPane.getBlackChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) boardPane.getBlackChessList().get(i);
            if (piece.getX() == x && piece.getY() == y)
                return piece;
        }
        return null;
    }

    public ChessPiece getWhitePiece(int x, int y) {
        for (int i = 0; i < boardPane.getWhiteChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) boardPane.getWhiteChessList().get(i);
            if (piece.getX() == x && piece.getY() == y)
                return piece;
        }
        return null;
    }

    public ChessPiece getBlackPiece(int x, int y) {
        for (int i = 0; i < boardPane.getBlackChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) boardPane.getBlackChessList().get(i);
            if (piece.getX() == x && piece.getY() == y)
                return piece;
        }
        return null;
    }

    public JSONObject save() throws IOException {
        JSONObject out = new JSONObject();
        for (ChessPiece i : (List<ChessPiece>) boardPane.getBlackChessList()) {
            out.append("chess", i.toMap());
        }
        for (ChessPiece i : (List<ChessPiece>) boardPane.getWhiteChessList()) {
            out.append("chess", i.toMap());
        }
        if (taken.size() != 0) {
            for (ChessPiece i : taken) {
                out.append("taken", i.toMap());
            }
        } else {
            out.put("taken", new JSONArray());
        }
        if (step.size() != 0) {
            out.put("step", step);
        } else {
            out.put("step", new JSONArray());
        }
        out.put("currentPlayer", boardPane.getCurrentSide());
        return out;
    }

    public void remove(ChessPiece piece) {
        taken.add(piece);
        piece.getImageView().setVisible(false);
        if (boardPane.getCurrentSide().equals("W")) {
            for (int i = 0; i < boardPane.getBlackChessList().size(); i++) {
                ChessPiece piece1 = (ChessPiece) boardPane.getBlackChessList().get(i);
                if (piece.equals(piece1)) {
                    boardPane.getBlackChessList().remove(i);
                    break;
                }
            }
        } else {
            for (int i = 0; i < boardPane.getWhiteChessList().size(); i++) {
                ChessPiece piece1 = (ChessPiece) boardPane.getWhiteChessList().get(i);
                if (piece.equals(piece1)) {
                    boardPane.getWhiteChessList().remove(i);
                    break;
                }
            }
        }
    }

    public void refresh() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                chess[i][j] = 0;
            }
        for (int i = 0; i < boardPane.getWhiteChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) boardPane.getWhiteChessList().get(i);
            chess[piece.getY()][piece.getX()] = 1;
        }
        for (int i = 0; i < boardPane.getBlackChessList().size(); i++) {
            ChessPiece piece = (ChessPiece) boardPane.getBlackChessList().get(i);
            chess[piece.getY()][piece.getX()] = 2;
        }
    }
}
