/*
 * Created on Mar 25, 2005
 */
package org.jcommon.io;

import java.io.*;

import javax.swing.filechooser.FileFilter;

/**
 * @author Matt Hicks
 */
public class SimpleFileFilter extends FileFilter {
    private String description;
    private String endsWith;
    
    public SimpleFileFilter(String description, String endsWith) {
        this.description = description;
        this.endsWith = endsWith;
    }
    
    public boolean accept(File f) {
        if (f.isFile()) {
            if (f.getName().toLowerCase().endsWith(endsWith.toLowerCase())) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public String getDescription() {
        return description;
    }
    
    public String toString() {
        return description;
    }
}
