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

public class TestAlert extends BorderPane {
    public BorderPane notification;

    public TestAlert() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/testNotification.fxml"));

        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
//            initialize(pane, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BorderPane getNotification() {
        return notification;
    }
}
