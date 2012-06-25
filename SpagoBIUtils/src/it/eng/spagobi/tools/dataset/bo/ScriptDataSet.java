/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.QuerableBehaviour;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.dataproxy.ScriptDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.XmlDataReader;

import org.apache.log4j.Logger;

public class ScriptDataSet extends ConfigurableDataSet {
	
	public static String DS_TYPE = "SbiScriptDataSet";
	
	private static transient Logger logger = Logger.getLogger(ScriptDataSet.class);
	
	public ScriptDataSet() {
		super();
		setDataProxy( new ScriptDataProxy() );
		setDataReader( new XmlDataReader() );
		addBehaviour( new QuerableBehaviour(this) );
	}
	
	public ScriptDataSet(SpagoBiDataSet dataSetConfig) {
		super(dataSetConfig);
		
		setDataProxy( new ScriptDataProxy() );
		setDataReader( new XmlDataReader() );
		addBehaviour( new QuerableBehaviour(this) );	
		setScript( dataSetConfig.getScript() );
		setScriptLanguage(dataSetConfig.getLanguageScript());
		
	}
	
	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd;
		
		sbd = super.toSpagoBiDataSet();	
		
		sbd.setType( DS_TYPE );		
		
		sbd.setScript( getScript() );
		sbd.setLanguageScript(getScriptLanguage());
		
		return sbd;
	}

	public ScriptDataProxy getDataProxy() {
		IDataProxy dataProxy;
		
		dataProxy = super.getDataProxy();
		
		if(dataProxy == null) {
			setDataProxy( new ScriptDataProxy() );
			dataProxy = getDataProxy();
		}
		
		if(!(dataProxy instanceof  ScriptDataProxy)) throw new RuntimeException("DataProxy cannot be of type [" + 
				dataProxy.getClass().getName() + "] in FileDataSet");
		
		return (ScriptDataProxy)dataProxy;
	}
	
	public void setScript(String script) {
		getDataProxy().setStatement(script);
	}
	
	public String getScript() {
		return getDataProxy().getStatement();
	}
	
	public void setScriptLanguage(String language){
		getDataProxy().setLanguage(language);
	}
	
	public String getScriptLanguage(){
		return getDataProxy().getLanguage();
	}
	
	

}
