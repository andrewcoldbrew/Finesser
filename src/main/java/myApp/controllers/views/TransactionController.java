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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import myApp.Main;
import myApp.controllers.components.*;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;
import myApp.utils.LocalDateComparator;
import myApp.utils.NotificationCenter;

import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class TransactionController implements Initializable {
    public BorderPane mainPane;
    public MFXTextField searchBar;
    public MFXPaginatedTableView<Transaction> transactionTable;
    public StackPane stackPane;
    public ImageView firstCategoryIcon;
    public ImageView secondCategoryIcon;
    public ImageView thirdCategoryIcon;

    @FXML private Label firstCategoryTotalLabel;
    @FXML private Label secondCategoryTotalLabel;
    @FXML private Label thirdCategoryTotalLabel;
    private AddTransactionForm addForm;
    private UpdateTransactionForm updateForm;

    private final Draggable draggable = new Draggable();

    private final ObservableList<Transaction> transactionData = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactions;

    private static final Map<String, String> icons = Map.of(
            "Clothes", "/images/category/clothes.png",
            "Education", "/images/category/education.png",
            "Entertainment", "/images/category/entertainment.png",
            "Food", "/images/category/food.png",
            "Groceries", "/images/category/groceries.png",
            "Healthcare", "/images/category/healthcare.png",
            "Transportation", "/images/category/transportation.png",
            "Travel", "/images/category/travel.png",
            "Other", "/images/category/other.png"
    );
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new LoadingScreen(stackPane);
        Platform.runLater(() -> {
            loadTransactions();
            filteredTransactions = new FilteredList<>(transactionData, p -> true);
            setupTransactionTable();
            setupSearchBar();
        });
        transactionTable.getSelectionModel().setAllowsMultipleSelection(false);
        transactionTable.autosizeColumnsOnInitialization();

        When.onChanged(transactionTable.currentPageProperty())
                .then((oldValue, newValue) -> transactionTable.autosizeColumns())
                .listen();

    }


    private void setupTransactionTable() {
       // MFXTableColumn<Transaction> idCol = new MFXTableColumn<>("ID", false, Comparator.comparing(Transaction::getTransactionID));
        MFXTableColumn<Transaction> nameCol = new MFXTableColumn<>("Name", true, Comparator.comparing(Transaction::getName));
        MFXTableColumn<Transaction> amountCol = new MFXTableColumn<>("Amount", false, Comparator.comparing(Transaction::getAmount));
        MFXTableColumn<Transaction> descriptionCol = new MFXTableColumn<>("Description", true, Comparator.comparing(Transaction::getDescription));
        MFXTableColumn<Transaction> typeCol = new MFXTableColumn<>("Type", false, Comparator.comparing(Transaction::getCategory));
        MFXTableColumn<Transaction> bankCol = new MFXTableColumn<>("Bank", false, Comparator.comparing(Transaction::getBankName));
        MFXTableColumn<Transaction> dateCol = new MFXTableColumn<>("Date", false, Comparator.comparing(Transaction::getDate, new LocalDateComparator()));
        MFXTableColumn<Transaction> actionCol = new MFXTableColumn<>("Actions");
       // idCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getTransactionID));
        nameCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getName));
        amountCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getAmount));
        descriptionCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getDescription));
        typeCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getCategory));
        bankCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getBankName));
        dateCol.setRowCellFactory(transaction -> new MFXTableRowCell<>(Transaction::getDate));
        actionCol.setRowCellFactory(transaction -> {
            HBox buttonContainer = createButtonContainer(); // Pass the transaction here

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

        Platform.runLater(() -> transactionTable.autosizeColumns());
    }

    public void loadTransactions() {
        transactionData.clear();

        String query = "SELECT t.transactionID, t.name, t.amount, t.description, t.category, COALESCE(b.bankName, 'Cash') AS bankName, t.transactionDate, t.recurrencePeriod\n" +
                "FROM transaction t\n" +
                "LEFT JOIN bank b ON t.bankID = b.bankID\n" +
                "WHERE t.userID = ? AND (b.linked = true OR b.linked IS NULL)\n" +
                "AND t.category NOT IN ('Income', 'Dividend Income', 'Investment', 'Rent', 'Subscription', 'Insurance', 'Bills')\n" +
                "ORDER BY t.transactionDate DESC;";


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
                    calculateTopSpentCategories();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Transactions loaded: " + transactionData.size());

        Platform.runLater(() -> {
            transactionTable.setItems(FXCollections.observableArrayList(transactionData));
            transactionTable.setCurrentPage(1);
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

    private double calculateTotalForCategory(String category) {
        double total = transactionData.stream()
                .filter(tr -> tr.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();

        return total;
    }
    private void calculateTopSpentCategories() {
        Set<String> excludedCategories = new HashSet<>(Arrays.asList("Income", "Dividend Income", "Investment", "Rent", "Subscription", "Insurance", "Bills"));

        Map<String, Double> categoryTotals = transactionData.stream()
                .filter(transaction -> !excludedCategories.contains(transaction.getCategory()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        List<Map.Entry<String, Double>> topCategories = categoryTotals.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            if (topCategories.size() > 0) {
                String category = topCategories.get(0).getKey();
                firstCategoryTotalLabel.setText(category + ": $" + String.format("%.2f", topCategories.get(0).getValue()));
                firstCategoryIcon.setImage(new Image(icons.get(category)));
            } else {
                firstCategoryTotalLabel.setText("N/A");
            }
            if (topCategories.size() > 1) {
                String category = topCategories.get(1).getKey();
                secondCategoryTotalLabel.setText(category + ": $" + String.format("%.2f", topCategories.get(1).getValue()));
                secondCategoryIcon.setImage(new Image(icons.get(category)));
            } else {
                secondCategoryTotalLabel.setText("N/A");
            }
            if (topCategories.size() > 2) {
                String category = topCategories.get(2).getKey();
                thirdCategoryTotalLabel.setText(category + ": $" + String.format("%.2f", topCategories.get(2).getValue()));
                thirdCategoryIcon.setImage(new Image(icons.get(category)));
            } else {
                thirdCategoryTotalLabel.setText("N/A");
            }
        });
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

            Platform.runLater(() -> {
                loadTransactions();
                closeUpdateForm();
                NotificationCenter.successAlert("Transaction Updated!", "Your transaction has been updated successfully");
            });
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
        System.out.println("Transadtion deleted: " + transaction.getName());
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
               if (selectedTransaction.getRecurrencePeriod() != null) {
                   new NewManualAlert(NewManualAlert.Type.WARNING, "Warning!", "You cannot update a finance through the transaction table. Please navigate to the finance page").show();
               } else {
                   openUpdateForm(selectedTransaction);
               }
           }
        });

    }

    private void deleteTransaction() {

        Platform.runLater(() -> {
            Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedValues().getFirst();
            System.out.println(selectedTransaction);
            if (selectedTransaction.getRecurrencePeriod() != null) {
                new NewManualAlert(NewManualAlert.Type.WARNING, "Warning!", "You cannot delete a finance through the transaction table. Please navigate to the finance page").show();
            } else {
                NewManualAlert confirm = new NewManualAlert(NewManualAlert.Type.CONFIRMATION, "Confirm Deletion",
                        "Are you sure you want to delete this budget? This action cannot be revert");

                confirm.setYesAction(() -> {
                    deleteTransactionFromDatabase(selectedTransaction);
                    Platform.runLater(this::loadTransactions);
                });

                confirm.show();
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
        if (!isAddFormOpen()) {
            stackPane.getChildren().add(new AddTransactionForm(this));
        }
    }
    private boolean isAddFormOpen() {
        // Check if a LinkBankForm is already present in mainPane
        for (Node node : stackPane.getChildren()) {
            if (node instanceof AddTransactionForm) {
                return true;
            }
        }
        return false;
    }

    private void openUpdateForm(Transaction transaction) {
        if (!isUpdateFormOpen()) {
            stackPane.getChildren().add(new UpdateTransactionForm(transaction, this));
        }
    }
    private void closeUpdateForm() {
        for (Node node : stackPane.getChildren()) {
            if (node instanceof UpdateTransactionForm) {
                stackPane.getChildren().remove(node);
                break;
            }
        }
    }
    private boolean isUpdateFormOpen() {
        // Check if a LinkBankForm is already present in mainPane
        for (Node node : stackPane.getChildren()) {
            if (node instanceof UpdateTransactionForm) {
                return true;
            }
        }
        return false;
    }

    public StackPane getStackPane() {
        return stackPane;
    }
}
