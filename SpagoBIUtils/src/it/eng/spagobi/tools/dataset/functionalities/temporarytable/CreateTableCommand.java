package it.eng.spagobi.tools.dataset.functionalities.temporarytable;


import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.persist.DataSetTableDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/** Utility class to crate dataset table creation command
 * 
 * @author gavardi
 *
 */

public class CreateTableCommand {


	public static transient Logger logger = Logger.getLogger(CreateTableCommand.class);

	/**
	 *  mapping String type to db type
	 */
	Map<String, String> sqlTypeMapping = null;
	{
		sqlTypeMapping = new HashMap<String, String>();
		sqlTypeMapping.put("java.lang.Integer", "INTEGER");
		sqlTypeMapping.put("java.lang.String", "VARCHAR");
		sqlTypeMapping.put("java.lang.String0", "TEXT");
		sqlTypeMapping.put("java.lang.Boolean", "TINYINT(1)");
		sqlTypeMapping.put("java.lang.Float", "FLOAT");
		sqlTypeMapping.put("java.lang.Double", "FLOAT");
		sqlTypeMapping.put("java.util.Date", "DATE");
	}

	Map<String, String> oracleTypeMapping = null;
	{
		oracleTypeMapping = new HashMap<String, String>();
		oracleTypeMapping.put("java.lang.Integer", "NUMBER");
		oracleTypeMapping.put("java.lang.String", "VARCHAR2");
		oracleTypeMapping.put("java.lang.String0", "CLOB");
		oracleTypeMapping.put("java.lang.Boolean", "VARCHAR2(1)");
		oracleTypeMapping.put("java.lang.Float", "NUMBER");
		oracleTypeMapping.put("java.lang.Double", "NUMBER");
		oracleTypeMapping.put("java.util.Date", "DATE");
		oracleTypeMapping.put("java.sql.Date", "DATE");
		oracleTypeMapping.put("java.sql.Timestamp", "TIMESTAMP");
		oracleTypeMapping.put("oracle.sql.TIMESTAMP", "TIMESTAMP");
		oracleTypeMapping.put("java.math.BigDecimal", "NUMBER");
		
		
	}

	// properties
	public static final String SIZE ="size";
	public static final String decimal ="decimal";

	/**
	 *  
	 */
	String tableName;
	List<ColumnMeta> columns;

	/**
	 *  Mapping physical column to rea names
	 */
	//Map<String, String> physicalColMapping;
	DataSetTableDescriptor dsTableDescriptor;

	int counter=0;



	public CreateTableCommand(String tableName) {
		super();
		this.tableName = tableName;
		dsTableDescriptor = new DataSetTableDescriptor();
		dsTableDescriptor.setTableName(tableName);
	}


	public void addColumn(IFieldMetaData fieldMeta) {
		logger.debug("IN");
		if(columns == null) columns = new ArrayList<ColumnMeta>();

		Class fieldClass = fieldMeta.getType();
		String name = fieldMeta.getName();
		String alias = fieldMeta.getAlias();

		Map<String, Object> properties = fieldMeta.getProperties();

		//add column deifnition
		ColumnMeta columnMeta = new ColumnMeta(name, fieldClass, properties);
		columns.add(columnMeta);
		logger.debug("OUT");
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}



	public String createSQLQuery(){
		String query ="CREATE TABLE ";
		query+=tableName+ " (";

		// run al columns
		for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			ColumnMeta columnMeta = (ColumnMeta) iterator.next();

			// assign physical name to column
			String physicalName = "COL_"+counter;
			query+=physicalName;

			// type
			//Integer size = columnMeta.getSize();
			String typeJavaName = columnMeta.getType().getName();
			query += writeType(typeJavaName, columnMeta.getProperties());

			// semicolon separator
			if(iterator.hasNext()){
				query+=", ";
			}

			// ad field description
			dsTableDescriptor.addField(columnMeta.getName(), physicalName, columnMeta.getType());
			counter++;
		}

		//query+=");";
		query+=")";
		
		logger.debug("Query is "+query);
		System.out.println(query);
		logger.debug("OUT");
		return query;
	}

	
	
	
	
	
	
	
	
	
	
	private String writeType(String typeJavaName, Map properties){
		// convert java type in SQL type
		String queryType ="";
		String typeSQL ="";

		// proeprties
		Integer size = null;
		Integer precision = null;
		Integer scale = null;

		if(properties.get("size") != null) 
			size = Integer.valueOf(properties.get("size").toString());
		if(properties.get("precision") != null) 
			precision = Integer.valueOf(properties.get("precision").toString());
		if(properties.get("scale") != null) 
			scale = Integer.valueOf(properties.get("scale").toString());


		// particular case of VARCHAR and CLOB

		if(typeJavaName.equalsIgnoreCase(String.class.getName())){
			// varchar with no size is text
			if((size == null || size == 0) ){
				typeSQL = oracleTypeMapping.get(typeJavaName+"0");
			}
			else {
				typeSQL = oracleTypeMapping.get(typeJavaName);				
			}
		}
		else typeSQL = oracleTypeMapping.get(typeJavaName);


		// write Type
		queryType +=" "+typeSQL+""; 

		if(typeJavaName.equalsIgnoreCase(String.class.getName())){
			if( size != null && size!= 0){
				queryType +="("+size+")";
			}
		}
		else
			if(typeJavaName.equalsIgnoreCase(Integer.class.getName())
					||
					typeJavaName.equalsIgnoreCase(Double.class.getName())
					||
					typeJavaName.equalsIgnoreCase(Float.class.getName())
			){
				if(precision != null && scale != null){
					queryType+="("+precision+","+scale+")";
				}

			}
				queryType+=" ";
				return queryType;
			}


		public DataSetTableDescriptor getDsTableDescriptor() {
			return dsTableDescriptor;
		}






	}
