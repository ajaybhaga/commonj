/*
 * Created on May 26, 2005
 */
package org.jcommon.swing.robust;

import javax.swing.*;

import org.jcommon.swing.robust.gui.*;

/**
 * @author Matt Hicks
 */
public class RobustManager {
    private static RobustManager manager;
    
    private RobustSettings settings;
    private RobustMonitor monitor;
    private RobustDesktop desktop;
    
    protected RobustManager() {
        settings = new RobustSettings();
        monitor = RobustMonitor.getInstance();
        
        desktop = RobustDesktop.getInstance();
    }
    
    public void registerMenu(JMenu menu) {
        desktop.registerMenu(menu);
    }
    
    public void registerMenuItem(String menuName, JMenuItem item) {
        desktop.registerMenuItem(menuName, item);
    }
    
    public void registerWindow(RobustWindow window) {
        desktop.registerWindow(window);
    }
    
    public void initialize() {
        desktop.initialize(settings);
    }
    
    public RobustSettings getSettings() {
        return settings;
    }
}
