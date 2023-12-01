package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import myApp.utils.ConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddBudgetForm extends AnchorPane {
    public MFXFilterComboBox<String> categoryComboBox;
    public MFXTextField limitField;
    public MFXDatePicker startDatePicker;
    public MFXDatePicker endDatePicker;
    public MFXButton addBudgetButton;
    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "Food", "Clothes", "Groceries", "Entertainment", "Utilities",
            "Transportation", "Healthcare", "Education"
    );

    public AddBudgetForm() {
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
    }

    private void addBudget(ActionEvent actionEvent) {
        String category = categoryComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        try {
            double limit = Double.parseDouble(limitField.getText());
            Connection con = ConnectionManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO budget (category, budget_limit, start_date, end_date) VALUES (?, ?, ?, ?)");
            statement.setString(1, category);
            statement.setDouble(2, limit);
            statement.setDate(3, Date.valueOf(startDate));
            statement.setDate(4, Date.valueOf(endDate));
            statement.execute();
            System.out.println("Budget added!");
        } catch (NumberFormatException e) {
            System.out.println("Limit must be a number");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the budget to the database.");
        }
    }
}
