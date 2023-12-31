package myApp.utils;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.controls.MFXSimpleNotification;
import io.github.palexdev.materialfx.controls.cell.MFXNotificationCell;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.enums.NotificationState;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.notifications.MFXNotificationCenterSystem;
import io.github.palexdev.materialfx.notifications.MFXNotificationSystem;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.RandomUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import myApp.controllers.components.SuccessAlert;
import myApp.controllers.components.TestAlert;

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
        ExampleNotification notification = new ExampleNotification();
        notification.setContentText("Hello");
        return notification;
    }

     private static class ExampleNotification extends MFXSimpleNotification {
        private final StringProperty headerText = new SimpleStringProperty("Notification Header");
        private final StringProperty contentText = new SimpleStringProperty();

        public ExampleNotification() {
            Label headerLabel = new Label();
            headerLabel.textProperty().bind(headerText);
            ImageView icon = new ImageView("/images/alert/check.png");
            StackPane.setAlignment(icon, Pos.CENTER_RIGHT);
            StackPane placeHolder = new StackPane(icon);
            placeHolder.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(placeHolder, Priority.ALWAYS);
            HBox header = new HBox(10, icon, headerLabel, placeHolder);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setPadding(InsetsFactory.of(5, 0, 5, 0));
            header.setMaxWidth(Double.MAX_VALUE);

            Label contentLabel = new Label();
            contentLabel.getStyleClass().add("content");
            contentLabel.textProperty().bind(contentText);
            contentLabel.setWrapText(true);
            contentLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentLabel.setAlignment(Pos.TOP_LEFT);

            MFXButton action1 = new MFXButton("Action 1");
            MFXButton action2 = new MFXButton("Action 2");
            HBox actionsBar = new HBox(15, action1, action2);
            actionsBar.getStyleClass().add("actions-bar");
            actionsBar.setAlignment(Pos.CENTER_RIGHT);
            actionsBar.setPadding(InsetsFactory.all(5));

            BorderPane container = new BorderPane();
            container.getStyleClass().add("notification");
            container.setTop(header);
            container.setCenter(contentLabel);
            container.setBottom(actionsBar);
            container.setMinHeight(200);
            container.setMaxWidth(400);

            setContent(container);
        }

         public void setContentText(String contentText) {
             this.contentText.set(contentText);
         }
     }


}