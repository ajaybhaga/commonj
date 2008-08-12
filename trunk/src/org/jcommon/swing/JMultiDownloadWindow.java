/*
 * Created on Nov 5, 2004
 */
package org.jcommon.swing;

import java.awt.*;
import java.io.*;
import java.net.*;

import org.jcommon.lang.SizeFormatter;

/**
 * @author Matt Hicks
 */
public class JMultiDownloadWindow extends JProgressWindow {
    private URL[] urls;

    public JMultiDownloadWindow(Component parent, String title, URL[] urls) {
        super(parent, title, 100);
        this.urls = urls;
    }
    
    public File[] download(File[] directories) throws Exception {
        start();
        try {
	        URLConnection[] connections = new URLConnection[urls.length];
	        File[] files = new File[urls.length];
	        long[] lengths = new long[urls.length];
	        long downloadSize = 0;
	        String[] filenames = new String[urls.length];
	        for (int i = 0; i < urls.length; i++) {
	            connections[i] = urls[i].openConnection();
	            lengths[i] = connections[i].getContentLength();
	            downloadSize += lengths[i];
	            filenames[i] = urls[i].getFile();
	            if (filenames[i].startsWith("/")) {
	                filenames[i] = filenames[i].substring(1);
		        }
		        if (filenames[i].indexOf("/") > -1) {
		            filenames[i] = filenames[i].substring(filenames[i].lastIndexOf("/") + 1);
		        }
		        if (directories[i].getPath().trim().length() == 0) {
		            files[i] = new File(filenames[i]);
		        } else {
		            files[i] = new File(directories[i].getPath() + "/" + filenames[i]);
		        }
	        }
	        
	        FileOutputStream output;
	        InputStream input;
	        SizeFormatter formatter = new SizeFormatter();
	        byte[] b = new byte[8192];
	        int n;
	        long written;
	        long writtenTotal = 0;
	        int percent;
	        String lengthBytes;
	        for (int i = 0; i < urls.length; i++) {
	            lengthBytes = formatter.format(lengths[i]);
	            output = new FileOutputStream(files[i]);
	            input = connections[i].getInputStream();
	            written = 0;
	            while ((n = input.read(b)) > 0) {
	                output.write(b, 0, n);
	                written += n;
	                writtenTotal += n;
	                percent = (int)(((double)writtenTotal / (double)downloadSize) * 100);
	                setProgress(percent);
	                setNote(filenames[i] + " (" + (i + 1) + " of " + urls.length + ") - " + formatter.format(written) + " of " + lengthBytes);
	            }
	            input.close();
	            output.close();
	        }
	        destroy();
	        
	        return files;
        } finally {
            destroy();
        }
    }
    
    public File[] download(File directory) throws Exception {
        File[] directories = new File[urls.length];
        for (int i = 0; i < urls.length; i++) {
            directories[i] = directory;
        }
        return download(directories);
    }
}
