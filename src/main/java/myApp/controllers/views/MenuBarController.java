package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import myApp.utils.LoginStageManager;
import myApp.utils.MainAppManager;

public class MenuBarController {
    public MFXButton dashBoardButton;
    public MFXButton transactionButton;
    public MFXButton budgetButton;
    public MFXButton financeButton;
    public MFXButton logoutButton;

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
