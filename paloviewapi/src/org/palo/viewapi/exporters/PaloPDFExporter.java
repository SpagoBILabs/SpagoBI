/*
*
* @file PaloPDFExporter.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author AndreasEbbert
*
* @version $Id: PaloPDFExporter.java,v 1.6 2010/04/15 09:54:49 PhilippBouillon Exp $
*
*/

/* (c) 2008 Tensegrity Software GmbH */
package org.palo.viewapi.exporters;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.palo.api.Cell;
import org.palo.api.Cube;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.uimodels.axis.AxisFlatModel;
import org.palo.viewapi.uimodels.axis.AxisItem;
import org.palo.viewapi.uimodels.formats.BorderData;
import org.palo.viewapi.uimodels.formats.Format;
import org.palo.viewapi.uimodels.formats.FormatRangeInfo;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * To export Palo views to PDF. TODO: formats TODO: show schnitt-elemente
 * @author AndreasEbbert
 * @version $Id: PaloPDFExporter.java,v 1.6 2010/04/15 09:54:49 PhilippBouillon Exp $
 */
public class PaloPDFExporter implements CubeViewExporter {
	private static final DecimalFormat DEFAULT_FORMATTER = new DecimalFormat("#,##0.00");

	// private final static float PARA_INDENTATION = 25f;
	private final static Font FONT_H1, FONT_CELL, FONT_CELL_H;
	private final static BaseFont BASE_FONT_CELL;
	private final static BaseFont BASE_FONT_CELL_H;
	// private final static BaseFont BASE_FONT_H1;

	private final static Font FONT_PNUMBER = new Font(Font.FontFamily.COURIER, 9);
	private final static BaseColor COLOR_CELL_BG = new BaseColor(0xf8, 0xf8, 0xf8);
	private final static File tempDir;

	private final static int DIM_OBJECT_WIDTH = 75;
	
	// private final static float MARGIN_TOP = 20;
	// private final static float MARGIN_BOTTOM = MARGIN_TOP;
	// private final static float MARGIN_LEFT = 20;
	// private final static float MARGIN_RIGHT = MARGIN_LEFT;

	// private final File outDir;
	private PaloPDFExporterConfiguration config;
	// private File outFile;
	private CubeView view;
	private int rowCount;
	private IndexRangeInfo[] indexRanges;
	// to store the calculated column widths
	private float[] cWidths;
	private float cHeight;
	private float headerRows;
	private float firstSiteYOffSet;
	private HashMap<Dimension, ArrayList<LevelRangeInfo>> allDimensionLevels;
	private HashMap<String, Element[]> indexCellMap = new HashMap<String, Element[]>();
	private File outFile;

	private PdfWriter writer;
	private static final String INDENT = "    ";
	private float columsMaxWidth;
	private float rowHeadersMaxWidth;
	private final String cellReplacementString;
	
	static {
		String user_home = System.getProperty("user.home");
		tempDir = new File(user_home, ".palotmp");

		FONT_H1 = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
		FONT_CELL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
		FONT_CELL_H = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

		// BASE_FONT_H1 = FONT_H1.getCalculatedBaseFont(false);
		BASE_FONT_CELL = FONT_CELL.getCalculatedBaseFont(false);
		BASE_FONT_CELL_H = FONT_CELL_H.getCalculatedBaseFont(false);
	}

	public PaloPDFExporter() {
		this(null);
	}

	public PaloPDFExporter(PaloPDFExporterConfiguration config) {
		this.config = config != null ? config : PaloPDFExporterConfiguration.createDefault();
		if (config == null) {
			cellReplacementString = "#####";
			determineWidths("-999.999.999.999.999.999,00", "This is the reference string");
		} else {
			cellReplacementString = config.getCellReplaceString();
			determineWidths(config.getMaxColString(), config.getMaxRowsHeaderString());
		}
	}

	private final void determineWidths(String cmw, String rhmw) {
		columsMaxWidth = BASE_FONT_CELL.getWidthPoint(cmw + "9", FONT_CELL.getSize());
		rowHeadersMaxWidth = BASE_FONT_CELL_H.getWidthPoint(rhmw, FONT_CELL_H.getSize());
	}
	
