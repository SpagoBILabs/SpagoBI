/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */
package com.tensegrity.wpalo.client.ui.mvc.fasttree;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.DecoratedFastTreeItem;
import com.google.gwt.widgetideas.client.overrides.DOMHelper;
import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
import com.tensegrity.wpalo.client.ui.model.TreeNode;
import com.tensegrity.wpalo.client.ui.model.XObjectModel;
import com.tensegrity.wpalo.client.ui.mvc.cubeview.FullHierarchyTree;

/**
 * An item that can be contained within a
 * {@link com.google.gwt.widgetideas.client.FastTree}.
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.TreeExample}
 */

public class FastMSTreeItem extends UIObject implements HasHTML, HasFastMSTreeItems {
  private static final String STYLENAME_SELECTED = "selected";

  // TODO(ECC) change states to enums and move style names to FastTree where
  // they below.
  private static final int TREE_NODE_LEAF = 1;
  private static final int TREE_NODE_INTERIOR_NEVER_OPENED = 2;
  private static final int TREE_NODE_INTERIOR_OPEN = 3;
  private static final int TREE_NODE_INTERIOR_CLOSED = 4;
  private static final String STYLENAME_CHILDREN = "children";
  private static final String STYLENAME_LEAF_DEFAULT = "gwt-FastTreeItem gwt-FastTreeItem-leaf";
  private static final String STYLENAME_OPEN = "open";
  private static final String STYLENAME_CLOSED = "closed";
  private static final String STYLENAME_LEAF = "leaf";

  private static final String STYLENAME_CONTENT = "treeItemContent";
  /**
   * The base tree item element that will be cloned.
   */
  private static Element TREE_LEAF;

  /**
   * Static constructor to set up clonable elements.
   */
  static {
    if (GWT.isClient()) {
      // Create the base element that will be cloned
      TREE_LEAF = DOM.createDiv();
      // leaf contents.
      setStyleName(TREE_LEAF, STYLENAME_LEAF_DEFAULT);
      Element content = DOM.createDiv();
      setStyleName(content, STYLENAME_CONTENT);
      DOM.appendChild(TREE_LEAF, content);
    }
  }

  private int state = TREE_NODE_LEAF;
  private int depth;
  private ArrayList<FastMSTreeItem> children;
  Element contentElem, childElems;
  private FastMSTreeItem parent;
  private FastMSTree tree;
  private Widget widget;
  private TreeNode model;
  private XObjectModel xObjModel;
  private XElementNode node;
  private AsyncCallback <Void> finishHandler;
  private HTML htmlWidget = null;
  
  /**
   * Creates an empty tree item.
   */
  public FastMSTreeItem() {
    Element elem = createLeafElement();
    setElement(elem);
    depth = 0;
  }

  public HTML getHtml() {
	  if (htmlWidget == null) {
		  htmlWidget = new HTML(DOM.getInnerHTML(getElementToAttach()));
	  }
	  return htmlWidget;
  }
  
  public void setFinishHandler(AsyncCallback <Void> finishHandler) {
	  this.finishHandler = finishHandler;
  }
  
  public AsyncCallback <Void> getFinishHandler() {
	  return this.finishHandler;
  }
  
  public void setModel(TreeNode node) {
	  this.model = node;
	  node.setItem(this);
  }
  
  public TreeNode getModel() {
	  return model;
  }
  
  public void setXObjectModel(XObjectModel model) {
	  xObjModel = model;
	  model.setItem(this);
  }
  
  public XObjectModel getXObjectModel() {
	  return xObjModel;
  }
  
  /**
   * Constructs a tree item with the given HTML.
   * 
   * @param html the item's HTML
   */
  public FastMSTreeItem(String html) {
    this();
    DOM.setInnerHTML(getElementToAttach(), html);
  }

  /**
   * Constructs a tree item with the given <code>Widget</code>.
   * 
   * @param widget the item's widget
   */
  public FastMSTreeItem(Widget widget) {
    this();
    addWidget(widget);
  }

  /**
   * This constructor is only for use by {@link DecoratedFastTreeItem}.
   * 
   * @param element element
   */
  FastMSTreeItem(Element element) {
    setElement(element);
  }

  public int getDepth() {
	  return depth;
  }
  
	public void fastAddItem(FastMSTreeItem item) {
		item.depth = depth + 1;
		if (isLeafNode()) {
			becomeInteriorNode();
		}
		if (children == null) {
			children = new ArrayList<FastMSTreeItem>();
		}
		item.setParentItem(this);
		children.add(item);

		item.tree = tree;
	}
  
