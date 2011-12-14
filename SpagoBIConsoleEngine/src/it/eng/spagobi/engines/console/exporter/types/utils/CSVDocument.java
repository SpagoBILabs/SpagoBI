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
