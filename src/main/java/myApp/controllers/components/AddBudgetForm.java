package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import myApp.Main;
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
    public MFXButton exitButton;
    private Stage stage;
    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "Clothes", "Education", "Entertainment", "Food", "Groceries",
            "Healthcare", "Transportation", "Travel", "Utilities", "Miscellaneous", "Other"
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
        exitButton.setOnAction(this::exit);
    }

    private void exit(ActionEvent actionEvent) {
        closeStage();
    }

    private void addBudget(ActionEvent actionEvent) {

        String category = categoryComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        try {
            double limit = Double.parseDouble(limitField.getText());
            Connection con = ConnectionManager.getConnection();
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO budget (userID, category, budgetLimit, startDate, endDate) VALUES (?, ?, ?, ?, ?)");
            statement.setInt(1, Main.getUserId());
            statement.setString(2, category);
            statement.setDouble(3, limit);
            statement.setDate(4, Date.valueOf(startDate));
            statement.setDate(5, Date.valueOf(endDate));
            statement.execute();
            System.out.println("Budget added!");
            closeStage();
        } catch (NumberFormatException e) {
            System.out.println("Limit must be a number");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the budget to the database.");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    private void closeStage() {
        if (stage != null) {
            System.out.println("CLOSING STAGE!");
            stage.close();
        } else {
            System.out.println("STAGE NULL");
        }
    }
}
