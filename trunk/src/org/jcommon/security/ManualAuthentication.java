/*
 * Created on Aug 24, 2004
 */
package org.jcommon.security;

import java.util.*;
import javax.security.auth.*;
import javax.security.auth.kerberos.*;
import javax.security.auth.login.*;
import javax.security.auth.callback.*;
import javax.swing.*;

import org.jcommon.swing.DynamicDialog;

/**
 * @author Matt Hicks
 */
public class ManualAuthentication implements Authentication, CallbackHandler {
    protected LoginContext lc;
    
    protected String username;
    protected char[] password;
    
    public NameCallback ncb = null;
    public PasswordCallback pcb = null;
    
    protected String name;
    protected JFrame frame;
    
    private boolean cancelled = false;
    
    public ManualAuthentication(JFrame frame, String realm, String kdc, String config) throws LoginException {
        this.frame = frame;
        System.setProperty("java.security.krb5.realm", realm);
        System.setProperty("java.security.krb5.kdc", kdc);
        System.setProperty("java.security.auth.login.config", config);
    }
    
    public void login() throws LoginException {
        lc = new LoginContext("ManualAuthentication", this);
        lc.login();
        Subject subject = lc.getSubject();
        Set s = subject.getPrincipals();
        Iterator i = s.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof KerberosPrincipal) {
                KerberosPrincipal p = (KerberosPrincipal)o;
                name = p.getName();
            }
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void logout() throws LoginException {
        //Subject subject = lc.getSubject();
        //System.out.println("Still exists:" + subject.getPrincipals());
        lc.logout();
        System.setProperty("user.name", "mblevins");
        //ncb.setName(null);
        //pcb.setPassword(null);
        //lc.getSubject();
    }
    
    public Subject getSubject() {
        return lc.getSubject();
    }
    
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                if (username == null) {
                    /*String prompt = ((NameCallback)callbacks[i]).getPrompt();
                    if ((prompt != null) && (prompt.indexOf("[") > -1) && (prompt.indexOf("]", prompt.indexOf("[")) > -1)) {
                        prompt = prompt.substring(prompt.indexOf("[") + 1, prompt.indexOf("]", prompt.indexOf("[")));
                    } else {
                        prompt = "";
                    }*/
                    String prompt = System.getenv("USERNAME");
                    /*HashMap map = SwingLoginDialog.prompt(frame, prompt);
                    username = (String)map.get("Username") + "@IECOKC.COM";
                    if (!Character.isUpperCase(username.charAt(0))) {
                        username = String.valueOf(Character.toUpperCase(username.charAt(0))) + (char)Character.toUpperCase(username.charAt(1)) + username.substring(2);
                    }
                    password = (char[])map.get("Password");*/
                    
                    JTextField userField = new JTextField();
                    userField.setName("Username");
                    userField.setText(prompt);
                    userField.setSelectionStart(0);
                    userField.setSelectionEnd(prompt.length());
                    JPasswordField passField = new JPasswordField();
                    passField.setName("Password");
                    DynamicDialog dialog = new DynamicDialog(frame, "Windows Authentication", new JComponent[] {userField, passField}, 300, 150);
                    dialog.addEmptyFieldValidation(new JComponent[] {userField, passField});
                    if (dialog.waitForAction()) {
                        username = userField.getText() + "@IECOKC.COM";
                        if (!Character.isUpperCase(username.charAt(0))) {
                            username = String.valueOf(Character.toUpperCase(username.charAt(0))) + (char)Character.toUpperCase(username.charAt(1)) + username.substring(2);
                        }
                        password = passField.getPassword();
                    } else {
                        cancelled = true;
                    }
                }
                ncb = (NameCallback)callbacks[i];
                ((NameCallback)callbacks[i]).setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                pcb = (PasswordCallback)callbacks[i];
                ((PasswordCallback)callbacks[i]).setPassword(password);
            }
        }
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
}