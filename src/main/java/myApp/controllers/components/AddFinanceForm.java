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
import myApp.utils.ConnectionManager;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

public class AddFinanceForm extends BorderPane {
    public MFXTextField nameField;
    public MFXTextField amountField;
    public MFXTextField timeDurationField;
    public MFXTextField descriptionField;
    public MFXDatePicker startDatePicker;
    public MFXButton addButton;
    public MFXComboBox<String> categoryComboBox;
    public MFXComboBox<String> typeComboBox;
    public MFXComboBox<String> typeOfTimeComboBox;
    public Button exitButton;
    private final ObservableList<String> incomeTypeList = FXCollections.observableArrayList(
            "Salary", "Rental Income", "Bonuses", "Alimony", "Others" /* Add more income types as needed */
    );

    private final ObservableList<String> outcomeTypeList = FXCollections.observableArrayList(
            "Bills", "Expenses", "Taxes",
            "Rent", "Loans", "Insurance", "Membership", "Subscriptions", "Others"
    );

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
        typeComboBox.setItems(financeTypeList);
        typeComboBox.selectItem("Income");
        categoryComboBox.setItems(incomeTypeList);
        typeOfTimeComboBox.setItems(timeTypeList);
        exitButton.setOnAction(this::closeStage);
        typeComboBox.setOnAction(this::updateCategory);
        addButton.setOnAction(this::addFinance);
    }

    private void addFinance(ActionEvent actionEvent) {
        String type = typeComboBox.getSelectedItem();
        String category = categoryComboBox.getSelectedItem();
        String name = nameField.getText().trim();
        String amountText = amountField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        String timeDurationText = timeDurationField.getText();
        String timeType = typeOfTimeComboBox.getSelectedItem();
        String description = descriptionField.getText().trim();

        if (description.isEmpty()) {
            description = "No description";
        }
        if (name.isEmpty() || amountText.isEmpty() || category == null ) {
            System.out.println("Please fill in all required fields dumbass.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            int timeDuration = Integer.parseInt(timeDurationText);

            try (Connection con = ConnectionManager.getConnection();
                 PreparedStatement statement = con.prepareStatement("INSERT INTO finance (name, amount, time_duration, time_type, start_date, description, type, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

                statement.setString(1, name);
                statement.setDouble(2, amount);
                statement.setInt(3, timeDuration);
                statement.setString(4, timeType);
                statement.setDate(5, Date.valueOf(startDate));
                statement.setString(6, description);
                statement.setString(7, type);
                statement.setString(8, category);

                statement.execute();
                System.out.println("Finance added!");
                exit();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the finance to the database.");
        }
    }



    private void updateCategory(ActionEvent actionEvent) {
        String type = typeComboBox.getSelectedItem();
        categoryComboBox.clear();
        if (type.equals("Income")) {
            categoryComboBox.setItems(incomeTypeList);
        } else if (type.equals("Outcome")) {
            categoryComboBox.setItems(outcomeTypeList);
        } else {
            System.out.println("Type combo box is null");
        }
    }

    private void closeStage(ActionEvent actionEvent) {
        exit();
    }

    private void exit() {
        if (stage != null) {
            System.out.println("CLOSING STAGE!");
            stage.close();
        } else {
            System.out.println("STAGE NULL");
        }
    }

    public void clearData() {
        nameField.clear();
        amountField.clear();
        typeComboBox.selectItem("Income");
        categoryComboBox.setItems(incomeTypeList);
        timeDurationField.clear();
        startDatePicker.clear();
        descriptionField.clear();
        typeOfTimeComboBox.clear();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public Stage getStage() {
        return stage;
    }
}
