/**
 * 
 */
package test.writeback;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.exception.WhatIfPersistingTransformationException;
import it.eng.spagobi.engines.whatif.model.SpagoBICellSetWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBIPivotModel;
import it.eng.spagobi.engines.whatif.model.transform.CellTransformation;
import it.eng.spagobi.engines.whatif.model.transform.algorithm.DefaultWeightedAllocationAlgorithm;
import it.eng.spagobi.writeback4j.mondrian.CacheManager;

import java.util.Random;

import org.eigenbase.xom.AssertFailure;
import org.olap4j.CellSet;
import org.olap4j.Position;

import test.AbstractWhatIfTestCase;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public class WriteBackTestCase extends AbstractWhatIfTestCase {
	
	public static final Double accurancy = 0.00001d;
	
	
	
	public void setUp() throws Exception {
		super.setUp();
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}

	

	
	public Double persistTransformations(it.eng.spagobi.tools.datasource.bo.DataSource ds, String catalog){
		WhatIfEngineInstance ei = getWhatifengineiEngineInstance(ds, catalog);
		SpagoBIPivotModel pivotModel = (SpagoBIPivotModel)ei.getPivotModel();
	
		SpagoBICellSetWrapper cellSetWrapper = (SpagoBICellSetWrapper)pivotModel.getCellSet();
		SpagoBICellWrapper cellWrapper = (SpagoBICellWrapper) cellSetWrapper.getCell(0);

		Double value = 1000000d;
		
		
		CellTransformation transformation = new CellTransformation(value,cellWrapper.getValue(), cellWrapper, new DefaultWeightedAllocationAlgorithm(ei));
		cellSetWrapper.applyTranformation(transformation);

			try {
				pivotModel.persistTransformations();
			} catch (WhatIfPersistingTransformationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			}

		
		CacheManager.flushCache(pivotModel.getDataSource());
		String mdx = pivotModel.getMdx();
		pivotModel.setMdx( mdx);
		pivotModel.initialize();
		
		cellSetWrapper = (SpagoBICellSetWrapper)pivotModel.getCellSet();
		cellWrapper = (SpagoBICellWrapper) cellSetWrapper.getCell(0);

		Double newValue =  (Double) cellWrapper.getValue();
		Double ration = 1-newValue/value;
		
		return ration;
		
		
	}
	

}
