/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.spagobi.engines.console.exporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spago.dbaccess.sql.DateDecorator;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.decorator.DisplaySizeDecorator;
import it.eng.spagobi.tools.dataset.common.decorator.IDataStoreDecorator;
import it.eng.spagobi.utilities.assertion.Assert;

// TODO: Auto-generated Javadoc
/**
 * The Class BasicTemplateBuilder.
 * 
 * @author Andrea Gioia
 */
public class CopyOfTemplateBuilder {
	

	private IDataStore dataStore;	
	private Map params;
	
	
	public static final String PN_BAND_WIDTH = "bandWidth";	
	public static final String PN_HEADER_HEIGHT = "columnHeaderHeight";	
	public static final String PN_PIXEL_PER_CHAR = "pixelPerChar";
	public static final String PN_PIXEL_PER_ROW = "pixelPerRow";
	public static final String PN_MAXLINE_PER_ROW = "maxLinesPerRow";	
	public static final String PN_HEADER_FONT = "columnHeaderFont";	
	public static final String PN_HEADER_FONT_SIZE = "columnHeaderFontSize";
	public static final String PN_HEADER_FONT_BOLD = "columnHeaderFontBold";
	public static final String PN_HEADER_FONT_ITALIC = "columnHeaderFontItalic";
	public static final String PN_HEADER_FORECOLOR = "columnHeaderForegroundColor";
	public static final String PN_HEADER_BACKCOLOR = "columnHeaderBackgroundColor";
	public static final String PN_ROW_FONT = "rowFont";	
	public static final String PN_ROW_FONT_SIZE = "rowFontSize";
	public static final String PN_DETAIL_EVEN_ROW_FORECOLOR = "evenRowsForegroundColor";
	public static final String PN_DETAIL_EVEN_ROW_BACKCOLOR = "evenRowsBackgroundColor";
	public static final String PN_DETAIL_ODD_ROW_FORECOLOR = "oddRowsForegroundColor";
	public static final String PN_DETAIL_ODD_ROW_BACKCOLOR = "oddRowsBackgroundColor";
	public static final String DEFAULT_BAND_WIDTH = "802";	
	public static final String DEFAULT_HEADER_HEIGHT = "40";
	public static final String DEFAULT_PIXEL_PER_CHAR = "9";
	public static final String DEFAULT_PIXEL_PER_ROW = "16";
	public static final String DEFAULT_MAXLINE_PER_ROW = "4";
	public static final String DEFAULT_HEADER_FONT = "Helvetica-Bold";
	public static final String DEFAULT_HEADER_FONT_SIZE = "12";
	public static final String DEFAULT_HEADER_FONT_BOLD = "true";
	public static final String DEFAULT_HEADER_FONT_ITALIC = "false";
	public static final String DEFAULT_HEADER_FORECOLOR = "FFFFFF";
	public static final String DEFAULT_HEADER_BACKCOLOR = "#E4ECF2";
	public static final String DEFAULT_ROW_FONT = "Times-Roman";	
	public static final String DEFAULT_ROW_FONT_SIZE = "10";
	public static final String DEFAULT_DETAIL_EVEN_ROW_FORECOLOR = "#000000";
	public static final String DEFAULT_DETAIL_EVEN_ROW_BACKCOLOR = "#EEEEEE";
	public static final String DEFAULT_DETAIL_ODD_ROW_FORECOLOR = "#000000";
	public static final String DEFAULT_DETAIL_ODD_ROW_BACKCOLOR = "#FFFFFF";
	public static final String DEFAULT_NUMBER_PATTERN = "#,##0.##";
	
	private static transient Logger logger = Logger.getLogger(CopyOfTemplateBuilder.class);
	
	
	public CopyOfTemplateBuilder(IDataStore dataStore, Map params) {
		this.dataStore = dataStore;
		this.params = params;
	}
	
