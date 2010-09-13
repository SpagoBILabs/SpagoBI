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

package it.eng.qbe.crosstab.bo;

import it.eng.qbe.crosstab.bo.CrosstabDefinition.Measure;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
	public static final String CROSSTAB_JSON_MEASURES_METADATA = "measures_metadata";
	
	public static final String MEASURE_NAME = "name";
	public static final String MEASURE_TYPE = "type";
	public static final String MEASURE_FORMAT = "format";

	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy" );
	private static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );

	private Node columnsRoot;
	private Node rowsRoot;
	private String[][] dataMatrix;
	private JSONObject config;
	private List<MeasureInfo> measures;
	
	/**
	 * Builds the crossTab (headers structure and data)
	 * @param dataStore: the source of the data
	 * @param crosstabDefinition: the definition of the crossTab
	 */
	public CrossTab(IDataStore dataStore, CrosstabDefinition crosstabDefinition) throws JSONException{
		IRecord record;
		String rowPath;
		String columnPath;
		this.config = crosstabDefinition.getConfig();
		int cellLimit = new Integer((String)ConfigSingleton.getInstance().getAttribute("QBE.QBE-CROSSTAB-CELLS-LIMIT.value"));
		boolean columnsOverflow = false; //true if the number of cell shown in the crosstab is less than the total number of cells
		
		boolean measuresOnColumns = crosstabDefinition.isMeasuresOnColumns();
		
		int rowsCount = crosstabDefinition.getRows().size();
		int columnsCount = crosstabDefinition.getColumns().size();
		int measuresCount = crosstabDefinition.getMeasures().size();
		int index;
		
		cellLimit = cellLimit/measuresCount;
		
		List<String> rowCordinates = new ArrayList<String>();
		List<String> columnCordinates = new ArrayList<String>();
		List<String> data = new ArrayList<String>();

		columnsRoot = new Node("rootC");
		rowsRoot = new Node("rootR");

		for(index = 0; index<dataStore.getRecordsCount() && index<cellLimit; index++){
			record = dataStore.getRecordAt(index);
			addRecord(columnsRoot, record, 0, columnsCount);
			addRecord(rowsRoot, record, columnsCount, columnsCount+rowsCount);
		}
		
		if(index<dataStore.getRecordsCount()){
			Node completeColumnsRoot =  new Node("rootCompleteC");
			for(index = 0; index<dataStore.getRecordsCount(); index++){
				record = dataStore.getRecordAt(index);
				addRecord(completeColumnsRoot, record, 0, columnsCount);
			}
			columnsOverflow =  columnsRoot.getLeafsNumber()<completeColumnsRoot.getLeafsNumber();
		}
				
		for(index = 0; index<dataStore.getRecordsCount(); index++){
			record = dataStore.getRecordAt(index);
			List<IField> fields= record.getFields();
			columnPath="";
			for(int i=0; i<columnsCount; i++){
				Object value = fields.get(i).getValue();
				String valueStr = null;
				if (value == null){
					valueStr = "null";
				} else {
					valueStr = value.toString();
				}
				columnPath = columnPath + valueStr;
			}
						
			rowPath="";
			for(int i=columnsCount; i<record.getFields().size()-measuresCount; i++){
				Object value = fields.get(i).getValue();
				String valueStr = null;
				if (value == null){
					valueStr = "null";
				} else {
					valueStr = value.toString();
				}
				rowPath = rowPath + valueStr.toString();
			}

			for(int i=record.getFields().size()-measuresCount; i<record.getFields().size(); i++){
				columnCordinates.add(columnPath);
				rowCordinates.add(rowPath);
				data.add(""+getStringValue(fields.get(i).getValue()));
			}
		}
		
		List<String> columnsSpecification = getLeafsPathList(columnsRoot);
		List<String> rowsSpecification = getLeafsPathList(rowsRoot);
		
		if(measuresOnColumns){
			addMeasuresToTree(columnsRoot, crosstabDefinition.getMeasures());
		}else{
			addMeasuresToTree(rowsRoot, crosstabDefinition.getMeasures());
		}
		config.put("columnsOverflow", columnsOverflow);
		dataMatrix = getDataMatrix(columnsSpecification, rowsSpecification, columnCordinates, rowCordinates, data, measuresOnColumns, measuresCount, columnsRoot.getLeafsNumber());
		
		// put measures' info into measures variable 
		measures = new ArrayList<CrossTab.MeasureInfo>();
		IDataStoreMetaData meta = dataStore.getMetaData();
		for(int i = meta.getFieldCount() - measuresCount; i < meta.getFieldCount(); i++){
			// the field number i contains the measure number (i - <number of dimensions>)
			// but <number of dimension> is <total fields count> - <total measures count>
			IFieldMetaData fieldMeta = meta.getFieldMeta(i);
			Measure relevantMeasure = crosstabDefinition.getMeasures().get( i - (meta.getFieldCount() - measuresCount));
			measures.add(getMeasureInfo(fieldMeta, relevantMeasure));
		}
	}
	

	/**
	 * Get the JSON representation of the cross tab  
	 * @return JSON representation of the cross tab  
	 * @throws JSONException
	 */
	public JSONObject getJSONCrossTab() throws JSONException{
		JSONObject crossTabDefinition = new JSONObject();
		crossTabDefinition.put(CROSSTAB_JSON_MEASURES_METADATA, getJSONMeasuresMetadata());
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
	private String[][] getDataMatrix(List<String> columnsSpecification, List<String> rowsSpecification, List<String> columnCordinates, List<String> rowCordinates,  List<String> data, boolean measuresOnColumns, int measuresLength, int columnsN){
		String[][] dataMatrix;
		int x,y;
		int rowsN;
		
		if (measuresOnColumns) {
			rowsN = (rowsSpecification.size() > 0 ? rowsSpecification.size() : 1);
		} else {
			rowsN = (rowsSpecification.size() > 0 ? rowsSpecification.size() : 1)*measuresLength;
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
					if ( x < 0 ) {
						x = 0;
					}
					y = columnsSpecification.indexOf(columnCordinates.get(i+j));
					if ( y < 0 ) {
						y = 0;
					}
					if((y*measuresLength+j)<columnsN && (y*measuresLength+j)>=0){
						dataMatrix[x][y*measuresLength+j]=data.get(i+j);
					}
				}
			}
		}else{
			for(int i=0; i<data.size(); i=i+measuresLength){
				for(int j=0; j<measuresLength; j++){
					x = rowsSpecification.indexOf(rowCordinates.get(i+j));
					if ( x < 0 ) {
						x = 0;
					}
					y = columnsSpecification.indexOf(columnCordinates.get(i+j));
					if ( y < 0 ) {
						y = 0;
					}
					if(y<columnsN && y>=0){
						dataMatrix[x*measuresLength+j][y]=data.get(i+j);
					}
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
			node = new Node(field.getValue().toString());
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

	
	private static String getStringValue(Object obj){
		
		if (obj == null) {
			return "NULL";
		}
		
		String fieldValue = null;
		
		Class clazz = obj.getClass();
		if (clazz == null) {
			clazz = String.class;
		} 
		if (Timestamp.class.isAssignableFrom(clazz)) {
			fieldValue =  TIMESTAMP_FORMATTER.format(  obj );
		} else if (Date.class.isAssignableFrom(clazz)) {
			fieldValue =  DATE_FORMATTER.format( obj );
		} else {
			fieldValue =  obj.toString();
		}
		
		return fieldValue;

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
		
		public int getLeafsNumber(){
			if(childs.size()==0){
				return 1;
			}else{
				int leafsNumber=0;
				for(int i=0; i<childs.size(); i++){
					leafsNumber = leafsNumber + childs.get(i).getLeafsNumber();
				}
				return leafsNumber;
			}
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
	
	
	private MeasureInfo getMeasureInfo(IFieldMetaData fieldMeta, Measure measure) {
		Class clazz = fieldMeta.getType();
		if (clazz == null) {
			clazz = String.class;
		} 
		
		String fieldName = measure.getAlias();  // the measure name is not the name (or alias) of the field coming with the datastore
												// since it is something like SUM(col_0_0_) (see how crosstab datastore query is created)
		
		if( Number.class.isAssignableFrom(clazz) ) {
			
			//BigInteger, Integer, Long, Short, Byte
			if(Integer.class.isAssignableFrom(clazz) 
		       || BigInteger.class.isAssignableFrom(clazz) 
			   || Long.class.isAssignableFrom(clazz) 
			   || Short.class.isAssignableFrom(clazz)
			   || Byte.class.isAssignableFrom(clazz)) {
				return new MeasureInfo(fieldName, "int", null);
			} else {
				return new MeasureInfo(fieldName, "float", null);
			}
			
		} else if( Timestamp.class.isAssignableFrom(clazz) ) {
			return new MeasureInfo(fieldName, "timestamp", "d/m/Y H:i:s");
		} else if( Date.class.isAssignableFrom(clazz) ) {
			return new MeasureInfo(fieldName, "date", "d/m/Y");
		} else {
			return new MeasureInfo(fieldName, "string", null);
		}
	}
	
	
	private JSONArray getJSONMeasuresMetadata() throws JSONException {
		JSONArray array = new JSONArray();
		Iterator<MeasureInfo> it = measures.iterator();
		while (it.hasNext()) {
			MeasureInfo mi = it.next();
			JSONObject jsonMi = new JSONObject();
			jsonMi.put(MEASURE_NAME, mi.getName());
			jsonMi.put(MEASURE_TYPE, mi.getType());
			jsonMi.put(MEASURE_FORMAT, mi.getFormat() != null ? mi.getFormat() : "");
			array.put(jsonMi);
		}
		return array;
	}
	
	public class MeasureInfo {
		
		String name;
		String type;
		String format;
		
		public MeasureInfo(String name, String type, String format) {
			this.name = name;
			this.type = type;
			this.format = format;
		}

		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		public String getFormat() {
			return format;
		}

	}
	
}
