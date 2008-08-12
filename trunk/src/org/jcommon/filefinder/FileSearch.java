/*
 * Created on Apr 25, 2005
 */
package org.jcommon.filefinder;

import java.io.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class FileSearch {
    private ArrayList patterns;
    private ArrayList startsWithCaseSensitive;
    private ArrayList startsWithCaseInsensitive;
    private ArrayList endsWithCaseSensitive;
    private ArrayList endsWithCaseInsensitive;
    
    public FileSearch() {
        patterns = new ArrayList();
        startsWithCaseSensitive = new ArrayList();
        startsWithCaseInsensitive = new ArrayList();
        endsWithCaseSensitive = new ArrayList();
        endsWithCaseInsensitive = new ArrayList();
    }
    
    public void addSearchPattern(String pattern) {
        patterns.add(pattern);
    }
    
    public void addSearchStartsWith(String startsWith, boolean caseSensitive) {
        if (caseSensitive) {
            startsWithCaseSensitive.add(startsWith);
        } else {
            startsWithCaseInsensitive.add(startsWith);
        }
    }
    
    public void addSearchEndsWith(String endsWith, boolean caseSensitive) {
        if (caseSensitive) {
            endsWithCaseSensitive.add(endsWith);
        } else {
            endsWithCaseInsensitive.add(endsWith);
        }
    }
    
    public boolean matches(File file) {
        return matches(file.getName());
    }
    
    public boolean matches(String filename) {
        String s;
        
        // Patterns
        for (int i = 0; i < patterns.size(); i++) {
            s = (String)patterns.get(i);
            if (!filename.matches(s)) {
                return false;
            }
        }
        
        // Case Sensitive Starts With
        for (int i = 0; i < startsWithCaseSensitive.size(); i++) {
            s = (String)startsWithCaseSensitive.get(i);
            if (!filename.startsWith(s)) {
                return false;
            }
        }
        
        // Case Insensitive Starts With
        for (int i = 0; i < startsWithCaseInsensitive.size(); i++) {
            s = (String)startsWithCaseInsensitive.get(i);
            if (!filename.toLowerCase().startsWith(s.toLowerCase())) {
                return false;
            }
        }
        
        // Case Sensitive Ends With
        for (int i = 0; i < endsWithCaseSensitive.size(); i++) {
            s = (String)endsWithCaseSensitive.get(i);
            if (!filename.endsWith(s)) {
                return false;
            }
        }
        
        // Case Insensitive Ends With
        for (int i = 0; i < endsWithCaseInsensitive.size(); i++) {
            s = (String)endsWithCaseInsensitive.get(i);
            if (!filename.toLowerCase().endsWith(s.toLowerCase())) {
                return false;
            }
        }
        
        return true;
    }
}
