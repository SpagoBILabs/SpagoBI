/*
*
* @file PaloTable.java
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
* @version $Id: PaloTable.java,v 1.56 2010/03/11 10:42:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellCollection;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XDelta;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;
import com.tensegrity.palo.gwt.core.client.models.palo.XCell;
import com.tensegrity.palo.gwt.core.server.services.cubeview.util.AxisItemVisitor;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.Header;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.HeaderItem;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.HorizontalLayouter;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.ItemVisitor;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.VerticalLayouter;
import com.tensegrity.palo.gwt.widgets.client.palotable.levelselector.HorizontalLevelSelector;
import com.tensegrity.palo.gwt.widgets.client.palotable.levelselector.LevelSelector;
import com.tensegrity.palo.gwt.widgets.client.palotable.levelselector.LevelSelectorListener;
import com.tensegrity.palo.gwt.widgets.client.palotable.levelselector.VerticalLevelSelector;
import com.tensegrity.palo.gwt.widgets.client.util.Point;
import com.tensegrity.palo.gwt.widgets.client.util.Ruler;
import com.tensegrity.palo.gwt.widgets.client.util.UserAgent;

/**
 * <code>PaloTable</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: PaloTable.java,v 1.56 2010/03/11 10:42:18 PhilippBouillon Exp $
 **/
public class PaloTable extends AbsolutePanel implements LeafListener, ExpandListener, MouseClickListener {

	private static final int INDENT = 5;
	private static final String STYLE = "palo-gwt-palotable";
	
	//CubeView COLUMN_AXIS and CubeView.ROW_AXIS;
	private static final String ROW_AXIS = "rows";
	private static final String COLUMN_AXIS = "cols";
	
	//our components:
	//the different header layouter:
	private final VerticalLayouter vLayouter = new VerticalLayouter();
	private final HorizontalLayouter hLayouter = new HorizontalLayouter();
	//the headers:
	private final TableHeader horHeader = new TableHeader(hLayouter);
	private final TableHeader verHeader = new TableHeader(vLayouter);
	//the table content:
	private final TableContent tableContent = new TableContent();
	//level buttons selector:
	private final LevelSelector rowLevelSelector = new HorizontalLevelSelector();
	private final LevelSelector colLevelSelector = new VerticalLevelSelector();

//	private ItemLoader itemLoader;
	private ItemExpandListener expandListener;
	private ItemClickListener clickListener;
	
	//input:
	private String viewId;
	
	//listeners:
	private final List<ExpandListener> expandListeners = new ArrayList<ExpandListener>();
	private boolean changeSize = true;
	private HeaderItem itemToCollapse = null;
	
	public PaloTable() {
		initComponents();
		initEventhandling();
	}

	public final void saveState(XViewModel view) {
		XAxis row = view.getRowAxis();
		row.setExpanded(verHeader.getExpandedItems());
		XAxis col = view.getColumnAxis();
		col.setExpanded(horHeader.getExpandedItems());
	}
	public final void addCellChangedListener(CellChangedListener listener) {
		tableContent.addCellChangedListener(listener);
	}
	public final void addExpandListener(ExpandListener listener) {
		expandListeners.add(listener);
	}
	
	public final int previewHideEmptyCells(boolean doIt) {
		return tableContent.previewHideEmptyCells(doIt);
	}
	
	public final void hideEmptyCells(boolean doIt) {
		if (tableContent.isHideEmpty() != doIt) {
			tableContent.hideEmptyCells(doIt);
			layout();
		}
	}
	
	public final void markRuleBasedCells(boolean doIt) {
		tableContent.markRules(doIt);
		tableContent.layout();
	}

	public final int getCellCount() {
		return tableContent.getCellCount();
	}
	
