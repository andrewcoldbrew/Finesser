package myApp.controllers.components;

import animatefx.animation.FadeIn;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.enums.ButtonType;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


import javax.swing.*;
import java.io.IOException;

public class ReportLoading extends BorderPane {
    public Text message;
    public ImageView imageView;
    public VBox contentContainer;
    public MFXProgressBar progressBar;
    private StackPane mainPane;

    public ReportLoading(StackPane mainPane) {
        this.mainPane = mainPane;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/reportLoading.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            initialize();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() {
        progressBar.getRanges1().add(NumberRange.of(0.0, 0.30));
        progressBar.getRanges2().add(NumberRange.of(0.31, 0.60));
        progressBar.getRanges3().add(NumberRange.of(0.61, 1.0));
        mainPane.getChildren().add(this);
//        createAndPlayAnimation(progressBar);
        new FadeIn(this).play();
    }

    public void changeState() {
        message.setText("Your report is completed!");
        contentContainer.getChildren().remove(progressBar);
        contentContainer.getChildren().add(generateButton());
    }

    private MFXButton generateButton() {
        MFXButton button = new MFXButton("OK");
        button.setButtonType(ButtonType.RAISED);
        button.setDepthLevel(DepthLevel.LEVEL2);
        button.setStyle("-fx-padding: 10 20 10 20; -fx-font-size: 18");
        button.setOnAction(this::closeLoading);
        return button;
    }

    private void closeLoading(ActionEvent actionEvent) {
        mainPane.getChildren().remove(this);
    }

}