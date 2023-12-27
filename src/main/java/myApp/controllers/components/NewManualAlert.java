package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myApp.utils.Draggable;

import java.io.IOException;
import java.util.Map;

public class NewManualAlert extends VBox {

    public Text titleText;
    public Text contentText;
    public ImageView imageContainer;
    public ImageView closeIcon;
    public MFXButton okButton;
    public Button closeButton;
    public HBox buttonContainer;
    private Stage stage;
    private static final Map<Type, String> icons = Map.of(
            Type.ERROR, "/images/gif/error.gif",
            Type.SUCCESS, "/images/gif/check.gif",
            Type.WARNING, "/images/gif/warning.gif",
            Type.CONFIRMATION, "/images/gif/question.gif"
    );

    private Runnable yesAction; // Callback for Yes button
    private Runnable noAction;  // Callback for No button

    public enum Type {
        ERROR,
        WARNING,
        CONFIRMATION,
        SUCCESS
    }

    public NewManualAlert(Type type, String titleText, String contentText) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/manualAlert.fxml"));
        try {
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            initialize(type, titleText, contentText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(Type type, String titleText, String contentText) {
        this.titleText.setText(titleText);
        this.contentText.setText(contentText);
        imageContainer.setImage(new Image(icons.get(type)));
        closeIcon.setImage(new Image("/images/finance/exitButton.png"));
        closeButton.setOnAction(this::close);
        if (type.equals(Type.CONFIRMATION)) {
            createConfirmAlert();
        } else {
            okButton.setOnAction(this::close);
        }
    }

    private void createConfirmAlert() {
        buttonContainer.getChildren().clear();
        MFXButton yesButton = new MFXButton("Yes");
        MFXButton noButton = new MFXButton("No");
        yesButton.setButtonType(ButtonType.RAISED);
        noButton.setButtonType(ButtonType.RAISED);
        yesButton.setDepthLevel(DepthLevel.LEVEL2);
        noButton.setDepthLevel(DepthLevel.LEVEL2);
        yesButton.setPadding(new Insets(10, 30, 10, 30));
        noButton.setPadding(new Insets(10, 30, 10, 30));
        buttonContainer.getChildren().addAll(yesButton, noButton);

        yesButton.setOnAction(event -> {
            if (yesAction != null) {
                yesAction.run();
            }
            close(event);
        });

        noButton.setOnAction(this::close);
    }

    private void close(ActionEvent actionEvent) {
        if (stage != null) {
            this.getStage().close();
        }
    }

    public void show() {
        stage = new Stage();
        this.setStage(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(this));
        stage.showAndWait();
        new Draggable().makeDraggable(stage);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setYesAction(Runnable yesAction) {
        this.yesAction = yesAction;
    }

    public void setNoAction(Runnable noAction) {
        this.noAction = noAction;
    }
}
