/**
 * 
 */
package test.writeback.export;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.exception.WhatIfPersistingTransformationException;
import it.eng.spagobi.engines.whatif.model.SpagoBICellSetWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBIPivotModel;
import it.eng.spagobi.engines.whatif.model.transform.CellTransformation;
import it.eng.spagobi.engines.whatif.model.transform.algorithm.DefaultWeightedAllocationAlgorithm;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.writeback4j.IMemberCoordinates;
import it.eng.spagobi.writeback4j.ISchemaRetriver;
import it.eng.spagobi.writeback4j.sql.AnalysisExporter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.olap4j.OlapException;
import org.olap4j.metadata.Dimension.Type;
import org.olap4j.metadata.Member;

import test.AbstractWhatIfTestCase;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public abstract class AbstractWhatIfExportResult extends AbstractWhatIfTestCase {





	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		super.tearDown();
	}




	public void testBuildExportDataQuery() throws Exception{
		WhatIfEngineInstance ei = getWhatifengineiEngineInstance(getCatalogue());
		SpagoBIPivotModel pivotModel = (SpagoBIPivotModel)ei.getPivotModel();

		SpagoBICellSetWrapper cellSetWrapper = (SpagoBICellSetWrapper)pivotModel.getCellSet();
		SpagoBICellWrapper cellWrapper = (SpagoBICellWrapper) cellSetWrapper.getCell(0);

		Double value = (new Random()).nextFloat()*1000000d;

		DefaultWeightedAllocationAlgorithm al = new DefaultWeightedAllocationAlgorithm(ei, false);
		CellTransformation transformation = new CellTransformation(value,cellWrapper.getValue(), cellWrapper, al);
		cellSetWrapper.applyTranformation(transformation);

		Connection connection;
		IDataSource dataSource = ei.getDataSource();

		
		
		try {

			connection = dataSource.getConnection( null );
		} catch (Exception e) {
			fail();
			throw e;
		} 

		try {
			
			List<IMemberCoordinates> memberCordinates = new ArrayList<IMemberCoordinates>();
			
			Member[] members = cellWrapper.getMembers();
			ISchemaRetriver retriver = ei.getWriteBackManager().getRetriver();
			//init the query with the update set statement
			StringBuffer query = new StringBuffer();
			
			//gets the measures and the coordinates of the dimension members 
			for (int i=0; i< members.length; i++) {
				Member aMember = members[i];
				
				try {
					if(!(aMember.getDimension().getDimensionType().equals(Type.MEASURE))){
						memberCordinates.add(retriver.getMemberCordinates(aMember));
					}
				} catch (OlapException e) {
					fail();
					throw e;
				}
			}
			
			AnalysisExporter ae = new AnalysisExporter(retriver);
			
			ResultSet queryString = ae.exportCSV(memberCordinates, connection, 1, "|","");
			
			System.out.println(queryString);
		} catch (WhatIfPersistingTransformationException e) {

			fail();
			throw e;
		}finally{
			try {
				connection.close();
			} catch (SQLException e) {
				throw e;
			}
		}


	}



	public abstract String getCatalogue();



}
