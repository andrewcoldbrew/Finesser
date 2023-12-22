package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.filter.DoubleFilter;
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
import javafx.scene.layout.*;
import myApp.Main;
import myApp.controllers.components.AddTransactionForm;
import myApp.controllers.components.ManualAlert;
import myApp.controllers.components.UpdateTransactionForm;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;
import myApp.utils.LocalDateComparator;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ResourceBundle;


public class TransactionController implements Initializable {
    public BorderPane mainPane;
    public MFXTextField searchBar;
    public MFXPaginatedTableView<Transaction> transactionTable;

    // Filter buttons
    @FXML private Label totalFood;
    @FXML private Label totalEntertainment;
    @FXML private Label totalMisc;
    private AddTransactionForm addForm;
    private UpdateTransactionForm updateForm;

    private final Draggable draggable = new Draggable();

    private final ObservableList<Transaction> transactionData = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadTransactions();
        filteredTransactions = new FilteredList<>(transactionData, p -> true);
        setupTransactionTable();
        setupSearchBar();
        transactionTable.getSelectionModel().setAllowsMultipleSelection(false);
        transactionTable.autosizeColumnsOnInitialization();

        When.onChanged(transactionTable.currentPageProperty())
                .then((oldValue, newValue) -> transactionTable.autosizeColumns())
                .listen();

    }


    private void setupTransactionTable() {
        MFXTableColumn<Transaction> idCol = new MFXTableColumn<>("ID", false, Comparator.comparing(Transaction::getTransactionID));
        MFXTableColumn<Transaction> nameCol = new MFXTableColumn<>("Name", true, Comparator.comparing(Transaction::getName));
        MFXTableColumn<Transaction> amountCol = new MFXTableColumn<>("Amount", false, Comparator.comparing(Transaction::getAmount));
        MFXTableColumn<Transaction> descriptionCol = new MFXTableColumn<>("Description", true, Comparator.comparing(Transaction::getDescription));
        MFXTableColumn<Transaction> typeCol = new MFXTableColumn<>("Type", false, Comparator.comparing(Transaction::getCategory));
        MFXTableColumn<Transaction> bankCol = new MFXTableColumn<>("Bank", false, Comparator.comparing(Transaction::getBankName));
        MFXTableColumn<Transaction> dateCol = new MFXTableColumn<>("Date", false, Comparator.comparing(Transaction::getDate, new LocalDateComparator()));
        MFXTableColumn<Transaction> actionCol = new MFXTableColumn<>("Actions");
        idCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getTransactionID));
        nameCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getName));
        amountCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getAmount));
        descriptionCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getDescription));
        typeCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getCategory));
        bankCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getBankName));
        dateCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getDate));
        actionCol.setRowCellFactory(transaction -> {
            HBox buttonContainer = createButtonContainer(transaction); // Pass the transaction here

            MFXTableRowCell<Transaction, HBox> cell = new MFXTableRowCell<>(value -> buttonContainer, value -> "");
            cell.setGraphic(buttonContainer);
            return cell;
        });

        transactionTable.getTableColumns().addAll(idCol,nameCol, amountCol, descriptionCol, typeCol, bankCol, dateCol, actionCol);
        transactionTable.getFilters().addAll(

                new StringFilter<>("Name", Transaction::getName),
                new DoubleFilter<>("Amount", Transaction::getAmount),
                new StringFilter<>("Description", Transaction::getDescription),
                new StringFilter<>("Category", Transaction::getCategory),
                new StringFilter<>("Bank", Transaction::getBankName)
                );

//        transactionTable.setItems(transactionData);
        transactionTable.autosizeColumnsOnInitialization();
