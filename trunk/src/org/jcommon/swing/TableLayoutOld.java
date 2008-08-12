/*
 * Created on May 16, 2005
 */
package org.jcommon.swing;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import org.jcommon.swing.table.*;

/**
 * @author Matt Hicks
 * @deprecated use org.jcommon.swing.layout.TableLayout instead
 */
public class TableLayoutOld implements LayoutManager {
    public static boolean showWarnings = true;
    
    public static final int ALIGN_LEFT = SwingConstants.LEFT;
    public static final int ALIGN_CENTER = SwingConstants.CENTER;
    public static final int ALIGN_RIGHT = SwingConstants.RIGHT;
    public static final int ALIGN_TOP = SwingConstants.TOP;
    public static final int ALIGN_MIDDLE = SwingConstants.CENTER;
    public static final int ALIGN_BOTTOM = SwingConstants.BOTTOM;
    
    private int columns;
    private float[] percentages;
    private int defaultTopMargin;
    private int defaultBottomMargin;
    private int defaultLeftMargin;
    private int defaultRightMargin;
    private boolean defaultFill;
    private int defaultHorizontalAlignment;
    private int defaultVerticalAlignment;
    private int defaultVerticalContainerAlignment;
    
    private int[] widths;
    private int[] heights;
    
    private HashMap colSpans;
    private HashMap rowSpans;
    private HashMap horizontalAlignments;
    private HashMap verticalAlignments;
    private HashMap fills;
    private HashSet disabledVerticalStretchingRows;
    private HashMap topPadding;
    private HashMap bottomPadding;
    private HashMap leftPadding;
    private HashMap rightPadding;
    private HashMap backgroundColors;
    
    private HashMap absoluteComponents;
    
    private HashMap bounds;
    
    private Border defaultCellBorder;
    private HashMap cellBorders;
    
    private int[][] layout;
    private int maxWide = 0;
    private int maxTall = 0;
    
    // TODO implement capability of setting percentages for some columns and absolute sizes for other columns
    // Perhaps use Object[] instead of float[] and use Float for percentages and Integer for absolute?
    public TableLayoutOld(int columns) {
        this.columns = columns;
        colSpans = new HashMap();
        rowSpans = new HashMap();
        horizontalAlignments = new HashMap();
        verticalAlignments = new HashMap();
        fills = new HashMap();
        defaultTopMargin = 0;
        defaultBottomMargin = 0;
        defaultLeftMargin = 0;
        defaultRightMargin = 0;
        topPadding = new HashMap();
        bottomPadding = new HashMap();
        leftPadding = new HashMap();
        rightPadding = new HashMap();
        defaultFill = true;
        defaultHorizontalAlignment = ALIGN_LEFT;
        defaultVerticalAlignment = ALIGN_TOP;
        defaultVerticalContainerAlignment = ALIGN_TOP;
        disabledVerticalStretchingRows = new HashSet();
        absoluteComponents = new HashMap();
        cellBorders = new HashMap();
        backgroundColors = new HashMap();
    }
    
    public TableLayoutOld(float[] percentages) {
        this(percentages.length);
        this.percentages = percentages;
        columns = percentages.length;
        float total = 0;
        for (int i = 0; i < percentages.length; i++) {
            total += percentages[i];
        }
        if (total != 1) {
            if (showWarnings) System.err.println("WARNING: Percentages assigned to TableLayout does not equal 100% (" + (total * 100) + "% used).");
        }
    }
    
    public TableLayoutOld(float[] percentages, boolean defaultFill) {
        this(percentages);
        this.defaultFill = defaultFill;
    }
    
    public void setColumnCount(int columns) {
    	this.columns = columns;
    }
    
    public int getColumnCount() {
    	return columns;
    }
    
    public void setDefaultCellBorder(Border defaultCellBorder) {
    	this.defaultCellBorder = defaultCellBorder;
    }
    
    public void setCellBorder(Component c, Border border) {
    	cellBorders.put(c, border);
    }
    
    public Border getCellBorder(Component c) {
    	Border b = defaultCellBorder;
    	if (cellBorders.containsKey(c)) {
    		b = (Border)cellBorders.get(c);
    	}
    	return b;
    }
    
    public void setBackgroundColor(Component c, Color color) {
    	backgroundColors.put(c, color);
    }
    
    public Color getBackgroundColor(Component c) {
    	return (Color)backgroundColors.get(c);
    }
    
    public void setDefaultFill(boolean defaultFill) {
        this.defaultFill = defaultFill;
    }
    
