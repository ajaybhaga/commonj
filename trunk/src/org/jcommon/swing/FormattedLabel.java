/*
 * Created on Sep 15, 2004
 */
package org.jcommon.swing;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Matt Hicks
 */
public class FormattedLabel extends JLabel {
    protected static final Border noFocusBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    
    public static final int TABLE_CELL_TYPE = 1;
    public static final int STANDARD_TYPE = 2;
    
    public static final int TEXT_FORMAT = 1;
    public static final int NUMBER_FORMAT = 2;
    public static final int DECIMAL_FORMAT = 3;
    public static final int CURRENCY_FORMAT = 4;
    
    protected int type;
    protected int format;
    protected NumberFormat formatter;
    protected Color unselectedForeground;
    protected Color unselectedBackground;
    
    public FormattedLabel(int type, int format) {
        super();
        setOpaque(true);
        setBorder(noFocusBorder);
        setType(type);
        setFormat(format);
    }
    
    public void setType(int type) {
        this.type = type;
        if (type == TABLE_CELL_TYPE) {
            setFont(new Font("Helvetica", Font.PLAIN, 12));
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
    }
    
    public void setFormat(int format) {
        this.format = format;
        if (format != TEXT_FORMAT) {
            setHorizontalAlignment(JTextField.RIGHT);;
        }
    }
    
    public void setForeground(Color c) {
        super.setForeground(c);
        unselectedForeground = c;
    }
    
    public void setBackground(Color c) {
        super.setBackground(c);
        unselectedBackground = c;
    }
    
    public void updateUI() {
        super.updateUI();
        setForeground(null);
        setBackground(null);
    }
    
    public void validate() {
        if (type != TABLE_CELL_TYPE) {
            super.validate();
        }
    }
    
    public void invalidate() {
        if (type != TABLE_CELL_TYPE) {
            super.invalidate();
        }
    }
    
    public void revalidate() {
        if (type != TABLE_CELL_TYPE) {
            super.revalidate();
        }
    }
    
    public void repaint(long tm, int x, int y, int width, int height) {
        if (type != TABLE_CELL_TYPE) {
            super.repaint(tm, x, y, width, height);
        }
    }
    
    public void repaint(Rectangle r) {
        if (type != TABLE_CELL_TYPE) {
            super.repaint(r);
        }
    }
    
    public void repaint() {
        if (type != TABLE_CELL_TYPE) {
            super.repaint();
        }
    }
    
    public boolean isOpaque() {
        if (type != TABLE_CELL_TYPE) {
            return super.isOpaque();
        }
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground()) && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (type != TABLE_CELL_TYPE) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        } else if (propertyName == "text") {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
    
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (type != TABLE_CELL_TYPE) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
    
    protected void setValue(Object value) {
        if (value == null) {
            setText("");
        } else {
            setText(value.toString());
        }
    }
    
    public void setText(String text) {
        if (format == DECIMAL_FORMAT) {
            if (formatter == null) {
                formatter = NumberFormat.getInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
            }
            super.setText(formatter.format(new Double(text)));
        } else if (format == NUMBER_FORMAT) {
            if (formatter == null) {
                formatter = NumberFormat.getInstance();
                formatter.setMinimumFractionDigits(0);
                formatter.setMaximumFractionDigits(0);
            }
            formatter.setGroupingUsed(false);
            super.setText(formatter.format(new Long(text)));
        } else if (format == CURRENCY_FORMAT) {
            if (formatter == null) {
                formatter = NumberFormat.getCurrencyInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
            }
            super.setText(formatter.format(new Double(text)));
        } else {
            super.setText(text);
        }
        super.setToolTipText(super.getText());
    }
}
