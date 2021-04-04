package com.digitized.model;

public abstract class Member extends JSONData{

    private String tpNumber;
    private String fullName;
    private String emailAddress;

    public Member() {
        this.tpNumber = "undefined";
        this.fullName = "undefined";
        this.emailAddress = "undefined";
    }

    public Member(String tpNumber, String fullName, String emailAddress) {
        this.tpNumber = tpNumber;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
    }

    public String getTpNumber() {
        return tpNumber;
    }

    public void setTpNumber(String tpNumber) {
        this.tpNumber = tpNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
