/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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
