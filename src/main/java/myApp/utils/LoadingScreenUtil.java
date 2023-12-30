package myApp.utils;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import myApp.controllers.components.LoadingScreen;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class LoadingScreenUtil {

    public static void run(StackPane mainPane, Runnable heavyTask) {
        LoadingScreen loadingScreen = new LoadingScreen(mainPane);
        loadingScreen.show();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Run the heavy task in the background
                    heavyTask.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    // Close the loading screen on the JavaFX Application Thread
                    Platform.runLater(loadingScreen::close);
                    get(); // Ensure any exceptions thrown during doInBackground are propagated
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        // Execute the SwingWorker in a separate thread
        worker.execute();
    }
}
