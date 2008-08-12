package org.jcommon.swing;

import java.awt.*;

import javax.swing.*;

public class Scroller extends JPanel {
	public Scroller(Component component) {
		setLayout(new BorderLayout());
		JViewport viewport = new JViewport();
		viewport.setView(component);
		add(BorderLayout.CENTER, viewport);
		System.out.println("Viewport: " + viewport.getView().getSize());
	}
	
	public static void main(String[] args) throws Exception {
		JTextPane c = new JTextPane();
		c.setPage("http://rollarama.captiveimagination.com");
		Scroller scroller = new Scroller(c);
		DynamicDialog dialog = new DynamicDialog(null, "Test", new JComponent[] {scroller}, 600, 500);
		dialog.waitForAction();
		dialog.dispose();
	}
}
