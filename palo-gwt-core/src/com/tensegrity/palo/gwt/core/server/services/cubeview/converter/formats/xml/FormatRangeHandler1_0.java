/*
*
* @file FormatRangeHandler1_0.java
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
* @author Philipp Bouillon
*
* @version $Id: FormatRangeHandler1_0.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
*
* @file FormatRangeHandler1_0.java
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
* @author Philipp Bouillon
*
* @version $Id: FormatRangeHandler1_0.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.xml;

import java.util.ArrayList;
import java.util.List;

import org.palo.api.Cube;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.impl.xml.EndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.impl.xml.StartHandler;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.Property;
import org.palo.viewapi.uimodels.formats.Format;
import org.palo.viewapi.uimodels.formats.FormatRangeInfo;
import org.xml.sax.Attributes;

import com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.FormatConverter;

/**
 * <code>FormatHandler1_0</code>
 * Defines <code>{@link StartHandler}</code>s and 
 * <code>{@link EndHandler}</code>s to read ranges of formats which are stored
 * using version 1.0
 *
 * @author Philipp Bouillon
 * @version $Id: FormatRangeHandler1_0.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
class FormatRangeHandler1_0 extends FormatRangeHandler {

	private final CubeView view;

    FormatRangeHandler1_0(CubeView cubeView) {
    	super();
    	this.view = cubeView;
    }

    protected void registerEndHandlers() {
	}
    
    protected void registerStartHandlers() {
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "ranges/formatrange";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String id = attributes.getValue("id");
				Format format = view.getFormat(id);
				if(format != null) {
					String from = attributes.getValue("from");
					String to = attributes.getValue("to");					
					FormatRangeInfo range = createFormatRange(from, to);
					if(range != null) {
						format.addRange(range);
						addRangeProperty(range, from, to);
					}
				} else
					System.err.println("Unknown format id: "+id);
			}
		});
    }    
    
    private FormatRangeInfo createFormatRange(String from, String to) {
		int[] fromRowCol = getCoordinate(from);
		int[] toRowCol = getCoordinate(to);
		
    	Cube cube = view.getCube();
		Element[] coord = new Element[cube.getDimensionCount()];		
		Axis[] axes = view.getAxes();
		for (int i = 0; i < axes.length; i++) {
			if (!isRowOrColumn(axes[i]))
				fillCoordinateWithSelectedElements(coord, axes[i]
						.getAxisHierarchies());
		}

		Axis row = view.getAxis(CubeView.ROW_AXIS);
		Axis column = view.getAxis(CubeView.COLUMN_AXIS);

		List<Element[]> cells = fill(coord, fromRowCol, toRowCol, row, column);
		
		return new FormatRangeInfo(cells.toArray(new Element[0][]));
    }
    private int [] getCoordinate(String string) {
    	String [] res = string.split(",");
    	int [] coord = new int[2];
    	if (res.length >= 2) {
    		coord[0] = Integer.parseInt(res[0]);
    		coord[1] = Integer.parseInt(res[1]);
    	}
    	return coord;
    }
    
    private final boolean isRowOrColumn(Axis axis) {
		String axisId = axis.getId();
		return axisId.equals(CubeView.ROW_AXIS)
				|| axisId.equals(CubeView.COLUMN_AXIS);
	}
	private final void fillCoordinateWithSelectedElements(Element[] coordinate,
			AxisHierarchy[] axisHierarchies) {
		// viewModel.getSelectionAxis().getAxisHierarchies();
		for (int i = 0; i < axisHierarchies.length; ++i) {			
			int index = getIndexInCube(axisHierarchies[i].getHierarchy());
			Element[] selElements = axisHierarchies[i].getSelectedElements();
			// palo can use only the first selected element:
			if (selElements != null && selElements.length > 0) {
				coordinate[index] = selElements[0];
			}
		}
	}
	
	private final List<Element[]> fill(Element[] coord, int[] from, int[] to, Axis row, Axis column) {
		Hierarchy[] rowHierarchies = row.getHierarchies();
		Hierarchy[] colHierarchies = column.getHierarchies();
		int lastRowHierarchy = rowHierarchies.length - 1;
		int lastColHierarchy = colHierarchies.length - 1;
		List<Element[]> coordinates = new ArrayList<Element[]>();
		for(int rowIndex = from[0]; rowIndex <= to[0]; rowIndex++) {
			for(int colIndex = from[1]; colIndex <= to[1]; colIndex++) {
				Element[] coordinate = coord.clone();
				fill(coordinate, rowIndex, rowHierarchies, lastRowHierarchy);
				fill(coordinate, colIndex, colHierarchies, lastColHierarchy);
				coordinates.add(coordinate);
			}
		}
		return coordinates;
	}
    
	private final void fill(Element[] coord, int elIndex, Hierarchy[] hierarchies, int hierIndex) {
		if(hierIndex < 0)
			return;
		int coordIndex = getIndexInCube(hierarchies[hierIndex]);
		int elCount = hierarchies[hierIndex].getElementCount();		
		Element el = hierarchies[hierIndex].getElementsInOrder()[elIndex % elCount]; 
		coord[coordIndex] = el;
		fill(coord, elIndex / elCount, hierarchies, hierIndex - 1);
	}
	
	private final int getIndexInCube(Hierarchy hierarchy) {
		Dimension[] dimensions = view.getCube().getDimensions();
		for(int i=0;i<dimensions.length;i++) {
			if(dimensions[i].getDefaultHierarchy().equals(hierarchy))
				return i;
		}
		return -1;
	}
	
	private final void addRangeProperty(FormatRangeInfo range, String from,
			String to) {
		Property<Object> ranges = view
				.getProperty(FormatConverter.PROPERTY_FORMAT_RANGES);
		if (ranges == null) {
			ranges = view.addProperty(FormatConverter.PROPERTY_FORMAT_RANGES,
					FormatConverter.getFromToString(from, to));
		} else
			ranges.setValue(FormatConverter.getFromToString(from, to));
	}

}
