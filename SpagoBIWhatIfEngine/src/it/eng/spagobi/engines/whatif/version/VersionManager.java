/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.version;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.common.WhatIfConstants;
import it.eng.spagobi.engines.whatif.exception.WhatIfPersistingTransformationException;
import it.eng.spagobi.engines.whatif.model.ModelConfig;
import it.eng.spagobi.engines.whatif.model.ModelUtilities;
import it.eng.spagobi.engines.whatif.model.SpagoBIPivotModel;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.writeback4j.sql.ConnectionManager;
import it.eng.spagobi.writeback4j.sql.SqlInsertStatement;
import it.eng.spagobi.writeback4j.sql.SqlQueryStatement;
import it.eng.spagobi.writeback4j.sql.VersionManagementStatements;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.olap4j.OlapException;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.ChangeSlicer;

public class VersionManager {

	public static transient Logger logger = Logger.getLogger(VersionManager.class);

	private PivotModel model;
	private WhatIfEngineInstance instance;
	private ModelConfig modelConfig;

	

	public VersionManager(WhatIfEngineInstance instance, PivotModel model, ModelConfig modelConfig) {
		super();
		this.model = model;
		this.instance = instance;
		this.modelConfig = modelConfig;
	}
	
	
	
	public PivotModel persistNewVersionProcedure(){
		logger.debug("IN");
		
		logger.debug("Set connection to DB");
		ConnectionManager connectionManager = new ConnectionManager(instance.getDataSource());		

		connectionManager.openConnection();

		logger.debug("Get last version");
		Integer lastVersion = getLastVersion(connectionManager);
		logger.debug("Last version to be increased is "+lastVersion);
		
		logger.debug("Duplicate data with new version");
		increaseActualVersion(connectionManager, lastVersion);
		
		//Integer lastVersion = 8;
		
		logger.debug("Reload Model");
		new ModelUtilities().reloadModel(instance, model);
		
		logger.debug("Apply Transformations on new version");
		applyTransformation(lastVersion);
	
		logger.debug("Reload Model");
		new ModelUtilities().reloadModel(instance, model);
		
		logger.debug("Set new version as actual");
		modelConfig.setActualVersion(lastVersion+1);
		
		connectionManager.closeConnection();
		
		logger.debug("OUT");
		return model;	
		
	}
	
	
	public Integer getLastVersion(ConnectionManager connectionManager){
		logger.debug("IN");
		Integer lastVersion = null;
		logger.debug("get Last version");
		
		VersionManagementStatements statement = new VersionManagementStatements(instance.getWriteBackManager().getRetriver(), instance.getDataSource());
		
		try {
			String sqlQuery = statement.buildGetLastVersion();	
			SqlQueryStatement queryStatement = new SqlQueryStatement(instance.getDataSource(), sqlQuery) ;
			Object o= queryStatement.getSingleValue(connectionManager.getConnection(), instance.getWriteBackManager().getRetriver().getVersionColumnName());
			if(o != null){
				logger.debug("Last version is "+o);	
				lastVersion = (Integer) o;
			
			}
			else{
				logger.debug("No last version found, it is assumed to be 0");
				lastVersion = 0;
			}

		
		}
		catch (NumberFormatException e) {
			logger.error("Error in converting to Integer the version value: check your db settings for version column", e);
			connectionManager.closeConnection();
			throw new SpagoBIRuntimeException("Error in converting to Integer the version value: check your db settings for version column", e);		
		}
		catch (SpagoBIEngineException e) {
			logger.error("Error when recovering last model version", e);
			connectionManager.closeConnection();
			throw new SpagoBIRuntimeException("Error when recovering last model version", e);
		}		
		logger.debug("OUT");
		return lastVersion;
	}
	
	
	public void increaseActualVersion(ConnectionManager connectionManager, Integer lastVersion){
		logger.debug("IN");

		VersionManagementStatements statement = new VersionManagementStatements(instance.getWriteBackManager().getRetriver(), instance.getDataSource());
		
		
		
		logger.debug("Data duplication");
		
		try {
			String sqlInsertIntoVirtual = statement.buildInserttoDuplicateData(lastVersion);		
			SqlInsertStatement insertStatement = new SqlInsertStatement(sqlInsertIntoVirtual) ;
			insertStatement.executeStatement(connectionManager.getConnection());
		} catch (SpagoBIEngineException e) {
			logger.error("Error in increasing version procedure: error when duplicating data and changing version", e);
			connectionManager.closeConnection();
			throw new SpagoBIRuntimeException("Error in increasing version procedure: error when duplicating data and changing version", e);
		}	

		
//		logger.debug("delete temporary table");
//		
//		try {
//			String sqlDelete = statement.buildDeleteTemporaryStatement();		
//			SqlUpdateStatement deleteStatement = new SqlUpdateStatement(sqlDelete) ;
//			deleteStatement.executeStatement(connectionManager.getConnection());
//		} catch (SpagoBIEngineException e) {
//			logger.error("Error in increasing version procedure: error when deleteing temporary table", e);
//			connectionManager.closeConnection();
//			throw new SpagoBIRuntimeException("Error in increasing version procedure: error when deleteing temporary table", e);
//		}		
//		
//		logger.debug("Bring data of current version into temporary table");
//		
//		try {
//			String sqlInsertIntoTemporary = statement.buildInsertInTemporaryStatement(lastVersion);		
//			SqlInsertStatement insertStatement = new SqlInsertStatement( sqlInsertIntoTemporary) ;
//			insertStatement.executeStatement(connectionManager.getConnection());
//		} catch (SpagoBIEngineException e) {
//			logger.error("Error in increasing version procedure: error when bringing data in temporary table", e);
//			connectionManager.closeConnection();
//			throw new SpagoBIRuntimeException("Error in increasing version procedure: error when bringing data in temporary table", e);
//		}		
//
//		logger.debug("Update data of temporary table to current version");
//		
//		try {
//			String sqlUpdateVersionTemporary = statement.buildUpdateVersionNumberStatement(lastVersion);		
//			SqlUpdateStatement updateStatement = new SqlUpdateStatement(sqlUpdateVersionTemporary) ;
//			updateStatement.executeStatement(connectionManager.getConnection());
//		} catch (SpagoBIEngineException e) {
//			logger.error("Error in increasing version procedure: error when updating version number in temporary table", e);
//			connectionManager.closeConnection();
//			throw new SpagoBIRuntimeException("Error in increasing version procedure: error when updating version number in temporary table", e);
//		}		
//		
//		
//		logger.debug("Bring data from temporary to virtualtable");
//		
//		try {
//			String sqlInsertIntoVirtual = statement.buildInsertInVirtualStatement(lastVersion);		
//			SqlInsertStatement insertStatement = new SqlInsertStatement(sqlInsertIntoVirtual) ;
//			insertStatement.executeStatement(connectionManager.getConnection());
//		} catch (SpagoBIEngineException e) {
//			logger.error("Error in increasing version procedure: error when bringing data back from temporary to virtual table", e);
//			connectionManager.closeConnection();
//			throw new SpagoBIRuntimeException("Error in increasing version procedure: error when bringing data back from temporary to virtual table", e);
//		}		

	
		
		logger.debug("OUT");
		
	}
	
	
	
	
	
	
	public void applyTransformation(Integer lastVersion){
		logger.debug("IN");
		
		// get cube
		Cube cube = model.getCube();

		// get dimension
		NamedList<Dimension> dimensions =  cube.getDimensions();
		Dimension versionDimension = null;
		for (Iterator iterator = dimensions.iterator(); iterator.hasNext();) {
			Dimension dimension = (Dimension) iterator.next();
			if(dimension.getUniqueName().equals(WhatIfConstants.VERSION_DIMENSION_UNIQUENAME)){
				versionDimension = dimension;
			};
		}
		if(versionDimension == null){
			logger.error("Could not find version dimension");
			throw new SpagoBIEngineRuntimeException("Could not find version dimension");
		}
		logger.debug("Found dimension "+versionDimension.getUniqueName());
		
		// get Hierarchy Used by dimension version
		NamedList<Hierarchy> hierarchies = versionDimension.getHierarchies();
		Hierarchy hierarchy = null;
		if(hierarchies == null || hierarchies.size() == 0 ){
			logger.error("Could not find hierarchies for version dimension");
			throw new SpagoBIEngineRuntimeException("Could not find hierarchies for version dimension");
		}
		else if(hierarchies.size()==1){
			hierarchy = hierarchies.get(0);
		}
		else{
			String hierarchyUsed = modelConfig.getDimensionHierarchyMap().get(WhatIfConstants.VERSION_DIMENSION_UNIQUENAME);
			hierarchy = hierarchies.get(hierarchyUsed);	
		}
		logger.debug("Found hierarchy "+hierarchy.getUniqueName());
		
		// get member of new version
		
		Integer newVersion = lastVersion+1;
		logger.debug("New version is "+newVersion);		
		Member member = null;
		try {
			NamedList<Member> members = hierarchy.getRootMembers();
			
			//get last member
			logger.debug("Member list has "+members.size()+" members");
			
			member = members.get(members.size()-1);
							
		} catch (OlapException e) {
			logger.error("Error when searching for current version member among the version one");
			throw new SpagoBIEngineRuntimeException("Could not find current version member",e);
		}
		
		ChangeSlicer ph =  model.getTransform(ChangeSlicer.class);
		List<Member> slicers = ph.getSlicer(hierarchy);
				

		slicers.clear();

		slicers.add(member);
		ph.setSlicer(hierarchy,slicers);
		
		logger.debug("Slicer is set");

		SpagoBIPivotModel modelWrapper = (SpagoBIPivotModel) model;
		try {
			((SpagoBIPivotModel)model).persistTransformations();
		} catch (WhatIfPersistingTransformationException e) {
			logger.debug("Error persisting the modifications",e);
			throw new SpagoBIEngineRestServiceRuntimeException(e.getLocalizationmessage(), modelWrapper.getLocale(), "Error persisting modifications", e);
		}
		logger.debug("OUT");
	
	}

}
