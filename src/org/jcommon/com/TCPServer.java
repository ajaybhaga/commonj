package org.jcommon.com;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import org.jcommon.pool.ObjectPool;
import org.jcommon.util.*;

public class TCPServer {
	private Selector selector;
	private ServerSocketChannel serverChannel;
	
	private Map<SocketChannel, Session> sessions;
	private ObjectPool<ByteBuffer> bufferPool;
	
	public TCPServer(SocketAddress address) throws Exception {
		selector = Selector.open();
		serverChannel = selector.provider().openServerSocketChannel();
		serverChannel.socket().bind(address);
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		sessions = new HashMap<SocketChannel, Session>();
		
		bufferPool = new ObjectPool<ByteBuffer>(new ByteBufferGenerator(512 * 1000), 0, -1);
	}
	
	protected ByteBuffer getBuffer() throws Exception {
		return bufferPool.get();
	}
	
	protected void releaseBuffer(ByteBuffer buffer) {
		bufferPool.release(buffer);
	}
	
	protected Selector getSelector() {
		return selector;
	}
	
	public void update() throws IOException {
		if (selector.selectNow() > 0) {
			Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey activeKey = keys.next();
				keys.remove();
				if (activeKey.isValid()) {
					if (activeKey.isConnectable()) {
						connect((SocketChannel)activeKey.channel());
					}
					if (activeKey.isAcceptable()) {
						accept((ServerSocketChannel)activeKey.channel());
					}
					if (activeKey.isReadable()) {
						read((SocketChannel)activeKey.channel());
					}
					if (activeKey.isWritable()) {
						write((SocketChannel)activeKey.channel());
					}
				}
			}
		}
	}
	
	private void connect(SelectableChannel channel) {
		System.out.println("Connect!");
	}
	
	private void accept(ServerSocketChannel channel) throws IOException {
		Session session = new Session(channel.accept(), this);
		sessions.put(session.getSocketChannel(), session);
		System.out.println("ACCEPTED!");
	}
	
	private void read(SocketChannel channel) {
		System.out.println("Read! " + sessions.get(channel));
	}
	
	private void write(SocketChannel channel) {
		System.out.println("Write! " + sessions.get(channel));
	}
	
	public static void main(String[] args) throws Exception {
		TCPServer server = new TCPServer(new InetSocketAddress(8000));
		Updater updater = new Updater(server);
		Thread thread = new Thread(updater);
		thread.start();
	}
}