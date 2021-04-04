package com.digitized.model;

public class SearchOption {

    private String optionDescription;

    public SearchOption(){
        this.optionDescription = "Undefined";
    }

    public SearchOption(String optionDescription) {
        this.optionDescription = optionDescription;
    }

    public String getOptionDescription() {
        return optionDescription;
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }

}
