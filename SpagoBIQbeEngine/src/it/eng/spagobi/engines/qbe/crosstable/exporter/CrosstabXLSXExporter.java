/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.qbe.crosstable.exporter;


import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;

/**
 * Exports the crosstab data (formatted as a JSON object in input) into a XLSX file.
 * The JSON object should have this structure (a node is {node_key:"Text", node_childs:[...]}):
 * 		columns: {...} contains tree node structure of the columns' headers
 * 		rows: {...} contains tree node structure of the rows' headers
 * 		data: [[...], [...], ...] 2-dimensional matrix containing crosstab data
 * 
 * @author Alberto Ghedin (alberto.ghedin@eng.it), Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class CrosstabXLSXExporter extends CrosstabXLSExporter {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(CrosstabXLSExporter.class);
	
	public CrosstabXLSXExporter(Properties properties) {
		super(properties);
	}
	
	protected int getCellTypeNumeric () {
		return XSSFCell.CELL_TYPE_NUMERIC;
	}
	
	protected int getCellTypeString () {
		return XSSFCell.CELL_TYPE_STRING;
	}

}
