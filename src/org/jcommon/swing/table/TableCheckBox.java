/*
 * Created on Sep 16, 2004
 */
package org.jcommon.swing.table;

import java.awt.*;
import javax.swing.*;

/**
 * @author Matt Hicks
 */
public class TableCheckBox extends JCheckBox {
    protected BeanTable table;
    protected Color unselectedForeground;
    protected Color unselectedBackground;
    
    public TableCheckBox() {
        super();
    }
    
    public TableCheckBox(BeanTable table) {
        super();
        this.table = table;
        
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
    }
    
    public void invalidate() {
    }
    
    public void revalidate() {
    }
    
    public void repaint(long tm, int x, int y, int width, int height) {
    }
    
    public void repaint(Rectangle r) {
    }
    
    public void repaint() {
    }
    
    public boolean isOpaque() {
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        boolean colorMatch = (back != null) && (p != null) && back.equals(p.getBackground()) && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyName == "text") {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
    
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

    /*public void actionPerformed(ActionEvent e) {
        if (table != null) {
            table.cel
        }
    }*/
}
