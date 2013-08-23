/*
*
* @file XViewModel.java
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
* @version $Id: XViewModel.java,v 1.12 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.util.ArrayList;
import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>XViewModel</code>
 * A cube view model representation for GWTs RPC feature.
 *
 * @version $Id: XViewModel.java,v 1.12 2010/03/11 10:42:20 PhilippBouillon Exp $
 **/
public class XViewModel extends XObject {

	//a view consists of:
	private List<XAxisItem> rowRoots = new ArrayList<XAxisItem>();
	private List<XAxisItem> colRoots = new ArrayList<XAxisItem>();
	private String [] warnings;
	private XCellCollection cells;
	private XAxis rowAxis;
	private XAxis columnAxis;
	private XAxis selectionAxis;
	private XAxis repositoryAxis;
	private boolean hideEmptyCells;
	private boolean columnsReversed;
	private boolean rowsReversed;
	private boolean showRules;
	private String externalId;
	private String ownerId;
	private boolean needsRestore;
	
	public XViewModel() {		
	}
	/**
	 * Creates a new {@link XViewModel} instance with the specified id and name.
	 * @param id the cube view id
	 * @param name the cube view name
	 */
	public XViewModel(String id, String name) {
		setId(id);
		setName(name);
	}
	
	public String [] getWarnings() {
		if (warnings == null) {
			return new String[0];
		}
		return warnings;
	}
	
	public void setWarnings(String [] warnings) {
		this.warnings = warnings;
	}
	
	public final void clear() {
		rowRoots.clear();
		colRoots.clear();
		cells.clear();
	}
	
	public final void addRowAxis(XAxis axis) {
		rowAxis = axis;
	}
	public final void addColumnAxis(XAxis axis) {
		columnAxis = axis;
	}
	public final void addPovAxis(XAxis axis) {
		selectionAxis = axis;
	}
	public final void addRepositoryAxis(XAxis axis) {
		repositoryAxis = axis;
	}
		
	public final XAxisHierarchy getAxisHierarchy(String id) {
		for (XAxisHierarchy hier: rowAxis.getAxisHierarchies()) {
			if (id.equals(hier.getId())) return hier;
		}
		for (XAxisHierarchy hier: columnAxis.getAxisHierarchies()) {
			if (id.equals(hier.getId())) return hier;
		}
		for (XAxisHierarchy hier: selectionAxis.getAxisHierarchies()) {
			if (id.equals(hier.getId())) return hier;
		}
		for (XAxisHierarchy hier: repositoryAxis.getAxisHierarchies()) {
			if (id.equals(hier.getId())) return hier;
		}		
		return null;
	}
	
	public final XAxis getRowAxis() {
		return rowAxis;
	}
	public final XAxis getColumnAxis() {
		return columnAxis;
	}
	public final XAxis getSelectionAxis() {
		return selectionAxis;
	}
	public final XAxis getRepositoryAxis() {
		return repositoryAxis;
	}
	
	/**
	 * Adds the given item as a new root item of the views row axis. 
	 * @param item the item to add
	 */
	public final void addRowRoot(XAxisItem item) {
		rowRoots.add(item);
	}
	/**
	 * Adds the given item as a new root item of the views column axis. 
	 * @param item the item to add
	 */
	public final void addColumnRoot(XAxisItem item) {
		colRoots.add(item);
	}
	
	/**
	 * Returns a list of all root items of the row axis.
	 * @return the roots items of the row axis
	 */
	public final List<XAxisItem> getRowRoots() {
		return rowRoots;
	}
	/**
	 * Returns a list of all root items of the column axis.
	 * @return the roots items of the column axis
	 */
	public final List<XAxisItem> getColumnRoots() {
		return colRoots;
	}
	public final XCellCollection getCells() {
		return cells;
	}
	public final void setCells(XCellCollection cells) {
		this.cells = cells;
	}
	
	public final String getType() {
		return getClass().getName();
	}
	public String getAccountId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setColumnsReversed(boolean reversed) {
		columnsReversed = reversed;
	}
	
	public void setRowsReversed(boolean reversed) {
		rowsReversed = reversed;
	}
	
	public void setHideEmptyCells(boolean hidden) {
		hideEmptyCells = hidden;
	}
	
	public void setShowRules(boolean showRules) {
		this.showRules = showRules;
	}
		
	public boolean isColumnsReversed() {
		return columnsReversed;
	}
	
	public boolean isRowsReversed() {
		return rowsReversed;
	}
	
	public boolean isHideEmptyCells() {
		return hideEmptyCells;
	}

	public boolean isShowRules() {
		return showRules;
	}
	
	public String getExternalId() {
		return externalId;
	}
	
	public void setExternalId(String id) {
		externalId = id;
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String id) {
		ownerId = id;
	}
	
	public boolean needsRestore() {
		return needsRestore;
	}
	
	public void setNeedsRestore(boolean v) {
		needsRestore = v;
	}
}
