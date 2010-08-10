/**
 * WSDataSetServiceSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.spagobi.tools.dataset.wsconnectors.stub;

public class WSDataSetServiceSoapBindingImpl implements it.eng.spagobi.tools.dataset.wsconnectors.stub.IWsConnector{
    public java.lang.String readDataSet(java.lang.String in0, java.util.Map in1, java.lang.String in2) throws java.rmi.RemoteException {
        if(in2.equalsIgnoreCase("a"))
    	return "<ROWS><ROW name='io' value='30'/></ROWS>";
        if(in2.equalsIgnoreCase("b"))
        	return "<ROWS><ROW name='io' value='80'/></ROWS>";
        return null;
    }

}
	