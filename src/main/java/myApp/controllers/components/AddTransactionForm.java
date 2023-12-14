package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import myApp.Main;
import myApp.utils.ConnectionManager;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddTransactionForm extends AnchorPane {
    public MFXFilterComboBox<String> typeComboBox;
    public MFXFilterComboBox<String> bankComboBox;
    public TextField transactionNameField;
    public MFXButton addButton;
    public TextField descriptionField;
    public TextField amountField;
    public MFXButton cancelButton;
    private final Connection con = ConnectionManager.getConnection();

    private final ObservableList<String> typeList = FXCollections.observableArrayList(
            "Clothes", "Education", "Entertainment", "Food", "Groceries",
            "Healthcare", "Transportation", "Travel", "Utilities", "Miscellaneous", "Other"
    );
    private ObservableList<String> bankList;
//    private Connection con; // Database connection

    public AddTransactionForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/addTransactionForm.fxml"));
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
        // Initialize ComboBox items with typeList and bankList
        System.out.println("INITIALIZE TRANSACTION");

        loadBank();

        typeComboBox.setItems(typeList);

        addButton.setOnAction(this::addTransaction);
        cancelButton.setOnAction(this::closeTransactionForm);

    }

    private void addTransaction(ActionEvent actionEvent) {
        String name = transactionNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = typeComboBox.getValue();
        String bankName = bankComboBox.getValue();
        LocalDate date = LocalDate.now();
        int userId = Main.getUserId();
        if (description.isEmpty()) {
            description = "No description";
        }
        if (name.isEmpty() || amountText.isEmpty() || category == null || bankName == null) {
            System.out.println("Please fill in all required fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (bankName.equals("None")) {
                addCashTransaction(name, amount, description, category, date, userId);
                updateCashAmount(userId, amount);
            } else {
                int bankId = getBankIdByName(bankName); // Fetch bankId based on bank name
                addBankTransaction(name, amount, description, category, bankId, date, userId);
                updateBankBalance(userId, amount);
            }


        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the transaction to the database.");
        }
    }

    private int getBankIdByName(String bankName) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT bankID FROM bank WHERE bankName = ?")) {
            stmt.setString(1, bankName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bankID");
                } else {
                    throw new SQLException("Bank not found");
                }
            }
        }
    }

    private void closeTransactionForm(ActionEvent actionEvent) {
        // Remove the form from its parent
        ((Pane) getParent()).getChildren().remove(this);
    }

    private void addCashTransaction(String name, double amount, String description, String category, LocalDate date, int userId) {
        try (PreparedStatement statement = con.prepareStatement(
                "INSERT INTO transaction (name, amount, description, category, transactionDate, userID) VALUES (?, ?, ?, ?, ?, ?)")) {

            statement.setString(1, name);
            statement.setDouble(2, amount);
            statement.setString(3, description);
            statement.setString(4, category);
            statement.setDate(5, Date.valueOf(date));
            statement.setInt(6, userId);

            statement.execute();
            System.out.println("Transaction added successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addBankTransaction(String name, double amount, String description, String category, int bankId, LocalDate date, int userId) {
        try (PreparedStatement statement = con.prepareStatement(
                "INSERT INTO transaction (name, amount, description, category, bankID, transactionDate, userID) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            statement.setString(1, name);
            statement.setDouble(2, amount);
            statement.setString(3, description);
            statement.setString(4, category);
            statement.setInt(5, bankId);
            statement.setDate(6, Date.valueOf(date));
            statement.setInt(7, userId);

            statement.execute();
            System.out.println("Transaction added successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCashAmount(int userId, double transactionAmount) {
        try (PreparedStatement statement = con.prepareStatement(
                "UPDATE user SET cashAmount = cashAmount - ? WHERE userID = ?")) {

            statement.setDouble(1, transactionAmount);
            statement.setInt(2, userId);

            statement.execute();
            System.out.println("CashAmount updated successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateBankBalance(int userId, double transactionAmount) {
        try (PreparedStatement statement = con.prepareStatement(
                "UPDATE bank SET balance = balance - ? WHERE ownerID = ?")) {

            statement.setDouble(1, transactionAmount);
            statement.setInt(2, userId);

            statement.execute();
            System.out.println("Bank balance updated successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadBank() {
        bankList = FXCollections.observableArrayList();
        bankList.add("None");
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
}
