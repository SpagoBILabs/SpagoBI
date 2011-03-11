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
package it.eng.qbe.crosstab.exporter;


import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
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
	
	public static transient Logger logger = Logger.getLogger(CrosstabXLSExporter.class);
	
	public static final String CROSSTAB_JSON_DESCENDANTS_NUMBER = "descendants_no";

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
		CreationHelper createHelper = wb.getCreationHelper();
	    Sheet sheet = wb.createSheet("new sheet");

	    // we enrich the JSON object putting every node the descendants_no property: it is useful when merging cell into rows/columns headers
	    // and when initializing the sheet
    	calculateDescendants(json);
    	// init the sheet
    	initSheet(sheet, json);
    	JSONObject columnsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
    	JSONArray columnsRootChilds = columnsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
    	int columnsDepth = getDepth(columnsRoot);
		JSONObject rowsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
		int rowsDepth = getDepth(rowsRoot);
		JSONArray rowsRootChilds = rowsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		
		JSONArray data = (JSONArray) json.get(CrossTab.CROSSTAB_JSON_DATA);
		
		// build headers for column first ...
		buildColumnsHeader(sheet, columnsRootChilds, 0, rowsDepth, createHelper);
		// ... then build headers for rows ....
	    buildRowsHeaders(sheet, rowsRootChilds, columnsDepth, 0, createHelper);
	    // then put the matrix data
	    buildDataMatrix(sheet, data, columnsDepth, rowsDepth, createHelper);
		
		return wb;
	}
	
	

	
	
	/**
	 * Add descendants_no attribute to each node of rows/columns headers' structure.
	 * descendants_no is useful for merging cells when drawing rows/columns headers' into XLS file.
	 */
	private void calculateDescendants(JSONObject json) throws JSONException {
		JSONObject columnsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
		getDescentantNumber(columnsHeaders);
		
		JSONObject rowsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
		getDescentantNumber(rowsHeaders);
	}






	/**
	 * The descendant number of a node is:
	 * 
	 *                  --  root[3] --        // the descendant number is the sum of the children
	 *                 |              |
	 *          -- node[2] --       node[1]   // the descendant number is the count of the children
	 *         |             |        |
	 *      leaf[0]       leaf[0]   leaf[0]   // leaves have no children
	 *      
	 * @param node The node of the rows/columns headers' structure
	 * @return
	 */
	private int getDescentantNumber(JSONObject aNode) throws JSONException {
		int descendants = 0;
		JSONArray childs = aNode.optJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		if (childs != null && childs.length() > 0) {
			for (int i = 0; i < childs.length(); i++) {
				JSONObject aChild = (JSONObject) childs.get(i);
				int childDescendants = getDescentantNumber(aChild);
				if (childDescendants == 0) {
					descendants ++;
				} else {
					descendants += childDescendants;
				}
			}
		}
		aNode.put(CROSSTAB_JSON_DESCENDANTS_NUMBER, descendants);
		return descendants;
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
		int columnsDepth = getDepth(columnsHeaders);
		JSONObject rowsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
		int rowsNumber = rowsHeaders.getInt(CROSSTAB_JSON_DESCENDANTS_NUMBER);
		int totalRowsNumber = columnsDepth + rowsNumber + 1; // + 1 because there may be also the bottom row with the totals
		for (int i = 0; i < totalRowsNumber; i++) {
			sheet.createRow(i);
		}
	}







	private void buildDataMatrix(Sheet sheet, JSONArray data, int rowOffset, int columnOffset, CreationHelper createHelper) throws JSONException {
		for (int i = 0; i < data.length(); i++) {
			JSONArray array = (JSONArray) data.get(i);
			for (int j = 0; j < array.length(); j++) {
				String text = (String) array.get(j);
				int rowNum = rowOffset + i;
				int columnNum = columnOffset + j;
				Row row = sheet.getRow(rowNum);
				Cell cell = row.createCell(columnNum);
				
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
		for (int i = 0; i < siblings.length(); i++) {
			JSONObject aNode = (JSONObject) siblings.get(i);
			Row row = sheet.getRow(rowsCounter);
			Cell cell = row.createCell(columnNum);
			String text = (String) aNode.get(CrossTab.CROSSTAB_NODE_JSON_KEY);
			cell.setCellValue(createHelper.createRichTextString(text));
		    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		    int descendants = aNode.getInt(CROSSTAB_JSON_DESCENDANTS_NUMBER);
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
		for (int i = 0; i < siblings.length(); i++) {
			JSONObject aNode = (JSONObject) siblings.get(i);
			Row row = sheet.getRow(rowNum);
			Cell cell = row.createCell(columnCounter);
			String text = (String) aNode.get(CrossTab.CROSSTAB_NODE_JSON_KEY);
			cell.setCellValue(createHelper.createRichTextString(text));
		    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		    int descendants = aNode.getInt(CROSSTAB_JSON_DESCENDANTS_NUMBER);
		    if (descendants > 1) {
			    sheet.addMergedRegion(new CellRangeAddress(
			    		rowNum, //first row (0-based)
			    		rowNum, //last row  (0-based)
			    		columnCounter, //first column (0-based)
			    		columnCounter + descendants - 1  //last column  (0-based)
			    ));
		    }
		    JSONArray childs = aNode.optJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		    if (childs != null && childs.length() > 0) {
		    	buildColumnsHeader(sheet, childs, rowNum + 1, columnCounter, createHelper);
		    }
		    int increment = descendants > 1 ? descendants : 1;
		    columnCounter = columnCounter + increment;
		}
		
	}

	
	
	/**
	 * Calculates the path length in the nodes structure in input between the root node and a leaf.
	 * Note that this method assumes the path length to be the same between the root node and any leaf!!!
	 * @param node The root node of the tree structure 
	 * @return the path length between the root node and a leaf
	 * @throws JSONException
	 */
	private int getDepth(JSONObject node) throws JSONException {
		int toReturn = 0;
		while (node.opt(CrossTab.CROSSTAB_NODE_JSON_CHILDS) != null) {
			toReturn++;
			JSONArray childs = (JSONArray) node.get(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
			node = (JSONObject) childs.get(0);
		}
		return toReturn;
	}

}
