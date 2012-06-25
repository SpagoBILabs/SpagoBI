/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo;

import it.eng.spagobi.utilities.engines.EngineAnalysisState;

import java.util.Iterator;
import java.util.List;

/**
 * The Class GeoEngineAnalysisState.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GeoEngineAnalysisState extends EngineAnalysisState {
	
	// property names 
	public static final String SELECTED_HIERARCHY = "selected_hierachy";
	public static final String SELECTED_HIERARCHY_LEVEL= "selected_hierarchy_level";
	public static final String SELECTED_MAP = "selected_map";
	public static final String SELECTED_LAYERS = "selected_layers";
	
	
	
	public GeoEngineAnalysisState( ) {
		super();
	}
		
	public void load(byte[] rowData) {
		
		String str = null;
		String[] chuncks = null;
			
		str = new String( rowData );
		chuncks = str.split(";");
		for(int i = 0; i < chuncks.length; i++) {
			String[] propChunk = chuncks[i].split("=");
			String pName = propChunk[0];
			String pValue = propChunk[1];
			setProperty(pName, pValue);
		}
	}

	
	public byte[] store() {
		StringBuffer buffer = new StringBuffer();
		Iterator it = propertyNameSet().iterator();
		while( it.hasNext() ) {
			String pName = (String)it.next();
			String pValue = (String)getProperty( pName );
			buffer.append(pName + "=" + pValue + ";");
		}
		
		return buffer.toString().getBytes();
	}
	
	/**
	 * Gets the selected hierarchy.
	 * 
	 * @return the selected hierarchy
	 */
	public String getSelectedHierarchy() {
		return (String)getProperty(SELECTED_HIERARCHY);
	}
	
	/**
	 * Sets the selected hierarchy name.
	 * 
	 * @param hierarchyName the new selected hierarchy name
	 */
	public void setSelectedHierarchyName(String hierarchyName) {
		setProperty(SELECTED_HIERARCHY, hierarchyName);
	}

	/**
	 * Gets the selected hierarchy level.
	 * 
	 * @return the selected hierarchy level
	 */
	public String getSelectedHierarchyLevel() {
		return (String)getProperty(SELECTED_HIERARCHY_LEVEL);		
	}
	
	/**
	 * Sets the selected level name.
	 * 
	 * @param levelName the new selected level name
	 */
	public void setSelectedLevelName(String levelName) {
		setProperty(SELECTED_HIERARCHY_LEVEL, levelName);		
	}

	/**
	 * Gets the selected map name.
	 * 
	 * @return the selected map name
	 */
	public String getSelectedMapName() {
		return (String)getProperty(SELECTED_MAP);
	}
	
	/**
	 * Sets the selected map name.
	 * 
	 * @param mapName the new selected map name
	 */
	public void setSelectedMapName(String mapName) {
		setProperty(SELECTED_MAP, mapName);
	}

	/**
	 * Gets the selected layers.
	 * 
	 * @return the selected layers
	 */
	public String getSelectedLayers() {
		return (String)getProperty(SELECTED_LAYERS);
	}
	
	/**
	 * Sets the selected layers.
	 * 
	 * @param layers the new selected layers
	 */
	public void setSelectedLayers(String layers) {
		setProperty(SELECTED_LAYERS, layers);
	}
	
	/**
	 * Sets the selected layers.
	 * 
	 * @param layers the new selected layers
	 */
	public void setSelectedLayers(List layers) {
		String layersStr = null;
		
		if(layers.size() > 0) layersStr = (String)layers.get(0);
		for(int i = 1; i < layers.size(); i++) {
			layersStr += "," + (String)layers.get(i);
		}
		
		if(layersStr != null){
			setSelectedLayers(layersStr);
		}
	}
}