	public final void reverseRows(boolean doIt) {
		if (vLayouter.isReverse() != doIt) {
			vLayouter.reverse(doIt);
			verHeader.layout();
			tableContent.layout();
		}
	}
	public final void reverseColumns(boolean doIt) {
		if (hLayouter.isReverse() != doIt) {
			hLayouter.reverse(doIt);
			horHeader.layout();
			//have to layout rows too, since here all cells are placed...
			verHeader.layout();
			tableContent.layout();
		}
	}

	public final void setInput(XViewModel view) {
		viewId = view.getId();
		fillTable(view);
		fillLevelSelectors(view);
	}

	public final boolean isChangeSize() {
		return changeSize;
	}
	
	public final void setChangeSize(boolean newChangeSize) {
		changeSize = newChangeSize;
	}

	public final void setSize(int width, int height) {
		if (!changeSize) {
			return;
		}		
		Ruler.setSize(this, width, height);
		setContentSize();
		setHeadersAndTableSize(Ruler.getClientWidth(this), 
				Ruler.getClientHeight(this));
		arrangeHeadersAndContent();
		arrangeLevelSelectors();
		scrolled(tableContent.getHorizontalScrollPosition(), 
				tableContent.getScrollPosition());		
	}
	
	public final synchronized void layout() {
		layoutHeadersAndContent();
		if (UserAgent.getInstance().isIE) {
			int width = Ruler.getClientWidth(this);
			int height = Ruler.getClientHeight(this);
			Ruler.setClientSize(this, width + 4, height + 4);
		}
		setSize(Ruler.getClientWidth(this), Ruler.getClientHeight(this));
	}
	private final synchronized void layoutHeadersAndContent() {
		Point horSize = horHeader.layout();
		Point verSize = verHeader.layout();
		tableContent.layout();
		Ruler.setClientWidth(verHeader, verSize.x);
		Ruler.setClientHeight(horHeader, horSize.y);
	}
	private final void setContentSize() {
		int contentWidth = Ruler.getClientWidth(horHeader.getHeader());
		int contentHeight = Ruler.getClientHeight(verHeader.getHeader());
		tableContent.setContentSize(contentWidth, contentHeight); 
	}
	private final void setHeadersAndTableSize(int width, int height) {
		int borderOffset = Ruler.getBorderOffset();
		int twoTimesBorderOffset = 2 * 4; // Ruler.getBorderOffset();
		int w = width - INDENT - verHeader.getOffsetWidth() - twoTimesBorderOffset + 2;
		int h = height - INDENT - horHeader.getOffsetHeight() - twoTimesBorderOffset + 1;		
		
		//+1 because headers are arranged at position (x-1,y-1) to make their borders match nicely...
		Ruler.setClientWidth(horHeader, w + 1);
		Ruler.setClientHeight(verHeader, h + 1);
		Ruler.setClientSize(tableContent, w + borderOffset, h + borderOffset);
	}
	private final void arrangeHeadersAndContent() {
		int x = verHeader.getOffsetWidth() + INDENT;
		int y = horHeader.getOffsetHeight() + INDENT;
 
		setWidgetPosition(horHeader, x - 1, INDENT);
		setWidgetPosition(verHeader, INDENT, y - 1);
		setWidgetPosition(tableContent, x, y);
	}
	private final void arrangeLevelSelectors() {
		int colH = Ruler.getClientHeight(horHeader);
		int rowW = Ruler.getClientWidth(verHeader);
		Ruler.setClientWidth(rowLevelSelector, rowW);
		Ruler.setClientHeight(colLevelSelector, colH);
		
		setWidgetPosition(rowLevelSelector, INDENT, colH + INDENT - rowLevelSelector.getOffsetHeight() - 1);
		setWidgetPosition(colLevelSelector, rowW + INDENT - colLevelSelector.getOffsetWidth(), INDENT);
	}
	
	/** throws away all currently cached items and cells except for current visible state */
	public final void initWithCurrentState() {
		tableContent.initWithCurrentState();
		horHeader.initWithCurrentState();
		verHeader.initWithCurrentState();
	}
	
