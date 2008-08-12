package org.jcommon.xml.bean;

public class XMLInstance {
	private String name;
	private Class<?> clazz;
	private XMLHandler handler;
	
	public XMLInstance(String name, Class<?> clazz, XMLHandler handler) {
		this.name = name;
		this.clazz = clazz;
		this.handler = handler;
	}

	public String getName() {
		return name;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public XMLHandler getHandler() {
		return handler;
	}
}