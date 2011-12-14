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

package it.eng.spagobi.engines.qbe.crosstable;

import groovy.util.Eval;
import it.eng.spagobi.engines.worksheet.bo.Attribute;
import it.eng.spagobi.engines.worksheet.bo.Measure;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.utilities.assertion.Assert;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	public static final String CROSSTAB_JSON_ROWS_HEADERS_DESCRIPTION = "rows_description";
	public static final String CROSSTAB_JSON_COLUMNS_HEADERS = "columns";
	public static final String CROSSTAB_JSON_DATA = "data";
	public static final String CROSSTAB_JSON_CONFIG = "config";
	public static final String CROSSTAB_JSON_MEASURES_METADATA = "measures_metadata";
	public static final String CROSSTAB_JSON_ROWS_HEADER_TITLE = "rowHeadersTitle";
	
	
	public static final String MEASURE_NAME = "name";
	public static final String MEASURE_TYPE = "type";
	public static final String MEASURE_FORMAT = "format";
	public static final String TOTAL = "Total";
	public static final String SUBTOTAL = "SubTotal";

	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy" );
	private static final SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" );

	private Node columnsRoot;
	private Node rowsRoot;
	String[][] dataMatrix;
	private JSONObject config;
	private List<MeasureInfo> measures;
	private  CrosstabDefinition crosstabDefinition;
	private  List<String> rowHeadersTitles;
	//private boolean measuresOnRow;
	
	public enum CellType {DATA, CF, SUBTOTAL, TOTAL }
	
	private List<CellType> celltypeOfColumns;
	private List<CellType> celltypeOfRows;
	
	public CrossTab(){}
	
	/**
	 * Builds the crossTab (headers structure and data)
	 * @param dataStore: the source of the data
	 * @param crosstabDefinition: the definition of the crossTab
	 * @param calculateFields: array of JSONObjects the CF
	 */
	public CrossTab(IDataStore dataStore, CrosstabDefinition crosstabDefinition, JSONArray calculateFields) throws JSONException{
		this(dataStore, crosstabDefinition);
		if(calculateFields!=null){
			for(int i=0; i<calculateFields.length(); i++){
				
				JSONObject cf = calculateFields.getJSONObject(i);
				boolean horizontal =  cf.getBoolean("horizontal");
				calculateCF(cf.getString("operation"), horizontal, cf.getInt("level"), cf.getString("name"), CellType.CF);
			}
		}
		addTotals();
		addSubtotals();
	}
	
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
		this.crosstabDefinition = crosstabDefinition;
		int cellLimit = crosstabDefinition.getCellLimit();
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

		for(index = 0; index<dataStore.getRecordsCount() && (cellLimit<=0 || index<cellLimit); index++){
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
		IMetaData meta = dataStore.getMetaData();
		for(int i = meta.getFieldCount() - measuresCount; i < meta.getFieldCount(); i++){
			// the field number i contains the measure number (i - <number of dimensions>)
			// but <number of dimension> is <total fields count> - <total measures count>
			IFieldMetaData fieldMeta = meta.getFieldMeta(i);
			Measure relevantMeasure = crosstabDefinition.getMeasures().get( i - (meta.getFieldCount() - measuresCount));
			measures.add(getMeasureInfo(fieldMeta, relevantMeasure));
		}
		
		
		celltypeOfColumns = new ArrayList<CrossTab.CellType>();
		celltypeOfRows = new ArrayList<CrossTab.CellType>();
		
		for(int i=0; i< dataMatrix.length; i++){
			celltypeOfRows.add(CellType.DATA);
		}
		
		for(int i=0; i< dataMatrix[0].length; i++){
			celltypeOfColumns.add(CellType.DATA);
		}
		
	}
	
	private <T extends Attribute> void addHeaderTitles(List<T> lines, int linesIndex, Node node){
		if(linesIndex<lines.size()){
			Node descriptionNode = new Node(lines.get(linesIndex).getAlias());
			linesIndex++;
			List<Node> children = node.getChilds();
			List<Node> newchildren = new ArrayList<Node>();
			newchildren.add(descriptionNode);
			for (int i = 0; i < children.size(); i++) {
				descriptionNode.addChild(node.getChilds().get(i));
				addHeaderTitles(lines,linesIndex , node.getChilds().get(i));
			}
			node.setChilds(newchildren);
		}	
	}
	
	private <T extends Attribute> JSONArray getHeaderDescriptions(List<T> lines){
		JSONArray descriptions = new JSONArray();
		for (int i = 0; i < lines.size(); i++) {
			descriptions.put(lines.get(i).getAlias());
		}
		return descriptions;
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
		
		JSONArray descriptions = getHeaderDescriptions( crosstabDefinition.getRows());
		crossTabDefinition.put(CROSSTAB_JSON_ROWS_HEADERS_DESCRIPTION, descriptions);
		
		//add the headers in the columns
		List<CrosstabDefinition.Column> columns =  crosstabDefinition.getColumns();
		Node columnsRootWithHeaders = columnsRoot.clone();
		addHeaderTitles(columns, 0, columnsRootWithHeaders);

		crossTabDefinition.put(CROSSTAB_JSON_COLUMNS_HEADERS, columnsRootWithHeaders.toJSONObject());
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
			if (field.getValue() != null) {
				node = new Node(field.getValue().toString());
			} else {
				node = new Node("null");
			}
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
			toReturn.add(prefix+(String)(n.getValue()));
			return toReturn;
		}else{
			for(int i=0; i<n.getChilds().size(); i++){
				toReturn.addAll(visit(n.getChilds().get(i), prefix+(String)(n.getValue())));
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
			for(int i=0; i<measuresNodes.size(); i++){
				Node n = measuresNodes.get(i).clone();
				node.addChild(n);
			}
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
				String decimalPrecision = (String)fieldMeta.getProperty(IFieldMetaData.DECIMALPRECISION);
				if(decimalPrecision!=null){
					return new MeasureInfo(fieldName, "float", "{decimalPrecision:"+decimalPrecision+"}");
				}else{
					return new MeasureInfo(fieldName, "float", null);
				}
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
	


	/*************************************************
	 *               CALCULATED FIELDS
	 ************************************************/

	/**
	 * Get a list of nodes and merge them..
	 * It builds a tree with all the node in common with the subtree in with
	 * radix in input..
	 * For example nodes=[A,[1,2,3], A,[1,6]] the result is [A,[1]]
	 * In the leafs (in this case 1) it add the position of that
	 * header in the matrix..
	 * In this case, suppose the id of the first occurence of A.1 is at row 3 and the second in row 7
	 * the leafs with id A.1 has this list [3,7]
	 * @param nodes
	 * @param NodeValue
	 * @return
	 */
	private Node mergeNodes(List<Node> nodes, String NodeValue){
		Assert.assertNotNull(nodes, "We need at least a node to merge");
		Assert.assertTrue(nodes.size()>0, "We need at least a node to merge");
		int index;
		List<Node> commonChildNode;
		Node newNode = new Node(NodeValue);
		List<Node> newchilds = new ArrayList<Node>();
		if(nodes.size()>0){
			//get the first node. If a child of the first node
			//is not a child of the other nodes is not in common... 
			Node firstNode = nodes.get(0);
			List<Node> firstNodeChilds = firstNode.getChilds();
			if(firstNodeChilds!=null && firstNodeChilds.size()>0){
				for(int i=0; i<firstNodeChilds.size(); i++){
					commonChildNode = new ArrayList<Node>();
					commonChildNode.add(firstNodeChilds.get(i));
					//look for the child in all other nodes
					for(int j=1; j<nodes.size(); j++){
						index = nodes.get(j).getChilds().indexOf(firstNodeChilds.get(i));
						if(index>=0){
							commonChildNode.add(nodes.get(j).getChilds().get(index));
						}else{
							commonChildNode = null;
							break;
						}
					}
					if(commonChildNode!=null){
						newchilds.add(mergeNodes(commonChildNode, firstNodeChilds.get(i).getValue()));
					}
				}
			}else{
				//we are the leafs.. so we want the id of the node
				List<Integer> leafPositions= new ArrayList<Integer>();
				for(int j=0; j<nodes.size(); j++){
					leafPositions.add(nodes.get(j).getLeafPosition());
				}
				newNode.setLeafPositionsForCF(leafPositions);
			}
		}
		newNode.setChilds(newchilds);
		return newNode;
	}
	
	/**
	 * Remove the leafs not in the last level of the tree
	 * @param node
	 * @param level
	 */
	private void cleanTreeAfterMerge(Node node, int level){
		int treeDepth = node.getSubTreeDepth();
		List<Node> listOfNodesToRemove = cleanTreeAfterMergeRecorsive(node, treeDepth, level);
		for (Iterator iterator = listOfNodesToRemove.iterator(); iterator.hasNext();) {
			Node node2 = (Node) iterator.next();
			node2.removeNodeFromTree();
			
		}
	}
	
	/**
	 * Remove the dead nodes (the inner nodes with no leafs)
	 * @param node
	 * @param treeDepth
	 * @param level
	 * @return
	 */
	private List<Node> cleanTreeAfterMergeRecorsive(Node node, int treeDepth, int level){
		List<Node> listOfNodesToRemove = new ArrayList<Node>();
		if(node.getChilds().size()==0){
			if(level<treeDepth-1){
				listOfNodesToRemove.add(node);
			}
		}else{
			for(int i=0; i<node.getChilds().size(); i++){
				listOfNodesToRemove.addAll(cleanTreeAfterMergeRecorsive(node.getChilds().get(i), treeDepth, level+1));
			}
		}
		return listOfNodesToRemove;
	}
	
	
	/**
	 * Calculate the calculated fields and add the result in the structure
	 * @param operation 
	 * @param horizontal
	 * @param level
	 * @param cfName
	 * @param celltype
	 */
	private void calculateCF(String operation,  boolean horizontal, int level, String cfName, CellType celltype){
		Node rootNode;
		if(horizontal){
			rootNode = columnsRoot;
		}else{
			rootNode = rowsRoot;
		}
		
		List<Node> fathersOfTheNodesOfTheLevel = rootNode.getLevel(level-1);
		
		for(int i=0; i<fathersOfTheNodesOfTheLevel.size(); i++){
			rootNode.setLeafPositions();
			calculateCFSub(operation, fathersOfTheNodesOfTheLevel.get(i), horizontal, level, cfName, celltype);
		}
	}
	
	/**
	 * Calculate the calculated fields and add the result in the structure
	 * @param operation
	 * @param node the result of the calculated fields will add as child of this node
	 * @param horizontal
	 * @param level
	 * @param cfName
	 * @param celltype
	 */
	private void calculateCF(String operation, Node node, boolean horizontal, int level, String cfName, CellType celltype){
		Node rootNode;
		if(horizontal){
			rootNode = columnsRoot;
		}else{
			rootNode = rowsRoot;
		}
		
		List<Node> fathersOfTheNodesOfTheLevel = new ArrayList<Node>();
		fathersOfTheNodesOfTheLevel.add(node);
		
		for(int i=0; i<fathersOfTheNodesOfTheLevel.size(); i++){
			rootNode.setLeafPositions();
			calculateCFSub(operation, fathersOfTheNodesOfTheLevel.get(i), horizontal, level, cfName, celltype);
		}
	}
	
	/**
	 * 
	 * @param operation
	 * @param node the parent node of the CF
	 * @param horizontal
	 * @param level
	 * @param cfName
	 * @param celltype
	 */
	private void calculateCFSub(String operation, Node node, boolean horizontal, int level, String cfName,  CellType celltype){
		List<String[]> calculatedFieldResult = new ArrayList<String[]>();
		List<String> operationParsed;
		List<String> operationExpsNames;
		List<List<String>> parseOperationR = parseOperation(operation);
		operationParsed = parseOperationR.get(0);
		operationExpsNames = parseOperationR.get(1);
		
		List<Node> levelNodes = node.getChilds();
		
		Object[] expressionMap = buildExpressionMap(levelNodes, operationExpsNames);
		Map<String, Integer> expressionToIndexMap = (Map<String, Integer>) expressionMap[1];
		List<Node> nodeInvolvedInTheOperation = (List<Node>) expressionMap[0];
		if(nodeInvolvedInTheOperation.size()>0){
			Node mergedNode = mergeNodes(nodeInvolvedInTheOperation, cfName);
			cleanTreeAfterMerge(mergedNode, level);
			List<Node> mergedNodeLeafs = mergedNode.getLeafs();
			for(int i=0; i<mergedNodeLeafs.size(); i++){
				List<String[]> arraysInvolvedInTheOperation = getArraysInvolvedInTheOperation(horizontal, operationExpsNames, expressionToIndexMap, mergedNodeLeafs.get(i).getLeafPositionsForCF());
				calculatedFieldResult.add(executeOperationOnArrays(arraysInvolvedInTheOperation, operationParsed));
			}

			//add the header
			int positionToAdd = node.getRightMostLeafPositionCF()+1;
			node.addChild(mergedNode);
			addCrosstabDataLine(positionToAdd, calculatedFieldResult, horizontal, celltype);
		}
	}
	
	/**
	 * Parse the operation
	 * @param operation
	 * @return
	 */
	private static List<List<String>> parseOperation(String operation){
		String freshOp = " "+operation;
		List<String> operationParsed = new ArrayList<String>();
		List<String> operationExpsNames = new ArrayList<String>();
		int index =0;
    	//parse the operation
    	while(freshOp.indexOf("field[")>=0){
    		index =  freshOp.indexOf("field[")+6;
    		operationParsed.add(freshOp.substring(0,index-6));
    		freshOp = freshOp.substring(index);
    		index = freshOp.indexOf("]");
    		operationExpsNames.add(freshOp.substring(0, index));
    		freshOp = freshOp.substring(index+1);
    	}
    	operationParsed.add(freshOp);
    	List<List<String>> toReturn=  new ArrayList<List<String>>();
    	toReturn.add(operationParsed);
    	toReturn.add(operationExpsNames);
    	return toReturn;
	}
	
	/**
	 * Build the list of the nodes involved in the operation and the map of the indexes
	 * es: 
	 * 	nodes: [A,[1,2]], [B[1,3]] , [C[1,3]]
	 *  operationExpsNames: [A, C]
	 *  nodeInvolvedInTheOperation: [A,[1,2]], [B[1,3]]
	 *  expressionToIndexMap: 0,2
	 * @param nodes the list of the nodes of the level involved in the cf
	 * @param operationExpsNames the alias of the fields in the query
	 * @return
	 */
	private Object[] buildExpressionMap(List<Node> nodes, List<String> operationExpsNames){
		Map<String, Integer> expressionToIndexMap = new HashMap<String, Integer>();
		List<Node> nodeInvolvedInTheOperation = new ArrayList<Node>();
		int foundNode=0;
		for (Iterator<String> iterator = operationExpsNames.iterator(); iterator.hasNext();) {
			String operationElement = iterator.next();
			if(!expressionToIndexMap.containsKey(operationElement)){
				expressionToIndexMap.put(operationElement, foundNode);
				for(int y=0; y<nodes.size();y++){
					if(nodes.get(y).getValue().equals(operationElement)){
						nodeInvolvedInTheOperation.add(nodes.get(y));
						foundNode++;
						break;
					}
				}
			}
		}
		Object[] toReturn= new Object[2]; 
		toReturn[0]=nodeInvolvedInTheOperation;
		toReturn[1]=expressionToIndexMap;

		return toReturn;
	}
	
	/**
	 * 
	 * @param horizontal
	 * @param operationExpsNames the names of the operation : A+ D+C-(A*C) = A,D,C,A,C
	 * @param expressionToIndexMap if the operation is the same of before and the Nodes of the level are A,B,C,D the map is (A->0, B->1...)
	 * @param indexInTheArray
	 * @return
	 */
	private List<String[]> getArraysInvolvedInTheOperation(boolean horizontal, List<String> operationExpsNames,  Map<String, Integer> expressionToIndexMap, List<Integer> indexInTheArray){
		List<String[]> toReturn = new ArrayList<String[]>();
		for (int y=0; y<operationExpsNames.size(); y++) {
			String alias = operationExpsNames.get(y);
			int index = expressionToIndexMap.get(alias);
			if(horizontal){
				toReturn.add(getCrosstabDataColumn(indexInTheArray.get(index)));
			}else{
				toReturn.add(getCrosstabDataRow(indexInTheArray.get(index)));
			}
		}
		return toReturn;
	}
	
	/**
	 * Build and execute the operation.. For example x+y = [4,6]
	 * @param data list of rows/columns members of the operation l'operazione es [1,2], [3,4]
	 * @param operation the operation es: [+]
	 * @return
	 */
	private String[] executeOperationOnArrays(List<String[]> data, List<String> operation){
		List<String> operationElements;
		int datalength = data.get(0).length;
		String[] operationResult = new String[datalength];
		for(int i =0; i<datalength; i++){
			operationElements = new ArrayList<String>();
			for(int j =0; j<data.size(); j++){
				operationElements.add(data.get(j)[i]);
			}	
			operationResult[i] = executeOperationOnNumbers(operationElements, operation);
		}
		return operationResult;
	}
	
	/**
	 * Execute the operation
	 * 1+2-(2*4)
	 * @param data the members of the operation.. es: 1,2,3,4
	 * @param op the list of operator: +,-(,*,)
	 * @return
	 */
	private String executeOperationOnNumbers(List<String> data, List<String> op){
    	String operation ="";
    	int i=0;
    	for(i=0; i<op.size()-1; i++){
    		operation = operation+op.get(i);
    		operation = operation+data.get(i);
    		if(data.get(i)=="NA" || data.get(i)=="null"  || data.get(i)==null){
    			return "NA";
    		}
    	}
    	operation = operation + op.get(i);
    	String evalued = (Eval.me(operation)).toString();
    	return evalued;
	}

	
	/************************************************************
	  							TOTALS
	 ***********************************************************  */
	
	
	/**
	 * Sum the values of the rows (the right pannel)
	 * @param measuresOnRow
	 */
	private List<String[]> getTotalsOnRows(boolean measuresOnRow){
		List<String[]> sum = new ArrayList<String[]>();
		double[] st;
		int measures = 1;
		if(!measuresOnRow){
			measures= this.measures.size();
		}
		int iteration = dataMatrix[0].length/measures;
		for(int measureId=0; measureId<measures; measureId++){
			st = new double[dataMatrix.length];
			for(int i=0; i<dataMatrix.length; i++){
				for(int j=0; j<iteration; j++){
					st[i] = st[i] + new Double(dataMatrix[i][j*measures+measureId]);
				}
			}
			sum.add(toStringArray(st));
		}
		return sum;
	}
	
	private String[] toStringArray(double[] doubleArray){
		String[] strings = new String[doubleArray.length];
		for(int i=0; i<doubleArray.length; i++){
			strings[i] = ""+(doubleArray[i]);
		}
		return strings;
	}
	
	/**
	 * Sum the values of the columns (the bottom pannel)
	 * @param measuresOnRow
	 * @return
	 */
	private List<String[]> getTotalsOnColumns(boolean measuresOnRow){
		List<String[]> sum = new ArrayList<String[]>();
		double[] st;
		int measures = 1;
		if(measuresOnRow){
			measures= this.measures.size();
		}
		int iteration = dataMatrix.length/measures;
		for(int measureId=0; measureId<measures; measureId++){
			st = new double[dataMatrix[0].length];
			for(int i=0; i<iteration; i++){
				for(int j=0; j<dataMatrix[0].length; j++){
					st[j] = st[j] + new Double(dataMatrix[i*measures+measureId][j]);
				}
			}
			sum.add(toStringArray(st));
		}
		return sum;
	}
	
	/**
	 * 
	 * @param withMeasures
	 * @param deepth = tree depth-1
	 * @return
	 */
	private Node getHeaderTotalSubTree(boolean withMeasures, int deepth){
		Node node= new Node(TOTAL);
		if(withMeasures && deepth==2){
			for(int i=0; i<measures.size(); i++){
				node.addChild(new Node(measures.get(i).getName()));
			}
		}else{
			if(deepth>1){
				node.addChild(getHeaderTotalSubTree(withMeasures, deepth-1));
			}
		}
		return node;
	}
	
	
	private void addTotals() throws JSONException{
		
		String rowsTotals = config.optString("calculatetotalsoncolumns");
		String columnsTotals = config.optString("calculatetotalsonrows");
		boolean measuresOnRow = config.getString("measureson").equals("rows");
		
		if(rowsTotals!=null && rowsTotals.equals("on")){
			rowsRoot.addChild(getHeaderTotalSubTree(measuresOnRow, rowsRoot.getSubTreeDepth()-1));
			addCrosstabDataRow(dataMatrix.length, getTotalsOnColumns(measuresOnRow), CellType.TOTAL);
		}
		
		if(columnsTotals!=null && columnsTotals.equals("on")){
			columnsRoot.addChild(getHeaderTotalSubTree(!measuresOnRow, columnsRoot.getSubTreeDepth()-1));
			addCrosstabDataColumns(dataMatrix[0].length, getTotalsOnRows(measuresOnRow), CellType.TOTAL);
		}
	}

	
	/************************************************************************
	 *								 SUBTOTALS
	********************************************************************** */
	public void addSubtotals(){
		String rowsTotals = config.optString("calculatesubtotalsonrows");
		String columnsTotals = config.optString("calculatesubtotalsoncolumns");
		boolean measuresOnRow = config.optString("measureson").equals("rows");
		if(rowsTotals!=null && rowsTotals.equals("on")){
			List<Node> childOfRoot = columnsRoot.getChilds();
			for(int i=0; i<childOfRoot.size(); i++){
				addSubtotalsToTheNode(childOfRoot.get(i), true, 2, measuresOnRow);
			}
		}
		
		if(columnsTotals!=null && columnsTotals.equals("on")){
			List<Node> childOfRoot = rowsRoot.getChilds();
			for(int i=0; i<childOfRoot.size(); i++){
				addSubtotalsToTheNode(childOfRoot.get(i), false, 2, measuresOnRow);
			}
		}
	}

	/**
	 * Prepare and execute a CF for the subtotals
	 * @param n
	 * @param horizontal
	 * @param level
	 * @param measuresOnRow
	 */
	public void addSubtotalsToTheNode(Node n, boolean horizontal, int level, boolean measuresOnRow){
		List<Node> childs = n.getChilds();
		if(measuresOnRow){
			if(!horizontal && level<2){
				return;
			}
		}else{
			if(horizontal && level<2){
				return;
			}
		}
		if(childs.size()>0 && n.getValue()!=TOTAL && n.getValue()!=SUBTOTAL ){
			//build the calcuated field for the sum
			StringBuilder sb = new StringBuilder(" ");
			for(int i=0; i<childs.size(); i++){
				sb.append("field[");
				sb.append(childs.get(i).getValue());
				sb.append("] +");
			}
			sb.delete(sb.length()-1, sb.length());		
			calculateCF(sb.toString(), n, horizontal, level, SUBTOTAL, CellType.SUBTOTAL);
			for(int i=0; i<childs.size(); i++){
				addSubtotalsToTheNode(childs.get(i), horizontal, level-1, measuresOnRow);
			}
		}
	}


	
	/********************************************************
	                 UTILITY METHODS
	************************************************************/
	
	/**
	 * Returns a column of the data matrix
	 * @param i the id of the column to get
	 * @return the i-th column of the data matrix
	 */
	private String[] getCrosstabDataColumn(int i){
		String[] column = new String[dataMatrix.length];
		for (int j = 0; j < dataMatrix.length; j++) {
			column[j] = dataMatrix[j][i];
		}
		return column;
	}
	
	/**
	 * Returns a row of the data matrix
	 * @param i the id of the row to get
	 * @return the i-th row of the data matrix
	 */
	private String[] getCrosstabDataRow(int i){
		return dataMatrix[i];
	}
	
	/**
	 * Inserts lines in the crosstab data matrix
	 * @param startposition the position where insert the rows/columns into the matrix
	 * @param line the lines to insert
	 * @param horizontal true to insert columns/false to insert rows
	 * @param type the type of the data
	 */
	public void addCrosstabDataLine(int startposition, List<String[]> line, boolean horizontal, CellType type){
		if(horizontal){
			addCrosstabDataColumns(startposition, line, type);
		}else{
			addCrosstabDataRow(startposition, line, type);
		}
	}
	
	/**
	 * Inserts columns in the crosstab data matrix
	 * @param startposition the position where insert the columns into the matrix
	 * @param colums the lines to insert
	 * @param type the type of the data
	 */
	public void addCrosstabDataColumns(int startposition, List<String[]> colums, CellType type){
		Assert.assertNotNull(dataMatrix, "The data matrix must not be null");
		Assert.assertTrue(startposition<=dataMatrix[0].length, "The position you want to add the columns is bigger than the table size ts="+dataMatrix[0].length+" position= "+startposition);
		String[][] newData = new String[dataMatrix.length][dataMatrix[0].length+colums.size()];
		int columnsToAddSize = colums.size();
		for (int i = 0; i < dataMatrix.length; i++) {
			for(int x=0; x<startposition; x++){
				newData[i][x] =dataMatrix[i][x]; 
			}
			
			for(int x=0; x<columnsToAddSize; x++){
				newData[i][startposition+x] =colums.get(x)[i]; 
			}
			
			for(int x=0; x<dataMatrix[0].length-startposition; x++){
				newData[i][startposition+columnsToAddSize+x] =dataMatrix[i][startposition+x]; 
			}
		}
		//update the list of columns type
		for(int i=0; i< colums.size(); i++){
			celltypeOfColumns.add(i+startposition, type);
		}
		dataMatrix = newData;
	}
	
	/**
	 * Inserts rows in the crosstab data matrix
	 * @param startposition the position where insert the rows into the matrix
	 * @param colums the lines to insert
	 * @param type the type of the data
	 */
	public void addCrosstabDataRow(int startposition, List<String[]> rows, CellType type){
		Assert.assertNotNull(dataMatrix, "The data matrix must not be null");
		Assert.assertTrue(startposition<=dataMatrix.length, "The position you want to add the rows is bigger than the table size ts="+dataMatrix[0].length+" position= "+startposition);
		
		String[][] newData = new String[dataMatrix.length+rows.size()][];
		int rowsToAddSize = rows.size();
		
		for(int x=0; x<startposition; x++){
			newData[x] =dataMatrix[x]; 
		}
			
		for(int x=0; x<rowsToAddSize; x++){
			newData[startposition+x] =rows.get(x); 
		}
		
		for(int x=0; x<dataMatrix.length-startposition; x++){
			newData[startposition+rowsToAddSize+x] =dataMatrix[startposition+x]; 
		}
		//update the list of rows type
		for(int i=0; i< rows.size(); i++){
			celltypeOfRows.add(i+startposition, type);
		}
		
		dataMatrix = newData;
	}

	/**
	 * Get the CellType of the cell
	 * @param row the row
	 * @param column the column
	 * @return the celltype of the cell
	 */
	public CellType getCellType(int row, int column){
		CellType cellCellType;
		CellType rowCellType = celltypeOfRows.get(row);
		CellType columnCellType = celltypeOfColumns.get(column);
		cellCellType = rowCellType;
		if(columnCellType.compareTo(rowCellType)>0){
			cellCellType =  columnCellType;
		}
		return cellCellType;
	}
	
	public Node getColumnsRoot() {
		return columnsRoot;
	}

	public Node getRowsRoot() {
		return rowsRoot;
	}

	public String[][] getDataMatrix() {
		return dataMatrix;
	}

	public List<String> getRowHeadersTitles() {
		if(rowHeadersTitles==null){
			rowHeadersTitles = new ArrayList<String>();
			List<CrosstabDefinition.Row> rows =  crosstabDefinition.getRows();
			for(int i=0; i<rows.size(); i++){
				rowHeadersTitles.add(rows.get(i).getAlias());
			}
		}
		return rowHeadersTitles;
	}
	
	
}
