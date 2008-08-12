/*
 * Created on May 31, 2005
 */
package org.jcommon.swing.robust.event;

import java.util.*;

/**
 * @author Matt Hicks
 */
public class RobustEvent {
    private Object originator;
    private String type;
    private Object object;
    private Calendar stamp;
    
    public RobustEvent(Object originator, String type, Object object) {
        this.originator = originator;
        this.type = type;
        this.object = object;
        stamp = new GregorianCalendar();
    }
    
    public Object getOriginator() {
        return originator;
    }
    
    public String getType() {
        return type;
    }
    
    public Object getObject() {
        return object;
    }
    
    public Calendar getStamp() {
        return stamp;
    }
}
