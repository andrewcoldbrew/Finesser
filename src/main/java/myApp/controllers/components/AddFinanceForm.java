package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import myApp.Main;
import myApp.utils.ConnectionManager;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class AddFinanceForm extends BorderPane {
    public MFXTextField nameField;
    public MFXTextField amountField;
    public MFXTextField descriptionField;
    public MFXDatePicker datePicker;
    public MFXButton addButton;
    public MFXComboBox<String> categoryComboBox;
    public MFXComboBox<String> bankComboBox;

    public Button exitButton;
    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "Income", "Rent", "Subscription", "Insurance", "Taxes", "Bills" /* Add more income types as needed */
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
        loadBank();
        categoryComboBox.setItems(categoryList);
        datePicker.setValue(LocalDate.now());
        exitButton.setOnAction(this::closeStage);
        addButton.setOnAction(this::addFinance);
    }

    private void addFinance(ActionEvent actionEvent) {
        int userID = Main.getUserId();
        String category = categoryComboBox.getSelectedItem();
        String name = nameField.getText().trim();
        String amountText = amountField.getText().trim();
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText().trim();
        String bankName = bankComboBox.getSelectedItem();

        if (description.isEmpty()) {
            description = "No description";
        }
        if (name.isEmpty() || amountText.isEmpty() || category.isEmpty() || date == null || bankName.isEmpty() ) {
            new ManualAlert(Alert.AlertType.ERROR, "ERROR", "There are empty fields", "Please fill in all required fields!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            int bankID = getBankIdByName(bankName);

            try (Connection con = ConnectionManager.getConnection();
                 PreparedStatement statement = con.prepareStatement("INSERT INTO transaction (name, amount, description, category, bankID, transactionDate, userID) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                statement.setString(1, name);
                statement.setDouble(2, amount);
                statement.setString(3, description);
                statement.setString(4, category);
                statement.setInt(5, bankID);
                statement.setDate(6, Date.valueOf(date));
                statement.setInt(7, userID);
                statement.execute();
                new SuccessAlert("Finance added successfully!");
                exit();
            }
        } catch (NumberFormatException e) {
            new ManualAlert(Alert.AlertType.ERROR, "ERROR!", "Invalid amount", "Amount must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the finance to the database.");
        }
    }

    private int getBankIdByName(String bankName) {
        String query = "SELECT bankID FROM bank WHERE bankName = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, bankName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bankID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void loadBank() {
        ObservableList<String> bankList = FXCollections.observableArrayList();
        bankList.add("None");
        Connection con = ConnectionManager.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT bankName FROM bank WHERE ownerID = ?")) {
            stmt.setInt(1, Main.getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String bankName = rs.getString("bankName");
                bankList.add(bankName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        bankComboBox.setItems(bankList);
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

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public Stage getStage() {
        return stage;
    }
}
