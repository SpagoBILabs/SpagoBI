/**
 * MapsSDKServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.sdk.maps.stub;

import it.eng.spagobi.sdk.maps.impl.MapsSDKServiceImpl;

public class MapsSDKServiceSoapBindingImpl implements it.eng.spagobi.sdk.maps.stub.MapsSDKService{
    public it.eng.spagobi.sdk.maps.bo.SDKMap[] getMaps() throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
    	MapsSDKServiceImpl impl = new MapsSDKServiceImpl();
    	return impl.getMaps();
    }

    public it.eng.spagobi.sdk.maps.bo.SDKMap getMapById(java.lang.Integer in0) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
    	MapsSDKServiceImpl impl = new MapsSDKServiceImpl();
    	return impl.getMapById(in0);
    }

    public it.eng.spagobi.sdk.maps.bo.SDKFeature[] getMapFeatures(java.lang.Integer in0) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
    	MapsSDKServiceImpl impl = new MapsSDKServiceImpl();
    	return impl.getMapFeatures(in0);
    }

    public it.eng.spagobi.sdk.maps.bo.SDKFeature[] getFeatures() throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
    	MapsSDKServiceImpl impl = new MapsSDKServiceImpl();
    	return impl.getFeatures();
    }

    public it.eng.spagobi.sdk.maps.bo.SDKFeature getFeatureById(java.lang.Integer in0) throws java.rmi.RemoteException, it.eng.spagobi.sdk.exceptions.NotAllowedOperationException {
    	MapsSDKServiceImpl impl = new MapsSDKServiceImpl();
    	return impl.getFeatureById(in0);
    }

}
