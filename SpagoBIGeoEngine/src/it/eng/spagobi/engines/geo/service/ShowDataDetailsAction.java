/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.service;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.geo.datamart.provider.IDataMartProvider;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class ShowDataDetailsAction.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class ShowDataDetailsAction extends AbstractGeoEngineAction {
	
	/** Request parameters. */
	public static final String SELECTED_FEATURE_ID = "featureValue";
	
	/** Session parameters. */
	public static final String RESULT_SET = "RESULT_SET";
	
	/** The Constant SELECTED_FEATURE_DESC. */
	public static final String SELECTED_FEATURE_DESC = "FEATURE_DESC";
	
	/** Default serial version number (just to keep eclipse happy). */
	private static final long serialVersionUID = 1L;

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ShowDataDetailsAction.class);
	
    
	/* (non-Javadoc)
	 * @see it.eng.spagobi.utilities.engines.AbstractEngineAction#service(it.eng.spago.base.SourceBean, it.eng.spago.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		
		String featureValue = null;
		SourceBean resultSB = null;
		IDataMartProvider datamartprovicer;
		
		logger.debug("IN");
		
		try{
			super.service(serviceRequest, serviceResponse);
			
			Assert.assertNotNull(getGeoEngineInstance(), "GeoEngineInstance cannot be null");
			
			featureValue = getAttributeAsString(SELECTED_FEATURE_ID);
			logger.debug("Parameter [" + SELECTED_FEATURE_ID + "] is equals to [" + featureValue + "]");
			Assert.assertNotNull(featureValue, "Parameter [" + SELECTED_FEATURE_ID + "] cannot be null");
			
			datamartprovicer =  getGeoEngineInstance().getDataMartProvider();
			Assert.assertNotNull(datamartprovicer, "Impossible to get datamart");
			
			resultSB = datamartprovicer.getDataDetails(featureValue);
			logger.debug("ResultSet: \n" + resultSB);
					
			setAttribute(RESULT_SET, resultSB);
			setAttribute(SELECTED_FEATURE_DESC, featureValue);
		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			// no resources need to be released
		}	
		
		logger.debug("OUT");
	}
}