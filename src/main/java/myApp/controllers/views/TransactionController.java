package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import myApp.controllers.components.TransactionSortForm;
import myApp.controllers.components.AddTransactionForm;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class TransactionController implements Initializable {
    public AnchorPane mainPane;
    public ImageView sortButton;
    public MFXTextField searchBar;

    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> nameColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> bankColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;

    // Filter buttons
    @FXML private Label totalFood;
    @FXML private Label totalEntertainment;
    @FXML private Label totalMisc;

    private TransactionSortForm sortForm = new TransactionSortForm();
    private AddTransactionForm addForm = new AddTransactionForm();
    private final Connection con = ConnectionManager.getConnection();
    private final Draggable draggable = new Draggable();

    private final ObservableList<Transaction> transactionData = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactions;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupTransactionTable();
        loadTransactions();

        filteredTransactions = new FilteredList<>(transactionData, p -> true);
        transactionTable.setItems(filteredTransactions);
//        sortForm.setSortingEventHandler(this::handleSortingEvent);
        searchBar.textProperty().addListener((observable, oldValue, newValue) ->
                filteredTransactions.setPredicate(transaction -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    if (transaction.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (transaction.getDescription() != null &&
                            transaction.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (String.valueOf(transaction.getAmount()).toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (transaction.getCategory() != null &&
                            transaction.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (transaction.getBank() != null &&
                            transaction.getBank().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }


                    return false;
                })
        );
        updateTotals();
    }

    private void setupTransactionTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bank"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        transactionTable.setItems(transactionData);
    }

    private void loadTransactions() {
        try (PreparedStatement stmt = con.prepareStatement("SELECT name, amount, description, category, bank, transaction_date FROM transactions");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                double amount = rs.getDouble("amount");
                String description = rs.getString("description");
                String category = rs.getString("category");
                String bank = rs.getString("bank");
                LocalDate date = rs.getDate("transaction_date").toLocalDate();
                Transaction transaction = new Transaction(name, amount, description, category, bank, date);
                transactionData.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterByFood(ActionEvent event) {
        filterTransactions("Food");
    }

    @FXML
    private void filterByEntertainment(ActionEvent event) {
        filterTransactions("Entertainment");
    }

    @FXML
    private void filterByMisc(ActionEvent event) {
        filterTransactions("Miscellaneous");
    }

    private void filterTransactions(String category) {
        filteredTransactions.setPredicate(transaction ->
                category == null || category.isEmpty() || transaction.getCategory().equals(category)
        );
    }
    private void updateTotals() {
        double totalFoodAmount = calculateTotalForCategory("Food");
        double totalEntertainmentAmount = calculateTotalForCategory("Entertainment");
        double totalMiscAmount = calculateTotalForCategory("Miscellaneous");

        //System.out.println("Total Food Amount: " + totalFoodAmount); // Debug
        //System.out.println("Total Entertainment Amount: " + totalEntertainmentAmount); // Debug
        //System.out.println("Total Misc Amount: " + totalMiscAmount); // Debug

        totalFood.setText(String.format("Total: $%.2f", totalFoodAmount));
        totalEntertainment.setText(String.format("Total: $%.2f", totalEntertainmentAmount));
        totalMisc.setText(String.format("Total: $%.2f", totalMiscAmount));
    }

    private double calculateTotalForCategory(String category) {
        double total = transactionData.stream()
                .filter(tr -> tr.getCategory().equals(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
       //System.out.println("Total for " + category + ": " + total);
        return total;
    }

    public void handleAddForm(ActionEvent actionEvent) {

        if (!mainPane.getChildren().contains(addForm)) {
            // Set the TransactionTable to the center of the mainPane
            AnchorPane.setTopAnchor(addForm, (mainPane.getHeight() - addForm.getPrefHeight()) / 2);
            AnchorPane.setLeftAnchor(addForm, (mainPane.getWidth() - addForm.getPrefWidth()) / 2);

            mainPane.getChildren().add(addForm);
            draggable.makeDraggable(addForm);
        }

    }


    public void handleSortForm(MouseEvent mouseEvent) {
        if (!mainPane.getChildren().contains(sortForm)) {
            AnchorPane.setTopAnchor(sortForm, (mainPane.getHeight() - sortForm.getPrefHeight()) / 2);
            AnchorPane.setLeftAnchor(sortForm, (mainPane.getWidth() - sortForm.getPrefWidth()) / 2);

            mainPane.getChildren().add(sortForm);
            draggable.makeDraggable(sortForm);
        }

        // Set the event handler for the SortingEvent
        sortForm.setSortingEventHandler(sortingEvent -> {
            String sortingQuery = sortingEvent.getSortingQuery();
            // Update the data in the transaction table based on the sorting query
            Platform.runLater(() -> {
                // Update the data in the transaction table based on the sorting query
                System.out.println("RECEIVED QUERY: " + sortingQuery);
                try {
                    transactionData.clear();
                    Statement statement = con.createStatement();
                    ResultSet rs = statement.executeQuery(sortingQuery);
                    while (rs.next()) {
                        String name = rs.getString("name");
                        double amount = rs.getDouble("amount");
                        String description = rs.getString("description");
                        String category = rs.getString("category");
                        String bank = rs.getString("bank");
                        LocalDate date = rs.getDate("transaction_date").toLocalDate();
                        Transaction transaction = new Transaction(name, amount, description, category, bank, date);
                        transactionData.add(transaction);
                    }
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        });

    }

}
