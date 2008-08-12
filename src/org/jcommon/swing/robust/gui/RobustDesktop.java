/*
 * Created on May 26, 2005
 */
package org.jcommon.swing.robust.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.jcommon.swing.robust.*;
import org.jcommon.swing.robust.event.*;
import org.jcommon.swing.robust.gui.action.*;
import org.jcommon.swing.robust.util.*;

/**
 * @author Matt Hicks
 */
public class RobustDesktop implements RobustActionListener, MenuListener {
    private static RobustDesktop desktop;
    
    private JFrame frame;
    
    private JMenuBar bar;
    private JScrollPane desktopScrolling;
    private JDesktopPane desk;
    private JLabel status;
    
    private HashMap windows;
    
    private boolean scrollable;
    
    private HashMap menus;
    
    private RobustListener listener;
    
    private RobustDesktop() {
        windows = new HashMap();
        setScrollable(true);
        createLayout();
    }
    
    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }
    
    public boolean isScrollable() {
        return scrollable;
    }
    
    private void createLayout() {
        listener = new RobustListener(this);
        
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // TODO remove this
        Container c = frame.getContentPane();
        c.setLayout(new BorderLayout());
        
        desk = new JDesktopPane();
        desktopScrolling = new JScrollPane(desk);
        //desktopScrolling.getVerticalScrollBar().setBlockIncrement(1);
        c.add(BorderLayout.CENTER, desktopScrolling);
        
        status = new JLabel();
        status.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        c.add(BorderLayout.SOUTH, status);
        
        createMenu();
    }
    
    private void createMenu() {
        menus = new HashMap();
        
        bar = new JMenuBar();
        JMenu menu;
        JMenuItem item;
        
        menu = new JMenu("File");
        menu.setMnemonic('F');
        item = new JMenuItem("Exit");
        item.setMnemonic('X');
        item.addActionListener(listener);
        registerMenu(menu);
        registerMenuItem("File", item);
        
        menu = new JMenu("Window");
        menu.addMenuListener(this);
        menu.setMnemonic('W');
        registerMenu(menu);
        
        frame.setJMenuBar(bar);
    }
    
    public void registerMenu(JMenu menu) {
        menus.put(menu.getText(), menu);
        bar.add(menu);
    }
    
    public void registerMenuItem(String menuName, JMenuItem item) {
        JMenu menu;
        if (menus.get(menuName) == null) {
            menu = new JMenu(menuName);
            registerMenu(menu);
        } else {
            menu = (JMenu)menus.get(menuName);
        }
        menu.add(item, 0);
    }
    
    public void registerWindow(RobustWindow window) {
        desk.add(window.getInternal());
        window.getInternal().addComponentListener(listener);
        windows.put(window.getInternal(), window);
    }
    
    public void unregisterWindow(RobustWindow window) {
        desk.remove(window.getInternal());
        windows.remove(window.getInternal());
    }
    
    public void initialize(RobustSettings settings) {
        int width = settings.getSettingInteger("frameWidth").intValue();
        int height = settings.getSettingInteger("frameHeight").intValue();
        int screenWidth = settings.getSettingInteger("screenWidth").intValue();
        int screenHeight = settings.getSettingInteger("screenHeight").intValue();
        int x = 0;
        if ("centered".equals(settings.getSetting("framePositionX"))) {
            x = (screenWidth / 2) - (width / 2);
        }
        int y = 0;
        if ("centered".equals(settings.getSetting("framePositionY"))) {
            y = (screenHeight / 2) - (height / 2);
        }
        frame.setSize(width, height);
        frame.setTitle(settings.getSetting("title"));
        frame.setLocation(x, y);
        if ("true".equals(settings.getSetting("frameMaximized"))) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        if ("true".equals(settings.getSetting("frameMinimized"))) {
            frame.setExtendedState(frame.getExtendedState() | JFrame.ICONIFIED);
        }
        
        status.setText(settings.getSetting("defaultStatus"));
        
        frame.setVisible(true);
        resizeDesktop();
    }
    
    public static RobustDesktop getInstance() {
        if (desktop == null) {
            desktop = new RobustDesktop();
        }
        return desktop;
    }

    public JFrame getFrame() {
        return frame;
    }
    
    public JMenuBar getMenuBar() {
        return bar;
    }
    
    public void setMenuBarVisible(boolean v) {
        bar.setVisible(v);
    }
    
    public boolean isMenuBarVisible() {
        return bar.isVisible();
    }
    
    protected JDesktopPane getDesktopPane() {
        return desk;
    }
    
    public void setStatus(String status) {
        if (status == null) {
            status = Robust.getInstance().getManager().getSettings().getSetting("defaultStatus");
        }
        this.status.setText(status);
    }
    
    public void resizeDesktop() {
        if (!isScrollable()) return;
        JInternalFrame[] frames = desk.getAllFrames();
        Point p;
        Dimension d;
        int locationX = 0;
        int locationY = 0;
        int widest = 0;
        int tallest = 0;
        for (int i = 0; i < frames.length; i++) {
            if (!frames[i].isVisible()) {
                continue;
            }
            p = frames[i].getLocation();
            d = frames[i].getSize();
            if (p.x + d.width > widest) {
                widest = p.x + d.width;
            }
            if (p.y + d.height > tallest) {
                tallest = p.y + d.height;
            }
            if (p.x < locationX) {
                locationX = p.x;
            }
            if (p.y < locationY) {
                locationY = p.y;
            }
        }
        d = desktopScrolling.getSize();
        int scrollWidth = d.width - desktopScrolling.getHorizontalScrollBar().getSize().width - desktopScrolling.getInsets().left - desktopScrolling.getInsets().right;
        int scrollHeight = d.height - desktopScrolling.getVerticalScrollBar().getSize().height -  - desktopScrolling.getInsets().top -  - desktopScrolling.getInsets().bottom;
        if (widest < scrollWidth) {
            widest = scrollWidth;
        }
        if (tallest < scrollHeight) {
            tallest = scrollHeight;
        }
        desk.setSize(new Dimension(widest, tallest));
        desk.setPreferredSize(new Dimension(widest, tallest));
    }
    
    public void action(RobustAction action) {
        // implement window listener
        if (RobustAction.isComponentEvent(action)) {
            resizeDesktop();
        } else if (action.getType() == RobustAction.ACTION_PERFORMED) {
            if ("Window".equals(action.getComponent().getName())) {
                RobustWindow window = (RobustWindow)((ObjectMenuItem)action.getComponent()).getObject();
                try {
	                if (window.isAttached()) {
	                    if (desk.getSelectedFrame() == window.getInternal()) {
	                        if (window.getInternal().isMaximum()) {
	                            window.getInternal().setMaximum(false);
	                        } else if (window.getInternal().isIcon()) {
	                            window.getInternal().setIcon(false);
	                        } else {
	                            window.getInternal().setMaximum(true);
	                        }
	                    } else {
	                        //window.getInternal().setIcon(false);
	                        //desk.setSelectedFrame(window.getInternal());
	                        //window.getInternal().grabFocus();
	                        window.getInternal().moveToFront();
	                        window.getInternal().setSelected(true);
	                    }
	                }
                } catch(Exception exc) {
                    exceptionHandler(exc);
                }
            } else if (action.getComponent() instanceof ObjectMenuItem) {
                ObjectMenuItem item = (ObjectMenuItem)action.getComponent();
                if (item.getText().equals("Cascade")) {
                    cascadeWindows();
                } else if (item.getText().equals("Tile")) {
                    tileWindows();
                } else if (item.getText().equals("Minimize All")) {
                    minimizeWindows();
                } else if (item.getText().equals("Restore All")) {
                    restoreWindows();
                } else if (item.getText().equals("Close All")) {
                    closeWindows();
                }
            }
        } else {
            System.out.println("Action: " + action.getType());
        }
    }
    
    public void cascadeWindows() {
        // TODO implement
    }
    
    public void tileWindows() {
        // TODO implement
    }
    
    public void minimizeWindows() {
        // TODO implement
    }
    
    public void restoreWindows() {
        // TODO implement
    }
    
    public void closeWindows() {
        Iterator iterator = windows.values().iterator();
        RobustWindow window;
        while (iterator.hasNext()) {
            window = (RobustWindow)iterator.next();
            window.close();
        }
    }

    public void menuSelected(MenuEvent evt) {
        JMenu menu = (JMenu)evt.getSource();
        if (menu.getText().equals("Window")) {
            menu.removeAll();
            JInternalFrame selected = desk.getSelectedFrame();
            JInternalFrame[] frames = desk.getAllFrames();
            ObjectMenuItem item;
            if (selected != null) {
                if (!selected.isVisible()) {
                    selected = null;
                } else {
	                item = new ObjectMenuItem(windows.get(selected));
	                item.setText(selected.getTitle());
	                item.addActionListener(listener);
	                item.setName("Window");
	                menu.add(item);
	                if (frames.length > 1) menu.addSeparator();
                }
            }
            for (int i = 0; i < frames.length; i++) {
                if ((frames[i] != selected) && (frames[i].isVisible())) {
                    item = new ObjectMenuItem(windows.get(frames[i]));
                    item.setText(frames[i].getTitle());
                    item.setName("Window");
                    item.addActionListener(listener);
                    menu.add(item);
                }
            }
            if (frames.length > 0) {
                menu.addSeparator();
            }
            item = new ObjectMenuItem("Cascade");
            item.addActionListener(listener);
            if (frames.length == 0) item.setEnabled(false);
            menu.add(item);
            item = new ObjectMenuItem("Tile");
            item.addActionListener(listener);
            if (frames.length == 0) item.setEnabled(false);
            menu.add(item);
            item = new ObjectMenuItem("Minimize All");
            item.addActionListener(listener);
            if (frames.length == 0) item.setEnabled(false);
            menu.add(item);
            item = new ObjectMenuItem("Restore All");
            item.addActionListener(listener);
            if (frames.length == 0) item.setEnabled(false);
            menu.add(item);
            item = new ObjectMenuItem("Close All");
            item.addActionListener(listener);
            if (frames.length == 0) item.setEnabled(false);
            menu.add(item);
            
            menu.invalidate();
            menu.repaint();
        }
    }

    public void menuDeselected(MenuEvent evt) {
    }

    public void menuCanceled(MenuEvent evt) {
    }
    
    public void exceptionHandler(Exception exc) {
        RobustMonitor.getInstance().fireEvent(new RobustEvent(this, "Exception", exc));
    }
}
