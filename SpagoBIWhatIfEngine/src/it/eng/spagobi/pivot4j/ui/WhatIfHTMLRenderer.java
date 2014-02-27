package it.eng.spagobi.pivot4j.ui;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.OlapException;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.ui.CellType;
import com.eyeq.pivot4j.ui.RenderContext;
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
				System.out.println(context.getMember().getMemberType().name());
				try {
					int childrenNum = context.getMember().getChildMemberCount();
					int depth = context.getMember().getDepth();
					int allSize= context.getHierarchy().getLevels().size();
					System.out.println(depth);
					System.out.println(allSize);

					
					if(childrenNum > 0){	
						if((allSize-1) == depth){
							styleClass += " " + "expanded";
						}else{
							styleClass += " " + "collapsed";
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

		///spagobi whatif engine 
		CellType ct = context.getCellType();
		//context.getCellSetAxis().getAxisOrdinal().axisOrdinal();
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

		if(ct.name().equalsIgnoreCase("Header")){
			//explode or collapse for drill down functionality
			attributes.put("onClick", "javascript:Sbi.olap.eventManager.drillDown("+axis+" , "+pos+" , "+memb+")");

			
		}else if(ct.name().equalsIgnoreCase("Value")){
			//edit cell value functionality
			attributes.put("onClick", "alert('"+ct.name()+"')");
		}
		
		return attributes;
	}

}
