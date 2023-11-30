package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import myApp.controllers.components.AddBudgetForm;
import myApp.controllers.components.TransactionSortForm;

public class BudgetController {
    public AnchorPane mainPane;
    public MFXButton addBudgetButton;
    public FlowPane flowPane;
    private final AddBudgetForm addBudgetForm = new AddBudgetForm();
    public void handleAddBudgetForm(ActionEvent actionEvent) {
        if (!mainPane.getChildren().contains(addBudgetForm)) {
            AnchorPane.setTopAnchor(addBudgetForm, (mainPane.getHeight() - addBudgetForm.getPrefHeight()) / 2);
            AnchorPane.setLeftAnchor(addBudgetForm, (mainPane.getWidth() - addBudgetForm.getPrefWidth()) / 2);

            mainPane.getChildren().add(addBudgetForm);
        }
    }
}
