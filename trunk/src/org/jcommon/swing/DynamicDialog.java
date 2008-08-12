/*
 * Created on Mar 10, 2005
 */
package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * @author Matt Hicks
 */
public class DynamicDialog implements ActionListener, WindowListener, InternalFrameListener, KeyListener {
	public static final int FRAME_TYPE = 1;
	public static final int DIALOG_TYPE = 2;
	
	public static int TYPE = DIALOG_TYPE;
	
    private static final int OK = 1;
    private static final int CANCEL = 2;
    
    protected JInternalFrame internal;
    protected Window dialog;
    //protected JLabel[] labels;
    protected JComponent[] fields;
    protected int pressed;
    
    private Component parent;
    private int width;
    private int height;
    private boolean showOK;
    private boolean showClose;
    private boolean resizable;
    protected ArrayList validators;
    protected ArrayList listeners;
    
    private ArrayList leftButtons;
    private ArrayList rightButtons;
    
    private boolean fieldActions;
    
    public static void main(String[] args) throws Exception {
        final JTextField field1 = new JTextField();
        field1.setName("Test Field");
        JCheckBox check1 = new JCheckBox("Testing 1");
        check1.setName("Options");
        //JCheckBox check2 = new JCheckBox("Testing 2");
        
        /*JFrame frame = new JFrame("Testing");
        JDesktopPane pane = new JDesktopPane();
        frame.getContentPane().add(pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);*/
        
        DynamicDialog.TYPE = DynamicDialog.FRAME_TYPE;
        DynamicDialog dialog = new DynamicDialog(null, "Test", new JComponent[] {field1}, true);
        dialog.waitForAction();
        
        /*JInternalFrame jif = new JInternalFrame("Test Internal");
        jif.setLocation(50, 50);
        jif.setResizable(true);
        Container c = jif.getContentPane();
        c.setLayout(new BorderLayout());
        DynamicPanel panel = new DynamicPanel(new JComponent[] {field1});
        c.add(BorderLayout.NORTH, panel);
        panel = new DynamicPanel(new JComponent[] {check1});
        c.add(BorderLayout.SOUTH, panel);
        jif.pack();
        jif.setVisible(true);
        jif.pack();
        pane.add(jif);*/
    }
    
    public DynamicDialog(Component parent, String title, JComponent[] fields, boolean showOK) {
        this(parent, title, fields, showOK, true);
    }
    
    public DynamicDialog(Component parent, String title, JComponent[] fields, boolean showOK, boolean showClose) {
        // Dynamically assign a window size
        int col1Width = 0;
        int col2Width = 0;
        int height = 40;
        Dimension d;
        for (int i = 0; i < fields.length; i++) {
            d = fields[i].getPreferredSize();
            //System.out.println("Field" + i + ": " + fields[i].getPreferredSize().width + ", " + fields[i].getPreferredSize().height);
            if (fields[i].getName() != null) {
                if (fields[i].getName().length() > col1Width) col1Width = fields[i].getName().length();
            }
            if (fields[i] instanceof JTextField) {
                col2Width = 100;
            }
            fields[i].addKeyListener(this);
            if (col2Width < d.width + 10) col2Width = d.width + 10;
            height += d.height + 10;
        }
        // Buttons
        height += 26;
        
        // Multiplier
        col1Width *= 18;
        if (col1Width > 0) col1Width += 10;
        
        //System.out.println("Width: " + col1Width + ", " + col2Width + ", Height: " + height);
        initialize(parent, title, fields, col1Width + col2Width, height, showOK, showClose);
        //initialize(parent, title, fields, -1, -1, showOK);
    }
    
    public DynamicDialog(Component parent, String title, JComponent[] fields, int width, int height) {
        this(parent, title, fields, width, height, true);
    }
    
    public DynamicDialog(Component parent, String title, JComponent[] fields, int width, int height, boolean showOK) {
    	for (int i = 0; i < fields.length; i++) {
            fields[i].addKeyListener(this);
        }
        initialize(parent, title, fields, width, height, showOK, true);
    }
    
    public Component getWindow() {
        if (internal != null) {
            return internal;
        }
        return dialog;
    }
    
    public void setResizable(boolean resizable) {
    	if (dialog != null) {	// Already initted
    		if (dialog instanceof JFrame) {
    			((JFrame)dialog).setResizable(false);
    		} else if (dialog instanceof JDialog) {
    			((JDialog)dialog).setResizable(false);
    		}
    	} else if (internal != null) {	// Using internal frame
    		internal.setResizable(resizable);
    	}
    	this.resizable = resizable;
    }
    