  public void addItem(FastMSTreeItem item) {
    // Detach item from existing parent.
    item.depth = depth + 1;
	if ((item.getParentItem() != null) || (item.getTree() != null)) {
      item.remove();
    }
    if (isLeafNode()) {
      boolean sel = isSelected();
      if (sel) {
    	  setSelection(false, false);
      }
      becomeInteriorNode();      
      if (sel) {
    	  setSelection(true, false);
      }
    }
    if (children == null) {
      // Never had children.
      children = new ArrayList<FastMSTreeItem>();
    }
    // Logical attach.
    item.setParentItem(this);
    children.add(item);

    // Physical attach.
    if (state != TREE_NODE_INTERIOR_NEVER_OPENED) {
      DOM.appendChild(childElems, item.getElement());
    }

    // Adopt.
    if (tree != null) {
      item.setTree(tree);
    }
  }
  
  public boolean moveItemUp(FastMSTreeItem item) {
	int index = children.indexOf(item);
	if (index < 1) {
		return false;
	}
	children.remove(index);
	index--;
	children.add(index, item);
	Element cElem = item.getElement();
	index = DOM.getChildIndex(childElems, cElem);
	DOM.removeChild(childElems, cElem);
	index--;
	DOM.insertChild(childElems, cElem, index);
	return true;
  }

  public boolean moveItemDown(FastMSTreeItem item) {
		int index = children.indexOf(item);
		if (index == -1 || index > children.size() - 2) {
			return false;
		}
		children.remove(index);
		index++;
		children.add(index, item);
		Element cElem = item.getElement();
		index = DOM.getChildIndex(childElems, cElem);
		DOM.removeChild(childElems, cElem);
		index++;
		DOM.insertChild(childElems, cElem, index);
		return true;
  }
  
  public FastMSTreeItem addItem(String itemText) {
    FastMSTreeItem ret = new FastMSTreeItem(itemText);
    addItem(ret);
    return ret;
  }

  public FastMSTreeItem addItem(Widget widget) {
    FastMSTreeItem ret = new FastMSTreeItem(widget);
    addItem(ret);
    return ret;
  }

  /**
   * Become an interior node.
   */
  public void becomeInteriorNode() {
    if (!isInteriorNode()) {
      state = TREE_NODE_INTERIOR_NEVER_OPENED;

      Element control = DOM.createDiv();
      setStyleName(control, STYLENAME_CLOSED);
      DOM.appendChild(control, contentElem);
      convertElementToInteriorNode(control);
    }
  }

  public FastMSTreeItem getChild(int index) {
    if ((index < 0) || (index >= getChildCount())) {
      throw new IndexOutOfBoundsException("No child at index " + index);
    }
    return children.get(index);
  }

  public int getChildCount() {
    if (children == null) {
      return 0;
    }
    return children.size();
  }

  public int getChildIndex(FastMSTreeItem child) {
    if (children == null) {
      return -1;
    }
    return children.indexOf(child);
  }

  /**
   * Returns the width of the control open/close image. Must be overridden if
   * the TreeItem is using a control image that is <i>not</i> 16 pixels wide.
   * 
   * @return the width of the control image
   */
  public int getControlImageWidth() {
    return 16;
  }

  public String getHTML() {
    return DOM.getInnerHTML(getElementToAttach());
  }

  /**
   * Gets this item's parent.
   * 
   * @return the parent item
   */
  public FastMSTreeItem getParentItem() {
    return parent;
  }

  public String getText() {
    return DOM.getInnerText(getElementToAttach());
  }

  /**
   * Gets the tree that contains this item.
   * 
   * @return the containing tree
   */
  public final FastMSTree getTree() {
    return tree;
  }

  /**
   * Gets the <code>Widget</code> associated with this tree item.
   */
  public Widget getWidget() {
    return widget;
  }

  /**
   * Has this {@link FastMSTreeItem} ever been opened?
   * 
   * @return whether the {@link FastMSTreeItem} has ever been opened.
   */
  public boolean hasBeenOpened() {
    return state > TREE_NODE_INTERIOR_NEVER_OPENED;
  }

  /**
   * Does this {@link FastMSTreeItem} represent an interior node?
   */
  public boolean isInteriorNode() {
    return state >= TREE_NODE_INTERIOR_NEVER_OPENED;
  }

  /**
   * Is this {@link FastMSTreeItem} a leaf node?
   */
  public boolean isLeafNode() {
    return state <= TREE_NODE_LEAF;
  }

