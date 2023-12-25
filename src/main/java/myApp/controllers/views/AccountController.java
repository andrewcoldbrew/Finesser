package myApp.controllers.views;

import animatefx.animation.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myApp.Main;
import myApp.controllers.components.AddWalletForm;
import myApp.controllers.components.BankBox;
import myApp.controllers.components.LinkBankForm;
import myApp.utils.Animate;
import myApp.utils.ConnectionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AccountController implements Initializable {
    public HBox creditCardContainer;
    public Button leftButton;
    public Button rightButton;
    public HBox paginationContainer;
    public BorderPane creditCardWrapper;
    public StackPane mainPane;
    private Stage linkBankDialog;
    private Stage addWalletDialog;
    public Label emailLabel;
    public Label genderLabel;
    public Label dobLabel;
    public Label countryLabel;
    public Label totalBalanceLabel;
    public Label bankBalanceLabel;
    public Label walletBalanceLabel;
    public ImageView profileImage;
    public Label fullNameLabel;
    public MFXButton linkBankButton;

    private List<BankBox> creditCardList;
    private Scene dialogScene;
    private List<Button> paginationList;
    private Label noBankLabel;

    public AccountController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserProfile();
        loadCreditCard();
        rightButton.setOnAction(this::moveToRightCard);
        leftButton.setOnAction(this::moveToLeftCard);
        Animate.addHoverScalingEffect(leftButton, 1.1);
        Animate.addHoverScalingEffect(rightButton, 1.1);
    }

    public void loadCreditCard() {
        creditCardList = new ArrayList<>();
        int userId = Main.getUserId();

        Connection con = ConnectionManager.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT u.fname, u.lname, b.bankName, b.accountNumber  FROM user u JOIN bank b ON u.userID = b.ownerID WHERE u.userID = ? AND b.linked = true");
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String bankName = rs.getString("bankName");
                String fname = rs.getString("fname");
                String lname = rs.getString("lname");
                String accountNumber = rs.getString("accountNumber");
                String fullname = fname + " " + lname;
                BankBox bankBox = new BankBox(bankName, fullname, accountNumber);
                creditCardList.add(bankBox);
            }

            System.out.println(creditCardList);

            if (creditCardList.isEmpty()) {
                noBankLabel = new Label("You haven't connect to any banks");
                noBankLabel.setPrefWidth(300);
                noBankLabel.setFont(new Font(30));
                noBankLabel.setWrapText(true);
                noBankLabel.setTextAlignment(TextAlignment.CENTER);
                creditCardWrapper.setCenter(noBankLabel);
                leftButton.setVisible(false);
                rightButton.setVisible(false);
            } else {
                creditCardWrapper.setCenter(creditCardContainer);
                leftButton.setVisible(true);
                rightButton.setVisible(true);
                displayCreditCard(0);
                loadPagination();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayCreditCard(int index) {
        creditCardContainer.getChildren().clear();
        creditCardContainer.getChildren().add(creditCardList.get(index));
        new Pulse(getCurentCard().accountNumberLabel).play();
        new Pulse(getCurentCard().bankNameLabel).play();
        new Pulse(getCurentCard().fullNameLabel).play();
        new GlowText(getCurentCard().fullNameLabel, Color.WHITE, Color.GOLDENROD).play();
        new GlowText(getCurentCard().accountNumberLabel, Color.WHITE, Color.GOLDENROD).play();
        new GlowText(getCurentCard().bankNameLabel, Color.WHITE, Color.GOLDENROD).play();
        // Update pagination buttons
        updatePagination(index);
    }

    private int getCurrentCardIndex() {
        BankBox currentCard = (BankBox) creditCardContainer.getChildren().getFirst();
        return creditCardList.indexOf(currentCard);
    }

    private BankBox getCurentCard() {
        return (BankBox) creditCardContainer.getChildren().getFirst();
    }

    private void moveToLeftCard(ActionEvent actionEvent) {
        int currentIndex = getCurrentCardIndex();
        if (currentIndex != 0) {
            displayCreditCard(currentIndex - 1);
        }
    }

    private void moveToRightCard(ActionEvent actionEvent) {
        int currentIndex = getCurrentCardIndex();
        if (currentIndex != creditCardList.size() - 1) {
            displayCreditCard(currentIndex + 1);
        }
    }


    private void loadPagination() {
        paginationContainer.getChildren().clear();
        int size = creditCardList.size();
        for (int i = 0; i < size; i++) {
            Button paginationButton = createPagination();
            paginationContainer.getChildren().add(paginationButton);
            final int index = i; // Use final keyword here

            // Add event handler to update pagination on button click
            paginationButton.setOnAction(event -> displayCreditCard(index));
            // Initially set the first pagination button as selected
            if (i == 0) {
                setPaginationSelected(paginationButton);
            }
        }
    }

    private Button createPagination() {
        ImageView image = new ImageView("/images/account/unselect.png");
        image.setFitWidth(20);
        image.setFitHeight(20);

        Button button = new Button();
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setGraphic(image);
        button.setBackground(Background.EMPTY);
        return button;
    }

    private void updatePagination(int selectedIndex) {
        for (Node node : paginationContainer.getChildren()) {
            Button paginationButton = (Button) node;
            int currentIndex = paginationContainer.getChildren().indexOf(paginationButton);

            // Set the selected image for the selected pagination button
            if (currentIndex == selectedIndex) {
                setPaginationSelected(paginationButton);
            } else {
                setPaginationUnselected(paginationButton);
            }
        }
    }

    private void setPaginationSelected(Button button) {
        ((ImageView) button.getGraphic()).setImage(new Image("/images/account/selected.png"));
    }

    private void setPaginationUnselected(Button button) {
        ((ImageView) button.getGraphic()).setImage(new Image("/images/account/unselect.png"));
    }

    public void loadUserProfile() {
        int userId = Main.getUserId();
        Connection con = ConnectionManager.getConnection();
        try (PreparedStatement preparedStatement = con.prepareStatement("SELECT fname, lname, email, gender, DOB, country, cashAmount, COALESCE((SELECT SUM(balance) FROM bank WHERE ownerID = ? AND linked = true), 0) AS bankAmount, cashAmount + COALESCE((SELECT SUM(balance) FROM bank WHERE ownerID = ? AND linked = true), 0) AS totalBalance FROM user WHERE userID = ?")) {
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

                    fullNameLabel.setText(fullname);
                    emailLabel.setText(email);
                    genderLabel.setText(gender);
                    dobLabel.setText(String.valueOf(dob));
                    countryLabel.setText(country);
                    walletBalanceLabel.setText(String.format("Your current wallet balance: %.2f", cashAmount));
                    bankBalanceLabel.setText(String.format("Your current bank balance: %.2f", bankAmount));
                    totalBalanceLabel.setText(String.format("Your total balance: %.2f", totalBalance));
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


//    private void initializeLinkBankForm() {
//        linkBankDialog = new Stage(StageStyle.UNDECORATED);
//
//        LinkBankForm linkBankForm = new LinkBankForm(this);
//        dialogScene = new Scene(linkBankForm, linkBankForm.getPrefWidth(), linkBankForm.getPrefHeight());
//        linkBankDialog.setTitle("Link Bank");
//
//        linkBankForm.setStage(linkBankDialog);
//        System.out.println(linkBankForm.getStage());
//
//        linkBankDialog.setScene(dialogScene);
//
//        linkBankDialog.initModality(Modality.WINDOW_MODAL);
//        dialogScene.setFill(Color.TRANSPARENT);
//
//        linkBankDialog.setResizable(false);
//        linkBankDialog.show();
//    }
//
//    private void initializeAddWalletForm() {
//        addWalletDialog = new Stage(StageStyle.UNDECORATED);
//        AddWalletForm addWalletForm = new AddWalletForm();
//        dialogScene = new Scene(addWalletForm, addWalletForm.getPrefWidth(), addWalletForm.getPrefHeight());
//
//        addWalletDialog.setTitle("Link Bank");
//
//        addWalletForm.setStage(addWalletDialog);
//        System.out.println(addWalletForm.getStage());
//
//        addWalletDialog.setScene(dialogScene);
//
//        addWalletDialog.initModality(Modality.APPLICATION_MODAL);
//        addWalletDialog.initStyle(StageStyle.UNDECORATED);
//        addWalletDialog.setResizable(true);
//        dialogScene.setFill(Color.TRANSPARENT);
//
//        addWalletDialog.setResizable(false);
//        addWalletDialog.show();
//    }

    public void handleLinkBankForm(ActionEvent actionEvent) {
        // Check if a LinkBankForm is already present
        if (!isLinkBankFormOpen()) {
            mainPane.getChildren().add(new LinkBankForm(this));
        }
    }

    public void handleAddWalletForm(ActionEvent actionEvent) {
        // Check if an AddWalletForm is already present
        if (!isAddWalletFormOpen()) {
            mainPane.getChildren().add(new AddWalletForm(this));
        }
    }

    private boolean isLinkBankFormOpen() {
        // Check if a LinkBankForm is already present in mainPane
        for (Node node : mainPane.getChildren()) {
            if (node instanceof LinkBankForm) {
                return true;
            }
        }
        return false;
    }

    private boolean isAddWalletFormOpen() {
        // Check if an AddWalletForm is already present in mainPane
        for (Node node : mainPane.getChildren()) {
            if (node instanceof AddWalletForm) {
                return true;
            }
        }
        return false;
    }

    public void handleUpdateInfo(ActionEvent actionEvent) {
    }

    public void handleChangePassword(ActionEvent actionEvent) {
    }

    public StackPane getMainPane() {
        return mainPane;
    }
}
