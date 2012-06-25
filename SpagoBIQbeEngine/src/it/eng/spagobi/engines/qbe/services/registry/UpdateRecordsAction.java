/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.services.registry;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.hibernate.HibernateDataSource;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.spago.base.SourceBean;
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
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.json.JSONException;
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
		IDataSource genericDatasource = qbeEngineInstance.getDataSource();
		genericDatasource.getPersistenceManager().updateRecord(aRecord, registryConf);
	}
	
}
