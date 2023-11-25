package myApp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import myApp.utils.ConnectionManager;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {
    private final ObservableList<String> typeList = FXCollections.observableArrayList("Food",
            "Clothes",
            "Groceries",
            "Entertainment",
            "Utilities",
            "Transportation",
            "Healthcare",
            "Education",
            "Travel",
            "Miscellaneous");

    private final ObservableList<String> bankList = FXCollections.observableArrayList("TPB",
            "VCB",
            "ACB",
            "BIDV",
            "MB",
            "Techcombank",
            "VietinBank",
            "VPBank",
            "Eximbank");
    public Button addTransactionButton;
    public TextField transactionNameField;
    public TextField amountField;
    public TextField descriptionField;
    public ComboBox<String> typeComboBox;
    public Button addButton;
    public ComboBox<String> bankComboBox;
    public Label testLabel1;
    public Label testLabel2;
    public Label testLabel3;
    public AnchorPane popupTransactionDialog;
    public TableView transactionTable;
    public ComboBox sortComboBox;

    public void addTransaction(ActionEvent actionEvent) {
        // Retrieve values from the input fields
        String name = transactionNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = typeComboBox.getValue();
        String bank = bankComboBox.getValue();

        // Set description when it's empty
        if (description.isEmpty()) {
            description = "No description";
        }
        // Validate input
        if (name.isEmpty() || amountText.isEmpty() || type == null || bank == null) {
            // Display an error message or handle the validation failure appropriately
            System.out.println("Please fill in all required fields.");
            return; // Don't proceed with the transaction if validation fails
        }

        try {
            // Parse the amount
            double amount = Double.parseDouble(amountText);

            // Insert the transaction into the database
            Connection con = ConnectionManager.getConnection();
            try {
                PreparedStatement statement = con.prepareStatement("INSERT INTO transactions (name, amount, description, category, bank) VALUES (?,?,?,?,?)");
                statement.setString(1, name);
                statement.setDouble(2, amount);
                statement.setString(3, description);
                statement.setString(4, type);
                statement.setString(5, bank);

                System.out.println("SQL statement: " + statement);
                statement.execute();
                statement.close();
                System.out.println("Transaction added!");

                popupTransactionDialog.setVisible(false); // Close the popup window
            } catch (SQLException e) {
                // Handle database-related errors
                e.printStackTrace();
                System.out.println("Error adding the transaction to the database.");
            }
        } catch (NumberFormatException e) {
            // Handle parsing error for the amount
            System.out.println("Invalid amount. Please enter a valid number.");
        }
    }

    public void openPopupDialog(ActionEvent actionEvent) {
        popupTransactionDialog.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeComboBox.setItems(typeList);
        bankComboBox.setItems(bankList);

    }
}
