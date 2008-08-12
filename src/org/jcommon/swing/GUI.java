/*
 * Created on Mar 9, 2005
 */
package org.jcommon.swing;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;

/**
 * @author Matt Hicks
 */
public class GUI {
	public static final int NORTH = BasicArrowButton.NORTH;
	public static final int SOUTH = BasicArrowButton.SOUTH;
	public static final int EAST = BasicArrowButton.EAST;
	public static final int WEST = BasicArrowButton.WEST;

	private static Color gray = new Color(238, 238, 238);

	/**
	 * Centers the window on the parent. If parent is null the
	 * window will be centered on the screen size.
	 * 
	 * @param parent
	 * @param window
	 */
	public static void center(Component parent, Component window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = window.getSize();
		double centerWidth = screenSize.width / 2d;
		double centerHeight = screenSize.height / 2d;

		Component parentWindow = null;
		Dimension size = null;
		Point point = null;
		if (parent instanceof Dialog) {
			parentWindow = (Dialog) parent;
			size = parentWindow.getSize();
			point = parentWindow.getLocation();
		} else if (parent instanceof Frame) {
			parentWindow = (Frame) parent;
			size = parentWindow.getSize();
			point = parentWindow.getLocation();
		} else if (parent != null) {
			parentWindow = JOptionPane.getFrameForComponent(parent);
			size = parentWindow.getSize();
			point = parentWindow.getLocation();
		}
		if (parentWindow != null) {
			centerWidth = (size.width / 2) + point.x;
			centerHeight = (size.height / 2) + point.y;
		}
		window.setLocation((int) centerWidth - (windowSize.width / 2), (int) centerHeight - (windowSize.height / 2));
	}

	/**
	 * Method to attempt a dynamic update for any GUI accessible by this JVM. It will
	 * filter through all frames and sub-components of the frames.
	 */
	public static void updateAllUIs() {
		Frame frames[];
		frames = Frame.getFrames();

		for (int i = 0; i < frames.length; i++) {
			updateWindowUI(frames[i]);
		}
	}

	/**
	 * Method to attempt a dynamic update for all components of the given <code>Window</code>.
	 * @param window The <code>Window</code> for which the look and feel update has to be performed against.
	 */
	public static void updateWindowUI(Window window) {
		try {
			updateComponentTreeUI(window);
		} catch (Exception exception) {
		}

		Window windows[] = window.getOwnedWindows();

		for (int i = 0; i < windows.length; i++)
			updateWindowUI(windows[i]);
	}

	/**
	 * A simple minded look and feel change: ask each node in the tree
	 * to <code>updateUI()</code> -- that is, to initialize its UI property
	 * with the current look and feel.
	 *
	 * Based on the Sun SwingUtilities.updateComponentTreeUI, but ensures that
	 * the update happens on the components of a JToolbar before the JToolbar
	 * itself.
	 */
	public static void updateComponentTreeUI(Component c) {
		updateComponentTreeUI0(c);
		c.invalidate();
		c.validate();
		c.repaint();
	}

	private static void updateComponentTreeUI0(Component c) {
		Component[] children = null;
		if (c instanceof JToolBar) {
			children = ((JToolBar) c).getComponents();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					updateComponentTreeUI0(children[i]);
				}
			}

			((JComponent) c).updateUI();
		} else {
			if (c instanceof JComponent) {
				((JComponent) c).updateUI();
			}

			if (c instanceof JMenu) {
				children = ((JMenu) c).getMenuComponents();
			} else if (c instanceof Container) {
				children = ((Container) c).getComponents();
			}

			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					updateComponentTreeUI0(children[i]);
				}
			}
		}
	}

	/**
	 * @param direction
	 * @return
	 * 		A JButton with an arrow pointing the direction specified:
	 * 			NORTH, SOUTH, EAST, or WEST
	 */
	public static JButton createArrow(int direction) {
		BasicArrowButton button = new BasicArrowButton(direction, gray, Color.LIGHT_GRAY, Color.BLACK, Color.LIGHT_GRAY);
		button.setBorder(BorderFactory.createEtchedBorder());
		return button;
	}

	/**
	 * Tiles <code>image</code> from x, y starting point
	 * to the width and height defined.
	 * 
	 * @param image
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void tileImage(Image image, Graphics g, int x, int y, int width, int height) {
		int posX = x;
		int posY = y;
		while (posY < height + y) {
			int imgWidth = image.getWidth(null);
			int imgHeight = image.getHeight(null);
			if (posX > width + x) {
				posX = x;
				posY += imgHeight;
			}
			if (posX + imgWidth > x + width) {
				imgWidth = (x + width) - posX;
			}
			if (posY + imgHeight > y + height) {
				imgHeight = (y + height) - posY;
			}
			g.drawImage(image, posX, posY, posX + imgWidth, posY + imgHeight, 0, 0, imgWidth, imgHeight, null);
			posX += image.getWidth(null);
		}
	}
}
