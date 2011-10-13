package test;


import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.utilities.StringUtils;

import org.apache.log4j.Logger;


public class InsertCommand {


	public static transient Logger logger = Logger.getLogger(InsertCommand.class);

	IMetaData metadata;
	IRecord record;
	String tableName;

	public InsertCommand(IMetaData metadata, String tableName) {
		this.metadata = metadata;
		this.tableName = tableName;
	}

	public void setRecord(IRecord record) {
		this.record = record;
	}

	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String createSQLQuery(){
		StringBuffer buffer = new StringBuffer("INSERT INTO " + this.tableName + " VALUES (");
		
		int count = this.metadata.getFieldCount();
		for (int i = 0 ; i < count ; i++) {
			IFieldMetaData fieldMetadata = this.metadata.getFieldMeta(i);
			Class c = fieldMetadata.getType();
			IField field = this.record.getFieldAt(i);
			String value = field.getValue().toString();
			
			if ( String.class.isAssignableFrom(c) ) {
				value = StringUtils.escapeQuotes(value);
				buffer.append("'" + value + "'");
			} else {
				buffer.append(value);
			}
			
			if (i < count -1) {
				buffer.append(",");
			}

		}
		
		buffer.append(")");
		String query = buffer.toString();
		logger.debug("Query is " + query);
		logger.debug("OUT");
		return query;
	}

}
