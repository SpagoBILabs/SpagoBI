/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */
package com.tensegrity.wpalo.client.ui.mvc.fasttree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.libideas.client.StyleInjector;
import com.google.gwt.libideas.resources.client.DataResource;
import com.google.gwt.libideas.resources.client.ImmutableResourceBundle;
import com.google.gwt.libideas.resources.client.TextResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.FocusListenerCollection;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerCollection;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.FocusImpl;
import com.google.gwt.widgetideas.client.overrides.DOMHelper;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.SelectionCountListener;

/**
 * A standard hierarchical tree widget. The tree contains a hierarchy of
 * {@link FastMSTreeItem}s.
 * 
 * Explicitly call FastTree.addDefaultCSS() to include the default style sheet.
 * 
 * <p>
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-FastTree { the tree itself }</li>
 * <li>.gwt-FastTree .gwt-FastMSTreeItem { a tree item }</li>
 * <li>.gwt-FastTree .selection-bar {the selection bar used to highlight the
 * selected tree item}</li> </ul>
 */
public class FastMSTree extends Panel implements HasWidgets, HasFocus,
    HasFastMSTreeItems {
	public static final int SELECT   = 0;
	public static final int ADD      = 1;
	public static final int INTERVAL = 2;
	public static final int ADDONLY  = 3;
	
	private final boolean isMultiSelect;
	private boolean childSelect;
	private boolean listenToStateChange = false;
	
  /**
   * Resources used.
   */
  public interface DefaultResources extends ImmutableResourceBundle {

    /**
     * The css file.
     */
    @Resource("FastTree.css")
    TextResource css();

    /**
     * The rtl css file.
     */
    @Resource("FastTreeRTL.css")
    TextResource cssRTL();

    /**
     * The gif used to highlight selection.
     */
    @Resource("selectionBar.gif")
    DataResource selectionBar();

    /**
     * "+" gif.
     */
    @Resource("treeClosed.gif")
    DataResource treeClosed();

    /**
     * "-" gif.
     */
    @Resource("treeOpen.gif")
    DataResource treeOpen();
  }

  private static final String STYLENAME_DEFAULT = "gwt-FastTree";

  private static final String STYLENAME_SELECTION = "selection-bar";

  private static FocusImpl impl = FocusImpl.getFocusImplForPanel();

  public void setListenToStateChange(boolean listen) {
	 this.listenToStateChange = listen;
  }
  
  public boolean isListenToStateChange() {
	  return listenToStateChange;	  
  }
  
  /**
   * Add the default style sheet and images.
   * 
   * This method is not called by the Tree and should be called by explicitly
   * by the consumer to include the default style sheet. 
   */
  public static void addDefaultCSS() {
    DefaultResources instance = GWT.create(DefaultResources.class);
    if (LocaleInfo.getCurrentLocale().isRTL()) {
      StyleInjector.injectStylesheet(instance.cssRTL().getText(), instance);
    } else {
      StyleInjector.injectStylesheet(instance.css().getText(), instance);
    }
  }

  private static boolean hasModifiers(Event event) {
    boolean alt = event.getAltKey();
    boolean ctrl = event.getCtrlKey();
    boolean meta = event.getMetaKey();
    boolean shift = event.getShiftKey();

    return alt || ctrl || meta || shift;
  }

  private boolean lostMouseDown = true;
  /**
   * Map of TreeItem.widget -> TreeItem.
   */
  private final HashMap<Widget, FastMSTreeItem> childWidgets = new HashMap<Widget, FastMSTreeItem>();
  private final LinkedHashSet<FastMSTreeItem> curSelection = new LinkedHashSet<FastMSTreeItem>();
  private final Element focusable;
  private FocusListenerCollection focusListeners;
  private KeyboardListenerCollection keyboardListeners;
  private MouseListenerCollection mouseListeners;
  private final FastMSTreeItem root;
  private Event keyDown;
  private final ArrayList <LoadListener> loadListeners = new ArrayList<LoadListener>();
  private Event lastKeyDown;
  private FastMSTreeItem lastSelectedItem = null;
  private boolean firingLoadedEvent = false;
  private ArrayList <LoadListener> toBeRemoved = new ArrayList<LoadListener>();
  private ArrayList <LoadListener> toBeAdded = new ArrayList<LoadListener>();
  private final ArrayList <DoubleClickListener> doubleClickListeners = new ArrayList<DoubleClickListener>();
  private final ArrayList <SelectionCountListener> selectionCountListeners = new ArrayList<SelectionCountListener>();
  
  /**
   * Constructs a tree.
   */
  public FastMSTree(boolean ms) {
	  childSelect = true;
	  isMultiSelect = ms;
    setElement(DOM.createDiv());

    focusable = createFocusElement();
    setStyleName(focusable, STYLENAME_SELECTION);

    sinkEvents(Event.MOUSEEVENTS | Event.ONCLICK | Event.KEYEVENTS
        | Event.MOUSEEVENTS | Event.ONDBLCLICK);

    // The 'root' item is invisible and serves only as a container
    // for all top-level items.
    root = new FastMSTreeItem() {
      @Override
      public void addItem(FastMSTreeItem item) {
        super.addItem(item);

        DOM.appendChild(FastMSTree.this.getElement(), item.getElement());

        // Explicitly set top-level items' parents to null.
        item.setParentItem(null);

        // Use no margin on top-most items.
        DOM.setIntStyleAttribute(item.getElement(), "margin", 0);
      }

      @Override
      public void fastAddItem(FastMSTreeItem item) {
        super.fastAddItem(item);

        DOM.appendChild(FastMSTree.this.getElement(), item.getElement());

        // Explicitly set top-level items' parents to null.
        item.setParentItem(null);

        // Use no margin on top-most items.
        DOM.setIntStyleAttribute(item.getElement(), "margin", 0);
      }

      @Override
      public void removeItem(FastMSTreeItem item) {
        if (!getChildren().contains(item)) {
          return;
        }

        // Update Item state.
        item.clearTree();
        item.setParentItem(null);
        getChildren().remove(item);

        DOM.removeChild(FastMSTree.this.getElement(), item.getElement());
      }
    };
    root.setTree(this);

    setStyleName(STYLENAME_DEFAULT);
    if (!curSelection.isEmpty()) {
    	moveSelectionBar(curSelection.iterator().next());
    }
  }

  /**
   * Adds the widget as a root tree item.
   * 
   * @see com.google.gwt.user.client.ui.HasWidgets#add(com.google.gwt.user.client.ui.Widget)
   * @param widget widget to add.
   */
  @Override
  public void add(Widget widget) {
    addItem(widget);
  }

  public void expandAll() {
	  for (FastMSTreeItem item: getChildren()) {
		  item.expandAll();
	  }
  }
  
  public void collapseAll() {	  
	  LinkedHashSet <FastMSTreeItem> sels = new LinkedHashSet<FastMSTreeItem>(); 
	  for (FastMSTreeItem item: getChildren()) {
		  item.collapseAll();
		  if (item.isSelected()) {
			  sels.add(item);
		  }
	  }	  
	  fastSetSelectedItems(sels);
  }
  
  public void deepExpand(LinkedHashSet <FastMSTreeItem> nodes) {
	  for (FastMSTreeItem node: nodes) {
		  node.expandAll();
	  }
  }

  public boolean moveItemUp(FastMSTreeItem item) {
		int index = root.getChildren().indexOf(item);
		if (index < 1) {
			return false;
		}
		root.getChildren().remove(index);
		index--;
		root.getChildren().add(index, item);
		Element cElem = item.getElement();
		index = DOM.getChildIndex(FastMSTree.this.getElement(), cElem);
		DOM.removeChild(FastMSTree.this.getElement(), cElem);
		index--;
		DOM.insertChild(FastMSTree.this.getElement(), cElem, index);
		return true;
  }

  public boolean moveItemDown(FastMSTreeItem item) {
		int index = root.getChildren().indexOf(item);
		if (index == -1 || index > root.getChildCount() - 2) {
			return false;
		}
		root.getChildren().remove(index);
		index++;
		root.getChildren().add(index, item);
		Element cElem = item.getElement();
		index = DOM.getChildIndex(FastMSTree.this.getElement(), cElem);
		DOM.removeChild(FastMSTree.this.getElement(), cElem);
		index++;
		DOM.insertChild(FastMSTree.this.getElement(), cElem, index);
		return true;
  }
  
  public int getTotalSize() {
	  int result = getChildCount();
	  for (FastMSTreeItem it: getChildren()) {
		  result += it.getTotalSize();
	  }
	  return result;
  }
  
  public void addFocusListener(FocusListener listener) {
    if (focusListeners == null) {
      focusListeners = new FocusListenerCollection();
    }
    focusListeners.add(listener);
  }

  /**
   * Adds an item to the root level of this tree.
   * 
   * @param item the item to be added
   */
  public void addItem(FastMSTreeItem item) {
    root.addItem(item);    
  }
  
  public void fastAddItem(FastMSTreeItem item) {
	  root.fastAddItem(item);
  }

  /**
   * Adds a simple tree item containing the specified text.
   * 
   * @param itemText the text of the item to be added
   * @return the item that was added
   */
  public FastMSTreeItem addItem(String itemText) {
    FastMSTreeItem ret = new FastMSTreeItem(itemText);
    addItem(ret);

    return ret;
  }

  /**
   * Adds a new tree item containing the specified widget.
   * 
   * @param widget the widget to be added
   */
  public FastMSTreeItem addItem(Widget widget) {
    return root.addItem(widget);
  }

  public void addKeyboardListener(KeyboardListener listener) {
    if (keyboardListeners == null) {
      keyboardListeners = new KeyboardListenerCollection();
    }
    keyboardListeners.add(listener);
  }

  public void addMouseListener(MouseListener listener) {
    if (mouseListeners == null) {
      mouseListeners = new MouseListenerCollection();
    }
    mouseListeners.add(listener);
  }

  /**
   * Clears all tree items from the current tree.
   */
  @Override
  public void clear() {
    int size = root.getChildCount();
    for (int i = size - 1; i >= 0; i--) {
      root.getChild(i).remove();
    }
  }

  /**
   * Ensures that the currently-selected item is visible, opening its parents
   * and scrolling the tree as necessary.
   */
  public void ensureSelectedItemsVisible() {
    if (curSelection == null || curSelection.isEmpty()) {
      return;
    }

    FastMSTreeItem item = curSelection.iterator().next();
   	FastMSTreeItem parent = item.getParentItem();
   	while (parent != null) {
   		parent.setState(true);
   		parent = parent.getParentItem();
   	}    	
   	moveFocus(item);

  }

  public void ensurePathOpen(FastMSTreeItem item) {
		if (item == null) {
			return;
		}

		FastMSTreeItem parent = item.getParentItem();
		while (parent != null) {
			parent.setState(true, false);
			parent = parent.getParentItem();
		}	  
  }
  
  public void ensureUnselectedItemVisible(FastMSTreeItem item) {
		if (item == null) {
			return;
		}

		FastMSTreeItem parent = item.getParentItem();
		while (parent != null) {
			parent.setState(true);
			parent = parent.getParentItem();
		}
		moveUnselectedFocus(item);
  }
    
  public void ensureItemVisible(FastMSTreeItem item) {
	    if (item == null) {
	      return;
	    }

	   	FastMSTreeItem parent = item.getParentItem();	   	
	   	while (parent != null) {
	   		parent.setState(true);
	   		parent = parent.getParentItem();
	   	}
	   	moveFocus(item);
	  }

  public FastMSTreeItem getChild(int index) {
    return root.getChild(index);
  }

  public int getChildCount() {
    return root.getChildCount();
  }

  public ArrayList <FastMSTreeItem> getChildren() {
	  return root.getChildren();
  }
  
  public int getChildIndex(FastMSTreeItem child) {
    return root.getChildIndex(child);
  }

  /**
   * Gets the top-level tree item at the specified index.
   * 
   * @param index the index to be retrieved
   * @return the item at that index
   */
  public FastMSTreeItem getItem(int index) {
    return root.getChild(index);
  }

  /**
   * Gets the number of items contained at the root of this tree.
   * 
   * @return this tree's item count
   */
  public int getItemCount() {
    return root.getChildCount();
  }

  /**
   * Gets the currently selected item.
   * 
   * @return the selected item
   */
  public LinkedHashSet <FastMSTreeItem> getSelectedItems() {
    return curSelection;
  }
  
  public int getNumberOfSelectedItems() {
	  return curSelection.size();
  }

  public void addSelectionCountListener(SelectionCountListener l) {
	  selectionCountListeners.add(l);
  }
  
  public void removeSelectionCountListener(SelectionCountListener l) {
	  selectionCountListeners.remove(l);
  }
  
  public final void fireSelectionNumberChanged() {
	 int n = getNumberOfSelectedItems();
	  for (SelectionCountListener sl: selectionCountListeners) {
		 sl.selectionCountChanged(n);
	 }
  }
  
  public int getTabIndex() {
    return impl.getTabIndex(focusable);
  }

  public Iterator<Widget> iterator() {
    final Widget[] widgets = new Widget[childWidgets.size()];
    childWidgets.keySet().toArray(widgets);
    return WidgetIterators.createWidgetIterator(this, widgets);
  }

  @Override
  @SuppressWarnings("fallthrough")
  public void onBrowserEvent(Event event) {
    int eventType = DOM.eventGetType(event);

    switch (eventType) {
      case Event.ONDBLCLICK: {
    	  doubleClicked(event);
    	  break;
      }
      case Event.ONCLICK: {
        Element e = DOM.eventGetTarget(event);                
        if (shouldTreeDelegateFocusToElement(e)) {
          // The click event should have given focus to this element already.
          // Avoid moving focus back up to the tree (so that focusable widgets
          // attached to TreeItems can receive keyboard events).
        } else {
//          if (!hasModifiers(event)) {
            clickedOnFocus(DOM.eventGetTarget(event));
//          }
        }
        break;
      }

      case Event.ONMOUSEMOVE: {
        if (mouseListeners != null) {
          mouseListeners.fireMouseEvent(this, event);
        }
        break;
      }

      case Event.ONMOUSEUP: {
        boolean left = event.getButton() == Event.BUTTON_LEFT;

        if (lostMouseDown) {
          // artificial mouse down due to IE bug where mouse downs are lost.

          if (left) {
            elementClicked(root, event);
          }
        }
        if (mouseListeners != null) {
          mouseListeners.fireMouseEvent(this, event);
        }
        lostMouseDown = true;
        break;
      }
      case Event.ONMOUSEDOWN: {
        boolean left = event.getButton() == Event.BUTTON_LEFT;

        lostMouseDown = false;
        if (mouseListeners != null) {
          mouseListeners.fireMouseEvent(this, event);
        }
        if (left) {
          elementClicked(root, event);
        }
        break;
      }
      case Event.ONMOUSEOVER: {
        if (mouseListeners != null) {
          mouseListeners.fireMouseEvent(this, event);
        }
        break;
      }

      case Event.ONMOUSEOUT: {
        if (mouseListeners != null) {
          mouseListeners.fireMouseEvent(this, event);
        }
        break;
      }

      case Event.ONFOCUS:
        // If we already have focus, ignore the focus event.
        if (focusListeners != null) {
          focusListeners.fireFocusEvent(this, event);
        }
        break;

      case Event.ONBLUR: {
        if (focusListeners != null) {
          focusListeners.fireFocusEvent(this, event);
        }

        break;
      }

      case Event.ONKEYDOWN:
        keyDown = event;
        // Intentional fallthrough.
      case Event.ONKEYUP:
//        if (eventType == Event.ONKEYUP) {
//          // If we got here because of a key tab, then we need to make sure the
//          // current tree item is selected.
//          if (DOM.eventGetKeyCode(event) == KeyboardListener.KEY_TAB) {
//            ArrayList<Element> chain = new ArrayList<Element>();
//            collectElementChain(chain, getElement(), DOM.eventGetTarget(event));
//            FastMSTreeItem item = findItemByChain(chain, 0, root);
//            if (item != getSelectedItems()) {
//              setSelectedItems(item, true);
//            }
//          }
//        }

        // Intentional fall through.
      case Event.ONKEYPRESS: {
        if (keyboardListeners != null) {
          keyboardListeners.fireKeyboardEvent(this, event);
        }

        if (hasModifiers(event)) {
          break;
        }

        // Trying to avoid duplicate key downs and fire navigation despite
        // missing key downs.
        if (eventType != Event.ONKEYUP) {
          if (lastKeyDown == null || (!lastKeyDown.equals(keyDown))) {
            //keyboardNavigation(event);
          }
          if (eventType == Event.ONKEYPRESS) {
            lastKeyDown = null;
          } else {
            lastKeyDown = keyDown;
          }
        }
        if (DOMHelper.isArrowKey(DOM.eventGetKeyCode(event))) {
          DOM.eventCancelBubble(event, true);
          DOM.eventPreventDefault(event);
        }
        break;
      }
    }

    // We must call SynthesizedWidget's implementation for all other events.
    super.onBrowserEvent(event);
  }

  private final void doubleClicked(Event event) {
	    Element target = DOM.eventGetTarget(event);
	    ArrayList<Element> chain = new ArrayList<Element>();
	    collectElementChain(chain, getElement(), target);
	    FastMSTreeItem item = findItemByChain(chain, 0, root);
	    if (item != null) {
	    	fireDoubleClicked(item);
	    }
  }

  public void addDoubleClickListener(DoubleClickListener listener) {
	  doubleClickListeners.add(listener);
  }
  
  public void removeDoubleClickListener(DoubleClickListener listener) {
	  doubleClickListeners.remove(listener);
  }
  
  private final void fireDoubleClicked(FastMSTreeItem item) {
	  for (DoubleClickListener l: doubleClickListeners) {
		  l.doubleClicked(item);
	  }
  }
  
  @Override
  public boolean remove(Widget w) {
    // Validate.
    FastMSTreeItem item = childWidgets.get(w);
    if (item == null) {
      return false;
    }

    // Delegate to TreeItem.setWidget, which performs correct removal.
    item.setWidget(null);
    return true;
  }

  public void removeFocusListener(FocusListener listener) {
    if (focusListeners != null) {
      focusListeners.remove(listener);
    }
  }

  /**
   * Removes an item from the root level of this tree.
   * 
   * @param item the item to be removed
   */
  public void removeItem(FastMSTreeItem item) {
    root.removeItem(item);
  }

  /**
   * Removes all items from the root level of this tree.
   */
  public void removeItems() {
    while (getItemCount() > 0) {
      removeItem(getItem(0));
    }
  }

  public void removeKeyboardListener(KeyboardListener listener) {
    if (keyboardListeners != null) {
      keyboardListeners.remove(listener);
    }
  }

  public void setAccessKey(char key) {
    impl.setAccessKey(focusable, key);
  }

  public void setFocus(boolean focus) {
    if (focus) {
      impl.focus(focusable);
    } else {
      impl.blur(focusable);
    }
  }

  /**
   * Selects a specified item.
   * 
   * @param item the item to be selected, or <code>null</code> to deselect all
   *          items
   */
  public void setSelectedItems(LinkedHashSet <FastMSTreeItem> items) {
    setSelectedItems(items, true);
  }

  public void fastSetSelectedItems(LinkedHashSet <FastMSTreeItem> items) {
	  	Iterator<FastMSTreeItem> it = curSelection.iterator();
	  	while (it.hasNext()) {			
	  		FastMSTreeItem item = it.next();
	        // Select the item and fire the selection event.
	        item.setSelection(false, false);
	        it.remove();
		}
	  selectTheseItems(items);
  }
  
  /**
   * Selects a specified item.
   * 
   * @param item the item to be selected, or <code>null</code> to deselect all
   *          items
   * @param fireEvents <code>true</code> to allow selection events to be fired
   */
  public void setSelectedItems(LinkedHashSet <FastMSTreeItem> items, boolean fireEvents) {
    if (items == null || items.isEmpty()) {
      if (curSelection.isEmpty()) {
        return;
      }
      for (FastMSTreeItem item: curSelection) {
    	  item.setSelection(false, fireEvents);
      }
      curSelection.clear();
      return;
    } else {
    	setSelectedItems(null, false);
    }
    
    for (FastMSTreeItem item: items) {
    	onSelection(item, fireEvents, true, ADD);
    }
  }

  public void setTabIndex(int index) {
    impl.setTabIndex(focusable, index);
  }

  /**
   * Iterator of tree items.
   */
  public Iterator<FastMSTreeItem> treeItemIterator() {
    ArrayList<FastMSTreeItem> accum = new ArrayList<FastMSTreeItem>();
    root.dumpTreeItems(accum);
    return accum.iterator();
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    DOM.setEventListener(focusable, this);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    DOM.setEventListener(focusable, null);
  }

  public FastMSTreeItem getRoot() {
    return root;
  }

//  protected void keyboardNavigation(Event event) {
//    // If nothing's selected, select the first item.
//    if (curSelection == null) {
//      if (root.getChildCount() > 0) {
//        onSelection(root.getChild(0), true, true);
//      }
//      super.onBrowserEvent(event);
//    } else {
//
//      // Handle keyboard events if keyboard navigation is enabled
//
//      switch (DOMHelper.standardizeKeycode(DOM.eventGetKeyCode(event))) {
////        case KeyboardListener.KEY_UP: {
////          moveSelectionUp(curSelection);
////          break;
////        }
////        case KeyboardListener.KEY_DOWN: {
////          moveSelectionDown(curSelection, true);
////          break;
////        }
////        case KeyboardListener.KEY_LEFT: {
////          if (curSelection.isOpen()) {
////            curSelection.setState(false);
////          } else {
////            FastMSTreeItem parent = curSelection.getParentItem();
////            if (parent != null) {
////              setSelectedItems(parent);
////            }
////          }
////          break;
////        }
////        case KeyboardListener.KEY_RIGHT: {
////          if (!curSelection.isOpen()) {
////            curSelection.setState(true);
////          }
////          // Do nothing if the element is already open.
////          break;
////        }
//      }
//    }
//  }

  /**
   * Moves the selection bar around the given {@link FastMSTreeItem}.
   * 
   * @param item the item to move selection bar to
   */
  protected void moveSelectionBar(FastMSTreeItem item) {
	  if (item == null || item.isShowing() == false) {
		  UIObject.setVisible(focusable, false);
		  return;
	  }
	  // focusable is being used for highlight as well.
	  // Get the location and size of the given item's content element relative
	  // to the tree.
	  Element selectedElem = item.getContentElem();
	  moveElementOverTarget(focusable, selectedElem);
	  UIObject.setVisible(focusable, true);
  }

  @Override
  protected void onLoad() {
    if (!getSelectedItems().isEmpty()) {
      moveSelectionBar(getSelectedItems().iterator().next());
    }
  }

  private final boolean addAllItems(FastMSTreeItem from, FastMSTreeItem to, LinkedHashSet <FastMSTreeItem> items) {
	  if (from == null || to == null) {
		  return false;
	  }
	  items.add(from);
	  if (from.equals(to)) {
		  return true;
	  }
	  if (childSelect) {
		  if (from.getChildren() != null && from.isOpen()) {
			  for (FastMSTreeItem kid: from.getChildren()) {			  
				  if (addAllItems(kid, to, items)) {
					  return true;
				  }
			  }
		  }
	  } else {
		  if (from.getChildren() != null && from.isOpen()) {
			  for (FastMSTreeItem kid: from.getChildren()) {			  
				  if (checkAbortCondition(kid, to)) {
					  return true;
				  }
			  }
		  }		  
	  }
	  return false;
  }
  
  private final boolean checkAbortCondition(FastMSTreeItem from, FastMSTreeItem to) {
	  if (from == null || to == null) {
		  return false;
	  }
	  if (from.equals(to)) {
		  return true;
	  }
	  if (from.getChildren() != null && from.isOpen()) {
		  for (FastMSTreeItem kid: from.getChildren()) {			  
			  if (checkAbortCondition(kid, to)) {
				  return true;
			  }
		  }
	  }
	  return false;
  }

  private final boolean collectAllItems(FastMSTreeItem from, FastMSTreeItem to, LinkedHashSet <FastMSTreeItem> items) {
	  if (from == null || to == null) {
		  return false;
	  }
	  items.add(from);
	  if (from.equals(to)) {
		  return true;
	  }
	  if (childSelect) {
		  if (from.getChildren() != null && from.isOpen()) {
			  for (FastMSTreeItem kid: from.getChildren()) {
				  if (collectAllItems(kid, to, items)) {
					  return true;
				  }
			  }
		  }
	  } else {
		  if (from.getChildren() != null && from.isOpen()) {
			  for (FastMSTreeItem kid: from.getChildren()) {
				  if (checkAbortCondition(kid, to)) {
					  return true;
				  }
			  }
		  }		  
	  }
	  while (from.getParentItem() != null) {
		  int index = from.getParentItem().getChildIndex(from);
		  index++;
		  if (index < from.getParentItem().getChildCount()) {
			  for (; index < from.getParentItem().getChildCount(); index++) {
				  if (addAllItems(from.getParentItem().getChild(index), to, items)) {
					  return true;
				  }
			  }
		  }
		  from = from.getParentItem();
	  }
	  int index = from.getTree().getChildIndex(from);
	  if (index != -1) {
		  index++;
		  if (index < from.getTree().getChildCount()) {
			  for (; index < from.getTree().getChildCount(); index++) {
				  if (addAllItems(from.getTree().getChild(index), to, items)) {
					  return true;
				  }
			  }
		  }
	  }
	  return false;
  }
  
  public void onDeselection(FastMSTreeItem item) {
	  if (item == root) {
		  return;
	  }
	  if (curSelection.isEmpty()) {
		  return;
	  }
  	  if (!curSelection.contains(item)) {
  		  return;
  	  }
	  curSelection.remove(item);
	  item.setSelection(false, false);
//	  moveSelectionBar(item);
//	  moveFocus(item);	  
  }
  
  public void selectTheseItems(LinkedHashSet <FastMSTreeItem> items) {
  	if (items == null || items.size() == 0) {
  		return;
  	}
  	FastMSTreeItem item = null;
	for (FastMSTreeItem it: items) {
		curSelection.add(it);
        // Select the item and fire the selection event.
        it.setSelection(true, false);
        item = it;
	}    	
//  	FastMSTreeItem item = items.get(items.size() - 1);
  	moveSelectionBar(item);
	lastSelectedItem = item;
	fireSelectionNumberChanged();
  }
    
  public void deselectTheseItems(LinkedHashSet <FastMSTreeItem> items) {
	  	if (items == null || items.size() == 0) {
	  		return;
	  	}
	  	FastMSTreeItem item = null;
	  	for (FastMSTreeItem it: items) {
			curSelection.remove(it);
	        // Select the item and fire the selection event.
	        it.setSelection(false, false);
	        item = it;
		}
	  	moveSelectionBar(item);
	  	fireSelectionNumberChanged();
  }

  public void setMaySelectChildren(boolean childSelect) {
	  this.childSelect = childSelect;
  }
  
  private final boolean checkMaySelect(FastMSTreeItem item) {
	  if (childSelect) {
		  return true;
	  }
	  FastMSTreeItem parent = item.getParentItem();
	  while (parent != null) {
		  if (curSelection.contains(parent)) {
			  return false;
		  }
		  parent = parent.getParentItem();
	  }
	  return true;
  }
  
  private final LinkedHashSet <FastMSTreeItem> checkDeselectionNecessary(FastMSTreeItem item) {	  
	  if (childSelect) {
		  return null;
	  }
	  LinkedHashSet <FastMSTreeItem> newSel = new LinkedHashSet<FastMSTreeItem>();
	  Iterator <FastMSTreeItem> itemIter = curSelection.iterator();
	  while (itemIter.hasNext()) {
		  FastMSTreeItem it = itemIter.next();
		  FastMSTreeItem parent = it.getParentItem();
		  while (parent != null) {
			  if (parent.equals(item)) {
				  newSel.add(it);
				  break;
			  }
			  parent = parent.getParentItem();
		  }
	  }
	  return newSel.size() == 0 ? null : newSel;
  }
  
  public void onSelection(FastMSTreeItem item, boolean fireEvents,
      boolean moveFocus, int mode) {
	  try {
    // 'root' isn't a real item, so don't let it be selected
    // (some cases in the keyboard handler will try to do this)
    if (item == root) {
      return;
    }

//    if (curSelection == item) {
//      return;
//    }
    if (!curSelection.isEmpty()) {
      if (mode == SELECT || !isMultiSelect) {
    	  Iterator <FastMSTreeItem> iterate = curSelection.iterator();
    	  while (iterate.hasNext()) {
    		FastMSTreeItem it = iterate.next();
    		if (!it.beforeSelectionLost()) {
    			continue;
    		}
    		it.setSelection(false, fireEvents);
    		iterate.remove();
    	}
      }
    }
    
    if (!isMultiSelect) {
    	mode = SELECT;
    }
    
    if (isMultiSelect && item != null && mode == INTERVAL && lastSelectedItem != null && lastSelectedItem.isSelected()) {
    	// Get all items between item and lastSelectedItem (inclusive)
    	if (item.equals(lastSelectedItem)) {
    		fireSelectionNumberChanged();
    		return;
    	}
    	FastMSTreeItem from;
    	FastMSTreeItem to;
    	if (lastSelectedItem.getElement().getAbsoluteTop() < item.getElement().getAbsoluteTop()) {
    		// Travel down from lastSelectedItem
    		from = lastSelectedItem;
    		to = item;
    	} else {
    		// Travel down from currentItem
    		from = item;
    		to = lastSelectedItem;
    	}
    	
    	LinkedHashSet <FastMSTreeItem> interval = new LinkedHashSet<FastMSTreeItem>();
    	collectAllItems(from, to, interval);
    	for (FastMSTreeItem it: interval) {
    		curSelection.add(it);
            // Select the item and fire the selection event.
            it.setSelection(true, false);                      	
    	}
       	if (moveFocus) {
       		moveFocus(item);
        } else {
             moveSelectionBar(item);
        }
    	lastSelectedItem = item;
    	fireSelectionNumberChanged();
    	return;
    }
    if (item != null) {
    	lastSelectedItem = item;
    }    
    if (item != null && !curSelection.contains(item)) {
    	if (!checkMaySelect(item)) {
    		return;
    	}
    	LinkedHashSet <FastMSTreeItem> newSel = checkDeselectionNecessary(item);
        if (newSel != null) {
        	deselectTheseItems(newSel);
        }
        curSelection.add(item);
        if (!curSelection.isEmpty()) {
        	if (moveFocus) {
              moveFocus(item);
            } else {
              // Move highlight even if we do no not need to move focus.
              moveSelectionBar(item);
            }

            // Select the item and fire the selection event.
            item.setSelection(true, fireEvents);
          }        
    } else if (item != null && mode == ADD && isMultiSelect) {
    	curSelection.remove(item);
    	item.setSelection(false, fireEvents);
        if (moveFocus) {
            moveFocus(item);
          } else {
            // Move highlight even if we do no not need to move focus.
            moveSelectionBar(item);
          }   
    }    
	  } catch (Throwable t) {
		  t.printStackTrace();
	  }
	  fireSelectionNumberChanged();
  }

  /**
   * This method is called immediately before a widget will be detached from the
   * browser's document.
   */
  @Override
  protected void onUnload() {
  }

  /**
   * This is called when a valid selectable element is clicked in the tree.
   * Subclasses can override this method to decide whether or not FastTree
   * should keep processing the element clicked. For example, a subclass may
   * decide to return false for this method if selecting a new item in the tree
   * is subject to asynchronous approval from other components of the
   * application.
   * 
   * @returns true if element should be processed normally, false otherwise.
   *          Default returns true.
   */
  protected boolean processElementClicked(FastMSTreeItem item) {
    return true;
  }

  void adopt(Widget widget, FastMSTreeItem treeItem) {
    assert (!childWidgets.containsKey(widget));
    childWidgets.put(widget, treeItem);
    super.adopt(widget);
  }

  /*
   * This method exists solely to support unit tests.
   */
  HashMap<Widget, FastMSTreeItem> getChildWidgets() {
    return childWidgets;
  }

  void treeOrphan(Widget widget) {
    super.orphan(widget);

    // Logical detach.
    childWidgets.remove(widget);
  }

  private void clickedOnFocus(Element e) {
    // An element was clicked on that is not focusable, so we use the hidden
    // focusable to not shift focus.
    moveElementOverTarget(focusable, e);
    impl.focus(focusable);
  }

  /**
   * Collects parents going up the element tree, terminated at the tree root.
   */
  private void collectElementChain(ArrayList<Element> chain, Element hRoot,
      Element hElem) {
    if ((hElem == null) || hElem.equals(hRoot)) {
      return;
    }

    collectElementChain(chain, hRoot, DOM.getParent(hElem));
    chain.add(hElem);
  }

  private Element createFocusElement() {
    Element e = impl.createFocusable();
    DOM.setStyleAttribute(e, "position", "absolute");
    DOM.appendChild(getElement(), e);
    DOM.sinkEvents(e, Event.FOCUSEVENTS | Event.ONMOUSEDOWN);
    // Needed for IE only
    DOM.setElementAttribute(e, "focus", "false");
    return e;
  }

  /**
   * Disables the selection text on IE.
   */
  private native void disableSelection(Element element)
  /*-{
    element.onselectstart = function() {
      return false;
    };
  }-*/;

  private void elementClicked(FastMSTreeItem root, Event event) {
    Element target = DOM.eventGetTarget(event);
    ArrayList<Element> chain = new ArrayList<Element>();
    collectElementChain(chain, getElement(), target);
    FastMSTreeItem item = findItemByChain(chain, 0, root);
    if (item != null) {
      if (item.isInteriorNode() && item.getControlElement().equals(target)) {
    	  item.getTree().setListenToStateChange(true);
    	  item.setState(!item.isOpen(), true);
    	  item.getTree().setListenToStateChange(false);
        moveSelectionBar(item);
        disableSelection(target);
      } else if (processElementClicked(item)) {
    	  if (event.getCtrlKey()) {
    		onSelection(item, true, !shouldTreeDelegateFocusToElement(target), ADD);
    		disableSelection(target);
    	  } else if (event.getShiftKey()) {
    		  onSelection(item, true, !shouldTreeDelegateFocusToElement(target), INTERVAL);
    		  disableSelection(target);
    	  } else {
    		  onSelection(item, true, !shouldTreeDelegateFocusToElement(target), SELECT);
    		  disableSelection(target);
    	  }
      }
    }
  }

  private FastMSTreeItem findDeepestOpenChild(FastMSTreeItem item) {
    if (!item.isOpen()) {
      return item;
    }
    return findDeepestOpenChild(item.getChild(item.getChildCount() - 1));
  }

  private FastMSTreeItem findItemByChain(ArrayList<Element> chain, int idx,
      FastMSTreeItem root) {
    if (idx == chain.size()) {
      return root;
    }

    Element hCurElem = chain.get(idx);
    for (int i = 0, n = root.getChildCount(); i < n; ++i) {
      FastMSTreeItem child = root.getChild(i);
      if (child.getElement().equals(hCurElem)) {
        FastMSTreeItem retItem = findItemByChain(chain, idx + 1, root.getChild(i));
        if (retItem == null) {
          return child;
        }
        return retItem;
      }
    }

    return findItemByChain(chain, idx + 1, root);
  }

  private void moveElementOverTarget(Element movable, Element target) {
    int containerTop = getAbsoluteTop();

    int top = DOM.getAbsoluteTop(target) - containerTop;
    int height = DOM.getElementPropertyInt(target, "offsetHeight");

    // Set the element's position and size to exactly underlap the
    // item's content element.

    DOM.setStyleAttribute(movable, "height", height + "px");
    DOM.setStyleAttribute(movable, "top", top + "px");
  }

  /**
   * Move the tree focus to the specified selected item.
   * 
   * @param selection
   */
  private void moveFocus(FastMSTreeItem item) {
	moveSelectionBar(item);
   	DOM.scrollIntoView(focusable);
   	HasFocus focusableWidget = item.getFocusableWidget();
   	if (focusableWidget != null) {
   		focusableWidget.setFocus(true);
   	} else {
   		// Ensure Focus is set, as focus may have been previously delegated by
   		// tree.
   		impl.focus(focusable);
   	}
  }

  public void moveUnselectedFocus(FastMSTreeItem item) {
	   	DOM.scrollIntoView(focusable);
	   	HasFocus focusableWidget = item.getFocusableWidget();
	   	if (focusableWidget != null) {
	   		focusableWidget.setFocus(true);
	   	} else {
	   		// Ensure Focus is set, as focus may have been previously delegated by
	   		// tree.
	   		impl.focus(focusable);
	   	}
	  }

  /**
   * Moves to the next item, going into children as if dig is enabled.
   */
//  private void moveSelectionDown(FastMSTreeItem sel, boolean dig) {
//    if (sel == root) {
//      return;
//    }
//    FastMSTreeItem parent = sel.getParentItem();
//    if (parent == null) {
//      parent = root;
//    }
//    int idx = parent.getChildIndex(sel);
//
//    if (!dig || !sel.isOpen()) {
//      if (idx < parent.getChildCount() - 1) {
//        onSelection(parent.getChild(idx + 1), true, true);
//      } else {
//        moveSelectionDown(parent, false);
//      }
//    } else if (sel.getChildCount() > 0) {
//      onSelection(sel.getChild(0), true, true);
//    }
//  }

  /**
   * Moves the selected item up one.
   */
//  private void moveSelectionUp(List <FastMSTreeItem> sel) {
//    FastMSTreeItem parent = sel.getParentItem();
//    if (parent == null) {
//      parent = root;
//    }
//    int idx = parent.getChildIndex(sel);
//
//    if (idx > 0) {
//      FastMSTreeItem sibling = parent.getChild(idx - 1);
//      onSelection(findDeepestOpenChild(sibling), true, true);
//    } else {
//      onSelection(parent, true, true);
//    }
//  }

  private native boolean shouldTreeDelegateFocusToElement(Element elem)
  /*-{
    var name = elem.nodeName;
    return ((name == "SELECT") ||
       (name == "INPUT")  ||
       (name == "TEXTAREA") ||
       (name == "OPTION") ||
       (name == "BUTTON") ||
       (name == "LABEL") 
    );
  }-*/;
  
  public void addLoadListener(LoadListener listener) {
	  if (firingLoadedEvent) {
		  toBeAdded.add(listener);
		  return;
	  }
	  loadListeners.add(listener);
  }  
  
  public void removeLoadListener(LoadListener listener) {
	  if (firingLoadedEvent) {
		  toBeRemoved.add(listener);
		  return;
	  }
	  loadListeners.remove(listener);
  }
  
  public void loaded(LoadEvent le) {
	  try {
		  firingLoadedEvent = true;
		  for (LoadListener ll: loadListeners) {
			  ll.loaderLoad(le);
		  }		  
		  loadListeners.removeAll(toBeRemoved);
		  loadListeners.addAll(toBeAdded);
		  toBeRemoved.clear();
		  toBeAdded.clear();
	  } finally {
		  firingLoadedEvent = false;
	  }
  }
  
  private final void traverse(FastMSTreeItem item, ArrayList <FastMSTreeItem> result) {
	  if (item == null) {
		  return;
	  }
	  result.add(item);
	  if (item.isOpen()) {
		  for (FastMSTreeItem kid: item.getChildren()) {
			  traverse(kid, result);
		  }
	  }
  }
  
  public ArrayList <FastMSTreeItem> getVisibleItems() {
	  ArrayList <FastMSTreeItem> result = new ArrayList<FastMSTreeItem>();
	  for (FastMSTreeItem kid: root.getChildren()) {
		  traverse(kid, result);
	  }
	  return result;
  }
  
  private final FastMSTreeItem find(FastMSTreeItem item, TreeNode node) {
	  if (node.equals(item.getModel())) {
		  return item;
	  }
	  for (FastMSTreeItem kid: item.getChildren()) {
		  FastMSTreeItem r = find(kid, node);
		  if (r != null) {
			  return r;
		  }
	  }
	  return null;
  }
  
//  public FastMSTreeItem find(TreeNode node) {
//	  for (FastMSTreeItem kid: root.getChildren()) {
//		  FastMSTreeItem r = find(kid, node);
//		  if (r != null) {
//			  return r;
//		  }
//	  }
//	  return null;
//  }
  
 }

