package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BudgetBox extends AnchorPane {
    public Label categoryLabel;
    public Label budgetLabel;
    public Label spentLabel;
    public Label endDateLabel;
    public Label percentagelabel;
    public MFXProgressBar progressBar;
    public BudgetBox(String category, double budget, double spent, LocalDate endDate) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/budgetBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(category, budget, spent, endDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String category, double budget, double spent, LocalDate endDate) {
        categoryLabel.setText(category);
        budgetLabel.setText(String.format("Budget: %.0f", budget));
        spentLabel.setText(String.format("Spent: %.0f", spent));
        String formattedEndDate = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        endDateLabel.setText(String.format("Ends at: %s", formattedEndDate));
    }
}
