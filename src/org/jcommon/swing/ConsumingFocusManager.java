/*
 * Created on May 4, 2004
 */
package org.jcommon.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author matthew_hicks
 */
public class ConsumingFocusManager extends FocusManager {
	public void processKeyEvent(Component c, KeyEvent e) {
		e.consume();
	}
}
