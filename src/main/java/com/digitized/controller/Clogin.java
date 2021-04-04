package com.digitized.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.util.Duration;
import com.digitized.model.Librarian;

public class Clogin extends Cmain implements Initializable {

    @FXML
    private Button loginBtn, successLogin;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox actionMessage;

    private Librarian tempLibrarian = new Librarian();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {}

    @FXML
    private void loginUser() {
        String inputEmail = emailField.getText().toUpperCase(Locale.ROOT);
        String inputPassword = passwordField.getText();
        Librarian librarian = new Librarian();
        librarian.setEmailAddress(inputEmail);
        librarian.setPassword(inputPassword);
        String validateResult = librarian.login();
        if (validateResult == "success"){
            //
            tempLibrarian.setFullName(librarian.getFullName());
            tempLibrarian.setEmailAddress(inputEmail.toLowerCase(Locale.ROOT));
            //
            loginBtn.setVisible(false);
            successLogin.setVisible(true);
            fade(200, successLogin, 0, 1);
            actionMessage.setStyle("-fx-background-color: #60C79A");
            actionMessage(actionMessage, "Login successfully");
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), (callback) -> {
                try {
                    switchScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            timeline.play();
        } else if (validateResult == "wrong email address"){
            actionMessage(actionMessage, "Incorrect email address");
        } else if (validateResult == "wrong password"){
            actionMessage(actionMessage, "Incorrect password");
        }
    }

    private void switchScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent mainParent = loader.load();
        Scene mainScene = new Scene(mainParent);
        mainScene.getStylesheets().add(getClass().getResource("/fxml/css/main.css").toExternalForm());
        Stage window = (Stage) loginBtn.getScene().getWindow();
        window.setScene(mainScene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        window.setX((screenBounds.getWidth() - window.getWidth()) / 2);
        window.setY((screenBounds.getHeight() - window.getHeight()) / 2);
        // Loaded
        Cmain cmain = loader.getController();
        cmain.retrieveUserDetail(tempLibrarian);
    }


}
