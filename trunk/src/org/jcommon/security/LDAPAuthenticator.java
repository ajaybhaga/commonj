package org.jcommon.security;

import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;

public class LDAPAuthenticator implements Authenticator {
	private boolean authenticated;
	private String domain;
	private String[] realms;
	
	private String username;
	private String password;
	private String realm;
	private DirContext context;
	
	public LDAPAuthenticator(String domain, String ... realms) {
		this.domain = domain;
		this.realms = realms;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getRealm() {
		return realm;
	}
	
	public String[] getRealms() {
		return realms;
	}
	
	public boolean login(String username, String password, String realm, boolean testMode) {
		if (testMode) {
			this.username = username;
			this.password = password;
			this.realm = realm;
			authenticated = true;
			return true;
		} else {
			if ((username.length() == 0) || (password.length() == 0)) return false;
			try {
				Hashtable<String, String> env = new Hashtable<String, String>();
				env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
				env.put("java.naming.ldap.version", "3");
				env.put(Context.REFERRAL, "follow");
				env.put(Context.PROVIDER_URL, "ldap://" + domain);
				env.put(Context.SECURITY_AUTHENTICATION, "simple");
				env.put(Context.SECURITY_PRINCIPAL, realm + "\\" + username);
				env.put(Context.SECURITY_CREDENTIALS, password);
				
		        context = new InitialDirContext(env);
		        this.username = username;
		        this.password = password;
		        this.realm = realm;
		        authenticated = true;
		        return true;
			} catch(AuthenticationException exc) {
				//exc.printStackTrace();
				System.err.println("Credentials rejected for: " + username);
			} catch(NamingException exc) {
				exc.printStackTrace();
			}
			return false;
		}
	}
	
	public void logout() {
		authenticated = false;
		username = null;
	}
}