    private void initialize(Component parent, String title, JComponent[] fields, int width, int height, boolean showOK, boolean showClose) {
    	this.parent = parent;
        this.width = width;
        this.height = height;
        this.showOK = showOK;
        this.showClose = showClose;
        fieldActions = true;
        leftButtons = new ArrayList();
        rightButtons = new ArrayList();
        // TODO make JInternalFrame functional...doesn't appear for some reason
        if (parent instanceof JDesktopPane) {
            internal = new JInternalFrame(title);
            if (title == null) {
                internal.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
            }
            internal.addInternalFrameListener(this);
            internal.setResizable(resizable);
        } else {
        	if (TYPE == DIALOG_TYPE) {
	            dialog = new JDialog(getFrame(parent)) {
	                private static final long serialVersionUID = 1L;
	    
	                // TODO determine what the crap I was thinking here
//	                public void setVisible(boolean visible) {
//	                    if ((visible) || (pressed == CANCEL) || (checkValid())) {
//	                        super.setVisible(visible);
//	                    }
//	                }
	            };
	            
	            if (parent != null) ((JDialog)dialog).setModal(true);
	            ((JDialog)dialog).setTitle(title);
	            ((JDialog)dialog).setResizable(resizable);
        	} else {
        		dialog = new JFrame();
        		((JFrame)dialog).setTitle(title);
        		((JFrame)dialog).setResizable(resizable);
        	}
            dialog.addWindowListener(this);
        }
        
        this.fields = fields;
        validators = new ArrayList();
        listeners = new ArrayList();
    }
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    public void addButtonLeft(JButton button) {
        leftButtons.add(button);
    }
    
    public void addButtonRight(JButton button) {
        rightButtons.add(button);
    }
    
    public void setFieldActions(boolean enabled) {
    	this.fieldActions = enabled;
    }
    
    protected void createLayout() {
        Container c;
        if (internal != null) {
            c = internal.getContentPane();
        } else {
        	if (dialog instanceof JDialog) {
        		c = ((JDialog)dialog).getContentPane();
        	} else {
        		c = ((JFrame)dialog).getContentPane();
        	}
        }
        c.setLayout(new BorderLayout());
        
        JPanel panel1 = new DynamicPanel(fields);
        
        // FieldActions
        if (fieldActions) {
	        for (int i = 0; i < fields.length; i++) {
	        	if (fields[i] instanceof JTextField) {
	        		((JTextField)fields[i]).addActionListener(this);
	        	}
	        }
        }
        c.add(BorderLayout.CENTER, panel1);
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());
        for (int i = 0; i < leftButtons.size(); i++) {
            panel2.add((JButton)leftButtons.get(i));
        }
        if (showOK) {
	        JButton okButton = new JButton("OK");
	        okButton.setName("OK");
	        okButton.addActionListener(this);
	        JButton cancelButton = new JButton("Cancel");
	        cancelButton.setName("Cancel");
	        cancelButton.addActionListener(this);
	        panel2.add(okButton);
	        panel2.add(cancelButton);
        } else if (showClose) {
            JButton closeButton = new JButton("Close");
            closeButton.setName("Close");
            closeButton.addActionListener(this);
            panel2.add(closeButton);
        }
        for (int i = 0; i < rightButtons.size(); i++) {
            panel2.add((JButton)rightButtons.get(i));
        }
        c.add(BorderLayout.SOUTH, panel2);
        
