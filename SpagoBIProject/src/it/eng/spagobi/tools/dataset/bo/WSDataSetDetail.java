/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.bo;


/**
 * @author Chiara Chiarelli
 */
public class WSDataSetDetail extends GuiDataSetDetail{
	
    private String address=null;
    private String operation=null;
    
    /**
     * Gets the adress.
     * 
     * @return the adress
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Sets the adress.
     * 
     * @param adress the new adress
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
	/**
	 * Gets the operation.
	 * 
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	
	/**
	 * Sets the operation.
	 * 
	 * @param operation the new operation
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
    
}
