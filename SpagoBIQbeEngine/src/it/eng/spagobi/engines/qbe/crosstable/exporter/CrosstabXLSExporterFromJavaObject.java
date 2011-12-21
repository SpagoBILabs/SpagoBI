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


import it.eng.qbe.serializer.SerializationException;
import it.eng.spagobi.engines.qbe.crosstable.CrossTab;
import it.eng.spagobi.engines.qbe.crosstable.CrossTab.CellType;
import it.eng.spagobi.engines.qbe.crosstable.Node;
import it.eng.spagobi.engines.worksheet.services.export.MeasureFormatter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Exports the crosstab data (formatted as a JSON object in input) into a XLS file.
 * The JSON object should have this structure (a node is {node_key:"Text", node_childs:[...]}):
 * 		columns: {...} contains tree node structure of the columns' headers
 * 		rows: {...} contains tree node structure of the rows' headers
 * 		data: [[...], [...], ...] 2-dimensional matrix containing crosstab data
 * 
 * @author Alberto Ghedin (alberto.ghedin@eng.it), Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class CrosstabXLSExporterFromJavaObject {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(CrosstabXLSExporterFromJavaObject.class);


	
	public int fillAlreadyCreatedSheet(Sheet sheet,CrossTab cs, JSONObject crosstabJSON, CreationHelper createHelper, int startRow) throws JSONException, SerializationException{		
	    // we enrich the JSON object putting every node the descendants_no property: it is useful when merging cell into rows/columns headers
	    // and when initializing the sheet
    	int totalRowNum = commonFillSheet(sheet, cs, crosstabJSON, createHelper,startRow);
    	return totalRowNum;
	}
	
	public int commonFillSheet(Sheet sheet,CrossTab cs, JSONObject crosstabJSON, CreationHelper createHelper, int startRow) throws SerializationException, JSONException{	
    	int columnsDepth = cs.getColumnsRoot().getSubTreeDepth();
		int rowsDepth = cs.getRowsRoot().getSubTreeDepth();

		
		MeasureFormatter measureFormatter = new MeasureFormatter(crosstabJSON, new DecimalFormat("#0.00"),"#0.00");
		int rowsNumber = cs.getDataMatrix().length;
		int totalRowsNumber = columnsDepth + rowsNumber + 1; // + 1 because there may be also the bottom row with the totals
		for (int i = 0; i < totalRowsNumber + 5; i++) {
			sheet.createRow(startRow+i);
		}
//	
		// build headers for column first ...
		buildColumnsHeader(sheet, cs.getColumnsRoot().getChilds(), startRow, rowsDepth - 1, createHelper);
		// ... then build headers for rows ....
	    buildRowsHeaders(sheet, cs.getRowsRoot().getChilds(), columnsDepth - 1 + startRow, 0, createHelper);
	    // then put the matrix data
	    buildDataMatrix(sheet, cs, columnsDepth + startRow-1, rowsDepth - 1, createHelper, measureFormatter);
	    
	    buildRowHeaderTitle(sheet, null, columnsDepth-2, 0, startRow, createHelper);
	    
	    return startRow+totalRowsNumber;
	}
	

	/**
	 * Sheet initialization. We create as many rows as it is required to contain the crosstab.
	 * 
	 * @param sheet The XLS sheet
	 * @param json The crosstab data (it must have been enriched with the calculateDescendants method)
	 * @throws JSONException
	 */
	public int initSheet(Sheet sheet, CrossTab cs) throws JSONException {
		
		int columnsDepth = cs.getColumnsRoot().getSubTreeDepth();
		int rowsNumber =  cs.getRowsRoot().getSubTreeDepth();
		int totalRowsNumber = columnsDepth + rowsNumber + 1; // + 1 because there may be also the bottom row with the totals
		for (int i = 0; i < totalRowsNumber + 4; i++) {
			sheet.createRow(i);
		}
		return totalRowsNumber+4;
	}

	private int buildDataMatrix(Sheet sheet, CrossTab cs, int rowOffset, int columnOffset, CreationHelper createHelper, MeasureFormatter measureFormatter) throws JSONException {
		//CellStyle cellStyle = buildDataCellStyle(sheet);
		
		//CellStyle sumCellStyle = buildDataCellStyle(sheet);
		
		Map<Integer, CellStyle> decimalFormats = new HashMap<Integer, CellStyle>();
		int endRowNum = 0;
		for (int i = 0; i < cs.getDataMatrix().length; i++) {
			for (int j = 0; j < cs.getDataMatrix()[0].length; j++) {
				String text = (String) cs.getDataMatrix()[i][j];
				int rowNum = rowOffset + i ;
				int columnNum = columnOffset + j ;
				Row row = sheet.getRow(rowNum);
				if(row==null){
					row = sheet.createRow(rowNum);
				}
				endRowNum = rowNum;
				Cell cell = row.createCell(columnNum);
				//cell.setCellStyle(cellStyle);
				
				
				try {

					double value = Double.parseDouble(text);
					int decimals = measureFormatter.getFormatXLS(i, j);
					cell.setCellValue(new Double(value));
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(getNumberFormat(decimals, decimalFormats, sheet, createHelper,cs.getCellType(i,j) ));
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
	private void buildRowsHeaders(Sheet sheet, List<Node> siblings, int rowNum, int columnNum, CreationHelper createHelper) throws JSONException {
		int rowsCounter = rowNum;
		
		CellStyle cellStyle = buildHeaderCellStyle(sheet);
        
		for (int i = 0; i < siblings.size(); i++) {
			Node aNode =  siblings.get(i);
			Row row = sheet.getRow(rowsCounter);
			Cell cell = row.createCell(columnNum);
			String text = (String) aNode.getValue();
			cell.setCellValue(createHelper.createRichTextString(text));
		    cell.setCellType(HSSFCell.CELL_TYPE_STRING);

	        cell.setCellStyle(cellStyle);
	       
		    int descendants = aNode.getLeafsNumber();
		    if (descendants > 1) {
			    sheet.addMergedRegion(new CellRangeAddress(
			    		rowsCounter, //first row (0-based)
			    		rowsCounter + descendants - 1, //last row  (0-based)
			    		columnNum, //first column (0-based)
			    		columnNum //last column  (0-based)
			    ));
		    }
		    List<Node> childs = aNode.getChilds();
		    if (childs != null && childs.size() > 0) {
		    	buildRowsHeaders(sheet, childs, rowsCounter, columnNum + 1, createHelper);
		    }
		    int increment = descendants > 1 ? descendants : 1;
		    rowsCounter = rowsCounter + increment;
		}
	}
	
	/**
	 * Add the title of the columns in the row headers
	 * @param sheet
	 * @param titles list of titles
	 * @param columnHeadersNumber number of column headers
	 * @param startColumn first column of the crosstab in the xls
	 * @param startRow first row of the crosstab in the xls
	 * @param createHelper
	 * @throws JSONException
	 */
	private void buildRowHeaderTitle(Sheet sheet, List<String> titles, int columnHeadersNumber, int startColumn, int startRow, CreationHelper createHelper) throws JSONException {
		if(titles!=null){
			
			Row row = sheet.getRow(startRow+columnHeadersNumber);
			CellStyle cellStyle = buildHeaderCellStyle(sheet);
			for (int i = 0; i < titles.size(); i++) {
			
				Cell cell = row.createCell(startColumn+i);
				String text = titles.get(i);
				cell.setCellValue(createHelper.createRichTextString(text));
			    cell.setCellType(HSSFCell.CELL_TYPE_STRING);	    
			    cell.setCellStyle(cellStyle);
			}
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
	private void buildColumnsHeader(Sheet sheet, List<Node> siblings, int rowNum, int columnNum, CreationHelper createHelper) throws JSONException {
		int columnCounter = columnNum;
		CellStyle cellStyle = buildHeaderCellStyle(sheet);
		for (int i = 0; i < siblings.size(); i++) {
			Node aNode = (Node) siblings.get(i);
			Row row = sheet.getRow(rowNum);
			Cell cell = row.createCell(columnCounter);
			String text = (String) aNode.getValue();
			cell.setCellValue(createHelper.createRichTextString(text));
		    cell.setCellType(HSSFCell.CELL_TYPE_STRING);	    
		    int descendants = aNode.getLeafsNumber();
		    if (descendants > 1) {
			    sheet.addMergedRegion(new CellRangeAddress(
			    		rowNum, //first row (0-based)
			    		rowNum, //last row  (0-based)
			    		columnCounter, //first column (0-based)
			    		columnCounter + descendants - 1  //last column  (0-based)
			    ));
		    }
		    cell.setCellStyle(cellStyle);
		    
		    List<Node> childs = aNode.getChilds();
		    if (childs != null && childs.size() > 0) {
		    	buildColumnsHeader(sheet, childs, rowNum + 1, columnCounter, createHelper);
		    }
		    int increment = descendants > 1 ? descendants : 1;
		    columnCounter = columnCounter + increment;
		}
	}

	
	private CellStyle getNumberFormat(int j, Map<Integer, CellStyle> decimalFormats, Sheet sheet, CreationHelper createHelper, CellType celltype){

		int mapPosition =  j;
		
		if(celltype.equals(CellType.TOTAL)){
			mapPosition = j+90000;
		}else if(celltype.equals(CellType.SUBTOTAL)){
			mapPosition = j+80000;
		}else if(celltype.equals(CellType.CF)){
			mapPosition = j+60000;
		}
		
		if(decimalFormats.get(mapPosition)!=null)
			return decimalFormats.get(mapPosition);
		String decimals="";
		for(int i=0; i<j; i++){
			decimals+="0";
		}
		
		CellStyle cellStyle = buildDataCellStyle(sheet);
		DataFormat df = createHelper.createDataFormat();
		cellStyle.setDataFormat(df.getFormat("#,##0."+decimals));
		
		if(celltype.equals(CellType.TOTAL)){
			cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		}
		if(celltype.equals(CellType.CF)){
			cellStyle.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
		}
		if(celltype.equals(CellType.SUBTOTAL)){
			cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		}
		
		decimalFormats.put(mapPosition, cellStyle);
		return cellStyle;
	}


}
