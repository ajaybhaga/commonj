/*
 * Created on Sep 12, 2004
 */
package org.jcommon.io;

import java.io.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class CustomizableFileFilter implements FileFilter {
    protected boolean acceptFiles;
    protected boolean acceptDirectories;
    protected HashSet startsWith;
    protected HashSet endsWith;
    
    public CustomizableFileFilter() {
        startsWith = new HashSet();
        endsWith = new HashSet();
    }
    
    public boolean accept(File file) {
        if (file.isFile() && acceptFiles) {
            return isNameAcceptable(file);
        } else if (file.isDirectory() && acceptDirectories) {
            return isNameAcceptable(file);
        }
        return false;
    }
    
    protected boolean isNameAcceptable(File file) {
        if (startsWith.size() > 0) {
            if (isStartsWith(file.getName())) {
                if (endsWith.size() > 0) {
                    return isEndsWith(file.getName());
                } else {
                    return true;
                }
            }
        } else if (endsWith.size() > 0) {
            if (isEndsWith(file.getName())) {
                if (startsWith.size() > 0) {
                    return isStartsWith(file.getName());
                } else {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected boolean isStartsWith(String name) {
        Iterator iterator;
        String value;
        iterator = startsWith.iterator();
        while (iterator.hasNext()) {
            value = (String)iterator.next();
            if (name.startsWith(value)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isEndsWith(String name) {
        Iterator iterator;
        String value;
        iterator = endsWith.iterator();
        while (iterator.hasNext()) {
            value = (String)iterator.next();
            if (name.endsWith(value)) {
                return true;
            }
        }
        return false;
    }
    
    public void setAcceptFiles(boolean acceptFiles) {
        this.acceptFiles = acceptFiles;
    }
    
    public void setAcceptDirectories(boolean acceptDirectories) {
        this.acceptDirectories = acceptDirectories;
    }
}
