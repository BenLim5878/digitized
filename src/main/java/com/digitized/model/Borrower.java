package com.digitized.model;

public class Borrower extends Member {

    private String contactNumber;

    public Borrower(){
        super();
        this.contactNumber = "undefined";
    }

    public Borrower(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Borrower(String tpNumber, String fullName, String emailAddress, String contactNumber) {
        super(tpNumber, fullName, emailAddress);
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString(){
        return super.getFullName() + " (" + super.getTpNumber() + ") has an email address of " + super.getEmailAddress() + " and contact number of " + contactNumber;
    }
}
