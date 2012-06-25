/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.weka.configurators;

import weka.gui.beans.Filter;

/**
 * @author Gioia
 *
 */
public class WekaBeanConfiguratorFactory {
	
	private static WekaBeanConfigurator filterConfigurator = new FilterConfigurator();
	
	/**
	 * Sets the up.
	 * 
	 * @param bean the new up
	 */
	public static void setup(Object bean) {
		try{
			if(bean.getClass().getName().equalsIgnoreCase(Filter.class.getName()))
				filterConfigurator.setup(bean);	
		} catch(Exception e) {
			e.printStackTrace();
		}
				
	}
}
