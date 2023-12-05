package myApp.controllers.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.LocalDate;

public class FinanceBox extends HBox {
    public Label nameField;
    public Label amountField;
    public Label startDateField;
    public Label timeDurationField;

    public FinanceBox(String name, double amount, LocalDate startDate, int timeDuration, String timeType) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/financeBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize(name, amount, startDate, timeDuration, timeType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String name, double amount, LocalDate startDate, int timeDuration, String timeType) {
        nameField.setText("Name: " + name);
        amountField.setText(String.format("Amount: %.2f", amount));
        startDateField.setText(String.format("Start at: %s", startDate));
        timeDurationField.setText(String.format("Every %d %s", timeDuration, timeType));
    }
}
