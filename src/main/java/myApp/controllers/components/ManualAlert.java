package myApp.controllers.components;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ManualAlert extends Alert {

    private static final Map<ICON, String> scenes = Map.of(
            ICON.ERROR, "/images/error.gif"
    );

    private enum ICON {
        ERROR,
        SUCCESS,
        WARNING
    }


    public ManualAlert(AlertType alertType, String title, String header, String content, ICON icon) {
        super(alertType);
        setupAlert(title, header, content, icon);
        this.showAndWait();
    }

    private void setupAlert(String title, String header, String content, ICON icon) {
        if (title != null && !title.isEmpty()) {
            this.setTitle(title);
        }
        this.getDialogPane().getButtonTypes().clear();
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);
        this.setHeaderText(header);
        this.setContentText(content);
        addImageToDialogPane(icon);
    }

    private void addImageToDialogPane(ICON icon) {
        ImageView img = new ImageView(scenes.get(icon));
        img.setFitWidth(50);
        img.setFitHeight(50);
        getDialogPane().setGraphic(img);
    }
}
