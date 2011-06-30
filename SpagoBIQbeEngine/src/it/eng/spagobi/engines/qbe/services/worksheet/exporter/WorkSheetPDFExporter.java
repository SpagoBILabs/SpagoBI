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
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class WorkSheetPDFExporter {

	private Document pdfDocument = null;
	private PdfWriter docWriter = null;
	
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

	public void addSheet(JSONObject sheetJSON) {
		try {
			pdfDocument.newPage();
			
			if (sheetJSON.has(WorkSheetPDFExporter.HEADER)) {
				JSONObject header = sheetJSON
						.getJSONObject(WorkSheetPDFExporter.HEADER);
				setHeader(header);
			}
			
			JSONObject content = sheetJSON.getJSONObject(WorkSheetPDFExporter.CONTENT);
			String sheetType = content.getString(WorkSheetPDFExporter.SHEET_TYPE);
	
			if (WorkSheetPDFExporter.CHART.equalsIgnoreCase(sheetType)) {
				addChart(content);
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

	private void setHeader(JSONObject header) {
		try {
			String title = header.getString(TITLE);
			String imgName = header.getString(IMG);
			String imagePosition = header.getString(POSITION);
			int horizontalAlignment = getAlignment(imagePosition);
			if ( imgName != null && !imgName.equals("")
					&& !imgName.equals("null") ) {
				File imageFile = getImage(imgName);
				Image image = Image.getInstance(imageFile.getPath());
				image.setAlignment(horizontalAlignment);
				pdfDocument.add(image);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while adding header", e);
		}
	}
	
	private void setFooter(JSONObject footerJSON) {
		try {
			String title = footerJSON.getString(TITLE);
			String imgName = footerJSON.getString(IMG);
			String imagePosition = footerJSON.getString(POSITION);
			int horizontalAlignment = getAlignment(imagePosition);
			if ( imgName != null && !imgName.equals("")
					&& !imgName.equals("null") ) {
				File imageFile = getImage(imgName);
				Image image = Image.getInstance(imageFile.getPath());
				image.setAlignment(horizontalAlignment);
				image.setAbsolutePosition(image.absoluteX(), 0);
				pdfDocument.add(image);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error while adding header", e);
		}
	}
	
	private int getAlignment(String imagePosition) {
		if (LEFT.equals(imagePosition)) {
			return Image.ALIGN_LEFT;
		}
		if (CENTER.equals(imagePosition)) {
			return Image.ALIGN_CENTER;
		}
		if (RIGHT.equals(imagePosition)) {
			return Image.ALIGN_RIGHT;
		}
		return Image.ALIGN_LEFT;
	}

	private File getImage(String fileName) {
		logger.debug("IN");
		File toReturn = null;
		File imagesDir = QbeEngineConfig.getInstance().getWorksheetImagesDir();
		toReturn = new File(imagesDir, fileName);
		logger.debug("OUT");
		return toReturn;
	}

	private void addChart(JSONObject content) {
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
		    fitImage(jpg);
		    jpg.setAlignment(Image.MIDDLE);
		    pdfDocument.add(jpg);
		} catch (Exception e) {
			throw new RuntimeException("Error while adding chart", e);
		}
	}
	
	
	/**
	 * Set the dimension of the image to fit the A4 page size
	 * The layout of the page should be horizontal 
	 * @param jpg the image to fit
	 */
	private void fitImage(Image jpg) {
//		if (jpg.width() > PageSize.A4.height()) {
//			float imgScaledWidth = PageSize.A4.height() - 100;
//			float imgScaledHeight = (imgScaledWidth / jpg.width())
//					* jpg.height();
//			jpg.scaleAbsolute(imgScaledWidth, imgScaledHeight);
//		}
//		if (jpg.height() > PageSize.A4.width()) {
//			float imgScaledHeight = PageSize.A4.width() - 100;
//			float imgScaledWidth = (imgScaledHeight / jpg.height())
//					* jpg.width();
//			jpg.scaleAbsolute(imgScaledWidth, imgScaledHeight);
//		}
		
		jpg.scaleAbsolute(jpg.width() / 1.6f, jpg.height() / 1.6f);
		
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
	
}
