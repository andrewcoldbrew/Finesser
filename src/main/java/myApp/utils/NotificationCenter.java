package myApp.utils;

import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import io.github.palexdev.materialfx.controls.cell.MFXNotificationCell;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.notifications.MFXNotificationCenterSystem;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import io.github.palexdev.materialfx.notifications.base.INotification;
import javafx.stage.Stage;

public class NotificationCenter {

    public static void initalizeNotification(Stage stage) {
        MFXNotificationSystem.instance().initOwner(stage);
        MFXNotificationCenterSystem.instance().initOwner(stage);
        MFXNotificationCenter center = MFXNotificationCenterSystem.instance().getCenter();
        center.setCellFactory(notification -> new MFXNotificationCell(center, notification) {
            {
                setPrefHeight(400);
            }
        });
    }

    public static void successAlert(String message) {
        MFXNotificationSystem.instance()
                .setPosition(NotificationPos.BOTTOM_RIGHT)
                .publish(createNotification());
    }

    private static INotification createNotification() {
        return new
    }
}
