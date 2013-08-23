/*
*
* @file ViewModel.java
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
* @version $Id: ViewModel.java,v 1.10 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import org.palo.viewapi.Axis;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.internal.io.CubeViewReader;
import org.palo.viewapi.uimodels.axis.AxisItem;
import org.palo.viewapi.uimodels.axis.AxisModel;
import org.palo.viewapi.uimodels.axis.AxisTreeModel;
import org.palo.viewapi.uimodels.axis.events.AxisModelEvent;
import org.palo.viewapi.uimodels.axis.events.AxisModelListener;

/**
 * <code>ViewModel</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewModel.java,v 1.10 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
class ViewModel implements AxisModelListener {
	
	private static final String REPOSITORY_AXIS = "hierarchy-repository";	
	
	private final AxisModel rowAxis;
	private final AxisModel columnAxis;
	private final Axis selectionAxis;
	private final Axis repositoryAxis;
	
	private final CubeView cubeView;
	
	private String name;
	
	//TODO have to remove expandedItems!!!
	private AxisItem[][] expandedItems;
	
	
	ViewModel(CubeView cubeView) {
		this.cubeView = cubeView;
		this.name = cubeView.getName();
		
		try {
//			// create axes:
			CubeViewReader.CHECK_RIGHTS = false;
			rowAxis = createAxisModel(CubeView.ROW_AXIS, cubeView);
			columnAxis = createAxisModel(CubeView.COLUMN_AXIS, cubeView);
			selectionAxis =  getOrCreateAxis(CubeView.SELECTION_AXIS, cubeView);
			repositoryAxis = getOrCreateAxis(REPOSITORY_AXIS, cubeView);
		} finally {
			CubeViewReader.CHECK_RIGHTS = true;
		}
	}
	
	private final AxisModel createAxisModel(String axisId, CubeView cubeView) {
		Axis axis = getOrCreateAxis(axisId, cubeView);
		return createAxisModel(axis);
	}
	private final Axis getOrCreateAxis(String axisId, CubeView cubeView) {
		Axis axis = cubeView.getAxis(axisId);
		if(axis == null) {
			axis = cubeView.addAxis(axisId, axisId);
		}
		return axis;
	}

	private final AxisModel createAxisModel(Axis forAxis) {
		AxisModel model = new AxisTreeModel(forAxis);
		model.addListener(this);
		return model;
	}

	public final CubeView getCubeView() {
		return cubeView;
	}
	
	public final String getName() {
		return name;
	}
	
	public final AxisModel getRowAxis() {
		return rowAxis;
	}
	public final AxisModel getColumnAxis() {
		return columnAxis;
	}
	public final Axis getSelectionAxis() {
		return selectionAxis;
	}
	public final Axis getRepositoryAxis() {
		return repositoryAxis;
	}
	
	public final boolean isColumnAxis(String axisId) {
		return axisId.equals(CubeView.COLUMN_AXIS);
	}
	
	public final AxisModel getAxisModelById(String id) {
		if(id.equals(CubeView.ROW_AXIS))
			return rowAxis;
		else if(id.equals(CubeView.COLUMN_AXIS))
			return columnAxis;
		return null;
	}
	
	public final AxisItem[][] getExpandedItems() {
		return expandedItems;
	}
	
	public void collapsed(AxisModelEvent e) { /* ignore */ }
	public void expanded(AxisModelEvent e) {
		expandedItems = e.getItems();
	}
	public void structureChanged(AxisModelEvent e) { /* ignore */ }
	public void willCollapse(AxisModelEvent e) { /* ignore */ }
	public void willExpand(AxisModelEvent e) { /* ignore */ }
}
