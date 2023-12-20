package myApp.controllers.views;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import myApp.controllers.components.DBTransaction;
import myApp.utils.ConnectionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    public VBox transactionContainer;
    public Label totalBalanceBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTransactions();
    }

    private void loadTransactions() {
        Connection con = ConnectionManager.getConnection();
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT name, amount, transactionDate FROM transaction ORDER BY transactionDate DESC LIMIT 5");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String name = rs.getString("name");
                double amount = rs.getDouble("amount");
                LocalDate date = rs.getDate("transactionDate").toLocalDate();
                Platform.runLater(() -> {
                    DBTransaction dbTransaction = new DBTransaction(name, amount, date);
                    transactionContainer.getChildren().add(dbTransaction);
                });

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
