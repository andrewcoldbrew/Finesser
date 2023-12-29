package myApp.controllers.views;

import animatefx.animation.*;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import myApp.Main;
import myApp.controllers.components.*;
import myApp.models.Transaction;
import myApp.models.User;
import myApp.utils.Animate;
import myApp.utils.ConnectionManager;


import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class AccountController implements Initializable {
    public HBox creditCardContainer;
    public Button leftButton;
    public Button rightButton;
    public HBox paginationContainer;
    public BorderPane creditCardWrapper;
    public StackPane stackPane;
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
    private Label noBankLabel;

    private static final String IMAGE_SAVE_DIRECTORY = "src/main/resources/images/profiles";
    private Preferences prefs;
    public AccountController() {
        prefs = Preferences.userNodeForPackage(AccountController.class);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new LoadingScreen(stackPane);
        loadUserProfile();
        loadCreditCard();
        loadProfilePicture();
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

    public void updateInfoInDatabase(String fname, String lname, String email, String gender, LocalDate dob, String country) {
        String sql = "UPDATE user SET fname = ?, lname = ?, email = ?, gender = ?, DOB = ?, country = ? WHERE userID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setString(3, email);
            stmt.setString(4, gender);
            stmt.setDate(5, Date.valueOf(dob));
            stmt.setString(6, country);
            stmt.setInt(7, Main.getUserId());
            stmt.executeUpdate();

            closeUpdateInfoForm();

            Platform.runLater(() -> {
                loadUserProfile();
                new SuccessAlert(stackPane, "Finance successfully updated!");
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private User getUserFromDatabase() {
        int userId = Main.getUserId();
        Connection con = ConnectionManager.getConnection();
        try (PreparedStatement preparedStatement = con.prepareStatement("SELECT fname, lname, email, gender, DOB, country FROM user WHERE userID = ?")) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String fname = resultSet.getString("fname");
                    String lname = resultSet.getString("lname");
                    String email = resultSet.getString("email");
                    String gender = resultSet.getString("gender");
                    LocalDate dob = resultSet.getDate("DOB").toLocalDate();
                    String country = resultSet.getString("country");
                    User user = new User(userId, fname, lname, email, gender, dob, country);
                    return user;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void openUpdateInfoForm(ActionEvent actionEvent) {
        if (!isUpdateFormOpen()) {
            stackPane.getChildren().add(new UpdateInfoForm(this, getUserFromDatabase()));
        }
    }

    private void closeUpdateInfoForm() {
        for (Node node : stackPane.getChildren()) {
            if (node instanceof UpdateInfoForm) {
                stackPane.getChildren().remove(node);
                break;
            }
        }
    }

    private boolean isUpdateFormOpen() {
        // Check if a LinkBankForm is already present in mainPane
        for (Node node : stackPane.getChildren()) {
            if (node instanceof UpdateFinanceForm) {
                return true;
            }
        }
        return false;
    }


    public void handleLinkBankForm(ActionEvent actionEvent) {
        // Check if a LinkBankForm is already present
        if (!isLinkBankFormOpen()) {
            stackPane.getChildren().add(new LinkBankForm(this));
        }
    }

    public void handleAddWalletForm(ActionEvent actionEvent) {
        // Check if an AddWalletForm is already present
        if (!isAddWalletFormOpen()) {
            stackPane.getChildren().add(new AddWalletForm(this));
        }
    }

    private boolean isLinkBankFormOpen() {
        // Check if a LinkBankForm is already present in mainPane
        for (Node node : stackPane.getChildren()) {
            if (node instanceof LinkBankForm) {
                return true;
            }
        }
        return false;
    }

    private boolean isAddWalletFormOpen() {
        // Check if an AddWalletForm is already present in mainPane
        for (Node node : stackPane.getChildren()) {
            if (node instanceof AddWalletForm) {
                return true;
            }
        }
        return false;
    }
    private void loadProfilePicture() {
        String imagePath = getUserProfileImagePath(Main.getUserId());
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                profileImage.setImage(image);
            }
        }
    }

    public void handleUploadProfilePicture(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(getWindow());

        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            profileImage.setImage(selectedImage);

            saveUserProfileImage(Main.getUserId(), selectedFile);
        }
    }

    private void saveUserProfileImage(int userId, File sourceFile) {
        String storageDirectory = IMAGE_SAVE_DIRECTORY;

        File destinationDir = new File(storageDirectory);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        String destinationPath = storageDirectory + File.separator + "user_" + userId + ".png";

        try {
            Files.copy(sourceFile.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateUserProfileImagePath(userId, destinationPath);
    }
    private String getUserProfileImagePath(int userId) {
        String imagePath = null;
        String query = "SELECT profileImagePath FROM user WHERE userID = ?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                imagePath = rs.getString("profileImagePath");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private void updateUserProfileImagePath(int userId, String imagePath) {
        String query = "UPDATE user SET profileImagePath = ? WHERE userID = ?";

        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, imagePath);
            pst.setInt(2, userId);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Window getWindow() {
        return profileImage.getScene().getWindow();
    }

    public void handleChangePassword(ActionEvent actionEvent) {
    }

    public StackPane getStackPane() {
        return stackPane;
    }
}
