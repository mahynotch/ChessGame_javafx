package com.sustech.chess;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class Settings extends Pane {
    private final Controller controller;

    public Settings(Controller controller) {
        this.controller = controller;
        ChoiceBox<Object> musicCB = new ChoiceBox<>();
        musicCB.setLayoutX(0);
        musicCB.setItems(FXCollections.observableArrayList(
                "Assassin's Creed", "Odyssey",
                "The Queen's High Seas", "Thomas the Tank Engine Theme")
        );
        String[] songs = new String[]{"Assassin's Creed", "Odyssey",
                "The Queen's High Seas", "Thomas the Tank Engine Theme"};
        musicCB.getSelectionModel().selectedIndexProperty().addListener((ov, oldv, newv) -> {
            MediaLoader.name = songs[newv.intValue()];
        });
        ChoiceBox<Object> lightColorCB = new ChoiceBox<>();
        lightColorCB.setLayoutY(MainGUI.cellL * 1);
        lightColorCB.setItems(FXCollections.observableArrayList(
                "White", "DarkGrey",
                "Grey", "GreenYellow", "Beige", "AntiqueWhite")
        );
        Color[] colors = new Color[]{Color.WHITE, Color.DARKGRAY, Color.GRAY, Color.GREENYELLOW, Color.BEIGE, Color.ANTIQUEWHITE};
        lightColorCB.getSelectionModel().selectedIndexProperty().addListener((ov, oldv, newv) -> {
            BoardPane.lighterColor = colors[newv.intValue()];
        });
        ChoiceBox<Object> darkColorCB = new ChoiceBox<>();
        darkColorCB.setLayoutX(MainGUI.cellL * 2);
        darkColorCB.setLayoutY(MainGUI.cellL * 1);
        darkColorCB.setItems(FXCollections.observableArrayList(
                "DarkGrey", "Golden",
                "Black", "ALICEBLUE")
        );
        Color[] dcolors = new Color[]{Color.DARKGRAY, Color.DARKGOLDENROD, Color.BLACK, Color.ALICEBLUE};
        darkColorCB.getSelectionModel().selectedIndexProperty().addListener((ov, oldv, newv) -> {
            BoardPane.deeperColor = dcolors[newv.intValue()];
        });
        ChoiceBox<Object> backgroundCB = new ChoiceBox<>();
        backgroundCB.setLayoutY(MainGUI.cellL * 2);
        backgroundCB.setItems(FXCollections.observableArrayList(
                "Tech", "Space",
                "Wood", "Walnut", "Ice")
        );
        String[] backGroundID = new String[]{"1", "2",
                "3", "4", "5"};
        backgroundCB.getSelectionModel().selectedIndexProperty().addListener((ov, oldv, newv) -> {
            BoardPane.backGroundID = backGroundID[newv.intValue()];
        });
        Button confirm = new Button("Confirm");
        confirm.setLayoutY(MainGUI.cellL * 2);
        confirm.setLayoutX(MainGUI.cellL * 4);
        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    MediaLoader.music.stop();
                    MediaLoader.mediaLoader();
                    controller.stage1.close();
                    controller.reload();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ChoiceBox<Object> chessPieceCB = new ChoiceBox<>();
        chessPieceCB.setLayoutX(MainGUI.cellL * 3);
        chessPieceCB.setItems(FXCollections.observableArrayList(
                "default", "demo")
        );
        String[] chessPices = new String[]{"default", "demo"};
        chessPieceCB.getSelectionModel().selectedIndexProperty().addListener((ov, oldv, newv) -> {
            Utils.picType = chessPices[newv.intValue()];
        });
        getChildren().add(darkColorCB);
        getChildren().add(lightColorCB);
        getChildren().add(backgroundCB);
        getChildren().add(musicCB);
        getChildren().add(confirm);
        getChildren().add(chessPieceCB);
    }
}
