package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class UpdateBudgetForm extends BorderPane {
    public MFXFilterComboBox<String> categoryComboBox;
    public MFXTextField limitField;
    public MFXDatePicker startDatePicker;
    public MFXDatePicker endDatePicker;
    public MFXButton addBudgetButton;
    public MFXButton exitButton;
    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "Clothes", "Education", "Entertainment", "Food", "Groceries",
            "Healthcare", "Transportation", "Travel", "Utilities", "Miscellaneous", "Other"
    );

    public UpdateBudgetForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/updateBudgetForm.fxml"));
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
    }
}
