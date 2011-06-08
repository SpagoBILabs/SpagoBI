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
package it.eng.spagobi.engines.qbe.services.worksheet.exporter;

import it.eng.qbe.serializer.SerializationException;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.query.Exporter;
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
import java.util.List;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
public class WorkSheetXLSExporter {

	/** Logger component. */
	public static transient Logger logger = Logger
			.getLogger(WorkSheetXLSExporter.class);

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

	
	public JSONObject getOptionalUserFilters(JSONObject paramsJSON) throws JSONException{
		JSONObject optionalUserFiltersJSON = null;
		if(paramsJSON.has(QbeEngineStaticVariables.OPTIONAL_FILTERS)){
			String optionalUserFilters = paramsJSON.getString(QbeEngineStaticVariables.OPTIONAL_FILTERS);
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
			  IDataStore dataStore) throws SerializationException, JSONException{
		
		Exporter exp = new Exporter(dataStore);
		exp.fillSheet(sheet, wb, createHelper);
	}

	public void setHeader(HSSFSheet sheet, JSONObject header,
			CreationHelper createHelper, HSSFWorkbook wb) throws JSONException, IOException {
		String title = header.getString(TITLE);
		String imgName = header.getString(IMG);
		String imagePosition = header.getString(POSITION);
		//sheet.createRow(1);
		
		if(title!=null && !title.equals("")){			
			Row row = sheet.getRow(1);
			Cell cell = row.createCell(5);
			cell.setCellValue(createHelper.createRichTextString(title));
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		
		if(imgName!=null && !imgName.equals("") && !imgName.equals("null")){
			File img = getImage(imgName);
			String imgNameUpperCase = imgName.toUpperCase();
			int impgType = HSSFWorkbook.PICTURE_TYPE_PICT;
			if(imgNameUpperCase.contains(".PNG")){
				impgType = HSSFWorkbook.PICTURE_TYPE_PNG;
			}else if(imgNameUpperCase.contains(".JPG") || imgNameUpperCase.contains(".JPEG")){
				impgType = HSSFWorkbook.PICTURE_TYPE_JPEG;
			}
			int r = 1;
			int rowend = 4;
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
			setImageIntoWorkSheet(wb, sheet, img, c, r, colend, rowend,impgType);
		}
		
	}

	public void setFooter(HSSFSheet sheet, JSONObject footer,
			CreationHelper createHelper, HSSFWorkbook wb) throws JSONException, IOException {
		String title = footer.getString(TITLE);
		String imgName = footer.getString(IMG);
		String imagePosition = footer.getString(POSITION);
		sheet.createRow(40);
		
		if(title!=null && !title.equals("")){		
			Row row = sheet.getRow(40);
			Cell cell = row.createCell(5);
			cell.setCellValue(createHelper.createRichTextString(title));
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		
		if(imgName!=null && !imgName.equals("") && !imgName.equals("null")){
			File img = getImage(imgName);
			String imgNameUpperCase = imgName.toUpperCase();
			int impgType = HSSFWorkbook.PICTURE_TYPE_PICT;
			if(imgNameUpperCase.contains(".PNG")){
				impgType = HSSFWorkbook.PICTURE_TYPE_PNG;
			}else if(imgNameUpperCase.contains(".JPG") || imgNameUpperCase.contains(".JPEG")){
				impgType = HSSFWorkbook.PICTURE_TYPE_JPEG;
			}
			int r = 40;
			int rowend = 44;
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
			setImageIntoWorkSheet(wb, sheet, img, c, r, colend, rowend,impgType);
		}
	}
	
	private File getImage(String fileName) {
		logger.debug("IN");
		File toReturn = null;
		File imagesDir = QbeEngineConfig.getInstance().getWorksheetImagesDir();
		toReturn = new File(imagesDir, fileName);
		logger.debug("OUT");
		return toReturn;
	}

	public void setImageIntoWorkSheet(HSSFWorkbook wb, HSSFSheet sheet,
			File f, int col, int row, int colend, int rowend,int imgType) throws IOException {
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
		HSSFClientAnchor anchor = new HSSFClientAnchor(dx1, dy1, dx2, dy2, (short) col,	row, (short) colend, rowend);
		int index = wb.addPicture(imgBytes.toByteArray(),imgType);
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		patriarch.createPicture(anchor, index);
		anchor.setAnchorType(0);
		imgBytes.close();
		fis.close();
	}

	public static File createJPGImage(JSONObject content) {
		File exportFile = null;
		try {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			String svg = content.getString(SVG);
			inputStream = new ByteArrayInputStream(svg.getBytes("UTF-8"));
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

}
