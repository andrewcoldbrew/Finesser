package myApp.controllers.components;

import animatefx.animation.*;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Iterator;

public class SuccessAlert extends MFXSimpleNotification {
    @FXML
    private BorderPane successAlert;
    public ImageView successIcon;
    public Text messageLabel;

    public SuccessAlert(Pane pane, String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/successAlert.fxml"));

        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            initialize(pane, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(Pane pane, String message) {
        messageLabel.setText(message);
        if (!alertIsShown(pane)) {
            pane.getChildren().add(successAlert);

            // Play FadeIn animation
            FadeIn fadeInAnimation = new FadeIn(successAlert);
            fadeInAnimation.setSpeed(1.25);
            fadeInAnimation.setOnFinished(fadeInEvent -> {
                // Play FadeOut animation after FadeIn finishes
                FadeOut fadeOutAnimation = new FadeOut(successAlert);
                fadeOutAnimation.setDelay(Duration.seconds(3));
                fadeOutAnimation.setSpeed(1.25);
                fadeOutAnimation.setOnFinished(fadeOutEvent -> {
                    // Clear the alert after FadeOut finishes
                    Platform.runLater(() -> clearAlert(pane));
                });
                fadeOutAnimation.play();
            });
            fadeInAnimation.play();
        }
    }

    private boolean alertIsShown(Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof ErrorAlert)
                return true;
        }
        return false;
    }

    private void clearAlert(Pane pane) {
        Iterator<Node> iterator = pane.getChildren().iterator();
        while (iterator.hasNext()) {
            Node child = iterator.next();
            if (child instanceof ErrorAlert) {
                iterator.remove(); // Remove the ErrorAlert from the children
                System.out.println("Alert cleared");
            }
        }
    }

}
