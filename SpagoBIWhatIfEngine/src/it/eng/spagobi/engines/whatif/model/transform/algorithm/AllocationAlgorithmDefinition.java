/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 * 
 * @class Definition of the allocation algorithm. It contains the useful properties to create an allocation algorithm
 */
package it.eng.spagobi.engines.whatif.model.transform.algorithm;

public class AllocationAlgorithmDefinition {
	private final String name;
	private final String className;
	private final boolean inMemory;
	private final boolean persistent;
	private boolean defaultAlgorithm;

	public AllocationAlgorithmDefinition(String name, String className, boolean inMemory, boolean persistent) {
		super();
		this.name = name;
		this.className = className;
		this.inMemory = inMemory;
		this.persistent = persistent;
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	public boolean isInMemory() {
		return inMemory;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public boolean isDefaultAlgorithm() {
		return defaultAlgorithm;
	}

	public void setDefaultAlgorithm(boolean defaultAlgorithm) {
		this.defaultAlgorithm = defaultAlgorithm;
	}

}
