package myApp.controllers.components;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import myApp.controllers.views.ReportController;
import myApp.utils.ChatbotManager;
import myApp.utils.Draggable;
import myApp.utils.MainAppManager;

import java.io.IOException;

public class ChatScreen extends BorderPane {

    public Button closeButton;
    public MFXScrollPane chatScrollPane;
    public VBox chatContainer;
    public MFXTextField textField;
    public MFXButton sendButton;
    public ChatScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/chatScreen.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initialize();
            sendButton.setOnAction(this::addBotResponse);
            closeButton.setOnAction(this::close);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() {
        chatContainer.getChildren().add(new ActionMessage(this));
    }

    private void close(ActionEvent actionEvent) {
        ((Pane) getParent()).getChildren().remove(this);
    }

    public void addBotResponse(ActionEvent actionEvent) {
        String input = textField.getText().trim();
        String response;
        if (input.equalsIgnoreCase("guide me")) {
            chatContainer.getChildren().add(createUserMessage("What is this page about?"));
            response = ChatbotManager.getBotResponse("guide " + MainAppManager.getCurrentPage());
        }
        else {
            response = ChatbotManager.getBotResponse(input);
            chatContainer.getChildren().add(createUserMessage(input));
        }
        chatContainer.getChildren().add(createBotResponse(response));
        System.out.println("You entered: " + input);
        System.out.println("Bot said: " + response);
    }

    public void addBotResponse(String input) {
        String response;
        if (input.equalsIgnoreCase("guide me")) {
            chatContainer.getChildren().add(createUserMessage("What is this page about?"));
            response = ChatbotManager.getBotResponse("guide " + MainAppManager.getCurrentPage());
        }
        else {
            response = ChatbotManager.getBotResponse(input);
            chatContainer.getChildren().add(createUserMessage(input));
        }
        chatContainer.getChildren().add(createBotResponse(response));
        System.out.println("You entered: " + input);
        System.out.println("Bot said: " + response);


    }

    public void customResponse(String input, String response) {
        System.out.println("You entered: " + input);
        System.out.println("Bot said: " + response);
        chatContainer.getChildren().add(createUserMessage(input));
        chatContainer.getChildren().add(createBotResponse(response));
    }

    public HBox createUserMessage(String input) {
        HBox messageBox = new HBox(10); // Adjust spacing as needed
        messageBox.setMaxWidth(350);
        messageBox.setAlignment(Pos.CENTER_RIGHT);
        // Create and set up the profile image
        ImageView profileImage = new ImageView(new Image("/images/profiles/user_11.png")); // Set the path to your profile image
        profileImage.setFitWidth(40); // Adjust the width as needed
        profileImage.setFitHeight(40); // Adjust the height as needed

        Rectangle clip = new Rectangle(
                profileImage.getFitWidth(), profileImage.getFitHeight()
        );
        clip.setArcWidth(50);
        clip.setArcHeight(50);
        profileImage.setClip(clip);

        // Create and set up the label for the message text
        Label messageLabel = new Label(input);
        messageLabel.getStyleClass().add("user-message-label"); // Apply CSS style
        messageLabel.setWrapText(true); // Enable text wrapping

        // Add the components to the HBox
        messageBox.getChildren().addAll(messageLabel, profileImage);
        messageBox.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        return messageBox;
    }

    private HBox createBotResponse(String response) {
        HBox messageBox = new HBox(10); // Adjust spacing as needed
        messageBox.setMaxWidth(350);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        // Create and set up the profile image
        ImageView profileImage = new ImageView(new Image("/images/chatbot/chatbot.png")); // Set the path to your profile image
        profileImage.setFitWidth(40); // Adjust the width as needed
        profileImage.setFitHeight(40); // Adjust the height as needed

        Rectangle clip = new Rectangle(40, 40);
        profileImage.setClip(clip);
        profileImage.setPreserveRatio(false); // Disable image ratio preservation

        // Create and set up the label for the message text
        Label messageLabel = new Label(response);
        messageLabel.getStyleClass().add("user-message-label"); // Apply CSS style
        messageLabel.setWrapText(true); // Enable text wrapping

        // Add the components to the HBox
        messageBox.getChildren().addAll(profileImage, messageLabel);
        messageBox.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        return messageBox;
    }
}
