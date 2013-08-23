/*
*
* @file ExportContextInfo.java
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
* @author ArndHouben
*
* @version $Id: ExportContextInfo.java,v 1.5 2010/02/26 13:55:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package com.tensegrity.palojava;

/**
 * A simple data class which helps to provide the Cube#getDataExport() 
 * functionality. 
 * 
 * @author ArndHouben
 * @version $Id: ExportContextInfo.java,v 1.5 2010/02/26 13:55:49 PhilippBouillon Exp $
 */
public class ExportContextInfo {
	
	private String conditionRepresentation;
	private int type;
	private int blocksize;
	private double progress;
	private volatile boolean useRules;
	private boolean baseCellsOnly;	
	private boolean ignoreEmptyCells;
	private String[] exportAfterPath;
	private String[][] cellArea;
	
	/**
	 * Sets the export condition
	 * @param conditionRepresentation a textual representation of the export 
	 * condition
	 */
	public final synchronized void setConditionRepresentation(
			String conditionRepresentation) {
		this.conditionRepresentation = conditionRepresentation;
	}
	/**
	 * Returns the export condition
	 * @return a textual representation of the export condition
	 */
	public final synchronized String getConditionRepresentation() {
		return conditionRepresentation;
	}
	
	/**
	 * Signals if only base cells, i.e. cells which have no children, should be
	 * exported
	 * @return <code>true</code> if only base cells should be exported, 
	 * <code>false</code> otherwise
	 */
	public final synchronized boolean isBaseCellsOnly() {
		return baseCellsOnly;
	}
	/**
	 * Sets the base cell flag. Specify <code>true</code> if only base cells 
	 * should be exported, i.e. cells without children, <code>false</code>
	 * otherwise
	 * @param baseCellsOnly set to <code>true</code> to export only base cells,
	 * to <code>false</code> otherwise
	 */
	public final synchronized void setBaseCellsOnly(boolean baseCellsOnly) {
		this.baseCellsOnly = baseCellsOnly;
	}

	/**
	 * Signals if empty cells, i.e. cells which have no value, should be ignored
	 * for the export
	 * @return <code>true</code> if empty cells should not be exported, 
	 * <code>false</code> otherwise
	 */
	public final synchronized boolean ignoreEmptyCells() {
		return ignoreEmptyCells;
	}
	/**
	 * Sets the ignore empty cell flag. Specify <code>true</code> if empty cells 
	 * should not be exported, i.e. cells without any value, <code>false</code>
	 * otherwise
	 * @param ignoreEmptyCells set to <code>true</code> to not export empty 
	 * cells, to <code>false</code> otherwise
	 */
	public final synchronized void setIgnoreEmptyCells(boolean ignoreEmptyCells) {
		this.ignoreEmptyCells = ignoreEmptyCells;
	}

	/**
	 * Returns the current block size, i.e. the maximum number of cells which 
	 * gets exported
	 * @return the maximum number of exported cells
	 */
	public final synchronized int getBlocksize() {
		return blocksize;
	}
	/**
	 * Sets the current block size, i.e. the maximum number of exported cells
	 * @param blocksize the maximum number of exported cells
	 */
	public final synchronized void setBlocksize(int blocksize) {
		this.blocksize = blocksize;
	}
	
	public final synchronized int getType() {
		return type;
	}
	
	public final synchronized void setType(int type) {
		this.type = type;
	}
	/**
	 * Returns the current progress. The progress is a number between 0.0 and
	 * 1.0.
	 * @return current progress
	 */
	public final synchronized double getProgress() {
		return progress;
	}
	/**
	 * Sets the current progress. Should be a number between 0.0 and 1.0
	 * @param progress the current progress
	 */
	public final synchronized void setProgress(double progress) {
		this.progress = progress;
	}

	/**
	 * Returns the <code>Element</code> names which build up a path after which
	 * the export should start 
	 * @return the path build up by <code>Element</code> names after which the
	 * export should start
	 */
	public final synchronized String[] getExportAfter() {
		return exportAfterPath;
	}
	/**
	 * Sets the <code>Element</code> names which build up the path after which 
	 * the export should start 
	 * @param exportAfterPath the path build up by <code>Element</code> names 
	 * after which the export should start
	 */
	public final synchronized void setExportAfter(String[] exportAfterPath) {
		this.exportAfterPath = exportAfterPath;
	}
	
	/**
	 * Returns the cell coordinates as an array of <code>Element</code> names
	 * which determine the area (cartesian product) of cells to export
	 * @return cell coordinates which determine the export area 
	 */
	public final synchronized String[][] getCellsArea() {
		return cellArea;
	}
	/**
	 * Sets the cell coordinates as an array of <code>Element</code> names
	 * which determine the area (cartesian product) of cells to export
	 * @param cellArea coordinates which determine the area to export
	 */
	public final synchronized void setCellsArea(String[][] cellArea) {
		this.cellArea = cellArea;
	}

	/**
	 * Signals if cells based on rules should be exported
	 * @return <code>true</code> if only cells based on rules should be exported, 
	 * <code>false</code> otherwise
	 */
	public final boolean useRules() {
		return useRules;
	}
	/**
	 * Sets the use rules cells flag. Specify <code>true</code> to export only
	 * cells based on rules, <code>false</code> otherwise
	 * @param useRules set to <code>true</code> to export only cells based on 
	 * rules, to <code>false</code> otherwise
	 */
	
	public final void setUseRules(boolean useRules) {
		this.useRules = useRules;
	}
}
