package org.jcommon.shortcut;

import java.io.*;
import java.util.*;

import javax.swing.*;

import org.jcommon.util.*;

/**
 * Really crappy way of generating Windows shortcuts, but for lack of a better solution,
 * here it is.
 * 
 * @author Matt Hicks
 */
public class Shortcut {
	public static final int WINDOW_DEFAULT = 1;
	public static final int WINDOW_MAXIMIZED = 3;
	public static final int WINDOW_MINIMIZED = 7;
	
	private File location;
	private File target;
	private File workingDirectory;
	private File icon;
	private int iconIndex;
	private List<String> arguments;
	private String description;
	private int windowStyle;

	public Shortcut(File location) {
		this.location = location;
		arguments = new ArrayList<String>();
		windowStyle = WINDOW_DEFAULT;
		iconIndex = 0;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public void addArgument(String argument) {
		arguments.add(argument);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public File getIcon() {
		return icon;
	}

	public void setIcon(File icon) {
		this.icon = icon;
	}

	public int getIconIndex() {
		return iconIndex;
	}

	public void setIconIndex(int iconIndex) {
		this.iconIndex = iconIndex;
	}

	public File getLocation() {
		return location;
	}

	public void setLocation(File location) {
		this.location = location;
	}

	public File getTarget() {
		return target;
	}

	public void setTarget(File target) {
		this.target = target;
	}

	public int getWindowStyle() {
		return windowStyle;
	}

	public void setWindowStyle(int windowStyle) {
		this.windowStyle = windowStyle;
	}

	public void setWorkingDirectory(File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public File getWorkingDirectory() {
		if (workingDirectory == null) {
			workingDirectory = target.getAbsoluteFile().getParentFile();
		}
		return workingDirectory;
	}

	public synchronized void save() throws IOException, InterruptedException {
		if (!target.isFile()) throw new IOException("Target is not a file: " + target.getAbsolutePath());
		if (!getWorkingDirectory().isDirectory()) {
			throw new IOException("Working directory is not a directory: " + workingDirectory.getAbsolutePath());
		}
		
		File scriptFile = new File("createshortcut.vbs");
		BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));
		writer.write("set WshShell = WScript.CreateObject(\"WScript.Shell\")\r\n");
		writer.write("set oShellLink = WshShell.CreateShortcut(\"" + processFile(getLocation()) + "\")\r\n");
		writer.write("oShellLink.TargetPath = \"" + processFile(getTarget()) + "\"\r\n");
		writer.write("oShellLink.WorkingDirectory = \"" + processFile(getWorkingDirectory()) + "\"\r\n");
		if (getDescription() != null) writer.write("oShellLink.Description = \"" + getDescription() + "\"\r\n");
		writer.write("oShellLink.WindowStyle = " + getWindowStyle() + "\r\n");
		if (getIcon() != null) writer.write("oShellLink.IconLocation = \"" + processFile(getIcon()) + "," + getIconIndex() + "\"\r\n");
		if (getArguments().size() > 0) {
			writer.write("oShellLink.Arguments =");
			boolean first = true;
			for (String argument : getArguments()) {
				if (first) {
					writer.write(" \"" + argument);
					first = false;
				} else {
					writer.write(" " + argument);
				}
			}
			writer.write("\"\r\n");
		}
		writer.write("oShellLink.Save");
		writer.flush();
		writer.close();
		
		Process p = Runtime.getRuntime().exec("cmd /C cscript \"" + scriptFile.getCanonicalPath() + "\"");
		System.out.println(StringUtilities.toString(p.getInputStream()));
		p.waitFor();
		if (!scriptFile.delete()) {
			scriptFile.deleteOnExit();
		}
	}
	
	public static synchronized final Shortcut load(File location) throws IOException, InterruptedException {
		Shortcut s = new Shortcut(location);
		
		File scriptFile = new File("readshortcut.vbs");
		BufferedWriter writer = new BufferedWriter(new FileWriter(scriptFile));
		writer.write("set WshShell = WScript.CreateObject(\"WScript.Shell\")\r\n");
		writer.write("set oShellLink = WshShell.CreateShortcut(\"" + processFile(location) + "\")\r\n");
		writer.write("WScript.Echo oShellLink.TargetPath\r\n");
		writer.write("WScript.Echo oShellLink.WorkingDirectory\r\n");
		writer.write("WScript.Echo oShellLink.Description\r\n");
		writer.write("WScript.Echo oShellLink.WindowStyle\r\n");
		writer.write("WScript.Echo oShellLink.IconLocation\r\n");
		writer.write("WScript.Echo oShellLink.Arguments\r\n");
		writer.flush();
		writer.close();
		
		Process p = Runtime.getRuntime().exec("cmd /C cscript " + scriptFile.getCanonicalPath());
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.trim().length() == 0) break;
		}
		s.setTarget(new File(reader.readLine()));
		s.setWorkingDirectory(new File(reader.readLine()));
		s.setDescription(reader.readLine());
		s.setWindowStyle(Integer.parseInt(reader.readLine()));
		String[] parser = reader.readLine().split(",");
		if (parser[0].trim().length() > 0) {
			s.setIcon(new File(parser[0]));
		}
		s.setIconIndex(Integer.parseInt(parser[1]));
		parser = reader.readLine().split(" ");
		for (String arg : parser) {
			if (arg.trim().length() == 0) continue;
			s.addArgument(arg);
		}
		if (!scriptFile.delete()) {
			scriptFile.deleteOnExit();
		}
		
		return s;
	}
	
	private static final String processFile(File file) throws IOException {
		String filepath = file.getCanonicalPath();
		filepath = filepath.replace('/', '\\');
		return filepath;
	}

	public static void main(String[] args) throws Exception {
		Shortcut.load(new File("testjava.lnk"));
//		Shortcut s = new Shortcut(new File("testjava.lnk"));
//		s.setTarget(new File("C:/Program Files/Mozilla Firefox/firefox.exe"));
//		s.setDescription("Testing 1, 2, 3");
//		s.save();
		
//		Map<String,String> env = System.getenv();
//		Iterator<String> iterator = env.keySet().iterator();
//		while (iterator.hasNext()) {
//			String key = iterator.next();
//			String value = env.get(key);
//			System.out.println(key + "," + value);
//		}
	}
}