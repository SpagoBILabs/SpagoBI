/*
*
* @file ViewModelController.java
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
* @version $Id: ViewModelController.java,v 1.20 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.internal.LocalFilterImpl;
import org.palo.viewapi.uimodels.axis.AxisItem;
import org.palo.viewapi.uimodels.axis.AxisModel;
import org.palo.viewapi.uimodels.axis.AxisTreeModel;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.server.services.cubeview.util.AxisItemVisitor;
import com.tensegrity.palo.gwt.core.server.services.cubeview.util.AxisTraverser;
import com.tensegrity.palo.gwt.core.server.services.cubeview.util.ViewModelUtils;

/**
 * <code>ViewModelController2</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewModelController.java,v 1.20 2010/03/11 10:42:20 PhilippBouillon Exp $
 **/
public class ViewModelController {

	private int loadedRows;
	private int loadedColumns;
	private List<AxisItem> rowLeafs;
	private List<AxisItem> columnLeafs;
	private ViewModel viewModel;
	private final LoadMarker loadMarker = new LoadMarker();
	private final Stack<Expanded> expandStack = new Stack<Expanded>();
	private final Stack<Collapsed> collapseStack = new Stack<Collapsed>();
	private int parentCounter = 0;
	private final HashMap <Element, String> origNodes = new HashMap<Element, String>();
	private boolean initializedOriginalPaths = false;
	
	ViewModelController(CubeView cubeView) {
		this.viewModel = new ViewModel(cubeView);
		initialize();		
	}
	private final void initialize() {
		ViewModelUtils.determineLeafIndexes(viewModel.getRowAxis());
		ViewModelUtils.determineLeafIndexes(viewModel.getColumnAxis());
		initLeafs();
		initializeLoadMarker();
	}
	private final void initLeafs() {
		rowLeafs = ViewModelUtils.getLeafs(viewModel.getRowAxis());
		columnLeafs = ViewModelUtils.getLeafs(viewModel.getColumnAxis());
	}
	private final void initializeLoadMarker() {
		//extend load marker:
		extendLoadMarker(0, false);
		extendLoadMarker(0, true);
	}	
	private final void extendLoadMarker(int atIndex, boolean extendColumns) {
		if(extendColumns)
			loadMarker.insertColumns(atIndex, columnLeafs.size());
		else
			loadMarker.insertRows(atIndex, rowLeafs.size());
	}

	private final static void deepAmend(AxisItem [] roots, AxisItem child) {
		for (AxisItem i: roots) {
			AxisItem copy = i.copy();
			if (i.hasRootsInNextHierarchy()) {
				deepAmend(i.getRootsInNextHierarchy(), copy);
			}
			child.addRootInNextHierarchy(copy);
			copy.setParentInPreviousHierarchy(child);
		}		
	}
	
	private final void updateAxisItemStructure(ViewModel model) {		
		AxisItemVisitor visitor1 = new AxisItemVisitor() {
			public void visit(AxisItem item, AxisItem parent,
					AxisItem parentInPrevHierarchy) {
				if (item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
					for (AxisItem child : item.getChildren()) {
						if (item.hasRootsInNextHierarchy() && !child.hasRootsInNextHierarchy()) {
							AxisItem [] items = item.getRootsInNextHierarchy();
							deepAmend(items, child);
						}
						if (item.getParentInPrevHierarchy() != null && child.getParentInPrevHierarchy() == null) {
							child.setParentInPreviousHierarchy(item.getParentInPrevHierarchy());
						}
					}
				}
			}
		};
		AxisTraverser traverser1 = new AxisTraverser();
		
		AxisItem [] items;
		if (model.getRowAxis() != null && (items = model.getRowAxis().getRoots()) != null) {
			traverser1.traverseVisible(items, visitor1);
		}
		if (model.getColumnAxis() != null && (items = model.getColumnAxis().getRoots()) != null) {
			traverser1.traverseVisible(items, visitor1);	
		}		
	}
	
