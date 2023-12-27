package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myApp.Main;
import myApp.controllers.components.*;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;

import java.net.URL;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FinanceController implements Initializable {
    public MFXButton addButton;
    public Label totalLabel;
    public MFXButton allTimeButton;
    public MFXButton weeklyButton;
    public MFXButton monthyButton;
    public MFXButton yearlyButton;
    public StackPane stackPane;


    private AddFinanceForm addFinanceForm;
    private Stage dialogStage;
    private Scene dialogScene;
    public GridPane incomeGrid;
    public GridPane outcomeGrid;
    private Connection con = ConnectionManager.getConnection();

    private double totalIncome = 0.0;
    private double totalOutcome = 0.0;

    private enum TimeFrame {
        ALL_TIME, WEEKLY, MONTHLY, YEARLY
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new LoadingScreen(stackPane);

        allTimeButton.setOnAction(event -> filterFinances(TimeFrame.ALL_TIME));
        weeklyButton.setOnAction(event -> filterFinances(TimeFrame.WEEKLY));
        monthyButton.setOnAction(event -> filterFinances(TimeFrame.MONTHLY));
        yearlyButton.setOnAction(event -> filterFinances(TimeFrame.YEARLY));

        Platform.runLater(() -> {
//            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
//            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
//            loadFinanceData(startOfMonth, endOfMonth);
            filterFinances(TimeFrame.ALL_TIME);
            handleTransactionRecurrences();
        });

    }


    private void filterFinances(TimeFrame timeFrame) {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        switch (timeFrame) {
            case ALL_TIME, YEARLY:
                start = start.withDayOfYear(1);
                end = start.plusYears(1).withDayOfYear(1).minusDays(1);
                break;
            case WEEKLY:
                start = start.with(DayOfWeek.MONDAY);
                end = start.plusWeeks(1).with(DayOfWeek.SUNDAY);
                break;
            case MONTHLY:
                start = start.withDayOfMonth(1);
                end = start.plusMonths(1).withDayOfMonth(1).minusDays(1);
                break;
        }

        incomeGrid.getChildren().clear();
        outcomeGrid.getChildren().clear();

        loadIncome(start, end);
        loadOutcome(start, end);
    }

    public void loadFinanceData(LocalDate startDate, LocalDate endDate) {
        incomeGrid.getChildren().clear();
        outcomeGrid.getChildren().clear();
        loadIncome(startDate, endDate);
        loadOutcome(startDate, endDate);
    }

    private void loadFinances(LocalDate startDate, LocalDate endDate, String categoryFilter, GridPane targetGrid) {
        System.out.println("Loading transactions...");

        int userID = Main.getUserId();

        String query = "SELECT * FROM transaction WHERE userID = ? AND category IN (" + categoryFilter + ") AND transactionDate BETWEEN ? AND ? ORDER BY transactionDate ASC";
        Connection con = ConnectionManager.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userID);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));
            System.out.println(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                int row = 1;
                while (rs.next()) {
                    count++;
                    int financeID = rs.getInt("transactionID");
                    String name = rs.getString("name");
                    double amount = rs.getDouble("amount");
                    String description = rs.getString("description");
                    String category = rs.getString("category");
                    int bankID = rs.getInt("bankID");
                    LocalDate transactionDate = rs.getDate("transactionDate").toLocalDate();
                    String recurrencePeriod = rs.getString("recurrencePeriod");
                    String bankName = getBankNameByID(bankID);
                    Transaction transaction = new Transaction(financeID, name, amount, description, category, bankName, transactionDate, recurrencePeriod);
                    FinanceBox financeBox = new FinanceBox(transaction, this);
                    targetGrid.add(financeBox, 0, row++);
                }

                if (count == 0) {
                    System.out.println("No transactions found for the current criteria.");
                }

                double totalAmount = 0.0;
                for (Node node : targetGrid.getChildren()) {
                    if (node instanceof FinanceBox) {
                        FinanceBox box = (FinanceBox) node;
                        totalAmount += box.getAmount();
                    }
                }

                if (categoryFilter.equals("Income")) {
                    totalIncome = totalAmount;
                } else {
                    totalOutcome = totalAmount;
                }

                updateSurplus();
            }
        } catch (SQLException e) {
            System.err.println("SQLException in loadTransactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadIncome(LocalDate startDate, LocalDate endDate) {
        loadFinances(startDate, endDate, "'Income', 'Dividend Income', 'Investment'", incomeGrid);
    }

    private void loadOutcome(LocalDate startDate, LocalDate endDate) {
        loadFinances(startDate, endDate, "'Rent', 'Bills', 'Insurance', 'Subscription'", outcomeGrid);
    }

    public void updateFinanceInDatabase(String name, double amount, String description, String category, String bankName, LocalDate transactionDate, String recurrencePeriod, int transactionID) {
        int bankID = getBankIdByName(bankName);
        String sql = "UPDATE transaction SET name = ?, amount = ?, description = ?, category = ?, bankID = ?, transactionDate = ?, recurrencePeriod = ? WHERE transactionID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setDouble(2, amount);
            stmt.setString(3, description);
            stmt.setString(4, category);
            stmt.setInt(5, bankID);
            stmt.setDate(6, Date.valueOf(transactionDate));
            stmt.setString(7, recurrencePeriod);
            stmt.setInt(8, transactionID);
            stmt.executeUpdate();

            closeUpdateFinanceForm();

            Platform.runLater(() -> {
                filterFinances(TimeFrame.ALL_TIME);
                new SuccessAlert(stackPane, "Finance successfully updated!");
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFinanceFromDatabase(Transaction transaction) {
        String sql = "DELETE FROM transaction WHERE transactionID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getTransactionID());
            pstmt.executeUpdate();
            filterFinances(TimeFrame.ALL_TIME);
            new SuccessAlert(stackPane, "Finance successfully deleted!");
            System.out.println("Deleted finance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void openUpdateFinanceForm(Transaction transaction) {
        if (!isUpdateFormOpen()) {
            stackPane.getChildren().add(new UpdateFinanceForm(this, transaction));
        }
    }

    private void closeUpdateFinanceForm() {
        for (Node node : stackPane.getChildren()) {
            if (node instanceof UpdateFinanceForm) {
                stackPane.getChildren().remove(node);
                break;
            }
        }
    }

    private boolean isUpdateFormOpen() {
        // Check if a LinkBankForm is already present in mainPane
        for (Node node : stackPane.getChildren()) {
            if (node instanceof UpdateFinanceForm) {
                return true;
            }
        }
        return false;
    }

    private int getBankIdByName(String bankName) {
        String query = "SELECT bankID FROM bank WHERE bankName = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, bankName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bankID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String getBankNameByID(int bankID) throws SQLException {
        Connection con = ConnectionManager.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT bankName FROM bank WHERE bankID = ? AND ownerID = ?")) {
            stmt.setInt(1, bankID);
            stmt.setInt(2, Main.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("bankName");
                } else {
                    throw new SQLException("Bank not found");
                }
            }
        }
    }

    private void updateSurplus() {
        double surplus = totalIncome - totalOutcome;
        totalLabel.setText(String.format("Surplus Funds: $%.2f", surplus));
        System.out.println("Total surplus"+surplus);
    }

    private LocalDate[] getAllTimeDateRange() {
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        LocalDate endOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        return new LocalDate[] { startOfYear, endOfYear };
    }

    private LocalDate[] getWeeklyDateRange() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        return new LocalDate[] { startOfWeek, endOfWeek };
    }

    private LocalDate[] getMonthlyDateRange() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        return new LocalDate[] { startOfMonth, endOfMonth };
    }

    private LocalDate[] getYearlyDateRange() {
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.withDayOfYear(1);
        LocalDate endOfYear = today.withDayOfYear(today.lengthOfYear());
        return new LocalDate[] { startOfYear, endOfYear };
    }

    @FXML
    private void handleAllTimeFilter(ActionEvent event) {
        LocalDate[] range = getAllTimeDateRange();
        loadFinanceData(range[0], range[1]);
    }

    @FXML
    private void handleWeeklyFilter(ActionEvent event) {
        LocalDate[] range = getWeeklyDateRange();
        loadFinanceData(range[0], range[1]);
    }

    @FXML
    private void handleMonthlyFilter(ActionEvent event) {
        LocalDate[] range = getMonthlyDateRange();
        loadFinanceData(range[0], range[1]);
    }

    @FXML
    private void handleYearlyFilter(ActionEvent event) {
        LocalDate[] range = getYearlyDateRange();
        loadFinanceData(range[0], range[1]);
    }
    private void handleTransactionRecurrences() {
        LocalDate today = LocalDate.now();
        String query = "SELECT * FROM transaction WHERE recurrencePeriod IS NOT NULL AND userID = ?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, Main.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = extractTransactionFromResultSet(con, rs);
                    if (shouldCreateNewTransaction(transaction, today)) {
                        createNewTransactionBasedOnRecurrence(transaction, today);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Transaction extractTransactionFromResultSet(Connection con, ResultSet rs) throws SQLException {
        int transactionID = rs.getInt("transactionID");
        String name = rs.getString("name");
        double amount = rs.getDouble("amount");
        String description = rs.getString("description");
        String category = rs.getString("category");
        LocalDate date = rs.getDate("transactionDate").toLocalDate();
        String recurrencePeriod = rs.getString("recurrencePeriod");
        int bankID = rs.getInt("bankID");

        String bankName = null;

        try (PreparedStatement bankStmt = con.prepareStatement("SELECT bankName FROM bank WHERE bankID = ?")) {
            bankStmt.setInt(1, bankID);

            try (ResultSet bankRs = bankStmt.executeQuery()) {
                if (bankRs.next()) {
                    bankName = bankRs.getString("bankName");
                }
            }
        }
        return new Transaction(transactionID, name, amount, description, category, bankName, date, recurrencePeriod);
    }
    private boolean shouldCreateNewTransaction(Transaction transaction, LocalDate today) {
        LocalDate lastDate = transaction.getDate();
        String recurrencePeriod = transaction.getRecurrencePeriod();

        switch (recurrencePeriod) {
            case "monthly":
                return lastDate.plusMonths(1).isBefore(today) || lastDate.plusMonths(1).isEqual(today);
            case "weekly":
                LocalDate nextWeeklyDate = lastDate.plusWeeks(1);
                LocalDate nextWeeklyOrEqualDate = lastDate.plusWeeks(1).minusDays(1);
                return (nextWeeklyDate.isBefore(today) || nextWeeklyDate.isEqual(today))
                        && today.isBefore(nextWeeklyOrEqualDate);
            default:
                return false;
        }
    }


    private void createNewTransactionBasedOnRecurrence(Transaction transaction, LocalDate today) {
        LocalDate newDate = LocalDate.now();
        String insertQuery = "INSERT INTO transaction (name, amount, description, category, transactionDate, recurrencePeriod, userID, bankID) " +
                "SELECT ?, ?, ?, ?, ?, ?, ?, (SELECT bankID FROM bank WHERE bankName = ?) AS bankID";


        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, transaction.getName());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getDescription());
            stmt.setString(4, transaction.getCategory());
            stmt.setDate(5, java.sql.Date.valueOf(newDate));
            stmt.setString(6, transaction.getRecurrencePeriod());
            stmt.setInt(7, Main.getUserId());
            stmt.setString(8, transaction.getBankName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception
        }
    }

    private void initializeAddFinanceForm() {
        dialogStage = new Stage();
        addFinanceForm = new AddFinanceForm();

        dialogScene = new Scene(addFinanceForm, addFinanceForm.getPrefWidth(), addFinanceForm.getPrefHeight());
        dialogScene.setFill(Color.TRANSPARENT);

        addFinanceForm.setStage(dialogStage);

        dialogStage.setTitle("Add Budget Dialog");
        dialogStage.setScene(dialogScene);

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogScene.setFill(Color.TRANSPARENT);

        dialogStage.setResizable(false);
        dialogStage.show();
        new Draggable().makeDraggable(dialogStage);
    }

    public void handleAddFinanceForm(ActionEvent actionEvent) {
        initializeAddFinanceForm();
    }
}
