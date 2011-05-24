/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.chart.utils;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spagobi.container.ObjectUtils;
import it.eng.spagobi.engines.chart.bo.charttypes.barcharts.BarCharts;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 * Utility Class for highcharts integration
 */
public class Template {
	
	//template constants
	public static final String HIGHCHART_TEMPLATE = "HIGHCHART";
	public static final String HIGH_CHART = "CHART";
	public static final String HIGH_TITLE = "TITLE";
	public static final String HIGH_SUBTITLE = "SUBTITLE";
	public static final String HIGH_TOOLTIP = "TOOLTIP";
	public static final String HIGH_LEGEND = "LEGEND";
	public static final String HIGH_PLOTOPTIONS = "PLOTOPTIONS";
	public static final String HIGH_PLOT_SERIES = "SERIES";
	public static final String HIGH_PLOT_SERIES_NAMES = "SERIES_NAMES";
	public static final String HIGH_PLOT_SERIES_COLORS = "SERIES_COLORS";
	public static final String HIGH_PLOT_SERIES_DASHSTYLES = "SERIES_DASHSTYLES";
	public static final String HIGH_PLOT_SERIES_MARKERS = "SERIES_MARKERS";
	public static final String HIGH_PLOT_SERIES_VISIBLES = "SERIES_VISIBLES";
	public static final String HIGH_PLOT_SERIES_ZINDEX = "SERIES_ZINDEX";
	public static final String HIGH_PLOT_SERIES_STACK = "SERIES_STACK";
	public static final String HIGH_PLOT_SERIES_TYPES = "SERIES_TYPES";
	public static final String HIGH_PLOT_SERIES_XAXIS = "SERIES_XAXIS";
	public static final String HIGH_PLOT_DATALABELS = "DATALABELS";
	public static final String HIGH_XAXIS = "XAXIS";
	public static final String HIGH_PLOT_XAXIS_TITLESS = "XAXIS_TITLES";
	public static final String HIGH_PLOT_YAXIS = "YAXIS";
	public static final String HIGH_PLOT_YAXIS_TITLES = "YAXIS_TITLES";
	public static final String HIGH_STYLE = "STYLE";
	public static final String WIDTH = "WIDTH";
	public static final String HEIGHT = "HEIGHT";
	
	private static transient Logger logger = Logger.getLogger(Template.class);
	
	private String divWidth = "100%";
	private String divHeight = "100%";
	private boolean firstBlock = true;
	
	/**
	 * Returns a JSONObject with the input configuration (xml format). 
	 * 
	 * @param xmlTemplate the template in xml language
	 * 
	 * @return JSONObject the same template in json format (because highcharts uses json format)
	 */
	public JSONObject getJSONTemplateFromXml(SourceBean xmlTemplate) throws JSONException {
		JSONObject toReturn = null;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    OutputStreamWriter ow = new OutputStreamWriter(out);

	    try{
		    //the begin of all...
		    ow.write("{\n");
			
			//dimension definition
			setDivWidth((String)xmlTemplate.getAttribute(WIDTH));
			setDivHeight((String)xmlTemplate.getAttribute(HEIGHT));
			
			xmlTemplate.delAttribute(WIDTH);
			xmlTemplate.delAttribute(HEIGHT);
			
			setFirstBlock(true);
			ow = getPropertiesDetail(xmlTemplate, ow);
			ow.write("}\n");
			ow.flush();			
			System.out.println("*** template: " + out.toString());
			logger.debug("ChartConfig: " + out.toString());
			
	    }catch (IOException ioe){
	    	logger.error("Error while defining json chart template: " + ioe.getMessage());
	    }catch (Exception e){
	    	logger.error("Error while defining json chart template: " + e.getMessage());
	    }finally{
	    	try{
	    		ow.close();
	    	}catch (IOException ioe2){
		    	logger.error("Error while closing the output writer object: " + ioe2.getMessage());
		    }
	    	
	    }
	    toReturn =  ObjectUtils.toJSONObject(out.toString());
		
		return toReturn;
	}

	/**
	 * @return the divWidth
	 */
	public String getDivWidth() {
		return divWidth;
	}

	/**
	 * @param divWidth the divWidth to set
	 */
	public void setDivWidth(String divWidth) {
		this.divWidth = divWidth;
	}

	/**
	 * @return the divHeight
	 */
	public String getDivHeight() {
		return divHeight;
	}

	/**
	 * @param divHeight the divHeight to set
	 */
	public void setDivHeight(String divHeight) {
		this.divHeight = divHeight;
	}
	
	/**
	 * @return the firstBlock
	 */
	public boolean isFirstBlock() {
		return firstBlock;
	}

