package myApp.controllers.components;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import javafx.animation.Animation;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import myApp.models.Budget;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BudgetBox extends AnchorPane {
    public Label categoryLabel;
    public Label budgetLabel;
    public Label spentLabel;
    public Label endDateLabel;
    public Label daysUntilExpiryLabel;
    public Label percentageLabel;
    public MFXProgressBar progressBar;

    public BudgetBox(Budget budget) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/budgetBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(budget);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(Budget budget) {
        categoryLabel.setText(budget.getCategory());
        budgetLabel.setText(String.format("Budget: %.0f", budget.getAllocatedAmount()));
        spentLabel.setText(String.format("Spent: %.0f", budget.getSpentAmount()));
        String formattedEndDate = budget.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        endDateLabel.setText(String.format("Ends at: %s", formattedEndDate));
        percentageLabel.setText(String.format("%.1f%%", budget.calculatePercentage() * 100));
        progressBar.getRanges1().add(NumberRange.of(0.0, 0.49));
        progressBar.getRanges2().add(NumberRange.of(0.50, 0.79));
        progressBar.getRanges3().add(NumberRange.of(0.80, 1.0));
        progressBar.setProgress(budget.calculatePercentage());

        // Calculate days until expiry
        LocalDate currentDate = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(currentDate, budget.getEndDate());
        daysUntilExpiryLabel.setText(String.format("Days until expiry: %d", daysUntilExpiry));
    }
}

