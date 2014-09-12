/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.pivot4j.ui;

import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.olap4j.Axis;
import org.olap4j.OlapException;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.PlaceMembersOnAxes;
import com.eyeq.pivot4j.ui.CellType;
import com.eyeq.pivot4j.ui.RenderContext;
import com.eyeq.pivot4j.ui.command.CellCommand;
import com.eyeq.pivot4j.ui.command.CellParameters;
import com.eyeq.pivot4j.ui.command.DrillDownCommand;
import com.eyeq.pivot4j.ui.html.HtmlRenderer;
import com.eyeq.pivot4j.ui.property.PropertySupport;
import com.eyeq.pivot4j.util.CssWriter;

public class WhatIfHTMLRenderer extends HtmlRenderer {

	private boolean measureOnRows;
	// cache that maps the row/column number and the name of the measure of that
	// row/column (row if measures stay in the rows, column otherwise)
	private Map<Integer, String> positionMeasureMap;
	private boolean initialized = false;

	public static transient Logger logger = Logger.getLogger(HtmlRenderer.class);

	@Override
	public void render(PivotModel model) {
		super.render(model);
		initialized = false;
	}

	public WhatIfHTMLRenderer(Writer writer) {
		super(writer);
	}

	@Override
	public void startCell(RenderContext context, List<CellCommand<?>> commands) {
		boolean header;

		switch (context.getCellType()) {
		case Header:
		case Title:
		case None:
			header = true;
			break;
		default:
			header = false;
			break;
		}

		String name = header ? "th" : "td";

		getWriter().startElement(name, getCellAttributes(context));

		if (commands != null && !commands.isEmpty()) {

			startCommand(context, commands);
		}
	}

	@Override
	protected Map<String, String> getCellAttributes(RenderContext context) {
		String styleClass = null;

		StringWriter writer = new StringWriter();
		CssWriter cssWriter = new CssWriter(writer);

		switch (context.getCellType()) {
		case Header:
			if (context.getAxis() == Axis.COLUMNS) {
				styleClass = getColumnHeaderStyleClass();
			} else {
				styleClass = getRowHeaderStyleClass();

				if (getRowHeaderLevelPadding() > 0) {

					int padding = getRowHeaderLevelPadding()
							* context.getMember().getDepth();

					cssWriter.writeStyle("padding-left", padding + "px");
				}
			}
			break;
		case Title:
		case Aggregation:

			if (context.getAxis() == Axis.COLUMNS) {
				styleClass = getColumnTitleStyleClass();
			} else if (context.getAxis() == Axis.ROWS) {
				styleClass = getRowTitleStyleClass();
			}
			break;
		case Value:
			styleClass = getCellStyleClass();
			break;
		case None:
			styleClass = getCornerStyleClass();

			break;
		default:
			assert false;
		}

		Map<String, String> attributes = new TreeMap<String, String>();

		PropertySupport properties = getProperties(context);

		if (properties != null) {
			cssWriter.writeStyle("color",
					getPropertyValue("fgColor", properties, context));

			String bgColor = getPropertyValue("bgColor", properties, context);
			if (bgColor != null) {
				cssWriter.writeStyle("background-color", bgColor);
				cssWriter.writeStyle("background-image", "none");
			}

			cssWriter.writeStyle("font-family",
					getPropertyValue("fontFamily", properties, context));
			cssWriter.writeStyle("font-size",
					getPropertyValue("fontSize", properties, context));

			String fontStyle = getPropertyValue("fontStyle", properties,
					context);
			if (fontStyle != null) {
				if (fontStyle.contains("bold")) {
					cssWriter.writeStyle("font-weight", "bold");
				}

				if (fontStyle.contains("italic")) {
					cssWriter.writeStyle("font-style", "oblique");
				}
			}

			String styleClassValue = getPropertyValue("styleClass", properties,
					context);

			if (styleClassValue != null) {
				if (styleClass == null) {
					styleClass = styleClassValue;
				} else {
					styleClass += " " + styleClassValue;
				}
			}
		}

		if (styleClass != null) {
			// adds the proper style (depending if it was collapsed or expanded)
			if (context.getMember() != null && context.getMember().getMemberType() != null && !context.getMember().getMemberType().name().equalsIgnoreCase("Value")) {

				try {
					int childrenNum = context.getMember().getChildMemberCount();

					if (childrenNum > 0) {
						if (getEnableRowDrillDown() || getEnableColumnDrillDown()) {
							styleClass += " " + "collapsed";
						} else {
							styleClass += " " + "expanded";
						}
					}
				} catch (OlapException e) {
					logger.error(e);
				}
			} else if (context.getCellType() == CellType.Title) {
				styleClass = "dimension-title";
			}
			attributes.put("class", styleClass);
		}
		if (context.getCellType() == CellType.Value) {

			initializeInternal(context);

			// need the name of the measure to check if it's editable
			String measureName = getMeasureName(context);
			// attributes.put("contentEditable", "true");
			int colId = context.getColumnIndex();
			int rowId = context.getRowIndex();
			int positionId = context.getCell().getOrdinal();
			// String memberUniqueName = context.getMember().getUniqueName();
			String id = positionId + "!" + rowId + "!" + colId + "!" + System.currentTimeMillis() % 1000;
			attributes.put("ondblclick", "javascript:Sbi.olap.eventManager.makeEditable('" + id + "','" + measureName + "')");
			attributes.put("id", id);
		} else if (context.getCellType() == CellType.Header) {
			String uniqueName = context.getMember().getUniqueName();
			int axis = context.getAxis().axisOrdinal();
			attributes.put("ondblclick", "javascript:Sbi.olap.eventManager.setCalculatedFieldParent('" + uniqueName + "','" + axis + "')");

		}

		writer.flush();
		IOUtils.closeQuietly(writer);

		String style = writer.toString();

		if (StringUtils.isNotEmpty(style)) {
			attributes.put("style", style);
		}

		if (context.getColumnSpan() > 1) {
			attributes
					.put("colspan", Integer.toString(context.getColumnSpan()));
		}

		if (context.getRowSpan() > 1) {
			attributes.put("rowspan", Integer.toString(context.getRowSpan()));
		}

		return attributes;
	}

