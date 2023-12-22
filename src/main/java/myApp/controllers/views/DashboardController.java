package myApp.controllers.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import myApp.Main;
import myApp.controllers.components.DBTransaction;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;
import myApp.utils.MainAppManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    public VBox transactionContainer;
    public Label totalBalanceBox;
    public Hyperlink seeMoreLink;

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private BarChart<String, Number> budgetVsSpendingChart;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTransactions();
        loadPieChartData();
        loadBudgetVsSpendingData();
        seeMoreLink.setOnAction(this::moveToTransaction);
    }

    private void loadTransactions() {
        Connection con = ConnectionManager.getConnection();
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT name, amount FROM transaction WHERE userID = ? ORDER BY transactionDate DESC LIMIT 10");
            stmt.setInt(1, Main.getUserId());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String name = rs.getString("name");
                double amount = rs.getDouble("amount");
                Platform.runLater(() -> {
                    DBTransaction dbTransaction = new DBTransaction(name, amount);
                    transactionContainer.getChildren().add(dbTransaction);
                });

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadPieChartData() {
        Map<String, Double> categoryTotals = new HashMap<>();
        double totalAmount = 0;

        String query = "SELECT category, SUM(amount) AS totalAmount FROM transaction WHERE userID = ? GROUP BY category";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Main.getUserId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String category = rs.getString("category");
                double amount = rs.getDouble("totalAmount");
                totalAmount += amount;
                categoryTotals.put(category, amount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final double finalTotalAmount = totalAmount;
        //lambda shit that I hate
        Platform.runLater(() -> {
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                double percentage = (entry.getValue() / finalTotalAmount) * 100;
                String label = String.format("%s: %.2f%%", entry.getKey(), percentage);
                categoryPieChart.getData().add(new PieChart.Data(label, entry.getValue()));
            }
        });
    }

        /*private void loadPieChartData() {

            Map<String, Double> categoryTotals = new HashMap<>();

            String query = "SELECT category, SUM(amount) AS totalAmount FROM transaction WHERE userID = ? GROUP BY category";
            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, Main.getUserId());

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String category = rs.getString("category");
                    double amount = rs.getDouble("totalAmount");
                    categoryTotals.put(category, amount);
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }


            Platform.runLater(() -> {
                for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                    categoryPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            });
        }*/


    private void loadBudgetVsSpendingData() {
        Map<String, Double> budgetData = new HashMap<>();
        Map<String, Double> actualSpendingData = new HashMap<>();

        String query = "SELECT b.category, b.budgetLimit AS allocatedAmount, IFNULL(SUM(t.amount), 0) AS spentAmount " +
                "FROM budget b " +
                "LEFT JOIN transaction t ON b.category = t.category AND t.transactionDate BETWEEN b.startDate AND b.endDate " +
                "WHERE b.userID = ? " +
                "GROUP BY b.category, b.budgetLimit";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, Main.getUserId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String category = rs.getString("category");
                double allocatedAmount = rs.getDouble("allocatedAmount");
                double spentAmount = rs.getDouble("spentAmount");

                budgetData.put(category, allocatedAmount);
                actualSpendingData.put(category, spentAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        populateBarChart(budgetData, actualSpendingData);
    }

    private void populateBarChart(Map<String, Double> budgetData, Map<String, Double> actualSpendingData) {
        XYChart.Series<String, Number> budgetSeries = new XYChart.Series<>();
        budgetSeries.setName("Budget");

        XYChart.Series<String, Number> spendingSeries = new XYChart.Series<>();
        spendingSeries.setName("Actual Spending");

        for (String category : budgetData.keySet()) {
            double budgetAmount = budgetData.get(category);
            double spendingAmount = actualSpendingData.getOrDefault(category, 0.0);

            budgetSeries.getData().add(new XYChart.Data<>(category, budgetAmount));
            spendingSeries.getData().add(new XYChart.Data<>(category, spendingAmount));
        }

        Platform.runLater(() -> {
            budgetVsSpendingChart.getData().clear();
            budgetVsSpendingChart.getData().addAll(budgetSeries, spendingSeries);

            applyCssStyleToDataNodes(budgetSeries, "budget-series");
            applyCssStyleToDataNodes(spendingSeries, "spending-series");
        });
    }

    private void applyCssStyleToDataNodes(XYChart.Series<String, Number> series, String styleClass) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.getStyleClass().add(styleClass);
                }
            });
        }
    }

    private void moveToTransaction(ActionEvent actionEvent) {
        MainAppManager.switchScene("transaction");
    }
}
