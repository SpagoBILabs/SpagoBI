/*
*
* @file XCellCollection.java
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
* @version $Id: XCellCollection.java,v 1.10 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tensegrity.palo.gwt.core.client.models.palo.XCell;

/**
 * <code>XCellDelta</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XCellCollection.java,v 1.10 2010/03/11 10:42:20 PhilippBouillon Exp $
 **/
public class XCellCollection implements Serializable {

	/** generated */
	private static final long serialVersionUID = -4932545144233326275L;
	
	private int rowCount;
	private int loadedRows;
	private int columnCount;
	private int loadedColumns;
	private int visibleCellCount;
	
	private int insertIndex;
	private List<XCell> cells = new ArrayList<XCell>();
	
	//empty rows and columns
	private Set<Integer> emptyRowIndex = new HashSet<Integer>();
	private Set<Integer> emptyColumnIndex = new HashSet<Integer>();

	public XCellCollection() {
	}

	public XCellCollection(int insertIndex) {
		this.insertIndex = insertIndex;
	}
	
	public final int getInsertIndex() {
		return insertIndex;
	}
	
	public final int getRowCount() {
		return rowCount;
	}
	public final void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	public final int getColumnCount() {
		return columnCount;
	}
	public final void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}
	
	public final int getLoadedRowsCount() {
		return loadedRows;
	}

	public final void setLoadedRowsCount(int rows) {
		this.loadedRows = rows;
	}
	
	public final int getLoadedColumnsCount() {
		return loadedColumns;
	}

	public final void setLoadedColumnsCount(int cols) {
		this.loadedColumns = cols;
	}

	public final int getVisibleCellCount() {
		return visibleCellCount;
	}

	public final void setVisibleCellCount(int visibleCellCount) {
		this.visibleCellCount = visibleCellCount;
	}

	public final boolean isEmpty() {
		return cells.isEmpty();
	}
	
	public final void clear() {
		cells.clear();
		emptyRowIndex.clear();
		emptyColumnIndex.clear();
	}
	
	/**
	 * Adds the given cell model to this load delta.
	 * @param cell the cell model to add
	 */
	public final void add(XCell cell) {
		cells.add(cell);
	}
	public final void add(XCellCollection cells) {
		this.cells.addAll(cells.getCells());
		rowCount = cells.rowCount;
		loadedRows = cells.loadedRows;
		columnCount = cells.columnCount;
		loadedColumns = cells.loadedColumns;

	}
	/**
	 * Returns a list of all cell models which where added during loading
	 * @return a list of all loaded cells
	 */
	public final List<XCell> getCells() {
		return cells;
	}

	public final void setCells(List <XCell> cells) {
		this.cells = cells;
	}
	
	public final void addEmptyRow(int index) {
		emptyRowIndex.add(index);
	}
	public final void addEmptyColumn(int index) {
		emptyColumnIndex.add(index);
	}
	public final Set<Integer> getEmptyRows() {
		return emptyRowIndex;
	}
	public final Set<Integer> getEmptyColumns() {
		return emptyColumnIndex;
	}
	
	public final int size() {
		return cells.size();
	}
}
