package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.jcommon.util.*;

/**
 * @author Matt Hicks
 */
public class JDropDownCalendar extends JPanel implements CalendarChangeListener, ActionListener {
	public static final int DATE = 8;
	public static final int TIME = 7;
	public static final int DATETIME = 15;
	
	private JTextField editor;
	private JButton arrow;
	private JPopupMenu menu;
	private JCalendar calendar;
	
	private int type;
	
	private long closed;
	
	public JDropDownCalendar() {
		this(DATE);
	}
	
	public JDropDownCalendar(int type) {
		this.type = type;
		
		initGUI();
	}
	
	private void initGUI() {
		calendar = new JCalendar();
		
		editor = new JTextField(type);
		editor.setFont(getFont());
		editor.setEditable(false);
		updateDisplay();
		calendar.addCalendarListener(this);
		
		arrow = GUI.createArrow(GUI.SOUTH);
		arrow.addActionListener(this);
		
		menu = new JPopupMenu();
		menu.setLayout(new BorderLayout());
		menu.add(BorderLayout.CENTER, calendar);
		menu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				closed = System.currentTimeMillis();
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, editor);
		add(BorderLayout.EAST, arrow);
	}
	
	private void updateDisplay() {
		String formatText = "";
		if (type == DATE) {
			formatText = "%MMM% %d%, %yyyy%";
		} else if (type == TIME) {
			formatText = "%HH%:%mm%:%ss% %amPM%";
		} else if (type == DATETIME) {
			formatText = "%MMM% %d%, %yyyy%  %HH%:%mm%:%ss% %amPM%";
		}
		editor.setText(StringUtilities.format(calendar.getCalendar(), formatText));
	}
	
	public void changed(MonitoredGregorianCalendar c, int type, int field, long value) {
		updateDisplay();
	}
	
	public void addCalendarListener(CalendarChangeListener listener) {
		calendar.addCalendarListener(listener);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (menu.isVisible()) {
			MenuSelectionManager.defaultManager().clearSelectedPath();
		} else if (System.currentTimeMillis() - closed > 500) {
			menu.show(this, 0, this.getHeight());
			menu.repaint();
		}
	}
	
	public void setPopupVisible(boolean visible) {
		if (visible) {
			menu.show(this, 0, this.getHeight());
			menu.repaint();
		} else {
			MenuSelectionManager.defaultManager().clearSelectedPath();
		}
	}
	
	public Calendar getCalendar() {
		return calendar.getCalendar();
	}
	
	public void setEditable(boolean editable) {
		arrow.setVisible(editable);
	}
	
	public void setOpaque(boolean opaque) {
		super.setOpaque(opaque);
		if (editor != null) editor.setOpaque(opaque);
	}
	
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (editor != null) editor.setBackground(bg);
	}
	
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame();
    	frame.setSize(600, 500);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	Container c = frame.getContentPane();
    	c.setLayout(new FlowLayout());
    	
    	//JDropDownCalendar calendar = new JDropDownCalendar(DATE);
    	//c.add(calendar);
    	
    	JButton button = new JButton("New Game");
    	button.setPreferredSize(new Dimension(200, 50));
    	c.add(button);
    	
    	frame.setVisible(true);
	}

}
