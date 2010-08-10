/**
 * DataSourcesSDKServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.sdk.datasources.stub;

import it.eng.spagobi.sdk.datasets.impl.DataSetsSDKServiceImpl;
import it.eng.spagobi.sdk.datasources.impl.DataSourcesSDKServiceImpl;

public class DataSourcesSDKServiceSoapBindingImpl implements it.eng.spagobi.sdk.datasources.stub.DataSourcesSDKService{
    public it.eng.spagobi.sdk.datasources.bo.SDKDataSource getDataSource(java.lang.Integer in0) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
    	DataSourcesSDKServiceImpl impl = new DataSourcesSDKServiceImpl();
    	return impl.getDataSource(in0);
    }

    public it.eng.spagobi.sdk.datasources.bo.SDKDataSource[] getDataSources() throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
    	DataSourcesSDKServiceImpl impl = new DataSourcesSDKServiceImpl();
    	return impl.getDataSources();
    }

}
