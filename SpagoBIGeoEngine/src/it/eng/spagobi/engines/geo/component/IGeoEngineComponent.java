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
package it.eng.spagobi.engines.geo.component;

import it.eng.spagobi.engines.geo.GeoEngineException;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Interface IGeoEngineComponent.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IGeoEngineComponent {
	
	/**
	 * Inits the.
	 * 
	 * @param conf the conf
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	void init(Object conf) throws GeoEngineException;
	
	/**
	 * Sets the env.
	 * 
	 * @param env the new env
	 */
	void setEnv(Map env);
}
