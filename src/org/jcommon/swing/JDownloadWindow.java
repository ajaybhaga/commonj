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
public class JDownloadWindow extends JProgressWindow {
    private URL url;

    public JDownloadWindow(Component parent, String title, URL url) {
        super(parent, title, 100);
        this.url = url;
    }
    
    public File download(File directory) throws Exception {
        start();
        try {
	        URLConnection connection = url.openConnection();
	        long length = connection.getContentLength();
	        String filename = url.getFile();
	        if (filename.startsWith("/")) {
	            filename = filename.substring(1);
	        }
	        if (filename.indexOf("/") > -1) {
	            filename = filename.substring(filename.lastIndexOf("/") + 1);
	        }
	        File file = new File(directory.getPath() + "/" + filename);
	        FileOutputStream output = new FileOutputStream(file);
	        InputStream input = connection.getInputStream();
	        
	        SizeFormatter formatter = new SizeFormatter();
	        byte[] b = new byte[8192];
	        int n;
	        long written = 0;
	        int percent = 0;
	        String lengthBytes = formatter.format(length);
	        while ((n = input.read(b)) > 0) {
	            output.write(b, 0, n);
	            written += n;
	            percent = (int)(((double)written / (double)length) * 100);
	            setProgress(percent);
	            setNote(formatter.format(written) + " of " + lengthBytes);
	            formatter.format(written);
	            formatter.format(length);
	        }
	        output.close();
	        
			destroy();
			return file;
        } finally {
            destroy();
        }
    }
}