	public final void reset() {
		tableContent.reset();
		horHeader.reset();
		verHeader.reset();
		rowLevelSelector.reset();
		colLevelSelector.reset();
	}
	
	public final void reset(Header header) {
		tableContent.reset(verHeader.getHeader() == header); 		
	}

	public final void visitedLeaf(HeaderItem item) {		
		Widget parent = item.getParent();
		if(parent != null) {
			if(parent == horHeader.getHeader()) {
				tableContent.visitedColumnLeaf(item);
			} else if (parent == verHeader.getHeader()) {
				tableContent.visitedRowLeaf(item);
			}
		}		
	}

//	public final void register(ItemLoader itemLoader) {
//		this.itemLoader = itemLoader;
//	}

	public final void register(ItemExpandListener expandListener) {
		this.expandListener = expandListener;
	}

	public final void register(ItemClickListener clickListener) {
		this.clickListener = clickListener;
	}
	
	public final void proceedCollapse() {
//		if(expandListener != null && items.length > 0) {
//			for(HeaderItem item : items) {
		if (itemToCollapse != null) {		
			itemToCollapse.setIsExpanded(false);
		}
		itemToCollapse = null;
//				expandListener.collapse(item.getModel(), viewId, axisId, columns);
//				for(ExpandListener listener : expandListeners)
//					listener.collapsed(item);
//			}
//		}
		layout();
	}
	private final boolean isColumnItem(HeaderItem item) {
		Widget parent = item.getParent();
		if (parent != null && parent instanceof Header) {
			return (parent == horHeader.getHeader());
		}
		return false;
	}
	public final void willCollapse(HeaderItem item) {
		if (expandListener != null) {
			itemToCollapse = item;
			boolean column = isColumnItem(item);
			String axisId = column ? COLUMN_AXIS : ROW_AXIS;
			expandListener.willCollapse(item.getModel(), viewId, axisId, column);
		}
	}

	public final void willExpand(HeaderItem item) {
		if(expandListener != null) {
			boolean column = isColumnItem(item);
			String axisId = column ? COLUMN_AXIS : ROW_AXIS;
			expandListener.willExpand(item.getModel(), viewId, axisId, column);
		}
	}
	
	public final void leftClicked(HeaderItem item, int x, int y) {
		if (clickListener != null) {
			boolean column = isColumnItem(item);
			String axisId = column ? COLUMN_AXIS : ROW_AXIS;
			HeaderItem pprev = item.getParentInPreviousHierarchy();
			List <XAxisItem> roots = new ArrayList<XAxisItem>();
			if (pprev != null) {
				for (HeaderItem it: item.getParentInPreviousHierarchy().getRootsInNextLevel()) {
					roots.add(it.getModel());
				}
			} else {
				Widget parent = item.getParent();			
				if (parent != null && parent instanceof Header) {
					for (HeaderItem it: ((Header) parent).getItems()) {
						roots.add(it.getModel());
					}
				}
			}
			clickListener.leftClicked(item.getModel(), roots, viewId, axisId, column, x, y);
		}
	}

	public final void rightClicked(HeaderItem item, int x, int y) {
		if(clickListener != null) {
			boolean column = isColumnItem(item);
			String axisId = column ? COLUMN_AXIS : ROW_AXIS;
			clickListener.rightClicked(item.getModel(), null, viewId, axisId, column, x, y);
		}
	}

	private final void initComponents() {
		vLayouter.register(this);
		vLayouter.register(rowLevelSelector);
		
		hLayouter.register(this);
		hLayouter.register(colLevelSelector);

		add(horHeader);
		add(verHeader);
		add(tableContent);
		add(rowLevelSelector);
		add(colLevelSelector);
		
		//STYLES:
		setStyleName(STYLE);
	}
	
