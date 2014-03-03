 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * 
 * interface that provides services to renderer of the model...
 * 
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
package it.eng.spagobi.engines.whatif.services.serializer;

import com.eyeq.pivot4j.PivotModel;

public interface IPivotSerializer {
	/**
	 * Serialize the model
	 * @param model the PivotModel to serialize
	 * @return teh serialized model
	 */
	public String renderModel(PivotModel model);
}