	public String buildTemplate() {
		String templateStr;
		
		logger.debug("IN");
		
		try {
			templateStr = getTemplateFileContent();
			
			IDataStoreDecorator dataStoreDecorator = new DisplaySizeDecorator();
			dataStoreDecorator.decorate(dataStore);
			
			
			
			if(getParamValue("pagination", "false").equalsIgnoreCase("true")) {
				templateStr = replaceParam(templateStr, "pagination", "isIgnorePagination=\"true\"");
			} else {
				templateStr = replaceParam(templateStr, "pagination", "");
			}
		
			templateStr = replaceParam(templateStr, "fields", getFieldsBlock());
			templateStr = replaceParam(templateStr, "body", getColumnHeaderBlock() + getDetailsBlock());
		} catch (Throwable t) {
			if(t instanceof ExportException) throw (ExportException)t;
			throw new ExportException("An upredictable error occured building template", t);
		} finally {
			logger.debug("OUT");
		}
		
		return templateStr;
	}
		
	
	private String getTemplateFileContent() {
		StringBuffer buffer;
		InputStream is;
		File baseTemplateFile;
		BufferedReader reader;
		String line;
		
		logger.debug("OUT");
		
		try {
			
			buffer = new StringBuffer();
			
			baseTemplateFile = ExporterConfiguration.getInstance().getBaseTemplateFile();
			logger.debug("Base template file path is equal to [" + baseTemplateFile.getAbsolutePath() + "]");
			Assert.assertNotNull(baseTemplateFile, "baseTemplate file must be set. It cannot be null.");
			Assert.assertTrue(baseTemplateFile.exists(), "Base template file [" + baseTemplateFile.getAbsolutePath() + "] does not exist");
			Assert.assertTrue(baseTemplateFile.canRead(), "Base template file [" + baseTemplateFile.getAbsolutePath() + "] is not readable");
			
			
			try {
				is = new FileInputStream( baseTemplateFile );
			} catch (Throwable t) {
				throw new ExportException("Impossible to access base template file [" + baseTemplateFile.getAbsolutePath() + "]", t);
			}
			
			
			reader = new BufferedReader( new InputStreamReader(is) );
			line = null;
			try {
				while( (line = reader.readLine()) != null) {
					buffer.append(line + "\n");
				}
			} catch (Throwable t) {
				throw new ExportException("An error occurred while reading content from base template file [" + baseTemplateFile + "]. Last line succesfully read is [" + line + "]", t);
			}
		} catch (Throwable t) {
			if(t instanceof ExportException) throw (ExportException)t;
			throw new ExportException("An upredictable error occured while reading base template content");
		} finally {
			logger.debug("OUT");
		}
		
		
		return buffer.toString();
	}
	
	
	
	
	
	/**
	 * Gets the fields block.
	 * 
	 * @return the fields block
	 */
	public String getFieldsBlock() {
		StringBuffer buffer = new StringBuffer();
				
		int fieldNo = dataStore.getMetaData().getFieldCount();
		for(int i = 0; i < fieldNo; i++) {
			IFieldMetaData fieldMeta = dataStore.getMetaData().getFieldMeta(i);
			if(fieldMeta.getType().getName().equalsIgnoreCase(DateDecorator.class.getName())) {
				fieldMeta.setType(Date.class);
			} 
			buffer.append("<field name=\"" + fieldMeta.getName() + "\" class=\"" +  fieldMeta.getType().getName() + "\"/>\n");
		}
		
		return buffer.toString();
	}
	
