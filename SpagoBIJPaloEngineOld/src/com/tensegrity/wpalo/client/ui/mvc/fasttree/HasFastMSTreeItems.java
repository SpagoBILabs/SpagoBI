/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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