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
package it.eng.qbe.model.io;

import java.util.List;

import it.eng.qbe.model.DataMartModel;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.QueryMeta;

// TODO: Auto-generated Javadoc
/**
 * The Interface IQueryPersister.
 * 
 * @author Andrea Zoppello
 * 
 * This is the interface for classes that implements
 * logig to load and persist query on a persistent store than can be, JSR 170 repository, Database,
 * File System and so on
 */
public interface IQueryPersister {

	/**
	 * Persist.
	 * 
	 * @param dm the dm
	 * @param wizObject the wiz object
	 */
	public void persist(DataMartModel dm, Query wizObject, QueryMeta meta);
	
	/**
	 * Load all queries.
	 * 
	 * @param dm the dm
	 * 
	 * @return all the query for datamart dm
	 */
	public List loadAllQueries(DataMartModel dm);
	
	/**
	 * Load.
	 * 
	 * @param dm the dm
	 * @param key the key
	 * 
	 * @return the query of the datamart identified by key
	 */
	public Query load(DataMartModel dm, String key);
}
