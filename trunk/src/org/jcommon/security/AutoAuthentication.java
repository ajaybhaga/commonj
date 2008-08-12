/*
 * Created on Aug 24, 2004
 */
package org.jcommon.security;

import java.util.*;

import javax.security.auth.*;
import javax.security.auth.kerberos.*;
import javax.security.auth.login.*;
import javax.security.auth.callback.*;

/**
 * @author Matt Hicks
 */
public class AutoAuthentication implements Authentication, CallbackHandler {
    protected LoginContext lc;
    
    protected String name;
    
    public AutoAuthentication(String realm, String kdc, String config) throws LoginException {
        System.setProperty("java.security.krb5.realm", realm);
        System.setProperty("java.security.krb5.kdc", kdc);
        System.setProperty("java.security.auth.login.config", config);
        lc = new LoginContext("AutoAuthentication", this);
    }
    
    public void login() throws LoginException {
        lc.login();
        Subject subject = lc.getSubject();
        Set s = subject.getPrincipals();
        Iterator i = s.iterator();
        while (i.hasNext()) {
            Object o = i.next();
            if (o instanceof KerberosPrincipal) {
                KerberosPrincipal p = (KerberosPrincipal)o;
                name = p.getName();
                //System.out.println("Name: " + p.getName() + ", " + p.getNameType());
            }
        }
    }
    
    public String getName() {
        return name;
    }
    
    public Subject getSubject() {
        return lc.getSubject();
    }
    
    public void logout() throws LoginException {
        lc.logout();
    }
    
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            throw new UnsupportedCallbackException(callbacks[i]);
        }
    }
}
