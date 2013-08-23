/*
*
* @file ExportContextImpl.java
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
* @author Axel Kiselev
*
* @version $Id: ExportContextImpl.java,v 1.9 2010/02/26 13:55:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl;

import org.palo.api.Condition;
import org.palo.api.Cube;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ExportContext;

import com.tensegrity.palojava.ExportContextInfo;

/**
 * {@<describe>}
 * <p>
 * <code>ExportContext</code> interface implementation
 * </p>
 * {@</describe>}
 *
 * @author Axel Kiselev
 * @author ArndHouben
 * @version $Id: ExportContextImpl.java,v 1.9 2010/02/26 13:55:49 PhilippBouillon Exp $
 */
class ExportContextImpl implements ExportContext {

	private final Cube cube;
	private final ExportContextInfo contextInfo;
	
	ExportContextImpl(Cube cube) {
		this(cube,null);
	}
	
	ExportContextImpl(Cube cube, Element[][] area) {
		this.cube = cube;
		contextInfo = new ExportContextInfo();
		init(cube, area);
	}
	
	ExportContextInfo getInfo() {
		return contextInfo;
	}
	
	public void reset() {
		init(cube,null);
	}

	public Condition createCondition(String condition,double value) {
		Condition cond = ConditionImpl.getCondition(condition);
		cond.setValue(value);
		return cond;
	}

	public Condition createCondition(String condition,String value) {
		Condition cond = ConditionImpl.getCondition(condition);
		cond.setValue(value);
		return cond;
	}

	public String getConditionRepresentation() {
		return contextInfo.getConditionRepresentation();
	}


	public void setCombinedCondition(Condition firstCondition, Condition secondCondition, String operator) {
		if(isValid(operator)) {
			StringBuffer condition = new StringBuffer();
			condition.append(firstCondition.toString());
			condition.append(operator);
			condition.append(secondCondition.toString());
			contextInfo.setConditionRepresentation(condition.toString());
		}
	}

	public void setCondition(Condition condition) {
		contextInfo.setConditionRepresentation(condition.toString());
	}


	public boolean isBaseCellsOnly() {
		return contextInfo.isBaseCellsOnly();
	}

	public void setBaseCellsOnly(boolean baseCellsOnly) {
		contextInfo.setBaseCellsOnly(baseCellsOnly);
	}

	public boolean ignoreEmptyCells() {
		return contextInfo.ignoreEmptyCells();
	}

	public void setIgnoreEmptyCells(boolean ignoreEmptyCells) {
		contextInfo.setIgnoreEmptyCells(ignoreEmptyCells);
	}

	public final boolean isUseRules() {
		return contextInfo.useRules();
	}
	public final void setUseRules(boolean useRules) {
		contextInfo.setUseRules(useRules);
	}

	public int getBlocksize() {
		return contextInfo.getBlocksize();
	}

	public void setBlocksize(int blocksize) {
		contextInfo.setBlocksize(blocksize);
	}
	
	public int getType() {
		return contextInfo.getType();
	}
	
	public void setType(int type) {
		contextInfo.setType(type);
	}
	
	public Element[][] getCellsArea() {
		String[][] area = contextInfo.getCellsArea();
		Element[][] cells = new Element[area.length][];
		for(int i=0;i<area.length;i++) {
			cells[i] = new Element[area[i].length];
			for(int j=0;j<area[i].length;j++) {
				Dimension dim = cube.getDimensionAt(j);
				cells[i][j] = dim.getDefaultHierarchy().getElementByName(area[i][j]);
			}
		}
		return cells;
	}

	public void setCellsArea(Element[][] area) {
		if(area == null)
			setAreaToDefault();
		else
			setArea(area);
	}

	public double getProgress() {
		return contextInfo.getProgress();
	}
	public void setProgress(double progress) {
		contextInfo.setProgress(progress);
	}

	public Element[] getExportAfter() {
		String[] ids = contextInfo.getExportAfter();
		if(ids == null)
			return null;
		Element[] path = new Element[ids.length];		
		for(int i=0;i<ids.length;i++) {
			Dimension dim = cube.getDimensionAt(i);
			path[i] = dim.getDefaultHierarchy().getElementById(ids[i]);
		}
		return path;
	}

	public void setExportAfter(Element[] path) {
		//AXEL
		//incoming null must be accepted - otherwise there is no way
		//to re-start export correctly from the beginning
		if (path == null) {
			contextInfo.setExportAfter(null);
			return;
		}
		String[] ids = new String[path.length];
		for (int i = 0; i < path.length; i++)
			ids[i] = path[i].getId();
		contextInfo.setExportAfter(ids);
	}

	
	//--------------------------------------------------------------------------
	// INTERNAL
	//
	private final boolean isValid(String operator) {
		return operator.equals(ExportContext.OR)
				|| operator.equals(ExportContext.XOR)
				|| operator.equals(ExportContext.AND);
	}
	
	private final void init(Cube cube,Element[][] area) {
		//default values:
		contextInfo.setProgress(0);
		contextInfo.setConditionRepresentation(null);
		contextInfo.setBlocksize(1000);
		contextInfo.setType(ExportContext.TYPE_BOTH);
		contextInfo.setBaseCellsOnly(true);
		contextInfo.setIgnoreEmptyCells(true);
		contextInfo.setExportAfter(null);	//AXEL required for real reset
		if (area == null) {
			// create a default cell area
			setAreaToDefault();
		} else {
			setArea(area); //this.area = area;
		}
	}
	
	private final void setAreaToDefault() {
		// create a default cell area
		Dimension[] dims = cube.getDimensions();
		Element[][] area = new Element[dims.length][];
		for (int i = 0; i < area.length; ++i) {
			Dimension dim = cube.getDimensionAt(i);
			area[i] = dim.getDefaultHierarchy().getElements();
		}
		setArea(area);
	}
	
	private final void setArea(Element[][] area) {
		String[][] ids = new String[area.length][];
		for(int i=0;i<area.length;i++) {
			ids[i] = new String[area[i].length];
			for(int j=0;j<area[i].length;j++) {
				ids[i][j] = area[i][j].getId();
			}
		}
		contextInfo.setCellsArea(ids);
	}
}
