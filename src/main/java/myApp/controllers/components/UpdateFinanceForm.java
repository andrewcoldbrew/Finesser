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
import myApp.controllers.views.FinanceController;
import myApp.controllers.views.TransactionController;
import myApp.models.Transaction;
import myApp.utils.Draggable;

import java.io.IOException;
import java.time.LocalDate;

public class UpdateFinanceForm extends StackPane {
    public StackPane stackPane;
    public MFXFilterComboBox<String> typeComboBox;
    public TextField financeNameField;
    public MFXFilterComboBox<String> recurrencePeriodComboBox;
    public MFXButton updateButton;
    public TextField descriptionField;
    public TextField amountField;
    public MFXDatePicker datePicker;
    public MFXButton cancelButton;
    private FinanceController financeController;
    private Transaction transaction;

    private final ObservableList<String> typeList = FXCollections.observableArrayList(
            "Income", "Dividend Income", "Investment", "Rent", "Subscription", "Insurance", "Bills"
    );

    private final ObservableList<String> recurrencePeriodList = FXCollections.observableArrayList(
            "None", "Weekly", "Monthly"
    );

    public UpdateFinanceForm(FinanceController financeController, Transaction transaction) {
        this.financeController = financeController;
        this.transaction = transaction;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/updateFinanceForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        new Draggable().makeDraggable(this);
        try {
            fxmlLoader.load();
            initialize(transaction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(Transaction transaction) {
        typeComboBox.setItems(typeList);
        recurrencePeriodComboBox.setItems(recurrencePeriodList);

        typeComboBox.selectItem(transaction.getCategory());
        financeNameField.setText(transaction.getName());
        amountField.setText(String.valueOf(transaction.getAmount()));
        descriptionField.setText(transaction.getDescription());
        datePicker.setValue(transaction.getDate());
        recurrencePeriodComboBox.selectItem(transaction.getRecurrencePeriod());
        updateButton.setOnAction(this::updateTransaction);
        cancelButton.setOnAction(this::cancel);
    }

    private void cancel(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }

    private void updateTransaction(ActionEvent actionEvent) {
        String name = financeNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = typeComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String recurrencePeriod = recurrencePeriodComboBox.getValue();

        if (description.isEmpty()) {
            description = "No description";
        }
        if (name.isEmpty() || amountText.isEmpty() || category.isEmpty() || recurrencePeriod.isEmpty() || date == null) {
            new ErrorAlert(stackPane, "Update declined", "Please fill in all fields!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            // INVOKE THE FUNCTION HERE! ~UwU~
            financeController.updateFinanceInDatabase(name, amount, description, category, transaction.getBankName(), date, recurrencePeriod, transaction.getTransactionID());

        } catch (NumberFormatException e) {
            new ErrorAlert(stackPane, "Invalid input", "Amount must be a number");
        }
    }
}