	public final Expanded initWithCurrentModelState() {
		loadMarker.clear();
		updateAxisItemStructure(viewModel);
//		ViewModelUtils.resetAxis(viewModel.getRowAxis());
//		ViewModelUtils.resetAxis(viewModel.getColumnAxis());
		initialize();
		
		List<ViewCell> cells = getCellsToLoad(rowLeafs, columnLeafs);
		Expanded expanded = new Expanded(null, null, true, 0, null, false);
		expanded.setCellsToLoad(cells, loadMarker.getRowCount(), loadMarker
				.getColumnCount(), loadedRows, loadedColumns);
		markLoadedCells(cells);
		return expanded;
	}
	
	public final void proceedSwapAxes() {
		AxisHierarchy [] rowHiers = viewModel.getRowAxis().getAxisHierarchies();
		AxisHierarchy [] colHiers = viewModel.getColumnAxis().getAxisHierarchies();
		
		String [] rowExpanded = ((AxisTreeModel) viewModel.getRowAxis()).getExpandedNodes();
		String [] colExpanded = ((AxisTreeModel) viewModel.getColumnAxis()).getExpandedNodes();
		viewModel.getColumnAxis().removeAll();
		for (AxisHierarchy hier: rowHiers) {
			viewModel.getColumnAxis().add(hier);
		}
		viewModel.getRowAxis().removeAll();
		for (AxisHierarchy hier: colHiers) {
			viewModel.getRowAxis().add(hier);
		}
		((AxisTreeModel) viewModel.getRowAxis()).expandTheseNodes(colExpanded);
		((AxisTreeModel) viewModel.getColumnAxis()).expandTheseNodes(rowExpanded);
	}
	
	public final void refreshAxis(Axis axis) {
		if (axis == null || viewModel == null) {
			return;
		}
		AxisModel model = viewModel.getAxisModelById(axis.getId());
		if (model == null) {
			return;
		}
		model.refresh();
	}

	public final ViewModel getModel() {
		return viewModel;
	}
	
	public final int getLoadLevel() {
		return loadMarker.getLoadLevel();
	}
	public final void clearLoadLevel() {
		loadMarker.clear();
	}
	
	public final int getVisibleCellCount() {
		List<AxisItem> rows = ViewModelUtils.getVisibleLeafs(viewModel.getRowAxis());
		List<AxisItem> cols = ViewModelUtils.getVisibleLeafs(viewModel.getColumnAxis());
		return rows.size() * cols.size();
	}
	
	public final int getNumberOfCellsToLoad() {
		List<AxisItem> rows = ViewModelUtils.getVisibleLeafs(viewModel.getRowAxis());
		List<AxisItem> cols = ViewModelUtils.getVisibleLeafs(viewModel.getColumnAxis());
		int loadCount = 0;
		for (int r = 0, n = rows.size(); r < n; r++) {
			for (AxisItem colItem : cols) {
				AxisItem rowItem = rows.get(r);
				int rowIndex = rowItem.leafIndex;
				int colIndex = colItem.leafIndex;
				if (!loadMarker.isLoaded(rowIndex, colIndex))
					loadCount++;
			}
		}
		return loadCount;
	}
	
	private final void addAll(LocalFilter lf, ElementNode [] roots) {
		if (roots == null || roots.length == 0) {
			return;
		}
		for (ElementNode n: roots) {
			lf.addVisibleElement(n);
		}		
	}
		
	private final void addAll(ElementNode [] roots, HashMap <ElementNode, Integer> parentId, ArrayList <String> result, HashMap <Element, String> origNodes) {
		for (ElementNode en: roots) {										
			result.add(en.getElement().getId());
			result.add(en.getElement().getName());
			result.add(en.getElement().getTypeAsString());
			if (origNodes.containsKey(en.getElement())) {
				result.add(origNodes.get(en.getElement()));
			} else {
				result.add(CubeViewService.getPath(en));
			}
			if (en.getParent() != null && parentId.containsKey(en.getParent())) {
				result.add("" + parentId.get(en.getParent()));
			} else {
				result.add("-1");
			}
			if (en.hasChildren()) {
				parentId.put(en, parentCounter++);			
				addAll(en.getChildren(), parentId, result, origNodes);
			} else {
				parentCounter++;
			}
		}		
	}
	
