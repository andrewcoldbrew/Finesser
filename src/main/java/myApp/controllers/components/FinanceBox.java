package myApp.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FinanceBox extends VBox {
    @FXML
    private Label nameLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label dateLabel;

    public FinanceBox(String name, double amount, String category, LocalDate transactionDate) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/financeBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(name, amount, category, transactionDate);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file", e);
        }
    }
    public double getAmount() {
        return Double.parseDouble(amountLabel.getText().replace("$", ""));
    }


    private void initialize(String name, double amount, String category, LocalDate transactionDate) {
        nameLabel.setText(name);
        amountLabel.setText(String.format("$%.2f", amount));
        dateLabel.setText(transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));


        // this.getStyleClass().add("finance-box-" + category.toLowerCase());
    }
}
