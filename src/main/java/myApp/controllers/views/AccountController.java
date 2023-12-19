package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myApp.Main;
import myApp.controllers.components.AddWalletForm;
import myApp.controllers.components.BankBox;
import myApp.controllers.components.LinkBankForm;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AccountController implements Initializable {
    private final LinkBankForm linkBankForm = new LinkBankForm();
    private final AddWalletForm addWalletForm = new AddWalletForm();
    private final Stage linkBankDialog = new Stage();
    private final Stage addWalletDialog = new Stage();
    public Label balanceLabel;
    public MFXScrollPane scrollPane;
    public GridPane gridPane;
    public Label emailLabel;
    public Label genderLabel;
    public Label dobLabel;
    public Label countryLabel;
    public Label totalBalanceLabel;
    public Label bankBalanceLabel;
    public Label walletBalanceLabel;
    private Scene dialogScene;
    public ImageView profileImage;
    public Label fullNameLabel;
    public MFXButton linkBankButton;
    private final Connection con = ConnectionManager.getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeLinkBankForm();
        initializeAddWalletForm();
        Draggable draggable = new Draggable();
        draggable.makeDraggable(linkBankDialog);


        scrollPane.setOnScroll(event -> {
            if (event.getDeltaX() == 0 && event.getDeltaY() != 0) {
                double speedMultiplier = 2.5; // Adjust this value to change the scrolling speed
                scrollPane.setHvalue(scrollPane.getHvalue() - event.getDeltaY() * speedMultiplier / gridPane.getWidth());
            }
        });

        loadUserProfile();
        loadUserBanks();
    }


    private void loadUserProfile() {
        int userId = Main.getUserId();

        try (PreparedStatement preparedStatement = con.prepareStatement("SELECT fname, lname, email, gender, DOB, country, cashAmount, COALESCE((SELECT SUM(balance) FROM bank WHERE ownerID = ?), 0) AS bankAmount, cashAmount + COALESCE((SELECT SUM(balance) FROM bank WHERE ownerID = ?), 0) AS totalBalance FROM user WHERE userID = ?")) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String fname = resultSet.getString("fname");
                    String lname = resultSet.getString("lname");
                    String email = resultSet.getString("email");
                    String gender = resultSet.getString("gender");
                    LocalDate dob = resultSet.getDate("DOB").toLocalDate();
                    String country = resultSet.getString("country");
                    double cashAmount = resultSet.getDouble("cashAmount");
                    double bankAmount = resultSet.getDouble("bankAmount");
                    double totalBalance = resultSet.getDouble("totalBalance");
                    String fullname = fname + " " + lname;

                    fullNameLabel.setText("Fullname: " + fullname);
                    emailLabel.setText("Email: " + email);
                    genderLabel.setText("Gender: " + gender);
                    dobLabel.setText("Date of Birth: " + dob);
                    countryLabel.setText("Country: " + country);
                    walletBalanceLabel.setText(String.format("Your current wallet: %.2f", cashAmount));
                    bankBalanceLabel.setText(String.format("Your current banks' money: %.2f", bankAmount));
                    totalBalanceLabel.setText(String.format("Your total money: %.2f", totalBalance));
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUserBanks() {
        int userId = Main.getUserId();
        int col = 0;

        try {
            PreparedStatement statement = con.prepareStatement("SELECT u.username, b.bankName, b.accountNumber  FROM user u JOIN bank b ON u.userID = b.ownerID WHERE u.userID = ? AND b.linked = true");
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String bankName = rs.getString("bankName");
                String userName = rs.getString("username");
                String accountNumber = rs.getString("accountNumber");
                BankBox bankBox = new BankBox(bankName, userName, accountNumber);
                gridPane.add(bankBox, col++, 1);
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

    public void handleUpdateInfo(ActionEvent actionEvent) {
    }

    public void handleChangePassword(ActionEvent actionEvent) {
    }

    public void handleAddCash(ActionEvent actionEvent) {
    }
}
