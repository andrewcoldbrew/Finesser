package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
public class SuccessAlert{

    public MFXButton closeButton;
    public Text content;
    public Label title;

    public Stage stage;

    public MFXButton getCloseButton() {
        return closeButton;
    }

    public Text getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content.setText(content);
    }

    public Label getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
