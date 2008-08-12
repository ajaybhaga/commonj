package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;

import org.jcommon.swing.layout.*;
import org.jcommon.util.*;

/**
 * @author Matt Hicks
 *
 */
public class JCalendar extends JPanel implements ActionListener, MouseListener, CalendarChangeListener {
	private static Font font = new Font("Arial", Font.PLAIN, 12);
	
	private MonitoredGregorianCalendar c;
	private JTextField month;
	private JTextField year;
	private JLabel[] days;
	
	private JPanel top;
	
	private Color highlight;
	private Color selected;
	private int blockSize;
	
	public JCalendar() {
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
		c = new MonitoredGregorianCalendar();
		
		highlight = new Color(158, 200, 245);
		selected = Color.LIGHT_GRAY;
		blockSize = 1;
		
		initGUI();
		c.addListener(this);
	}
	
	public void initGUI() {
		setLayout(new BorderLayout());
		
		top = new JPanel();
		top.setLayout(new GridLayout(1, 2));
		
		JPanel monthPanel = new JPanel();
        monthPanel.setLayout(new BorderLayout());
        JButton leftArrow = GUI.createArrow(BasicArrowButton.WEST);
        leftArrow.setName("PreviousMonth");
        leftArrow.addActionListener(this);
        month = new JTextField(6);
        month.setText(StringUtilities.format(c, "%MMMM%"));
        month.setEditable(false);
        JButton rightArrow = GUI.createArrow(BasicArrowButton.EAST);
        rightArrow.setName("NextMonth");
        rightArrow.addActionListener(this);
        monthPanel.add(BorderLayout.WEST, leftArrow);
        monthPanel.add(BorderLayout.CENTER, month);
        monthPanel.add(BorderLayout.EAST, rightArrow);
        top.add(monthPanel);
        
        JPanel yearPanel = new JPanel();
        yearPanel.setLayout(new BorderLayout());
        leftArrow = GUI.createArrow(BasicArrowButton.WEST);
        leftArrow.setName("PreviousYear");
        leftArrow.addActionListener(this);
        year = new JTextField(6);
        year.setText(StringUtilities.format(c, "%yyyy%"));
        year.setEditable(false);
        rightArrow = GUI.createArrow(BasicArrowButton.EAST);
        rightArrow.setName("NextYear");
        rightArrow.addActionListener(this);
        yearPanel.add(BorderLayout.WEST, leftArrow);
        yearPanel.add(BorderLayout.CENTER, year);
        yearPanel.add(BorderLayout.EAST, rightArrow);
        top.add(yearPanel);
		
        JPanel bottom = new JPanel();
        JLabel label;
        TableLayout layout = new TableLayout(7);
        bottom.setLayout(layout);
        label = new JLabel("S");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(label);
        label = new JLabel("M");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(label);
        label = new JLabel("T");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(label);
        label = new JLabel("W");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(label);
        label = new JLabel("T");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(label);
        label = new JLabel("F");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(label);
        label = new JLabel("S");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(label);
        
        days = new JLabel[42];
        
        for (int i = 0; i < 42; i++) {
        	days[i] = new JLabel(" ");
        	days[i].addMouseListener(this);
        	days[i].setOpaque(false);
        	days[i].setHorizontalAlignment(SwingConstants.CENTER);
        	days[i].setForeground(getForeground());
        	//days[i].setBackground(getBackground());
        	days[i].setFont(font);
            bottom.add(days[i]);
        }
        changed(c, 0, 0, 0);
        
        add(BorderLayout.NORTH, top);
        add(BorderLayout.CENTER, bottom);
	}

	public void setHighlight(Color highlight) {
		this.highlight = highlight;
	}
	
	public void setSelected(Color selected) {
		this.selected = selected;
	}
	
	public void setForeground(Color foreground) {
		super.setForeground(foreground);
		changed(c, 0, 0, 0);
	}
	
	public void setBackground(Color background) {
		super.setBackground(background);
		changed(c, 0, 0, 0);
	}
	
	public void setMonthYearVisible(boolean visible) {
		top.setVisible(visible);
	}
	
	public boolean isMonthYearVisible() {
		return top.isVisible();
	}
	
	public Dimension getMinimumSize() {
		if (isMonthYearVisible()) {
			return new Dimension(160, 110);
		} else {
			return new Dimension(160, 96);
		}
	}
	
	public Calendar getCalendar() {
		return c;
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
		changed(c, 0, 0, 0);
	}
	
	public int getBlockSize() {
		return blockSize;
	}
	
	public void mouseClicked(MouseEvent evt) {
    }

