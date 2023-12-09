package com.okayu.homework;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class Icon extends ImageView {
    public Icon(String path, int size){
        super();
        try (var fullpath = getClass().getResourceAsStream("icons/" + path + ".png")) {
            if(fullpath != null) setImage(new Image(fullpath));
        }catch (IOException e){
            e.printStackTrace();
        }
        setFitHeight(size);
        setFitWidth(size);
    }
}
