package myApp.controllers.components;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import javafx.animation.Animation;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BudgetBox extends AnchorPane {
    public Label categoryLabel;
    public Label budgetLabel;
    public Label spentLabel;
    public Label endDateLabel;
    public Label daysUntilExpiryLabel;
    public Label percentageLabel;
    public MFXProgressBar progressBar;

    public BudgetBox(String category, double budget, double spent, LocalDate endDate, double percentage, double progressValue) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/budgetBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(category, budget, spent, endDate, percentage, progressValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String category, double budget, double spent, LocalDate endDate, double percentage, double progressValue) {
        categoryLabel.setText(category);
        budgetLabel.setText(String.format("Budget: %.0f", budget));
        spentLabel.setText(String.format("Spent: %.0f", spent));
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        endDateLabel.setText(String.format("Ends at: %s", formattedEndDate));
        percentageLabel.setText(String.format("%.1f%%", percentage));
        progressBar.getRanges1().add(NumberRange.of(0.0, 0.49));
        progressBar.getRanges2().add(NumberRange.of(0.50, 0.79));
        progressBar.getRanges3().add(NumberRange.of(0.80, 1.0));
        progressBar.setProgress(progressValue);
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
