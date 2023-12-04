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
import myApp.utils.Draggable;

import java.net.URL;
import java.util.ResourceBundle;

public class FinanceController implements Initializable {
    public MFXButton addButton;
    public FlowPane incomeFlowPane;
    public FlowPane outcomeFlowPane;
    public Label totalLabel;

    private final AddFinanceForm addFinanceForm = new AddFinanceForm();
    private final Stage dialogStage = new Stage();
    private final Scene dialogScene = new Scene(addFinanceForm, addFinanceForm.getPrefWidth(), addFinanceForm.getPrefHeight());
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeAddFinanceForm();
        Draggable draggable = new Draggable();
        draggable.makeDraggable(dialogStage);
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

    public void handleAddFinanceForm(ActionEvent actionEvent) {
        dialogStage.hide();
        dialogStage.show();
    }
}
