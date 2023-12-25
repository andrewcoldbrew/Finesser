package myApp.controllers.components;

import animatefx.animation.*;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
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

public class SuccessAlert extends BorderPane {
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
            pane.getChildren().add(this);
            new FadeInUpBig(this).setSpeed(0.9).play();
            new FadeOut(this).setDelay(Duration.seconds(3)).play();
        }
    }

    private boolean alertIsShown(Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof ErrorAlert)
                return true;
        }
        return false;
    }
}
