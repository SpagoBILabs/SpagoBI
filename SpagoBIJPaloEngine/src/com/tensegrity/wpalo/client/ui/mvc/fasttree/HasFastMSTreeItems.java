/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.ui.mvc.fasttree;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * A widget that implements this interface contains {@link FastTreeItem}
 * children and can add and remove them.
 */
public interface HasFastMSTreeItems {

  /**
   * Adds another item as a child to this one.
   * 
   * @param item the item to be added
   */

  void addItem(FastMSTreeItem item);
  void fastAddItem(FastMSTreeItem item);

  /**
   * Adds a child tree item containing the specified text.
   * 
   * @param itemText the text to be added
   * @return the item that was added
   */
  FastMSTreeItem addItem(String itemText);

  /**
   * Adds a child tree item containing the specified widget.
   * 
   * @param widget the widget to be added
   * @return the item that was added
   */
  FastMSTreeItem addItem(Widget widget);

  /**
   * Gets the child at the specified index.
   * 
   * 
   * @param index the index to be retrieved
   * @return the item at that index
   */

  FastMSTreeItem getChild(int index);

  /**
   * Gets the number of children contained in this item.
   * 
   * @return this item's child count.
   */

  int getChildCount();

  /**
   * Gets the index of the specified child item.
   * 
   * @param child the child item to be found
   * @return the child's index, or <code>-1</code> if none is found
   */

  int getChildIndex(FastMSTreeItem child);

  /**
   * Removes one of this item's children.
   * 
   * @param item the item to be removed
   */

  void removeItem(FastMSTreeItem item);

}