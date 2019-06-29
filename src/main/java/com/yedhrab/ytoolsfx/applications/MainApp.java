package com.yedhrab.ytoolsfx.applications;

import com.yedhrab.ytoolsfx.controllers.FXMLController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    private double xOffset;
    private double yOffset;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/main.fxml"));

        primaryStage.initStyle(StageStyle.TRANSPARENT); // Windows çerçevesini kaldırır

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.getScene().setFill(Color.TRANSPARENT);
        primaryStage.show();

        //TODO: Hepsinini görünürlüğünü false yap

        root.setOnMousePressed(mouseEvent -> {
            FXMLController.isMouseDragging = false;

            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        root.setOnMouseDragged(mouseEvent -> {
            FXMLController.isMouseDragging= true;

            primaryStage.setX(mouseEvent.getScreenX() - xOffset);
            primaryStage.setY(mouseEvent.getScreenY() - yOffset);
        });
    }
}
