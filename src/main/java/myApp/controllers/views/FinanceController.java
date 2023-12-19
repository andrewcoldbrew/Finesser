package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import java.time.LocalDate;
import javafx.scene.Scene;
import java.time.DayOfWeek;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myApp.controllers.components.AddFinanceForm;
import myApp.controllers.components.FinanceBox;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class FinanceController implements Initializable {
    public MFXButton addButton;
    public FlowPane incomeFlowPane;
    public FlowPane outcomeFlowPane;
    public Label totalLabel;
    public MFXButton allTimeButton;
    public MFXButton weeklyButton;
    public MFXButton monthyButton;
    public MFXButton yearlyButton;


    private final AddFinanceForm addFinanceForm = new AddFinanceForm();
    private final Stage dialogStage = new Stage();
    private final Scene dialogScene = new Scene(addFinanceForm, addFinanceForm.getPrefWidth(), addFinanceForm.getPrefHeight());
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
        initializeAddFinanceForm();
        dialogStage.setScene(dialogScene);
        Draggable draggable = new Draggable();
        draggable.makeDraggable(dialogStage);

        allTimeButton.setOnAction(event -> filterFinances(TimeFrame.ALL_TIME));
        weeklyButton.setOnAction(event -> filterFinances(TimeFrame.WEEKLY));
        monthyButton.setOnAction(event -> filterFinances(TimeFrame.MONTHLY));
        yearlyButton.setOnAction(event -> filterFinances(TimeFrame.YEARLY));


        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        loadFinanceData(startOfMonth, endOfMonth);
    }


    private void filterFinances(TimeFrame timeFrame) {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        switch (timeFrame) {
            case ALL_TIME:
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
            case YEARLY:
                start = start.withDayOfYear(1);
                end = start.plusYears(1).withDayOfYear(1).minusDays(1);
                break;
        }


        incomeFlowPane.getChildren().clear();
        outcomeFlowPane.getChildren().clear();


        loadIncome(start, end);
        loadOutcome(start, end);
    }


    private void initializeAddFinanceForm() {

    }

    private void loadFinanceData(LocalDate startDate, LocalDate endDate) {
        incomeGrid.getChildren().clear();
        outcomeGrid.getChildren().clear();
        loadIncome(startDate, endDate);
        loadOutcome(startDate, endDate);
    }


    private void loadIncome(LocalDate startDate, LocalDate endDate) {
        System.out.println("Loading income...");
        String query = "SELECT name, amount, transactionDate, category FROM transaction WHERE category = 'Income' AND transactionDate BETWEEN ? AND ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                int row = 1;
                while (rs.next()) {
                    count++;
                    String name = rs.getString("name");
                    double amount = rs.getDouble("amount");
                    LocalDate transactionDate = rs.getDate("transactionDate").toLocalDate();
                    String category = rs.getString("category");

                    System.out.println("Income #" + count + ": " + name + ", Amount: " + amount + ", Date: " + transactionDate + ", Category: " + category);
                    FinanceBox financeBox = new FinanceBox(name, amount, category, transactionDate);
                    incomeGrid.add(financeBox, 0, row++);
                }
                if (count == 0) {
                    System.out.println("No income transactions found for the current month.");
                }
                totalIncome = 0.0;
                for (Node node : incomeGrid.getChildren()) {
                    if (node instanceof FinanceBox) {
                        FinanceBox box = (FinanceBox) node;
                        totalIncome += box.getAmount();
                    }
                }
                updateSurplus();
            }
        } catch (SQLException e) {
            System.err.println("SQLException in loadIncome: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadOutcome(LocalDate startDate, LocalDate endDate) {
        System.out.println("Loading outcome...");
        String query = "SELECT name, amount, transactionDate, category FROM transaction WHERE category IN ('subscription', 'rent') AND transactionDate BETWEEN ? AND ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                int row = 1;
                while (rs.next()) {
                    count++;
                    String name = rs.getString("name");
                    double amount = rs.getDouble("amount");
                    LocalDate transactionDate = rs.getDate("transactionDate").toLocalDate();
                    String category = rs.getString("category");

                    System.out.println("Outcome #" + count + ": " + name + ", Amount: " + amount + ", Date: " + transactionDate + ", Category: " + category);
                    FinanceBox financeBox = new FinanceBox(name, amount, category, transactionDate);
                    outcomeGrid.add(financeBox, 0, row++);
                }
                if (count == 0) {
                    System.out.println("No outcome transactions found for the current month.");
                }
                totalOutcome = 0.0;
                for (Node node : outcomeGrid.getChildren()) {
                    if (node instanceof FinanceBox) {
                        FinanceBox box = (FinanceBox) node;
                        totalOutcome += box.getAmount();
                    }
                }
                updateSurplus();
            }
        } catch (SQLException e) {
            System.err.println("SQLException in loadOutcome: " + e.getMessage());
            e.printStackTrace();
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

    public void handleAddFinanceForm(ActionEvent actionEvent) {

    }
}
