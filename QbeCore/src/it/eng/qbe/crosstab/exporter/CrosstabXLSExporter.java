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

import it.eng.qbe.crosstab.bo.CrossTab;

import java.io.File;
import java.io.FileOutputStream;

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
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class CrosstabXLSExporter {
	
	static CreationHelper createHelper = null;
	public static final String CROSSTAB_JSON_DESCENDANTS_NUMBER = "descendants_no";
	
	public static Workbook export(JSONObject json) {
		
		Workbook wb = new HSSFWorkbook();
	    createHelper = wb.getCreationHelper();
	    Sheet sheet = wb.createSheet("new sheet");

	    try {
	    	calculateDescendants(json);
	    	System.out.println(json);
	    	initSheet(sheet, json);
	    	JSONObject columnsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
	    	JSONArray columnsRootChilds = columnsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
	    	int columnsDepth = getDepth(columnsRoot);
			JSONObject rowsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
			int rowsDepth = getDepth(rowsRoot);
			JSONArray rowsRootChilds = rowsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
			
			JSONArray data = (JSONArray) json.get(CrossTab.CROSSTAB_JSON_DATA);
			
			buildColumnsHeader(sheet, columnsRootChilds, 0, rowsDepth);
		    buildRowsHeaders(sheet, rowsRootChilds, columnsDepth, 0);
		    buildDataMatrix(sheet, data, columnsDepth, rowsDepth);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return wb;
	}
	
	
	public static void main(String[] args) {
		
//		Workbook wb = new HSSFWorkbook();
//	    Sheet sheet = wb.createSheet("new sheet");
//
//	    Row row = sheet.createRow((short) 1);
//	    Cell cell = row.createCell((short) 1);
//	    cell.setCellValue("This is a test of merging");
//
//	    sheet.addMergedRegion(new CellRangeAddress(
//	            1, //first row (0-based)
//	            1, //last row  (0-based)
//	            1, //first column (0-based)
//	            4  //last column  (0-based)
//	    ));

		
		
		JSONObject json = null;
		
		try {
			json = new JSONObject(TEST);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Workbook wb = new HSSFWorkbook();
	    createHelper = wb.getCreationHelper();
	    Sheet sheet = wb.createSheet("new sheet");

	    try {
	    	calculateDescendants(json);
	    	System.out.println(json);
	    	initSheet(sheet, json);
	    	JSONObject columnsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
	    	JSONArray columnsRootChilds = columnsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
	    	int columnsDepth = getDepth(columnsRoot);
			JSONObject rowsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
			int rowsDepth = getDepth(rowsRoot);
			JSONArray rowsRootChilds = rowsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
			
			JSONArray data = (JSONArray) json.get(CrossTab.CROSSTAB_JSON_DATA);
			
			buildColumnsHeader(sheet, columnsRootChilds, 0, rowsDepth);
		    buildRowsHeaders(sheet, rowsRootChilds, columnsDepth, 0);
		    buildDataMatrix(sheet, data, columnsDepth, rowsDepth);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    
	    // Write the output to a file
	    FileOutputStream fileOut;
		try {
			File file = new File("C:/workbook.xls");
			if (file.exists()) {
				file.delete();
			}
			fileOut = new FileOutputStream(file);
		    wb.write(fileOut);
		    fileOut.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	
	
	/**
	 * Add descendants_no attribute to each node of rows/columns headers' structure.
	 * descendants_no is useful for merging cells when drawing rows/columns headers' into XLS file.
	 */
	private static void calculateDescendants(JSONObject json) throws JSONException {
		JSONObject columnsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
		getDescentantNumber(columnsHeaders);
		
		JSONObject rowsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
		getDescentantNumber(rowsHeaders);
	}






	/**
	 * The descendant number is the some of the descendants of the children, or 0 if the node has no children
	 * @param node
	 * @return
	 */
	private static int getDescentantNumber(JSONObject aNode) throws JSONException {
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






	private static void initSheet(Sheet sheet, JSONObject json) throws JSONException {
		JSONObject columnsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
		int columnsNumber = columnsHeaders.getInt(CROSSTAB_JSON_DESCENDANTS_NUMBER);
		int columnsDepth = getDepth(columnsHeaders);
		JSONObject rowsHeaders = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
		int rowsNumber = rowsHeaders.getInt(CROSSTAB_JSON_DESCENDANTS_NUMBER);
		int rowsDepth = getDepth(rowsHeaders);
		int totalRowsNumber = columnsDepth + rowsNumber;
		for (int i = 0; i < totalRowsNumber; i++) {
			sheet.createRow(i);
		}
		
	}







	private static void buildDataMatrix(Sheet sheet, JSONArray data, int rowOffset, int columnOffset) throws JSONException {
		for (int i = 0; i < data.length(); i++) {
			JSONArray array = (JSONArray) data.get(i);
			for (int j = 0; j < array.length(); j++) {
				String text = (String) array.get(j);
				int rowNum = rowOffset + i;
				int columnNum = columnOffset + j;
				Row row = sheet.getRow(rowNum);
				Cell cell = row.createCell(columnNum);
				cell.setCellValue(createHelper.createRichTextString(text));
			    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			}
		}
		
	}







	private static void buildRowsHeaders(Sheet sheet, JSONArray siblings, int rowNum, int columnNum) throws JSONException {
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
		    	buildRowsHeaders(sheet, childs, rowsCounter, columnNum + 1);
		    }
		    int increment = descendants > 1 ? descendants : 1;
		    rowsCounter = rowsCounter + increment;
		}
		
	}







	private static void buildColumnsHeader(Sheet sheet, JSONArray siblings, int rowNum, int columnNum) throws JSONException {
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
		    	buildColumnsHeader(sheet, childs, rowNum + 1, columnCounter);
		    }
		    int increment = descendants > 1 ? descendants : 1;
		    columnCounter = columnCounter + increment;
		}
		
	}




	private static int getDepth(JSONObject columnsHeaders) throws JSONException {
		int toReturn = 0;
		while (columnsHeaders.opt(CrossTab.CROSSTAB_NODE_JSON_CHILDS) != null) {
			toReturn++;
			JSONArray childs = (JSONArray) columnsHeaders.get(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
			columnsHeaders = (JSONObject) childs.get(0);
		}
		return toReturn;
	}




	public static final String TEST = "{data:[[\"2.0\", \"5.52\", \"27.0\", \"74.52\", \"20.0\", \"55.2\", \"25.0\", \"69.0\", \"30.0\", \"82.8\", \"22.0\", \"60.72\", \"22.0\", \"60.72\"," +  
	"\"4.0\", \"11.04\", \"23.0\", \"63.48\", \"29.0\", \"80.04\", \"19.0\", \"52.44\", \"NA\", \"NA\", \"NA\", \"NA\"], [\"7.0\", \"18.22\", \"24.0\", \"76.3\", " + 
	"\"48.0\", \"113.46\", \"29.0\", \"80.78\", \"37.0\", \"88.36\", \"34.0\", \"79.08\", \"38.0\", \"79.78\", \"NA\", \"NA\", \"33.0\", \"87.66\", \"27.0\", " + 
	"\"69.1\", \"35.0\", \"122.0\", \"6.0\", \"18.56\", \"14.0\", \"52.92\"], [\"6.0\", \"6.76\", \"106.0\", \"104.58\", \"64.0\", \"67.9\", \"64.0\", \"70.6\", " + 
	"\"59.0\", \"57.7\", \"65.0\", \"76.72\", \"62.0\", \"65.2\", \"1.0\", \"1.2\", \"63.0\", \"70.44\", \"79.0\", \"80.72\", \"121.0\", \"134.36\", \"12.0\", " + 
	"\"10.98\", \"36.0\", \"31.62\"], [\"10.0\", \"21.4\", \"63.0\", \"126.09\", \"44.0\", \"92.96\", \"61.0\", \"129.13\", \"74.0\", \"154.79\", \"73.0\", " + 
	"\"149.71\", \"59.0\", \"113.63\", \"8.0\", \"17.57\", \"46.0\", \"89.35\", \"57.0\", \"132.54\", \"100.0\", \"217.66\", \"7.0\", \"17.83\", \"23.0\", " + 
	"\"53.03\"], [\"4.0\", \"3.04\", \"58.0\", \"66.14\", \"68.0\", \"64.24\", \"80.0\", \"77.93\", \"65.0\", \"82.02\", \"138.0\", \"155.71\", \"87.0\", " + 
	"\"127.93\", \"NA\", \"NA\", \"51.0\", \"49.16\", \"80.0\", \"94.02\", \"109.0\", \"166.47\", \"NA\", \"NA\", \"23.0\", \"36.45\"], [\"7.0\", \"20.79\", " + 
	"\"73.0\", \"165.2\", \"36.0\", \"86.82\", \"54.0\", \"126.53\", \"55.0\", \"147.82\", \"90.0\", \"189.83\", \"85.0\", \"176.76\", \"5.0\", \"9.93\", " + 
	"\"113.0\", \"316.43\", \"40.0\", \"96.78\", \"93.0\", \"223.58\", \"4.0\", \"3.67\", \"22.0\", \"57.51\"], [\"31.0\", \"63.85\", \"224.0\", \"464.09\", " + 
	"\"316.0\", \"674.82\", \"365.0\", \"694.2\", \"344.0\", \"666.53\", \"486.0\", \"963.52\", \"374.0\", \"774.39\", \"32.0\", \"71.65\", \"352.0\", " + 
	"\"677.79\", \"407.0\", \"746.63\", \"467.0\", \"952.74\", \"35.0\", \"65.48\", \"166.0\", \"314.92\"]], " +
	"" +
	"columns:" +
	"	{node_key:\"rootC\", node_childs:[" +
	"			{node_key:\"USA\", node_childs:[" +
	"				{node_key:\"Bellingham\", node_childs:[" +
	"					{node_key:\"Unit Sales\"}, " +
	"					{node_key:\"Store Sales\"}]}," +  
	"				{node_key:\"Beverly Hills\", node_childs:[" +
	"					{node_key:\"Unit Sales\"}, " +
	"					{node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"Bremerton\", node_childs:[" +
	"					{node_key:\"Unit Sales\"}, " +
	"					{node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"Los Angeles\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"Portland\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"Salem\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"San Diego\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"San Francisco\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"Seattle\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"Spokane\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"Tacoma\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}, " + 
	"				{node_key:\"Walla Walla\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}," +  
	"				{node_key:\"Yakima\", node_childs:[{node_key:\"Unit Sales\"}, {node_key:\"Store Sales\"}]}]}]}, " + 
	"config:{measureson:\"columns\"}, " +
	"" +
	"rows:" +
	"	{node_key:\"rootR\", node_childs:[" +
	"		{node_key:\"ADJ\"}, " + 
	"		{node_key:\"James Bay\"}, " +
	"		{node_key:\"Jardon\"}, " +
	"		{node_key:\"Jeffers\"}, " +
	"		{node_key:\"Johnson\"}," +  
	"		{node_key:\"Jumbo\"}, " +
	"		{node_key:\"Just Right\"}]}} ";
	
	


//	public static final String TEST = "{data:[[\"2.0\", \"5.52\", \"27.0\", \"74.52\", \"20.0\", \"55.2\", \"25.0\", \"69.0\", \"30.0\", \"82.8\", \"22.0\", \"60.72\", \"22.0\", \"60.72\"," +  
//"\"4.0\", \"11.04\", \"23.0\", \"63.48\", \"29.0\", \"80.04\", \"19.0\", \"52.44\", \"NA\", \"NA\", \"NA\", \"NA\"], [\"7.0\", \"18.22\", \"24.0\", \"76.3\", " + 
//"\"48.0\", \"113.46\", \"29.0\", \"80.78\", \"37.0\", \"88.36\", \"34.0\", \"79.08\", \"38.0\", \"79.78\", \"NA\", \"NA\", \"33.0\", \"87.66\", \"27.0\", " + 
//"\"69.1\", \"35.0\", \"122.0\", \"6.0\", \"18.56\", \"14.0\", \"52.92\"], [\"6.0\", \"6.76\", \"106.0\", \"104.58\", \"64.0\", \"67.9\", \"64.0\", \"70.6\", " + 
//"\"59.0\", \"57.7\", \"65.0\", \"76.72\", \"62.0\", \"65.2\", \"1.0\", \"1.2\", \"63.0\", \"70.44\", \"79.0\", \"80.72\", \"121.0\", \"134.36\", \"12.0\", " + 
//"\"10.98\", \"36.0\", \"31.62\"], [\"10.0\", \"21.4\", \"63.0\", \"126.09\", \"44.0\", \"92.96\", \"61.0\", \"129.13\", \"74.0\", \"154.79\", \"73.0\", " + 
//"\"149.71\", \"59.0\", \"113.63\", \"8.0\", \"17.57\", \"46.0\", \"89.35\", \"57.0\", \"132.54\", \"100.0\", \"217.66\", \"7.0\", \"17.83\", \"23.0\", " + 
//"\"53.03\"], [\"4.0\", \"3.04\", \"58.0\", \"66.14\", \"68.0\", \"64.24\", \"80.0\", \"77.93\", \"65.0\", \"82.02\", \"138.0\", \"155.71\", \"87.0\", " + 
//"\"127.93\", \"NA\", \"NA\", \"51.0\", \"49.16\", \"80.0\", \"94.02\", \"109.0\", \"166.47\", \"NA\", \"NA\", \"23.0\", \"36.45\"], [\"7.0\", \"20.79\", " + 
//"\"73.0\", \"165.2\", \"36.0\", \"86.82\", \"54.0\", \"126.53\", \"55.0\", \"147.82\", \"90.0\", \"189.83\", \"85.0\", \"176.76\", \"5.0\", \"9.93\", " + 
//"\"113.0\", \"316.43\", \"40.0\", \"96.78\", \"93.0\", \"223.58\", \"4.0\", \"3.67\", \"22.0\", \"57.51\"], [\"31.0\", \"63.85\", \"224.0\", \"464.09\", " + 
//"\"316.0\", \"674.82\", \"365.0\", \"694.2\", \"344.0\", \"666.53\", \"486.0\", \"963.52\", \"374.0\", \"774.39\", \"32.0\", \"71.65\", \"352.0\", " + 
//"\"677.79\", \"407.0\", \"746.63\", \"467.0\", \"952.74\", \"35.0\", \"65.48\", \"166.0\", \"314.92\"]], " +
//"" +
//"columns:" +
//"	{node_key:\"rootC\", descendants_no: 26, node_childs:[" +
//"			{node_key:\"USA\", descendants_no: 26, node_childs:[" +
//"				{node_key:\"Bellingham\", descendants_no: 2, node_childs:[" +
//"					{node_key:\"Unit Sales\", descendants_no: 0}, " +
//"					{node_key:\"Store Sales\", descendants_no: 0}]}," +  
//"				{node_key:\"Beverly Hills\", descendants_no: 2, node_childs:[" +
//"					{node_key:\"Unit Sales\", descendants_no: 0}, " +
//"					{node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"Bremerton\", descendants_no: 2, node_childs:[" +
//"					{node_key:\"Unit Sales\", descendants_no: 0}, " +
//"					{node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"Los Angeles\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"Portland\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"Salem\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"San Diego\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"San Francisco\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"Seattle\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"Spokane\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"Tacoma\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}, " + 
//"				{node_key:\"Walla Walla\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}," +  
//"				{node_key:\"Yakima\", descendants_no: 2, node_childs:[{node_key:\"Unit Sales\", descendants_no: 0}, {node_key:\"Store Sales\", descendants_no: 0}]}]}]}, " + 
//"config:{measureson:\"columns\"}, " +
//"" +
//"rows:" +
//"	{node_key:\"rootR\", descendants_no: 7, node_childs:[" +
//"		{node_key:\"ADJ\", descendants_no: 0}, " + 
//"		{node_key:\"James Bay\", descendants_no: 0}, " +
//"		{node_key:\"Jardon\", descendants_no: 0}, " +
//"		{node_key:\"Jeffers\", descendants_no: 0}, " +
//"		{node_key:\"Johnson\", descendants_no: 0}," +  
//"		{node_key:\"Jumbo\", descendants_no: 0}, " +
//"		{node_key:\"Just Right\", descendants_no: 0}]}} ";

}
