/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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
		setLanguageScript(dataSetConfig.getLanguageScript());
		
	}
	
	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd;
		
		sbd = super.toSpagoBiDataSet();	
		
		sbd.setType( DS_TYPE );		
		
		sbd.setScript( getScript() );
		sbd.setLanguageScript(getLanguageScript());
		
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
		getDataProxy().setScript(script);
	}
	
	public String getScript() {
		return getDataProxy().getScript();
	}
	
	public void setLanguageScript(String languageScript){
		getDataProxy().setLanguageScript(languageScript);
	}
	
	public String getLanguageScript(){
		return getDataProxy().getLanguageScript();
	}
	
	

}
