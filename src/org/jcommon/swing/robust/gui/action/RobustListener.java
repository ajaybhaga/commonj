/*
 * Created on May 26, 2005
 */
package org.jcommon.swing.robust.gui.action;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.event.*;

/**
 * @author Matt Hicks
 */
public class RobustListener implements ActionListener, ComponentListener, ContainerListener, InternalFrameListener, FocusListener, MouseListener, TreeSelectionListener, MenuListener, PropertyChangeListener, KeyListener {
    private RobustActionListener listener;
    
    public RobustListener(RobustActionListener listener) {
        this.listener = listener;
    }
    
    public void actionPerformed(ActionEvent e) {
        action(RobustAction.ACTION_PERFORMED, (Component)e.getSource(), e);
    }
    
    public void focusGained(FocusEvent e) {
        action(RobustAction.FOCUS_GAINED, (Component)e.getSource(), e);
    }
    
    public void focusLost(FocusEvent e) {
        action(RobustAction.FOCUS_LOST, (Component)e.getSource(), e);
    }
    
    public void componentAdded(ContainerEvent e) {
        action(RobustAction.COMPONENT_ADDED, (Component)e.getSource(), e);
    }
    
    public void componentRemoved(ContainerEvent e) {
        action(RobustAction.COMPONENT_REMOVED, (Component)e.getSource(), e);
    }
    
    public void componentHidden(ComponentEvent e) {
        action(RobustAction.COMPONENT_HIDDEN, (Component)e.getSource(), e);
    }
    
    public void componentMoved(ComponentEvent e) {
        action(RobustAction.COMPONENT_MOVED, (Component)e.getSource(), e);
    }
    
    public void componentResized(ComponentEvent e) {
        action(RobustAction.COMPONENT_RESIZED, (Component)e.getSource(), e);
    }
    
    public void componentShown(ComponentEvent e) {
        action(RobustAction.COMPONENT_SHOWN, (Component)e.getSource(), e);
    }
    
    public void internalFrameActivated(InternalFrameEvent e) {
        action(RobustAction.INTERNAL_FRAME_ACTIVATED, (Component)e.getSource(), e);
    }
    
    public void internalFrameClosed(InternalFrameEvent e) {
        action(RobustAction.INTERNAL_FRAME_CLOSED, (Component)e.getSource(), e);
    }
    
    public void internalFrameClosing(InternalFrameEvent e) {
        action(RobustAction.INTERNAL_FRAME_CLOSING, (Component)e.getSource(), e);
    }
    
    public void internalFrameDeactivated(InternalFrameEvent e) {
        action(RobustAction.INTERNAL_FRAME_DEACTIVATED, (Component)e.getSource(), e);
    }
    
    public void internalFrameDeiconified(InternalFrameEvent e) {
        action(RobustAction.INTERNAL_FRAME_DEICONIFIED, (Component)e.getSource(), e);
    }
    
    public void internalFrameIconified(InternalFrameEvent e) {
        action(RobustAction.INTERNAL_FRAME_ICONIFIED, (Component)e.getSource(), e);
    }
    
    public void internalFrameOpened(InternalFrameEvent e) {
        action(RobustAction.INTERNAL_FRAME_OPENED, (Component)e.getSource(), e);
    }
    
    public void mouseClicked(MouseEvent e) {
        action(RobustAction.MOUSE_CLICKED, (Component)e.getSource(), e);
    }
    
    public void mouseEntered(MouseEvent e) {
        action(RobustAction.MOUSE_ENTERED, (Component)e.getSource(), e);
    }
    
    public void mouseExited(MouseEvent e) {
        action(RobustAction.MOUSE_EXITED, (Component)e.getSource(), e);
    }
    
    public void mousePressed(MouseEvent e) {
        action(RobustAction.MOUSE_PRESSED, (Component)e.getSource(), e);
    }
    
    public void mouseReleased(MouseEvent e) {
        action(RobustAction.MOUSE_RELEASED, (Component)e.getSource(), e);
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        action(RobustAction.TREE_SELECTION, (Component)e.getSource(), e);
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        action(RobustAction.PROPERTY_CHANGE, (Component)e.getSource(), e);
    }
    
    public void keyPressed(KeyEvent e) {
        action(RobustAction.KEY_PRESSED, (Component)e.getSource(), e);
    }
    
    public void keyReleased(KeyEvent e) {
        action(RobustAction.KEY_RELEASED, (Component)e.getSource(), e);
    }
    
    public void keyTyped(KeyEvent e) {
        action(RobustAction.KEY_TYPED, (Component)e.getSource(), e);
    }
    
    public synchronized void action(String type, Component component, EventObject event) {
        final RobustAction action = new RobustAction(type, component, event);
        new Thread() {
            public void run() {
                listener.action(action);
            }
        }.start();
    }

    public void menuSelected(MenuEvent e) {
        action(RobustAction.MENU_SELECTED, (Component)e.getSource(), e);
    }

    public void menuDeselected(MenuEvent e) {
        action(RobustAction.MENU_DESELECTED, (Component)e.getSource(), e);
    }

    public void menuCanceled(MenuEvent e) {
        action(RobustAction.MENU_CANCELED, (Component)e.getSource(), e);
    }
}
