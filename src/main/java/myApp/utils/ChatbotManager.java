package myApp.utils;

import myApp.controllers.components.ChatScreen;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;

import java.io.File;

public class ChatbotManager {
    private static final boolean TRACE_MODE = false;
    static String botName = "super";
    private static Bot bot;
    private static Chat chatSession;

    private static ChatScreen chatScreen;

    public static void initializeBot() {
        try {
            String resourcesPath = getResourcesPath();
            MagicBooleans.trace_mode = TRACE_MODE;
            bot = new Bot("super", resourcesPath);
            chatSession = new Chat(bot);
            bot.writeAIMLFiles();
            bot.brain.nodeStats();

            chatScreen = new ChatScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBotResponse(String userInput) {
        try {
            String response = chatSession.multisentenceRespond(userInput);
            while (response.contains("&lt;"))
                response = response.replace("&lt;", "<");
            while (response.contains("&gt;"))
                response = response.replace("&gt;", ">");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I encountered an error.";
        }
    }

    // Optional: Close the chatbot resources when done
    public static void closeBot() {
        bot.writeQuit();
    }

    private static String getResourcesPath() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        path = path.substring(0, path.length() - 2);
        System.out.println(path);
        String resourcesPath = path + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        return resourcesPath;
    }

    public static ChatScreen getChatScreen() {
        return chatScreen;
    }
}