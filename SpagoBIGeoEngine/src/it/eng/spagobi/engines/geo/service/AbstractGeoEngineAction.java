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
package it.eng.spagobi.engines.geo.service;

import it.eng.spagobi.engines.geo.GeoEngineInstance;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.EngineConstants;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractGeoEngineAction.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractGeoEngineAction extends AbstractEngineAction {
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(AbstractGeoEngineAction.class);
    	
		
	/**
	 * Gets the geo engine instance.
	 * 
	 * @return the geo engine instance
	 */
	public GeoEngineInstance getGeoEngineInstance() {
		return (GeoEngineInstance)getEngineInstance();
	}
	
	
}
