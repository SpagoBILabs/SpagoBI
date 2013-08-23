/*
*
* @file XMLAElementLoader.java
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
* @author ArndHouben
*
* @version $Id: XMLAElementLoader.java,v 1.24 2009/06/22 12:15:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palo.xmla.loader;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tensegrity.palo.xmla.XMLAClient;
import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLADimensionInfo;
import com.tensegrity.palo.xmla.XMLAElementInfo;
import com.tensegrity.palo.xmla.XMLAHierarchyInfo;
import com.tensegrity.palo.xmla.XMLAProperties;
import com.tensegrity.palo.xmla.XMLARestrictions;
import com.tensegrity.palo.xmla.builders.BuilderRegistry;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.loader.ElementLoader;

/**
 * <code>HttpElementInfoLoader</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: XMLAElementLoader.java,v 1.24 2009/06/22 12:15:15 PhilippBouillon Exp $
 **/
public class XMLAElementLoader extends ElementLoader {
	private Set <String> elementIds = null;
	private final XMLAClient xmlaClient;
    private final boolean LOAD_ALL_ELEMENTS;
    private final HashMap <Integer, ElementInfo []> depthCache;
	private final XMLAHierarchyInfo hierarchy;
	
    public XMLAElementLoader(XMLAConnection paloConnection, XMLAClient xmlaClient,
			DimensionInfo dimension) {
		super(paloConnection, dimension.getDefaultHierarchy());
		this.xmlaClient = xmlaClient;
		this.depthCache = new HashMap <Integer, ElementInfo[]> ();
		this.hierarchy = null;
		LOAD_ALL_ELEMENTS = true; //!paloConnection.usedByWPalo();
	}

    public XMLAElementLoader(XMLAConnection paloConnection, XMLAClient xmlaClient,
    		HierarchyInfo hierarchy) {
		super(paloConnection, hierarchy);
		this.xmlaClient = xmlaClient;
		this.depthCache = new HashMap <Integer, ElementInfo[]> ();
		this.hierarchy = (XMLAHierarchyInfo) hierarchy;
		LOAD_ALL_ELEMENTS = true; //!paloConnection.usedByWPalo();    	
    }
    
	public String[] getAllElementIds() {
		if (elementIds == null) {
			if (LOAD_ALL_ELEMENTS) {
				loadElements();
			} else {
				loadAllElementIds();
			}
		}
		return elementIds.toArray(new String[0]);
	}

	public final ElementInfo load(int index) {
		String[] elIds = getAllElementIds();
		if (index < 0 || index > elIds.length - 1)
			return null;
		return load(elIds[index]);
	}

	public ElementInfo[] getElementsAtDepth(int depth) {
		if (depthCache.containsKey(depth)) {
			return depthCache.get(depth);
		}
//		if (hierarchy == null) {
//			hierarchy = (XMLAHierarchyInfo) dimension.getActiveHierarchy();
//		}		
		XMLAElementInfo [] elementInfos;
		elementInfos = BuilderRegistry.getInstance().getElementInfoBuilder().
			getElements((XMLAConnection) paloConnection, xmlaClient, 
					((XMLADimensionInfo) hierarchy.getDimension()).getCubeId(),
					hierarchy, depth);
		depthCache.put(depth, elementInfos);
		return elementInfos;
	}

	public ElementInfo loadByName(String name) {		
		if (LOAD_ALL_ELEMENTS) {
			// Has to load _all_ elements for a given dimension if they haven't
			// been loaded, because it's a lot faster to get all elements in _one_
			// request and keep it.

			//first check if we have it loaded already
			ElementInfo element = findElement(name);
			if (element == null) {
				//if not, we have to ask server...
				loadElements();
				
			}
			return findElement(name);			
		} else {
			ElementInfo info;
			String cleanedText = XMLADimensionInfo.transformId(name);
//			if (hierarchy == null) {
//				hierarchy = (XMLAHierarchyInfo) dimension.getActiveHierarchy();
//			}
			if (!loadedInfo.containsKey(cleanedText)) {
				info = BuilderRegistry.getInstance().getElementInfoBuilder().
					getElementInfo((XMLAConnection) paloConnection, xmlaClient, hierarchy, name);
				loaded(info);
			} else {
				info = (ElementInfo) loadedInfo.get(cleanedText);
			}
			return info;
		}
	}
	
