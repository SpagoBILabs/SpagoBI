/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.map.renderer;

import java.io.File;

import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.component.IGeoEngineComponent;
import it.eng.spagobi.engines.geo.datamart.provider.IDataMartProvider;
import it.eng.spagobi.engines.geo.map.provider.IMapProvider;

// TODO: Auto-generated Javadoc
/**
 * The Interface IMapRenderer.
 * 
 * @author Andrea Gioia
 */
public interface IMapRenderer  extends IGeoEngineComponent {
	
	/**
	 * Render map.
	 * 
	 * @param mapProvider the map provider
	 * @param datamartProvider the datamart provider
	 * @param outputFormat the output format
	 * 
	 * @return the file
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public File renderMap(IMapProvider mapProvider, 
			IDataMartProvider datamartProvider,
			  String outputFormat) throws GeoEngineException;
			  
	
	/**
	 * Render map.
	 * 
	 * @param mapProvider the map provider
	 * @param datamartProvider the datamart provider
	 * 
	 * @return the file
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	File renderMap(IMapProvider mapProvider, IDataMartProvider datamartProvider) throws GeoEngineException;
	
	/**
	 * Gets the layer names.
	 * 
	 * @return the layer names
	 */
	public String[] getLayerNames();
	
	/**
	 * Gets the layer.
	 * 
	 * @param layerName the layer name
	 * 
	 * @return the layer
	 */
	public Layer getLayer(String layerName);
	
	/**
	 * Adds the layer.
	 * 
	 * @param layer the layer
	 */
	public void addLayer(Layer layer);
	
	/**
	 * Clear layers.
	 */
	void clearLayers();
	
	void setSelectedMeasureName(String selectedMeasureName);
}
