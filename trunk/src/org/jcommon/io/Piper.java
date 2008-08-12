package org.jcommon.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Piper {
	private InputStream input;
	private OutputStream output;
	
	private volatile int b;
	
	public Piper() {
		input = new PipedInputStream(this);
		output = new PipedOutputStream(this);
	}
	
	public InputStream getInputStream() {
		return input;
	}
	
	public OutputStream getOutputStream() {
		return output;
	}
	
	protected int get() {
		while (b == -1) {
			try {
				Thread.sleep(10);
			} catch(InterruptedException exc) {
				exc.printStackTrace();
			}
		}
		int b = this.b;
		this.b = -1;
		return b;
	}
	
	protected synchronized void write(int b) {
		while (b != -1) {
			try {
				Thread.sleep(10);
			} catch(InterruptedException exc) {
				exc.printStackTrace();
			}
		}
		this.b = b;
	}
}

class PipedInputStream extends InputStream {
	private Piper piper;
	
	public PipedInputStream(Piper piper) {
		this.piper = piper;
	}
	
	public int read() throws IOException {
		return piper.get();
	}	
}

class PipedOutputStream extends OutputStream {
	private Piper piper;
	
	public PipedOutputStream(Piper piper) {
		this.piper = piper;
	}
	
	public void write(int b) throws IOException {
		piper.write(b);
	}
}
