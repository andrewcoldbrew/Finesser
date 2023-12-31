package myApp.utils;

import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOutDown;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import myApp.controllers.components.ErrorAlert;
import myApp.controllers.components.SuccessAlert;

import java.io.IOException;

public class NotificationCenter {
    private static boolean alertShowing = false;

    public static void successAlert(String title, String content) {
        if (alertShowing) {
            return; // Ignore the request if an alert is already showing
        }

        alertShowing = true;

        FXMLLoader fxmlLoader = new FXMLLoader(NotificationCenter.class.getResource("/components/successAlert.fxml"));
        try {
            Parent root = fxmlLoader.load();
            SuccessAlert successAlert = fxmlLoader.getController();
            successAlert.setTitle(title);
            successAlert.setContent(content);

            Stage alertStage = createStage(root);

            // Animation
            playAlertAnimation(root, alertStage);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load SuccessAlert FXML", e);
        }
    }

    public static void errorAlert(String title, String content) {
        if (alertShowing) {
            return; // Ignore the request if an alert is already showing
        }

        alertShowing = true;

        FXMLLoader fxmlLoader = new FXMLLoader(NotificationCenter.class.getResource("/components/errorAlert.fxml"));
        try {
            Parent root = fxmlLoader.load();
            ErrorAlert errorAlert = fxmlLoader.getController();
            errorAlert.setTitle(title);
            errorAlert.setContent(content);

            Stage alertStage = createStage(root);

            // Animation
            playAlertAnimation(root, alertStage);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load ErrorAlert FXML", e);
        }
    }

    private static Stage createStage(Parent root) {
        Stage alertStage = new Stage();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.initOwner(null);
        Scene scene = new Scene(root, Color.TRANSPARENT);
        alertStage.setScene(scene);
        return alertStage;
    }

    private static void playAlertAnimation(Parent root, Stage alertStage) {
        alertStage.show();
        FadeInUp fadeIn = new FadeInUp(root);
        FadeOutDown fadeOut = new FadeOutDown(root);
        fadeOut.setOnFinished(actionEvent -> {
            alertStage.close();
            alertShowing = false; // Reset the flag when the alert is closed
        });
        fadeOut.setDelay(Duration.seconds(2.5));
        fadeIn.playOnFinished(fadeOut);
        fadeIn.play();

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double x = bounds.getMinX() + (bounds.getWidth() - alertStage.getWidth()) - 15;
        double y = bounds.getMinY() + (bounds.getHeight() - alertStage.getHeight()) - 15;
        alertStage.setX(x);
        alertStage.setY(y);
    }
}
