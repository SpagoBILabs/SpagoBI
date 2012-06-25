/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.common.datastore;



/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public class Field implements IField {
	Object value = null;



    public Field(Object value) {
		super();
		this.value = value;
	}
    


	public Field() {
		super();
		// TODO Auto-generated constructor stub
	}



	public Object getValue() {
    	return value;
    }


	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return "" + getValue();
	}

}