	private final void initEventhandling() {
		tableContent.addScrollListener(new ScrollListener() {
			public void onScroll(Widget widget, int scrollLeft, int scrollTop) {
				scrolled(scrollLeft, scrollTop);
			}
		});
		rowLevelSelector.addListener(new LevelSelectorListener() {
			public void selected(int hierarchy, int level) {
				showRowItems(hierarchy, level);
			}
		});
		colLevelSelector.addListener(new LevelSelectorListener() {
			public void selected(int hierarchy, int level) {
				showColItems(hierarchy, level);
			}
		});
	}

	private final void showRowItems(final int hierarchy, final int level) {
		showItems(hierarchy, level, false);
	}
	private final void showColItems(int hierarchy, final int level) {
		showItems(hierarchy, level, true);
	}
	
	private final void showItems(final int hierarchy, final int depth,
			boolean columns) {
		final List<HeaderItem> expandedItems = new ArrayList<HeaderItem>();
		final List<HeaderItem> collapsedItems = new ArrayList<HeaderItem>();
		ItemVisitor visitor = new ItemVisitor() {
			public boolean visit(HeaderItem item) {
				if (item.getLevel() == hierarchy) {					
					// check hierarchy, level (aka depth) and parent in prev. hierarchy
					HeaderItem parentPrevHier = 
							item.getParentInPreviousHierarchy();
					if (item.getDepth() < depth
							&& (parentPrevHier == null 
									|| parentPrevHier.isVisible())) {
//						item.setIsExpanded(true);
						expandedItems.add(item);
					} else {
//						item.setIsExpanded(false);
						if (item.hasChildren())
							collapsedItems.add(item);
					}
				}
				return true;
			}
		};
		Header header = columns ? horHeader.getHeader() : verHeader.getHeader();
		header.traverse(visitor);
		setExpandState(expandedItems, collapsedItems, depth, columns);
		
//		if(expandListener != null) {
//			Header header = columns ? horHeader.getHeader() : verHeader.getHeader();
//			List<XAxisItem> roots = getRoots(header, hierarchy);
//			String axisId = columns ? COLUMN_AXIS : ROW_AXIS;
//			expandListener.setExpandState(roots.toArray(new XAxisItem[0]), depth, viewId, axisId, columns);
//		}
	}
//	private final List<XAxisItem> getRoots(Header header, int hierarchy) {
//		List<XAxisItem> roots = new ArrayList<XAxisItem>();
//		for(HeaderItem item : header.getItems()) {
//			if(item.getLevel() == hierarchy) {
//				XAxisItem xItem = item.getModel();
//				if(xItem != null)
//					roots.add(xItem);
//			}
//		}
//		return roots;
//	}
	private final void setExpandState(List<HeaderItem> expanded,
			List<HeaderItem> collapsed, int expandDepth, boolean columns) {
		if (expandListener != null) {
			String axisId = columns ? COLUMN_AXIS : ROW_AXIS;
			List<XAxisItem> expandedItems = new ArrayList<XAxisItem>();
			List<XAxisItem> collapsedItems = new ArrayList<XAxisItem>();
			for (HeaderItem item : expanded) {
				expandedItems.add(item.getModel());
			}
			for (HeaderItem item : collapsed)
				collapsedItems.add(item.getModel());
			expandListener.setExpandState(
						expandedItems.toArray(new XAxisItem[0]), 
						collapsedItems.toArray(new XAxisItem[0]), 
						expandDepth, viewId, axisId, columns);
		} else
			layout();

//		for (ExpandListener listener : expandListeners) {
//			listener.expanded(expanded.toArray(new HeaderItem[0]));
//			listener.collapsed(collapsed.toArray(new HeaderItem[0]));
//		}
	}

