package org.jcommon.swing;

import java.awt.*;

import javax.swing.*;

import org.jcommon.swing.layout.*;

/**
 * @author Matt Hicks
 *
 */
public class DynamicPanel extends JPanel {
	protected JLabel[] labels;
	private JComponent[] fields;
	
	private TableLayout layout;
	
	public DynamicPanel(JComponent[] fields) {
		this.fields = fields;
		
		createLayout();
	}
	
	private void createLayout() {
		int columnCount = 1;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName() != null) {
                columnCount = 2;
                break;
            }
        }
        
        layout = new TableLayout(columnCount);
        layout.setDefaultCellMargin(new Insets(5, 5, 5, 5));
        setLayout(layout);
        labels = new JLabel[fields.length];
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName() != null) {
	            labels[i] = new JLabel(fields[i].getName() + ":");
	            labels[i].setHorizontalAlignment(SwingConstants.RIGHT);
	            labels[i].setVerticalAlignment(SwingConstants.TOP);
	            add(labels[i]);
	            layout.getCell(labels[i]).setHorizontalAlignment(TableLayout.ALIGN_RIGHT);
            } else if (columnCount == 2) {
                layout.getCell(fields[i]).setColumnSpan(2);
            }
            if (fields[i] instanceof JTextField) {
                if (((JTextField)fields[i]).getColumns() == 0) ((JTextField)fields[i]).setColumns(10);
                //layout.disableVerticalRowStretching(i);
            } else if ((fields[i] instanceof JMenuBar) || (fields[i] instanceof JSeparator)) {
            	//layout.disableVerticalRowStretching(i);
            }
            add(fields[i]);
        }
	}
	
	public TableLayout getTableLayout() {
		return layout;
	}
}
