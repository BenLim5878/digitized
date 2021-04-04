package com.digitized.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import com.digitized.model.Borrow;
import org.json.JSONArray;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class Cmanage implements Initializable {

    @FXML
    private Button extendExpiryDateBtn;
    @FXML
    private BorderPane dashboardWindow;
    @FXML
    private BorderPane contentWindow;
    @FXML
    private TextField searchBar;
    @FXML
    private HBox actionMessage;

    private ObservableList<Borrow> transactions = FXCollections.observableArrayList();
    private Borrow selectedBorrow = new Borrow();
    private Cmain cmain = new Cmain();
    String previousExpireBefore = "";
    LocalDateTime dateTime;
    ZoneId zoneId = ZoneId.systemDefault();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    TableView<Borrow> transactionTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createTable();
        dashboardWindow.getStylesheets().add(getClass().getResource("/fxml/css/manage.css").toExternalForm());
    }

    private void createTable() {
        transactionTable = new TableView();
        createTableColumns(transactionTable);
        contentWindow.setCenter(transactionTable);
        // Import data into data
        getTransactionData(transactionTable);
        // Add event listener on table row click
        transactionTable.setRowFactory(tr -> {
            TableRow<Borrow> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 1 && (!row.isEmpty())) {
                    Long timeNow = System.currentTimeMillis();
                    String dateTimeNow = Instant.ofEpochMilli(timeNow).atZone(zoneId).format(dtf);
                    LocalDateTime currentDateTime = LocalDateTime.parse(dateTimeNow, dtf);
                    selectedBorrow = row.getItem();
                    dateTime = LocalDateTime.parse(selectedBorrow.getExpireBefore(), dtf);
                    Long epoch = dateTime.atZone(zoneId).toInstant().toEpochMilli();
                    if (selectedBorrow.getStatus().equals("returned")) {
                        extendExpiryDateBtn.setVisible(false);
                    } else if (dateTime.isAfter(currentDateTime)) {
                        previousExpireBefore = Long.toString(epoch);
                        extendExpiryDateBtn.setVisible(true);
                        extendExpiryDateBtn.setDisable(false);
                        extendExpiryDateBtn.setText("Extend expiry date...");
                        extendExpiryDateBtn.setStyle("-fx-text-fill: white;-fx-background-color: #3FA69F");
                    } else {
                        extendExpiryDateBtn.setText("This loan is expired");
                        extendExpiryDateBtn.setDisable(true);
                        extendExpiryDateBtn.setVisible(true);
                        extendExpiryDateBtn.setStyle("-fx-text-fill: black;-fx-background-color: #e2e2e2");
                    }
                }
            });
            return row;
        });
    }

    private void createTableColumns(TableView transactionTable) {
        TableColumn<Borrow, String> tpID = new TableColumn("TP Number");
        TableColumn<Borrow, String> fullName = new TableColumn("Name");
        TableColumn<Borrow, String> emailAddress = new TableColumn("Email Address");
        TableColumn<Borrow, String> bookID = new TableColumn("Book ID");
        TableColumn<Borrow, String> bookName = new TableColumn("Book Name");
        TableColumn<Borrow, String> expireBefore = new TableColumn("Expire Before");
        TableColumn<Borrow, String> status = new TableColumn("Status");
        TableColumn<Borrow, String> phoneNumber = new TableColumn("Contact Number");
        setProperty(transactionTable, tpID, fullName, emailAddress, bookID, bookName, expireBefore, status, phoneNumber);
        transactionTable.getColumns().addAll(tpID, fullName, emailAddress, bookID, bookName, expireBefore, status, phoneNumber);
    }

    private void setProperty(TableView transactionTable, TableColumn tpID, TableColumn fullName, TableColumn emailAddress, TableColumn bookID, TableColumn bookName, TableColumn expireBefore, TableColumn status, TableColumn phoneNumber) {
        tpID.setCellValueFactory(new PropertyValueFactory<>("tpID"));
        setColumnCustomProperty(tpID, false);
        tpID.setPrefWidth(75);
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        setColumnCustomProperty(fullName, false);
        fullName.setPrefWidth(130);
        emailAddress.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        setColumnCustomProperty(emailAddress, false);
        emailAddress.setPrefWidth(200);
        bookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        setColumnCustomProperty(bookID, false);
        bookID.setPrefWidth(101);
        bookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        setColumnCustomProperty(bookName, false);
        bookName.setPrefWidth(203);
        expireBefore.setCellValueFactory(new PropertyValueFactory<>("expireBefore"));
        setColumnCustomProperty(expireBefore, false);
        expireBefore.setPrefWidth(167);
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        setColumnCustomProperty(status, false);
        status.setPrefWidth(102);
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        setColumnCustomProperty(phoneNumber, false);
        phoneNumber.setPrefWidth(120);
        transactionTable.setItems(transactions);
    }

    private void setColumnCustomProperty(TableColumn column, Boolean result) {
        column.setResizable(result);
        column.setReorderable(result);
    }

    @FXML
    private void filterList() {
        getTransactionData(transactionTable);
    }

    private void getTransactionData(TableView table) {
        extendExpiryDateBtn.setVisible(false);
        previousExpireBefore = "";
        Borrow selectedBorrow = new Borrow();
        transactions.clear();
        JSONArray data = cmain.retrieveJSONData("transaction.json");
        if (!searchBar.getText().trim().isEmpty()) {
            for (int i = 0; i < data.length(); i++) {
                String StpID = data.getJSONObject(i).get("tpID").toString();
                if (StpID.toLowerCase(Locale.ROOT).contains(searchBar.getText().toLowerCase(Locale.ROOT))) {
                    addDataToList(data, i);
                }
            }
        } else {
            for (int i = 0; i < data.length(); i++) {
                addDataToList(data, i);
            }
        }
    }

    public void showActionMessage(String message) {
        cmain.actionMessage(actionMessage, message);
    }

    private void addDataToList(JSONArray data, int i) {
        String StpID = data.getJSONObject(i).get("tpID").toString();
        String SfullName = data.getJSONObject(i).get("fullName").toString();
        String SemailAddress = data.getJSONObject(i).get("emailAddress").toString();
        String SbookID = data.getJSONObject(i).get("bookID").toString();
        String SbookName = data.getJSONObject(i).get("bookName").toString();
        // Expire Before
        String SexpireBefore = data.getJSONObject(i).get("expireBefore").toString();
        String formattedDate = Instant.ofEpochMilli(Long.parseLong(SexpireBefore)).atZone(zoneId).format(dtf);
        // Status
        String Sstatus = data.getJSONObject(i).get("status").toString();
        Long timeNow = System.currentTimeMillis();
        String dateTimeNow = Instant.ofEpochMilli(timeNow).atZone(zoneId).format(dtf);
        LocalDateTime currentDateTime = LocalDateTime.parse(dateTimeNow, dtf);
        dateTime = LocalDateTime.parse(formattedDate, dtf);
        if (currentDateTime.isAfter(dateTime) && !Sstatus.equals("returned")) {
            Sstatus = "expired";
        }
        String SphoneNumber = data.getJSONObject(i).get("phoneNumber").toString();
        transactions.add(new Borrow(StpID, SfullName, SemailAddress, SbookID, SbookName, formattedDate, Sstatus, SphoneNumber));
    }

    @FXML
    private void updateData(){
        LocalDateTime updatedDateTime = dateTime.plusDays(14);
        long unixUpdatedTime = updatedDateTime.atZone(zoneId).toInstant().toEpochMilli();
        selectedBorrow.setExpireBefore(Long.toString(unixUpdatedTime));
        selectedBorrow.setStatus("extended");
        selectedBorrow.updateData(previousExpireBefore);
        getTransactionData(transactionTable);
        cmain.actionMessage(actionMessage, "Record updated successfully");
    }

}
