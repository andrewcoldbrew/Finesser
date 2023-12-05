package myApp.controllers.components;

import io.github.palexdev.materialfx.beans.NumberRange;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FinanceBox extends HBox {

    public FinanceBox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/financeBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
