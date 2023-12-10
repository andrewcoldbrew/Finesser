package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.DoubleFilter;
import io.github.palexdev.materialfx.filter.EnumFilter;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.utils.others.observables.When;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import myApp.Main;
import myApp.controllers.components.TransactionSortForm;
import myApp.controllers.components.AddTransactionForm;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;
import myApp.utils.LocalDateComparator;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;


public class TestTransactionController implements Initializable {
    public BorderPane mainPane;
    public MFXTextField searchBar;
    public MFXPaginatedTableView<Transaction> transactionTable;

    // Filter buttons
    @FXML private Label totalFood;
    @FXML private Label totalEntertainment;
    @FXML private Label totalMisc;
    private AddTransactionForm addForm = new AddTransactionForm();
    private final Connection con = ConnectionManager.getConnection();
    private final Draggable draggable = new Draggable();

    private final ObservableList<Transaction> transactionData = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactions;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        updateTotals();
        loadTransactions();
        setupTransactionTable();
        transactionTable.autosizeColumnsOnInitialization();

        When.onChanged(transactionTable.currentPageProperty())
                .then((oldValue, newValue) -> transactionTable.autosizeColumns())
                .listen();
    }


    private void setupTransactionTable() {
        MFXTableColumn<Transaction> nameCol = new MFXTableColumn<>("Name", true, Comparator.comparing(Transaction::getName));
        MFXTableColumn<Transaction> amountCol = new MFXTableColumn<>("Amount", false, Comparator.comparing(Transaction::getAmount));
        MFXTableColumn<Transaction> descriptionCol = new MFXTableColumn<>("Description", true, Comparator.comparing(Transaction::getDescription));
        MFXTableColumn<Transaction> typeCol = new MFXTableColumn<>("Type", false, Comparator.comparing(Transaction::getCategory));
        MFXTableColumn<Transaction> bankCol = new MFXTableColumn<>("Bank", false, Comparator.comparing(Transaction::getBankName));
        MFXTableColumn<Transaction> dateCol = new MFXTableColumn<>("Date", false, Comparator.comparing(Transaction::getDate, new LocalDateComparator()));
        MFXTableColumn<Transaction> actionCol = new MFXTableColumn<>("Actions");
        nameCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getName));
        amountCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getAmount));
        descriptionCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getDescription));
        typeCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getCategory));
        bankCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getBankName));
        dateCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getDate));
        actionCol.setRowCellFactory(transaction -> {
            HBox buttonContainer = createButtonContainer();

            MFXTableRowCell<Transaction, HBox> cell = new MFXTableRowCell<>(value -> buttonContainer, value -> "");
            cell.setGraphic(buttonContainer);
            return cell;
        });

        transactionTable.getTableColumns().addAll(nameCol, amountCol, descriptionCol, typeCol, bankCol, dateCol, actionCol);
        transactionTable.getFilters().addAll(
                new StringFilter<>("Name", Transaction::getName),
                new DoubleFilter<>("Amount", Transaction::getAmount),
                new StringFilter<>("Description", Transaction::getDescription),
                new StringFilter<>("Category", Transaction::getCategory),
                new StringFilter<>("Bank", Transaction::getBankName)
                );
        transactionTable.setItems(transactionData);
        transactionTable.autosizeColumnsOnInitialization();
    }

    private void loadTransactions() {
        int userId = Main.getUserId(); // Get the logged-in user's ID

        String query = "SELECT t.name, t.amount, t.description, t.category, COALESCE(b.name, 'Cash') AS bankName, t.transaction_date " +
                "FROM transaction t " +
                "LEFT JOIN bank b ON t.bankId = b.bankId " +
                "WHERE t.userId = ? " +
                "ORDER BY b.bankId DESC";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String name = rs.getString("name");
                    double amount = rs.getDouble("amount");
                    String description = rs.getString("description");
                    String category = rs.getString("category");
                    String bankName = rs.getString("bankName");
                    LocalDate date = rs.getDate("transaction_date").toLocalDate();

                    Transaction transaction = new Transaction(name, amount, description, category, bankName, date);
                    transactionData.add(transaction);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotals() {
        double totalFoodAmount = calculateTotalForCategory("Food");
        double totalEntertainmentAmount = calculateTotalForCategory("Entertainment");
        double totalMiscAmount = calculateTotalForCategory("Miscellaneous");

        totalFood.setText(String.format("Total: $%.2f", totalFoodAmount));
        totalEntertainment.setText(String.format("Total: $%.2f", totalEntertainmentAmount));
        totalMisc.setText(String.format("Total: $%.2f", totalMiscAmount));
    }

    private double calculateTotalForCategory(String category) {
        double total = transactionData.stream()
                .filter(tr -> tr.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();

        return total;
    }

    public void handleAddForm(ActionEvent actionEvent) {

        if (!mainPane.getChildren().contains(addForm)) {
            AnchorPane.setTopAnchor(addForm, (mainPane.getHeight() - addForm.getPrefHeight()) / 2);
            AnchorPane.setLeftAnchor(addForm, (mainPane.getWidth() - addForm.getPrefWidth()) / 2);

            mainPane.getChildren().add(addForm);
            draggable.makeDraggable(addForm);
        }
    }

    private HBox createButtonContainer() {
        HBox buttonContainer = new HBox();

        MFXButton updateButton = createButton("Update", "updateButton");
        MFXButton deleteButton = createButton("Delete", "deleteButton");

        updateButton.setOnAction(actionEvent -> updateTransaction());
        deleteButton.setOnAction(actionEvent -> deleteTransaction());

        buttonContainer.getChildren().addAll(updateButton, deleteButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(20);

        return buttonContainer;
    }

    private void deleteTransaction() {
    }

    private void updateTransaction() {
    }

    private MFXButton createButton(String text, String id) {
        MFXButton button = new MFXButton(text);
        button.setOnAction(actionEvent -> {});
        button.setId(id);
        return button;
    }



}
