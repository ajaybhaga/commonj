package org.jcommon.com;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;

public class Session {
	private SocketChannel channel;
	private TCPServer server;
	
	private ConcurrentLinkedQueue<ByteBuffer> bufferOutQueue;
	private ByteBuffer currentOutBuffer;
	
	public Session(SocketChannel channel, TCPServer server) throws IOException {
		this.channel = channel;
		this.server = server;
		bufferOutQueue = new ConcurrentLinkedQueue<ByteBuffer>();
		configureSession();
	}
	
	private void configureSession() throws IOException {
		channel.configureBlocking(false);
		channel.socket().setTcpNoDelay(true);
		SelectionKey key = channel.register(server.getSelector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		key.attach(this);
	}
	
	public SocketChannel getSocketChannel() {
		return channel;
	}
	
	public TCPServer getTCPServer() {
		return server;
	}

	public synchronized void write(byte[] bytes) throws Exception {
		if (currentOutBuffer == null) {
			currentOutBuffer = server.getBuffer();
			currentOutBuffer.clear();
		}
		if (bytes.length > currentOutBuffer.remaining()) {
			flush();
			write(bytes);
		} else {
			currentOutBuffer.put(bytes);
		}
	}
	
	public synchronized void flush() {
		if (currentOutBuffer != null) {
			bufferOutQueue.add(currentOutBuffer);
			server.releaseBuffer(currentOutBuffer);
			currentOutBuffer = null;
		}
	}
}
