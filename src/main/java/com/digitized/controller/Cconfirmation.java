package com.digitized.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import com.digitized.model.Book;
import com.digitized.model.Borrow;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class Cconfirmation extends CprocessLoan implements Initializable {

    @FXML
    private Text emailAddressText, contactNumberText, nameText, tpNumberText, expireDateText, bookTitleText, bookIDText, bookQuantityText;
    @FXML
    private Button nextBtn;
    // Progress Bar
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Circle userDetailsCircle, loanDetailsCircle, confirmationCircle;
    @FXML
    private Text userDetailsText, loanDetailsText, confirmationText;

    private Borrow tempBorrow = new Borrow();
    private String[] tempBookOrdered = new String[3];
    private int tempNumsOfBooks;
    private ObservableList<Book> books = FXCollections.observableArrayList();
    private Cmain cmain = new Cmain();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void changeProgressBar(int progress) {
        setCurrentProgressNode(progressBar, progress, userDetailsCircle, userDetailsText, loanDetailsCircle, loanDetailsText, confirmationCircle, confirmationText);
    }

    public void retrieveSessionContent(Borrow currentBorrow, String[] previousBookOrdered, int numsOfBooks) {
        tempBorrow = currentBorrow;
        tempBookOrdered = previousBookOrdered;
        tempNumsOfBooks = numsOfBooks;
        retrieveCheckoutDetails();
    }

    private void retrieveCheckoutDetails() {
        // Loaner Details
        String tpNumber = tempBorrow.getTpID();
        String fullName = tempBorrow.getFullName();
        String emailAddress = tempBorrow.getEmailAddress();
        String contactNumber = tempBorrow.getPhoneNumber();
        tpNumberText.setText(tpNumber);
        nameText.setText(fullName);
        emailAddressText.setText(emailAddress);
        contactNumberText.setText(contactNumber);

        // Book Borrowed
        int totalBooks = tempNumsOfBooks;
        bookQuantityText.setText(Integer.toString(totalBooks));
        Date currentTime = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_YEAR, 14);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
        Date date = calendar.getTime();
        expireDateText.setText(formatter.format(date));
        for (int i = 0; i < tempBookOrdered.length; i++) {
            String ISBN = tempBookOrdered[i];
            retrieveBookList(ISBN);
        }
    }

    private void retrieveBookList(String ISBN) {
        JSONArray data = cmain.retrieveJSONData("book.json");
        for (int i = 0; i < data.length(); i++) {
            String bookISBN = data.getJSONObject(i).get("ISBN").toString();
            if (bookISBN.equals(ISBN)) {
                int isbn = Integer.parseInt(data.getJSONObject(i).get("ISBN").toString());
                String bookTitle = data.getJSONObject(i).get("title").toString();
                String author = data.getJSONObject(i).get("author").toString();
                Book book = new Book(isbn, bookTitle, author);
                books.add(book);
            }
        }
        showBookBorrowedText();
    }

    private void showBookBorrowedText() {
        String bookTitles = "";
        String ISBNs = "";
        int counter = 1;
        for (Book book : books) {
            if (counter == tempNumsOfBooks) {
                ISBNs += book.getISBN();
                bookTitles += book.getBookTitle();
            } else {
                ISBNs += book.getISBN() + ", ";
                bookTitles += book.getBookTitle() + ", ";
                counter += 1;
            }
        }
        bookTitleText.setText(bookTitles);
        bookIDText.setText(ISBNs);
    }

    @FXML
    private void backButtonClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookDetails.fxml"));
        switchContent(loader, nextBtn);
        // Loaded
        CbookDetails cbookDetails = loader.getController();
        cbookDetails.retrieveSessionContent(tempBorrow);
        cbookDetails.retrieveOrderedBooks(tempBookOrdered, tempNumsOfBooks);
        cbookDetails.retrieveBookList();
        cbookDetails.changeProgressBar(4);
    }

    @FXML
    private void saveData() {
        for (Book book : books) {
            Long currentTime = System.currentTimeMillis();
            Long expiredTime = currentTime + 1209600000;
            Borrow borrow = new Borrow();
            borrow.setTpID(tempBorrow.getTpID());
            borrow.setFullName(tempBorrow.getFullName());
            borrow.setEmailAddress(tempBorrow.getEmailAddress());
            borrow.setBookID(Integer.toString(book.getISBN()));
            borrow.setBookName(book.getBookTitle());
            borrow.setExpireBefore(expiredTime.toString());
            borrow.setStatus("normal");
            borrow.setPhoneNumber(tempBorrow.getPhoneNumber());
            saveIntoJSON(borrow);
        }
        redirectToManage();
    }

    private void redirectToManage() {
        try {
            AnchorPane parent = (AnchorPane) progressBar.getParent().getParent().getParent().getParent();
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
            cmanage.showActionMessage("Records added successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveIntoJSON(Borrow borrow) {
        borrow.validateNewBorrower();
        borrow.createNewRecord();
    }
}
