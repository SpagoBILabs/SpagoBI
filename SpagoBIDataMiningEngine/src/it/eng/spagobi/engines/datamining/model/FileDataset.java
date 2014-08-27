/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.datamining.model;

import java.io.Serializable;

/**
 * @author Monica Franceschini
 */
public class FileDataset implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String readType;
	private String name;
	private String options;
	private String spagobiLabel;
	private String type;

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getSpagobiLabel() {
		return spagobiLabel;
	}

	public void setSpagobiLabel(String spagobiLabel) {
		this.spagobiLabel = spagobiLabel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReadType() {
		return readType;
	}

	public void setReadType(String readType) {
		this.readType = readType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
