/*
 * Created on Sep 15, 2004
 */
package org.jcommon.swing;

import java.util.*;

import javax.swing.text.*;

/**
 * @author Matt Hicks
 */
public class CustomizablePlainDocument extends PlainDocument {
    protected int maxLength;
    protected String allowedCharacters;
    protected int format;
    
    private ArrayList validators;
    
    public CustomizablePlainDocument() {
        maxLength = 100;
        allowedCharacters = null;
        validators = new ArrayList();
    }
    
    public void addValueValidator(ValueValidator validator) {
        validators.add(validator);
    }
    
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    
    public void addAllowedCharacter(char c) {
        if (allowedCharacters == null) {
            allowedCharacters = String.valueOf(c);
        } else {
            allowedCharacters += String.valueOf(c);
        }
    }
    
    public void setAllowedCharacters(String allowedCharacters) {
        this.allowedCharacters = allowedCharacters;
    }

    public void insertString(int offset, String s, AttributeSet a) throws BadLocationException {
        char[] chars = s.toCharArray();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            if (this.getLength() >= (maxLength)) return;
            if ((allowedCharacters == null) || (allowedCharacters.indexOf(chars[i]) > -1)) {
                buffer.append(chars[i]);
            }
        }
        StringBuffer modified = new StringBuffer(super.getText(0, super.getLength()));
        modified.insert(offset, buffer.toString());
        if (valid(modified.toString())) {
            super.insertString(offset, buffer.toString(), a);
        }
    }
    
    public boolean valid(String s) {
        ValueValidator validator;
        for (int i = 0; i < validators.size(); i++) {
            validator = (ValueValidator)validators.get(i);
            if (!validator.isValid(s)) return false;
        }
        return true;
    }
}
