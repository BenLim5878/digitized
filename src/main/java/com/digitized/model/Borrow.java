package com.digitized.model;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Borrow extends JSONData{

    private String tpID;
    private String fullName;
    private String emailAddress;
    private String bookID;
    private String bookName;
    private String expireBefore;
    private String status;
    private String phoneNumber;

    public Borrow() {
        this.tpID = "undefined";
        this.fullName = "undefined";
        this.emailAddress = "undefined";
        this.bookID = "undefined";
        this.bookName = "undefined";
        this.expireBefore = "undefined";
        this.status = "undefined";
        this.phoneNumber = "undefined";
    }

    public Borrow(String tpID, String fullName, String emailAddress, String bookID, String bookName, String expireBefore, String status, String phoneNumber) {
        this.tpID = tpID;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.bookID = bookID;
        this.bookName = bookName;
        this.expireBefore = expireBefore;
        this.status = status;
        this.phoneNumber = phoneNumber;
    }

    public String getTpID() {
        return tpID;
    }

    public void setTpID(String tpID) {
        this.tpID = tpID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getExpireBefore() {
        return expireBefore;
    }

    public void setExpireBefore(String expireBefore) {
        this.expireBefore = expireBefore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void createNewRecord() {
        JSONObject obj = new JSONObject();
        obj.put("tpID", tpID.toUpperCase(Locale.ROOT));
        obj.put("fullName", fullName);
        obj.put("emailAddress", emailAddress.toLowerCase(Locale.ROOT));
        obj.put("bookID", bookID);
        obj.put("bookName", bookName);
        obj.put("expireBefore", expireBefore);
        obj.put("status", status);
        obj.put("phoneNumber", phoneNumber);
        JSONArray data = retrieveJSONData("transaction.json");
        JSONArray newData = new JSONArray();
        for (int i = 0; i < data.length(); i++) {
            newData.put(data.getJSONObject(i));
        }
        newData.put(obj);
        try {
            Files.write(Paths.get(getJSONFilePath("transaction.json")), newData.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validateNewBorrower() {
        JSONArray data = retrieveJSONData("member.json");
        JSONArray newData = new JSONArray();
        Boolean result = true;
        for (int i = 0; i < data.length(); i++) {
            String tpNumber = data.getJSONObject(i).get("ID").toString();
            if (tpNumber.equals(tpID.toUpperCase(Locale.ROOT))) {
                result = false;
                break;
            }
        }
        if (result == true) {
            JSONObject obj = new JSONObject();
            obj.put("ID", tpID.toUpperCase(Locale.ROOT));
            obj.put("Name", fullName);
            obj.put("Email Address", emailAddress.toLowerCase(Locale.ROOT));
            obj.put("Contact Number", phoneNumber);
            for (int i = 0; i < data.length(); i++) {
                newData.put(data.getJSONObject(i));
            }
            newData.put(obj);
            try {
                Files.write(Paths.get(getJSONFilePath("member.json")), newData.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int calculateTotalOverdued() {
        JSONArray transactionList = retrieveJSONData("transaction.json");
        int totalOverdue = 0;
        for (int i = 0; i < transactionList.length(); i++) {
            String status = transactionList.getJSONObject(i).get("status").toString();
            LocalDateTime[] dates = retrieveDates(transactionList, i);
            if (dates[0].isAfter(dates[1]) && !status.equals("returned")) {
                totalOverdue += 1;
            }
        }
        return totalOverdue;
    }

    private LocalDateTime[] retrieveDates(JSONArray transactionList, Integer i) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime[] dates = new LocalDateTime[2];
        String SexpireBefore = transactionList.getJSONObject(i).get("expireBefore").toString();
        String formattedDate = Instant.ofEpochMilli(Long.parseLong(SexpireBefore)).atZone(ZoneId.systemDefault()).format(dtf);
        Long timeNow = System.currentTimeMillis();
        String dateTimeNow = Instant.ofEpochMilli(timeNow).atZone(ZoneId.systemDefault()).format(dtf);
        LocalDateTime currentDateTime = LocalDateTime.parse(dateTimeNow, dtf);
        LocalDateTime dateTime = LocalDateTime.parse(formattedDate, dtf);
        dates[0] = currentDateTime;
        dates[1] = dateTime;
        return dates;
    }

    public int calculateTotalBorrowed() {
        JSONArray transactionList = retrieveJSONData("transaction.json");
        int totalBorrowed = 0;
        for (int i = 0; i < transactionList.length(); i++) {
            String status = transactionList.getJSONObject(i).get("status").toString();
            if (status.equals("normal") || status.equals("extended")) {
                totalBorrowed += 1;
            }
        }
        return totalBorrowed;
    }

    public int calculateTotalExtended() {
        JSONArray transactionList = retrieveJSONData("transaction.json");
        int totalExtended = 0;
        for (int i = 0; i < transactionList.length(); i++) {
            String status = transactionList.getJSONObject(i).get("status").toString();
            if (status.equals("extended")) {
                totalExtended += 1;
            }
        }
        return totalExtended;
    }

    public VBox generateNoticeData() {
        VBox noticeWindow = new VBox();
        noticeWindow.setSpacing(10);
        JSONArray transactionList = retrieveJSONData("transaction.json");
        for (int i = 0; i < transactionList.length(); i++) {
            LocalDateTime[] dates = retrieveDates(transactionList, i);
            String status = transactionList.getJSONObject(i).get("status").toString();
            if (dates[0].isAfter(dates[1]) && !status.equals("returned")) {
                String tpID = transactionList.getJSONObject(i).get("tpID").toString();
                String fullName = transactionList.getJSONObject(i).get("fullName").toString();
                String description = "Loan expired";
                VBox notice = addNotice(tpID, fullName, description);
                noticeWindow.getChildren().add(notice);
            }
        }
        return noticeWindow;
    }

    private VBox addNotice(String tpID, String fullName, String description) {
        VBox notice = new VBox();
        notice.setPrefWidth(290);
        notice.getStyleClass().add("noticeWindow");
        Text tpIDText = new Text(tpID);
        Text fullNameText = new Text(fullName);
        Text descriptionText = new Text(description);
        notice.getChildren().addAll(tpIDText, fullNameText, descriptionText);
        notice.setSpacing(5);
        return notice;
    }

    public void updateData(String previousExpireBefore) {
        // New data object
        JSONObject updatedData = new JSONObject();
        updatedData.put("tpID", tpID);
        updatedData.put("fullName", fullName);
        updatedData.put("emailAddress", emailAddress);
        updatedData.put("bookID", bookID);
        updatedData.put("bookName", bookName);
        updatedData.put("expireBefore", expireBefore);
        updatedData.put("status", status);
        updatedData.put("phoneNumber", phoneNumber);
        // Update into JSON
        JSONArray data = retrieveJSONData("transaction.json");
        JSONArray newData = new JSONArray();
        for (int i = 0; i < data.length(); i++) {
            String dataTpID = data.getJSONObject(i).get("tpID").toString();
            String dataBookID = data.getJSONObject(i).get("bookID").toString();
            String dataBookExpireBefore = data.getJSONObject(i).get("expireBefore").toString();
            if (previousExpireBefore != "") {
                if (dataTpID.equals(tpID) && dataBookID.equals(bookID) && dataBookExpireBefore.equals(previousExpireBefore)) {
                } else {
                    newData.put(data.getJSONObject(i));
                }
            } else {
                if (dataTpID.equals(tpID) && dataBookID.equals(bookID) && dataBookExpireBefore.equals(expireBefore)) {
                } else {
                    newData.put(data.getJSONObject(i));
                }
            }
        }
        newData.put(updatedData);
        try {
            Files.write(Paths.get(getJSONFilePath("transaction.json")), newData.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



