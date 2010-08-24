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

package it.eng.spagobi.engines.qbe.services.crosstab;

import it.eng.spagobi.engines.qbe.bo.CrosstabDefinition;
import it.eng.spagobi.engines.qbe.bo.CrosstabDefinition.Measure;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Alberto Ghedin
 * 
 * This Class encapsulates the crossTab
 * The publics methods are:
 * - CrossTab(IDataStore dataStore, CrosstabDefinition crosstabDefinition) that builds
 *   the crossTab (headers structure and data)
 *-  getJSONCrossTab() that returns the JSON representation of the crosstab
 */

public class CrossTab {
	
	public static final String CROSSTAB_NODE_JSON_KEY = "node_key";
	public static final String CROSSTAB_NODE_JSON_CHILDS = "node_childs";
	public static final String CROSSTAB_JSON_ROWS_HEADERS = "rows";
	public static final String CROSSTAB_JSON_COLUMNS_HEADERS = "columns";
	public static final String CROSSTAB_JSON_DATA = "data";
	public static final String CROSSTAB_JSON_CONFIG = "config";


	private Node columnsRoot;
	private Node rowsRoot;
	private String[][] dataMatrix;
	private JSONObject config;
	
	/**
	 * Builds the crossTab (headers structure and data)
	 * @param dataStore: the source of the data
	 * @param crosstabDefinition: the definition of the crossTab
	 */
	public CrossTab(IDataStore dataStore, CrosstabDefinition crosstabDefinition){
		IRecord record;
		String rowPath;
		String columnPath;
		this.config = crosstabDefinition.getConfig();
		
		
		boolean measuresOnColumns = crosstabDefinition.isMeasuresOnColumns();
		
		int rowsCount = crosstabDefinition.getRows().size();
		int columnsCount = crosstabDefinition.getColumns().size();
		int measuresCount = crosstabDefinition.getMeasures().size();
		
		List<String> rowCordinates = new ArrayList<String>();
		List<String> columnCordinates = new ArrayList<String>();
		List<String> data = new ArrayList<String>();

		columnsRoot = new Node("rootC");
		rowsRoot = new Node("rootR");
		
		for(int index = 0; index<dataStore.getRecordsCount(); index++){
			record = dataStore.getRecordAt(index);
			addRecord(columnsRoot, record, 0, columnsCount);
			addRecord(rowsRoot, record, columnsCount, columnsCount+rowsCount);
		}

		for(int index = 0; index<dataStore.getRecordsCount(); index++){
			record = dataStore.getRecordAt(index);
			List<IField> fields= new ArrayList<IField>();
			fields = record.getFields();
			columnPath="";
			for(int i=0; i<columnsCount; i++){
				columnPath = columnPath+ (String)fields.get(i).getValue();
			}
						
			rowPath="";
			for(int i=columnsCount; i<record.getFields().size()-measuresCount; i++){
				rowPath = rowPath+ (String)fields.get(i).getValue();
			}

			for(int i=record.getFields().size()-measuresCount; i<record.getFields().size(); i++){
				columnCordinates.add(columnPath);
				rowCordinates.add(rowPath);
				data.add(""+((BigDecimal)fields.get(i).getValue()).intValue());
			}
		}
		
		List<String> columnsSpecification = getLeafsPathList(columnsRoot);
		List<String> rowsSpecification = getLeafsPathList(rowsRoot);
		
		if(measuresOnColumns){
			addMeasuresToTree(columnsRoot, crosstabDefinition.getMeasures());
		}else{
			addMeasuresToTree(rowsRoot, crosstabDefinition.getMeasures());
		}
		
		dataMatrix = getDataMatrix(columnsSpecification, rowsSpecification, columnCordinates, rowCordinates, data, measuresOnColumns, measuresCount);
	}
	
