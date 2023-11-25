package myApp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.scene.control.cell.PropertyValueFactory;


public class TransactionController implements Initializable {
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> nameColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> bankColumn;

    @FXML private Button addTransactionButton;
    @FXML private TextField transactionNameField;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> bankComboBox;
    @FXML private AnchorPane popupTransactionDialog;

    private final ObservableList<String> typeList = FXCollections.observableArrayList(
            "Food", "Clothes", "Groceries", "Entertainment", "Utilities",
            "Transportation", "Healthcare", "Education", "Travel", "Miscellaneous"
    );
    private final ObservableList<String> bankList = FXCollections.observableArrayList(
            "TPB", "VCB", "ACB", "BIDV", "MB", "Techcombank", "VietinBank", "VPBank", "Eximbank"
    );
    private final ObservableList<Transaction> transactionData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeComboBox.setItems(typeList);
        bankComboBox.setItems(bankList);
        setupTransactionTable();
        loadTransactions();
    }

    private void setupTransactionTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bank"));

        transactionTable.setItems(transactionData);
    }

    private void loadTransactions() {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name, amount, description, category, bank FROM transactions");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                double amount = rs.getDouble("amount");
                String description = rs.getString("description");
                String category = rs.getString("category");
                String bank = rs.getString("bank");

                Transaction transaction = new Transaction(name, amount, description, category, bank);
                transactionData.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                transactionData.add(new Transaction(name, amount, description, category, bank));

                popupTransactionDialog.setVisible(false);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the transaction to the database.");
        }
    }

    public void openPopupDialog(ActionEvent actionEvent) {
        popupTransactionDialog.setVisible(true);
    }

}
