package org.jcommon.j2ee;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.jcommon.util.*;

public class VirtualProxy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static String base;
	public static HashMap<String, String> mappings;

	public void init(ServletConfig sc) throws ServletException {
		mappings = new HashMap<String, String>();
		
		Enumeration e = sc.getInitParameterNames();
		while (e.hasMoreElements()) {
			String virtualDirectory = (String)e.nextElement();
			String actualDirectory = (String)sc.getInitParameter(virtualDirectory);
			if (virtualDirectory.equals("base")) {
				base = actualDirectory;
			} else {
				mappings.put(virtualDirectory, actualDirectory);
			}
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		stream(request.getServletPath().substring(1), request, response, null);
	}
	
	public static void stream(String mapping, HttpServletRequest request, HttpServletResponse response, String attachment) throws IOException, ServletException {
		String path = mapping;
		if (path.lastIndexOf('/') == -1) {
			if ("vp".equals(path)) {
				path = request.getRequestURI().substring(request.getRequestURI().indexOf("/vp/") + 4, request.getRequestURI().length());
				mapping = path;
			}
		}
		path = path.substring(0, path.lastIndexOf('/'));
		String filename = mapping.substring(mapping.lastIndexOf('/') + 1);
		path = mappings.get(path);
		URL url;
		if (path == null) {
			// No mapping, so we don't proxy
			url = request.getSession().getServletContext().getResource(request.getServletPath());
		} else {
			if (base != null) {
				path = base + path;
			}
			File file = new File(path + "/" + filename);
			if (!file.exists()) {
				throw new FileNotFoundException("File does not exist: " + file.getCanonicalPath());
			}
			url = file.toURI().toURL();
		}
		
		if (attachment != null) {
            response.addHeader("Content-Disposition", "Attachment; filename=\"" + attachment + "\"");
        }
		
		URLConnection connection = url.openConnection();
		String contentType = connection.getContentType();
		response.setContentType(contentType);
		InputStream is = connection.getInputStream();
		ServletOutputStream out = response.getOutputStream();
		StreamUtilities.stream(is, out, false);
		out.flush();
		out.close();
		is.close();
	}
}
