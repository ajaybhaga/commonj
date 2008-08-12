package org.jcommon.swing.layout;

import java.awt.*;

import javax.swing.*;

/**
 * @author Matt Hicks
 *
 */
public class TablePanel extends JPanel {
	private TableLayout layout;
	
	public TablePanel(int columnCount) {
		layout = new TableLayout(columnCount);
		setLayout(layout);
	}
	
	public TableLayout getTableLayout() {
		return layout;
	}
	
	/*public Dimension getSize() {
		return getParent().getSize();
	}*/
	
	public Dimension getPreferredSize() {
		return getParent().getSize();
	}
	
	protected void paintBorder(java.awt.Graphics g) {
		super.paintBorder(g);
	}
	
	protected void paintChildren(java.awt.Graphics g) {
		layout.paintBackground(this, g);
		super.paintChildren(g);
		layout.paintBorders(this, g);
	}
}
