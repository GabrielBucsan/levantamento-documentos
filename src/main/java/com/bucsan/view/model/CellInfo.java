package com.bucsan.view.model;

public class CellInfo {

    private String value;
    private boolean isFileLink;

    public CellInfo (String value) {
        this.value = value;
        this.isFileLink = false;
    }

    public CellInfo (String value, boolean isFileLink) {
        this.value = value;
        this.isFileLink = isFileLink;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFileLink() {
        return isFileLink;
    }

    public void setFileLink(boolean fileLink) {
        isFileLink = fileLink;
    }

}
