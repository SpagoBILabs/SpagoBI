/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.datamart.provider;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.component.AbstractGeoEngineComponent;
import it.eng.spagobi.engines.geo.datamart.provider.configurator.AbstractDataMartProviderConfigurator;
import it.eng.spagobi.engines.geo.dataset.DataMart;
import it.eng.spagobi.engines.geo.dataset.DataSetMetaData;
import it.eng.spagobi.engines.geo.dataset.provider.Hierarchy;
import it.eng.spagobi.engines.geo.dataset.provider.Hierarchy.Level;

import java.util.Map;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractDatasetProvider.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractDataMartProvider extends AbstractGeoEngineComponent implements IDataMartProvider {

	/** The meta data. */
	private DataSetMetaData metaData;	
	
	/** The hierarchies. */
	private Map hierarchies;	
	
	/** The selected hierarchy name. */
	private String selectedHierarchyName;
	
	/** The selected level name. */
	private String selectedLevelName;
	
    /**
     * Instantiates a new abstract dataset provider.
     */
    public AbstractDataMartProvider() {
        super();
    }   
    
    /* (non-Javadoc)
     * @see it.eng.spagobi.engines.geo.AbstractGeoEngineComponent#init(java.lang.Object)
     */
    public void init(Object conf) throws GeoEngineException {
		super.init(conf);
		AbstractDataMartProviderConfigurator.configure( this, getConf() );
	}
  
    /* (non-Javadoc)
     * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#getDataSet()
     */
    public DataMart getDataMart() throws GeoEngineException {
        return null;
    }

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#getDataDetails(java.lang.String)
	 */
	public SourceBean getDataDetails(String filterValue) {
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#getHierarchyNames()
	 */
	public Set getHierarchyNames() {
		if(hierarchies != null) {
			return hierarchies.keySet();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#getHierarchy(java.lang.String)
	 */
	public Hierarchy getHierarchy(String name) {
		if(hierarchies != null) {
			return (Hierarchy)hierarchies.get( name );
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#getSelectedHierarchy()
	 */
	public Hierarchy getSelectedHierarchy() {
		if(hierarchies != null) {
			return (Hierarchy)hierarchies.get( selectedHierarchyName );
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#getSelectedLevel()
	 */
	public Hierarchy.Level getSelectedLevel() {
		Hierarchy selectedHierarchy = getSelectedHierarchy();
		if(selectedHierarchy != null) {
			return selectedHierarchy.getLevel( selectedLevelName );
		}
		return null;
	}

	/**
	 * Gets the meta data.
	 * 
	 * @return the meta data
	 */
	public DataSetMetaData getMetaData() {
		return metaData;
	}

	/**
	 * Sets the meta data.
	 * 
	 * @param metaData the new meta data
	 */
	public void setMetaData(DataSetMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * Sets the hierarchies.
	 * 
	 * @param hierarchies the new hierarchies
	 */
	public void setHierarchies(Map hierarchies) {
		this.hierarchies = hierarchies;
		
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#getSelectedHierarchyName()
	 */
	public String getSelectedHierarchyName() {
		return selectedHierarchyName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#setSelectedHierarchyName(java.lang.String)
	 */
	public void setSelectedHierarchyName(String selectedHierarchyName) {
		this.selectedHierarchyName = selectedHierarchyName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#getSelectedLevelName()
	 */
	public String getSelectedLevelName() {
		return selectedLevelName;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.dataset.provider.IDatasetProvider#setSelectedLevelName(java.lang.String)
	 */
	public void setSelectedLevelName(String selectedLevelName) {
		this.selectedLevelName = selectedLevelName;
	}
}
