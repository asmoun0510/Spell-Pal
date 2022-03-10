package io.beyn.spell;

import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author asmou
 */
public class Element {
    String text;
    String state;
    Vector<Error> errors = new Vector<>();

    public Element(String text, String state) {
        this.text = text;
        this.state = state;
    }

    public Element() {

    }

    public void addError(Error error) {
        this.errors.add(error);
    }



    public void setText(String text) {
        this.text = text;
    }

    public void setState(String state) {
        // waiting #ffffff // correct #3fdd7b // ortographe #e86868 // grammaire #ddb83e // examiner #f64dff //typo #5e80fc
        this.state = state;
    }

    public int getNumberError() {
        return this.errors.size();
    }
    public String getText() {
        return this.text;
    }
    public String getState() {
        return this.state;
    }
    public Vector<Error> getErrors () {
        return this.errors;
    }


}
