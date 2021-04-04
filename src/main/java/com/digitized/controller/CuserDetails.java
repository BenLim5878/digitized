package com.digitized.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import com.digitized.model.Borrow;
import com.digitized.model.Borrower;
import org.json.JSONArray;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class CuserDetails extends CprocessLoan implements Initializable {

    @FXML
    private ComboBox idNumber;
    @FXML
    private TextField fullNameInput, emailAddressInput, contactNumberInput;
    @FXML
    private Button addMemberBtn;
    @FXML
    private Pane fullNameError, emailAddressError, contactNumberError;
    @FXML
    private HBox actionMessage;
    // Progress Bar
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Circle userDetailsCircle, loanDetailsCircle, confirmationCircle;
    @FXML
    private Text userDetailsText, loanDetailsText, confirmationText;

    private ObservableList<String> members = FXCollections.observableArrayList();
    private Borrow tempBorrow = new Borrow();
    private Cmain cmain = new Cmain();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            changeProgressBar(0);
            idNumber.setItems(members);
            importDataIntoSelect();
            validateEachField(fullNameInput);
            validateEachField(emailAddressInput);
            validateEachField(contactNumberInput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateEachField(TextField textField) {
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) //focus out
                {
                    switch (textField.getId()) {
                        case "fullNameInput":
                            validateEmptyField(fullNameInput, fullNameError);
                            break;
                        case "emailAddressInput":
                            validateEmptyField(emailAddressInput, emailAddressError);
                            if (!textField.getText().toUpperCase(Locale.ROOT).contains("@MAIL.APU.EDU.MY")) {
                                emailAddressError.setVisible(true);
                            } else {
                                emailAddressError.setVisible(false);
                            }
                            break;
                        case "contactNumberInput":
                            validateEmptyField(contactNumberInput, contactNumberError);
                            if (!textField.getText().matches("\\d{11}")) {
                                contactNumberError.setVisible(true);
                            } else {
                                contactNumberError.setVisible(false);
                            }
                            break;
                    }
                }
            }
        });
    }

    private void validateEmptyField(TextField textField, Pane errorPane) {
        if (textField.getText().trim().isEmpty()) {
            errorPane.setVisible(true);
        } else {
            errorPane.setVisible(false);
        }
    }

    public void changeProgressBar(int progress) {
        setCurrentProgressNode(progressBar, progress, userDetailsCircle, userDetailsText, loanDetailsCircle, loanDetailsText, confirmationCircle, confirmationText);
    }

    private void resetErrorValidation() {
        fullNameError.setVisible(false);
        emailAddressError.setVisible(false);
        contactNumberError.setVisible(false);
    }

    @FXML
    private void autoCompleteFields() {
        String ID = idNumber.getEditor().getText().toUpperCase(Locale.ROOT);
        resetErrorValidation();
        JSONArray data = cmain.retrieveJSONData("member.json");
        for (int i = 0; i < data.length(); i++) {
            String idNumber = data.getJSONObject(i).get("ID").toString();
            String fullName = data.getJSONObject(i).get("Name").toString();
            String emailAddress = data.getJSONObject(i).get("Email Address").toString();
            String contactNumber = data.getJSONObject(i).get("Contact Number").toString();
            Borrower borrower = new Borrower(idNumber, fullName, emailAddress, contactNumber);
            if (borrower.getTpNumber().toUpperCase(Locale.ROOT).equals(ID)) {
                fullNameInput.setText(borrower.getFullName());
                emailAddressInput.setText(borrower.getEmailAddress());
                contactNumberInput.setText(borrower.getContactNumber());
                inputsController(true);
                break;
            } else {
                fullNameInput.setText("");
                emailAddressInput.setText("");
                contactNumberInput.setText("");
                inputsController(false);
            }
        }
    }

    private void inputsController(boolean result) {
        fullNameInput.setDisable(result);
        emailAddressInput.setDisable(result);
        contactNumberInput.setDisable(result);
        addMemberBtn.setVisible(!result);
    }

    public void retrieveSessionContent(Borrow currentBorrow) {
        tempBorrow = currentBorrow;
        idNumber.getEditor().setText(tempBorrow.getTpID());
        fullNameInput.setText(tempBorrow.getFullName());
        emailAddressInput.setText(tempBorrow.getEmailAddress());
        contactNumberInput.setText(tempBorrow.getPhoneNumber());
        inputsController(true);
    }

    private void importDataIntoSelect() {
        JSONArray data = cmain.retrieveJSONData("member.json");
        for (int i = 0; i < data.length(); i++) {
            String idNumber = data.getJSONObject(i).get("ID").toString();
            String fullName = data.getJSONObject(i).get("Name").toString();
            String emailAddress = data.getJSONObject(i).get("Email Address").toString();
            String contactNumber = data.getJSONObject(i).get("Contact Number").toString();
            Borrower borrower = new Borrower(idNumber, fullName, emailAddress, contactNumber);
            members.add(borrower.getTpNumber());
        }
    }

    @FXML
    private void validateFields() {
        if (fullNameError.isVisible() || contactNumberError.isVisible() || emailAddressError.isVisible()) {
            cmain.actionMessage(actionMessage, "Please correct input error before proceeds");
        } else if (fullNameInput.getText().trim().isEmpty() || idNumber.getEditor().getText().trim().isEmpty() || contactNumberInput.getText().trim().isEmpty() || emailAddressInput.getText().trim().isEmpty()) {
            cmain.actionMessage(actionMessage, "Please make sure all fields are filled");
        } else if (!idNumber.getEditor().getText().toUpperCase(Locale.ROOT).contains("TP") || idNumber.getEditor().getText().length() != 8) {
            cmain.actionMessage(actionMessage, "Please enter proper ID number");
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookDetails.fxml"));
            switchContent(loader, idNumber);
            // Loaded
            CbookDetails cbookDetails = loader.getController();
            Borrow currentBorrow = new Borrow();
            currentBorrow.setTpID(idNumber.getEditor().getText().toUpperCase(Locale.ROOT));
            currentBorrow.setFullName(fullNameInput.getText());
            currentBorrow.setEmailAddress(emailAddressInput.getText());
            currentBorrow.setPhoneNumber(contactNumberInput.getText());
            currentBorrow.setBookID(tempBorrow.getBookID());
            currentBorrow.setBookName(tempBorrow.getBookName());
            //
            cbookDetails.retrieveSessionContent(currentBorrow);
            cbookDetails.retrieveBookList();
            cbookDetails.changeProgressBar(1);
        }
    }

}
