/*
*
* @file XMLADimensionLoader.java
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
* @version $Id: XMLADimensionLoader.java,v 1.8 2009/04/29 10:35:38 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palo.xmla.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLACubeInfo;
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palo.xmla.parsers.XMLADimensionRequestor;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.loader.DimensionLoader;

public class XMLADimensionLoader extends DimensionLoader {
	public static final String DIMENSION_ID_SEP  = "|.#.|";
	private Map <String, String []> dimensionIds = null;
	private Set <String> allIds = null;
	private final XMLAClient xmlaClient;
	
	
	public XMLADimensionLoader(DbConnection paloConnection, XMLAClient xmlaClient,
			DatabaseInfo database) {
		super(paloConnection, database);
		this.xmlaClient = xmlaClient;		
	}

	public String[] getAllDimensionIds() {
		if (allIds == null) {
			allIds = new LinkedHashSet <String> ();
			if (dimensionIds == null) {			
				dimensionIds = new LinkedHashMap <String, String []> ();
			}
			String [] cubeIds = 
				((XMLAConnection) paloConnection).
					getCubeLoader(database).getAllCubeIds();
			for (String cubeId: cubeIds) {
				if (!dimensionIds.containsKey(cubeId)) {
					XMLADimensionRequestor req = new XMLADimensionRequestor(
							(XMLACubeInfo) ((XMLAConnection) paloConnection).getCubeLoader(database).load(cubeId),
							(XMLAConnection) paloConnection);
					req.setCatalogNameRestriction(database.getId());
					req.setCubeNameRestriction(cubeId);
					XMLADimensionInfo [] dims = req.requestDimensions(xmlaClient);
					//dimensionIds.put(cubeId, loadAllDimensionIds(cubeId));
					ArrayList <String> dimIds = new ArrayList<String>();
					for (XMLADimensionInfo dim: dims) {
						dimIds.add(dim.getId());
					}
					dimensionIds.put(cubeId, dimIds.toArray(new String[0]));
				}
				allIds.addAll(Arrays.asList(dimensionIds.get(cubeId)));
			}
		}
		return allIds.toArray(new String[0]);
	}
	
	public String [] getAllDimensionIdsForCube(CubeInfo cube) {
		if (dimensionIds == null) {			
			dimensionIds = new LinkedHashMap <String, String []> ();
		}
		if (!dimensionIds.containsKey(cube.getId())) {
			XMLADimensionRequestor req = new XMLADimensionRequestor(
					(XMLACubeInfo) cube,
					(XMLAConnection) paloConnection);
			req.setCatalogNameRestriction(database.getId());
			req.setCubeNameRestriction(cube.getId());
			XMLADimensionInfo [] dims = req.requestDimensions(xmlaClient);
			ArrayList <String> dimIds = new ArrayList<String>();
			for (XMLADimensionInfo dim: dims) {
				dimIds.add(dim.getId());
			}
			dimensionIds.put(cube.getId(), dimIds.toArray(new String[0]));
			//dimensionIds.put(cube.getId(), loadAllDimensionIds(cube.getId()));
		}
		return dimensionIds.get(cube.getId());
	}

	public DimensionInfo loadByName(String name) {
		//first check if we have it loaded already
		DimensionInfo dimInfo = findDimension(name);
		if(dimInfo == null) {
			//if not, we have to ask server...
			return loadDimension(name);
		}
		return dimInfo;
	}

	protected final void reload() {
		System.out.println("XMLADimensionLoader::reload.");
	}

	private final DimensionInfo findDimension(String name) {
		Collection<PaloInfo> infos = getLoaded();
		for (PaloInfo info : infos) {
			if (info instanceof DimensionInfo) {
				DimensionInfo dimInfo = (DimensionInfo) info;
				if (dimInfo.getName().equals(name))
					return dimInfo;
			}
		}
		return null;
	}
	
//	private final String [] loadAllDimensionIds(String cubeId) {		
//		String connectionName = xmlaClient.getConnections()[0].getName();
//		ArrayList <String> dimIds = new ArrayList <String> ();
//		
//		try {
//    	    XMLARestrictions rest = new XMLARestrictions();
//    	    XMLAProperties   prop = new XMLAProperties();
//
//	        prop.setDataSourceInfo(connectionName);	        	        
//	        prop.setCatalog(database.getId());
//	        rest.setCatalog(database.getId());
//	        rest.setCubeName(cubeId);
//	        
//	        Document resultDim = xmlaClient.getHierarchyList(rest, prop);
//	        NodeList nl = resultDim.getElementsByTagName("row");
//    	    	        
//			if (nl == null || nl.getLength() == 0) {
//				return new String[0];
//			}				
//			for (int i = 0, n = nl.getLength(); i < n; i++) {
//				NodeList nlRow = nl.item(i).getChildNodes();
//				for (int j = 0; j < nlRow.getLength(); j++) {				
//					if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
//						String nodeName = nlRow.item(j).getNodeName();
//						if (nodeName.equals("HIERARCHY_UNIQUE_NAME")) {
//							String text = XMLAClient.getTextFromDOMElement(nlRow.item(j));													
//							dimIds.add(XMLADimensionInfo.getIDString(text, cubeId));
//							//dimensionIds.add(XMLADimensionInfo.getIDString(text, cubeId));
//						}
//					}
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return dimIds.toArray(new String[0]);
//	}
	
	private final XMLADimensionInfo loadDimension(String name) {
		// TODO
		return null;
//		System.out.println("XMLADimensionLoader.loadDimension not supported.");
//		throw new RuntimeException("Not supported.");
	}

	public String[] getDimensionIds(int typeMask) {
		return getAllDimensionIds();
	}			
}
