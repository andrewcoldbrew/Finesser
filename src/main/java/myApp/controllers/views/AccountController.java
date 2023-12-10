package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myApp.Main;
import myApp.controllers.components.AddBudgetForm;
import myApp.controllers.components.AddWalletForm;
import myApp.controllers.components.LinkBankForm;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AccountController implements Initializable {
    private final LinkBankForm linkBankForm = new LinkBankForm();
    private final AddWalletForm addWalletForm = new AddWalletForm();
    private final Stage linkBankDialog = new Stage();
    private final Stage addWalletDialog = new Stage();
    public Label balanceLabel;
    private Scene dialogScene;
    public ImageView profileImage;
    public Label fullNameLabel;
    public Label usernameLabel;
    public Label passwordLabel;
    public FlowPane flowPane;
    public MFXButton linkBankButton;
    private final Connection con = ConnectionManager.getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeLinkBankForm();
        initializeAddWalletForm();
        flowPane.setPadding(new Insets(30, 0, 30, 30));
        Draggable draggable = new Draggable();
        draggable.makeDraggable(linkBankDialog);
        loadUserProfile();
    }


    private void loadUserProfile() {
        int userId = Main.getUserId();

        try (PreparedStatement preparedStatement = con.prepareStatement("SELECT name, password, cashAmount + COALESCE((SELECT SUM(balance) FROM bank WHERE ownerId = userId), 0) AS totalBalance FROM user WHERE userId = ?")) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String password = resultSet.getString("password");
                    double balance = resultSet.getDouble("totalBalance");

                    System.out.println(name);
                    System.out.println(password);
                    System.out.println(balance);

                    usernameLabel.setText("Username: " + name);
                    passwordLabel.setText("Password: " + password);
                    balanceLabel.setText("Balance: " + balance);
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeLinkBankForm() {
        dialogScene = new Scene(linkBankForm, linkBankForm.getPrefWidth(), linkBankForm.getPrefHeight());
        linkBankDialog.setTitle("Link Bank");

        linkBankForm.setStage(linkBankDialog);
        System.out.println(linkBankForm.getStage());

        linkBankDialog.setScene(dialogScene);

        linkBankDialog.initModality(Modality.WINDOW_MODAL);
        linkBankDialog.initStyle(StageStyle.UNDECORATED);
        dialogScene.setFill(Color.TRANSPARENT);

        linkBankDialog.setResizable(false);
    }

    private void initializeAddWalletForm() {
        dialogScene = new Scene(addWalletForm, addWalletForm.getPrefWidth(), addWalletForm.getPrefHeight());
        addWalletDialog.setTitle("Link Bank");

        addWalletForm.setStage(addWalletDialog);
        System.out.println(addWalletForm.getStage());

        addWalletDialog.setScene(dialogScene);

        addWalletDialog.initModality(Modality.WINDOW_MODAL);
        addWalletDialog.initStyle(StageStyle.UNDECORATED);
        dialogScene.setFill(Color.TRANSPARENT);

        addWalletDialog.setResizable(false);
    }

    public void handleLinkBankForm(ActionEvent actionEvent) {
        addWalletDialog.close();
        linkBankDialog.close();
        linkBankDialog.show();
    }

    public void handleAddWalletForm(ActionEvent actionEvent) {
        linkBankDialog.close();
        addWalletDialog.close();
        addWalletDialog.show();
    }
}
