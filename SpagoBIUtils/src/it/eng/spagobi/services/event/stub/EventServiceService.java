/**
 * EventServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.services.event.stub;

public interface EventServiceService extends javax.xml.rpc.Service {
    public java.lang.String getEventServiceAddress();

    public it.eng.spagobi.services.event.stub.EventService getEventService() throws javax.xml.rpc.ServiceException;

    public it.eng.spagobi.services.event.stub.EventService getEventService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
