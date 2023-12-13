package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import myApp.Main;
import myApp.utils.ConnectionManager;
import myApp.utils.HashManager;
import myApp.utils.LoginStageManager;
import myApp.utils.MainAppManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LinkBankForm extends BorderPane {
    public MFXFilterComboBox<String> bankComboBox;
    public MFXTextField balanceField;
    public MFXButton linkBankButton;

    public Button exitButton;
    public VBox loadingContainer;
    private final Connection con = ConnectionManager.getConnection();
    private final ObservableList<String> bankList = FXCollections.observableArrayList(
            "TPB", "VCB", "ACB", "BIDV", "MB", "Techcombank", "VietinBank", "VPBank", "Eximbank"
    );
    private Stage stage;
    private MFXProgressSpinner spinner;
    private Label balanceLabel;


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
        exit();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.25));
        pause.setOnFinished(event -> {
            new SuccessAlert("The bank has been linked successfully!");
        });
        pause.play();


    }



    private void initialize() {
        bankComboBox.setItems(bankList);
        balanceLabel = new Label();
        spinner = new MFXProgressSpinner();
        spinner.setPrefSize(30, 30);
        bankComboBox.setOnAction(e -> {
            handleBankSelection();
        });
    }

    private void handleBankSelection() {
        String selectedBank = bankComboBox.getSelectionModel().getSelectedItem();
        if (selectedBank != null && !selectedBank.isBlank()) {
            showSpinnerAndBalanceLabel(selectedBank);
        }
    }

    private void showSpinnerAndBalanceLabel(String selectedBank) {
        removeExistingBalanceLabel(balanceLabel);
        int userId = Main.getUserId();
        loadingContainer.getChildren().addAll(spinner);

        // PauseTransition to delay the spinner for 1.5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> {
            // Remove the spinner after 1.5 seconds and set the label
            loadingContainer.getChildren().addAll(balanceLabel);
            loadingContainer.getChildren().remove(spinner);

            // Check if the bank is not valid
            double bankBalance = fetchBankBalance(selectedBank, userId);
            if (bankBalance == -1) {
                balanceLabel.setText("You don't have an account linked with this bank.");
                linkBankButton.setDisable(true);
            } else {
                balanceLabel.setText(String.format("Your balance for this bank is $%.2f", bankBalance));
                linkBankButton.setDisable(false);
            }
        });

        pause.play();
    }

    private void removeExistingBalanceLabel(Label balanceLabel) {
        if (loadingContainer.getChildren().contains(balanceLabel)) {
            loadingContainer.getChildren().remove(balanceLabel);
        }
    }

    private double fetchBankBalance(String bankName, int userId) {
        try {
            // Update the query: use 'name' instead of 'username'
            PreparedStatement statement = con.prepareStatement("SELECT balance FROM bank WHERE ownerId = ? AND name = ?");
            statement.setInt(1, userId);
            statement.setString(2, bankName);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
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
