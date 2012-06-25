/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.dataproxy.WebServiceDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.XmlDataReader;

import org.apache.log4j.Logger;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class WebServiceDataSet extends ConfigurableDataSet {
    
    public static String DS_TYPE = "SbiWSDataSet";
    
    private static transient Logger logger = Logger.getLogger(WebServiceDataSet.class);
    
	
	/**
	 * Instantiates a new wS data set.
	 */
	public WebServiceDataSet() {
		super();
		setDataProxy( new WebServiceDataProxy());
		setDataReader( new XmlDataReader() );
		//addBehaviour( new QuerableBehaviour(this) );
	}
	
	public WebServiceDataSet(SpagoBiDataSet dataSetConfig) {
		super(dataSetConfig);
		
		setDataProxy(  new WebServiceDataProxy() );
		setDataReader( new XmlDataReader() );
		
		setAddress( dataSetConfig.getAdress() );
		setOperation( dataSetConfig.getOperation() );
		//setParamsMap(dataSetConfig.getP)
		
		//addBehaviour( new QuerableBehaviour(this) );

		
	}
		
	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd;
		
		sbd = super.toSpagoBiDataSet();
		
		sbd.setType( DS_TYPE );		
		
		sbd.setAdress( getAddress() );
		sbd.setOperation( getOperation() );

		return sbd;
	}
	
	public WebServiceDataProxy getDataProxy() {
		IDataProxy dataProxy;
		
		dataProxy = super.getDataProxy();
		
		if(dataProxy == null) {
			setDataProxy( new WebServiceDataProxy() );
			dataProxy = getDataProxy();
		}
		
		if(!(dataProxy instanceof  WebServiceDataProxy)) throw new RuntimeException("DataProxy cannot be of type [" + 
				dataProxy.getClass().getName() + "] in WebServiceDataProxy");
		
		return (WebServiceDataProxy)dataProxy;
	}
	
	public String getAddress() {
		return getDataProxy().getAddress();
	}
	
	public void setAddress(String address) {
		getDataProxy().setAddress(address);
	}
	
	public  String getOperation() {
		return getDataProxy().getOperation();
	}
	
	public  void setOperation(String operation) {
		getDataProxy().setOperation(operation);
	}


	
}
