/**
 * TestConnectionServiceSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.eng.spagobi.sdk.test.stub;

public class TestConnectionServiceSoapBindingSkeleton implements it.eng.spagobi.sdk.test.stub.TestConnectionService, org.apache.axis.wsdl.Skeleton {
    private it.eng.spagobi.sdk.test.stub.TestConnectionService impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("connect", _params, new javax.xml.namespace.QName("", "connectReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("urn:spagobisdktestconnection", "connect"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("connect") == null) {
            _myOperations.put("connect", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("connect")).add(_oper);
    }

    public TestConnectionServiceSoapBindingSkeleton() {
        this.impl = new it.eng.spagobi.sdk.test.stub.TestConnectionServiceSoapBindingImpl();
    }

    public TestConnectionServiceSoapBindingSkeleton(it.eng.spagobi.sdk.test.stub.TestConnectionService impl) {
        this.impl = impl;
    }
    public boolean connect() throws java.rmi.RemoteException
    {
        boolean ret = impl.connect();
        return ret;
    }

}
