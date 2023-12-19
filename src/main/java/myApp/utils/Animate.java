package myApp.utils;

import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animate {
    public static void addHoverScalingEffect(Node node, double val) {
        ScaleTransition scaleInTransition = createScaleTransition(node, 1.0, val);
        ScaleTransition scaleOutTransition = createScaleTransition(node, val, 1.0);

        node.setOnMouseEntered(event -> {
            scaleInTransition.play();
            node.setCursor(Cursor.HAND);
        });

        node.setOnMouseExited(event -> {
            scaleOutTransition.play();
            node.setCursor(Cursor.DEFAULT);
        });
    }

    private static ScaleTransition createScaleTransition(Node node, double fromValue, double toValue) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), node);
        scaleTransition.setFromX(fromValue);
        scaleTransition.setFromY(fromValue);
        scaleTransition.setToX(toValue);
        scaleTransition.setToY(toValue);
        return scaleTransition;
    }
}
