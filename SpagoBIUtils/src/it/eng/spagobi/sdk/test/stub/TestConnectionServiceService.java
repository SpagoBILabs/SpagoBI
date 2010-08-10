/**
 * TestConnectionServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.sdk.test.stub;

public interface TestConnectionServiceService extends javax.xml.rpc.Service {
    public java.lang.String getTestConnectionServiceAddress();

    public it.eng.spagobi.sdk.test.stub.TestConnectionService getTestConnectionService() throws javax.xml.rpc.ServiceException;

    public it.eng.spagobi.sdk.test.stub.TestConnectionService getTestConnectionService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
