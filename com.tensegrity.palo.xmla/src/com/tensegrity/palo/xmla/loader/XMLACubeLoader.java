/*
*
* @file XMLACubeLoader.java
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
* @version $Id: XMLACubeLoader.java,v 1.7 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palo.xmla.loader;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLACubeInfo;
import com.tensegrity.palo.xmla.XMLADatabaseInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palo.xmla.XMLAVariableInfo;
import com.tensegrity.palo.xmla.builders.BuilderRegistry;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.loader.CubeLoader;

public class XMLACubeLoader extends CubeLoader {
	private Set <String> cubeIds = null;
	private final XMLAClient xmlaClient;
	private final XMLAConnection xmlaConnection;
	
	public XMLACubeLoader(DbConnection paloConnection, XMLAClient xmlaClient, DatabaseInfo database, XMLAConnection con) {
		super(paloConnection, database);		
		this.xmlaClient = xmlaClient;
		this.xmlaConnection = con;
	}

	public String [] getAllCubeIds() {
		if (cubeIds == null) {
			loadAllCubeIds();
		}
		return cubeIds.toArray(new String[0]);
	}

	public String [] getCubeIds(DimensionInfo dimension) {		
		CubeInfo [] cubes = xmlaConnection.getCubes(dimension);
		String [] ids = new String[cubes.length];
		int counter = 0;
		for (CubeInfo cube: cubes) {
			ids[counter++] = cube.getId();
		}
		return ids;
	}	
	
	public CubeInfo loadByName(String name) {
		//first check if we have it loaded already
		CubeInfo cube = findCube(name);
		if (cube == null) {
			//if not, we have to ask server...
			return loadCube(name);
		}
		return cube;
	}

	protected final void reload() {
		System.out.println("XMLACubeLoader::reload.");
	}

	private final CubeInfo findCube(String name) {
		Collection<PaloInfo> infos = getLoaded();
		for (PaloInfo info : infos) {
			if (info instanceof CubeInfo) {
				CubeInfo cube = (CubeInfo) info;
				if (cube.getId().equals(name))
					return cube;
			}
		}
		return null;
	}

	private final void loadAllCubeIds() {
		cubeIds = new LinkedHashSet <String> ();
		String connectionName = xmlaClient.getConnections()[0].getName();

		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();

	        prop.setDataSourceInfo(connectionName);
	        prop.setFormat("Tabular");
	        prop.setContent("SchemaData");
	        prop.setCatalog(database.getId());

	        rest.setCatalog(database.getId());	        
	        Document result = xmlaClient.getCubeList(rest, prop);
		    NodeList nl = result.getElementsByTagName("row");
    	    	        
			if (nl == null || nl.getLength() == 0) {
				return;
			}			
			for (int i = 0; i < nl.getLength(); i++) {
				NodeList nlRow = nl.item(i).getChildNodes();
				for (int j = 0; j < nlRow.getLength(); j++) {
					if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
						if (nlRow.item(j).getNodeName().equals("CUBE_NAME"))  {	
							String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));
							if (XMLAClient.IGNORE_VARIABLE_CUBES) {
								PropertyInfo pi = 
									xmlaConnection.getPropertyLoader().load(XMLAConnection.PROPERTY_SAP_VARIABLES);		
								if (pi != null) {
									if (Boolean.parseBoolean(pi.getValue())) {
										XMLAVariableInfo [] infos = BuilderRegistry.getInstance().
										getVariableInfoBuilder().requestVariables(xmlaClient, 
												(XMLADatabaseInfo) database, text);
										if (infos != null && infos.length > 0) {
											continue;
										}
									}
								}								
							}
							cubeIds.add(text);
						} 
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private final XMLACubeInfo loadCube(String name) {
		XMLACubeInfo cubeInfo = 
			BuilderRegistry.getInstance().getCubeInfoBuilder().
				getCubeInfo(xmlaClient, (XMLADatabaseInfo) database, name,
						xmlaConnection);
		loadedInfo.put(name, cubeInfo);		
		return cubeInfo;
	}

	public String[] getCubeIds(int typeMask) {
		return getAllCubeIds();
	}
}
