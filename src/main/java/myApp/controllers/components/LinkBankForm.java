package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.enums.FloatMode;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import myApp.Main;
import myApp.controllers.views.AccountController;
import myApp.utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static myApp.Main.getUserId;

public class LinkBankForm extends StackPane {
    public StackPane stackPane;
    public MFXFilterComboBox<String> bankComboBox;
    public MFXTextField balanceField;
    public MFXButton linkBankButton;

    public Button exitButton;
    public VBox loadingContainer;
    private final ObservableList<String> bankList = FXCollections.observableArrayList(
            "TPBank", "VietcomBank", "ACBank", "BIDV", "MBBank", "TechcomBank", "VietinBank", "VPBank", "EximBank", "SHBank", "NAB"
    );
    private Stage stage;
    private MFXProgressSpinner spinner;
    private Label balanceLabel;
    private MFXTextField accountKeyField;
    private final AccountController accountController;

    public LinkBankForm(AccountController accountController) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/linkBankForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        this.accountController = accountController;

        try {
            fxmlLoader.load();
            initialize();
            linkBankButton.setDisable(true);
            linkBankButton.setOnAction(this::linkBank);
            exitButton.setOnAction(this::closeStage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void linkBank(ActionEvent actionEvent) {
        String accountKey = getAccountKeyFromDB();
        String inputAccountKey = accountKeyField.getText();
        String bankName = bankComboBox.getValue();
        if (inputAccountKey.equals(accountKey)) {
            Connection con = ConnectionManager.getConnection();
            try {
                PreparedStatement statement = con.prepareStatement("UPDATE bank SET linked = true WHERE ownerID = ? AND bankName = ?");
                statement.setInt(1, Main.getUserId());
                statement.setString(2, bankName);
                statement.execute();
                new SuccessAlert(accountController.getMainPane(), "The bank has been linked successfully!");

                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(event -> {
                    exit();
                    accountController.loadUserProfile();
                    Platform.runLater(accountController::loadCreditCard);

                });
                pause.play();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
//            new ErrorAlert("Wrong Key", "The entered account key is not correct!");
        }
    }

    private void initialize() {
        bankComboBox.setItems(bankList);
        balanceLabel = new Label();
        spinner = new MFXProgressSpinner();
        spinner.setPrefSize(30, 30);
        spinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        bankComboBox.setOnAction(e -> {
            handleBankSelection();
        });
        new Draggable().makeDraggable(this);
    }

    private void handleBankSelection() {
        String selectedBank = bankComboBox.getSelectionModel().getSelectedItem();
        if (selectedBank != null && !selectedBank.isBlank()) {
            showSpinnerAndBalanceLabel(selectedBank);
        }
    }

    private void showSpinnerAndBalanceLabel(String selectedBank) {
        int userId = getUserId();
        loadingContainer.getChildren().clear();
        loadingContainer.getChildren().add(spinner);

        // PauseTransition to delay the spinner for 1.5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(event -> {
            // Remove the spinner after 1.5 seconds and set the label
            loadingContainer.getChildren().add(balanceLabel);
            loadingContainer.getChildren().remove(spinner);

            // Check if the bank is not valid
            double bankBalance = fetchBankBalance(selectedBank, userId);
            if (bankBalance == -1) {
                balanceLabel.setText("You don't have an account linked with this bank.");
                linkBankButton.setDisable(true);
            } else if (bankBalance == -2) {
                balanceLabel.setText("You have already linked with this bank");
                linkBankButton.setDisable(true);
            } else {
                balanceLabel.setText(String.format("Your balance for this bank is $%.2f", bankBalance));
                accountKeyField = createAccountKeyForm();
                loadingContainer.getChildren().add(accountKeyField);
                linkBankButton.setDisable(false);
            }
        });

        pause.play();
    }

    private String getAccountKeyFromDB() {
        Connection con = ConnectionManager.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("SELECT accountKey FROM bank WHERE bankName = ? AND ownerID = ?");
            stmt.setString(1, bankComboBox.getValue());
            stmt.setInt(2, Main.getUserId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getString("accountKey");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    private MFXTextField createAccountKeyForm() {
        MFXTextField textField = new MFXTextField();
        textField.setPrefWidth(200);
        textField.setFloatingText("Enter your bank account key here");
        textField.setFloatMode(FloatMode.INLINE);
        return textField;
    }

    private double fetchBankBalance(String bankName, int userId) {
        Connection con = ConnectionManager.getConnection();
        try {
            // Update the query: use 'name' instead of 'username'
            PreparedStatement statement = con.prepareStatement("SELECT balance, linked FROM bank WHERE ownerID = ? AND bankName = ?");
            statement.setInt(1, userId);
            statement.setString(2, bankName);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                boolean isLinked = rs.getBoolean("linked");
                if (isLinked) {
                    return -2;
                } else {
                    return rs.getDouble("balance");
                }
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }


    private void closeStage(ActionEvent actionEvent) {
        exit();
    }

    private void exit() {
//        if (stage != null) {
//            System.out.println("CLOSING STAGE!");
//            stage.close();
//        } else {
//            System.out.println("STAGE NULL");
//        }
        ((Pane) getParent()).getChildren().remove(this);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
