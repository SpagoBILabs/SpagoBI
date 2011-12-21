/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.behaviouralmodel.analyticaldriver.metadata;

import it.eng.spagobi.commons.metadata.SbiHibernateModel;

public class SbiObjParview extends SbiHibernateModel {


	private SbiObjParviewId id;
	private Integer prog;
	private String viewLabel;
	
	
	// Constructors

	/**
	 * default constructor.
	 */
	public SbiObjParview() {
	}

	/**
	 * constructor with id.
	 * 
	 * @param id the id
	 */
	public SbiObjParview(SbiObjParviewId id) {
		this.id = id;
	}
	
	public SbiObjParviewId getId() {
		return id;
	}
	public void setId(SbiObjParviewId id) {
		this.id = id;
	}
	public Integer getProg() {
		return prog;
	}
	public void setProg(Integer prog) {
		this.prog = prog;
	}
	public String getViewLabel() {
		return viewLabel;
	}
	public void setViewLabel(String viewLabel) {
		this.viewLabel = viewLabel;
	}

	


}
