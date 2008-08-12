/*
 * Created on Oct 29, 2004
 */
package org.jcommon.swing.table;

import java.awt.*;
import java.util.*;

import org.jcommon.swing.*;
import org.jcommon.util.*;

/**
 * @author Matt Hicks
 */
public class CalendarBeanTableCell extends BeanTableCell {
    public Component getNewRenderer(Object value) {
        Calendar c = (Calendar)value;
        JDropDownCalendar renderer = new JDropDownCalendar(JDropDownCalendar.DATE);
        renderer.getCalendar().setTimeInMillis(c.getTimeInMillis());
	    renderer.setEditable(editable);
	    renderer.setFont(getTable().getDefaultRendererFont());
        return renderer;
    }

    public Component getNewEditor(Object value) {
        JDropDownCalendar editor = new JDropDownCalendar(JDropDownCalendar.DATE);
        editor.getCalendar().setTimeInMillis(((Calendar)value).getTimeInMillis());
        editor.addCalendarListener(new CalendarChangeListener() {
        	public void changed(MonitoredGregorianCalendar c, int type, int field, long value) {
        		if (field == Calendar.DAY_OF_MONTH) {
	                ((JDropDownCalendar)getRenderer()).getCalendar().setTimeInMillis((((JDropDownCalendar)getEditor()).getCalendar()).getTimeInMillis());
	                ((JDropDownCalendar)getRenderer()).setPopupVisible(false);
	                stopCellEditing();
        		}
            }
        });
        editor.setFont(getTable().getDefaultEditorFont());
        return editor;
    }

    public Object getCellEditorValue() {
        return ((JDropDownCalendar)getEditor()).getCalendar();
    }

    public boolean shouldSelectCell(EventObject e) {
        return false;
    }
}
