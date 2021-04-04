package com.digitized.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CprocessReturn implements Initializable {

    @FXML
    private BorderPane dashboardWindow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            dashboardWindow.getStylesheets().add(getClass().getResource("/fxml/css/processReturn.css").toExternalForm());
        } catch (Exception e){
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

    protected void switchContent(FXMLLoader loader, Node node){
        try {
            Parent targetContent = loader.load();
            BorderPane parent = (BorderPane) node.getParent().getParent().getParent();
            parent.setCenter(targetContent);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
