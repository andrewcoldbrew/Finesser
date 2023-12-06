package myApp.utils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Draggable {
    private double startX;
    private double startY;

    public void makeDraggable(Node node){

        node.setOnMousePressed(e -> {
            startX = e.getSceneX() - node.getTranslateX();
            startY = e.getSceneY() - node.getTranslateY();
        });

        node.setOnMouseDragged(e -> {
            node.setTranslateX(e.getSceneX() - startX);
            node.setTranslateY(e.getSceneY() - startY);
        });
    }

    public void makeDraggable(Stage stage){
        stage.getScene().setOnMousePressed(e -> {
            startX = e.getScreenX() - stage.getX();
            startY = e.getScreenY() - stage.getY();
        });

        stage.getScene().setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - startX);
            stage.setY(e.getScreenY() - startY);
        });
    }
}
