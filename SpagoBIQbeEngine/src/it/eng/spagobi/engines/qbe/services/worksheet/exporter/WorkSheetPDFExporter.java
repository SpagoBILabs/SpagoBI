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

import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.crosstable.exporter.CrosstabPDFExporter;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class WorkSheetPDFExporter {

	private Document pdfDocument = null;
	private PdfWriter docWriter = null;
	private IDataStore dataStore = null;
	
	private static float IMAGE_MAX_WIDTH = 100;
	private static float IMAGE_MAX_HEIGHT = 100;
	private static float TITLE_MAX_HEIGHT = 50;
	
	private static float MARGIN_TOP = 36;
	private static float MARGIN_RIGHT = 36;
	private static float MARGIN_BOTTOM = 36;
	private static float MARGIN_LEFT = 36;
	
	public static final String HEADER = "HEADER";
	public static final String FOOTER = "FOOTER";
	public static final String CONTENT = "CONTENT";

	public static final String SHEET_TYPE = "SHEET_TYPE";
	public static final String CHART = "CHART";
	public static final String CROSSTAB = "CROSSTAB";
	public static final String TABLE = "TABLE";
	
	public static final String POSITION = "position";
	public static final String TITLE = "title";
	public static final String IMG = "img";
	
	public static final String CENTER = "center";
	public static final String RIGHT = "right";
	public static final String LEFT = "left";

	public static final String SVG = "SVG";
	
	private DecimalFormat numberFormat;
	private String userDateFormat;
	
	
	/** Logger component. */
	public static transient Logger logger = Logger.getLogger(WorkSheetPDFExporter.class);

	public void open( OutputStream outputStream ) throws DocumentException {
	    pdfDocument = new Document(PageSize.A4.rotate());
	    docWriter = PdfWriter.getInstance(pdfDocument, outputStream);
	    pdfDocument.open();
	}
	
	public void close() {
	    pdfDocument.close();
	    docWriter.close();
	}

	public void addSheet(JSONObject sheetJSON, IDataStore dataStore){
		this.dataStore = dataStore;
		addSheet(sheetJSON);
	}
	
	public void addSheet(JSONObject sheetJSON) {
		try {
			pdfDocument.newPage();
			
			float[] margins = getContentMargins(sheetJSON); 
			
			pdfDocument.setMargins(MARGIN_LEFT, MARGIN_RIGHT, margins[0], margins[1]);
			
			if (sheetJSON.has(WorkSheetPDFExporter.HEADER)) {
				JSONObject header = sheetJSON
						.getJSONObject(WorkSheetPDFExporter.HEADER);
				setHeader(header);
			}
			
			JSONObject content = sheetJSON.getJSONObject(WorkSheetPDFExporter.CONTENT);
			String sheetType = content.getString(WorkSheetPDFExporter.SHEET_TYPE);
			      
			if (WorkSheetPDFExporter.CHART.equalsIgnoreCase(sheetType)) {
				addChart(content, margins);
			}else if (WorkSheetPDFExporter.TABLE.equalsIgnoreCase(sheetType)) {
				addTable(content);
			}else if (WorkSheetPDFExporter.CROSSTAB.equalsIgnoreCase(sheetType)) {
				addCrosstab(content);
			} else {
				logger.error("Sheet type " + sheetType + " not recognized");
			}
			
			if (sheetJSON.has(WorkSheetPDFExporter.FOOTER)) {
				JSONObject footer = sheetJSON
						.getJSONObject(WorkSheetPDFExporter.FOOTER);
				setFooter(footer);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Error while adding sheet", e);
		}
		
	}

	private float[] getContentMargins(JSONObject sheetJSON) throws Exception {
		float top = 0;
		float bottom = 0;
		if (sheetJSON.has(WorkSheetPDFExporter.HEADER)) {
			top = getTopMargin(sheetJSON.getJSONObject(WorkSheetPDFExporter.HEADER));
		}
		if (sheetJSON.has(WorkSheetPDFExporter.FOOTER)) {
			bottom = getBottomMargin(sheetJSON.getJSONObject(WorkSheetPDFExporter.HEADER));
		}
		float[] toReturn = new float[] {top, bottom};
		return toReturn;
	}

	private float getBottomMargin(JSONObject footer) throws Exception {
		return getMargin(footer) + MARGIN_BOTTOM;
	}

	private float getTopMargin(JSONObject header) throws Exception {
		return getMargin(header) + MARGIN_TOP;
	}

	private float getMargin(JSONObject headerOrFooter) throws Exception {
		float toReturn = 0;
		String title = headerOrFooter.getString(TITLE);
		String imgName = headerOrFooter.getString(IMG);
		String imagePosition = headerOrFooter.getString(POSITION);
		if (title != null && !title.trim().equals("") ) {
			toReturn = TITLE_MAX_HEIGHT;
		}
		if ( imgName != null && !imgName.equals("")
				&& !imgName.equals("null") ) {
			if (CENTER.equals(imagePosition)) {
				toReturn += IMAGE_MAX_HEIGHT;
			} else {
				toReturn = IMAGE_MAX_HEIGHT;
			}
		}
		return toReturn;
	}
	
	private void setHeader(JSONObject header) {
		try {
			String title = header.getString(TITLE);
			String imgName = header.getString(IMG);
			String imagePosition = header.getString(POSITION);
			Image image = null;
			if ( imgName != null && !imgName.equals("")
					&& !imgName.equals("null") ) {
				File imageFile = getImage(imgName);
				if (!imageFile.exists() || !imageFile.isFile()) {
					logger.error("Image " + imgName + " not found!!!");
				} else {
					image = Image.getInstance(imageFile.getPath());
					fitImage(image, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);
					setHeaderImagePosition(image, imagePosition);
					pdfDocument.add(image);
				}
			}
			if (title != null && !title.trim().equals("") ) {
				
				float[] titlePosition = getHeaderTitlePosition(image, imagePosition);
				addHtmlToPdfContentByte(title, titlePosition);

			}
		} catch (Exception e) {
			throw new RuntimeException("Error while adding header", e);
		}
	}
	

	private float[] getHeaderTitlePosition(Image image, String imagePosition) {
		float llx, lly; // lower-left corner x and y position
		float urx, ury; // upper-right corner x and y position
		float imageHeight = image != null ? image.getHeight() : 0;
		float imageWidth = image != null ? image.getWidth() : 0;
		if ( LEFT.equals(imagePosition) ) {
			llx = MARGIN_LEFT + imageWidth + 30;
			lly = PageSize.A4.getWidth() - ( MARGIN_TOP + Math.max(imageHeight, IMAGE_MAX_HEIGHT) );
			urx = PageSize.A4.getHeight() - MARGIN_RIGHT;
			ury = PageSize.A4.getWidth() - MARGIN_TOP;
		} else if ( RIGHT.equals(imagePosition) ) {
			llx = MARGIN_LEFT;
			lly = PageSize.A4.getWidth() - ( MARGIN_TOP + Math.max(imageHeight, IMAGE_MAX_HEIGHT) );
			urx = PageSize.A4.getHeight() - ( MARGIN_RIGHT + imageWidth + 30 );
			ury = PageSize.A4.getWidth() - MARGIN_TOP;
		} else { // CENTER case
			llx = MARGIN_LEFT;
			lly = PageSize.A4.getWidth() - ( MARGIN_TOP + imageHeight + TITLE_MAX_HEIGHT );
			urx = PageSize.A4.getHeight() - MARGIN_RIGHT;
			ury = PageSize.A4.getWidth() - ( MARGIN_TOP + imageHeight );
		}
		float[] toReturn = new float[] {llx, lly, urx, ury};
		return toReturn;
	}
	
	public void addHtmlToPdfContentByte(String html, float[] pos) {
		
		PdfContentByte cb = docWriter.getDirectContent();
		StyleSheet styles = createDefaultStyleSheet();

		ColumnText ct = new ColumnText(cb);
		ct.setSimpleColumn(pos[0], pos[1], pos[2], pos[3]);
		ct.setYLine(pos[3]);
		try {
			ArrayList htmlObjs = HTMLWorker.parseToList(new StringReader(html),
					styles);
			for (int k = 0; k < htmlObjs.size(); ++k) {
				ct.addElement((Element) htmlObjs.get(k));
			}
			ct.go();
		} catch (Exception e) {
			throw new RuntimeException("Could not parse HTML", e);
		}
	}
	
	private StyleSheet createDefaultStyleSheet() {
		StyleSheet styles = new StyleSheet();

//		styles.loadTagStyle("ul", "face", "Times");
//		styles.loadTagStyle("ul", "size", "25px");
//		styles.loadTagStyle("ul", "leading", "15f");
//		styles.loadTagStyle("ul", "list-style-type", "square");
//		styles.loadTagStyle("li", "face", "Times");
//		styles.loadTagStyle("li", "size", "25px");
//		styles.loadTagStyle("li", "leading", "15f");
//		styles.loadTagStyle("p", "face", "Times");
//		styles.loadTagStyle("p", "size", "11px");
//		styles.loadTagStyle("p", "leading", "12f");
//		styles.loadTagStyle("p", "spacingAfter", "6x");

		return styles;
	}

	private void setHeaderImagePosition(Image image, String imagePosition) {
		float top = PageSize.A4.getWidth() - (MARGIN_TOP + image.getHeight()); // remember that the page is A4 rotated
		float left = MARGIN_LEFT;
		if (LEFT.equals(imagePosition)) {
			left = MARGIN_LEFT;
		} else if (CENTER.equals(imagePosition)) {
			left = PageSize.A4.getHeight() / 2 - image.getWidth() / 2;
		} else if (RIGHT.equals(imagePosition)) {
			left = PageSize.A4.getHeight() - (MARGIN_RIGHT + image.getWidth()); // remember that the page is A4 rotated
		}
		image.setAbsolutePosition(left, top);
	}

	private void setFooterImagePosition(Image image, String imagePosition) {
		float top = MARGIN_BOTTOM;
		float left = MARGIN_LEFT;
		if (LEFT.equals(imagePosition)) {
			left = MARGIN_LEFT;
		} else if (CENTER.equals(imagePosition)) {
			left = PageSize.A4.getHeight() / 2 - image.getWidth() / 2;
		} else if (RIGHT.equals(imagePosition)) {
			left = PageSize.A4.getHeight() - (MARGIN_RIGHT + image.getWidth()); // remember that the page is A4 rotated
		}
		image.setAbsolutePosition(left, top);
	}
	
	private void setFooter(JSONObject footer) {
		try {
			String title = footer.getString(TITLE);
			String imgName = footer.getString(IMG);
			String imagePosition = footer.getString(POSITION);
			Image image = null;
			if ( imgName != null && !imgName.equals("")
					&& !imgName.equals("null") ) {
				File imageFile = getImage(imgName);
				if (!imageFile.exists() || !imageFile.isFile()) {
					logger.error("Image " + imgName + " not found!!!");
				} else {
					image = Image.getInstance(imageFile.getPath());
					fitImage(image, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);
					setFooterImagePosition(image, imagePosition);
					pdfDocument.add(image);
				}
			}
			if (title != null && !title.trim().equals("") ) {
				
				float[] titlePosition = getFooterTitlePosition(image, imagePosition);
				addHtmlToPdfContentByte(title, titlePosition);
				
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while adding header", e);
		}
	}
	
	
	private float[] getFooterTitlePosition(Image image, String imagePosition) {
		float llx, lly; // lower-left corner x and y position
		float urx, ury; // upper-right corner x and y position
		float imageHeight = image != null ? image.getHeight() : 0;
		float imageWidth = image != null ? image.getWidth() : 0;
		if ( LEFT.equals(imagePosition) ) {
			llx = MARGIN_LEFT + imageWidth + 30;
			lly = MARGIN_BOTTOM;
			urx = PageSize.A4.getHeight() - MARGIN_RIGHT;
			ury = MARGIN_BOTTOM + Math.max(imageHeight, IMAGE_MAX_HEIGHT);
		} else if ( RIGHT.equals(imagePosition) ) {
			llx = MARGIN_LEFT;
			lly = MARGIN_BOTTOM;
			urx = PageSize.A4.getHeight() - ( MARGIN_RIGHT + imageWidth + 30 );
			ury = MARGIN_BOTTOM + Math.max(imageHeight, IMAGE_MAX_HEIGHT);
		} else { // CENTER case
			llx = MARGIN_LEFT;
			lly = MARGIN_BOTTOM + imageHeight;
			urx = PageSize.A4.getHeight() - MARGIN_RIGHT;
			ury = MARGIN_BOTTOM + imageHeight + TITLE_MAX_HEIGHT;
		}
		float[] toReturn = new float[] {llx, lly, urx, ury};
		return toReturn;
	}


	private File getImage(String fileName) {
		logger.debug("IN");
		File toReturn = null;
		File imagesDir = QbeEngineConfig.getInstance().getWorksheetImagesDir();
		//File imagesDir = new File("C:/Progetti/SpagoBI/SpagoBI-2.x-Helios-workspace/runtimes/apache-tomcat-6.0.18/resources_mysql/qbe/worksheet/images");
		toReturn = new File(imagesDir, fileName);
		logger.debug("OUT");
		return toReturn;
	}

	private void addChart(JSONObject content, float[] margins) {
		try {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			String svg = content.getString(SVG);
			//Don't change ISO-8859-1 because it's the only way to export specific symbols
			inputStream = new ByteArrayInputStream(svg.getBytes("ISO-8859-1"));
			File imageFile = File.createTempFile("chart", ".jpg");
			outputStream = new FileOutputStream(imageFile);
			transformSVGIntoJPEG(inputStream, outputStream);
			
		    Image jpg = Image.getInstance(imageFile.getPath());
//		    Image jpg = Image.getInstance("C:/Davide/Varie/11_03_22/thumbbig-28333.jpg");
		    
		    float topMargin = margins[0];
		    float bottomMargin = margins[1];
		    
		    float chartMaxHeight = PageSize.A4.getWidth() - (topMargin + bottomMargin);  // remember that the page is A4 rotated
		    float chartMaxWidth = PageSize.A4.getHeight() - (MARGIN_LEFT + MARGIN_RIGHT);  // remember that the page is A4 rotated
		    
		    float[] newDimensions = fitImage( jpg, chartMaxWidth, chartMaxHeight );

		    float positionX = (PageSize.A4.getHeight() - newDimensions[0]) / 2;
		    float positionY = bottomMargin;
		    jpg.setAbsolutePosition(positionX, positionY);
		    
		    pdfDocument.add(jpg);
		} catch (Exception e) {
			throw new RuntimeException("Error while adding chart", e);
		}
	}
	
	private void addTable(JSONObject content) {
		try {
			DataSourceTablePDFExporter tableExp = new DataSourceTablePDFExporter(dataStore, numberFormat, userDateFormat);
			tableExp.export(pdfDocument);

		} catch (Exception e) {
			throw new RuntimeException("Error while adding chart", e);
		}
	}
	
	private void addCrosstab(JSONObject content) {
		try {
			
			CrosstabPDFExporter csExporter = new CrosstabPDFExporter();
			String crosstab = content.getString(WorkSheetXLSExporter.CROSSTAB);
			JSONObject crosstabJSON = new JSONObject(crosstab);	
			csExporter.export(crosstabJSON, pdfDocument,numberFormat);
		    
		} catch (Exception e) {
			throw new RuntimeException("Error while adding chart", e);
		}
	}
	
	
	private float[] fitImage(Image jpg, float maxWidth, float maxHeight) {
		float newWidth = 0;
		float newHeight = 0;
		if (jpg.getWidth() > maxWidth) {
			newWidth = maxWidth;
			newHeight = (newWidth / jpg.getWidth())
					* jpg.getHeight();
			jpg.scalePercent(newWidth * 100 / jpg.getWidth());
		}
		if (jpg.getHeight() > maxHeight) {
			newHeight = maxHeight;
			newWidth = (newHeight / jpg.getHeight())
					* jpg.getWidth();
			jpg.scalePercent(newHeight * 100 / jpg.getHeight());
		}
		float[] toReturn = new float[] {newWidth, newHeight};
		return toReturn;
		
	}
	
	public static void transformSVGIntoJPEG (InputStream inputStream,	OutputStream outputStream) {
		// create a JPEG transcoder
		JPEGTranscoder t = new JPEGTranscoder();
		
		// set the transcoding hints
		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));
		t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(1000));
		t.addTranscodingHint(JPEGTranscoder.KEY_ALLOWED_SCRIPT_TYPES, "*");
		t.addTranscodingHint(JPEGTranscoder.KEY_CONSTRAIN_SCRIPT_ORIGIN, new Boolean(true));
		t.addTranscodingHint(JPEGTranscoder.KEY_EXECUTE_ONLOAD, new Boolean(true));
		
		// create the transcoder input
		Reader reader = new InputStreamReader(inputStream);
		TranscoderInput input = new TranscoderInput(reader);
		
		// create the transcoder output
		TranscoderOutput output = new TranscoderOutput(outputStream);
		
		// save the image
		try {
			t.transcode(input, output);
		} catch (TranscoderException e) {
			logger.error("Impossible to convert svg to jpeg: " + e.getCause(), e);
			throw new SpagoBIEngineRuntimeException("Impossible to convert svg to jpeg: " + e.getCause(), e);
		}
	}

	public void setNumberFormat(DecimalFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

	public void setUserDateFormat(String userDateFormat) {
		this.userDateFormat = userDateFormat;
	}	
	
	
	
	
}
