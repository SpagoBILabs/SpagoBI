/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.datasource.jpa;

import it.eng.qbe.datasource.IPersistenceManager;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Column;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class JPAPersistenceManager implements IPersistenceManager {

	private JPADataSource dataSource;
	
	public static transient Logger logger = Logger.getLogger(JPAPersistenceManager.class);

	
	public JPAPersistenceManager(JPADataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}
	
	public JPADataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(JPADataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void updateRecord(JSONObject aRecord, RegistryConfiguration registryConf) {
		
		EntityTransaction entityTransaction = null;
		
		logger.debug("IN");
		EntityManager entityManager = null;
		try {
			Assert.assertNotNull(aRecord, "Input parameter [record] cannot be null");
			Assert.assertNotNull(aRecord, "Input parameter [registryConf] cannot be null");
			
			logger.debug("New record: " + aRecord.toString(3));
			logger.debug("Target entity: " + registryConf.getEntity());
			
			entityManager = dataSource.getEntityManager();
			Assert.assertNotNull(entityManager, "entityManager cannot be null");
			
			entityTransaction = entityManager.getTransaction();
			
			EntityType targetEntity = getTargetEntity(registryConf, entityManager);
			String keyAttributeName = getKeyAttributeName(targetEntity);
			logger.debug("Key attribute name is equal to " + keyAttributeName);
			
			Iterator it = aRecord.keys();
				
			Object keyColumnValue = aRecord.get(keyAttributeName);
			logger.debug("Key of new record is equal to " + keyColumnValue);
			logger.debug("Key column java type equal to [" + targetEntity.getJavaType() + "]");
			Attribute a = targetEntity.getAttribute(keyAttributeName);
			Object obj = entityManager.find(targetEntity.getJavaType(), this.convertValue(keyColumnValue, a));
			logger.debug("Key column class is equal to [" + obj.getClass().getName() + "]");
				
			while (it.hasNext()) {
				String attributeName = (String) it.next();
				logger.debug("Processing column [" + attributeName + "] ...");
				
				if (keyAttributeName.equals(attributeName)) {
					logger.debug("Skip column [" + attributeName + "] because it is the key of the table");
					continue;
				}
				Column column = registryConf.getColumnConfiguration(attributeName);
				
				if (column.getSubEntity() != null) {
					logger.debug("Column [" + attributeName + "] is a foreign key");
					manageForeignKey(targetEntity, column, obj, attributeName, aRecord, entityManager);
				} else {
					logger.debug("Column [" + attributeName + "] is a normal column");
					manageProperty(targetEntity, obj, attributeName, aRecord);
				}
			}

			if(!entityTransaction.isActive()){
				entityTransaction.begin();
			}
	
	
			entityManager.persist(obj);
			entityManager.flush();				    
			entityTransaction.commit();
			
		} catch (Throwable t) {
			if ( entityTransaction != null && entityTransaction.isActive() ) {
				entityTransaction.rollback();
			}
			logger.error(t);
			throw new SpagoBIRuntimeException("Error saving entity", t);
		} finally {
			if ( entityManager != null ) {
				if ( entityManager.isOpen() ) {
					entityManager.close();
				}
			}
			logger.debug("OUT");
		}
		
	}
	
	public EntityType getTargetEntity(RegistryConfiguration registryConf, EntityManager entityManager) {
		
		EntityType targetEntity;
		
		String targetEntityName = getTargetEntityName(registryConf);
	
		Metamodel classMetadata = entityManager.getMetamodel();
		Iterator it = classMetadata.getEntities().iterator();

		targetEntity = null;
		while(it.hasNext()) {
			EntityType entity = (EntityType)it.next();
			String jpaEntityName = entity.getName();

			if(entity != null && jpaEntityName.equals(targetEntityName)) {
				targetEntity = entity;
				break;
			}
		}
		
		return targetEntity;
	}
	
	public String getKeyAttributeName(EntityType entity) {
		logger.debug("IN : entity = [" + entity + "]");
		String keyName = null;
		for(Object attribute : entity.getAttributes()) {
			if (attribute instanceof SingularAttribute) {
				SingularAttribute s = (SingularAttribute) attribute;
				logger.debug("Attribute: "+s.getName()+" is a singular attribute.");
				if (s.isId()) {
					keyName = s.getName();
					break;
				}
			} else {
				logger.debug("Attribute " + attribute + " is not singular attribute, cannot manage it");
			}
		}
		Assert.assertNotNull(keyName, "Key attribute name was not found!");
		logger.debug("OUT : " + keyName);
		return keyName;
	}
	
	// case of foreign key
	private void manageForeignKey(EntityType targetEntity, Column c, 
			Object obj, String aKey, JSONObject aRecord, EntityManager entityManager) {
		
		logger.debug("column " + aKey + " is a FK");
		
		Attribute a = targetEntity.getAttribute(c.getSubEntity());
		
		Attribute.PersistentAttributeType type = a.getPersistentAttributeType();	
		if (type.equals(PersistentAttributeType.MANY_TO_ONE)) { 	
			String entityType = a.getJavaType().getName();
			String subKey = a.getName();
			int lastPkgDotSub = entityType.lastIndexOf(".");
			String entityNameNoPkgSub = entityType.substring(lastPkgDotSub+1);
					
			try {
				Object referenced = getReferencedObjectJPA(entityManager, entityNameNoPkgSub, c.getField(), aRecord.get(aKey));

				Class clas = targetEntity.getJavaType();
				Field f =clas.getDeclaredField(subKey);
				f.setAccessible(true);
				entityManager.refresh(referenced);
				f.set(obj, referenced);
			} catch (JSONException e) {
				logger.error(e);
				throw new SpagoBIRuntimeException("Property " + c.getSubEntity() + " is not a many-to-one relation", e);
			} catch (Exception e) {
				throw new SpagoBIRuntimeException("Error setting Field " + aKey + "", e);
			}  
		} else {
			throw new SpagoBIRuntimeException("Property " + c.getSubEntity() + " is not a many-to-one relation");
		}
	}
	
	private void manageProperty(EntityType targetEntity, Object obj, String aKey, JSONObject aRecord) {
		
		logger.debug("IN");
		
		try {
			Attribute a = targetEntity.getAttribute(aKey);
			Class clas = targetEntity.getJavaType();
			Field f =clas.getDeclaredField(aKey);
			f.setAccessible(true);
			Object valueConverted = this.convertValue(aRecord.get(aKey), a);
			f.set(obj, valueConverted);
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Error setting Field " + aKey + "", e);
		}  finally {
			logger.debug("OUT");
		}
	}
	
	private String getTargetEntityName(RegistryConfiguration registryConf) {
		String entityName = registryConf.getEntity();
		int lastPkgDot = entityName.lastIndexOf(".");
		String entityNameNoPkg = entityName.substring(lastPkgDot+1);
		return entityNameNoPkg;
	}

	private Object convertValue(Object valueObj, Attribute attribute) {
		if (valueObj == null) {
			return null;
		}
		String value = valueObj.toString();
		Object toReturn = null;

		Class clazz = attribute.getJavaType();
		String clazzName = clazz.getName();
		logger.error("Field type: " + clazzName);
		
		if( Number.class.isAssignableFrom(clazz) ) {
			if(value.equals("NaN") || value.equals("null")){
				toReturn = null;
				return toReturn;
			}
			//BigInteger, Integer, Long, Short, Byte
			if (Integer.class.getName().equals(clazzName)) {
				logger.error(">>> Integer");
				toReturn = Integer.parseInt(value);
			} else if (Double.class.getName().equals(clazzName)) {
				logger.error(">>> Double");
				toReturn = new Double(value);
			} else if (BigDecimal.class.getName().equals(clazzName)) {
				logger.error(">>> BigDecimal");
				toReturn = new BigDecimal(value);
			} else if (BigInteger.class.getName().equals(clazzName)) {
				logger.error(">>> BigInteger");
				toReturn = new BigInteger(value);
			} else if (Long.class.getName().equals(clazzName)) {
				logger.error(">>> Long");
				toReturn = new Long(value);
			} else if (Short.class.getName().equals(clazzName)) {
				logger.error(">>> Short");
				toReturn = new Short(value);
			} else if (Byte.class.getName().equals(clazzName)) {
				logger.error(">>> Byte");
				toReturn = new Byte(value);
			} else {
				logger.error(">>> Float");
				toReturn = new Float(value);
			}
		} else if( String.class.isAssignableFrom(clazz) ) {
			if(value.equals("")){
				toReturn = null;
			}else
				toReturn = value;
		} else if( Timestamp.class.isAssignableFrom(clazz) ) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			try {
				toReturn = sdf.parse(value);
			} catch (ParseException e) {
				logger.error("Unparsable timestamp", e);
			}

		} else if( Date.class.isAssignableFrom(clazz) ) {
			// TODO manage dates
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			try {
				toReturn = sdf.parse(value);
			} catch (ParseException e) {
				logger.error("Unparsable date", e);
			}

		} else if( Boolean.class.isAssignableFrom(clazz) ) {
			toReturn = Boolean.parseBoolean(value);
		} else {
			toReturn = value;
		}
		
		return toReturn;
	}
	private Object getReferencedObjectJPA(EntityManager em, String entityType,
			String field, Object value) {

		final List result = em.createQuery(
				"select x from " + entityType + " x where x." + field
						+ " = :val").setParameter("val", value).getResultList();

		if (result == null || result.size() == 0) {
			throw new SpagoBIRuntimeException("Record with " + field
					+ " equals to " + value.toString()
					+ " not found for entity " + entityType);
		}
		if (result.size() > 1) {
			throw new SpagoBIRuntimeException("More than 1 record with "
					+ field + " equals to " + value.toString() + " in entity "
					+ entityType);
		}

		return result.get(0);
	}
}
