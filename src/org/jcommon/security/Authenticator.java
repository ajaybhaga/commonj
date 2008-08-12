/**
 * Created: Apr 26, 2007
 */
package org.jcommon.security;

/**
 * @author Matt Hicks
 *
 */
public interface Authenticator {
	public boolean isAuthenticated();
	
	public String getUsername();
	
	public String getPassword();
	
	public String getRealm();
	
	public String[] getRealms();
	
	public boolean login(String username, String password, String realm, boolean testMode);

	public void logout();
}