	public void export(CubeView view) {
		this.view = view;
		try {
			Document document = new Document(config.getPageSize());
			// document.setMargins(document.leftMargin(), document.rightMargin(),
			// document.topMargin(),
			// document.bottomMargin() + 75f);
			document.addCreator("JPalo / www.tensegrity.com");
			// document.setFooter(new HeaderFooter(new Phrase("JPalo / Tensegrity
			// Software GmbH"), false));

			outFile = null;
			outFile = getOutFile();
//			System.out.println("Writing to file: " + outFile.getAbsolutePath() + " (" + outFile.getName() + ")");
			writer = PdfWriter.getInstance(document, new FileOutputStream(outFile));
			writer.setStrictImageSequence(true);

			writer.setViewerPreferences(PdfWriter.PageModeUseOutlines | PdfWriter.PageLayoutSinglePage);
			PDFPageEventHandler eventHandler = new PDFPageEventHandler();
			writer.setPageEvent(eventHandler);

			document.addTitle(view.getName());
			document.open();

			String title;
			try {
				title = config.getTitle(); 
//					"Export view " + view.getCube().getName(); 
//					ExportMessages.getString("export.view.title", new String[] { view.getName(),
//						view.getCube().getName() });
			}
			catch (Exception e) {
				title = view.getName();
			}
			if (config.isShowTitle()) {
				Phrase phrase = new Phrase(title, FONT_H1);
				document.add(phrase);
			}

			firstSiteYOffSet = document.topMargin() + (config.isShowTitle() ? FONT_H1.getSize() : 0);

			export(writer, document, view);

			document.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private final void export(PdfWriter writer, Document doc, CubeView view) throws DocumentException {
		Cube cube = view.getCube();

		Dimension[] cubeDims = cube.getDimensions();
		HashMap<Hierarchy, Integer> hierIndex = new HashMap<Hierarchy, Integer>();
		for (int i = 0; i < cubeDims.length; ++i)
			hierIndex.put(cubeDims[i].getDefaultHierarchy(), i);

		Element[] coord = new Element[cubeDims.length];
		Axis selAxis = view.getAxis("selected");
		Axis repAxis = view.getAxis("hierarchy-repository");
		for (int i = 0; i < cubeDims.length; i++) {
			AxisHierarchy axisHierarchy = selAxis.getAxisHierarchy(cubeDims[i]
					.getDefaultHierarchy());
			if (axisHierarchy != null) {
				Element[] selElements = axisHierarchy.getSelectedElements();
				// palo can use only the first selected element:
				if (selElements != null && selElements.length > 0) {
					coord[i] = selElements[0];
				}
			} else {
				axisHierarchy = repAxis.getAxisHierarchy(cubeDims[i].getDefaultHierarchy());
				if (axisHierarchy != null) {
					Element [] selElements = axisHierarchy.getSelectedElements();
					if (selElements != null && selElements.length > 0) {
						coord[i] = selElements[0];
					}
				}
			}
		}
		
		// ----------------------------------------------------------------------------------
		// prepare to draw the main table
		// palo defines a rows, cols and selected axis
		Object rr = view.getPropertyValue(CubeView.PROPERTY_ID_REVERSE_VERTICAL_LAYOUT);
		
		AxisFlatModel rows = null;
		try {
			rows = new AxisFlatModel(view.getAxis("rows"), AxisFlatModel.VERTICAL, rr != null && Boolean.parseBoolean("" + rr));
		} catch (Throwable t) {
			doc.close();
			return;
		}
		final AxisItem[][] rowsModel = rows.getModel();
		Object rc = view.getPropertyValue(CubeView.PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT);		
		final AxisFlatModel cols = new AxisFlatModel(view.getAxis("cols"), rc != null && Boolean.parseBoolean("" + rc));
		final AxisItem[][] colsModel = cols.getModel();

		final int rowLeaf = rowsModel[0].length - 1;
		final int colLeaf = colsModel.length - 1;
		int cells = rowsModel.length * colsModel[colLeaf].length;
		Element[][] cellCoords = new Element[cells][];

		int cellIndex = 0;
		HashMap<String, Integer[]> cellIndexMap = new HashMap<String, Integer[]>();
		for (int r = 0; r < rowsModel.length; ++r) {
			for (int c = 0; c < colsModel[colLeaf].length; ++c) {
				cellCoords[cellIndex] = coord.clone();
				// fill row coordinates:
				fill(cellCoords[cellIndex], rowsModel[r][rowLeaf], hierIndex);
				// and finally the col coordinates:
				fill(cellCoords[cellIndex], colsModel[colLeaf][c], hierIndex);				
				String key = getString(cellCoords[cellIndex]);
				// System.out.println("cellIndexMap.put: " + key);
				cellIndexMap.put(key, new Integer[] { r, c });
				indexCellMap.put(r + ";" + c, cellCoords[cellIndex]);
				cellIndex++;
			}
		}
		Cell[] cellValues;
		try {
			cellValues = cube.getCells(cellCoords);
		} catch (Throwable t) {
			doc.close();
			return;
		}

		Object hz = view.getPropertyValue(CubeView.PROPERTY_ID_HIDE_EMPTY);
		ArrayList <Integer> emptyColumns = new ArrayList<Integer>();
		ArrayList <Integer> emptyRows = new ArrayList<Integer>();
		if (hz != null && Boolean.parseBoolean(hz.toString())) {
			cellIndex = 0;
			boolean [] hasRowValue = new boolean[colsModel[colLeaf].length];
			for (int c = 0; c < colsModel[colLeaf].length; ++c) {
				hasRowValue[c] = false;
			}
			for (int r = 0; r < rowsModel.length; ++r) {
				boolean hasColValues = false;
				for (int c = 0; c < colsModel[colLeaf].length; ++c) {
					if (!cellValues[cellIndex].isEmpty()) {
						hasColValues = true;
						hasRowValue[c] = true;
					}
					cellIndex++;
				}
				if (!hasColValues) {
					emptyRows.add(r);
				}
			}
			for (int c = 0; c < colsModel[colLeaf].length; ++c) {
				if (!hasRowValue[c]) {
					emptyColumns.add(c);
				}
			}
		}
		
		// prepare formats
		allDimensionLevels = new HashMap<Dimension, ArrayList<LevelRangeInfo>>();
		ArrayList<IndexRangeInfo> allCoordinates = new ArrayList<IndexRangeInfo>();
		for (Format f : view.getFormats()) {
			for (FormatRangeInfo r : f.getRanges()) {
				if (r.getCells() != null) {
					for (Element[] e : r.getCells()) {
						String key = getString(e);
						// System.out.println("cellIndexMap.get: " + key);
						Integer[] rcCoord = cellIndexMap.get(key);
						// for (int j = 0; j < rcCoord.length; j++) {
						// System.out.print(rcCoord[j] + " ");
						// }
						// System.out.println(rcCoord);
						if (rcCoord != null) {
							allCoordinates.add(new IndexRangeInfo(f, rcCoord[0], rcCoord[1]));
						}
					}
				}
				else {
					for (Dimension d : r.getDimensions()) {
						ArrayList<LevelRangeInfo> l;
						l = allDimensionLevels.get(d);
						if (l == null) {
							l = new ArrayList<LevelRangeInfo>();
						}
						l.add(new LevelRangeInfo(f, r.getLevel(d)));
						allDimensionLevels.put(d, l);
					}
				}
			}
		}
		indexRanges = allCoordinates.toArray(new IndexRangeInfo[allCoordinates.size()]);

		final int colCount = colsModel[colLeaf].length + rowLeaf + 1;
//		System.out.println("table columns: " + colCount);
		PdfPTable table = createTable(colCount - emptyColumns.size());
		// export table:
		// dumpSelectedElements(coord);
		rowCount = 0;

		cWidths = new float[colCount - emptyColumns.size()];
		cHeight = -1;
		// cWVal = new String[colCount];

//		PdfPTable headers = createTable(view.getAxis("cols").getAxisHierarchies().length);
//		int counter = 0;
//		float [] widths = new float[view.getAxis("cols").getAxisHierarchies().length];
//		for (AxisHierarchy ah : view.getAxis("cols").getAxisHierarchies()) {
//			// html.append("<td>" + ah.getHierarchy().getName() + "</td>");
//			PdfPCell cell = createHCell(ah.getHierarchy().getName());
//			headers.addCell(cell);
//			widths[counter] = calcWidth(counter, cell, ah.getHierarchy().getName());
//			counter++;
//		}
//		headers.setTotalWidth(widths);
//		headers.setLockedWidth(true);
//		addTable(writer, doc, headers, cHeight, widths, firstSiteYOffSet, 1);
//		
//		cHeight = -1;
		
//		addColumnTable(doc, view.getAxis("cols"));
				
//		createHeaderCells(table, colsModel, rowsModel);
		createHeaderCells(table, rowsModel, colsModel, emptyColumns);
//		createRowCells(table, rowsModel, colsModel[colLeaf].length, cellValues);
		createRowCells(table, rowsModel, colsModel, emptyColumns, emptyRows, cellValues);

		table.setTotalWidth(cWidths);
		table.setLockedWidth(true);

		for (int i = 0; i < colsModel.length; i++) {
			headerRows += table.getRowHeight(i);
//			System.out.println("RowHeight " + i + ": " + table.getRowHeight(i));
		}
		
//		System.out.println("HeaderRows: " + headerRows);		

//		int counter = 0;
		float selAxisHeight        = -1.0f;
//		float horizontalAxisHeight = -1.0f;
//		float verticalAxisWidth    = -1.0f;
//		int emptyCellRowCount = getMaxDimDepth(rowsModel[0]);
	
		selAxisHeight        = 
			config.isShowPOV() ? calcSelectionAxisHeight(doc, selAxis) : 0.0f;

//		System.out.println("Axis calculation results:");
//		System.out.println("  selAxisHeight == " + selAxisHeight);
//		System.out.println("  horAxisHeight == " + horizontalAxisHeight);
//		System.out.println("  verAxisWidth  == " + verticalAxisWidth);
		
		if (config.isShowPOV()) {
			addSelectionAxisHeader(doc, selAxis, selAxisHeight);
		}
//		addHorizontalAxisHeader(doc, colsModel, rowsModel);
//		addVerticalAxisHeader(doc, colsModel, rowsModel, horizontalAxisHeight, selAxisHeight);
		
		firstSiteYOffSet -= 26.0f;
		if (!config.isShowTitle()) {
			firstSiteYOffSet -= FONT_H1.getSize();
		}
		firstSiteYOffSet += selAxisHeight;
		
		addTable(writer, doc, table, cHeight, cWidths, firstSiteYOffSet, rowCount, colsModel.length, selAxisHeight);
	}

	private final float calcSelectionAxisHeight(Document doc, Axis selAxis) {
		AxisHierarchy [] axisHierarchies = selAxis.getAxisHierarchies();
		int width = axisHierarchies.length * (DIM_OBJECT_WIDTH + 5) - 5;
		if (width <= 0) {
			return 12.0f;
		}
		float pdfWidth = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin();
		int reps = (int) (width / pdfWidth);
		return (reps + 1) * 32.0f;
	}
	
	private final float calcHorizontalAxisHeight(float verticalAxisWidth, Document doc, int numberOfDims, int emptyCellRowCount) {
		int width = numberOfDims * (DIM_OBJECT_WIDTH + 5) - 5;
		if (width <= 0) {
			return 0.0f;
		}
		if (verticalAxisWidth < 0.0) {
			verticalAxisWidth = 32.0f;
		}
		float val = getColPos(doc, emptyCellRowCount, cWidths) + doc.leftMargin() + verticalAxisWidth;		
		float pdfWidth = doc.getPageSize().getWidth() - doc.rightMargin() - val;
		int reps = (int) (width / pdfWidth);
		return (reps + 1) * 16.0f;		
	}
	
	private final float calcVerticalAxisWidth(float selAxisHeight, float horizontalAxisHeight, Document doc, int numberOfDims) {
		int height = numberOfDims * (DIM_OBJECT_WIDTH + 5) - 5;
		if (height <= 0) {
			return 0.0f;
		}
		if (selAxisHeight < 0.0) {
			selAxisHeight = 32.0f;
		}
		if (horizontalAxisHeight < 0.0) {
			horizontalAxisHeight = 16.0f;
		}
		float pdfHeight = doc.getPageSize().getHeight() - firstSiteYOffSet - 9.0f - horizontalAxisHeight - selAxisHeight - headerRows;
		int reps = (int) (height / pdfHeight);
		return (reps + 1) * 16.0f;
	}
	
	private final void addSelectionAxisHeader(Document doc, Axis selAxis, float selAxisHeight) {
		PdfContentByte cb = writer.getDirectContent();
		float pdfWidth = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin();
		int width = selAxis.getAxisHierarchies().length * (DIM_OBJECT_WIDTH + 5) - 5;

		if (width <= 0) {
			return;
		}
		
		PdfTemplate tp = cb.createTemplate(pdfWidth, selAxisHeight);
		Graphics2D g2d = tp.createGraphics(pdfWidth, selAxisHeight); 
		int xOffset = 0;
		int yOffset = 0;
		g2d.setFont(g2d.getFont().deriveFont(8.0f));
		AxisHierarchy [] axisHierarchies = selAxis.getAxisHierarchies();		
		for (int i = 0; i < axisHierarchies.length; i++) {
			if ((xOffset + DIM_OBJECT_WIDTH) > pdfWidth) {
				xOffset = 0;
				yOffset += 32;
			}
			xOffset = renderSelectionHierarchy(g2d, axisHierarchies[i], xOffset, yOffset);
		}
				
		g2d.dispose();
		float offset = config.isShowTitle() ? 18.0f + selAxisHeight : selAxisHeight - 9.0f;
		cb.addTemplate(tp, doc.leftMargin(), doc.getPageSize().getHeight() - doc.topMargin() - offset);
//		System.out.println(doc.topMargin() + " " + doc.getPageSize().getHeight());
	}
	
	private final void addHorizontalAxisHeader(Document doc, AxisItem [][] colsModel, AxisItem [][] rowsModel) {
		PdfContentByte cb = writer.getDirectContent();
		PdfTemplate tp = cb.createTemplate(400.0f, 16.0f);
		Graphics2D g2d = tp.createGraphics(400.0f, 16.0f); 

		int xOffset = 0;
		int yOffset = 0;
		g2d.setFont(g2d.getFont().deriveFont(8.0f));
		for (int i = 0; i < colsModel.length; i++) {
			xOffset = renderHierarchyName(g2d, colsModel[i][0], xOffset, yOffset, false); 
		}
		
		g2d.dispose();
		float left = 10.0f;
		int emptyCellRowCount = getMaxDimDepth(rowsModel[0]);
		float val = getColPos(doc, emptyCellRowCount, cWidths) + doc.leftMargin() + 32;
		left = val;
		cb.addTemplate(tp, left, doc.top() - firstSiteYOffSet - 16.0f);		
	}
	
	private final void addVerticalAxisHeader(Document doc, AxisItem [][] colsModel, AxisItem [][] rowsModel, float horizontalAxisHeight, float selAxisHeight) {
		PdfContentByte cb = writer.getDirectContent();
		float pdfHeight = doc.getPageSize().getHeight() - firstSiteYOffSet - 9.0f - horizontalAxisHeight - selAxisHeight - headerRows;

		PdfTemplate tp = cb.createTemplate(128.0f, pdfHeight);
		Graphics2D g2d = tp.createGraphics(128.0f, pdfHeight); 

		int xPosition = (int) pdfHeight;
		int yOffset = (int) pdfHeight;
		g2d.setFont(g2d.getFont().deriveFont(8.0f));
//		g2d.setFont(g2d.getFont().deriveFont(AffineTransform.getRotateInstance(3.1415926535)));
		g2d.rotate(3 * 3.1415926535 / 2, 0, yOffset);
		for (int i = rowsModel[0].length - 1; i >= 0; i--) {
			if (xPosition - DIM_OBJECT_WIDTH - 10 <= 0) {
				xPosition = (int) pdfHeight;
				yOffset -= 16;
			}
			int xOffset = renderHierarchyName(g2d, rowsModel[0][i], xPosition, yOffset, true);
			int diff = xOffset - xPosition;
			xPosition -= diff;			
		}
		
		g2d.dispose();
		//Rectangle ps = doc.getPageSize();
		//float yoff = ps.getHeight() - firstSiteYOffSet - headerRows * cHeight - xOffset - 10;
		float yoff = doc.getPageSize().getHeight() - firstSiteYOffSet - 9.0f - horizontalAxisHeight - selAxisHeight - headerRows; 
		cb.addTemplate(tp, doc.leftMargin(), headerRows);		
	}

	private final String shorten(String text, int maxWidth, FontMetrics fm) {
		int textWidth = fm.stringWidth(text);

		String origName = text;
		int origLength = origName.length();
		while (textWidth > maxWidth) {
			if (origLength > 40) {
				text = origName.substring(0, 40);
				origName = text;
				origLength = 40;
			}
			origName = origName.substring(0, origLength - 1);
			origLength--;
			text = origName + "...";
			textWidth = fm.stringWidth(text);
		}
		
		return text;
	}
	
	private final int renderHierarchyName(Graphics2D g2d, AxisItem item, int xOffset, int yOffset, boolean rightAligned) {
		String name = item.getHierarchy().getName();
		int width = DIM_OBJECT_WIDTH;
		int height = g2d.getFontMetrics().getHeight();
		FontMetrics fm = g2d.getFontMetrics();
		
		name = shorten(name, width - 10, fm);
		int textWidth = g2d.getFontMetrics().stringWidth(name);
		
		g2d.setColor(new Color(0xf8, 0xf8, 0xf8));
		g2d.fillRoundRect(xOffset, yOffset, width, height + 1, 5, 5);
		g2d.setColor(Color.black);
		g2d.drawRoundRect(xOffset, yOffset, width, height + 1, 5, 5);
		if (rightAligned) {
			g2d.drawString(name, xOffset + DIM_OBJECT_WIDTH - 5 - textWidth, yOffset + height - 2);
		} else {
			g2d.drawString(name, xOffset + 5, yOffset + height - 2);
		}
		
		return xOffset + width + 5;
	}
		
	private final int renderSelectionHierarchy(Graphics2D g2d, AxisHierarchy hier, int xOffset, int yOffset) {
		String name = hier.getHierarchy().getName();
		
		int width = DIM_OBJECT_WIDTH;
		int fontHeight = g2d.getFontMetrics().getHeight(); 
		int height = fontHeight * 2 + 1;		
		FontMetrics fm = g2d.getFontMetrics();
		java.awt.Font plainF = g2d.getFont();
		java.awt.Font f = g2d.getFont().deriveFont(Font.BOLD);
		FontMetrics fmbold = g2d.getFontMetrics(f);
		
		name = shorten(name, width - 10, fmbold);	
		String elementName = "";
		if (hier.getSelectedElements() != null && hier.getSelectedElements().length > 0 && hier.getSelectedElements()[0] != null) {
			elementName = shorten(hier.getSelectedElements()[0].getName(), width - 10, fm);	
		}
				
		g2d.setColor(new Color(0xf8, 0xf8, 0xf8));
		g2d.fillRoundRect(xOffset, yOffset, width, height + 1, 5, 5);
		g2d.setColor(Color.black);
		g2d.drawRoundRect(xOffset, yOffset, width, height + 1, 5, 5);
		g2d.drawLine(xOffset, yOffset + height / 2 + 1, xOffset + width - 1, yOffset + height / 2 + 1);

		g2d.setFont(f);
		g2d.drawString(name, xOffset + 5, yOffset + fontHeight - 2);
		g2d.setFont(plainF);
		g2d.drawString(elementName, xOffset + 5, yOffset + fontHeight * 2 - 1);
		
		return xOffset + width + 5;
	}

	// private String[] cWVal;

	private boolean isExtPage = false;

	private void addTable(PdfWriter writer, Document doc, PdfPTable table, float colHeight, float [] widths, float yOffset, int rows, int colHeaderRows, float selAxisHeight) {
		final Rectangle ps = doc.getPageSize();
		int startRow = 0;
		int page = 0;
		int remainingRows = rows;
		
		while (remainingRows > 0) {
			// for (int page = 0; page < pageCount; page++) {
			// System.out.println("page: " + p);
			int cStart = 0;
			float sy = ps.getHeight() - doc.topMargin();
			if (page == 0) {
				sy = ps.getHeight() - yOffset - 32.0f;
			}
//			System.out.println("page:             " + page);
//			System.out.println("sy :              " + sy);
//			System.out.println("ColHeight:        " + colHeight);
//			System.out.println("firstSiteYOffSet: " + firstSiteYOffSet);
			float height = sy - doc.bottomMargin();

			int rowsPerPage = (int) (height / colHeight); // + 2;
			if (page == 0) {
				int diff = (int) ((headerRows - colHeaderRows * colHeight) / colHeight);
//				System.out.println("Diff: " + diff + " RPP: " + rowsPerPage + ", RPPNew: " + (rowsPerPage - diff));
				rowsPerPage -= diff;				
				rowsPerPage++;
			} else {
				rowsPerPage++;
			}
//			System.out.println("rowsPerPage: " + rowsPerPage);
			while (true) {
				int cEnd = getColEnd(doc, cStart, widths, page);
				if (cEnd == -1) {
					isExtPage = false;
					break;
				}

				isExtPage = cStart != 0;
				// System.out.println("start: " + cs + " - " + ce);
//				float xOffset = 32;
				table.writeSelectedRows(cStart, cEnd, startRow, startRow + rowsPerPage, doc.leftMargin(),
						sy, writer.getDirectContent());
				doc.newPage();
				cStart = cEnd;
			}
			startRow = startRow + rowsPerPage;
			remainingRows -= rowsPerPage;
			if (remainingRows > 0) {
				page++;
			}
		}
	}

	private static float getColPos(Document doc, int col, float [] widths) {
		if (col >= widths.length) {
			return 0.0f;
		}
		float sum = 0;
		for (int i = 0; i < col; i++) {
			sum += widths[i];
		}
		return sum;		
	}
	
	private static int getColEnd(Document doc, int startCol, float[] widths, int page) {
		if (startCol >= widths.length)
			return -1;
		final Rectangle ps = doc.getPageSize();
		float width = ps.getWidth() - doc.leftMargin() - doc.rightMargin();
		float sum = 0;
		for (int i = startCol; i < widths.length; i++) {
			if (sum + widths[i] > width)
				return i;
			sum += widths[i];
		}
		return widths.length;
	}

	private final void fill(Element[] coord, AxisItem item, HashMap<Hierarchy, Integer> hierIndex) {
		if (item == null)
			return;

		coord[hierIndex.get(item.getHierarchy())] = item.getElement();
		fill(coord, item.getParentInPrevHierarchy(), hierIndex);
	}

	private final String getString(Element[] coord) {
		if (coord == null)
			return "";
		StringBuffer b = new StringBuffer();
		for (Element e : coord) {
			if (e == null)
			 continue;
			// b.append(e.getId() + ",");
			b.append(e.hashCode() + ",");
		}
		return b.toString();
	}

	public File getOutFile() {
		if (outFile != null)
			return outFile;
		if (config == null) {
			if (!tempDir.exists())
				tempDir.mkdir();
			return getOutFileChecked(tempDir);
		}
		return getOutFileChecked(new File(config.getPath()));
	}

	private File getOutFileChecked(File dir) {
		String name = view.getName().replace(" ", "_");
		name = name.replaceAll("/", "_");
		name = name.replaceAll("\\\\", "_"); // :*?"<>|
		name = name.replaceAll(":", "_");
		name = name.replaceAll("\\*", "_");
		name = name.replaceAll("\\?", "_");
		name = name.replaceAll("\"", "_");
		name = name.replaceAll("<", "_");
		name = name.replaceAll(">", "_");
		name = name.replaceAll("\\|", "_");
		File f;
		try {
			f = File.createTempFile(name + "_(", ").pdf", dir);
			return f;
		} catch (IOException e) {
			e.printStackTrace();
			f = new File(dir, name + ".pdf");
		}		
		//File 
		if (!f.exists())
			return f;
		// if (f.canWrite())
		// return f;

		int n = 2;
		do {
			name = view.getName().replace(" ", "_") + "_" + Integer.toString(n++) + ".pdf";
			f = new File(dir, name);
			if (!f.exists())
				return f;
			// if (f.canWrite())
			// return f;
		}
		while (true);

		// return f;
	}

	private PdfPTable createTable(int cols) {
		PdfPTable table = new PdfPTable(cols);
		// table.setLeft(PARA_INDENTATION * 5);
		// table.setPadding(2.0f);
		return table;
	}

	private PdfPCell createCell(String t, Format f) {
		final Phrase phrase = new Phrase(formatNumber(t, null), FONT_CELL);
		PdfPCell c = new PdfPCell(phrase);
		c.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_TOP);
		c.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
		c.setPaddingTop(0.25f);
		c.setPaddingBottom(c.getPaddingBottom() + 0.25f);
		// TESTING
		// if (rowCount == 1)
//		c.setCellEvent(new BorderPdfPCellEvent(getDummyBorderData()));

		// TESTING

		if (f != null) {
			applyFormat(f, c);
		}
		return c;
	}

	private PdfPCell createHCell(String h, int colSpan, AxisItem item, boolean row) {		
		String orig = h;
		int length = h.length();
		if (row) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < item.getLevel(); i++) {
				buf.append(INDENT);
			}
			float rhmw = BASE_FONT_CELL_H.getWidthPoint(buf.toString(), FONT_CELL_H.getSize()) + rowHeadersMaxWidth;

			while ((BASE_FONT_CELL_H.getWidthPoint(h, FONT_CELL_H.getSize())) > rhmw && length > 2) {
				length--;
				h = orig.substring(0, length) + "...";
			}
		} else {
			while ((BASE_FONT_CELL_H.getWidthPoint(h, FONT_CELL_H.getSize())) > columsMaxWidth && length > 2) {
				length--;
				h = orig.substring(0, length) + "...";
			}			
		}
//		if (h.length() > 20) {
//			h = h.substring(0, 18) + "...";
//		}
		PdfPCell c = null;
		if (item == null) {
			Phrase ph = new Phrase(h, FONT_CELL_H);
			c = new PdfPCell(ph);
		} else {
			int depth = item.getElementNode().getDepth();
			if (item.hasChildren()) {
				if (item.hasState(AxisItem.EXPANDED)) {					
					URL url = getClass().getResource("minus2.png");
					Phrase ph = null;
					try {
						Image image = Image.getInstance(url);
						image.scaleAbsolute(6, 6);
						ph = new Phrase();
						if (row) {
							if (config.isIndent()) {
								for (int i = 0; i < depth; i++) {
									ph.add(new Chunk(INDENT, FONT_CELL_H));
									h = h.substring(INDENT.length());
								}
							}
						} else {
							if (config.isIndent()) {
								for (int i = 0; i < depth; i++) {
									ph.add(new Chunk("\r\n", FONT_CELL_H));
									h = h.substring(2);
								}
							}
						}
						if (config.isShowExpansionStates()) {
							ph.add(new Chunk(image, 0, 0));
							ph.add(new Chunk(" " + h, FONT_CELL_H));
						} else {
							ph.add(new Chunk(h, FONT_CELL_H));
						}
					} catch (Exception e) {
						ph = new Phrase(h, FONT_CELL_H);
					}
					c = new PdfPCell(ph);
				} else {
					URL url = getClass().getResource("plus2.png");
					Phrase ph = null;
					try {
						Image image = Image.getInstance(url);
						image.scaleAbsolute(6, 6);
						ph = new Phrase();
						if (row) {
							if (config.isIndent()) {
								for (int i = 0; i < depth; i++) {
									ph.add(new Chunk(INDENT, FONT_CELL_H));
									h = h.substring(INDENT.length());
								}
							}
						} else {
							if (config.isIndent()) {
								for (int i = 0; i < depth; i++) {
									ph.add(new Chunk("\r\n", FONT_CELL_H));
									h = h.substring(2);
								}
							}
						}
						if (config.isShowExpansionStates()) {
							ph.add(new Chunk(image, 0, 0));						
							ph.add(new Chunk(" " + h, FONT_CELL_H));
						} else {
							ph.add(new Chunk(h, FONT_CELL_H));
						}
					} catch (Exception e) {
						ph = new Phrase(h, FONT_CELL_H);
					}
					c = new PdfPCell(ph);
				}
			} else {
				Phrase ph = new Phrase(h, FONT_CELL_H);
				c = new PdfPCell(ph);									
			}
		}
		c.setColspan(colSpan);
		c.setBackgroundColor(COLOR_CELL_BG);		
		c.setPaddingTop(0.25f);
		c.setPaddingBottom(c.getPaddingBottom() + 0.25f);		
		return c;
	}
	
	private PdfPCell createInvisibleHCell() {
		Phrase ph = new Phrase("", FONT_CELL_H);		
		PdfPCell c = new PdfPCell(ph);
		c.setBorder(0);
		c.setPaddingTop(0.25f);
		c.setPaddingBottom(c.getPaddingBottom() + 0.25f);
		return c;
	}

	public int traverseVisible(AxisItem item, HashMap <AxisItem, Boolean> emptyItems) {		
		ArrayList <Integer> result = new ArrayList<Integer>();
		result.add(0);		
		traverseVisible(item, null, null, result, true, emptyItems);		
		return result.get(0);
	}
	
	public void traverseVisible(AxisItem item, AxisItem parent, AxisItem parentInPrevHierarchy, ArrayList <Integer> result, boolean start, HashMap <AxisItem, Boolean> emptyItems) {
		if (item.hasRootsInNextHierarchy()) {
			for (AxisItem root: item.getRootsInNextHierarchy()) {
				traverseVisible(root, null, item, result, false, emptyItems);
			}
		} else {
			if (!emptyItems.containsKey(item) || !emptyItems.get(item)) {
				result.set(0, result.get(0) + 1);
			}
			if (item.hasChildren() && item.hasState(AxisItem.EXPANDED) && !start) {
				for (AxisItem kid: item.getChildren()) {
					traverseVisible(kid, item, parentInPrevHierarchy, result, false, emptyItems);
				}
			}
		}
	}

