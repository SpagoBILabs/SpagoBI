/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.datamart.provider.configurator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.engines.geo.GeoEngineConstants;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.datamart.provider.AbstractDataMartProvider;
import it.eng.spagobi.engines.geo.dataset.DataSetMetaData;
import it.eng.spagobi.engines.geo.dataset.provider.Hierarchy;
import it.eng.spagobi.engines.geo.dataset.provider.Link;
import it.eng.spagobi.utilities.assertion.Assert;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractDatasetProviderConfigurator.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractDataMartProviderConfigurator {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(AbstractDataMartProviderConfigurator.class);
	
	
	/**
	 * Configure.
	 * 
	 * @param abstractDatasetProvider the abstract dataset provider
	 * @param conf the conf
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public static void configure(AbstractDataMartProvider abstractDatasetProvider, Object conf) throws GeoEngineException {
		SourceBean confSB = null;
		
		if(conf instanceof String) {
			try {
				confSB = SourceBean.fromXMLString( (String)conf );
			} catch (SourceBeanException e) {
				logger.error("Impossible to parse configuration block for DataSetProvider", e);
				throw new GeoEngineException("Impossible to parse configuration block for DataSetProvider", e);
			}
		} else {
			confSB = (SourceBean)conf;
		}
		
		if(confSB != null) {
			DataSetMetaData mataData = null;
			Map hierarchies = null;
			String selectedHierarchyName = null;
			String selectedLevelName = null;
						
			selectedHierarchyName = getSelectedHierarchyName( confSB );
			selectedLevelName = getSelectedLevelName( confSB );
			mataData = getMetaData( confSB );
			
			String stdHierarchy = (String)abstractDatasetProvider.getEnv().get(GeoEngineConstants.ENV_STD_HIERARCHY);
			SourceBean stdHierarchySB = null;
			try {
				stdHierarchySB = SourceBean.fromXMLString(stdHierarchy);
			} catch (SourceBeanException e) {
				e.printStackTrace();
			}
			hierarchies = getHierarchies( confSB, stdHierarchySB );				
			setLink(confSB, hierarchies);
			
			abstractDatasetProvider.setMetaData(mataData);
			abstractDatasetProvider.setHierarchies( hierarchies );
			abstractDatasetProvider.setSelectedHierarchyName(selectedHierarchyName);
			abstractDatasetProvider.setSelectedLevelName(selectedLevelName);
		}
	}
	
	/**
	 * Sets the link.
	 * 
	 * @param confSB the conf sb
	 * @param hierarchies the hierarchies
	 */
	private static void setLink(SourceBean confSB, Map hierarchies) {
		SourceBean corssNavConfSB = (SourceBean)confSB.getAttribute("CROSS_NAVIGATION");
		if( corssNavConfSB == null ) return;
		
		List links = corssNavConfSB.getAttributeAsList("LINK");
		for(int i = 0; i < links.size(); i++) {
			SourceBean linkSB = (SourceBean)links.get(i);
			String hierarchyName = (String)linkSB.getAttribute("HIERARCHY");
			Hierarchy hierarchy = (Hierarchy)hierarchies.get(hierarchyName);
			if(hierarchy == null) continue;
			String levelName = (String)linkSB.getAttribute("LEVEL");
			Hierarchy.Level level = (Hierarchy.Level)hierarchy.getLevel(levelName);
			if(level == null) continue;
			
			String measure = (String)linkSB.getAttribute("MEASURE");
			logger.debug("add link: " + hierarchyName + "->" + levelName + "->" + measure);
			
			Link link = new Link();
			List parameters = linkSB.getAttributeAsList("PARAM");
			for(int j = 0; j < parameters.size(); j++) {
				SourceBean parameterSB = (SourceBean)parameters.get(j);
				String type = (String)parameterSB.getAttribute("TYPE");
				String scope = (String)parameterSB.getAttribute("SCOPE");
				String name = (String)parameterSB.getAttribute("NAME");
				String value = (String)parameterSB.getAttribute("VALUE");
				link.addParameter(type, scope, name, value);
			}
			
			level.setLink(measure, link);
		}
	}

	/**
	 * Gets the selected level name.
	 * 
	 * @param confSB the conf sb
	 * 
	 * @return the selected level name
	 */
	private static String getSelectedLevelName(SourceBean confSB) {
		return (String)confSB.getAttribute("LEVEL");
	}

	/**
	 * Gets the selected hierarchy name.
	 * 
	 * @param confSB the conf sb
	 * 
	 * @return the selected hierarchy name
	 */
	private static String getSelectedHierarchyName(SourceBean confSB) {
		return (String)confSB.getAttribute("HIERARCHY");
	}

	/**
	 * Gets the meta data.
	 * 
	 * @param confSB the conf sb
	 * 
	 * @return the meta data
	 */
	public static DataSetMetaData getMetaData(SourceBean confSB) {
		DataSetMetaData metaData;
		SourceBean metadataSB;
		List columns;
		SourceBean columnSB;
		String columnName;
		String columnType;
		
		logger.debug("IN");
		
		metaData = null;
		metadataSB = null;
		
		try {
		
			metadataSB = (SourceBean)confSB.getAttribute(GeoEngineConstants.METADATA_TAG);
			if(metadataSB == null) {
				logger.warn("Cannot find metadata configuration settings: tag name " + GeoEngineConstants.METADATA_TAG);
				logger.info("Metadata configuration settings must be injected at execution time");
				return null;
			}
			
			
			logger.debug("Metadata block has been found in configuration");
			
			metaData = new DataSetMetaData();
			
			columns = metadataSB.getAttributeAsList(GeoEngineConstants.COLUMN_TAG);
			logger.debug("Metadata block contains settings for [" + columns + "] columns");
			
			for(int i = 0; i < columns.size(); i++) {
				columnSB = null;
				try {
					logger.debug("Parsing column  [" + i + "]");
					columnSB = (SourceBean)columns.get(i);
					
					columnName = (String)columnSB.getAttribute(GeoEngineConstants.COLUMN_NAME_ATTRIBUTE);
					logger.debug("Column [" + i + "] name [" + columnName + "]");
					Assert.assertNotNull(columnName, "Attribute [" + GeoEngineConstants.COLUMN_NAME_ATTRIBUTE + "] of tag [" + GeoEngineConstants.COLUMN_TAG + "] cannot be null");
					
					columnType = (String)columnSB.getAttribute(GeoEngineConstants.COLUMN_TYPE_ATTRIBUTE);	
					logger.debug("Column [" + i + "] name [" + columnType + "]");
					Assert.assertNotNull(columnName, "Attribute [" + GeoEngineConstants.COLUMN_TYPE_ATTRIBUTE + "] of tag [" + GeoEngineConstants.COLUMN_TAG + "] cannot be null");
									
					metaData.addColumn(columnName);
					metaData.setColumnProperty(columnName, "column_id", columnName);
					metaData.setColumnProperty(columnName, "type", columnType);
					
					if( columnType.equalsIgnoreCase("geoid")) {
						String hierarchyName = (String)columnSB.getAttribute(GeoEngineConstants.COLUMN_HIERARCHY_REF_ATTRIBUTE);
						logger.debug("Column [" + i + "] attribute [" + GeoEngineConstants.COLUMN_HIERARCHY_REF_ATTRIBUTE + "]is equal to [" + hierarchyName + "]");
						Assert.assertNotNull(hierarchyName, "Attribute [" + GeoEngineConstants.COLUMN_HIERARCHY_REF_ATTRIBUTE + "] of tag [" + GeoEngineConstants.COLUMN_TAG + "] cannot be null");
						metaData.setColumnProperty(columnName, "hierarchy", hierarchyName);	
						
						String levelName = (String)columnSB.getAttribute(GeoEngineConstants.COLUMN_LEVEL_REF_ATTRIBUTE);
						logger.debug("Column [" + i + "] attribute [" + GeoEngineConstants.COLUMN_LEVEL_REF_ATTRIBUTE + "]is equal to [" + levelName + "]");
						Assert.assertNotNull(hierarchyName, "Attribute [" + GeoEngineConstants.COLUMN_LEVEL_REF_ATTRIBUTE + "] of tag [" + GeoEngineConstants.COLUMN_TAG + "] cannot be null");
						metaData.setColumnProperty(columnName, "level", levelName);
					} else if( columnType.equalsIgnoreCase("measure")) {
						String aggFunc = (String)columnSB.getAttribute(GeoEngineConstants.COLUMN_AFUNC_REF_ATTRIBUTE);
						logger.debug("Column [" + i + "] attribute [" + GeoEngineConstants.COLUMN_AFUNC_REF_ATTRIBUTE + "]is equal to [" + aggFunc + "]");
						Assert.assertNotNull(aggFunc, "Attribute [" + GeoEngineConstants.COLUMN_AFUNC_REF_ATTRIBUTE + "] of tag [" + GeoEngineConstants.COLUMN_TAG + "] cannot be null");
						metaData.setColumnProperty(columnName, "func", aggFunc);				
					}
					logger.debug("Column  [" + i + "] parsed succesfully");
				} catch (Throwable t) {
					throw new GeoEngineException("An error occurred while parsing column [" + columnSB + "]", t);
				}
			}
		
		} catch (Throwable t) {
			GeoEngineException e = new GeoEngineException("An error occurred while parsing metadata [" + metadataSB + "]", t);
			e.addHint("Download document template and fix the problem that have coused the syntax/semantic error");
			throw e;
		} finally {
			logger.debug("OUT");
		}
		
		return metaData;
	}
	
	/**
	 * Gets the hierarchies.
	 * 
	 * @param confSB the conf sb
	 * @param sdtHierarchySB the sdt hierarchy sb
	 * 
	 * @return the hierarchies
	 */
	public static Map getHierarchies(SourceBean confSB, SourceBean sdtHierarchySB) {
		Map hierarchies = null;
		
		SourceBean hierarchiesSB = (SourceBean)confSB.getAttribute(GeoEngineConstants.HIERARCHIES_TAG);
		if(hierarchiesSB == null) {
			logger.warn("Cannot find hierachies configuration settings: tag name " + GeoEngineConstants.HIERARCHIES_TAG);
			logger.info("Hierarchies configuration settings must be injected at execution time");
			return null;
		}
		
		hierarchies = new HashMap();
		
		Hierarchy hierarchy = null;
		List hierarchyList = hierarchiesSB.getAttributeAsList(GeoEngineConstants.HIERARCHY_TAG);
		for(int i = 0; i < hierarchyList.size(); i++) {
			
			SourceBean hierarchySB = (SourceBean)hierarchyList.get(i);
			String name = (String)hierarchySB.getAttribute(GeoEngineConstants.HIERARCHY_NAME_ATTRIBUTE);
			String type = (String)hierarchySB.getAttribute(GeoEngineConstants.HIERARCHY_TYPE_ATTRIBUTE);
			List levelList = null;
			if(type.equalsIgnoreCase("custom"))  {
				hierarchy = new Hierarchy(name);
				levelList =  hierarchySB.getAttributeAsList(GeoEngineConstants.HIERARCHY_LEVEL_TAG);
			} else {
				if(sdtHierarchySB!= null) {
					hierarchySB = sdtHierarchySB;
					String table = (String)hierarchySB.getAttribute(GeoEngineConstants.HIERARCHY_TABLE_ATRRIBUTE);
					hierarchy = new Hierarchy(name, table);
					levelList = hierarchySB.getAttributeAsList(GeoEngineConstants.HIERARCHY_LEVEL_TAG);
				} else {
					logger.error("Impossible to include default hierarchy");
				}
			}
			
			for(int j = 0; j < levelList.size(); j++) {
				SourceBean levelSB = (SourceBean)levelList.get(j);
				String lname = (String)levelSB.getAttribute(GeoEngineConstants.HIERARCHY_LEVEL_NAME_ATRRIBUTE);
				String lcolumnid = (String)levelSB.getAttribute(GeoEngineConstants.HIERARCHY_LEVEL_COLUMN_ID_ATRRIBUTE);
				String lcolumndesc = (String)levelSB.getAttribute(GeoEngineConstants.HIERARCHY_LEVEL_COLUMN_DESC_ATRRIBUTE);
				String lfeaturename = (String)levelSB.getAttribute(GeoEngineConstants.HIERARCHY_LEVEL_FEATURE_NAME_ATRRIBUTE);
				Hierarchy.Level level = new Hierarchy.Level();
				level.setName(lname);
				level.setColumnId(lcolumnid);
				level.setColumnDesc(lcolumndesc);
				level.setFeatureName(lfeaturename);
				hierarchy.addLevel(level);
			}
			
			hierarchies.put(hierarchy.getName(), hierarchy);
		}	
		
		return hierarchies;
	}
}
