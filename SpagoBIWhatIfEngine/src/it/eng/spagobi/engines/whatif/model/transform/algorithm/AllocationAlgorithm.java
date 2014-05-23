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

public abstract class AllocationAlgorithm {
	
	public abstract String getName();

	/**
	 * Apply the allocation algorithm to the target cellset, given modified cell, the old value and the new value.
	 * Pay attention to the fact that the modified cell may refer to a cellset that is DIFFERENT from the target cellset. 
	 * @param cell The modified cell
	 * @param oldValue The old value of the modified cell
	 * @param newValue The new value of the modified cell
	 * @param targetCellSet The cell set to modify
	 */
	public abstract void apply(SpagoBICellWrapper cell, Object oldValue,
			Object newValue, SpagoBICellSetWrapper targetCellSet);
	
	public abstract void persist(SpagoBICellWrapper cell, Object oldValue, Object newValue, Integer version);

}
