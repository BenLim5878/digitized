package com.digitized.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import com.digitized.model.Borrow;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.*;
import java.util.ResourceBundle;


public class Cpayment extends CprocessReturn implements Initializable {

    @FXML
    private TextArea booksReturn, booksLateReturn;
    @FXML
    private TextField paymentTotal;
    @FXML
    private Pane booksLateReturnPane;
    @FXML
    private Button nextBtn;

    private Cmain cmain = new Cmain();
    private ObservableList<Borrow> selectedReturn = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void retrieveSessionContent(ObservableList<Borrow> selectReturn) {
        selectedReturn = selectReturn;
        listAllReturnedBook();
    }

    private void listAllReturnedBook() {

        String listAllBooks = "";
        String listAllExpiredBooks = "";
        String paymentTotalText = "RM 0.00";
        double totalPayment = 0;
        LocalDate dateNow = LocalDate.now();
        for (Borrow borrow : selectedReturn) {
            LocalDate dateThen = Instant.ofEpochMilli(Long.parseLong(borrow.getExpireBefore())).atZone(ZoneId.systemDefault()).toLocalDate();
            if (dateNow.isAfter(dateThen)) {
                DecimalFormat df = new DecimalFormat("#.00");
                int yearsBetween = dateThen.until(dateNow).getYears();
                int monthsBetween = dateThen.until(dateNow).getMonths();
                int daysBetween = dateThen.until(dateNow).getDays();
                totalPayment += (yearsBetween * 365) + (monthsBetween * 30) + (daysBetween);
                paymentTotalText = "RM " + df.format(Math.abs(totalPayment));
                booksLateReturnPane.setVisible(true);
                paymentTotal.setStyle("-fx-border-color: #E04F5F");
                listAllExpiredBooks += borrow.getBookName() + " (" + borrow.getBookID() + ") (Expired on " + dateThen + ") \n";
            } else {
                listAllBooks += borrow.getBookName() + " (" + borrow.getBookID() + ")\n";

            }
        }
        booksLateReturn.setText(listAllExpiredBooks);
        booksReturn.setText(listAllBooks);
        paymentTotal.setText(paymentTotalText);
    }


    @FXML
    private void backButtonClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/returnDetails.fxml"));
        switchContent(loader, nextBtn);
    }

    @FXML
    private void updateRecords() {
        for (Borrow borrow : selectedReturn) {
            borrow.setStatus("returned");
            borrow.updateData("");
        }
        redirectToManage();
    }

    private void redirectToManage() {
        try {
            AnchorPane parent = (AnchorPane) nextBtn.getParent().getParent().getParent().getParent();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manage.fxml"));
            Parent node = loader.load();
            HBox window = (HBox) parent.getParent();
            Scene currentScene = (Scene) window.getScene();
            VBox menuPanel = (VBox) currentScene.lookup("#menuPanel");
            if (menuPanel != null) {
                ((HBox) menuPanel.getParent()).getChildren().remove(menuPanel);
            }
            currentScene.lookup("#menuOpenBtn").setVisible(true);
            parent.getChildren().remove(3);
            parent.getChildren().add(node);
            // Loaded
            Cmanage cmanage = loader.getController();
            cmanage.showActionMessage("Records updated successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
