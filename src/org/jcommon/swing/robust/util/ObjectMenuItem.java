/*
 * Created on Jun 2, 2005
 */
package org.jcommon.swing.robust.util;

import javax.swing.*;

/**
 * @author Matt Hicks
 */
public class ObjectMenuItem extends JMenuItem {
    private Object o;
    
    public ObjectMenuItem() {
        super();
    }
    
    public ObjectMenuItem(Object o) {
        super();
        if (o != null) {
            this.o = o;
            setText(o.toString());
        }
    }
    
    public Object getObject() {
        return o;
    }
    
    public void setObject(Object o) {
        this.o = o;
    }
}
