package org.jcommon.swing.layout.table;

import java.awt.*;

import org.jcommon.swing.*;

/**
 * @author Matt Hicks
 *
 */
public class TableRow {
	public static final int MODE_DYNAMIC = 9;
	public static final int MODE_STATIC = 10;
	public static final int MODE_PERCENT = 11;
	
	private int preferredHeight;
	private int minimumHeight;
	private int height;
	private int mode;
	private float percent;
	private Color backgroundColor;
	private Image backgroundImage;
	
	public TableRow() {
		mode = MODE_DYNAMIC;
	}
	
	public void setPreferredHeight(int preferredHeight) {
		this.preferredHeight = preferredHeight;
	}
	
	public int getPreferredHeight() {
		return preferredHeight;
	}

	public int getMinimumHeight() {
		return minimumHeight;
	}

	public void setMinimumHeight(int minimumHeight) {
		this.minimumHeight = minimumHeight;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
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

	public float getPercent() {
		return percent;
	}
	
	public void setPercent(float percent) {
		this.percent = percent;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
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

	public void paintBackground(Container parent, Graphics g, int x, int y, int width) {
		if (getBackgroundColor() != null) {
			g.setColor(getBackgroundColor());
			g.fillRect(x, y, width, getHeight());
		}
		if (getBackgroundImage() != null) {
			GUI.tileImage(getBackgroundImage(), g, x, y, width, getHeight());
		}
	}
}
