/*
*
* @file RuleInfoBuilder.java
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
* @version $Id: RuleInfoBuilder.java,v 1.9 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.builders;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLACubeInfo;
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAElementInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.RuleInfo;
import com.tensegrity.palojava.impl.RuleImpl;

public class RuleInfoBuilder {
	private XMLAClient       xmlaClient;
	private String           connectionName;
	private final HashMap    functionLists;
	private final HashMap    functionNamesLists;
	private final HashMap    ruleInfoMap;
	
    RuleInfoBuilder() {
    	functionLists = new HashMap();
    	functionNamesLists = new HashMap();
    	ruleInfoMap = new HashMap();
    }
    
    public RuleInfo [] getRules(XMLAConnection con, XMLAClient xmlaClient, XMLACubeInfo cube) {
		this.xmlaClient = xmlaClient;
		if (!ruleInfoMap.containsKey(cube.getId())) {			 		
			ArrayList ruleInfos = new ArrayList();
			DimensionInfo [] dimInfo = con.getDimensions(cube);
//			String [] dimIds = cube.getDimensions();			
//			List dimensions = new ArrayList();
//			for (int i = 0, n = dimIds.length; i < n; i++) {
//				for (int j = 0, m = dimInfo.length; j < m; j++) {
//					if (dimInfo[j].getId().equals(dimIds[i])) {
//						dimensions.add(dimInfo[j]);
//						break;
//					}
//				}
//			}
			for (int i = 0, n = dimInfo.length; i < n; i++) {
				XMLAElementInfo [] elInfo = 
					con.getCubeElements(cube, (XMLADimensionInfo) dimInfo[i]);
				for (int j = 0, m = elInfo.length; j < m; j++) {
					if (elInfo[j].isCalculated()) {
						ruleInfos.add(elInfo[j].getRule());
					}
				}
			}
			ruleInfoMap.put(cube.getId(), ruleInfos);
		}
		RuleImpl [] rules = (RuleImpl []) ((ArrayList) ruleInfoMap.get(
				cube.getId())).toArray(new RuleImpl[0]); 
		return rules;
    }

    public String getRule(XMLACubeInfo cube, ElementInfo [] coordinate) {
    	String id = "";
		for (int j = 0, m = coordinate.length; j < m; j++) {
			XMLAElementInfo info = (XMLAElementInfo) coordinate[j];
			if (info.isCalculated()) {
				id = info.getRule().getId();
				break;
			}
		}
		return id;
    }
        
    public String getFunctions(XMLAClient client) {
		xmlaClient = client;
		connectionName = client.getConnections()[0].getName();
		if (!functionLists.containsKey(client)) {
			requestFunctions();
		}
		
		return (String) functionLists.get(xmlaClient);
	}	
	
	public String getFunctionNames(XMLAClient client) {
		xmlaClient = client;
		connectionName = client.getConnections()[0].getName();
		if (!functionNamesLists.containsKey(client)) {
			requestFunctions();
		}
		return (String) functionNamesLists.get(xmlaClient);		
	}
    	
	private void requestFunctions() {
		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        prop.setDataSourceInfo(connectionName);
    		
	        Document result = 
	        	xmlaClient.getFunctionList(rest, prop);
	        NodeList functionList = result.getElementsByTagName("row");	        
	        
    		storeFunctions(functionList);		    
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	private void storeFunctions(NodeList nl) {
		StringBuffer functionList = new StringBuffer("<functions>\n");
		StringBuffer functionNames = new StringBuffer();
		
		if (nl == null || nl.getLength() == 0) {
			functionList.append("</functions>\n");
			functionLists.put(xmlaClient, functionList.toString());
			functionNamesLists.put(xmlaClient, functionNames.toString());
			return;
		}
		
		functionList.append(Resource.getBaseFunctions());
		Set definedFunctions = new HashSet();
		for (int i = 0; i < nl.getLength(); i++) {
			NodeList nlRow = nl.item(i).getChildNodes();

			String functionName = null;
			int    functionOrigin = 1;
			String functionCaption = null;
			String functionDescription = null;
			String functionParameters = null;
			String functionReturnType = null;			
			for (int j = 0; j < nlRow.getLength(); j++) {
				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
					if (nlRow.item(j).getNodeName().equals("FUNCTION_NAME"))  {	
						functionName = XMLAClient.getTextFromDOMElement(nlRow.item(j));
					} else if (nlRow.item(j).getNodeName().equals("DESCRIPTION")) { 
						functionDescription = XMLAClient.getTextFromDOMElement(nlRow.item(j));
					} else if (nlRow.item(j).getNodeName().equals("ORIGIN")) {
						functionOrigin = Integer.parseInt(XMLAClient.getTextFromDOMElement(nlRow.item(j)));
					} else if (nlRow.item(j).getNodeName().equals("CAPTION")) {
						functionCaption = XMLAClient.getTextFromDOMElement(nlRow.item(j));
					} else if (nlRow.item(j).getNodeName().equals("PARAMETER_LIST")) {
						functionParameters = XMLAClient.getTextFromDOMElement(nlRow.item(j));
					} else if (nlRow.item(j).getNodeName().equals("RETURN_TYPE")) { 
						functionReturnType = XMLAClient.getTextFromDOMElement(nlRow.item(j));
					}

				}
			}
			if (definedFunctions.contains(functionName)) {
				continue;
			}
			definedFunctions.add(functionName);
			functionParameters = functionParameters.replaceAll("<", "");
			functionParameters = functionParameters.replaceAll(">", "");									
			functionList.append("  <function>\n");
			functionList.append("    <name>" + functionName + "</name>\n");							
			functionList.append("    <category>SQLServer</category>\n");
			functionList.append("    <short-description language=\"english\">\n");
			functionList.append("      " + functionCaption + "(" + functionParameters + ")\n");
			functionList.append("    </short-description>\n");
			functionList.append("    <short-description language=\"german\">\n");
			functionList.append("      " + functionCaption + "(" + functionParameters + ")\n");
			functionList.append("    </short-description>\n");
			functionList.append("    <long-description language=\"english\">\n");
			functionList.append("      " + functionDescription + "\n");
			functionList.append("    </long-description>\n");
			functionList.append("    <long-description language=\"german\">\n");
			functionList.append("      " + functionDescription + "\n");
			functionList.append("    </long-description>\n");
			functionList.append("    <minimal-arguments>" + "0" + "</minimal-arguments>\n");
			functionList.append("    <maximal-arguments>" + "0" + "</maximal-arguments>\n");
			functionList.append("  </function>\n");
			if (functionNames.length() != 0) {
				functionNames.append(", ");
			}
			functionNames.append(functionCaption);
		}
		functionList.append("</functions>\n");
		functionLists.put(xmlaClient, functionList.toString());
		functionNamesLists.put(xmlaClient, functionNames.toString());
	}
	
	private void parseParameters(String functionParameters) {
		// TODO
	}	
}