	private final void getAllOriginalPaths(ElementNode [] roots) {
		if (roots == null || roots.length == 0) {
			return;
		}
		for (ElementNode en: roots) {
			origNodes.put(en.getElement(), CubeViewService.getPath(en));
			getAllOriginalPaths(en.getChildren());
		}
		initializedOriginalPaths = true;
	}
	
	public final String [] hideItem(XAxisItem xItem, List <XAxisItem> xItemRoots, String axisId, String viewId, boolean hideLevel) {
		AxisModel axis = viewModel.getAxisModelById(axisId);
//		hideLevel = !xItem.isExpanded;
		List <XAxisItem> itemsToHide = new ArrayList<XAxisItem>();
		if (hideLevel) {
			if (xItem.getParent() == null) {				
				if (xItemRoots != null) {
					itemsToHide = xItemRoots;
				} else {
					itemsToHide.add(xItem);
				}
			} else {
				itemsToHide = xItem.getParent().getChildren();
			}
		} else {
			itemsToHide.add(xItem);
		}		
		AxisItem item = getAxisItem(xItem, axis);			
		for (AxisHierarchy ah: axis.getAxisHierarchies()) {
			if (ah.getHierarchy().getId().equals(item.getHierarchy().getId())) {
				LocalFilter lf = ah.getLocalFilter();
				if (lf == null) {
					lf = new LocalFilterImpl();
					ElementNode [] roots = ah.getHierarchy().getElementsTree();					
					addAll(lf, roots);
//					System.err.println("Getting all original paths...");
					getAllOriginalPaths(ah.getHierarchy().getElementsTree());
				} else {					
					if (!initializedOriginalPaths) {
//						System.err.println("Getting all original paths...");
						getAllOriginalPaths(ah.getHierarchy().getElementsTree());
					}
				}
				for (XAxisItem it: itemsToHide) {
					item = getAxisItem(it, axis);
					ElementNode elem = item.getElementNode();
					if (it.isExpanded) {
						ElementNode [] kids = elem.getChildren();
						ElementNode parent = elem.getParent();					
						if (parent == null) {
							int index = lf.indexOf(elem);
							int counter = 0;
							if (index == -1) {
								index = 0;
							}
							for (ElementNode k: kids) {
								ElementNode c = new ElementNode(k.getElement(), k.getConsolidation(), k.getIndex());
								c.setChildren(k.getChildren().clone());
								String path = CubeViewService.getPath(k);							
								elem.removeChild(k);
								c.setParent(null);
								lf.addVisibleElement(c, index + counter++);
//								origNodes.put(c.getElement(), path);
							}
						} else {
							parent = lf.findElementNode(parent);
							int index = parent.indexOf(elem);
							if (index == -1) {
								index = 0;
							}
							int counter = 0;
							for (ElementNode k: kids) {
								ElementNode c = new ElementNode(k.getElement(), k.getConsolidation(), k.getIndex());
								c.setChildren(k.getChildren().clone());
								String path = CubeViewService.getPath(k);							
								elem.removeChild(k);
								c.setParent(parent);
								parent.forceAddChild(c, index + counter++);
//								origNodes.put(c.getElement(), path);
							}
						}
						lf.removeVisibleElement(elem);					
					} else {
						lf.removeVisibleElement(elem);
					}
				}
				ah.setLocalFilter(lf);
				
				ArrayList <String> result = new ArrayList<String>();
				parentCounter = 0;
				HashMap <ElementNode, Integer> parentId = new HashMap<ElementNode, Integer>();
				addAll(lf.getVisibleElements(), parentId, result, origNodes);
				return result.toArray(new String[0]);
			}
		}
		return null;
	}
	
	/**
	 * Starts an expand action for the given {@link XAxisItem} within 
	 * {@link AxisModel} specified by the given axis id.
	 * @param xItem
	 * @param axisId
	 * @return
	 */
	public final int willExpand(XAxisItem xItem, String axisId) {
		AxisModel axis = viewModel.getAxisModelById(axisId);
		AxisItem item = getAxisItem(xItem, axis);
		return willExpand(xItem, item, axis);
	}
	
