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
	
	public CrossTab(){};
	
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
		IMetaData meta = dataStore.getMetaData();
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


	private class Node implements Cloneable{
		private String value;
		private List<Node> childs;
		private int leafPosition =-1;
		private List<Integer> leafPositionsForCF;//Uset for the CF
		private Node fatherNode; //!= from null only if we need the value
		
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

		public int getLeafPosition() {
			return leafPosition;
		}
		
		public void setLeafPositions(){
			setLeafPositions(0);
		}
		
		private int setLeafPositions(int pos){
			if(childs.size()==0){
				leafPosition = pos;
				pos++;
			}else{
				for(int i=0; i<childs.size(); i++){
					pos = childs.get(i).setLeafPositions(pos);
				}
			}
			return pos;
		}
		
		public Node clone(){
			Node n = new Node(value);
			if(childs.size()>0){
				for (int j = 0; j < childs.size(); j++) {
					n.addChild(childs.get(j).clone());
				}
			}
			return n;
		}

		public List<Integer> getLeafPositionsForCF() {
			return leafPositionsForCF;
		}

		public void setLeafPositionsForCF(List<Integer> leafPositionsForCF) {
			this.leafPositionsForCF = leafPositionsForCF;
		}

		public List<Node> getLevel(int level){
			List<Node> nodes = new ArrayList<CrossTab.Node>();
			if(level==0){
				nodes.add(this);
			}else{
				if(childs.size()==0){
					return null;
				}
				for(int i=0; i<childs.size(); i++){
					nodes.addAll(childs.get(i).getLevel(level-1));
				}
			}
			return nodes;
		}
		
		public List<Node> getLeafs(){
			List<Node> list = new ArrayList<CrossTab.Node>();
			if(childs.size()==0){
				list.add(this);
			}else{
				for(int i=0; i<childs.size(); i++){
					list.addAll(childs.get(i).getLeafs());
				}
			}
			return list;
		}
		
		public void updateFathers(){
			for(int i=0; i<childs.size(); i++ ){
				childs.get(i).fatherNode = this;
				childs.get(i).updateFathers();
			}
		}
		
		public int getSubTreeDepth(){
			if(childs.size()==0){
				return 1;
			}else{
				return 1 + childs.get(0).getSubTreeDepth();
			}
		}
		
		public void removeNodeFromTree(){
			if(fatherNode!=null){
				List<Node> fatherChilds = fatherNode.getChilds();
				for(int i=0;i<fatherChilds.size(); i++){
					if(fatherChilds.get(i)==this){
						fatherChilds.remove(i);
						break;
					}
				}
				if(fatherChilds.size()==0){
					fatherNode.removeNodeFromTree();
				}
			}
		}
		
		
		public int getRightMostLeafPositionCF(){
			if(childs.size()==0){
				return leafPosition;
			}
			return childs.get(childs.size()-1).getRightMostLeafPositionCF();
		}

		public Node buildRoot(){
			Node root = new Node("Root");
			
			Node A = new Node("A");
			Node Aa = new Node("a");
			Node Ab = new Node("b");
			Node Aa2 = new Node("1");
			Node Aa3 = new Node("3");
			Node Ab1 = new Node("1");
			Node Ab2 = new Node("2");
			
			Aa.addChild(Aa3);
			Aa.addChild(Aa2);
			Ab.addChild(Ab2);
			Ab.addChild(Ab1);
			A.addChild(Aa);
			A.addChild(Ab);
			
			Node B = new Node("B");
			Node Ba = new Node("a");
			Node Bb = new Node("b");
			Node Bc = new Node("c");
			Node Ba1 = new Node("1");
			Node Ba3 = new Node("3");
			Node Bb1 = new Node("1");
			Node Bb2 = new Node("2");
			Node Bc2 = new Node("2");
			Node Bc3 = new Node("3");
			
			Bc.addChild(Bc3);
			Bc.addChild(Bc2);
			Ba.addChild(Ba3);
			Ba.addChild(Ba1);
			Bb.addChild(Bb2);
			Bb.addChild(Bb1);
			B.addChild(Ba);
			B.addChild(Bb);
			B.addChild(Bc);
			
			Node C = new Node("C");
			Node Ca = new Node("a");
			Node Cb = new Node("b");
			Node Cc = new Node("c");
			Node Ca1 = new Node("111");
//			Node Ca3 = new Node("31");
			Node Cb1 = new Node("1");
			Node Cb2 = new Node("2");
//			Node Cc2 = new Node("21");
			Node Cc3 = new Node("3");
			
			Cc.addChild(Cc3);
			Ca.addChild(Ca1);
			Cb.addChild(Cb2);
			Cb.addChild(Cb1);
			C.addChild(Ca);
			C.addChild(Cb);
			
			root.addChild(A);
			root.addChild(B);
			root.addChild(C);
			
			return root;
		}
		
		
		public Node buildRoot2(){
			Node root = new Node("X");
			
			Node A = new Node("A");
			Node Aa = new Node("a");
			Node Ab = new Node("b");
			Node Aa1 = new Node("1");
			Node Aa2 = new Node("1");
//			Node Ab1 = new Node("1");
//			Node Ab2 = new Node("2");
			
//			Aa.addChild(Aa3);
//			Aa.addChild(Aa2);
			Ab.addChild(Aa1);
			Aa.addChild(Aa2);
			A.addChild(Aa);
			A.addChild(Ab);
			
			Node B = new Node("B");
			Node Ba = new Node("a");
			Node Bb = new Node("b");
//			Node Bc = new Node("c");
//			Node Ba1 = new Node("1");
//			Node Ba3 = new Node("3");
//			Node Bb1 = new Node("1");
//			Node Bb2 = new Node("2");
//			Node Bc2 = new Node("2");
//			Node Bc3 = new Node("3");
			
//			Bc.addChild(Bc3);
//			Bc.addChild(Bc2);
//			Ba.addChild(Ba3);
//			Ba.addChild(Ba1);
//			Bb.addChild(Bb2);
//			Bb.addChild(Bb1);
//			B.addChild(Ba);
//			B.addChild(Bb);
//			
//			Node C = new Node("B");
//			Node Ca = new Node("a");
//			Node Cb = new Node("b");
//			Node Cc = new Node("c");
//			Node Ca1 = new Node("1");
////			Node Ca3 = new Node("3");
//			Node Cb1 = new Node("1");
//			Node Cb2 = new Node("2");
////			Node Cc2 = new Node("2");
//			Node Cc3 = new Node("3");
			
//			Cc.addChild(Cc3);
//			Ca.addChild(Ca1);
//			Cb.addChild(Cb2);
//			Cb.addChild(Cb1);
//			C.addChild(Ca);
//			C.addChild(Cb);
			
			root.addChild(A);
			root.addChild(B);
//			root.addChild(C);
			
			return A;
		}
		
		public void buildSubTree(int height, int branch){
			if(height<2){
				for(int i=0; i<branch; i++){
					addChild(new Node(""+i));
				}
			}else{
				for(int i=0; i<branch; i++){
					Node n = new Node(value+"_"+i);
					addChild(n);
					n.buildSubTree(height-1, branch);
				}
			}
		}
		
	}
	
	
	private static String[][] buildMatrix(int rows, int columns){
		String[][] m = new String[rows][columns];
		for(int i=0; i<rows; i++){
			for(int j=0; j<columns; j++){
				m[i][j]=""+i;
			}
		}
		return m;
	}
	
	public static void main(String args[]){
		CrossTab cs = new CrossTab();
		Node root = cs.new Node("Root");
		root.buildSubTree(1, 2);
		

//		String[] aa= new String[16];
//		aa[0]="1";
//		aa[1]="2";
//		aa[2]="3";
//		aa[3]="4";
//		
//		aa[4]="50";
//		aa[5]="60";
//		aa[6]="70";
//		aa[7]="80";
//		
//		aa[8]="90";
//		aa[9]="100";
//		aa[10]="1100";
//		aa[11]="1200";
//		
//		aa[12]="1300";
//		aa[13]="1400";
//		aa[14]="1500";
//		aa[15]="1600";
//		
//		
//		String[][] aaa= new String[2][4];
//		aaa[0] = aa;
//		aaa[1] = aa;
		cs.dataMatrix = buildMatrix(2, 16);

		
		cs.calculateCF("field[0]+field[1]+(7*field[1])", root, true, 1);
		
		System.out.println("");
	}
	

	
	private Node mergeNodes(List<Node> nodes, String NodeValue){
		Assert.assertNotNull(nodes, "We need at least a node to merge");
		Assert.assertTrue(nodes.size()>0, "We need at least a node to merge");
		int index;
		List<Node> commonChildNode;
		Node newNode = new Node(NodeValue);
		List<Node> newchilds = new ArrayList<CrossTab.Node>();
		if(nodes.size()>1){
			//get the first node. If a child of the first node
			//is not a child of the other nodes is not in common... 
			Node firstNode = nodes.get(0);
			List<Node> firstNodeChilds = firstNode.getChilds();
			if(firstNodeChilds!=null && firstNodeChilds.size()>0){
				for(int i=0; i<firstNodeChilds.size(); i++){
					commonChildNode = new ArrayList<CrossTab.Node>();
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
						newchilds.add(mergeNodes(commonChildNode, firstNodeChilds.get(i).value));
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
		}else{
			newNode = nodes.get(0).clone(); 
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
	
	private List<Node> cleanTreeAfterMergeRecorsive(Node node, int treeDepth, int level){
		List<Node> listOfNodesToRemove = new ArrayList<CrossTab.Node>();
		if(node.childs.size()==0){
			if(level<treeDepth-1){
				listOfNodesToRemove.add(node);
			}
		}else{
			for(int i=0; i<node.childs.size(); i++){
				listOfNodesToRemove.addAll(cleanTreeAfterMergeRecorsive(node.childs.get(i), treeDepth, level+1));
			}
		}
		return listOfNodesToRemove;
	}
	
	
	
	private void calculateCF(String operation, Node rootNode, boolean horizontal, int level){
		
		List<Node> fathersOfTheNodesOfTheLevel = rootNode.getLevel(level-1);
		
		for(int i=0; i<fathersOfTheNodesOfTheLevel.size(); i++){
			rootNode.setLeafPositions();
			calculateCFSub(operation, fathersOfTheNodesOfTheLevel.get(i), horizontal, level);
		}
	}
	
	
	private void calculateCFSub(String operation, Node node, boolean horizontal, int level){
		List<String[]> calculatedFieldResult = new ArrayList<String[]>();
		List<String> operationParsed;
		List<String> operationExpsNames;
		List<List<String>> parseOperationR = parseOperation(operation);
		operationParsed = parseOperationR.get(0);
		operationExpsNames = parseOperationR.get(1);
		
		List<Node> levelNodes = node.childs;
		
		Object[] expressionMap = buildExpressionMap(levelNodes, operationExpsNames);
		Map<String, Integer> expressionToIndexMap = (Map<String, Integer>) expressionMap[1];
		List<Node> nodeInvolvedInTheOperation = (List<Node>) expressionMap[0];
		
		Node mergedNode = mergeNodes(nodeInvolvedInTheOperation, "CF");
		cleanTreeAfterMerge(mergedNode, level);
		List<Node> mergedNodeLeafs = mergedNode.getLeafs();
		for(int i=0; i<mergedNodeLeafs.size(); i++){
			List<String[]> arraysInvolvedInTheOperation = getArraysInvolvedInTheOperation(horizontal, operationExpsNames, expressionToIndexMap, mergedNodeLeafs.get(i).getLeafPositionsForCF());
			calculatedFieldResult.add(executeOperationOnArrays(arraysInvolvedInTheOperation, operationParsed));
		}
		
		
		//add the header
		int positionToAdd = node.getRightMostLeafPositionCF()+1;
		node.addChild(mergedNode);
		addCrosstabDataLine(positionToAdd, calculatedFieldResult, horizontal);
		
		//return calculatedFieldResult;
	}
	
	
	
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
	 * prende la lista di nodi di un livello e i campi che compaiono nella quey...
	 * Costruisce la lista dei nodi coinvolti nell'operazione e la mappa degli indici operationExpsNames-->indice del nodo corrispondente nella lista prcedente
	 * @param nodes
	 * @param operationExpsNames
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
					if(nodes.get(y).value.equals(operationElement)){
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
	 * @param indexInTheArray è una lista la cui proima posizione è l'indice della colonna/riga nella tabella corrispondente al dato A,....
	 * @return
	 */
	private List<String[]> getArraysInvolvedInTheOperation(boolean horizontal, List<String> operationExpsNames,  Map<String, Integer> expressionToIndexMap, List<Integer> indexInTheArray){
		List<String[]> toReturn = new ArrayList<String[]>();
		for (int y=0; y<operationExpsNames.size(); y++) {
			String alias = operationExpsNames.get(y);
			int index = expressionToIndexMap.get(alias);
			if(horizontal){
				toReturn.add(getCrosstabDataRow(indexInTheArray.get(index)));
			}else{
				toReturn.add(getCrosstabDataColumn(indexInTheArray.get(index)));
			}
		}
		return toReturn;
	}
	
	/**
	 * Dati i parametri costruisce la lista risultante dell'esecuzione dell'operazione 
	 * sulle liste passate. es: [4,6]
	 * @param data lista di colonne/righe della crosstab su cui eseguire l'operazione es [1,2], [3,4]
	 * @param operation l'opearzione parsata es: +
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
	 * Vene creata ed eseguita un operazione. dati i parametri descitti sotto viene composta l'operazione
	 * 1+2-(2*4)
	 * @param data una lista di valori che rappresentano gli elementi dell'operazione.. es: 1,2,3,4
	 * @param op lista di stringhe che rappresentano l'operazione per sempio: +,-(,*,)
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
				String decimalPrecision = (String)fieldMeta.getProperty(IMetaData.DECIMALPRECISION);
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
	
	private String[] getCrosstabDataColumn(int i){
		String[] column = new String[dataMatrix.length];
		for (int j = 0; j < dataMatrix.length; j++) {
			column[j] = dataMatrix[j][i];
		}
		return column;
	}
	
	private String[] getCrosstabDataRow(int i){
		return dataMatrix[i];
	}
	
	public void addCrosstabDataLine(int startposition, List<String[]> line, boolean horizontal){
		if(horizontal){
			addCrosstabDataRow(startposition, line);
		}else{
			addCrosstabDataColumns(startposition, line);
		}
	}
	
	public void addCrosstabDataColumns(int startposition, List<String[]> colums){
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
		dataMatrix = newData;
	}
	
	public void addCrosstabDataRow(int startposition, List<String[]> rows){
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
		
		dataMatrix = newData;
	}
	
}
