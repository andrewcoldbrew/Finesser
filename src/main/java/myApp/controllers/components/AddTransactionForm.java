package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import myApp.utils.ConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddTransactionForm extends AnchorPane {
    public MFXFilterComboBox<String> typeComboBox;
    public MFXFilterComboBox<String> bankComboBox;
    public TextField transactionNameField;
    public MFXButton addButton;
    public TextField descriptionField;
    public TextField amountField;
    public MFXButton cancelButton;

    private final ObservableList<String> typeList = FXCollections.observableArrayList(
            "Food", "Clothes", "Groceries", "Entertainment", "Utilities",
            "Transportation", "Healthcare", "Education", "Travel", "Miscellaneous"
    );
    private final ObservableList<String> bankList = FXCollections.observableArrayList(
            "TPB", "VCB", "ACB", "BIDV", "MB", "Techcombank", "VietinBank", "VPBank", "Eximbank"
    );

    private Connection con; // Database connection

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
        this.con = ConnectionManager.getConnection(); // Initialize the connection

        // Initialize ComboBox items with typeList and bankList
        typeComboBox.setItems(typeList);
        bankComboBox.setItems(bankList);

        addButton.setOnAction(this::addTransaction);
        cancelButton.setOnAction(this::closeTransactionForm);
    }

    private void addTransaction(ActionEvent actionEvent) {
        String name = transactionNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = typeComboBox.getValue();
        String bankName = bankComboBox.getValue();

        if (description.isEmpty()) {
            description = "No description";
        }
        if (name.isEmpty() || amountText.isEmpty() || category == null || bankName == null) {
            System.out.println("Please fill in all required fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            String bankId = getBankIdByName(bankName); // Fetch bankId based on bank name

            try (PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO transactions (name, amount, description, category, bankId) VALUES (?, ?, ?, ?, ?)")) {

                statement.setString(1, name);
                statement.setDouble(2, amount);
                statement.setString(3, description);
                statement.setString(4, category);
                statement.setString(5, bankId);

                statement.execute();
                System.out.println("Transaction added successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the transaction to the database.");
        }
    }

    private String getBankIdByName(String bankName) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("SELECT bankId FROM bank WHERE name = ?")) {
            stmt.setString(1, bankName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("bankId");
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
}
