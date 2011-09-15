package it.eng.spagobi.tools.dataset.bo;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;
import it.eng.spagobi.tools.dataset.common.dataproxy.CustomDataProxy;
import it.eng.spagobi.tools.dataset.common.dataproxy.IDataProxy;
import it.eng.spagobi.tools.dataset.common.datareader.XmlDataReader;

import java.util.Map;

import org.apache.log4j.Logger;

public class CustomDataSet extends ConfigurableDataSet {

	public static String DS_TYPE = "SbiCustomDataSet";
	
	private static transient Logger logger = Logger.getLogger(CustomDataSet.class);
	 
	
	public CustomDataSet() {
		super();
		setDataProxy( new CustomDataProxy() );
		setDataReader( new XmlDataReader() );
	}
	
	public CustomDataSet(SpagoBiDataSet dataSetConfig) {
		super(dataSetConfig);
		setDataProxy( new CustomDataProxy() );
		setDataReader( new XmlDataReader() );		
		
		setCustomData( dataSetConfig.getCustomData() );
		setJavaClassName( dataSetConfig.getJavaClassName() );
	}
	
	
	public CustomDataSet(String javaClassName, Map<String, Object> properties) {
		super();
		
		setDataProxy( new CustomDataProxy() );
		setDataReader( new XmlDataReader() );		
		
		setCustomData( properties.toString() );
		setJavaClassName( javaClassName );
	}
	
	public SpagoBiDataSet toSpagoBiDataSet() {
		SpagoBiDataSet sbd;
		
		sbd = super.toSpagoBiDataSet();
		
		sbd.setType( DS_TYPE );
				
		sbd.setCustomData( getCustomData() );
		sbd.setJavaClassName( getJavaClassName() );
		
		return sbd;
	}
	
	public CustomDataProxy getDataProxy() {
		IDataProxy dataProxy;
		
		dataProxy = super.getDataProxy();
		
		if(dataProxy == null) {
			setDataProxy( new CustomDataProxy() );
			dataProxy = getDataProxy();
		}
		
		if(!(dataProxy instanceof  CustomDataProxy)) throw new RuntimeException("DataProxy cannot be of type [" + 
				dataProxy.getClass().getName() + "] in CustomDataSet");
		
		return (CustomDataProxy)dataProxy;
	}

	public void setCustomData(String customData) {
		getDataProxy().setCustomData(customData);
	}
	
	public String getCustomData() {
		return getDataProxy().getCustomData();
	}
	
	public void setJavaClassName(String javaClassName) {
		getDataProxy().setJavaClassName(javaClassName);
	}
	
	public String getJavaClassName() {
		return getDataProxy().getJavaClassName();
	}
}
