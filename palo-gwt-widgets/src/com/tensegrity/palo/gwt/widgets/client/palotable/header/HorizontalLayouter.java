/*
*
* @file HorizontalLayouter.java
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
* @version $Id: HorizontalLayouter.java,v 1.41 2010/03/11 10:42:18 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.palotable.header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tensegrity.palo.gwt.widgets.client.palotable.Content;
import com.tensegrity.palo.gwt.widgets.client.palotable.LeafListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.levelselector.LevelSelector;
import com.tensegrity.palo.gwt.widgets.client.separator.HorizontalSeparator;
import com.tensegrity.palo.gwt.widgets.client.separator.Separator;
import com.tensegrity.palo.gwt.widgets.client.util.Point;
import com.tensegrity.palo.gwt.widgets.client.util.Ruler;

public class HorizontalLayouter implements HeaderLayouter {

	private static final String STYLE_HEADER_END = "columns-end";
	//TODO remove it
	protected static final int SEPARATOR_HEIGHT = 2;
	protected final Point size;
	protected int leaf;
	protected Header header;
	protected LeafListener leafListener;
	protected LevelSelector selector;
	
	protected int INDENT = 8; //seems to be wanted...
	
	private boolean reverse;
	private final Map<Integer, Integer> maxHeight = new HashMap<Integer, Integer>();

	
	public HorizontalLayouter() {
		size = new Point();
	}
	
	public final void register(Header header) {
		this.header = header;
		this.header.addStyleName(STYLE_HEADER_END);
	}
	public final void register(LeafListener listener) {
		leafListener = listener;
	}
	public final void register(LevelSelector selector) {
		this.selector = selector;
	}
	
	public final Separator createSeparator() {
		return new HorizontalSeparator();
	}
	
	public final void reverse(boolean doIt) {
		reverse = doIt;
	}
	public final boolean isReverse() {
		return reverse;
	}
	
	public void initialize(HeaderItem item) {
		int depth = item.getDepth() + 1;
		setMaxLevelHeight(item.getLevel(), 2 + INDENT + (INDENT * depth));
	}

	public final void reset() {
		size.x = 0;
		size.y = 0;
		maxHeight.clear();
	}
	
	public Point layout() {	
		leaf = 0;
		recalculateMaxHeightPerLevel();
		setMinimumSize();
		leafListener.reset(header);
		int borderOffset = 1 - Ruler.getBorderOffset();
		if(reverse)
			doReverseLayout(borderOffset);
		else
			doNormalLayout(borderOffset);		
		drawSeparatorsAndSelector();
		Ruler.setSize(header, size.x, size.y);
		return size;
	}

	private void recalculateMaxHeightPerLevel() {
		reset();
		header.traverseVisible(new ItemVisitor() {
			public boolean visit(HeaderItem item) {
				initialize(item);
				return true;
			}
		});
	}
	private void doReverseLayout(int borderOffset) {
		size.x += borderOffset;
		for (HeaderItem item : header.getItems())
			size.x = reverseLayout(item, size.x,  0, true, 0); //item.isVisible(), 0);
	}
	private void doNormalLayout(int borderOffset) {
		for (HeaderItem item : header.getItems())
			size.x = layout(item, size.x, 0, true, 0); //item.isVisible() , 0);
		size.x += borderOffset;
	}
	
	/** works only after layout */
	public final List<Integer> getHierarchyGaps() {
		List<Integer> gaps = new ArrayList<Integer>();
		gaps.add(0);
		HeaderItem root = header.getFirstRoot();
		if(root == null)
			return gaps;
		while(root.hasRootsInNextLevel()) {
			root = root.getFirstRootInNextLevel();
			gaps.add(header.getWidgetTop(root));
		}
		return gaps;
	}

	protected int layout(HeaderItem item, int x, int y, boolean isVisible, int minWidth) {
		item.setStyleName(HeaderItem.STYLE_LEFT_BORDER);
		boolean hidden = true;
		item.resetWidth(false);
		Ruler.setPosition(item, x, y);
		item.setVisible(isVisible);		
		int itemWidth = item.getInnerWidth();
		if(itemWidth < minWidth) {
			itemWidth = minWidth;
			item.setInnerWidth(itemWidth);
		}
		if (itemWidth > Content.MAX_COLUMN_WIDTH) {
			itemWidth = Content.MAX_COLUMN_WIDTH;
			item.setInnerWidth(itemWidth);
		}
		int tmpX = isVisible ? x + itemWidth : x;
		int lvlHeight = getLevelHeight(item.getLevel());
		lvlHeight -= INDENT * item.getDepth();

		if(item.hasRootsInNextLevel()) {
			int _w = x;
			int c = item.getRootsInNextLevel().size();
			int part = itemWidth / c;
			int remainingWidth = itemWidth;
			int counter = 1;
			for(HeaderItem root : item.getRootsInNextLevel()) {
				if (counter == c) {
					_w = layout(root, _w, y + lvlHeight + SEPARATOR_HEIGHT, isVisible, remainingWidth);
				} else {
					remainingWidth -= part;
					_w = layout(root, _w, y + lvlHeight + SEPARATOR_HEIGHT, isVisible, part);
				}
				counter++;
				if(isVisible && _w > tmpX)
					tmpX = _w;
				if(hidden) hidden = root.isHidden();
			}
			item.hide(hidden);
			if(hidden) {
				item.setVisible(false);
				tmpX = x;
			}
		} else {
			//we count leaf indexes...			
			item.setLeafIndex(leaf++);
			leafListener.visitedLeaf(item);
			if(item.isHidden()) {
				item.setVisible(false);
				tmpX = x;
			}
			if(item.isVisible()) {
				int newItemWidth = item.getInnerWidth();
				if(newItemWidth > itemWidth) {
					tmpX = tmpX - itemWidth + newItemWidth;
				}
//				itemWidth = newItemWidth;
//				item.setInnerWidth(itemWidth);
			}
		}
		item.setInnerHeight(lvlHeight);
		if (item.hasChildren()) {
			for (HeaderItem child : item.getChildren()) {
				tmpX = layout(child, tmpX, y + INDENT, isVisible
						&& item.isExpanded(), 0); //itemWidth);
			}
		}
		return tmpX;
	}

	protected int reverseLayout(HeaderItem item, int x, int y, boolean isVisible, int minWidth) {
		item.setStyleName(HeaderItem.STYLE_RIGHT_BORDER);
		boolean hidden = true;
		
		item.resetWidth(true);
		int itemWidth = item.getInnerWidth();
		if(itemWidth < minWidth) {
			itemWidth = minWidth;
			item.setInnerWidth(itemWidth);
		}
	     // ENABLE WIDTH CHECK
//		if (itemWidth > Content.MAX_COLUMN_WIDTH) {
//			itemWidth = Content.MAX_COLUMN_WIDTH;
//			item.setInnerWidth(itemWidth);
//		}		
		int tmpX = x;
		boolean isLeaf = false;
		int lvlHeight = getLevelHeight(item.getLevel());
		lvlHeight -= (INDENT * item.getDepth());

		if (item.hasChildren()) {
			for (HeaderItem child : item.getChildren())
				tmpX = reverseLayout(child, tmpX, y + INDENT, isVisible
						&& item.isExpanded(), 0);
		}
		
		int _w = tmpX; //have to set tmpX later if necessary
		if (item.hasRootsInNextLevel()) {
			int c = item.getRootsInNextLevel().size();
			int part = itemWidth / c;
			int remainingWidth = itemWidth;
			int counter = 1;
			for (HeaderItem root : item.getRootsInNextLevel()) {
				if (counter == c) {
					_w = reverseLayout(root, _w, y + lvlHeight + SEPARATOR_HEIGHT,
						isVisible, remainingWidth);
				} else {
					_w = reverseLayout(root, _w, y + lvlHeight + SEPARATOR_HEIGHT,
							isVisible, part);
					remainingWidth -= part;
				}
				counter++;
				if (hidden)
					hidden = root.isHidden();
			}
			item.hide(hidden);
		} else {
			isLeaf = true;
		}
		item.setInnerHeight(lvlHeight);
		//header.setWidgetPosition(item, tmpX, y);
		Ruler.setPosition(item, tmpX, y);

		
		//_w contains the right edge of the last child in next hierarchy,
		//so my new width is (_w - tpmX) if larger...		
		int nw = _w - tmpX;
		if(nw >= itemWidth) //'=' is important since '=' is the normal case on collapse!!
			item.setInnerWidth(nw);
		
		if(isLeaf) {
			//DON'T DO THIS!! ITS DONE UPFRONT!!! 
			//item.setLeafIndex(leaf++);
			item.setVisible(isVisible);
			leafListener.visitedLeaf(item);
			if(item.isHidden()) {
				item.setVisible(false);
				tmpX = x;
			}
			if(item.isVisible()) { //isVisible) {
				int newItemWidth = item.getInnerWidth();
				if(newItemWidth > itemWidth) {
					tmpX = tmpX - itemWidth + newItemWidth;
				}
			}
		}
		item.setVisible(isVisible && !item.isHidden());
		tmpX = item.isVisible() ? tmpX + itemWidth : x;		
		if(_w > tmpX) 
			tmpX = _w;			

		return tmpX;
	}

	private final void drawSeparatorsAndSelector() {
		HeaderItem root = header.getFirstRoot();
		if(root == null)
			return;
		
		String separatorWidth = size.x + "px";
		Point selectorSize = selector.layout(root.getLevel(), 0, 0);
		while(root.hasRootsInNextLevel()) {
			root = root.getFirstRootInNextLevel();
			Separator separator = header.getSeparator(root.getLevel());
			separator.setWidth(separatorWidth);
			int top = header.getWidgetTop(root);
//			header.setWidgetPosition(separator, 0, top - SEPARATOR_HEIGHT);
			Ruler.setPosition(separator, 0, top - SEPARATOR_HEIGHT);
			selectorSize = selector.layout(root.getLevel(), 0, top);
		}
		//TODO check that size is bigger...
		Ruler.setClientSize(selector, selectorSize.x, selectorSize.y);
//		Limiter.setClientSize(selector, selectorSize.x, selectorSize.y);
	}
	
	private final void setMinimumSize() {
		size.x = 0;
		size.y = 0;
		int levels = header.getLevelCount();
		for(int i = 0; i<levels; ++i)
			size.y += getLevelHeight(i) + SEPARATOR_HEIGHT;
	}
	
//	private final int getLevelHeight(int lvl) {	
//		return 2 * INDENT + 6 + (INDENT * header.getMaxLevelDepth(lvl));
//	}

	private final int getLevelHeight(int lvl) {	
		return getMaxLevelHeight(lvl); // + (INDENT * header.getMaxLevelDepth(lvl));
	}

	private final int getMaxLevelHeight(int lvl) {
		Integer height = maxHeight.get(lvl);
		if(height == null)
			return -1;
		return height.intValue();
	}
	private final void setMaxLevelHeight(int lvl, int height) {
		if(getMaxLevelHeight(lvl) < height)
			maxHeight.put(lvl, height);
	}

}
