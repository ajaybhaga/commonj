package org.jcommon.swing.layout;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import org.jcommon.swing.*;
import org.jcommon.swing.layout.table.*;

/**
 * @author Matt Hicks
 *
 */
public class TableLayout implements LayoutManager {
	public static final int MODE_DYNAMIC = 9;
	public static final int MODE_STATIC = 10;
	public static final int MODE_PERCENT = 11;
	public static final int ALIGN_LEFT = SwingConstants.LEFT;
    public static final int ALIGN_CENTER = SwingConstants.CENTER;
    public static final int ALIGN_RIGHT = SwingConstants.RIGHT;
    public static final int ALIGN_TOP = SwingConstants.TOP;
    public static final int ALIGN_MIDDLE = SwingConstants.CENTER;
    public static final int ALIGN_BOTTOM = SwingConstants.BOTTOM;
	
	private int columnCount;
	private HashMap<Component, TableCell> cells;
	private ArrayList<TableColumn> columns;
	private ArrayList<TableRow> rows;
	
	private Dimension minimumSize;
	private Dimension preferredSize;
	private Dimension size;
	
	private boolean fillHorizontal;
	private boolean fillVertical;
	private int horizontalAlignment;
	private int verticalAlignment;
	private Insets margin;
	private Border border;
	private Color backgroundColor;
	private Image backgroundImage;
	
	private boolean defaultCellFillHorizontal;
	private boolean defaultCellFillVertical;
	private int defaultCellHorizontalAlignment;
	private int defaultCellVerticalAlignment;
	private Border defaultCellBorder;
	private Color defaultCellBackgroundColor;
	private Insets defaultCellMargin;
	private Insets defaultCellSpacing;
	
	private int absoluteWidth;
	private int absoluteHeight;
	private int widthMode;
	private int heightMode;
	
	public TableLayout(int columnCount) {
		this.columnCount = columnCount;
		
		cells = new HashMap<Component, TableCell>();
		columns = new ArrayList<TableColumn>();
		rows = new ArrayList<TableRow>();
		
		fillHorizontal = true;
		fillVertical = true;
		horizontalAlignment = ALIGN_LEFT;
		verticalAlignment = ALIGN_TOP;
		margin = new Insets(0, 0, 0, 0);
		widthMode = MODE_DYNAMIC;
		heightMode = MODE_DYNAMIC;
		
		defaultCellFillHorizontal = false;
		defaultCellFillVertical = false;
		defaultCellHorizontalAlignment = ALIGN_LEFT;
		defaultCellVerticalAlignment = ALIGN_MIDDLE;
		defaultCellMargin = new Insets(0, 0, 0, 0);
		defaultCellSpacing = new Insets(0, 0, 0, 0);
	}
	
	public void setWidth(int width) {
		absoluteWidth = width;
	}
	
	public void setHeight(int height) {
		absoluteHeight = height;
	}
	
	public void setWidthMode(int mode) {
		widthMode = mode;
	}
	
	public void setHeightMode(int mode) {
		heightMode = mode;
	}
	
	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
	
	public int getColumnCount() {
		return columnCount;
	}
	
	public TableCell getCell(Component component) {
		if (!cells.containsKey(component)) {
			cells.put(component, new TableCell(component, this));
		}
		return cells.get(component);
	}
	
	public TableColumn getColumn(int column) {
		while (columns.size() <= column) {
			columns.add(new TableColumn());
		}
		return columns.get(column);
	}
	
	public TableRow getRow(int row) {
		while (rows.size() <= row) {
			rows.add(new TableRow());
		}
		return rows.get(row);
	}
	
	public void setFill(boolean fill) {
		fillHorizontal = fill;
		fillVertical = fill;
	}
	
	public boolean isFillHorizontal() {
		return fillHorizontal;
	}
	
	public void setFillHorizontal(boolean fillHorizontal) {
		this.fillHorizontal = fillHorizontal;
	}
	
