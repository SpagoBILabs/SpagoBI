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
package it.eng.spagobi.engines.qbe.services.worksheet.exporter;

import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;

import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Table;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class DataSourceTablePDFExporter {
	
	private static final Color headerbackgroundColor = new Color(228,236,242);
	private static final Color evenrowsBackgroundColor = new Color(238,238,238);
	private static final Color oddrowsBackgroundColor = new Color(255,255,255);
	private static final Color cellsBorderColor = new Color(208,208,208);
	private static final int tablePadding = 2;
	private IDataStore dataStore;
	private DecimalFormat numberFormat;
	private String userDateFormat;

	public DataSourceTablePDFExporter(IDataStore dataStore, DecimalFormat numberFormat, String userDateFormat){
		this.dataStore=dataStore;
		this.numberFormat=numberFormat;
	}
	
	/**
	 * Create the object table and put it in the 
	 * pdf document
	 * @param pdfDocument the destination document
	 * @throws BadElementException
	 * @throws DocumentException
	 */
	public void export(Document pdfDocument) throws BadElementException, DocumentException{
		Table table = buildTableHeader(dataStore);
		buildTableContent(dataStore,table); 	
	    pdfDocument.add(table);
	} 
	
	/**
	 * Builds the header of the table..
	 * It creates also the object table..
	 * @param dataStore 
	 * @return the table object
	 * @throws BadElementException
	 */
	public Table buildTableHeader(IDataStore dataStore) throws BadElementException{	
		IDataStoreMetaData dataStoreMetaData = dataStore.getMetaData();	
    	int colunum = dataStoreMetaData.getFieldCount();
	    int visibleColumns=0;
	    List<String> columnsName = new ArrayList<String>();
    	
	    //reads the names of the visible table columns
    	for(int j = 0; j < colunum; j++){
    	    String fieldName = dataStoreMetaData.getFieldName(j);
    	    IFieldMetaData fieldMetaData = dataStoreMetaData.getFieldMeta(j);
//    	    String format = (String) fieldMetaData.getProperty("format");
    	    String alias = (String) fieldMetaData.getAlias();
    	    Boolean visible = (Boolean) fieldMetaData.getProperty("visible");

    	    if (visible != null && visible.booleanValue()) { 
            	if(alias!=null && !alias.equals("")){
            		columnsName.add(alias);
            	}else{
            		columnsName.add(fieldName);
            	}	 
            	visibleColumns++;
            }
    	}
    	
    	Table table = new Table(visibleColumns);
    	
    	//For each column builds a cell
		Cell d = table.getDefaultCell();
		d.setBorderColor(cellsBorderColor);
		table.setDefaultCell(d);
    	
		table.setBorderColor(cellsBorderColor);
		table.setPadding(tablePadding);
		if(visibleColumns<4){
			table.setWidth(visibleColumns*25);
		}else{
			table.setWidth(100);
		}
		
    	for(int j = 0; j < visibleColumns; j++){
    		Cell cell = new Cell(columnsName.get(j));
    		cell.setHeader(true);
    		cell.setBackgroundColor(headerbackgroundColor);
    		table.addCell(cell);
    	}
    	table.endHeaders();
    	return table;
	}
	
	/**
	 * Build the content of the table
	 * @param dataStore
	 * @param table the table with the headers
	 * @throws BadElementException
	 */
	public void buildTableContent(IDataStore dataStore, Table table) throws BadElementException{	
	
		boolean oddRows  = true;
		Cell cell;
		
		Iterator it = dataStore.iterator();
    	
    	IDataStoreMetaData d = dataStore.getMetaData();	
		
		while(it.hasNext()){//for each record
			IRecord record =(IRecord)it.next();
			List fields = record.getFields();
			int length = fields.size();
			//build the row
			for(int fieldIndex =0; fieldIndex<length; fieldIndex++){
				IField f = (IField)fields.get(fieldIndex);
				IFieldMetaData fieldMetaData = d.getFieldMeta(fieldIndex);
		    	Boolean visible = (Boolean) fieldMetaData.getProperty("visible");
		    	
		    	if(visible){
		    		if (f == null || f.getValue()== null) {
		    			cell = new Cell("");
		    		}else{
						Class c = d.getFieldType(fieldIndex);
						cell = new Cell(formatPDFCell(c, f));
						if(oddRows){
							cell.setBackgroundColor(oddrowsBackgroundColor);
						}else{
							cell.setBackgroundColor(evenrowsBackgroundColor);
						}
		    		}
		    		table.addCell(cell);
		    	}
				
			}
			oddRows = !oddRows;
		}
	}
	
	/**
	 * Formats the cell
	 * @param c
	 * @param f
	 * @return
	 */
	private String formatPDFCell(Class c, IField f){
		String cellValue ="";
		if( Integer.class.isAssignableFrom(c) || Short.class.isAssignableFrom(c)) {
			Number val = (Number)f.getValue();
			cellValue = numberFormat.format(val);
		}else if( Number.class.isAssignableFrom(c) ) {
		    Number val = (Number)f.getValue();
		    cellValue = numberFormat.format(val);
		}else if( String.class.isAssignableFrom(c)){
			cellValue = (String)f.getValue();
		}else if( Boolean.class.isAssignableFrom(c) ) {
		    Boolean val = (Boolean)f.getValue();
		    cellValue = val.toString();
		}else if(Date.class.isAssignableFrom(c)){	    
		    Date val = (Date)f.getValue();
		    if(userDateFormat==null){
		    	userDateFormat = "MM/dd/yyyy";
		    }
			DateFormat userDataFormat = new SimpleDateFormat(userDateFormat);		
			cellValue = userDataFormat.format(val);		
		}else{
			cellValue = f.getValue().toString();
		}
		return cellValue;
	}
	

	
}