	private final int willExpand(XAxisItem xItem, AxisItem item, AxisModel axis) {
		if(item == null)
			return 0;
		
		int insertIndex = getLastChildLeafIndex(item) + 1;
		boolean isFirstExpand = expandInModel(item, axis);
		boolean isColumnExpand = isColumn(axis);
		calculateLeafs(isColumnExpand);
				
		if(isFirstExpand) {
			//on first expand we have to recalculate leaf indexes...
			ViewModelUtils.determineLeafIndexes(axis); //TODO this should be done by AxisModel!!!!
			//... and enlarge load marker:
			extendLoadMarker(insertIndex, isColumnExpand);
		}
		
		//determine cells to load:
		List<ViewCell> cellsToLoad = getCellsToLoad(rowLeafs, columnLeafs);
						
		// push expand info on expand stack:
		Expanded expand = new Expanded(xItem, item, isFirstExpand, insertIndex, axis,
				isColumnExpand);
		expand.setCellsToLoad(cellsToLoad, loadMarker.getRowCount(), loadMarker
				.getColumnCount(), loadedRows, loadedColumns);
		expandStack.push(expand);
		return cellsToLoad.size();
	}
	private final AxisItem getAxisItem(XAxisItem xItem, AxisModel axis) {
		return axis.getItem(xItem.getPath(), xItem.index);		
	}
	private final int getLastChildLeafIndex(AxisItem item) {
		while (item.hasRootsInNextHierarchy()) {
			AxisItem[] rootsNxtHier = item.getRootsInNextHierarchy();
			item = rootsNxtHier[rootsNxtHier.length - 1];
			item = getLastChild(item);
		}
		return item.leafIndex;
	}
	private final AxisItem getLastChild(AxisItem item) {
		AxisItem[] children = item.getChildren();
		if(children != null && children.length > 0)
			return getLastChild(children[children.length - 1]);
		return item;
	}
	/** returns <code>true</code> if item was expanded first time */
	private final boolean expandInModel(AxisItem item, AxisModel axis) {
		// before expand item, since expand will load item, i.e. after expand
		// item is cached!
		boolean firstTime = !item.hasState(AxisItem.CACHED);
		// expand item in model:
		axis.expand(item);
		return firstTime;
	}
	private final boolean isColumn(AxisModel axis) {
		return axis.getAxis().getId().equals(CubeView.COLUMN_AXIS);
	}
	private final void calculateLeafs(boolean expandedColumn) {
		AxisModel otherAxis = expandedColumn ? viewModel.getRowAxis()
				: viewModel.getColumnAxis();
		AxisItem[][] expandedItems = viewModel.getExpandedItems();
		if (expandedItems != null && expandedItems.length > 0) {
			AxisItem[] roots = expandedItems[0];
			// expanded cells:
			rowLeafs = expandedColumn ? ViewModelUtils
					.getVisibleLeafs(otherAxis) : ViewModelUtils
					.getVisibleLeafs(roots);
			columnLeafs = expandedColumn ? ViewModelUtils
					.getVisibleLeafs(roots) : ViewModelUtils
					.getVisibleLeafs(otherAxis);
		}
	}
	private final List<ViewCell> getCellsToLoad(List<AxisItem> rows,
			List<AxisItem> columns) {
		loadedRows = -1;
		int rowCount = 0;
		List<ViewCell> cellsToLoad = new ArrayList<ViewCell>();
		for (int r = 0, n = rowLeafs.size(); r < n; r++) {
			loadedColumns = 0;
			for (AxisItem column : columns) {
				AxisItem rowItem = rowLeafs.get(r);
				int rowIndex = rowItem.leafIndex;
				int colIndex = column.leafIndex;
				if (!loadMarker.isLoaded(rowIndex, colIndex)) {
					ViewCell cell = new ViewCell();
					cell.row = rowItem;
					cell.column = column;
					cellsToLoad.add(cell);
					loadedColumns++;
					if(loadedRows == -1)
						loadedRows = r; //first insert
				}
			}
			rowCount++;
		}
		loadedRows = rowCount - loadedRows;
		return cellsToLoad;
	}
	
	
	/**
	 * Proceeds with expand action, i.e. marks all cells as loaded which need
	 * to be loaded due to this expand
	 * @return the cells to load
	 */
	public final Expanded proceedExpand() {
		//simply pop last expand from stack:
		Expanded expanded = expandStack.pop();
		markLoadedCells(expanded.getCellsToLoad());
		return expanded;
	}
	
