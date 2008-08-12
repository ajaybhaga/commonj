/*
 * Created on Nov 24, 2004
 */
package org.jcommon.mail;

/**
 * @author Matt Hicks
 */
public class SMTPResponse {
    private int type;
    private int code;
    private String message;
    
    public SMTPResponse(int type, String response) {
        this.type = type;
        try {
        	code = Integer.parseInt(response.substring(0, response.indexOf(" ")));
        } catch(NumberFormatException exc) {
        	code = Integer.parseInt(response.substring(0, response.indexOf("-")));
        }
        message = response.substring(response.indexOf(" ") + 1);
    }
    
    public int getType() {
        return type;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isOkay() {
        if (type == SMTP.GREETING) {
            if (code == 220) {
                return true;
            }
        } else if ((type == SMTP.EHLO) || (type == SMTP.HELO)) {
            if ((code == 250) || (code == 220)) {
                return true;
            }
        } else if (type == SMTP.MAIL) {
            if (code == 250) {
                return true;
            }
        } else if (type == SMTP.RCPT) {
            if ((code == 250) || (code == 251)) {
                return true;
            }
        } else if (type == SMTP.DATA) {
            if ((code == 250) || (code == 354)) {
                return true;
            }
        } else if (type == SMTP.RSET) {
            if (code == 250) {
                return true;
            }
        } else if (type == SMTP.VRFY) {
            if ((code == 250) || (code == 251) || (code == 252)) {
                return true;
            }
        } else if (type == SMTP.EXPN) {
            if ((code == 250) || (code == 252)) {
                return true;
            }
        } else if (type == SMTP.HELP) {
            if ((code == 211) || (code == 214)) {
                return true;
            }
        } else if (type == SMTP.NOOP) {
            if (code == 250) {
                return true;
            }
        } else if (type == SMTP.QUIT) {
            if ((code == 221) || (code == 250)) {
                return true;
            }
        }
        return false;
    }
}
