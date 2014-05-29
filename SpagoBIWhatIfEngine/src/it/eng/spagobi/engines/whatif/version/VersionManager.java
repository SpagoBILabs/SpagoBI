/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.version;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.cube.CubeUtilities;
import it.eng.spagobi.engines.whatif.exception.WhatIfPersistingTransformationException;
import it.eng.spagobi.engines.whatif.model.ModelUtilities;
import it.eng.spagobi.engines.whatif.model.SpagoBIPivotModel;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.writeback4j.sql.SqlInsertStatement;
import it.eng.spagobi.writeback4j.sql.SqlQueryStatement;
import it.eng.spagobi.writeback4j.sql.VersionManagementStatements;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.olap4j.OlapException;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.transform.ChangeSlicer;

public class VersionManager {

	public static transient Logger logger = Logger.getLogger(VersionManager.class);

	private WhatIfEngineInstance instance;

	public VersionManager(WhatIfEngineInstance instance) {
		super();
		this.instance = instance;
	}
	
	public PivotModel persistNewVersionProcedure(){
		return persistNewVersionProcedure(null);
	}
	
	public PivotModel persistNewVersionProcedure(Integer newVersion){
		Integer actualVersion = getActualVersionSlicer();
		return persistNewVersionProcedure(actualVersion, newVersion);
	}

	/**
	 * Creates a new version in the db and persists the modifications in the new version
	 * @param version the actual version (the new one will be version+1)
	 * @return
	 */
	public PivotModel persistNewVersionProcedure(Integer version, Integer newVersion){
		logger.debug("IN");

		
		Connection connection;
		IDataSource dataSource = instance.getDataSource();
		
		try {
			logger.debug("Getting the connection to DB");
			connection = dataSource.getConnection( null );
		} catch (Exception e) {
		logger.error("Error in opening connection to datasource "+dataSource.getLabel());
			throw new SpagoBIRuntimeException("Error in opening connection to datasource "+dataSource.getLabel(), e);	
		} 
		
		try {
			
			if(newVersion == null){
				logger.debug("Get last version");
				newVersion = getLastVersion(connection);
				newVersion = newVersion+1;
				logger.debug("Tne new version is "+newVersion);
			}

			logger.debug("Duplicate data with new version");
			increaseActualVersion(connection, version, newVersion);

			logger.debug("Apply Transformations on new version");
			applyTransformation(connection, newVersion);

			logger.debug("Reload Model");
			new ModelUtilities().reloadModel(instance, instance.getPivotModel());

			logger.debug("Set new version as actual");
			instance.getModelConfig().setActualVersion(newVersion);

			
		} catch (Exception e) {
			logger.error("Error creating a new version",e);
			throw new SpagoBIEngineRestServiceRuntimeException("versionresource.generic.error", instance.getLocale(), e);
		}finally{
			logger.debug("Closing the SAVE AS connection");
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("Error closing the connection to the db");
				throw new SpagoBIEngineRestServiceRuntimeException( instance.getLocale(), e);
			}
			logger.debug("SAVE AS connection closed");
		}

		setNextVersionSlicer(newVersion);

