package com.okayu.homework;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        stage.setTitle("Hello!");
        AnchorPane anchorPane = fxmlLoader.load();
        Scene scene = new Scene(anchorPane, anchorPane.getPrefWidth(), anchorPane.getPrefHeight(), true, SceneAntialiasing.BALANCED);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}