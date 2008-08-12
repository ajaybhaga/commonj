package org.jcommon.j2ee;

public class NTLMUser {
	private String username;
	private String remoteHost;
	private String domain;
	
	protected NTLMUser(String username, String remoteHost, String domain) {
		this.username = username;
		this.remoteHost = remoteHost;
		this.domain = domain;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getRemoteHost() {
		return remoteHost;
	}
	
	public String getDomain() {
		return domain;
	}
}
