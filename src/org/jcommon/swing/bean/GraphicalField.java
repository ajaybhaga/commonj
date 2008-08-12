package org.jcommon.swing.bean;

import java.awt.*;
import java.lang.reflect.*;

public abstract class GraphicalField {
	private Component component;
	protected Object object;
	protected Field field;
	
	public GraphicalField(Component component, Object object, Field field) {
		this.component = component;
		this.object = object;
		this.field = field;
	}
	
	public Component getComponent() {
		return component;
	}
	
	public abstract void updateFromBean() throws IllegalArgumentException, IllegalAccessException;

	public abstract void updateToBean() throws IllegalArgumentException, IllegalAccessException;
}