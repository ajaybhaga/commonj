package org.jcommon.swing;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;

import org.jcommon.util.ArrayUtilities;

public class JComboEditor extends JPanel implements ActionListener, FocusListener, KeyListener, MouseListener {
    private static Color gray = new Color(238, 238, 238);
    
	protected int selected;
	protected List c;
	protected JTextField editor;
	protected JButton arrow;
	protected JPopupMenu menu;
	protected boolean visible;
	
	protected boolean editable;
	protected boolean validateExtended;
	protected boolean acceptCharacters;
	protected boolean acceptNumbers;
	protected int limit;
	
	protected HashSet listeners;
	
	public JComboEditor() {
		this((List)null, true);
	}
	
	public JComboEditor(Object[] c, boolean editable) {
	    this(ArrayUtilities.toList(c), editable);
	}
	
	public JComboEditor(List c, boolean editable) {
		super();
		this.editable = editable;
		listeners = new HashSet();
		editor = new JTextField();
		editor.setBorder(BorderFactory.createEtchedBorder());
		if (!editable) {
		    editor.setEditable(false);
		}
		setValues(c);
		editor.addKeyListener(this);
		editor.addFocusListener(this);
		editor.addMouseListener(this);
		arrow = createArrow(BasicArrowButton.SOUTH);
		arrow.addActionListener(this);
		setSelected(0);
		menu = new JPopupMenu();
		
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, editor);
		add(BorderLayout.EAST, arrow);
		
		visible = false;
		
		validateExtended = true;
		acceptCharacters = true;
		acceptNumbers = true;
		limit = -1;
	}
	
	public Collection getValues() {
		validateEditor();
		return c;
	}
	
	public void setValues(List c) {
		if ((c != null) && (c.size() > 0)) {
			this.c = c;
		} else {
			this.c = JComboEditor.getEmptyList();
		}
		setSelected(0);
	}
	
	public int getSelected() {
		return selected;
	}
	
	public Object getSelectedValue() {
	    return c.get(selected);
	}
	
	public JTextField getEditor() {
		return editor;
	}
	
	public void setEditor(JTextField editor) {
		this.editor = editor;
	}
	
	public JButton getButton() {
		return arrow;
	}
	
	public void setButton(JButton button) {
		arrow = button;
	}
	
	public JPopupMenu getPopupMenu() {
		return menu;
	}
	
	public void setPopupMenu(JPopupMenu menu) {
		this.menu = menu;
	}
	
	protected void populatePopup() {
		menu.removeAll();
		
		Iterator i = c.iterator();
		int s = 0;
		String text;
		JMenuItem item;
		while (i.hasNext()) {
			text = i.next().toString();
			if (s != selected) {
				item = new JMenuItem(text);
				item.setName(String.valueOf(s));
				item.addActionListener(this);
				menu.add(item);
			}
			s++;
		}
	}
	
	public void popup() {
		populatePopup();
		menu.setPopupSize(this.getWidth(), (c.size() - 1) * 20);
		menu.show(this, 0, this.getHeight());
		menu.repaint();
	}
	
	public void unpopup() {
		menu.setVisible(false);
	}
	
	public void toggle() {
		if (visible) {
			unpopup();
			visible = false;
		} else {
			popup();
			visible = true;
		}
	}
	
	public void setSelected(int selected) {
		this.selected = selected;
		Object[] o = c.toArray();
		if ((o.length > selected) && (selected > -1)) {
			editor.setText(o[selected].toString());
		} else if (selected < 0) {
			setSelected(o.length - 1);
		} else if (o.length > 0) {
			setSelected(0);
		}
	}
	
	public void setSelected(Object o) {
	    for (int i = 0; i < c.size(); i++) {
	        if (c.get(i).equals(o)) {
	            setSelected(i);
	            return;
	        }
	    }
	}
	
	public void next() {
		if (c.size() > 0) {
			setSelected(selected + 1);
		}
	}
	
	public void previous() {
		if (c.size() > 0) {
			setSelected(selected - 1);
		}
	}
	
	protected void validateEditor() {
		if (c.size() > 0) {
			ArrayList list = new ArrayList(c);
			if (selected < list.size()) {
				if ((!list.get(selected).toString().equals(editor.getText())) || (editor.getText().trim().length() <= 0)) {
					list.remove(selected);
					if (editor.getText().trim().length() > 0) {
						list.add(selected, editor.getText());
					} else if (list.size() == 0) {
						list = new ArrayList(JComboEditor.getEmptyList());
					}
					c = list;
				}
			}
		}
	}
	
	protected int eventID = 0;
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof BasicArrowButton) {
			if (c.size() > 0) {
				toggle();
			}
		} else {
			JMenuItem item = (JMenuItem)e.getSource();
			validateEditor();
			setSelected(Integer.parseInt(item.getName()));
			visible = false;
			Iterator iterator = listeners.iterator();
			while (iterator.hasNext()) {
			    ((ActionListener)iterator.next()).actionPerformed(new ActionEvent(editor, eventID++, "valueChanged"));
			}
		}
	}
	
	public void keyTyped(KeyEvent e) {
		if ((!Character.isISOControl(e.getKeyChar())) && (limit > -1) && (editor.getText().length() >= limit)) {
			System.out.println("Consuming!");
			e.consume();
		} else {
			if (!acceptCharacters) {
				if ((!Character.isDigit(e.getKeyChar())) && (!Character.isISOControl(e.getKeyChar()))) {
					e.consume();
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if (validateExtended) {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				validateEditor();
				previous();
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				validateEditor();
				next();
			} else if (e.getKeyCode() == KeyEvent.VK_F6) {
				ArrayList list = new ArrayList(getValues());
				for (int i = 0; i < list.size(); i++) {
					System.out.println("Value:" + list.get(i));
				}
			} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				if (!acceptCharacters) {
					if (!((JTextField)e.getSource()).getText().trim().equals("")) {
						c.add("");
						validateEditor();
						setSelected(c.size() - 1);
					}
				}
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if (validateExtended) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!((JTextField)e.getSource()).getText().trim().equals("")) {
					c.add("");
					validateEditor();
					setSelected(c.size() - 1);
				}
			}
		}
	}
	
	public void focusLost(FocusEvent e) {
	}
	
	public void focusGained(FocusEvent e) {
		visible = false;
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
		visible = false;
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	/** Extended Features
	*	sets UP and DOWN arrows to cycle through options,
	*	ENTER creates a new entry
	*/
	public void extendedFeatures(boolean validateExtended) {
		this.validateExtended = validateExtended;
	}
	
	public void acceptCharacters(boolean acceptCharacters) {
		this.acceptCharacters = acceptCharacters;
	}
	
	public void acceptNumbers(boolean acceptNumbers) {
		this.acceptNumbers = acceptNumbers;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public void addActionListener(ActionListener l) {
	    listeners.add(l);
	}
	
	protected static final List getEmptyList() {
		List collection = new ArrayList();
		collection.add("");
		return collection;
	}

	private static JButton createArrow(int direction) {
        BasicArrowButton button = new BasicArrowButton(direction, gray, Color.LIGHT_GRAY, Color.BLACK, Color.LIGHT_GRAY);
        button.setBorder(BorderFactory.createEtchedBorder());
        return button;
    }
}
