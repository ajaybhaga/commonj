package org.jcommon.j2ee;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.transform.*;
import javax.xml.xpath.*;

import org.jcommon.io.*;
import org.jcommon.util.*;
import org.w3c.dom.*;
import org.w3c.tidy.*;

/**
 * <code>InlineWrapper</code> is a Servlet that provides the ability to include content
 * inline within a web site.
 * 
 * @author Matt Hicks
 */
public class InlineWrapper extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String redirect;
	
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc);
		try {
			redirect = sc.getInitParameter("wrapper"); 
			if (redirect == null) {
				System.err.println("The initialization parameter \"wrapper\" was not set properly for InlineWrapper!");
			}
		} catch(Exception exc) {
			throw new ServletException(exc);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Split out values
		String postData = StringUtilities.toString(request.getInputStream());
		int start = postData.indexOf("wrap=") + 5;
		int end = postData.indexOf('&', start);
		if (end == -1) {
			end = postData.length();
		}
		String query = postData.substring(start, end);
		query = URLDecoder.decode(query, "UTF-8");
		URL url = new URL(query);
		String servletLocation = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI();
		String content = null;
		try {
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			processHeaders(request, connection);
			content = proxyPage(connection, servletLocation, postData, response);
			request.getSession().setAttribute("InlineHTML", content);
		} catch(Exception exc) {
			throw new ServletException(exc);
		}
		if (content != null) response.sendRedirect(redirect + "?" + query);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("wrap");
		
		// Process GET values
		query = query + generateGET(request);
		
		URL url = new URL(query);
		String servletLocation = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI();
		String content = null;
		try {
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			processHeaders(request, connection);
			content = proxyPage(connection, servletLocation, null, response);
			request.getSession().setAttribute("InlineHTML", content);
		} catch(Exception exc) {
			throw new ServletException(exc);
		}
		if (content != null) response.sendRedirect(redirect + "?" + query);
	}
	
	private static final void processHeaders(HttpServletRequest request, HttpURLConnection connection) {
		Enumeration names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String)names.nextElement();
			if ("host".equals(name)) continue;
			if ("cookie".equals(name)) continue;
			if ("accept-encoding".equals(name)) continue;
			Enumeration values = request.getHeaders(name);
			while (values.hasMoreElements()) {
				String value = (String)values.nextElement();
				connection.addRequestProperty(name, value);
			}
		}
	}
	
	public static final String proxyPage(URL url, String proxyURL, boolean absoluteProxy) throws XPathExpressionException, IOException, TransformerFactoryConfigurationError, TransformerException {
		return proxyPage((HttpURLConnection)url.openConnection(), proxyURL, null, null, absoluteProxy);
	}
	
	public static final String proxyPage(HttpURLConnection connection, String proxyURL, String postData, HttpServletResponse response) throws IOException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {
		return proxyPage(connection, proxyURL, postData, response, true);
	}
	
	public static final String proxyPage(HttpURLConnection connection, String proxyURL, String postData, HttpServletResponse response, boolean absoluteProxy) throws IOException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {
		Tidy tidy = new Tidy();
		tidy.setXmlOut(true);
		tidy.setShowWarnings(false);
		tidy.setQuiet(true);
		
		if (postData != null) {
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			//StreamUtilities.stream(postInput, connection.getOutputStream());
			StreamUtilities.stream(postData, connection.getOutputStream());
		} else {
			connection.setRequestMethod("GET");
		}
		if ((connection.getContentType() == null) || (connection.getContentType().startsWith("text/html"))) {
			Document document = tidy.parseDOM(connection.getInputStream(), null);
			
			// Determine if there's a frameset, if so, it screws up our agenda
			NodeList list = document.getElementsByTagName("frameset");
			if (list.getLength() > 0) {
				// We can't display it properly so we embed it
				//return "<object type=\"text/html\" data=\"" + url.toString() + "\"><p><a href=\"" + url.toString() + "\">view somepage</a></p></object>";
				return "<iframe src=\"" + connection.getURL().toString() + "\" style=\"width: 90%; height: 600px;\" marginwidth=\"0\" marginheight=\"0\" frameborder=\"0\" vspace=\"0\" hspace=\"0\"></iframe>";
			}
			
			// Find BASE and delete if it exists
			list = document.getElementsByTagName("base");
			if (list.getLength() > 0) {
				Element e = (Element)list.item(0);
				e.getParentNode().removeChild(e);
			}
			
			// Find CSS link references
			// TODO disabled because of issues causing rest of page to go crazy
//			list = document.getElementsByTagName("link");
//			for (int i = 0; i < list.getLength(); i++) {
//				Element e = (Element)list.item(i);
//				String modified = makeAbsoluteURL(url, e.getAttribute("href"));
//				e.setAttribute("href", modified);
//			}
			
			// Parse images
			list = document.getElementsByTagName("img");
			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element)list.item(i);
				String modified = makeAbsoluteURL(connection.getURL(), e.getAttribute("src"), true);
				e.setAttribute("src", modified);
			}
			
			// Parse Table background images
			list = document.getElementsByTagName("table");
			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element)list.item(i);
				if (e.getAttribute("background").length() > 0) {
					String modified = makeAbsoluteURL(connection.getURL(), e.getAttribute("background"), true);
					e.setAttribute("background", modified);
				}
			}
			
			// Parse TD background images
			list = document.getElementsByTagName("td");
			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element)list.item(i);
				if (e.getAttribute("background").length() > 0) {
					String modified = makeAbsoluteURL(connection.getURL(), e.getAttribute("background"), true);
					e.setAttribute("background", modified);
				}
			}
			
			// Parse Script src
			list = document.getElementsByTagName("script");
			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element)list.item(i);
				if (e.getAttribute("src").length() > 0) {
					String modified = makeAbsoluteURL(connection.getURL(), e.getAttribute("src"), true);
					e.setAttribute("src", modified);
				}
			}
			
			// Proxy URLs if proxyURL is not null
			if (proxyURL != null) {
				// Modify links to direct through InlineWrapper
				list = document.getElementsByTagName("a");
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element)list.item(i);
					if (e.getAttribute("href").length() > 0) {
						String modified = makeAbsoluteURL(connection.getURL(), e.getAttribute("href"), absoluteProxy);
						if (modified.startsWith("mailto:")) {
							e.setAttribute("href", modified);
						} else if (e.getAttribute("href").startsWith("javascript:")) {
							// Do nothing
						} else if (modified.toLowerCase().endsWith(".pdf")) {
							e.setAttribute("href", modified);
						} else if (modified.toLowerCase().endsWith(".rtf")) {
							e.setAttribute("href", modified);
						} else {
							e.setAttribute("href", proxyURL + "?wrap=" + URLEncoder.encode(modified, "UTF-8"));
						}
					}
				}
				
				// Modify links in an image map to direct through InlineWrapper
				list = document.getElementsByTagName("area");
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element)list.item(i);
					if (e.getAttribute("href").length() > 0) {
						String modified = makeAbsoluteURL(connection.getURL(), e.getAttribute("href"), absoluteProxy);
						if (modified.startsWith("mailto:")) {
							e.setAttribute("href", modified);
						} else if (e.getAttribute("href").startsWith("javascript:")) {
							// Do nothing
						} else if (modified.toLowerCase().endsWith(".pdf")) {
							e.setAttribute("href", modified);
						} else if (modified.toLowerCase().endsWith(".rtf")) {
							e.setAttribute("href", modified);
						} else {
							e.setAttribute("href", proxyURL + "?wrap=" + URLEncoder.encode(modified, "UTF-8"));
						}
					}
				}
				
				// Modify forms to submit back to InlineWrapper
				list = document.getElementsByTagName("form");
				for (int i = 0; i < list.getLength(); i++) {
					Element e = (Element)list.item(i);				
					if (e.getAttribute("action").length() > 0) {
						String modified = makeAbsoluteURL(connection.getURL(), e.getAttribute("action"), absoluteProxy);
						
						// Create hidden input
						Element wrap = document.createElement("input");
						wrap.setAttribute("type", "hidden");
						wrap.setAttribute("name", "wrap");
						wrap.setAttribute("value", modified);
						e.appendChild(wrap);
						
						if (modified.startsWith("mailto:")) {
							e.setAttribute("action", modified);
						} else {
							e.setAttribute("action", proxyURL);
						}
					}
				}
			}
			
		    StringOutputStream sos = new StringOutputStream();
		    tidy.pprint(document, sos);
		    
		    // Do pattern matching for window.location via JavaScript
		    Pattern p = Pattern.compile("window.location ??= ??'(.*)'");
		    Matcher m = p.matcher(sos.getString());
		    StringBuffer sb = new StringBuffer();
		    while (m.find()) {
		    	String replacement = m.group().replaceAll(m.group(1), makeAbsoluteURL(connection.getURL(), m.group(1), true));
		    	System.out.println("Replacing: " + m.group(1) + " with " + replacement + ":" + m.group() + ":");
		    	m.appendReplacement(sb, replacement);
		    }
		    m.appendTail(sb);
		    
		    // <base href="http://www.yahoo.com/" target=_top>
		    p = Pattern.compile("<base.*?>");
		    m = p.matcher(sb.toString());
		    sb = new StringBuffer();
		    while (m.find()) {
		    	//String replacement = m.group().replaceAll(m.group(1), makeAbsoluteURL(connection.getURL(), m.group(1)));
		    	//System.out.println("Replacing: " + m.group(1) + " with " + replacement + ":" + m.group() + ":");
		    	m.appendReplacement(sb, "");
		    }
		    m.appendTail(sb);
		    
			return sb.toString();
		} else if (connection.getContentType().startsWith("text/plain")) {
			String content = StringUtilities.toString(connection.getInputStream());
			content = content.replaceAll("\r\n", "<br/>");
			content = content.replaceAll("\n", "<br/>");
			content = content.replaceAll(" ", "&nbsp;");
			return content;
		} else if (connection.getContentType().startsWith("application")) {
			InputStream in = connection.getInputStream();
			OutputStream out = response.getOutputStream();
			int len;
			byte[] b = new byte[512];
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			out.flush();
			out.close();
			in.close();
			return null;
		} else {
			throw new IOException("Unknown Content Type: " + connection.getContentType());
		}
	}
	
	private static final String makeAbsoluteURL(URL url, String original, boolean absolute) {
		if (!absolute) return original;
		
		String root = url.getProtocol() + "://" + url.getHost();
		if ((url.getPort() > 0) && (url.getPort() != 80)) {
			root += ":" + url.getPort();
		}
		String path = "/";
		if (url.getPath().length() > 0) {
			path = url.getPath().substring(0, url.getPath().lastIndexOf('/') + 1);
		}
		if (original.startsWith("/")) {
			path = original;
		} else if (original.startsWith("../")) {
			path = path + original;
		} else if (original.startsWith("http://")) {
			return original;
		} else if (original.startsWith("mailto:")) {
			return original;
		} else {
			path = path + original;
		}
		
		return root + path;
	}

	private static final String generateGET(HttpServletRequest request) throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer();
		Enumeration iterator = request.getParameterNames();
		while (iterator.hasMoreElements()) {
			String key = (String)iterator.nextElement();
			if ("wrap".equals(key)) {
				continue;
			}
			String value = request.getParameter(key);
			if (buffer.length() == 0) {
				buffer.append("?");
			} else {
				buffer.append("&");
			}
			buffer.append(key + "=" + value);
		}
		return buffer.toString();
	}

	public static void main(String[] args) throws Exception {
		String s = "<one><two><three></three><base href=\"http://www.yahoo.com/\" target=_top></two><three></three></one>";
		Pattern p = Pattern.compile("<base.*?>");
	    Matcher m = p.matcher(s);
	    StringBuffer sb = new StringBuffer();
	    while (m.find()) {
	    	System.out.println("Replacing:" + m.group());
	    }
	}
}