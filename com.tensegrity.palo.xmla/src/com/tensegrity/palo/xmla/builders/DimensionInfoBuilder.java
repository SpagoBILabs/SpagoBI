/*
*
* @file DimensionInfoBuilder.java
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
* @version $Id: DimensionInfoBuilder.java,v 1.23 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.builders;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLACubeInfo;
import com.tensegrity.palo.xmla.XMLADatabaseInfo;
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAHierarchyInfo;
import com.tensegrity.palo.xmla.parsers.XMLADimensionRequestor;
import com.tensegrity.palo.xmla.parsers.XMLAHierarchyRequestor;

public class DimensionInfoBuilder {
    DimensionInfoBuilder() {
    }
    
//	public XMLADimensionInfo getDimensionInfo(XMLAClient client, XMLADatabaseInfo database, String dimensionId, XMLAConnection con) {
//		xmlaClient = client;
//		connectionName = client.getConnections()[0].getName();
//		this.database = database;
//		
//		return requestDimension(dimensionId, con);
//	}
	
    public XMLADimensionInfo [] getDimensionInfo(XMLAConnection connection, XMLAClient client, XMLADatabaseInfo database, XMLACubeInfo cube) {
		XMLADimensionRequestor req = new XMLADimensionRequestor(cube, connection);
		req.setCubeNameRestriction(cube.getId());
		req.setCatalogNameRestriction(database.getId());
		return req.requestDimensions(client);
//    	xmlaClient = client;
//		connectionName = client.getConnections()[0].getName();
//		this.database = database;
//		this.cube = cube;
//		return requestDimensions();
	}
	
	public XMLAHierarchyInfo [] getHierarchyInfo(XMLAConnection connection, XMLAClient client, XMLADimensionInfo dimension) {
//		xmlaClient = client;
//		connectionName = client.getConnections()[0].getName();
//		this.database = (XMLADatabaseInfo) dimension.getDatabase();
//		this.cube = null;
		
		XMLADatabaseInfo database = (XMLADatabaseInfo) dimension.getDatabase();
		XMLAHierarchyRequestor req = new XMLAHierarchyRequestor(dimension, database, connection);
		req.setCatalogNameRestriction(database.getId());
		req.setCubeNameRestriction(dimension.getCubeId());
		req.setDimensionUniqueNameRestriction(dimension.getDimensionUniqueName());		
		return req.requestHierarchies(client);
	}
	
	public XMLAHierarchyInfo getHierarchyInfo(XMLAConnection connection, XMLAClient client, XMLADimensionInfo dimension, String id) {
		XMLADatabaseInfo database = (XMLADatabaseInfo) dimension.getDatabase();
		XMLAHierarchyRequestor req = new XMLAHierarchyRequestor(dimension, database, connection);
		req.setCatalogNameRestriction(database.getId());
		req.setCubeNameRestriction(dimension.getCubeId());
		req.setDimensionUniqueNameRestriction(dimension.getDimensionUniqueName());
		req.setHierarchyUniqueNameRestriction(XMLADimensionInfo.transformId(
				XMLADimensionInfo.getDimIdFromId(id)));
		XMLAHierarchyInfo [] result = req.requestHierarchies(client);
		if (result == null || result.length < 1) {
			return null;
		}
		return result[0];
	}

	public void updateMaxLevelAndDepth(XMLADimensionInfo dimInfo, int maxDepth, int maxLevel) {		
		dimInfo.setMaxDepth(maxDepth);
		dimInfo.setMaxLevel(maxLevel);
	}
	
//	private XMLADimensionInfo [] requestDimensions() {
//		try {
//    	    XMLARestrictions rest = new XMLARestrictions();
//    	    XMLAProperties   prop = new XMLAProperties();
//
//	        prop.setDataSourceInfo(connectionName);	        	        
//	        prop.setCatalog(database.getId());
//	        rest.setCatalog(database.getId());
//	        rest.setCubeName(cube.getId());
//	        
//	        Document resultDim = xmlaClient.getDimensionList(rest, prop);
//	        NodeList nldim = resultDim.getElementsByTagName("row");
//	        LinkedHashMap dimensions = new LinkedHashMap();
//	        determineDimensions(nldim, dimensions);
//	        
//	        Document resulthier = xmlaClient.getHierarchyList(rest, prop);
//	        NodeList nlhier  = resulthier.getElementsByTagName("row");
//		    		        
//		    return storeDimensions(nlhier, dimensions);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return new XMLADimensionInfo[0];
//	}
	
//	private XMLADimensionInfo requestDimension(String id, XMLAConnection con) {
//		try {
//    	    XMLARestrictions rest = new XMLARestrictions();
//    	    XMLAProperties   prop = new XMLAProperties();
//
//	        prop.setDataSourceInfo(connectionName);	        	        
//	        prop.setCatalog(database.getId());
//	        rest.setCatalog(database.getId());
//	        	        
//	        id = XMLADimensionInfo.transformId(id);
//	        int indexSep = id.indexOf(XMLADimensionLoader.DIMENSION_ID_SEP);
//	        if (indexSep == -1) {
//	        	return null;
//	        }
//	        int rIndexSep = indexSep + XMLADimensionLoader.DIMENSION_ID_SEP.length();
//	        String cubeId = id.substring(0, indexSep);
//	        String dimensionId = id.substring(rIndexSep);
//	        String cubeName = con.getCubeLoader(database).load(cubeId).getName();  
//	        rest.setCubeName(cubeId);
//	        rest.setHierarchyUniqueName(dimensionId);
//	        	        
//	        Document resulthier = xmlaClient.getHierarchyList(rest, prop);
//	        NodeList nlhier  = resulthier.getElementsByTagName("row");
//		    		        
//			if (nlhier == null || nlhier.getLength() == 0) {
//				return null;
//			}
//
//			NodeList nlRow = nlhier.item(0).getChildNodes();			
//			XMLADimensionInfo dimInfo = new XMLADimensionInfo("", database, cubeId);
//			for (int j = 0; j < nlRow.getLength(); j++) {				
//				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {					
//					String nodeName = nlRow.item(j).getNodeName();
//					if (nodeName.equals("HIERARCHY_UNIQUE_NAME")) {						
//						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
//						dimInfo.setName(text);
//						dimInfo.setHierarchyUniqueName(text);
//						dimInfo.setId(text);
//					} else if (nodeName.equals("DIMENSION_TYPE")) {
//						int type = Integer.parseInt(XMLAClient.getTextFromDOMElement(nlRow.item(j)));
//						dimInfo.setCubeId(cubeId);
//						dimInfo.setXmlaType(type == 2 ? XMLADimensionInfo.XMLA_TYPE_MEASURES : XMLADimensionInfo.XMLA_TYPE_NORMAL);
//					} else if (nodeName.equals("HIERARCHY_CARDINALITY")) {
//						int kids = Integer.parseInt(
//								XMLAClient.getTextFromDOMElement(nlRow.item(j)));
//						dimInfo.setElementCount(kids);
//					} else if (nodeName.equals("DIMENSION_UNIQUE_NAME")) {
//						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
//						dimInfo.setDimensionUniqueName(text);
//					} else if (nodeName.equals("HIERARCHY_CAPTION")) {
//						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
//						dimInfo.setName(text); // + " (" + cubeName + ")");
//						dimInfo.setHierarchyCaption(text);
//					} else if (nodeName.equals("DEFAULT_MEMBER")) {
//						dimInfo.setDefaultElementName(XMLAClient.getTextFromDOMElement(nlRow.item(j)));
//					}
//				}
//			}			
//	        return dimInfo;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;		
//	}
		
//	private void determineDimensions(NodeList nl, LinkedHashMap dimensions) {
//		if (nl == null || nl.getLength() == 0) {
//			return;
//		}
//		for (int i = 0, n = nl.getLength(); i < n; i++) {
//			NodeList nlRow = nl.item(i).getChildNodes();
//			String uniqueName = null;
//			String caption = null;
//			for (int j = 0; j < nlRow.getLength(); j++) {				
//				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
//					String nodeName = nlRow.item(j).getNodeName();
//					if (nodeName.equals("DIMENSION_NAME")) {
//						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
//					} else if (nodeName.equals("DIMENSION_UNIQUE_NAME")) {
//						uniqueName = XMLAClient.getTextFromDOMElement(nlRow.item(j));						
//					} else if (nodeName.equals("DIMENSION_CAPTION")) {
//						caption = XMLAClient.getTextFromDOMElement(nlRow.item(j));						
//					}
//				}
//			}
//			if (uniqueName != null) {
//				dimensions.put(uniqueName, caption);
//			}
//		}
//	}
		
//	private XMLADimensionInfo [] storeDimensions(NodeList nlhier, LinkedHashMap dimensions) {
//		ArrayList dimensionList = new ArrayList();
//		
//		if (nlhier == null || nlhier.getLength() == 0) {
//			return new XMLADimensionInfo[0];
//		}
//		
//		for (int i = 0, n = nlhier.getLength(); i < n; i++) {
//			NodeList nlRow = nlhier.item(i).getChildNodes();			
//			XMLADimensionInfo dimInfo;
//			if (cube != null) {
//				dimInfo = new XMLADimensionInfo("", database, cube.getId());
//			} else {
//				dimInfo = new XMLADimensionInfo("", database, null);
//			}
//			ArrayList currentList = null;
//			for (int j = 0; j < nlRow.getLength(); j++) {				
//				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {					
//					String nodeName = nlRow.item(j).getNodeName();
//					if (nodeName.equals("HIERARCHY_UNIQUE_NAME")) {						
//						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
//						if (cube != null) {
//							dimInfo.setCubeId(cube.getId());
//						} else {
//							dimInfo.setCubeId(null);
//						}
//						dimInfo.setName(text);
//						dimInfo.setHierarchyUniqueName(text);
//						dimInfo.setId(text);
//					} else if (nodeName.equals("DIMENSION_TYPE")) {
//						int type = Integer.parseInt(XMLAClient.getTextFromDOMElement(nlRow.item(j)));
//						if (cube != null) {
//							dimInfo.setCubeId(cube.getId());
//						} else {
//							dimInfo.setCubeId(null);
//						}
//						dimInfo.setXmlaType(type == 2 ? XMLADimensionInfo.XMLA_TYPE_MEASURES : XMLADimensionInfo.XMLA_TYPE_NORMAL);
//						dimInfo.setInternalXmlaType(type);
//					} else if (nodeName.equals("HIERARCHY_CARDINALITY")) {
//						int kids = Integer.parseInt(
//								XMLAClient.getTextFromDOMElement(nlRow.item(j)));
//						dimInfo.setElementCount(kids);
//					} else if (nodeName.equals("DIMENSION_UNIQUE_NAME")) {
//						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
//						dimInfo.setDimensionUniqueName(text);
//						Object o = dimensions.get(text);
//						if (o instanceof String) {
//							dimInfo.setDimensionCaption((String) o);
//							currentList = new ArrayList();
//							dimensions.put(text, currentList);
//						} else {
//							currentList = (ArrayList) o;
//						}
//					} else if (nodeName.equals("HIERARCHY_CAPTION")) {
//						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
//						dimInfo.setName(text); // + " (" + cube.getName() + ")");
//						//dimInfo.setName(cube.getName() + "." + text);
//						dimInfo.setHierarchyCaption(text);
//					} else if (nodeName.equals("DEFAULT_MEMBER")) {
//						dimInfo.setDefaultElementName(XMLAClient.getTextFromDOMElement(nlRow.item(j)));
//					}
//				}
//			}
//			if (currentList != null) {
//				currentList.add(dimInfo);
//				dimensions.put(dimInfo.getDimensionUniqueName(), currentList);
//			}
//			if (cube != null) {
//				dimInfo.setCubeId(cube.getId());
//			} else {
//				dimInfo.setCubeId(null);
//			}
//			dimensionList.add(dimInfo);
//		}
//
//		Set set = dimensions.keySet();
//		LinkedHashMap <String, XMLAHierarchyInfo> hierarchies;
//		hierarchies = (LinkedHashMap <String, XMLAHierarchyInfo>) 
//							hierarchyLists.get(cube.getId());
//		if (hierarchies == null) { 
//			hierarchies = new LinkedHashMap <String, XMLAHierarchyInfo> ();
//		}
//		for (Object o: set) {			
//			ArrayList list = (ArrayList) dimensions.get(o);
//			XMLADimensionInfo rootDim = (XMLADimensionInfo) list.get(0);
//			XMLAHierarchyInfo currentHierarchy;
//			currentHierarchy = hierarchies.get(rootDim.getHierarchyUniqueName());
//			if (currentHierarchy == null) {
//				currentHierarchy = new XMLAHierarchyInfo(rootDim,
//					rootDim.getDimensionCaption(), 
//					rootDim.getHierarchyUniqueName());
//			}
//			for (Object l: list) {
//				if (!rootDim.containsDimension((XMLADimensionInfo) l)) {
//					rootDim.addDimension((XMLADimensionInfo) l);
//				}
//			}
//			hierarchies.put(rootDim.getHierarchyUniqueName(), currentHierarchy);
//		}		
//		
//		hierarchyLists.put(cube.getId(), hierarchies);
//
//		return (XMLADimensionInfo [])
//			dimensionList.toArray(new XMLADimensionInfo [0]);
//	}
	
//	public void setDimensionListInternal(XMLADatabaseInfo db, XMLACubeInfo cb, List dims) {
//		throw new RuntimeException("No longer supported.");
//	}		
}
