/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.analiticalmodel.document.handlers;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.BIObjectParameter;
import it.eng.spagobi.behaviouralmodel.analyticaldriver.bo.ObjParuse;
import it.eng.spagobi.behaviouralmodel.lov.bo.ILovDetail;
import it.eng.spagobi.behaviouralmodel.lov.bo.QueryDetail;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.utilities.cache.CacheInterface;
import it.eng.spagobi.utilities.cache.CacheSingleton;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class caches LOV (list of values) executions' result.
 * The key of the cache element is composed by the user's identifier and the LOV definition.
 * In case the LOV is a query and there are dependencies, the wrapped statement is used instead of the original statement. 
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class LovResultCacheManager {
	
	private static Logger logger = Logger.getLogger(LovResultCacheManager.class);
	
	private CacheInterface cache = null;
	
	public LovResultCacheManager() {
		this.cache = CacheSingleton.getInstance();
	}

	/**
	 * Returns the LOV result. If the LOV result is in cache, it is returned;
	 * otherwise, if retrieveIfNotcached is true, the LOV is executed and
	 * returned, otherwise null is returned.
	 * 
	 * @param profile
	 *            The user profile object
	 * @param parameter
	 *            The BIObjectParameter to retrieve the value for
	 * @param executionInstance
	 *            The execution instance
	 * @param retrieveIfNotcached
	 *            If true and LOV is not cached, the LOV executed and cached,
	 *            otherwise the LOV is not executed (and not cached)
	 * @return the LOV result, or null if the LOV is not cached and
	 *         retrieveIfNotcached is false
	 * @throws Exception
	 */
	public String getLovResult(IEngUserProfile profile, BIObjectParameter parameter, 
			ExecutionInstance executionInstance, boolean retrieveIfNotcached) throws Exception {
		logger.debug("IN");
		
		ILovDetail lovDefinition = executionInstance.getLovDetail(parameter);
		List<ObjParuse> dependencies = executionInstance.getDependencies(parameter);
		
		String lovResult = null;
		String cacheKey = getCacheKey(profile, lovDefinition, dependencies, executionInstance);
		logger.info("Cache key : " + cacheKey);
		if (cache.contains(cacheKey)) {
			logger.info("Retrieving lov result from cache...");
			// lov provider is present, so read the DATA in cache
			lovResult = cache.get(cacheKey);
			logger.debug(lovResult);
		} else if (retrieveIfNotcached) {
			logger.info("Executing lov to get result ...");
			lovResult = lovDefinition.getLovResult(profile, dependencies, executionInstance);
			logger.debug(lovResult);
			// insert the data in cache
			if (lovResult != null) 
				cache.put(cacheKey, lovResult);
		}
		
		logger.debug("OUT");
		return lovResult;
	}

	/**
	 * This method finds out the cache to be used for lov's result cache. This
	 * key is composed mainly by the user identifier and the lov definition.
	 * Note that, in case when the lov is a query and there is correlation, the
	 * executed statement if different from the original query (since
	 * correlation expression is injected inside SQL query using in-line view
	 * construct), therefore we should consider the modified query.
	 * 
	 * @param profile
	 *            The user profile
	 * @param lovDefinition
	 *            The lov original definition
	 * @param dependencies
	 *            The dependencies to be considered (if any)
	 * @param executionInstance
	 *            The execution instance (it may be null, since a lov can be
	 *            executed outside an execution instance context)
	 * @return The key to be used in cache
	 */
	private String getCacheKey(IEngUserProfile profile,
			ILovDetail lovDefinition, List<ObjParuse> dependencies,
			ExecutionInstance executionInstance) {
		logger.debug("IN");
		String toReturn = null;
		String userID = (String)((UserProfile)profile).getUserId();
		if (lovDefinition instanceof QueryDetail) {
			QueryDetail queryDetail = (QueryDetail) lovDefinition;
			QueryDetail clone = queryDetail.clone();
			clone.setQueryDefinition(queryDetail.getWrappedStatement(dependencies, executionInstance));
			toReturn = userID + ";" + clone.toXML();
		} else {
			toReturn = userID + ";" + lovDefinition.toXML();
		}
		logger.debug("OUT: returning [" + toReturn + "]");
		return toReturn;
	}

}
