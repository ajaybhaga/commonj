package org.jcommon.j2ee;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jcommon.util.StreamUtilities;
import org.jcommon.util.StringUtilities;

public class Restreamer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Pattern ANCHOR_MATCH = Pattern.compile("(<a href=\")(.*?)(\".*?>)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private Set<String> domains;
	private List<Pattern> patterns;
	private List<String> replacements;
	
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc);
		
		try {
			// Process domains
			int position = 1;
			
			while (sc.getInitParameter("matchDomain" + position) != null) {
				if (domains == null) {
					domains = new HashSet<String>();
				}
				domains.add(sc.getInitParameter("matchDomain" + position).toLowerCase());
				
				position++;
			}
			
			// Process search and replace entries
			position = 1;
			patterns = new ArrayList<Pattern>();
			replacements = new ArrayList<String>();
			
			while (sc.getInitParameter("replaceSource" + position) != null) {
				patterns.add(Pattern.compile(sc.getInitParameter("replaceSource" + position), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
				if (sc.getInitParameter("replaceTarget" + position) != null) {
					replacements.add(sc.getInitParameter("replaceTarget" + position));
				} else if (sc.getInitParameter("replaceTargetResource" + position) != null) {
					String replacement = StringUtilities.toString(getClass().getClassLoader().getResourceAsStream(sc.getInitParameter("replaceTargetResource" + position)));
					replacements.add(replacement);
				} else {
					throw new ServletException("Unable to find replaceTarget for " + position);
				}
				
				position++;
			}
		} catch(Exception exc) {
			throw new ServletException(exc);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String urlString = request.getParameter("r");
		if (!urlString.startsWith("http://")) {
			// Add host reference
			urlString = "http://" + request.getParameter("h") + urlString;
		}
		
		URL url = new URL(urlString);
		String host = url.getHost();
		if (matchingDomain(host)) {
			// Restream
    		URLConnection connection = url.openConnection();
    		response.setContentLength(connection.getContentLength());
    		response.setContentType(connection.getContentType());
    		
    		if (connection.getContentType().startsWith("text")) {
    			// Modify content
        		String content = StringUtilities.toString(connection.getInputStream());
        		for (int i = 0; i < patterns.size(); i++) {
        			content = fixContent(content, patterns.get(i), replacements.get(i));
        		}
    
        		// Update anchors
        		String replacement = "$1" + request.getRequestURL() + "?h=" + host + "&r=$2$3";
        		content = fixContent(content, ANCHOR_MATCH, replacement);
        		
        		PrintWriter writer = response.getWriter();
        		writer.write(content);
        		writer.flush();
        		writer.close();
    		} else {
    			// Direct stream
    			StreamUtilities.stream(connection.getInputStream(), response.getOutputStream(), true);
    		}
		} else {
			// Excluded from restreaming, so redirect
			response.sendRedirect(urlString);
		}
	}
	
	private boolean matchingDomain(String host) {
		if (domains == null) {
			return true;
		}
		return domains.contains(host.toLowerCase());
	}
	
	private static final String fixContent(String content, Pattern pattern, String replacement) {
		Matcher m = pattern.matcher(content);
		StringBuffer buffer = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(buffer, replacement);
		}
		m.appendTail(buffer);
		return buffer.toString();
	}
}
