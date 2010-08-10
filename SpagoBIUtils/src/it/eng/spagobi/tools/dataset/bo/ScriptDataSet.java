package it.eng.spagobi.tools.dataset.bo;

import org.apache.log4j.Logger;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.QuerableBehaviour;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.dataproxy.ScriptDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.XmlDataReader;

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
