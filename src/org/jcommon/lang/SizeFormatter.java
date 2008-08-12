/*
 * Created on Nov 5, 2004
 */
package org.jcommon.lang;

/**
 * @author Matt Hicks
 */
public class SizeFormatter {
    public static final String[] TYPE = new String[] {"b", "kb", "mb", "gb", "tb"};
    
    private int digits;
    
    public SizeFormatter() {
        setDigits(3);
    }
    
    public void setDigits(int digits) {
        this.digits = digits;
    }
    
    public int getDigits() {
        return digits;
    }
    
    public String format(long size) {
        String s = String.valueOf(size);
        int type = (s.length() - 1) / 3;
        int remainder = s.length() % 3;
        StringBuffer buffer = new StringBuffer();
        int length = getDigits();
        if (s.length() < length) {
            length = s.length();
        }
        for (int i = 0; i < length; i++) {
            buffer.append(s.charAt(i));
            if ((i == remainder - 1) && (i < length - 1)) {
                buffer.append(".");
            }
        }
        return buffer.toString() + TYPE[type];
    }
}
