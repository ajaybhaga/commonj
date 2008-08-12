package org.jcommon.j2ee;

import java.io.*;
import java.util.concurrent.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * <code>LivePage</code> provides support for server push JavaScript events. It is
 * most powerfully used within an iframe embedded in a web page.
 * 
 * @author Matt Hicks
 */
public class LivePage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static long PAGE_TIMEOUT = 5 * 60 * 1000;
	
	private ConcurrentLinkedQueue<String> queue;
	
	public LivePage() {
		queue = new ConcurrentLinkedQueue<String>();
	}
	
	protected void doGet(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Register LivePage in the session
		request.getSession().setAttribute("LivePage", this);
		
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("text/html");
		out.print("<html><head>");
		out.print("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		out.print("<meta http-equiv=\"Pragma\" content=\"no-cache\">");
		out.print("</head><body>\r\n");
		out.flush();
		try {
			long lastLoad = System.currentTimeMillis();
			String query;
			while (true) {
				if ((query = nextQuery()) != null) {
					out.println("<script language=\"JavaScript\">" + query + "</script>");
					out.flush();
				} else {
					if (lastLoad + PAGE_TIMEOUT < System.currentTimeMillis()) {
						out.println("<script language=\"JavaScript\">window.location = 'LivePage';</script>");
						out.flush();
						out.close();
						return;
					} else {
						Thread.sleep(100);
					}
				}
			}
		} catch(InterruptedException exc) {
			throw new ServletException(exc);
		}
	}
	
	protected String nextQuery() {
		return queue.poll();
	}
	
	public void enqueue(String query) {
		queue.add(query);
	}
}