  /**
   * Is the {@link FastMSTreeItem} open? Returns false if the {@link FastMSTreeItem}
   * is closed or a leaf node.
   */
  public boolean isOpen() {
    return state == TREE_NODE_INTERIOR_OPEN;
  }

  /**
   * Determines whether this item is currently selected.
   * 
   * @return <code>true</code> if it is selected
   */
  public boolean isSelected() {
    if (tree == null) {
      return false;
    } else {
      return tree.getSelectedItems().contains(this);
    }
  }

  /**
   * Returns whether the tree is currently showing this {@link FastMSTreeItem}.
   */
  public boolean isShowing() {
    if (tree == null || isVisible() == false) {
      return false;
    } else if (parent == null) {
      return true;
    } else if (!parent.isOpen()) {
      return false;
    } else {
      return parent.isShowing();
    }
  }

  /**
   * Removes this item from its tree.
   */
  public void remove() {
    if (parent != null) {
      // If this item has a parent, remove self from it.
      parent.removeItem(this);
    } else if (tree != null) {
      // If the item has no parent, but is in the Tree, it must be a top-level
      // element.
      tree.removeItem(this);
    }
  }

  public int getTotalSize() {
	  int result = getChildCount();
	  for (FastMSTreeItem it: getChildren()) {
		  result += it.getTotalSize();
	  }
	  return result;
  }

  /**
   * Removes an item from the tree. Note, tree items do not automatically become
   * a leaf node again if the last child is removed. (
   */
  public void removeItem(FastMSTreeItem item) {
    // Validate.
    if (children == null || !children.contains(item)) {
      return;
    }

    // Orphan.
    item.clearTree();

    // Physical detach.
    if (state != TREE_NODE_INTERIOR_NEVER_OPENED) {
      DOM.removeChild(childElems, item.getElement());
    }

    // Logical detach.
    item.setParentItem(null);
    children.remove(item);
  }

  /**
   * Removes all of this item's children.
   */
  public void removeItems() {
    while (getChildCount() > 0) {
      removeItem(getChild(0));
    }
  }

  public void setHTML(String html) {
    clearWidget();
    DOM.setInnerHTML(getElementToAttach(), html);
  }

  /**
   * Sets whether this item's children are displayed.
   * 
   * @param open whether the item is open
   */
  public final void setState(boolean open) {
    setState(open, true);
  }

  /**
   * Sets whether this item's children are displayed.
   * 
   * @param open whether the item is open
   * @param fireEvents <code>true</code> to allow open/close events to be fired
   */
  public void setState(boolean open, boolean fireEvents) {
    if (open == isOpen()) {
      return;
    }
    // Cannot open leaf nodes.
    if (isLeafNode()) {
      return;
    }
    if (open) {
      beforeOpen();
      if (state == TREE_NODE_INTERIOR_NEVER_OPENED) {
        ensureChildren();
        childElems = DOM.createDiv();
        UIObject.setStyleName(childElems, STYLENAME_CHILDREN);
        convertElementToHaveChildren(childElems);

        if (children != null) {
          for (FastMSTreeItem item : children) {
            DOM.appendChild(childElems, item.getElement());
          }
        }
      }

      state = TREE_NODE_INTERIOR_OPEN;
    } else {
      beforeClose();
      state = TREE_NODE_INTERIOR_CLOSED;
    }
    updateState();
    if (open) {
      afterOpen();
    } else {
      afterClose();
    }
  }

  public void expandAll() {
	  setState(true, false);
	  for (FastMSTreeItem item: getChildren()) {
//		  item.setState(true, false);
		  item.expandAll();
	  }
  }
  
  public void collapseAll() {
	  setState(false, false);
	  for (FastMSTreeItem item: getChildren()) {
//		  item.setState(false, false);
		  item.collapseAll();
	  }
  }

  public void setText(String text) {
    clearWidget();
    DOM.setInnerText(getElementToAttach(), text);
  }

  public void setWidget(Widget widget) {
    // Physical detach old from self.
    // Clear out any existing content before adding a widget.

    DOM.setInnerHTML(getElementToAttach(), "");
    clearWidget();
    addWidget(widget);
  }

  /**
   * Called after the tree item is closed.
   */
  protected void afterClose() {
  }

  /**
   * Called after the tree item is opened.
   */
  protected void afterOpen() {
  }

  /**
   * Called before the tree item is closed.
   */
  protected void beforeClose() {
  }

  /**
   * Called before the tree item is opened.
   */
  protected void beforeOpen() {
  }

  /**
   * Called when tree item is being unselected. Returning <code>false</code>
   * cancels the unselection.
   * 
   */
  protected boolean beforeSelectionLost() {
    return true;
  }

