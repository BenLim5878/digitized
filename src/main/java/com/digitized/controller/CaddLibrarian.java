package com.digitized.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import com.digitized.model.Librarian;
import java.util.Locale;

public class CaddLibrarian {

    @FXML
    private TextField tpNumberInput, nameInput, emailAddressInput, passwordInput, confirmPasswordInput;
    @FXML
    private HBox actionMessage;
    private Cmain cmain = new Cmain();

    @FXML
    private void registerNewLibrarian() {
        Boolean validateResult = validateFields();
        if (validateResult == true) {
            String tpNumber = tpNumberInput.getText();
            String name = nameInput.getText();
            String emailAddress = emailAddressInput.getText();
            String password = confirmPasswordInput.getText();

            Librarian librarian = new Librarian(tpNumber, name, emailAddress, password);
            Boolean result = librarian.addNewLibrarian(librarian);
            if (result == true) {
                clearAllFields();
                cmain.actionMessage(actionMessage, "Librarian added successfully");
                actionMessage.setStyle("-fx-background-color:  #00A1F1 !important");
            } else {
                cmain.actionMessage(actionMessage, "This librarian already exist");
                actionMessage.setStyle("-fx-background-color:  #e94a4a !important");
            }
        }
    }

    private void clearAllFields() {
        tpNumberInput.clear();
        nameInput.clear();
        emailAddressInput.clear();
        passwordInput.clear();
        confirmPasswordInput.clear();
    }

    private Boolean validateFields() {
        Boolean result = true;
        if (tpNumberInput.getText().trim().isEmpty() || nameInput.getText().trim().isEmpty() || emailAddressInput.getText().trim().isEmpty() || passwordInput.getText().trim().isEmpty() || confirmPasswordInput.getText().trim().isEmpty()) {
            cmain.actionMessage(actionMessage, "Please fill in all the fields");
            actionMessage.setStyle("-fx-background-color:  #e94a4a !important");
            result = false;
        } else if (!tpNumberInput.getText().toUpperCase(Locale.ROOT).contains("TP") || tpNumberInput.getText().length() != 8) {
            cmain.actionMessage(actionMessage, "Please provide proper TP Number");
            actionMessage.setStyle("-fx-background-color:  #e94a4a !important");
            result = false;
        } else if (!emailAddressInput.getText().toUpperCase(Locale.ROOT).contains("@MAIL.APU.EDU.MY")) {
            cmain.actionMessage(actionMessage, "Please provide proper email address");
            actionMessage.setStyle("-fx-background-color:  #e94a4a !important");
            result = false;
        } else if (!passwordInput.getText().equals(confirmPasswordInput.getText())) {
            cmain.actionMessage(actionMessage, "Password do not match");
            actionMessage.setStyle("-fx-background-color:  #e94a4a !important");
            result = false;
        }
        return result;
    }
}
