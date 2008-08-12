package org.jcommon.web;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Matt Hicks
 *
 */
public interface RequestHandler {
	public void handleRequest(HashMap<String,String> headers, Socket s, WebSession session) throws IOException;
}
