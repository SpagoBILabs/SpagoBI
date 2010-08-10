/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.tools.dataset.metadata;


/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class SbiWSDataSet extends SbiDataSetConfig{
    private String adress=null;
    private String executorClass=null;
    private String operation=null;
    
    /**
     * Gets the adress.
     * 
     * @return the adress
     */
    public String getAdress() {
        return adress;
    }
    
    /**
     * Sets the adress.
     * 
     * @param adress the new adress
     */
    public void setAdress(String adress) {
        this.adress = adress;
    }
    
    /**
     * Gets the executor class.
     * 
     * @return the executor class
     */
    public String getExecutorClass() {
        return executorClass;
    }
    
    /**
     * Sets the executor class.
     * 
     * @param executorClass the new executor class
     */
    public void setExecutorClass(String executorClass) {
        this.executorClass = executorClass;
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
