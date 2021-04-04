package com.digitized;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage arg0) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Image applicationIcon = new Image(getClass().getResource("/fxml/materials/Logo.png").toExternalForm());
        Scene loginPage = new Scene(root);
        //
        arg0.getIcons().add(applicationIcon);
        loginPage.getStylesheets().add(getClass().getResource("/fxml/css/login.css").toExternalForm());
        arg0.setTitle("Digitized");
        arg0.setScene(loginPage);
        arg0.centerOnScreen();
        arg0.show();
        //
        arg0.resizableProperty().setValue(Boolean.FALSE);
        arg0.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
