/*
 * Created on Sep 7, 2004
 */
package org.jcommon.logging;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Matt Hicks
 */
public class MailSender extends Thread {
    private static long wait = 10000;
    
    protected String from;
    protected String[] to;
    protected String smtpServer;
    protected MailLogger logger;
    
    protected BufferedReader reader;
    protected BufferedWriter writer;
    
    public MailSender(String from, String[] to, String smtpServer, MailLogger logger) {
        this.from = from;
        this.to = to;
        this.smtpServer = smtpServer;
        this.logger = logger;
        this.start();
    }
    
    public void run() {
        try {
            Thread.sleep(wait);
            
            StringBuffer buffer = new StringBuffer();
            List messages = logger.getMessages();
            int size = messages.size();
            for (int i = 0; i < size; i++) {
                buffer.append((String)messages.get(0));
                messages.remove(0);
            }
            
            Socket s = new Socket(smtpServer, 25);
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            String response = reader.readLine();
            if (response.startsWith("220")) {
                sendMessage("HELO MailSender");
                response = reader.readLine();
                if (response.startsWith("250")) {
                    sendMessage("MAIL FROM:" + from);
                    response = reader.readLine();
                    StringBuffer recipients = new StringBuffer();
                    for (int i = 0; i < to.length; i++) {
                        if (response.startsWith("250")) {
                            sendMessage("RCPT TO:" + to[i]);
                            if (i > 0) recipients.append("; ");
                            recipients.append(to[i]);
                            response = reader.readLine();
                        } else {
                            break;
                        }
                    }
                    if (response.startsWith("250")) {
                        sendMessage("DATA");
                        response = reader.readLine();
                        if (response.startsWith("354")) {
                           sendMessage("From: " + from);    
                           sendMessage("To: " + recipients.toString());
                           sendMessage("Subject: Task Scheduler Failure");
                           sendMessage("Content-Type: text/html");
                           sendMessage("");
                           sendMessage(buffer.toString());
                           sendMessage(".");
                           response = reader.readLine();
                           if (response.startsWith("250")) {
                               sendMessage("QUIT");
                               response = reader.readLine();
                               if (response.startsWith("221")) {
                                   response = null;
                               }
                           }
                        }
                    }
                }
            }
            if (response != null) {
                System.out.println("An error occurred while trying to send mail: " + response);
            }
            s.close();
            buffer = null;
            logger.setSender(null);
        } catch(InterruptedException e) {
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void sendMessage(String message) throws IOException {
        //System.out.println("Writing: " + message);
        writer.write(message + "\r\n");
        writer.flush();
    }
}
