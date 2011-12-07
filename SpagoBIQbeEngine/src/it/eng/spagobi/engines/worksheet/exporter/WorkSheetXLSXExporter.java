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
package it.eng.spagobi.engines.worksheet.exporter;

import it.eng.qbe.serializer.SerializationException;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.exporter.QbeXLSXExporter;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Exports the crosstab data (formatted as a JSON object in input) into a XLS
 * file. The JSON object should have this structure (a node is {node_key:"Text",
 * node_childs:[...]}): columns: {...} contains tree node structure of the
 * columns' headers rows: {...} contains tree node structure of the rows'
 * headers data: [[...], [...], ...] 2-dimensional matrix containing crosstab
 * data
 * 
 * @author Chiara Chiarelli
 */
public class WorkSheetXLSXExporter {

	/** Logger component. */
	public static transient Logger logger = Logger
			.getLogger(WorkSheetXLSXExporter.class);

	public static final String CROSSTAB_JSON_DESCENDANTS_NUMBER = "descendants_no";
	public static final String SHEETS_NUM = "SHEETS_NUM";
	public static final String EXPORTED_SHEETS = "EXPORTED_SHEETS";

	public static String OUTPUT_FORMAT_JPEG = "image/jpeg";

	public static final String HEADER = "HEADER";
	public static final String FOOTER = "FOOTER";
	public static final String CONTENT = "CONTENT";

	public static final String SHEET_TYPE = "SHEET_TYPE";
	public static final String CHART = "CHART";
	public static final String CROSSTAB = "CROSSTAB";
	public static final String TABLE = "TABLE";

	public static final String SVG = "SVG";

	public static final String POSITION = "position";
	public static final String TITLE = "title";
	public static final String IMG = "img";
	
	public static final String CENTER = "center";
	public static final String RIGHT = "right";
	public static final String LEFT = "left";

	Map<Integer, String> decimalFormats = new HashMap<Integer, String>();
	
	public JSONObject getOptionalUserFilters(JSONObject paramsJSON) throws JSONException{
		JSONObject optionalUserFiltersJSON = null;
		if(paramsJSON.has(QbeEngineStaticVariables.FILTERS)){
			String optionalUserFilters = paramsJSON.getString(QbeEngineStaticVariables.FILTERS);
			optionalUserFiltersJSON = new JSONObject(optionalUserFilters);	
		}
		return optionalUserFiltersJSON;
	}
	
	public List<String> getJsonVisibleSelectFields(JSONObject paramsJSON) throws JSONException{
		JSONArray jsonVisibleSelectFields = null;
		if(paramsJSON.has(QbeEngineStaticVariables.OPTIONAL_VISIBLE_COLUMNS)){
			String jsonVisibleSelectFieldsS = paramsJSON.getString(QbeEngineStaticVariables.OPTIONAL_VISIBLE_COLUMNS);
			jsonVisibleSelectFields = new JSONArray(jsonVisibleSelectFieldsS);	 
		}
		
		List<String> visibleSelectFields = new ArrayList<String>();
		try {
			if (jsonVisibleSelectFields != null) {
				for (int j = 0; j < jsonVisibleSelectFields.length(); j++) {
					JSONObject jsonVisibleSelectField = jsonVisibleSelectFields.getJSONObject(j);
					visibleSelectFields.add(jsonVisibleSelectField.getString("alias"));
				}	
			}
		} catch (Exception e) {
			logger.debug("The optional attribute visibleselectfields is not valued. No visible select field selected.. All fields will be taken..");
		}
		return visibleSelectFields;
	}
	
	public void designTableInWorksheet(Sheet sheet,Workbook wb, CreationHelper createHelper, 
			  IDataStore dataStore, int startRow) throws SerializationException, JSONException{
		
		QbeXLSXExporter exp = new QbeXLSXExporter(dataStore);
		exp.fillSheet(sheet, wb, createHelper, startRow);
	}

