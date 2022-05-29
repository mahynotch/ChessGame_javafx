package com.sustech.chess;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MediaLoader {
    public static MediaPlayer music;

    public static String name = "Assassin's Creed";

    public static MediaPlayer mediaLoader() {
        String path = new Object() {
            public String getPath() {
                return this.getClass().getResource("/assets/sound/" + name + ".mp3").toExternalForm();
            }
        }.getPath();
        Media media = new Media(path);
        MediaPlayer md = new MediaPlayer(media);
        music = md;
        return music;
    }
}