	public boolean isFillVertical() {
		return fillVertical;
	}

	public void setFillVertical(boolean fillVertical) {
		this.fillVertical = fillVertical;
	}

	public void setHorizontalAlignment(int alignment) {
		this.horizontalAlignment = alignment;
	}
	
	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}
	
	public int getDefaultCellHorizontalAlignment() {
		return defaultCellHorizontalAlignment;
	}

	public void setDefaultCellHorizontalAlignment(int defaultCellHorizontalAlignment) {
		this.defaultCellHorizontalAlignment = defaultCellHorizontalAlignment;
	}

	public int getDefaultCellVerticalAlignment() {
		return defaultCellVerticalAlignment;
	}

	public void setDefaultCellVerticalAlignment(int defaultCellVerticalAlignment) {
		this.defaultCellVerticalAlignment = defaultCellVerticalAlignment;
	}

	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(int verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}
	
	public Border getBorder() {
		return border;
	}

	public void setBorder(Border border) {
		this.border = border;
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

	public void setDefaultCellFill(boolean fill) {
		defaultCellFillHorizontal = fill;
		defaultCellFillVertical = fill;
	}
	
	public boolean isDefaultCellFillHorizontal() {
		return defaultCellFillHorizontal;
	}
	
	public void setDefaultCellFillHorizontal(boolean defaultCellFillHorizontal) {
		this.defaultCellFillHorizontal = defaultCellFillHorizontal;
	}
	
	public boolean isDefaultCellFillVertical() {
		return defaultCellFillVertical;
	}

	public void setDefaultCellFillVertical(boolean defaultCellFillVertical) {
		this.defaultCellFillVertical = defaultCellFillVertical;
	}

	public Border getDefaultCellBorder() {
		return defaultCellBorder;
	}

	public void setDefaultCellBorder(Border defaultCellBorder) {
		this.defaultCellBorder = defaultCellBorder;
	}
	
	public Color getDefaultCellBackgroundColor() {
		return defaultCellBackgroundColor;
	}

	public void setDefaultCellBackgroundColor(Color defaultCellBackgroundColor) {
		this.defaultCellBackgroundColor = defaultCellBackgroundColor;
	}
	
	public Insets getDefaultCellMargin() {
		return defaultCellMargin;
	}

	public void setDefaultCellMargin(Insets defaultCellMargin) {
		this.defaultCellMargin = defaultCellMargin;
	}

	public Insets getDefaultCellSpacing() {
		return defaultCellSpacing;
	}
	
	public void setDefaultCellSpacing(Insets defaultCellSpacing) {
		this.defaultCellSpacing = defaultCellSpacing;
	}
	
	public Insets getMargin() {
		return margin;
	}

	public void setMargin(Insets margin) {
		this.margin = margin;
	}

	public void addLayoutComponent(String name, Component comp) {
	}
	
	public void removeLayoutComponent(Component comp) {
	}
	
	public Dimension preferredLayoutSize(Container parent) {
		initialize(parent);
		return preferredSize;
	}

	public Dimension minimumLayoutSize(Container parent) {
		initialize(parent);
		return minimumSize;
	}

	public void layoutContainer(Container parent) {
		initialize(parent);
		
		// Specify absolute sizes
		initSizes(parent);
		
		// Lay out components
		int currentColumn = 0;
		int currentRow = 0;
		
		int x = getStartX(parent);
		int y = getStartY(parent);
		TableCell cell;
		int[] used = new int[columnCount];
		for (int i = 0; i < parent.getComponentCount(); i++) {
			if (currentColumn >= columnCount) {
				y += getRow(currentRow).getHeight();
				currentColumn = 0;
				x = getStartX(parent);
				currentRow++;
			}
			if (used[currentColumn] > 0) {
				used[currentColumn]--;
				x += getColumn(currentColumn).getWidth();
				currentColumn++;
				i--;
				//System.out.println("Current: " + currentColumn + ", " + currentRow + " - " + used[currentColumn]);
				continue;
			}
			cell = getCell(parent.getComponent(i));
			int width = 0;
			for (int j = 0; j < cell.getColumnSpan(); j++) {
				width += getColumn(currentColumn + j).getWidth();
			}
			int height = 0;
			for (int j = 0; j < cell.getRowSpan(); j++) {
				height += getRow(currentRow + j).getHeight();
			}
			// TODO fix problem where rowspan pushes item to new row and row height is not initialized
			used[currentColumn] = cell.getRowSpan() - 1;
			cell.layout(x, y, width, height);
			x += getColumn(currentColumn).getWidth();
			currentColumn += cell.getColumnSpan();
		}
	}
	
	private int getStartX(Container parent) {
		int xStart = 0;
		if (getBorder() != null) {
			xStart += getBorder().getBorderInsets(parent).left;
		}
		xStart += parent.getInsets().left;
		xStart += getMargin().left;
		if (horizontalAlignment == ALIGN_CENTER) {
			xStart = (parent.getSize().width / 2) - (size.width / 2);
		} else if (horizontalAlignment == ALIGN_RIGHT) {
			xStart = parent.getSize().width - size.width;
			if (getBorder() != null) {
				xStart -= getBorder().getBorderInsets(parent).right;
			}
			xStart -= parent.getInsets().right;
			xStart -= getMargin().right;
		}
		return xStart;
	}
	
	private int getStartY(Container parent) {
		int y = 0;
		if (getBorder() != null) {
			y += getBorder().getBorderInsets(parent).top;
		}
		y += parent.getInsets().top;
		y += getMargin().top;
		if (verticalAlignment == ALIGN_MIDDLE) {
			y = (parent.getSize().height / 2) - (size.height / 2);
		} else if (verticalAlignment == ALIGN_BOTTOM) {
			y = parent.getSize().height - size.height;
			if (getBorder() != null) {
				y -= getBorder().getBorderInsets(parent).bottom;
			}
			y -= parent.getInsets().bottom;
			y -= getMargin().bottom;
		}
		return y;
	}
	
	private void initialize(Container parent) {
		if (true) {
			initSizes(parent);
			return;
		}
		
		// If columnCount has changed, we need to remove the extra columns
		while (columns.size() > columnCount) {
			columns.remove(columns.size() - 1);
		}
		
		// Iterate through children
		Component c;
		TableCell cell;
		int currentColumn = 0;
		int currentRow = 0;
		Dimension d;
		int[] used = new int[columnCount];
		for (int i = 0; i < parent.getComponentCount(); i++) {
			if (currentColumn >= columnCount) {
				currentColumn = 0;
				currentRow++;
			}
			if (used[currentColumn] > 0) {
				used[currentColumn]--;
				currentColumn++;
				continue;
			}
			c = parent.getComponent(i);
			cell = getCell(c);
			d = cell.getPreferredSize(parent);
			if (d.width / cell.getColumnSpan() > getColumn(currentColumn).getPreferredWidth()) {
				getColumn(currentColumn).setPreferredWidth(d.width / cell.getColumnSpan());
			}
			if (d.height / cell.getRowSpan() > getRow(currentRow).getPreferredHeight()) {
				getRow(currentRow).setPreferredHeight(d.height / cell.getRowSpan());
			}
			d = cell.getMinimumSize(parent);
			if (d.width / cell.getColumnSpan() > getColumn(currentColumn).getMinimumWidth()) {
				getColumn(currentColumn).setMinimumWidth(d.width / cell.getColumnSpan());
			}
			if (d.height / cell.getRowSpan() > getRow(currentRow).getMinimumHeight()) {
				getRow(currentRow).setMinimumHeight(d.height / cell.getRowSpan());
			}
			used[currentColumn] = cell.getRowSpan() - 1;
			currentColumn += cell.getColumnSpan();
		}
		int maxPreferredWidth = 0;
		int maxMinimumWidth = 0;
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).isDynamic()) {
				maxPreferredWidth += columns.get(i).getPreferredWidth();
			} else if (columns.get(i).isStatic()) {
				maxPreferredWidth += columns.get(i).getWidth();
			} else if (columns.get(i).isPercent()) {
				maxPreferredWidth += Math.round(columns.get(i).getPercent() * parent.getSize().width);
			}
			maxMinimumWidth += columns.get(i).getMinimumWidth();
		}
		int maxPreferredHeight = 0;
		int maxMinimumHeight = 0;
		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).isDynamic()) {
				maxPreferredHeight += rows.get(i).getPreferredHeight();
			} else if (rows.get(i).isStatic()) {
				maxPreferredHeight += rows.get(i).getHeight();
			} else if (rows.get(i).isPercent()) {
				maxPreferredHeight += Math.round(rows.get(i).getPercent() * parent.getSize().height);
			}
			maxMinimumHeight += rows.get(i).getMinimumHeight();
		}
		minimumSize = new Dimension(maxMinimumWidth, maxMinimumHeight);
		preferredSize = new Dimension(maxPreferredWidth, maxPreferredHeight);
		
		if (cells.size() > parent.getComponentCount()) {
			clean(parent);
		}
		
		// If the row count has changed we need to remove
		while (rows.size() > currentRow + 1) {
			rows.remove(rows.size() - 1);
		}
	}
	
	private void clean(Container parent) {
		boolean found;
		Component component;
		Iterator<Component> iterator = cells.keySet().iterator();
		while (iterator.hasNext()) {
			component = iterator.next();
			found = false;
			for (int i = 0; i < parent.getComponentCount(); i++) {
				if (parent.getComponent(i) == component) {
					found = true;
					break;
				}
			}
			if (!found) {
				iterator.remove();
			}
		}
	}
	
	private void initSizes(Container parent) {
		Dimension available = getAvailableSize(parent);
		initPreferredSize(available, parent);
		Dimension useable = getUseableSize(available, parent);
		Dimension adjusted = getAdjustedSize(useable, parent);
		Dimension preferred = getPreferredSize(parent);
		float widthConversion = (float)adjusted.width / (float)preferred.width;
		float heightConversion = (float)adjusted.height / (float)preferred.height;
//		System.out.println("Available: " + available);
//		System.out.println("Useable: " + useable);
//		System.out.println("Preferred: " + preferred);
//		System.out.println("Conversion: " + widthConversion + ", " + heightConversion);
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).isStatic()) {
				// Already defined
			} else if (columns.get(i).isPercent()) {
				columns.get(i).setWidth(Math.round(columns.get(i).getPercent() * available.width));
			} else {
				columns.get(i).setWidth(Math.round(columns.get(i).getPreferredWidth() * widthConversion));
			}
		}
		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).isStatic()) {
				// Already defined
			} else if (rows.get(i).isPercent()) {
				rows.get(i).setHeight(Math.round(rows.get(i).getPercent() * available.height));
				//System.out.println("IsPercent: " + Math.round(rows.get(i).getPercent() * adjusted.height) + ", " + adjusted.height);
			} else {
				rows.get(i).setHeight(Math.round(rows.get(i).getPreferredHeight() * heightConversion));
			}
		}
		size = new Dimension(useable.width, useable.height);
	}
	
	private Dimension getAvailableSize(Container parent) {
		int containerWidth = parent.getSize().width;
		int containerHeight = parent.getSize().height;
		if (getBorder() != null) {
			containerWidth -= getBorder().getBorderInsets(parent).left + getBorder().getBorderInsets(parent).right;
			containerHeight -= getBorder().getBorderInsets(parent).top + getBorder().getBorderInsets(parent).bottom;
		}
		containerWidth -= getMargin().left + getMargin().right;
		containerWidth -= parent.getInsets().left + parent.getInsets().right;
		containerHeight -= getMargin().top + getMargin().bottom;
		containerHeight -= parent.getInsets().top + parent.getInsets().bottom;
		return new Dimension(containerWidth, containerHeight);
	}
	
	private void initPreferredSize(Dimension available, Container parent) {
		// If columnCount has changed, we need to remove the extra columns
		while (columns.size() > columnCount) {
			columns.remove(columns.size() - 1);
		}
		
		// Iterate through children
		Component c;
		TableCell cell;
		int currentColumn = 0;
		int currentRow = 0;
		Dimension d;
		int[] used = new int[columnCount];
		for (int i = 0; i < parent.getComponentCount(); i++) {
			if (currentColumn >= columnCount) {
				currentColumn = 0;
				currentRow++;
			}
			if (used[currentColumn] > 0) {
				used[currentColumn]--;
				currentColumn++;
				continue;
			}
			c = parent.getComponent(i);
			cell = getCell(c);
			d = cell.getPreferredSize(parent);
			if (d.width / cell.getColumnSpan() > getColumn(currentColumn).getPreferredWidth()) {
				getColumn(currentColumn).setPreferredWidth(d.width / cell.getColumnSpan());
			}
			if (d.height / cell.getRowSpan() > getRow(currentRow).getPreferredHeight()) {
				getRow(currentRow).setPreferredHeight(d.height / cell.getRowSpan());
			}
			d = cell.getMinimumSize(parent);
			if (d.width / cell.getColumnSpan() > getColumn(currentColumn).getMinimumWidth()) {
				getColumn(currentColumn).setMinimumWidth(d.width / cell.getColumnSpan());
			}
			if (d.height / cell.getRowSpan() > getRow(currentRow).getMinimumHeight()) {
				getRow(currentRow).setMinimumHeight(d.height / cell.getRowSpan());
			}
			used[currentColumn] = cell.getRowSpan() - 1;
			currentColumn += cell.getColumnSpan();
		}
		int maxPreferredWidth = 0;
		int maxMinimumWidth = 0;
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).isDynamic()) {
				maxPreferredWidth += columns.get(i).getPreferredWidth();
			} else if (columns.get(i).isStatic()) {
				maxPreferredWidth += columns.get(i).getWidth();
			} else if (columns.get(i).isPercent()) {
				maxPreferredWidth += Math.round(columns.get(i).getPercent() * available.width);
			}
			maxMinimumWidth += columns.get(i).getMinimumWidth();
		}
		// Account for border on panel
		if (parent instanceof JComponent) {
			Border border = ((JComponent)parent).getBorder();
			if (border != null) {
				int borderWidth =  border.getBorderInsets(parent).left + border.getBorderInsets(parent).right;
				maxPreferredWidth += borderWidth;
				maxMinimumWidth += borderWidth;
			}
		}
		int maxPreferredHeight = 0;
		int maxMinimumHeight = 0;
		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).isDynamic()) {
				maxPreferredHeight += rows.get(i).getPreferredHeight();
			} else if (rows.get(i).isStatic()) {
				maxPreferredHeight += rows.get(i).getHeight();
			} else if (rows.get(i).isPercent()) {
				maxPreferredHeight += Math.round(rows.get(i).getPercent() * available.height);
				//System.out.println("Setting height: " + Math.round(rows.get(i).getPercent() * available.height) + ", " + available.height);
			}
			maxMinimumHeight += rows.get(i).getMinimumHeight();
		}
		// Account for border on panel
		if (parent instanceof JComponent) {
			Border border = ((JComponent)parent).getBorder();
			if (border != null) {
				int borderHeight = border.getBorderInsets(parent).top + border.getBorderInsets(parent).bottom;
				maxPreferredHeight += borderHeight;
				maxMinimumHeight += borderHeight;
			}
		}
		minimumSize = new Dimension(maxMinimumWidth, maxMinimumHeight);
		preferredSize = new Dimension(maxPreferredWidth, maxPreferredHeight);
		
		if (cells.size() > parent.getComponentCount()) {
			clean(parent);
		}
		
		// If the row count has changed we need to remove
		while (rows.size() > currentRow + 1) {
			rows.remove(rows.size() - 1);
		}
	}
	
	private Dimension getUseableSize(Dimension available, Container parent) {
		int width;
		int height;
		if (widthMode == MODE_PERCENT) {
			width = Math.round((absoluteWidth / 100.0f) * available.width);
		} else if (widthMode == MODE_STATIC) {
			width = absoluteWidth;
		} else if (isFillHorizontal()) {
			width = available.width;
		} else {
			width = preferredSize.width;
		}
		
		if (heightMode == MODE_PERCENT) {
			height = Math.round((absoluteHeight / 100.0f) * available.height);
		} else if (heightMode == MODE_STATIC) {
			height = absoluteHeight;
		} else if (isFillVertical()) {
			height = available.height;
		} else {
			height = preferredSize.height;
		}
		
		return new Dimension(width, height);
	}
	
	private Dimension getAdjustedSize(Dimension useable, Container parent) {
		int width = useable.width;
		int height = useable.height;
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).isStatic()) {
				width -= columns.get(i).getWidth();
			} else if (columns.get(i).isPercent()) {
				width -= (float)columns.get(i).getPercent() * (float)parent.getSize().width;
			}
		}
		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).isStatic()) {
				height -= rows.get(i).getHeight();
			} else if (rows.get(i).isPercent()) {
				height -= (float)rows.get(i).getPercent() * (float)parent.getSize().height;
			}
		}
		return new Dimension(width, height);
	}
	
	private Dimension getPreferredSize(Component parent) {
		int width = 0;
		int height = 0;
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).isDynamic()) {
				width += columns.get(i).getPreferredWidth();
			}
		}
		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).isDynamic()) {
				height += rows.get(i).getPreferredHeight();
			}
		}
		return new Dimension(width, height);
	}
	
	public void paintBorders(Container parent, Graphics g) {
		// Paint the table border if it exists
		if (getBorder() != null) {
			int x = getStartX(parent);
			x -= getBorder().getBorderInsets(parent).left;
			int y = getStartY(parent);
			y -= getBorder().getBorderInsets(parent).top;
			int width = size.width;
			width += getBorder().getBorderInsets(parent).left + getBorder().getBorderInsets(parent).right;
			int height = size.height;
			height += getBorder().getBorderInsets(parent).top + getBorder().getBorderInsets(parent).bottom;
			getBorder().paintBorder(parent, g, x, y, width, height);
		}
		
		// Paint each cell's border
		Iterator<TableCell> iterator = cells.values().iterator();
		while (iterator.hasNext()) {
			iterator.next().paintBorder(parent, g);
		}
	}
	
	public void paintBackground(Container parent, Graphics g) {
		int x = getStartX(parent);
		int y = getStartY(parent);
		
		// Paint the background color for the table
		if (getBackgroundColor() != null) {
			int width = size.width;
			int height = size.height;
			width += getMargin().left + getMargin().right;
			height += getMargin().top + getMargin().bottom;
			x -= getMargin().left;
			y -= getMargin().top;
			if (getBorder() != null) {
				width += getBorder().getBorderInsets(parent).left + getBorder().getBorderInsets(parent).right;
				height += getBorder().getBorderInsets(parent).top + getBorder().getBorderInsets(parent).bottom;
				x -= getBorder().getBorderInsets(parent).left;
				y -= getBorder().getBorderInsets(parent).top;
			}
			g.setColor(getBackgroundColor());
			//System.out.println("Painting background: " + x + ", " + y + ", " + width + ", " + height);
			g.fillRect(x, y, width, height);
		}
		
		// Paint the background image for the table
		if (getBackgroundImage() != null) {
			int width = size.width;
			int height = size.height;
			
			GUI.tileImage(getBackgroundImage(), g, x, y, width, height);
		}
		
		x = getStartX(parent);
		y = getStartY(parent);
		
		// Paint the background for each row
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).paintBackground(parent, g, x, y, size.width);
			y += rows.get(i).getHeight();
		}
		
		// Paint the background for each column
		y = getStartY(parent);
		for (int i = 0; i < columns.size(); i++) {
			columns.get(i).paintBackground(parent, g, x, y, size.height);
			x += columns.get(i).getWidth();
		}
		
		// Paint the background for each cell
		Iterator<TableCell> iterator = cells.values().iterator();
		while (iterator.hasNext()) {
			iterator.next().paintBackground(parent, g);
		}
	}
	
	public static void main(String[] args) throws Exception {
		/*TablePanel panel = new TablePanel(2);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		//panel.getInsets().left = 10;
		panel.getTableLayout().setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
		panel.getTableLayout().setMargin(new Insets(5, 5, 5, 5));
		panel.getTableLayout().setHorizontalAlignment(TableLayout.ALIGN_CENTER);
		//panel.getTableLayout().setVerticalAlignment(TableLayout.ALIGN_TOP);
		panel.getTableLayout().setFill(false);
		panel.getTableLayout().setDefaultCellBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		//panel.getTableLayout().setDefaultCellBackgroundColor(Color.GRAY);
		//panel.getTableLayout().setDefaultCellHorizontalAlignment(TableLayout.ALIGN_CENTER);
		panel.getTableLayout().setDefaultCellVerticalAlignment(TableLayout.ALIGN_BOTTOM);
		panel.getTableLayout().setDefaultCellFill(false);
		panel.getTableLayout().setDefaultCellMargin(new Insets(5, 5, 5, 5));
		panel.getTableLayout().setDefaultCellSpacing(new Insets(5, 5, 5, 5));
		panel.getTableLayout().getRow(0).setMode(TableRow.MODE_PERCENT);
		panel.getTableLayout().getRow(0).setPercent(0.5f);
		//panel.getTableLayout().getColumn(0).setBackgroundColor(Color.MAGENTA);
		//panel.getTableLayout().getRow(0).setBackgroundColor(Color.PINK);
		//panel.getTableLayout().setBackgroundColor(Color.CYAN);
		//panel.getTableLayout().setBackgroundImage(new ImageIcon(TableLayout.class.getResource("/org/jcommon/swing/layout/tomcat.gif")).getImage());
		panel.getTableLayout().setWidth(50);
		panel.getTableLayout().setWidthMode(TableLayout.MODE_PERCENT);
		panel.getTableLayout().setHeight(100);
		panel.getTableLayout().setHeightMode(TableLayout.MODE_PERCENT);
		
		JLabel l1 = new JLabel("Testing 1");
		panel.add(l1);
		panel.getTableLayout().getCell(l1).setColumnSpan(2);
		
		JButton button = new JButton("Testing 2");
		panel.add(button);
		panel.getTableLayout().getCell(button).setRowSpan(2);
		panel.add(new JButton("Testing 3"));
		panel.add(new JLabel("Testing 4"));
		panel.add(new JLabel("Testing 5"));
		panel.add(new JLabel("Testing 6"));*/
		
		JPanel panel = new JPanel();
		TableLayout layout = new TableLayout(2);
		//layout.setFill(false);
		panel.setLayout(layout);
		
		panel.setBorder(BorderFactory.createTitledBorder("Testing"));
		panel.add(new JButton("Test Button"));
		
		JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        //frame.getContentPane().setLayout(new BorderLayout());
        //frame.getContentPane().add(BorderLayout.CENTER, new JScrollPane(panel));
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(panel);
        frame.setVisible(true);
	}
}