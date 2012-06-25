/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.geo.component.GeoEngineComponentFactory;
import it.eng.spagobi.engines.geo.datamart.provider.IDataMartProvider;
import it.eng.spagobi.engines.geo.dataset.provider.Hierarchy;
import it.eng.spagobi.engines.geo.map.provider.IMapProvider;
import it.eng.spagobi.engines.geo.map.renderer.IMapRenderer;
import it.eng.spagobi.engines.geo.map.renderer.Layer;
import it.eng.spagobi.utilities.engines.AbstractEngineInstance;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;

// TODO: Auto-generated Javadoc
/**
 * The Class GeoEngineInstance.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GeoEngineInstance extends AbstractEngineInstance {
	
	/** The map provider. */
	IMapProvider mapProvider;
	
	/** The dataset provider. */
	IDataMartProvider dataMartProvider;
	
	/** The map renderer. */
	IMapRenderer mapRenderer;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(GeoEngineInstance.class);
	
    
	/**
	 * Instantiates a new geo engine instance.
	 * 
	 * @param mapProvider the map provider
	 * @param datasetProvider the dataset provider
	 * @param mapRenderer the map renderer
	 */
	protected GeoEngineInstance(IMapProvider mapProvider, IDataMartProvider datasetProvider, IMapRenderer mapRenderer) {
		super();
		
		logger.debug("IN");
		
		setMapProvider( mapProvider );
		setDataMartProvider( datasetProvider );
		setMapRenderer( mapRenderer );
		logger.info("MapProvider class: " + getMapProvider().getClass().getName());
		logger.info("DatasetProvider class: " + getDataMartProvider().getClass().getName());
		logger.info("MapRenderer class: " + getMapRenderer().getClass().getName());
		
		logger.debug("OUT");
	}
	
	/**
	 * Instantiates a new geo engine instance.
	 * 
	 * @param template the template
	 * @param env the env
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	protected GeoEngineInstance(SourceBean template, Map env) {
		super( env );
		
		logger.debug("IN");
			
		setMapProvider( GeoEngineComponentFactory.buildMapProvider( template, env ) );
		setDataMartProvider( GeoEngineComponentFactory.buildDataMartProvider(template, env) );
		setMapRenderer( GeoEngineComponentFactory.buildMapRenderer(template, env) );
				
		logger.info("MapProvider class: " + getMapProvider().getClass().getName());
		logger.info("DatasetProvider class: " + getDataMartProvider().getClass().getName());
		logger.info("MapRenderer class: " + getMapRenderer().getClass().getName());
		
		validate();
		
		logger.debug("OUT");
	}

	/**
	 * Validate.
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public void validate() {
		String selectedHierarchyName = getDataMartProvider().getSelectedHierarchyName();
		if(selectedHierarchyName == null) {
			GeoEngineException geoException;
			logger.error("Select hierarchy name is not defined");
			String description = "Select hierarchy name is not defined";
			geoException =  new GeoEngineException("Configuration error");
			geoException.setDescription(description);
			throw geoException;
		}
		
		Hierarchy selectedHierarchy = getDataMartProvider().getHierarchy(selectedHierarchyName);
		if(selectedHierarchy == null) {
			GeoEngineException geoException;
			logger.error("Selected hierarchy [" + selectedHierarchyName + "] does not exist");
			String description = "Selected hierarchy [" + selectedHierarchyName + "] does not exist";
			List hints = new ArrayList();
			hints.add("Check if hierarchy name is correct");
			hints.add("Check if a hierarchy named " + selectedHierarchyName +"  has been defined. Defined hierarachy are: " 
					+ Arrays.toString( getDataMartProvider().getHierarchyNames().toArray()) );
			geoException =  new GeoEngineException("Configuration error");
			geoException.setDescription(description);
			geoException.setHints(hints);
			throw geoException;
		}
		
		String selectedLevelName = getDataMartProvider().getSelectedLevelName();
		if(selectedLevelName == null) {
			GeoEngineException geoException;
			logger.error("Select level name is not defined");
			String description = "Select level name is not defined";
			geoException =  new GeoEngineException("Configuration error");
			geoException.setDescription(description);
			throw geoException;
		}
		
		Hierarchy.Level selectedLevel = selectedHierarchy.getLevel(selectedLevelName);
		if(selectedLevel == null) {
			GeoEngineException geoException;
			logger.error("Selected level [" + selectedHierarchyName + "] does not exist in selected hierarchy [" + selectedHierarchyName + "]");
			String description = "Selected level [" + selectedHierarchyName + "] does not exist in selected hierarchy [" + selectedHierarchyName + "]";
			List hints = new ArrayList();
			hints.add("Check if level name is correct");
			hints.add("Check if a level named " + selectedLevelName +"  is defined into hierarachy " + selectedHierarchyName + ". " +
					"Defined level are: " 
					+ Arrays.toString( selectedHierarchy.getLevelNames().toArray()) );
			geoException =  new GeoEngineException("Configuration error");
			geoException.setDescription(description);
			geoException.setHints(hints);
			throw geoException;
		}
		
	}
	
	

	public IEngineAnalysisState getAnalysisState() {
		GeoEngineAnalysisState analysisState = null;
		
		analysisState = new GeoEngineAnalysisState();
		analysisState.setSelectedMapName( getMapProvider().getSelectedMapName() );
		analysisState.setSelectedHierarchyName( getDataMartProvider().getSelectedHierarchyName() );
		analysisState.setSelectedLevelName( getDataMartProvider().getSelectedLevelName() );
		String selectedLayers = null;
		String[] layerNames = getMapRenderer().getLayerNames();
		if(layerNames.length > 0) selectedLayers = layerNames[0];
		for(int i = 1; i < layerNames.length; i++) {
			 selectedLayers += "," + layerNames[i];
		}
		analysisState.setSelectedLayers( selectedLayers );
		
		return analysisState;
	}
	
	/**
	 * Sets the analysis state.
	 * 
	 * @param geoAnalysisState the new analysis state
	 */
	public void setAnalysisState(IEngineAnalysisState analysisState) {	
		GeoEngineAnalysisState geoAnalysisState = null;
		String selectedHiearchyName = null;
		String selectedLevelName = null;
		String selectedMapName = null;
		String selectedLayerNames = null;
		
		logger.debug("IN");
		
		geoAnalysisState = (GeoEngineAnalysisState)analysisState;
		selectedHiearchyName = geoAnalysisState.getSelectedHierarchy();
		selectedLevelName = geoAnalysisState.getSelectedHierarchyLevel();
		selectedMapName = geoAnalysisState.getSelectedMapName();
		selectedLayerNames = geoAnalysisState.getSelectedLayers();
		
		if(selectedHiearchyName != null) {
			logger.debug("Previous selected hierarchy: " + getDataMartProvider().getSelectedHierarchyName());
			getDataMartProvider().setSelectedHierarchyName(selectedHiearchyName);
			logger.debug("New selected hierarchy: " + getDataMartProvider().getSelectedHierarchyName());
		}
		
		if(selectedLevelName != null) {
			logger.debug("Previous selected level: " + getDataMartProvider().getSelectedLevelName());
			getDataMartProvider().setSelectedLevelName(selectedLevelName);
			logger.debug("New selected level: " + getDataMartProvider().getSelectedLevelName());			
		}
		
		if(selectedMapName != null) {
			getMapProvider().setSelectedMapName(selectedMapName);
		}	
		
		if(selectedLayerNames != null) {
			logger.debug("Previous selected layers: " + Arrays.toString( getMapRenderer().getLayerNames() ) );
			getMapRenderer().clearLayers();
			String[] layers = selectedLayerNames.split(",");			
			for(int i = 0; i < layers.length; i++) {				
				Layer layer = getMapRenderer().getLayer(layers[i]);
				if(layer != null) {
					layer.setSelected(true);
				} else {
					layer = new Layer();
					layer.setName(layers[i]);
					layer.setDescription(layers[i]);
					layer.setSelected(true);
					getMapRenderer().addLayer(layer);
				}
			}
			logger.debug("New selected layers: " + Arrays.toString( getMapRenderer().getLayerNames() ) );
		}
		
		logger.debug("OUT");
	}
	
	
	
	
	
	
	/**
	 * Render map.
	 * 
	 * @param format the format
	 * 
	 * @return the file
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public File renderMap(String format) throws GeoEngineException {
		return getMapRenderer().renderMap( getMapProvider(), getDataMartProvider(), format);
	}
	
	/**
	 * Gets the map provider.
	 * 
	 * @return the map provider
	 */
	public IMapProvider getMapProvider() {
		return mapProvider;
	}

	/**
	 * Sets the map provider.
	 * 
	 * @param mapProvider the new map provider
	 */
	protected void setMapProvider(IMapProvider mapProvider) {
		this.mapProvider = mapProvider;
	}

	/**
	 * Gets the map renderer.
	 * 
	 * @return the map renderer
	 */
	public IMapRenderer getMapRenderer() {
		return mapRenderer;
	}

	/**
	 * Sets the map renderer.
	 * 
	 * @param mapRenderer the new map renderer
	 */
	protected void setMapRenderer(IMapRenderer mapRenderer) {
		this.mapRenderer = mapRenderer;
	}

	public IDataMartProvider getDataMartProvider() {
		return dataMartProvider;
	}

	public void setDataMartProvider(IDataMartProvider dataMartProvider) {
		this.dataMartProvider = dataMartProvider;
	}
}
