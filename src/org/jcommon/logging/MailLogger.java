/*
 * Created on Sep 7, 2004
 */
package org.jcommon.logging;

import java.util.*;

/**
 * @author Matt Hicks
 */
public class MailLogger extends Logger {
    protected String from;
    protected String[] to;
    protected String smtpServer;
    
    protected MailSender sender;
    protected List buffer;

    public MailLogger(String from, String[] to, String smtpServer) {
        this.from = from;
        this.to = to;
        this.smtpServer = smtpServer;
        buffer = Collections.synchronizedList(new ArrayList());
    }
    
    public void log(int level, String message) {
        for (int i = 0; i < getLogLevels().length; i++) {
            if (level == getLogLevels()[i]) {
                if (buffer.size() == 0) {
                    buffer.add(0, getLayout().getHeader());
        		    buffer.add(1, getLayout().getLogStart());
                }
                buffer.add(getLayout().format(level, message));
                if (sender == null) sender = new MailSender(from, to, smtpServer, this);
                break;
            }
        }
    }
    
    public List getMessages() {
        return buffer;
    }
    
    public void setSender(MailSender sender) {
        this.sender = sender;
    }
}
