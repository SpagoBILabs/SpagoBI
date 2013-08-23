/*
*
* @file XMLACubeRequestor.java
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
* @version $Id: XMLACubeRequestor.java,v 1.4 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.parsers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLACubeInfo;
import com.tensegrity.palo.xmla.XMLADatabaseInfo;
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palo.xmla.XMLAVariableInfo;
import com.tensegrity.palo.xmla.builders.BuilderRegistry;
import com.tensegrity.palojava.PropertyInfo;

public class XMLACubeRequestor extends AbstractXMLARequestor {
	public static String ITEM_CATALOG_NAME            = "CATALOG_NAME";
	public static String ITEM_SCHEMA_NAME             = "SCHEMA_NAME";
	public static String ITEM_CUBE_NAME               = "CUBE_NAME";
	public static String ITEM_CUBE_TYPE               = "CUBE_TYPE";
	public static String ITEM_CUBE_GUID               = "CUBE_GUID";
	public static String ITEM_CREATED_ON              = "CREATED_ON";
	public static String ITEM_LAST_SCHEMA_UPDATE      = "LAST_SCHEMA_UPDATE";
	public static String ITEM_SCHEMA_UPDATED_BY       = "SCHEMA_UPDATED_BY";
	public static String ITEM_LAST_DATA_UPDATE        = "LAST_DATA_UPDATE";
	public static String ITEM_DATA_UPDATED_BY         = "DATA_UPDATED_BY";
	public static String ITEM_DESCRIPTION             = "DESCRIPTION";
	public static String ITEM_IS_DRILLTHROUGH_ENABLED = "IS_DRILLTHROUGH_ENABLED";
	public static String ITEM_IS_LINKABLE             = "IS_LINKABLE";
	public static String ITEM_IS_WRITE_ENABLED        = "IS_WRITE_ENABLED";
	public static String ITEM_IS_SQL_ENABLED          = "IS_SQL_ENABLED";
	public static String ITEM_CUBE_CAPTION            = "CUBE_CAPTION";
	public static String ITEM_BASE_CUBE_NAME          = "BASE_CUBE_NAME";
	public static String ITEM_ANNOTATIONS             = "ANNOTATIONS";
	
	private String restrictionCatalog;
	private String restrictionSchema;
	private String restrictionCube;
	private String restrictionCubeSource;
	private String restrictionBaseCube;
	private String propertyCatalog;
	
	private final ArrayList <XMLACubeInfo> cubeInfos = 
		new ArrayList<XMLACubeInfo>();
	private final XMLADatabaseInfo database; 
	private final XMLAConnection   connection;
	
	public XMLACubeRequestor(XMLAConnection connection, XMLADatabaseInfo database) {
		activateItem(ITEM_CATALOG_NAME);
		activateItem(ITEM_SCHEMA_NAME);
		activateItem(ITEM_CUBE_NAME);
		activateItem(ITEM_CUBE_TYPE);
		activateItem(ITEM_CUBE_GUID);
		activateItem(ITEM_CREATED_ON);
		activateItem(ITEM_LAST_SCHEMA_UPDATE);
		activateItem(ITEM_SCHEMA_UPDATED_BY);
		activateItem(ITEM_LAST_DATA_UPDATE);
		activateItem(ITEM_DATA_UPDATED_BY);
		activateItem(ITEM_DESCRIPTION);
		activateItem(ITEM_IS_DRILLTHROUGH_ENABLED);
		activateItem(ITEM_IS_LINKABLE);
		activateItem(ITEM_IS_WRITE_ENABLED);
		activateItem(ITEM_IS_SQL_ENABLED);
		activateItem(ITEM_CUBE_CAPTION);
		activateItem(ITEM_BASE_CUBE_NAME);
		activateItem(ITEM_ANNOTATIONS);       
		this.database = database;
		this.connection = connection;
	}
	
	public void setCatalogNameProperty(String catalogName) {
		propertyCatalog = catalogName;
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
	
	public void setCubeSourceRestriction(String id) {
		restrictionCubeSource = id;
	}
	
	public void setBaseCubeRestriction(String cubeName) {
		restrictionBaseCube = cubeName;
	}
		
	private final XMLARestrictions setRestrictions() {
		XMLARestrictions rest = new XMLARestrictions();

		rest.setCatalog(restrictionCatalog);
		rest.setSchema(restrictionSchema);
		rest.setCubeName(restrictionCube);
		rest.setCubeSource(restrictionCubeSource);
		rest.setBaseCube(restrictionBaseCube);
		
		return rest;
	}
	
	public XMLACubeInfo [] requestCubes(XMLAClient xmlaClient) {
		cubeInfos.clear();
		
		try {    	    
			XMLARestrictions rest = setRestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        String connectionName = xmlaClient.getConnections()[0].getName();
    	    prop.setDataSourceInfo(connectionName);
    	    prop.setCatalog(propertyCatalog);
    		    		
    	    Document result = xmlaClient.getCubeList(rest, prop);
		    NodeList nl  = result.getElementsByTagName("row");
			if (nl == null || nl.getLength() == 0) {
				return new XMLACubeInfo[0];
			}
			parseXMLANodeList(nl, connectionName, xmlaClient);
		} catch (Exception e) {
			e.printStackTrace();
			return new XMLACubeInfo[0];
		}
		return cubeInfos.toArray(new XMLACubeInfo[0]);
	}

	protected void parseResult(HashMap <String, String> result, 
			String connectionName, XMLAClient xmlaClient) {
		String cubeName = result.get(ITEM_CUBE_NAME);

		XMLACubeInfo cubeInfo = new XMLACubeInfo(cubeName, cubeName, database, connectionName, xmlaClient, connection);
		BigInteger cellCount = new BigInteger("1");
		XMLADimensionRequestor req = new XMLADimensionRequestor(cubeInfo, connection);
		req.setCatalogNameRestriction(restrictionCatalog);
		req.setCubeNameRestriction(cubeName);
		XMLADimensionInfo [] dimensions = req.requestDimensions(xmlaClient);

		cubeInfo.setDimensionCount(dimensions.length);
		ArrayList <String> dimIds = new ArrayList<String>();
		for (XMLADimensionInfo info: dimensions) {
			dimIds.add(info.getId());
		}
		cubeInfo.setDimensions(dimIds.toArray(new String[0]));
		cubeInfo.setCellCount(cellCount);
		cubeInfo.setFilledCellCount(cellCount);						
		PropertyInfo pi = 
			connection.getPropertyLoader().load(XMLAConnection.PROPERTY_SAP_VARIABLES);		
		if (pi != null) {
			if (Boolean.parseBoolean(pi.getValue())) {
				XMLAVariableInfo [] infos = BuilderRegistry.getInstance().
				getVariableInfoBuilder().requestVariables(
						xmlaClient, (XMLADatabaseInfo) cubeInfo.getDatabase(), 
						cubeInfo.getId());
				cubeInfo.setVariables(infos);
				if (XMLAClient.IGNORE_VARIABLE_CUBES) {
					if (infos != null && infos.length > 0) {
						return;
					}
				}
			}
		}		
		if (xmlaClient.isSAP()) {
			String desc = result.get(ITEM_DESCRIPTION);
			if (desc != null && desc.trim().length() > 0) {
				cubeInfo.setName(desc);
			}
		}
		String cap = result.get(ITEM_CUBE_CAPTION);
		if (cap != null && cap.trim().length() > 0) {
			cubeInfo.setName(cap);
		}
		cubeInfos.add(cubeInfo);
	}	
}
