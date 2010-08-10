/**
 * DocumentExecuteServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.services.execute.stub;

import it.eng.spagobi.services.event.service.EventServiceImpl;
import it.eng.spagobi.services.execute.service.ServiceChartImpl;
import it.eng.spagobi.services.execute.service.ServiceKpiValueXml;

public class DocumentExecuteServiceSoapBindingImpl implements it.eng.spagobi.services.execute.stub.DocumentExecuteService{
    public byte[] executeChart(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.util.HashMap in3) throws java.rmi.RemoteException {
    	ServiceChartImpl service=new ServiceChartImpl();
    	return service.executeChart(in0, in1, in2, in3);
    }

    public java.lang.String getKpiValueXML(java.lang.String in0, java.lang.String in1, java.lang.Integer in2) throws java.rmi.RemoteException {
        ServiceKpiValueXml service = new ServiceKpiValueXml();
        return service.getKpiValueXML(in0, in1, in2);
    }

}
