/*
 * Created on Mar 17, 2005
 */
package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.*;

import org.jcommon.util.ArrayUtilities;

/**
 * @author Matt Hicks
 */
public class JOptionField extends JPanel implements ActionListener {
    private static Color gray = new Color(238, 238, 238);
    
    private Collection c;
    private Object selectedObject;
    private boolean editable;
    
    private JTextField editor;
    private JButton arrow;
    private JPopupMenu menu;
    
    private int actionID;
    private List listeners;
    
    public JOptionField(Object[] array, Object selectedObject, boolean editable) {
        this(ArrayUtilities.toList(array), selectedObject, editable);
    }
    
    public JOptionField(Collection c, Object selectedObject, boolean editable) {
        this.c = c;
        this.selectedObject = selectedObject;
        this.editable = editable;
        
        actionID = 0;
        listeners = Collections.synchronizedList(new ArrayList());
        
        setLayout();
    }
    
    private void setLayout() {
        editor = new JTextField();
        editor.setBorder(BorderFactory.createEtchedBorder());
        editor.setOpaque(false);
        if (selectedObject != null) {
            editor.setText(selectedObject.toString());
        }
        if (!editable) {
            editor.setEditable(false);
        } else {
            throw new RuntimeException("Editing of this has not yet been enabled.");
        }
        
        arrow = new BasicArrowButton(BasicArrowButton.SOUTH, gray, Color.LIGHT_GRAY, Color.BLACK, Color.LIGHT_GRAY);
        arrow.setBorder(BorderFactory.createEtchedBorder());
        arrow.addActionListener(this);
        
        menu = new JPopupMenu();
        
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, editor);
        this.add(BorderLayout.EAST, arrow);
    }
    
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == arrow) {
            // Toggle popup
            if (menu.isVisible()) {
                hidePopup();
            } else {
                showPopup();
            }
        } else if (evt.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem)evt.getSource();
            String text = item.getText();
            setSelectedObjectByText(text);
        }
    }
    
    public void showPopup() {
        menu.removeAll();
        
        Iterator iterator = c.iterator();
        Object o;
        JMenuItem item;
        while (iterator.hasNext()) {
            o = iterator.next();
            if ((selectedObject == null) || (!o.toString().equals(selectedObject.toString()))) {
                item = new JMenuItem(o.toString());
                item.addActionListener(this);
                menu.add(item);
            }
        }
        int height;
        if (selectedObject == null) {
            height = c.size() * 20;
        } else {
            height = (c.size() - 1) * 20;
        }
        menu.setPopupSize(this.getWidth(), height);
        menu.show(this, 0, this.getHeight());
        menu.repaint();
    }
    
    public void hidePopup() {
        menu.setVisible(false);
    }
    
    public Object getSelectedObject() {
        return selectedObject;
    }
    
    public synchronized void setSelectedObject(Object selectedObject) {
        if (this.selectedObject != selectedObject) {
	        this.selectedObject = selectedObject;
	        if (selectedObject != null) {
	            editor.setText(selectedObject.toString());
	        } else {
	            editor.setText("");
	        }
	        
	        ActionListener listener;
	        for (int i = 0; i < listeners.size(); i++) {
	            listener = (ActionListener)listeners.get(i);
	            listener.actionPerformed(new ActionEvent(getSelectedObject(), ++actionID, "Selection Changed"));
	        }
        }
    }
    
    public void setSelectedObjectByText(String text) {
        Iterator iterator = c.iterator();
        Object o;
        while (iterator.hasNext()) {
            o = iterator.next();
            if (o.toString().equals(text)) {
                setSelectedObject(o);
                break;
            }
        }
    }
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    public static void main(String[] args) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Testing");
        list.add("Testing2");
        list.add("Testing3");
        
        JComboEditor editor1 = new JComboEditor(list, false);
        editor1.setName("JComboEditor");
        
        JOptionField editor2 = new JOptionField(list, "Testing", false);
        editor2.setName("JOptionField");
        
        DynamicDialog dialog = new DynamicDialog(null, "Test", new JComponent[] {editor1, editor2}, 200, 200);
        System.out.println("Valid: " + dialog.waitForAction() + ", " + editor2.getSelectedObject());
        dialog.dispose();
    }
}
