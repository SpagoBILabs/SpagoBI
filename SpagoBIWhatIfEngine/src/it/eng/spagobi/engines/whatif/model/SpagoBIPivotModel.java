/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */

package it.eng.spagobi.engines.whatif.model;

import it.eng.spagobi.engines.whatif.model.transform.CellTransformation;
import it.eng.spagobi.engines.whatif.model.transform.CellTransformationsStack;
import it.eng.spagobi.engines.whatif.model.transform.algorithm.AllocationAlgorithm;

import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapDataSource;

import com.eyeq.pivot4j.impl.PivotModelImpl;

public class SpagoBIPivotModel extends PivotModelImpl {
	
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
		CellTransformation transformation = new CellTransformation(newValue, cellWrapper.getValue(), cellWrapper.getMembers(), algorithm);
		pendingTransformations.add(transformation);
	}

	public boolean hasPendingTransformations() {
		return pendingTransformations.size() > 0;
	}
	
	public void addPendingTransformation(CellTransformation transformation) {
		pendingTransformations.add(transformation);
	}
	
}
