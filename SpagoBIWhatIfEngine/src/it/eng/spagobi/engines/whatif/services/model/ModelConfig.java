/**
 * 
 */
package it.eng.spagobi.engines.whatif.services.model;

import com.eyeq.pivot4j.ui.command.DrillDownCommand;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public class ModelConfig {

	private String drillType;

	public ModelConfig() {
		drillType = DrillDownCommand.MODE_POSITION;
	}

	public String getDrillType() {
		return drillType;
	}

	public void setDrillType(String drillType) {
		this.drillType = drillType;
	}
	
	
	
}
