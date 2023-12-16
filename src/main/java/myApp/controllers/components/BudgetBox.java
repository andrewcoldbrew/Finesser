package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import myApp.controllers.views.BudgetController;
import myApp.models.Budget;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import myApp.utils.ConnectionManager;

public class BudgetBox extends AnchorPane {
    public Label categoryLabel;
    public Label budgetLabel;
    public Label spentLabel;
    public Label endDateLabel;
    public Label percentageLabel;
    public MFXProgressBar progressBar;
    public MFXButton updateButton;
    public MFXButton deleteButton;
    private Budget budget;

    private BudgetController budgetController;

    public BudgetBox(Budget budget, BudgetController budgetController) {
        this.budget = budget;
        this.budgetController = budgetController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/budgetBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize();
            updateButton.setOnAction(this::updateBudget);
            deleteButton.setOnAction(this::deleteBudget);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteBudget(ActionEvent actionEvent) {
        String sql = "DELETE FROM budget WHERE budgetID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budget.getId()); // Use ID here for the operation
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                this.setVisible(false);
                this.setManaged(false);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete budget: " + e.getMessage());
        }
    }

    private void updateBudget(ActionEvent actionEvent) {
        budgetController.openUpdateBudgetForm(budget);
    }

    private void initialize() {
        categoryLabel.setText(budget.getCategory());
        budgetLabel.setText(String.format("Budget: %.0f", budget.getAllocatedAmount()));
        spentLabel.setText(String.format("Spent: %.0f", budget.getSpentAmount()));
        String formattedEndDate = budget.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        endDateLabel.setText(String.format("Ends at: %s", formattedEndDate));
        percentageLabel.setText(String.format("%.1f%%", budget.calculatePercentage() * 100));
        progressBar.setProgress(budget.calculatePercentage());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