	public final ElementInfo[] getChildren(ElementInfo parent) {
		XMLAElementInfo _parent = (XMLAElementInfo)parent;
		String[] children = _parent.getChildren();
		XMLAElementInfo[] elementInfos = null;
//		if (hierarchy == null) {
//			hierarchy = (XMLAHierarchyInfo) dimension.getActiveHierarchy();
//		}
		if (children == null || children.length == 0) {
			if (!_parent.hasChildren())
				elementInfos = new XMLAElementInfo[0];
			else {
				elementInfos = BuilderRegistry.getInstance()
				.getElementInfoBuilder().getChildren(
						(XMLAConnection) paloConnection, xmlaClient,
						((XMLADimensionInfo) hierarchy.getDimension()).getCubeId(),
						(XMLAElementInfo) parent);					
				for (XMLAElementInfo info : elementInfos)
					loaded(info);
				_parent.setChildren(elementInfos);
			}
		} else {
			elementInfos = new XMLAElementInfo[children.length];
			for (int i = 0; i < elementInfos.length; ++i)
				elementInfos[i] = (XMLAElementInfo) load(children[i]);
		}
		return elementInfos;		
	}

	protected final void reload() {
		System.out.println("XMLAElementLoader::reload.");
	}
	
	public final ElementInfo findElement(String name) {
		Collection<PaloInfo> infos = getLoaded();
		String modName = name.replaceAll("\\[", "((");
		modName = modName.replaceAll("\\]", "))");
		modName = modName.replaceAll(":", "**");		
		//System.out.print("Searching " + name);
		for(PaloInfo info : infos) {
			if (info instanceof ElementInfo) {
				ElementInfo elInfo = (ElementInfo) info;				
				if (elInfo.getId().equals(modName)) {					
					//System.out.println(" returned " + elInfo.getId());
					return elInfo;					
				}
					
			}
		}
		// Check for "real" name:
		for(PaloInfo info : infos) {
			if (info instanceof ElementInfo) {
				ElementInfo elInfo = (ElementInfo) info;				
				if (elInfo.getName().equals(modName)) {					
					//System.out.println(" returned " + elInfo.getId());
					return elInfo;					
				}
					
			}
		}
		
		//System.out.println(" returned null.");
		return null;
	}
	
	private void loadAllElementIds() {
		elementIds = new LinkedHashSet <String> ();
		String connectionName = xmlaClient.getConnections()[0].getName();
		
		try {
    	    XMLARestrictions rest = new XMLARestrictions();
    	    XMLAProperties   prop = new XMLAProperties();
   	        prop.setDataSourceInfo(connectionName);
   	        prop.setCatalog(hierarchy.getDimension().getDatabase().getId());
   	    	rest.setCatalog(hierarchy.getDimension().getDatabase().getId());
   	    	String cleanId = XMLADimensionInfo.transformId(hierarchy.getDimension().getId());
   	    	int index = cleanId.indexOf(XMLADimensionLoader.DIMENSION_ID_SEP);
    	    String cubeId = cleanId.substring(0, index);
//    		if (hierarchy == null) {
//    			hierarchy = (XMLAHierarchyInfo) dimension.getActiveHierarchy();
//    		}
   	    	rest.setHierarchyUniqueName(XMLADimensionInfo.transformId(XMLADimensionInfo.getDimIdFromId(hierarchy.getId())));
   	    	rest.setCubeName(cubeId);
   	    	
   	    	Document result = xmlaClient.getMemberList(rest, prop);
	        NodeList nl  = result.getElementsByTagName("row");
	        
	        if (nl == null || nl.getLength() == 0) {
	        	return;
	        }
			for (int i = 0; i < nl.getLength(); i++) {
				NodeList nlRow = nl.item(i).getChildNodes();				
				for (int j = 0; j < nlRow.getLength(); j++) {
					if (nlRow.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Node currentItem = nlRow.item(j);
						String nodeName = currentItem.getNodeName();
						if (nodeName.equals("MEMBER_UNIQUE_NAME")) {
							String text = XMLAClient.getTextFromDOMElement(currentItem);
							String cleanedText = text.replaceAll("\\[", "((");
							cleanedText = cleanedText.replaceAll("\\]", "))");
							cleanedText = cleanedText.replaceAll(":", "**");
							elementIds.add(cleanedText);
						} 
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;		
	}
	
	private final void loadElements() {
		elementIds = new LinkedHashSet <String> ();
		XMLAElementInfo [] elementInfos;
		if (hierarchy == null) {
			System.out.println("Hierarchy == null");
			return;
		}
			elementInfos = BuilderRegistry.getInstance().getElementInfoBuilder().
				getElements((XMLAConnection) paloConnection, xmlaClient, 
						((XMLADimensionInfo) hierarchy.getDimension()).getCubeId(),
						hierarchy);
		XMLAElementLoader otherLoader = null;
		if (hierarchy != null) {
			otherLoader = (XMLAElementLoader)
				((XMLAConnection)  paloConnection).getElementLoader(
					hierarchy.getDimension());
		}
		for (XMLAElementInfo info: elementInfos) {
			elementIds.add(info.getId());
			loaded(info);
			if (otherLoader != null) {
				otherLoader.loaded(info);
			}
		}
	}	
}
