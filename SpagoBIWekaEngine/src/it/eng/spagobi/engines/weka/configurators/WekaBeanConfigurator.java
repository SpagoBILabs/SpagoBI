/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.weka.configurators;

/**
 * @author Gioia
 *
 */
public interface WekaBeanConfigurator {
	
	/**
	 * Sets the up.
	 * 
	 * @param bean the new up
	 * 
	 * @throws Exception the exception
	 */
	public void setup(Object bean) throws Exception;
}
