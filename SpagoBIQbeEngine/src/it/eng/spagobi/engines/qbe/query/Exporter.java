package it.eng.spagobi.engines.qbe.query;

import it.eng.spagobi.engines.qbe.crosstable.CrossTab;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Exporter {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(Exporter.class);
	
    
	IDataStore dataStore = null;
	Vector extractedFields = null;

	public Exporter(IDataStore dataStore) {
		super();
		this.dataStore = dataStore;
	}

	public IDataStore getDataStore() {
		return dataStore;
	}

	public void setDataStore(IDataStore dataStore) {
		this.dataStore = dataStore;
	}

	public Exporter() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Workbook exportInExcel(){
		Workbook wb = new HSSFWorkbook();
	    CreationHelper createHelper = wb.getCreationHelper();
	    Sheet sheet = wb.createSheet("new sheet");
	    
	    fillSheet(sheet, wb, createHelper);

	    return wb;
	}
	
	public void fillSheet(Sheet sheet,Workbook wb, CreationHelper createHelper) {		
	    // we enrich the JSON object putting every node the descendants_no property: it is useful when merging cell into rows/columns headers
	    // and when initializing the sheet
		 if(dataStore!=null  && !dataStore.isEmpty()){
		    	IDataStoreMetaData d = dataStore.getMetaData();	
		    	int colnum = d.getFieldCount();
		    	Row row = sheet.createRow((short)0);
		    	CellStyle[] cellTypes = new CellStyle[colnum]; // array for numbers patterns storage
		    	for(int j =0;j<colnum;j++){
		    		Cell cell = row.createCell(j);
		    	    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		    	    String fieldName = d.getFieldName(j);
		    	    IFieldMetaData fieldMetaData = d.getFieldMeta(j);
		    	    String format = (String) fieldMetaData.getProperty("format");
		            if (extractedFields != null && extractedFields.get(j) != null) {
		    	    	Field field = (Field) extractedFields.get(j);
		    	    	fieldName = field.getAlias();
		    	    	if (field.getPattern() != null) {
		    	    		format = field.getPattern();
		    	    	}
		    	    }
		            if (format != null) {
	    	    		short formatInt = HSSFDataFormat.getBuiltinFormat(format);
	    	    		CellStyle aCellStyle = wb.createCellStyle();   
	    	    		aCellStyle.setDataFormat(formatInt);
	    		    	cellTypes[j] = aCellStyle;
		            }
		    	    cell.setCellValue(createHelper.createRichTextString(fieldName));
		    	}
		    	
		    	Iterator it = dataStore.iterator();
		    	int rownum = 1;
		    	short formatIndexInt = HSSFDataFormat.getBuiltinFormat("#,##0");
			    CellStyle cellStyleInt = wb.createCellStyle(); // cellStyleInt is the default cell style for integers
			    cellStyleInt.setDataFormat(formatIndexInt);
			    
			    short formatIndexDoub = HSSFDataFormat.getBuiltinFormat("#,##0.00");
			    CellStyle cellStyleDoub = wb.createCellStyle(); // cellStyleDoub is the default cell style for doubles
			    cellStyleDoub.setDataFormat(formatIndexDoub);
			    
				CellStyle cellStyleDate = wb.createCellStyle(); // cellStyleDate is the default cell style for dates
				cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
			    
				while(it.hasNext()){
					Row rowVal = sheet.createRow(rownum);
					IRecord record =(IRecord)it.next();
					List fields = record.getFields();
					int length = fields.size();
					for(int fieldIndex =0; fieldIndex<length; fieldIndex++){
						IField f = (IField)fields.get(fieldIndex);
						if (f != null && f.getValue()!= null) {
							
							Class c = d.getFieldType(fieldIndex);
							logger.debug("Column [" + (fieldIndex+1) + "] class is equal to [" + c.getName() + "]");
							if( Integer.class.isAssignableFrom(c) || Short.class.isAssignableFrom(c)) {
								logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "INTEGER" + "]");
								Cell cell = rowVal.createCell(fieldIndex);
							    Number val = (Number)f.getValue();
							    cell.setCellValue(val.intValue());
							    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							    cell.setCellStyle((cellTypes[fieldIndex] != null) ? cellTypes[fieldIndex] : cellStyleInt);
							}else if( Number.class.isAssignableFrom(c) ) {
								logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "NUMBER" + "]");
								Cell cell = rowVal.createCell(fieldIndex);
							    Number val = (Number)f.getValue();
							    cell.setCellValue(val.doubleValue());
							    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							   // List formats = HSSFDataFormat.getBuiltinFormats();
							    cell.setCellStyle((cellTypes[fieldIndex] != null) ? cellTypes[fieldIndex] : cellStyleDoub);
							}else if( String.class.isAssignableFrom(c)){
								logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "STRING" + "]");
								Cell cell = rowVal.createCell(fieldIndex);		    
							    String val = (String)f.getValue();
							    cell.setCellValue(createHelper.createRichTextString(val));
							    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							}else if( Boolean.class.isAssignableFrom(c) ) {
								logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "BOOLEAN" + "]");
								Cell cell = rowVal.createCell(fieldIndex);
							    Boolean val = (Boolean)f.getValue();
							    cell.setCellValue(val.booleanValue());
							    cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
							}else if(Date.class.isAssignableFrom(c)){
								logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "DATE" + "]");
							    Cell cell = rowVal.createCell(fieldIndex);		    
							    Date val = (Date)f.getValue();
							    cell.setCellValue(val);	
							    cell.setCellStyle(cellStyleDate);
							}else{
								logger.warn("Column [" + (fieldIndex+1) + "] type is equal to [" + "???" + "]");
								Cell cell = rowVal.createCell(fieldIndex);
							    String val = f.getValue().toString();
							    cell.setCellValue(createHelper.createRichTextString(val));
							    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							}
							
						}
					}
					rownum ++;
				}
		    }
	}

	public void setExtractedFields(Vector extractedFields) {
		this.extractedFields = extractedFields;
	}
	
}
