package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myApp.Main;
import myApp.controllers.components.AddBudgetForm;
import myApp.controllers.components.BudgetBox;
import myApp.models.Budget;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BudgetController implements Initializable {
    public MFXButton addBudgetButton;
    public MFXScrollPane scrollPane;
    public AnchorPane mainPane;
    @FXML
    private FlowPane flowPane;
    private final AddBudgetForm addBudgetForm = new AddBudgetForm();
    private final Stage dialogStage = new Stage();
    private final Scene dialogScene = new Scene(addBudgetForm, addBudgetForm.getPrefWidth(), addBudgetForm.getPrefHeight());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBudgetDataAsync();
        initializeAddBudgetForm();
        flowPane.setPadding(new Insets(30, 0, 30, 30));
        Draggable draggable = new Draggable();
        draggable.makeDraggable(dialogStage);
    }

    private void loadBudgetDataAsync() {

        int currentUserId = Main.getUserId();

        Task<List<Budget>> task = new Task<>() {
            @Override
            protected List<Budget> call() {
                return fetchBudgetData(currentUserId);
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
            BudgetBox budgetBox = new BudgetBox(budget, this);
            flowPane.getChildren().add(budgetBox);
        }

    }

    private List<Budget> fetchBudgetData(int userId) {
        List<Budget> budgets = new ArrayList<>();
        String query = "SELECT b.budgetID, b.category, b.budgetLimit, b.startDate, b.endDate, IFNULL(SUM(t.amount), 0) as spentAmount " +
                "FROM budget b " +
                "LEFT JOIN transaction t ON b.category = t.category AND t.transactionDate BETWEEN b.startDate AND b.endDate " +
                "WHERE b.userID = ? " +
                "GROUP BY b.budgetID, b.category, b.budgetLimit, b.startDate, b.endDate";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int budgetId = rs.getInt("budgetID");
                    String category = rs.getString("category");
                    double allocatedAmount = rs.getDouble("budgetLimit");
                    LocalDate startDate = rs.getDate("startDate").toLocalDate();
                    LocalDate endDate = rs.getDate("endDate").toLocalDate();
                    double spentAmount = rs.getDouble("spentAmount");

                    budgets.add(new Budget(budgetId, category, allocatedAmount, spentAmount, startDate, endDate));
                }
                System.out.println("Fetched " + budgets.size() + " budgets successfully.");
            }
        } catch (SQLException e) {
            showError(e);
        }
        return budgets;
    }

    public void openUpdateBudgetForm(Budget budget) {

        TextInputDialog dialog = new TextInputDialog(String.valueOf(budget.getAllocatedAmount()));
        dialog.setTitle("Update Budget");
        dialog.setHeaderText("Update Budget for " + budget.getCategory());
        dialog.setContentText("Enter new budget limit:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newLimit -> {
            try {
                updateBudgetInDatabase(budget.getId(), Double.parseDouble(newLimit));
                // Reload or refresh budget data to reflect changes
                loadBudgetDataAsync();
            } catch (NumberFormatException e) {
                // Handle invalid number format
            }
        });
    }

    private void updateBudgetInDatabase(int id, double v) {
        String sql = "UPDATE budget SET budgetLimit = ? WHERE budgetID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, v);
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Budget updated successfully.");
            }
        } catch (SQLException e) {
            showError(e);
        }

    }


    private void showError(Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void initializeAddBudgetForm() {
        dialogStage.setTitle("Add Budget Dialog");

        addBudgetForm.setStage(dialogStage);
        System.out.println(addBudgetForm.getStage());

        dialogStage.setScene(dialogScene);

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogScene.setFill(Color.TRANSPARENT);

        dialogStage.setResizable(false);
    }
    @FXML
    private void handleAddBudgetForm() {
        dialogStage.hide();
        dialogStage.show();
    }

}
