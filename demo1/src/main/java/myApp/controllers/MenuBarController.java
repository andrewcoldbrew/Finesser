package myApp.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import myApp.utils.SceneManager;

public class MenuBarController {
    public Button dashBoardButton;
    public Button transactionButton;
    public Button budgetButton;
    public Button financeButton;

    public void moveToDashBoard(ActionEvent actionEvent) {
        SceneManager.switchToSceneWithMenuBar("test");
    }

    public void moveToTransaction(ActionEvent actionEvent) {
        SceneManager.switchToSceneWithoutMenuBar("login");
    }

    public void moveToBudget(ActionEvent actionEvent) {
    }

    public void moveToFinance(ActionEvent actionEvent) {
    }
}
