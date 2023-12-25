package myApp.controllers.components;

import animatefx.animation.FadeOut;
import animatefx.animation.JackInTheBox;
import animatefx.animation.Pulse;
import animatefx.animation.Shake;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.DepthLevel;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import myApp.utils.ImageBlender;

import java.io.IOException;

public class ErrorAlert extends BorderPane {
    public ImageView errorIcon;
    public Label errorTitle;
    public Label errorMessage;

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
            new Shake(this).setSpeed(0.9).play();
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

//    private void showAlert(Scene scene) {
//        // Create a new undecorated stage
//        Stage stage = new Stage();
//        stage.initStyle(StageStyle.UNDECORATED);
//        stage.setScene(scene);
//        stage.show();
//        new JackInTheBox(this).playOnFinished(new FadeOut(this)).play();
//    }
}
