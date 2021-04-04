package com.digitized.model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

public class Librarian extends Member {

    private String password;
    private String encryptedPassword;

    public Librarian() {
        this.password = "none";
        this.encryptedPassword = "none";
    }

    public Librarian(String tpNumber, String fullName, String emailAddress, String password) {
        super(tpNumber, fullName, emailAddress);
        this.password = password;
        encryptPassword(password);
    }

    public String getPassword() {
        return password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }


    public void setPassword(String password) {
        this.password = password;
        encryptPassword(password);
    }

    private void encryptPassword(String password) {
        encryptedPassword = "";
        char[] chars = password.toCharArray();
        for (char c : chars) {
            c += 23;
            encryptedPassword += c;
        }
    }

    public String decryptPassword(String encryptedPassword) {
        String decryptedPassword = "";
        char[] chars = encryptedPassword.toCharArray();
        for (char c : chars) {
            c -= 23;
            decryptedPassword += c;
        }
        return decryptedPassword;
    }

    public Boolean addNewLibrarian(Librarian librarian) {
        Boolean result = null;
        Boolean accountExist = validateIfAccountExist(librarian.getTpNumber().toUpperCase(Locale.ROOT));
        if (accountExist == true) {
            result = false;
        } else {
            result = true;
            JSONObject obj = new JSONObject();
            obj.put("tp", librarian.getTpNumber());
            obj.put("fullName", librarian.getFullName());
            obj.put("emailAddress", librarian.getEmailAddress());
            obj.put("password", librarian.getEncryptedPassword());
            JSONArray data = retrieveJSONData("librarian.json");
            JSONArray newData = new JSONArray();
            for (int i = 0; i < data.length(); i++) {
                newData.put(data.getJSONObject(i));
            }
            newData.put(obj);
            try {
                Files.write(Paths.get(getJSONFilePath("librarian.json")), newData.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Boolean validateIfAccountExist(String tpNumber) {
        Boolean result = null;
        JSONArray data = retrieveJSONData("librarian.json");
        for (int i = 0; i < data.length(); i++) {
            String tpID = data.getJSONObject(i).get("tp").toString().toUpperCase(Locale.ROOT);
            if (tpID.equals(tpNumber)) {
                result = true;
                break;
            } else {
                result = false;
            }
        }
        return result;
    }

    public String login() {
        String validateResult = "";
        JSONArray data = retrieveJSONData("librarian.json");
        for (int i = 0; i < data.length(); i++) {
            Librarian librarian = new Librarian();
            String dataEmailAddress = data.getJSONObject(i).get("emailAddress").toString().toUpperCase(Locale.ROOT);
            String encryptedPassword = data.getJSONObject(i).get("password").toString();
            String dataPassword = librarian.decryptPassword(encryptedPassword);
            if (dataEmailAddress.equals(super.getEmailAddress()) && dataPassword.equals(password)) {
                validateResult = "success";
                this.setFullName(data.getJSONObject(i).get("fullName").toString());
                break;
            } else if (!dataEmailAddress.equals(super.getEmailAddress())) {
                validateResult = "wrong email address";
            } else if (!dataPassword.equals(password)) {
                validateResult = "wrong password";
            }
        }
        return validateResult;
    }

}
