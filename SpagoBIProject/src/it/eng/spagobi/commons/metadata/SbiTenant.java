/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.metadata;



public class SbiTenant  extends SbiHibernateModel {

    // Fields    

	private Integer id;
	private String name;
	
    // Constructors

    /**
     * default constructor.
     */
    public SbiTenant() {
    }
    
    /**
     * constructor with id.
     * 
     * @param valueId the value id
     */
    public SbiTenant(Integer id) {
        this.id = id;
    }
   

    // Property accessors
    
    
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}