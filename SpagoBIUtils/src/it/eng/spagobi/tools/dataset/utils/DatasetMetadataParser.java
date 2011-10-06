/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.tools.dataset.utils;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanAttribute;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;



/** Functions that convert from IMetadata object to xml rapresentation via sourcebean and viceversa
 * 
 * @author gavardi
 *
 */




public class DatasetMetadataParser {

	private static transient Logger logger = Logger.getLogger(DatasetMetadataParser.class);


	// XML tags
	public static final String COLUMNLIST = "COLUMNLIST"; 
	public static final String COLUMN = "COLUMN"; 
	public static final String PROPERTY = "PROPERTY"; 

	// XML attributes for tag COLUMM
	public static final String NAME = "name"; 
	public static final String FIELD_TYPE = "fieldType"; 
	public static final String TYPE = "type"; 
	public static final String ALIAS = "alias"; 

	// XML VALUES FOR PROPERTIES TAG

	public static final String VALUE = "value"; 
	public static final String NAME_P = "name"; 


	public String metadataToXML(IDataStore dataStore) {
		if(dataStore==null || dataStore.getMetaData()==null){
			logger.error("Data Store is null, cannot recover metadata because Data Store or Data Store metadata is null");
			return null;
		} 
		return metadataToXML(dataStore.getMetaData());

	}

	public String metadataToXML(IMetaData dataStoreMetaData) {
		logger.debug("IN");


		SourceBean sb = null;
		try{

			sb = new SourceBean(DatasetMetadataParser.COLUMNLIST);


			for (int i = 0; i < dataStoreMetaData.getFieldCount(); i++) {
				IFieldMetaData fieldMetaData=dataStoreMetaData.getFieldMeta(i);
				String name = fieldMetaData.getName();
				Assert.assertNotNull(name, "Name of the field cannot be null");
				String alias = fieldMetaData.getAlias();
				String type = fieldMetaData.getType().getName();
				Assert.assertNotNull(type, "Type of the field "+name+" cannot be null");
				FieldType fieldType = fieldMetaData.getFieldType();
				Map properties = fieldMetaData.getProperties();

				SourceBean sbMeta = new SourceBean(DatasetMetadataParser.COLUMN);
				SourceBeanAttribute attN = new SourceBeanAttribute(NAME, name);
				SourceBeanAttribute attT = new SourceBeanAttribute(TYPE, type);
				SourceBeanAttribute attA = alias != null? new SourceBeanAttribute(ALIAS, alias) : null;
				SourceBeanAttribute attF = fieldType != null? new SourceBeanAttribute(FIELD_TYPE, fieldType.toString()) : null;
				sbMeta.setAttribute(attN);
				sbMeta.setAttribute(attT);
				if(attA != null) sbMeta.setAttribute(attA);
				if(attF != null) sbMeta.setAttribute(attF);
				sb.setAttribute(sbMeta);

				// insert properties
				if(properties != null){
					insertPropertiesInSourceBean(sbMeta, properties );
				}
			}		
		}
		catch (Exception e) {
			logger.error("Error in building xml from metadata", e);
			return null;
		}

		String xml1 = sb.toXML(false);
		logger.debug("OUT");

		return xml1;
	}


	public void insertPropertiesInSourceBean(SourceBean sbMeta, Map properties ) throws SourceBeanException{
		logger.debug("IN");
		for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext();) {
			String  name = (String) iterator.next();
			Assert.assertNotNull(name, "Property name cannot be null");
			Object value = properties.get(name);
			Assert.assertNotNull(value, "Value of property "+name +" cannot be null");
			if(value != null){
				SourceBean sbP = new SourceBean(DatasetMetadataParser.PROPERTY);
				SourceBeanAttribute attN = new SourceBeanAttribute(NAME, name);
				SourceBeanAttribute attV = new SourceBeanAttribute(VALUE, value.toString());
				sbP.setAttribute(attN);
				sbP.setAttribute(attV);
				sbMeta.setAttribute(sbP);
			}
		}
		logger.debug("OUT");
	}








	public IMetaData xmlToMetadata(String xmlMetadata) throws Exception {
		logger.debug("IN");
		MetaData dsMeta=new MetaData();

		if(xmlMetadata==null){
			logger.error("String rapresentation of metadata is null");
			throw new Exception("Xml Metadata String cannot be null ");
		}
		SourceBean sb=null; 
		try {
			sb=SourceBean.fromXMLString(xmlMetadata);
		} catch (SourceBeanException e) {
			logger.error("wrong xml metadata format "+xmlMetadata);
			return null;
		}


		List lst=sb.getAttributeAsList(DatasetMetadataParser.COLUMN);
		if(lst == null || lst.size()==0){
			lst=sb.getAttributeAsList("ROWS.ROW");
		}

		for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
			SourceBean sbRow = (SourceBean)iterator.next();
			String name=sbRow.getAttribute(NAME)!= null ? sbRow.getAttribute(NAME).toString() : null;
			String type=sbRow.getAttribute(TYPE)!= null ? sbRow.getAttribute(TYPE).toString() : null;
			String alias=sbRow.getAttribute(ALIAS)!= null ? sbRow.getAttribute(ALIAS).toString() : null;
			String fieldType=sbRow.getAttribute(FIELD_TYPE)!= null ? sbRow.getAttribute(FIELD_TYPE).toString() : null;

			if(name!=null){
				FieldMetadata fieldMeta=new FieldMetadata();
				fieldMeta.setName(name);
				if(type!=null){
					// remove class!
					// operation for back compatibility, if there is class remove it otherwise not needed)
					if(type.startsWith("class")){
						type=type.substring(6);						
					}
					fieldMeta.setType(Class.forName(type.trim()));
				}
				fieldMeta.setAlias(alias);
				if(fieldType != null && fieldType.equalsIgnoreCase(FieldType.ATTRIBUTE.toString())) 
					fieldMeta.setFieldType(FieldType.ATTRIBUTE);
				else if(fieldType != null && fieldType.equalsIgnoreCase(FieldType.MEASURE.toString())) 
					fieldMeta.setFieldType(FieldType.MEASURE);
				else fieldMeta.setFieldType(FieldType.ATTRIBUTE);

				List properties =sbRow.getAttributeAsList(DatasetMetadataParser.PROPERTY);

				if(properties != null && properties.size()!=0){
					try{
						insertPropertiesInMeta(fieldMeta, properties);
					}
					catch (Exception e) {
						logger.error("Error in reading properties");
						throw new Exception("Error in inserting properties: "+e.getMessage());
					}
				}


				dsMeta.addFiedMeta(fieldMeta);
			}
		}
		logger.debug("OUT");
		return dsMeta;
	}



	public void insertPropertiesInMeta(IFieldMetaData meta, List propertiesBean ) throws SourceBeanException{
		logger.debug("IN");

		Map properties = meta.getProperties();

		for (Iterator iterator = propertiesBean.iterator(); iterator.hasNext();) {
			SourceBean sb = (SourceBean) iterator.next();
			String name=sb.getAttribute(NAME_P)!= null ? sb.getAttribute(NAME_P).toString() : null;
			Assert.assertNotNull(name, "Property name cannot be null");
			String value=sb.getAttribute(VALUE)!= null ? sb.getAttribute(VALUE).toString() : null;
			Assert.assertNotNull(value, "value of property's "+name+" cannot be null");
			properties.put(name, value);
		}

		logger.debug("OUT");
	}


}