    public void setDefaultHorizontalAlignment(int defaultHorizontalAlignment) {
        this.defaultHorizontalAlignment = defaultHorizontalAlignment;
    }
    
    public void setDefaultVerticalAlignment(int defaultVerticalAlignment) {
        this.defaultVerticalAlignment = defaultVerticalAlignment;
    }
    
    public void setDefaultVerticalContainerAlignment(int defaultVerticalContainerAlignment) {
        this.defaultVerticalContainerAlignment = defaultVerticalContainerAlignment;
    }
    
    public void setHorizontalAlignment(Component c, int alignment) {
        horizontalAlignments.put(c, new Integer(alignment));
    }
    
    public int getHorizontalAlignment(Component c) {
    	int align = defaultHorizontalAlignment;
    	if (horizontalAlignments.containsKey(c)) {
    		align = ((Integer)horizontalAlignments.get(c)).intValue();
    	}
    	return align;
    }
    
    public void setVerticalAlignment(Component c, int alignment) {
        verticalAlignments.put(c, new Integer(alignment));
    }
    
    public int getVerticalAlignment(Component c) {
    	int align = defaultVerticalAlignment;
    	if (verticalAlignments.containsKey(c)) {
    		align = ((Integer)verticalAlignments.get(c)).intValue();
    	}
    	return align;
    }
    
    public void setColSpan(Component c, int span) {
        colSpans.put(c, new Integer(span));
    }
    
    public int getColSpan(Component c) {
    	int span = 1;
    	if (colSpans.containsKey(c)) {
    		span = ((Integer)colSpans.get(c)).intValue();
    	}
    	return span;
    }
    
    public void setRowSpan(Component c, int span) {
        rowSpans.put(c, new Integer(span));
    }
    
    public int getRowSpan(Component c) {
    	int span = 1;
    	if (rowSpans.containsKey(c)) {
    		span = ((Integer)rowSpans.get(c)).intValue();
    	}
    	return span;
    }
    
    public void setFill(Component c, boolean fill) {
        fills.put(c, new Boolean(fill));
    }
    
    public void setAbsolute(Component c, Point p) {
        absoluteComponents.put(c, p);
    }
    
    public void disableVerticalRowStretching(int row) {
        disabledVerticalStretchingRows.add(new Integer(row));
    }
    
    public void setComponentMargins(int top, int bottom, int left, int right) {
        defaultTopMargin = top;
        defaultBottomMargin = bottom;
        defaultLeftMargin = left;
        defaultRightMargin = right;
    }
    
    public void setLeftMargin(Component c, int value) {
    	leftPadding.put(c, new Integer(value));
    }
    
    public int getLeftMargin(Component c) {
    	int padding = defaultLeftMargin;
    	if (leftPadding.containsKey(c)) {
    		padding = ((Integer)leftPadding.get(c)).intValue();
    	}
    	return padding;
    }
    
    public void setRightMargin(Component c, int value) {
    	rightPadding.put(c, new Integer(value));
    }
    
    public int getRightMargin(Component c) {
    	int padding = defaultRightMargin;
    	if (rightPadding.containsKey(c)) {
    		padding = ((Integer)rightPadding.get(c)).intValue();
    	}
    	return padding;
    }
    
    public void setTopMargin(Component c, int value) {
    	topPadding.put(c, new Integer(value));
    }
    
    public int getTopMargin(Component c) {
    	int padding = defaultTopMargin;
    	if (topPadding.containsKey(c)) {
    		padding = ((Integer)topPadding.get(c)).intValue();
    	}
    	return padding;
    }
    
    public void setBottomMargin(Component c, int value) {
    	bottomPadding.put(c, new Integer(value));
    }
    
    public int getBottomMargin(Component c) {
    	int padding = defaultBottomMargin;
    	if (bottomPadding.containsKey(c)) {
    		padding = ((Integer)bottomPadding.get(c)).intValue();
    	}
    	return padding;
    }
    
    public void addLayoutComponent(String name, Component comp) {
        //System.out.println("Add layout component: " + name);
    }

    public void removeLayoutComponent(Component comp) {
        //System.out.println("Remove layout component!");
    }

    public Dimension preferredLayoutSize(Container target) {
        layoutContainer(target);
        return new Dimension(maxWide, maxTall);
    }

    public Dimension minimumLayoutSize(Container target) {
        //System.out.println("Minimum Size: " + target.getClass().getName());
        return null;
    }
    
