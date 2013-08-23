/*
*
* @file XMLADimensionInfo.java
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
* @version $Id: XMLADimensionInfo.java,v 1.20 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tensegrity.palo.xmla.loader.XMLADimensionLoader;
import com.tensegrity.palo.xmla.parsers.XMLAHierarchyRequestor;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.PropertyInfo;

public class XMLADimensionInfo implements DimensionInfo, XMLAPaloInfo {
	public static final int XMLA_TYPE_NORMAL   = 0;
	public static final int XMLA_TYPE_MEASURES = 1;
	
	private String name;
	private XMLADatabaseInfo database;
	private int elementCount = 0;
	private int maxDepth = 0;
	private int maxLevel = 0;
	private String id;
	private String hierarchyUniqueName;
	private String dimensionUniqueName;
	private String dimensionCaption;
	private String hierarchyCaption;	
	//private XMLACubeInfo cube;
	private String cubeId;
	private final Map elements;
	private final Map elementIds;
	private int xmlaType;
	private final List <XMLAHierarchyInfo> hierarchies;
	private int internalType = 0;
	private String defaultElementName;
	private XMLAHierarchyInfo activeHierarchy = null;
	private XMLAHierarchyInfo defaultHierarchy = null;
	private int hierarchyCount = 0;
	private final XMLAClient xmlaClient;
	private final XMLAConnection connection;
	
	public XMLADimensionInfo(XMLAClient xmlaClient, String dimensionName, String dimensionId, XMLADatabaseInfo database, String cubeId, XMLAConnection connection) {		
		this.name = dimensionName;
		this.connection = connection;
		this.database = database;
		this.cubeId = cubeId;
		if (cubeId != null) {
			this.id = getIDString(dimensionId, cubeId);
		} else {
			this.id = getIDString(dimensionId, "");
		}
		elements = new LinkedHashMap();
		elementIds = new LinkedHashMap();
		hierarchies = new ArrayList<XMLAHierarchyInfo>();
		defaultElementName = "";
		this.xmlaClient = xmlaClient;
	}
		
	public String getAttributeCube() {
		return "";
	}

	public String getAttributeDimension() {
		return "";
	}

	public String getDefaultElementName() {
		return defaultElementName;
	}
	
	public void setDefaultElementName(String name) {
		defaultElementName = name;
	}
	
	public DatabaseInfo getDatabase() {
		return database;
	}

	public int getElementCount() {
		return getDefaultHierarchy().getElementCount();
	}
	
	public void setElementCount(int newElementCount) {
		elementCount = newElementCount;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}

	public int getMaxIndent() {
//		 TODO Auto-generated method stub
		return 0;
	}

	public int getMaxLevel() {		
		return maxLevel;
	}

	public void setMaxLevel(int newMaxLevel) {
		maxLevel = newMaxLevel;
	}
	
	public void setMaxDepth(int newMaxDepth) {
		maxDepth = newMaxDepth;
	}
	
	public String getName() {
		return name;
	}

	public String getRightsCube() {
		return "";
	}

	public int getToken() {
		return 0;
	}

	public void setName(String name) {
		this.name = name;		
	}

	public String getId() {
		return id;
	}
	
	public void setId(String newNameForId) {		
		if (cubeId != null) {
			this.id = getIDString(newNameForId, cubeId);
		} else {
			this.id = getIDString(newNameForId, "");
		}
	}

	public int getType() {
		return TYPE_NORMAL;
	}
	
	public String getHierarchyUniqueName() {
		return hierarchyUniqueName;
	}
	
	public String getDimensionUniqueName() {
		return dimensionUniqueName;
	}
	
	public void setHierarchyUniqueName(String newName) {
		hierarchyUniqueName = newName;
	}
	
	public void setDimensionUniqueName(String newName) {
		dimensionUniqueName = newName;
	}
	
	public void setCubeId(String cubeId) {
		this.cubeId = cubeId;
	}
	
	public String getCubeId() {
		return cubeId;
	}
	
	public String toString() {
		return "Dimension " + getName() + " [" + getId() + "]: " + getElementCount() + " elements.";
	}
	
	public void clearMembersInternal() {
		elements.clear();
		elementIds.clear();
	}
	
	public void addMemberInternal(XMLAElementInfo element) {
		elements.put(element.getUniqueName(), element);
		elementIds.put(//"" + element.getUniqueName().hashCode(), element);
				element.getId(), element);
	}
	
	public int getMemberCountInternal() {
		return elements.size();
	}
	
	public XMLAElementInfo getMemberInternal(String elementName) {
		if (elements.get(elementName) == null) {
			String ee = transformId(elementName);
			return (XMLAElementInfo) elements.get(ee);
		}
		return (XMLAElementInfo) elements.get(elementName);
	}

	public XMLAElementInfo getMemberByIdInternal(String elementId) {
		if (elementIds.get(elementId) == null) {
			String ee = transformId(elementId);
			return (XMLAElementInfo) elementIds.get(ee);
		}
		return (XMLAElementInfo) elementIds.get(elementId);
	}
	
	public XMLAElementInfo [] getMembersInternal() {
		return (XMLAElementInfo []) elements.values().toArray(new XMLAElementInfo[0]);
	}
	
	public static String getIDString(String text, String cubeName) {
		String cleanedCubeName = cubeName.replaceAll("\\[", "((");
		cleanedCubeName = cleanedCubeName.replaceAll("\\]", "))");
		cleanedCubeName = cleanedCubeName.replaceAll(":", "**");
		String cleanedText = text.replaceAll("\\[", "((");
		cleanedText = cleanedText.replaceAll("\\]", "))");
		cleanedText = cleanedText.replaceAll(":", "**");
		cleanedText = cleanedText.replaceAll(",", "(comma)");
		//System.out.println(cleanedCubeName + "." + cleanedText);
		return cleanedCubeName + XMLADimensionLoader.DIMENSION_ID_SEP + cleanedText;
//		String idString;
//		//if (type == XMLA_TYPE_MEASURES) {
//			idString = "" + (cubeName + text.hashCode()).hashCode();	
//		//} else {
//		//	idString = "" + text.hashCode();
//		//}		
//		return idString;
	}
		
	public static String getCubeNameFromId(String id) {
		int index = id.indexOf(XMLADimensionLoader.DIMENSION_ID_SEP);
		if (index == -1) {
			return id;
		}
		String cubeName = id.substring(0, index).trim();
		cubeName = cubeName.replaceAll("\\*\\*", ":");
		cubeName = cubeName.replaceAll("\\)\\)", "]");
		cubeName = cubeName.replaceAll("\\(\\(", "[");
		return cubeName;
	}
	
	public static String getDimIdFromId(String id) {
		int index = id.indexOf(XMLADimensionLoader.DIMENSION_ID_SEP);
		if (index == -1) {
			return id;
		}
		String dimensionId = id.substring(index + XMLADimensionLoader.DIMENSION_ID_SEP.length()).trim();
		dimensionId = dimensionId.replaceAll("\\*\\*", ":");
		dimensionId = dimensionId.replaceAll("\\)\\)", "]");
		dimensionId = dimensionId.replaceAll("\\(\\(", "[");
		dimensionId = dimensionId.replaceAll("\\(comma\\)", ",");
		return dimensionId;
		
	}
	
	public static String transformId(String id) {
		String res = id.replaceAll("\\(\\(", "[");
		res = res.replaceAll("\\)\\)", "]");
		res = res.replaceAll("\\*\\*", ":");
		res = res.replaceAll("\\(comma\\)", ",");
		return res;
	}
	
	public void setXmlaType(int newXmlaType) {
		xmlaType = newXmlaType;
		setId(getHierarchyUniqueName());
	}
	
	public int getXmlaType() {
		return xmlaType;
	}
	
	public void setHierarchyCaption(String newCaption) {
		hierarchyCaption = newCaption;
	}
	
	public String getHierarchyCaption() {
		return hierarchyCaption;
	}
	
	public void setDimensionCaption(String newCaption) {
		dimensionCaption = newCaption;
	}
	
	public String getDimensionCaption() {
		return dimensionCaption;
	}
	
	public void addHierarchy(XMLAHierarchyInfo hier) {
		hierarchies.add(hier);
	}
	
	public XMLAHierarchyInfo [] getHierarchies() {
		if (hierarchies.size() == 0) {
			XMLAHierarchyRequestor req = new XMLAHierarchyRequestor(this, (XMLADatabaseInfo) getDatabase(), connection);
			req.setCubeNameRestriction(getCubeId());
			req.setCatalogNameRestriction(getDatabase().getId());
			req.setDimensionUniqueNameRestriction(getDimensionUniqueName());
			hierarchies.addAll(Arrays.asList(req.requestHierarchies(xmlaClient)));
		}
//		if (connection.usedByWPalo()) {
//			return new XMLAHierarchyInfo [] {getDefaultHierarchy()};
//		}
		return hierarchies.toArray(new XMLAHierarchyInfo [0]);
	}
	
	public String[] getAllKnownPropertyIds(DbConnection con) {
		return new String[0];
	}

	public PropertyInfo getProperty(DbConnection con, String id) {
		return null;
	}
	
	public int getInternalXmlaType() {
		return internalType;
	}
	
	public void setInternalXmlaType(int newType) {
		internalType = newType;
	}

	public boolean canBeModified() {
		return false;
	}

	public boolean canCreateChildren() {
		return false;
	}

	public void setDefaultHierarchy(XMLAHierarchyInfo defaultHier) {
		defaultHierarchy = defaultHier;
	}
	
	public XMLAHierarchyInfo getDefaultHierarchy() {
		return defaultHierarchy;		
	}
	
	public HierarchyInfo getActiveHierarchy() {
		if (activeHierarchy == null) {
			activeHierarchy = defaultHierarchy;
		}
		return activeHierarchy;
	}

	public void setActiveHierarchy(HierarchyInfo newActive) {
		activeHierarchy = (XMLAHierarchyInfo) newActive;
	}
	
	public int getHierarchyCount() {
//		if (connection.usedByWPalo()) {
//			return 1;
//		}
		return hierarchyCount;
	}
	
	public void setHierarchyCount(int hierCount) {
		hierarchyCount = hierCount;
	}
}