    public void mousePressed(MouseEvent evt) {
        if (evt.getSource() instanceof JLabel) {
            JLabel label = (JLabel)evt.getSource();
            if (!label.getText().equals(" ")) {
	            label.setBackground(getBackground());
	            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(label.getText()));
            }
        }
    }

    public void mouseReleased(MouseEvent evt) {
    }

    public void mouseEntered(MouseEvent evt) {
        if (evt.getSource() instanceof JLabel) {
            JLabel label = (JLabel)evt.getSource();
            if (!label.getText().equals(" ")) {
                label.setBackground(highlight);
            }
        }
    }

    public void mouseExited(MouseEvent evt) {
        if (evt.getSource() instanceof JLabel) {
            JLabel label = (JLabel)evt.getSource();
            if (!label.getText().equals(" ")) {
                if (withinBlock(Integer.parseInt(label.getText()))) {
                	label.setBackground(selected);
                } else {
                	label.setBackground(getBackground());
                }
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
    	if (evt.getSource() instanceof BasicArrowButton) {
            BasicArrowButton button = (BasicArrowButton)evt.getSource();
            if (button.getName() == null) {
                // Ignore
            } else if (button.getName().equals("PreviousMonth")) {
                c.add(Calendar.MONTH, -1);
            } else if (button.getName().equals("NextMonth")) {
                c.add(Calendar.MONTH, 1);
            } else if (button.getName().equals("PreviousYear")) {
                c.add(Calendar.YEAR, -1);
            } else if (button.getName().equals("NextYear")) {
                c.add(Calendar.YEAR, 1);
            }
        }
	}
    
    public void addCalendarListener(CalendarChangeListener listener) {
    	c.addListener(listener);
    }
    
    public JLabel getSelected() {
    	for (int i = 0; i < days.length; i++) {
    		if (String.valueOf(c.get(Calendar.DAY_OF_MONTH)).equals(days[i].getText())) {
    			return days[i];
    		}
    	}
    	return null;
    }
    
    public void changed(MonitoredGregorianCalendar c, int type, int field, long value) {
    	if (c == null) return;
    	
    	// Update text fields
    	month.setText(StringUtilities.format(c, "%MMMM%"));
        year.setText(StringUtilities.format(c, "%yyyy%"));
    	
    	Calendar temp = new GregorianCalendar();
        temp.setTimeInMillis(c.getTimeInMillis());
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int location = 0;
        for (int i = 1; i < temp.get(Calendar.DAY_OF_WEEK); i++) {
            days[location].setText(" ");
            days[location].setForeground(getForeground());
            days[location].setBackground(getBackground());
            location++;
        }
        for (int i = 1; i <= c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            if (withinBlock(i)) {
                //days[location].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            	days[location].setBackground(selected);
            	days[location].setOpaque(true);
            } else {
                //days[location].setBorder(BorderFactory.createLineBorder(Color.WHITE));
            	days[location].setBackground(getBackground());
            	days[location].setOpaque(false);
            }
            days[location].setForeground(getForeground());
            days[location].setText(String.valueOf(i));
            location++;
        }
        
        for (; location < 42; location++) {
            days[location].setText(" ");
            days[location].setForeground(getForeground());
            //days[location].setBorder(BorderFactory.createLineBorder(Color.WHITE));
            days[location].setBackground(getBackground());
        }
	}
    
    private boolean withinBlock(int dayOfMonth) {
    	if (c.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
    		return true;
    	} else if ((blockSize > 1) && (c.get(Calendar.DAY_OF_MONTH) < dayOfMonth)) {
    		int distance = ((blockSize - 1) / 2) + ((blockSize - 1) % 2);
    		if ((c.get(Calendar.DAY_OF_MONTH) >= dayOfMonth - distance) && (c.get(Calendar.DAY_OF_MONTH) < dayOfMonth)) {
    			return true;
    		}
    	} else if ((blockSize > 1) && (c.get(Calendar.DAY_OF_MONTH) > dayOfMonth)) {
    		int distance = (blockSize - 1) / 2;
    		if ((c.get(Calendar.DAY_OF_MONTH) <= dayOfMonth + distance) && (c.get(Calendar.DAY_OF_MONTH) > dayOfMonth)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static void main(String[] args) throws Exception {
    	JFrame frame = new JFrame();
    	frame.setSize(600, 500);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	Container c = frame.getContentPane();
    	c.setLayout(new BorderLayout());
    	final JCalendar calendar = new JCalendar();
    	calendar.setBorder(BorderFactory.createEtchedBorder());
    	calendar.getCalendar().add(Calendar.MONTH, 1);
    	//calendar.setHighlight(Color.GREEN);
    	calendar.setBlockSize(3);
    	calendar.setForeground(Color.BLUE);
    	calendar.setBackground(Color.WHITE);
    	
    	/*JButton button = new JButton("Do Something");
    	button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calendar.setMonthYearVisible(!calendar.isMonthYearVisible());
			}
    	});*/
    	
    	c.add(BorderLayout.CENTER, calendar);
    	//c.add(button);
    	frame.setVisible(true);
    }
}
