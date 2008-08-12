/*
 * Created on Nov 24, 2004
 */
package org.jcommon.mail;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class SMTPClientSession {
    private static final boolean debug = true;
    
    private String host;
    private int port;
    
    private Socket s;
    private InputStream input;
    private OutputStream output;
    private BufferedReader reader;
    private BufferedWriter writer;
    
    private SMTPResponse response;
    
    public SMTPClientSession(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void connect() throws UnknownHostException, IOException {
        s = new Socket(host, port);
        input = s.getInputStream();
        output = s.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        writer = new BufferedWriter(new OutputStreamWriter(output));
    }
    
    public SMTPResponse getGreeting() throws IOException {
        return getResponse(SMTP.GREETING);
    }
    
    public SMTPResponse doHelo(String msg) throws IOException {
        write("HELO " + msg);
        return getResponse(SMTP.HELO);
    }
    
    public SMTPResponse doEhlo(String msg) throws IOException {
        write("EHLO " + msg);
        return getResponse(SMTP.EHLO);
    }
    
    public SMTPResponse doMail(String from) throws IOException {
        write("MAIL FROM: " + from);
        return getResponse(SMTP.MAIL);
    }
    
    public SMTPResponse doRcpt(String to) throws IOException {
        write("RCPT TO: " + to);
        return getResponse(SMTP.RCPT);
    }
    
    public SMTPResponse doData() throws IOException {
        write("DATA");
        return getResponse(SMTP.DATA);
    }
    
    public SMTPResponse sendData(InputStream data) throws IOException {
        BufferedReader dataReader = new BufferedReader(new InputStreamReader(data));
        String line;
        while ((line = dataReader.readLine()) != null) {
            write(line);
        }
        write(".");
        return getResponse(SMTP.DATA);
    }
    
    public SMTPResponse sendData(String data) throws IOException {
        write(data);
        write(".");
        return getResponse(SMTP.DATA);
    }
    
    public SMTPResponse doQuit() throws IOException {
        write("QUIT");
        SMTPResponse r = getResponse(SMTP.QUIT);
        close();
        return r;
    }
    
    public SMTPResponse getResponse(int type) throws IOException {
        response = new SMTPResponse(type, read());
        return response;
    }
    
    public SMTPResponse getLastResponse() {
        return response;
    }
    
    public String read() throws IOException {
        String line = reader.readLine();
        if (debug) System.out.println("Server> " + line);
        return line;
    }
    
    public void write(String line) throws IOException {
        if (debug) System.out.println("Client> " + line);
        writer.write(line + "\r\n");
        writer.flush();
    }
    
    public void close() {
        try {
            s.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean sendMessage(SMTPClientSession session, String from, String to, InputStream data) throws IOException {
        try {
	        session.connect();
	        SMTPResponse response = session.getGreeting();
	        if (response.isOkay()) {
	            response = session.doHelo(getHeloGreeting());
	            if (response.isOkay()) {
	                response = session.doMail(from);
	                if (response.isOkay()) {
	                    response = session.doRcpt(to);
	                    if (response.isOkay()) {
	                        response = session.doData();
	                        if (response.isOkay()) {
	                            response = session.sendData(data);
	                            if (response.isOkay()) {
	                                response = session.doQuit();
	                                if (response.isOkay()) {
	                                    return true;
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
        } finally {
            session.close();
        }
        return false;
    }
    
    public static boolean sendMessage(SMTPClientSession session, String from, String to, String data) throws IOException {
        try {
	        session.connect();
	        SMTPResponse response = session.getGreeting();
	        if (response.isOkay()) {
	            response = session.doHelo(getHeloGreeting());
	            if (response.isOkay()) {
	                response = session.doMail(from);
	                if (response.isOkay()) {
	                    response = session.doRcpt(to);
	                    if (response.isOkay()) {
	                        response = session.doData();
	                        if (response.isOkay()) {
	                            response = session.sendData(data);
	                            if (response.isOkay()) {
	                                response = session.doQuit();
	                                if (response.isOkay()) {
	                                    return true;
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
        } finally {
            session.close();
        }
        return false;
    }

    public static boolean sendMessage(SMTPClientSession session, String from, String to, HashMap headers, String message) throws IOException {
        StringBuffer data = new StringBuffer();
        Iterator iterator = headers.keySet().iterator();
        String key;
        String value;
        while (iterator.hasNext()) {
            key = (String)iterator.next();
            value = (String)headers.get(key);
            data.append(key + ": " + value + "\r\n");
        }
        data.append("\r\n");
        data.append(message);
        return sendMessage(session, from, to, data.toString());
    }

    public static String getHeloGreeting() throws UnknownHostException {
    	return InetAddress.getLocalHost().getHostAddress();
    }
}
