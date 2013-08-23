/*
*
* @file XMLAHierarchyRequestor.java
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
* @version $Id: XMLAHierarchyRequestor.java,v 1.3 2009/04/29 10:35:37 PhilippBouillon Exp $
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
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAHierarchyInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;

public class XMLAHierarchyRequestor extends AbstractXMLARequestor {
	public static String ITEM_CATALOG_NAME                 = "CATALOG_NAME";
	public static String ITEM_SCHEMA_NAME                  = "SCHEMA_NAME";
	public static String ITEM_CUBE_NAME                    = "CUBE_NAME";
	public static String ITEM_DIMENSION_UNIQUE_NAME        = "DIMENSION_UNIQUE_NAME";
	public static String ITEM_HIERARCHY_NAME               = "HIERARCHY_NAME";
	public static String ITEM_HIERARCHY_UNIQUE_NAME        = "HIERARCHY_UNIQUE_NAME";
	public static String ITEM_HIERARCHY_GUID               = "HIERARCHY_GUID";
	public static String ITEM_HIERARCHY_CAPTION            = "HIERARCHY_CAPTION";
	public static String ITEM_DIMENSION_TYPE               = "DIMENSION_TYPE";
	public static String ITEM_HIERARCHY_CARDINALITY        = "HIERARCHY_CARDINALITY";
	public static String ITEM_DEFAULT_MEMBER               = "DEFAULT_MEMBER";
	public static String ITEM_ALL_MEMBER                   = "ALL_MEMBER";
	public static String ITEM_DESCRIPTION                  = "DESCRIPTION";
	public static String ITEM_STRUCTURE                    = "STRUCTURE";
	public static String ITEM_IS_VIRTUAL                   = "IS_VIRTUAL";
	public static String ITEM_IS_READWRITE                 = "IS_READWRITE";
	public static String ITEM_DIMENSION_UNIQUE_SETTINGS    = "DIMENSION_UNIQUE_SETTINGS";
	public static String ITEM_DIMENSION_MASTER_UNIQUE_NAME = "DIMENSION_MASTER_UNIQUE_NAME";
	public static String ITEM_DIMENSION_IS_VISIBLE         = "DIMENSION_IS_VISIBLE";
	public static String ITEM_HIERARCHY_ORDINAL            = "HIERARCHY_ORDINAL";
	public static String ITEM_DIMENSION_IS_SHARED          = "DIMENSION_IS_SHARED";
	public static String ITEM_HIERARCHY_IS_VISIBLE         = "HIERARCHY_IS_VISIBLE";
	public static String ITEM_HIERARCHY_ORIGIN             = "HIERARCHY_ORIGIN";
	public static String ITEM_HIERARCHY_DISPLAY_FOLDER     = "HIERARCHY_DISPLAY_FOLDER";
	public static String ITEM_INSTANCE_SELECTION           = "INSTANCE_SELECTION";
	
	private String restrictionCatalog;
	private String restrictionSchema;
	private String restrictionCube;
	private String restrictionDimensionUniqueName;
	private String restrictionHierarchyName;
	private String restrictionHierarchyUniqueName;
	private String restrictionHierarchyOrigin;
	private String restrictionCubeSource;
	private String restrictionHierarchyVisibility;
		
	private final ArrayList <XMLAHierarchyInfo> hierarchyInfos = 
		new ArrayList <XMLAHierarchyInfo>();
	private final XMLADatabaseInfo database; 
	private final XMLADimensionInfo dimension;
	private final XMLAConnection connection;
	
	public XMLAHierarchyRequestor(XMLADimensionInfo dimension, XMLADatabaseInfo database, XMLAConnection connection) {
		activateItem(ITEM_CATALOG_NAME);
		activateItem(ITEM_SCHEMA_NAME);
		activateItem(ITEM_CUBE_NAME);
		activateItem(ITEM_DIMENSION_UNIQUE_NAME);
		activateItem(ITEM_HIERARCHY_NAME);
		activateItem(ITEM_HIERARCHY_UNIQUE_NAME);
		activateItem(ITEM_HIERARCHY_GUID);               
		activateItem(ITEM_HIERARCHY_CAPTION);            
		activateItem(ITEM_DIMENSION_TYPE);               
		activateItem(ITEM_HIERARCHY_CARDINALITY);        
		activateItem(ITEM_DEFAULT_MEMBER);               
		activateItem(ITEM_ALL_MEMBER);                   
		activateItem(ITEM_DESCRIPTION);                  
		activateItem(ITEM_STRUCTURE);                    
		activateItem(ITEM_IS_VIRTUAL);                   
		activateItem(ITEM_IS_READWRITE);                 
		activateItem(ITEM_DIMENSION_UNIQUE_SETTINGS);    
		activateItem(ITEM_DIMENSION_MASTER_UNIQUE_NAME); 
		activateItem(ITEM_DIMENSION_IS_VISIBLE);         
		activateItem(ITEM_HIERARCHY_ORDINAL);            
		activateItem(ITEM_DIMENSION_IS_SHARED);          
		activateItem(ITEM_HIERARCHY_IS_VISIBLE);         
		activateItem(ITEM_HIERARCHY_ORIGIN);             
		activateItem(ITEM_HIERARCHY_DISPLAY_FOLDER);     
		activateItem(ITEM_INSTANCE_SELECTION);           
		this.database = database;
		this.dimension = dimension;
		this.connection = connection;
	}
		
	public void setCatalogNameRestriction(String catalogName) {
		restrictionCatalog = catalogName;
	}
			
	public void setSchemaNameRestriction(String schemaName) {
		restrictionSchema = schemaName;
	}
	
	public void setCubeNameRestriction(String cubeName) {
		restrictionCube = cubeName;
	}
	
	public void setDimensionUniqueNameRestriction(String dimName) {
		restrictionDimensionUniqueName = dimName;
	}
	
	public void setHierarchyNameRestriction(String hierName) {
		restrictionHierarchyName = hierName;
	}
	
	public void setHierarchyUniqueNameRestriction(String hierName) {
		restrictionHierarchyUniqueName = hierName;
	}

	public void setHierarchyOriginRestriction(String hierOrigin) {
		restrictionHierarchyOrigin = hierOrigin;
	}
	
	public void setCubeSourceRestriction(String cubeName) {
		restrictionCubeSource = cubeName;
	}
	
	public void setHierarchyVisibilityRestriction(String hierVisibility) {
		restrictionHierarchyVisibility = hierVisibility;
	}
		
	private final XMLARestrictions setRestrictions() {
		XMLARestrictions rest = new XMLARestrictions();

		rest.setCatalog(restrictionCatalog);
		rest.setSchema(restrictionSchema);
		rest.setCubeName(restrictionCube);
		rest.setDimensionUniqueName(restrictionDimensionUniqueName);
		rest.setHierarchyName(restrictionHierarchyName);
		rest.setHierarchyUniqueName(restrictionHierarchyUniqueName);
		rest.setHierarchyOrigin(restrictionHierarchyOrigin);
		rest.setCubeSource(restrictionCubeSource);
		rest.setHierarchyVisibility(restrictionHierarchyVisibility);
		
		return rest;
	}
	
	public XMLAHierarchyInfo [] requestHierarchies(XMLAClient xmlaClient) {
		hierarchyInfos.clear();
		
		try {    	    
			XMLARestrictions rest = setRestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        String connectionName = xmlaClient.getConnections()[0].getName();
	        prop.setDataSourceInfo(connectionName);
	        prop.setCatalog(database.getId());
    	    
	        Document result = xmlaClient.getHierarchyList(rest, prop);
	        NodeList nl = result.getElementsByTagName("row");
    		    		
			if (nl == null || nl.getLength() == 0) {
				return new XMLAHierarchyInfo[0];
			}
			parseXMLANodeList(nl, connectionName, xmlaClient);
		} catch (Exception e) {
			e.printStackTrace();
			return new XMLAHierarchyInfo[0];
		}
		
		dimension.setHierarchyCount(hierarchyInfos.size());
		return hierarchyInfos.toArray(new XMLAHierarchyInfo[0]);
	}

	protected void parseResult(HashMap <String, String> result, 
			String connectionName, XMLAClient xmlaClient) {
		
		String hun = XMLADimensionInfo.getIDString(
				result.get(ITEM_HIERARCHY_UNIQUE_NAME), dimension.getCubeId());
		String name = result.get(ITEM_HIERARCHY_CAPTION);
		XMLAHierarchyInfo info;
		info = new XMLAHierarchyInfo(dimension, name, hun);
		info.setCardinality(result.get(ITEM_HIERARCHY_CARDINALITY));
		hierarchyInfos.add(info);
	}	
}
