/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

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
