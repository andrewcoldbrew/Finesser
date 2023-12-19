package myApp.utils;

import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animate {

    public static void applyScaleInOut(Node node, double v1, double v2) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.25), node);
        scaleIn.setToX(v1);
        scaleIn.setToY(v1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.25), node);
        scaleOut.setToX(v2);
        scaleOut.setToY(v2);

        node.setOnMouseEntered(event -> scaleIn.play());
        node.setOnMouseExited(event -> scaleOut.play());
    }
}
