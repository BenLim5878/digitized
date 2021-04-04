package com.digitized.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.digitized.model.Announcement;
import java.net.URL;
import java.util.ResourceBundle;

public class CannouncementDialog extends Cdashboard implements Initializable {

    @FXML private Button postBtn,cancelBtn;
    @FXML private TextField createTitle;
    @FXML private TextArea createDescription;
    @FXML private HBox actionMessage;
    @FXML private BorderPane dialogWindow;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        dialogWindow.getStylesheets().add(getClass().getResource("/fxml/css/announcementDialog.css").toExternalForm());
    }

    @FXML
    private void postAnnouncement(){
        Boolean result = validateFields();
        if (result == true){
            Announcement announcement = new Announcement(createTitle.getText(),createDescription.getText());
            announcement.postAnnouncementDate();
            exitDialog();
        }
    }

    private Boolean validateFields(){
        Boolean result = true;
        if (createTitle.getText().trim().isEmpty() || createDescription.getText().trim().isEmpty()){
            actionMessage(actionMessage,"Please fill in all the fields");
            result = false;
        }
        return result;
    }

    @FXML
    private void exitDialog(){
        Stage currentStage = (Stage) postBtn.getScene().getWindow();
        currentStage.fireEvent(new WindowEvent(currentStage, WindowEvent.WINDOW_CLOSE_REQUEST));

    }

    @FXML
    public void closeDialog(){
        Stage currentStage = (Stage) postBtn.getScene().getWindow();
        currentStage.close();
    }
}
