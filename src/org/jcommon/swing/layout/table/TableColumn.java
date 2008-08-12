package org.jcommon.swing.layout.table;

import java.awt.*;

import org.jcommon.swing.*;

/**
 * @author Matt Hicks
 *
 */
public class TableColumn {
	public static final int MODE_DYNAMIC = 9;
	public static final int MODE_STATIC = 10;
	public static final int MODE_PERCENT = 11;
	
	private int preferredWidth;
	private int minimumWidth;
	private int width;
	private int mode;
	private float percent;
	private Color backgroundColor;
	private Image backgroundImage;
	
	public TableColumn() {
		mode = MODE_DYNAMIC;
	}
	
	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}
	
	public int getPreferredWidth() {
		return preferredWidth;
	}

	public int getMinimumWidth() {
		return minimumWidth;
	}

	public void setMinimumWidth(int minimumWidth) {
		this.minimumWidth = minimumWidth;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public float getPercent() {
		return percent;
	}
	
	public void setPercent(float percent) {
		this.percent = percent;
	}
	
	public boolean isDynamic() {
		return mode == MODE_DYNAMIC;
	}
	
	public boolean isStatic() {
		return mode == MODE_STATIC;
	}
	
	public boolean isPercent() {
		return mode == MODE_PERCENT;
	}

	public void paintBackground(Container parent, Graphics g, int x, int y, int height) {
		if (getBackgroundColor() != null) {
			g.setColor(getBackgroundColor());
			g.fillRect(x, y, getWidth(), height);
		}
		if (getBackgroundImage() != null) {
			GUI.tileImage(getBackgroundImage(), g, x, y, getWidth(), height);
		}
	}
}