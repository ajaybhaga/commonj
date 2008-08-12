package org.jcommon.swing;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class EventQueue implements ActionListener,
								   AdjustmentListener,
								   AncestorListener,
								   CaretListener,
								   CellEditorListener,
								   ChangeListener,
								   ComponentListener,
								   ContainerListener,
								   DragGestureListener,
								   DragSourceListener,
								   DragSourceMotionListener,
								   DropTargetListener,
								   FocusListener,
								   HierarchyListener,
								   HyperlinkListener,
								   InternalFrameListener,
								   ItemListener,
								   KeyListener,
								   ListDataListener,
								   ListSelectionListener,
								   MenuDragMouseListener,
								   MenuKeyListener,
								   MenuListener,
								   MouseInputListener,
								   MouseListener,
								   MouseMotionListener,
								   MouseWheelListener,
								   PopupMenuListener,
								   PropertyChangeListener,
								   TableColumnModelListener,
								   TableModelListener,
								   TextListener,
								   TreeExpansionListener,
								   TreeModelListener,
								   TreeSelectionListener,
								   TreeWillExpandListener,
								   UndoableEditListener,
								   WindowFocusListener,
								   WindowListener,
								   WindowStateListener {
	public static final int ACTION_PERFORMED = 1;

	public static final int ADJUSTMENT_VALUE_CHANGED = 2;

	public static final int ANCESTOR_ADDED = 3;

	public static final int ANCESTOR_REMOVED = 4;

	public static final int ANCESTOR_MOVED = 5;

	public static final int CARET_UPDATE = 6;

	public static final int EDITING_STOPPED = 7;

	public static final int EDITING_CANCELED = 8;

	public static final int STATE_CHANGED = 9;

	public static final int COMPONENT_RESIZED = 10;

	public static final int COMPONENT_MOVED = 11;

	public static final int COMPONENT_SHOWN = 12;

	public static final int COMPONENT_HIDDEN = 13;

	public static final int COMPONENT_ADDED = 14;

	public static final int COMPONENT_REMOVED = 15;

	public static final int DRAG_GESTURE_RECOGNIZED = 16;

	public static final int DRAG_ENTER = 17;

	public static final int DRAG_OVER = 18;

	public static final int DROP_ACTION_CHANGED = 19;

	public static final int DRAG_EXIT = 20;

	public static final int DRAG_DROP_END = 21;

	public static final int DRAG_MOUSE_MOVED = 22;

	public static final int DROP = 23;

	public static final int FOCUS_GAINED = 24;

	public static final int FOCUS_LOST = 25;

	public static final int HIERARCHY_CHANGED = 26;

	public static final int HYPERLINK_UPDATE = 27;

	public static final int INTERNAL_FRAME_OPENED = 28;

	public static final int INTERNAL_FRAME_CLOSING = 29;

	public static final int INTERNAL_FRAME_CLOSED = 30;

	public static final int INTERNAL_FRAME_ICONIFIED = 31;

	public static final int INTERNAL_FRAME_DEICONIFIED = 32;

	public static final int INTERNAL_FRAME_ACTIVATED = 33;

	public static final int INTERNAL_FRAME_DEACTIVATED = 34;

	public static final int ITEM_STATE_CHANGED = 35;

	public static final int KEY_TYPED = 36;

	public static final int KEY_PRESSED = 37;

	public static final int KEY_RELEASED = 38;

	public static final int INTERVAL_ADDED = 39;

	public static final int INTERVAL_REMOVED = 40;

	public static final int CONTENTS_CHANGED = 41;

	public static final int VALUE_CHANGED = 42;

	public static final int MENU_DRAG_MOUSE_ENTERED = 43;

	public static final int MENU_DRAG_MOUSE_EXITED = 44;

	public static final int MENU_DRAG_MOUSE_DRAGGED = 45;

	public static final int MENU_DRAG_MOUSE_RELEASED = 46;

	public static final int MENU_KEY_TYPED = 47;

	public static final int MENU_KEY_PRESSED = 48;

	public static final int MENU_KEY_RELEASED = 49;

	public static final int MENU_SELECTED = 50;

	public static final int MENU_DESELECTED = 51;

	public static final int MENU_CANCELED = 52;

	public static final int MOUSE_CLICKED = 53;

	public static final int MOUSE_PRESSED = 54;

	public static final int MOUSE_RELEASED = 55;

	public static final int MOUSE_ENTERED = 56;

	public static final int MOUSE_EXITED = 57;

	public static final int MOUSE_DRAGGED = 58;

	public static final int MOUSE_MOVED = 59;

	public static final int MOUSE_WHEEL_MOVED = 60;

	public static final int POPUP_MENU_WILL_BECOME_VISIBLE = 61;

	public static final int POPUP_MENU_WILL_BECOME_INVISIBLE = 62;

	public static final int POPUP_MENU_CANCELED = 63;

	public static final int PROPERTY_CHANGE = 64;

	public static final int COLUMN_ADDED = 65;

	public static final int COLUMN_REMOVED = 66;

	public static final int COLUMN_MOVED = 67;

	public static final int COLUMN_MARGIN_CHANGED = 68;

	public static final int COLUMN_SELECTION_CHANGED = 69;

	public static final int TABLE_CHANGED = 70;

	public static final int TEXT_VALUE_CHANGED = 71;

	public static final int TREE_EXPANDED = 72;

	public static final int TREE_COLLAPSED = 73;

	public static final int TREE_NODES_CHANGED = 74;

	public static final int TREE_NODES_INSERTED = 75;

	public static final int TREE_NODES_REMOVED = 76;

	public static final int TREE_STRUCTURE_CHANGED = 77;

	public static final int TREE_WILL_EXPAND = 79;

	public static final int TREE_WILL_COLLAPSE = 78;

	public static final int UNDOABLE_EDIT_HAPPENED = 80;

	public static final int WINDOW_GAINED_FOCUS = 81;

	public static final int WINDOW_LOST_FOCUS = 82;

	public static final int WINDOW_OPENED = 83;

	public static final int WINDOW_CLOSING = 84;

	public static final int WINDOW_CLOSED = 85;

	public static final int WINDOW_ICONIFIED = 86;

	public static final int WINDOW_DEICONIFIED = 87;

	public static final int WINDOW_ACTIVATED = 88;

	public static final int WINDOW_DEACTIVATED = 89;

	public static final int WINDOW_STATE_CHANGED = 90;

	private EventThread thread;

	private ArrayList actionListeners;

	private ArrayList adjustmentListeners;

	private ArrayList ancestorListeners;

	private ArrayList caretListeners;

	private ArrayList cellEditorListeners;

	private ArrayList changeListeners;

	private ArrayList componentListeners;

	private ArrayList containerListeners;

	private ArrayList dragGestureListeners;

	private ArrayList dragSourceListeners;

	private ArrayList dragSourceMotionListeners;

	private ArrayList dropTargetListeners;

	private ArrayList focusListeners;

	private ArrayList hierarchyListeners;

	private ArrayList hyperlinkListeners;

	private ArrayList internalFrameListeners;

	private ArrayList itemListeners;

	private ArrayList keyListeners;

	private ArrayList listDataListeners;

	private ArrayList listSelectionListeners;

	private ArrayList menuDragMouseListeners;

	private ArrayList menuKeyListeners;

	private ArrayList menuListeners;

	private ArrayList mouseInputListeners;

	private ArrayList mouseListeners;

	private ArrayList mouseMotionListeners;

	private ArrayList mouseWheelListeners;

	private ArrayList popupMenuListeners;

	private ArrayList propertyChangeListeners;

	private ArrayList tableColumnModelListeners;

	private ArrayList tableModelListeners;

	private ArrayList textListeners;

	private ArrayList treeExpansionListeners;

	private ArrayList treeModelListeners;

	private ArrayList treeSelectionListeners;

	private ArrayList treeWillExpandListeners;

	private ArrayList undoableEditListeners;

	private ArrayList windowFocusListeners;

	private ArrayList windowListeners;

	private ArrayList windowStateListeners;

	public EventQueue() {
		thread = new EventThread();
		thread.start();
		init();
	}

	public EventQueue(EventThread thread) {
		this.thread = thread;
		init();
	}

	private void init() {
		actionListeners = new ArrayList();
		adjustmentListeners = new ArrayList();
		ancestorListeners = new ArrayList();
		caretListeners = new ArrayList();
		cellEditorListeners = new ArrayList();
		changeListeners = new ArrayList();
		componentListeners = new ArrayList();
		containerListeners = new ArrayList();
		dragGestureListeners = new ArrayList();
		dragSourceListeners = new ArrayList();
		dragSourceMotionListeners = new ArrayList();
		dropTargetListeners = new ArrayList();
		focusListeners = new ArrayList();
		hierarchyListeners = new ArrayList();
		hyperlinkListeners = new ArrayList();
		internalFrameListeners = new ArrayList();
		itemListeners = new ArrayList();
		keyListeners = new ArrayList();
		listDataListeners = new ArrayList();
		listSelectionListeners = new ArrayList();
		menuDragMouseListeners = new ArrayList();
		menuKeyListeners = new ArrayList();
		menuListeners = new ArrayList();
		mouseInputListeners = new ArrayList();
		mouseListeners = new ArrayList();
		mouseMotionListeners = new ArrayList();
		mouseWheelListeners = new ArrayList();
		popupMenuListeners = new ArrayList();
		propertyChangeListeners = new ArrayList();
		tableColumnModelListeners = new ArrayList();
		tableModelListeners = new ArrayList();
		textListeners = new ArrayList();
		treeExpansionListeners = new ArrayList();
		treeModelListeners = new ArrayList();
		treeSelectionListeners = new ArrayList();
		treeWillExpandListeners = new ArrayList();
		undoableEditListeners = new ArrayList();
		windowFocusListeners = new ArrayList();
		windowListeners = new ArrayList();
		windowStateListeners = new ArrayList();
	}

	public EventThread getEventThread() {
		return thread;
	}

	// Actions
	public void actionPerformed(ActionEvent e) {
		thread.enqueue(e, this, ACTION_PERFORMED);
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		thread.enqueue(e, this, ADJUSTMENT_VALUE_CHANGED);
	}

	public void ancestorAdded(AncestorEvent event) {
		thread.enqueue(event, this, ANCESTOR_ADDED);
	}

	public void ancestorRemoved(AncestorEvent event) {
		thread.enqueue(event, this, ANCESTOR_REMOVED);
	}

	public void ancestorMoved(AncestorEvent event) {
		thread.enqueue(event, this, ANCESTOR_MOVED);
	}

	public void caretUpdate(CaretEvent e) {
		thread.enqueue(e, this, CARET_UPDATE);
	}

	public void editingStopped(ChangeEvent e) {
		thread.enqueue(e, this, EDITING_STOPPED);
	}

	public void editingCanceled(ChangeEvent e) {
		thread.enqueue(e, this, EDITING_CANCELED);
	}

	public void stateChanged(ChangeEvent e) {
		thread.enqueue(e, this, STATE_CHANGED);
	}

	public void componentResized(ComponentEvent e) {
		thread.enqueue(e, this, COMPONENT_RESIZED);
	}

	public void componentMoved(ComponentEvent e) {
		thread.enqueue(e, this, COMPONENT_MOVED);
	}

	public void componentShown(ComponentEvent e) {
		thread.enqueue(e, this, COMPONENT_SHOWN);
	}

	public void componentHidden(ComponentEvent e) {
		thread.enqueue(e, this, COMPONENT_HIDDEN);
	}

	public void componentAdded(ContainerEvent e) {
		thread.enqueue(e, this, COMPONENT_ADDED);
	}

	public void componentRemoved(ContainerEvent e) {
		thread.enqueue(e, this, COMPONENT_REMOVED);
	}

	public void dragGestureRecognized(DragGestureEvent dge) {
		thread.enqueue(dge, this, DRAG_GESTURE_RECOGNIZED);
	}

	public void dragEnter(DragSourceDragEvent dsde) {
		thread.enqueue(dsde, this, DRAG_ENTER);
	}

	public void dragOver(DragSourceDragEvent dsde) {
		thread.enqueue(dsde, this, DRAG_OVER);
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
		thread.enqueue(dsde, this, DROP_ACTION_CHANGED);
	}

	public void dragExit(DragSourceEvent dse) {
		thread.enqueue(dse, this, DRAG_EXIT);
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		thread.enqueue(dsde, this, DRAG_DROP_END);
	}

	public void dragMouseMoved(DragSourceDragEvent dsde) {
		thread.enqueue(dsde, this, DRAG_MOUSE_MOVED);
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		thread.enqueue(dtde, this, DRAG_ENTER);
	}

	public void dragOver(DropTargetDragEvent dtde) {
		thread.enqueue(dtde, this, DRAG_OVER);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		thread.enqueue(dtde, this, DROP_ACTION_CHANGED);
	}

	public void dragExit(DropTargetEvent dte) {
		thread.enqueue(dte, this, DRAG_EXIT);
	}

	public void drop(DropTargetDropEvent dtde) {
		thread.enqueue(dtde, this, DROP);
	}

	public void focusGained(FocusEvent e) {
		thread.enqueue(e, this, FOCUS_GAINED);
	}

	public void focusLost(FocusEvent e) {
		thread.enqueue(e, this, FOCUS_LOST);
	}

	public void hierarchyChanged(HierarchyEvent e) {
		thread.enqueue(e, this, HIERARCHY_CHANGED);
	}

	public void hyperlinkUpdate(HyperlinkEvent e) {
		thread.enqueue(e, this, HYPERLINK_UPDATE);
	}

	public void internalFrameOpened(InternalFrameEvent e) {
		thread.enqueue(e, this, INTERNAL_FRAME_OPENED);
	}

	public void internalFrameClosing(InternalFrameEvent e) {
		thread.enqueue(e, this, INTERNAL_FRAME_CLOSING);
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		thread.enqueue(e, this, INTERNAL_FRAME_CLOSED);
	}

	public void internalFrameIconified(InternalFrameEvent e) {
		thread.enqueue(e, this, INTERNAL_FRAME_ICONIFIED);
	}

	public void internalFrameDeiconified(InternalFrameEvent e) {
		thread.enqueue(e, this, INTERNAL_FRAME_DEICONIFIED);
	}

	public void internalFrameActivated(InternalFrameEvent e) {
		thread.enqueue(e, this, INTERNAL_FRAME_ACTIVATED);
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
		thread.enqueue(e, this, INTERNAL_FRAME_DEACTIVATED);
	}

	public void itemStateChanged(ItemEvent e) {
		thread.enqueue(e, this, ITEM_STATE_CHANGED);
	}

	public void keyTyped(KeyEvent e) {
		thread.enqueue(e, this, KEY_TYPED);
	}

	public void keyPressed(KeyEvent e) {
		thread.enqueue(e, this, KEY_PRESSED);
	}

	public void keyReleased(KeyEvent e) {
		thread.enqueue(e, this, KEY_RELEASED);
	}

	public void intervalAdded(ListDataEvent e) {
		thread.enqueue(e, this, INTERVAL_ADDED);
	}

	public void intervalRemoved(ListDataEvent e) {
		thread.enqueue(e, this, INTERVAL_REMOVED);
	}

	public void contentsChanged(ListDataEvent e) {
		thread.enqueue(e, this, CONTENTS_CHANGED);
	}

	public void valueChanged(ListSelectionEvent e) {
		thread.enqueue(e, this, VALUE_CHANGED);
	}

	public void menuDragMouseEntered(MenuDragMouseEvent e) {
		thread.enqueue(e, this, MENU_DRAG_MOUSE_ENTERED);
	}

	public void menuDragMouseExited(MenuDragMouseEvent e) {
		thread.enqueue(e, this, MENU_DRAG_MOUSE_EXITED);
	}

	public void menuDragMouseDragged(MenuDragMouseEvent e) {
		thread.enqueue(e, this, MENU_DRAG_MOUSE_DRAGGED);
	}

	public void menuDragMouseReleased(MenuDragMouseEvent e) {
		thread.enqueue(e, this, MENU_DRAG_MOUSE_RELEASED);
	}

	public void menuKeyTyped(MenuKeyEvent e) {
		thread.enqueue(e, this, MENU_KEY_TYPED);
	}

	public void menuKeyPressed(MenuKeyEvent e) {
		thread.enqueue(e, this, MENU_KEY_PRESSED);
	}

	public void menuKeyReleased(MenuKeyEvent e) {
		thread.enqueue(e, this, MENU_KEY_RELEASED);
	}

	public void menuSelected(MenuEvent e) {
		thread.enqueue(e, this, MENU_SELECTED);
	}

	public void menuDeselected(MenuEvent e) {
		thread.enqueue(e, this, MENU_DESELECTED);
	}

	public void menuCanceled(MenuEvent e) {
		thread.enqueue(e, this, MENU_CANCELED);
	}

	public void mouseClicked(MouseEvent e) {
		thread.enqueue(e, this, MOUSE_CLICKED);
	}

	public void mousePressed(MouseEvent e) {
		thread.enqueue(e, this, MOUSE_PRESSED);
	}

	public void mouseReleased(MouseEvent e) {
		thread.enqueue(e, this, MOUSE_RELEASED);
	}

	public void mouseEntered(MouseEvent e) {
		thread.enqueue(e, this, MOUSE_ENTERED);
	}

	public void mouseExited(MouseEvent e) {
		thread.enqueue(e, this, MOUSE_EXITED);
	}

	public void mouseDragged(MouseEvent e) {
		thread.enqueue(e, this, MOUSE_DRAGGED);
	}

	public void mouseMoved(MouseEvent e) {
		thread.enqueue(e, this, MOUSE_MOVED);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		thread.enqueue(e, this, MOUSE_WHEEL_MOVED);
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		thread.enqueue(e, this, POPUP_MENU_WILL_BECOME_VISIBLE);
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		thread.enqueue(e, this, POPUP_MENU_WILL_BECOME_INVISIBLE);
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
		thread.enqueue(e, this, POPUP_MENU_CANCELED);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		thread.enqueue(evt, this, PROPERTY_CHANGE);
	}

	public void columnAdded(TableColumnModelEvent e) {
		thread.enqueue(e, this, COLUMN_ADDED);
	}

	public void columnRemoved(TableColumnModelEvent e) {
		thread.enqueue(e, this, COLUMN_REMOVED);
	}

	public void columnMoved(TableColumnModelEvent e) {
		thread.enqueue(e, this, COLUMN_MOVED);
	}

	public void columnMarginChanged(ChangeEvent e) {
		thread.enqueue(e, this, COLUMN_MARGIN_CHANGED);
	}

	public void columnSelectionChanged(ListSelectionEvent e) {
		thread.enqueue(e, this, COLUMN_SELECTION_CHANGED);
	}

	public void tableChanged(TableModelEvent e) {
		thread.enqueue(e, this, TABLE_CHANGED);
	}

	public void textValueChanged(TextEvent e) {
		thread.enqueue(e, this, TEXT_VALUE_CHANGED);
	}

	public void treeExpanded(TreeExpansionEvent event) {
		thread.enqueue(event, this, TREE_EXPANDED);
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		thread.enqueue(event, this, TREE_COLLAPSED);
	}

	public void treeNodesChanged(TreeModelEvent e) {
		thread.enqueue(e, this, TREE_NODES_CHANGED);
	}

	public void treeNodesInserted(TreeModelEvent e) {
		thread.enqueue(e, this, TREE_NODES_INSERTED);
	}

	public void treeNodesRemoved(TreeModelEvent e) {
		thread.enqueue(e, this, TREE_NODES_REMOVED);
	}

	public void treeStructureChanged(TreeModelEvent e) {
		thread.enqueue(e, this, TREE_STRUCTURE_CHANGED);
	}

	public void valueChanged(TreeSelectionEvent e) {
		thread.enqueue(e, this, VALUE_CHANGED);
	}

	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		thread.enqueue(event, this, TREE_WILL_EXPAND);
	}

	public void treeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		thread.enqueue(event, this, TREE_WILL_COLLAPSE);
	}

	public void undoableEditHappened(UndoableEditEvent e) {
		thread.enqueue(e, this, UNDOABLE_EDIT_HAPPENED);
	}

	public void windowGainedFocus(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_GAINED_FOCUS);
	}

	public void windowLostFocus(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_LOST_FOCUS);
	}

	public void windowOpened(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_OPENED);
	}

	public void windowClosing(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_CLOSING);
	}

	public void windowClosed(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_CLOSED);
	}

	public void windowIconified(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_ICONIFIED);
	}

	public void windowDeiconified(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_DEICONIFIED);
	}

	public void windowActivated(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_ACTIVATED);
	}

	public void windowDeactivated(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_DEACTIVATED);
	}

	public void windowStateChanged(WindowEvent e) {
		thread.enqueue(e, this, WINDOW_STATE_CHANGED);
	}

	// Add listeners
	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	public void addAdjustmentListener(AdjustmentListener listener) {
		adjustmentListeners.add(listener);
	}

	public void addAncestorListener(AncestorListener listener) {
		ancestorListeners.add(listener);
	}

	public void addCaretListener(CaretListener listener) {
		caretListeners.add(listener);
	}

	public void addCellEditorListener(CellEditorListener listener) {
		cellEditorListeners.add(listener);
	}

	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	public void addComponentListener(ComponentListener listener) {
		componentListeners.add(listener);
	}

	public void addContainerListener(ContainerListener listener) {
		containerListeners.add(listener);
	}

	public void addDragGestureListener(DragGestureListener listener) {
		dragGestureListeners.add(listener);
	}

	public void addDragSourceListener(DragSourceListener listener) {
		dragSourceListeners.add(listener);
	}

	public void addDragSourceMotionListener(DragSourceMotionListener listener) {
		dragSourceMotionListeners.add(listener);
	}

	public void addDropTargetListener(DropTargetListener listener) {
		dropTargetListeners.add(listener);
	}

	public void addFocusListener(FocusListener listener) {
		focusListeners.add(listener);
	}

	public void addHierarchyListener(HierarchyListener listener) {
		hierarchyListeners.add(listener);
	}

	public void addHyperlinkListener(HyperlinkListener listener) {
		hyperlinkListeners.add(listener);
	}

	public void addInternalFrameListener(InternalFrameListener listener) {
		internalFrameListeners.add(listener);
	}

	public void addItemListener(ItemListener listener) {
		itemListeners.add(listener);
	}

	public void addKeyListener(KeyListener listener) {
		keyListeners.add(listener);
	}

	public void addListDataListener(ListDataListener listener) {
		listDataListeners.add(listener);
	}

	public void addListSelectionListener(ListSelectionListener listener) {
		listSelectionListeners.add(listener);
	}

	public void addMenuDragMouseListener(MenuDragMouseListener listener) {
		menuDragMouseListeners.add(listener);
	}

	public void addMenuKeyListener(MenuKeyListener listener) {
		menuKeyListeners.add(listener);
	}

	public void addMenuListener(MenuListener listener) {
		menuListeners.add(listener);
	}

	public void addMouseInputListener(MouseInputListener listener) {
		mouseInputListeners.add(listener);
	}

	public void addMouseListener(MouseListener listener) {
		mouseListeners.add(listener);
	}

	public void addMouseMotionListener(MouseMotionListener listener) {
		mouseMotionListeners.add(listener);
	}

	public void addMouseWheelListener(MouseWheelListener listener) {
		mouseWheelListeners.add(listener);
	}

	public void addPopupMenuListener(PopupMenuListener listener) {
		popupMenuListeners.add(listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	public void addTableColumnModelListener(TableColumnModelListener listener) {
		tableColumnModelListeners.add(listener);
	}

	public void addTableModelListener(TableModelListener listener) {
		tableModelListeners.add(listener);
	}

	public void addTextListener(TextListener listener) {
		textListeners.add(listener);
	}

	public void addTreeExpansionListener(TreeExpansionListener listener) {
		treeExpansionListeners.add(listener);
	}

	public void addTreeModelListener(TreeModelListener listener) {
		treeModelListeners.add(listener);
	}

	public void addTreeSelectionListener(TreeSelectionListener listener) {
		treeSelectionListeners.add(listener);
	}

	public void addTreeWillExpandListener(TreeWillExpandListener listener) {
		treeWillExpandListeners.add(listener);
	}

	public void addUndoableEditListener(UndoableEditListener listener) {
		undoableEditListeners.add(listener);
	}

	public void addWindowFocusListener(WindowFocusListener listener) {
		windowFocusListeners.add(listener);
	}

	public void addWindowListener(WindowListener listener) {
		windowListeners.add(listener);
	}

	public void addWindowStateListener(WindowStateListener listener) {
		windowStateListeners.add(listener);
	}

	// Threaded events
	public void threadedEvent(EventObject evt, int type) {
		try {
			if (type == ACTION_PERFORMED) {
				threadedActionPerformed((ActionEvent) evt);
			} else if (type == ADJUSTMENT_VALUE_CHANGED) {
				threadedAdjustmentValueChanged((AdjustmentEvent) evt);
			} else if (type == ANCESTOR_ADDED) {
				threadedAncestorAdded((AncestorEvent) evt);
			} else if (type == ANCESTOR_REMOVED) {
				threadedAncestorRemoved((AncestorEvent) evt);
			} else if (type == ANCESTOR_MOVED) {
				threadedAncestorMoved((AncestorEvent) evt);
			} else if (type == CARET_UPDATE) {
				threadedCaretUpdate((CaretEvent) evt);
			} else if (type == EDITING_STOPPED) {
				threadedEditingStopped((ChangeEvent) evt);
			} else if (type == EDITING_CANCELED) {
				threadedEditingCanceled((ChangeEvent) evt);
			} else if (type == STATE_CHANGED) {
				threadedStateChanged((ChangeEvent) evt);
			} else if (type == COMPONENT_RESIZED) {
				threadedComponentResized((ComponentEvent) evt);
			} else if (type == COMPONENT_MOVED) {
				threadedComponentMoved((ComponentEvent) evt);
			} else if (type == COMPONENT_SHOWN) {
				threadedComponentShown((ComponentEvent) evt);
			} else if (type == COMPONENT_HIDDEN) {
				threadedComponentHidden((ComponentEvent) evt);
			} else if (type == COMPONENT_ADDED) {
				threadedComponentAdded((ContainerEvent) evt);
			} else if (type == COMPONENT_REMOVED) {
				threadedComponentRemoved((ContainerEvent) evt);
			} else if (type == DRAG_GESTURE_RECOGNIZED) {
				threadedDragGestureRecognized((DragGestureEvent) evt);
			} else if (type == DRAG_ENTER) {
				threadedDragEnter((DragSourceDragEvent) evt);
			} else if (type == DRAG_OVER) {
				threadedDragOver((DragSourceDragEvent) evt);
			} else if (type == DROP_ACTION_CHANGED) {
				threadedDropActionChanged((DragSourceDragEvent) evt);
			} else if (type == DRAG_EXIT) {
				threadedDragExit((DragSourceEvent) evt);
			} else if (type == DRAG_DROP_END) {
				threadedDragDropEnd((DragSourceDropEvent) evt);
			} else if (type == DRAG_MOUSE_MOVED) {
				threadedDragMouseMoved((DragSourceDragEvent) evt);
			} else if (type == DROP) {
				threadedDrop((DropTargetDropEvent) evt);
			} else if (type == FOCUS_GAINED) {
				threadedFocusGained((FocusEvent) evt);
			} else if (type == FOCUS_LOST) {
				threadedFocusLost((FocusEvent) evt);
			} else if (type == HIERARCHY_CHANGED) {
				threadedHierarchyChanged((HierarchyEvent) evt);
			} else if (type == HYPERLINK_UPDATE) {
				threadedHyperlinkUpdate((HyperlinkEvent) evt);
			} else if (type == INTERNAL_FRAME_OPENED) {
				threadedInternalFrameOpened((InternalFrameEvent) evt);
			} else if (type == INTERNAL_FRAME_CLOSING) {
				threadedInternalFrameClosing((InternalFrameEvent) evt);
			} else if (type == INTERNAL_FRAME_CLOSED) {
				threadedInternalFrameClosed((InternalFrameEvent) evt);
			} else if (type == INTERNAL_FRAME_ICONIFIED) {
				threadedInternalFrameIconified((InternalFrameEvent) evt);
			} else if (type == INTERNAL_FRAME_DEICONIFIED) {
				threadedInternalFrameDeiconified((InternalFrameEvent) evt);
			} else if (type == INTERNAL_FRAME_ACTIVATED) {
				threadedInternalFrameActivated((InternalFrameEvent) evt);
			} else if (type == INTERNAL_FRAME_DEACTIVATED) {
				threadedInternalFrameDeactivated((InternalFrameEvent) evt);
			} else if (type == ITEM_STATE_CHANGED) {
				threadedItemStateChanged((ItemEvent) evt);
			} else if (type == KEY_TYPED) {
				threadedKeyTyped((KeyEvent) evt);
			} else if (type == KEY_PRESSED) {
				threadedKeyPressed((KeyEvent) evt);
			} else if (type == KEY_RELEASED) {
				threadedKeyReleased((KeyEvent) evt);
			} else if (type == INTERVAL_ADDED) {
				threadedIntervalAdded((ListDataEvent) evt);
			} else if (type == INTERVAL_REMOVED) {
				threadedIntervalRemoved((ListDataEvent) evt);
			} else if (type == CONTENTS_CHANGED) {
				threadedContentsChanged((ListDataEvent) evt);
			} else if (type == VALUE_CHANGED) {
				threadedValueChanged((ListSelectionEvent) evt);
			} else if (type == MENU_DRAG_MOUSE_ENTERED) {
				threadedMenuDragMouseEntered((MenuDragMouseEvent) evt);
			} else if (type == MENU_DRAG_MOUSE_EXITED) {
				threadedMenuDragMouseExited((MenuDragMouseEvent) evt);
			} else if (type == MENU_DRAG_MOUSE_DRAGGED) {
				threadedMenuDragMouseDragged((MenuDragMouseEvent) evt);
			} else if (type == MENU_DRAG_MOUSE_RELEASED) {
				threadedMenuDragMouseReleased((MenuDragMouseEvent) evt);
			} else if (type == MENU_KEY_TYPED) {
				threadedMenuKeyTyped((MenuKeyEvent) evt);
			} else if (type == MENU_KEY_PRESSED) {
				threadedMenuKeyPressed((MenuKeyEvent) evt);
			} else if (type == MENU_KEY_RELEASED) {
				threadedMenuKeyReleased((MenuKeyEvent) evt);
			} else if (type == MENU_SELECTED) {
				threadedMenuSelected((MenuEvent) evt);
			} else if (type == MENU_DESELECTED) {
				threadedMenuDeselected((MenuEvent) evt);
			} else if (type == MENU_CANCELED) {
				threadedMenuCanceled((MenuEvent) evt);
			} else if (type == MOUSE_CLICKED) {
				threadedMouseClicked((MouseEvent) evt);
			} else if (type == MOUSE_PRESSED) {
				threadedMousePressed((MouseEvent) evt);
			} else if (type == MOUSE_RELEASED) {
				threadedMouseReleased((MouseEvent) evt);
			} else if (type == MOUSE_ENTERED) {
				threadedMouseEntered((MouseEvent) evt);
			} else if (type == MOUSE_EXITED) {
				threadedMouseExited((MouseEvent) evt);
			} else if (type == MOUSE_DRAGGED) {
				threadedMouseDragged((MouseEvent) evt);
			} else if (type == MOUSE_MOVED) {
				threadedMouseMoved((MouseEvent) evt);
			} else if (type == MOUSE_WHEEL_MOVED) {
				threadedMouseWheelMoved((MouseWheelEvent) evt);
			} else if (type == POPUP_MENU_WILL_BECOME_VISIBLE) {
				threadedPopupMenuWillBecomeVisible((PopupMenuEvent) evt);
			} else if (type == POPUP_MENU_WILL_BECOME_INVISIBLE) {
				threadedPopupMenuWillBecomeInvisible((PopupMenuEvent) evt);
			} else if (type == POPUP_MENU_CANCELED) {
				threadedPopupMenuCanceled((PopupMenuEvent) evt);
			} else if (type == PROPERTY_CHANGE) {
				threadedPropertyChange((PropertyChangeEvent) evt);
			} else if (type == COLUMN_ADDED) {
				threadedColumnAdded((TableColumnModelEvent) evt);
			} else if (type == COLUMN_REMOVED) {
				threadedColumnRemoved((TableColumnModelEvent) evt);
			} else if (type == COLUMN_MOVED) {
				threadedColumnMoved((TableColumnModelEvent) evt);
			} else if (type == COLUMN_MARGIN_CHANGED) {
				threadedColumnMarginChanged((ChangeEvent) evt);
			} else if (type == COLUMN_SELECTION_CHANGED) {
				threadedColumnSelectionChanged((ListSelectionEvent) evt);
			} else if (type == TABLE_CHANGED) {
				threadedTableChanged((TableModelEvent) evt);
			} else if (type == TEXT_VALUE_CHANGED) {
				threadedTextValueChanged((TextEvent) evt);
			} else if (type == TREE_EXPANDED) {
				threadedTreeExpanded((TreeExpansionEvent) evt);
			} else if (type == TREE_COLLAPSED) {
				threadedTreeCollapsed((TreeExpansionEvent) evt);
			} else if (type == TREE_NODES_CHANGED) {
				threadedTreeNodesChanged((TreeModelEvent) evt);
			} else if (type == TREE_NODES_INSERTED) {
				threadedTreeNodesInserted((TreeModelEvent) evt);
			} else if (type == TREE_NODES_REMOVED) {
				threadedTreeNodesRemoved((TreeModelEvent) evt);
			} else if (type == TREE_STRUCTURE_CHANGED) {
				threadedTreeStructureChanged((TreeModelEvent) evt);
			} else if (type == TREE_WILL_EXPAND) {
				threadedTreeWillExpand((TreeExpansionEvent) evt);
			} else if (type == TREE_WILL_COLLAPSE) {
				threadedTreeWillCollapse((TreeExpansionEvent) evt);
			} else if (type == UNDOABLE_EDIT_HAPPENED) {
				threadedUndoableEditHappened((UndoableEditEvent) evt);
			} else if (type == WINDOW_GAINED_FOCUS) {
				threadedWindowGainedFocus((WindowEvent) evt);
			} else if (type == WINDOW_LOST_FOCUS) {
				threadedWindowLostFocus((WindowEvent) evt);
			} else if (type == WINDOW_OPENED) {
				threadedWindowOpened((WindowEvent) evt);
			} else if (type == WINDOW_CLOSING) {
				threadedWindowClosing((WindowEvent) evt);
			} else if (type == WINDOW_CLOSED) {
				threadedWindowClosed((WindowEvent) evt);
			} else if (type == WINDOW_ICONIFIED) {
				threadedWindowIconified((WindowEvent) evt);
			} else if (type == WINDOW_DEICONIFIED) {
				threadedWindowDeiconified((WindowEvent) evt);
			} else if (type == WINDOW_ACTIVATED) {
				threadedWindowActivated((WindowEvent) evt);
			} else if (type == WINDOW_DEACTIVATED) {
				threadedWindowDeactivated((WindowEvent) evt);
			} else if (type == WINDOW_STATE_CHANGED) {
				threadedWindowStateChanged((WindowEvent) evt);
			}
		} catch (ExpandVetoException exc) {
			throw new RuntimeException(exc);
		}
	}

	private void threadedActionPerformed(ActionEvent evt) {
		ActionListener al;
		for (int i = 0; i < actionListeners.size(); i++) {
			al = (ActionListener) actionListeners.get(i);
			al.actionPerformed(evt);
		}
	}

	public void threadedAdjustmentValueChanged(AdjustmentEvent e) {
		AdjustmentListener l;
		for (int i = 0; i < adjustmentListeners.size(); i++) {
			l = (AdjustmentListener) adjustmentListeners.get(i);
			l.adjustmentValueChanged(e);
		}
	}

	public void threadedAncestorAdded(AncestorEvent event) {
		AncestorListener l;
		for (int i = 0; i < ancestorListeners.size(); i++) {
			l = (AncestorListener) ancestorListeners.get(i);
			l.ancestorAdded(event);
		}
	}

	public void threadedAncestorRemoved(AncestorEvent event) {
		AncestorListener l;
		for (int i = 0; i < ancestorListeners.size(); i++) {
			l = (AncestorListener) ancestorListeners.get(i);
			l.ancestorRemoved(event);
		}
	}

	public void threadedAncestorMoved(AncestorEvent event) {
		AncestorListener l;
		for (int i = 0; i < ancestorListeners.size(); i++) {
			l = (AncestorListener) ancestorListeners.get(i);
			l.ancestorMoved(event);
		}
	}

	public void threadedCaretUpdate(CaretEvent e) {
		CaretListener l;
		for (int i = 0; i < caretListeners.size(); i++) {
			l = (CaretListener) caretListeners.get(i);
			l.caretUpdate(e);
		}
	}

	public void threadedEditingStopped(ChangeEvent e) {
		CellEditorListener l;
		for (int i = 0; i < cellEditorListeners.size(); i++) {
			l = (CellEditorListener) cellEditorListeners.get(i);
			l.editingStopped(e);
		}
	}

	public void threadedEditingCanceled(ChangeEvent e) {
		CellEditorListener l;
		for (int i = 0; i < cellEditorListeners.size(); i++) {
			l = (CellEditorListener) cellEditorListeners.get(i);
			l.editingCanceled(e);
		}
	}

	public void threadedStateChanged(ChangeEvent e) {
		ChangeListener l;
		for (int i = 0; i < changeListeners.size(); i++) {
			l = (ChangeListener) changeListeners.get(i);
			l.stateChanged(e);
		}
	}

	public void threadedComponentResized(ComponentEvent e) {
		ComponentListener l;
		for (int i = 0; i < componentListeners.size(); i++) {
			l = (ComponentListener) componentListeners.get(i);
			l.componentResized(e);
		}
	}

	public void threadedComponentMoved(ComponentEvent e) {
		ComponentListener l;
		for (int i = 0; i < componentListeners.size(); i++) {
			l = (ComponentListener) componentListeners.get(i);
			l.componentMoved(e);
		}
	}

	public void threadedComponentShown(ComponentEvent e) {
		ComponentListener l;
		for (int i = 0; i < componentListeners.size(); i++) {
			l = (ComponentListener) componentListeners.get(i);
			l.componentShown(e);
		}
	}

	public void threadedComponentHidden(ComponentEvent e) {
		ComponentListener l;
		for (int i = 0; i < componentListeners.size(); i++) {
			l = (ComponentListener) componentListeners.get(i);
			l.componentHidden(e);
		}
	}

	public void threadedComponentAdded(ContainerEvent e) {
		ContainerListener l;
		for (int i = 0; i < containerListeners.size(); i++) {
			l = (ContainerListener) containerListeners.get(i);
			l.componentAdded(e);
		}
	}

	public void threadedComponentRemoved(ContainerEvent e) {
		ContainerListener l;
		for (int i = 0; i < containerListeners.size(); i++) {
			l = (ContainerListener) containerListeners.get(i);
			l.componentRemoved(e);
		}
	}

	public void threadedDragGestureRecognized(DragGestureEvent dge) {
		DragGestureListener l;
		for (int i = 0; i < dragGestureListeners.size(); i++) {
			l = (DragGestureListener) dragGestureListeners.get(i);
			l.dragGestureRecognized(dge);
		}
	}

	public void threadedDragEnter(DragSourceDragEvent dsde) {
		DragSourceListener l;
		for (int i = 0; i < dragSourceListeners.size(); i++) {
			l = (DragSourceListener) dragSourceListeners.get(i);
			l.dragEnter(dsde);
		}
	}

	public void threadedDragOver(DragSourceDragEvent dsde) {
		DragSourceListener l;
		for (int i = 0; i < dragSourceListeners.size(); i++) {
			l = (DragSourceListener) dragSourceListeners.get(i);
			l.dragOver(dsde);
		}
	}

	public void threadedDropActionChanged(DragSourceDragEvent dsde) {
		DragSourceListener l;
		for (int i = 0; i < dragSourceListeners.size(); i++) {
			l = (DragSourceListener) dragSourceListeners.get(i);
			l.dropActionChanged(dsde);
		}
	}

	public void threadedDragExit(DragSourceEvent dse) {
		DragSourceListener l;
		for (int i = 0; i < dragSourceListeners.size(); i++) {
			l = (DragSourceListener) dragSourceListeners.get(i);
			l.dragExit(dse);
		}
	}

	public void threadedDragDropEnd(DragSourceDropEvent dsde) {
		DragSourceListener l;
		for (int i = 0; i < dragSourceListeners.size(); i++) {
			l = (DragSourceListener) dragSourceListeners.get(i);
			l.dragDropEnd(dsde);
		}
	}

	public void threadedDragMouseMoved(DragSourceDragEvent dsde) {
		DragSourceMotionListener l;
		for (int i = 0; i < dragSourceMotionListeners.size(); i++) {
			l = (DragSourceMotionListener) dragSourceMotionListeners.get(i);
			l.dragMouseMoved(dsde);
		}
	}

	public void threadedDragEnter(DropTargetDragEvent dtde) {
		DropTargetListener l;
		for (int i = 0; i < dropTargetListeners.size(); i++) {
			l = (DropTargetListener) dropTargetListeners.get(i);
			l.dragEnter(dtde);
		}
	}

	public void threadedDragOver(DropTargetDragEvent dtde) {
		DropTargetListener l;
		for (int i = 0; i < dropTargetListeners.size(); i++) {
			l = (DropTargetListener) dropTargetListeners.get(i);
			l.dragOver(dtde);
		}
	}

	public void threadedDropActionChanged(DropTargetDragEvent dtde) {
		DropTargetListener l;
		for (int i = 0; i < dropTargetListeners.size(); i++) {
			l = (DropTargetListener) dropTargetListeners.get(i);
			l.dropActionChanged(dtde);
		}
	}

	public void threadedDragExit(DropTargetEvent dte) {
		DropTargetListener l;
		for (int i = 0; i < dropTargetListeners.size(); i++) {
			l = (DropTargetListener) dropTargetListeners.get(i);
			l.dragExit(dte);
		}
	}

	public void threadedDrop(DropTargetDropEvent dtde) {
		DropTargetListener l;
		for (int i = 0; i < dropTargetListeners.size(); i++) {
			l = (DropTargetListener) dropTargetListeners.get(i);
			l.drop(dtde);
		}
	}

	public void threadedFocusGained(FocusEvent e) {
		FocusListener l;
		for (int i = 0; i < focusListeners.size(); i++) {
			l = (FocusListener) focusListeners.get(i);
			l.focusGained(e);
		}
	}

	public void threadedFocusLost(FocusEvent e) {
		FocusListener l;
		for (int i = 0; i < focusListeners.size(); i++) {
			l = (FocusListener) focusListeners.get(i);
			l.focusLost(e);
		}
	}

	public void threadedHierarchyChanged(HierarchyEvent e) {
		HierarchyListener l;
		for (int i = 0; i < hierarchyListeners.size(); i++) {
			l = (HierarchyListener) hierarchyListeners.get(i);
			l.hierarchyChanged(e);
		}
	}

	public void threadedHyperlinkUpdate(HyperlinkEvent e) {
		HyperlinkListener l;
		for (int i = 0; i < hyperlinkListeners.size(); i++) {
			l = (HyperlinkListener) hyperlinkListeners.get(i);
			l.hyperlinkUpdate(e);
		}
	}

	public void threadedInternalFrameOpened(InternalFrameEvent e) {
		InternalFrameListener l;
		for (int i = 0; i < internalFrameListeners.size(); i++) {
			l = (InternalFrameListener) internalFrameListeners.get(i);
			l.internalFrameOpened(e);
		}
	}

	public void threadedInternalFrameClosing(InternalFrameEvent e) {
		InternalFrameListener l;
		for (int i = 0; i < internalFrameListeners.size(); i++) {
			l = (InternalFrameListener) internalFrameListeners.get(i);
			l.internalFrameClosing(e);
		}
	}

	public void threadedInternalFrameClosed(InternalFrameEvent e) {
		InternalFrameListener l;
		for (int i = 0; i < internalFrameListeners.size(); i++) {
			l = (InternalFrameListener) internalFrameListeners.get(i);
			l.internalFrameClosed(e);
		}
	}

	public void threadedInternalFrameIconified(InternalFrameEvent e) {
		InternalFrameListener l;
		for (int i = 0; i < internalFrameListeners.size(); i++) {
			l = (InternalFrameListener) internalFrameListeners.get(i);
			l.internalFrameIconified(e);
		}
	}

	public void threadedInternalFrameDeiconified(InternalFrameEvent e) {
		InternalFrameListener l;
		for (int i = 0; i < internalFrameListeners.size(); i++) {
			l = (InternalFrameListener) internalFrameListeners.get(i);
			l.internalFrameDeiconified(e);
		}
	}

	public void threadedInternalFrameActivated(InternalFrameEvent e) {
		InternalFrameListener l;
		for (int i = 0; i < internalFrameListeners.size(); i++) {
			l = (InternalFrameListener) internalFrameListeners.get(i);
			l.internalFrameActivated(e);
		}
	}

	public void threadedInternalFrameDeactivated(InternalFrameEvent e) {
		InternalFrameListener l;
		for (int i = 0; i < internalFrameListeners.size(); i++) {
			l = (InternalFrameListener) internalFrameListeners.get(i);
			l.internalFrameDeactivated(e);
		}
	}

	public void threadedItemStateChanged(ItemEvent e) {
		ItemListener l;
		for (int i = 0; i < itemListeners.size(); i++) {
			l = (ItemListener) itemListeners.get(i);
			l.itemStateChanged(e);
		}
	}

	public void threadedKeyTyped(KeyEvent e) {
		KeyListener l;
		for (int i = 0; i < keyListeners.size(); i++) {
			l = (KeyListener) keyListeners.get(i);
			l.keyTyped(e);
		}
	}

	public void threadedKeyPressed(KeyEvent e) {
		KeyListener l;
		for (int i = 0; i < keyListeners.size(); i++) {
			l = (KeyListener) keyListeners.get(i);
			l.keyPressed(e);
		}
	}

	public void threadedKeyReleased(KeyEvent e) {
		KeyListener l;
		for (int i = 0; i < keyListeners.size(); i++) {
			l = (KeyListener) keyListeners.get(i);
			l.keyReleased(e);
		}
	}

	public void threadedIntervalAdded(ListDataEvent e) {
		ListDataListener l;
		for (int i = 0; i < listDataListeners.size(); i++) {
			l = (ListDataListener) listDataListeners.get(i);
			l.intervalAdded(e);
		}
	}

	public void threadedIntervalRemoved(ListDataEvent e) {
		ListDataListener l;
		for (int i = 0; i < listDataListeners.size(); i++) {
			l = (ListDataListener) listDataListeners.get(i);
			l.intervalRemoved(e);
		}
	}

	public void threadedContentsChanged(ListDataEvent e) {
		ListDataListener l;
		for (int i = 0; i < listDataListeners.size(); i++) {
			l = (ListDataListener) listDataListeners.get(i);
			l.contentsChanged(e);
		}
	}

	public void threadedValueChanged(ListSelectionEvent e) {
		ListSelectionListener l;
		for (int i = 0; i < listSelectionListeners.size(); i++) {
			l = (ListSelectionListener) listSelectionListeners.get(i);
			l.valueChanged(e);
		}
	}

	public void threadedMenuDragMouseEntered(MenuDragMouseEvent e) {
		MenuDragMouseListener l;
		for (int i = 0; i < menuDragMouseListeners.size(); i++) {
			l = (MenuDragMouseListener) menuDragMouseListeners.get(i);
			l.menuDragMouseEntered(e);
		}
	}

	public void threadedMenuDragMouseExited(MenuDragMouseEvent e) {
		MenuDragMouseListener l;
		for (int i = 0; i < menuDragMouseListeners.size(); i++) {
			l = (MenuDragMouseListener) menuDragMouseListeners.get(i);
			l.menuDragMouseExited(e);
		}
	}

	public void threadedMenuDragMouseDragged(MenuDragMouseEvent e) {
		MenuDragMouseListener l;
		for (int i = 0; i < menuDragMouseListeners.size(); i++) {
			l = (MenuDragMouseListener) menuDragMouseListeners.get(i);
			l.menuDragMouseDragged(e);
		}
	}

	public void threadedMenuDragMouseReleased(MenuDragMouseEvent e) {
		MenuDragMouseListener l;
		for (int i = 0; i < menuDragMouseListeners.size(); i++) {
			l = (MenuDragMouseListener) menuDragMouseListeners.get(i);
			l.menuDragMouseReleased(e);
		}
	}

	public void threadedMenuKeyTyped(MenuKeyEvent e) {
		MenuKeyListener l;
		for (int i = 0; i < menuKeyListeners.size(); i++) {
			l = (MenuKeyListener) menuKeyListeners.get(i);
			l.menuKeyTyped(e);
		}
	}

	public void threadedMenuKeyPressed(MenuKeyEvent e) {
		MenuKeyListener l;
		for (int i = 0; i < menuKeyListeners.size(); i++) {
			l = (MenuKeyListener) menuKeyListeners.get(i);
			l.menuKeyPressed(e);
		}
	}

	public void threadedMenuKeyReleased(MenuKeyEvent e) {
		MenuKeyListener l;
		for (int i = 0; i < menuKeyListeners.size(); i++) {
			l = (MenuKeyListener) menuKeyListeners.get(i);
			l.menuKeyReleased(e);
		}
	}

	public void threadedMenuSelected(MenuEvent e) {
		MenuListener l;
		for (int i = 0; i < menuListeners.size(); i++) {
			l = (MenuListener) menuListeners.get(i);
			l.menuSelected(e);
		}
	}

	public void threadedMenuDeselected(MenuEvent e) {
		MenuListener l;
		for (int i = 0; i < menuListeners.size(); i++) {
			l = (MenuListener) menuListeners.get(i);
			l.menuDeselected(e);
		}
	}

	public void threadedMenuCanceled(MenuEvent e) {
		MenuListener l;
		for (int i = 0; i < menuListeners.size(); i++) {
			l = (MenuListener) menuListeners.get(i);
			l.menuCanceled(e);
		}
	}

	public void threadedMouseClicked(MouseEvent e) {
		MouseListener l;
		for (int i = 0; i < mouseListeners.size(); i++) {
			l = (MouseListener) mouseListeners.get(i);
			l.mouseClicked(e);
		}
	}

	public void threadedMousePressed(MouseEvent e) {
		MouseListener l;
		for (int i = 0; i < mouseListeners.size(); i++) {
			l = (MouseListener) mouseListeners.get(i);
			l.mousePressed(e);
		}
	}

	public void threadedMouseReleased(MouseEvent e) {
		MouseListener l;
		for (int i = 0; i < mouseListeners.size(); i++) {
			l = (MouseListener) mouseListeners.get(i);
			l.mouseReleased(e);
		}
	}

	public void threadedMouseEntered(MouseEvent e) {
		MouseListener l;
		for (int i = 0; i < mouseListeners.size(); i++) {
			l = (MouseListener) mouseListeners.get(i);
			l.mouseEntered(e);
		}
	}

	public void threadedMouseExited(MouseEvent e) {
		MouseListener l;
		for (int i = 0; i < mouseListeners.size(); i++) {
			l = (MouseListener) mouseListeners.get(i);
			l.mouseExited(e);
		}
	}

	public void threadedMouseDragged(MouseEvent e) {
		MouseMotionListener l;
		for (int i = 0; i < mouseMotionListeners.size(); i++) {
			l = (MouseMotionListener) mouseMotionListeners.get(i);
			l.mouseDragged(e);
		}
	}

	public void threadedMouseMoved(MouseEvent e) {
		MouseMotionListener l;
		for (int i = 0; i < mouseMotionListeners.size(); i++) {
			l = (MouseMotionListener) mouseMotionListeners.get(i);
			l.mouseMoved(e);
		}
	}

	public void threadedMouseWheelMoved(MouseWheelEvent e) {
		MouseWheelListener l;
		for (int i = 0; i < mouseWheelListeners.size(); i++) {
			l = (MouseWheelListener) mouseWheelListeners.get(i);
			l.mouseWheelMoved(e);
		}
	}

	public void threadedPopupMenuWillBecomeVisible(PopupMenuEvent e) {
		PopupMenuListener l;
		for (int i = 0; i < popupMenuListeners.size(); i++) {
			l = (PopupMenuListener) popupMenuListeners.get(i);
			l.popupMenuWillBecomeVisible(e);
		}
	}

	public void threadedPopupMenuWillBecomeInvisible(PopupMenuEvent e) {
		PopupMenuListener l;
		for (int i = 0; i < popupMenuListeners.size(); i++) {
			l = (PopupMenuListener) popupMenuListeners.get(i);
			l.popupMenuWillBecomeInvisible(e);
		}
	}

	public void threadedPopupMenuCanceled(PopupMenuEvent e) {
		PopupMenuListener l;
		for (int i = 0; i < popupMenuListeners.size(); i++) {
			l = (PopupMenuListener) popupMenuListeners.get(i);
			l.popupMenuCanceled(e);
		}
	}

	public void threadedPropertyChange(PropertyChangeEvent e) {
		PropertyChangeListener l;
		for (int i = 0; i < propertyChangeListeners.size(); i++) {
			l = (PropertyChangeListener) propertyChangeListeners.get(i);
			l.propertyChange(e);
		}
	}

	public void threadedColumnAdded(TableColumnModelEvent e) {
		TableColumnModelListener l;
		for (int i = 0; i < tableColumnModelListeners.size(); i++) {
			l = (TableColumnModelListener) tableColumnModelListeners.get(i);
			l.columnAdded(e);
		}
	}

	public void threadedColumnRemoved(TableColumnModelEvent e) {
		TableColumnModelListener l;
		for (int i = 0; i < tableColumnModelListeners.size(); i++) {
			l = (TableColumnModelListener) tableColumnModelListeners.get(i);
			l.columnRemoved(e);
		}
	}

	public void threadedColumnMoved(TableColumnModelEvent e) {
		TableColumnModelListener l;
		for (int i = 0; i < tableColumnModelListeners.size(); i++) {
			l = (TableColumnModelListener) tableColumnModelListeners.get(i);
			l.columnMoved(e);
		}
	}

	public void threadedColumnMarginChanged(ChangeEvent e) {
		TableColumnModelListener l;
		for (int i = 0; i < tableColumnModelListeners.size(); i++) {
			l = (TableColumnModelListener) tableColumnModelListeners.get(i);
			l.columnMarginChanged(e);
		}
	}

	public void threadedColumnSelectionChanged(ListSelectionEvent e) {
		TableColumnModelListener l;
		for (int i = 0; i < tableColumnModelListeners.size(); i++) {
			l = (TableColumnModelListener) tableColumnModelListeners.get(i);
			l.columnSelectionChanged(e);
		}
	}

	public void threadedTableChanged(TableModelEvent e) {
		TableModelListener l;
		for (int i = 0; i < tableModelListeners.size(); i++) {
			l = (TableModelListener) tableModelListeners.get(i);
			l.tableChanged(e);
		}
	}

	public void threadedTextValueChanged(TextEvent e) {
		TextListener l;
		for (int i = 0; i < textListeners.size(); i++) {
			l = (TextListener) textListeners.get(i);
			l.textValueChanged(e);
		}
	}

	public void threadedTreeExpanded(TreeExpansionEvent event) {
		TreeExpansionListener l;
		for (int i = 0; i < treeExpansionListeners.size(); i++) {
			l = (TreeExpansionListener) treeExpansionListeners.get(i);
			l.treeExpanded(event);
		}
	}

	public void threadedTreeCollapsed(TreeExpansionEvent event) {
		TreeExpansionListener l;
		for (int i = 0; i < treeExpansionListeners.size(); i++) {
			l = (TreeExpansionListener) treeExpansionListeners.get(i);
			l.treeCollapsed(event);
		}
	}

	public void threadedTreeNodesChanged(TreeModelEvent e) {
		TreeModelListener l;
		for (int i = 0; i < treeModelListeners.size(); i++) {
			l = (TreeModelListener) treeModelListeners.get(i);
			l.treeNodesChanged(e);
		}
	}

	public void threadedTreeNodesInserted(TreeModelEvent e) {
		TreeModelListener l;
		for (int i = 0; i < treeModelListeners.size(); i++) {
			l = (TreeModelListener) treeModelListeners.get(i);
			l.treeNodesInserted(e);
		}
	}

	public void threadedTreeNodesRemoved(TreeModelEvent e) {
		TreeModelListener l;
		for (int i = 0; i < treeModelListeners.size(); i++) {
			l = (TreeModelListener) treeModelListeners.get(i);
			l.treeNodesRemoved(e);
		}
	}

	public void threadedTreeStructureChanged(TreeModelEvent e) {
		TreeModelListener l;
		for (int i = 0; i < treeModelListeners.size(); i++) {
			l = (TreeModelListener) treeModelListeners.get(i);
			l.treeStructureChanged(e);
		}
	}

	public void threadedValueChanged(TreeSelectionEvent e) {
		TreeSelectionListener l;
		for (int i = 0; i < treeSelectionListeners.size(); i++) {
			l = (TreeSelectionListener) treeSelectionListeners.get(i);
			l.valueChanged(e);
		}
	}

	public void threadedTreeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		TreeWillExpandListener l;
		for (int i = 0; i < treeWillExpandListeners.size(); i++) {
			l = (TreeWillExpandListener) treeWillExpandListeners.get(i);
			l.treeWillExpand(event);
		}
	}

	public void threadedTreeWillCollapse(TreeExpansionEvent event)
			throws ExpandVetoException {
		TreeWillExpandListener l;
		for (int i = 0; i < treeWillExpandListeners.size(); i++) {
			l = (TreeWillExpandListener) treeWillExpandListeners.get(i);
			l.treeWillCollapse(event);
		}
	}

	public void threadedUndoableEditHappened(UndoableEditEvent e) {
		UndoableEditListener l;
		for (int i = 0; i < undoableEditListeners.size(); i++) {
			l = (UndoableEditListener) undoableEditListeners.get(i);
			l.undoableEditHappened(e);
		}
	}

	public void threadedWindowGainedFocus(WindowEvent e) {
		WindowFocusListener l;
		for (int i = 0; i < windowFocusListeners.size(); i++) {
			l = (WindowFocusListener) windowFocusListeners.get(i);
			l.windowGainedFocus(e);
		}
	}

	public void threadedWindowLostFocus(WindowEvent e) {
		WindowFocusListener l;
		for (int i = 0; i < windowFocusListeners.size(); i++) {
			l = (WindowFocusListener) windowFocusListeners.get(i);
			l.windowLostFocus(e);
		}
	}

	public void threadedWindowOpened(WindowEvent e) {
		WindowListener l;
		for (int i = 0; i < windowListeners.size(); i++) {
			l = (WindowListener) windowListeners.get(i);
			l.windowOpened(e);
		}
	}

	public void threadedWindowClosing(WindowEvent e) {
		WindowListener l;
		for (int i = 0; i < windowListeners.size(); i++) {
			l = (WindowListener) windowListeners.get(i);
			l.windowClosing(e);
		}
	}

	public void threadedWindowClosed(WindowEvent e) {
		WindowListener l;
		for (int i = 0; i < windowListeners.size(); i++) {
			l = (WindowListener) windowListeners.get(i);
			l.windowClosed(e);
		}
	}

	public void threadedWindowIconified(WindowEvent e) {
		WindowListener l;
		for (int i = 0; i < windowListeners.size(); i++) {
			l = (WindowListener) windowListeners.get(i);
			l.windowIconified(e);
		}
	}

	public void threadedWindowDeiconified(WindowEvent e) {
		WindowListener l;
		for (int i = 0; i < windowListeners.size(); i++) {
			l = (WindowListener) windowListeners.get(i);
			l.windowDeiconified(e);
		}
	}

	public void threadedWindowActivated(WindowEvent e) {
		WindowListener l;
		for (int i = 0; i < windowListeners.size(); i++) {
			l = (WindowListener) windowListeners.get(i);
			l.windowActivated(e);
		}
	}

	public void threadedWindowDeactivated(WindowEvent e) {
		WindowListener l;
		for (int i = 0; i < windowListeners.size(); i++) {
			l = (WindowListener) windowListeners.get(i);
			l.windowDeactivated(e);
		}
	}

	public void threadedWindowStateChanged(WindowEvent e) {
		WindowStateListener l;
		for (int i = 0; i < windowStateListeners.size(); i++) {
			l = (WindowStateListener) windowStateListeners.get(i);
			l.windowStateChanged(e);
		}
	}

	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("Testing");
		frame.setSize(600, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();
		c.setLayout(new FlowLayout());
		JButton button1 = new JButton("Button1");
		JButton button2 = new JButton("Button2");
		c.add(button1);
		c.add(button2);

		EventQueue queue1 = new EventQueue();
		queue1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Event 1");
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException exc) {
				}
				System.out.println("Event 1 returning");
			}
		});
		button1.addActionListener(queue1);

		EventQueue queue2 = new EventQueue();
		queue2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Event 2");
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException exc) {
				}
				System.out.println("Event 2 returning");
			}
		});
		button2.addActionListener(queue2);

		frame.setVisible(true);
	}
}
