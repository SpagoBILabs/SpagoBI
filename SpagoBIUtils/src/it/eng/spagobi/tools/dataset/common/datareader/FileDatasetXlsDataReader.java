/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.datareader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.Field;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.datastore.Record;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;

/**
 * @author Marco Cortella
 *         marco.cortella@eng.it
 */
public class FileDatasetXlsDataReader extends AbstractDataReader {
	
	

	private static transient Logger logger = Logger.getLogger(FileDatasetXlsDataReader.class);
	
  

	public FileDatasetXlsDataReader() {
		super();
	}

	public IDataStore read( Object data ) {
		DataStore dataStore = null;
		
		InputStream inputDataStream;

		
		logger.debug("IN");
		
		inputDataStream = (InputStream)data;
		
		try {				
			dataStore = readXls(inputDataStream);

				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataStore;
    }
	
 	private DataStore readXls( InputStream inputDataStream ) throws Exception {
 		
    	DataStore dataStore = null;
		MetaData dataStoreMeta;
    	dataStore = new DataStore();
		dataStoreMeta = new MetaData();
		dataStore.setMetaData(dataStoreMeta);
		
		
		//HSSFWorkbook wb = HSSFReadWrite.readFile(fileName);
		HSSFWorkbook wb = new HSSFWorkbook(inputDataStream);

		logger.debug("XSL Data dump:\n");

		for (int k = 0; k < wb.getNumberOfSheets(); k++) {
			HSSFSheet sheet = wb.getSheetAt(k);
			int rows = sheet.getPhysicalNumberOfRows();
			logger.debug("Sheet " + k + " \"" + wb.getSheetName(k) + "\" has " + rows
					+ " row(s).");
			for (int r = 0; r < rows; r++) {
				HSSFRow row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				
				//create new Dataset record
				IRecord record = new Record(dataStore);
				


				int cells = row.getPhysicalNumberOfCells();
				logger.debug("\nROW " + row.getRowNum() + " has " + cells
						+ " cell(s).");
				for (int c = 0; c < cells; c++) {
					HSSFCell cell = row.getCell(c);
					String value = null;
					String valueField = null;

					switch (cell.getCellType()) {

						case HSSFCell.CELL_TYPE_FORMULA:
							value = "FORMULA value=" + cell.getCellFormula();
							valueField = cell.getCellFormula().toString();
							break;

						case HSSFCell.CELL_TYPE_NUMERIC:
							value = "NUMERIC value=" + cell.getNumericCellValue();
							valueField = String.valueOf(cell.getNumericCellValue());
							break;

						case HSSFCell.CELL_TYPE_STRING:
							value = "STRING value=" + cell.getStringCellValue();
							valueField = cell.getStringCellValue();
							break;

						default:
					}
					
					//First row used as header
					if (r == 0){
						FieldMetadata fieldMeta = new FieldMetadata();
	                	fieldMeta.setName(valueField);
	                	fieldMeta.setType(String.class);
	                	dataStoreMeta.addFiedMeta(fieldMeta);
					} else {
                   	 IField field = new Field(valueField);
						record.appendField(field);
					}
					
					logger.debug("CELL col=" + cell.getColumnIndex() + " VALUE="
							+ value);
				}
				if (r != 0){
					dataStore.appendRecord(record);	
				}
			}
		}

		
		
		
	        return dataStore;
	}

}