	/**
	 * Get the JSON representation of the cross tab  
	 * @return JSON representation of the cross tab  
	 * @throws JSONException
	 */
	public JSONObject getJSONCrossTab() throws JSONException{
		JSONObject crossTabDefinition = new JSONObject();
		crossTabDefinition.put(CROSSTAB_JSON_ROWS_HEADERS, rowsRoot.toJSONObject());
		crossTabDefinition.put(CROSSTAB_JSON_COLUMNS_HEADERS, columnsRoot.toJSONObject());
		crossTabDefinition.put(CROSSTAB_JSON_DATA,  getJSONDataMatrix());
		crossTabDefinition.put(CROSSTAB_JSON_CONFIG,  config);
		return crossTabDefinition;
	}
	
	/**
	 * Get the matrix that represent the data
	 * @param columnsSpecification: A list with all the possible coordinates of the columns 
	 * @param rowsSpecification: A list with all the possible coordinates of the rows
	 * @param columnCordinates: A list with the column coordinates of all the data
	 * @param rowCordinates: A list with the column rows of all the data
	 * @param data: A list with the data
	 * @param measuresOnColumns: true if the measures live in the columns, false if the measures live in the rows
	 * @param measuresLength: the number of the measures
	 * @return the matrix that represent the data
	 */
	private String[][] getDataMatrix(List<String> columnsSpecification, List<String> rowsSpecification, List<String> columnCordinates, List<String> rowCordinates,  List<String> data, boolean measuresOnColumns, int measuresLength){
		String[][] dataMatrix;
		int x,y;
		int rowsN,columnsN;
		
		if(measuresOnColumns){
			rowsN = rowsSpecification.size();
			columnsN = columnsSpecification.size()*measuresLength;
		}else{
			rowsN = rowsSpecification.size()*measuresLength;
			columnsN = columnsSpecification.size();
		}
		
		dataMatrix = new String[rowsN][columnsN];
		
		//init the matrix
		for(int i=0; i<rowsN; i++){
			for(int j=0; j<columnsN; j++){
				dataMatrix[i][j] = "NA";
			}
		}
		
		if(measuresOnColumns){
			for(int i=0; i<data.size(); i=i+measuresLength){
				for(int j=0; j<measuresLength; j++){
					x = rowsSpecification.indexOf(rowCordinates.get(i+j));
					y = columnsSpecification.indexOf(columnCordinates.get(i+j));
					dataMatrix[x][y*measuresLength+j]=data.get(i+j);
				}
			}
		}else{
			for(int i=0; i<data.size(); i=i+measuresLength){
				for(int j=0; j<measuresLength; j++){
					x = rowsSpecification.indexOf(rowCordinates.get(i+j));
					y = columnsSpecification.indexOf(columnCordinates.get(i+j));
					dataMatrix[x*measuresLength+j][y]=data.get(i+j);
				}
			}
		}		
		
		return dataMatrix;
		
	}
	
	/**
	 * Serialize the matrix in a JSON format
	 * @return the matrix in a JSON format
	 */
	public JSONArray getJSONDataMatrix(){
	
		JSONArray matrix = new JSONArray();
		JSONArray row = new JSONArray();
		
		//transform the matrix
		for(int i=0; i<dataMatrix.length; i++){
			row = new JSONArray();
			for(int j=0; j<dataMatrix[i].length; j++){
				row.put(dataMatrix[i][j]);
			}
			matrix.put(row);
		}
	
		return matrix;
	}

