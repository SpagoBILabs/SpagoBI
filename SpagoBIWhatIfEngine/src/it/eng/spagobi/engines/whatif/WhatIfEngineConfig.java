/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.engines.whatif.template.WhatIfTemplate;
import it.eng.spagobi.engines.whatif.template.WhatIfXMLTemplateParser;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.writeback4j.WriteBackEditConfig;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.olap4j.OlapDataSource;

import sun.misc.BASE64Encoder;

import com.eyeq.pivot4j.datasource.SimpleOlapDataSource;

/**
 * @author ...
 */
public class WhatIfEngineConfig {
	
	private static final BASE64Encoder ENCODER = new BASE64Encoder();
	
	private EnginConf engineConfig;
	
	private Map<String, List> includes;
	private Set<String> enabledIncludes;
	private String resourceFolder= "whatif";
	private static transient Logger logger = Logger.getLogger(WhatIfEngineConfig.class);
	private static final String PROPORTIONAL_ALGORITHM_CONF = "proportionalAlgorithmPersistQueryWhereClause"; 

	
	// -- singleton pattern --------------------------------------------
	private static WhatIfEngineConfig instance;
	
	public static WhatIfEngineConfig getInstance(){
		if(instance==null) {
			instance = new WhatIfEngineConfig();
		}
		return instance;
	}
	
	private WhatIfEngineConfig() {
		setEngineConfig( EnginConf.getInstance() );
	}
	// -- singleton pattern  --------------------------------------------
	
	
	// -- CORE SETTINGS ACCESSOR Methods---------------------------------
	
	public List getIncludes() {
		List results;
		
		//includes = null;
		if(includes == null) {
			initIncludes();
		}
		
		results = new ArrayList();
		Iterator<String> it = enabledIncludes.iterator();
		while(it.hasNext()) {
			String includeName = it.next();
			List urls = includes.get( includeName );
			results.addAll(urls);
			logger.debug("Added [" + urls.size() + "] for include [" + includeName + "]");
		}
		
		
		return results;
	}
	
	
	// -- PARSE Methods -------------------------------------------------
	
	private final static String INCLUDES_TAG = "INCLUDES";
	private final static String INCLUDE_TAG = "INCLUDE";
	private final static String URL_TAG = "URL";
	private final static String WRITEBACK_TAG = "WRITEBACK";
	
	
	public String getInitiallMdx() {
		String initialMdx = "SELECT {[Measures].[Unit Sales], [Measures].[Store Cost]} ON COLUMNS, {[Product].[Food]} ON ROWS FROM [Sales]";
		SourceBean sb = (SourceBean) getConfigSourceBean().getAttribute("MDX");
		if(sb!=null){
			initialMdx = sb.getCharacters();
		}
		return initialMdx;
	}
	
	public String getCatalogue(){
		String catalog = "/home/spagobi/apache-tomcat-7.0.50/resources/Olap/FoodMart.xml";
		SourceBean sb = (SourceBean) getConfigSourceBean().getAttribute("CATALOG");
		if(sb!=null){
			String system = System.getProperty("catalina.home");
			catalog = system+sb.getCharacters();
		}
		return catalog;
	}
	
	public String getConnectionString(){
		String connectionString =  "jdbc:mondrian:Jdbc=jdbc:mysql://sibilla2:3306/foodmart";
		SourceBean sb = (SourceBean) getConfigSourceBean().getAttribute("CONNECTIONSTRING");
		if(sb!=null)
			connectionString = sb.getCharacters();
		return connectionString;
	}
	
	public String getConnectionPwd(){
		String pwd =  "foodmart";
		SourceBean sb = (SourceBean) getConfigSourceBean().getAttribute("PWD");
		if(sb==null)
			pwd = "";
		else
			pwd= sb.getCharacters();
		if (pwd == null) {
			pwd = "";
		}
		return pwd;
	}
	
	public String getConnectionUsr(){
		String usr =  "foodmart";
		SourceBean sb = (SourceBean) getConfigSourceBean().getAttribute("USR");
		if(sb!=null)
			usr = sb.getCharacters();
		return usr;
	}
	
	public String getDriver(){
		String driver =  "com.mysql.jdbc.Driver";
		SourceBean sb = (SourceBean) getConfigSourceBean().getAttribute("DRIVER_TAG");
		if(sb!=null)
			driver = sb.getCharacters();
		return driver;
	}
	
