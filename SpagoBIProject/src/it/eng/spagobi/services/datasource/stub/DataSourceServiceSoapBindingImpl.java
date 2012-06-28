/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.services.datasource.stub;

import it.eng.spagobi.services.datasource.service.DataSourceServiceImpl;

public class DataSourceServiceSoapBindingImpl implements it.eng.spagobi.services.datasource.stub.DataSourceService{
    public it.eng.spagobi.services.datasource.bo.SpagoBiDataSource getDataSource(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException {
	DataSourceServiceImpl service=new DataSourceServiceImpl();
	return service.getDataSource(in0, in1,in2);
    }
    public it.eng.spagobi.services.datasource.bo.SpagoBiDataSource getDataSourceByLabel(java.lang.String in0, java.lang.String in1, java.lang.String in2) throws java.rmi.RemoteException {
	DataSourceServiceImpl service=new DataSourceServiceImpl();
	return service.getDataSourceByLabel(in0, in1,in2);
    }
    public it.eng.spagobi.services.datasource.bo.SpagoBiDataSource[] getAllDataSource(java.lang.String in0, java.lang.String in1) throws java.rmi.RemoteException {
	DataSourceServiceImpl service=new DataSourceServiceImpl();
	return service.getAllDataSource(in0,in1);
    }

}
