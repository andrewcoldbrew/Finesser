package myApp.controllers.components;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
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
    public Label percentageLabel;
    public MFXProgressBar progressBar;
    public MFXButton updateButton;
    public MFXButton deleteButton;
    public BudgetBox(String category, double budget, double spent, LocalDate endDate, double percentage, double progressValue) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/budgetBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(category, budget, spent, endDate, percentage, progressValue);
            updateButton.setOnAction(this::updateBudget);
            deleteButton.setOnAction(this::deleteBudget);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteBudget(ActionEvent actionEvent) {
    }

    private void updateBudget(ActionEvent actionEvent) {
        
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
}
