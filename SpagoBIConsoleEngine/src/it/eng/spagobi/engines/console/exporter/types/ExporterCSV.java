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

package it.eng.spagobi.engines.console.exporter.types;

import it.eng.spagobi.engines.console.exporter.Field;
import it.eng.spagobi.engines.console.exporter.types.utils.CSVDocument;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class ExporterCSV extends Exporter {

	/** Logger component. */
	public static transient Logger logger = Logger.getLogger(ExporterCSV.class);


	public ExporterCSV() {
		super();
	}


	public ExporterCSV(IDataStore dataStore) {
		super();
		this.dataStore = dataStore;
	}




	public CSVDocument export(){
		logger.debug("IN");
		CSVDocument toreturn = new CSVDocument();

		if(dataStore!=null  && !dataStore.isEmpty()){
			fillHeader(toreturn);
			fillData(toreturn);
		}

		logger.debug("OUT");
		return toreturn;
	}


	public void fillHeader(CSVDocument csvDoc) {	
		logger.debug("IN");
		int column = extractedFieldsMetaData.size();

		for(int j = 0; j < column; j++){
			IFieldMetaData fieldMetaData = extractedFieldsMetaData.get(j);
			String fieldName = fieldMetaData.getName();
			String format = (String) fieldMetaData.getProperty("format");
			String alias = (String) fieldMetaData.getAlias();
			Boolean visible = (Boolean) fieldMetaData.getProperty("visible");

			if (extractedFields != null && extractedFields.get(j) != null) {
				Object f = extractedFields.get(j);
				logger.debug("Extracted field "+fieldName+" is instance of "+f.getClass().getName());
				if(f instanceof Field){
					Field field = (Field) f;
					fieldName = field.getName();
					if (field.getPattern() != null) {
						format = field.getPattern();
					}
				}
			}

			if (visible != null && visible.booleanValue() == true) { 
				if(alias!=null && !alias.equals("")){
					csvDoc.getHeader().add(alias);
				}else{
					csvDoc.getHeader().add(fieldName);
				}	 
			}	   
		}
		logger.debug("OUT");

		return;
	}




	public void fillData(CSVDocument csvDoc) {	
		logger.debug("IN");

		int rownum = 0;

		long rowsNumber = (int)dataStore.getRecordsCount();

		for(int i= rownum; i<rowsNumber ; i++){
			CSVDocument.Row row = csvDoc.new Row();
			IRecord record =(IRecord)dataStore.getRecordAt(i);
			List fields = record.getFields();
			int length = extractedFieldsMetaData.size();

			for(int fieldIndex =0; fieldIndex< length; fieldIndex++){
				IFieldMetaData metaField = extractedFieldsMetaData.get(fieldIndex);
				IField f = (IField)record.getFieldAt((Integer)metaField.getProperty("index"));
				if (f != null && f.getValue()!= null) {

					Boolean visible = (Boolean) metaField.getProperty("visible");
					if(visible){
						String b = f.getValue() != null ? f.getValue().toString() : ""; 
						//byte[] b = getBytes(f.getValue());
						row.getColumns().add(b);
					}
				}
			}
			csvDoc.getRows().add(row);
			rownum ++;
		}
		logger.debug("OUT");

	}




	public void write(CSVDocument csvDoc, FileWriter fw) throws IOException {
		logger.debug("IN");
		// write the header
		for (Iterator iterator = csvDoc.getHeader().iterator(); iterator.hasNext();) {
			String header = (String) iterator.next();
			//			byte[] h = getBytes(header);
			//			fw.write(h);
			fw.write(header);
			if(iterator.hasNext()) fw.write(csvDoc.getSeparator());
		}

		fw.write('\n');

		// write the data for each row
		for (Iterator iterator = csvDoc.getRows().iterator(); iterator.hasNext();) {
			CSVDocument.Row row = (CSVDocument.Row) iterator.next();

			for (Iterator iterator2 = row.getColumns().iterator(); iterator2.hasNext();) {
				String col = (String) iterator2.next();
				fw.write(col);
				if(iterator2.hasNext()) fw.write(csvDoc.getSeparator());
			}
			if(iterator.hasNext()) fw.write('\n');
		}
		logger.debug("OUT");
	}


	//	public static byte[] getBytes(Object obj){
	//		byte [] data = new byte[]{};
	//		if(obj != null){
	//			try{
	//				ByteArrayOutputStream bos = new ByteArrayOutputStream();
	//				ObjectOutputStream oos = new ObjectOutputStream(bos);
	//				oos.writeObject(obj);
	//				oos.flush();
	//				oos.close();
	//				bos.close();
	//				data = bos.toByteArray();
	//			}
	//			catch (Exception e) {
	//				logger.warn("error while parsing to byte "+obj.toString());
	//			}
	//		}
	//		return data;
	//	}


}

