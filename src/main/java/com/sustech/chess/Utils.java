package com.sustech.chess;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;

public class Utils {
    public static String picType = "default";

    public static ImageView imageViewLoader(String name) {
        try {
            return new ImageView(new Image(new FileInputStream("src/main/resources/assets/textures/" + picType + "/" + name)));
        } catch (Exception e) {
            System.out.println("Pic not found");
            return null;
        }
    }

    public static Image pieceImageLoader(String name) {
        try {
            return new Image(new FileInputStream("src/main/resources/assets/textures/" + picType + "/" + name));
        } catch (Exception e) {
            System.out.println("Pic not found");
            return null;
        }
    }

    public static Image imageLoader(String path) {
        try {
            return new Image(new FileInputStream("src/main/resources/" + path));
        } catch (Exception e) {
            return null;
        }
    }
}