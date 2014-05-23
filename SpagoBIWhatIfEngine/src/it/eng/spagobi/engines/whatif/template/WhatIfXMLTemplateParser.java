/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.whatif.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.writeback4j.WriteBackEditConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */
public class WhatIfXMLTemplateParser implements IWhatIfTemplateParser {
	
	public static String TAG_ROOT = "OLAP";
	public static String TAG_CUBE = "CUBE";
	public static String TAG_WRITEBACK = "CUBE";
	public static String TAG_MDX_QUERY = "MDXquery";
	public static String TAG_PARAMETER = "parameter";
	public static String TAG_MDX_MONDRIAN_QUERY = "MDXMondrianQuery";
	public static String PROP_SCHEMA_REFERENCE = "reference";
	public final static String MEASURE_TAG = "MEASURE";
	public final static String WRITEBACK_TAG = "WRITEBACK";
	public final static String EDIT_CUBE_ATTRIBUTE = "editCube";
	public static String PROP_PARAMETER_NAME = "name";
	public static String PROP_PARAMETER_ALIAS = "as";
	public static String SCENARIO_VARIABLES_TAG = "SCENARIO_VARIABLES";
	public static String VARIABLE_TAG = "VARIABLE";
	public static String VARIABLE_NAME_TAG = "name";
	public static String VARIABLE_VALUE_TAG = "value";
	public static String TAG_DATA_ACCESS = "DATA-ACCESS";
	public static String TAG_USER_ATTRIBUTE = "ATTRIBUTE";
	public static String PROP_USER_ATTRIBUTE_NAME = "name";

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(WhatIfXMLTemplateParser.class);
	
    public WhatIfTemplate parse(Object template) {
    	Assert.assertNotNull(template, "Input parameter [template] cannot be null");
    	Assert.assertTrue(template instanceof SourceBean, "Input parameter [template] cannot be of type [" + template.getClass().getName() + "]");
    	return parse((SourceBean)template);
    }
    
	private WhatIfTemplate parse(SourceBean template) {
		
		WhatIfTemplate toReturn = null;
		
		try {
			logger.debug("Starting template parsing....");
			
			toReturn = new WhatIfTemplate();
			
			SourceBean cubeSB = (SourceBean) template.getAttribute( TAG_CUBE );
			logger.debug(TAG_CUBE + ": " + cubeSB);
			Assert.assertNotNull(cubeSB, "Template is missing " + TAG_CUBE + " tag");
			String reference = (String) cubeSB.getAttribute(PROP_SCHEMA_REFERENCE);
			logger.debug(PROP_SCHEMA_REFERENCE + ": " + reference);
			toReturn.setMondrianSchema(reference);
			
			SourceBean mdxSB = (SourceBean) template.getAttribute( TAG_MDX_QUERY );
			logger.debug(TAG_MDX_QUERY + ": " + mdxSB);
			Assert.assertNotNull(mdxSB, "Template is missing " + TAG_MDX_QUERY + " tag");
			String mdxQuery = mdxSB.getCharacters();
			toReturn.setMdxQuery(mdxQuery);
			
			SourceBean mdxMondrianSB = (SourceBean) template.getAttribute( TAG_MDX_MONDRIAN_QUERY );
			logger.debug(TAG_MDX_MONDRIAN_QUERY + ": " + mdxMondrianSB);
			//Assert.assertNotNull(mdxMondrianSB, "Template is missing " + TAG_MDX_MONDRIAN_QUERY + " tag");
			String mdxMondrianQuery = mdxMondrianSB.getCharacters();
			toReturn.setMondrianMdxQuery(mdxMondrianQuery);
			
			//add the writeback configuration
			setWriteBackConf(template, toReturn);
			
			//add the variables
			setVariables(template, toReturn);
			
			List<WhatIfTemplate.Parameter> parameters = new ArrayList<WhatIfTemplate.Parameter>();
			List parametersSB = mdxSB.getAttributeAsList(TAG_PARAMETER);
			Iterator it = parametersSB.iterator();
			while (it.hasNext()) {
				SourceBean parameterSB = (SourceBean) it.next();
				logger.debug("Found " + TAG_PARAMETER + " definition :" + parameterSB);
				String name = (String) parameterSB.getAttribute(PROP_PARAMETER_NAME);
				String alias = (String) parameterSB.getAttribute(PROP_PARAMETER_ALIAS);
				Assert.assertNotNull(name, "Missing parameter's " + PROP_PARAMETER_NAME + " attribute");
				Assert.assertNotNull(alias, "Missing parameter's " + PROP_PARAMETER_ALIAS + " attribute");
				WhatIfTemplate.Parameter parameter = toReturn.new Parameter();
				parameter.setName(name);
				parameter.setAlias(alias);
				parameters.add(parameter);
			}
			toReturn.setParameters(parameters);
			
			// read user profile for profiled data access
			setProfilingUserAttributes(template, toReturn);
			
			logger.debug("Template parsed succesfully");
		} catch (Throwable t) {
			logger.error("Impossible to parse template [" + template.toString() + "]", t);
			throw new WhatIfTemplateParseException( t );
		} finally {
			logger.debug("OUT");
		}	
		
		return toReturn;
	}
	