	//	private final void expandItems(List<HeaderItem> items, boolean columns) {
//		List<XAxisItem> itemsToLoad = new ArrayList<XAxisItem>();
//		for (HeaderItem item : items) {
//			itemsToLoad.add(item.getModel());
//		}
//		if (expandListener != null) {
//			String axisId = columns ? COLUMN_AXIS : ROW_AXIS;
//			expandListener.expand(itemsToLoad.toArray(new XAxisItem[0]),
//					viewId, axisId, columns);
//		} else
//			layout();
//
//		for (ExpandListener listener : expandListeners)
//			listener.expanded(items.toArray(new HeaderItem[0]));
//	}
//	private final void collapseItems(List<HeaderItem> items, boolean columns) {
//		List<XAxisItem> itemsToLoad = new ArrayList<XAxisItem>();
//		for (HeaderItem item : items) {
//			itemsToLoad.add(item.getModel());
//		}
//		if (expandListener != null) {
//			String axisId = columns ? COLUMN_AXIS : ROW_AXIS;
//			expandListener.collapse(itemsToLoad.toArray(new XAxisItem[0]),
//					viewId, axisId, columns);
//		}
//		//layout is called on expand
//		//layout();
//		for (ExpandListener listener : expandListeners)
//			listener.collapsed(items.toArray(new HeaderItem[0]));
//	}
	private final void scrolled(int offX, int offY) {
		horHeader.placeContent(-offX - 1, 0);
		verHeader.placeContent(0, -offY - 1);
	}
	
	private final void addDepths(Header toHeader,
			List<XAxisHierarchy> hierarchies) {
		for (int i = 0, n = hierarchies.size(); i < n; i++)
			toHeader.addMaxLevelDepth(i, hierarchies.get(i).getMaxDepth());
	}

	private final HeaderItem createHeaderItem(XAxisItem item, HeaderItem parentPrevHier, boolean isVertical) {
		HeaderItem hItem = new HeaderItem(item, parentPrevHier, isVertical);
		hItem.register((ExpandListener) this);
		hItem.register((MouseClickListener) this);
		if(hItem.hasChildren())
			for(XAxisItem child : item.getChildren())
				hItem.addChild(createHeaderItem(child, parentPrevHier, isVertical));
		for(XAxisItem root : item.getRootsInNextHier())
			hItem.addRootInNextLevel(createHeaderItem(root, hItem, isVertical));
		return hItem;
	}
	
	public final void insert(XDelta delta, boolean column) {
		insert(new XDelta[] {delta}, column);
	}
	
