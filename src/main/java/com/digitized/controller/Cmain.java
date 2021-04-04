package com.digitized.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import com.digitized.model.JSONData;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.digitized.model.Librarian;

public class Cmain  extends JSONData implements Initializable {

    @FXML
    private VBox userPanel;
    @FXML
    private Text userEmail;
    @FXML
    private Text userName;
    @FXML
    private Button menuBtn;
    @FXML
    private VBox menuPanel;
    @FXML
    private AnchorPane contentWindow;
    @FXML
    private Button userButton, menuOpenBtn;
    @FXML
    private HBox window;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ((HBox) menuPanel.getParent()).getChildren().remove(menuPanel);
        menuOpenBtn.setVisible(true);
        menuBtn.setOnAction(e -> {
            slide(175, menuPanel, 0, -283);
            fade(175, menuPanel, 1, 0);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(175), (callback) -> {
                ((HBox) menuPanel.getParent()).getChildren().remove(menuPanel);
                menuOpenBtn.setVisible(true);
                expandContentWindow();
            }));
            timeline.play();
        });
        menuOpenBtn.setOnAction(e -> {
            shrinkContentWindow();
            slide(175, menuPanel, -283, 0);
            fade(175, menuPanel, 0, 1);
            window.getChildren().add(0, menuPanel);
            menuOpenBtn.setVisible(false);
        });

    }

    public void retrieveUserDetail(Librarian librarian){
        userEmail.setText(librarian.getEmailAddress());
        userName.setText(librarian.getFullName());
    }

    @FXML
    private void userBtnClicked() {
        if (userPanel.isVisible()) {
            userPanel.setVisible(false);
            fade(200, userPanel, 1, 0);
            userPanel.toBack();
        } else {
            fade(200, userPanel, 0, 1);
            userPanel.setVisible(true);
            userPanel.toFront();
        }
    }

    @FXML
    private void changeScene(Event e) throws IOException {
        userPanel.toBack();
        userPanel.setVisible(false);
        Button tempBtn = (Button) e.getTarget();
        if (tempBtn.getText().contains("Dashboard")) {
            Parent node = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            setScene(node, e);
            shrinkContentWindow();
        } else if (tempBtn.getText().contains("Manage")) {
            Parent node = FXMLLoader.load(getClass().getResource("/fxml/manage.fxml"));
            setScene(node, e);
            shrinkContentWindow();
        } else if (tempBtn.getText().contains("Loans")) {
            Parent node = FXMLLoader.load(getClass().getResource("/fxml/processLoan.fxml"));
            setScene(node, e);
            shrinkContentWindow();
         } else if (tempBtn.getText().contains("Returns")) {
            Parent node = FXMLLoader.load(getClass().getResource("/fxml/processReturn.fxml"));
            setScene(node, e);
            shrinkContentWindow();
        } else if (tempBtn.getText().contains("Add Librarian")) {
            Parent node = FXMLLoader.load(getClass().getResource("/fxml/addLibrarian.fxml"));
            setScene(node, e);
            shrinkContentWindow();
        }else if (tempBtn.getText().contains("Books")) {
            Parent node = FXMLLoader.load(getClass().getResource("/fxml/books.fxml"));
            setScene(node, e);
            shrinkContentWindow();
        }
    }

    private void expandContentWindow() {
        Parent parent = contentWindow;
        BorderPane dashboardWindow = (BorderPane) contentWindow.lookup("#dashboardWindow");
        dashboardWindow.setPrefWidth(1169);
    }

    private void shrinkContentWindow() {
        Parent parent = contentWindow;
        BorderPane dashboardWindow = (BorderPane) contentWindow.lookup("#dashboardWindow");
        dashboardWindow.setPrefWidth(887); //887
    }

    private void setScene(Parent parent, Event event) {
        // Remove content in current window
        contentWindow.getChildren().remove(3);
        // Set new content
        contentWindow.getChildren().add(parent);
    }

    // Transition Animation
    public static void slide(int duration, Parent object, int fromX, int toX) {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(duration), object);
        slideOut.setFromX(fromX);
        slideOut.setToX(toX);
        slideOut.play();
    }

    public static void slide(int duration, Parent object, int fromX, int toX, int fromY, int toY) {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(duration), object);
        slideOut.setFromX(fromX);
        slideOut.setFromY(fromY);
        slideOut.setToX(toX);
        slideOut.setToY(toY);
        slideOut.play();
    }

    // Fade Animation
    public static void fade(int duration, Parent object, int fromValue, int toValue) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(duration), object);
        fadeIn.setFromValue(fromValue);
        fadeIn.setToValue(toValue);
        fadeIn.play();
    }

    // Configure actionMessage
    public static void actionMessage(HBox target,String message){
        target.getChildren().clear();
        Timeline terminateTimeline = new Timeline(new KeyFrame(Duration.millis(250),(callback)-> {
            target.setVisible(false);
        }));
        // Text Node
        Text result = new Text(message);
        result.getStyleClass().add("statusMessage");

        // Drop Shadow
        DropShadow shadow = new DropShadow();
        shadow.setHeight(8.45);
        shadow.setOffsetX(2.0);
        shadow.setOffsetY(2.0);
        shadow.setRadius(3.5944);
        shadow.setWidth(7.93);
        shadow.setColor(Color.GRAY);
        //

        // Target
        target.setAlignment(Pos.CENTER);
        target.setEffect(shadow);
        target.getChildren().addAll(result);
        target.setVisible(true);
        fade(150,target,0,1);
        Timeline animationTimeline = new Timeline(new KeyFrame(Duration.millis(3000), (callback) -> {
            fade(150,target,1,0);
            terminateTimeline.play();
        }));
        animationTimeline.play();
        //
    }

    protected void setDropShadow (Node target, double offSetX, double offSetY){
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(offSetX);
        dropShadow.setOffsetY(offSetY);
        dropShadow.setColor(Color.rgb(215,215,215));
        target.setEffect(dropShadow);
    }

    @FXML private void logout () throws Exception {
        Stage currentStage = (Stage) menuOpenBtn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent loginPage = loader.load();
        Scene scene = new Scene(loginPage);
        scene.getStylesheets().add(getClass().getResource("/fxml/css/login.css").toExternalForm());
        currentStage.setTitle("Digitized");
        currentStage.setScene(scene);
        currentStage.centerOnScreen();
    }

}
