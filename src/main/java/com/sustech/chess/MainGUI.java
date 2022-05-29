package com.sustech.chess;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;

public class MainGUI extends Application {

    public static Stage primaryStage;
    public static double cellL = 100;


    public static void main(String[] args) {
        Application.launch(args);
    }

    public static void singlePlayerMenu() {
        Group loadMenu = new Group();
        Button loadFromSave = new Button("Load previous game");
        Button startNew = new Button("Start a new game");
        Button back = new Button("\uD83E\uDC14");
        back.setPrefSize(cellL, 0.5 * cellL);
        back.setLayoutX(cellL / 10);
        back.setLayoutY(cellL / 10);
        back.setFont(new Font("宋体", 30));
        loadMenu.getChildren().add(back);
        loadMenu.getChildren().add(loadFromSave);
        loadMenu.getChildren().add(startNew);
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MainGUI mainGUI = new MainGUI();
                mainGUI.start(primaryStage);
            }
        });
        loadFromSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.setInitialDirectory(new File("./"));
                File a = fileChooser.showOpenDialog(primaryStage);
                String name = a.getName();
                if (!name.contains(".json")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    String content = "Wrong file type!";
                    alert.setContentText(content);
                    alert.setHeaderText("File Reading Error 104");
                    alert.showAndWait();
                    singlePlayerMenu();
                    return;
                }
                startSinglePlayerLoad(SaveLoad.readJsonObjectFile(a.toString()));
            }
        });
        startNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startSinglePlayerNew();
            }
        });
        loadFromSave.setLayoutX(3.33 * cellL);
        loadFromSave.setLayoutY(6.66 * cellL);
        loadFromSave.setPrefSize(3.33 * cellL, cellL);
        startNew.setLayoutX(3.33 * cellL);
        startNew.setLayoutY(3.33 * cellL);
        startNew.setPrefSize(3.33 * cellL, cellL);
        primaryStage.setScene(new Scene(loadMenu, 10 * cellL, 10 * cellL));
    }

    public static void startSinglePlayerLoad(JSONObject save) {
        com.sustech.chess.BoardPane pane = new com.sustech.chess.BoardPane(primaryStage, save);
        pane.setOnMouseClicked(new Controller(pane));
        if (pane.isSuccess) {
            primaryStage.setScene(new Scene(pane, 12 * cellL, 10 * cellL));
        }else{
            MediaLoader.music.stop();
        }
        primaryStage.show();
    }

    public static void startSinglePlayerNew() {
        com.sustech.chess.BoardPane pane = new com.sustech.chess.BoardPane(primaryStage);
        pane.setOnMouseClicked(new Controller(pane));
        primaryStage.setScene(new Scene(pane, 12 * cellL, 10 * cellL));
        primaryStage.show();
    }

    public static void startMultiPlayerClient() {
        com.sustech.chess.BoardPane pane = new com.sustech.chess.BoardPane(primaryStage);
        pane.setOnMouseClicked(new Controller(pane));
        primaryStage.setScene(new Scene(pane, 12 * cellL, 12 * cellL));
        primaryStage.show();
    }

    public static void startMultiPlayerServer() {
        com.sustech.chess.BoardPane pane = new com.sustech.chess.BoardPane(primaryStage);
        pane.setOnMouseClicked(new Controller(pane));
        primaryStage.setScene(new Scene(pane, 12 * cellL, 10 * cellL));
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) {
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        cellL = (screenRectangle.getHeight() - 80) / 10;
        Controller.cellL = cellL;
        ChessPiece.cellL = cellL;
        MainGUI.primaryStage = primaryStage;
        primaryStage.setTitle("Chess");
        Group startMenu = new Group();
        Button startSinglePlayer = new Button("SinglePlay");
        Button startMultiPlayer = new Button("Multiplay");
        startSinglePlayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                singlePlayerMenu();
            }
        });
        startMultiPlayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        startSinglePlayer.setLayoutX(3.33 * cellL);
        startSinglePlayer.setLayoutY(3.33 * cellL);
        startMultiPlayer.setLayoutX(3.33 * cellL);
        startMultiPlayer.setLayoutY(6.66 * cellL);
        startSinglePlayer.setPrefSize(3.33 * cellL, cellL);
        startMultiPlayer.setPrefSize(3.33 * cellL, cellL);
        startMenu.getChildren().add(startSinglePlayer);
        startMenu.getChildren().add(startMultiPlayer);
        primaryStage.setScene(new Scene(startMenu, 10 * cellL, 10 * cellL));
        primaryStage.show();
    }
}

