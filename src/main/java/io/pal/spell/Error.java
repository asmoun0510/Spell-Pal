package io.pal.spell;

public class Error {
    String type = "";
    String correction = "";
    String explication = "";
    String textFragment = "";

    public Error(String type, String correction, String textFragment) {
        this.correction = correction;
        this.type = type;
        this.textFragment = type;
    }

    public Error () {

    }

    public String getExplication() {
        return explication;
    }

    public String getType() {
        return type;
    }

    public String getTextFragment() {
        return textFragment;
    }

    public String getCorrection() {
        return correction;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCorrection(String correction) {
        this.correction = correction;
    }

    public void setTextFragment(String textFragment) {
        this.textFragment = textFragment;
    }

    public void setExplication(String explication) {
        this.explication = explication;
    }
}
