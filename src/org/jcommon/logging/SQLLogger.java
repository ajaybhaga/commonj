/*
 * Created on Nov 22, 2004
 */
package org.jcommon.logging;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.sql.*;

/**
 * @author Matt Hicks
 */
public class SQLLogger extends Logger implements Runnable {
    protected DataSource dataSource;
    protected LoggingSystem parent;
    protected String table;
    protected String dateColumn;
    protected String threadColumn;
    protected String levelColumn;
    protected String loggerColumn;
    protected String messageColumn;
    protected List buffer;
    
    public SQLLogger(DataSource dataSource, LoggingSystem parent, String table, String dateColumn, String threadColumn, String levelColumn, String loggerColumn, String messageColumn) {
        this.dataSource = dataSource;
        this.parent = parent;
        this.table = table;
        this.dateColumn = dateColumn;
        this.threadColumn = threadColumn;
        this.levelColumn = levelColumn;
        this.loggerColumn = loggerColumn;
        this.messageColumn = messageColumn;
        buffer = Collections.synchronizedList(new ArrayList());
        setLogLevels(new int[] {Logger.DEBUG, Logger.INFORMATION, Logger.WARNING, Logger.ERROR});
        Thread thread = new Thread(this);
        thread.start();
    }
    
    public void log(int level, String message) {
        LogEvent event = new LogEvent(new GregorianCalendar(), Thread.currentThread(), level, parent, message);
        buffer.add(event);
    }
    
    public void run() {
        while (LoggingSystem.isOpen()) {
            try {
                Thread.sleep(3000);
                LogEvent event;
                if (buffer.size() > 0) {
                    Connection connection = dataSource.getConnection();
                    PreparedStatement statement = getPreparedStatement(connection);
                    int size = buffer.size();
                    for (int i = 0; i < size; i++) {
                        event = (LogEvent)buffer.get(0);
                        logEvent(event, statement);
                        buffer.remove(0);
                    }
                    connection.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public PreparedStatement getPreparedStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("INSERT INTO " + table + " (" + dateColumn + ", " + threadColumn + ", " + levelColumn + ", " + loggerColumn + ", " + messageColumn + ") VALUES (?, ?, ?, ?, ?)");
    }
    
    public void logEvent(LogEvent event, PreparedStatement statement) throws Exception {
        statement.setDate(1, new Date(event.getDateTime().getTimeInMillis()));
        statement.setString(2, event.getThread().getName());
        statement.setString(3, Logger.getLevelString(event.getLevel()));
        statement.setString(4, event.getLoggingSystem().getName());
        statement.setString(5, event.getMessage());
        statement.execute();
    }
}
