/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.dataproxy.JavaClassDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.XmlDataReader;

import org.apache.log4j.Logger;

public class JavaClassDataSet extends ConfigurableDataSet {
	 
	public static String DS_TYPE = "SbiJClassDataSet";
	
	private static transient Logger logger = Logger.getLogger(JavaClassDataSet.class);
	 
	
	public JavaClassDataSet() {
		super();
		setDataProxy( new JavaClassDataProxy() );
		setDataReader( new XmlDataReader() );
	}
	
	public JavaClassDataSet(SpagoBiDataSet dataSetConfig) {
		super(dataSetConfig);
		setDataProxy( new JavaClassDataProxy() );
		setDataReader( new XmlDataReader() );		
		
		setClassName( dataSetConfig.getJavaClassName() );
	}
	
	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd;
		
		sbd = super.toSpagoBiDataSet();
		
		sbd.setType( DS_TYPE );
				
		sbd.setJavaClassName( getClassName() );
		
		return sbd;
	}
	
	public JavaClassDataProxy getDataProxy() {
		IDataProxy dataProxy;
		
		dataProxy = super.getDataProxy();
		
		if(dataProxy == null) {
			setDataProxy( new JavaClassDataProxy() );
			dataProxy = getDataProxy();
		}
		
		if(!(dataProxy instanceof  JavaClassDataProxy)) throw new RuntimeException("DataProxy cannot be of type [" + 
				dataProxy.getClass().getName() + "] in FileDataSet");
		
		return (JavaClassDataProxy)dataProxy;
	}

	public void setClassName(String className) {
		getDataProxy().setClassName(className);
	}
	
	public String getClassName() {
		return getDataProxy().getClassName();
	}
	
	
	 
}
