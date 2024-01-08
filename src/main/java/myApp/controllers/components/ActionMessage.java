package myApp.controllers.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import myApp.Main;
import myApp.utils.ConnectionManager;
import myApp.utils.MainAppManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ActionMessage extends VBox {
    public Button aboutButton;
    public Button spentButton;
    public Button commandButton;
    public ChatScreen chatScreen;
    public ActionMessage(ChatScreen chatScreen) {
        this.chatScreen = chatScreen;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/actionMessage.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            aboutButton.setOnAction(this::responseAbout);
            spentButton.setOnAction(this::responseSpent);
            commandButton.setOnAction(this::responseCommand);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void responseCommand(ActionEvent actionEvent) {
        chatScreen.addBotResponse("help");
    }

    private void responseSpent(ActionEvent actionEvent) {
        double spent = getUserSpending();
        chatScreen.customResponse("How much have i spent?", "Up until now, you have spent: $" + spent);
    }

    private void responseAbout(ActionEvent actionEvent) {
        chatScreen.addBotResponse("guide me");
    }

    private double getUserSpending() {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SUM(amount) AS total FROM transaction WHERE userID = ? AND category NOT IN ('Income', 'Dividend Income', 'Investment')")) {

            stmt.setInt(1, Main.getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
