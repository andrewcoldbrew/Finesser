package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import myApp.Main;
import myApp.models.Budget;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class UpdateTransactionForm extends BorderPane {
    public MFXFilterComboBox<String> typeComboBox;
    public TextField transactionNameField;
    public MFXButton updateButton;
    public TextField descriptionField;
    public TextField amountField;
    public MFXButton cancelButton;
    private final ObservableList<String> typeList = FXCollections.observableArrayList(
            "Clothes", "Education", "Entertainment", "Food", "Groceries",
            "Healthcare", "Transportation", "Travel", "Utilities", "Miscellaneous", "Other"
    );
    public UpdateTransactionForm(Transaction transaction) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/updateTransactionForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(transaction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(Transaction transaction) {
        typeComboBox.setItems(typeList);

        typeComboBox.selectItem(transaction.getCategory());
        transactionNameField.setText(transaction.getName());
        amountField.setText(String.valueOf(transaction.getAmount()));
        descriptionField.setText(transaction.getDescription());

        updateButton.setOnAction(this::updateTransaction);
        cancelButton.setOnAction(this::cancel);
    }

    private void cancel(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }

    private void updateTransaction(ActionEvent actionEvent) {
        int userId = Main.getUserId();

        String name = transactionNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = typeComboBox.getValue();
        LocalDate date = LocalDate.now();

        if (description.isEmpty()) {
            description = "No description";
        }
        if (name.isEmpty() || amountText.isEmpty() || category == null) {
            System.out.println("Please fill in all required fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            // INVOKE THE FUNCTION HERE! ~UwU~

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        }
    }

    private void updateTransactionInDB(String name, double amount, String description, String category, int bankId, LocalDate date, int userId) {
        // UPDATE TRANSACTION LOGIC HERE! ~UwU~
    }
}
