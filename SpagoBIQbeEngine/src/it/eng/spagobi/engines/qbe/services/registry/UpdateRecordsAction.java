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
package it.eng.spagobi.engines.qbe.services.registry;

import it.eng.qbe.datasource.hibernate.HibernateDataSource;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Column;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.engines.qbe.services.initializers.RegistryEngineStartAction;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.property.Setter;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */

public class UpdateRecordsAction extends AbstractQbeEngineAction {
	
	private static final long serialVersionUID = -642121076148276452L;

	public static transient Logger logger = Logger.getLogger(UpdateRecordsAction.class);
	
	// INPUT PARAMETERS
	public static final String RECORDS = "records";
	
	public void service(SourceBean request, SourceBean response)  {	

		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;

		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("QbeEngine.updateRecordsAction.totalTime");

			executeUpdate();
			
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
			
		} catch (Throwable t) {
			errorHitsMonitor = MonitorFactory.start("QbeEngine.updateRecordsAction.errorHits");
			errorHitsMonitor.stop();
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			if (totalTimeMonitor != null) totalTimeMonitor.stop();
			logger.debug("OUT");
		}	
	}

	private void executeUpdate() throws Exception {
		QbeEngineInstance qbeEngineInstance = null;
		RegistryConfiguration registryConf = null;
		JSONArray modifiedRecords = null;
		
		modifiedRecords = this.getAttributeAsJSONArray(RECORDS);
		logger.debug(modifiedRecords);
		if (modifiedRecords == null || modifiedRecords.length() == 0) {
			logger.warn("No records to update....");
			return;
		}
		
		qbeEngineInstance = (QbeEngineInstance) getAttributeFromSession( RegistryEngineStartAction.ENGINE_INSTANCE );
		Assert.assertNotNull(qbeEngineInstance, "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
		
		registryConf = qbeEngineInstance.getRegistryConfiguration();
		Assert.assertNotNull(registryConf, "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of RegistryConfiguration class");

		for (int i = 0; i < modifiedRecords.length(); i++) {
			JSONObject aRecord = modifiedRecords.getJSONObject(i);
			updateRecord(aRecord, qbeEngineInstance, registryConf);
		}
		
	}

	private void updateRecord(JSONObject aRecord,
			QbeEngineInstance qbeEngineInstance,
			RegistryConfiguration registryConf) {
		
		HibernateDataSource ds = (HibernateDataSource) qbeEngineInstance.getDataSource();
		SessionFactory sf = ds.getHibernateSessionFactory();
		Configuration cfg = ds.getHibernateConfiguration();
		Session aSession = null;
		Transaction tx = null;
		try {
			aSession = sf.openSession();
			tx = aSession.beginTransaction();
			String entityName = registryConf.getEntity();
			PersistentClass classMapping = cfg.getClassMapping(entityName);
			ClassMetadata classMetadata = sf.getClassMetadata(entityName);
			String keyName = classMetadata.getIdentifierPropertyName();
			Object key = aRecord.get(keyName);
			Object obj = aSession.load(entityName, (Serializable) key);
			Iterator it = aRecord.keys();
			while (it.hasNext()) {
				String aKey = (String) it.next();
				if (keyName.equals(aKey)) {
					continue;
				}
				Column c = registryConf.getColumnConfiguration(aKey);
				if (c.getSubEntity() != null) {
					// case of foreign key
					Property property = classMapping.getProperty(c.getSubEntity());
					Type propertyType = property.getType();	
					if (propertyType instanceof ManyToOneType) { 
				 		ManyToOneType manyToOnePropertyType = (ManyToOneType) propertyType; 
				 		String entityType = manyToOnePropertyType.getAssociatedEntityName();
						Object referenced = getReferencedObject(aSession, entityType, c.getField(), aRecord.get(aKey));
						Setter setter = property.getSetter(obj.getClass());
						setter.getMethod().invoke(obj, referenced);
					} else {
						throw new SpagoBIEngineServiceException(getActionName(), "Property " + c.getSubEntity() + " is not a many-to-one relation");
					}
				} else {
					// case of property
					Property property = classMapping.getProperty(aKey);
					Setter setter = property.getSetter(obj.getClass());
					Object valueObj = aRecord.get(aKey);
					Object valueConverted = this.convertValue(valueObj, property);
					setter.getMethod().invoke(obj, valueConverted);
				}

			}
			aSession.save(obj);
			tx.commit();
		} catch (Exception e) {

			if ( tx != null ) {
				tx.rollback();
			}
			throw new RuntimeException(e);
		} finally {
			if ( aSession != null ) {
				if ( aSession.isOpen() ) aSession.close();
			}
		}
		
		
		
		
	}

	private Object convertValue(Object valueObj, Property property) {
		if (valueObj == null) {
			return null;
		}
		String value = valueObj.toString();
		Object toReturn = null;
		Type type = property.getType();
		Class clazz = type.getReturnedClass();
		
		if( Number.class.isAssignableFrom(clazz) ) {
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
			toReturn = value;
		} else if( Timestamp.class.isAssignableFrom(clazz) ) {
			// TODO manage dates
			toReturn = value;

		} else if( Date.class.isAssignableFrom(clazz) ) {
			// TODO manage dates
			toReturn = value;

		} else if( Boolean.class.isAssignableFrom(clazz) ) {
			toReturn = Boolean.parseBoolean(value);
		} else {
			toReturn = value;
		}
		
		return toReturn;
	}

	private Object getReferencedObject(Session aSession, String entityType,
			String field, Object value) {
		Query query = aSession.createQuery(" from " + entityType + " where " + field + " = ?");
		query.setParameter(0, value);
		List result = query.list();
		if (result == null || result.size() == 0) {
			throw new SpagoBIEngineServiceException(getActionName(), "Record with " + field + " equals to " + value.toString() + " not found for entity " + entityType);
		}
		if (result.size() > 1) {
			throw new SpagoBIEngineServiceException(getActionName(), "More than 1 record with " + field + " equals to " + value.toString() + " in entity " + entityType);
		}
		return result.get(0);
	}

}
