/*
 * Created on Mar 31, 2005
 */
package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

/**
 * @author Matt Hicks
 */
public class JSplashScreen extends JWindow implements Runnable, MouseListener, MouseMotionListener {
    private Component splash;
    private JLabel label;
    private JProgressBar progress;
    private Color fg;
    private Color bg;
    private boolean showProgress;
    private int max;
    private int current;
    
    private Thread t;
    
    protected int x;
    protected int y;
    
    public JSplashScreen(Component splash, Color fg, Color bg, int max, boolean showProgress) {
        super();
        this.splash = splash;
        this.fg = fg;
        this.bg = bg;
        this.max = max;
        this.showProgress = showProgress;
        current = -1;
        
        initGUI();
    }
    
    private void initGUI() {
    	addMouseListener(this);
        addMouseMotionListener(this);
        Dimension d = splash.getPreferredSize();
        if (showProgress) {
            setSize(d.width, d.height + 60);
        } else {
            setSize(d.width, d.height + 20);
        }
        
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BorderLayout());
        label = new JLabel("");
        label.setOpaque(true);
        if (fg != null) label.setForeground(fg);
        if (bg != null) label.setBackground(bg);
        label.setPreferredSize(new Dimension(100, 20));
        label.setHorizontalAlignment(JLabel.CENTER);
        progressPanel.add(BorderLayout.NORTH, label);
        progress = new JProgressBar(0, max);
        if (showProgress) {
            progress.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createLineBorder(Color.BLACK)));
            if (fg != null) progress.setForeground(fg);
            if (bg != null) progress.setBackground(bg);
            progress.setPreferredSize(new Dimension(100, 40));
            progressPanel.add(BorderLayout.CENTER, progress);
        }
        
        JPanel c = new JPanel();
        c.setBorder(BorderFactory.createEmptyBorder());
        c.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //c.setBorder(BorderFactory.createEtchedBorder());
        c.setLayout(new BorderLayout());
        c.add(BorderLayout.CENTER, splash);
        c.add(BorderLayout.SOUTH, progressPanel);
        
        Container pane = getContentPane();
        pane.add(c);
        
        t = new Thread(this);
        GUI.center(null, this);
        t.setDaemon(true);
        t.start();
    }
    
    public void setName(String name) {
        setProgress(0, name);
        super.setName(name);
    }
    
    public void run() {
        try {
	        while (true) {
	            if ((current >= 0) && (current <= max)) {
	                progress.setValue(current);
	                setVisible(true);
	            } else {
	                if (current > max) {
	                    dispose();
	                }
	                setVisible(false);
	            }
	            synchronized(t) {
	                t.wait();
	            }
	        }
        } catch(InterruptedException exc) {
        }
    }
    
    public void close() {
        dispose();
        t.interrupt();
    }
    
    public void dispose() {
        super.dispose();
        t.interrupt();
    }
    
    public void setProgress(int current, String message) {
        this.current = current;
        label.setText(message);
        synchronized(t) {
            t.notify();
        }
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
		setLocation(getLocation().x + (e.getX() - x), getLocation().y + (e.getY() - y));
	}
	
	public void mouseMoved(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
    
    public static void main(String[] args) throws Exception {
        JLabel label = new JLabel(new ImageIcon(new File("someimage.jpg").toURL()));
        label.setBorder(BorderFactory.createEmptyBorder());
        JSplashScreen splash = new JSplashScreen(label, new Color(47, 61, 127), new Color(128, 132, 160), 100, false);
        System.out.println("Waiting...");
        Thread.sleep(5000);
        System.out.println("Starting...");
        for (int i = 0; i <= 101; i++) {
            splash.setProgress(i, "Testing " + i);
            Thread.sleep(15);
        }
    }
}
