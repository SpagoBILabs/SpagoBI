 /* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
package it.eng.spagobi.engines.whatif.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.eyeq.pivot4j.ui.command.DrillDownCommand;

public class ModelConfig implements Serializable {

	private static final long serialVersionUID = 2687163910212567575L;
	public static final String WRITEBACK = "writeback";
	private String drillType;
	private Map<String, String> dimensionHierarchyMap;
	private Map<String, String> writeBackConf;

	public ModelConfig() {
		drillType = DrillDownCommand.MODE_POSITION;
		dimensionHierarchyMap = new HashMap<String, String>();
		writeBackConf = new HashMap<String, String>();
	}

	public String getDrillType() {
		return drillType;
	}

	public void setDrillType(String drillType) {
		this.drillType = drillType;
	}

	public Map<String, String> getDimensionHierarchyMap() {
		return dimensionHierarchyMap;
	}

	public void setDimensionHierarchyMap(Map<String, String> dimensionHierarchyMap) {
		this.dimensionHierarchyMap = dimensionHierarchyMap;
	}
	
	public void setDimensionHierarchy(String dimensionUniqueName, String hierarchyUniqueName) {
		this.dimensionHierarchyMap.put(dimensionUniqueName, hierarchyUniqueName);
	}

	public Map<String, String> getWriteBackConf() {
		return writeBackConf;
	}

	public void setWriteBackConf(Map<String, String> writeBackConf) {
		this.writeBackConf = writeBackConf;
	}
	

	public boolean isWriteBackEnabled() {
		return this.writeBackConf.containsKey(WRITEBACK);
	}
	
	
}
