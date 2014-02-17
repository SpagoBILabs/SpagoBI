/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.dataset.cache;

import java.util.List;

/**
 * @author  Marco Cortella (marco.cortella@eng.it)
 * 			Antonella Giachino (antonella.giachino@eng.it)
 *
 */
public interface ICacheMetadata {
	/**
	 * @return the cache dimension space free
	 */
	Double getDimensionSpaceAvailable();
	
	/**
	 * @return the cache dimension space used
	 */
	Double getDimensionSpaceUsed();
	
	/**
	 * @return the number of the objects cached
	 */
	Integer getNumberOfObjects();
	
	/**
	 * @return true if the cache space is near the full limit
	 */
	boolean isFull();
	
	/**
	 * @return a list of the cached objects ordered by dimension (largest at the begin)
	 */
	List getObjectsByDimension();
	
	/**
	 * @return a list of the cached objects ordered by store time (oldest at the begin)
	 */
	List getObjectsByTime();
}
