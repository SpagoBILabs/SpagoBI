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
package it.eng.spagobi.engines.qbe.crosstable.exporter;


import it.eng.spagobi.engines.qbe.crosstable.CrossTab;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Exports the crosstab data (formatted as a JSON object in input) into a XLS file.
 * The JSON object should have this structure (a node is {node_key:"Text", node_childs:[...]}):
 * 		columns: {...} contains tree node structure of the columns' headers
 * 		rows: {...} contains tree node structure of the rows' headers
 * 		data: [[...], [...], ...] 2-dimensional matrix containing crosstab data
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class CrosstabXLSExporter {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(CrosstabXLSExporter.class);


	/**
	 * Exports the crosstab data (formatted as a JSON object in input) into a XLS file.
	 * The JSON object should have this structure (a node is {node_key:"Text", node_childs:[...]}):
	 * 		columns: {...} contains tree node structure of the columns' headers
	 * 		rows: {...} contains tree node structure of the rows' headers
	 * 		data: [[...], [...], ...] 2-dimensional matrix containing crosstab data
	 * @param json The crosstab data serialization
	 * @return the Workbook object (the XLS file)
	 * @throws JSONException
	 */
	public Workbook export(JSONObject json) throws JSONException {
		
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("new sheet");
		CreationHelper createHelper = wb.getCreationHelper();
	    fillSheet(sheet, json, createHelper);		
		return wb;
	}
	
	public void fillSheet(Sheet sheet,JSONObject json, CreationHelper createHelper) throws JSONException{		
	    // we enrich the JSON object putting every node the descendants_no property: it is useful when merging cell into rows/columns headers
	    // and when initializing the sheet
		CrosstabExporterUtility.calculateDescendants(json);
    	// init the sheet
    	initSheet(sheet, json);
    	commonFillSheet(sheet, json, createHelper);
	}
	
	public int fillAlreadyCreatedSheet(Sheet sheet,JSONObject json, CreationHelper createHelper) throws JSONException{		
	    // we enrich the JSON object putting every node the descendants_no property: it is useful when merging cell into rows/columns headers
	    // and when initializing the sheet
		CrosstabExporterUtility.calculateDescendants(json);
    	int totalRowNum = commonFillSheet(sheet, json, createHelper);
    	return totalRowNum;
	}
	
	public int commonFillSheet(Sheet sheet,JSONObject json, CreationHelper createHelper) throws JSONException{	
		JSONObject columnsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
    	JSONArray columnsRootChilds = columnsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
    	int columnsDepth = CrosstabExporterUtility.getDepth(columnsRoot);
		JSONObject rowsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
		int rowsDepth = CrosstabExporterUtility.getDepth(rowsRoot);
		JSONArray rowsRootChilds = rowsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		JSONArray data = (JSONArray) json.get(CrossTab.CROSSTAB_JSON_DATA);
		
		// build headers for column first ...
		buildColumnsHeader(sheet, columnsRootChilds, 4, rowsDepth + 4, createHelper);
		// ... then build headers for rows ....
	    buildRowsHeaders(sheet, rowsRootChilds, columnsDepth + 4, 4, createHelper);
	    // then put the matrix data
	    int totalRowNum = buildDataMatrix(sheet, data, columnsDepth + 4, rowsDepth + 4, createHelper);
	    return totalRowNum;
	}
	

	/**
	 * Sheet initialization. We create as many rows as it is required to contain the crosstab.
	 * 
	 * @param sheet The XLS sheet
	 * @param json The crosstab data (it must have been enriched with the calculateDescendants method)
	 * @throws JSONException
	 */
	private void initSheet(Sheet sheet, JSONObject json) throws JSONException {
		JSONObject columnsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
		int columnsDepth = CrosstabExporterUtility.getDepth(columnsHeaders);
		JSONObject rowsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
		int rowsNumber = rowsHeaders.getInt(CrosstabExporterUtility.CROSSTAB_JSON_DESCENDANTS_NUMBER);
		int totalRowsNumber = columnsDepth + rowsNumber + 1; // + 1 because there may be also the bottom row with the totals
		for (int i = 0; i < totalRowsNumber + 4; i++) {
			sheet.createRow(i);
		}
	}

	private int buildDataMatrix(Sheet sheet, JSONArray data, int rowOffset, int columnOffset, CreationHelper createHelper) throws JSONException {
		CellStyle cellStyle = buildDataCellStyle(sheet);
		int endRowNum = 0;
		for (int i = 0; i < data.length(); i++) {
			JSONArray array = (JSONArray) data.get(i);
			for (int j = 0; j < array.length(); j++) {
				String text = (String) array.get(j);
				int rowNum = rowOffset + i ;
				int columnNum = columnOffset + j ;
				Row row = sheet.getRow(rowNum);
				if(row==null){
					row = sheet.createRow(rowNum);
				}
				endRowNum = rowNum;
				Cell cell = row.createCell(columnNum);
				cell.setCellStyle(cellStyle);
				try {
					double value = Double.parseDouble(text);
					cell.setCellValue(value);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				} catch (NumberFormatException e) {
					logger.debug("Text " + text + " is not recognized as a number");
					cell.setCellValue(createHelper.createRichTextString(text));
				    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				}
				
			}
		}
		return endRowNum;
	}


	/**
	 * Builds the rows' headers recursively with this order:
	 * |-----|-----|-----|
	 * |     |     |  3  |
	 * |     |     |-----|
	 * |     |  2  |  4  |
	 * |     |     |-----|
	 * |  1  |     |  5  |
	 * |     |-----|-----|
	 * |     |     |  7  |
	 * |     |  6  |-----|
	 * |     |     |  8  |
	 * |-----|-----|-----|
	 * |     |     |  11 |
	 * |  9  |  10 |-----|
	 * |     |     |  12 |
	 * |-----|-----|-----|
	 * 
	 * @param sheet The sheet of the XLS file
	 * @param siblings The siblings nodes of the headers structure
	 * @param rowNum The row number where the first sibling must be inserted
	 * @param columnNum The column number where the siblings must be inserted
	 * @param createHelper The file creation helper
	 * @throws JSONException
	 */
	private void buildRowsHeaders(Sheet sheet, JSONArray siblings, int rowNum, int columnNum, CreationHelper createHelper) throws JSONException {
		int rowsCounter = rowNum;
		
		CellStyle cellStyle = buildHeaderCellStyle(sheet);
        
		for (int i = 0; i < siblings.length(); i++) {
			JSONObject aNode = (JSONObject) siblings.get(i);
			Row row = sheet.getRow(rowsCounter);
			Cell cell = row.createCell(columnNum);
			String text = (String) aNode.get(CrossTab.CROSSTAB_NODE_JSON_KEY);
			cell.setCellValue(createHelper.createRichTextString(text));
		    cell.setCellType(HSSFCell.CELL_TYPE_STRING);

	        cell.setCellStyle(cellStyle);
	       
		    int descendants = aNode.getInt(CrosstabExporterUtility.CROSSTAB_JSON_DESCENDANTS_NUMBER);
		    if (descendants > 1) {
			    sheet.addMergedRegion(new CellRangeAddress(
			    		rowsCounter, //first row (0-based)
			    		rowsCounter + descendants - 1, //last row  (0-based)
			    		columnNum, //first column (0-based)
			    		columnNum //last column  (0-based)
			    ));
		    }
		    JSONArray childs = aNode.optJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		    if (childs != null && childs.length() > 0) {
		    	buildRowsHeaders(sheet, childs, rowsCounter, columnNum + 1, createHelper);
		    }
		    int increment = descendants > 1 ? descendants : 1;
		    rowsCounter = rowsCounter + increment;
		}
		
	}


	public CellStyle buildHeaderCellStyle(Sheet sheet){
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);  
        cellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.DARK_BLUE.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.DARK_BLUE.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.DARK_BLUE.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.DARK_BLUE.getIndex());
        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short)14);
        font.setFontName("Arial");
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);
        return cellStyle;
	}
	
	public CellStyle buildDataCellStyle(Sheet sheet){
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);    
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLUE.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.BLUE.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.BLUE.getIndex());
        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFont(font);
        return cellStyle;
	}



	/**
	 * Builds the columns' headers recursively with this order:
	 * |------------------------------------------|
	 * |              1              |     9      |
	 * |------------------------------------------|
	 * |     2     |        5        |     10     |
	 * |-----------|-----------------|------------|
	 * |  3  |  4  |  6  |  7  |  8  |  11  | 12  |
	 * |------------------------------------------|
	 * 
	 * @param sheet The sheet of the XLS file
	 * @param siblings The siblings nodes of the headers structure
	 * @param rowNum The row number where the siblings must be inserted
	 * @param columnNum The column number where the first sibling must be inserted
	 * @param createHelper The file creation helper
	 * @throws JSONException
	 */
	private void buildColumnsHeader(Sheet sheet, JSONArray siblings, int rowNum, int columnNum, CreationHelper createHelper) throws JSONException {
		int columnCounter = columnNum;
		CellStyle cellStyle = buildHeaderCellStyle(sheet);
		for (int i = 0; i < siblings.length(); i++) {
			JSONObject aNode = (JSONObject) siblings.get(i);
			Row row = sheet.getRow(rowNum);
			Cell cell = row.createCell(columnCounter);
			String text = (String) aNode.get(CrossTab.CROSSTAB_NODE_JSON_KEY);
			cell.setCellValue(createHelper.createRichTextString(text));
		    cell.setCellType(HSSFCell.CELL_TYPE_STRING);	    
		    int descendants = aNode.getInt(CrosstabExporterUtility.CROSSTAB_JSON_DESCENDANTS_NUMBER);
		    if (descendants > 1) {
			    sheet.addMergedRegion(new CellRangeAddress(
			    		rowNum, //first row (0-based)
			    		rowNum, //last row  (0-based)
			    		columnCounter, //first column (0-based)
			    		columnCounter + descendants - 1  //last column  (0-based)
			    ));
		    }
		    cell.setCellStyle(cellStyle);
		    
		    JSONArray childs = aNode.optJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		    if (childs != null && childs.length() > 0) {
		    	buildColumnsHeader(sheet, childs, rowNum + 1, columnCounter, createHelper);
		    }
		    int increment = descendants > 1 ? descendants : 1;
		    columnCounter = columnCounter + increment;
		}
	}

	
	


}
