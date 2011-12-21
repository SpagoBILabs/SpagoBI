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
package it.eng.spagobi.engines.geo.component;

import it.eng.spagobi.engines.geo.GeoEngineException;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractGeoEngineComponent.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractGeoEngineComponent implements IGeoEngineComponent {
	
	/** The conf. */
	Object conf;
	
	/** The env. */
	Map env;
	
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.IGeoEngineComponent#init(java.lang.Object)
	 */
	public void init(Object conf) throws GeoEngineException {
		this.conf = conf;	
	}
	
	/**
	 * Gets the conf.
	 * 
	 * @return the conf
	 */
	protected Object getConf() {
		return conf;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.geo.IGeoEngineComponent#setEnv(java.util.Map)
	 */
	public void setEnv(Map env) {
		this.env = env; 	
	}
	
	/**
	 * Gets the env.
	 * 
	 * @return the env
	 */
	public Map getEnv() {
		return env;
	}

}
