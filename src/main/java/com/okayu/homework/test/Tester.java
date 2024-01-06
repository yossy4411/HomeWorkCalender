package com.okayu.homework.test;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Tester extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Sliderを使用した複数範囲の選択
        Slider slider1 = new Slider(0, 100, 50);
        Slider slider2 = new Slider(0, 100, 75);

        // Spinnerを使用した複数範囲の選択
        Spinner<Integer> spinner1 = new Spinner<>(0, 100, 50);
        Spinner<Integer> spinner2 = new Spinner<>(0, 100, 75);

        // レイアウトの構築
        HBox root = new HBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                new Label("Slider 1:"),
                slider1,
                new Label("Slider 2:"),
                slider2,
                new Label("Spinner 1:"),
                spinner1,
                new Label("Spinner 2:"),
                spinner2
        );

        // シーンの作成
        Scene scene = new Scene(root, 600, 100);

        // ステージの設定
        primaryStage.setTitle("Multiple Range Selection Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
