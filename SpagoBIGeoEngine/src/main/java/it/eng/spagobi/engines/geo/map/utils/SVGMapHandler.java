/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.map.utils;

import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;

// TODO: Auto-generated Javadoc
/**
 * The Class SVGMapHandler.
 * 
 * @author Andrea Gioia
 */
public class SVGMapHandler {
	
	/**
	 * Adds the attributes.
	 * 
	 * @param e the e
	 * @param attributes the attributes
	 */
	public static void addAttributes(Element e, Map attributes) {
		Iterator it = attributes.keySet().iterator();
		while(it.hasNext()) {
			String attributeName = (String)it.next();
			String attributeValue = (String)attributes.get(attributeName);
			e.setAttribute("attrib:" + attributeName, attributeValue);
		}
	}
}
