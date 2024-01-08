package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import myApp.controllers.views.AccountController;
import myApp.models.User;
import myApp.utils.Draggable;
import myApp.utils.NotificationCenter;

import java.io.IOException;
import java.time.LocalDate;

public class UpdateInfoForm extends StackPane {
    public StackPane stackPane;
    public MFXTextField fnameField;
    public MFXTextField lnameField;
    public MFXTextField emailField;
    public MFXComboBox<String> genderComboBox;
    public MFXComboBox<String> countryComboBox;
    public MFXDatePicker datePicker;
    public MFXButton updateButton;
    public Button exitButton;
    public AccountController accountController;
    public User user;
    public UpdateInfoForm(AccountController accountController, User user) {
        this.accountController = accountController;
        this.user = user;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/updateInfoForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        new Draggable().makeDraggable(this);
        try {
            fxmlLoader.load();
            initialize(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(User user) {
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        countryComboBox.setItems(FXCollections.observableArrayList("Vietnam", "China", "Australia",
                "Jamaica", "India", "Canada", "United States", "Japan", "Korea"));
        fnameField.setText(user.getFname());
        lnameField.setText(user.getLname());
        emailField.setText(user.getEmail());
        genderComboBox.selectItem(user.getGender());
        countryComboBox.selectItem(user.getCountry());
        datePicker.setValue(user.getDateOfBirth());
        updateButton.setOnAction(this::updateInfo);
        exitButton.setOnAction(this::cancel);
    }

    private void updateInfo(ActionEvent actionEvent) {
        String fname = fnameField.getText().trim();
        String lname = lnameField.getText().trim();
        String email = emailField.getText().trim();
        String gender = genderComboBox.getValue();
        LocalDate dob = datePicker.getValue();
        String country = countryComboBox.getValue();


        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || gender == null || dob == null || country == null) {
            NotificationCenter.errorAlert("Empty fields!", "Please fill in all fields before proceed");
        } else {
            accountController.updateInfoInDatabase(fname, lname, email, gender, dob, country);

        }
    }

    private void cancel(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }


}
