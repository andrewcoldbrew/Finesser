package myApp.controllers.components;

import animatefx.animation.FadeOut;
import animatefx.animation.Shake;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Iterator;

public class ErrorAlert extends BorderPane {
    public ImageView errorIcon;
    public Label errorTitle;
    public Text errorMessage;

    public ErrorAlert(Pane pane, String title, String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/errorAlert.fxml"));

        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);

            fxmlLoader.load();
            initialize(pane, title, message);


//            showAlert(scene);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(Pane pane, String title, String message) {
        errorTitle.setText(title);
        errorMessage.setText(message);

        if (!alertIsShown(pane)) {
            pane.getChildren().add(this);

            // Play Shake animation
            Shake shakeAnimation = new Shake(this);
            shakeAnimation.setSpeed(0.9);
            shakeAnimation.setOnFinished(event -> {
                // Play FadeOut animation after Shake finishes
                FadeOut fadeOutAnimation = new FadeOut(this);
                fadeOutAnimation.setDelay(Duration.seconds(3));
                fadeOutAnimation.setOnFinished(fadeEvent -> {
                    // Clear the alert after FadeOut finishes
                    Platform.runLater(() -> clearAlert(pane));
                });
                fadeOutAnimation.play();
            });
            shakeAnimation.play();
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


//    private void showAlert(Scene scene) {
//        // Create a new undecorated stage
//        Stage stage = new Stage();
//        stage.initStyle(StageStyle.UNDECORATED);
//        stage.setScene(scene);
//        stage.show();
//        new JackInTheBox(this).playOnFinished(new FadeOut(this)).play();
//    }
}