	public int setHeader(XSSFSheet sheet, JSONObject header,
			CreationHelper createHelper, XSSFWorkbook wb, XSSFDrawing patriarch, int sheetRow) throws JSONException, IOException {
		String title = header.getString(TITLE);
		String imgName = header.optString(IMG);
		String imagePosition = header.getString(POSITION);
		CellStyle cellStyle = buildHeaderTitleCellStyle(sheet);
		
		if(title!=null && !title.equals("")){			
			Row row = sheet.createRow(sheetRow);
			sheetRow++;
			Cell cell = row.createCell(6);
			cell.setCellValue(createHelper.createRichTextString(title));
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			cell.setCellStyle(cellStyle);
		}
		
		if(imgName!=null && !imgName.equals("") && !imgName.equals("null")){
			File img = getImage(imgName);
			String imgNameUpperCase = imgName.toUpperCase();
			int impgType = getImageType(imgNameUpperCase);
			
			int c = 7;
			int colend = 9;

			if(imagePosition!=null && !imagePosition.equals("")){
				if(imagePosition.equals(LEFT)){
					c = 1;
					colend = 3;
				}else if(imagePosition.equals(RIGHT)){
					c = 11;
					colend = 13;
				}
			}
			if(impgType!=0){
				for(int i=0; i<4; i++){
					sheet.createRow(sheetRow+i);
				}
				setImageIntoWorkSheet(wb, patriarch, img, c, colend, sheetRow, 4,impgType);

				sheetRow = sheetRow+4;
			}
		}
		
		return sheetRow;
		
	}

	public int setFooter(XSSFSheet sheet, JSONObject footer,
			CreationHelper createHelper, XSSFWorkbook wb, XSSFDrawing patriarch, int sheetRow) throws JSONException, IOException {
		String title = footer.getString(TITLE);
		String imgName = footer.optString(IMG);
		String imagePosition = footer.getString(POSITION);
		CellStyle cellStyle = buildHeaderTitleCellStyle(sheet);
		
		if(title!=null && !title.equals("")){		
			Row row = sheet.createRow(sheetRow);
			sheetRow++;
			Cell cell = row.createCell(6);
			cell.setCellValue(createHelper.createRichTextString(title));
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			cell.setCellStyle(cellStyle);
		}
		
		if(imgName!=null && !imgName.equals("") && !imgName.equals("null")){
			File img = getImage(imgName);
			String imgNameUpperCase = imgName.toUpperCase();
			int impgType = getImageType(imgNameUpperCase);
			

			int c = 7;
			int colend = 9;
			
			if(imagePosition!=null && !imagePosition.equals("")){
				if(imagePosition.equals(LEFT)){
					c = 1;
					colend = 3;			
				}else if(imagePosition.equals(RIGHT)){
					c = 11;
					colend = 13;
				}
			}
			if(impgType!=0){
				setImageIntoWorkSheet(wb, patriarch, img, c, colend, sheetRow, 4,impgType);
				sheetRow = sheetRow+4;
			}
		}
		
		return sheetRow;
	}
	
	public int getImageType(String imgNameUpperCase){
		int impgType = 0;
		if(imgNameUpperCase.contains(".PNG")){
			impgType = XSSFWorkbook.PICTURE_TYPE_PNG;
		}else if(imgNameUpperCase.contains(".JPG") || imgNameUpperCase.contains(".JPEG")){
			impgType = XSSFWorkbook.PICTURE_TYPE_JPEG;
		}else if(imgNameUpperCase.contains(".DIB") || imgNameUpperCase.contains(".BMP")){
			impgType = XSSFWorkbook.PICTURE_TYPE_DIB;
		}else if(imgNameUpperCase.contains(".EMF")){
			impgType = XSSFWorkbook.PICTURE_TYPE_EMF;
		}else if(imgNameUpperCase.contains(".PICT") || imgNameUpperCase.contains(".PCT") || imgNameUpperCase.contains(".PIC")){
			impgType = XSSFWorkbook.PICTURE_TYPE_PICT;
		}else if(imgNameUpperCase.contains(".WMF") || imgNameUpperCase.contains(".WMZ")){
			impgType = XSSFWorkbook.PICTURE_TYPE_WMF;
		}
		return impgType;
	}
	
