package myApp.controllers.components;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import myApp.utils.ImageBlender;

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
            Image successImage = new Image("/images/account/success.png");
            Image blendedImage = ImageBlender.blendColor(successImage, Color.GREEN);
            successIcon.setImage(blendedImage);

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

        // Set initial transparency to 0 (fully transparent)
        setOpacity(0);

        // Create a fade-in transition
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.1), this);
        fadeIn.setToValue(1); // Set the target opacity to 1 (fully opaque)

        stage.show();
        fadeIn.play();

        // Create a pause for 2 sec
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.1), this);
            fadeOut.setToValue(0); // Set the target opacity to 0 (fully transparent)
            // Set the action to be performed after the fade-out transition completes
            fadeOut.setOnFinished(fadeEvent -> {
                stage.close();
            });
            fadeOut.play();
        });


        pause.play();
    }
}