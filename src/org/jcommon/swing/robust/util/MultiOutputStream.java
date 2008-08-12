package org.jcommon.swing.robust.util;

import java.io.*;

/**
 * @author Matt Hicks
 */
public class MultiOutputStream extends OutputStream {
    private OutputStream[] streams;
    
    public MultiOutputStream() {
        super();
        streams = new OutputStream[0];
    }
    
    public void addOutputStream(OutputStream out) {
        OutputStream[] temp = new OutputStream[streams.length + 1];
        int i = 0;
        for (; i < streams.length; i++) {
            temp[i] = streams[i];
        }
        temp[i] = out;
        streams = temp;
    }

    public void write(int b) throws IOException {
        for (int i = 0; i < streams.length; i++) {
            streams[i].write(b);
        }
    }
    
    public void write(byte[] b) throws IOException {
        for (int i = 0; i < streams.length; i++) {
            streams[i].write(b);
        }
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < streams.length; i++) {
            streams[i].write(b, off, len);
        }
    }
    
    public void flush() throws IOException {
        for (int i = 0; i < streams.length; i++) {
            streams[i].flush();
        }
    }
    
    public void close() throws IOException {
        for (int i = 0; i < streams.length; i++) {
            streams[i].close();
        }
    }
    
    public static void main(String[] args) throws Exception {
        MultiOutputStream output = new MultiOutputStream();
        output.addOutputStream(System.out);
        output.addOutputStream(new FileOutputStream(new File("test.txt")));
        
        PrintStream stream = new PrintStream(output);
        stream.println("Testing, 1, 2, 3");
        stream.flush();
        stream.close();
    }
}
