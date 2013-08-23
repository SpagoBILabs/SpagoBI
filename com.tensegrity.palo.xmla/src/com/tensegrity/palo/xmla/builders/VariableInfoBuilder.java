/*
*
* @file VariableInfoBuilder.java
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
* @version $Id: VariableInfoBuilder.java,v 1.12 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.builders;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLADatabaseInfo;
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAElementInfo;
import com.tensegrity.palo.xmla.XMLAHierarchyInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palo.xmla.XMLAVariableInfo;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.VariableInfo;
import com.tensegrity.palojava.loader.ElementLoader;

public class VariableInfoBuilder {
	private XMLAClient xmlaClient;
	private XMLADatabaseInfo xmlaDatabase;
	private String connectionName;
	
	public XMLAVariableInfo [] requestVariables(XMLAClient client, XMLADatabaseInfo database, String cubeName) {
		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        xmlaClient = client;
	        connectionName = client.getConnections()[0].getName();
	        xmlaDatabase = database;
	        
    	    prop.setDataSourceInfo(connectionName);
	        
    	    prop.setCatalog(xmlaDatabase.getId());
	        rest.setCatalog(xmlaDatabase.getId());
	        rest.setCubeName(cubeName);

	        Document resultvars = xmlaClient.getSAPVariableList(rest, prop);
	        NodeList nlvars  = resultvars.getElementsByTagName("row");
	        
	        return storeVariables(nlvars);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return new XMLAVariableInfo[0];
	}
	
	private XMLAVariableInfo [] storeVariables(NodeList nlvars) {
		if (nlvars == null || nlvars.getLength() == 0) {
			return new XMLAVariableInfo[0];
		}
		ArrayList <XMLAVariableInfo> varInfos = 
			new ArrayList <XMLAVariableInfo> ();
		for (int i = 0, n = nlvars.getLength(); i < n; i++) {
			XMLAVariableInfo varInf = new XMLAVariableInfo();
			NodeList nlRow = nlvars.item(i).getChildNodes();
			for (int j = 0; j < nlRow.getLength(); j++) {
				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
					String nodeName = nlRow.item(j).getNodeName();
					String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
					if (nodeName.equals("VARIABLE_NAME")) {						 
						varInf.setId(text);
					} else if (nodeName.equals("VARIABLE_CAPTION")) {
						varInf.setName(text);
					} else if (nodeName.equals("VARIABLE_UID")) {
						varInf.setUId(text);
					} else if (nodeName.equals("VARIABLE_ORDINAL")) {
						varInf.setOrdinal(text);						
					} else if (nodeName.equals("VARIABLE_TYPE")) {
						try {
							varInf.setType(Integer.parseInt(text));
						} catch (NumberFormatException e) {
							varInf.setType(
									VariableInfo.VAR_TYPE_UNKNOWN);
						}
					} else if (nodeName.equals("DATA_TYPE")) {
						varInf.setDataType(text);																		
					} else if (nodeName.equals("CHARACTER_MAXIMUM_LENGTH")) {
						varInf.setCharacterMaximumLength(text);																								
					} else if (nodeName.equals("VARIABLE_PROCESSING_TYPE")) {
						try {
							varInf.setVariableProcessingType(Integer.parseInt(text));
						} catch (NumberFormatException e) {
							varInf.setVariableProcessingType(
									VariableInfo.VAR_PROC_TYPE_UNKNOWN);
						}
					} else if (nodeName.equals("VARIABLE_SELECTION_TYPE")) {
						try {
							varInf.setSelectionType(Integer.parseInt(text));
						} catch (NumberFormatException e) {
							varInf.setSelectionType(
									VariableInfo.VAR_SELECTION_TYPE_UNKNOWN);
						}

					} else if (nodeName.equals("VARIABLE_ENTRY_TYPE")) {
						try {
							varInf.setInputType(Integer.parseInt(text));
						} catch (NumberFormatException e) {
							varInf.setInputType(VariableInfo.VAR_INPUT_TYPE_UNKNOWN);
						}
					} else if (nodeName.equals("REFERENCE_DIMENSION")) {
						varInf.setReferenceDimension(text);																																																						
					} else if (nodeName.equals("REFERENCE_HIERARCHY")) {
						varInf.setReferenceHierarchy(text);																																																												
					} else if (nodeName.equals("DEFAULT_LOW")) {
						varInf.setDefaultLow(text);
					} else if (nodeName.equals("DEFAULT_LOW_CAP")) {
						varInf.setDefaultLowCap(text);
					} else if (nodeName.equals("DEFAULT_HIGH")) {
						varInf.setDefaultHigh(text);
					} else if (nodeName.equals("DEFAULT_HIGH_CAP")) {
						varInf.setDefaultHighCap(text);
					} else if (nodeName.equals("DESCRIPTION")) {
						varInf.setDescription(text);
					}
				}
			}
			//requestVarElements(varInf);
			varInf.setHideConsolidations(
					varInf.getReferenceDimension().equals(varInf.getReferenceHierarchy()));
			varInfos.add(varInf);
		}
		return varInfos.toArray(new XMLAVariableInfo[0]);
	}
	
	public void requestVarElements(XMLAVariableInfo varInf, XMLAConnection con, XMLADatabaseInfo database, XMLAClient client) {
    	try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();
   	        this.xmlaDatabase = database;
    	    prop.setDataSourceInfo(connectionName);
   	        prop.setCatalog(xmlaDatabase.getId());    	    	
   	    	rest.setCatalog(xmlaDatabase.getId());    	    
   	    	rest.setHierarchyUniqueName(varInf.getReferenceHierarchy());
   	    	XMLAClient.printStackTrace(
   	    			Thread.currentThread().getStackTrace(), System.err);
   	    	Document result = xmlaClient.getMemberList(rest, prop);
	        NodeList nl  = result.getElementsByTagName("row");
	        xmlaClient = client;
	        
	        buildStructure(nl, varInf, con);		    	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void buildStructure(NodeList nl, XMLAVariableInfo varInf, XMLAConnection con) {	
		if (nl == null || nl.getLength() == 0) {
			varInf.setElementDimension(
					new XMLADimensionInfo(xmlaClient, "VariableDimension", "varDimensionID", 
							xmlaDatabase, null, con));
			return;
		}
		
		XMLAElementInfo   currentMember;
		XMLADimensionInfo currentDimension = 
			new XMLADimensionInfo(xmlaClient, "VariableDimension", "varDimensionID", xmlaDatabase, null, con);
		XMLAHierarchyInfo currentHierarchy =
			new XMLAHierarchyInfo(currentDimension, "VariableDimension", "varDimensionID");
		String            currentMemberName;
		String            currentMemberUniqueName;
		String            currentMemberInternalName;
		String            guid;
		
		currentDimension.clearMembersInternal();
		ElementLoader loader = con.getElementLoader(currentDimension);
		for (int i = 0; i < nl.getLength(); i++) {
			NodeList nlRow = nl.item(i).getChildNodes();
			
			currentMember             = null;
			currentMemberName         = "";
			currentMemberUniqueName   = "";
			currentMemberInternalName = "";
			for (int j = 0; j < nlRow.getLength(); j++) {
				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
					Node currentItem = nlRow.item(j);
					String nodeName = currentItem.getNodeName();
					if (nodeName.equals("MEMBER_NAME")) {
						currentMemberName = XMLAClient.getTextFromDOMElement(currentItem);
						currentMemberInternalName = currentMemberName;
					} else if (nodeName.equals("MEMBER_UNIQUE_NAME")) {
						currentMemberUniqueName = XMLAClient.getTextFromDOMElement(currentItem);
						currentMemberInternalName = currentMemberUniqueName;
						currentMember = currentDimension.getMemberInternal(currentMemberUniqueName);
						if (currentMember == null) {
							currentMember = new XMLAElementInfo(currentHierarchy, currentDimension, xmlaClient, con);
							if (currentMemberName.length() != 0) {
								currentMember.setName(currentMemberName);
							} else {
								currentMember.setName(currentMemberUniqueName);
							}
							currentMember.setUniqueName(currentMemberUniqueName);
							currentMember.setId(currentMemberUniqueName); //"" + currentMemberUniqueName.hashCode());
							currentMember.setPosition(currentDimension.getMemberCountInternal());
							currentDimension.addMemberInternal(currentMember);							
						} 							
					} else if (nodeName.equals("MEMBER_TYPE")) {
						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
						try {
							int typeNumber = Integer.parseInt(text);
							if (typeNumber == 1 || typeNumber == 3) {
								currentMember.setType(ElementInfo.TYPE_NUMERIC);
							} else {
								currentMember.setType(ElementInfo.TYPE_STRING);
							}
						} catch (NumberFormatException e) {
							currentMember.setType(ElementInfo.TYPE_STRING);
						}
					} else if (nodeName.equals("PARENT_UNIQUE_NAME")) {
						String text = XMLAClient.getTextFromDOMElement(currentItem);
						XMLAElementInfo parent = currentDimension.getMemberInternal(text);
						if (parent == null) {
							parent = new XMLAElementInfo(currentHierarchy, currentDimension, xmlaClient, con);
							parent.setUniqueName(text);
							parent.setId(text); //"" + text.hashCode());
						}
						parent.addChildInternal(currentMember);
						parent.addChild(currentMember);
						currentDimension.addMemberInternal(parent);
						currentMember.setParentInternal(new XMLAElementInfo []{parent});
						currentMember.setParents(new String []{parent.getId()});
					} else if (nlRow.item(j).getNodeName().equals("MEMBER_CAPTION")) {
						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
						currentMember.setName(text);
					} 
				}
			}
			if (currentDimension != null && currentMember != null) {
				if (currentMemberUniqueName.length() != 0) {
					currentMember.setUniqueName(currentMemberUniqueName);
					currentMember.setId(currentMemberUniqueName);
				}
				currentDimension.addMemberInternal(currentMember);
			}			
		}	
		if (varInf.getHideConsolidations()) {
			XMLAElementInfo [] members = currentDimension.getMembersInternal();		
			currentDimension.clearMembersInternal();			
			for (XMLAElementInfo member: members) {
				if (member.getChildrenInternal().length == 0) {
					currentDimension.addMemberInternal(member);
					member.setParents(null);
					member.setParentCount(0);
				}
				member.clearChildren();
			}
		} else {
			XMLAElementInfo [] members = currentDimension.getMembersInternal();
			for (XMLAElementInfo member: members) {
				member.clearChildren();
				member.setChildren(member.getChildrenInternal());
				loader.loaded(member);
			}
		}
		
		varInf.setElementDimension(currentDimension);
	}	
}