	public CellStyle buildHeaderTitleCellStyle(Sheet sheet){
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);  
        Font font = sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short)16);
        font.setFontName("Arial");
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);
        return cellStyle;
	}
	
	private File getImage(String fileName) {
		logger.debug("IN");
		File toReturn = null;
		File imagesDir = QbeEngineConfig.getInstance().getWorksheetImagesDir();
		toReturn = new File(imagesDir, fileName);
		logger.debug("OUT");
		return toReturn;
	}

	public void setImageIntoWorkSheet(XSSFWorkbook wb, XSSFDrawing drawing ,
			File f, int col, int colend, int sheetRow, int height,int imgType) throws IOException {
		FileInputStream fis = new FileInputStream(f);

		ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
		int b;
		while ((b = fis.read()) != -1) {
			imgBytes.write(b);
		}	
		int dx1 = 0;
        int dy1 = 0;
        int dx2 = 0;
        int dy2 = 0;
		
		int index = wb.addPicture(imgBytes.toByteArray(),imgType);
		imgBytes.close();
		fis.close();
		
		XSSFClientAnchor anchor = new XSSFClientAnchor(dx1, dy1, dx2, dy2, (short) col,	sheetRow, (short) colend, sheetRow+height);
		Picture pict = drawing.createPicture(anchor, index);
		
		//HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		//patriarch.createPicture(anchor, index);
		//anchor.setAnchorType(0);
	}

	public static File createJPGImage(JSONObject content) {
		File exportFile = null;
		try {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			String svg = content.getString(SVG);
			//Don't change ISO-8859-1 because it's the only way to export specific symbols
			inputStream = new ByteArrayInputStream(svg.getBytes("ISO-8859-1"));
			String ext = ".jpg";
			exportFile = File.createTempFile("chart", ext);
			outputStream = new FileOutputStream(exportFile);
			transformSVGIntoJPEG(inputStream, outputStream);
		} catch (IOException e) {
			logger.error(e);
		} catch (JSONException e) {
			logger.error(e);
		}
		return exportFile;
	}

	public static void transformSVGIntoJPEG(InputStream inputStream,
			OutputStream outputStream) {
		// create a JPEG transcoder
		JPEGTranscoder t = new JPEGTranscoder();

		// set the transcoding hints
		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));
		t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(1000));
		t.addTranscodingHint(JPEGTranscoder.KEY_ALLOWED_SCRIPT_TYPES, "*");
		t.addTranscodingHint(JPEGTranscoder.KEY_CONSTRAIN_SCRIPT_ORIGIN,
				new Boolean(true));
		t.addTranscodingHint(JPEGTranscoder.KEY_EXECUTE_ONLOAD, new Boolean(
				true));

		// create the transcoder input
		Reader reader = new InputStreamReader(inputStream);
		TranscoderInput input = new TranscoderInput(reader);

		// create the transcoder output
		TranscoderOutput output = new TranscoderOutput(outputStream);

		// save the image
		try {
			t.transcode(input, output);
		} catch (TranscoderException e) {
			logger.error("Impossible to convert svg to jpeg: " + e.getCause(),
					e);
			throw new SpagoBIEngineRuntimeException(
					"Impossible to convert svg to jpeg: " + e.getCause(), e);
		}
	}

	private String getNumberFormat(String decimal){
		int j = new Integer(decimal);
		if(decimalFormats.get(j)!=null)
			return decimalFormats.get(j);
		String decimals="";
		for(int i=0; i<j; i++){
			decimals+="0";
		}
		decimalFormats.put(j, decimals);
		return decimals;
	}
	
}