	private void setProfilingUserAttributes(SourceBean template,
			WhatIfTemplate toReturn) {
		SourceBean dataAccessSB = (SourceBean) template.getAttribute( TAG_DATA_ACCESS );
		logger.debug(TAG_DATA_ACCESS + ": " + dataAccessSB);
		List<String> attributes = new ArrayList<String>();
		if (dataAccessSB != null) {
			List attributesSB = dataAccessSB.getAttributeAsList(TAG_USER_ATTRIBUTE);
			Iterator it = attributesSB.iterator();
			while (it.hasNext()) {
				SourceBean attributeSB = (SourceBean) it.next();
				logger.debug("Found " + TAG_USER_ATTRIBUTE + " definition :" + attributeSB);
				String name = (String) attributeSB.getAttribute(PROP_USER_ATTRIBUTE_NAME);
				Assert.assertNotNull(name, "Missing [" + PROP_PARAMETER_NAME + "] attribute in user profile attribute");
				attributes.add(name);
			}
		}
		toReturn.setProfilingUserAttributes(attributes);
	}

	private void setWriteBackConf(SourceBean template, WhatIfTemplate toReturn){
		SourceBean writeBackSB = (SourceBean) template.getAttribute(WRITEBACK_TAG);
		if(writeBackSB!=null){
			logger.debug(WRITEBACK_TAG + ": " + writeBackSB);
			WriteBackEditConfig writeBackConfig = new WriteBackEditConfig();
			String editCube = (String)writeBackSB.getAttribute(WhatIfXMLTemplateParser.EDIT_CUBE_ATTRIBUTE);
			if(editCube==null || editCube.length()==0){
				logger.error("In the writeback is enabled you must specify a cube to edit. Remove the "+WRITEBACK_TAG+" tag or specify a value for the attribute "+EDIT_CUBE_ATTRIBUTE );
				throw new SpagoBIEngineRuntimeException("In the writeback is enabled you must specify a cube to edit. Remove the "+WRITEBACK_TAG+" tag or specify a value for the attribute "+EDIT_CUBE_ATTRIBUTE);
			}
			List<SourceBean> editableMeasuresBeans = (List<SourceBean>)writeBackSB.getAttributeAsList(WhatIfXMLTemplateParser.MEASURE_TAG);
			if(editableMeasuresBeans!=null && editableMeasuresBeans.size()>0 ){
				List<String> editableMeasures = new ArrayList<String>();
				for(int i=0; i<editableMeasuresBeans.size(); i++){
					editableMeasures.add(editableMeasuresBeans.get(i).getCharacters());
				}
				writeBackConfig.setEditableMeasures(editableMeasures);
				logger.debug(WRITEBACK_TAG + ":the editable measures are "+editableMeasures);
			}
			writeBackConfig.setEditCubeName(editCube);
			logger.debug(WRITEBACK_TAG + ":the edit cube is "+editCube);
			toReturn.setWritebackEditConfig(writeBackConfig);
		}else{
			logger.debug(WRITEBACK_TAG + ": no write back configuration found in the template");
		}
	}
	
	private void setVariables(SourceBean template, WhatIfTemplate toReturn){
		SourceBean scenarioVariablesSB = (SourceBean) template.getAttribute(SCENARIO_VARIABLES_TAG);
		if(scenarioVariablesSB!=null){
			logger.debug(SCENARIO_VARIABLES_TAG + ": " + scenarioVariablesSB);
			Map<String, String> variables = new HashMap<String, String>();

			List<SourceBean> variablesBeans = (List<SourceBean>)scenarioVariablesSB.getAttributeAsList(VARIABLE_TAG);
			if(variablesBeans!=null && variablesBeans.size()>0 ){
				for(int i=0; i<variablesBeans.size(); i++){
					String name = (String)variablesBeans.get(i).getAttribute(VARIABLE_NAME_TAG);
					String value = (String)variablesBeans.get(i).getAttribute(VARIABLE_VALUE_TAG);
					variables.put(name, value);
				}
			}
			toReturn.setScenarioVariables(variables);
		}else{
			logger.debug(SCENARIO_VARIABLES_TAG + ": no scenario variable found in the template");
		}
	}
}
