package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import myApp.Main;
import myApp.controllers.views.TransactionController;
import myApp.models.Transaction;

import java.io.IOException;
import java.time.LocalDate;

public class UpdateTransactionForm extends StackPane {
    public StackPane stackPane;
    public MFXFilterComboBox<String> typeComboBox;
    public TextField transactionNameField;
    public MFXButton updateButton;
    public TextField descriptionField;
    public TextField amountField;
    public MFXDatePicker datePicker;
    public MFXButton cancelButton;
    private TransactionController transactionController;
    private Transaction transaction;
    private final ObservableList<String> typeList = FXCollections.observableArrayList(
            "Clothes", "Education", "Entertainment", "Food", "Groceries",
            "Healthcare", "Transportation", "Travel", "Utilities", "Miscellaneous", "Other"
    );
    public UpdateTransactionForm(Transaction transaction, TransactionController transactionController) {
        this.transactionController = transactionController;
        this.transaction = transaction;
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
        datePicker.setValue(transaction.getDate());

        updateButton.setOnAction(this::updateTransaction);
        cancelButton.setOnAction(this::cancel);
    }

    private void cancel(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }

    private void updateTransaction(ActionEvent actionEvent) {
        String name = transactionNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = typeComboBox.getValue();
        LocalDate date = datePicker.getValue();

        if (description.isEmpty()) {
            description = "No description";
        }
        if (name.isEmpty() || amountText.isEmpty() || category.isEmpty() || date == null) {
            new ErrorAlert(stackPane, "Update declined", "Please fill in all fields!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            // INVOKE THE FUNCTION HERE! ~UwU~
            transactionController.updateTransactionInDatabase(name, amount, description, category, transaction.getBankName(), date, transaction.getTransactionID());
            new SuccessAlert(stackPane, "Your transaction has been updated successfully!");

        } catch (NumberFormatException e) {
            new ErrorAlert(stackPane, "Invalid input", "Amount must be a number");
        }
    }
}
