package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jdk.jfr.Event;
import myApp.utils.SortingEvent;
import org.controlsfx.control.RangeSlider;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSortForm extends AnchorPane {

    public MFXFilterComboBox<String> bankComboBox;
    public MFXFilterComboBox<String> categoryComboBox;
    public MFXTextField higherField;
    public MFXTextField lowerField;
    public MFXDatePicker datePicker;
    public MFXButton sortButton;
    public MFXButton exitButton;

    private final ObservableList<String> categoryList = FXCollections.observableArrayList(
            "All", "Food", "Clothes", "Groceries", "Entertainment", "Utilities",
            "Transportation", "Healthcare", "Education", "Travel", "Miscellaneous"
    );
    private final ObservableList<String> bankList = FXCollections.observableArrayList(
            "All", "TPB", "VCB", "ACB", "BIDV", "MB", "Techcombank", "VietinBank", "VPBank", "Eximbank"
    );

    public TransactionSortForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/transactionSortForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private EventHandler<SortingEvent> sortingEventHandler;

    public void setSortingEventHandler(EventHandler<SortingEvent> sortingEventHandler) {
        this.sortingEventHandler = sortingEventHandler;
    }

    public void initialize() {
        bankComboBox.setItems(bankList);
        categoryComboBox.setItems(categoryList);
        sortButton.setOnAction(this::sort);
        exitButton.setOnAction(this::exitForm);
    }


    private void sort(ActionEvent actionEvent) {
        // Check if the text fields are empty before parsing
        Double high = higherField.getText().isEmpty() ? null : Double.valueOf(higherField.getText());
        Double low = lowerField.getText().isEmpty() ? null : Double.valueOf(lowerField.getText());

        LocalDate date = datePicker.getValue();
        String category = categoryComboBox.getSelectedItem();
        String bank = bankComboBox.getSelectedItem();

        System.out.println(high);
        System.out.println(low);
        System.out.println(date);
        System.out.println(category);
        System.out.println(bank);

        String query = buildQuery(high, low, date, category, bank);
        if (!query.equals("SELECT * FROM transactions WHERE ")) {
            System.out.println(query);
            if (sortingEventHandler != null) {
                sortingEventHandler.handle(new SortingEvent(query));
            }
        } else {
            System.out.println("EMPTY QUERY");
        }
    }

    private void exitForm(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }




    public String buildQuery(Double high, Double low, LocalDate date, String category, String bank) {
        // Start with the baseline query
        StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE ");

        // List to store conditions
        List<String> conditions = new ArrayList<>();

        // Check and add conditions based on user inputs
        if (high != null) {
            conditions.add("amount > " + high);
        }

        if (low != null) {
            conditions.add("amount < " + low);
        }

        if (date != null) {
            conditions.add("DATE(transaction_date) = '" + date + "'");
        }

        if (category != null && !category.isEmpty() && !category.equals("All")) {
            conditions.add("category = '" + category + "'");
        }

        if (bank != null && !bank.isEmpty() && !bank.equals("All")) {
            conditions.add("bank = '" + bank + "'");
        }

        // Join conditions with 'AND' only if there are conditions
        if (!conditions.isEmpty()) {
            query.append(String.join(" AND ", conditions));
        }

        return query.toString();
    }
}
