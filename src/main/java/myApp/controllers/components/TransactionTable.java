package myApp.controllers.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import myApp.utils.ConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionTable extends AnchorPane {
    public ComboBox<String> typeComboBox;
    public ComboBox<String> bankComboBox;
    public TextField transactionNameField;
    public Button addButton;
    public TextField descriptionField;
    public TextField amountField;
    public Button cancelButton;

    private final ObservableList<String> typeList = FXCollections.observableArrayList(
            "Food", "Clothes", "Groceries", "Entertainment", "Utilities",
            "Transportation", "Healthcare", "Education", "Travel", "Miscellaneous"
    );
    private final ObservableList<String> bankList = FXCollections.observableArrayList(
            "TPB", "VCB", "ACB", "BIDV", "MB", "Techcombank", "VietinBank", "VPBank", "Eximbank"
    );

    public TransactionTable() {
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
        typeComboBox.setItems(typeList);
        bankComboBox.setItems(bankList);
        addButton.setOnAction(this::addTransaction);
        cancelButton.setOnAction(this::closeTransactionForm);

    }
    public void addTransaction(ActionEvent actionEvent) {
        String name = transactionNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = typeComboBox.getValue();
        String bank = bankComboBox.getValue();

        if (description.isEmpty()) {
            description = "No description";
        }
        if (name.isEmpty() || amountText.isEmpty() || category == null || bank == null) {
            System.out.println("Please fill in all required fields dumbass.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            try (Connection con = ConnectionManager.getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO transactions (name, amount, description, category, bank) VALUES (?, ?, ?, ?, ?)")) {

                statement.setString(1, name);
                statement.setDouble(2, amount);
                statement.setString(3, description);
                statement.setString(4, category);
                statement.setString(5, bank);

                statement.execute();
                System.out.println("Transaction added you rich fuck!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the transaction to the database.");
        }
    }

    public void closeTransactionForm(ActionEvent actionEvent) {
        // Assuming you want to remove the entire TransactionTable from its parent
        ((Pane) getParent()).getChildren().remove(this);
    }
}
