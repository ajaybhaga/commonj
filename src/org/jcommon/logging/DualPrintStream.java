/*
 * Created on Nov 24, 2004
 */
package org.jcommon.logging;

import java.io.*;

/**
 * @author Matt Hicks
 */
public class DualPrintStream extends PrintStream {
    private PrintStream ps1;
    private PrintStream ps2;
    
    public DualPrintStream(PrintStream ps1, PrintStream ps2) {
        super(new OutputStream() {
            public void write(int b) {
                // do nothing
            }
        });
        this.ps1 = ps1;
        this.ps2 = ps2;
    }
    
    public boolean checkError() {
        ps1.checkError();
        return ps2.checkError();
    }
    
    public void close() {
        ps1.close();
        ps2.close();
    }
    
    public void flush() {
        ps1.flush();
        ps2.flush();
    }
    
    public void print(boolean b) {
        ps1.print(b);
        ps2.print(b);
    }
    
    public void print(char c) {
        ps1.print(c);
        ps2.print(c);
    }
    
    public void print(char[] s) {
        ps1.print(s);
        ps2.print(s);
    }
    
    public void print(double d) {
        ps1.print(d);
        ps2.print(d);
    }
    
    public void print(float f) {
        ps1.print(f);
        ps2.print(f);
    }
    
    public void print(int i) {
        ps1.print(i);
        ps2.print(i);
    }
    
    public void print(long l) {
        ps1.print(l);
        ps2.print(l);
    }
    
    public void print(Object obj) {
        ps1.print(obj);
        ps2.print(obj);
    }
    
    public void print(String s) {
        ps1.print(s);
        ps2.print(s);
    }
    
    public void println() {
        ps1.println();
        ps2.println();
    }
    
    public void println(boolean b) {
        ps1.println(b);
        ps2.println(b);
    }
    
    public void println(char c) {
        ps1.println(c);
        ps2.println(c);
    }
    
    public void println(char[] s) {
        ps1.println(s);
        ps2.println(s);
    }
    
    public void println(double d) {
        ps1.println(d);
        ps2.println(d);
    }
    
    public void println(float f) {
        ps1.println(f);
        ps2.println(f);
    }
    
    public void println(int i) {
        ps1.println(i);
        ps2.println(i);
    }
    
    public void println(long l) {
        ps1.println(l);
        ps2.println(l);
    }
    
    public void println(Object obj) {
        ps1.println(obj);
        ps2.println(obj);
    }
    
    public void println(String s) {
        ps1.println(s);
        ps2.println(s);
    }
    
    protected void setError() {
        // Unimplemented override
    }
    
    public void write(byte[] buf, int off, int len) {
        ps1.write(buf, off, len);
        ps2.write(buf, off, len);
    }
    
    public void write(int b) {
        ps1.write(b);
        ps2.write(b);
    }
    
    public void write(byte[] b) throws IOException {
        ps1.write(b);
        ps2.write(b);
    }
}
