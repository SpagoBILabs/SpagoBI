/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
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
