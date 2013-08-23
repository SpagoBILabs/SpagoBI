/*
*
* @file PaloCSVExporter.java
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
* @version $Id: PaloCSVExporter.java,v 1.6 2010/04/12 11:15:09 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.exporters;

import java.util.HashMap;

import org.palo.api.Cube;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.viewapi.Axis;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.uimodels.axis.AxisFlatModel;
import org.palo.viewapi.uimodels.axis.AxisItem;

/**
 * <code>PaloCSVExporter</code>
 * Exports a Palo Cube View to a Comma Separated Value string.
 *
 * @version $Id: PaloCSVExporter.java,v 1.6 2010/04/12 11:15:09 PhilippBouillon Exp $
 **/
public class PaloCSVExporter implements CubeViewExporter {
	/**
	 * The buffer holding the CSV information.
	 */
	private final StringBuffer csv = new StringBuffer();
	
	/**
	 * Separator character; using tab instead of comma allows for a nicer copy
	 * and paste to Excel.
	 */
	private final char separator = '\t';
	
	/**
	 * Returns the csv string of the view.
	 * @return the csv string of the view.
	 */
	public final String getCSV() {
		return csv.toString();
	}

	/**
	 * Exports the given view to a csv string.
	 */
	public final void export(CubeView view) {		
		Cube cube = view.getCube();
		
		Dimension[] cubeDims = cube.getDimensions();
		HashMap<Hierarchy, Integer> hierIndex = 
			new HashMap<Hierarchy, Integer>();
		for(int i=0;i<cubeDims.length;++i)
			hierIndex.put(cubeDims[i].getDefaultHierarchy(), i);
		
		Element[] coord = new Element[cubeDims.length];
		//selected elements:
		Axis selAxis = view.getAxis("selected");
		for (int i = 0; i < cubeDims.length; i++) {
			AxisHierarchy axisHierarchy = selAxis.getAxisHierarchy(cubeDims[i]
					.getDefaultHierarchy());
			if (axisHierarchy != null) {
				Element[] selElements = axisHierarchy.getSelectedElements();
				// palo can use only the first selected element:
				if (selElements != null && selElements.length > 0) {
					coord[i] = selElements[0];
				}
			}
		}
		
		//palo defines a rows, cols and selected axis
		Object rr = view.getPropertyValue(CubeView.PROPERTY_ID_REVERSE_VERTICAL_LAYOUT);		
		AxisFlatModel rows = //rows are vertical aligned... 
			new AxisFlatModel(view.getAxis("rows"),AxisFlatModel.VERTICAL, rr != null && Boolean.parseBoolean("" + rr));
		AxisItem[][] rowsModel = rows.getModel();
		Object rc = view.getPropertyValue(CubeView.PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT);
		AxisFlatModel cols = new AxisFlatModel(view.getAxis("cols"), rc != null && Boolean.parseBoolean("" + rc));
		AxisItem[][] colsModel = cols.getModel();
		
//		int rowLeaf = rowsModel.length - 1;
		int colLeaf = colsModel.length - 1;
//		int cells = rowsModel[rowLeaf].length * colsModel[colLeaf].length;
		int cells = rowsModel.length * colsModel[colLeaf].length;
		Element[][] cellCoords = new Element[cells][];
		
		int cellIndex = 0;
		int rowLeaf = rowsModel[0].length - 1;
		for (int r = 0; r < rowsModel.length; ++r) {
			for (int c = 0; c < colsModel[colLeaf].length; ++c) {
				cellCoords[cellIndex] = coord.clone();				
				//fill row coordinates:
				fill(cellCoords[cellIndex], rowsModel[r][rowLeaf], hierIndex);
				//and finally the col coordinates:
				fill(cellCoords[cellIndex], colsModel[colLeaf][c], hierIndex);
				cellIndex++;
			}
		}
		Object[] cellValues = cube.getDataBulk(cellCoords);

		//export table:
		dumpSelectedElements(coord);		
		dumpColumns(colsModel, rowLeaf + 1); // rowsModel.length);
		dumpRowsAndCells(rowsModel, colsModel[colLeaf].length, cellValues);
	}
	
