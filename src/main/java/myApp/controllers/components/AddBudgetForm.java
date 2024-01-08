package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import myApp.controllers.views.BudgetController;
import myApp.utils.Draggable;
import myApp.utils.NotificationCenter;

import java.io.IOException;
import java.time.LocalDate;

public class AddBudgetForm extends BorderPane {
    public MFXFilterComboBox<String> categoryComboBox;
    public MFXTextField limitField;
    public MFXDatePicker startDatePicker;
    public MFXDatePicker endDatePicker;
    public MFXButton addBudgetButton;
    public MFXButton exitButton;
    public Button exitIcon;
    private Stage stage;
    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "Clothes", "Education", "Entertainment", "Food", "Groceries",
            "Healthcare", "Transportation", "Travel", "Other"
    );

    private BudgetController budgetController;

    public AddBudgetForm(BudgetController budgetController) {
        this.budgetController = budgetController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/addBudgetForm.fxml"));
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

        categoryComboBox.setItems(categoryList);
        categoryComboBox.selectFirst(); // Select the first item by default
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusMonths(1)); // Example end date
        addBudgetButton.setOnAction(this::addBudget);
        exitButton.setOnAction(this::exit);
        exitIcon.setOnAction(this::exit);
        new Draggable().makeDraggable(this);
    }

    private void exit(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }

    private void addBudget(ActionEvent actionEvent) {

        String category = categoryComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        try {
            double limit = Double.parseDouble(limitField.getText());
            budgetController.addBudgetInDataBase(category, limit, startDate, endDate);
        } catch (NumberFormatException e) {
            NotificationCenter.errorAlert("Invalid Amount", "Entered amount must be a number");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
