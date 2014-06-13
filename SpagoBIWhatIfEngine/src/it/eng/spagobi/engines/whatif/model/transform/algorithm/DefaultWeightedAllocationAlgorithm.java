/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 */

package it.eng.spagobi.engines.whatif.model.transform.algorithm;

import it.eng.spagobi.engines.whatif.WhatIfEngine;
import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.model.SpagoBICellSetWrapper;
import it.eng.spagobi.engines.whatif.model.SpagoBICellWrapper;
import it.eng.spagobi.engines.whatif.model.transform.CellRelation;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.writeback4j.sql.DefaultWeightedAllocationAlgorithmPersister;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.olap4j.Position;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class DefaultWeightedAllocationAlgorithm extends AllocationAlgorithm {

	public static final String NAME = "DEFAULT_WEIGHTED_ALLOCATION_ALGORITHM";
	
	private static Logger logger = Logger.getLogger(DefaultWeightedAllocationAlgorithm.class);
	private WhatIfEngineInstance ei;
	private DefaultWeightedAllocationAlgorithmPersister persister;
	private String lastQuery;
	
	
	public DefaultWeightedAllocationAlgorithm(WhatIfEngineInstance ei, boolean useInClause) {
		super();
		this.ei = ei;
		persister = new DefaultWeightedAllocationAlgorithmPersister(ei.getWriteBackManager().getRetriver(), ei.getDataSource());
		persister.setUseInClause(useInClause);
	}

	
	public DefaultWeightedAllocationAlgorithm(WhatIfEngineInstance ei) {
		this(ei, WhatIfEngine.getConfig().getProportionalAlghorithmConf());
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(SpagoBICellWrapper cell, Object oldValue,
			Object newValue, SpagoBICellSetWrapper cellSetWrapper) {
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
		
		logger.debug("IN");
		try {
			totalTimeMonitor = MonitorFactory.start("SpagoBIWhatIfEngine/it.eng.spagobi.engines.whatif.model.transform.DefaultWeightedAllocationAlgorithm.apply.totalTime");
			this.applyInternal(cell, oldValue, newValue, cellSetWrapper);
		} catch (Exception e) {
			errorHitsMonitor = MonitorFactory.start("SpagoBIWhatIfEngine/it.eng.spagobi.engines.whatif.model.transform.DefaultWeightedAllocationAlgorithm.apply.errorHits");
			errorHitsMonitor.stop();
			throw new SpagoBIRuntimeException("Error while applying transformation", e);
		} finally {
			if (totalTimeMonitor != null) {
				totalTimeMonitor.stop();
			}
			logger.debug("OUT");
		}

	}

	private void applyInternal(SpagoBICellWrapper cell, Object oldValue,
			Object newValue, SpagoBICellSetWrapper cellSetWrapper) throws Exception {
		
		// Iteration over a two-axis query
		for (Position axis_0_Position : cellSetWrapper.getAxes()
				.get(0).getPositions()) {

			for (Position axis_1_Position : cellSetWrapper.getAxes()
					.get(1).getPositions()) {

				SpagoBICellWrapper wrappedCell = (SpagoBICellWrapper) cellSetWrapper.getCell(axis_0_Position, axis_1_Position);
				
				CellRelation relation = wrappedCell.getRelationTo(cell);
				Double newDoubleValue = null;
				switch (relation) {
				case EQUAL : 
					newDoubleValue = ((Number) newValue).doubleValue();
					wrappedCell.setValue(newDoubleValue);
					break;
				case ABOVE : 
					// in case we modified a cell that had no value, we consider 0 as previous value
					if (oldValue == null) {
						oldValue = 0;
					}
					newDoubleValue = wrappedCell.getDoubleValue() + ((Number) newValue).doubleValue() -  ((Number) oldValue).doubleValue();
					wrappedCell.setValue(newDoubleValue);
					break;
				case BELOW :
					// in case the cell is below and doesn't contain a value, we don't modify it
					if (wrappedCell.isNull() || wrappedCell.isError() || wrappedCell.isEmpty()) {
						continue;
					}
					newDoubleValue = wrappedCell.getDoubleValue() * ((Number) newValue).doubleValue() /  ((Number) oldValue).doubleValue();
					wrappedCell.setValue(newDoubleValue);
					break;
				default:
					break;
				}
				
			}
		}
	}

	
	

	public void persist(SpagoBICellWrapper cell, Object oldValue, Object newValue, Connection connection, Integer version) throws Exception {
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
		
		logger.debug("IN");
		try {
			totalTimeMonitor = MonitorFactory.start("SpagoBIWhatIfEngine/it.eng.spagobi.engines.whatif.model.transform.DefaultWeightedAllocationAlgorithm.persist.totalTime");
			this.persistInternal(cell, oldValue, newValue, connection, version);
		} catch (Exception e) {
			errorHitsMonitor = MonitorFactory.start("SpagoBIWhatIfEngine/it.eng.spagobi.engines.whatif.model.transform.DefaultWeightedAllocationAlgorithm.persist.errorHits");
			errorHitsMonitor.stop();
			throw e;
		} finally {
			if (totalTimeMonitor != null) {
				totalTimeMonitor.stop();
			}
			logger.debug("OUT");
		}

	}

	private void persistInternal(SpagoBICellWrapper cell, Object oldValue,Object newValue, Connection connection, Integer version) throws Exception {
		Double prop = ((Number) newValue).doubleValue()/((Number) oldValue).doubleValue();
		lastQuery = persister.executeProportionalUpdate(cell.getMembers(), prop, connection, version);
	}


	public String getLastQuery() {
		return lastQuery;
	}


	public DefaultWeightedAllocationAlgorithmPersister getPersister() {
		return persister;
	}
	
	

}
	


