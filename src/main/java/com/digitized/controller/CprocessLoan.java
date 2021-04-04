package com.digitized.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CprocessLoan implements Initializable {

    @FXML
    private BorderPane dashboardWindow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            dashboardWindow.getStylesheets().add(getClass().getResource("/fxml/css/processLoan.css").toExternalForm());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void setCurrentProgressNode(ProgressBar progressBar,int stage, Circle userDetailsCircle, Text userDetailsText, Circle loanDetailsCircle,Text loanDetailsText,Circle confirmationCircle, Text confirmationText){
        switch (stage){
            case 0:
                animateProgressBar(progressBar, 0.00, 0.00);
                // Current
                setCurrentNode(userDetailsCircle,userDetailsText);
                // Incomplete
                setIncompleteNode(loanDetailsCircle,loanDetailsText,confirmationCircle,confirmationText);
                break;
            case 1:
                animateProgressBar(progressBar,0.00,0.5);
                // Completed
                setCompletedNode(userDetailsCircle,userDetailsText);
                // Current
                setCurrentNode(loanDetailsCircle,loanDetailsText);
                // Incomplete
                setIncompleteNode(confirmationCircle,confirmationText);
                break;
            case 2:
                animateProgressBar(progressBar,0.5,1.0);
                // Completed
                setCompletedNode(userDetailsCircle,userDetailsText, loanDetailsCircle,loanDetailsText);
                // Current
                setCurrentNode(confirmationCircle,confirmationText);
                break;
            case 3:
                animateProgressBar(progressBar, 0.5, 0.00);
                // Current
                setCurrentNode(userDetailsCircle,userDetailsText);
                // Incomplete
                setIncompleteNode(loanDetailsCircle,loanDetailsText,confirmationCircle,confirmationText);
                break;
            case 4:
                animateProgressBar(progressBar,1.0,0.5);
                // Completed
                setCompletedNode(userDetailsCircle,userDetailsText);
                // Current
                setCurrentNode(loanDetailsCircle,loanDetailsText);
                // Incomplete
                setIncompleteNode(confirmationCircle,confirmationText);
                break;
        }
    }

    // Animation for progress bar
    private void animateProgressBar(ProgressBar progressBar, Double origin, Double target){
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), origin)),
                new KeyFrame(Duration.millis(500), e-> {
                }, new KeyValue(progressBar.progressProperty(), target))
        );
        timeline.play();
    }

    private void setCurrentNode(Circle currentCircle, Text currentText){
        currentCircle.setStyle("-fx-fill: #00A1F1 !important; -fx-stroke:#00A1F1 !important");
        currentText.setStyle("-fx-fill:#00A1F1");
    }

    // IncompleteNode Overloading
    private void setIncompleteNode(Circle incompleteCircle1, Text incompleteText1){
        incompleteCircle1.setStyle("-fx-fill: white !important; -fx-stroke: #ababab !important;");
        incompleteText1.setStyle("-fx-fill: #8d8d8d");
    }

    private void setIncompleteNode(Circle incompleteCircle1, Text incompleteText1,Circle incompleteCircle2, Text incompleteText2){
        incompleteCircle1.setStyle("-fx-fill: white !important; -fx-stroke: #ababab !important;");
        incompleteText1.setStyle("-fx-fill: #8d8d8d");
        incompleteCircle2.setStyle("-fx-fill: white !important; -fx-stroke: #ababab !important;");
        incompleteText2.setStyle("-fx-fill: #8d8d8d");
    }

    //CompletedNode Overloading
    private void setCompletedNode(Circle completedCircle1, Text completedText1){
        completedCircle1.setStyle("-fx-fill: #60C79A !important; -fx-stroke:#60C79A !important;");
        completedText1.setStyle("-fx-fill: #60C79A");
    }

    private void setCompletedNode(Circle completedCircle1, Text completedText1, Circle completedCircle2, Text completedText2){
        completedCircle1.setStyle("-fx-fill: #60C79A !important; -fx-stroke:#60C79A !important;");
        completedText1.setStyle("-fx-fill: #60C79A");
        completedCircle2.setStyle("-fx-fill: #60C79A !important; -fx-stroke:#60C79A !important;");
        completedText2.setStyle("-fx-fill: #60C79A");
    }

    protected void switchContent(FXMLLoader loader, Node node){
        try {
            Parent targetContent = loader.load();
            BorderPane parent = (BorderPane) node.getParent().getParent().getParent();
            parent.setCenter(targetContent);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void setDropShadow (Node target, double offSetX, double offSetY){
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(offSetX);
        dropShadow.setOffsetY(offSetY);
        dropShadow.setColor(Color.rgb(215,215,215));
        target.setEffect(dropShadow);
    }

}
