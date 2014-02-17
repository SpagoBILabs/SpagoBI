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
package it.eng.spagobi.dataset.cache.impl.sqldbcache;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.dataset.cache.ICacheMetadata;
import it.eng.spagobi.tools.datasource.bo.IDataSource;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */
public class SQLDBCacheMetadata implements ICacheMetadata {
	
	static private Logger logger = Logger.getLogger(SQLDBCacheMetadata.class);
	
	public static final String CACHE_NAME_PREFIX_CONFIG = "SPAGOBI.CACHE.NAMEPREFIX";
	
	private Double dimensionSpaceFree ;
	private Double dimensionSpaceUsed ;
	
	
	private Config tableNamePrefixConfig;
	
	public SQLDBCacheMetadata(){
		try {
			IConfigDAO configDao = DAOFactory.getSbiConfigDAO();
			//recuperare info di config legate alla dimensione disponibile e alla % da ripulire
//			tableNamePrefixConfig = configDao.loadConfigParametersByLabel(CACHE_NAME_PREFIX_CONFIG);
//			if (tableNamePrefixConfig.isActive()){
//				String tablePrefix = tableNamePrefixConfig.getValueCheck();
//			}

		} catch (EMFUserError e) {
			logger.debug("Impossible to instantiate SbiConfigDAO in SQLDBCache");
		} catch (Exception e) {
			logger.debug("Impossible to instantiate SbiConfigDAO in SQLDBCache");
		}
	}
	

	public Double getDimensionSpaceAvailable(){
		//TODO: calcolare spazio disponibile
		return dimensionSpaceFree;
	}	
	

	public Double getDimensionSpaceUsed(){
		//TODO: calcolcare spazio usato
		return dimensionSpaceUsed;
	}
	

	public Integer getNumberOfObjects(){
		//TODO
		return null;
	}
	

	public boolean isFull(){
		//TODO
		return false;
	}

	public List getObjectsByDimension(){
		//TODO
		return null;
	}
	

	public List getObjectsByTime(){
		//TODO
		return null;
	}
}
