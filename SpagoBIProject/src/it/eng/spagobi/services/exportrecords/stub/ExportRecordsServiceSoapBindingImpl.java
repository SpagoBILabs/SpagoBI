/**
 * ExportRecordsServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eng.spagobi.services.exportrecords.stub;

public class ExportRecordsServiceSoapBindingImpl implements it.eng.spagobi.services.exportrecords.stub.ExportRecordsService{
    public java.lang.String processRecords(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException {
        System.out.println("******************");
        System.out.println("Service START");
        System.out.println("Records: " + in0);
        System.out.println("Operation: " + in1);
        System.out.println("Service END");
        System.out.println("******************");
    	return "Records processed properly";
    }

}
