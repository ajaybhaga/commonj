package org.jcommon.security;

import java.util.Map;

public class BasicAuthenticator implements Authenticator {
	private Map<String, String> map;
	
	private String username;
	private String password;
	
	public BasicAuthenticator(Map<String, String> map) {
		this.map = map;
	}
	
	public String getPassword() {
		return password;
	}

	public String getRealm() {
		return null;
	}

	public String[] getRealms() {
		return new String[0];
	}

	public String getUsername() {
		return username;
	}

	public boolean isAuthenticated() {
		return username != null;
	}

	public boolean login(String username, String password, String realm, boolean testMode) {
		if (password.equals(map.get(username))) {
			this.username = username;
			this.password = password;
			return true;
		}
		return false;
	}

	public void logout() {
		username = null;
		password = null;
	}
}