	public final void insert(XDelta[] deltas, boolean columns) {
		Header header = columns ? horHeader.getHeader() : verHeader.getHeader();
		InsertHelper helper = new InsertHelper();
		// Have to restructure the deltas. It may happen that a delta contains
		// a set of cells that are above the insertIndex of the next delta
		// (this happens when old expanded elements are hidden -- if their
		// grandparent element is collapsed -- and then revealed again if
		// the level buttons are used to expand the whole level). 
		XDelta oldDelta = null;
		for (XDelta delta: deltas) {
			if (oldDelta == null) {
				oldDelta = delta;
				continue;
			}
			int insertIndex = delta.getCells().getInsertIndex();
			ArrayList <XCell> toBeMoved = new ArrayList<XCell>();
			ArrayList <XCell> toBeRetained = new ArrayList<XCell>();
			for (XCell cell: oldDelta.getCells().getCells()) {
				int val = columns ? cell.col : cell.row;
				if (val > insertIndex) {
					toBeMoved.add(cell);
				} else {
					toBeRetained.add(cell);
				}
			}
			if (!toBeMoved.isEmpty()) {
				oldDelta.getCells().setCells(toBeRetained);
				for (XCell c: toBeMoved) {
					delta.getCells().add(c);
				}
			}
			oldDelta = delta;
		}
		for(XDelta delta : deltas) {
			XAxisItem xParent = delta.getParent();
			if(xParent != null) {
				HeaderItem parent = find(xParent, header);
				parent.setIsExpanded(true);
				if(!delta.isEmpty()) {
					setLeafIndex(xParent, helper);
					if(!parent.isLoaded()) {
						createAndAddItemsTo(header, delta.getItems(), parent, columns);
						parent.setIsLoaded(true);
					}
					insert(columns, delta.getCells(), parent);
					update(helper, delta, xParent.depth);
				}
			}
			collapse(delta.getCollapsedItems(), header);
		}
		//finally layout again:
		layout();
	}
	private final void collapse(List<XAxisItem> items, Header header) {
		for(XAxisItem item : items) {
			HeaderItem hItem = find(item, header);
			if(hItem != null)
				hItem.setIsExpanded(false);
		}
	}
	private final void setLeafIndex(XAxisItem xItem, InsertHelper helper) {
		if (xItem.leafIndex == -1) {
			if (xItem.depth != helper.lastDepth)
				xItem.leafIndex = helper.lastParentIndex + 1;
			else
				xItem.leafIndex = helper.lastChildIndex + 1;
		}
	}
	private final HeaderItem find(XAxisItem xItem, Header inHeader) {
		HeaderItem item = inHeader.find(xItem);
		if(item == null)
			System.err.println("COULDN'T FIND ITEM: "+xItem.getName());
//		item.setIsLoaded(true);
		item.setIsExpanded(xItem.isExpanded);
		return item;
	}
	private final void createAndAddItemsTo(Header header,
			List<XAxisItem> xItems, HeaderItem parent, boolean cols) {
		for (XAxisItem child : xItems) {
			HeaderItem kid = createHeaderItem(child, parent
					.getParentInPreviousHierarchy(), !cols);
			header.addChild(kid, parent);
		}
	}
	private final void insert(boolean columns, XCellCollection cells,
			HeaderItem parent) {
		tableContent.insertCells(cells, columns);
	}
//	private final int getLeafIndexOf(HeaderItem item) {
//		while(item.hasRootsInNextLevel()) {
//			//is not leaf dimension => run down...
//			List<HeaderItem> rootsNextLevel = item.getRootsInNextLevel();
//			item = rootsNextLevel.get(rootsNextLevel.size() - 1);
//		}
//		return item.getLeafIndex(); 
//	}

	private final void update(InsertHelper helper, XDelta delta, int depth) {
		helper.lastDepth = depth;
		helper.lastParentIndex = delta.getParent().leafIndex;
		helper.lastChildIndex = helper.lastParentIndex + delta.getItems().size();

	}
	
	public final void setCells(XCellCollection cells, XViewModel view) {
		//insert cells:
		tableContent.setCells(cells);
		layout();
	}

	private final void fillTable(XViewModel view) {
		XCellCollection cellCollection = view.getCells();
		if(cellCollection.isEmpty()) {
			hideTable(true);
			return;
		} else {
			hideTable(false);
		}			
		fillHeader(horHeader, view);
		fillHeader(verHeader, view);
		tableContent.setCells(view.getCells());
	}
	private final void hideTable(boolean doIt) {
		horHeader.setVisible(!doIt);
		verHeader.setVisible(!doIt);
		tableContent.setVisible(!doIt);
		rowLevelSelector.setVisible(!doIt);
		colLevelSelector.setVisible(!doIt);
	}
	private final void fillHeader(TableHeader tableHeader, XViewModel view) {
		Header header = tableHeader.getHeader();
		List<XAxisItem> items;
		boolean isVertical;
		if(tableHeader == verHeader) {
			addDepths(header, view.getRowAxis().getAxisHierarchies());
			items = view.getRowRoots();
			isVertical = true;
		} else {
			addDepths(header, view.getColumnAxis().getAxisHierarchies());
			items = view.getColumnRoots();
			isVertical = false;
		}
		//header items:
		for(XAxisItem item : items)
			header.add(createHeaderItem(item, null, isVertical));
	}
	
	private final void fillLevelSelectors(XViewModel xView) {
		rowLevelSelector.setAxis(xView.getRowAxis());
		colLevelSelector.setAxis(xView.getColumnAxis());
	}
}

class InsertHelper {	
	int lastParentIndex;
	int lastChildIndex;
	int lastDepth;
}

