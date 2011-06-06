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
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
	private String theme = "";
	private boolean firstBlock = true;
	private JSONArray parametersJSON = null;
	/**
	 * Returns a JSONObject with the input configuration (xml format). 
	 * 
	 * @param xmlTemplate the template in xml language
	 * @param
	 * 
	 * @return JSONObject the same template in json format (because highcharts uses json format)
	 */
	public JSONObject getJSONTemplateFromXml(SourceBean xmlTemplate, JSONArray parsJSON) throws JSONException {
		JSONObject toReturn = null;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    OutputStreamWriter ow = new OutputStreamWriter(out);
	    parametersJSON = parsJSON;
	    try{
		    //the begin of all...
		    ow.write("{\n");
			
			//dimension definition
			setDivWidth((String)xmlTemplate.getAttribute(WIDTH));
			setDivHeight((String)xmlTemplate.getAttribute(HEIGHT));
			
			xmlTemplate.delAttribute(WIDTH);
			xmlTemplate.delAttribute(HEIGHT);

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
	    //replace dublicate , charachter
	    String json = out.toString().replaceAll(", ,", ",");
	    toReturn =  ObjectUtils.toJSONObject(json);
		
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
	 * @return the theme
	 */
	public String getTheme() {
		return theme;
	}

	/**
	 * @param theme the theme to set
	 */
	public void setTheme(String theme) {
		this.theme = theme;
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
	    	for (int i=0; i< atts.size();i++) {

				SourceBeanAttribute object = (SourceBeanAttribute) atts.get(i);

				String key=(String)object.getKey();
				if(key.endsWith("_LIST")){
					String arrayKey = key.substring(0, key.indexOf("_LIST"));
					toReturn.write("      " + convertKeyString(arrayKey) +": [ \n");	
					toReturn = getAllArrayAttributes(object, toReturn);
					toReturn.write("       ]\n");
				}else{
					toReturn.write("      " + convertKeyString(key) +": { \n");	
					toReturn = getAllAttributes(object, ow);
					toReturn.write("       }\n");
				}
				if(i != atts.size()-1){
					toReturn.write(", ");		
				}
			}
	    	
	    }catch (Exception e){
	    	logger.error("Error while defining json chart template: " + e.getMessage());
	    }	 
	    return toReturn;
	}
	

	/**
	 * Returns an OutputStreamWriter with all details about a single key (ie. CHART tag)
	 * @param key
	 * @param sb
	 * @param ow
	 * @return
	 */
	private OutputStreamWriter getAllAttributes(SourceBeanAttribute sb, OutputStreamWriter ow){
		OutputStreamWriter toReturn = ow;
		
		try{
			if (sb.getValue() instanceof SourceBean){
				SourceBean sbSubConfig = (SourceBean)sb.getValue();
				List subAtts = sbSubConfig.getContainedAttributes();
				List containedSB = sbSubConfig.getContainedSourceBeanAttributes();
				int numberOfSb = containedSB.size();
				int sbCounter = 1;
				//standard tag attributes
				for(int i =0; i< subAtts.size(); i++){
					SourceBeanAttribute object = (SourceBeanAttribute)subAtts.get(i);
					if (object.getValue() instanceof SourceBean){
						
						String key=(String)object.getKey();

						if(key.endsWith("_LIST")){
							String arrayKey = key.substring(0, key.indexOf("_LIST"));
							toReturn.write("      " + convertKeyString(arrayKey) +": [ \n");	
							toReturn = getAllArrayAttributes(object, toReturn);
							toReturn.write("       ]\n");
						}else{
							toReturn.write("      " + convertKeyString(key) +": { \n");	
							toReturn = getAllAttributes(object, toReturn);
							toReturn.write("       }\n");
						}
						if(i != subAtts.size()-1){
							toReturn.write("       , ");
						}
						sbCounter++;
					}else{
						SourceBeanAttribute subObject2 = (SourceBeanAttribute) subAtts.get(i);
						toReturn = writeTagAttribute(subObject2, toReturn, false);
						if(i != subAtts.size()-1){
							toReturn.write("       , ");
						}
					}
				}
			
			}	
		}catch (IOException ioe){
	    	logger.error("Error while defining json chart template: " + ioe.getMessage());
	    }catch (Exception e){
	    	logger.error("Error while defining json chart template: " + e.getMessage());
	    }
		return toReturn;
	}
	private OutputStreamWriter getAllArrayAttributes(SourceBeanAttribute sb, OutputStreamWriter ow){
		OutputStreamWriter toReturn = ow;
		
		try{
			if (sb.getValue() instanceof SourceBean){
				SourceBean sbSubConfig = (SourceBean)sb.getValue();

				List containedSB = sbSubConfig.getContainedSourceBeanAttributes();
				int numberOfSb = containedSB.size();
				int sbCounter = 1;
				//standard tag attributes
				for(int i =0; i< containedSB.size(); i++){
					SourceBeanAttribute object = (SourceBeanAttribute)containedSB.get(i);
					Object o = object.getValue();
					SourceBean sb1 = SourceBean.fromXMLString(o.toString());
					String v = sb1.getCharacters();
					if(v!= null){
						toReturn.write(v + "\n" );

					}else{
						//attributes

						toReturn.write("{ 	\n" );
				    	List atts = ((SourceBean)sb1).getContainedAttributes();
						toReturn = getAllAttributes(object, toReturn);
				    	toReturn.write("} 	\n" );
					}
					if(i != containedSB.size()-1){
						toReturn.write("       , ");
					}
				}
			
			}	
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
				//finalValue = Integer.valueOf(value);
				finalValue =Long.valueOf(value);
			}catch (Exception e){
					//checks if the value is a boolean
					if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false") //boolean
							&& ! value.startsWith("[")//not an array example for categories...
							//&& ! value.trim().startsWith("function")//not an array example for categories...
						){
						//replace parameters
						if(value.contains("$P{")){
							finalValue = replaceParametersInValue(value);
							finalValue = "'" + finalValue + "'";
						}else{
							//the value is a string!
							finalValue = "'" + value + "'";
						}
					}else{
						//the value is not a string
						finalValue = value;
					}
			}
		}
		return finalValue;
	}
	private String replaceParametersInValue(String valueString){
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(valueString,"$P{");
		while(st.hasMoreTokens()){
			String tok = st.nextToken();
			if(tok.indexOf("}") != -1){
				String [] str = tok.split("}");				
				if(str.length >= 1){
					String parName = str[0];
					for(int i=0; i<parametersJSON.length(); i++){
						try {
							JSONObject objPar = (JSONObject)parametersJSON.get(i);								
							if(((String)objPar.get("name")).equals(parName)){
								String val = ((String)objPar.get("value")).replaceAll("'", "");
								sb.append(val);
							}
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if(str.length > 1){
						sb.append(str[1]);
					}
					
				}
			}else{
				sb.append(tok);
			}
		}

		return sb.toString(); 
	}
	/**
	 * @param sb
	 * @param toReturn
	 * @return
	 * @throws IOException
	 */
	private OutputStreamWriter writeTagAttribute(SourceBeanAttribute sb, OutputStreamWriter toReturn, boolean isTag) throws IOException{

		Object subValue = getAttributeValue(sb.getKey(), sb);
		if (subValue != null){
			if(isTag){
				toReturn.write("      " + convertKeyString(sb.getKey()) + ": " + subValue + "\n" );	
			}else{				
				toReturn.write("      " + sb.getKey() + ": " + subValue + "\n" );	
			}

		}
		return toReturn;
	}
	private String convertKeyString(String xmlTag){
		String jsonKey = xmlTag.toLowerCase();
		StringBuffer sb = new StringBuffer();
		int count = 0;
	    for (String s : xmlTag.split("_")) {
	    	if(count == 0){
	    		sb.append(Character.toLowerCase(s.charAt(0)));
	    	}else{
	    		sb.append(Character.toUpperCase(s.charAt(0)));
	    	}
	        if (s.length() > 1) {
	            sb.append(s.substring(1, s.length()).toLowerCase());
	        }
	        count++;
	    }

	    if(!sb.toString().equals("")){
	    	jsonKey = sb.toString();
	    }
	    
	    return jsonKey;

	}
}
