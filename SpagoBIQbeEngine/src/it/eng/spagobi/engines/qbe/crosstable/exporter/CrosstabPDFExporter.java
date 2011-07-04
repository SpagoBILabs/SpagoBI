/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.crosstable.exporter;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import it.eng.spagobi.engines.qbe.crosstable.CrossTab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lowagie.text.Cell;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Table;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class CrosstabPDFExporter {

	
	private Vector<List<Cell>> dataMatrix;
	//private static final Color headersFontColor = new Color(55,131,232);
	private static final Color headersBackgroundColor = new Color(238,238,238);
	private static final Color contentBackgroundColor = new Color(255,255,255);
	private static final Color tableBorderColor = new Color(153,187,232);
	private static final Color cellsBorderColor = new Color(208,208,208);
	private static final int cellsContentHorizontalAlign = Table.ALIGN_RIGHT;
	private static final int cellsHeaderHorizontalAlign = Table.ALIGN_CENTER;
	//private static final int cellsHeaderVerticalAlign = Table.ALIGN_MIDDLE;
	private static final int cellsContentVerticalAlign = Table.ALIGN_MIDDLE;
	
	private static final int tablePadding = 2;
	private DecimalFormat numberFormat;
	
	/**
	 * Builds the table for the crosstab
	 * @param json the JSON representation of the crosstab
	 * @param pdfDocument the pdf document that should contains the crosstab
	 * @param numberFormat the formatter for the numbers
	 * @throws JSONException
	 * @throws BadElementException
	 * @throws DocumentException
	 */
	public void export(JSONObject json, Document pdfDocument, DecimalFormat numberFormat) throws JSONException, BadElementException, DocumentException {
		//prepare the crosstab for the export
		CrosstabExporterUtility.calculateDescendants(json);
		JSONObject columnsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_COLUMNS_HEADERS);
    	JSONArray columnsRootChilds = columnsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		JSONObject rowsRoot = (JSONObject) json.get(CrossTab.CROSSTAB_JSON_ROWS_HEADERS);
		JSONArray rowsRootChilds = rowsRoot.getJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		JSONArray data = (JSONArray) json.get(CrossTab.CROSSTAB_JSON_DATA);

		this.numberFormat = numberFormat;
		
		//build the matrix for the content
		dataMatrix = new Vector<List<Cell>>();
		buildDataMatrix(data);
		
		//number of headers lavels
		int rowsDepth = CrosstabExporterUtility.getDepth(rowsRoot);
		int columnsDepth = CrosstabExporterUtility.getDepth(columnsRoot);

		//build the table
		Table table = new Table(rowsDepth+dataMatrix.get(0).size());
		table.setPadding(tablePadding);
		table.setBorderColor(tableBorderColor);
		Cell d = table.getDefaultCell();
		d.setBorderColor(cellsBorderColor);
		d.setHorizontalAlignment(cellsContentHorizontalAlign);
		d.setBackgroundColor(contentBackgroundColor);
		d.setVerticalAlignment(cellsContentVerticalAlign);
		table.setDefaultCell(d);
		
		//build the empty cell on the top left 
		Cell topLeftCell = new Cell("");
		topLeftCell.setRowspan(columnsDepth);
		topLeftCell.setColspan(rowsDepth);
		topLeftCell.setBorderColorLeft(Color.WHITE);
		topLeftCell.setBorderColorTop(Color.WHITE);
		topLeftCell.setBorderColorBottom(tableBorderColor);
		topLeftCell.setBorderColorRight(tableBorderColor);
		table.addCell(topLeftCell);
		
		List<Cell> cells = new ArrayList<Cell>();

		//builds the headers
		cells.addAll(buildColumnsHeader(columnsRootChilds));
		cells.addAll(buildRowsHeaders(rowsRootChilds));
		
		//adds the headers
		for(int i=0; i<cells.size();i++){
			table.addCell(cells.get(i));
		}
		table.setWidth(100);
		pdfDocument.add(table);
	}
	

	/**
	 * Build the matrix for the content of the crosstab
	 * @param data
	 * @throws JSONException
	 */
	private void buildDataMatrix(JSONArray data) throws JSONException {
		for (int i = 0; i < data.length(); i++) {
			JSONArray array = (JSONArray) data.get(i);
			List<Cell> dataRow = new ArrayList<Cell>();
			for (int j = 0; j < array.length(); j++) {
				String text = (String) array.get(j);
				Cell cell = new Cell(getFormattedString(text));
				dataRow.add(cell);				
			}
			dataMatrix.add(dataRow);
		}
	}

	/**
	 * Builds the row headers. This method performs a depth first visit
	 * of the row headers tree
	 * @param siblings: a level (L) of headers
	 * @return the cells from level L to the leafs
	 * @throws JSONException
	 * @throws BadElementException
	 */
	private List<Cell> buildRowsHeaders(JSONArray siblings) throws JSONException, BadElementException {
		JSONArray childs;
		List<Cell> rowNodes = new ArrayList<Cell>(); 

		//For every node of the level..
		for (int i = 0; i < siblings.length(); i++) {
			JSONObject aNode = (JSONObject) siblings.get(i);
			String text = (String) aNode.get(CrossTab.CROSSTAB_NODE_JSON_KEY);
			int descendants = aNode.getInt(CrosstabExporterUtility.CROSSTAB_JSON_DESCENDANTS_NUMBER);
		    
			Cell cell = new Cell(text);
			cell.setBackgroundColor(headersBackgroundColor);
			cell.setHorizontalAlignment(cellsHeaderHorizontalAlign);
			
			if(descendants>1){
				cell.setRowspan(descendants);
			}
			
			//1) add the node name
			rowNodes.add(cell);
			
			//2) add the child node names
			childs = aNode.optJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		    if (childs != null && childs.length() > 0) {
		    	rowNodes.addAll(buildRowsHeaders(childs));
		    }else{
		    	rowNodes.addAll(dataMatrix.remove(0));
		    }
		}
		return rowNodes;
	}

	/**
	 * Builds cells for the column headers
	 * @param siblings the top level 
	 * @return
	 * @throws JSONException
	 * @throws BadElementException
	 */
	private List<Cell> buildColumnsHeader(JSONArray siblings) throws JSONException, BadElementException {

		List<Cell> cells = new ArrayList<Cell>();
		
		List<JSONObject> columnNodes = getAllNodes(siblings); 
			
		for (int i = 0; i < columnNodes.size(); i++) {
			JSONObject aNode = (JSONObject) columnNodes.get(i);
			String text = (String) aNode.get(CrossTab.CROSSTAB_NODE_JSON_KEY);
			int descendants = aNode.getInt(CrosstabExporterUtility.CROSSTAB_JSON_DESCENDANTS_NUMBER);
		    
			Cell cell = new Cell(text);
			cell.setBorderColor(cellsBorderColor);
			cell.setBackgroundColor(headersBackgroundColor);
			cell.setHorizontalAlignment(cellsHeaderHorizontalAlign);
			
			if(descendants>1){
				cell.setColspan(descendants);
			}
			cells.add(cell);
		}
		
		return cells;
	}

	/**
	 * Performs a breadth first visit of the tree..
	 * @param siblings
	 * @return
	 * @throws JSONException
	 */
	private List<JSONObject> getAllNodes(JSONArray siblings) throws JSONException{
		JSONArray childs;
		List<JSONObject> childLevelNodes = new ArrayList<JSONObject>();
		List<JSONObject> levelNodes = new ArrayList<JSONObject>();
		
		for (int i = 0; i < siblings.length(); i++) {
			JSONObject aNode = (JSONObject) siblings.get(i);
			levelNodes.add(aNode);
			childs = aNode.optJSONArray(CrossTab.CROSSTAB_NODE_JSON_CHILDS);
		    if (childs != null && childs.length() > 0) {
		    	childLevelNodes.addAll(getAllNodes(childs));
		    }
		}

		levelNodes.addAll(childLevelNodes);
		
		return levelNodes;
	}
	
	private String getFormattedString(String string) {
		try{
			Float f = new Float(string);
			return numberFormat.format(f);
		}catch (Exception e) {
			return string;
		}
	}


}