//	public void traverseVisibleOld(AxisItem item, AxisItem parent, AxisItem parentInPrevHierarchy, ArrayList <Integer> result, boolean start) {
//		if(!item.hasRootsInNextHierarchy()) {
//			result.set(0, result.get(0) + 1);
//		}
//		for(AxisItem rootInNextHierarchy : item.getRootsInNextHierarchy()) {
//			traverseVisible(rootInNextHierarchy, null, item, result, false);
//		}
//		if (!start) {
//			if (item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
//				for (AxisItem child : item.getChildren()) {
//					traverseVisible(child, item, parentInPrevHierarchy, result, false);
//				}
//			}
//		} else {
//			if (item.getParent() != null) {
//				for (AxisItem sibling: item.getParent().getChildren()) {
//					if (!sibling.equals(item)) {
//						traverseVisible(sibling, sibling.getParent(), parentInPrevHierarchy, result, false);
//					} 
//				}
//			}
//		}
//	}
	
	private final int numberOfRemainingEmptyColumns(ArrayList <Integer> emptyColumns, int c) {
		int result = 0;
		for (Integer ec: emptyColumns) {
			if (ec > c) {
				result++;
			}
		}
		return result;
	}
	
	class Pair {
		public int x;
		public int y;
		
		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int hashCode() {
			return x * 35 + y * 7;
		}
		
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Pair)) {
				return false;
			}
			Pair ot = (Pair) o;
			return ot.x == x && ot.y == y;
		}
	}
	
	class ItemPair {
		AxisItem parentInPrevHier;
		int col;
		
		public ItemPair(AxisItem parent, int col) {
			this.parentInPrevHier = parent;
			this.col = col;
		}
		
		public int hashCode() {
			return parentInPrevHier == null ? 17 : parentInPrevHier.hashCode() +
					7 * col;
		}
		
		public boolean equals(Object o) {
			if (o == null || !(o instanceof ItemPair)) {
				return false;
			}
			ItemPair ip = (ItemPair) o;
			if (ip.parentInPrevHier == null) {
				return parentInPrevHier == null && col == ip.col;
			}
			return ip.parentInPrevHier.equals(parentInPrevHier) && col == ip.col;
		}
	}
	
	private final void createHeaderCells(PdfPTable table, AxisItem [][] rowModel, AxisItem [][] colModel, ArrayList <Integer> emptyColumns) {
		int colLeaf = colModel.length - 1;
		headerRows = 0.0f;
		
		int emptyCellRowCount = getMaxDimDepth(rowModel[0]);
		PdfPCell [][] header = new PdfPCell[colModel.length][];
		HashMap <Integer, Integer> offset = new HashMap<Integer, Integer>();
		float [] headerHeight = new float[colModel.length];	
		for (int i = 0; i < colModel.length; i++) {
			headerHeight[i] = -1.0f;
			header[i] = new PdfPCell[colModel[colLeaf].length + emptyCellRowCount];
			for (int j = 0; j < emptyCellRowCount; j++) {
				header[i][j] = createInvisibleHCell();
				calcColWidth(true, j, header[i][j], "", -1);
				offset.put(i, j);			
			}
		}	

		// Mapping machen von Elementnamen zu Col nummer, dann beim
		// Traverse gucken, ob das Element in einer leeren Col ist,
		// wenn ja, nicht addieren (aber weiter traversieren!)
		HashMap <AxisItem, Boolean> emptyItems = new HashMap<AxisItem, Boolean>();
		for (int c = 0; c < colModel[colLeaf].length; ++c) {
			emptyItems.put(colModel[colLeaf][c], emptyColumns.contains(c));
		}
		
		HashMap <ItemPair, Integer> isEmpty = new HashMap<ItemPair, Integer>();
		HashMap <PdfPCell, Integer> colSpan = new HashMap<PdfPCell, Integer>();
		HashMap <Pair, String> contents = new HashMap<Pair, String>();
		for (int c = 0; c < colModel[colLeaf].length; ++c) {
			AxisItem itemInPrevHier = colModel[colLeaf][c].getParentInPrevHierarchy();
			if (emptyColumns.contains(c)) {				
//				for (ItemPair ip: isEmpty.keySet()) {
//					if ((itemInPrevHier == null && ip.parentInPrevHier != null) ||
//						!(itemInPrevHier.equals(ip.parentInPrevHier))) {
//						continue;
//					}
//					int val = isEmpty.get(ip);
//					System.out.println("Val [1] == " + val);					
//					if (val != 0) {
//						isEmpty.put(ip, val - 1);	
//					} 
//				}
//				for (PdfPCell cell: colSpan.keySet()) {
//					int val = colSpan.get(cell);
//					System.out.println(cell.getPhrase().getContent() + " Val == " + val);
//					if (val != 0) {
//						cell.setColspan(val - 1);
//						colSpan.put(cell, val - 1);
//					}
//				}
				continue;
			}
			AxisItem origItem = colModel[colLeaf][c];
			for (int j = 0; j < colModel.length; ++j) {
				AxisItem item = origItem;
				int counter = colLeaf;
				while (j < counter) {
					item = item.getParentInPrevHierarchy();
					counter--;	
				}
				ItemPair ip = new ItemPair(origItem.getParentInPrevHierarchy(), j);
				if (!isEmpty.containsKey(ip)) {
					isEmpty.put(ip, 0);
				}
				if (isEmpty.get(ip) > 0) {
					isEmpty.put(ip, isEmpty.get(ip) - 1);
				} else {
					int val = offset.get(j) + 1;
					offset.put(j, val);
	 				String tv = getVerticalIndentName(item.getName(), item.getElementNode().getDepth());
					int width = /*item.hasChildren() && item.hasState(AxisItem.EXPANDED) ? 1 :*/ traverseVisible(item, emptyItems);
	 				header[j][val] = createHCell(tv, width, item, false);
	 				if (item.hasChildren() && config.isShowExpansionStates()) {
	 					tv += "  ";
	 				}
	 				contents.put(new Pair(j, val), tv);
	 				colSpan.put(header[j][val], width);
//					calcColWidth(true, val, header[j][val], tv);
					if (width > 0) {
						width--;
					}
					isEmpty.put(ip, width);
				}								
			}			
		}
			
		for (int i = 0; i < header.length; i++) {
			for (int j = 0; j <= offset.get(i); j++) {
				String tv = contents.get(new Pair(i, j));
				if (tv == null) { tv = ""; };
				calcColWidth(true, j, header[i][j], tv, -1);
				table.addCell(header[i][j]);
			}
			rowCount++;
		}		
//		System.out.println("RowCount == " + rowCount);
	}
	
	private final String getIndentName(String h, int depth) {
//		if (h.length() > 20) {
//			h = h.substring(0, 18) + "...";		
//		}
		String orig = h;
		int length = h.length();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < depth; i++) {
			buf.append(INDENT);
		}
		float rhmw = BASE_FONT_CELL_H.getWidthPoint(buf.toString(), FONT_CELL_H.getSize()) + rowHeadersMaxWidth;
		
		while ((BASE_FONT_CELL_H.getWidthPoint(h, FONT_CELL_H.getSize())) > rhmw && length > 2) {
			length--;
			h = orig.substring(0, length) + "...";
		}
		
		StringBuffer name = new StringBuffer();
		if (config.isIndent()) {
			for (int i = 0; i < depth; i++) {
				name.append(INDENT);
			}
		}
		name.append(h);
		return name.toString();
	}
	
	private final String getVerticalIndentName(String h, int depth) {
		String orig = h;
		int length = h.length();
		while ((BASE_FONT_CELL_H.getWidthPoint(h, FONT_CELL_H.getSize())) > columsMaxWidth && length > 2) {
			length--;
			h = orig.substring(0, length) + "...";
		}			
		
		StringBuffer name = new StringBuffer();
		if (config.isIndent()) {
			for (int i = 0; i < depth; i++) {
				name.append("\r\n");
			}
		}
		name.append(h);
		return name.toString();
	}

	private PdfPCell firstCell = null;
	
	private final void createRowCells(PdfPTable table, AxisItem [][] rowModel, AxisItem [][] colModel, ArrayList <Integer> emptyColumns, ArrayList <Integer> emptyRows, Cell [] cellValues) {
		int cellPosition = 0;
		int columnCount = colModel[colModel.length - 1].length;
		ArrayList <PdfPCell> allCells = new ArrayList<PdfPCell>();
		ArrayList <PdfPCell> lastRow = new ArrayList<PdfPCell>();
		HashMap <Integer, Integer> isEmpty = new HashMap<Integer, Integer>();
		HashMap <Integer, PdfPCell> lastCell = new HashMap<Integer, PdfPCell>();

		HashMap <AxisItem, Boolean> emptyItems = new HashMap<AxisItem, Boolean>();
		for (int i = 0; i < rowModel.length; ++i) {
			emptyItems.put(rowModel[i][rowModel[i].length - 1], emptyRows.contains(isEmpty));
		}
		
		for (int i = 0; i < rowModel.length; ++i) {
			int col = 0;
			if (emptyRows.contains(i)) {
				for (Integer j: isEmpty.keySet()) {
					int val = isEmpty.get(j);
					if (val != 0) {
//						isEmpty.put(j, val - 1);
						if ((val - 1) == 0) {
							if (lastCell.get(j) != null) {
								lastCell.get(j).setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
							}
						}
					} 
				}
				cellPosition += columnCount;
				continue;
			}
			lastRow.clear();
			for (int j = 0; j < rowModel[i].length; ++j) {
				AxisItem item = rowModel[i][j];				
				if (!isEmpty.containsKey(j)) {
					isEmpty.put(j, 0);
				}
				if (isEmpty.get(j) != 0) {
					isEmpty.put(j, isEmpty.get(j) - 1);
					PdfPCell cell = createHCell("", 1, null, false);
					int bits = Rectangle.LEFT | Rectangle.RIGHT;
					if (isEmpty.get(j) == 0) {
						bits |= Rectangle.BOTTOM;
					}
					cell.setBorder(bits);
					calcColWidth(true, col, cell, "", item.getLevel());
					//table.addCell(cell);
					allCells.add(cell);
					lastCell.put(j, cell);
					lastRow.add(cell);
				} else {
					String tv = item.getName();
					int width = /*item.hasChildren() && item.hasState(AxisItem.EXPANDED) ? 1 :*/ traverseVisible(item, emptyItems);
					tv = getIndentName(tv, item.getElementNode().getDepth());
					PdfPCell cell = createHCell(tv, 1, item, true);
					if (item.hasChildren()) {
//						for (int k = 0; k < item.getElementNode().getDepth(); k++) {
//							tv = tv.substring(INDENT.length());
//						}
//						tv += "    ";
//						System.out.println("Tv == '" + tv + "'");
					}
					calcColWidth(true, col, cell, tv, item.getLevel());
					int bits = Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP;
					if (width == 1 || width == 0) {
						bits |= Rectangle.BOTTOM;
					}
					cell.setBorder(bits);
					allCells.add(cell);
					lastRow.add(cell);
//					table.addCell(cell);					
					if (width > 0) {
						width--;
					}
					isEmpty.put(j, width);
					lastCell.put(j, cell);
					if (firstCell == null) {
						firstCell = cell;
					}
				}				
				col++;
			}
			// draw da cells:
			int start = i * columnCount;
			int end = start + columnCount;
			for (int c = start; c < end; ++c) {
				if (emptyColumns.contains(c - start)) {
					cellPosition++;
					continue;
				}
				String tv = cellValues[cellPosition++].getValue().toString();
				PdfPCell cell = createCell(tv, getFormat(i, (c - start), indexCellMap.get(i
						+ ";" + (c - start))));
				calcColWidth(false, col, cell, tv, -1);
				//table.addCell(cell);
				allCells.add(cell);
				lastRow.add(cell);
				col++;
			}
			rowCount++;
		}
		for (PdfPCell c: lastRow) {
			int bits = Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM;
			if ((c.getBorder() & Rectangle.TOP) != 0) {
				bits |= Rectangle.TOP;
			}
			c.setBorder(bits);
		}
		for (PdfPCell c: allCells) {
			table.addCell(c);
		}
	}
	
	private float calcWidth(int column, PdfPCell cell, String tv) {
		if (cHeight == -1) {
			cHeight = FONT_CELL_H.getSize() + cell.getPaddingBottom() + cell.getPaddingTop()
					+ cell.getBorderWidth();
		}
		final float cm = cell.getBorderWidth() + cell.getPaddingLeft() + cell.getPaddingRight();
		float w = BASE_FONT_CELL_H.getWidthPoint(tv, FONT_CELL_H.getSize());
		w += cm; // + 10;
		return w + cm;	
	}
	
	private void calcColWidth(boolean header, int column, PdfPCell cell, String tv, int rowDepth) {
		String orig = tv;
		int length = tv.length();
		if (header) {			
			if (rowDepth > -1) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < rowDepth; i++) {
					buf.append(INDENT);
				}
				float rhmw = BASE_FONT_CELL_H.getWidthPoint(buf.toString(), FONT_CELL_H.getSize()) + rowHeadersMaxWidth;
				while ((BASE_FONT_CELL_H.getWidthPoint(tv, FONT_CELL_H.getSize())) > rhmw && length > 2) {
					length--;
					tv = orig.substring(0, length) + "...";
				}
			} else {
				while ((BASE_FONT_CELL_H.getWidthPoint(tv, FONT_CELL_H.getSize())) > columsMaxWidth && length > 2) {
					length--;
					tv = orig.substring(0, length) + "...";
				}			
			}
		} 
