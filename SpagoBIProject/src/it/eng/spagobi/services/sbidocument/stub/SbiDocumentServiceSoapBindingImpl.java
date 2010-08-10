/**
 * SbiDocumentServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.services.sbidocument.stub;

import it.eng.spagobi.services.sbidocument.service.SbiDocumentServiceImpl;

public class SbiDocumentServiceSoapBindingImpl implements it.eng.spagobi.services.sbidocument.stub.SbiDocumentService{
    public it.eng.spagobi.services.sbidocument.bo.SpagobiAnalyticalDriver[] getDocumentAnalyticalDrivers(java.lang.String in0, java.lang.String in1, java.lang.Integer in2, java.lang.String in3, java.lang.String in4) throws java.rmi.RemoteException {
        SbiDocumentServiceImpl impl = new SbiDocumentServiceImpl();
        return impl.getDocumentAnalyticalDrivers(in0, in1, in2, in3, in4);
    }

    public java.lang.String getDocumentAnalyticalDriversJSON(java.lang.String in0, java.lang.String in1, java.lang.Integer in2, java.lang.String in3, java.lang.String in4) throws java.rmi.RemoteException {
        SbiDocumentServiceImpl impl = new SbiDocumentServiceImpl();
        return impl.getDocumentAnalyticalDriversJSON(in0, in1, in2, in3, in4);
    }

}
