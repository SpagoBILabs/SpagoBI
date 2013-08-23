/*
*
* @file TableContent.java
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
* @version $Id: TableContent.java,v 1.23 2010/03/11 10:42:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client.palotable;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XCellCollection;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.HeaderItem;

/**
 * <code>TableContent</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: TableContent.java,v 1.23 2010/03/11 10:42:18 PhilippBouillon Exp $
 **/
public class TableContent extends ScrollPanel implements ClickListener {

	private static final String STYLE = "content";
	private Set<Integer> emptyRows = new HashSet<Integer>();
	private Set<Integer> emptyColumns = new HashSet<Integer>();
	private boolean hideEmpty;
	
	private final Content content = new Content(this);
	private final CellEditor cellEditor = new CellEditor();

	public TableContent() {
		initComponent();
		initEventHandling();
	}
	
	public final int getCellCount() {
		return content.getCellCount();
	}
	
	public final int previewHideEmptyCells(boolean doIt) {
		if (!doIt) {
			return content.getCellCount();
		} else {
			int totalCells = content.getCellCount();
			int emptyCells = emptyRows.size() * content.getColumnCount();
			emptyCells += (emptyColumns.size() * (totalCells / content.getColumnCount() - emptyRows.size()));
			return totalCells - emptyCells;
		}
	}
	
	public final void hideEmptyCells(boolean doIt) {
		hideEmpty = doIt;
	}

	public final int getEmptyColumns() {
		return emptyColumns.size();
	}
	
	public final void setCells(XCellCollection cells) {
		emptyRows.clear();
		emptyColumns.clear();
		content.removeCells();
		insertCells(cells, true);
		
		//add empty rows and columns:
		emptyRows.addAll(cells.getEmptyRows());
		emptyColumns.addAll(cells.getEmptyColumns());
	}
	public final void insertCells(XCellCollection cells, boolean colInsert) {
		content.insert(cells, colInsert);
		this.emptyRows = cells.getEmptyRows();
		this.emptyColumns = cells.getEmptyColumns();
	}
	
	public final void addCellChangedListener(CellChangedListener listener) {
		cellEditor.addCellChangedListener(listener);
	}
	
	public final void markRules(boolean doIt) {
		content.markRules(doIt);
	}
	
	/** layout inner content */
	public final void layout() {
		content.layout();		
	}
	
	public final void reset() {
		content.reset();
		emptyRows.clear();
		emptyColumns.clear();
	}

	public final void reset(boolean row) {
		content.reset(row);
	}
	
	public final void initWithCurrentState() {
		content.initWithCurrentState();
	}
	public final void setContentSize(int width, int height) {
		content.setPixelSize(width, height);
	}
	
	public final boolean isEmptyRow(int rowIndex) {
		return emptyRows.contains(rowIndex);
	}
	public final boolean isEmptyColumn(int colIndex) {
		return emptyColumns.contains(colIndex);
	}

	public final void visitedRowLeaf(HeaderItem item) {		
		boolean hideMe = hideEmpty && emptyRows.contains(item.getLeafIndex());
		item.hide(hideMe);
		content.visitedRowLeaf(item);
	}
	public final void visitedColumnLeaf(HeaderItem item) {		
		boolean hideMe = hideEmpty && emptyColumns.contains(item.getLeafIndex());
		item.hide(hideMe);
		content.visitedColumnLeaf(item);
	}
	
	private final void initComponent() {
		add(content);
		content.add(cellEditor.getComponent());
		//style:
		setStyleName(STYLE);
	}
	
	private final void initEventHandling() {
		content.addClickListener(this);
	}

	private final void adjustIndices(Set<Integer> emptyIndizes, int atIndex, int offset) {
		Set<Integer> newIndizes = new HashSet<Integer>();
		for(Integer index : emptyIndizes) {
			int oldIndex = index.intValue();
			if(oldIndex >= atIndex)
				newIndizes.add((oldIndex + offset));
			else
				newIndizes.add(oldIndex);
		}
		emptyIndizes.clear();
		emptyIndizes.addAll(newIndizes);		
	}
	
	private final void adjustIndices(Set<Integer> emptyIndizes, int offsetIndex) {
		Set<Integer> newIndizes = new HashSet<Integer>();
		for(Integer index : emptyIndizes) {
			int oldIndex = index.intValue();
			newIndizes.add((oldIndex + offsetIndex));
		}
		emptyIndizes.clear();
		emptyIndizes.addAll(newIndizes);		
	}


	public void onClick(Widget sender) {
		Cell cell = (Cell)sender;
		cellEditor.setSize(cell.getOffsetWidth(), cell.getOffsetHeight());
		content.setWidgetPosition(cellEditor.getComponent(), content.getWidgetLeft(cell), content.getWidgetTop(cell));
		cellEditor.edit(cell);
	}
	
	public boolean isHideEmpty() {
		return hideEmpty;
	}
}
