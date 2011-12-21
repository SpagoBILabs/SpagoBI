/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.engines.qbe.template;

import it.eng.spagobi.utilities.assertion.Assert;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * The Class QbeTemplate.
 * 
 * @author Andrea Gioia
 */
public class QbeJSONTemplateParser implements IQbeTemplateParser {
	


	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(QbeJSONTemplateParser.class);
    
    public static String ID = "id";
    public static String OPERATOR = "operator";
    
    public static String STATIC_CLOSED_FILTERS = "staticClosedFilters";
    public static String STATIC_OPEN_FILTERS = "staticOpenFilters";
    public static String DYNAMIC_FILTERS = "dynamicFilters";
    public static String GROUPING_VARIABLES = "groupingVariables";
    public static String OPTIONS = "options";
    public static String STATIC_CLOSED_FILTER_SINGLE_SELECTION = "singleSelection";
    public static String STATIC_CLOSED_FILTER_NO_SELECTION = "noSelection";
    
    public static String STATIC_XOR_FILTERS_PREFIX = "xorFilter-";
    public static String STATIC_XOR_OPTIONS_PREFIX = "option-";
    public static String STATIC_ON_OFF_FILTERS_PREFIX = "onOffFilter-";
    public static String STATIC_ON_OFF_OPTIONS_PREFIX = "option-";
    public static String OPEN_FILTERS_PREFIX = "openFilter-";
    public static String DYNAMIC_FILTERS_PREFIX = "dynamicFilter-";
    public static String GROUPING_VARIABLE_PREFIX = "groupingVariable-";
    
	
    public QbeTemplate parse(Object template) {
    	Assert.assertNotNull(template, "Input parameter [template] cannot be null");
    	Assert.assertTrue(template instanceof JSONObject, "Input parameter [template] cannot be of type [" + template.getClass().getName() + "]");
    	return parse((JSONObject)template);
    }
	
	private QbeTemplate parse(JSONObject template) {
		logger.debug("IN: input template: " + template);
		QbeTemplate qbeTemplate = null;
		try {
			
			qbeTemplate = new QbeTemplate();
			addAdditionalInfo(template);
			logger.debug("Modified template: " + template);
			qbeTemplate.setProperty("jsonTemplate", template);
			
			JSONObject qbeConf = template.optJSONObject("qbeConf");
			JSONArray datamartsName = (JSONArray) qbeConf.get("datamartsName");
			for (int i = 0; i < datamartsName.length(); i++ ) {
				String aDatamartName = (String) datamartsName.get(i);
				qbeTemplate.addDatamartName(aDatamartName);
			}
			qbeTemplate.setProperty("query", qbeConf.getString("query"));
			
			logger.debug("Templete parsed succesfully");
		} catch(Throwable t) {
			throw new QbeTemplateParseException("Impossible to parse tempate [" + template.toString()+ "]", t);
		} finally {
			logger.debug("OUT");
		}	
		
		return qbeTemplate;
	}

	public static void addAdditionalInfo(JSONObject template) {
		logger.debug("IN");
		try {
			JSONArray staticClosedFilters = template.optJSONArray(STATIC_CLOSED_FILTERS);
			int xorFiltersCounter = 1;
			int onOffFiltersCounter = 1;
			if (staticClosedFilters != null && staticClosedFilters.length() > 0) {
				for (int i = 0; i < staticClosedFilters.length(); i++) {
					JSONObject aStaticClosedFilter = (JSONObject) staticClosedFilters.get(i);
					if (aStaticClosedFilter.getBoolean(STATIC_CLOSED_FILTER_SINGLE_SELECTION)) {
						// xor filter
						aStaticClosedFilter.put(ID, STATIC_XOR_FILTERS_PREFIX + xorFiltersCounter);
						JSONArray options = aStaticClosedFilter.getJSONArray(OPTIONS);
						for (int j = 0; j < options.length(); j++) {
							JSONObject anOption = (JSONObject) options.get(j);
							anOption.put(ID, STATIC_XOR_OPTIONS_PREFIX + (j+1));
						}
						xorFiltersCounter++;
					} else {
						// on off filter
						aStaticClosedFilter.put(ID, STATIC_ON_OFF_FILTERS_PREFIX + onOffFiltersCounter);
						JSONArray options = aStaticClosedFilter.getJSONArray(OPTIONS);
						for (int j = 0; j < options.length(); j++) {
							JSONObject anOption = (JSONObject) options.get(j);
							anOption.put(ID, STATIC_ON_OFF_FILTERS_PREFIX + onOffFiltersCounter + "-" + STATIC_ON_OFF_OPTIONS_PREFIX + (j+1));
						}
						onOffFiltersCounter++;
					}
				}
			}
			
			JSONArray staticOpenFilters = template.optJSONArray(STATIC_OPEN_FILTERS);
			if (staticOpenFilters != null && staticOpenFilters.length() > 0) {
				for (int i = 0; i < staticOpenFilters.length(); i++) {
					JSONObject aStaticOpenFilter = (JSONObject) staticOpenFilters.get(i);
					aStaticOpenFilter.put(ID, OPEN_FILTERS_PREFIX + (i+1));
				}
			}
			
			JSONArray dynamicFilters = template.optJSONArray(DYNAMIC_FILTERS);
			if (dynamicFilters != null && dynamicFilters.length() > 0) {
				for (int i = 0; i < dynamicFilters.length(); i++) {
					JSONObject aDynamicFilter = (JSONObject) dynamicFilters.get(i);
					aDynamicFilter.put(ID, DYNAMIC_FILTERS_PREFIX + (i+1));
				}
			}
			
			JSONArray groupingVariables = template.optJSONArray(GROUPING_VARIABLES);
			if (groupingVariables != null && groupingVariables.length() > 0) {
				for (int i = 0; i < groupingVariables.length(); i++) {
					JSONObject aGroupingVariable = (JSONObject) groupingVariables.get(i);
					aGroupingVariable.put(ID, GROUPING_VARIABLE_PREFIX + (i+1));
				}
			}
			
		} catch(Throwable t) {
			throw new QbeTemplateParseException("Cannot parse template [" + template.toString()+ "]", t);
		} finally {
			logger.debug("OUT");
		}
	}
}
