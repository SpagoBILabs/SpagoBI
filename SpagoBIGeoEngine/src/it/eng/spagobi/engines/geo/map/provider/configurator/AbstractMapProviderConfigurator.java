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
package it.eng.spagobi.engines.geo.map.provider.configurator;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.engines.geo.GeoEngineConstants;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.map.provider.AbstractMapProvider;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMapProviderConfigurator.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class AbstractMapProviderConfigurator {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(AbstractMapProviderConfigurator.class);
	
	
	/**
	 * Configure.
	 * 
	 * @param abstractMapProvider the abstract map provider
	 * @param conf the conf
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public static void configure(AbstractMapProvider abstractMapProvider, Object conf) throws GeoEngineException {
		SourceBean confSB = null;
		
		if(conf instanceof String) {
			try {
				confSB = SourceBean.fromXMLString( (String)conf );
			} catch (SourceBeanException e) {
				logger.error("Impossible to parse configuration block for MapProvider", e);
				throw new GeoEngineException("Impossible to parse configuration block for MapProvider", e);
			}
		} else {
			confSB = (SourceBean)conf;
		}
		
		if(confSB != null) {
			String mapName = (String)confSB.getAttribute(GeoEngineConstants.MAP_NAME_TAG);
			abstractMapProvider.setSelectedMapName(mapName);
		}
	}
	
	
}
