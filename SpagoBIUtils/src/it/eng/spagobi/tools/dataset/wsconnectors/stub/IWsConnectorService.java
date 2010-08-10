/**
 * IWsConnectorService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.tools.dataset.wsconnectors.stub;

public interface IWsConnectorService extends javax.xml.rpc.Service {
    public java.lang.String getWSDataSetServiceAddress();

    public it.eng.spagobi.tools.dataset.wsconnectors.stub.IWsConnector getWSDataSetService() throws javax.xml.rpc.ServiceException;

    public it.eng.spagobi.tools.dataset.wsconnectors.stub.IWsConnector getWSDataSetService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
