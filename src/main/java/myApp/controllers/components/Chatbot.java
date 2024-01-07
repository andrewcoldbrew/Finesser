package myApp.controllers.components;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import myApp.utils.ChatbotManager;

public class Chatbot extends HBox {
    public HBox chatbotContainer;
    public Button chatButton;
    public Chatbot() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/chatbot.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            chatButton.setOnAction(this::openChatScreen);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openChatScreen(ActionEvent actionEvent) {
        if (!isChatScreenOpen()) {
            chatbotContainer.getChildren().add(0, ChatbotManager.getChatScreen());
        }
    }

    private boolean isChatScreenOpen(){
        for (Node node : chatbotContainer.getChildren()) {
            if (node instanceof ChatScreen) {
                return true;
            }
        }
        return false;
    }



}
