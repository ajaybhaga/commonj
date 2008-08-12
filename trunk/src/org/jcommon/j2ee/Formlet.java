package org.jcommon.j2ee;

import java.io.*;
import java.util.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;

import com.javarelational.*;
import com.javarelational.sql.bean.*;
import com.javarelational.util.*;
//import com.mysql.jdbc.jdbc2.optional.*;

public class Formlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Session session;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// Initialize the Servlet
			if (session == null) {
				String dbName = "Formlet";
				if (getServletContext().getInitParameter("FormletDB") != null) {
				    dbName = getServletContext().getInitParameter("FormletDB");
				}
				InitialContext initContext = new InitialContext();
				DataSource dataSource = (DataSource)initContext.lookup("java:comp/env/jdbc/FormletDB");
				Server server = JDBCManager.buildServer("FormletDB", dataSource, new String[] {dbName}, new String[] {"Response"});
				JDBCManager.buildDynamicLinks(server);
				session = Session.getSession(server, new Class[] {Response.class});
			}
			// Submit the Response object
			File file = null;
			if (getServletContext().getInitParameter("FormletSaveDirectory") != null) {
			    file = new File(getServletContext().getInitParameter("FormletSaveDirectory"));
			}
			FormRequest fr = new FormRequest(request, file);
			Response r;
			String formIdentifier = request.getParameter("formIdentifier");
			String referer = request.getHeader("Referer");
			String remoteAddress = request.getRemoteAddr();
			String remoteHost = request.getRemoteHost();
			String value;
			GregorianCalendar calendar = new GregorianCalendar();
			long group = getRandomLong();
			String[] names = fr.getParameterNames();
			// Parameters
			for (int i = 0; i < names.length; i++) {
				value = fr.getParameter(names[i]);
				if (names[i].equals("formIdentifier")) continue;
				else if (names[i].equals("redirect")) continue;
				
				r = new Response();
				r.setFormIdentifier(formIdentifier);
				r.setReferer(referer);
				r.setName(names[i]);
				r.setValue(value);
				r.setSubmitted(calendar);
				r.setGroup(group);
				r.setRemoteAddress(remoteAddress);
				r.setRemoteHost(remoteHost);
				session.persist(r);
			}
			// Files
			names = fr.getFileFieldNames();
			for (int i = 0; i < names.length; i++) {
			    file = fr.getFile(names[i]);
				if (names[i].equals("formIdentifier")) continue;
				else if (names[i].equals("redirect")) continue;
				
				r = new Response();
				r.setFormIdentifier(formIdentifier);
				r.setReferer(referer);
				r.setName(names[i]);
				if (file != null) {
				    r.setValue(file.getCanonicalPath());
				}
				r.setSubmitted(calendar);
				r.setGroup(group);
				r.setRemoteAddress(remoteAddress);
				r.setRemoteHost(remoteHost);
				session.persist(r);
			}
			
			// Finished, redirect
			if (request.getParameter("redirect") != null) {
				response.sendRedirect(request.getParameter("redirect"));
			} else {
				response.sendRedirect(request.getHeader("Referer"));
			}
		} catch(Exception exc) {
			throw new ServletException(exc);
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
	
	public static long getRandomLong() {
		long r = Math.round(Math.random() * Long.MAX_VALUE);
		return r;
	}
}
