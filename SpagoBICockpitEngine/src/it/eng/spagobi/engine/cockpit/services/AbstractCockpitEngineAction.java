/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.cockpit.services;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.query.Query;
import it.eng.spagobi.engine.cockpit.CockpitEngineInstance;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.EngineConstants;

import org.apache.log4j.Logger;

/**
 * The Class AbstractCockpitEngineAction.
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public abstract class AbstractCockpitEngineAction extends AbstractEngineAction {
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(AbstractCockpitEngineAction.class);
    
	
    public CockpitEngineInstance getEngineInstance() {
    	return (CockpitEngineInstance)getAttributeFromSession( EngineConstants.ENGINE_INSTANCE );
    }
    
//	public IDataSource getDataSource() {
//		CockpitEngineInstance cockpitEngineInstance  = null;
//		cockpitEngineInstance = getEngineInstance();
//    	if(cockpitEngineInstance == null) {
//    		return null;
//    	}
//    	return cockpitEngineInstance.getDataSource();
//	}

//	public void setDataSource(IDataSource dataSource) {
//		CockpitEngineInstance cockpitEngineInstance  = null;
//		cockpitEngineInstance = getEngineInstance();
//    	if(cockpitEngineInstance == null) {
//    		return;
//    	}
//    	cockpitEngineInstance.setDataSource(dataSource);
//	}
	
	
	public Query getQuery() {
		CockpitEngineInstance cockpitEngineInstance  = null;
		cockpitEngineInstance = getEngineInstance();
    	if(cockpitEngineInstance == null) {
    		return null;
    	}
    	return cockpitEngineInstance.getActiveQuery();
	}	
	
}