	private final void markLoadedCells(List<ViewCell> cells) {
		for(ViewCell cell : cells) {
			loadMarker.loaded(cell.row.leafIndex, cell.column.leafIndex, true);
		}
	}

	/**
	 * Cancels a former started expand action, i.e. the expanded item is 
	 * collapsed again and unloaded if it was loaded during expand
	 */
	public final void cancelExpand() {
		//simply pop last expand from stack:
		Expanded expand= expandStack.pop();
		undo(expand);
	}
	private final void undo(Expanded expand) {
		//we have to
		collapse(expand.getItem(), expand.getAxis());
		if(expand.firstTime()) {
			//we have to take back marker extension:
			AxisItem item = expand.getItem(); //getAxisItem(expand.getXItem(), expand.getAxis());
			if(item != null) {
				item.removeChildren(); //this marks item as uncached too!!
			}
			shrinkLoadMarker(expand);
			ViewModelUtils.determineLeafIndexes(expand.getAxis());
		}		
	}
	private final void collapse(AxisItem item, AxisModel axis) {
		axis.collapse(item);
	}
	private final void shrinkLoadMarker(Expanded expand) {
		//adjust load marker:
		if(expand.inColumn())
			loadMarker.removeColumns(expand.insertIndex(), expand.getLoadedColumns());
		else
			loadMarker.removeRows(expand.insertIndex(), expand.getLoadedRows());
	}

	/**
	 * Starts a collapse action for the given {@link XAxisItem} within 
	 * {@link AxisModel} specified by the given axis id.
	 * @param xItem
	 * @param axisId
	 */
	public final void willCollapse(XAxisItem xItem, String axisId) {
		AxisModel axis = viewModel.getAxisModelById(axisId);
		AxisItem item = getAxisItem(xItem, axis);
		if(item == null)
			return;	
		axis.collapse(item);
		Collapsed collapse = new Collapsed(xItem, item, axis);		
		collapseStack.push(collapse);
	}
	/**
	 * Proceed with a formerly started cancel action
	 */
	public final Collapsed proceedCollapse() {
		return collapseStack.pop();
	}
	/**
	 * Cancels a formerly started collapse action
	 */
	public final void cancelCollapse() {
		Collapsed collapse = collapseStack.pop();
		//we simply expand collapsed item in axis
		collapse.getAxis().expand(collapse.getItem());
	}
	
	
//	public final int willSetExpandState(String axisId, XAxisItem[] rootItems, int expandDepth) {
	public final int willSetExpandState(XAxisItem[] expanded, XAxisItem[] collapsed, String axisId, int expandDepth) {
		if(!expandStack.isEmpty())
			System.err.println("some expand actions not finished!!");
		if(!collapseStack.isEmpty())
			System.err.println("some collapse actions not finished!!");
		expandStack.clear();
		collapseStack.clear();
		
		AxisModel axis = viewModel.getAxisModelById(axisId);
		//we first collapse...
		for(XAxisItem xItem : collapsed) {
			AxisItem item = axis.getItem(xItem.getPath(), xItem.index);
			willCollapse(xItem, item, axis);
		}
		//..then expand up to depth:
		int cells = 0;
		for(XAxisItem xItem : expanded) {
			AxisItem item = axis.getItem(xItem.getPath(), xItem.index);
			cells += willExpand(xItem, item, axis, expandDepth);
		}
		return cells;
	}
	
