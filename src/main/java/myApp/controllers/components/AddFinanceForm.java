package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import myApp.Main;
import myApp.controllers.views.FinanceController;
import myApp.utils.ConnectionManager;
import myApp.utils.NotificationCenter;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class AddFinanceForm extends StackPane {
    public StackPane stackPane;
    public MFXTextField nameField;
    public MFXTextField amountField;
    public MFXTextField descriptionField;
    public MFXDatePicker datePicker;
    public MFXButton addButton;
    public MFXComboBox<String> categoryComboBox;
    public MFXComboBox<String> bankComboBox;
    public MFXComboBox<String> recurrenceComboBox;

    public Button exitButton;
    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "Income", "Dividend Income", "Investment", "Rent", "Subscription", "Insurance", "Bills" /* Add more income types as needed */
    );
    private FinanceController financeController;
    public AddFinanceForm(FinanceController financeController) {
        this.financeController = financeController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/addFinanceForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() {
        loadBank();
        categoryComboBox.setItems(categoryList);
        datePicker.setValue(LocalDate.now());
        exitButton.setOnAction(this::closeStage);
        addButton.setOnAction(this::addFinance);

        recurrenceComboBox.getItems().clear();
        recurrenceComboBox.getItems().addAll("None", "Weekly", "Monthly");
        recurrenceComboBox.setValue("None");
    }

    private void addFinance(ActionEvent actionEvent) {
        String category = categoryComboBox.getSelectedItem();
        String name = nameField.getText().trim();
        String amountText = amountField.getText().trim();
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText().trim();
        String bankName = bankComboBox.getSelectedItem();
        String recurrencePeriod = recurrenceComboBox.getValue();

        if (name.isEmpty() || amountText.isEmpty() || category.isEmpty() || date == null || bankName.isEmpty()) {
            new ManualAlert(Alert.AlertType.ERROR, "ERROR", "There are empty fields", "Please fill in all required fields!").show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            new ManualAlert(Alert.AlertType.ERROR, "ERROR!", "Invalid amount", "Amount must be a number.").show();
            return;
        }

        try (Connection con = ConnectionManager.getConnection()) {
            int bankID = getBankIdByName(bankName, con);

            try (PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO transaction (name, amount, description, category, bankID, transactionDate, recurrencePeriod, userID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

                statement.setString(1, name);
                statement.setDouble(2, amount);
                statement.setString(3, description.isEmpty() ? "No description" : description);
                statement.setString(4, category);
                statement.setInt(5, bankID);
                statement.setDate(6, Date.valueOf(date));
                statement.setString(7, "None".equals(recurrencePeriod) ? null : recurrencePeriod);
                statement.setInt(8, Main.getUserId());

                statement.execute();
                Platform.runLater(() -> financeController.loadFinanceData());
                NotificationCenter.successAlert("Finance added!", "Your finance has been added successfully");
                exit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getBankIdByName(String bankName, Connection con) {
        String query = "SELECT bankID FROM bank WHERE bankName = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
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


    private void loadBank() {
        ObservableList<String> bankList = FXCollections.observableArrayList();
        bankList.add("None");
        Connection con = ConnectionManager.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT bankName FROM bank WHERE ownerID = ?")) {
            stmt.setInt(1, Main.getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bankName = rs.getString("bankName");
                bankList.add(bankName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        bankComboBox.setItems(bankList);
    }

    private void closeStage(ActionEvent actionEvent) {
        exit();
    }

    private void exit() {
        ((Pane) getParent()).getChildren().remove(this);
    }


}
