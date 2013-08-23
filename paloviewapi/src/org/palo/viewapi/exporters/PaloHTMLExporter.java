/*
*
* @file PaloHTMLExporter.java
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
* @author PhilippBouillon
*
* @version $Id: PaloHTMLExporter.java,v 1.6 2010/04/12 11:15:09 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.exporters;

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
import org.palo.viewapi.uimodels.formats.Format;
import org.palo.viewapi.uimodels.formats.FormatRangeInfo;
import org.palo.viewapi.uimodels.formats.TrafficLightData;

/**
 * 
 * <code>PaloHTMLExporter</code>
 * Exports a Palo Cube View to an html string.
 * 
 * @author PhilippBouillon
 * @version $Id: PaloHTMLExporter.java,v 1.6 2010/04/12 11:15:09 PhilippBouillon Exp $
 */
public class PaloHTMLExporter implements CubeViewExporter {
	/**
	 * Decimal format to display numbers in html if not specially formatted.
	 */
	private static final DecimalFormat DEFAULT_FORMATTER = 
		new DecimalFormat("#,##0.00");
	
	/**
	 * Custom decimal formatter to display numbers in html.
	 */
	private DecimalFormat customFormatter = new DecimalFormat("#,##0.00");
	
	/**
	 * Helper class to match formats to row and column indices. 
	 */
	class IndexRangeInfo {
		/**
		 * Row that is affected by this format.
		 */
		int row;
		
		/**
		 * Column that is affected by this format.
		 */
		int col;
		
		/**
		 * The format for the row/column.
		 */
		Format format;
		
		/**
		 * Creates a new IndexRangeInfo for a format that affets the cell at
		 * row/col.
		 * 
		 * @param f the format that is to be applied to the cell.
		 * @param row row of the cell.
		 * @param col col of the cell.
		 */
		IndexRangeInfo(Format f, int row, int col) {
			this.format = f;
			this.row = row;
			this.col = col;
		}
	}
	
	/**
	 * Helper class to match formats to levels of a dimension. 
	 */
	class LevelRangeInfo {
		/**
		 * The affected level.
		 */
		int level;
		
		/**
		 * The format to be applied. 
		 */
		Format format;
		
		/**
		 * Creates a new LevelRangeInfo that indicates a level and a format to
		 * be applied to that level.
		 * 
		 * @param f the format to apply.
		 * @param level the level to apply the format to.
		 */
		LevelRangeInfo(Format f, int level) {
			this.level = level;
			this.format = f;
		}
	}
		
	/**
	 * All cell values of the view.
	 */
	private Cell [] cellValues;
	
	/**
	 * Current cell position.
	 */
	private int cellPosition = 0;
	
	/**
	 * Number of columns in the view.
	 */
	private int columnCount = 0;
	
	/**
	 * Number of rows in the view.
	 */
	private int rowCount = 0;
	
	/**
	 * Matches indices of cells to ranges for formats.
	 */
	private IndexRangeInfo [] indexRanges;
	
	/**
	 * Matches all dimensions to possible formatted levels.
	 */
	private HashMap <Dimension, ArrayList <LevelRangeInfo>> allDimensionLevels;
	
	/**
	 * Matches String coordinates to Element coordinates.
	 */
	private HashMap <String, Element []> indexCellMap =
		new HashMap <String, Element []> ();
	
	/**
	 * The html string.
	 */	
	private final StringBuffer html = new StringBuffer();
	
	/**
	 * Returns the html string of the exporter.
	 * @return the html string of the exporter.
	 */
	public final String getHTML() {
		return html.toString();
	}
	
	/**
	 * Transforms element coordinates into a string.
	 * 
	 * @param coord the coordinate to be transformed.
	 * @return the transformed element coordinates as a string.
	 */
	private final String getString(Element [] coord) {
		StringBuffer b = new StringBuffer();
		for (Element e: coord) {
			b.append(e.getId() + ",");
		}
		return b.toString();
	}
	
	public int traverseVisible(AxisItem item) {		
		ArrayList <Integer> result = new ArrayList<Integer>();
		result.add(0);		
		traverseVisible(item, null, null, result, true);		
		return result.get(0);
	}
	
	public void traverseVisible(AxisItem item, AxisItem parent, AxisItem parentInPrevHierarchy, ArrayList <Integer> result, boolean start) {
		if(!item.hasRootsInNextHierarchy()) {
			result.set(0, result.get(0) + 1);
		}
		for(AxisItem rootInNextHierarchy : item.getRootsInNextHierarchy()) {
			traverseVisible(rootInNextHierarchy, null, item, result, false);
		}
		if (!start) {
			if (item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
				for (AxisItem child : item.getChildren()) {
					traverseVisible(child, item, parentInPrevHierarchy, result, false);
				}
			}
		}
	}
			
