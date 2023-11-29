package myApp.controllers.views;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import myApp.utils.LoginStageManager;
import myApp.utils.MainAppManager;

public class MenuBarController {
    public Button dashBoardButton;
    public Button transactionButton;
    public Button budgetButton;
    public Button financeButton;
    public Button logoutButton;

    public void moveToDashBoard(ActionEvent actionEvent) {
    }

    public void moveToTransaction(ActionEvent actionEvent) {
        MainAppManager.switchScene("transaction");
    }

    public void moveToBudget(ActionEvent actionEvent) {
        MainAppManager.switchScene("budget");
    }

    public void moveToFinance(ActionEvent actionEvent) {

    }

    public void moveToLogin(ActionEvent actionEvent) {
        MainAppManager.getMainAppStage().close();
        LoginStageManager.setupLoginStage();
    }
}
