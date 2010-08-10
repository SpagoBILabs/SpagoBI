/**
 * DataSetServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.spagobi.services.dataset.stub;

import it.eng.spagobi.services.dataset.service.DataSetServiceImpl;

public class DataSetServiceSoapBindingImpl implements it.eng.spagobi.services.dataset.stub.DataSetService{
    public it.eng.spagobi.services.dataset.bo.SpagoBiDataSet getDataSet(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException {
    	DataSetServiceImpl supplier=new DataSetServiceImpl();
    	return supplier.getDataSet(in0, in1, in2);
    }

    public it.eng.spagobi.services.dataset.bo.SpagoBiDataSet getDataSetByLabel(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException {
    	DataSetServiceImpl supplier=new DataSetServiceImpl();
    	return supplier.getDataSetByLabel(in0, in1, in2);
    }

    public it.eng.spagobi.services.dataset.bo.SpagoBiDataSet[] getAllDataSet(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException {
    	DataSetServiceImpl supplier=new DataSetServiceImpl();
    	return supplier.getAllDataSet(in0, in1);
    }

}
