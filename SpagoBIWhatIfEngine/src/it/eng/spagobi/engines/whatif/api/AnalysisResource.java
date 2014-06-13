/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 * @class AnalysisResource
 * 
 * Provides services to manage the analysis.

 * 
 */
package it.eng.spagobi.engines.whatif.api;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.common.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.writeback4j.IMemberCoordinates;
import it.eng.spagobi.writeback4j.ISchemaRetriver;
import it.eng.spagobi.writeback4j.sql.AnalysisExporter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.olap4j.metadata.Dimension.Type;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;

@Path("/1.0/analysis")
public class AnalysisResource extends AbstractWhatIfEngineService {

	public static transient Logger logger = Logger.getLogger(AnalysisResource.class);


	private static final String EXPORT_FILE_NAME = "SpagoBIOlapExport";
	private static final String CSV_FIELDS_SEPARATOR = "|";
	private static final String CSV_ROWS_SEPARATOR = "\r\n";
	
	
	@GET
	@Path("/exportCSV/{version}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response exportEditTableCSV( @PathParam("version") int version){

		byte[] csv = null;
		Connection connection;
		WhatIfEngineInstance ei = getWhatIfEngineInstance();

		PivotModel model = ei.getPivotModel();


		logger.debug("Persisting the modifications..");

		IDataSource dataSource = ei.getDataSource();
		try {
			logger.debug("Getting the connection to DB");
			connection = dataSource.getConnection( null );
		} catch (Exception e) {
			logger.error("Error opening connection to datasource "+dataSource.getLabel());
			throw new SpagoBIRuntimeException("Error opening connection to datasource "+dataSource.getLabel(), e);	
		} 
		try {
			SpagoBICellWrapper cellWrapper = (SpagoBICellWrapper)model.getCellSet().getCell(0);

			List<IMemberCoordinates> memberCordinates = new ArrayList<IMemberCoordinates>();

			Member[] members = cellWrapper.getMembers();
			ISchemaRetriver retriver = ei.getWriteBackManager().getRetriver();
			//init the query with the update set statement


			//gets the measures and the coordinates of the dimension members 
			for (int i=0; i< members.length; i++) {
				Member aMember = members[i];


				if(!(aMember.getDimension().getDimensionType().equals(Type.MEASURE))){
					memberCordinates.add(retriver.getMemberCordinates(aMember));
				}

			}

			AnalysisExporter esporter = new AnalysisExporter(ei.getWriteBackManager().getRetriver());
			csv =   esporter.exportCSV(memberCordinates, connection,version, CSV_FIELDS_SEPARATOR, CSV_ROWS_SEPARATOR);
		} catch (Exception e) {
			logger.debug("Error persisting the modifications",e);

		}finally{
			logger.debug("Closing the connection used to persist the modifications");
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("Error closing the connection to the db");
				throw new SpagoBIEngineRestServiceRuntimeException( getLocale(), e);
			}
			logger.debug("Closed the connection used to persist the modifications");
		}


        String fileName = EXPORT_FILE_NAME+"-"+(new Date()).toLocaleString()+".csv";
        
    	return Response
                .ok(csv, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = "+fileName)
                .build();

	}


}
