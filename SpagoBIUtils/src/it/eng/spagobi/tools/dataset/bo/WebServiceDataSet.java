/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
