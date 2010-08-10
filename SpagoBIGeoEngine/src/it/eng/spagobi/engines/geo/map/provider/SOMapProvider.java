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

import it.eng.spagobi.engines.geo.GeoEngineConfig;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.map.provider.configurator.SOMapProviderConfigurator;
import it.eng.spagobi.engines.geo.map.utils.SVGMapLoader;
import it.eng.spagobi.services.content.bo.Content;
import it.eng.spagobi.utilities.callbacks.mapcatalogue.MapCatalogueAccessUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.w3c.dom.svg.SVGDocument;

// TODO: Auto-generated Javadoc
/**
 * The Class SOMapProvider.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class SOMapProvider extends AbstractMapProvider {

	/** The map catalogue service proxy. */
	private MapCatalogueAccessUtils mapCatalogueServiceProxy;
	
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(SOMapProvider.class);
	
	
	/**
	 * Instantiates a new sO map provider.
	 */
	public SOMapProvider() {
		 super();
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.AbstractMapProvider#init(java.lang.Object)
	 */
	public void init(Object conf) throws GeoEngineException {
		super.init(conf);
		SOMapProviderConfigurator.configure( this, getConf() );
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.AbstractMapProvider#getSVGMapDOMDocument(java.lang.String)
	 */
	public SVGDocument getSVGMapDOMDocument(String mapName) throws GeoEngineException {
	    logger.debug("IN.mapName="+mapName);
		SVGDocument svgDocument = null;		
		Content map = null;		
		
			
		try {
			map = mapCatalogueServiceProxy.readMap(mapName);			
		} catch (Exception e) {
			logger.error("An error occurred while invoking mapCatalogueService method: readMap()");
			throw new GeoEngineException("Impossible to load map from map catalogue", e);
		}
		
		try {
			
			svgDocument = SVGMapLoader.loadMapAsDocument(map);
		} catch (IOException e) {
			logger.error("Impossible to load map from map catalogue");
			throw new GeoEngineException("Impossible to load map from map catalogue", e);
		}
		
		
		logger.debug("OUT");
		
		return svgDocument;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.AbstractMapProvider#getSVGMapStreamReader(java.lang.String)
	 */
	public XMLStreamReader getSVGMapStreamReader(String mapName) throws GeoEngineException {
		XMLStreamReader streamReader = null;
		String mapUrl = null;		
		
		
		try {
			mapUrl = mapCatalogueServiceProxy.getMapUrl(mapName);
		} catch (Exception e) {
			logger.error("An error occurred while invoking mapCatalogueService method: getMapUrl()");
			throw new GeoEngineException("Impossible to load map from url: " + mapUrl, e);
		}
		
		try {
			streamReader = SVGMapLoader.getMapAsStream(mapUrl);
		} catch (XMLStreamException e) {
			logger.error("An error occurred while processing xml stream of the svg map");
			throw new GeoEngineException("An error occurred while processing xml stream of the svg map", e);
		} catch (FileNotFoundException e) {
			logger.error("Map file not found at url: " + mapUrl);
			throw new GeoEngineException("Map file not found at url: " + mapUrl, e);
		}
		
		
		return streamReader;
	}

	/**
	 * Gets the map catalogue service proxy.
	 * 
	 * @return the map catalogue service proxy
	 */
	public MapCatalogueAccessUtils getMapCatalogueServiceProxy() {
		return mapCatalogueServiceProxy;
	}

	/**
	 * Sets the map catalogue service proxy.
	 * 
	 * @param mapCatalogueServiceProxy the new map catalogue service proxy
	 */
	public void setMapCatalogueServiceProxy(
			MapCatalogueAccessUtils mapCatalogueServiceProxy) {
		this.mapCatalogueServiceProxy = mapCatalogueServiceProxy;
	}

	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.AbstractMapProvider#getMapNamesByFeature(java.lang.String)
	 */
	public List getMapNamesByFeature(String featureName) throws Exception {
		 return getMapCatalogueServiceProxy().getMapNamesByFeature(featureName);
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.AbstractMapProvider#getFeatureNamesInMap(java.lang.String)
	 */
	public List getFeatureNamesInMap(String mapName) throws Exception{
		 return getMapCatalogueServiceProxy().getFeatureNamesInMap(mapName);
	}
}