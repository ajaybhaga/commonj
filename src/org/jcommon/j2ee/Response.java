package org.jcommon.j2ee;

import java.util.*;

public class Response {
	private int id;
	private String referer;
	private String formIdentifier;
	private String name;
	private String value;
	private long group;
	private GregorianCalendar submitted;
	private String remoteAddress;
	private String remoteHost;
	
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public long getGroup() {
		return group;
	}

	public void setGroup(long group) {
		this.group = group;
	}

	public GregorianCalendar getSubmitted() {
		return submitted;
	}

	public void setSubmitted(GregorianCalendar submitted) {
		this.submitted = submitted;
	}

	public String getFormIdentifier() {
		return formIdentifier;
	}
	
	public void setFormIdentifier(String formIdentifier) {
		this.formIdentifier = formIdentifier;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getReferer() {
		return referer;
	}
	
	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
