/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.console.exporter.types.utils;


import java.util.Vector;

public class CSVDocument{

	private Vector<String> header;
	private Vector<Row> rows;
	
	Character separator;

	
	
	
	public class Row{
		private Vector<String> columns;
		public Row() {
			columns = new Vector<String>();
		}
		public Vector<String> getColumns() {
			return columns;
		}
		public void setColumns(Vector<String> columns) {
			this.columns = columns;
		}
		
	}

	public CSVDocument() {
		super();
		header = new Vector<String>();
		rows = new Vector<Row>();
		separator = ',';
	}

	public Vector<String> getHeader() {
		return header;
	}

	public void setHeader(Vector<String> header) {
		this.header = header;
	}

	public Vector<Row> getRows() {
		return rows;
	}

	public void setRows(Vector<Row> rows) {
		this.rows = rows;
	}

	public Character getSeparator() {
		return separator;
	}

	public void setSeparator(Character separator) {
		this.separator = separator;
	}






}
