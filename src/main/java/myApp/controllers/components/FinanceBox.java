package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.enums.ButtonType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FinanceBox extends HBox {

    public Label nameLabel;
    public Label categoryLabel;
    public Label amountLabel;
    public Label dateLabel;
    public MFXButton updateButton;
    public MFXButton deleteButton;
    public ImageView editIcon;
    public ImageView trashIcon;

    public FinanceBox(String name, double amount, String category, LocalDate transactionDate) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/financeBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(name, amount, category, transactionDate);

            updateButton.setOnAction(this::updateFinace);
            deleteButton.setOnAction(this::deleteFinance);
            // Animate button
            updateButton.setOnMouseEntered(this::animateUpdate);
            updateButton.setOnMouseExited(this::staticUpdate);
            deleteButton.setOnMouseEntered(this::animateDelete);
            deleteButton.setOnMouseExited(this::staticDelete);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file", e);
        }
    }

    private void deleteFinance(ActionEvent actionEvent) {

    }

    private void updateFinace(ActionEvent actionEvent) {
        
    }

    public double getAmount() {
        return Double.parseDouble(amountLabel.getText().replace("Amount: $", ""));
    }


    private void initialize(String name, double amount, String category, LocalDate transactionDate) {
        nameLabel.setText(name);
        amountLabel.setText(String.format("Amount: $%.2f", amount));
        categoryLabel.setText(category);
        dateLabel.setText(String.format("Date: %s", transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        // this.getStyleClass().add("finance-box-" + category.toLowerCase());
    }

    private void staticDelete(MouseEvent mouseEvent) {
        deleteButton.setButtonType(ButtonType.FLAT);
        trashIcon.setImage(new Image("/images/budget/trash.png"));
    }

    private void animateDelete(MouseEvent mouseEvent) {
        deleteButton.setButtonType(ButtonType.RAISED);
        deleteButton.setDepthLevel(DepthLevel.LEVEL2);
        trashIcon.setImage(new Image("/images/gif/trash.gif"));
    }

    private void staticUpdate(MouseEvent mouseEvent) {
        updateButton.setButtonType(ButtonType.FLAT);
        editIcon.setImage(new Image("/images/budget/edit.png"));
    }

    private void animateUpdate(MouseEvent mouseEvent) {
        updateButton.setButtonType(ButtonType.RAISED);
        updateButton.setDepthLevel(DepthLevel.LEVEL2);
        editIcon.setImage(new Image("/images/gif/edit.gif"));
    }
}
