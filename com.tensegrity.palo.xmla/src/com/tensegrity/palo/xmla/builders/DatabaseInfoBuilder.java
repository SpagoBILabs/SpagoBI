/*
*
* @file DatabaseInfoBuilder.java
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
* @version $Id: DatabaseInfoBuilder.java,v 1.21 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.builders;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLADatabaseInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palo.xmla.parsers.XMLADatabaseRequestor;

public class DatabaseInfoBuilder {
	private XMLAClient       xmlaClient;
	private String           connectionName;
	
    DatabaseInfoBuilder() {
    }
    	
	public XMLADatabaseInfo getDatabaseInfo(XMLAConnection connection, XMLAClient client, String name) {
		if (name == null) {
			return null;
		}
		XMLADatabaseRequestor req = new XMLADatabaseRequestor(connection);
		req.setCatalogNameRestriction(name);
		if (!req.requestDatabases(client)) {
			return null;
		}
		return req.getDatabaseInfos()[0];
		//return databaseInfo;
		
//		xmlaClient = client;
//		connectionName = client.getConnections()[0].getName();
//		return requestDatabase(name);
	}
    
//	public Object parseDatabaseName(String databaseName) {
//		XMLADatabaseInfo databaseInfo = new XMLADatabaseInfo(databaseName);
//		databaseInfo.setCubeCount(requestCubes(databaseName));
//		databaseInfo.setDimensionCount(requestDimensions(databaseName));						
//		return databaseInfo;	        		
//	}
	
//	private XMLADatabaseInfo requestDatabase(String name) {
//		if (xmlaClient.isSAP(xmlaClient.getConnections()[0])) {
//			if (name.equals("$INFOCUBE")) {
//				return null;
//			}
//		}
//		try {
//    	    XMLARestrictions rest = new XMLARestrictions();
//    	    XMLAProperties   prop = new XMLAProperties();
//
//	        prop.setDataSourceInfo(connectionName);
//    		rest.setCatalog(name);
//    		
//	        Document catalogResult = 
//	        	xmlaClient.getCatalogList(rest, prop);
//	        NodeList catalogList = catalogResult.getElementsByTagName("row");	        
//			if (catalogList == null || catalogList.getLength() == 0) {
//				return null;
//			}
//			
//			XMLADatabaseInfo databaseInfo = null; 
//			NodeList nlRow = catalogList.item(0).getChildNodes();
//			for (int j = 0; j < nlRow.getLength(); j++) {
//				if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
//					if (nlRow.item(j).getNodeName().equals("CATALOG_NAME"))  {	
//						String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
//						databaseInfo = new XMLADatabaseInfo(connection, text);
//						databaseInfo.setCubeCount(requestCubes(text));
//						databaseInfo.setDimensionCount(requestDimensions(text));						
//					} 
//				}
//			}
//			return databaseInfo;	        
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null; 		
//	}
			
	private int requestCubes(String databaseName) {
		// TODO This is nonsense! We have to request cubes later anyway to
		//      store them, so we might as well do it here, instead of only
		//      counting them.
		int cubeCount = 0;
		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        prop.setDataSourceInfo(connectionName);
	        prop.setFormat("Tabular");
	        prop.setContent("SchemaData");
	        prop.setCatalog(databaseName);

	        rest.setCatalog(databaseName);
	        Document result = xmlaClient.getCubeList(rest, prop);
		    NodeList nl  = result.getElementsByTagName("row");
		    
			if (nl == null || nl.getLength() == 0) {
				return 0;
			}
			for (int i = 0; i < nl.getLength(); i++) {
				NodeList nlRow = nl.item(i).getChildNodes();
					for (int j = 0; j < nlRow.getLength(); j++) {
					if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
						if (nlRow.item(j).getNodeName().equals("CUBE_NAME"))  {	
							cubeCount++;						
						}
					}
				}
			}
			return cubeCount;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
    }
	
	private int requestDimensions(String databaseName) {
		// TODO This is nonsense! We have to request dimensions later anyway to
		//      store them, so we might as well do it here, instead of only
		//      counting them.
		int dimensionCount = 0;
		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        prop.setDataSourceInfo(connectionName);
	        prop.setCatalog(databaseName);
	        rest.setCatalog(databaseName);

	        Document resultdim = xmlaClient.getDimensionList(rest, prop);
	        NodeList nldim  = resultdim.getElementsByTagName("row");
		    				
			if (nldim == null || nldim.getLength() == 0) {
				return 0;
			}
	        for (int i = 0, n = nldim.getLength(); i < n; i++)  {
				NodeList nlRow = nldim.item(i).getChildNodes();

				for (int j = 0; j < nlRow.getLength(); j++) {
					if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
						if (nlRow.item(j).getNodeName().equals("DIMENSION_UNIQUE_NAME"))  {							
							dimensionCount++;
						} 
					}
				}
			}
	        return dimensionCount;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
    }
}