    public void layoutContainer(Container container) {
        buildArray(container);
        
        buildMaxHeights(container);
        
        buildMaxWidths(container);
        
        
        bounds = new HashMap();
        // Layout Components
        for (int i = 0; i < container.getComponentCount(); i++) {
            layoutComponent(container.getComponent(i), i);
        }
    }
    
    private void layoutComponent(Component c, int componentID) {
        // Absolute position if in absolute positioning
        if (absoluteComponents.containsKey(c)) {
            Point p = (Point)absoluteComponents.get(c);
            c.setBounds(p.x, p.y, c.getPreferredSize().width, c.getPreferredSize().height);
            return;
        }
        
        // Determine container height
        int containerHeight = 0;
        for (int i = 0; i < heights.length; i++) {
            containerHeight += heights[i];
        }
        
        // Determine x, y position on grid
        int x = 0;
        int y = 0;
        loop: for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[0].length; col++) {
                if (layout[row][col] == componentID) {
                    x += col;
                    y += row;
                    break loop;
                }
            }
        }
        
        // Determine width and height
        int width = 0;
        int height = 0;
        int colSpan = 1;
        if (colSpans.get(c) != null) {
            colSpan = ((Integer)colSpans.get(c)).intValue();
        }
        int rowSpan = 1;
        if (rowSpans.get(c) != null) {
            rowSpan = ((Integer)rowSpans.get(c)).intValue();
        }
        for (int i = 0; i < colSpan; i++) {
            width += widths[x + i];
        }
        for (int i = 0; i < rowSpan; i++) {
            height += heights[y + i];
        }
        
        // Set X position
        int xOffset = 0;
        xOffset = c.getParent().getInsets().left;
        for (int i = 0; i < x; i++) {
            xOffset += widths[i];
        }
        x = xOffset;
        
        // Set Y Position
        int yOffset = 0;
        yOffset = c.getParent().getInsets().top;
        for (int i = 0; i < y; i++) {
            yOffset += heights[i];
        }
        y = yOffset;
        

        // Set bounds
    	bounds.put(c, new Rectangle2D.Float(x, y, width, height));
        
        //System.out.println("Component: " + componentID + ", " + x + ", " + y);
        
        // If Default Fill is turned off then set it to the preferred size if smaller than column
        int leftMargin = defaultLeftMargin;
        int rightMargin = defaultRightMargin;
        int topMargin = defaultTopMargin;
        int bottomMargin = defaultBottomMargin;
        if (topPadding.containsKey(c)) {
        	topMargin = ((Integer)topPadding.get(c)).intValue();
        } else if (bottomPadding.containsKey(c)) {
        	bottomMargin = ((Integer)bottomPadding.get(c)).intValue();
        } else if (leftPadding.containsKey(c)) {
        	leftMargin = ((Integer)leftPadding.get(c)).intValue();
        } else if (rightPadding.containsKey(c)) {
        	rightMargin = ((Integer)rightPadding.get(c)).intValue();
        }
        
        int horizontalAlignment = defaultHorizontalAlignment;
        if (horizontalAlignments.get(c) != null) {
            horizontalAlignment = ((Integer)horizontalAlignments.get(c)).intValue();
        }
        int verticalAlignment = defaultVerticalAlignment;
        if (verticalAlignments.get(c) != null) {
            verticalAlignment = ((Integer)verticalAlignments.get(c)).intValue();
        }
        boolean fill = defaultFill;
        if (fills.get(c) != null) {
            fill = ((Boolean)fills.get(c)).booleanValue();
        }
        if (!fill) {
            Dimension d = c.getPreferredSize();
            if (d.width < width + leftMargin + rightMargin) {
                if (horizontalAlignment == ALIGN_RIGHT) {
                    x += width - d.width - rightMargin;
                } else if ((horizontalAlignment == ALIGN_CENTER) || (horizontalAlignment == ALIGN_MIDDLE)) {
                    int componentWidth = d.width;
                    if (c instanceof JComponent) {
                        componentWidth += ((JComponent)c).getInsets().left;
                    }
                    x += (width - componentWidth) / 2;
                }
                width = d.width + leftMargin + rightMargin;
            }
            if (d.height < height + topMargin + bottomMargin) {
                if (verticalAlignment == ALIGN_BOTTOM) {
                    y += height - d.height - bottomMargin;
                } else if ((verticalAlignment == ALIGN_MIDDLE) || (verticalAlignment == ALIGN_CENTER)) {
                    y += (height - d.height) / 2;
                }
                height = d.height + topMargin + bottomMargin;
            }
        }
        // Adjust for internal margins
        x += leftMargin;
        y += topMargin;
        width -= leftMargin + rightMargin;
        height -= topMargin + bottomMargin;
        //System.out.println("BoundsLayout: " + x + ", " + y + ", " + width + ", " + height);
        
        //System.out.println("Bounds: " + x + ", " + y + ", " + width + ", " + height + ", " + componentID);
        if (defaultVerticalContainerAlignment == ALIGN_BOTTOM) {
            y += c.getParent().getSize().height - c.getParent().getInsets().bottom;
            y -= containerHeight;
        } else if ((defaultVerticalContainerAlignment == ALIGN_MIDDLE) || (defaultVerticalContainerAlignment == ALIGN_CENTER)) {
            y += (c.getParent().getSize().height - c.getParent().getInsets().bottom) / 2;
            y -= containerHeight / 2;
        }
        c.setBounds(x, y, width, height);
        if (x + width + rightMargin + 1 > maxWide) maxWide = x + width + rightMargin + 1;
        if (y + height + bottomMargin + 1 > maxTall) maxTall = y + height + bottomMargin + 1;
    }
    
    private void buildArray(Container container) {
        int total = 0;
        Component c;
        
        // Determine total necessary points on grid
        int cols;
        int rows;
        for (int i = 0; i < container.getComponentCount(); i++) {
            c = container.getComponent(i);
            cols = 1;
            if (colSpans.get(c) != null) {
                cols = ((Integer)colSpans.get(c)).intValue();
            }
            rows = 1;
            if (rowSpans.get(c) != null) {
                rows = ((Integer)rowSpans.get(c)).intValue();
            }
            total += cols * rows;
        }
        
        rows = (int)Math.round(Math.ceil((float)total / (float)columns));
        //System.out.println("Total: " + total + ", Rows: " + rows + ", Components: " + container.getComponentCount());
        
        layout = new int[rows][columns];
        // -1 the whole thing
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[0].length; j++) {
                layout[i][j] = -1;
            }
        }
        
        for (int i = 0; i < container.getComponentCount(); i++) {
            c = container.getComponent(i);
            cols = 1;
            if (colSpans.get(c) != null) {
                cols = ((Integer)colSpans.get(c)).intValue();
            }
            rows = 1;
            if (rowSpans.get(c) != null) {
                rows = ((Integer)rowSpans.get(c)).intValue();
            }
            findAndUse(c, i, cols, rows);
        }
        
        /*System.out.println("Layout: " + layout.length);
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[0].length; j++) {
                System.out.print(layout[i][j] + ", ");
            }
            System.out.println("");
        }*/
    }
    
    private void findAndUse(Component c, int componentID, int cols, int rows) {
        // Find next available cell
        yCheck: for (int y = 0; y < layout.length; y++) {
            xCheck: for (int x = 0; x < layout[0].length; x++) {
                if (y + rows > layout.length) continue xCheck;
                if (x + cols > layout[0].length) continue xCheck;
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        if (layout[y + row][x + col] != -1) {
                            continue xCheck;
                        }
                    }
                }
                //System.out.println("Found availability: " + x + ", " + y);
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        layout[y + row][x + col] = componentID;
                    }
                }
                return;
            }
        }
    	throw new RuntimeException("Unable to place component " + c.getClass().getName() + " (" + componentID + ") because not enough space is available in this table.");
    }
    
    private void buildMaxHeights(Container container) {
        heights = new int[layout.length];
        int maxHeight;
        Component c;
        Dimension d;
        int height;
        int totalHeight = 0;
        int fixedHeight = 0;
        for (int row = 0; row < layout.length; row++) {
            maxHeight = 0;
            for (int col = 0; col < layout[0].length; col++) {
                if (layout[row][col] != -1) {
	                c = container.getComponent(layout[row][col]);
	                d = c.getPreferredSize();
	                height = d.height + defaultTopMargin + defaultBottomMargin;
                    if (c instanceof JComponent) {
                        height += ((JComponent)c).getInsets().top;
                        height += ((JComponent)c).getInsets().bottom;
                    }
	                if (height > maxHeight) maxHeight = height;
                }
            }
            heights[row] = maxHeight;
            totalHeight += maxHeight;
            if (disabledVerticalStretchingRows.contains(new Integer(row))) {
                fixedHeight += maxHeight;
            }
        }
        
        // Fill available vertical area
        // TODO re-enable this once you can successfully disable some compontents and still
        // maintain correct percentages.
        
        int targetHeight = container.getSize().height;
        targetHeight -= container.getInsets().top;
        targetHeight -= container.getInsets().bottom;
        float percentage;
        if (targetHeight > 0) {
	        if ((defaultFill) || (totalHeight > targetHeight)) {
	            for (int i = 0; i < heights.length; i++) {
	                if (!disabledVerticalStretchingRows.contains(new Integer(i))) {
		                percentage = (float)heights[i] / (float)(totalHeight - fixedHeight);
		                heights[i] = Math.round((targetHeight - fixedHeight) * percentage);
	                }
	            }
	        }
        }
    }
    
    private void buildMaxWidths(Container container) {
        Component c;
        int width;
        //int row = 0;
        int column = 0;
        Insets insets = container.getInsets();
        int targetWidth = container.getSize().width - insets.left - insets.right;
        //int targetHeight = container.getSize().height - insets.top - insets.bottom;
        
        if (percentages != null) {
            // Define widths from percentages
	        widths = new int[columns];
	        for (int i = 0; i < columns; i++) {
	            widths[i] = (int)Math.round(targetWidth * percentages[i]);
	        }
        } else {
            // Define widths dynamically
            widths = new int[columns];
            for (int i = 0; i < container.getComponentCount(); i++) {
                if (column == columns) column = 0;
                c = container.getComponent(i);
                int leftMargin = defaultLeftMargin;
                int rightMargin = defaultRightMargin;
                if (leftPadding.containsKey(c)) {
                	leftMargin = ((Integer)leftPadding.get(c)).intValue();
                } else if (rightPadding.containsKey(c)) {
                	rightMargin = ((Integer)rightPadding.get(c)).intValue();
                }
                width = c.getPreferredSize().width + leftMargin + rightMargin;
                if (colSpans.containsKey(c)) {
                    int colSpan = ((Integer)colSpans.get(c)).intValue();
                    width = Math.round((float)width / (float)colSpan);
                }
                if (width > widths[column]) {
                	widths[column] = width;
                }
                column++;
            }
            int totalWidth = 0;
            for (int i = 0; i < widths.length; i++) {
            	totalWidth += widths[i];
            }
            // Check to see if preferred size is too much
            if ((targetWidth < totalWidth) && (targetWidth > 0)) {
                float modifier = (float)targetWidth / (float)totalWidth;
                totalWidth = 0;
                for (int i = 0; i < widths.length; i++) {
                    widths[i] = Math.round(widths[i] * modifier);
                    totalWidth += widths[i];
                }
            }
            //int fullWidth = container.getParent().getSize().width;
            int temp = 0;
            float variable = (float)targetWidth / (float)totalWidth;
            if (totalWidth < targetWidth) {
            	for (int i = 0; i < widths.length; i++) {
            		widths[i] = (int)(widths[i] * variable);
            		temp += widths[i];
            	}
            }
        }
    }

    /**
     * If you set borders on cells this must be called in the paintBorders method of your container
     * 
     * @param c
     * @param g
     */
    public void paintBorders(Container parent, Graphics g) {
    	if ((bounds != null) && (bounds.size() > 0)) {
    		Iterator i = bounds.keySet().iterator();
    		Component c;
    		Border border;
    		Rectangle2D rectangle;
    		while (i.hasNext()) {
    			c = (Component)i.next();
    			if (getCellBorder(c) != null) {
    				border = getCellBorder(c);
    				rectangle = (Rectangle2D)bounds.get(c);
    				border.paintBorder(parent, g, (int)rectangle.getX(), (int)rectangle.getY(), (int)rectangle.getWidth(), (int)rectangle.getHeight());
    			}
    		}
    	}
    }
    
    /**
     * If you set the background color for cells this must be called in the ___ method of your container.
     * 
     * @param parent
     * @param g
     */
    public void paintBackground(Container parent, Graphics g) {
    	if ((bounds != null) && (bounds.size() > 0)) {
    		Iterator i = bounds.keySet().iterator();
    		Component c;
    		Color bgColor;
    		Rectangle2D rectangle;
    		while (i.hasNext()) {
    			c = (Component)i.next();
    			if (getBackgroundColor(c) != null) {
    				bgColor = getBackgroundColor(c);
    				rectangle = (Rectangle2D)bounds.get(c);
    				//border.paintBorder(parent, g, (int)rectangle.getX(), (int)rectangle.getY(), (int)rectangle.getWidth(), (int)rectangle.getHeight());
    				g.setColor(bgColor);
    				g.fillRect((int)rectangle.getX(), (int)rectangle.getY(), (int)rectangle.getWidth(), (int)rectangle.getHeight());
    			}
    		}
    	}
    }
}
