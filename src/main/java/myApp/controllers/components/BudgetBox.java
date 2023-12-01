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
        categoryLabel.setText("Category: " + budget.getCategory());
        budgetLabel.setText("Allocated: " + budget.getAllocatedAmount());
        spentLabel.setText("Spent: " + budget.getSpentAmount());
        String formattedEndDate = budget.getEndDate().toString();
        endDateLabel.setText("Ends at: " + formattedEndDate);

        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), budget.getEndDate());
        daysUntilExpiryLabel.setText("Days Until Expiry: " + daysUntilExpiry);

        percentageLabel.setText(String.format("%.1f%%", budget.calculatePercentage()));
        progressBar.getRanges1().add(NumberRange.of(0.0, 0.49));
        progressBar.getRanges2().add(NumberRange.of(0.50, 0.79));
        progressBar.getRanges3().add(NumberRange.of(0.80, 1.0));
        progressBar.setProgress(budget.calculatePercentage());
    }
}