        if ((width == -1) || (height == -1)) {
            if (internal != null) {
                //internal.getContentPane().setPreferredSize(layout.minimumLayoutSize(internal.getContentPane()));
            	internal.getContentPane().setPreferredSize(panel1.getPreferredSize());
                internal.pack();
            } else {
                //dialog.getContentPane().setPreferredSize(layout.minimumLayoutSize(dialog.getContentPane()));
            	if (dialog instanceof JDialog) {
            		((JDialog)dialog).getContentPane().setPreferredSize(panel1.getPreferredSize());
            	} else {
            		((JFrame)dialog).getContentPane().setPreferredSize(panel1.getPreferredSize());
            	}
                dialog.pack();
            }
        } else {
            if (internal != null) {
                internal.setSize(width, height);
            } else {
                dialog.setSize(width, height);
            }
        }
    }
    
    public void dispose() {
        if (internal != null) {
            internal.dispose();
        } else {
            dialog.dispose();
        }
        pressed = CANCEL;
    }
    
    private static Frame getFrame(Component c) {
        if (c == null) return null;
        Frame f = JOptionPane.getFrameForComponent(c);
        return f;
    }

    public void popup() {
        createLayout();
        
        if (internal == null) {
            GUI.center(parent, dialog);
        }
        if (internal != null) {
            internal.setVisible(true);
            internal.pack();
            GUI.center(parent, internal);
            ((JDesktopPane)parent).add(internal);
        } else {
        	// Hack to allow modal dialog to popup and return if not in graphics thread
        	String threadName = Thread.currentThread().getName();
        	if ((threadName != null) && (threadName.startsWith("AWT-EventQueue"))) {
        		System.out.println("WARNING: Showing dialog in AWT thread causes blocking, try using with EventQueue.");
        		dialog.setVisible(true);
        	} else {
	        	Thread t = new Thread() {
	        		public void run() {
	        			dialog.setVisible(true);
	        		}
	        	};
	        	t.start();
	        	while (!dialog.isVisible()) {
	        		try {
	        			Thread.sleep(50);
	        		} catch(InterruptedException exc) {
	        			exc.printStackTrace();
	        		}
	        	}
	        	GUI.center(parent, dialog);
        	}
        }
    }
    
    public boolean waitForAction() {
        popup();
        while (pressed == 0) {
            try {
                Thread.sleep(50);
            } catch(InterruptedException exc) {
                exc.printStackTrace();
            }
        }
        if (pressed == OK) {
            return true;
        } else {
            return false;
        }
    }

    public void addDynamicValidator(DynamicValidator validator) {
        validators.add(validator);
    }
    
    private boolean checkValid() {
        DynamicValidator validator;
        String message;
        for (int i = 0; i < validators.size(); i++) {
            validator = (DynamicValidator)validators.get(i);
            message = validator.getError();
            if (message != null) {
                Component c;
                if (internal != null) {
                    c = internal;
                } else {
                    c = dialog;
                }
                JOptionPane.showMessageDialog(c, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            message = validator.getWarning();
            if (message != null) {
                Component c;
                if (internal != null) {
                    c = internal;
                } else {
                    c = dialog;
                }
                if (JOptionPane.showConfirmDialog(c, message + "\n\nDo you wish to continue?", "Validation Warning", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addEmptyFieldValidation(JComponent[] fields) {
        for (int i = 0; i < fields.length; i++) {
            final JComponent f = fields[i];
            addDynamicValidator(new DynamicValidator() {
                public String getError() {
                    if (f instanceof JTextField) {
                        JTextField field = (JTextField)f;
                        if (field.getText().trim().length() == 0) {
                            return "The field '" + f.getName() + "' must have a value supplied.";
                        }
                    } else if (f instanceof JOptionField) {
                        JOptionField field = (JOptionField)f;
                        if (field.getSelectedObject() == null) {
                            return "The field '" + f.getName() + "' must have a value supplised.";
                        }
                    }
                    return null;
                }
            });
        }
    }
    
    public void actionPerformed(ActionEvent evt) {
        Component c;
        if (internal != null) {
            c = internal;
        } else {
            c = dialog;
        }
        if (evt.getSource() instanceof JButton) {
            JButton button = (JButton)evt.getSource();
	        if (button.getName().equals("OK")) {
	        	if (!pressOkay()) {
	        		return;
	        	}
	        } else if ((button.getName().equals("Cancel")) || (button.getName().equals("Close"))) {
	            pressed = CANCEL;
	            c.setVisible(false);
	        }
        } else {
	        // Means action occurred on another field - act like OK pressed
        	if (!pressOkay()) {
        		return;
        	}
        }
        for (int i = 0; i < listeners.size(); i++) {
            ((ActionListener)listeners.get(i)).actionPerformed(evt);
        }
    }
    
    public boolean pressOkay() {
    	Component c;
        if (internal != null) {
            c = internal;
        } else {
            c = dialog;
        }
    	if (checkValid()) {
    		pressed = OK;
    		c.setVisible(false);
    		return true;
    	}
    	return false;
    }
    
    public boolean isOkay() {
        if (pressed == OK) {
            return true;
        }
        return false;
    }
    
    public boolean isCancel() {
        if (pressed == CANCEL) {
            return true;
        }
        return false;
    }
    
    public void windowOpened(WindowEvent evt) {
    }

    public void windowClosing(WindowEvent evt) {
        pressed = CANCEL;
    }

    public void windowClosed(WindowEvent evt) {
    }

    public void windowIconified(WindowEvent evt) {
    }

    public void windowDeiconified(WindowEvent evt) {
    }

    public void windowActivated(WindowEvent evt) {
    }

    public void windowDeactivated(WindowEvent evt) {
        //dialog.toFront();
    }

    public void internalFrameOpened(InternalFrameEvent e) {
    }
    
    public void internalFrameClosing(InternalFrameEvent e) {
        pressed = CANCEL;
    }

    public void internalFrameClosed(InternalFrameEvent e) {
    }

    public void internalFrameIconified(InternalFrameEvent e) {
    }

    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    public void internalFrameActivated(InternalFrameEvent e) {
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
    
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            dispose();
        }
    }
}
