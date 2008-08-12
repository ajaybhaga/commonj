package org.jcommon.swing.layout.table;

import java.awt.*;

import javax.swing.border.*;

import org.jcommon.swing.*;
import org.jcommon.swing.layout.TableLayout;

/**
 * @author Matt Hicks
 *
 */
public class TableCell {
	public static final int MODE_DYNAMIC = 9;
	public static final int MODE_STATIC = 10;
	public static final int MODE_PERCENT = 11;
	
	private Component component;
	private TableLayout layout;
	private int columnSpan;
	private int rowSpan;
	private Integer horizontalAlignment;
	private Integer verticalAlignment;
	private Border border;
	private Color backgroundColor;
	private Image backgroundImage;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private int absoluteWidth;
	private int absoluteHeight;
	private int mode;
	
	private Boolean fillHorizontal;
	private Boolean fillVertical;
	private Insets margin;
	private Insets spacing;
	
	public TableCell(Component component, TableLayout layout) {
		this.component = component;
		this.layout = layout;
		
		columnSpan = 1;
		rowSpan = 1;
		mode = MODE_DYNAMIC;
	}
	
	public void setWidth(int width) {
		absoluteWidth = width;
	}
	
	public void setHeight(int height) {
		absoluteHeight = height;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public int getMode() {
		return mode;
	}
	
	public int getColumnSpan() {
		return columnSpan;
	}
	
	public void setColumnSpan(int columnSpan) {
		this.columnSpan = columnSpan;
	}
	
	public int getRowSpan() {
		return rowSpan;
	}
	
	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}
	
	public int getHorizontalAlignment() {
		if (horizontalAlignment != null) {
			return horizontalAlignment;
		}
		return layout.getDefaultCellHorizontalAlignment();
	}
	
	public void setHorizontalAlignment(int alignment) {
		this.horizontalAlignment = alignment;
	}
	
	public int getVerticalAlignment() {
		if (verticalAlignment != null) {
			return verticalAlignment;
		}
		return layout.getDefaultCellVerticalAlignment();
	}
	
	public void setVerticalAlignment(int alignment) {
		this.verticalAlignment = alignment;
	}
	
	public void setFill(boolean fill) {
		fillHorizontal = fill;
		fillVertical = fill;
	}
	
	public void setFillHorizontal(boolean fillHorizontal) {
		this.fillHorizontal = fillHorizontal;
	}
	
	public boolean isFillHorizontal() {
		if (fillHorizontal != null) {
			return fillHorizontal;
		}
		return layout.isDefaultCellFillHorizontal();
	}
	
	public Boolean isFillVertical() {
		if (fillVertical != null) {
			return fillVertical;
		}
		return layout.isDefaultCellFillVertical();
	}

	public void setFillVertical(Boolean fillVertical) {
		this.fillVertical = fillVertical;
	}
	
	public Insets getMargin() {
		if (margin != null) {
			return margin;
		}
		return layout.getDefaultCellMargin();
	}

	public void setMargin(Insets margin) {
		this.margin = margin;
	}

	public Insets getSpacing() {
		if (spacing != null) {
			return spacing;
		}
		return layout.getDefaultCellSpacing();
	}
	
	public void setSpacing(Insets spacing) {
		this.spacing = spacing;
	}
	
	public Dimension getPreferredSize(Container parent) {
		if (mode == MODE_STATIC) {
			return new Dimension(absoluteWidth, absoluteHeight);
		} else if (mode == MODE_PERCENT) {
			return new Dimension(Math.round((absoluteWidth / 100.0f) * parent.getSize().width), Math.round((absoluteHeight / 100.0f) * parent.getSize().height));
		} else {
			Dimension size = component.getPreferredSize();
			if (getBorder() != null) {
				size.width += getBorder().getBorderInsets(component).left + getBorder().getBorderInsets(component).right;
				size.height += getBorder().getBorderInsets(component).top + getBorder().getBorderInsets(component).bottom;
			}
			size.width += getMargin().left + getMargin().right;
			size.height += getMargin().top + getMargin().bottom;
			size.width += getSpacing().left + getSpacing().right;
			size.height += getSpacing().top + getSpacing().bottom;
			return size;
		}
	}
	
