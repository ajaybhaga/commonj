package org.jcommon.swing;

/**
*	Purpose: Allow a simple progress bar to be displayed.
*	Author: Matthew Hicks
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class JProgressWindow extends Thread implements MouseListener, MouseMotionListener, ActionListener {
	protected FocusManager fm;
	protected FocusManager cfm;
	protected Frame frame;
	protected JPanel glassPane;
	protected JWindow window;
	protected int max;
	
	protected int count;
	
	protected JLabel title;
	protected JProgressBar bar;
	protected JLabel percent;
	protected JLabel label;
	protected String text;
	protected boolean visible;
	protected boolean keepAlive;
	protected boolean consumeEvents;
	
	private boolean cancelButton;
	private ArrayList listeners;
	
	protected int x;
	protected int y;
	
	private JPanel c;
	
	public static void main(String args[]) throws Exception {
		JProgressWindow window = new JProgressWindow(null, "Testing", 50);
		window.start();
		for (int i = 0; i < 50; i++) {
			window.increment("Test " + i);
			Thread.sleep(500);
		}
		window.destroy();
	}
	
	public JProgressWindow(Component parent, String title, int max) {
		if (parent != null) frame = JOptionPane.getFrameForComponent(parent);
		consumeEvents = true;
		if ((frame != null) && (frame instanceof JFrame)) {
			glassPane = new JPanel();
			glassPane.setOpaque(false);
			glassPane.addMouseListener(this);
			fm = FocusManager.getCurrentManager();
			cfm = new ConsumingFocusManager();
			((JFrame)frame).setGlassPane(glassPane);
		} else {
			glassPane = null;
		}
		window = new JWindow(frame);
		//window.addKeyListener(this);
		this.max = max;
		
		count = 1;
		this.title = new JLabel(title);
		this.title.setFont(new Font("Arial", Font.BOLD, 18));
		this.title.setHorizontalAlignment(SwingConstants.CENTER);
		this.title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		bar = new JProgressBar(0, max);
		//bar.addKeyListener(this);
		percent = new JLabel("0%");
		//percent.addKeyListener(this);
		percent.setHorizontalAlignment(SwingConstants.CENTER);
		label = new JLabel("");
		//label.addKeyListener(this);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 10));
		keepAlive = true;
		x = 0;
		y = 0;
	}
	
	public void setTitle(String title) {
	    this.title.setText(title);
	}
	
	public JWindow getWindow() {
	    return window;
	}
	
	public void setConsumeEvents(boolean consumeEvents) {
		this.consumeEvents = consumeEvents;
	}
	
	public void run() {
		if (consumeEvents) FocusManager.setCurrentManager(cfm);
		setLayout();
		if (glassPane != null) glassPane.setVisible(true);
		while (keepAlive) {
			if ((visible) && (!window.isVisible())) {
				//FocusManager.setCurrentManager(cfm);
				window.setVisible(true);
			} else if ((!visible) && (window.isVisible())) {
				//FocusManager.setCurrentManager(fm);
				window.setVisible(false);
			}
			try {
				Thread.sleep(50);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		destroy();
	}
	
	protected void setLayout() {
		window.setSize(300, 150);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = window.getSize();
		double centerWidth = screenSize.width / 2d;
		double centerHeight = screenSize.height / 2d;
		if (frame != null) {
			Point frameLocation = frame.getLocation();
			Dimension frameSize = frame.getSize();
			centerWidth = frameLocation.getX() + (frameSize.width / 2);
			centerHeight = frameLocation.getY() + (frameSize.height / 2);
		}
		window.setLocation((int)centerWidth - (windowSize.width / 2), (int)centerHeight - (windowSize.height / 2));
		window.addMouseListener(this);
		window.addMouseMotionListener(this);
		Container pane = window.getContentPane();
		c = new JPanel();
		pane.add(c);
		c.setLayout(new BorderLayout());
		c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		c.add(BorderLayout.NORTH, title);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		c.add(BorderLayout.CENTER, panel);
		if (cancelButton) {
		    JPanel cancelPanel = new JPanel();
		    JButton cancel = new JButton("Cancel");
		    cancel.addActionListener(this);
		    cancelPanel.add(cancel);
		    c.add(BorderLayout.SOUTH, cancelPanel);
		    //cancel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50), cancel.getBorder()));
		    panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		} else {
		    panel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
		}
		panel.add(BorderLayout.NORTH, percent);
		panel.add(BorderLayout.CENTER, bar);
		panel.add(BorderLayout.SOUTH, label);
		visible = true;
	}
	
	public void enableCancel() {
	    cancelButton = true;
	}
	
	public void addCancelListener(ActionListener listener) {
	    if (listeners == null) {
	        listeners = new ArrayList();
	    }
	    listeners.add(listener);
	}
	
	public void increment(String text) {
		if (keepAlive) {
			if (count < max) {
				count++;
				label.setText(text);
				bar.setValue(count);
				if (this.text != null) {
				    percent.setText((int)(bar.getPercentComplete() * 100) + "% - " + this.text);
				} else {
				    percent.setText((int)(bar.getPercentComplete() * 100) + "%");
				}
			} else {
				destroy();
			}
		} else {
			destroy();
		}
	}
	
	public void destroy() {
		if (glassPane != null) {
			glassPane.setVisible(false);
			if (consumeEvents) FocusManager.setCurrentManager(fm);
		}
		window.setVisible(false);
		window.dispose();
		keepAlive = false;
	}
	
	public void setText(String text) {
		//label.setText(text);
	    this.text = text;
	    setProgress(count);
	}
	
	public void setNote(String text) {
		label.setText(text);
	}
	
	public void setProgress(int count) {
		this.count = count - 1;
		increment(label.getText());
	}
	
	public void setVisible(boolean v) {
		visible = v;
	}
	
	public void actionPerformed(ActionEvent e) {
	    if (listeners != null) {
	        ActionListener listener;
	        for (int i = 0; i < listeners.size(); i++) {
	            listener = (ActionListener)listeners.get(i);
	            listener.actionPerformed(e);
	        }
	    }
	    destroy();
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
		if (e.getSource() instanceof JWindow) {
			x = e.getX();
			y = e.getY();
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		window.setLocation(window.getLocation().x + (e.getX() - x), window.getLocation().y + (e.getY() - y));
	}
	
	public void mouseMoved(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	
	/*public void keyPressed(KeyEvent e) {
	}
	
	public void keyReleased(KeyEvent e) {
	}
	
	public void keyTyped(KeyEvent e) {
	    System.out.println("key typed!");
	    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	        System.out.println("Escape!");
	        destroy();
	    }
	}*/
}
