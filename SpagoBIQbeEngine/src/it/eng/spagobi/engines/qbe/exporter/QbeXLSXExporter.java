/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/
package it.eng.spagobi.engines.qbe.exporter;

import it.eng.spagobi.engines.qbe.crosstable.exporter.CrosstabXLSExporter;
import it.eng.spagobi.engines.qbe.query.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class QbeXLSXExporter {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(QbeXLSXExporter.class);
	
    
	IDataStore dataStore = null;
	Vector extractedFields = null;
	Map<Integer, CellStyle> decimalFormats = new HashMap<Integer, CellStyle>();

	public QbeXLSXExporter(IDataStore dataStore) {
		super();
		this.dataStore = dataStore;
	}

	public IDataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(IDataStore dataStore) {
		this.dataStore = dataStore;
	}

	public QbeXLSXExporter() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Workbook export(){
		Workbook wb = new XSSFWorkbook();
	    CreationHelper createHelper = wb.getCreationHelper();
	    Sheet sheet = wb.createSheet("new sheet");
	    for(int j = 0; j < 50; j++){
			sheet.createRow(j);
		}
	    fillSheet(sheet, wb, createHelper, 0);

	    return wb;
	}
	
	public void fillSheet(Sheet sheet,Workbook wb, CreationHelper createHelper, int startRow) {		
	    // we enrich the JSON object putting every node the descendants_no property: it is useful when merging cell into rows/columns headers
	    // and when initializing the sheet
		 if(dataStore!=null  && !dataStore.isEmpty()){
			    CellStyle[] cellTypes = fillSheetHeader(sheet, wb, createHelper, startRow, 4);
			    fillSheetData(sheet, wb, createHelper, cellTypes, startRow+1, 4);    	
		    }
	}
	
	public CellStyle[] fillSheetHeader(Sheet sheet,Workbook wb, CreationHelper createHelper, int beginRowHeaderData, int beginColumnHeaderData) {	
		CrosstabXLSExporter xlsExp = new CrosstabXLSExporter();
		CellStyle hCellStyle = xlsExp.buildHeaderCellStyle(sheet);
		IMetaData d = dataStore.getMetaData();	
    	int colnum = d.getFieldCount();
    	Row row = sheet.getRow(beginRowHeaderData);
    	CellStyle[] cellTypes = new CellStyle[colnum]; // array for numbers patterns storage
    	for(int j = 0; j < colnum; j++){
    		Cell cell = row.createCell(j + beginColumnHeaderData);
    	    cell.setCellType(XSSFCell.CELL_TYPE_STRING);
    	    String fieldName = d.getFieldName(j);
    	    IFieldMetaData fieldMetaData = d.getFieldMeta(j);
    	    String format = (String) fieldMetaData.getProperty("format");
    	    String alias = (String) fieldMetaData.getAlias();

            if (extractedFields != null && extractedFields.get(j) != null) {
    	    	Field field = (Field) extractedFields.get(j);
    	    	fieldName = field.getAlias();
    	    	if (field.getPattern() != null) {
    	    		format = field.getPattern();
    	    	}
    	    }
            CellStyle aCellStyle = wb.createCellStyle(); 
            if (format != null) {
	    		short formatInt = (short) BuiltinFormats.getBuiltinFormat(format);  		  
	    		aCellStyle.setDataFormat(formatInt);
		    	cellTypes[j] = aCellStyle;
            }

           	if(alias!=null && !alias.equals("")){
           		cell.setCellValue(createHelper.createRichTextString(alias));
           	}else{
           		cell.setCellValue(createHelper.createRichTextString(fieldName));
           	}	 
           	cell.setCellStyle(hCellStyle);

    	}
    	return cellTypes;
	}
	
	public void fillSheetData(Sheet sheet,Workbook wb, CreationHelper createHelper,CellStyle[] cellTypes, int beginRowData, int beginColumnData) {	
		CrosstabXLSExporter xlsExp = new CrosstabXLSExporter();
		CellStyle dCellStyle = xlsExp.buildDataCellStyle(sheet);
		DataFormat df = createHelper.createDataFormat();
		Iterator it = dataStore.iterator();
    	int rownum = beginRowData;
    	short formatIndexInt = (short) BuiltinFormats.getBuiltinFormat("#,##0");
	    CellStyle cellStyleInt = wb.createCellStyle(); // cellStyleInt is the default cell style for integers
	    cellStyleInt.cloneStyleFrom(dCellStyle);
	    cellStyleInt.setDataFormat(formatIndexInt);
	    
	    short formatIndexDoub = (short) BuiltinFormats.getBuiltinFormat("#,##0.00");
	    CellStyle cellStyleDoub = wb.createCellStyle(); // cellStyleDoub is the default cell style for doubles
	    cellStyleDoub.cloneStyleFrom(dCellStyle);
	    cellStyleDoub.setDataFormat(formatIndexDoub);
	    
		CellStyle cellStyleDate = wb.createCellStyle(); // cellStyleDate is the default cell style for dates
		cellStyleDate.cloneStyleFrom(dCellStyle);
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
		
		IMetaData d = dataStore.getMetaData();	
		
		while(it.hasNext()){
			Row rowVal = sheet.getRow(rownum);
			IRecord record =(IRecord)it.next();
			List fields = record.getFields();
			int length = fields.size();
			for(int fieldIndex =0; fieldIndex<length; fieldIndex++){
				IField f = (IField)fields.get(fieldIndex);
				if (f != null && f.getValue()!= null) {

					Class c = d.getFieldType(fieldIndex);
					logger.debug("Column [" + (fieldIndex) + "] class is equal to [" + c.getName() + "]");
					if(rowVal==null){
						rowVal = sheet.createRow(rownum);
					}
					Cell cell = rowVal.createCell(fieldIndex + beginColumnData);
					cell.setCellStyle(dCellStyle);
					if( Integer.class.isAssignableFrom(c) || Short.class.isAssignableFrom(c)) {
						logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "INTEGER" + "]");					
					    Number val = (Number)f.getValue();
					    cell.setCellValue(val.intValue());
					    cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
					    cell.setCellStyle((cellTypes[fieldIndex] != null) ? cellTypes[fieldIndex] : cellStyleInt);
					}else if( Number.class.isAssignableFrom(c) ) {
			    	    IFieldMetaData fieldMetaData = d.getFieldMeta(fieldIndex);	    
						String decimalPrecision = (String)fieldMetaData.getProperty(IFieldMetaData.DECIMALPRECISION);
						CellStyle cs ;
					    if(decimalPrecision!=null){
					    	cs = getNumberFormat(new Integer(decimalPrecision), wb, createHelper, dCellStyle);
					    }else{
					    	cs = getNumberFormat(2, wb, createHelper, dCellStyle);
					    }

						logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "NUMBER" + "]");
					    Number val = (Number)f.getValue();

					    cell.setCellValue(val.doubleValue());			    
					    cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
					    cell.setCellStyle((cellTypes[fieldIndex] != null) ? cellTypes[fieldIndex] : cs);
					}else if( String.class.isAssignableFrom(c)){
						logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "STRING" + "]");	    
					    String val = (String)f.getValue();
					    cell.setCellValue(createHelper.createRichTextString(val));
					    cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					}else if( Boolean.class.isAssignableFrom(c) ) {
						logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "BOOLEAN" + "]");
					    Boolean val = (Boolean)f.getValue();
					    cell.setCellValue(val.booleanValue());
					    cell.setCellType(XSSFCell.CELL_TYPE_BOOLEAN);
					}else if(Date.class.isAssignableFrom(c)){
						logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "DATE" + "]");	    
					    Date val = (Date)f.getValue();
					    cell.setCellValue(val);	
					    cell.setCellStyle(cellStyleDate);
					}else{
						logger.warn("Column [" + (fieldIndex+1) + "] type is equal to [" + "???" + "]");
					    String val = f.getValue().toString();
					    cell.setCellValue(createHelper.createRichTextString(val));
					    cell.setCellType(XSSFCell.CELL_TYPE_STRING);	    
					}
				}
			}
		   rownum ++;
		}
	}

	public void setExtractedFields(Vector extractedFields) {
		this.extractedFields = extractedFields;
	}
	
	
	private CellStyle getNumberFormat(int j, Workbook wb, CreationHelper createHelper, CellStyle dCellStyle){

		if(decimalFormats.get(j)!=null)
			return decimalFormats.get(j);
		String decimals="";
		for(int i=0; i<j; i++){
			decimals+="0";
		}
		
	    CellStyle cellStyleDoub = wb.createCellStyle(); // cellStyleDoub is the default cell style for doubles
	    cellStyleDoub.cloneStyleFrom(dCellStyle);
	    DataFormat df = createHelper.createDataFormat();
	    cellStyleDoub.setDataFormat(df.getFormat("#,##0."+decimals));
		
		decimalFormats.put(j, cellStyleDoub);
		return cellStyleDoub;
	}

	
}
