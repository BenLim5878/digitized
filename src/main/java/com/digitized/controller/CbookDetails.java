package com.digitized.controller;

import com.digitized.model.JSONData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import com.digitized.model.Book;
import com.digitized.model.Borrow;
import com.digitized.model.SearchOption;
import org.json.JSONArray;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class CbookDetails extends CprocessLoan implements Initializable {

    @FXML
    private ScrollPane bookListPane;
    @FXML
    private Button nextBtn;
    @FXML
    private ComboBox searchByBox;
    @FXML
    private VBox bookList;
    @FXML
    private HBox actionMessage;
    // Progress Bar
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Circle userDetailsCircle, loanDetailsCircle, confirmationCircle;
    @FXML
    private Text userDetailsText, loanDetailsText, confirmationText;
    @FXML
    private TextField searchBar;

    private Cmain cmain = new Cmain();

    // Maximum borrow limit
    String[] bookOrdered = new String[3];

    private Borrow tempBorrow = new Borrow();
    private ObservableList<String> options = FXCollections.observableArrayList();
    private ObservableList<Book> books = FXCollections.observableArrayList();
    private int bookCounter = 0;

    @FXML
    private void changeSearchType() {
        String option = searchByBox.getValue().toString();
        switch (option) {
            case "Search By Book Title":
                searchBar.setPromptText("Type here to search book by title");
                searchBar.clear();
                retrieveBookList();
                break;
            case "Search By Book ID":
                searchBar.setPromptText("Type here to search book by ID");
                searchBar.clear();
                retrieveBookList();
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SearchOption byTitle = new SearchOption("Search By Book Title");
        SearchOption byID = new SearchOption("Search By Book ID");
        options.addAll(
                byTitle.getOptionDescription(),
                byID.getOptionDescription()
        );
        searchByBox.setItems(options);
        searchByBox.getSelectionModel().select(0);
        bookListPane.setContent(bookList);
    }

    public void retrieveSessionContent(Borrow currentBorrow) {
        tempBorrow = currentBorrow;
    }

    public void changeProgressBar(int progress) {
        setCurrentProgressNode(progressBar, progress, userDetailsCircle, userDetailsText, loanDetailsCircle, loanDetailsText, confirmationCircle, confirmationText);
    }

    @FXML
    private void backButtonClicked() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userDetails.fxml"));
        switchContent(loader, nextBtn);
        // Loaded
        CuserDetails cuserDetails = loader.getController();
        cuserDetails.retrieveSessionContent(tempBorrow);
        cuserDetails.changeProgressBar(3);
    }

    @FXML
    public void retrieveBookList() {
        String option = searchByBox.getValue().toString();
        bookList.getChildren().clear();
        books.clear();
        JSONArray data = cmain.retrieveJSONData("book.json");
        if (!searchBar.getText().equals("")) {
            for (int i = 0; i < data.length(); i++) {
                switch (option) {
                    case "Search By Book Title":
                        String title = data.getJSONObject(i).get("title").toString().toLowerCase(Locale.ROOT);
                        if (title.contains(searchBar.getText().toLowerCase(Locale.ROOT))) {
                            createObject(data, i);
                        }
                        break;
                    case "Search By Book ID":
                        String ISBN = data.getJSONObject(i).get("ISBN").toString().toLowerCase(Locale.ROOT);
                        if (ISBN.contains(searchBar.getText().toLowerCase(Locale.ROOT))) {
                            createObject(data, i);
                        }
                        break;
                }
            }
        } else {
            for (int i = 0; i < data.length(); i++) {
                createObject(data, i);
            }
        }
        createBook();
    }

    private void createObject(JSONArray data, int i) {
        int isbn = Integer.parseInt(data.getJSONObject(i).get("ISBN").toString());
        String memberID = tempBorrow.getTpID();
        String borrowedBook = "";
        JSONArray transactionList = cmain.retrieveJSONData("transaction.json");
        for (int c = 0; c < transactionList.length(); c++) {
            String tpID = transactionList.getJSONObject(c).get("tpID").toString().toUpperCase(Locale.ROOT);
            String bookID = transactionList.getJSONObject(c).get("bookID").toString();
            String status = transactionList.getJSONObject(c).get("status").toString();
            if (tpID.equals(memberID) && bookID.equals(Integer.toString(isbn)) && !status.equals("returned")) {
                borrowedBook = bookID;
            }
        }
        if (!Integer.toString(isbn).equals(borrowedBook)) {
            String bookTitle = data.getJSONObject(i).get("title").toString();
            String author = data.getJSONObject(i).get("author").toString();
            Book book = new Book(isbn, bookTitle, author);
            books.add(book);
        }
    }

    public void retrieveOrderedBooks(String [] booksOrdered, int totalBooks){
        bookOrdered = booksOrdered;
        bookCounter = totalBooks;
    }

    private void createBook() {
        for (Book book : books) {

            // VBox
            VBox bookWindow = new VBox();
            bookWindow.getStyleClass().add("bookWindow");
            setDropShadow(bookWindow, 2, 2);

            // ISBN
            String isbn = Integer.toString(book.getISBN());
            Text isbnText = new Text("ISBN: " + isbn);
            bookWindow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    addBookToOrderList(bookWindow, isbn);
                }
            });
            for (String id : bookOrdered) {
                if (isbn.equals(id)) {
                    bookWindow.setStyle("-fx-background-color: #7FD1AE");
                    bookWindow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            int indexOfID = Arrays.asList(bookOrdered).indexOf(isbn);
                            removeBookToOrderList(indexOfID, bookWindow, isbn);
                        }
                    });
                }
            }

            // Book Title
            Text titleText = new Text("Title: " + book.getBookTitle());

            // Book Author
            Text author = new Text("Author: " + book.getAuthor());

            bookWindow.getChildren().addAll(isbnText, titleText, author);
            bookList.getChildren().add(bookWindow);
        }
    }

    private void addBookToOrderList(Node node, String isbn) {
        if (bookCounter <= 2) {
            node.setStyle("-fx-background-color: #7FD1AE");
            bookOrdered[bookCounter] = isbn;
            node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    int indexOfID = Arrays.asList(bookOrdered).indexOf(isbn);
                    removeBookToOrderList(indexOfID, node, isbn);
                }
            });
            bookCounter += 1;
        } else {
            cmain.actionMessage(actionMessage, "You have exceeded the limit");
        }
    }

    private void removeBookToOrderList(int index, Node node, String isbn) {
        bookOrdered[index] = null;
        bookCounter -= 1;
        node.setStyle("-fx-background-color: white");
        // Sort null values to back
        // Referenced from https://stackoverflow.com/questions/14514467/sorting-array-with-null-values
        Arrays.sort(bookOrdered, (o1, o2) -> {
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
        });
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                addBookToOrderList(node, isbn);
            }
        });
    }

    @FXML
    private void validateFields() {
        if (bookCounter == 0) {
            cmain.actionMessage(actionMessage, "Please choose at least one book to continue");
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/confirmation.fxml"));
            switchContent(loader, nextBtn);
            // Loaded
            Cconfirmation cconfirmation = loader.getController();
            cconfirmation.retrieveSessionContent(tempBorrow, bookOrdered, bookCounter);
            cconfirmation.changeProgressBar(2);
        }
    }
}
