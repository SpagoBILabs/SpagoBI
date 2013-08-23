/*
*
* @file Content.java
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
* @version $Id: Content.java,v 1.56 2010/04/12 11:14:29 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.palotable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellCollection;
import com.tensegrity.palo.gwt.core.client.models.palo.XCell;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.HeaderItem;
import com.tensegrity.palo.gwt.widgets.client.util.Ruler;


public class Content extends AbsolutePanel implements SourcesClickEvents {

	private static final String CELL_INDEX = "cell_index";
	private static final int BASE_WIDTH = 6;
	
	public static int MAX_COLUMN_WIDTH = 100;
	public static int MAX_ROWS_COL_WIDTH = 100;
	public static String FILL_STRING = "#####";
	
	private int colCount;
	private int rowCount;
	private boolean markRules;
	private List<Integer> maxWidthPerColumn = new ArrayList<Integer>();
	private ClickListenerCollection clickListeners;

	private ArrayList<Cell> allCells = new ArrayList<Cell>();
//	private Map<Integer, Cell> cells = new HashMap<Integer, Cell>();

	private HashSet<Cell> visibleCells = new HashSet<Cell>();
	private List<Integer> visibleColumns = new ArrayList<Integer>();
	private int y;
	private final TableContent tableContent;
	
	public Content(TableContent tc) {
		tableContent = tc;
		setWidth("100%");
		setHeight("100%");
		sinkEvents(Event.ONDBLCLICK);
		DOM.setStyleAttribute(getElement(), "display", "inline");		
	}

	
	public final int getCellCount() {
		return allCells.size();
	}
	
	public final void markRules(boolean doIt) {
		markRules = doIt;
	}
	
	public final int getColumnCount() {
		return colCount;
	}
	
//	public final void layout(boolean colrev, boolean rowrev) {
//		for(Cell cell : allCells)
//			cell.setVisible(false);
//		
//		int visibleCols = visibleColumns.size();
//		int visibleRows = visibleCells.size() / visibleCols;
//		int col = colrev ? visibleCols - 1 : 0;
//		int row = rowrev ? visibleRows - 1 : 0;		
//		for(Cell cell : visibleCells) {
//			cell.setVisible(true);
//			cell.layout();
//			cell.setWriteBackCol(col);
//			cell.setWriteBackRow(row);
//			if (colrev) {
//				col--;
//				if (col == -1) {
//					col = visibleCols - 1;
//					if (rowrev) {
//						row--;
//					} else {
//						row++;
//					}
//				}
//			} else {
//				col++;	
//				if (col == visibleCols) {
//					col = 0;
//					if (rowrev) {
//						row--;
//					} else {
//						row++;
//					}
//				}
//			}
//		}		
////		for(Cell cell : cells.values()) {
////			if(cell != null)
////				cell.setVisible(false);
////		}
////		for(Cell cell : visibleCells) {
////			cell.setVisible(true);
////			cell.layout();
////		}		
//	}
	
	public final void layout() {
		for(Cell cell : allCells) {
			if (visibleCells.contains(cell)) {
				cell.setVisible(true);
				cell.markRule(markRules);
				cell.layout();				
			} else {
				cell.setVisible(false);
			}
		}
	}

	public final void removeCells() {
//		for(Cell cell : cells.values()) {
//			cell.removeFromParent();
//		}
//		cells.clear();
		for(Cell cell : allCells)
			cell.removeFromParent();
		allCells.clear();
		rowCount = 0;
		colCount = 0;
	}
	
	public final void reset() {
		removeCells();
		reset(true);
		reset(false);
		colCount = 0;
		maxWidthPerColumn.clear();
	}
	public final void reset(boolean row) {
		visibleCells.clear();
		if (!row)
			visibleColumns.clear();
		y = 0;
	}
	public final void initWithCurrentState() {
		Iterator<Cell> cellsIterator = allCells.iterator();
		while(cellsIterator.hasNext()) {
			Cell cell = cellsIterator.next();
			if(!cell.isVisible()) {
				cell.removeFromParent();
				cellsIterator.remove();
			}
		}
	}
	/**
	 * Inserts the given {@link Cell}s at the specified index within the table.
	 * Specify <code>true</code> for <code>isRow</code> parameter if the cells 
	 * represent one or more rows otherwise <code>false</code>.
	 * @param cells the cells to be inserted
	 * @param atIndex the insert index within this table
	 * @param cols specifies the number of columns in case of a column
	 * insert. A value less or equal zero represents a row insert of cells
	 * @param isRow <code>true</code> if cells represent on or more rows,
	 * <code>false</code> otherwise
	 */
	public final void insert(XCellCollection cells, boolean colInsert) {		
		amend(cells, colInsert);
		if(colInsert) {
			//PR 687: must initialize row count since on start we come here with column insert...
			rowCount = cells.getRowCount();			
			insertColumns(cells);
		} else {
			insertRows(cells);
		}
	}
		
	private final void amend(XCellCollection cells, boolean colInsert) {
		int lVal = cells.getColumnCount();
		Set <Integer> usedIndices = new HashSet<Integer>();
		
		ArrayList <XCell> toBeRemoved = new ArrayList<XCell>();
		for (XCell cell: cells.getCells()) {			
			int index = cell.row * lVal + cell.col;
			if (usedIndices.contains(index)) {
				toBeRemoved.add(cell);
			}
			usedIndices.add(index);
		}
		if (!toBeRemoved.isEmpty()) {
			List <XCell> c = cells.getCells();
			for (XCell d: toBeRemoved) {
				c.remove(d);
				if (colInsert) {
					cells.setLoadedColumnsCount(cells.getLoadedColumnsCount() - 1);
				} else {
					cells.setLoadedRowsCount(cells.getLoadedRowsCount() - 1);
				}
			}
			cells.setCells(c);
		}
//		if (colInsert) {
////		cells.setLoadedColumnsCount(cells.getCells().size());
//		} else {
////			cells.setLoadedRowsCount(cells.getCells().size());
//		}
	}
	
