/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.utilities.objects;

import java.io.Serializable;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class Couple implements Serializable {

	private static final long serialVersionUID = -7561892082469672604L;
	private Object first;
	private Object second;
	
	public Couple(Object first, Object second){
		this.first = first;
		this.second = second;
	}

	public Object getFirst() {
		return first;
	}

	public void setFirst(Object first) {
		this.first = first;
	}

	public Object getSecond() {
		return second;
	}

	public void setSecond(Object second) {
		this.second = second;
	}
	

	
	
}