	/**
	 * Gets the details block.
	 * 
	 * @return the details block
	 */
	public String getDetailsBlock() {
		StringBuffer buffer = new StringBuffer();
		
		int totalWidth = Integer.parseInt(getParamValue(PN_BAND_WIDTH, DEFAULT_BAND_WIDTH ));
		int detailHeight = getRowHeight(Integer.parseInt(DEFAULT_HEADER_HEIGHT));
		
		buffer.append("<detail>\n");
		buffer.append("<band " + 
					  "height=\"" + detailHeight + "\"  " + 
					  "isSplitAllowed=\"true\" >\n");
		
		int[] columnWidth = getColumnWidth(totalWidth);	
		int x = 0;
		
		int i=0;
		int fieldNo = dataStore.getMetaData().getFieldCount();
		for(i = 0; i < fieldNo; i++) {
			IFieldMetaData fieldMeta = dataStore.getMetaData().getFieldMeta(i);
			
			
			boolean isANumber = false;
			String className = fieldMeta.getType().getName();
			Class fieldClass = Object.class;
			try {
				fieldClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				logger.error("Class type not recognized: [" + className + "]", e);
			}
			if (Number.class.isAssignableFrom(fieldClass)){
				isANumber = true;
			}
			
			buffer.append("<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" ");
			if (isANumber) {
				String pattern = (String)fieldMeta.getProperty("pattern");
				buffer.append(" pattern=\"" + ((pattern != null) ? pattern : DEFAULT_NUMBER_PATTERN) + "\"");
			}
			buffer.append(" >\n");
			
			buffer.append("<reportElement " + 
						  		"mode=\"" + "Opaque" + "\" " + 
						  		"x=\"" + x + "\" " + 
						  		"y=\"" + 0 + "\" " + 
						  		"width=\"" + columnWidth[i] + "\" " + 
						  		"height=\"" + detailHeight + "\" " + 
						  		"forecolor=\"" + getParamValue(PN_DETAIL_EVEN_ROW_FORECOLOR, DEFAULT_DETAIL_EVEN_ROW_FORECOLOR ) + "\" " + 
						  		"backcolor=\"" + getParamValue(PN_DETAIL_EVEN_ROW_BACKCOLOR, DEFAULT_DETAIL_EVEN_ROW_BACKCOLOR) + "\" " + 
						  		"key=\"textField\">\n");
			
			buffer.append("<printWhenExpression><![CDATA[new Boolean(\\$V\\{REPORT_COUNT\\}.intValue() % 2 == 0)]]></printWhenExpression>");
			buffer.append("</reportElement>");
			buffer.append("<box leftPadding=\"2\" rightPadding=\"2\" topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>\n");

			
			buffer.append("<textElement " +
								"textAlignment=\"" + (isANumber ? "Right": "Left") + "\" " +
								"verticalAlignment=\"Middle\"> " +
								"<font pdfFontName=\"" + getParamValue(PN_ROW_FONT, DEFAULT_ROW_FONT)+ "\" " +
									  "size=\"" + getParamValue(PN_ROW_FONT_SIZE, DEFAULT_ROW_FONT_SIZE)+ "\"/>" +
						  "</textElement>\n");
			
			if(fieldMeta.getType().getName().equalsIgnoreCase("java.sql.Date")) {
				buffer.append("<textFieldExpression   " + 
						  "class=\"java.lang.String\"> " + 
						  "<![CDATA[\\$F\\{" + fieldMeta.getName() + "\\}.toString()]]>\n" +
						  "</textFieldExpression>\n");
			} else {
				buffer.append("<textFieldExpression   " + 
						  "class=\"" + fieldMeta.getType().getName() + "\"> " + 
						  "<![CDATA[\\$F\\{" + fieldMeta.getName() + "\\}]]>\n" +
						  "</textFieldExpression>\n");
			}
		
			
			buffer.append("</textField>\n\n");	
			
			
			
			
			buffer.append("<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" ");
			if (isANumber) {
				String pattern = (String)fieldMeta.getProperty("pattern");
				buffer.append(" pattern=\"" + ((pattern != null) ? pattern : DEFAULT_NUMBER_PATTERN) + "\"");
			}
			buffer.append(" >\n");
			buffer.append("<reportElement " + 
					      		"mode=\"" + "Opaque" + "\" " + 
					      		"x=\"" +  x  + "\" " + 
					      		"y=\"" + 0 + "\" " + 
					      		"width=\"" + columnWidth[i] + "\" " + 
					      		"height=\"" + detailHeight + "\" " + 
					      		"forecolor=\"" + getParamValue(PN_DETAIL_ODD_ROW_FORECOLOR, DEFAULT_DETAIL_ODD_ROW_FORECOLOR ) + "\" " + 
						  		"backcolor=\"" + getParamValue(PN_DETAIL_ODD_ROW_BACKCOLOR, DEFAULT_DETAIL_ODD_ROW_BACKCOLOR) + "\" " + 
						  		"key=\"textField\">\n");
			buffer.append("<printWhenExpression><![CDATA[new Boolean(\\$V\\{REPORT_COUNT\\}.intValue() % 2 != 0)]]></printWhenExpression>");
			buffer.append("</reportElement>");
			buffer.append("<box leftPadding=\"2\" rightPadding=\"2\" topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>\n");

			buffer.append("<textElement " +
								"textAlignment=\"" + (isANumber ? "Right": "Left") + "\" " +
								"verticalAlignment=\"Middle\"> " +
								"<font pdfFontName=\"" + getParamValue(PN_ROW_FONT, DEFAULT_ROW_FONT)+ "\" " +
								  "size=\"" + getParamValue(PN_ROW_FONT_SIZE, DEFAULT_ROW_FONT_SIZE)+ "\"/>" +
			  			  "</textElement>\n");
			
			if(fieldMeta.getType().getName().equalsIgnoreCase("java.sql.Date")) {
				buffer.append("<textFieldExpression   " + 
						  "class=\"java.lang.String\"> " + 
						  "<![CDATA[\\$F\\{" + fieldMeta.getName() + "\\}.toString()]]>\n" +
						  "</textFieldExpression>\n");
			} else {
				buffer.append("<textFieldExpression   " + 
						  "class=\"" + fieldMeta.getType().getName() + "\"> " + 
						  "<![CDATA[\\$F\\{" + fieldMeta.getName() + "\\}]]>\n" +
						  "</textFieldExpression>\n");
			}
			
			buffer.append("</textField>\n\n\n");	
			
			x += columnWidth[i];		
		}
		
		
		buffer.append("</band>");
		buffer.append("</detail>");
		
		
		return buffer.toString();
	}
	
	/**
	 * Gets the column header block.
	 * 
	 * @return the column header block
	 */
	public String getColumnHeaderBlock(){
		StringBuffer buffer = new StringBuffer();		
		
		int totalWidth = Integer.parseInt(getParamValue(PN_BAND_WIDTH, DEFAULT_BAND_WIDTH ));
		
		buffer.append("<columnHeader>\n");
		buffer.append("<band " + 
					  "height=\"" + getParamValue(PN_HEADER_HEIGHT, DEFAULT_HEADER_HEIGHT) + "\"  " + 
					  "isSplitAllowed=\"true\" >\n");
		
		int[] columnWidth = getColumnWidth(totalWidth);		
		int x = 0;
		
		int i=0;
		int fieldNo = dataStore.getMetaData().getFieldCount();
		for(i = 0; i < fieldNo; i++) {
			IFieldMetaData fieldMeta = dataStore.getMetaData().getFieldMeta(i);
			
			buffer.append("<staticText>\n");
			buffer.append("<reportElement " + 
			  		"mode=\"" + "Opaque" + "\" " + 
			  		"x=\"" + x + "\" " + 
			  		"y=\"" + 0 + "\" " + 
			  		"width=\"" + columnWidth[i] + "\" " + 
			  		"height=\"" + getParamValue(PN_HEADER_HEIGHT, DEFAULT_HEADER_HEIGHT ) + "\" " + 
			  		"forecolor=\"" + getParamValue(PN_HEADER_FORECOLOR, DEFAULT_HEADER_FORECOLOR ) + "\" " + 
			  		"backcolor=\"" + getParamValue(PN_HEADER_BACKCOLOR, DEFAULT_HEADER_BACKCOLOR ) + "\" " + 
			  		"key=\"staticText\"/>\n");	

			buffer.append("<box leftPadding=\"2\" rightPadding=\"2\" topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>\n");

			buffer.append("<textElement " +
					"textAlignment=\"" + (fieldMeta.getType().getName().equalsIgnoreCase("java.lang.String")? "Left": "Left") + "\" " +
					"verticalAlignment=\"Middle\"> " +
						"<font pdfFontName=\"" + getParamValue(PN_HEADER_FONT, DEFAULT_HEADER_FONT) + "\" " +
							  "size=\"" + getParamValue(PN_HEADER_FONT_SIZE, DEFAULT_HEADER_FONT_SIZE) + "\" " +
							  "isBold=\""+getParamValue(PN_HEADER_FONT_BOLD, DEFAULT_HEADER_FONT_BOLD)+"\" "+
							  "isItalic=\""+getParamValue(PN_HEADER_FONT_ITALIC, DEFAULT_HEADER_FONT_ITALIC)+"\"/> " +
			  "</textElement>\n");

			String alias = fieldMeta.getAlias();
			alias = (alias == null? fieldMeta.getName(): alias);
			String escapedAlias = escape(alias);
			buffer.append("<text><![CDATA[" + escapedAlias + "]]></text>\n");

			buffer.append("</staticText>\n\n");		

			x += columnWidth[i];	
		}
		
		buffer.append("</band>");
		buffer.append("</columnHeader>");
		
		return buffer.toString();
	}	
	
	
	
	
	public int[] getColumnWidth(int totalWidth) {
		
		int[] columnWidthInPixel;
		int fieldNo;
		int pixelPerChar;
		int freePixels;
		int overflowFieldNum;
		int pixelPerColumn;
		int remainderPixels;
		
		fieldNo = dataStore.getMetaData().getFieldCount();
		columnWidthInPixel = new int[fieldNo];
				
		
		freePixels = 0;
		overflowFieldNum = fieldNo;
		pixelPerColumn = totalWidth/(fieldNo);
		remainderPixels = totalWidth%(fieldNo);
		pixelPerChar = Integer.parseInt(getParamValue(PN_PIXEL_PER_CHAR, DEFAULT_PIXEL_PER_CHAR ));
		
		for(int i = 0; i < fieldNo; i++) {
			IFieldMetaData fieldMeta = dataStore.getMetaData().getFieldMeta(i);
			Integer fieldWidth = (Integer)fieldMeta.getProperty("width");
			int width = (fieldWidth == null)? 80: fieldWidth.intValue();
			int fieldRequiredWidthInPixel = width * pixelPerChar;
			if(fieldRequiredWidthInPixel < pixelPerColumn) {
				columnWidthInPixel[i] = fieldRequiredWidthInPixel;
				freePixels += (pixelPerColumn-fieldRequiredWidthInPixel);
				overflowFieldNum--;
			} else {
				columnWidthInPixel[i] = pixelPerColumn;
			}
			
		}
		
		if(overflowFieldNum > 0 && freePixels > 0) {
			freePixels += remainderPixels;
			pixelPerColumn = freePixels/overflowFieldNum;
			remainderPixels = freePixels%overflowFieldNum;
			for(int i = 0; i < fieldNo; i++) {
				IFieldMetaData fieldMeta = dataStore.getMetaData().getFieldMeta(i);
				Integer fieldWidth = (Integer)fieldMeta.getProperty("width");
				int width = (fieldWidth == null)? 80: fieldWidth.intValue();
				int fieldRequiredWidthInPixel = width * pixelPerChar;
				if(fieldRequiredWidthInPixel > columnWidthInPixel[i]) {
					columnWidthInPixel[i] += pixelPerColumn;
					if(fieldRequiredWidthInPixel > columnWidthInPixel[i]) overflowFieldNum--;
					freePixels -= pixelPerColumn;
				}
			}
			freePixels -= remainderPixels;			
		} 
		columnWidthInPixel[fieldNo-1] += remainderPixels;
				
		return columnWidthInPixel;
	}
	
	
	
	
	
	
	/**
	 * Gets the row height.
	 * 
	 * @param totalWidth the total width
	 * 
	 * @return the row height
	 */
	public int getRowHeight(int totalWidth) {
		int fieldNo = dataStore.getMetaData().getFieldCount();
		int pixelPerChar = Integer.parseInt(getParamValue(PN_PIXEL_PER_CHAR, DEFAULT_PIXEL_PER_CHAR ));
		int pixelPerRow = Integer.parseInt(getParamValue(PN_PIXEL_PER_ROW, DEFAULT_PIXEL_PER_ROW ));
		
		int rowHeight = pixelPerRow;
		int[] columnWidthInPixel = new int[fieldNo];
		
		int freePixels = 0;
		int overflowFieldNum =fieldNo;
		int pixelPerColumn = totalWidth/(fieldNo);
		int remainderPixels = totalWidth%(fieldNo);
		
		for(int i = 0; i < fieldNo; i++) {
			IFieldMetaData fieldMeta = dataStore.getMetaData().getFieldMeta(i);
			Integer fieldWidth = (Integer)fieldMeta.getProperty("width");
			int width = (fieldWidth == null)? 80: fieldWidth.intValue();
			int fieldRequiredWidthInPixel = width * pixelPerChar;
			if(fieldRequiredWidthInPixel < pixelPerColumn) {
				columnWidthInPixel[i] = fieldRequiredWidthInPixel;
				freePixels += (pixelPerColumn-fieldRequiredWidthInPixel);
				overflowFieldNum--;
			} else {
				columnWidthInPixel[i] = pixelPerColumn;
			}
			
		}
		
		int lines = 1;
		if(overflowFieldNum > 0 && freePixels > 0) {
			freePixels += remainderPixels;
			pixelPerColumn = freePixels/overflowFieldNum;
			remainderPixels = freePixels%overflowFieldNum;
			for(int i = 0; i < fieldNo; i++) {
				IFieldMetaData fieldMeta = dataStore.getMetaData().getFieldMeta(i);
				Integer fieldWidth = (Integer)fieldMeta.getProperty("width");
				int width = (fieldWidth == null)? 80: fieldWidth.intValue();
				int fieldRequiredWidthInPixel = width * pixelPerChar;
				if(fieldRequiredWidthInPixel > columnWidthInPixel[i]) {
					columnWidthInPixel[i] += pixelPerColumn;
					if(fieldRequiredWidthInPixel < columnWidthInPixel[i]) overflowFieldNum--;
					else {
						int l = fieldRequiredWidthInPixel/columnWidthInPixel[i];
						if(fieldRequiredWidthInPixel%columnWidthInPixel[i] > 0) l += 1;
						if(l > lines) lines = l;
					}
					freePixels -= pixelPerColumn;
				}
			}
			freePixels -= remainderPixels;			
		} 
		columnWidthInPixel[fieldNo-1] += remainderPixels;
		
		int maxLinesPerRow = Integer.parseInt(getParamValue(PN_MAXLINE_PER_ROW, DEFAULT_MAXLINE_PER_ROW));
		lines = (lines>maxLinesPerRow)? maxLinesPerRow: lines;
		rowHeight = lines * pixelPerRow;
		
		return (rowHeight);
	}
	
	
	

	
	

	private String replaceParam(String template, String pname, String pvalue) {
		int index = -1;
		while( (index = template.indexOf("${" + pname + "}")) != -1) {
			template = template.replaceAll("\\$\\{" + pname + "\\}", pvalue);
		}
		
		return template;
	}
	

	private String escape(String pvalue) {
		pvalue = pvalue.replace("\\", "\\\\");
		pvalue = pvalue.replace("$", "\\$");
		return pvalue;
	}
	
	private String getParamValue(String paramName, String paramDefaultValue) {
		String paramValue = null;
		
		paramValue = (String)params.get(paramName);
		paramValue = (paramValue != null)? paramValue: paramDefaultValue;
		
		return paramValue;
	}
	
	
}
