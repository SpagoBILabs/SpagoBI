/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
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
