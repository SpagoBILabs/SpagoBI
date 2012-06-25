/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.map.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;

// TODO: Auto-generated Javadoc
/**
 * The Class SVGMapMerger.
 * 
 * @author Andrea Gioia
 */
public class SVGMapMerger {
	
	/**
	 * Marge map.
	 * 
	 * @param srcMap the src map
	 * @param dstMap the dst map
	 * @param srcId the src id
	 * @param dstId the dst id
	 */
	public static void margeMap(SVGDocument srcMap, SVGDocument dstMap, String srcId, String dstId) {
		SVGElement srcMapRoot;
		Element srcElement;
		Element dstElement;
		
		srcMapRoot = srcMap.getRootElement();
		srcElement = ( srcId == null? srcMapRoot: srcMap.getElementById(srcId) );
		
		dstElement = dstMap.getElementById(dstId);
			    
		NodeList nodeList = srcElement.getChildNodes();	    
	    for(int i = 0; i < nodeList.getLength(); i++){
	    	Node node = (Node)nodeList.item(i);
	    	Node importedNode = dstMap.importNode(node, true);
	    	dstElement.appendChild(importedNode);
	    }
	}
}
