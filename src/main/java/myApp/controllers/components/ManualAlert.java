package myApp.controllers.components;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;

import java.util.Map;

public class ManualAlert extends Alert {

    private static final Map<ICON, String> scenes = Map.of(
            ICON.ERROR, "/images/gif/error.gif",
            ICON.SUCCESS, "/images/gif/check.gif",
            ICON.WARNING, "/images/gif/warning.gif",
            ICON.QUESTION, "/images/gif/question.gif"
    );

    private enum ICON {
        ERROR,
        SUCCESS,
        WARNING,
        QUESTION
    }


    public ManualAlert(AlertType alertType, String title, String header, String content) {
        super(alertType);
        setupAlert(alertType, title, header, content);
    }

    private void setupAlert(AlertType alertType, String title, String header, String content) {
        if (title != null && !title.isEmpty()) {
            this.setTitle(title);
        }
        this.getDialogPane().getButtonTypes().clear();
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);
        this.setHeaderText(header);
        this.setContentText(content);
        switch (alertType) {
            case ERROR -> setUpErrorAlert();
            case WARNING -> setUpWarningAlert();
            case INFORMATION -> setUpSuccessAlert();
            case CONFIRMATION -> setUpConfirmationAlert();
            default -> System.out.println("PLEASE SPECIFY AN ALERT TYPE");
        }
    }

    private void setUpErrorAlert() {
        addImageToDialogPane(ICON.ERROR);
        this.showAndWait();
    }

    private void setUpWarningAlert() {
        addImageToDialogPane(ICON.WARNING);
        this.showAndWait();
    }

    private void setUpSuccessAlert() {
        addImageToDialogPane(ICON.SUCCESS);
        this.showAndWait();
    }

    private void setUpConfirmationAlert() {
        addImageToDialogPane(ICON.QUESTION);
        this.getDialogPane().getButtonTypes().clear();
        this.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
    }

    private void addImageToDialogPane(ICON icon) {
        ImageView img = new ImageView(scenes.get(icon));
        img.setFitWidth(50);
        img.setFitHeight(50);
        getDialogPane().setGraphic(img);
    }
}
