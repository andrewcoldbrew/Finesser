package myApp.controllers.components;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class SuccessAlert extends BorderPane {
    public HBox animationContainer;
    public Label messageLabel;
    public SuccessAlert(String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/successAlert.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String message) {
        MFXProgressSpinner progressSpinner = new MFXProgressSpinner();
        progressSpinner.getRanges1().add(NumberRange.of(0.0, 0.30));
        progressSpinner.getRanges2().add(NumberRange.of(0.31, 0.60));
        progressSpinner.getRanges3().add(NumberRange.of(0.61, 1.0));
        animationContainer.getChildren().add(progressSpinner);
        createAndPlayAnimation(progressSpinner);

        messageLabel.setText(message);
    }

    public void showAlert() {
        // Create a new undecorated stage
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);

        // Set the SuccessAlert as the scene of the stage
        stage.setScene(this.getScene());

        // Show the stage
        stage.show();

        // Set the initial visibility to true
        setVisibility(true);

        // Create a timeline to hide the alert and close the stage after 1 second
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> {
            setVisibility(false);
            stage.close();
        });

        pause.play();

    }

    private void setVisibility(boolean visible) {
        for (Node child : getChildren()) {
            child.setVisible(visible);
        }
    }

    private void createAndPlayAnimation(ProgressIndicator indicator) {
        Animation a1 = AnimationUtils.TimelineBuilder.build()
                .add(
                        AnimationUtils.KeyFrames.of(2000, indicator.progressProperty(), 0.3, Interpolators.INTERPOLATOR_V1),
                        AnimationUtils.KeyFrames.of(4000, indicator.progressProperty(), 0.6, Interpolators.INTERPOLATOR_V1),
                        AnimationUtils.KeyFrames.of(6000, indicator.progressProperty(), 1.0, Interpolators.INTERPOLATOR_V1)
                )
                .getAnimation();

        Animation a2 = AnimationUtils.TimelineBuilder.build()
                .add(
                        AnimationUtils.KeyFrames.of(1000, indicator.progressProperty(), 0, Interpolators.INTERPOLATOR_V2)
                )
                .getAnimation();

        a1.setOnFinished(end -> AnimationUtils.PauseBuilder.build()
                .setDuration(Duration.seconds(1))
                .setOnFinished(event -> a2.playFromStart())
                .getAnimation()
                .play()
        );
        a2.setOnFinished(end -> AnimationUtils.PauseBuilder.build()
                .setDuration(Duration.seconds(1))
                .setOnFinished(event -> a1.playFromStart())
                .getAnimation()
                .play()
        );

        a1.play();
    }
}