	/**
	 * Fills all hierarchies of a cube with coordinate information to complete
	 * a full coordinate into the cube.
	 * 
	 * @param coord the coordinate to be filled.
	 * @param item the current AxisItem that is to be put into a coordinate.
	 * @param hierIndex the hierarchy-index map.
	 */
	private final void fill(Element[] coord, AxisItem item,
			HashMap<Hierarchy, Integer> hierIndex) {
		if(item == null)
			return;
		
//		coord[dimIndex.get(item.getDimension())] = item.getElement();
//		fill(coord, item.getParentInPrevDim(), dimIndex);
		coord[hierIndex.get(item.getHierarchy())] = item.getElement();
		fill(coord, item.getParentInPrevHierarchy(), hierIndex);

	}

	/**
	 * Puts all dimensions in the point of view into the csv string.
	 * @param coord the selected elements of all dimensions in the view.
	 */
	private final void dumpSelectedElements(Element[] coord) {
		//first dimensions...
		for(int i=0;i<coord.length; ++i) {
			if(coord[i] != null) {
				csv.append(coord[i].getDimension().getName());
				csv.append(separator);
			}
		}
		if(csv.length()<1)
			return;
		//replace last comma with new line:
		csv.replace(csv.length()-1, csv.length(), "\n");
		//finally the elements
		for(int i=0;i<coord.length; ++i) {
			if(coord[i] != null) {
				csv.append(coord[i].getName());
				csv.append(separator);
			}
		}
		//replace last comma with new line:
		csv.replace(csv.length()-1, csv.length(), "\n");
		csv.append("\n\n");
	}

	/**
	 * Puts all columns into the csv string.
	 * 
	 * @param colModel all columns of the view.
	 * @param rowDimCount rows per dimension in the view.
	 */
	private final void dumpColumns(AxisItem[][] colModel, int rowDimCount) {
		for (int i = 0; i < colModel.length; ++i) {
			for (int k = 0; k < rowDimCount; ++k)
				csv.append(separator);
			for (int j = 0; j < colModel[i].length; ++j) {
				AxisItem item = colModel[i][j];
				csv.append(item.getName());
				csv.append(separator);
			}
			//replace last comma with new line:
			csv.replace(csv.length()-1, csv.length(), "\n");
		}
	}

	/**
	 * Outputs all rows and the cell values into the csv string.
	 * 
	 * @param rowModel all rows of the view.
	 * @param colLeafCount number of columns per row.
	 * @param cellValues all cell value.
	 */
	private final void dumpRowsAndCells(AxisItem[][] rowModel,
			int colLeafCount, Object[] cellValues) {
		// we have to turn the rows around ;)
//		AxisItem[][] turnedModel = turnAround(rowModel);
//		for (int i = 0; i < turnedModel.length; ++i) {
//			for (int j = 0; j < turnedModel[i].length; ++j) {
//				AxisItem item = turnedModel[i][j];
		for (int i = 0; i < rowModel.length; ++i) {
			for (int j = 0; j < rowModel[i].length; ++j) {
				AxisItem item = rowModel[i][j];
				csv.append(item.getName());
				csv.append(separator);
			}
			// draw da cells:
			int start = i * colLeafCount;
			int end = start + colLeafCount;
			for (int c = start; c < end; ++c) {
				csv.append(cellValues[c].toString());
				csv.append(separator);
			}
			// replace last comma with new line:
			csv.replace(csv.length() - 1, csv.length(), "\n");
		}
	}

//	private final AxisItem[][] turnAround(AxisItem[][] model) {
//		int lastDim = model.length - 1;
//		AxisItem[][] turnedModel = new AxisItem[model[lastDim].length][model.length];
//		for (int i = 0; i < model[lastDim].length; ++i) {
//			AxisItem item = model[lastDim][i];
//			turnedModel[i][lastDim] = item;
//			//fill previous dims:
//			AxisItem parent;
//			int prevDim = lastDim - 1;
//			while((parent = item.getParentInPrevHierarchy())!= null) {
//				turnedModel[i][prevDim] = parent;
//				item = parent;
//				prevDim--;
//			}
//		}
//		return turnedModel;
//	}
}
