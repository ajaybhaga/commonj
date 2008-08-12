/*
 * Created on Nov 2, 2004
 */
package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import org.jcommon.mail.SMTPClientSession;
import org.jcommon.util.*;

/**
 * @author Matt Hicks
 */
public class JException extends JDialog implements ActionListener {
    private Throwable t;
    private JTextPane generic;
    public static String recipientName = "Exception Recipient";
    public static String recipientEmail;
    public static String host;
    public static String application = "Unspecified Application";
    public static int hostPort = 25;
    
    protected JException(Frame frame, Throwable t) {
        super(frame);
        this.t = t;
        setSize(300, 150);
        setResizable(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = getSize();
		double centerWidth = screenSize.width / 2;
		double centerHeight = screenSize.height / 2;
		if (frame != null) {
		    setModal(true);
			Point frameLocation = frame.getLocation();
			Dimension frameSize = frame.getSize();
			if ((frameSize.height > 0) && (frameSize.width > 0)) {
				centerWidth = frameLocation.getX() + (frameSize.width / 2);
				centerHeight = frameLocation.getY() + (frameSize.height / 2);
			}
		}
		setLocation((int)centerWidth - (windowSize.width / 2), (int)centerHeight - (windowSize.height / 2));
        String title = t.getClass().getName().substring(t.getClass().getName().lastIndexOf(".") + 1);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);
        getContentPane().setLayout(new BorderLayout());
        
        JLabel label = new JLabel("An error has occurred:");
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
        getContentPane().add(BorderLayout.NORTH, label);
        
        generic = new JTextPane();
        generic.setContentType("text/html");
        generic.setText("<span style=\"font-family: arial,sans-serif; font-size: small;\">" + t.getClass().getName() + " (" + t.getMessage() + ")</span>");
        generic.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        generic.setEditable(false);
        JScrollPane genericScroller = new JScrollPane(generic);
        genericScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(BorderLayout.CENTER, genericScroller);
        
        JPanel bottomBar = new JPanel();
        bottomBar.setLayout(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 3));
        JButton send = new JButton("Send");
        send.addActionListener(this);
        if ((host == null) || (recipientEmail == null)) {
        	send.setEnabled(false);
        }
        JButton details = new JButton("Details");
        details.addActionListener(this);
        JButton close = new JButton("Close");
        close.addActionListener(this);
        buttons.add(send);
        buttons.add(details);
        buttons.add(close);
        bottomBar.add(BorderLayout.EAST, buttons);
        getContentPane().add(BorderLayout.SOUTH, bottomBar);
        
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            JButton button = (JButton)e.getSource();
            if (button.getText().equals("Details")) {
                setSize(450, 250);
                Point position = getLocation();
                setLocation(position.x - 75, position.y - 25);
                //generic.setContentType("text/html");
                generic.setText(StringUtilities.getHTMLStackTrace(t));
                validate();
                repaint();
                button.setText("Simple");
            } else if (button.getText().equals("Simple")) {
                setSize(300, 150);
                Point position = getLocation();
                setLocation(position.x + 75, position.y + 25);
                generic.setText("<span style=\"font-family: arial,sans-serif; font-size: small;\">" + t.getClass().getName() + " (" + t.getMessage() + ")</span>");
                validate();
                repaint();
                button.setText("Details");
            } else if (button.getText().equals("Close")) {
                setVisible(false);
                dispose();
            } else if (button.getText().equals("Send")) {
                // TODO change this to utilize some sort of external list of addresses to send to
                try {
	                HashMap headers = new HashMap();
	                headers.put("From", "Robust User <" + System.getenv("USERNAME") + ">");
	                headers.put("To", recipientName + " <" + recipientEmail + ">");
	                headers.put("Subject", "Exception Report from " + application);
	                headers.put("Content-Type", "text/html");
	                StringBuffer message = new StringBuffer();
	                message.append("<font face=\"Arial\"><b>Username:</b> " + System.getenv("USERNAME") + "<br/>\r\n");
	                InetAddress address = InetAddress.getLocalHost();
	                message.append("<b>Host:</b> " + address.getHostName() + "<br/>\r\n");
	                message.append("<b>IP:</b> " + address.getHostAddress() + "<br/></font>\r\n");
	                message.append("<hr/><br/>\r\n");
	                message.append(StringUtilities.getHTMLStackTrace(t));
	                SMTPClientSession session = new SMTPClientSession(host, hostPort);
	                if (SMTPClientSession.sendMessage(session, recipientEmail, recipientEmail, headers, message.toString())) {
	                    button.setEnabled(false);
	                }
                } catch(Exception exc2) {
                    exc2.printStackTrace();
                }
            }
        }
    }
        
    public static void showException(Component c, Throwable t) {
        Frame frame = null;
        if (c != null) {
            frame = JOptionPane.getFrameForComponent(c);
        }
        new JException(frame, t);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Testing");
        frame.setSize(400, 400);
        frame.setVisible(true);
        try {
            Integer i = null;
            System.out.println("Integer: " + i.toString());
        } catch(Exception exc) {
            JException.showException(frame, exc);
        }
        System.out.println("Returned!");
    }
}