		logger.debug("OUT");
		return instance.getPivotModel();	

	}

	private void setNextVersionSlicer(Integer version){
		logger.debug("IN");

		// get cube
		Cube cube = instance.getPivotModel().getCube();

		Hierarchy hierarchy = CubeUtilities.getVersionHierarchy(cube, instance.getModelConfig());

		logger.debug("New version is "+version);		
		Member member = null;
		try {
			List<Member> members = hierarchy.getRootMembers();

			//get last member
			logger.debug("Member list has "+members.size()+" members");

			member = members.get(members.size()-1);

		} catch (OlapException e) {
			logger.error("Error when searching for current version member among the version one");
			throw new SpagoBIEngineRuntimeException("Could not find current version member",e);
		}

		ChangeSlicer ph =  instance.getPivotModel().getTransform(ChangeSlicer.class);
		List<Member> slicers = ph.getSlicer(hierarchy);

		slicers.clear();

		slicers.add(member);
		ph.setSlicer(hierarchy,slicers);

		logger.debug("Slicer is set");
	}

	private Integer getActualVersionSlicer(){
		logger.debug("IN");

		// get cube
		Cube cube = instance.getPivotModel().getCube();

		Hierarchy hierarchy = CubeUtilities.getVersionHierarchy(cube, instance.getModelConfig());
		
		ChangeSlicer ph =  instance.getPivotModel().getTransform(ChangeSlicer.class);
		List<Member> slicers = ph.getSlicer(hierarchy);
		
		if(slicers == null || slicers.size()==0){
			logger.error( "No version slicer deifined in the mdx query");
			throw new SpagoBIEngineRestServiceRuntimeException("versionresource.getactualversion.no.slicer.error", instance.getLocale(), "No version in the mdx query");
		}
		
		String slicerValue = slicers.get(0).getName();
		
		if(slicerValue == null){
			logger.error( "No version slicer deifined in the mdx query");
			throw new SpagoBIEngineRestServiceRuntimeException("versionresource.getactualversion.no.slicer.error", instance.getLocale(), "No version in the mdx query");
		}
		
		logger.debug("The actual version is "+slicerValue);
		logger.debug("OUT");
		return new Integer(slicerValue);

	}

	public Integer getLastVersion(Connection connection) throws SpagoBIEngineException, NumberFormatException{
		logger.debug("IN");
		Integer lastVersion = null;
		logger.debug("get Last version");

		if(instance.getModelConfig().getActualVersion()!=null){
			return instance.getModelConfig().getActualVersion();
		}

		VersionManagementStatements statement = new VersionManagementStatements(instance.getWriteBackManager().getRetriver(), instance.getDataSource());

		try {
			String sqlQuery = statement.buildGetLastVersion();	
			SqlQueryStatement queryStatement = new SqlQueryStatement(instance.getDataSource(), sqlQuery) ;
			Object o= queryStatement.getSingleValue(connection, instance.getWriteBackManager().getRetriver().getVersionColumnName());
			if(o != null){
				logger.debug("Last version is "+o);	

				// Oracle case
				if(o instanceof BigDecimal){
					lastVersion = ((BigDecimal)o).intValue();
				}
				else{
					lastVersion = (Integer) o;
				}
			}
			else{
				logger.debug("No last version found, it is assumed to be 0");
				lastVersion = 0;
			}
		}
		catch (NumberFormatException e) {
			logger.error("Error in converting to Integer the version value: check your db settings for version column", e);
			throw e;		
		}
		catch (SpagoBIEngineException e) {
			logger.error("Error when recovering last model version", e);
			throw e;
		}		
		logger.debug("OUT");
		return lastVersion;
	}


	private String increaseActualVersion(Connection connection, Integer lastVersion, Integer newVersion) throws SpagoBIEngineException{
		logger.debug("IN");

		VersionManagementStatements statement = new VersionManagementStatements(instance.getWriteBackManager().getRetriver(), instance.getDataSource());
		String sqlInsertIntoVirtual = null;


		logger.debug("Data duplication");

		try {
			sqlInsertIntoVirtual = statement.buildInserttoDuplicateData(lastVersion, newVersion);	
			logger.debug("The query for the new version is "+sqlInsertIntoVirtual);
			SqlInsertStatement insertStatement = new SqlInsertStatement(sqlInsertIntoVirtual) ;
			long dateBefore = System.currentTimeMillis();
			insertStatement.executeStatement(connection);
			long dateAfter = System.currentTimeMillis();
			logger.debug("Time to insert the new version "+(dateAfter-dateBefore));
		} catch (SpagoBIEngineException e) {
			logger.error("Error in increasing version procedure: error when duplicating data and changing version", e);
			throw e;
		}	

		logger.debug("OUT");
		return sqlInsertIntoVirtual;
	}

	private void applyTransformation(Connection connection, Integer newVersion) throws Exception{
		logger.debug("IN");

		// get member of new version
		logger.debug("New version is "+newVersion);		

		try {
			((SpagoBIPivotModel)instance.getPivotModel()).persistTransformations(connection, newVersion);
		} catch (WhatIfPersistingTransformationException e) {
			logger.debug("Error persisting the modifications",e);
			throw e;
		}
		logger.debug("OUT");

	}

}
