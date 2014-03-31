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
package it.eng.spagobi.api;

import java.util.List;

import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.cache.CacheManager;
import it.eng.spagobi.tools.dataset.cache.ICache;
import it.eng.spagobi.tools.dataset.cache.ICacheMetadata;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.CacheItem;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 * 
 */
@Path("/1.0/cache")
public class CacheResource extends AbstractSpagoBIResource {
	
	static private Logger logger = Logger.getLogger(CacheResource.class);
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getCache() {
		
		logger.debug("IN");
		try {
			ICache cache = CacheManager.getCache();
			return serializeCache(cache);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String deleteCache() {
		
		logger.debug("IN");
		try {
			ICache cache = CacheManager.getCache();
			cache.deleteAll();
			return serializeCache(cache);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String updateCache(@QueryParam("enabled") Boolean enabled) {
		
		logger.debug("IN");
		try {
			ICache cache = CacheManager.getCache();
			cache.deleteAll();
			cache.enable(enabled);
			return serializeCache(cache);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	@POST
	@Path("/meta")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getCacheMetadata() {
		
		logger.debug("IN");
		try {
			ICache cache = CacheManager.getCache();
			ICacheMetadata cacheMetadata = cache.getMetadata();
			return serializeCacheMetadata(cacheMetadata);
		} catch(Throwable t) {
			throw new SpagoBIServiceException(this.request.getPathInfo(), "An unexpected error occured while executing service", t);
		} finally {			
			logger.debug("OUT");
		}	
	}
	
	
	
	private String serializeCacheMetadata(ICacheMetadata cacheMetadata) {
		try {
			JSONArray resultJSON = new JSONArray();
			List<String> signatures = cacheMetadata.getSignatures();
			for(String signature: signatures) {
				CacheItem item = cacheMetadata.getCacheItem(signature);
				JSONObject itemJSON = new JSONObject();
				itemJSON.put("name", item.getName());
				itemJSON.put("signature", item.getSignature());
				itemJSON.put("table", item.getTable());
				itemJSON.put("dimension", item.getDimension());
				resultJSON.put(itemJSON);
			}
			return resultJSON.toString();
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while serializing results",  t);
		}	
	}
	/**
	 * @param cache
	 * @return
	 */
	private String serializeCache(ICache cache) {
		try {
			JSONObject resultJSON = new JSONObject();
			resultJSON.put("enabled", cache.isEnabled());
			resultJSON.put("totalMemory", cache.getMetadata().getTotalMemory());
			resultJSON.put("availableMemory", cache.getMetadata().getAvailableMemory());
			resultJSON.put("cachedObjectsCount", cache.getMetadata().getNumberOfObjects());
			resultJSON.put("cleaningEnabled", cache.getMetadata().isCleaningEnabled());
			resultJSON.put("cleaningQuota", cache.getMetadata().getCleaningQuota());
			return resultJSON.toString();
		} catch(Throwable t) {
			throw new RuntimeException("An unexpected error occured while serializing results",  t);
		}	
	}
}
