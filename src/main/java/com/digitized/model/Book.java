package com.digitized.model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

public class Book extends JSONData {

    private int ISBN;
    private String bookTitle;
    private String author;

    public Book() {
        this.ISBN = 00000000;
        this.bookTitle = "undefined";
        this.author = "undefined";
    }

    public Book(int ISBN, String bookTitle, String author) {
        this.ISBN = ISBN;
        this.bookTitle = bookTitle;
        this.author = author;
    }

    public int getISBN() {
        return ISBN;
    }

    public void setISBN(int ISBN) {
        this.ISBN = ISBN;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean addNewBook(Book book) {
        Boolean result = null;
        JSONObject obj = new JSONObject();
        String isbn = Integer.toString(book.getISBN());
        String bookTitle = book.getBookTitle();
        String bookAuthor = book.getAuthor();
        obj.put("ISBN", isbn);
        obj.put("title", bookTitle);
        obj.put("author", bookAuthor);
        result = validateIfBookExist(isbn);
        if (result == true) {
        } else {
            JSONArray data = retrieveJSONData("book.json");
            JSONArray newData = new JSONArray();
            for (int i = 0; i < data.length(); i++) {
                newData.put(data.getJSONObject(i));
            }
            newData.put(obj);
            try {
                Files.write(Paths.get(getJSONFilePath("book.json")), newData.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Boolean validateIfBookExist(String isbn) {
        Boolean result = false;
        JSONArray data = retrieveJSONData("book.json");
        for (int i = 0; i < data.length(); i++) {
            String bookID = data.getJSONObject(i).get("ISBN").toString().toUpperCase(Locale.ROOT);
            if (bookID.equals(isbn)) {
                result = true;
                break;
            } else {
                result = false;
            }
        }
        return result;
    }

    public int calculateTotalBook() {
        int totalBook = 0;
        JSONArray booksList = retrieveJSONData("book.json");
        for (int i = 0; i < booksList.length(); i++) {
            totalBook += 1;
        }
        return totalBook;
    }

}
