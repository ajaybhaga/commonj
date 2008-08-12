/*
 * Created on May 26, 2005
 */
package org.jcommon.swing.robust.gui.action;

import java.awt.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class RobustAction {
    public static final String ACTION_PERFORMED = "actionPerformed";
    public static final String COMPONENT_ADDED = "componentAdded";
    public static final String COMPONENT_REMOVED = "componentRemoved";
    public static final String COMPONENT_HIDDEN = "componentHidden";
    public static final String COMPONENT_MOVED = "componentMoved";
    public static final String COMPONENT_RESIZED = "componentResized";
    public static final String COMPONENT_SHOWN = "componentShown";
    public static final String FOCUS_GAINED = "focusGained";
    public static final String FOCUS_LOST = "focusLost";
    public static final String INTERNAL_FRAME_ACTIVATED = "internalFrameActivated";
    public static final String INTERNAL_FRAME_OPENED = "internalFrameOpened";
    public static final String INTERNAL_FRAME_CLOSED = "internalFrameClosed";
    public static final String INTERNAL_FRAME_CLOSING = "internalFrameClosing";
    public static final String INTERNAL_FRAME_DEACTIVATED = "internalFrameDeactivated";
    public static final String INTERNAL_FRAME_DEICONIFIED = "internalFrameDeiconified";
    public static final String INTERNAL_FRAME_ICONIFIED = "internalFrameIconified";
    public static final String KEY_PRESSED = "keyPressed";
    public static final String KEY_RELEASED = "keyReleased";
    public static final String KEY_TYPED = "keyTyped";
    public static final String MENU_SELECTED = "menuSelected";
    public static final String MENU_DESELECTED = "menuDeselected";
    public static final String MENU_CANCELED = "menuCanceled";
    public static final String MENU_CANCELLED = MENU_CANCELED;
    public static final String MOUSE_CLICKED = "mouseClicked";
    public static final String MOUSE_ENTERED = "mouseEntered";
    public static final String MOUSE_EXITED = "mouseExited";
    public static final String MOUSE_PRESSED = "mousePressed";
    public static final String MOUSE_RELEASED = "mouseReleased";
    public static final String PROPERTY_CHANGE = "propertyChange";
    public static final String TREE_SELECTION = "treeSelection";
    
    private String type;
    private Component c;
    private EventObject e;
    
    public RobustAction(String type, Component c, EventObject e) {
        this.type = type;
        this.c = c;
        this.e = e;
    }
    
    public String getType() {
        return type;
    }
    
    public Component getComponent() {
        return c;
    }
    
    public EventObject getEvent() {
        return e;
    }

    public static boolean isActionEvent(RobustAction a) {
        if (a.getType() == ACTION_PERFORMED) {
            return true;
        }
        return false;
    }
        
    public static boolean isComponentEvent(RobustAction a) {
        if (a.getType() == COMPONENT_HIDDEN) {
            return true;
        } else if (a.getType() == COMPONENT_MOVED) {
            return true;
        } else if (a.getType() == COMPONENT_RESIZED) {
            return true;
        } else if (a.getType() == COMPONENT_SHOWN) {
            return true;
        }
        return false;
    }
    
    public static boolean isContainerEvent(RobustAction a) {
        if (a.getType() == COMPONENT_ADDED) {
            return true;
        } else if (a.getType() == COMPONENT_REMOVED) {
            return true;
        }
        return false;
    }
    
    public static boolean isInternalFrameEvent(RobustAction a) {
        if (a.getType() == INTERNAL_FRAME_ACTIVATED) {
            return true;
        } else if (a.getType() == INTERNAL_FRAME_OPENED) {
            return true;
        } else if (a.getType() == INTERNAL_FRAME_CLOSED) {
            return true;
        } else if (a.getType() == INTERNAL_FRAME_CLOSING) {
            return true;
        } else if (a.getType() == INTERNAL_FRAME_DEACTIVATED) {
            return true;
        } else if (a.getType() == INTERNAL_FRAME_DEICONIFIED) {
            return true;
        } else if (a.getType() == INTERNAL_FRAME_ICONIFIED) {
            return true;
        }
        return false;
    }
    
    public static boolean isFocusEvent(RobustAction a) {
        if (a.getType() == FOCUS_GAINED) {
            return true;
        } else if (a.getType() == FOCUS_LOST) {
            return true;
        }
        return false;
    }
    
    public static boolean isMouseEvent(RobustAction a) {
        if (a.getType() == MOUSE_CLICKED) {
            return true;
        } else if (a.getType() == MOUSE_ENTERED) {
            return true;
        } else if (a.getType() == MOUSE_EXITED) {
            return true;
        } else if (a.getType() == MOUSE_PRESSED) {
            return true;
        } else if (a.getType() == MOUSE_RELEASED) {
            return true;
        }
        return false;
    }
    
    public static boolean isTreeSelectionEvent(RobustAction a) {
        if (a.getType() == TREE_SELECTION) {
            return true;
        }
        return false;
    }
    
    public static boolean isPropertyChangeEvent(RobustAction a) {
        if (a.getType() == PROPERTY_CHANGE) {
            return true;
        }
        return false;
    }
    
    public static boolean isKeyEvent(RobustAction a) {
        if (a.getType() == PROPERTY_CHANGE) {
            return true;
        }
        return false;
    }
    
    public static boolean isMenuEvent(RobustAction a) {
        if (a.getType() == MENU_SELECTED) {
            return true;
        } else if (a.getType() == MENU_DESELECTED) {
            return true;
        } else if (a.getType() == MENU_CANCELED) {
            return true;
        }
        return false;
    }
}
