/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.server.services.cubeview;

import org.palo.api.Cube;
import org.palo.api.Element;

import com.tensegrity.palo.gwt.core.client.models.palo.XCell;

/**
 * <code>DataWriter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CellWriter.java,v 1.4 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class CellWriter {

	//--------------------------------------------------------------------------
	// SINGLETON FACTORY
	//
	private static final CellWriter instance = new CellWriter();
	public static final CellWriter getInstance() {
		return instance;
	}
	
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private CellWriter() {		
	}
	
	public final void writeCell(XCell cell, Element[] coordinate, Cube cube) {
		if(isStringCell(coordinate)) {
			writeString(cell, coordinate, cube);
		} else {
			writeNumeric(cell, coordinate, cube);
		}
	}

	
	private final void writeString(XCell cell, Element[] coordinate, Cube cube) {
		cube.setData(coordinate, cell.value);
	}

	private final void writeNumeric(XCell cell, Element[] coordinate, Cube cube) {
		if(cell.value.equals(""))
			cell.value = "0";	//TODO should we ignore setting of empty numeric cell, i.e. simply return in this case??
		if(isConsolidatedCell(coordinate)) {
			cube.setDataSplashed(coordinate, cell.value);
		} else {
			cube.setData(coordinate, cell.value);
		}
	}
	
	private final boolean isStringCell(Element[] coordinate) {
		for(Element element : coordinate) {
			if(element.getType() == Element.ELEMENTTYPE_STRING)
				return true;
		}
		return false;
	}
	
	private final boolean isConsolidatedCell(Element[] coordinate) {
		for(Element element : coordinate) {
			if(element.getChildCount() > 0)
				return true;
		}
		return false;		
	}
}
