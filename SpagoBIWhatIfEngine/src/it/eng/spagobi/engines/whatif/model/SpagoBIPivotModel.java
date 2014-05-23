/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */

package it.eng.spagobi.engines.whatif.model;

import it.eng.spagobi.engines.whatif.exception.WhatIfPersistingTransformationException;
import it.eng.spagobi.engines.whatif.model.transform.CellTransformation;
import it.eng.spagobi.engines.whatif.model.transform.CellTransformationsAnalyzer;
import it.eng.spagobi.engines.whatif.model.transform.CellTransformationsStack;
import it.eng.spagobi.engines.whatif.model.transform.algorithm.AllocationAlgorithm;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapDataSource;

import com.eyeq.pivot4j.impl.PivotModelImpl;

public class SpagoBIPivotModel extends PivotModelImpl {
	public static transient Logger auditlogger = Logger.getLogger("audit.stack");
	public static transient Logger logger = Logger.getLogger(SpagoBIPivotModel.class);
	private CellTransformationsStack pendingTransformations = new CellTransformationsStack();
	private SpagoBICellSetWrapper wrapper = null;
	
	@Override
	public synchronized CellSet getCellSet() {
		// get cellset from super class (Mondrian)
		CellSet cellSet = super.getCellSet();

		// since the getCellSet method is invoked many times, we get the previous cell set and compare the new one
		SpagoBICellSetWrapper previous = this.getCellSetWrapper();
		if (previous != null && previous.unwrap() == cellSet) { // TODO check if this comparison is 100% valid
			return previous;
		}
		
		// wrap the cellset
		SpagoBICellSetWrapper wrapper = new SpagoBICellSetWrapper(cellSet, this);
		// apply pending transformations
		wrapper.restorePendingTransformations(pendingTransformations);
		// store cell set wrapper
		this.setCellSetWrapper(wrapper);

		return wrapper;
	}

	public SpagoBIPivotModel(OlapDataSource dataSource) {
		super(dataSource);
	}
	
	public SpagoBICellSetWrapper getCellSetWrapper() {
		return wrapper;
	}

	public void setCellSetWrapper(SpagoBICellSetWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public void setValue(Object newValue, Cell cell, AllocationAlgorithm algorithm) {
		// store the transformation into the stack
		SpagoBICellSetWrapper cellSetWrapper = this.getCellSetWrapper();
		SpagoBICellWrapper cellWrapper = SpagoBICellWrapper.wrap(cell, cellSetWrapper);
		CellTransformation transformation = new CellTransformation(newValue, cellWrapper.getValue(), cellWrapper, algorithm);
		pendingTransformations.add(transformation);
		logTransormations();
	}

	public boolean hasPendingTransformations() {
		return pendingTransformations.size() > 0;
	}
	
	public void addPendingTransformation(CellTransformation transformation) {
		pendingTransformations.add(transformation);
		logTransormations();
	}
	
	
	public void persistTransformations() throws WhatIfPersistingTransformationException{
		persistTransformations(null);
	}
	
	/**
	 * Persist the modifications in the selected version
	 * @param version the version of the model in witch persist the modification. In null persist in the version selected in the Version dimension
	 * @throws WhatIfPersistingTransformationException
	 */
	public void persistTransformations(Integer version) throws WhatIfPersistingTransformationException{
		CellTransformationsAnalyzer analyzer = new CellTransformationsAnalyzer();
		CellTransformationsStack bestStack = analyzer.getShortestTransformationsStack(pendingTransformations);
		Iterator<CellTransformation> iterator = bestStack.iterator();
		
		CellTransformationsStack executedTransformations = new  CellTransformationsStack();
		
		while (iterator.hasNext()) {
			CellTransformation transformation = iterator.next();
			try {
				AllocationAlgorithm algorithm = transformation.getAlgorithm();
				algorithm.persist(transformation.getCell(), transformation.getOldValue(), transformation.getNewValue(), version);
			} catch (Throwable e) {
				logger.error("Error persisting the transformation "+transformation, e);
				bestStack.removeAll(executedTransformations);
				logErrorTransformations(bestStack);
				throw new WhatIfPersistingTransformationException(getLocale(), executedTransformations, bestStack, e);
			}
			//update the list of executed transformations
			executedTransformations.add(transformation);
		}
		
		//everithing goes right so we can clean the pending transformations
		pendingTransformations.clear();
		logTransormations("Stack cleaned");
	}
	
	
	/**
	 * Undo last modification
	 */
	public synchronized void undo() {
		if (!this.hasPendingTransformations()) {
			throw new SpagoBIEngineRuntimeException("There are no modifications to undo!!");
		}
		pendingTransformations.remove( pendingTransformations.size() - 1 );
		logTransormations();
		// remove previous stored cell set, in any
		this.setCellSetWrapper(null);
		// force recalculation
		this.getCellSet();
	}
	
	/**
	 * @see com.eyeq.pivot4j.PivotModel#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		this.setCellSetWrapper(null);
	}

	
	public void logTransormations(){
		logTransormations(null);
	}
	
	public void logTransormations(String info){
		if(info!=null){
			auditlogger.info(info);
		}
		auditlogger.info(pendingTransformations.toString());
	}
	
	public void logErrorTransformations(CellTransformationsStack remaningTransformations){
		auditlogger.info(remaningTransformations.toString());
	}
	
	
	
}
