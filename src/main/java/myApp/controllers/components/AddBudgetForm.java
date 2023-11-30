package myApp.controllers.components;

import io.github.palexdev.materialfx.builders.control.IconBuilder;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import myApp.Main;
import myApp.utils.ConnectionManager;

import java.awt.*;
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
    public MFXButton exitButton;
    public MFXButton addBudgetButton;
    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "All", "Food", "Clothes", "Groceries", "Entertainment", "Utilities",
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
        categoryComboBox.selectItem("All");
        startDatePicker.setValue(LocalDate.now());
        addBudgetButton.setOnAction(this::addBudget);
        exitButton.setOnAction(this::exit);
    }

    private void exit(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }

    private void addBudget(ActionEvent actionEvent) {
        String userId = Main.getUserId();
        String category = categoryComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        try {
            double limit = Double.parseDouble(limitField.getText());
            Connection con = ConnectionManager.getConnection();
            PreparedStatement statement = con.prepareStatement("INSERT INTO budget (userId, category, budget_limit, start_date, end_date) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, userId);
            statement.setString(2, category);
            statement.setDouble(3, limit);
            statement.setDate(4, Date.valueOf(startDate));
            statement.setDate(5, Date.valueOf(endDate));
            statement.execute();
            System.out.println("Budget added!");
        } catch (NumberFormatException e) {
            System.out.println("Limit must be a number");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+"); // This regex checks if the string contains only digits
    }



}
