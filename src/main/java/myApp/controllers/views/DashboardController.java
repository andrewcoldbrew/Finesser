package myApp.controllers.views;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.css.converter.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import myApp.Main;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import myApp.controllers.components.DBTransaction;
import myApp.controllers.components.LoadingScreen;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;
import myApp.utils.MainAppManager;
import javafx.scene.text.Font;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class DashboardController implements Initializable {

    public VBox transactionContainer;
    public Label totalBalanceLabel;
    public Hyperlink seeMoreLink;
    public NumberAxis yAxis;
    public CategoryAxis xAxis;
    public ImageView userProfileImage;
    public Label welcomeLabel;
    public StackPane stackPane;

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private BarChart<String, Number> budgetVsSpendingChart;

    @FXML
    private AreaChart<String, Number> incomeVsOutcomeChart;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new LoadingScreen(stackPane);
        seeMoreLink.setOnAction(this::moveToTransaction);
        Platform.runLater(() -> {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    loadIncomeVsOutcomeData();
                    loadPieChartData();
                    loadBudgetVsSpendingData();
                    return null;
                }

                @Override
                protected void done() {
                    loadTransactions();
                    loadUserInfo();
                }
            };

            worker.execute();
        });

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
    private void loadIncomeVsOutcomeData() {
        AreaChart.Series<String, Number> incomeSeries = new AreaChart.Series<>();
        incomeSeries.setName("Cumulative Income");

        AreaChart.Series<String, Number> expensesSeries = new AreaChart.Series<>();
        expensesSeries.setName("Cumulative Expenses");

        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter chartFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        TreeMap<LocalDate, Double> incomeMap = new TreeMap<>();
        TreeMap<LocalDate, Double> expensesMap = new TreeMap<>();

        int userId = Main.getUserId();

        String incomeQuery = "SELECT transactionDate, SUM(amount) AS total FROM transaction WHERE userID = ? AND category = 'Income' GROUP BY transactionDate ORDER BY transactionDate";
       
        String expensesQuery = "SELECT transactionDate, SUM(amount) AS total FROM transaction WHERE userID = ? AND category IN ('Rent', 'Subscription') GROUP BY transactionDate ORDER BY transactionDate";

        updateTransactionMap(incomeQuery, userId, incomeMap, dbFormatter);
        updateTransactionMap(expensesQuery, userId, expensesMap, dbFormatter);

        TreeSet<LocalDate> allDates = new TreeSet<>(incomeMap.keySet());
        allDates.addAll(expensesMap.keySet());

        System.out.println(incomeMap);
        System.out.println(expensesMap);
        System.out.println(allDates);
        double lastIncome = 0;
        double lastExpenses = 0;

        for (LocalDate date : allDates) {
            lastIncome = incomeMap.getOrDefault(date, lastIncome);
            lastExpenses = expensesMap.getOrDefault(date, lastExpenses);

            incomeSeries.getData().add(new AreaChart.Data<>(date.format(chartFormatter), lastIncome));
            expensesSeries.getData().add(new AreaChart.Data<>(date.format(chartFormatter), lastExpenses));
        }

        Platform.runLater(() -> {
            incomeVsOutcomeChart.getData().clear();
            incomeVsOutcomeChart.getData().addAll(incomeSeries, expensesSeries);
        });
    }

    private void updateTransactionMap(String query, int userId, TreeMap<LocalDate, Double> map, DateTimeFormatter dbFormatter) {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            double cumulativeTotal = 0;
            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("transactionDate"), dbFormatter);
                double amount = rs.getDouble("total");
                cumulativeTotal += amount;
                map.put(date, cumulativeTotal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadUserInfo() {
        int userId = Main.getUserId();
        Connection con = ConnectionManager.getConnection();
        try (PreparedStatement preparedStatement = con.prepareStatement("SELECT fname, lname, cashAmount + COALESCE((SELECT SUM(balance) FROM bank WHERE ownerID = ? AND linked = true), 0) AS totalBalance FROM user WHERE userID = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String fname = resultSet.getString("fname");
                    String lname = resultSet.getString("lname");
                    String fullName = fname + " " + lname;
                    double totalBalance = resultSet.getDouble("totalBalance");
                    welcomeLabel.setText("Welcome, " + fullName);
                    totalBalanceLabel.setText(String.format("Total Balance: %.2f", totalBalance));
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveToTransaction(ActionEvent actionEvent) {
        MainAppManager.switchScene("transaction");
    }
}
