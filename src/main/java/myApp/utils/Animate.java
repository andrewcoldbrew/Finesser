package myApp.utils;

import javafx.animation.*;
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

    public static void fadeOutLeft(Node node) {
        // Create a FadeTransition for the fade-out effect
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Create a TranslateTransition for the leftward translation
        TranslateTransition translateLeft = new TranslateTransition(Duration.seconds(1), node);
        translateLeft.setByX(-30);

        // Combine the two transitions using a ParallelTransition
        ParallelTransition parallelTransition = new ParallelTransition(fadeOut, translateLeft);

        // Play the combined transition
        parallelTransition.play();
    }
}
