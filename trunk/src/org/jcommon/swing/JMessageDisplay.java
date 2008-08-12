package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jcommon.swing.layout.*;

/**
 * @author Matt Hicks
 *
 */
public class JMessageDisplay extends JPanel {
	private static final long serialVersionUID = -7674968915185386177L;
    
    private JLabel[] rows;
	private long[] timeouts;
	
	private Font defaultFont;
	private Color defaultForeground;
	
	public JMessageDisplay(int lines) {
		setLayout(new TableLayout(1));
        setOpaque(false);
		
		defaultFont = new Font("Arial", Font.BOLD, 12);
		defaultForeground = new Color(51, 51, 51, 255);
		
		rows = new JLabel[lines];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = new JLabel(" ");
			//rows[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			rows[i].setFont(defaultFont);
			add(rows[i]);
		}
		timeouts = new long[lines];
	}
    
    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
    }
    
    public void setDefaultForeground(Color defaultForeground) {
        this.defaultForeground = defaultForeground;
    }
	
	public void addMessage(String message, long timeout) {
		addMessage(message, timeout, defaultFont, defaultForeground);
	}
	
	public void addMessage(String message, long timeout, Font font, Color foreground) {
		for (int i = 1; i < rows.length; i++) {
			rows[i - 1].setText(rows[i].getText());
			rows[i - 1].setForeground(rows[i].getForeground());
			rows[i - 1].setFont(rows[i].getFont());
			timeouts[i - 1] = timeouts[i];
		}
		
		rows[rows.length - 1].setText(message);
		rows[rows.length - 1].setForeground(foreground);
		rows[rows.length - 1].setFont(font);
		if (timeout > 0) {
			timeouts[rows.length - 1] = System.currentTimeMillis() + timeout;
		} else {
			timeouts[rows.length - 1] = 0;
		}
	}
	
	public void update() {
		for (int i = 0; i < timeouts.length; i++) {
			if (timeouts[i] > 0) {
				if (System.currentTimeMillis() > timeouts[i]) {
					removeMessage(i);
				} else if (System.currentTimeMillis() > timeouts[i] - 2500) {
					int alpha = 255 - (int)(System.currentTimeMillis() - timeouts[i] + 2500) / 10;
					Color current = rows[i].getForeground();
					Color color = new Color(current.getRed(), current.getGreen(), current.getBlue(), alpha);
					rows[i].setForeground(color);
				}
			}
		}
	}
	
	private void removeMessage(int index) {
		for (int i = index - 1; i >= 0; i--) {
			rows[i + 1].setText(rows[i].getText());
			rows[i + 1].setForeground(rows[i].getForeground());
			rows[i + 1].setFont(rows[i].getFont());
			timeouts[i + 1] = timeouts[i];
		}
		rows[0].setText(" ");
		rows[0].setFont(defaultFont);
		timeouts[0] = 0;
	}
	
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame();
    	frame.setSize(600, 500);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	Container c = frame.getContentPane();
    	c.setLayout(new BorderLayout());
    	
    	final JMessageDisplay messages = new JMessageDisplay(10);
    	c.add(BorderLayout.CENTER, messages);
    	
    	JTextField text = new JTextField();
    	text.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField text = (JTextField)e.getSource();
				if (text.getText().startsWith("T")) {
					messages.addMessage(" " + text.getText(), 10000, new Font("Arial", Font.ITALIC, 16), Color.RED);
				} else {
					messages.addMessage(" " + text.getText(), 5000);
				}
				text.setText("");
			}
    	});
    	c.add(BorderLayout.SOUTH, text);
    	
    	frame.setVisible(true);
    	
    	Thread t = new Thread() {
    		public void run() {
    			while (true) {
    				messages.update();
    				try {
    					Thread.sleep(50);
    				} catch(Exception exc) {
    					exc.printStackTrace();
    				}
    			}
    		}
    	};
    	t.setDaemon(true);
    	t.start();
	}
}