	private String getMeasureName(RenderContext context) {
		int coordinate;
		if (this.measureOnRows) {
			coordinate = context.getRowIndex();
		} else {
			coordinate = context.getColumnIndex();
		}
		String measureName = this.positionMeasureMap.get(coordinate);

		if (measureName == null) {
			measureName = ((SpagoBICellWrapper) context.getCell()).getMeasureName();
			this.positionMeasureMap.put(coordinate, measureName);
		}

		return measureName;

	}

	private void initializeInternal(RenderContext context) {
		if (!this.initialized) {
			this.measureOnRows = true;
			this.initialized = true;
			this.positionMeasureMap = new HashMap<Integer, String>();

			// check if the measures are in the rows or in the columns
			List<Member> columnMembers = context.getColumnPosition().getMembers();
			try {
				if (columnMembers != null) {
					for (int i = 0; i < columnMembers.size(); i++) {
						Member member = columnMembers.get(i);
						if (member.getDimension().getDimensionType().equals(Dimension.Type.MEASURE)) {
							this.measureOnRows = false;
						}
					}
				}
			} catch (OlapException e) {
				throw new SpagoBIEngineRuntimeException("Erro getting the measure of a rendered cell ", e);
			}
		}
	}

	@Override
	public void cellContent(RenderContext context, String label) {

		String link = null;

		PropertySupport properties = getProperties(context);

		if (properties != null) {
			link = getPropertyValue("link", properties, context);
		}

		if (link == null) {
			Map<String, String> attributes = new TreeMap<String, String>();
			String drillMode = this.getDrillDownMode();
			if (context.getMember() != null && context.getMember().getMemberType() != null && !context.getMember().getMemberType().name().equalsIgnoreCase("Measure")) {

				List<CellCommand<?>> commands = getCommands(context);

				if (commands != null && !commands.isEmpty()) {
					for (CellCommand<?> command : commands) {
						String cmd = command.getName();

						// /spagobi whatif engine

						int colIdx = context.getColumnIndex();
						int rowIdx = context.getRowIndex();

						int axis = 0;
						if (context.getAxis() != null) {
							axis = context.getAxis().axisOrdinal();
						}
						int memb = 0;
						if (context.getPosition() != null) {
							memb = context.getPosition().getOrdinal();
						}
						int pos = 0;
						if (context.getAxis() == Axis.COLUMNS) {
							pos = rowIdx;
						} else {
							pos = colIdx;
						}
						if (cmd != null) {
							CellParameters parameters = command.createParameters(context);

							if ((cmd.equalsIgnoreCase("collapsePosition") || cmd.equalsIgnoreCase("drillUp") || cmd.equalsIgnoreCase("collapseMember")) &&
									(!drillMode.equals(DrillDownCommand.MODE_REPLACE))) {
								attributes.put("src", "../img/minus.gif");
								attributes.put("onClick", "javascript:Sbi.olap.eventManager.drillUp(" + axis + " , " + pos + " , " + memb + ")");
								getWriter().startElement("img", attributes);
								getWriter().endElement("img");
							} else if ((cmd.equalsIgnoreCase("expandPosition") || cmd.equalsIgnoreCase("drillDown") || cmd.equalsIgnoreCase("expandMember"))) {
								attributes.put("src", "../img/plus.gif");
								attributes.put("onClick", "javascript:Sbi.olap.eventManager.drillDown(" + axis + " , " + pos + " , " + memb + ")");
								getWriter().startElement("img", attributes);
								getWriter().endElement("img");
							}
						}

					}
				} else {
					if (context.getAxis() == Axis.ROWS) {
						// adding a transparent image to get a higher
						// indentation on rows headers
						attributes.put("src", "../img/nodrill.png");
						attributes.put("style", "padding : 2px");
						getWriter().startElement("img", attributes);
						getWriter().endElement("img");
					}
				}

			}

			if ((context.getCellType() == CellType.Title) && !label.equalsIgnoreCase("Measures")) {
				// /spagobi whatif engine

				int colIdx = context.getColumnIndex();
				int rowIdx = context.getRowIndex();

				int axis = 0;
				if (context.getAxis() != null) {
					axis = context.getAxis().axisOrdinal();
				}
				int memb = 0;
				if (context.getPosition() != null) {
					memb = context.getPosition().getOrdinal();
				}
				int pos = 0;
				if (context.getAxis() == Axis.COLUMNS) {
					pos = rowIdx;
				} else {
					pos = colIdx;
				}

				if (drillMode.equals(DrillDownCommand.MODE_REPLACE) && !this.getShowParentMembers()) {
					Hierarchy h = context.getHierarchy();
					PlaceMembersOnAxes pm = context.getModel().getTransform(PlaceMembersOnAxes.class);
					// PlaceHierarchiesOnAxes ph =
					// context.getModel().getTransform(PlaceHierarchiesOnAxes.class);
					List<Member> visibleMembers = pm.findVisibleMembers(h);
					int d = 0;
					for (Member m : visibleMembers) {
						Level l = m.getLevel();
						d = l.getDepth();
						if (d != 0) {
							break;
						}
					}
					if (d != 0) {
						attributes.put("src", "../img/arrow-up.png");
						attributes.put("onClick", "javascript:Sbi.olap.eventManager.drillUp(" + axis + " , " + pos + " , " + memb + ")");
						getWriter().startElement("img", attributes);
						getWriter().endElement("img");
					}
					getWriter().writeContent(label);

				} else if (!drillMode.equals(DrillDownCommand.MODE_REPLACE)) {
					getWriter().writeContent(label);
				}
			} else {
				getWriter().writeContent(label);
			}
		} else {
			Map<String, String> attributes = new HashMap<String, String>(1);
			attributes.put("href", link);

			getWriter().startElement("a", attributes);
			getWriter().writeContent(label);
			getWriter().endElement("a");
		}

	}

}
