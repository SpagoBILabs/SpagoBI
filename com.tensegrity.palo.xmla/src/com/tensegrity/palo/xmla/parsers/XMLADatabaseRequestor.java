/*
*
* @file XMLADatabaseRequestor.java
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
* @version $Id: XMLADatabaseRequestor.java,v 1.4 2009/06/04 14:01:59 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.parsers;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLADatabaseInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;

public class XMLADatabaseRequestor extends AbstractXMLARequestor {
	public static String ITEM_CATALOG_NAME  = "CATALOG_NAME";
	public static String ITEM_DESCRIPTION   = "DESCRIPTION";
	public static String ITEM_ROLES         = "ROLES";
	public static String ITEM_DATE_MODIFIED = "DATE_MODIFIED";
	
	private String restrictionName;
	private final ArrayList <XMLADatabaseInfo> databaseInfos = 
		new ArrayList<XMLADatabaseInfo>();
	
	private final XMLAConnection connection;
	
	public XMLADatabaseRequestor(XMLAConnection connection) {
		activateItem(ITEM_CATALOG_NAME);
		this.connection = connection;
	}
	
	public void setCatalogNameRestriction(String catalogName) {
		restrictionName = catalogName;
	}
			
	public boolean requestDatabases(XMLAClient xmlaClient) {
		databaseInfos.clear();
		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        String connectionName = xmlaClient.getConnections()[0].getName();
    	    prop.setDataSourceInfo(connectionName);
    		if (restrictionName != null && 
    				restrictionName.trim().length() != 0) {
    			rest.setCatalog(restrictionName);
    		}
    		
	        Document catalogResult = 
	        	xmlaClient.getCatalogList(rest, prop);
	        NodeList catalogList = catalogResult.getElementsByTagName("row");	        
			if (catalogList == null || catalogList.getLength() == 0) {
				return false;
			}
			parseXMLANodeList(catalogList, connectionName, xmlaClient);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void parseResult(HashMap <String, String> result, 
			String connectionName, XMLAClient xmlaClient) {
		String databaseName = result.get(ITEM_CATALOG_NAME);
//		if (xmlaClient.isSAP()) {
//			if (databaseName.equals("$INFOCUBE")) {
//				return;
//			}
//		}
		
		XMLADatabaseInfo databaseInfo = new XMLADatabaseInfo(connection, databaseName);

//		XMLACubeRequestor cubeReq = 
//			new XMLACubeRequestor(connection, databaseInfo);
//		cubeReq.setCatalogNameRestriction(databaseName);
//		cubeReq.setCatalogNameProperty(databaseName);
//		XMLACubeInfo [] cubes = cubeReq.requestCubes(xmlaClient);
		databaseInfo.setCubeCount(1); //cubes.length);
		
//		XMLAHierarchyRequestor hierReq =
//			new XMLAHierarchyRequestor(null, databaseInfo);
//		hierReq.setCatalogNameRestriction(databaseName);
//		XMLAHierarchyInfo [] hierarchies = hierReq.requestHierarchies(xmlaClient);
		databaseInfo.setDimensionCount(1); //hierarchies.length);
		
		databaseInfos.add(databaseInfo);	        			
	}
	
	public XMLADatabaseInfo [] getDatabaseInfos() {
		return databaseInfos.toArray(new XMLADatabaseInfo[0]);
	}
}
