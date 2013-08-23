/*
*
* @file XMLAHTMLExporter.java
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
* @version $Id: XMLAHTMLExporter.java,v 1.6 2010/04/12 11:15:09 PhilippBouillon Exp $
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

import org.palo.api.Cube;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.ext.ui.ColorDescriptor;
import org.palo.api.ext.ui.FontDescriptor;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.Property;
import org.palo.viewapi.uimodels.axis.AxisFlatModel;
import org.palo.viewapi.uimodels.axis.AxisItem;
import org.palo.viewapi.uimodels.formats.Format;
import org.palo.viewapi.uimodels.formats.FormatRangeInfo;
import org.palo.viewapi.uimodels.formats.TrafficLightData;

/**
 * <code>XMLAHTMLExporter</code>
 * Exports a XMLA Cube View to an html string.
 * 
 * @author PhilippBouillon
 * @version $Id: XMLAHTMLExporter.java,v 1.6 2010/04/12 11:15:09 PhilippBouillon Exp $
 */
public class XMLAHTMLExporter implements CubeViewExporter {
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
	 * All cell values of the view.
	 */
	private Object [] cellValues;

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
	
	/**
	 * Returns the width (in number of elements) of the column header of the
	 * view.
	 * 
	 * @param roots root items of the view.
	 * @return the width of the column header.
	 */
	private final int getWidth(AxisItem [] roots) {
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
		
	/**
	 * Recursively renders all rows of the view to the html string.
	 * 
	 * @param roots roots of the rows.
	 * @param prefix html prefix of the current row.
	 * @return the html string containing the row.
	 */
	private final String drawRows(AxisItem [] roots, String prefix) {
		if (roots == null || roots.length == 0) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (AxisItem root: roots) {
			String localPrefix = prefix + "<td>" + root.getName() + "</td>";
			if (!root.hasRootsInNextHierarchy()) {				
				result.append(prefix);
				result.append("<td>" + root.getElement().getName() + "</td>");
				for (int i = 0; i < columnCount; i++) {
					String formatted = cellValues[cellPosition++].toString();
					Format f = getFormat(rowCount, i, indexRanges);
					formatted = applyFormat(f, formatted);
					result.append(formatted);
				}
				result.append("</tr>\r\n");
				rowCount++;
			}
			result.append(drawRows(root.getRootsInNextHierarchy(), localPrefix));
			localPrefix = localPrefix.replaceAll("<td>.*?</td>", "<td>&nbsp;</td>");
			result.append(drawRows(root.getChildren(), prefix));
			prefix = prefix.replaceAll("<td>.*?</td>", "<td>&nbsp;</td>");							
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
	 * A method used to get a hierarchy for a dimension.
	 * 
	 * @param d the dimension to get the hierarchy for.
	 * @param dimHier dimension hierarchy map.
	 * @return the hierarchy for the given dimension.
	 * @deprecated this information is stored in the hierarchies now...
	 */
	private final Hierarchy getHier(Dimension d, HashMap <Dimension, Hierarchy> dimHier) {
		Hierarchy hier = dimHier.get(d);
		if (hier == null) {
			hier = d.getDefaultHierarchy();
		}		
		return hier;
	}
	
	/**
	 * Exports the given view to an html string.
	 */
	public final void export(CubeView view) {
		Cube cube = view.getCube();
		
		Property<Object> [] properties = view.getProperties();
		HashMap<Dimension, Hierarchy> dimHier =
			new HashMap<Dimension, Hierarchy>();

		for (Property <Object> prop: properties) {
			if (prop.getId().startsWith("dimensionHierarchy-")) {
				String dimId = prop.getId().substring("dimensionHierarchy-".length());
				Dimension dim = cube.getDimensionById(dimId);
				Hierarchy hier = dim.getHierarchyById(prop.getValue().toString());
				dimHier.put(dim, hier);
			}
		}
		
		Dimension[] cubeDims = cube.getDimensions();		
		HashMap<Hierarchy, Integer> hierIndex = 
			new HashMap<Hierarchy, Integer>();
		HashMap <String, Integer []> cellIndexMap =
			new HashMap <String, Integer []> ();
		for(int i=0;i<cubeDims.length;++i) {
			hierIndex.put(getHier(cubeDims[i], dimHier), i);
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
				// TODO multiple selections...
				Element [] currentlySelectedElements = ah.getSelectedElements(); 
				if (currentlySelectedElements == null || currentlySelectedElements.length == 0) {
					html.append("<td>-</td>");					
				} else {
					html.append("<td>" +ah.getSelectedElements()[0].getName() + "</td>");					
				}
			}
			html.append("</tr></table>\r\n");
		}
						
		//		for (AxisItem root: tree.getRoots()) {
//			System.out.println(root.getElement().getName());
//			for (AxisItem kid: root.getChildren()) {
//				System.out.println();
//			}
//		}
		
		Object rc = view.getPropertyValue(CubeView.PROPERTY_ID_REVERSE_HORIZONTAL_LAYOUT);		
		AxisFlatModel cols = new AxisFlatModel(view.getAxis("cols"), rc != null && Boolean.parseBoolean("" + rc));
		AxisItem[][] colsModel = cols.getModel();
		if (colsModel == null || colsModel.length == 0) {
			html.append("</body></html>\r\n");
			return;
		}

		Object rr = view.getPropertyValue(CubeView.PROPERTY_ID_REVERSE_VERTICAL_LAYOUT);
		AxisFlatModel rows= new AxisFlatModel(view.getAxis("rows"), AxisFlatModel.VERTICAL, rr != null && Boolean.parseBoolean("" + rr));
		AxisItem[][] rowsModel = rows.getModel();				
		
		
		html.append("<table border=\"1\">");
		int emptyCellRowCount = getMaxDimDepth(rowsModel[0]);

		for (AxisItem [] items: colsModel) {
			html.append("<tr>");
			for (int i = 0; i < emptyCellRowCount; i++) {
				html.append("<td>&nbsp;</td>");
			}
			for (AxisItem item: items) {
				html.append("<td>" + item.getElement().getName() + "</td>");
				int emptyCellCount = getWidth(item.getRootsInNextHierarchy()) - 1;
				for (int i = 0; i < emptyCellCount; i++) {
					html.append("<td>&nbsp;</td>");
				}
			}
			html.append("</tr>\r\n");
		}
		
		if (rowsModel == null || rowsModel.length == 0) {
			html.append("</table>\r\n</body></html>\r\n");
			return;
		}
			
		int cellIndex = 0;
		int rowLeaf = rowsModel.length - 1;
		int colLeaf = colsModel.length - 1;
		int cells = rowsModel[rowLeaf].length * colsModel[colLeaf].length;		
		Element[][] cellCoords = new Element[cells][];
		for (int r = 0; r < rowsModel[rowLeaf].length; ++r) {
			for (int c = 0; c < colsModel[colLeaf].length; ++c) {				
				cellCoords[cellIndex] = coord.clone();			
				//fill row coordinates:
				fill(cellCoords[cellIndex], rowsModel[rowLeaf][r], hierIndex);
				//and finally the col coordinates:
				fill(cellCoords[cellIndex], colsModel[colLeaf][c], hierIndex);
				cellIndexMap.put(getString(cellCoords[cellIndex]), 
						new Integer [] {r, c});
				cellIndex++;
			}
		}
				
		int size = 0;
		for (Format f: view.getFormats()) {
			size += f.getRangeCount();
		}		

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
					// TODO
				}
			}
		}
		indexRanges = allCoordinates.toArray(new IndexRangeInfo[0]);
		
		cellValues = cube.getDataBulk(cellCoords);
		cellPosition = 0;
		columnCount = colsModel[colLeaf].length;
		
		html.append(drawRows(rows.getRoots(), ""));		
		html.append("</table>\r\n</body></html>\r\n");
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

		coord[hierIndex.get(item.getHierarchy())] = item.getElement();
		fill(coord, item.getParentInPrevHierarchy(), hierIndex);
	}
			
	/**
	 * Returns the format for a given row and column in the view.
	 * 
	 * @param row the row of the cell the user is interested in.
	 * @param col the column of the cell the user is interested in.
	 * @param ranges the range-index map.
	 * @return the format for that cell or null if no format was applied.
	 */
	private final Format getFormat(int row, int col, IndexRangeInfo [] ranges) {
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
