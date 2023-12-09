package com.okayu.homework;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

public class FontChecker extends Application {

    @Override
    public void start(Stage primaryStage) {
        Polyline path = new Polyline();
        var paths = path.getPoints();
        paths.addAll(0.0,0.0,10.0,10.0);
        path.setStroke(Color.RED);
        path.setStrokeWidth(10);
        primaryStage.setScene(new Scene(new Pane(path)));
        primaryStage.show();
    }
}
