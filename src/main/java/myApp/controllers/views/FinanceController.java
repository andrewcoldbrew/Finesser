package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import myApp.controllers.components.AddFinanceForm;
import myApp.controllers.components.FinanceBox;
import myApp.models.Transaction;
import myApp.utils.ConnectionManager;
import myApp.utils.Draggable;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class FinanceController implements Initializable {
    public MFXButton addButton;
    public FlowPane incomeFlowPane;
    public FlowPane outcomeFlowPane;
    public Label totalLabel;

    private final AddFinanceForm addFinanceForm = new AddFinanceForm();
    private final Stage dialogStage = new Stage();
    private final Scene dialogScene = new Scene(addFinanceForm, addFinanceForm.getPrefWidth(), addFinanceForm.getPrefHeight());
    private Connection con = ConnectionManager.getConnection();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeAddFinanceForm();
        Draggable draggable = new Draggable();
        draggable.makeDraggable(dialogStage);
        loadIncome();

    }

    private void initializeAddFinanceForm() {
        dialogStage.setTitle("Add Finance Form");

        addFinanceForm.setStage(dialogStage);
        System.out.println("STAGE: " + addFinanceForm.getStage());

        dialogStage.setScene(dialogScene);

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogScene.setFill(Color.TRANSPARENT);

        dialogStage.setResizable(false);
    }

    private void loadIncome() {
        try (PreparedStatement stmt = con.prepareStatement("SELECT name, amount, time_duration, time_type, start_date, type FROM finance");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                double amount = rs.getDouble("amount");
                int timeDuration = rs.getInt("time_duration");
                String timeType = rs.getString("time_type");
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                String type = rs.getString("type");
                if (type.equals("Income")) {
                    FinanceBox financeBox = new FinanceBox(name, amount, startDate, timeDuration, timeType, true);
                    incomeFlowPane.getChildren().add(financeBox);
                } else if (type.equals("Outcome")) {
                    FinanceBox financeBox = new FinanceBox(name, amount, startDate, timeDuration, timeType, false);
                    outcomeFlowPane.getChildren().add(financeBox);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadOutcome() {

    }


    public void handleAddFinanceForm(ActionEvent actionEvent) {
        addFinanceForm.clearData();
        dialogStage.hide();
        dialogStage.show();
    }
}