//	private final void insertRows(XCellCollection loadedCells) {
//		int before = cells.size();
//		int newRowCount = loadedCells.getRowCount();
//		//do we need to transform old cells:
//		if(newRowCount != rowCount) {
//			int insertIndex = loadedCells.getInsertIndex();
//			int loadedRows = loadedCells.getLoadedRowsCount();
//			for (int r = rowCount; r >= 0; --r) {
//				for (int c = colCount; c >= 0; --c) {
//					// old index:
//					int oldIndex = r * colCount + c;
//					// new index:
//					int row = r < insertIndex ? r : r + loadedRows;
//					int newIndex = row * colCount + c;
//					Cell cell = cells.get(oldIndex);
//					if(cell!= null) {
//						DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX, insertIndex);
//						// just for debug purpose we check correct index:
//						if(cells.put(newIndex, cell)!=null)
//					} else
//				}
//			}			
//		}
//		//insert new loaded cells:
//		for(XCell xCell : loadedCells.getCells()) {
//			Cell cell = new Cell(xCell);
//			super.add(cell, 0, 0);					
//			int insertIndex = cell.getRow() * colCount + cell.getColumn();
//			DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX, insertIndex);
//			if(cells.put(insertIndex, cell)!=null)
//		}
//		rowCount = newRowCount;
//	}
//
//	private final void insertColumns(XCellCollection loadedCells) {
//		int before = cells.size();
//		int newColCount = loadedCells.getColumnCount();
//		//do we need to transform old cells:
//		if(newColCount != colCount) {
//			int insertIndex = loadedCells.getInsertIndex();
//			int loadedColumns = loadedCells.getLoadedColumnsCount();
//			for (int r = rowCount; r >= 0; --r) {
//				for (int c = colCount; c >= 0; --c) {
//					// old index:
//					int oldIndex = r * colCount + c;
//					// new index:
//					int col = c < insertIndex ? c : c + loadedColumns;
//					int newIndex = r * newColCount + col;
//					Cell cell = cells.get(oldIndex);
//					if(cell!= null) {
//						setMaxWidthPerColumn(cell.getColumn(), cell.getOffsetWidth() + 4);
//						DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX, insertIndex);
//						// just for debug purpose we check correct index:
//						if(cells.put(newIndex, cell)!=null)
//					} else
//				}
//			}			
//		}
//		//insert new loaded cells:
//		for(XCell xCell : loadedCells.getCells()) {
//			Cell cell = new Cell(xCell);
//			super.add(cell, 0, 0);					
//			int insertIndex = cell.getRow() * colCount + cell.getColumn();
//			setMaxWidthPerColumn(cell.getColumn(), cell.getOffsetWidth() + 4);
//			DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX, insertIndex);
//			if(cells.put(insertIndex, cell)!=null)
//		}
//		colCount = newColCount;
//	}

	private final void insertRows(XCellCollection loadedCells) {
//		int before = allCells.size();
		int insertIndex = loadedCells.getInsertIndex();
		int loadedRows = loadedCells.getLoadedRowsCount();
		//adjust old cell indexes:
		if (rowCount != loadedCells.getRowCount()) {
			for (Cell cell : allCells) {
				// old index:
				int row = cell.getRow();
				int col = cell.getColumn();
				// new index:
				row = row < insertIndex ? row : row + loadedRows;
				cell.setRow(row);
				int newIndex = row * colCount + col;				
				DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX,
						newIndex);
			}
		}
		for(XCell xCell : loadedCells.getCells()) {
			Cell cell = new Cell(xCell);
			super.add(cell, 0, 0);					
			int atIndex = cell.getRow() * colCount + cell.getColumn();
			int wdth = cell.getXCell().type == XCell.TYPE_NUMERIC ?
					cell.getValue().length() * BASE_WIDTH :
					cell.getOffsetWidth() + 4;
			cell.setInternalWidth(wdth);
			setMaxWidthPerColumn(cell.getColumn(), wdth);				
			DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX, atIndex);
			allCells.add(cell);
		}
		rowCount = loadedCells.getRowCount();
	}

	private final void insertColumns(XCellCollection loadedCells) {
//		int before = allCells.size();		
		int newColCount = loadedCells.getColumnCount();
		int insertIndex = loadedCells.getInsertIndex();
		int loadedColumns = loadedCells.getLoadedColumnsCount(); // .getCells().size();

		if (newColCount != colCount) {
			for (Cell cell : allCells) {
				int row = cell.getRow();
				int col = cell.getColumn();
				// new index:
				if (col >= insertIndex) {					
					int cWidth;
					if ((cWidth = maxWidthPerColumn.get(col)) != 0) {
						setMaxWidthPerColumn(col + loadedColumns, cWidth);
						maxWidthPerColumn.set(col, 0);
					}
				}
				col = col < insertIndex ? col : col + loadedColumns;
				cell.setColumn(col);
				int wdth = cell.getXCell().type == XCell.TYPE_NUMERIC ?
						cell.getValue().length() * BASE_WIDTH :
						cell.getOffsetWidth() + 4;
				setMaxWidthPerColumn(cell.getColumn(), wdth);				
				int newIndex = row * newColCount + col;
				DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX,
						newIndex);
			}
		}
		for(XCell xCell : loadedCells.getCells()) {
			Cell cell = new Cell(xCell);
			super.add(cell, 0, 0);	
			int atIndex = cell.getRow() * newColCount + cell.getColumn();
			int wdth = cell.getXCell().type == XCell.TYPE_NUMERIC ?
					cell.getValue().length() * BASE_WIDTH :
					cell.getOffsetWidth() + 4;
		    cell.setInternalWidth(wdth);
			setMaxWidthPerColumn(cell.getColumn(), wdth);
			DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX, atIndex);
			allCells.add(cell);
		}
		colCount = newColCount;
	}
	
	public final void visitedColumnLeaf(HeaderItem item) {
		if(!item.isVisible() || item.isHidden())
			return;
		
		int itemWidth = item.getInnerWidth();
		Integer width = getMaxWidth(item.getLeafIndex());
		if(width != null) {
			int w = width.intValue();
			if(w > itemWidth) {
				itemWidth = w;
				item.setInnerWidth(itemWidth, true);
			} else if ( w <= itemWidth)
				setMaxWidthPerColumn(item.getLeafIndex(), itemWidth);
		} else
			setMaxWidthPerColumn(item.getLeafIndex(), itemWidth);
		
		visibleColumns.add(item.getLeafIndex());
	}

	public final void visitedRowLeaf(HeaderItem item) {
		if(!item.isVisible() || item.isHidden())
			return;
		int h = item.getInnerHeight();		
		layoutRow(item.getLeafIndex(), h);
		y += h;
	}
	private final void layoutRow(int row, int h) {
		int x = 0;
		int borderOffset = Ruler.getBorderOffset();
		h -= borderOffset;
		for(Integer col : visibleColumns) {
			Cell cell = getVisibleCell(row, col);
			if(cell == null) {
				System.out.println("CANNOT FIND VISIBLE CELL ("+row+","+col+")");
				continue;
			}
			Ruler.setPosition(cell, x, y);
			int width = getMaxWidth(col);
			Ruler.setSize(cell, width - borderOffset, h);
//			cell.markRule(markRules);
			x += width;	
		}
	}
	private final Cell getVisibleCell(int row, int col) {
		int cellIndex = row * colCount + col;
		Cell cell = findCell(row, col);
		if (cell != null) {
			DOM.setElementPropertyInt(cell.getElement(), CELL_INDEX, cellIndex);
			visibleCells.add(cell);
		}
		return cell;
	}
	
	private final Cell findCell(int row, int col) {		
		for(Cell cell : allCells) {
			if(cell.getRow() == row && cell.getColumn() == col)
				return cell;
		}
		return null;	
	}
	
	private final void setMaxWidthPerColumn(int col, int width) {
		if (col < 0) {
			return;
		}
		if (width > MAX_COLUMN_WIDTH) {
			width = MAX_COLUMN_WIDTH;
		}
		if(col >= maxWidthPerColumn.size())
			fillMaxWidth(col);
		Integer _width = maxWidthPerColumn.get(col);		
		if(_width == null || width > _width) {
			maxWidthPerColumn.set(col, width);
		}
	}
	private final void fillMaxWidth(int upTo) {
		int additionals = upTo - maxWidthPerColumn.size() + 1;
		for(int i=0;i<additionals;++i)
			maxWidthPerColumn.add(0);
	}
	private final int getMaxWidth(int column) {
		if(column >= maxWidthPerColumn.size())
			return 0;
		return maxWidthPerColumn.get(column);
	}

	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
			case Event.ONDBLCLICK:
				Element source = event.getTarget();
				int cellIndex = getCellIndex(source);
				if(cellIndex > -1) { // && cellIndex < allCells.length) {
					//Cell _cell = cells.get(cellIndex); //allCells[cellIndex]; //cells.get(cellIndex);
					Cell _cell = findCell(cellIndex);
					if (_cell != null)
						clickListeners.fireClick(_cell);
				}
		}
	}

	private final Cell findCell(int cellIndex) {
		int visCols = visibleColumns.size();
		if (tableContent != null && tableContent.isHideEmpty()) {
			visCols += tableContent.getEmptyColumns();
		}
		for (Cell cell : visibleCells) {
			int index = cell.getRow() * visCols + cell.getColumn();
			if (index == cellIndex) {
				return cell;
			}
		}
		return null;
	}

	private final int getCellIndex(Element element) {
		if(element == null)
			return -1;
		if(hasProperty(element, CELL_INDEX))
			return element.getPropertyInt(CELL_INDEX);
		return getCellIndex(element.getParentElement());
	}
	
	private final boolean hasProperty(Element element, String prop) {
		return element.getPropertyString(prop) != null;
	}

	public void addClickListener(ClickListener listener) {
		if (clickListeners == null) {
			clickListeners = new ClickListenerCollection();
		}
		clickListeners.add(listener);
	}

	public void removeClickListener(ClickListener listener) {
		if (clickListeners != null) {
			clickListeners.remove(listener);
		}
	}
	
	public int getVisibleCellCount() {
		return visibleCells.size();
	}
}
