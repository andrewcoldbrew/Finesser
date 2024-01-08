package myApp.controllers.components;

import animatefx.animation.FadeOut;
import animatefx.animation.Shake;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Iterator;

public class ErrorAlert{
    public Label title;
    public Text content;
    public MFXButton closeButton;
    public Stage stage;

    public Label getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public Text getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content.setText(content);
    }

    public MFXButton getCloseButton() {
        return closeButton;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
