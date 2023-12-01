package myApp.controllers.views;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import myApp.controllers.components.AddBudgetForm;
import myApp.controllers.components.BudgetBox;
import myApp.models.Budget;
import myApp.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BudgetController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private FlowPane flowPane;
    private final AddBudgetForm addBudgetForm = new AddBudgetForm();

    @FXML
    public void initialize() {
        loadBudgetDataAsync();
    }

    private void loadBudgetDataAsync() {
        Task<List<Budget>> task = new Task<>() {
            @Override
            protected List<Budget> call() {
                return fetchBudgetData();
            }
        };

        task.setOnSucceeded(e -> updateUI(task.getValue()));
        task.setOnFailed(e -> showError(task.getException()));

        new Thread(task).start();
    }

    private void updateUI(List<Budget> budgets) {
        flowPane.getChildren().clear();

        if (budgets.isEmpty()) {
            System.out.println("No budgets found to display.");
            flowPane.getChildren().add(new Label("No budgets to display."));
            return;
        }

        for (Budget budget : budgets) {
            double progressValue = budget.calculatePercentage();
            BudgetBox budgetBox = new BudgetBox(budget);

            flowPane.getChildren().add(budgetBox);
        }
    }

    private List<Budget> fetchBudgetData() {
        List<Budget> budgets = new ArrayList<>();
        String query = "SELECT b.category, b.budget_limit, b.start_date, b.end_date, IFNULL(SUM(t.amount), 0) as spent_amount " +
                "FROM budget b " +
                "LEFT JOIN transactions t ON b.category = t.category AND t.transaction_date BETWEEN b.start_date AND b.end_date " +
                "GROUP BY b.category, b.budget_limit, b.start_date, b.end_date";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                double allocatedAmount = rs.getDouble("budget_limit");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();
                double spentAmount = rs.getDouble("spent_amount");

                budgets.add(new Budget(category, allocatedAmount, spentAmount, startDate, endDate));
            }
            System.out.println("Fetched " + budgets.size() + " budgets successfully.");
        } catch (SQLException e) {
            showError(e);
        }

        return budgets;
    }


    private void showError(Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @FXML
    private void handleAddBudgetForm() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Budget Dialog");

        AddBudgetForm addBudgetForm = new AddBudgetForm();

        Scene dialogScene = new Scene(addBudgetForm, addBudgetForm.getPrefWidth(), addBudgetForm.getPrefHeight());
        dialogStage.setScene(dialogScene);

        dialogStage.initModality(Modality.APPLICATION_MODAL);

        dialogStage.showAndWait();
    }
}
