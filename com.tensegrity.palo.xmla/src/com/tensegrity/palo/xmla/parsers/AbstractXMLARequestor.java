/*
*
* @file AbstractXMLARequestor.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: AbstractXMLARequestor.java,v 1.3 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.parsers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;

public abstract class AbstractXMLARequestor {
	private final LinkedHashSet <String> activatedItems = 
		new LinkedHashSet <String>();

	public void deactivateItem(String itemName) {
		activatedItems.remove(itemName);
	}
	
	public void activateItem(String itemName) {
		activatedItems.add(itemName);
	}
	
	public boolean isItemActive(String itemName) {
		return activatedItems.contains(itemName);
	}
	
	protected void parseXMLANodeList(NodeList parentNodeList, 
			String connectionName, XMLAClient xmlaClient) {
		LinkedHashMap <String, String> resultMap = 
			new LinkedHashMap<String, String>();				
		for (int i = 0, n = parentNodeList.getLength(); i < n; i++) {
			NodeList nlRow = parentNodeList.item(i).getChildNodes();
			for (int j = 0; j < nlRow.getLength(); j++) {
				Node itemNode = nlRow.item(j);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					for (String item: activatedItems) {
						if (itemNode.getNodeName().equals(item))  {	
							String text = XMLAClient.getTextFromDOMElement(itemNode);
							if (text == null || text.length() == 0) {
								continue;
							}
							resultMap.put(item, 
								XMLAClient.getTextFromDOMElement(itemNode));
						}
					} 
				}
			}
			parseResult(resultMap, connectionName, xmlaClient);
			resultMap.clear();
		}
	}
	
	protected abstract void parseResult(HashMap <String, String> result,
			String connectionName, XMLAClient xmlaClient);
}
