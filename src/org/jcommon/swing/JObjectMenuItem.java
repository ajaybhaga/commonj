/*
 * Created on May 19, 2005
 */
package org.jcommon.swing;

import javax.swing.*;

/**
 * @author Matt Hicks
 */
public class JObjectMenuItem extends JMenuItem {
    private Object o;
    
    public JObjectMenuItem(Object o) {
        super();
        if (o != null) {
            this.setText(o.toString());
        }
        setObject(o);
    }
    
    public void setObject(Object o) {
        this.o = o;
    }
    
    public Object getObject() {
        return o;
    }
}
