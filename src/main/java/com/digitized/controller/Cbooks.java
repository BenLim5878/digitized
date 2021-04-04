package com.digitized.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.digitized.model.Book;
import org.json.JSONArray;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class Cbooks extends Cmain implements Initializable {

    @FXML
    private TextField searchBar;
    @FXML
    private VBox bookList, createBookPane;
    @FXML
    private HBox bookWindow, actionMessage;
    @FXML
    private TextField isbnInput, bookTitleInput, bookAuthorInput;
    @FXML
    private Button addBook;

    private ObservableList<Book> books = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        retrieveBookList();
        bookWindow.getChildren().remove(1);
    }

    @FXML
    private void retrieveBookList() {
        String searchQuery = searchBar.getText().toUpperCase(Locale.ROOT);
        bookList.getChildren().clear();
        JSONArray data = retrieveJSONData("book.json");
        if (!searchQuery.trim().isEmpty()) {
            for (int i = 0; i < data.length(); i++) {
                String isbn = data.getJSONObject(i).get("ISBN").toString().toUpperCase(Locale.ROOT);
                if (isbn.contains(searchQuery)) {
                    createBookList(data, i);
                }
            }
        } else {
            for (int i = 0; i < data.length(); i++) {
                createBookList(data, i);
            }
        }
    }

    private void createBookList(JSONArray books, int index) {
        int isbn = Integer.parseInt(books.getJSONObject(index).get("ISBN").toString());
        String memberID = books.getJSONObject(index).get("title").toString();
        String author = books.getJSONObject(index).get("author").toString();
        Book book = new Book(isbn, memberID, author);
        createBookWindow(book);
    }

    private void createBookWindow(Book book) {
        // VBox
        VBox bookWindow = new VBox();
        bookWindow.getStyleClass().add("bookWindow");
        setDropShadow(bookWindow, 2, 2);

        // ISBN
        String isbn = Integer.toString(book.getISBN());
        Text isbnText = new Text("ISBN: " + isbn);

        // Book Title
        Text titleText = new Text("Title: " + book.getBookTitle());

        // Book Author
        Text author = new Text("Author: " + book.getAuthor());
        bookWindow.getChildren().addAll(isbnText, titleText, author);
        bookList.getChildren().add(bookWindow);
    }

    @FXML
    private void showCreateBookPane() {
        bookWindow.getChildren().add(createBookPane);
        addBook.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                bookWindow.getChildren().remove(1);
                addBook.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        showCreateBookPane();
                    }
                });
            }
        });
    }

    @FXML
    private void addNewBook() {
        Boolean outcome = validateFields();
        if (outcome == true) {
            Boolean result = null;
            String isbn = isbnInput.getText();
            String bookTitle = bookTitleInput.getText();
            String bookAuthor = bookAuthorInput.getText();
            Book newBook = new Book(Integer.parseInt(isbn), bookTitle, bookAuthor);
            result = newBook.addNewBook(newBook);
            if (result == true) {
                actionMessage(actionMessage, "This book already exist");
                actionMessage.setStyle("-fx-background-color:  #e94a4a !important");
            } else if (result == false) {
                actionMessage(actionMessage, "Book added successfully");
                actionMessage.setStyle("-fx-background-color:  #00A1F1 !important");
                bookWindow.getChildren().remove(1);
                searchBar.clear();
                retrieveBookList();
                clearAllFields();
                addBook.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        showCreateBookPane();
                    }
                });
            }
        }
    }

    private void clearAllFields() {
        isbnInput.clear();
        bookTitleInput.clear();
        bookAuthorInput.clear();
    }

    private Boolean validateFields() {
        Boolean result = true;
        if (isbnInput.getText().trim().isEmpty() || bookTitleInput.getText().trim().isEmpty() || bookAuthorInput.getText().trim().isEmpty()) {
            actionMessage(actionMessage, "Please fill in all the fields");
            actionMessage.setStyle("-fx-background-color:  #e94a4a !important");
            result = false;
        } else if (!isbnInput.getText().matches("\\d{8}")) {
            actionMessage(actionMessage, "Please enter proper Book ID");
            actionMessage.setStyle("-fx-background-color:  #e94a4a !important");
            result = false;
        }
        return result;
    }
}