//		else {
//			if ((BASE_FONT_CELL.getWidthPoint(tv, FONT_CELL.getSize())) > columsMaxWidth) {
//				tv = cellReplacementString;
//			}
//		}
//		if (tv.length() > 20) {
//			tv = tv.substring(0, 18) + "...";
//		}
		if (cHeight == -1) {		
			cHeight = FONT_CELL_H.getSize() + cell.getPaddingBottom() + cell.getPaddingTop();
		}
		final float cm = cell.getBorderWidth() + cell.getPaddingLeft() + cell.getPaddingRight();
		int imWidth = 0;
		if (cell.getPhrase().getChunks().size() >= 2 && config.isShowExpansionStates()) {
			imWidth = 9;
		}
		float w = header ? BASE_FONT_CELL_H.getWidthPoint(tv, FONT_CELL_H.getSize()) + imWidth : BASE_FONT_CELL
				.getWidthPoint(formatNumber(tv, null), FONT_CELL.getSize());
		w += cm; // + 10;
		if (cell.getColspan() == 1 || cell.getColspan() == 0) {
//			System.out.println("    [.] " + tv + ", " + column + " => " + w);			
			if (column < cWidths.length && w > cWidths[column]) {
				cWidths[column] = w;// + cm;
			}
		} else {
			float wPerCol = w / cell.getColspan();			
			for (int i = 0; i < cell.getColspan(); i++) {
//				System.out.println("  <...> " + tv + ", " + (column + i) + " => " + wPerCol);
				if ((column + i) < cWidths.length && wPerCol > cWidths[column + i]) {
					cWidths[column + i] = wPerCol;// + cm;
				}
			}
		}
	}
	
	private int getMaxDimDepth(AxisItem[] roots) {
		int localMax = 0;
		for (AxisItem root : roots) {
			int sum = 1 + getMaxDimDepth(root.getRootsInNextHierarchy());
			if (sum > localMax) {
				localMax = sum;
			}
		}
		return localMax;
	}

	private final int getWidth(AxisItem[] roots) {
		if (roots == null || roots.length == 0) {
			return 0;
		}
		int result = 0;
		int n = roots.length;
		for (int i = 0; i < n; i++) {
			result += getWidth(roots[i].getChildren());
			result += getWidth(roots[i].getRootsInNextHierarchy());
		}
		return result + n;
	}

	private final Format getFormat(int row, int col, Element[] coords) {
		Format f = null;
		if (coords != null) {
			for (Element e : coords) {
				boolean valid = true;
				if (allDimensionLevels.containsKey(e.getDimension())) {
					valid = false;
					for (LevelRangeInfo i : allDimensionLevels.get(e.getDimension())) {
						if (i.level == e.getLevel()) {
							valid = true;
							f = i.format;
							break;
						}
					}
					if (!valid) {
						return null;
					}
				}
			}
			if (f != null) {
				return f;
			}
		}
		for (IndexRangeInfo r : indexRanges) {
			if (row == r.row && col == r.col) {
				return r.format;
			}
		}
		return null;
	}

	private final void applyFormat(Format f, PdfPCell cell) {
		final Phrase phrase = cell.getPhrase();
		// background
		if (f.getBackgroundColor() != null) {
			ColorDescriptor bg = f.getBackgroundColor();
			if (bg != null)
				cell.setBackgroundColor(PDFUtils.convertColor(bg));
		}

		// border
		// TODO: set the line style for each side
		BorderData[] bd = f.getBorderData();
		if (bd != null && bd.length > 0) {
			cell.setCellEvent(new BorderPdfPCellEvent(bd));
		}
		// PdfContentByte pcb = writer.getDirectContent();
		// pcb.saveState();
		// pcb.setColorStroke(Color.BLACK);
		// pcb.moveTo(cell.getLeft(), cell.getTop());
		// pcb.lineTo(cell.getRight(), cell.getBottom());
		// pcb.stroke();
		//		
		// pcb.restoreState();

		// int borderStyle = 0;
		for (int i = 0; i < bd.length; i++) {
			BorderData bData = bd[i];
			switch (bData.getLinePosition()) {
			case BorderData.HORIZONTAL_TOP:
				// borderStyle |= PdfPCell.TOP;
				// cell.setBorderWidthTop(bData.getLineWidth());
				// cell.setBorderColorTop(PDFUtils.convertColor(bData.getLineColor()));
				break;
			case BorderData.HORIZONTAL_BOTTOM:
				// borderStyle |= PdfPCell.BOTTOM;
				// cell.setBorderWidthBottom(bData.getLineWidth());
				// cell.setBorderColorBottom(PDFUtils.convertColor(bData.getLineColor()));
				break;
			case BorderData.VERTICAL_LEFT:
				// borderStyle |= PdfPCell.LEFT;
				// cell.setBorderWidthLeft(bData.getLineWidth());
				// cell.setBorderColorLeft(PDFUtils.convertColor(bData.getLineColor()));
				break;
			case BorderData.VERTICAL_RIGHT:
				// borderStyle |= PdfPCell.RIGHT;
				// cell.setBorderWidthRight(bData.getLineWidth());
				// cell.setBorderColorRight(PDFUtils.convertColor(bData.getLineColor()));
				break;
			}

		}
		// cell.setBorder(borderStyle);

		// fonts
		final Font font = phrase.getFont();
		if (font != null) {
			// color
			if (f.getForegroundColor() != null)
				font.setColor(PDFUtils.convertColor(f.getForegroundColor()));

			final FontDescriptor fDesc = f.getFontData();
			if (fDesc != null) {
				// style
				font.setStyle(PDFUtils.convertFontStyle(fDesc));
				// size
				font.setSize(fDesc.getSize());
				// font family
				// TODO: set font family, embed custom fonts, etc.
			}
		}
	}

	private final String applyNumberFormat(String text) {
		try {
			Double d = Double.parseDouble(text);
			return DEFAULT_FORMATTER.format(d);
		}
		catch (NumberFormatException e) {
			return text;
		}
	}

	private final String formatNumber(Object number, String numberFormat) {
		String result = null;
		try {
			if (numberFormat == null)
				numberFormat = getDefaultNumberFormat();
			try {
				Double d = Double.parseDouble(number.toString());
				DecimalFormat df = new DecimalFormat(numberFormat);
				result = df.format(d);
			} catch (Throwable t) {
				// ignore
			}
		} catch (IllegalArgumentException e) {
			/* ignore */
		}
		if (result == null) {
			result = number.toString();
		}
		if ((BASE_FONT_CELL.getWidthPoint(result, FONT_CELL.getSize())) > columsMaxWidth) {
			result = cellReplacementString;
		}
		return result;
	}

	private static final String getDefaultNumberFormat() {
		return "#,##0.00";
	}
	
	private class PDFPageEventHandler extends PdfPageEventHelper {

		private int lastPage;
		private int extPageCount;

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			cb.saveState();

			// footer

			if (config.isShowPageNumbers()) {
				String text = getPageText(writer);
				Phrase footerPhr = new Phrase(text, FONT_PNUMBER);
				final float sy = document.bottomMargin() / 2;
				float textBase = document.bottom() - sy - 10;
				ColumnText.showTextAligned(cb, com.itextpdf.text.Element.ALIGN_CENTER, footerPhr, (document
						.right() - document.left())
						/ 2 + document.leftMargin(), textBase, 0);

				// bottom line
				cb.setColorStroke(BaseColor.GRAY);
				cb.setLineWidth(0.5f);
				final int ey = (int) (document.bottom() - sy);
				cb.moveTo(30, ey);
				cb.lineTo(document.getPageSize().getWidth() - 30, ey);

				cb.stroke();				
			}
			cb.restoreState();
		}

		private String getPageText(PdfWriter writer) {
			if (!isExtPage) {
				extPageCount = 0;
				return Integer.toString(++lastPage);
			}
			extPageCount++;
			return Integer.toString(lastPage) + NumberUtil.getCharNumber(extPageCount);
		}

	}

	private static class NumberUtil {
		private final static char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

		private NumberUtil() {
		}

		static String getCharNumber(int n) {
			StringBuffer sb = new StringBuffer();
			while (true) {
				int v = n % chars.length;
				n /= chars.length;
				sb.insert(0, getSingleCharNumber(v));
				if (n <= 0)
					break;
			}
			return sb.toString();
		}

		private static char getSingleCharNumber(int n) {
			return chars[n % chars.length];
		}
	}

	private static class IndexRangeInfo {
		int row;
		int col;
		Format format;

		IndexRangeInfo(Format f, int row, int col) {
			this.format = f;
			this.row = row;
			this.col = col;
		}
	}

	private static class LevelRangeInfo {
		int level;
		Format format;

		LevelRangeInfo(Format f, int level) {
			this.level = level;
			this.format = f;
		}
	}

	private static class BorderPdfPCellEvent implements PdfPCellEvent {
		private final static int MARGIN = 1;
		// private HashMap<PdfPCell, BorderData> bMap = new HashMap<PdfPCell, BorderData>();
		private final BorderData[] bData;

		BorderPdfPCellEvent(BorderData[] bData) {
			this.bData = bData;
		}

		public void cellLayout(PdfPCell cell, Rectangle pos, PdfContentByte[] canvases) {
			if (bData == null || bData.length == 0)
				return;

			PdfContentByte cb = canvases[PdfPTable.LINECANVAS]; 
			cb.saveState();
			final float left = pos.getLeft() + MARGIN;
			final float right = pos.getRight() - MARGIN;
			final float bottom = pos.getBottom() + MARGIN;
			final float top = pos.getTop() - MARGIN;

			for (int i = 0; i < bData.length; i++) {
				final BorderData bd = bData[i];
				if (bd == null) {
					cb.setLineWidth(1f);
					cb.setLineDash(1, 0);
					cb.setColorStroke(BaseColor.BLACK);
					continue;
				}
				
				cb.saveState();
				PDFUtils.applyBorderDataLineStyle(cb, bd);
				switch (bd.getLinePosition()) {
				case BorderData.HORIZONTAL_TOP:
					cb.moveTo(left, bottom);
					cb.lineTo(right, bottom);
					cb.stroke();
					break;
				case BorderData.HORIZONTAL_BOTTOM:
					cb.moveTo(left, top);
					cb.lineTo(right, top);
					cb.stroke();
					break;
				case BorderData.VERTICAL_LEFT:
					cb.moveTo(left, bottom);
					cb.lineTo(left, top);
					cb.stroke();
					break;
				case BorderData.VERTICAL_RIGHT:
					cb.moveTo(right, bottom);
					cb.lineTo(right, top);
					cb.stroke();
					break;
				}
				cb.restoreState();
			}

//			cb.stroke();
			cb.closePathStroke();
			cb.restoreState();
		}
	}

	public final static BorderData[] getDummyBorderData() {
		BorderData[] bd = new BorderData[4];
		bd[0] = new BorderData(1, BorderData.LINE_SOLID, new ColorDescriptor(0, 0, 0),
				BorderData.VERTICAL_LEFT);
		bd[1] = new BorderData(1, BorderData.LINE_SOLID, new ColorDescriptor(0, 0, 0),
				BorderData.VERTICAL_RIGHT);
		bd[2] = new BorderData(1, BorderData.LINE_DASH, new ColorDescriptor(255, 0, 0),
				BorderData.HORIZONTAL_TOP);
		bd[3] = new BorderData(1, BorderData.LINE_SOLID, new ColorDescriptor(0, 0, 0),
				BorderData.HORIZONTAL_BOTTOM);
		return bd;

	}
}