/**
 * A collection of convenience factories for creating iterators for widgets.
 * This mostly helps developers support {@link HasWidgets} without having to
 * implement their own {@link Iterator}.
 */
class WidgetIterators {

  /**
   * Wraps an array of widgets to be returned during iteration.
   * <code>null</code> is allowed in the array and will be skipped during
   * iteration.
   * 
   * @param container the container of the widgets in <code>contained</code>
   * @param contained the array of widgets
   * @return the iterator
   */
  static final Iterator<Widget> createWidgetIterator(
      final HasWidgets container, final Widget[] contained) {
    return new Iterator<Widget>() {
      int index = -1, last = -1;
      boolean widgetsWasCopied = false;
      Widget[] widgets = contained;

      {
        gotoNextIndex();
      }

      public boolean hasNext() {
        return (index < contained.length);
      }

      public Widget next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        last = index;
        final Widget w = contained[index];
        gotoNextIndex();
        return w;
      }

      public void remove() {
        if (last < 0) {
          throw new IllegalStateException();
        }

        if (!widgetsWasCopied) {
          widgets = copyWidgetArray(widgets);
          widgetsWasCopied = true;
        }

        container.remove(contained[last]);
        last = -1;
      }

      private void gotoNextIndex() {
        ++index;
        while (index < contained.length) {
          if (contained[index] != null) {
            return;
          }
          ++index;
        }
      }
    };
  }

  private static Widget[] copyWidgetArray(final Widget[] widgets) {
    final Widget[] clone = new Widget[widgets.length];
    for (int i = 0; i < widgets.length; i++) {
      clone[i] = widgets[i];
    }
    return clone;
  }

  private WidgetIterators() {
    // Not instantiable.
  }
}
