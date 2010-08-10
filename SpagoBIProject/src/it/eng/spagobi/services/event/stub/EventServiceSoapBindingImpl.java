/**
 * EventServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.spagobi.services.event.stub;

import it.eng.spagobi.services.event.service.EventServiceImpl;

public class EventServiceSoapBindingImpl implements it.eng.spagobi.services.event.stub.EventService{
    public java.lang.String fireEvent(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3, java.lang.String in4, java.lang.String in5) throws java.rmi.RemoteException {
	EventServiceImpl service=new EventServiceImpl();
	return service.fireEvent(in0, in1, in2, in3, in4, in5);
    }

}
