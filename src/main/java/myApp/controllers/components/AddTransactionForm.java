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
            "Food", "Clothes", "Groceries", "Entertainment", "Utilities",
            "Transportation", "Healthcare", "Education", "Travel", "Miscellaneous"
    );
    private final ObservableList<String> bankList = FXCollections.observableArrayList();
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
        bankComboBox.setItems(bankList);  // Move this line here

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
            } else {
                int bankId = getBankIdByName(bankName); // Fetch bankId based on bank name
                addBankTransaction(name, amount, description, category, bankId, date, userId);
            }


//            try (PreparedStatement statement = con.prepareStatement(
//                    "INSERT INTO transactions (name, amount, description, category, bankId) VALUES (?, ?, ?, ?, ?)")) {
//
//                statement.setString(1, name);
//                statement.setDouble(2, amount);
//                statement.setString(3, description);
//                statement.setString(4, category);
//                statement.setString(5, bankId);
//
//                statement.execute();
//                System.out.println("Transaction added successfully!");
//
//        }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the transaction to the database.");
        }
    }

    private int getBankIdByName(String bankName) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT bankId FROM bank WHERE name = ?")) {
            stmt.setString(1, bankName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bankId");
                } else {
                    throw new SQLException("Bank not found");
                }
            }
        }
    }

    private void closeTransactionForm(ActionEvent actionEvent) {
        // Remove the form from its parent
        ((AnchorPane) getParent()).getChildren().remove(this);
    }

    private void addCashTransaction(String name, double amount, String description, String category, LocalDate date, int userId) {
        try (PreparedStatement statement = con.prepareStatement(
                "INSERT INTO transaction (name, amount, description, category, transaction_date, userId) VALUES (?, ?, ?, ?, ?, ?)")) {

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
                "INSERT INTO transaction (name, amount, description, category, bankId transaction_date, userId) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

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

    private void loadBank() {
        bankList.clear();
        try (PreparedStatement stmt = con.prepareStatement("SELECT name FROM bank WHERE ownerId = ?")) {
            stmt.setInt(1, Main.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String bankName = rs.getString("name");
                    bankList.add(bankName);
                } else {
                    throw new SQLException("No bank left");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        bankComboBox.setItems(bankList);
    }
}
