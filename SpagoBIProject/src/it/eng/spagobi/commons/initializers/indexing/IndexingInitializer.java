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
package it.eng.spagobi.commons.initializers.indexing;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.utilities.SpagoBIUtilities;
import it.eng.spagobi.commons.utilities.indexing.LuceneIndexer;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;

/**Initializer class for Metadata Indexing
 * @author franceschini
 *
 */
public class IndexingInitializer implements InitializerIFace {
	
	static private Logger logger = Logger.getLogger(IndexingInitializer.class);
	private SourceBean _config;
	
	public SourceBean getConfig() {
		return _config;
	}

	/* (non-Javadoc)
	 * @see it.eng.spago.init.InitializerIFace#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
		logger.debug("IN");
		_config = config;

		    String jndiResourcePath = SingletonConfig.getInstance().getConfigValue("INDEX_INITIALIZATION.jndiResourcePath");
		    String location = SpagoBIUtilities.readJndiResource(jndiResourcePath);
		    String name = SingletonConfig.getInstance().getConfigValue("INDEX_INITIALIZATION.name");
		    //first checks if iindex exists
		    File idxFile = new File(location+name);
		    if(!idxFile.exists()){
		    	logger.debug("Creating index");
			    LuceneIndexer indexer = new LuceneIndexer();
			    try {
					indexer.createIndex(idxFile);
				} catch (CorruptIndexException e) {
					logger.error("Index corrupted "+e.getMessage(), e);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
		    }else{
		    	logger.debug("Index already exists");
		    }

		    

		logger.debug("OUT");
	}

}