	/**
	 * @param firstBlock the firstBlock to set
	 */
	public void setFirstBlock(boolean firstBlock) {
		this.firstBlock = firstBlock;
	}

	/**
	 * Returns an  OutputStreamWriter with the json template
	 * 
	 * @param sbConfig the sourcebean with the xml configuration 
	 * @param ow the current OutputStreamWriter
	 * @return
	 */
	private  OutputStreamWriter getPropertiesDetail (Object sbConfig, OutputStreamWriter ow ){
		//template complete
		OutputStreamWriter toReturn = ow;
		
		if (sbConfig == null) return toReturn;
		
	    try{
	    	List atts = ((SourceBean)sbConfig).getContainedAttributes();
			for (Iterator iterator = atts.iterator(); iterator.hasNext();) {
				SourceBeanAttribute object = (SourceBeanAttribute) iterator.next();
				String key=(String)object.getKey();
				if (!isFirstBlock()) {
		    		toReturn.write(", ");					
				}
				toReturn.write("      " + key.toLowerCase() +": { \n");	
				toReturn = getAllAttributes(key, object, toReturn);
				toReturn.write("       }\n");
				setFirstBlock(false);
			}
	    	
	    }catch (IOException ioe){
	    	logger.error("Error while defining json chart template: " + ioe.getMessage());
	    }catch (Exception e){
	    	logger.error("Error while defining json chart template: " + e.getMessage());
	    }	 
	    return toReturn;
	}
	
	/**
	 * Returns an object (String or Integer) with the value of the property.
	 * @param key the attribute key
	 * @param sbAttr the soureBeanAttribute to looking for the value of the key
	 * @return
	 */
	private Object getAttributeValue(String key, SourceBeanAttribute sbAttr){
		String value = new String((String)sbAttr.getValue());
		Object finalValue = "";
		if(value != null){
			try{
				//checks if the value is a number
				finalValue = Integer.valueOf(value);
			}catch (Exception e){
					//checks if the value is a boolean
					if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
						//the value is a string!
						finalValue = "'" + value + "'";
					}else{
						//the value is a boolean!
						finalValue = value;
					}
			}
		}
		return finalValue;
	}

	/**
	 * Returns an OutputStreamWriter with all details about a single key (ie. CHART tag)
	 * @param key
	 * @param sb
	 * @param ow
	 * @return
	 */
	private OutputStreamWriter getAllAttributes(String key, SourceBeanAttribute sb, OutputStreamWriter ow){
		OutputStreamWriter toReturn = ow;
		
		try{
			if (sb.getValue() instanceof SourceBean){
				int cont = 0;
				int cont2 = 0;
				int cont3 = 0;
				SourceBean sbSubConfig = (SourceBean)sb.getValue();
				List subAtts = sbSubConfig.getContainedAttributes();
				for (Iterator subIterator = subAtts.iterator(); subIterator.hasNext();) {
					SourceBeanAttribute subObject = (SourceBeanAttribute) subIterator.next();
					if (subObject.getValue() instanceof SourceBean){	
						cont2 = 0;
						SourceBean sbSubConfig2 = (SourceBean)subObject.getValue();
						List subAtts2 = sbSubConfig2.getContainedAttributes();
						if (cont>0 || cont3>0) {
							toReturn.write("       , " + subObject.getKey().toLowerCase() +": { \n");	
						}else{
							toReturn.write("         " + subObject.getKey().toLowerCase() +": { \n");	
							cont3++;
						}
						
						for (Iterator subIterator2 = subAtts2.iterator(); subIterator2.hasNext();) {
							SourceBeanAttribute subObject2 = (SourceBeanAttribute) subIterator2.next();
							Object subValue = getAttributeValue(subObject2.getKey(), subObject2);
							if (subValue != null){
								if (cont2 > 0) {
									toReturn.write("         , ");
								}else{
									toReturn.write("           ");
									cont2++;
								}
								toReturn.write(subObject2.getKey().toLowerCase() + ": " + subValue + "\n" );	
								//cont2++;
							}
						}
						toReturn.write("         }\n");
						//cont3++;
					}
					else{
						Object subValue = getAttributeValue(key, subObject);
						if (subValue != null){
							if (cont > 0) {
								toReturn.write("       , ");
							}else{
								toReturn.write("         ");
							}
							toReturn.write(subObject.getKey().toLowerCase() + ": " + subValue + "\n" );	
							cont++;
						}
					}
				}//for
			}	
		}catch (IOException ioe){
	    	logger.error("Error while defining json chart template: " + ioe.getMessage());
	    }catch (Exception e){
	    	logger.error("Error while defining json chart template: " + e.getMessage());
	    }
		return toReturn;
	}
}
