package myApp.controllers.components;

import animatefx.animation.AnimationFX;
import animatefx.animation.FadeOut;
import animatefx.animation.JackInTheBox;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class SuccessAlert extends BorderPane {
    public ImageView successIcon;
    public Label messageLabel;

    public SuccessAlert(String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/successAlert.fxml"));

        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            messageLabel.setText(message);


            // Show the stage with a fade-in transition
            showAlert(scene);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAlert(Scene scene) {
        // Create a new undecorated stage
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        new JackInTheBox(this).playOnFinished(new FadeOut(this)).play();
    }
}
