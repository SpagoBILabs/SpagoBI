/**
 * TestConnectionServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.spagobi.sdk.test.stub;

import it.eng.spagobi.sdk.test.impl.TestConnectionServiceImpl;

public class TestConnectionServiceSoapBindingImpl implements it.eng.spagobi.sdk.test.stub.TestConnectionService{
    public boolean connect() throws java.rmi.RemoteException {
    	TestConnectionServiceImpl impl = new TestConnectionServiceImpl();
    	return impl.connect();
    }

}
