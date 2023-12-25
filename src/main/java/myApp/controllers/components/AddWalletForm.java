package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import myApp.Main;
import myApp.controllers.views.AccountController;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddWalletForm extends BorderPane {
    public Button exitButton;
    public MFXTextField balanceField;
    public MFXButton addButton;
    private Connection con = ConnectionManager.getConnection();
    private Stage stage;
    private AccountController accountController;
    public AddWalletForm(AccountController accountController) {
        this.accountController = accountController;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/addWalletForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            addButton.setOnAction(this::addWallet);
            exitButton.setOnAction(this::closeStage);
            new Draggable().makeDraggable(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addWallet(ActionEvent actionEvent) {
        String balanceText = balanceField.getText();
        int userId = Main.getUserId();

        if (balanceText == null) {
            System.out.println("Please enter all fields first.");
            return;
        }

        try {
            double balance = Double.parseDouble(balanceText);

            try (PreparedStatement updateStatement = con.prepareStatement(
                    "UPDATE user SET cashAmount = cashAmount + ? WHERE userID = ?")) {

                updateStatement.setDouble(1, balance);
                updateStatement.setInt(2, userId);

                updateStatement.executeUpdate();
                System.out.println("User's cashAmount updated successfully!");
                new SuccessAlert(accountController.getMainPane(), "Cash added successfully");
                accountController.loadUserProfile();
                Platform.runLater(this::exit);

            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding the bank to the database.");
        }
    }

    private void closeStage(ActionEvent actionEvent) {
        exit();
    }

    private void exit() {
        ((Pane) getParent()).getChildren().remove(this);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
