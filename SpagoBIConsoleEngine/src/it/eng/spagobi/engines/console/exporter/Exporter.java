package it.eng.spagobi.engines.console.exporter;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.console.ConsoleEngineConfig;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Exporter {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(Exporter.class);
	
    
	IDataStore dataStore = null;
	Vector extractedFields = null;
	List<IFieldMetaData> extractedFieldsMetaData = null;
	private long numberOfRows;

	public long getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(long numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public List<IFieldMetaData> getExtractedFieldsMetaData() {
		return extractedFieldsMetaData;
	}

	public void setExtractedFieldsMetaData(List<IFieldMetaData> extractedFieldsMetaData) {
		this.extractedFieldsMetaData = extractedFieldsMetaData;
	}

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
	    for(int j = 0; j < 50; j++){
			sheet.createRow(j);
		}
	    fillSheet(sheet, wb, createHelper);

	    return wb;
	}
	
	public void fillSheet(Sheet sheet,Workbook wb, CreationHelper createHelper) {		
	    // we enrich the JSON object putting every node the descendants_no property: it is useful when merging cell into rows/columns headers
	    // and when initializing the sheet
		 if(dataStore!=null  && !dataStore.isEmpty()){
			    CellStyle[] cellTypes = fillSheetHeader(sheet, wb, createHelper, 4, 4);
			    fillSheetData(sheet, wb, createHelper, cellTypes, 5, 4);
			    int first = sheet.getRow(0).getFirstCellNum();
			    int last = sheet.getRow(0).getLastCellNum();
			    adjustToColumnContent(sheet,first, last );
		    }
	}
	private void adjustToColumnContent(Sheet sheet, int first, int last){

			for(int i = first; i <= last; i++){
				sheet.autoSizeColumn(i);
			}

	}
	public CellStyle[] fillSheetHeader(Sheet sheet,Workbook wb, CreationHelper createHelper, int beginRowHeaderData, int beginColumnHeaderData) {	
		CellStyle hCellStyle = buildHeaderCellStyle(sheet);

    	int colnum = extractedFieldsMetaData.size();
    	Row row = sheet.getRow(beginRowHeaderData);
    	CellStyle[] cellTypes = new CellStyle[colnum]; // array for numbers patterns storage

    	for(int j = 0; j < colnum; j++){
    		Cell cell = row.createCell(j + beginColumnHeaderData);
    	    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
    	    IFieldMetaData fieldMetaData = extractedFieldsMetaData.get(j);
    	    String fieldName = fieldMetaData.getName();
    	    String format = (String) fieldMetaData.getProperty("format");
    	    String alias = (String) fieldMetaData.getAlias();
    	    Boolean visible = (Boolean) fieldMetaData.getProperty("visible");
            if (extractedFields != null && extractedFields.get(j) != null) {
            	Object f = extractedFields.get(j);
            	logger.debug("Extracted field "+fieldName+" is instance of "+f.getClass().getName());
            	if(f instanceof Field){
	    	    	Field field = (Field) f;
	    	    	fieldName = field.getName();
	    	    	if (field.getPattern() != null) {
	    	    		format = field.getPattern();
	    	    	}
            	}
    	    }
            CellStyle aCellStyle = wb.createCellStyle(); 
            if (format != null) {
	    		short formatInt = HSSFDataFormat.getBuiltinFormat(format);  		  
	    		aCellStyle.setDataFormat(formatInt);
		    	cellTypes[j] = aCellStyle;
            }
            if (visible != null && visible.booleanValue() == true) { 
            	if(alias!=null && !alias.equals("")){
            		cell.setCellValue(createHelper.createRichTextString(alias));
            	}else{
            		cell.setCellValue(createHelper.createRichTextString(fieldName));
            	}	 
            	cell.setCellStyle(hCellStyle);
            }	   
    	}
    	return cellTypes;
	}
	public CellStyle buildHeaderCellStyle(Sheet sheet){
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);  
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
/*        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);*/

        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);
        return cellStyle;
	}
	public CellStyle buildDataCellStyle(Sheet sheet){
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);    
/*        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);*/
        cellStyle.setWrapText(false);
        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short)10);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFont(font);
        return cellStyle;
	}
	public void fillSheetData(Sheet sheet,Workbook wb, CreationHelper createHelper,CellStyle[] cellTypes, int beginRowData, int beginColumnData) {	
		
		CellStyle dCellStyle = buildDataCellStyle(sheet);


    	int rownum = beginRowData;
    	short formatIndexInt = HSSFDataFormat.getBuiltinFormat("#,##0");
	    CellStyle cellStyleInt = wb.createCellStyle(); // cellStyleInt is the default cell style for integers
	    cellStyleInt.cloneStyleFrom(dCellStyle);
	    cellStyleInt.setDataFormat(formatIndexInt);
	    
	    short formatIndexDoub = HSSFDataFormat.getBuiltinFormat("#,##0.00");
	    CellStyle cellStyleDoub = wb.createCellStyle(); // cellStyleDoub is the default cell style for doubles
	    cellStyleDoub.cloneStyleFrom(dCellStyle);
	    cellStyleDoub.setDataFormat(formatIndexDoub);
	    
		CellStyle cellStyleDate = wb.createCellStyle(); // cellStyleDate is the default cell style for dates
		cellStyleDate.cloneStyleFrom(dCellStyle);
		cellStyleDate.setDataFormat(HSSFDataFormat.getBuiltinFormat("yy-m-d h:mm"));

		
		for(int i= 0; i<numberOfRows ; i++){
			Row rowVal = sheet.getRow(rownum);
			IRecord record =(IRecord)dataStore.getRecordAt(i);
			List fields = record.getFields();
			int length = extractedFieldsMetaData.size();
			for(int fieldIndex =0; fieldIndex< length; fieldIndex++){
				IFieldMetaData metaField = extractedFieldsMetaData.get(fieldIndex);
				IField f = (IField)record.getFieldAt((Integer)metaField.getProperty("index"));
				if (f != null && f.getValue()!= null) {
	
		    	    Boolean visible = (Boolean) metaField.getProperty("visible");
		    	    if(visible){
						Class c = metaField.getType();
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
						    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						    cell.setCellStyle((cellTypes[fieldIndex] != null) ? cellTypes[fieldIndex] : cellStyleInt);
						}else if( Number.class.isAssignableFrom(c) ) {
							logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "NUMBER" + "]");
						    Number val = (Number)f.getValue();
						    cell.setCellValue(val.doubleValue());
						    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						    cell.setCellStyle((cellTypes[fieldIndex] != null) ? cellTypes[fieldIndex] : cellStyleDoub);
						}else if( String.class.isAssignableFrom(c)){
							logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "STRING" + "]");	    
						    String val = (String)f.getValue();
						    cell.setCellValue(createHelper.createRichTextString(val));
						    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						}else if( Boolean.class.isAssignableFrom(c) ) {
							logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "BOOLEAN" + "]");
						    Boolean val = (Boolean)f.getValue();
						    cell.setCellValue(val.booleanValue());
						    cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
						}else if(Date.class.isAssignableFrom(c)){
							logger.debug("Column [" + (fieldIndex+1) + "] type is equal to [" + "DATE" + "]");	    
						    Date val = (Date)f.getValue();
						    
						    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						    String dtString = df.format(val);
						    cell.setCellValue(dtString);	
						    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						}else{
							logger.warn("Column [" + (fieldIndex+1) + "] type is equal to [" + "???" + "]");
						    String val = f.getValue().toString();
						    cell.setCellValue(createHelper.createRichTextString(val));
						    cell.setCellType(HSSFCell.CELL_TYPE_STRING);	    
						}
		    	    }

				}
			}
			
		   rownum ++;
		}
	}

	public void setExtractedFields(Vector extractedFields) {
		this.extractedFields = extractedFields;
	}
	
}