	private final int willExpand(XAxisItem xItem, AxisItem item,
			AxisModel axis, int expandDepth) {
		int cells = 0;
		if (item != null && item.getLevel() < expandDepth && item.hasChildren()) {
			cells = willExpand(xItem, item, axis);
			if(xItem != null && xItem. isLoaded)
				return cells; //when item is loaded its unloaded children are expanded by another call of willExpand()!!!
			for (AxisItem child : item.getChildren())
				cells += willExpand(null, child, axis, expandDepth);
		} else
			willCollapse(xItem, item, axis); 
		return cells;
	}
	private final void willCollapse(XAxisItem xItem, AxisItem item, AxisModel axis) {
		axis.collapse(item);
		Collapsed collapse = new Collapsed(xItem, item, axis);
		collapseStack.push(collapse);
	}
	
	public final List<Expanded> proceedSetExpandStateExpanded() {
		List<Expanded> expanded = new ArrayList<Expanded>();
		while(!expandStack.isEmpty()) {
			expanded.add(0,proceedExpand());
		}
		return expanded;
	}
	public final List<Collapsed> proceedSetExpandStateCollapsed() {
		List<Collapsed> collapsed = new ArrayList<Collapsed>();
		while(!collapseStack.isEmpty())
			collapsed.add(proceedCollapse());
		return collapsed;
	}
	public final void cancelSetExpandState() {
		//we first collapse expanded...
		while(!expandStack.isEmpty())
			undo(expandStack.pop());
		
		//...and then expand collapsed:
		while(!collapseStack.isEmpty())
			undo(collapseStack.pop());
	}
	private final void undo(Collapsed collapsed) {
		collapsed.getAxis().expand(collapsed.getItem());
	}
}

class ViewCell {
	AxisItem row;
	AxisItem column;
}

class Expanded {
	private final boolean column;
	private final int insertIndex;
	private final XAxisItem xItem;
	private final AxisModel axis;
	private final AxisItem item;
	private final boolean firstTime;
	private int rows;
	private int columns;
	private int loadedRows;
	private int loadedColumns;
	private final List<ViewCell> cells = new ArrayList<ViewCell>();
	
	Expanded(XAxisItem xItem, AxisItem item, boolean firstTime, int insertIndex, AxisModel axis,
			boolean column) {				
		this.axis = axis;
		this.item = item;
		this.xItem = xItem;
		this.column = column;
		this.firstTime = firstTime;
		this.insertIndex = insertIndex;
	}
	
	final boolean firstTime() {
		return firstTime;
	}
	
	final AxisModel getAxis() {
		return axis;
	}
	
	final AxisItem getItem() {
		return item;
	}
	
	final XAxisItem getXItem() {
		return xItem;
	}
	
	final boolean inColumn() {
		return column;
	}
	
	final int insertIndex() {
		return insertIndex;
	}
	
	final void setCellsToLoad(List<ViewCell> cells, int rows, int columns,
			int loadedRows, int loadedColumns) {
		this.cells.clear();
		this.cells.addAll(cells);
		this.rows = rows;
		this.columns = columns;
		this.loadedRows = loadedRows;
		this.loadedColumns = loadedColumns;
	}
	final int getRows() {
		return rows;
	}
	final int getColumns() {
		return columns;
	}
	final int getLoadedRows() {
		return loadedRows;
	}
	final int getLoadedColumns() {
		return loadedColumns;
	}
	
	final List<ViewCell> getCellsToLoad() {
		return cells;
	}
	
	public String toString() {
		return "Expanded "+item.toString();
	}
}
class Collapsed {
	private final XAxisItem xItem;
	private final AxisItem item;
	private final AxisModel axis;
	
	Collapsed(XAxisItem xItem, AxisItem item, AxisModel axis) {
		this.axis = axis;
		this.item = item;
		this.xItem = xItem;
	}
	
	final AxisModel getAxis() {
		return axis;
	}
	final AxisItem getItem() {
		return item;
	}
	final XAxisItem getXItem() {
		return xItem;
	}
	
	public String toString() {
		return "Collapsed "+item.toString();
	}

}
