/*
 * Created on Oct 22, 2004
 */
package org.jcommon.j2ee;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @author Matt Hicks
 */
public class GetFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private String rootDirectory;
	
	public void init(ServletConfig sc) throws ServletException {
		rootDirectory = sc.getInitParameter("rootDirectory");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String rootDirectory = this.rootDirectory;
		if (request.getSession().getAttribute("rootDirectory") != null) {
			rootDirectory = (String)request.getSession().getAttribute("rootDirectory");
		}
        String file = rootDirectory + "/" + request.getParameter("file");
        System.out.println("Getting file: " + file);
        String attachment = request.getParameter("attachment");
        String contentType = request.getParameter("contentType");
        File f = new File(file);
        get(f.toURI().toURL(), attachment, contentType, response, null);
    }
    
    public static final void get(URL url, String attachment, String contentType, HttpServletResponse response, ServletOutputStream out) throws IOException {
    	if (attachment != null) {
            response.addHeader("Content-Disposition", "Attachment; filename=\"" + attachment + "\"");
        }
        response.setContentType(contentType);
        
        if (out == null) {
        	out = response.getOutputStream();
        }
        InputStream is = url.openStream();
        byte[] b = new byte[8192];
        int n;
        while ((n = is.read(b)) > 0) {
            out.write(b, 0, n);
        }
        out.close();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
