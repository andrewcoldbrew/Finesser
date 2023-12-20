package myApp.controllers.views;

import animatefx.animation.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TestAccountController implements Initializable {
    public HBox creditCardContainer;
    public Button leftButton;
    public Button rightButton;
    public HBox paginationContainer;
    private LinkBankForm linkBankForm;
    private AddWalletForm addWalletForm;
    private final Stage linkBankDialog = new Stage();
    private final Stage addWalletDialog = new Stage();
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
    private List<Button> paginationList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCreditCard();
        displayCreditCard(0);
        rightButton.setOnAction(this::moveToRightCard);
        leftButton.setOnAction(this::moveToLeftCard);
        Animate.addHoverScalingEffect(leftButton, 1.1);
        Animate.addHoverScalingEffect(rightButton, 1.1);
    }

    private void loadCreditCard() {
        creditCardList = new ArrayList<>();
        int userId = Main.getUserId();

        Connection con = ConnectionManager.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT u.username, b.bankName, b.accountNumber  FROM user u JOIN bank b ON u.userID = b.ownerID WHERE u.userID = ? AND b.linked = true");
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String bankName = rs.getString("bankName");
                String userName = rs.getString("username");
                String accountNumber = rs.getString("accountNumber");
                BankBox bankBox = new BankBox(bankName, userName, accountNumber);
                creditCardList.add(bankBox);
            }
            displayCreditCard(0);
            loadPagination();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayCreditCard(int index) {
        creditCardContainer.getChildren().clear();
        creditCardContainer.getChildren().add(creditCardList.get(index));
        new Pulse(getCurentCard().getAccountNumberLabel()).play();
        new Pulse(getCurentCard().getBankNameLabel()).play();
        new Pulse(getCurentCard().getUserNameLabel()).play();
        new GlowText(getCurentCard().getUserNameLabel(), Color.WHITE, Color.DARKGOLDENROD).play();
        new GlowText(getCurentCard().getAccountNumberLabel(), Color.WHITE, Color.DARKGOLDENROD).play();
        new GlowText(getCurentCard().getBankNameLabel(), Color.WHITE, Color.DARKGOLDENROD).play();
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
        int size = creditCardList.size();
        for (int i = 0; i < size; i++) {
            Button paginationButton = createPagination();
            paginationContainer.getChildren().add(paginationButton);
            final int index = i; // Use final keyword here

            // Add event handler to update pagination on button click
            paginationButton.setOnAction(event -> displayCreditCard(index));
            paginationButton.getStyleClass().add("paginationButton");
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

    public void handleUpdateInfo(ActionEvent actionEvent) {
    }

    public void handleChangePassword(ActionEvent actionEvent) {
    }

    public void handleAddWalletForm(ActionEvent actionEvent) {
    }

    public void handleLinkBankForm(ActionEvent actionEvent) {
    }
}
