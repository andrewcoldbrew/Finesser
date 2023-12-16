package myApp.controllers.views;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class NewSignupController implements Initializable {
    public AnchorPane leftSignup;
    public AnchorPane rightSignup;
    public VBox slidingPane;
    public MFXTextField fnameField;
    public MFXTextField lnameField;
    public MFXTextField emailField;
    public MFXDatePicker dateOfBirthPicker;
    public MFXComboBox<String> countryComboBox;
    public MFXComboBox<String> jobComboBox;
    public MFXComboBox<String> usageComboBox;
    private static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");
    private static final String[] upperChar = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
    private static final String[] lowerChar = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
    private static final String[] digits = "0 1 2 3 4 5 6 7 8 9".split(" ");
    private static final String[] specialCharacters = "! @ # & ( ) – [ { } ]: ; ' , ? / * ~ $ ^ + = < > -".split(" ");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slidingPane.getStyleClass().add("right");
        TranslateTransition t = new TranslateTransition(Duration.seconds(1), slidingPane);
        t.setToX(rightSignup.getLayoutX());
        t.play();
    }



    public void moveToLeft(ActionEvent actionEvent) {
        slidingPane.getStyleClass().remove("left");
        slidingPane.getStyleClass().add("right");
        TranslateTransition t = new TranslateTransition(Duration.seconds(1), slidingPane);
        t.setToX(rightSignup.getLayoutX() + 20);
        t.play();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), rightSignup);
        fadeOut.setToValue(0);
        fadeOut.play();

        t.setOnFinished(e -> {
            // Fade in leftSignup
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), leftSignup);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    public void moveToRight(ActionEvent actionEvent) {
        slidingPane.getStyleClass().remove("right");
        slidingPane.getStyleClass().add("left");
        TranslateTransition t = new TranslateTransition(Duration.seconds(1), slidingPane);
        t.setToX(leftSignup.getLayoutX());
        t.play();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), leftSignup);
        fadeOut.setToValue(0);
        fadeOut.play();

        t.setOnFinished(e -> {
            // Fade in rightSignup
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), rightSignup);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }


}
