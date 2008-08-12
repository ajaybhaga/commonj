/*
 * Created on Nov 24, 2004
 */
package org.jcommon.mail;

/**
 * @author Matt Hicks
 */
public class SMTP {
    public static final int GREETING = 1;
    public static final int EHLO = 2;
    public static final int HELO = 3;
    public static final int MAIL = 4;
    public static final int RCPT = 5;
    public static final int DATA = 6;
    public static final int RSET = 7;
    public static final int VRFY = 8;
    public static final int EXPN = 9;
    public static final int HELP = 10;
    public static final int NOOP = 11;
    public static final int QUIT = 12;
}
