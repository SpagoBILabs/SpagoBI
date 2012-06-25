/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.map.provider;

import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.component.AbstractGeoEngineComponent;
import it.eng.spagobi.engines.geo.map.provider.configurator.AbstractMapProviderConfigurator;

import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.w3c.dom.svg.SVGDocument;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMapProvider.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractMapProvider extends AbstractGeoEngineComponent implements IMapProvider {

	/** The selected map name. */
	private String selectedMapName;
		
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(AbstractMapProvider.class);
	
	
	/**
	 * Instantiates a new abstract map provider.
	 */
	public AbstractMapProvider() {
        super();
    }
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.AbstractGeoEngineComponent#init(java.lang.Object)
	 */
	public void init(Object conf) throws GeoEngineException {
		super.init(conf);
		AbstractMapProviderConfigurator.configure( this, getConf() );
	}
	
   
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.IMapProvider#getSVGMapStreamReader()
	 */
	public XMLStreamReader getSVGMapStreamReader() throws GeoEngineException {
    	return getSVGMapStreamReader(selectedMapName);
    }
	
    /* (non-Javadoc)
     * @see it.eng.spagobi.engines.geo.map.provider.IMapProvider#getSVGMapStreamReader(java.lang.String)
     */
    public XMLStreamReader getSVGMapStreamReader(String mapName) throws GeoEngineException {
    	return null;
    }
    
    /* (non-Javadoc)
     * @see it.eng.spagobi.engines.geo.map.provider.IMapProvider#getSVGMapDOMDocument()
     */
    public SVGDocument getSVGMapDOMDocument() throws GeoEngineException {
		return getSVGMapDOMDocument(selectedMapName);
	}
    
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.IMapProvider#getSVGMapDOMDocument(java.lang.String)
	 */
	public SVGDocument getSVGMapDOMDocument(String mapName) throws GeoEngineException {
		return null;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.IMapProvider#getSelectedMapName()
	 */
	public String getSelectedMapName() {
		return selectedMapName;
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.IMapProvider#setSelectedMapName(java.lang.String)
	 */
	public void setSelectedMapName(String selectedMapName) {
		this.selectedMapName = selectedMapName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.IMapProvider#getMapNamesByFeature(java.lang.String)
	 */
	public List getMapNamesByFeature(String featureName) throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.map.provider.IMapProvider#getFeatureNamesInMap(java.lang.String)
	 */
	public List getFeatureNamesInMap(String mapName) throws Exception {
		return null;
	}

	
   

}
