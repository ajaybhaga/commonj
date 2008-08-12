package org.jcommon.j2ee;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

public class FormRequest {
	private HashMap files;
	private HashMap fileNames;
	private HashMap parameters;
	
	// TODO replace apache's fileupload with MultipartReader wrapper
	public FormRequest(HttpServletRequest request, File directory) throws Exception {
		/*
		files = new LinkedHashMap();
		fileNames = new LinkedHashMap();
		parameters = new LinkedHashMap();
		if (directory == null) {
			directory = new File(".");
		}
		
		if (FileUpload.isMultipartContent(request)) {
			DiskFileUpload upload = new DiskFileUpload();
			List items = upload.parseRequest(request);
			Iterator iterator = items.iterator();
			FileItem item;
			String filename;
			File file;
			while (iterator.hasNext()) {
				item = (FileItem)iterator.next();
				if (item.isFormField()) {
					parameters.put(item.getFieldName(), item.getString());
				} else {
					filename = item.getName();
					fileNames.put(item.getFieldName(), filename);
					if (filename.trim().length() > 0) {
					    file = getFile(filename, directory); 
					    item.write(file);
					    files.put(item.getFieldName(), file);
					}
				}
			}
		} else {
			Enumeration e = request.getParameterNames();
			String key;
			String value;
			while (e.hasMoreElements()) {
				key = (String)e.nextElement();
				value = request.getParameter(key);
				parameters.put(key, value);
			}
		}*/
	}
	
	public String[] getFileFieldNames() {
		String[] names = new String[fileNames.size()];
		Iterator iterator = fileNames.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			names[i++] = (String)iterator.next();
		}
		return names;
	}
	
	public String[] getFileNames() {
		String[] names = new String[fileNames.size()];
		Iterator iterator = fileNames.values().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			names[i++] = (String)iterator.next();
		}
		return names;
	}
	
	public File[] getFiles() {
		File[] files = new File[this.files.size()];
		Iterator iterator = this.files.values().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			files[i++] = (File)iterator.next();
		}
		return files;
	}
	
	public File getFile(String fieldName) {
		return (File)files.get(fieldName);
	}
	
	public String[] getParameterNames() {
		String[] names = new String[parameters.size()];
		Iterator iterator = parameters.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			names[i++] = (String)iterator.next();
		}
		return names;
	}
	
	public String[] getParameterValues() {
		String[] names = new String[parameters.size()];
		Iterator iterator = parameters.values().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			names[i++] = (String)iterator.next();
		}
		return names;
	}
	
	public String getParameter(String key) {
		return (String)parameters.get(key);
	}
	
	/*private static final File getFile(String filename, File directory) {
	    if (filename.indexOf("/") > -1) {
	        filename = filename.substring(filename.lastIndexOf("/") + 1);
	    }
	    if (filename.indexOf("\\") > -1) {
	        filename = filename.substring(filename.lastIndexOf("\\") + 1);
	    }
		String pre = filename;
		String post = "";
		if (filename.indexOf(".") > -1) {
			pre = filename.substring(0, filename.lastIndexOf("."));
			post = filename.substring(filename.lastIndexOf(".") + 1);
		}
		File file = new File(directory, filename);
		int i = 1;
		while (file.exists()) {
			file = new File(directory, pre + i + "." + post);
			System.out.println("File: " + file.getAbsolutePath());
			i++;
		}
		return file;
	}*/
}