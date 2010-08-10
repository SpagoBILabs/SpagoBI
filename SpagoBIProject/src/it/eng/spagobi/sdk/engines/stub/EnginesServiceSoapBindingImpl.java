/**
 * EnginesServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.sdk.engines.stub;

import it.eng.spagobi.sdk.engines.impl.EnginesServiceImpl;
import it.eng.spagobi.services.dataset.service.DataSetServiceImpl;

public class EnginesServiceSoapBindingImpl implements it.eng.spagobi.sdk.engines.stub.EnginesService{
    public it.eng.spagobi.sdk.engines.bo.SDKEngine[] getEngines() throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
       	EnginesServiceImpl supplier=new EnginesServiceImpl();
    	return supplier.getEngines();
     }

    public it.eng.spagobi.sdk.engines.bo.SDKEngine getEngine(java.lang.Integer in0) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
       	EnginesServiceImpl supplier=new EnginesServiceImpl();
    	return supplier.getEngine(in0);
    }

}