	private final String indentItem(AxisItem it) {
		Element e = it.getElement();
		int indentDepth = e.getDepth();
		if (indentDepth == 0) {
			return e.getName();
		} 
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indentDepth; i++) {
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		sb.append(it.getName());
		return sb.toString();
	}
		
	private final String drawRows(AxisItem [][] rowModel, AxisItem [][] colModel, ArrayList <Integer> emptyColumns, ArrayList <Integer> emptyRows) {
		StringBuffer result = new StringBuffer();
		HashMap <Integer, Integer> isEmpty = new HashMap<Integer, Integer>();
		for (int i = 0; i < rowModel.length; ++i) {
			if (emptyRows.contains(i)) {
				for (Integer j: isEmpty.keySet()) {
					int val = isEmpty.get(j);
					if (val != 0) {
						isEmpty.put(j, val - 1);	
					} 
				}
				cellPosition += columnCount;
				continue;
			}
			result.append("<tr>");
			for (int j = 0; j < rowModel[i].length; ++j) {
				AxisItem item = rowModel[i][j];				
				if (!isEmpty.containsKey(j)) {
					isEmpty.put(j, 0);
				}
				if (isEmpty.get(j) != 0) {
					isEmpty.put(j, isEmpty.get(j) - 1);
					result.append("<td>&nbsp;</td>");
				} else {
					result.append("<td>" + indentItem(item) + "</td>");
					int width = traverseVisible(item);
					if (width > 0) {
						width--;
					}
					isEmpty.put(j, width);
				}				
				
			}
			// draw da cells:
			int start = i * columnCount;
			int end = start + columnCount;
			for (int c = start; c < end; ++c) {
				if (emptyColumns.contains(c - start)) {
					cellPosition++;
					continue;
				}
				String formatted = cellValues[cellPosition++].getValue().toString();
				Format f = getFormat(rowCount, i, indexRanges,
						indexCellMap.get(rowCount + ";" + i));
				formatted = applyFormat(f, formatted);
				result.append(formatted);
			}
			result.append("</tr>\r\n");			
		}
		return result.toString();
	}
	
	/**
	 * Returns the maximum depth of the specified roots.
	 * @param roots the model root elements.
	 * @return the maximum depth of the specified roots.
	 */
	private int getMaxDimDepth(AxisItem [] roots) {
		int localMax = 0;
		for (AxisItem root: roots) {
			int sum = 1 + getMaxDimDepth(root.getRootsInNextHierarchy());
			if (sum > localMax) {
				localMax = sum;
			}
		}
		return localMax;
	}
		
