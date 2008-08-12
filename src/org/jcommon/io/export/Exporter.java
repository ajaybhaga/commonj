/*
 * Created on Mar 25, 2005
 */
package org.jcommon.io.export;

import java.io.*;

/**
 * @author Matt Hicks
 */
public abstract class Exporter {
    private File file;
    
    public Exporter(File file) {
        this.file = file;
    }
    
    public File getFile() {
    	return file;
    }
    
    public abstract void setColumnNames(String[] names) throws Exception;
    
    public abstract void addRow(String[] data) throws Exception;
    
    public abstract void close() throws Exception;
}
