/*
*
* @file Header.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: Header.java,v 1.22 2010/03/04 09:12:35 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.palotable.header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.widgets.client.separator.Separator;
import com.tensegrity.palo.gwt.widgets.client.util.Point;


public class Header extends AbsolutePanel {

	private final HeaderLayouter layouter;
	private final List<HeaderItem> roots = new ArrayList<HeaderItem>();
	private final List<Separator> separators = new ArrayList<Separator>();
	private Map<Integer, Integer> depths = new HashMap<Integer, Integer>();
		
	public Header(HeaderLayouter layouter) {
		this.layouter = layouter;
		this.layouter.register(this);
	}
		
	public final void add(HeaderItem item) {
		roots.add(item);
		adopt(item);
	}
	
	public final void addChild(HeaderItem child, HeaderItem father) {
		father.addChild(child);
		adopt(child);
	}

	protected void doAttachChildren() {		
	// Ensure that all child widgets are attached.
		for (Iterator<Widget> it = iterator(); it.hasNext();) {
			Widget child = it.next();
			if(child instanceof HeaderItem) {
				HeaderItem item = (HeaderItem) child;
				item.onAttach();
				layouter.initialize(item);
			}
		}
	}

	private final void adopt(HeaderItem item) {
		super.add(item, 0, 0);
		addMaxLevelDepth(item.getLevel(), item.getDepth());
		layouter.initialize(item);
		for(HeaderItem child : item.getChildren())
			adopt(child);
		for(HeaderItem root : item.getRootsInNextLevel())
			adopt(root);
	}
	
	public final List<HeaderItem> getItems() {
		return roots;
	}
	
	public final HeaderItem getFirstRoot() {
		if(roots.isEmpty())
			return null;
		return roots.get(0);
	}
		
	public final int getLevelCount() {
		return depths.size();
	}
	
	public final int getMaxLevelDepth(int lvl) {
		Integer depth = depths.get(lvl);
		return depth != null ? depth.intValue() : -1;
	}
	
	public final void addMaxLevelDepth(int lvl, int depth) {
		if(depth > getMaxLevelDepth(lvl))
			depths.put(lvl, depth);
	}
	
	public final Separator getSeparator(int forLevel) {
		if(forLevel > 0) forLevel--;
		if(separators.size() <= forLevel)
			return addSeparator();
		return separators.get(forLevel);
	}
	public final Point layout() {
		if (roots.isEmpty())
			return new Point(0, 0);
		//TODO determineLeafIndizes() is partly integrated in layouter.layout(), 
		//but REQUIRED FOR REVERSE handling (load and initial if reverse)
		determineLeafIndizes();	
		return layouter.layout();
	}
	
	private void determineLeafIndizes() {
		final int[] leaf = new int[] {0};
		ItemVisitor visitor = new ItemVisitor() {
			public boolean visit(HeaderItem item) {
				if(!item.hasRootsInNextLevel())
					item.setLeafIndex(leaf[0]++);
				return true;
			}
		};
		traverse(visitor);
	}

	public final HeaderItem find(XAxisItem item) {
		final String itemPath = item.getPath();
		final int itemIndex = item.index;
		final HeaderItem[] _item = new HeaderItem[1];
		ItemVisitor visitor = new ItemVisitor() {
			public boolean visit(HeaderItem item) {
				XAxisItem model = item.getModel();
				if(model.getPath().equals(itemPath)) {
					boolean foundIt = true;
					//check optional index:
					if(itemIndex != -1 || model.index != -1)
						foundIt = itemIndex == model.index;
					if(foundIt) {
						_item[0] = item;
						return false;
					}
				}
				return true;
			}
		};
		traverse(visitor);
		return _item[0];
	}

	public final void reset() {
		//clear all items:
		for(HeaderItem root : roots)
			clear(root);
		roots.clear();
		depths.clear();
		layouter.reset();
		for(Separator sep : separators) {
			remove(sep);
			sep.removeFromParent();
		}
		separators.clear();
	}

	public final void initWithCurrentState() {
		final List<HeaderItem> itemsToRemove = new ArrayList<HeaderItem>();
		ItemVisitor visitor = new ItemVisitor() {
			public boolean visit(HeaderItem item) {
				//if item is not visible we remove it:
				if(!item.isVisible()) {
					itemsToRemove.add(item);
				} else {
					if(item.isExpanded()) {
						item.setIsLoaded(true);
					} else {
						item.setIsLoaded(false);
						item.getChildren().clear();
					}
				}
				return true;
			}
		};
		traverse(visitor);
		for(HeaderItem item : itemsToRemove) {
			item.removeFromParent();
			item.clear();
		}
	}
	private final void clear(HeaderItem item) {
		for(HeaderItem nxtLvlRoot : item.getRootsInNextLevel())
			clear(nxtLvlRoot);
		for(HeaderItem child : item.getChildren())
			clear(child);
		item.clear();
		remove(item);
	}
	
	public final void traverse(ItemVisitor visitor) {
		for(HeaderItem item : getItems())
			traverse(item, visitor);
	}
	
	private final void traverse(HeaderItem item, ItemVisitor visitor) {
		if(visitor.visit(item)) {
			if(item.hasRootsInNextLevel()) {
				for(HeaderItem root : item.getRootsInNextLevel())
					traverse(root, visitor);
			}
			if(item.hasChildren()) {
				for(HeaderItem child : item.getChildren())
					traverse(child, visitor);
			}
		}
	}
	
	public final void traverseVisible(ItemVisitor visitor) {
		for(HeaderItem item : getItems())
			traverseVisibleItems(item, visitor);
	}
	private final void traverseVisibleItems(HeaderItem item, ItemVisitor visitor) {
		if(visitor.visit(item)) {
			if(item.hasChildren() && item.isExpanded()) {
				for(HeaderItem child : item.getChildren())
					traverseVisibleItems(child, visitor);
			}
		}
		if(item.hasRootsInNextLevel()) {
			for(HeaderItem root : item.getRootsInNextLevel())
				traverseVisibleItems(root, visitor);
		}

	}
	
	private final Separator addSeparator() {
		Separator separator = layouter.createSeparator();
		super.add(separator);
		separators.add(separator);
		return separator;
	}
}
