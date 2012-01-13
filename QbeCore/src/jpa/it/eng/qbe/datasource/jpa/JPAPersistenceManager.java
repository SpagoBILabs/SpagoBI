/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.qbe.datasource.jpa;

import it.eng.qbe.datasource.IPersistenceManager;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Column;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.BasicType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.apache.log4j.Logger;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.json.JSONException;
import org.json.JSONObject;

public class JPAPersistenceManager implements IPersistenceManager {

	public JPAPersistenceManager(JPADataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	private JPADataSource dataSource;
	public static transient Logger logger = Logger
			.getLogger(JPAPersistenceManager.class);

	public JPADataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(JPADataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void updateRecord(JSONObject aRecord,
			RegistryConfiguration registryConf) {
		EntityTransaction tx = null;
		try{
			EntityManager em = dataSource.getEntityManager();
			Metamodel classMetadata = em.getMetamodel();

			String entityName = registryConf.getEntity();
			int lastPkgDot = entityName.lastIndexOf(".");
			String entityNameNoPkg = entityName.substring(lastPkgDot+1);
			Iterator itEnt = classMetadata.getEntities().iterator();
			 tx = em.getTransaction();
		while(itEnt.hasNext()){
			EntityType entity = (EntityType)itEnt.next();
			String jpaEntityName = entity.getName();

			if(entity != null && jpaEntityName.equals(entityNameNoPkg)){

				javax.persistence.metamodel.Type keyT = entity.getIdType();
				String keyName = "";
				if (keyT instanceof BasicType) {
					keyName = (entity.getId(Object.class)).getName();				
				}

				Iterator it = aRecord.keys();
				
				Object key = aRecord.get(keyName);				
				Object obj = em.find(entity.getJavaType(), key);
				
				while (it.hasNext()) {
					String aKey = (String) it.next();
					if (keyName.equals(aKey)) {
						continue;
					}
					Column c = registryConf.getColumnConfiguration(aKey);
					
					if (c.getSubEntity() != null) {
						// case of foreign key
						Attribute a = entity.getAttribute(c.getSubEntity());
						
						Attribute.PersistentAttributeType type = a.getPersistentAttributeType();	
						if (type.equals(PersistentAttributeType.MANY_TO_ONE)) { 	
					 		String entityType = a.getJavaType().getName();
					 		String subKey = a.getName();
					 		int lastPkgDotSub = entityType.lastIndexOf(".");
							String entityNameNoPkgSub = entityType.substring(lastPkgDotSub+1);
							try {
								
								Object referenced = getReferencedObjectJPA(em, entityNameNoPkgSub, c.getField(), aRecord.get(aKey));

								Class clas = entity.getJavaType();
								Field f =clas.getDeclaredField(subKey);
								f.setAccessible(true);
								em.refresh(referenced);
								f.set(obj, referenced);
								
							} catch (JSONException e) {
								logger.error(e);
								throw new SpagoBIRuntimeException("Property " + c.getSubEntity() + " is not a many-to-one relation");
							} catch (Exception e) {
								logger.error(e);
								throw new SpagoBIRuntimeException("Error setting Field " + aKey + "");
							}  

						} else {
							throw new SpagoBIRuntimeException("Property " + c.getSubEntity() + " is not a many-to-one relation");
						}
					} else {
						// case of property
						Attribute a = entity.getAttribute(aKey);
						Class clas;
						try {
							clas = entity.getJavaType();
							Field f =clas.getDeclaredField(aKey);
							f.setAccessible(true);
							Object valueConverted = this.convertValue(aRecord.get(aKey), a);
							f.set(obj, valueConverted);
							
							
						} catch (Exception e) {
							logger.error(e);
							throw new SpagoBIRuntimeException("Error setting Field " + aKey + "");
						}  

					}
				}

			    if(!tx.isActive()){
			    	tx.begin();
			    }


			    em.persist(obj);
			    em.flush();
			    
			    tx.commit();


				}
			}
		}catch (RuntimeException e) {
			    
			    throw new SpagoBIRuntimeException("Error saving entity ");
		}catch (Exception e) {

			    throw new SpagoBIRuntimeException("Error saving entity ");
		}
		finally {
			if ( tx != null && tx.isActive() ) 
				tx.rollback();

		}
		
	}
	private Object convertValue(Object valueObj, Attribute attribute) {
		if (valueObj == null) {
			return null;
		}
		String value = valueObj.toString();
		Object toReturn = null;

		Class clazz = attribute.getJavaType();
		
		if( Number.class.isAssignableFrom(clazz) ) {
			if(value.equals("NaN") || value.equals("null")){
				toReturn = null;
				return toReturn;
			}
			//BigInteger, Integer, Long, Short, Byte
			if (Integer.class.isAssignableFrom(clazz)) {

				toReturn = Integer.parseInt(value);
			} else if (BigInteger.class.isAssignableFrom(clazz)) {
				toReturn = new BigInteger(value);
			} else if (Long.class.isAssignableFrom(clazz)) {
				toReturn = new Long(value);
			} else if (Short.class.isAssignableFrom(clazz)) {
				toReturn = new Short(value);
			} else if (Byte.class.isAssignableFrom(clazz)) {
				toReturn = new Byte(value);
			} else {
				toReturn = new Float(value);
			}
		} else if( String.class.isAssignableFrom(clazz) ) {
			if(value.equals("")){
				toReturn = null;
			}else
				toReturn = value;
		} else if( Timestamp.class.isAssignableFrom(clazz) ) {
			// TODO manage dates
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