	/**
	 * Add to the root (columnRoot or rowRoot) a path from the root to a leaf. 
	 * A record contains both the columns definition and the rows definition:
	 * (it may be something like that: C1 C2 C3 R1 R2 M1 M1, where Ci represent a column,
	 * Ri represent a row, Mi a measure). So for take a column path (C1 C2 C3), we need
	 * need a start and end position in the record (in this case 0,3) 
	 * @param root: the node in witch add the record
	 * @param record
	 * @param startPosition 
	 * @param endPosition
	 */
	private void addRecord(Node root, IRecord record, int startPosition, int endPosition){
		IField field;
		Node node;
		Node nodeToCheck = root;
		int nodePosition;
		
		List<IField> fields= new ArrayList<IField>();
		fields = record.getFields();
		for(int indexFields = startPosition; indexFields<endPosition; indexFields++){
			field = fields.get(indexFields);
			node = new Node((String)field.getValue());
			nodePosition = nodeToCheck.getChilds().indexOf(node);
			if(nodePosition<0){
				nodeToCheck.addChild(node);
				nodeToCheck = node;
			}else{
				nodeToCheck = nodeToCheck.getChilds().get(nodePosition);
			}
		}
	}
	
	/**
	 * Return a list with all the path from the node n to the leafs
	 * @param n: the root node  
	 * @return list with all the path from the node n to the leafs
	 */
	private List<String> getLeafsPathList(Node n){
		List<String> toReturn = new ArrayList<String>();
		for(int i=0; i<n.getChilds().size(); i++){
			toReturn.addAll(visit(n.getChilds().get(i), ""));
		}
		return toReturn;
	}
	
	private List<String> visit(Node n, String prefix){
		List<String> toReturn = new ArrayList<String>();
		if(n.getChilds().size()==0){
			toReturn.add(prefix+(String)(n.getElement()));
			return toReturn;
		}else{
			for(int i=0; i<n.getChilds().size(); i++){
				toReturn.addAll(visit(n.getChilds().get(i), prefix+(String)(n.getElement())));
			}
			return toReturn;
		}
	}
	
	/**
	 * Add the measures as leafs to all the leafs 
	 * @param root
	 * @param measures
	 */
	private void addMeasuresToTree(Node root, List<Measure> measures){
		List<Node> measuresNodes = new ArrayList<Node>();
		for(int i=0; i<measures.size(); i++){
			measuresNodes.add(new Node(measures.get(i).getAlias()));
		}
		addMeasuresToLeafs(root,measuresNodes);
		
	}
	
	//It's ok that the list of the measures is the same for every leaf
	private void addMeasuresToLeafs(Node node, List<Node> measuresNodes){
		if(node.getChilds().size()==0){
			node.setChilds(measuresNodes);
		}else{
			for(int i=0; i<node.getChilds().size(); i++){
				addMeasuresToLeafs(node.getChilds().get(i),measuresNodes);
			}
		}
	}


	private class Node{
		private String value;
		private List<Node> childs;
		
		public Node(String value){
			this.value = value;
			childs = new ArrayList<Node>();
		}

		public String getElement() {
			return value;
		}

		public List<Node> getChilds() {
			return childs;
		}
		
		public void setChilds(List<Node> childs) {
			this.childs = childs;
		}
		
		public void addChild(Node child){
			childs.add(child);
		}
		
		public boolean isChild(Node child){
			return childs.contains(child);
		}
		
		public JSONObject toJSONObject() throws JSONException{
			JSONObject thisNode = new JSONObject();
			
			thisNode.put(CROSSTAB_NODE_JSON_KEY, value);
			
			if(childs.size()>0){
				JSONArray nodeChilds = new JSONArray();
				for(int i=0; i<childs.size(); i++){
					nodeChilds.put(childs.get(i).toJSONObject());
				}
				thisNode.put(CROSSTAB_NODE_JSON_CHILDS, nodeChilds);
			}
					
			return thisNode;
		}
		
		@Override
		public String toString(){
			String string;
			
			if(childs.size()==0){
				return "["+value.toString()+"]";
			}else{
				string = "["+value.toString()+",[";
				for(int i=0; i<childs.size()-1; i++){
					string = string+childs.get(i).toString()+",";
				}
				string=string+childs.get(childs.size()-1).toString()+"]]";
			}
			return string;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		private CrossTab getOuterType() {
			return CrossTab.this;
		}

	}
}
