package myApp.controllers.components;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import javafx.animation.Animation;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

public class LoadingScreen extends BorderPane {

    @FXML
    private MFXProgressBar progressBar;

    private Runnable dataLoadingLogic;
    private StackPane stackPane;

    public LoadingScreen(StackPane stackPane) {
        this.stackPane = stackPane;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/loadingScreen.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            stackPane.getChildren().add(this);
            initialize();
            createAndPlayAnimation(stackPane);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() {
        progressBar.getRanges1().add(NumberRange.of(0.0, 0.30));
        progressBar.getRanges2().add(NumberRange.of(0.31, 0.60));
        progressBar.getRanges3().add(NumberRange.of(0.61, 1.0));
    }

    private void createAndPlayAnimation(StackPane stackPane) {
        Animation a1 = AnimationUtils.TimelineBuilder.build()
                .add(
                        AnimationUtils.KeyFrames.of(1000, progressBar.progressProperty(), 0.3, Interpolators.INTERPOLATOR_V1),
                        AnimationUtils.KeyFrames.of(1500, progressBar.progressProperty(), 0.6, Interpolators.INTERPOLATOR_V1),
                        AnimationUtils.KeyFrames.of(2000, progressBar.progressProperty(), 1.0, Interpolators.INTERPOLATOR_V1)
                )
                .getAnimation();

        a1.setOnFinished(end -> AnimationUtils.PauseBuilder.build()
                .setDuration(Duration.seconds(1))
                .setOnFinished(event -> stackPane.getChildren().remove(this))
                .getAnimation()
                .play()
        );
        a1.play();
    }

}
