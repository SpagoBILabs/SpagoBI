/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.georeport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.services.common.EnginConf;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GeoReportEngineConfig {
	
	private EnginConf engineConfig;
	
	private Map<String, List> includes;
	private Set<String> enabledIncludes;
	
	List<Properties> levels;
	
	private static transient Logger logger = Logger.getLogger(GeoReportEngineConfig.class);

	
	// -- singleton pattern --------------------------------------------
	private static GeoReportEngineConfig instance;
	
	public static GeoReportEngineConfig getInstance(){
		if(instance==null) {
			instance = new GeoReportEngineConfig();
		}
		return instance;
	}
	
	private GeoReportEngineConfig() {
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
	
	public List<Properties> getLevels() {
		
		if(levels == null) {
			initGeoDimensionLevels();
		}
		
		return levels;
	}
	
	public Properties getLevelByName(String name) {
		
		Properties levelProps = null;
		
		if(name == null) return null;
		
		if(levels == null) {
			initGeoDimensionLevels();
		}
		
		for(Properties props : levels) {
			if(name.equals(props.getProperty("name"))) {
				levelProps = props;
			}
		}
		
		return levelProps;
	}
	
	
	// -- PARSE Methods -------------------------------------------------
	
	private final static String INCLUDES_TAG = "INCLUDES";
	private final static String INCLUDE_TAG = "INCLUDE";
	private final static String URL_TAG = "URL";
	
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
	
	private final static String GEO_DIMENSION_TAG = "GEO_DIMENSION";
	private final static String LEVELS_TAG = "LEVELS";
	private final static String LEVEL_TAG = "LEVEL";
	
	public void initGeoDimensionLevels() {
		SourceBean geoDimensionSB = (SourceBean) getConfigSourceBean().getAttribute(GEO_DIMENSION_TAG);
		SourceBean levelsSB = (SourceBean) geoDimensionSB.getAttribute(LEVELS_TAG);
		List<SourceBean> levelList = levelsSB.getAttributeAsList(LEVEL_TAG);
		levels = new ArrayList<Properties>();
		
		/*
		 <LEVEL name="comune_ita" 
				layerName="gadm_ita_comuni" 
				layerLabel="Comuni" 
				layerId="NAME_3" 
				layer_file="comuni_sudtirol.json"
				layer_zoom="" 
				layer_cetral_point=""/>
		 */
		for(SourceBean level : levelList) {
			String name = (String)level.getAttribute("name");
			String layerName = (String)level.getAttribute("layerName");
			String layerLabel = (String)level.getAttribute("layerLabel");
			String layerId = (String)level.getAttribute("layerId");
			String layer_file = (String)level.getAttribute("layer_file");
			String layer_zoom = (String)level.getAttribute("layer_zoom");
			String layer_cetral_point = (String)level.getAttribute("layer_cetral_point");
			
			Properties props = new Properties();
			props.setProperty("name", name);
			props.setProperty("layerName", layerName);
			props.setProperty("layerLabel", layerLabel);
			props.setProperty("layerId", layerId);
			props.setProperty("layer_file", layer_file);
			props.setProperty("layer_zoom", layer_zoom);
			props.setProperty("layer_cetral_point", layer_cetral_point);
			
			levels.add(props);
		}
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
}
