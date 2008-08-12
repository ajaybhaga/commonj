package org.jcommon.swing;

import java.awt.*;

import javax.swing.*;

public class BasicLayout implements LayoutManager {
	public static final int ALIGN_LEFT = SwingConstants.LEFT;
    public static final int ALIGN_CENTER = SwingConstants.CENTER;
    public static final int ALIGN_RIGHT = SwingConstants.RIGHT;
    public static final int ALIGN_TOP = SwingConstants.TOP;
    public static final int ALIGN_MIDDLE = SwingConstants.CENTER;
    public static final int ALIGN_BOTTOM = SwingConstants.BOTTOM;
	
	private int horizontalAlignment;
	private int verticalAlignment;
	
	private int preferredWidth;
	private int preferredHeight;
	private int minimumWidth;
	private int minimumHeight;
	
	public BasicLayout() {
		horizontalAlignment = ALIGN_LEFT;
		verticalAlignment = ALIGN_TOP;
	}
	
	public void setHorizontalAlignment(int alignment) {
		this.horizontalAlignment = alignment;
	}

	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setVerticalAlignment(int alignment) {
		this.verticalAlignment = alignment;
	}

	public int getVerticalAlignment() {
		return verticalAlignment;
	}
	
	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public Dimension preferredLayoutSize(Container parent) {
		determineValues(parent, null);
		return new Dimension(preferredWidth, preferredHeight);
	}

	public Dimension minimumLayoutSize(Container parent) {
		determineValues(parent, null);
		return new Dimension(minimumWidth, minimumHeight);
	}

	public void layoutContainer(Container parent) {
		// TODO convert this to allow for minimum size as well
		//parent.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
		//parent.setSize(new Dimension(preferredWidth, preferredHeight));
		//System.out.println("layingout: " + preferredWidth);
		Component c;
		int x = 0;
		int y = 0;
		int maxHeight = 0;
		int rowWidth = getRowWidth(parent, 0);
		//System.out.println("Layout Container: " + parent.getComponentCount() + ":" + parent.getSize().width + ", " + parent.getSize().height);
		for (int i = 0; i < parent.getComponentCount(); i++) {
			c = parent.getComponent(i);
			if (c instanceof BlankComponent) {
				rowWidth = getRowWidth(parent, i + 1);
				if (getHorizontalAlignment() == ALIGN_LEFT) {
					x = 0;
				} else if (getHorizontalAlignment() == ALIGN_CENTER) {
					x = (preferredWidth / 2) - (rowWidth / 2);
				} else if (getHorizontalAlignment() == ALIGN_RIGHT) {
					x = preferredWidth - rowWidth;
				}
				if (maxHeight < 16) maxHeight = 16;
				y += maxHeight;
				maxHeight = 0;
				rowWidth = -1;
			} else {
				//System.out.println("Placing at " + x + ", " + y + ":" + c.getPreferredSize().width + ", " + c.getPreferredSize().height);
				c.setBounds(x, y, c.getPreferredSize().width, c.getPreferredSize().height);
				x += c.getPreferredSize().width;
				if (c.getPreferredSize().height > maxHeight) maxHeight = c.getPreferredSize().height;
			}
		}
	}
	
	private static int getRowWidth(Container parent, int start) {
		int width = 0;
		Component c;
		for (int i = start; i < parent.getComponentCount(); i++) {
			c = parent.getComponent(i);
			if (c instanceof BlankComponent) {
				break;
			} else {
				width += c.getPreferredSize().width;
			}
		}
		return width;
	}
	
	private void determineValues(Container parent, Dimension size) {
		int maxWidthPreferred = 0;
		int widthPreferred = 0;
		int maxWidthMinimum = 0;
		int widthMinimum = 0;
		int maxHeightPreferred = 0;
		int heightPreferred = 0;
		int maxHeightMinimum = 0;
		int heightMinimum = 0;
		
		preferredWidth = 0;
		preferredHeight = 0;
		
		Component c;
		for (int i = 0; i < parent.getComponentCount(); i++) {
			c = parent.getComponent(i);
			if (c instanceof BlankComponent) {
				if (widthPreferred > maxWidthPreferred) {
					maxWidthPreferred = widthPreferred;
				}
				if (widthMinimum > maxWidthMinimum) {
					maxWidthMinimum = widthMinimum;
				}
				widthPreferred = 0;
				widthMinimum = 0;
				if (heightPreferred > maxHeightPreferred) {
					maxHeightPreferred = heightPreferred;
				}
				if (heightMinimum > maxHeightMinimum) {
					maxHeightMinimum = heightMinimum;
				}
				heightPreferred = 0;
				heightMinimum = 0;
				if (maxHeightPreferred < 16) maxHeightPreferred = 16;
				preferredHeight += maxHeightPreferred;
				maxHeightPreferred = 0;
			} else {
				widthPreferred += c.getPreferredSize().width;
				//widthMinimum += c.getMinimumSize().width;
				//System.out.println("Width: " + widthPreferred);
				if (c.getPreferredSize().height > heightPreferred) heightPreferred = c.getPreferredSize().height;
				//if (c.getMinimumSize().height > heightMinimum) heightMinimum = c.getMinimumSize().height;
			}
		}
		if (widthPreferred > maxWidthPreferred) {
			maxWidthPreferred = widthPreferred;
		}
		if (widthMinimum > maxWidthMinimum) {
			maxWidthMinimum = widthMinimum;
		}
		preferredWidth = maxWidthPreferred;
		minimumWidth = maxWidthMinimum;
		if (heightPreferred > maxHeightPreferred) {
			maxHeightPreferred = heightPreferred;
		}
		preferredHeight += maxHeightPreferred;
		
		if (parent.getParent().getSize().width > preferredWidth) {
			//System.out.println("PreferredWidth was: " + preferredWidth + ", is " + parent.getParent().getSize().width);
			preferredWidth = parent.getParent().getSize().width;
		}
		if (parent.getParent().getSize().height > preferredHeight) preferredHeight = parent.getParent().getSize().height;
	}
}
