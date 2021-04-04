package com.digitized.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.digitized.model.Announcement;
import com.digitized.model.Book;
import com.digitized.model.Borrow;

public class Cdashboard extends Cmain implements Initializable {

    @FXML
    private ScrollPane noticeList;
    @FXML
    protected ScrollPane announcementPane;
    @FXML
    private BorderPane dashboardWindow;
    @FXML private Text totalBorrowed, totalOverdued, totalExtended, totalBooks;
    private final Stage dialog = new Stage();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        dashboardWindow.getStylesheets().add(getClass().getResource("/fxml/css/dashboard.css").toExternalForm());
        Book book = new Book();
        Borrow borrow = new Borrow();
        // Retrieve data
        totalBooks.setText(book.calculateTotalBook()+" books");
        totalBorrowed.setText(borrow.calculateTotalBorrowed()+" lists");
        totalExtended.setText(borrow.calculateTotalExtended()+" lists");
        totalOverdued.setText(borrow.calculateTotalOverdued()+" lists");
        //
        CannouncementDialog dialogValidate = new CannouncementDialog();
        getAnnouncement();
        getNotice();
        borrow.generateNoticeData();
        dialog.resizableProperty().setValue(Boolean.FALSE);
        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                    getAnnouncement();
                }
            });
        };

    public void expandDialog(){
        setupPopout();
        dialog.show();
    }

    protected void getAnnouncement(){
        try {
            announcementPane.getStylesheets().add(getClass().getResource("/fxml/css/dashboard.css").toExternalForm());
            Announcement announcement = new Announcement();
            VBox v = announcement.getAnnouncementData();
            v.setSpacing(10);
            announcementPane.setContent(v);
            fade(150,v,0,1);
        } catch (Exception e) {
            return;
        }
    }

    private void getNotice(){
        try {
            Borrow borrow = new Borrow();
            VBox noticeWindow = borrow.generateNoticeData();
            noticeWindow.setSpacing(10);
            noticeList.setContent(noticeWindow);
        } catch (Exception e) {
            return;
        }
    }

    public void setupPopout(){
        try{
            Image applicationIcon = new Image(getClass().getResource("/fxml/materials/Logo.png").toExternalForm());
            Parent dialogComponents = FXMLLoader.load(getClass().getResource("/fxml/announcementDialog.fxml"));
            Scene addAnnouncementScene = new Scene(dialogComponents);
            // Dialog box config
            dialog.getIcons().add(applicationIcon);
            dialog.setScene(addAnnouncementScene);
            dialog.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
