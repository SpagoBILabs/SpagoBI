package it.eng.spagobi.engines.whatif.model;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;

import it.eng.spagobi.writeback4j.mondrian.CacheManager;

import org.apache.log4j.Logger;
import com.eyeq.pivot4j.PivotModel;

public class ModelUtilities {
	public static transient Logger logger = Logger.getLogger(ModelUtilities.class);
	
	public void reloadModel(WhatIfEngineInstance instance, PivotModel model){
		logger.debug("IN");
		SpagoBIPivotModel modelWrapper = (SpagoBIPivotModel) model;
		
		logger.debug("Cleaning the cache and restoring the model");
		CacheManager.flushCache(instance.getOlapDataSource());
		String mdx = modelWrapper.getCurrentMdx();
		modelWrapper.setMdx(mdx);
		modelWrapper.initialize();
		// force query riexecution
		modelWrapper.getCellSet();		
		logger.debug("Finish to clean the cache and restoring the model");
		
		logger.debug("OUT");
	}
	
}
