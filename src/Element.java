/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author asmou
 */
public class Element {

    String text;
    String state;
    String suggest;

    public Element(String text, String state, String suggest) {
        this.text = text;
        this.state = state;
        this.suggest = suggest;

    }

    public void setText(String text) {
        this.text = text;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public String getText() {
        return this.text;
    }

    public String getState() {
        return this.state;
    }

    public String getSuggest() {
        return this.suggest;
    }

}
