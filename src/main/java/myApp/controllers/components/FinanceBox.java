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

    public FinanceBox(String name, double amount, LocalDate startDate, int timeDuration, String timeType, boolean isIncome) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/financeBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            if (isIncome) {
                initialize(name, amount, startDate, timeDuration, timeType, true);
            } else {
                initialize(name, amount, startDate, timeDuration, timeType, false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(String name, double amount, LocalDate startDate, int timeDuration, String timeType, boolean isIncome) {
        nameField.setText("Name: " + name);
        startDateField.setText(String.format("Start at: %s", startDate));
        timeDurationField.setText(String.format("Every %d %s", timeDuration, timeType));
        if (isIncome) {
            amountField.setText(String.format("Amount: %.2f", amount));
        } else {
            amountField.setText(String.format("Amount: %.2f", -amount));
        }
    }
}
