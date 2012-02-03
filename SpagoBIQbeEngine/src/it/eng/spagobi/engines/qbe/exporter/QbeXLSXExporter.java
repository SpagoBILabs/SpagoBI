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
package it.eng.spagobi.engines.qbe.exporter;

import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class QbeXLSXExporter extends QbeXLSExporter {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(QbeXLSXExporter.class);
	
	public QbeXLSXExporter(IDataStore dataStore, Locale locale) {
		super(dataStore, locale);
	}
	
	protected Workbook instantiateWorkbook() {
		Workbook workbook = new XSSFWorkbook();
		return workbook;
	}
    
	protected int getCellTypeNumeric () {
		return XSSFCell.CELL_TYPE_NUMERIC;
	}
	
	protected int getCellTypeString () {
		return XSSFCell.CELL_TYPE_STRING;
	}
	
	protected int getCellTypeBoolean () {
		return XSSFCell.CELL_TYPE_BOOLEAN;
	}
	
	protected short getBuiltinFormat (String formatStr) {
		short format = (short) BuiltinFormats.getBuiltinFormat(formatStr); 
		return format;
	}
	
}
