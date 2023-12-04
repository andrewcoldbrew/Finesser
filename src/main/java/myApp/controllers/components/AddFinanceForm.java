package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.IOException;

public class AddFinanceForm extends BorderPane {
    public MFXTextField nameField;
    public MFXTextField amountField;
    public MFXTextField timeField;
    public MFXDatePicker startDatePicker;
    public MFXButton addButton;
    public MFXComboBox<String> typeComboBox;
    public MFXComboBox<String> typeOfTimeComboBox;
    public Button exitButton;
//    private final ObservableList<String> financeTypeList = FXCollections.observableArrayList(
//            "Salary", "Bills", "Expenses", "Taxes",
//            "Rent", "Loans", "Insurance", "Membership", "Subscriptions"
//    );

        private final ObservableList<String> financeTypeList = FXCollections.observableArrayList(
            "Income", "Outcome"
    );
    private final ObservableList<String> timeTypeList = FXCollections.observableArrayList(
        "Days", "Weeks", "Months", "Years"
    );
    private Stage stage;

    public AddFinanceForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/addFinanceForm.fxml"));
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
        exitButton.setOnAction(this::closeStage);
        typeComboBox.setItems(financeTypeList);
        typeOfTimeComboBox.setItems(timeTypeList);

    }

    private void closeStage(ActionEvent actionEvent) {
        if (stage != null) {
            System.out.println("CLOSING STAGE!");
            stage.close();
        } else {
            System.out.println("STAGE NULL");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public Stage getStage() {
        return stage;
    }
}
