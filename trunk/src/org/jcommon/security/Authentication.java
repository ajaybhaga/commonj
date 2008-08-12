/*
 * Created on Sep 2, 2004
 */
package org.jcommon.security;

import javax.security.auth.*;
import javax.security.auth.login.*;

/**
 * @author Matt Hicks
 */
public interface Authentication {
    public void login() throws LoginException;
    
    public String getName();
    
    public Subject getSubject();
    
    public void logout() throws LoginException;
}