	/**
	 * Exports the given view to an html string.
	 */
	public final void export(CubeView view) {
		Cube cube = view.getCube();
						
		Dimension[] cubeDims = cube.getDimensions();		
		HashMap<Hierarchy, Integer> hierIndex =
			new HashMap<Hierarchy, Integer>();
		HashMap <String, Integer []> cellIndexMap =
			new HashMap <String, Integer []> ();
		for(int i=0;i<cubeDims.length;++i) {
			hierIndex.put(cubeDims[i].getDefaultHierarchy(), i);
		}
						
		Element[] coord = new Element[cubeDims.length];
		//selected elements:
		Axis selAxis = view.getAxis("selected");
		for (int i = 0; i < cubeDims.length; i++) {
			AxisHierarchy axisHierarchy = selAxis.getAxisHierarchy(cubeDims[i]
					.getDefaultHierarchy());
			if (axisHierarchy != null) {
				Element[] selElements = axisHierarchy.getSelectedElements();
				// palo can use only the first selected element:
				if (selElements != null && selElements.length > 0) {
					coord[i] = selElements[0];
				}
			}
		}
		html.append("<html><body>\r\n");
		AxisHierarchy[] axisHierarchies = selAxis.getAxisHierarchies();
		if (axisHierarchies.length != 0) {
			html.append("<table border=\"1\"><tr>");
			for (AxisHierarchy ah : axisHierarchies) {
				html.append("<td>" + ah.getHierarchy().getName() + "</td>");
			}
			html.append("</tr>\r\n<tr>");
			for (AxisHierarchy ah : axisHierarchies) {
				html.append("<td>" + ah.getSelectedElements()[0].getName() + "</td>");
			}
			html.append("</tr></table>\r\n");
		}
				
		Object rc = view.getPropertyValue(CubeView.PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT);		
		AxisFlatModel cols = new AxisFlatModel(view.getAxis("cols"), rc != null && Boolean.parseBoolean("" + rc));
		AxisItem [][] colModel = cols.getModel();
		if (colModel == null || colModel.length == 0) {
			html.append("</body></html>\r\n");
			return;
		}
		Object rr = view.getPropertyValue(CubeView.PROPERTY_ID_REVERSE_VERTICAL_LAYOUT);
		AxisFlatModel rows= new AxisFlatModel(view.getAxis("rows"), AxisFlatModel.VERTICAL, rr != null && Boolean.parseBoolean("" + rr));
		AxisItem[][] rowModel = rows.getModel();				
		
		if (rowModel == null || rowModel.length == 0) {
			html.append("<table border=\"1\">");
			int emptyCellRowCount = getMaxDimDepth(rowModel[0]);
			
			for (AxisItem [] items: colModel) {
				html.append("<tr>");
				for (int i = 0; i < emptyCellRowCount; i++) {
					html.append("<td>&nbsp;</td>");
				}
				for (AxisItem item: items) {
					html.append("<td>" + item.getElement().getName() + "</td>");
					int emptyCellCount = traverseVisible(item) - 1; 
					//					getWidth(item.getRootsInNextHierarchy(), true) - 1;
					for (int i = 0; i < emptyCellCount; i++) {
						html.append("<td>&nbsp;</td>");
					}
				}
				html.append("</tr>\r\n");
			}
		
			html.append("</table>\r\n</body></html>\r\n");
			return;
		}
			
		int cellIndex = 0;
		int rowLeaf = rowModel[0].length - 1;
		int colLeaf = colModel.length - 1;
		int cells = rowModel.length * colModel[colLeaf].length;		
		Element[][] cellCoords = new Element[cells][];

		for (int r = 0; r < rowModel.length; ++r) {
			for (int c = 0; c < colModel[colLeaf].length; ++c) {				
				cellCoords[cellIndex] = coord.clone();			
				//fill row coordinates:
				fill(cellCoords[cellIndex], rowModel[r][rowLeaf], hierIndex);
				//and finally the col coordinates:
				fill(cellCoords[cellIndex], colModel[colLeaf][c], hierIndex);
				cellIndexMap.put(getString(cellCoords[cellIndex]), 
						new Integer [] {r, c});
				indexCellMap.put(r + ";" + c, cellCoords[cellIndex]);
				cellIndex++;
			}
		}
		
		cellValues = cube.getCells(cellCoords);
		Object hz = view.getPropertyValue(CubeView.PROPERTY_ID_HIDE_EMPTY);
		ArrayList <Integer> emptyColumns = new ArrayList<Integer>();
		ArrayList <Integer> emptyRows = new ArrayList<Integer>();
		if (hz != null && Boolean.parseBoolean(hz.toString())) {
			cellIndex = 0;
			boolean [] hasRowValue = new boolean[colModel[colLeaf].length];
			for (int c = 0; c < colModel[colLeaf].length; ++c) {
				hasRowValue[c] = false;
			}
			for (int r = 0; r < rowModel.length; ++r) {
				boolean hasColValues = false;
				for (int c = 0; c < colModel[colLeaf].length; ++c) {
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
			for (int c = 0; c < colModel[colLeaf].length; ++c) {
				if (!hasRowValue[c]) {
					emptyColumns.add(c);
				}
			}
		}
		
		int size = 0;
		for (Format f: view.getFormats()) {
			size += f.getRangeCount();
		}		
		allDimensionLevels = new HashMap<Dimension, ArrayList<LevelRangeInfo>>();
		ArrayList <IndexRangeInfo> allCoordinates = new ArrayList<IndexRangeInfo>();
		for (Format f: view.getFormats()) {
			for (FormatRangeInfo r: f.getRanges()) {
				if (r.getCells() != null) {
					for (Element [] e: r.getCells()) {
						Integer [] rcCoord = cellIndexMap.get(getString(e));
						if (rcCoord != null) {
							allCoordinates.add(new IndexRangeInfo(f, rcCoord[0], rcCoord[1]));
						}				 
					}
				} else {
					for (Dimension d: r.getDimensions()) {						
						ArrayList <LevelRangeInfo> l;
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
		indexRanges = allCoordinates.toArray(new IndexRangeInfo[0]);
		
		
		cellPosition = 0;
		columnCount = colModel[colLeaf].length;
		
		html.append(drawCols(rowModel, colModel, emptyColumns));
		html.append(drawRows(rowModel, colModel, emptyColumns, emptyRows));		
		html.append("</table>\r\n</body></html>\r\n");
	}

	private final String drawCols(AxisItem [][] rowModel, AxisItem [][] colModel, ArrayList <Integer> emptyColumns) {
		int colLeaf = colModel.length - 1;

		StringBuffer result = new StringBuffer();
		result.append("<table border=\"1\">");

		int emptyCellRowCount = getMaxDimDepth(rowModel[0]);
		StringBuffer [] header = new StringBuffer[colModel.length];
		for (int i = 0; i < colModel.length; i++) {
			header[i] = new StringBuffer();			
			header[i].append("<tr>");
			for (int j = 0; j < emptyCellRowCount; j++) {
				header[i].append("<td>&nbsp;</td>");
			}
		}

		HashMap <Integer, Integer> isEmpty = new HashMap<Integer, Integer>();
		for (int c = 0; c < colModel[colLeaf].length; ++c) {
			if (emptyColumns.contains(c)) {
				for (Integer i: isEmpty.keySet()) {
					int val = isEmpty.get(i);
					if (val != 0) {
						isEmpty.put(i, val - 1);	
					} 
				}
				continue;
			}
			AxisItem origItem = colModel[colLeaf][c];
			for (int j = 0; j < colModel.length; ++j) {
				//AxisItem item = colModel[j][c];
				AxisItem item = origItem;
				int counter = colLeaf;
				while (j < counter) {
					item = item.getParentInPrevHierarchy();
					counter--;
				}
				if (!isEmpty.containsKey(j)) {
					isEmpty.put(j, 0);
				}
				if (isEmpty.get(j) != 0) {
					isEmpty.put(j, isEmpty.get(j) - 1);
					header[j].append("<td>&nbsp;</td>");
				} else {
					header[j].append("<td>" + item.getName() + "</td>");
					int width = traverseVisible(item);
					if (width > 0) {
						width--;
					}
					isEmpty.put(j, width);
				}								
			}			
		}
		
		for (int i = 0; i < colModel.length; i++) {
			result.append(header[i] + "</tr>\r\n");			
		}
		
		return result.toString();
	}
	
	/**
	 * Applies the number format of the given format (if any) to the given
	 * text.
	 * 
	 * @param f format to apply.
	 * @param text text to apply the format to.
	 * @return the modified text.
	 */
	private final String applyNumberFormat(Format f, String text) {
		if (f == null || f.getNumberFormat() == null) {
			try {
				Double d = Double.parseDouble(text);
				return DEFAULT_FORMATTER.format(d);
			} catch (NumberFormatException e) {
				return text;
			}
		} else {
			customFormatter.applyPattern(f.getNumberFormat());
			try {
				Double d = Double.parseDouble(text);
				return customFormatter.format(d);
			} catch (NumberFormatException e) {
				return text;
			}
			
		}		
	}
	
	/**
	 * Transforms the given color to an html color specifier.
	 * 
	 * @param desc the color to set.
	 * @return html color string.
	 */
	private final String getHTMLColor(ColorDescriptor desc) {
		String red = Integer.toHexString(desc.getRed());
		String green = Integer.toHexString(desc.getGreen());
		String blue = Integer.toHexString(desc.getBlue());
		if (red.length() < 2)  red = "0" + red;
		if (green.length() < 2)  green = "0" + green;
		if (blue.length() < 2)  blue = "0" + blue;
		return "#" + red + green + blue;
	}
	
	/**
	 * Applies the given font and color to the specified text.
	 * 
	 * @param fd font to apply.
	 * @param fg color to apply.
	 * @param text text to apply the font and color to.
	 * @return the modified text.
	 */
	private final String applyFontAndColor(FontDescriptor fd, ColorDescriptor fg, String text) {
		if (text.equals("&nbsp;")) {
			return text;
		}
		String colorString = "";
		String fontFaceString = "";
		if (fg != null) {
			colorString = "color=\"" + getHTMLColor(fg) + "\"";
		}
		if (fd != null) {
			fontFaceString = "face=\"" + fd.getName() + "\"";
			if (fd.isBold()) {
				text = "<b>" + text + "</b>";
			}
			if (fd.isItalic()) {
				text = "<i>" + text + "</i>";
			}
			if (fd.isUnderlined()) {
				text = "<u>" + text + "</u>";
			}
		}
		if (fontFaceString.length() != 0 || colorString.length() != 0) {
			text = "<font " + fontFaceString + " " + colorString + ">" + text +
			"</font>";
		}
		return text;
	}
	
	/**
	 * Applies the traffic light data to the html string (by analyzing the value
	 * and setting the colors accordingly).
	 * 
	 * @param traffic traffic light data to apply.
	 * @param text text to apply the traffic light data to.
	 * @param originalText unformatted original text (to interpret the value).
	 * @return an array of a boolean value and a String. The boolean value
	 * indicates if the application was successful (true in that case) and the
	 * String contains the html code of the transformation, if it has been
	 * successful.
	 */
	private final Object [] applyTrafficLightData(TrafficLightData traffic, String text, String originalText) {
		try {
			Double d = Double.parseDouble(originalText);
			for (int i = 0; i < traffic.getSize(); i++) {
				if (traffic.getMinValueAt(i) <= d && d <= traffic.getMaxValueAt(i)) {
					ColorDescriptor bg = traffic.getBackgroundColorAt(i);
					ColorDescriptor fg = traffic.getForegroundColorAt(i);
					FontDescriptor  fd = traffic.getFontAt(i);
					text = applyFontAndColor(fd, fg, text);
					String bgColorString = "";
					if (bg != null) {
						bgColorString = "bgColor=\"" + getHTMLColor(bg) + "\"";
					}
					return new Object []{true, "<td align=\"right\" " + bgColorString + ">" + text + "</td>"};					
				}
			}
		} catch (NumberFormatException e) {
			return new Object [] {false, null};
		}
		return new Object [] {false, null};
	}
	
	/**
	 * Applies the given format to the specified text.
	 * 
	 * @param f format to apply.
	 * @param text text to apply the format to.
	 * @return the modified text.
	 */
	private final String applyFormat(Format f, String text) {
		String originalText = text;
		if (text.length() == 0) {
			text = "&nbsp;";
		}
		text = applyNumberFormat(f, text);
		if (f == null) {
			String align = "";
			try {
				Double.parseDouble(originalText);
				align = "align=\"right\"";
			} catch (NumberFormatException e) {				
			}
			return "<td " + align + ">" + text + "</td>";
		}
		Object [] result = new Object[2];
		result[0] = false;
		if (f.getTrafficLightData() != null) {
			result = applyTrafficLightData(f.getTrafficLightData(), text, originalText);
		}
		if (!((Boolean) result[0])) {
			// Apply font
			text = applyFontAndColor(f.getFontData(), f.getForegroundColor(), text);

			String bgColorString = "";
			if (f.getBackgroundColor() != null) {
				ColorDescriptor bg = f.getBackgroundColor();
				bgColorString = "bgColor=\"" + getHTMLColor(bg) + "\"";
			}
			String align = "";
			try {
				Double.parseDouble(originalText);
				align = "align=\"right\"";
			} catch (NumberFormatException e) {				
			}			
			return "<td " + align + " " + bgColorString + ">" + text + "</td>";
		}
		return result[1].toString();
	}
	
	/**
	 * Fills all hierarchies of a cube with coordinate information to complete
	 * a full coordinate into the cube.
	 * 
	 * @param coord the coordinate to be filled.
	 * @param item the current AxisItem that is to be put into a coordinate.
	 * @param hierIndex the hierarchy-index map.
	 */
	private final void fill(Element[] coord, AxisItem item,
			HashMap<Hierarchy, Integer> hierIndex) {
		if(item == null)
			return;

//		coord[dimIndex.get(item.getDimension())] = item.getElement();
//		fill(coord, item.getParentInPrevDim(), dimIndex);
		coord[hierIndex.get(item.getHierarchy())] = item.getElement();
		fill(coord, item.getParentInPrevHierarchy(), hierIndex);
	}
			
	/**
	 * Returns the format for a given row and column in the view.
	 * 
	 * @param row the row of the cell the user is interested in.
	 * @param col the column of the cell the user is interested in.
	 * @param ranges the range-index map.
	 * @param coords element coordinates of the given cell.
	 * @return the format for that cell or null if no format was applied.
	 */
	private final Format getFormat(int row, int col, IndexRangeInfo [] ranges, Element [] coords) {
		Format f = null;
		if (coords != null) {
			for (Element e: coords) {
				boolean valid = true;
				if (allDimensionLevels.containsKey(e.getDimension())) {
					valid = false;
					for (LevelRangeInfo i: allDimensionLevels.get(e.getDimension())) {
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
		for (IndexRangeInfo r: ranges) {
			if (row == r.row && col == r.col) {				
				return r.format;
			}
//			if (col >= r.fromCol && col <= r.toCol) {
//				if (row >= r.fromRow && row <= r.toRow) {
//					return r.format;
//				}
//			}
		}
		return null;
	}
}
