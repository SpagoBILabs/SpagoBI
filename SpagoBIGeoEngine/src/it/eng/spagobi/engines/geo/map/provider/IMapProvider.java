/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.map.provider;

import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.component.IGeoEngineComponent;

import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.svg.SVGDocument;

// TODO: Auto-generated Javadoc
/**
 * The Interface IMapProvider.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IMapProvider extends IGeoEngineComponent {
    
	
	/**
	 * Gets the sVG map stream reader.
	 * 
	 * @return the sVG map stream reader
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	XMLStreamReader getSVGMapStreamReader() throws GeoEngineException;
	
	/**
	 * Gets the sVG map stream reader.
	 * 
	 * @param mapName the map name
	 * 
	 * @return the sVG map stream reader
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	XMLStreamReader getSVGMapStreamReader(String mapName) throws GeoEngineException;
	
	/**
	 * Gets the sVG map dom document.
	 * 
	 * @return the sVG map dom document
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	SVGDocument getSVGMapDOMDocument() throws GeoEngineException;	
	
	/**
	 * Gets the sVG map dom document.
	 * 
	 * @param mapName the map name
	 * 
	 * @return the sVG map dom document
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	SVGDocument getSVGMapDOMDocument(String mapName) throws GeoEngineException;
	
	/**
	 * Gets the selected map name.
	 * 
	 * @return the selected map name
	 */
	String getSelectedMapName();
	
	/**
	 * Sets the selected map name.
	 * 
	 * @param mapName the new selected map name
	 */
	void setSelectedMapName(String mapName);
	
	/**
	 * Gets the map names by feature.
	 * 
	 * @param featureName the feature name
	 * 
	 * @return the map names by feature
	 * 
	 * @throws Exception the exception
	 */
	List getMapNamesByFeature(String featureName) throws Exception;
	
	/**
	 * Gets the feature names in map.
	 * 
	 * @param mapName the map name
	 * 
	 * @return the feature names in map
	 * 
	 * @throws Exception the exception
	 */
	List getFeatureNamesInMap(String mapName) throws Exception;
}