	public Dimension getMinimumSize(Container parent) {
		if (mode == MODE_STATIC) {
			return new Dimension(absoluteWidth, absoluteHeight);
		} else if (mode == MODE_PERCENT) {
			return new Dimension(Math.round((absoluteWidth / 100.0f) * parent.getSize().width), Math.round((absoluteHeight / 100.0f) * parent.getSize().height));
		} else {
			Dimension size = component.getMinimumSize();
			if (getBorder() != null) {
				size.width += getBorder().getBorderInsets(component).left + getBorder().getBorderInsets(component).right;
				size.height += getBorder().getBorderInsets(component).top + getBorder().getBorderInsets(component).bottom;
			}
			size.width += getMargin().left + getMargin().right;
			size.height += getMargin().top + getMargin().bottom;
			size.width += getSpacing().left + getSpacing().right;
			size.height += getSpacing().top + getSpacing().bottom;
			return size;
		}
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return x;
	}
	
	public int getXOffset() {
		int temp = getX();
		if (getHorizontalAlignment() == TableLayout.ALIGN_CENTER) {
			temp += (width / 2) - (getWidth() / 2);
		} else if (getHorizontalAlignment() == TableLayout.ALIGN_RIGHT) {
			temp += width - getWidth();
			if (getBorder() != null) {
				temp -= getBorder().getBorderInsets(component).right;
			}
			temp -= getMargin().right;
			temp -= getSpacing().right;
		} else {
			if (getBorder() != null) {
				temp += getBorder().getBorderInsets(component).left;
			}
			temp += getMargin().left;
			temp += getSpacing().left;
		}
		return temp;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getY() {
		return y;
	}
	
	public int getYOffset() {
		int temp = getY();
		if (getVerticalAlignment() == TableLayout.ALIGN_MIDDLE) {
			temp += (height / 2) - (getHeight() / 2);
		} else if (getVerticalAlignment() == TableLayout.ALIGN_BOTTOM) {
			temp += height - getHeight();
			if (getBorder() != null) {
				temp -= getBorder().getBorderInsets(component).bottom;
			}
			temp -= getMargin().bottom;
			temp -= getSpacing().bottom;
		} else {
			if (getBorder() != null) {
				temp += getBorder().getBorderInsets(component).top;
			}
			temp += getMargin().top;
			temp += getSpacing().top;
		}
		return temp;
	}
	
	public int getWidth() {
		if (isFillHorizontal()) {
			return getWidthFill();
		} else {
			if (component.getPreferredSize().width > width) {
				return getWidthFill();
			}
			return component.getPreferredSize().width;
		}
	}
	
	private int getWidthFill() {
		int temp = width;
		if (getBorder() != null) {
			temp -= getBorder().getBorderInsets(component).left + getBorder().getBorderInsets(component).right;
		}
		temp -= getMargin().left + getMargin().right;
		temp -= getSpacing().left + getSpacing().right;
		return temp;
	}
	
	public int getHeight() {
		if (isFillVertical()) {
			return getHeightFill();
		} else {
			if (component.getPreferredSize().height > height) {
				return getHeightFill();
			}
			return component.getPreferredSize().height;
		}
	}
	
	private int getHeightFill() {
		int temp = height;
		if (getBorder() != null) {
			temp -= getBorder().getBorderInsets(component).top + getBorder().getBorderInsets(component).bottom;
		}
		temp -= getMargin().top + getMargin().bottom;
		temp -= getSpacing().top + getSpacing().bottom;
		return temp;
	}
	
	public Border getBorder() {
		if (border != null) {
			return border;
		}
		return layout.getDefaultCellBorder();
	}

	public void setBorder(Border border) {
		this.border = border;
	}

	public Color getBackgroundColor() {
		if (backgroundColor != null) {
			return backgroundColor;
		}
		return layout.getDefaultCellBackgroundColor();
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

	public void layout(int x, int y, int width, int height) {
		setX(x);
		setY(y);
		this.width = width;
		this.height = height;
		component.setBounds(getXOffset(), getYOffset(), getWidth(), getHeight());
	}
	
	public void paintBorder(Container parent, Graphics g) {
		if (getBorder() != null) {
			int x = getX() + getSpacing().left;
			int y = getY() + getSpacing().top;
			int width = this.width - (getSpacing().left + getSpacing().right);
			int height = this.height - (getSpacing().top + getSpacing().bottom);
			getBorder().paintBorder(parent, g, x, y, width, height);
		}
	}
	
	public void paintBackground(Container parent, Graphics g) {
		int x = getX() + getSpacing().left;
		int y = getY() + getSpacing().top;
		int width = this.width - (getSpacing().left + getSpacing().right);
		int height = this.height - (getSpacing().top + getSpacing().bottom);
		if (getBackgroundColor() != null) {
			g.setColor(getBackgroundColor());
			//System.out.println("Painting background: " + x + ", " + y + ", " + width + ", " + height);
			g.fillRect(x, y, width, height);
		}
		if (getBackgroundImage() != null) {
			GUI.tileImage(getBackgroundImage(), g, x, y, width, height);
		}
	}
}