package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import myApp.Main;
import myApp.utils.ConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LinkBankForm extends BorderPane {
    public MFXFilterComboBox<String> bankComboBox;
    public MFXTextField balanceField;
    public MFXButton linkBankButton;
    public Button exitButton;
    private final Connection con = ConnectionManager.getConnection();
    private final ObservableList<String> bankList = FXCollections.observableArrayList(
            "TPB", "VCB", "ACB", "BIDV", "MB", "Techcombank", "VietinBank", "VPBank", "Eximbank"
    );
    private Stage stage;
    public LinkBankForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/linkBankForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize();
            linkBankButton.setOnAction(this::linkBank);
            exitButton.setOnAction(this::closeStage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void linkBank(ActionEvent actionEvent) {
        String bankName = bankComboBox.getValue();
        String balanceText = balanceField.getText();
        int userId = Main.getUserId();

        if (bankName == null || balanceText == null) {
            System.out.println("Please enter all fields first.");
            return;
        }

        try {
            double balance = Double.parseDouble(balanceText);

            try (PreparedStatement insertStatement = con.prepareStatement(
                    "INSERT INTO bank (name, ownerId, balance) VALUES (?, ?, ?)")) {

                insertStatement.setString(1, bankName);
                insertStatement.setInt(2, userId);
                insertStatement.setDouble(3, balance);

                insertStatement.execute();
                System.out.println("Bank added successfully!");

                // Update user's bankAmount in the user table
                try (PreparedStatement updateStatement = con.prepareStatement(
                        "UPDATE user SET bankAmount = bankAmount + ? WHERE userId = ?")) {

                    updateStatement.setDouble(1, balance);
                    updateStatement.setInt(2, userId);

                    updateStatement.executeUpdate();
                    System.out.println("User's bankAmount updated successfully!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the bank to the database.");
        }
    }


    private void initialize() {
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

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
