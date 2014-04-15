/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */

package it.eng.spagobi.engines.whatif.model.transform.algorithm;

import it.eng.spagobi.engines.whatif.model.SpagoBICellSetWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.engines.whatif.model.transform.CellRelation;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.olap4j.Cell;
import org.olap4j.Position;
import org.olap4j.metadata.Measure;
import org.olap4j.metadata.Member;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class DefaultWeightedAllocationAlgorithm extends AllocationAlgorithm {

	public static final String NAME = "DEFAULT_WEIGHTED_ALLOCATION_ALGORITHM";
	
	private static Logger logger = Logger.getLogger(DefaultWeightedAllocationAlgorithm.class);
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(Member[] members, Object oldValue, Object newValue,
			SpagoBICellSetWrapper spagoBICellSetWrapper) {
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
		
		logger.debug("IN");
		try {
			totalTimeMonitor = MonitorFactory.start("SpagoBIWhatIfEngine/it.eng.spagobi.engines.whatif.model.transform.DefaultWeightedAllocationAlgorithm.totalTime");
			this.applyInternal(members, oldValue, newValue, spagoBICellSetWrapper);
		} catch (Exception e) {
			errorHitsMonitor = MonitorFactory.start("SpagoBIWhatIfEngine/it.eng.spagobi.engines.whatif.model.transform.DefaultWeightedAllocationAlgorithm.errorHits");
			errorHitsMonitor.stop();
			throw new SpagoBIRuntimeException("Error while applying transformation", e);
		} finally {
			if (totalTimeMonitor != null) {
				totalTimeMonitor.stop();
			}
			logger.debug("OUT");
		}

	}

	private void applyInternal(Member[] members, Object oldValue, Object newValue,
			SpagoBICellSetWrapper cellSetWrapper) throws Exception {
		
		/*
		 * First, I iterate all possible cells and store the dimensions in
		 * one String array, the measure (possibly default measure) in
		 * String, and Value in String.
		 */
		List<List<List>> denormalised = new ArrayList<List<List>>();

		HashMap<Integer, Measure> measureMap = new HashMap<Integer, Measure>();
		
		// Iteration over a two-axis query
		for (Position axis_0_Position : cellSetWrapper.getAxes()
				.get(0).getPositions()) {

			for (Position axis_1_Position : cellSetWrapper.getAxes()
					.get(1).getPositions()) {

				SpagoBICellWrapper wrappedCell = (SpagoBICellWrapper) cellSetWrapper.getCell(axis_0_Position, axis_1_Position);
				
				CellRelation relation = wrappedCell.getRelationTo(members);
				Double newDoubleValue = null;
				switch (relation) {
				case EQUAL : 
					newDoubleValue = ((Number) newValue).doubleValue();
					wrappedCell.setValue(newDoubleValue);
					break;
				case ABOVE : 
					newDoubleValue = wrappedCell.getDoubleValue() + ((Number) newValue).doubleValue() -  ((Number) oldValue).doubleValue();
					wrappedCell.setValue(newDoubleValue);
					break;
				case BELOW : 
					newDoubleValue = wrappedCell.getDoubleValue() * ((Number) newValue).doubleValue() /  ((Number) oldValue).doubleValue();
					wrappedCell.setValue(newDoubleValue);
					break;
				default:
					break;
				}
				
			}
		}
	}

}
