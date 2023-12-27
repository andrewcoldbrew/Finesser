package myApp.controllers.views;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import myApp.Main;

import java.util.*;

import myApp.controllers.components.DBTransaction;
import myApp.controllers.components.LoadingScreen;
import myApp.utils.ConnectionManager;
import myApp.utils.MainAppManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class NewDashboardController implements Initializable {

    public VBox transactionContainer;
    public Label totalBalanceLabel;
    public Hyperlink seeMoreLink;
    public ImageView userProfileImage;
    public Label welcomeLabel;
    public StackPane stackPane;

    @FXML
    private PieChart categoryPieChart;
    @FXML
    private BarChart<String, Number> budgetVsSpendingChart;

    @FXML
    private AreaChart<String, Number> incomeVsOutcomeChart;
    private Tooltip pieChartToolTip;
    private Tooltip barChartToolTip;
    private Tooltip areaChartToolTip;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new LoadingScreen(stackPane);
        initializeToolTips();
        seeMoreLink.setOnAction(this::moveToTransaction);
        loadIncomeVsOutcomeData();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Perform heavy tasks in the background
                loadPieChartData();
                loadBudgetVsSpendingData();
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            addHoverToAllCharts();
            loadTransactions();
            loadUserInfo();
        });

        // Start the task on the JavaFX Application Thread
        Platform.runLater(() -> {
            new Thread(task).start();
        });
    }


    private void initializeToolTips() {
        pieChartToolTip = new Tooltip("");
        pieChartToolTip.setShowDelay(Duration.millis(0));
        pieChartToolTip.setHideDelay(Duration.millis(0));
        barChartToolTip = new Tooltip("");
        barChartToolTip.setShowDelay(Duration.millis(0));
        barChartToolTip.setHideDelay(Duration.millis(0));
        areaChartToolTip = new Tooltip("");
        areaChartToolTip.setShowDelay(Duration.millis(0));
        areaChartToolTip.setHideDelay(Duration.millis(0));
    }

    private void addHoverToAllCharts() {
            for (final PieChart.Data data : categoryPieChart.getData()) {
                Tooltip.install(data.getNode(), pieChartToolTip);
                addHoverToPieChart(data);
            }
            for (final XYChart.Series<String, Number> series : budgetVsSpendingChart.getData()) {
                for (final XYChart.Data<String, Number> data : series.getData()) {
                    Tooltip.install(data.getNode(), barChartToolTip);
                    addHoverToBarChart(data, series);
                }
            }

            for (final XYChart.Series<String, Number> series : incomeVsOutcomeChart.getData()) {
                for (final XYChart.Data<String, Number> data : series.getData()) {
                    Tooltip.install(data.getNode(), areaChartToolTip);
                    addHoverToAreaChart(data, series);
                }
            }

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
//            for (final PieChart.Data data : categoryPieChart.getData()) {
//                Tooltip.install(data.getNode(), pieChartToolTip);
//                addHoverToPieChart(data);
//            }
        });

    }

    private void addHoverToPieChart(final PieChart.Data data) {

        final Node node = data.getNode();

        node.setOnMouseEntered(arg0 -> {
            node.setEffect(new Glow());
            String styleString = "-fx-border-color: white; -fx-border-width: 2; -fx-border-style: dashed;";
            node.setStyle(styleString);
            pieChartToolTip.setText(data.getName() + "\n" + "Spending: " + data.getPieValue());
        });

        node.setOnMouseExited(arg0 -> {
            node.setEffect(null);
            node.setStyle("");
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
//        Platform.runLater(() -> {
//            for (final XYChart.Series<String, Number> series : budgetVsSpendingChart.getData()) {
//                for (final XYChart.Data<String, Number> data : series.getData()) {
//                    Tooltip.install(data.getNode(), barChartToolTip);
//                    addHoverToBarChart(data, series);
//                }
//            }
//        });

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

    private void addHoverToBarChart(final XYChart.Data data, XYChart.Series<String, Number> series) {
        final Node node = data.getNode();

        node.setOnMouseEntered(arg0 -> {
            node.setEffect(new Glow());
            String styleString = "-fx-border-color: white; -fx-border-width: 2; -fx-border-style: dashed;";
            node.setStyle(styleString);
            barChartToolTip.setText(series.getName() + "\n"
                    + "Category: " + data.getXValue().toString() + "\n"
                    + "Amount: $" + data.getYValue().toString());
        });

        node.setOnMouseExited(arg0 -> {
            node.setEffect(null);
            node.setStyle("");
        });
    }

    private void loadIncomeVsOutcomeData() {
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Cumulative Income");

        XYChart.Series<String, Number> expensesSeries = new XYChart.Series<>();
        expensesSeries.setName("Cumulative Expenses");

        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter chartFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Map<LocalDate, Double> incomeMap = new HashMap<>();
        Map<LocalDate, Double> expensesMap = new HashMap<>();

        int userId = Main.getUserId();

        String incomeQuery = "SELECT transactionDate, SUM(amount) AS total FROM transaction WHERE userID = ? AND category IN ('Income', 'Dividend Income', 'Investment') GROUP BY transactionDate ORDER BY transactionDate";

        String expensesQuery = "SELECT transactionDate, SUM(amount) AS total FROM transaction WHERE userID = ? AND category IN ('Rent', 'Subscription', 'Insurance', 'Bills') GROUP BY transactionDate ORDER BY transactionDate";

        updateTransactionMap(incomeQuery, userId, incomeMap, dbFormatter);
        updateTransactionMap(expensesQuery, userId, expensesMap, dbFormatter);

        Set<LocalDate> allDates = new TreeSet<>(incomeMap.keySet());
        allDates.addAll(expensesMap.keySet());

        double lastIncome = 0;
        double lastExpenses = 0;

        for (LocalDate date : allDates) {
            lastIncome = incomeMap.getOrDefault(date, lastIncome);
            lastExpenses = expensesMap.getOrDefault(date, lastExpenses);

            incomeSeries.getData().add(new XYChart.Data<>(date.format(chartFormatter), lastIncome));
            expensesSeries.getData().add(new XYChart.Data<>(date.format(chartFormatter), lastExpenses));
        }
        incomeVsOutcomeChart.getData().clear();
        incomeVsOutcomeChart.getData().addAll(incomeSeries, expensesSeries);

//        Platform.runLater(() -> {
//            for (final XYChart.Series<String, Number> series : incomeVsOutcomeChart.getData()) {
//                for (final XYChart.Data<String, Number> data : series.getData()) {
//                    Tooltip.install(data.getNode(), areaChartToolTip);
//                    addHoverToAreaChart(data, series);
//                }
//            }
//        });
    }

    private void updateTransactionMap(String query, int userId, Map<LocalDate, Double> map, DateTimeFormatter dbFormatter) {
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

    private void addHoverToAreaChart(final XYChart.Data data, XYChart.Series<String, Number> series) {
        final Node node = data.getNode();

        node.setOnMouseEntered(arg0 -> {
            node.setEffect(new Glow());
            String styleString = "-fx-background-color: red;";
            node.setStyle(styleString);
            areaChartToolTip.setText(series.getName() + "\n"
                    + "Date: " + data.getXValue().toString() + "\n"
                    + "Amount: " + data.getYValue().toString());
        });

        node.setOnMouseExited(arg0 -> {
            node.setEffect(null);
            node.setStyle("");
        });

    }


    private void loadUserInfo() {
        Platform.runLater(() -> {
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
        });

    }

    private void moveToTransaction(ActionEvent actionEvent) {
        MainAppManager.switchScene("transaction");
    }
}
