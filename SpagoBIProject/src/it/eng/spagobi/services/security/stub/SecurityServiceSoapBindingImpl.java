/**
 * SecurityServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.services.security.stub;

import it.eng.spagobi.services.security.service.SecurityServiceImpl;

public class SecurityServiceSoapBindingImpl implements it.eng.spagobi.services.security.stub.SecurityService{
    public it.eng.spagobi.services.security.bo.SpagoBIUserProfile getUserProfile(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException {
	SecurityServiceImpl impl=new SecurityServiceImpl();
	return impl.getUserProfile(in0,in1);
    }

    public boolean isAuthorized(java.lang.String in0, java.lang.String in1, java.lang.String in2, java.lang.String in3) throws java.rmi.RemoteException {
	SecurityServiceImpl impl=new SecurityServiceImpl();
	return impl.isAuthorized(in0,in1,in2,in3);
    }

    public boolean checkAuthorization(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException {
	SecurityServiceImpl impl=new SecurityServiceImpl();
	return impl.checkAuthorization(in0,in1,in2);
    }

}
