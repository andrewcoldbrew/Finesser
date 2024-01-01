package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import myApp.controllers.views.BudgetController;
import myApp.models.Budget;
import myApp.utils.Draggable;
import myApp.utils.NotificationCenter;

import java.io.IOException;
import java.time.LocalDate;

public class UpdateBudgetForm extends StackPane {
    public StackPane stackPane;
    public MFXFilterComboBox<String> categoryComboBox;
    public MFXTextField limitField;
    public MFXDatePicker startDatePicker;
    public MFXDatePicker endDatePicker;
    public MFXButton updateButdgetButton;
    public MFXButton exitButton;
    private Budget budget;
    private BudgetController budgetController;
    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "Clothes", "Education", "Entertainment", "Food", "Groceries",
            "Healthcare", "Transportation", "Travel", "Other"
    );

    public UpdateBudgetForm(Budget budget, BudgetController budgetController) {
        this.budget = budget;
        this.budgetController = budgetController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/updateBudgetForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            initialize(budget);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(Budget budget) {
        categoryComboBox.setItems(categoryList);
        categoryComboBox.selectItem(budget.getCategory());
        limitField.setText(String.valueOf(budget.getAllocatedAmount()));
        startDatePicker.setValue(budget.getStartDate());
        endDatePicker.setValue(budget.getEndDate());
        updateButdgetButton.setOnAction(this::updateBudget);
        exitButton.setOnAction(this::exit);
        new Draggable().makeDraggable(this);
    }

    private void exit(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }

    private void updateBudget(ActionEvent actionEvent) {
        String limitText = limitField.getText().trim();
        String category = categoryComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (limitText.isEmpty() || category.isEmpty() || startDate == null || endDate == null) {
            NotificationCenter.errorAlert("Empty fields!", "Please fill in all fields before proceed");
            return;
        }

        try {
            double limit = Double.parseDouble(limitText);
            // INVOKE THE FUNCTION HERE! ~UwU~
            budgetController.updateBudgetInDatabase(category, limit, startDate, endDate, budget.getId());

        } catch (NumberFormatException e) {
            NotificationCenter.errorAlert("Invalid Amount", "Entered amount must be a number");
        }
    }
}
