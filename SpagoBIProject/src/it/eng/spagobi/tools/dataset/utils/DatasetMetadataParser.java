package it.eng.spagobi.tools.dataset.utils;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.tools.dataset.common.datastore.DataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IFieldMetaData;
import it.eng.spagobi.tools.dataset.service.ListTestDataSetModule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class DatasetMetadataParser {

	private static transient Logger logger = Logger.getLogger(DatasetMetadataParser.class);


	public String metadataToXML(IDataStore dataStore) {
		logger.debug("IN");
		if(dataStore==null || dataStore.getMetaData()==null){
			logger.error("Data Store is null, cannot recover metadata because Data Store or Data Store metadata is null");
			return null;
		}
		IDataStoreMetaData dataStoreMetaData=dataStore.getMetaData();

		HashMap<String , Class> metadataMap=new HashMap<String, Class>();
		for (int i = 0; i < dataStoreMetaData.getFieldCount(); i++) {
			IFieldMetaData fieldMetaData=dataStoreMetaData.getFieldMeta(i);
			String name=fieldMetaData.getName();
			Class type=fieldMetaData.getType();
			if(!metadataMap.containsKey(name)){
				metadataMap.put(name, type);
			}
		}

		String metaXML = "";
		metaXML += "<METADATALIST>";
		metaXML += "<ROWS>";
		for (Iterator iterator = metadataMap.keySet().iterator(); iterator.hasNext();) {
			String name = (String) iterator.next();
			String type = metadataMap.get(name)!=null ? ((Class)metadataMap.get(name)).toString() : "";
			metaXML += "<ROW" +
			" NAME=\"" + name + "\"" +
			" TYPE=\"" + type + "\"" +
			"/>";		
		}
		metaXML += "</ROWS></METADATALIST>";
		logger.debug("OUT");
		return metaXML;
	}


	public IDataStoreMetaData xmlToMetadata(String xmlMetadata) throws ClassNotFoundException {
		logger.debug("IN");
		DataStoreMetaData dsMeta=new DataStoreMetaData();

		if(xmlMetadata==null){
			logger.error("String rapresentation of metadata is null");
			return null;
		}
		SourceBean sb=null; 
		try {
			//sb=new SourceBean(xmlMetadata);
			sb=SourceBean.fromXMLString(xmlMetadata);
		} catch (SourceBeanException e) {
			logger.error("wrong xml metadata format");
			return null;
		}

		List lst=sb.getAttributeAsList("ROWS.ROW");
		for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
			SourceBean sbRow = (SourceBean)iterator.next();
			String name=sbRow.getAttribute("NAME")!= null ? sbRow.getAttribute("NAME").toString() : null;
			String type=sbRow.getAttribute("TYPE")!= null ? sbRow.getAttribute("TYPE").toString() : null;
			if(name!=null){
				FieldMetadata fieldMeta=new FieldMetadata();
				fieldMeta.setName(name);
				if(type!=null){
					// remove class!
					type=type.substring(6);
					fieldMeta.setType(Class.forName(type.trim()));
				}				
				dsMeta.addFiedMeta(fieldMeta);
			}
		}
		logger.debug("OUT");
		return dsMeta;
	}



}
