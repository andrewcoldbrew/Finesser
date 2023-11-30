package myApp.controllers.views;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import myApp.controllers.components.AddBudgetForm;
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
    private AnchorPane mainPane1;
    @FXML
    private FlowPane flowPane1;
    private final AddBudgetForm addBudgetForm = new AddBudgetForm();

    @FXML
    public void initialize() {
        loadBudgetData();
    }

    public void loadBudgetData() {
        System.out.println("loadBudgetData() called");

        // Fetch the budget data from the database
        List<Budget> budgets = fetchBudgetData();
        System.out.println("Number of budgets fetched: " + budgets.size());

        // Clear any existing content in the FlowPane
        flowPane1.getChildren().clear();

        // Check if budgets list is empty
        if (budgets.isEmpty()) {
            System.out.println("No budgets found to display.");
            flowPane1.getChildren().add(new Label("No budgets to display."));
            return;
        }
        // Iterate over the fetched budgets and create UI elements for each
        for (Budget budget : budgets) {
            System.out.println("Processing budget: " + budget.getCategory());

            // Create a VBox for each budget entry to hold the details
            VBox budgetBox = new VBox();
            budgetBox.setSpacing(10); // Set spacing for better readability

            // Add Labels or other components as needed to the VBox
            budgetBox.getChildren().addAll(
                    new Label("Category: " + budget.getCategory()),
                    new Label("Allocated: " + budget.getAllocatedAmount()),
                    new Label("Spent: " + budget.getSpentAmount()),
                    new Label("Remaining: " + (budget.getAllocatedAmount() - budget.getSpentAmount())),
                    // Format dates as needed
                    new Label("Start Date: " + budget.getStartDate().toString()),
                    new Label("End Date: " + budget.getEndDate().toString())
            );

            // Add the VBox to the FlowPane
            flowPane1.getChildren().add(budgetBox);
        }

        System.out.println("Finished loading budget data");
    }

    private List<Budget> fetchBudgetData() {
        List<Budget> budgets = new ArrayList<>();
        String query = "SELECT category, budget_limit, start_date, end_date FROM budget";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                double allocatedAmount = rs.getDouble("budget_limit");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();

                budgets.add(new Budget(category, allocatedAmount, 0.0, startDate, endDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Calculating the spent amount for each budget
        for (Budget budget : budgets) {
            double spentAmount = calculateSpentAmount(budget.getCategory(), budget.getStartDate(), budget.getEndDate());
            budget.setSpentAmount(spentAmount);
        }

        return budgets;
    }

    private double calculateSpentAmount(String category, LocalDate startDate, LocalDate endDate) {

        double spentAmount = 0.0;
        String query = "SELECT SUM(amount) FROM transactions WHERE category = ? AND transaction_date BETWEEN ? AND ?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, category);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    spentAmount = rs.getDouble(1); // Assuming the sum is in the first column
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return spentAmount;
    }

    @FXML
    private void handleAddBudgetForm() {
        if (!mainPane1.getChildren().contains(addBudgetForm)) {
            AnchorPane.setTopAnchor(addBudgetForm, (mainPane1.getHeight() - addBudgetForm.getPrefHeight()) / 2);
            AnchorPane.setLeftAnchor(addBudgetForm, (mainPane1.getWidth() - addBudgetForm.getPrefWidth()) / 2);
            mainPane1.getChildren().add(addBudgetForm);
        }
    }
}
