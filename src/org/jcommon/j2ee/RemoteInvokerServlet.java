package org.jcommon.j2ee;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * RemoteInvokerServlet can be utilized as a mechanism for invoking Java methods on
 * a server via a specific bound object and receiving the responses, all via Java's
 * ObjectInputStream and ObjectOutputStream (request/response).
 * 
 * The web.xml must be configured as follows:
	<servlet>
		<servlet-name>RemoteInvokerServlet</servlet-name>
		<servlet-class>org.jcommon.j2ee.RemoteInvokerServlet</servlet-class>
		<init-param>
			<param-name>RemoteClass</param-name>
			<param-value>com.somewhere.ClassThatIWantToExpose</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>RemoteInvokerServlet</servlet-name>
		<url-pattern>/RemoteInvokerServlet</url-pattern>
	</servlet-mapping>
 * 
 * <code>RemoteClass</code> represents the class that will be exposed on the server.
 * 
 * <b>NOTE:</b> the parameters and returns of methods invoked MUST implement Serializable.
 * 
 * Use the <code>invokeMethod</code> static method in the RemoteInvoker class as a convenient way to
 * call from a client.
 * 
 * @author Matt Hicks
 */
public class RemoteInvokerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Class remoteClass;
	private Object remoteObject;
	
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc);
		try {
			remoteClass = Class.forName(sc.getInitParameter("RemoteClass")); 
			remoteObject = remoteClass.newInstance();
		} catch(Exception exc) {
			throw new ServletException(exc);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getQueryString();
		try {
			ObjectInputStream is = null;
			ArrayList list = new ArrayList();
			try {
				is = new ObjectInputStream(request.getInputStream());
				while (true) {
					Object obj = is.readObject();
					list.add(obj);
				}
			} catch(EOFException exc) {
				// We're done here!
			} finally {
				if (is != null) is.close();
			}
			Object[] params = list.toArray();
			Class[] paramClasses = new Class[params.length];
			for (int i = 0; i < params.length; i++) {
				paramClasses[i] = params[i].getClass();
			}
			Method m = getMethod(remoteClass, query, paramClasses); //remoteClass.getMethod(query, paramClasses);
			if (m == null) throw new ServletException("Cannot find method: " + query);
			Object responseObject = m.invoke(remoteObject, params);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(responseObject);
			os.flush();
			os.close();
		} catch(Exception exc) {
			if (exc instanceof ServletException) throw (ServletException)exc;
			throw new ServletException(exc);
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private static final Method getMethod(Class c, String methodName, Class[] params) {
		Method[] methods = c.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				if (methods[i].getParameterTypes().length == params.length) {
					if (params.length == 0) return methods[i];
					for (int j = 0; j < params.length; j++) {
						if (methods[i].getParameterTypes()[j].isAssignableFrom(params[j])) {
							return methods[i];
						}
					}
				}
			}
		}
		return null;
	}
}
