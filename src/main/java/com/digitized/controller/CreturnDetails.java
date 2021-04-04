package com.digitized.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.digitized.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;

public class CreturnDetails extends CprocessReturn implements Initializable {

    @FXML
    private VBox userList;
    @FXML
    private HBox contentWindow, actionMessage;
    @FXML
    private TextField searchBar;
    @FXML
    private Button nextBtn;

    private ObservableList<Borrower> borrowers = FXCollections.observableArrayList();
    private ObservableList<Book> booksBorrowed = FXCollections.observableArrayList();
    private ObservableList<Borrow> borrows = FXCollections.observableArrayList();
    private ObservableList<Borrow> selectedReturn = FXCollections.observableArrayList();
    private int counter;
    private int bookCounter;
    private Borrower tempChosenMember;
    private Cmain cmain = new Cmain();


    // Maximum borrow limit
    String[] bookReturnList = new String[8];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        retrieveMemberDetails();
    }

    private void retrieveMemberDetails() {
        // Clear all previous content
        booksBorrowed.clear();
        borrowers.clear();
        for (int i = 0; i < bookReturnList.length; i++) {
            bookReturnList[i] = null;
        }
        bookCounter = 0;
        nextBtn.setVisible(false);
        if (contentWindow.lookup("#userBookDetails") != null) {
            contentWindow.getChildren().remove(1);
        }
        counter = 0;
        //
        borrows.clear();
        userList.getChildren().clear();
        JSONArray data = cmain.retrieveJSONData("member.json");
        if (searchBar.getText() != "") {
            for (int i = 0; i < data.length(); i++) {
                String ID = data.getJSONObject(i).get("ID").toString().toLowerCase(Locale.ROOT);
                if (ID.contains(searchBar.getText().toLowerCase(Locale.ROOT))) {
                    Borrower borrower = createBorrower(data,i);
                    borrowers.add(borrower);
                }
            }
        } else {
            for (int i = 0; i < data.length(); i++) {
                Borrower borrower = createBorrower(data,i);
                borrowers.add(borrower);
            }
        }
        generateMemberList();
    }

    private Borrower createBorrower(JSONArray data, int index){
        Borrower borrower = new Borrower();
        borrower.setTpNumber(data.getJSONObject(index).get("ID").toString());
        borrower.setFullName(data.getJSONObject(index).get("Name").toString());
        borrower.setEmailAddress(data.getJSONObject(index).get("Email Address").toString());
        borrower.setContactNumber(data.getJSONObject(index).get("Contact Number").toString());
        return borrower;
    }

    private void generateMemberList() {
        for (Borrower borrower : borrowers) {
            // VBox
            VBox memberWindow = new VBox();
            memberWindow.getStyleClass().add("memberWindow");
            setDropShadow(memberWindow, 2, 2);

            // ID
            Text memberIDText = new Text("ID: " + borrower.getTpNumber());

            // Name
            Text memberNameText = new Text("Name: " + borrower.getFullName());

            // Email Address
            Text memberEmailAddressText = new Text("Email Address: " + borrower.getEmailAddress());

            // Contact Number
            Text memberContactNumberText = new Text("Contact Number: " + borrower.getContactNumber());

            memberWindow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    selectMember(memberWindow, borrower);
                }
            });
            memberWindow.getChildren().addAll(memberIDText, memberNameText, memberEmailAddressText, memberContactNumberText);
            userList.getChildren().add(memberWindow);
        }
    }

    private void selectMember(Node node, Borrower borrower) {
        if (counter < 1) {
            tempChosenMember = borrower;
            node.setStyle("-fx-background-color: #7FD1AE");
            createOrderDetailList(borrower.getTpNumber());
            counter += 1;
            node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    node.setStyle("-fx-background-color: white");
                    borrows.clear();
                    booksBorrowed.clear();
                    for (int i = 0; i < bookReturnList.length; i++) {
                        bookReturnList[i] = null;
                    }
                    bookCounter = 0;
                    nextBtn.setVisible(false);
                    contentWindow.getChildren().remove(1);
                    node.getStyleClass().add("memberWindow");
                    counter -= 1;
                    node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            selectMember(node, borrower);
                        }
                    });
                }
            });
        } else {
            cmain.actionMessage(actionMessage, "You only can choose one member from the list");
        }
    }

    private void createOrderDetailList(String memberID) {
        // Title
        Text title = new Text("Books Loaned");
        title.getStyleClass().add("userBookDetailsTitle");

        // Instruction
        Text instruction = new Text("Please select all the books to return");
        instruction.getStyleClass().add("userBookDetailsInstruction");
        instruction.setWrappingWidth(274);

        // Margin
        Pane margin10 = new Pane();
        margin10.setPrefHeight(10);
        Pane margin15 = new Pane();
        margin15.setPrefHeight(15);

        // Scroll Pane VBox
        retrieveMemberLoanDetails(memberID);
        VBox scrollList = new VBox();
        scrollList.setPrefWidth(274);
        scrollList.setStyle("-fx-background-color: white");
        scrollList.setSpacing(10);
        for (Book book : booksBorrowed) {
            VBox bookContent = new VBox();
            bookContent.getStyleClass().add("bookList");
            Text bookISBN = new Text(Integer.toString(book.getISBN()));
            Text bookTitle = new Text(book.getBookTitle());
            Text bookAuthor = new Text(book.getAuthor());
            bookContent.getChildren().addAll(bookISBN, bookTitle, bookAuthor);
            scrollList.getChildren().add(bookContent);
            bookContent.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    addReturnBookList(bookContent, Integer.toString(book.getISBN()));
                }
            });
        }

        // Scroll Pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: white");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(scrollList);
        scrollPane.setPrefHeight(400);

        // VBox
        VBox vbox = new VBox();
        vbox.setId("userBookDetails");
        vbox.setPrefWidth(314);
        vbox.getStyleClass().add("userBookDetails");
        setDropShadow(vbox, 2, 2);
        vbox.getChildren().addAll(title, margin10, instruction, margin15, scrollPane);

        contentWindow.getChildren().add(vbox);
    }

    private void retrieveMemberLoanDetails(String memberID) {
        JSONArray booksArray = new JSONArray();
        JSONArray data = cmain.retrieveJSONData("transaction.json");
        for (int i = 0; i < data.length(); i++) {
            String ID = data.getJSONObject(i).get("tpID").toString();
            if (ID.equals(memberID)) {
                String bookID = data.getJSONObject(i).get("bookID").toString();
                String bookExpireDate = data.getJSONObject(i).get("expireBefore").toString();
                String bookStatus = data.getJSONObject(i).get("status").toString();
                booksArray = cmain.retrieveJSONData("book.json");
                checkMatchedBookID(booksArray, bookID, bookExpireDate, bookStatus);
            }
        }
    }

    private void checkMatchedBookID(JSONArray booksArray, String bookID, String bookExpireDate, String bookStatus) {
        for (int c = 0; c < booksArray.length(); c++) {
            String ISBN = booksArray.getJSONObject(c).get("ISBN").toString();
            if (ISBN.equals(bookID) && !bookStatus.equals("returned")) {
                JSONObject book = booksArray.getJSONObject(c);
                String bookName = book.get("title").toString();
                String bookAuthor = book.get("author").toString();
                Book newBook = new Book(Integer.parseInt(book.get("ISBN").toString()), bookName, bookAuthor);
                booksBorrowed.add(newBook);
                Borrow borrow = new Borrow();
                borrow.setTpID(tempChosenMember.getTpNumber());
                borrow.setFullName(tempChosenMember.getFullName());
                borrow.setEmailAddress(tempChosenMember.getEmailAddress());
                borrow.setBookID(ISBN);
                borrow.setBookName(bookName);
                borrow.setExpireBefore(bookExpireDate);
                borrow.setStatus(bookStatus);
                borrow.setPhoneNumber(tempChosenMember.getContactNumber());
                borrows.add(borrow);
            }
        }
    }

    private void addReturnBookList(Node node, String isbn) {
        if (bookCounter < bookReturnList.length) {
            node.setStyle("-fx-background-color: #85B1CC");
            bookReturnList[bookCounter] = isbn;
            node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    int indexOfID = Arrays.asList(bookReturnList).indexOf(isbn);
                    removeBookFromReturnList(indexOfID, node, isbn);
                    node.getStyleClass().add("bookList");
                }
            });
            bookCounter += 1;
            nextBtn.setVisible(true);
        } else {
            cmain.actionMessage(actionMessage, "You have exceeded the return limit");
        }
    }

    private void removeBookFromReturnList(int index, Node node, String isbn) {
        bookReturnList[index] = null;
        bookCounter -= 1;
        if (bookCounter == 0) {
            nextBtn.setVisible(false);
        }
        node.setStyle("-fx-background-color: #eaeaea");
        // Sort null values to back
        // Referenced from https://stackoverflow.com/questions/14514467/sorting-array-with-null-values
        Arrays.sort(bookReturnList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return o1.compareTo(o2);
            }
        });
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                addReturnBookList(node, isbn);
            }
        });
    }

    @FXML
    private void filterID() {
        retrieveMemberDetails();
    }

    @FXML
    private void nextContent() {
        for (Borrow borrow : borrows) {
            for (String bookID : bookReturnList) {
                if (bookID != null && borrow.getBookID().equals(bookID)) {
                    selectedReturn.add(borrow);
                    break;
                }
            }
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payment.fxml"));
        switchContent(loader, nextBtn);
        // Loaded
        Cpayment cpayment = loader.getController();
        cpayment.retrieveSessionContent(selectedReturn);
    }
}

