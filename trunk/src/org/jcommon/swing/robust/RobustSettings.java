/*
 * Created on May 26, 2005
 */
package org.jcommon.swing.robust;

import java.awt.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class RobustSettings {
    private HashMap map;
    
    protected RobustSettings() {
        map = new HashMap();
        defaultSettings();
    }
    
    public void defaultSettings() {
        addSetting("frameWidth", "800");
        addSetting("frameHeight", "600");
        addSetting("title", "Robust v" + Robust.version);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        addSetting("screenWidth", String.valueOf(d.width));
        addSetting("screenHeight", String.valueOf(d.height));
        addSetting("framePositionX", "centered");
        addSetting("framePositionY", "centered");
        addSetting("defaultStatus", "Ready");
    }
    
    public void addSetting(String name, String value) {
        map.put(name.toLowerCase(), value);
    }
    
    public String getSetting(String name) {
        return (String)map.get(name.toLowerCase());
    }
    
    public Integer getSettingInteger(String name) {
        return new Integer((String)map.get(name.toLowerCase()));
    }
    
    public boolean hasSetting(String name) {
        if (map.get(name.toLowerCase()) != null) {
            return true;
        }
        return false;
    }
}
