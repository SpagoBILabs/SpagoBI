/*
*
* @file ElementInfoBuilder.java
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
* @version $Id: ElementInfoBuilder.java,v 1.37 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.builders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLACubeInfo;
import com.tensegrity.palo.xmla.XMLADatabaseInfo;
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAElementInfo;
import com.tensegrity.palo.xmla.XMLAExecuteProperties;
import com.tensegrity.palo.xmla.XMLAHierarchyInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.impl.RuleImpl;

public class ElementInfoBuilder {
    private XMLAClient        xmlaClient;
    private XMLADimensionInfo dimension;
    private String            cubeId;
    private String            connectionName;
	private long 			  totalTime;
	private int               maxDepth;
	private int               maxLevel;
	private XMLAConnection    xmlaConnection;
	private XMLAHierarchyInfo hierarchy;
	
    ElementInfoBuilder() {
    	totalTime = 0;
    }
    
    public void getElementsTest(XMLAConnection con, XMLAClient client, String databaseId, 
    		String dimensionId, String hierarchyId) {
    	XMLAClient.setVerbose(true);
    	XMLAExecuteProperties prop = new XMLAExecuteProperties();
		String conName = client.getConnections()[0].getName();
		prop.setDataSourceInfo(conName);
		prop.setCatalog(databaseId);
    	
    	try {
			StringBuffer sb = new StringBuffer("SELECT ");
			sb.append(hierarchyId);
			sb.append(".Levels(0) ON 0 FROM $" + dimensionId);
			System.out.println("Query == " + sb);
    		Document result = client.execute(sb.toString(), prop);
	        if (result == null) {
	        	System.out.println("Whoops: Document == null");
	        	return;
	        }
    		NodeList nl  = result.getElementsByTagName("Tuple");
	        if (nl == null || nl.getLength() == 0) {
	        	System.out.println("Null result...");
	        	return;
	        }
			for (int i = 0; i < nl.getLength(); i++) {
				try {
					String axisName = nl.item(i).getParentNode().getParentNode().
						getAttributes().getNamedItem("name").getNodeValue();
					if (!axisName.equalsIgnoreCase("axis0")) {
						continue;
					}
				} catch (Exception e) {
					continue;
				}
				NodeList nlRow = nl.item(i).getChildNodes().item(0).getChildNodes();				
				for (int j = 0; j < nlRow.getLength(); j++) {
					if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Node currentItem = nlRow.item(j);
						String nodeName = currentItem.getNodeName();
						if (nodeName.equals("UName")) {
							System.out.println(XMLAClient.getTextFromDOMElement(currentItem));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		XMLAClient.setVerbose(false);
    }
    
	public XMLAElementInfo [] getElements(XMLAConnection con, XMLAClient client, String cubeId, XMLADimensionInfo dimension) {
		xmlaClient     = client;
		connectionName = client.getConnections()[0].getName();
		this.dimension = dimension;	
		this.hierarchy = null;
		this.cubeId    = cubeId;
		this.xmlaConnection = con;		
		return requestElements(null);
	}
	
	public XMLAElementInfo [] getElements(XMLAConnection con, XMLAClient client, String cubeId, XMLAHierarchyInfo hierarchy) {
		xmlaClient     = client;
		connectionName = client.getConnections()[0].getName();
		this.dimension = (XMLADimensionInfo) hierarchy.getDimension();
		this.hierarchy = hierarchy;
		this.cubeId    = cubeId;
		this.xmlaConnection = con;		
		return requestElements(null);
	}

	public XMLAElementInfo [] getElements(XMLAConnection con, XMLAClient client, String cubeId, XMLADimensionInfo dimension, int level) {
		xmlaClient     = client;
		connectionName = client.getConnections()[0].getName();
		this.dimension = dimension;		
		this.cubeId    = cubeId;
		this.xmlaConnection = con;
		
		return requestElementsAtLevel(null, level);
	}
	
	public XMLAElementInfo [] getElements(XMLAConnection con, XMLAClient client, String cubeId, XMLAHierarchyInfo hierarchy, int level) {
		xmlaClient     = client;
		connectionName = client.getConnections()[0].getName();
		this.dimension = (XMLADimensionInfo) hierarchy.getDimension();
		this.hierarchy = hierarchy;
		this.cubeId    = cubeId;
		this.xmlaConnection = con;
		
		return requestElementsAtLevel(null, level);
	}

	public XMLAElementInfo [] getChildren(XMLAConnection con, XMLAClient client, String cubeId, XMLAElementInfo parent) {
		xmlaClient     = client;
		connectionName = client.getConnections()[0].getName();
		this.dimension = (XMLADimensionInfo) parent.getDimension();	
		this.hierarchy = (XMLAHierarchyInfo) parent.getHierarchy();
		this.cubeId    = cubeId;
		this.xmlaConnection = con;
		return requestElements(parent);
	}

	public XMLAElementInfo getElementInfo(XMLAConnection con, XMLAClient client, XMLADimensionInfo dimension, String id) {
		xmlaClient     = client;
		connectionName = client.getConnections()[0].getName();
		this.dimension = dimension;		
		this.xmlaConnection = con;
		
		return requestElement(id);
	}
	
	public XMLAElementInfo getElementInfo(XMLAConnection con, XMLAClient client, XMLAHierarchyInfo hierarchy, String id) {
		xmlaClient     = client;
		connectionName = client.getConnections()[0].getName();
		this.dimension = (XMLADimensionInfo) hierarchy.getDimension();
		this.hierarchy = hierarchy;
		this.xmlaConnection = con;
		
		return requestElement(id);
	}
			    
	private XMLAElementInfo requestElement(String id) {
    	try {
    	    id = XMLADimensionInfo.transformId(id);
    	    id = id.replaceAll("&", "&amp;");
    		XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();
   	        prop.setDataSourceInfo(connectionName);
   	        prop.setCatalog(dimension.getDatabase().getId());
   	        rest.setCatalog(dimension.getDatabase().getId());
   	    	rest.setMemberUniqueName(id);
   	    	rest.setCubeName(dimension.getCubeId());
   	    	Document result = xmlaClient.getMemberList(rest, prop);
	        NodeList nl  = result.getElementsByTagName("row");
			if (nl == null || nl.getLength() == 0) {
				return null;
			}
			
			NodeList nlRow = nl.item(0).getChildNodes();
			XMLAElementInfo currentMember = null;
			String currentMemberOrdinal = "<not initialized>";
			for (int j = 0; j < nlRow.getLength(); j++) {
				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
					Node currentItem = nlRow.item(j);
					String nodeName = currentItem.getNodeName();
					if (nodeName.equals("MEMBER_UNIQUE_NAME")) {
						String text = XMLAClient.getTextFromDOMElement(currentItem);					
						currentMember = ((XMLADimensionInfo) dimension).getMemberInternal(text);
						if (currentMember == null) {
							currentMember = new XMLAElementInfo(hierarchy, dimension, xmlaClient, xmlaConnection);
							currentMember.setName(text);
							currentMember.setUniqueName(text);
							currentMember.setId(text); //"" + currentMemberUniqueName.hashCode());
							((XMLADimensionInfo) dimension).addMemberInternal(currentMember);							
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
						XMLAElementInfo parent = ((XMLADimensionInfo) dimension).getMemberInternal(text);
						if (parent == null) {
							parent = new XMLAElementInfo(hierarchy, dimension, xmlaClient, xmlaConnection);
							parent.setUniqueName(text);
							parent.setId(text); //"" + text.hashCode());
						}
						parent.addChildInternal(currentMember);
						parent.addChild(currentMember);
						((XMLADimensionInfo) dimension).addMemberInternal(parent);
						currentMember.setParentInternal(new XMLAElementInfo []{parent});
						currentMember.setParents(new String []{parent.getId()});
					} else if (nlRow.item(j).getNodeName().equals("MEMBER_CAPTION")) {
						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
						currentMember.setName(text);
					} else if (nlRow.item(j).getNodeName().equals("MEMBER_ORDINAL")) {
						currentMemberOrdinal = XMLAClient.getTextFromDOMElement(nlRow.item(j));						
					} else if (nlRow.item(j).getNodeName().equals("CHILDREN_CARDINALITY")) {
						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
						int estimatedChildCount = Integer.parseInt(text);
						currentMember.setHasChildren( estimatedChildCount > 0);
						currentMember.setEstimatedChildCount(estimatedChildCount);
					}
				}
			}
			try {
				currentMember.setPosition(Integer.parseInt(currentMemberOrdinal));
			} catch (NumberFormatException e) {
				currentMember.setPosition(Integer.MAX_VALUE);
			}
			return currentMember;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	private XMLAElementInfo [] requestElements(XMLAElementInfo parent) {
		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();
   	        prop.setDataSourceInfo(connectionName);
   	        prop.setCatalog(dimension.getDatabase().getId());    	    	
   	    	rest.setCatalog(dimension.getDatabase().getId());
    	    if (cubeId != null) {
    	    	rest.setCubeName(cubeId);
    	    }
   	    	if (parent == null) {
   	    		if (hierarchy == null) {
   	    			rest.setHierarchyUniqueName(dimension.getHierarchyUniqueName());
   	    		} else {
   	    			rest.setHierarchyUniqueName(XMLADimensionInfo.transformId(XMLADimensionInfo.getDimIdFromId(hierarchy.getId())));
   	    		}
   	    	} else {
   	    		rest.setTreeOp(1);
   	    		if (hierarchy == null) {
   	    			rest.setHierarchyUniqueName(dimension.getHierarchyUniqueName());
   	    		} else {
   	    			rest.setHierarchyUniqueName(XMLADimensionInfo.transformId(XMLADimensionInfo.getDimIdFromId(hierarchy.getId())));
   	    		}
   	    		String modified = parent.getUniqueName().replaceAll("&", "&amp;");
   	    		rest.setMemberUniqueName(modified);
   	    	}
   	    	Document result = xmlaClient.getMemberList(rest, prop);
   	    	NodeList nl  = result.getElementsByTagName("row");
	        
	        if (nl == null || nl.getLength() == 0) {
				if (parent == null) {
					dimension.setElementCount(0);
				}
	        }
	        return buildStructure(nl, (XMLADatabaseInfo) dimension.getDatabase(),
	        				   cubeId,
	        				   dimension, hierarchy);		    	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new XMLAElementInfo[0];
	}
	   	
//	private XMLAElementInfo [] requestCalculatedMembers() {
//    	try {
//    	    XMLARestrictions rest = new XMLARestrictions();
//    	    XMLAProperties   prop = new XMLAProperties();
//   	        prop.setDataSourceInfo(connectionName);
//   	        prop.setCatalog(dimension.getDatabase().getId());    	    	
//   	    	rest.setCatalog(dimension.getDatabase().getId());
//   	    	rest.setMemberType(4);   	    	
//    	    if (cubeId != null) {
//    	    	rest.setCubeName(cubeId);
//    	    }
//   	    	if (hierarchy == null) {
//   	    		rest.setHierarchyUniqueName(dimension.getHierarchyUniqueName());
//   	    	} else {
//   	    		rest.setHierarchyUniqueName(XMLADimensionInfo.transformId(
//   	    				XMLADimensionInfo.getDimIdFromId(hierarchy.getId())));
//   	    	}   	    	   	    	   	    	
//   	    	Document result = xmlaClient.getMemberList(rest, prop);
//	        NodeList nl  = result.getElementsByTagName("row");
//	        
//	        XMLAElementInfo[] infos = buildStructure(nl, (XMLADatabaseInfo) dimension.getDatabase(),
//	        				   cubeId,
//	        				   dimension, hierarchy);
//	        if (infos != null) {
//	        	System.out.println("CalcMem == " + infos.length);
//	        } else {
//	        	System.out.println("CalcMem == <null>");
//	        }
//	        return infos;	         
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return new XMLAElementInfo[0];		
//	}
	
	private XMLAElementInfo [] requestElementsAtLevel(XMLAElementInfo parent, int level) {
    	try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();
   	        prop.setDataSourceInfo(connectionName);
   	        prop.setCatalog(dimension.getDatabase().getId());    	    	
   	    	rest.setCatalog(dimension.getDatabase().getId());
//   	    	ArrayList <XMLAElementInfo> calcMembers = new ArrayList<XMLAElementInfo>();
//   	    	if (level == 0) {
//   	    		for (XMLAElementInfo i: requestCalculatedMembers()) {
//   	    			calcMembers.add(i);	
//   	    		}
//   	    	}
   	    	rest.setLevelNumber("" + level);   	    	
    	    if (cubeId != null) {
    	    	rest.setCubeName(cubeId);
    	    }
   	    	if (hierarchy == null) {
   	    		rest.setHierarchyUniqueName(dimension.getHierarchyUniqueName());
   	    	} else {
   	    		rest.setHierarchyUniqueName(XMLADimensionInfo.transformId(
   	    				XMLADimensionInfo.getDimIdFromId(hierarchy.getId())));
   	    	}   	    	   	    	   	    	
   	    	Document result = xmlaClient.getMemberList(rest, prop);
	        NodeList nl  = result.getElementsByTagName("row");
	        
	        if (nl == null || nl.getLength() == 0) {
		        if (nl == null || nl.getLength() == 0) {
					if (parent == null) {
						dimension.setElementCount(0);
					}
		        }
	        }
	        XMLAElementInfo[] infos = buildStructure(nl, (XMLADatabaseInfo) dimension.getDatabase(),
	        				   cubeId,
	        				   dimension, hierarchy);
//	        if (!calcMembers.isEmpty()) {
//	        	XMLAElementInfo [] ret = new XMLAElementInfo[infos.length + calcMembers.size()];
//	        	System.arraycopy(infos, 0, ret, 0, infos.length);
//	        	int offset = infos.length;
//	        	for (XMLAElementInfo i: calcMembers) {
//	        		ret[offset++] = i;
//	        	}
//	        	return ret;
//	        }
	        return infos;	         
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new XMLAElementInfo[0];
	}
	
	private XMLAElementInfo [] buildStructure(NodeList nl, 
			XMLADatabaseInfo currentDatabase,
			String cubeId,
			XMLADimensionInfo currentDimension,
			XMLAHierarchyInfo hierarchy) {
	
		if (nl == null || nl.getLength() == 0) {
			return new XMLAElementInfo[0];
		}
		XMLAElementInfo   currentMember;
		String            currentMemberName;
		String            currentMemberUniqueName;
		ArrayList <XMLAElementInfo> response = new ArrayList<XMLAElementInfo>();
		
		for (int i = 0; i < nl.getLength(); i++) {
			NodeList nlRow = nl.item(i).getChildNodes();
			
			currentMember             = null;
			currentMemberName         = "";
			currentMemberUniqueName   = "";
			for (int j = 0; j < nlRow.getLength(); j++) {
				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
					Node currentItem = nlRow.item(j);
					String nodeName = currentItem.getNodeName();
					if (nodeName.equals("MEMBER_NAME")) {
						currentMemberName = XMLAClient.getTextFromDOMElement(currentItem);
					} else if (nodeName.equals("MEMBER_UNIQUE_NAME")) {
						currentMemberUniqueName = XMLAClient.getTextFromDOMElement(currentItem);
						currentMember = currentDimension.getMemberInternal(currentMemberUniqueName);
						if (currentMember == null) {
							currentMember = new XMLAElementInfo(hierarchy, dimension, xmlaClient, xmlaConnection);
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
						if (currentMemberUniqueName.indexOf("Avg Salary") != -1) {
							System.err.println("Member Type: " + text + " for " + currentMemberUniqueName + ", " + currentMemberName);
						}
//						if ("4".equals(text)) {
//							System.err.println("Formula: ";
//						}
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
							parent = new XMLAElementInfo(hierarchy, dimension, xmlaClient, xmlaConnection);
							parent.setUniqueName(text);
							parent.setId(text);
						}
						parent.addChildInternal(currentMember);
						parent.addChild(currentMember);
						currentDimension.addMemberInternal(parent);
						currentMember.setParentInternal(new XMLAElementInfo []{parent});
						currentMember.setParents(new String []{parent.getId()});						
					} else if (nlRow.item(j).getNodeName().equals("MEMBER_CAPTION")) {
						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
						currentMember.setName(text);
					} else if (nlRow.item(j).getNodeName().equals("EXPRESSION")) {
						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
						if (text.trim().length() != 0) {
							currentMember.setCalculated(true);
							CubeInfo cubeInfo = 
								xmlaConnection.getCubeLoader(currentDimension.getDatabase()).load(cubeId);
							RuleImpl rule = new RuleImpl((XMLACubeInfo) cubeInfo, text.trim());
							rule.setComment("Rule comment");
							rule.setExternalIdentifier(text.trim());
							rule.setDefinition(text.trim());
							rule.setTimestamp(new Date().getTime());
							currentMember.setRule(rule);
						}
					} else if (nlRow.item(j).getNodeName().equals("CHILDREN_CARDINALITY")) {
						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
						int estimatedChildCount = Integer.parseInt(text);
						currentMember.setHasChildren( estimatedChildCount > 0);
						currentMember.setEstimatedChildCount(estimatedChildCount);
					}
				}
			}
			if (currentDimension != null && currentMember != null) {
				if (currentMemberUniqueName.length() != 0) {
					currentMember.setUniqueName(currentMemberUniqueName);
					currentMember.setId(currentMemberUniqueName);
				}
				currentDimension.addMemberInternal(currentMember);
				response.add(currentMember);
			}			
			currentDimension.setElementCount(currentDimension.getMemberCountInternal());
		}	
						
		if(!xmlaConnection.usedByWPalo()) {
			XMLAElementInfo [] responseArray = 
				response.toArray(new XMLAElementInfo[0]);
			XMLAElementInfo[] infos = traverseDimension(currentDimension, responseArray);					
			return infos;
		} 
		return response.toArray(new XMLAElementInfo[0]);
	}

	private int setDepth(XMLADimensionInfo dim, XMLAElementInfo element, int depth, LinkedHashSet elementSet, int positionCounter) {
		if (element == null || dim == null) {
			return positionCounter;
		}		
		
		if (depth > element.getDepth()) {
			element.setDepth(depth);
		} else {
			elementSet.add(element);
			return positionCounter;
		}
		if (depth > maxDepth) {
			maxDepth = depth;
		}
		element.setPosition(positionCounter);
		elementSet.add(element);
		String [] kidIds = element.getChildren();
		for (int i = 0, n = kidIds.length; i < n; i++) {
			XMLAElementInfo kid = dim.getMemberByIdInternal(kidIds[i]);
			positionCounter = setDepth(dim, kid, depth + 1, elementSet, positionCounter + 1);
		}
		return positionCounter;
	}
	
	private void setLevel(XMLADimensionInfo dim, XMLAElementInfo element, int level, LinkedHashSet elementSet) {
		if (element == null || dim == null) {
			return;
		}		
		
		if (level > element.getLevel()) {
			element.setLevel(level);
		} else {
			elementSet.add(element);
			return;
		}
		elementSet.add(element);
		if (level > maxLevel) {
			maxLevel = level;
		}
		
		String [] parIds = element.getParents();
		for (int i = 0, n = parIds.length; i < n; i++) {
			XMLAElementInfo par = dim.getMemberByIdInternal(parIds[i]);
			setLevel(dim, par, level + 1, elementSet);
		}
	}
	
	private XMLAElementInfo [] traverseDimension(XMLADimensionInfo dim, XMLAElementInfo [] elements) {
		maxLevel = 0;
		maxDepth = 0;;
		
		List rootElements = new ArrayList();
		List baseElements = new ArrayList();
		for (int i = 0, n = elements.length; i < n; i++) {
			if (elements[i].getParentCount() == 0) {
				rootElements.add(elements[i]);
			}
			if (elements[i].getChildrenCount() == 0) {
				baseElements.add(elements[i]);
			}
		}		
		LinkedHashSet elementSet = new LinkedHashSet();
		int positionCounter = 0;
		for (int i = 0, n = rootElements.size(); i < n; i++) {
			XMLAElementInfo element = (XMLAElementInfo) rootElements.get(i);
			positionCounter = setDepth(dim, element, 0, elementSet, positionCounter);			
		}
		for (int i = 0, n = baseElements.size(); i < n; i++) {
			XMLAElementInfo element = (XMLAElementInfo) baseElements.get(i);
			setLevel(dim, element, 0, elementSet);
		}		
		BuilderRegistry.getInstance().getDimensionInfoBuilder().
			updateMaxLevelAndDepth(dim, maxDepth, maxLevel);
		XMLAElementInfo[] infos = (XMLAElementInfo []) elementSet.toArray(new XMLAElementInfo[0]);
		return elements;
	}
	
	public void setElementListInternal(String id, XMLADimensionInfo dim, List elements) {
		throw new RuntimeException("No longer supported.");
	}    
}