	public WriteBackEditConfig getWritebackEditConfig(){

		SourceBean writeBackSB = (SourceBean) getConfigSourceBean().getAttribute(WRITEBACK_TAG);
		if(writeBackSB!=null){
			logger.debug(WRITEBACK_TAG + ": " + writeBackSB);
			WriteBackEditConfig writeBackConfig = new WriteBackEditConfig();
			String editCube = (String)writeBackSB.getAttribute(WhatIfXMLTemplateParser.EDIT_CUBE_ATTRIBUTE);
			if(editCube==null || editCube.length()==0){
				logger.error("In the writeback is enabled you must specify a cube to edit. Remove the "+WhatIfXMLTemplateParser.WRITEBACK_TAG+" tag or specify a value for the attribute "+WhatIfXMLTemplateParser.EDIT_CUBE_ATTRIBUTE );
				throw new SpagoBIEngineRuntimeException("In the writeback is enabled you must specify a cube to edit. Remove the "+WRITEBACK_TAG+" tag or specify a value for the attribute "+WhatIfXMLTemplateParser.EDIT_CUBE_ATTRIBUTE);
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
			return writeBackConfig;
		}else{
			logger.debug(WRITEBACK_TAG + ": no write back configuration found in the template");
		}
		
		return null;
	}
	
	public Map<String, String> getScenarioVariables(){
		Map<String, String> variables = new HashMap<String, String>();
		SourceBean scenarioVariablesSB = (SourceBean) getConfigSourceBean().getAttribute(WhatIfXMLTemplateParser.SCENARIO_VARIABLES_TAG);
		if(scenarioVariablesSB!=null){
			logger.debug(WhatIfXMLTemplateParser.SCENARIO_VARIABLES_TAG + ": " + scenarioVariablesSB);
			

			List<SourceBean> variablesBeans = (List<SourceBean>)scenarioVariablesSB.getAttributeAsList(WhatIfXMLTemplateParser.VARIABLE_TAG);
			if(variablesBeans!=null && variablesBeans.size()>0 ){
				for(int i=0; i<variablesBeans.size(); i++){
					String name = (String)variablesBeans.get(i).getAttribute(WhatIfXMLTemplateParser.VARIABLE_NAME_TAG);
					String value = (String)variablesBeans.get(i).getAttribute(WhatIfXMLTemplateParser.VARIABLE_VALUE_TAG);
					variables.put(name, value);
				}
			}
		}else{
			logger.debug(WhatIfXMLTemplateParser.SCENARIO_VARIABLES_TAG + ": no scenario variable found in the template");
		}
		return variables;
	}
	
	
	public OlapDataSource getOlapDataSource(IDataSource ds, String reference, WhatIfTemplate template, IEngUserProfile profile) {
		Properties connectionProps = new Properties();
		String connectionString = null;
		if (ds.checkIsJndi()) {
			connectionProps.put("DataSource", ds.getJndi());
			connectionString = "jdbc:mondrian:DataSource=" + ds.getJndi();
		} else {
			connectionProps.put("JdbcUser",ds.getUser());
			connectionProps.put("JdbcPassword",ds.getPwd());
			connectionProps.put("JdbcDrivers",ds.getDriver());
			connectionString = "jdbc:mondrian:Jdbc=" + ds.getUrlConnection();
		}
		
		connectionProps.put("Catalog", reference);
		connectionProps.put("Provider","Mondrian");
		
		this.defineSchemaProcessorProperties(connectionProps, template, profile);

		OlapDataSource olapDataSource = new SimpleOlapDataSource();
		((SimpleOlapDataSource)olapDataSource).setConnectionString( connectionString);
		((SimpleOlapDataSource)olapDataSource).setConnectionProperties(connectionProps);
		
		return olapDataSource;
	}
	
	private void defineSchemaProcessorProperties(Properties connectionProps,
			WhatIfTemplate template, IEngUserProfile profile) {
		List<String> userProfileAttributes = template.getProfilingUserAttributes();
		if ( !userProfileAttributes.isEmpty() ) {
			logger.debug("Template contains data access restriction based on user's attributes");
			connectionProps.put("DynamicSchemaProcessor","it.eng.spagobi.engines.whatif.schema.SpagoBIFilterDynamicSchemaProcessor");
			Iterator<String> it = userProfileAttributes.iterator();
			while (it.hasNext()) {
				String attributeName = it.next();
				String value = this.getUserProfileEncodedValue(attributeName, profile);
				logger.debug("Adding profile attribute [" + attributeName + "]"
						+ " with encoded value [" + value + "]");
				connectionProps.put(attributeName, value);
			}
		} else {
			logger.debug("Template does not contain any data access restriction based on user's attributes");
		}
	}

	private String getUserProfileEncodedValue(String attributeName,
			IEngUserProfile profile) {
		String value;
		try {
			value = profile.getUserAttribute(attributeName) != null ? profile.getUserAttribute(attributeName).toString() : null;
		} catch (EMFInternalError e) {
			throw new SpagoBIEngineRuntimeException("Error while retrieving user profile [" + attributeName + "]", e);
		}
		logger.debug("Found profile attribute [" + attributeName + "]"
				+ " with value [" + value + "]");
		
		// encoding value in Base64 
		String valueBase64 = null;
		if (value != null) {
			try {
				valueBase64 = ENCODER.encode(value.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("UTF-8 encoding not supported!!!!!", e);
				valueBase64 = ENCODER.encode(value.getBytes());
			}
		}
		logger.debug("Attribute value in Base64 encoding is " + valueBase64);
		
		return valueBase64;
	}

	public void initIncludes() {
		SourceBean includesSB;
		List includeSBList;
		SourceBean includeSB;
		List urlSBList;
		SourceBean urlSB;
		
		includes = new HashMap();
		enabledIncludes = new LinkedHashSet();
		
		includesSB = (SourceBean) getConfigSourceBean().getAttribute(INCLUDES_TAG);
		if(includesSB == null) {
			logger.debug("Tag [" + INCLUDES_TAG + "] not specifeid in [engine-config.xml] file");
			return;
		}
		
		includeSBList = includesSB.getAttributeAsList(INCLUDE_TAG);
		if(includeSBList == null || includeSBList.size() == 0) {
			logger.debug("Tag [" + INCLUDES_TAG + "] does not contains any [" + INCLUDE_TAG + "] tag");
			return;
		}
		
		for(int i = 0; i < includeSBList.size(); i++) {
			includeSB = (SourceBean)includeSBList.get(i);
			String name = (String)includeSB.getAttribute("name");
			String bydefault = (String)includeSB.getAttribute("default");
			
			logger.debug("Include [" + name + "]: [" + bydefault + "]");
			
			List urls = new ArrayList();
			
			urlSBList = includeSB.getAttributeAsList(URL_TAG);
			for(int j = 0; j < urlSBList.size(); j++) {
				urlSB = (SourceBean)urlSBList.get(j);
				String url = urlSB.getCharacters();
				urls.add(url);
				logger.debug("Url [" + name + "] added to include list");
			}
			
			includes.put(name, urls);
			if(bydefault.equalsIgnoreCase("enabled")) {
				enabledIncludes.add(name);
			}
		}		
	}
	
	public boolean getProportionalAlghorithmConf(){
		SourceBean sb = (SourceBean) getConfigSourceBean().getAttribute(WRITEBACK_TAG);
		if(sb!=null){
			String conf =  (String)sb.getAttribute(PROPORTIONAL_ALGORITHM_CONF);
			if(conf!=null && conf.length()>0){
				return conf.equals("in");
			}
		}
		return 	true;
	}
	
	// -- ACCESS Methods  -----------------------------------------------
	public EnginConf getEngineConfig() {
		return engineConfig;
	}

	private void setEngineConfig(EnginConf engineConfig) {
		this.engineConfig = engineConfig;
	}
	
	public SourceBean getConfigSourceBean() {
		return getEngineConfig().getConfig();
	}
	
	public String getEngineResourcePath() {
		String path = null;
		if(getEngineConfig().getResourcePath() != null) {
			path = getEngineConfig().getResourcePath() + System.getProperty("file.separator") + resourceFolder;
		} else {
			path = ConfigSingleton.getRootPath() + System.getProperty("file.separator") + "resources" + System.getProperty("file.separator") + resourceFolder;
		}
		return path;
	}
}
