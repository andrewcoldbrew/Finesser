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
    public MFXButton reportButton;
    public MFXButton accountButton;

    // Variable to store the currently active button
    private MFXButton activeButton;

    public void initialize() {
        // Set the initial active button (e.g., dashboard)
    }

    public void moveToReport(ActionEvent actionEvent) {
        MainAppManager.switchScene("report");
    }

    public void moveToDashBoard(ActionEvent actionEvent) {
        MainAppManager.switchScene("dashboard");
    }

    public void moveToTransaction(ActionEvent actionEvent) {
        MainAppManager.switchScene("transaction");
    }

    public void moveToBudget(ActionEvent actionEvent) {
        MainAppManager.switchScene("budget");
    }

    public void moveToFinance(ActionEvent actionEvent) {
        MainAppManager.switchScene("finance");
    }

    public void moveToLogin(ActionEvent actionEvent) {
        MainAppManager.getMainAppStage().close();
        LoginStageManager.setupLoginStage();
    }

    public void moveToAccount(ActionEvent actionEvent) {
        MainAppManager.switchScene("account");
    }

    // Method to set the active button and update styles
    public void setActiveButton(MFXButton button) {
        // Reset the style of the previously active button
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active-button");
        }

        // Set the new active button
        activeButton = button;

        // Apply the style to the active button
        if (activeButton != null) {
            activeButton.getStyleClass().add("active-button");
//            System.out.println("Account Button: " + accountButton.getStyleClass());
//            System.out.println("Dashboard Button: " + dashBoardButton.getStyleClass());
//            System.out.println("Budget Button: " + budgetButton.getStyleClass());
//            System.out.println("Finance Button: " + financeButton.getStyleClass());
//            System.out.println("Transaction Button: " + transactionButton.getStyleClass());
//            System.out.println("Report Button: " + reportButton.getStyleClass());
        }
    }

    public void setActiveButtonForScene(String sceneName) {

        switch (sceneName) {
            case "dashboard":
                setActiveButton(dashBoardButton);
                break;
            case "transaction":
                setActiveButton(transactionButton);
                break;
            case "budget":
                setActiveButton(budgetButton);
                break;
            case "finance":
                setActiveButton(financeButton);
                break;
            case "report":
                setActiveButton(reportButton);
                break;
            case "account":
                setActiveButton(accountButton);
                break;
            // Add more cases as needed

            default:
                // Handle unknown scenes
                break;
        }
    }
}