  /**
   * Fired when a tree item receives a request to open for the first time.
   * Should be overridden in child clases.
   */
  protected void ensureChildren() {
  }

  /**
   * Returns the widget, if any, that should be focused on if this TreeItem is
   * selected.
   * 
   * @return widget to be focused.
   */
  protected HasFocus getFocusableWidget() {
    Widget w = getWidget();
    if (w instanceof HasFocus) {
      return (HasFocus) w;
    } else {
      return null;
    }
  }

  /**
   * Called when a tree item is selected.
   * 
   */
  protected void onSelected() {
  }

  void clearTree() {
    if (tree != null) {
      if (widget != null) {
        tree.treeOrphan(widget);
      }
      if (tree.getSelectedItems().contains(this)) {
        tree.setSelectedItems(null);
      }
      tree = null;
      for (int i = 0, n = getChildCount(); i < n; ++i) {
        children.get(i).clearTree();
      }
    }
  }

  void convertElementToHaveChildren(Element children) {
    DOM.appendChild(getElement(), children);
  }

  void convertElementToInteriorNode(Element control) {
    setStyleName(getElement(), getStylePrimaryName() + "-leaf", false);
    DOM.appendChild(getElement(), control);
  }

  Element createLeafElement() {
    Element elem = DOMHelper.clone(TREE_LEAF, true);
    contentElem = DOMHelper.rawFirstChild(elem);
    return elem;
  }

  void dumpTreeItems(List<FastMSTreeItem> accum) {
    if (isInteriorNode() && getChildCount() > 0) {
      for (int i = 0; i < children.size(); i++) {
        FastMSTreeItem item = children.get(i);
        accum.add(item);
        item.dumpTreeItems(accum);
      }
    }
  }

  public ArrayList<FastMSTreeItem> getChildren() {
	  if (children == null) {
		  return new ArrayList<FastMSTreeItem>();
	  }
	  return children;
  }

  Element getContentElem() {
    return contentElem;
  }

  Element getControlElement() {
    return DOM.getParent(contentElem);
  }

  Element getElementToAttach() {
    return contentElem;
  }

  void setParentItem(FastMSTreeItem parent) {
    this.parent = parent;
  }

  public void setElementNode(XElementNode nd) {
	  this.node = nd;
  }
  
  public XElementNode getElementNode() {
	  return this.node;
  }
  /**
   * Selects or deselects this item.
   * 
   * @param selected <code>true</code> to select the item, <code>false</code> to
   *          deselect it
   */
  void setSelection(boolean selected, boolean fireEvents) {
    setStyleName(getControlElement(), STYLENAME_SELECTED, selected);
    if (selected && fireEvents) {
      onSelected();
    }
  }

  void setTree(FastMSTree newTree) {
    if (tree == newTree) {
      return;
    }

    // Early out.
    if (tree != null) {
      throw new IllegalStateException(
          "Each Tree Item must be removed from its current tree before being added to another.");
    }
    tree = newTree;

    if (widget != null) {
      // Add my widget to the new tree.
      tree.adopt(widget, this);
    }

    for (int i = 0, n = getChildCount(); i < n; ++i) {
      children.get(i).setTree(newTree);
    }
  }

  void updateState() {
    // No work to be done.
    if (isLeafNode()) {
      return;
    }
    if (isOpen()) {
      showOpenImage();
      UIObject.setVisible(childElems, true);
    } else {
      showClosedImage();
      UIObject.setVisible(childElems, false);
    }
  }

  /**
   * Adds a widget to an already empty {@link FastMSTreeItem}.
   */
  private void addWidget(Widget newWidget) {
    // Detach new child from old parent.
    if (newWidget != null) {
      newWidget.removeFromParent();
    }

    // Logical detach old/attach new.
    widget = newWidget;

    if (newWidget != null) {
      DOM.appendChild(getElementToAttach(), widget.getElement());
      bidiSupport();
      // Attach child to tree.
      if (tree != null) {
        tree.adopt(widget, this);
      }
    }
  }

  private void bidiSupport() {
  }

  private void clearWidget() {
    // Detach old child from tree.
    if (widget != null && tree != null) {
      tree.treeOrphan(widget);
      widget = null;
    }
  }

  private void showClosedImage() {
    setStyleName(getControlElement(), STYLENAME_OPEN, false);
    setStyleName(getControlElement(), STYLENAME_CLOSED, true);
  }

  private void showOpenImage() {
    setStyleName(getControlElement(), STYLENAME_CLOSED, false);
    setStyleName(getControlElement(), STYLENAME_OPEN, true);
  }
}
