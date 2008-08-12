/*
 * Created on May 26, 2005
 */
package org.jcommon.swing.robust.gui;

import java.awt.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.*;

import org.jcommon.swing.robust.*;
import org.jcommon.swing.robust.event.*;
import org.jcommon.swing.robust.gui.action.*;

/**
 * @author Matt Hicks
 */
public abstract class RobustWindow implements RobustActionListener {
    private boolean detached;
    private JInternalFrame internal;
    private JDialog external;
    private RobustListener listener;
    private Border border;
    private Color bgColor;
    
    public RobustWindow(String name) {
        detached = false;
        internal = new JInternalFrame();
        internal.setTitle(name);
        internal.setIconifiable(true);
        internal.setMaximizable(true);
        internal.setResizable(true);
        internal.setClosable(true);
        listener = new RobustListener(this);
    }
    
    public RobustListener getListener() {
        return listener;
    }
    
    public void setSize(int width, int height) {
        if (detached) {
            external.setSize(width, height);
        } else {
            internal.setSize(width, height);
        }
    }
    
    public void setVisible(boolean visible) {
        if (detached) {
            external.setVisible(visible);
        } else {
            internal.setVisible(visible);
        }
    }
    
    public void makeTransparent() {
        if (detached) {
            bgColor = external.getContentPane().getBackground();
            external.getContentPane().setBackground(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0));
        } else {
            internal.setOpaque(false);
            bgColor = internal.getContentPane().getBackground();
            internal.getContentPane().setBackground(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0));
        }
    }
    
    public void makeOpaque() {
        if (detached) {
            bgColor = external.getContentPane().getBackground();
	        external.getContentPane().setBackground(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 255));
        } else {
	        internal.setOpaque(true);
	        bgColor = internal.getContentPane().getBackground();
	        internal.getContentPane().setBackground(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 255));
        }
    }
    
    public void dock() {
        if (detached) {
            // TODO implement alternative docking feature
        } else {
	        border = internal.getBorder();
	        internal.setVisible(false);
	        internal.setBorder(BorderFactory.createEmptyBorder());
	        ((javax.swing.plaf.basic.BasicInternalFrameUI)internal.getUI()).getNorthPane().setPreferredSize(new Dimension(0, 0));
	        internal.setVisible(true);
        }
    }
    
    public void undock() {
        if (detached) {
            // TODO implement alternative undocking feature
        } else {
	        internal.setVisible(false);
	        internal.setBorder(border);
	        ((javax.swing.plaf.basic.BasicInternalFrameUI)internal.getUI()).getNorthPane().setPreferredSize(new Dimension(internal.getWidth(), 23));
	        internal.setVisible(true);
        }
    }
    
    public void detach() {
        if (!detached) {
            try {
                // TODO remove this temporary work-around
                // leaving maximized causes strange screen flicker (endless loop?)
                internal.setMaximum(false);
            } catch(Exception exc) {
                exc.printStackTrace();
            }
            JFrame frame = RobustDesktop.getInstance().getFrame();
            external = new JDialog(frame);
            external.setSize(internal.getSize());
            external.setTitle(internal.getTitle());
            external.setContentPane(internal.getContentPane());
            
            int x = frame.getLocation().x + frame.getInsets().left + internal.getLocation().x;
            int y = frame.getLocation().y + frame.getInsets().top + internal.getLocation().y;
            if (RobustDesktop.getInstance().isMenuBarVisible()) {
                y += RobustDesktop.getInstance().getMenuBar().getSize().height;
            }
            external.setLocation(x, y);
            
            internal.setVisible(false);
            external.setVisible(true);
            detached = true;
        }
    }
    
    public void reattach() {
        if (detached) {
            RobustDesktop desktop = RobustDesktop.getInstance();
            JFrame frame = desktop.getFrame();
            internal.setSize(external.getSize());
            internal.setTitle(external.getTitle());
            internal.setContentPane(external.getContentPane());
            
            int x = external.getLocation().x - frame.getLocation().x - frame.getInsets().left;
            int y = external.getLocation().y - frame.getLocation().y - frame.getInsets().top;
            if (desktop.isMenuBarVisible()) {
                y -= desktop.getMenuBar().getSize().height;
            }
            
            Dimension desktopSize = desktop.getDesktopPane().getSize();
            if (!desktop.isScrollable()) {
                // If location is too far right or down adjust
                if ((x + internal.getSize().width) > desktopSize.width) {
                    x = desktopSize.width - internal.getSize().width;
                }
                if ((y + internal.getSize().height) > desktopSize.height) {
                    y = desktopSize.height - internal.getSize().height;
                }
            }
            
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            internal.setLocation(x, y);
            
            external.setVisible(false);
            internal.setVisible(true);
            detached = false;
        }
    }
    
    public void maximize() throws PropertyVetoException {
        if (detached) {
            // TODO implement alternative
        } else {
            internal.setMaximum(true);
        }
    }
    
    public void restore() throws PropertyVetoException {
        if (detached) {
            // TODO implement alternative
        } else {
            if (internal.isIcon()) internal.setIcon(false);
            else if (internal.isMaximum()) internal.setMaximum(false);
        }
    }
    
    public void minimize() throws PropertyVetoException {
        if (detached) {
            // TODO implement minimize
        } else {
            internal.setIcon(true);
        }
    }
    
    public Container getContainer() {
        if (detached) {
            return external.getContentPane();
        } else {
            return internal.getContentPane();
        }
    }
    
    public Container getWindowContainer() {
        if (detached) {
            return external;
        } else {
            return internal;
        }
    }
    
    public JInternalFrame getInternal() {
        return internal;
    }
    
    public JDialog getExternal() {
        return external;
    }
    
    public boolean isAttached() {
        return !detached;
    }
    
    public boolean isDetached() {
        return detached;
    }
    
    protected void exceptionHandler(Exception exc) {
        RobustMonitor.getInstance().fireEvent(new RobustEvent(this, "Exception", exc));
    }

    public void close() {
        getInternal().setVisible(false);
        getInternal().dispose();
        if (getExternal() != null) {
            getExternal().setVisible(false);
            getExternal().dispose();
        }
        RobustDesktop.getInstance().unregisterWindow(this);
    }
}
