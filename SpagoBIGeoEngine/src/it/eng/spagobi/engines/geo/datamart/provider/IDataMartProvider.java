/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.datamart.provider;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.component.IGeoEngineComponent;
import it.eng.spagobi.engines.geo.dataset.DataMart;
import it.eng.spagobi.engines.geo.dataset.provider.Hierarchy;

import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IDataMartProvider extends IGeoEngineComponent {
    
    /**
     * Gets the data set.
     * 
     * @return the data set
     * 
     * @throws GeoEngineException the geo engine exception
     */
    DataMart getDataMart() throws GeoEngineException;    
    
    /**
     * Gets the data details.
     * 
     * @param filterValue the filter value
     * 
     * @return the data details
     * 
     * @throws GeoEngineException the geo engine exception
     */
    SourceBean getDataDetails(String filterValue) throws GeoEngineException;   
    
    /**
     * Sets the selected hierarchy name.
     * 
     * @param hierarchyName the new selected hierarchy name
     */
    void setSelectedHierarchyName(String hierarchyName);
    
    /**
     * Gets the selected hierarchy name.
     * 
     * @return the selected hierarchy name
     */
    String getSelectedHierarchyName();
    
    /**
     * Sets the selected level name.
     * 
     * @param levelName the new selected level name
     */
    void setSelectedLevelName(String levelName);
    
    /**
     * Gets the selected level name.
     * 
     * @return the selected level name
     */
    String getSelectedLevelName();
    
    /**
     * Gets the hierarchy names.
     * 
     * @return the hierarchy names
     */
    Set getHierarchyNames();
    
    /**
     * Gets the hierarchy.
     * 
     * @param name the name
     * 
     * @return the hierarchy
     */
    Hierarchy getHierarchy(String name);    
    
    /**
     * Gets the selected hierarchy.
     * 
     * @return the selected hierarchy
     */
    Hierarchy getSelectedHierarchy();
    
    /**
     * Gets the selected level.
     * 
     * @return the selected level
     */
    Hierarchy.Level getSelectedLevel();
}
