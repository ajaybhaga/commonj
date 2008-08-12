/*
 * Created on Sep 20, 2004
 */
package org.jcommon.swing.table;

import java.awt.*;
import java.lang.reflect.*;

import javax.swing.*;

import org.jcommon.lang.Currency;
import org.jcommon.swing.CustomizablePlainDocument;
import org.jcommon.swing.FormattedLabel;
import org.jcommon.swing.JComboEditor;

/**
 * @author Matt Hicks
 */
public class NumberBeanTableCell extends BeanTableCell {
    public static final String NUMBERS_ALLOWED = "-,0123456789";
    public static final String DECIMALS_ALLOWED = "-,0123456789.";
    public static final String CURRENCY_ALLOWED = "-,0123456789.$";
    
    protected int type;
    
    public void setType(int type) {
        this.type = type;
    }
    
    public Component getNewRenderer(Object value) {
        FormattedLabel renderer;
        String s;
        if (value != null) {
            s = value.toString();
        } else {
            s = "--";
            renderer = new FormattedLabel(FormattedLabel.TABLE_CELL_TYPE, FormattedLabel.TEXT_FORMAT);
            renderer.setFont(getTable().getDefaultRendererFont());
            renderer.setHorizontalAlignment(SwingConstants.RIGHT);
            renderer.setText(s);
            return renderer;
        }
        if (type > 0) {
            renderer = new FormattedLabel(FormattedLabel.TABLE_CELL_TYPE, type);
        } else if ((getType() == Float.class) || (getType() == Double.class)) {
            renderer = new FormattedLabel(FormattedLabel.TABLE_CELL_TYPE, FormattedLabel.DECIMAL_FORMAT);
        } else if (getType() == Currency.class) {
            renderer = new FormattedLabel(FormattedLabel.TABLE_CELL_TYPE, FormattedLabel.CURRENCY_FORMAT);
            s = s.replaceAll("\\$", "");
        } else {
            renderer = new FormattedLabel(FormattedLabel.TABLE_CELL_TYPE, FormattedLabel.NUMBER_FORMAT);
        }
        s = s.replaceAll(",", "");
        renderer.setText(s);
        renderer.setFont(getTable().getDefaultRendererFont());
        return renderer;
    }

    public Component getNewEditor(Object value) {
        JTextField editor = new JTextField();
        CustomizablePlainDocument document = new CustomizablePlainDocument();
        document.setMaxLength(12);
        if ((getType() == Float.class) || (getType() == Double.class)) {
            document.setAllowedCharacters(DECIMALS_ALLOWED);
        } else if (getType() == Currency.class) {
            document.setAllowedCharacters(CURRENCY_ALLOWED);
        } else {
            document.setAllowedCharacters(NUMBERS_ALLOWED);
        }
        editor.setDocument(document);
        editor.setHorizontalAlignment(JTextField.RIGHT);
        editor.setText(value.toString());
        editor.setFont(getTable().getDefaultEditorFont());
        return editor;
    }

    public Object getCellEditorValue() {
        String value;
        if (getAVO() != null) {
            value = ((JComboEditor)getEditor()).getSelectedValue().toString();
        } else {
            value = ((JTextField)getEditor()).getText();
        }
        value = value.replaceAll("$", "");
        if (org.jcommon.util.StringUtilities.countOccurrences(".", value) > 1) {
            return null;
        }
        try {
            Constructor c = getType().getConstructor(new Class[] {String.class});
            return c.newInstance(new Object[] {value});
        } catch(NoSuchMethodException e) {
            return value;
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
