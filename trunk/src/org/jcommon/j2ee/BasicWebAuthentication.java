package org.jcommon.j2ee;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import org.jcommon.security.*;

import sun.misc.*;

public class BasicWebAuthentication {
	public static final boolean authenticate(HttpServletRequest request, HttpServletResponse response, String realm, Authenticator authenticator) throws IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) {
			StringTokenizer st = new StringTokenizer(authHeader);
			if (st.hasMoreTokens()) {
				String basic = st.nextToken();
				if (basic.equalsIgnoreCase("Basic")) {
					String credentials = st.nextToken();
					
					BASE64Decoder decoder = new BASE64Decoder();
					String userPass = new String(decoder.decodeBuffer(credentials));
					
					int p = userPass.indexOf(':');
					if (p != -1) {
						String username = userPass.substring(0, p);
						String password = userPass.substring(p + 1);
						return authenticator.login(username, password, realm, false);
					}
				}
			}
		} else if (request.getParameter("username") != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			if (request.getParameter("realm") != null) {
				realm = request.getParameter("realm");
			}
			return authenticator.login(username, password, realm, false);
		}
		response.setHeader("WWW-Authenticate", "Basic realm=\"Login\"");
		response.setStatus(401);
		return false;
	}
}
