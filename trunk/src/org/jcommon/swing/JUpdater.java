package org.jcommon.swing;

import java.io.*;
import java.net.*;

import javax.swing.*;

public class JUpdater extends Thread {
	/** Install the file if it doesn't exist */
	public static final int INSTALL = 1;
	/** Install the file if it doesn't exist and check for updates if it does */
	public static final int INSTALL_UPDATE = 2;
	/** Update the file if it exists */
	public static final int UPDATE = 3;
	
	private URL jarURL;
	private File jarFile;
	private JSplashScreen splash;
	private int startProgress;
	private int endProgress;
	private int mode;
	
	private boolean completed;
	
	public JUpdater(URL jarURL, File jarFile, JSplashScreen splash, int startProgress, int endProgress, int mode) {
		this.jarURL = jarURL;
		this.jarFile = jarFile;
		this.splash = splash;
		this.startProgress = startProgress;
		this.endProgress = endProgress;
		this.mode = mode;
	}
	
	public void run() {
		try {
			boolean update = false;
			HttpURLConnection connection = (HttpURLConnection)jarURL.openConnection();
			try {
				String updateMessage = null;
				if (!jarFile.exists()) {
					if ((!jarFile.getParentFile().exists()) && (!jarFile.getParentFile().mkdirs())) {
						JException.showException(null, new Exception("Unable to create path " + jarFile.getParentFile().getCanonicalPath()));
						return;
					}
					if ((mode == INSTALL) || (mode == INSTALL_UPDATE)) {
						update = true;
						updateMessage = "Downloading";
					}
				} else {
					if (connection.getResponseCode() == 200) {
						if (connection.getLastModified() != jarFile.lastModified()) {
							if ((mode == UPDATE) || (mode == INSTALL_UPDATE)) {
								update = true;
								updateMessage = "Updating";
							}
						}
					}
				}
				if (connection.getResponseCode() != 200) {
					update = false;
				}
				splash.setVisible(true);
				if (update) {
					long current = 0;
					long total = connection.getContentLength();
					InputStream in = connection.getInputStream();
					OutputStream out = new FileOutputStream(jarFile);
					int len;
					byte[] b = new byte[512];
					while ((len = in.read(b)) != -1) {
						out.write(b, 0, len);
						current += len;
						splash.setProgress((int)((current / total) * (endProgress - startProgress) + startProgress), updateMessage);
					}
					out.flush();
					out.close();
					in.close();
					jarFile.setLastModified(connection.getLastModified());
				}
				splash.setProgress(endProgress, updateMessage + " was successful");
			} finally {
				connection.disconnect();
				completed = true;
			}
		} catch(Exception exc) {
			splash.dispose();
			exc.printStackTrace();
			JException.showException(null, exc);
			completed = true;
		}
	}

	public void waitForCompletion() throws InterruptedException {
		while (!completed) {
			Thread.sleep(50);
		}
	}
}