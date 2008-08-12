package org.jcommon.j2ee;

import java.io.*;
import java.net.*;

public class RemoteInvoker {
	public static final Object invokeMethod(String servletURL, String method, Object... params) throws IOException, ClassNotFoundException {
		URL url = new URL(servletURL + "?" + method);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setDoOutput(true);
		ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
		for (int i = 0; i < params.length; i++) {
			oos.writeObject(params[i]);
		}
		oos.flush();
		oos.close();
		
		Object responseObject = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(connection.getInputStream());
			responseObject = ois.readObject();
		} catch(EOFException exc) {
			// No object or corrupt data
		} finally {
			if (ois != null) ois.close();
			connection.disconnect();
		}
		return responseObject;
	}
}
