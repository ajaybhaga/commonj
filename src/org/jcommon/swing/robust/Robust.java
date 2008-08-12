/*
 * Created on May 26, 2005
 */
package org.jcommon.swing.robust;

import java.io.*;

import org.jcommon.swing.robust.event.*;
import org.jcommon.swing.robust.util.*;

/**
 * @author Matt Hicks
 */
public class Robust {
    public static final double version = 0.01;
    
    private static Robust robust;
    
    private RobustManager manager;
    
    private Robust() {
        manager = new RobustManager();
        
        // Redirect Standard Output
        MultiOutputStream stream = new MultiOutputStream();
        stream.addOutputStream(System.out);
        stream.addOutputStream(new RobustEventOutputStream("Output"));
        System.setOut(new PrintStream(stream));
        
        // Redirect Error Output
        stream = new MultiOutputStream();
        stream.addOutputStream(System.err);
        stream.addOutputStream(new RobustEventOutputStream("Error"));
        System.setErr(new PrintStream(stream));
    }
    
    public void initialize() {
        manager.initialize();
    }
    
    public RobustManager getManager() {
        return manager;
    }
    
    public static Robust getInstance() {
        if (robust == null) {
            robust = new Robust();
        }
        return robust;
    }
    
    public static void main(String[] args) {
        // TODO remove this - this is temporary
        Robust.getInstance();
    }
}