//        transactionTable.setItems(filteredTransactions);
    }

    private void loadTransactions() {

        transactionData.clear();


        String query = "SELECT t.transactionID, t.name, t.amount, t.description, t.category, COALESCE(b.bankName, 'Cash') AS bankName, t.transactionDate, t.recurrencePeriod " +
                "FROM transaction t " +
                "LEFT JOIN bank b ON t.bankID = b.bankID " +
                "WHERE t.userID = ? AND b.linked = true " +
                "ORDER BY t.transactionDate DESC";


        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {


            stmt.setInt(1, Main.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int transactionId = rs.getInt("transactionID");
                    String name = rs.getString("name");
                    double amount = rs.getDouble("amount");
                    String description = rs.getString("description");
                    String category = rs.getString("category");
                    String bankName = rs.getString("bankName");
                    LocalDate date = rs.getDate("transactionDate").toLocalDate();
                    String recurrencePeriod = rs.getString("recurrencePeriod");

                    Transaction transaction = new Transaction(transactionId, name, amount, description, category, bankName, date, recurrencePeriod);
                    transactionData.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Transactions loaded: " + transactionData.size());

        Platform.runLater(() -> {
            transactionTable.setItems(FXCollections.observableArrayList(transactionData));
        });
    }

    private void setupSearchBar() {
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Search text: " + newValue);
            filterTransactions(newValue.trim());
        });
    }


    private void filterTransactions(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredTransactions.setPredicate(null);
        } else {
            filteredTransactions.setPredicate(transaction ->
                    transaction.getName().toLowerCase().contains(searchText.toLowerCase())
            );

        }
        transactionTable.setItems(FXCollections.observableArrayList(filteredTransactions));
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

    private HBox createButtonContainer(Transaction transaction) {
        HBox buttonContainer = new HBox();
        MFXButton updateButton = createButton("Update", "updateButton");
        MFXButton deleteButton = createButton("Delete", "deleteButton");

        updateButton.setOnAction(actionEvent -> updateTransaction());
        deleteButton.setOnAction(actionEvent -> deleteTransaction(transaction));

        buttonContainer.getChildren().addAll(updateButton, deleteButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(20);

        return buttonContainer;
    }


    public void updateTransactionInDatabase(String name, double amount, String description, String category, String bankName, LocalDate transactionDate, int transactionID) {

        int bankID = getBankIdByName(bankName);
        String sql = "UPDATE transaction SET name = ?, amount = ?, description = ?, category = ?, bankID = ?, transactionDate = ? WHERE transactionID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setDouble(2, amount);
            stmt.setString(3, description);
            stmt.setString(4, category);
            stmt.setInt(5, bankID);
            stmt.setDate(6, Date.valueOf(transactionDate));
            stmt.setInt(7, transactionID);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private int getBankIdByName(String bankName) {
        String query = "SELECT bankID FROM bank WHERE bankName = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, bankName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bankID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void deleteTransactionFromDatabase(Transaction transaction) {
        String sql = "DELETE FROM transaction WHERE transactionID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getTransactionID());
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Deleted rows: " + affectedRows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTransaction() {
        Platform.runLater(() -> {

           Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedValues().getFirst();

           if (!mainPane.getChildren().contains(updateForm)) {
               updateForm = new UpdateTransactionForm(selectedTransaction, this);
               AnchorPane.setTopAnchor(updateForm, (mainPane.getHeight() - updateForm.getPrefHeight()) / 2);
               AnchorPane.setLeftAnchor(updateForm, (mainPane.getWidth() - updateForm.getPrefWidth()) / 2);
               mainPane.getChildren().add(updateForm);
               draggable.makeDraggable(updateForm);
           }
        });

    }

    private void deleteTransaction(Transaction transaction) {
        ManualAlert confirm = new ManualAlert(Alert.AlertType.CONFIRMATION, "Confirm Deletion",
                "Are you sure you want to delete this budget?",
                "This action cannot be revert!");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                deleteTransactionFromDatabase(transaction);
                loadTransactions();
            }
        });
    }

    private MFXButton createButton(String text, String id) {
        MFXButton button = new MFXButton(text);
        button.setOnAction(actionEvent -> {});
        button.setId(id);
        return button;
    }

    public void handleAddTransactionForm(ActionEvent actionEvent) {
        if (!mainPane.getChildren().contains(addForm)) {
            addForm = new AddTransactionForm();
            AnchorPane.setTopAnchor(addForm, (mainPane.getHeight() - addForm.getPrefHeight()) / 2);
            AnchorPane.setLeftAnchor(addForm, (mainPane.getWidth() - addForm.getPrefWidth()) / 2);

            mainPane.getChildren().add(addForm);
            draggable.makeDraggable(addForm);
        }

    }



}
