/*
*
* @file VerticalLayouter.java
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
* @version $Id: VerticalLayouter.java,v 1.35 2010/03/04 09:12:35 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.palotable.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tensegrity.palo.gwt.widgets.client.palotable.Content;
import com.tensegrity.palo.gwt.widgets.client.palotable.LeafListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.levelselector.LevelSelector;
import com.tensegrity.palo.gwt.widgets.client.separator.Separator;
import com.tensegrity.palo.gwt.widgets.client.separator.VerticalSeparator;
import com.tensegrity.palo.gwt.widgets.client.util.Limiter;
import com.tensegrity.palo.gwt.widgets.client.util.Point;
import com.tensegrity.palo.gwt.widgets.client.util.Ruler;

public class VerticalLayouter implements HeaderLayouter {

	private static final String STYLE_HEADER_END = "rows-end";
	
	//TODO remove it
	protected static final int SEPARATOR_WIDTH = 2;

	protected  static final int INDENT = 15;
	
	protected int leaf;
	protected Header header;
	protected LevelSelector selector;
	protected final Point size;
	private final Map<Integer, Integer> maxWidth = new HashMap<Integer, Integer>();
	protected LeafListener leafListener;
	private boolean reverse;
	
	public VerticalLayouter() {
		size = new Point();
	}
	
	public final void register(Header header) {
		this.header = header;
		this.header.addStyleName(STYLE_HEADER_END);
	}
	public final void register(LevelSelector selector) {
		this.selector = selector;
	}
	public final void register(LeafListener listener) {
		leafListener = listener;
	}
	
	public final Separator createSeparator() {
		return new VerticalSeparator();
	}

	public final void reverse(boolean doIt) {
		reverse = doIt;
	}
	public final boolean isReverse() {
		return reverse;
	}
	
	public void initialize(HeaderItem item) {
		item.resetWidth(false);
		int width = item.getInnerWidth(); //OffsetWidth();
//		int calcWidth = width + (INDENT * item.getDepth() + 1) + 2; 
		int add = (INDENT * item.getDepth() + 1) + 2;
		width += add;
		if (width > (Content.MAX_ROWS_COL_WIDTH + add)) {
			width = Content.MAX_ROWS_COL_WIDTH + add;
			item.setInnerWidth(width);
			item.placeMenuIcon(width);
		}
		setMaxLevelWidth(item.getLevel(), width);			
	}

	public final void reset() {
		size.x = 0;
		size.y = 0;
		maxWidth.clear();
	}
	
	public Point layout() {
		leaf = 0;
		recalculateMaxWidthPerLevel();
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
		
	private void recalculateMaxWidthPerLevel() {
		reset();
		header.traverseVisible(new ItemVisitor() {
			public boolean visit(HeaderItem item) {
				initialize(item);
				return true;
			}
		});
	}

	private void doReverseLayout(int borderOffset) {
		size.y += (1 - Ruler.getBorderOffset());
		for (HeaderItem item : header.getItems())
			size.y = reverseLayout(item, 0, size.y, true); //item.isVisible());
	}
	private void doNormalLayout(int borderOffset) {
		for (HeaderItem item : header.getItems())
			size.y = layout(item, 0, size.y, true); //item.isVisible());
		size.y += (borderOffset);
	}

	protected final int getMaxLevelWidth(int lvl) {
		Integer width = maxWidth.get(lvl);
		if(width == null) {
			return -1;
		}			
		return width.intValue();
	}
	private final void setMaxLevelWidth(int lvl, int width) {
		if(getMaxLevelWidth(lvl) < width) {
			maxWidth.put(lvl, width);
		}
	}
	
	protected int layout(HeaderItem item, int x, int y, boolean isVisible) {
		item.setStyleName(HeaderItem.STYLE_TOP_BORDER);
		boolean hidden = true;
		item.resetHeight(false);
		Ruler.setPosition(item, x, y);
		item.setVisible(isVisible);		
		int itemHeight = item.getInnerHeight();
		int tmpY = item.isVisible() ? y + itemHeight : y;
		int lvlWidth = getLevelWidth(item.getLevel());
		lvlWidth -= (INDENT *item.getDepth());
		
//		if (lvlWidth > Content.MAX_COLUMN_WIDTH) {
//			lvlWidth = Content.MAX_COLUMN_WIDTH;
//		}
		
		if(item.hasRootsInNextLevel()) {
			//start of next hierarchy at _x:
			int _h = y;			
			for(HeaderItem root : item.getRootsInNextLevel()) {
				_h = layout(root, x + lvlWidth + SEPARATOR_WIDTH, _h, isVisible);
				if(isVisible && _h > tmpY)
					tmpY = _h;
				if(hidden) hidden = root.isHidden();
			}
			item.hide(hidden);
			if(hidden) {
				item.setVisible(false);
				tmpY = y;
			}
		} else {
			//we count leaf indexes...
			item.setLeafIndex(leaf++);
			leafListener.visitedLeaf(item);
			if(item.isHidden()) {
				item.setVisible(false);
				tmpY = y;
			}
		}
		item.setInnerWidth(lvlWidth);
		if(item.hasChildren()) {
			for(HeaderItem child : item.getChildren())
				tmpY = layout(child, x + INDENT, tmpY, isVisible && item.isExpanded());
		}
		return tmpY;
	}
	
	private final int reverseLayout(HeaderItem item, int x, int y, boolean isVisible) {
		item.setStyleName(HeaderItem.STYLE_BOTTOM_BORDER);
		boolean hidden = true;
		boolean isLeaf = false;
		int tmpY = y;
		item.resetHeight(true);
		int itemHeight = item.getInnerHeight();
		int lvlWidth = getLevelWidth(item.getLevel());
		lvlWidth -=  (INDENT * item.getDepth());
		
//		if (lvlWidth > Content.MAX_COLUMN_WIDTH) {
//			lvlWidth = Content.MAX_COLUMN_WIDTH;
//		}
		
		if (item.hasChildren()) {
			for (HeaderItem child : item.getChildren())
				tmpY = reverseLayout(child, x + INDENT, tmpY,
						isVisible && item.isExpanded());
		}
		int _h = tmpY; //have to set tmpY later if necessary
		if(item.hasRootsInNextLevel()) {
			for(HeaderItem root : item.getRootsInNextLevel()) {
				_h = reverseLayout(root, x + lvlWidth + SEPARATOR_WIDTH, _h, isVisible);
				if(hidden) hidden = root.isHidden();
			}
			item.hide(hidden);
		} else {
			isLeaf = true;
		}
		
		item.setInnerWidth(lvlWidth);
//		header.setWidgetPosition(item, x, tmpY);
		
		Ruler.setPosition(item, x, tmpY);
		//_h contains the bottom edge of the last child in next hierarchy,
		//so my new height is (_h - tmpY) if larger...		
		int nH = _h - tmpY;
		if(nH >= itemHeight) //'=' is important since '=' is the normal case on collapse!!
			item.setInnerHeight(nH);

		if(isLeaf) {
			//DON'T DO THIS!! ITS DONE UPFRONT!!! 
			//item.setLeafIndex(leaf++);
			item.setVisible(isVisible);
			leafListener.visitedLeaf(item);
		}
		item.setVisible(isVisible && !item.isHidden());
		//TODO is this right? I MEAN itemHeight has old value...
		tmpY = item.isVisible() ? tmpY + itemHeight : y;
		if(_h > tmpY) tmpY = _h;
		return tmpY;
	}
	
	private final void drawSeparatorsAndSelector() {
		HeaderItem root = header.getFirstRoot();
		if(root == null)
			return;
		Point selectorSize = selector.layout(root.getLevel(), 0, 0);
		selectorSize.y -= 2;
		while(root.hasRootsInNextLevel()) {
			root = root.getFirstRootInNextLevel();
			Separator separator = header.getSeparator(root.getLevel());
			separator.setHeight(size.y+"px");			
			int left = header.getWidgetLeft(root);
//			header.setWidgetPosition(separator, left - SEPARATOR_WIDTH, 0);
			Ruler.setPosition(separator, left - SEPARATOR_WIDTH, 0);
			selectorSize = selector.layout(root.getLevel(), left, 0);
			selectorSize.y -= 2;			
		}
		//TODO check that size is bigger...
		Limiter.setClientSize(selector, selectorSize.x, selectorSize.y);
	}

	private final void setMinimumSize() {
		size.x = 0;
		size.y = 0;
		int levels = header.getLevelCount();
		for(int i = 0; i<levels; ++i)
			size.x += getLevelWidth(i) + SEPARATOR_WIDTH;
	}

	private final int getLevelWidth(int lvl) {	
		return getMaxLevelWidth(lvl); // + (INDENT * header.getMaxLevelDepth(lvl));
	}

}
