package it.eng.spagobi.pivot4j.ui;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.component.UIParameter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.olap4j.Axis;
import org.olap4j.OlapException;

import com.eyeq.pivot4j.ui.RenderContext;
import com.eyeq.pivot4j.ui.command.CellCommand;
import com.eyeq.pivot4j.ui.command.CellParameters;
import com.eyeq.pivot4j.ui.html.HtmlRenderer;
import com.eyeq.pivot4j.ui.property.PropertySupport;
import com.eyeq.pivot4j.util.CssWriter;

public class WhatIfHTMLRenderer extends HtmlRenderer {

	public WhatIfHTMLRenderer(Writer writer) {
		super(writer);

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
							* (1 + context.getMember().getDepth());

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
			//adds the proper style (depending if it was collapsed or expanded)
			if(context.getMember() != null && context.getMember().getMemberType() != null && !context.getMember().getMemberType().name().equalsIgnoreCase("Measure")){

				try {
					int childrenNum = context.getMember().getChildMemberCount();
					
					if(childrenNum > 0){


						if(getEnableRowDrillDown() || getEnableColumnDrillDown()){
							styleClass += " " + "collapsed";
						}else{
							styleClass += " " + "expanded";
						}
						

					}
					
				} catch (OlapException e) {
					e.printStackTrace();
				}
			}	
			attributes.put("class", styleClass);
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
	
	@Override
	public void cellContent(RenderContext context, String label) {

		String link = null;

		PropertySupport properties = getProperties(context);

		if (properties != null) {
			link = getPropertyValue("link", properties, context);
		}

		if (link == null) {
			Map<String, String> attributes = new TreeMap<String, String>();
			if(context.getMember() != null && context.getMember().getMemberType() != null && !context.getMember().getMemberType().name().equalsIgnoreCase("Measure")){

				List <CellCommand<?>> commands = getCommands(context);

				if (commands != null && !commands.isEmpty()) {
					for (CellCommand<?> command : commands) {
						String cmd = command.getName();

						///spagobi whatif engine 

						int colIdx = context.getColumnIndex();
						int rowIdx = context.getRowIndex();

						int axis =0;
						if(context.getAxis()!= null){
							axis =context.getAxis().axisOrdinal();
						}
						int memb =0;
						if(context.getPosition()!= null){
							memb =context.getPosition().getOrdinal();
						}
						int pos =0;
						if(context.getAxis() == Axis.COLUMNS){
							pos = rowIdx;
						}else{
							pos = colIdx;
						}
						if(cmd != null && (cmd.equalsIgnoreCase("expandPosition")  || cmd.equalsIgnoreCase("drillDown") || cmd.equalsIgnoreCase("expandMember"))){
							attributes.put("src", "../img/plus.gif");
							attributes.put("onClick", "javascript:Sbi.olap.eventManager.drillDown("+axis+" , "+pos+" , "+memb+")");
							getWriter().startElement("img", attributes);			
							getWriter().endElement("img");
						}else if(cmd != null && (cmd.equalsIgnoreCase("collapsePosition") || cmd.equalsIgnoreCase("drillUp") || cmd.equalsIgnoreCase("collapseMember"))){
							attributes.put("src", "../img/minus.gif");
							attributes.put("onClick", "javascript:Sbi.olap.eventManager.drillUp("+axis+" , "+pos+" , "+memb+")");
							getWriter().startElement("img", attributes);			
							getWriter().endElement("img");
						}

					}
				}

			}else if(context.getMember() != null && context.getMember().getMemberType() != null && context.getMember().getMemberType().name().equalsIgnoreCase("Measure")){
				attributes.put("onClick", "javascript:void(document.body.contentEditable=\"true\");");

			}
			getWriter().writeContent(label);
		} else {
			Map<String, String> attributes = new HashMap<String, String>(1);
			attributes.put("href", link);

			getWriter().startElement("a", attributes);
			getWriter().writeContent(label);
			getWriter().endElement("a");
		}
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

		if (commands != null && !commands.isEmpty()) {
			for (CellCommand<?> command : commands) {
				CellParameters parameters = command.createParameters(context);

				UIParameter commandParam = new UIParameter();
				commandParam.setName("command");
				commandParam.setValue(command.getName());

				UIParameter axisParam = new UIParameter();
				axisParam.setName("axis");
				axisParam.setValue(parameters.getAxisOrdinal());


				UIParameter positionParam = new UIParameter();
				positionParam.setName("position");
				positionParam.setValue(parameters.getPositionOrdinal());


				UIParameter memberParam = new UIParameter();
				memberParam.setName("member");
				memberParam.setValue(parameters.getMemberOrdinal());


				UIParameter hierarchyParam = new UIParameter();
				hierarchyParam.setName("hierarchy");
				hierarchyParam.setValue(parameters.getHierarchyOrdinal());

				UIParameter cellParam = new UIParameter();
				cellParam.setName("cell");
				cellParam.setValue(parameters.getCellOrdinal());

			}
		}
		getWriter().startElement(name, getCellAttributes(context));
	}


}
