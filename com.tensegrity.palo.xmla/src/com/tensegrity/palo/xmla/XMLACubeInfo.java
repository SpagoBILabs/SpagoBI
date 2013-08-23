/*
*
* @file XMLACubeInfo.java
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
* @version $Id: XMLACubeInfo.java,v 1.14 2009/04/29 10:35:37 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tensegrity.palo.xmla.builders.BuilderRegistry;
import com.tensegrity.palo.xmla.parsers.XMLADimensionRequestor;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.impl.PropertyInfoImpl;

public class XMLACubeInfo implements CubeInfo, XMLAPaloInfo {
	private String name;
	private XMLADatabaseInfo database;
	private XMLAVariableInfo [] variables;
	private int dimensionCount;
	private String [] dimensions = null;
	private BigInteger cellCount;
	private BigInteger filledCellCount;
	private final Map dimensionMap;
	private String id;
	private String connectionName;
	private final XMLAClient xmlaClient;
	private final XMLAConnection xmlaConnection;
	
	public XMLACubeInfo(String name, String id, XMLADatabaseInfo database, String connectionName, XMLAClient xmlaClient, XMLAConnection con) {
		this.name = name;
		this.id = id;
		this.database = database;
		dimensionMap = new LinkedHashMap();
		variables = new XMLAVariableInfo[0];
		this.connectionName = connectionName;
		this.xmlaClient = xmlaClient;
		this.xmlaConnection = con;
	}
	
	public BigInteger getCellCount() {
		return cellCount;
	}

	public DatabaseInfo getDatabase() {
		return database;
	}

	public int getDimensionCount() {		
		return dimensionCount;
	}

	public String[] getDimensions() {
		if (dimensions == null) {
			XMLADimensionRequestor req = new XMLADimensionRequestor(this, xmlaConnection);
			req.setCatalogNameRestriction(getDatabase().getId());
			req.setCubeNameRestriction(getId());
			XMLADimensionInfo [] dims = req.requestDimensions(xmlaClient);
//			ArrayList dims = BuilderRegistry.getInstance().getCubeInfoBuilder().
//				requestDimensions(xmlaClient, connectionName, database, id);
//			setDimensionCount(dims.size());
//			dimensions = (String[]) dims.toArray(new String[0]);
			dimensions = new String[dims.length];
			for (int i = 0; i < dims.length; i++) {
				dimensions[i] = dims[i].getId();
			}
		}
		return dimensions;
	}

	public BigInteger getFilledCellCount() {
		return filledCellCount;
	}

	public final void setDimensionCount(int newDimensionCount) {
		dimensionCount = newDimensionCount;
	}
	
	public final void setDimensions(String [] dimensions) {
		this.dimensions = dimensions;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return STATUS_LOADED;
	}

	public int getToken() {
		return 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getType() {
		return TYPE_NORMAL;
	}
		
	public final void setCellCount(BigInteger newCellCount) {
		cellCount = newCellCount;
	}
	
	public final void setFilledCellCount(BigInteger newCellCount) {
		filledCellCount = newCellCount;
	}
	
	public String toString() {
		return "Cube " + getName() + " [" + getId() + "]: " + 
		       getDatabase().getName() + ", DimensionCount: " + 
		       getDimensionCount() + ", Cells: " + getCellCount(); 
	}	
	
	public void addDimensionInternal(XMLADimensionInfo dim) {
		dimensionMap.put(dim.getName(), dim);
	}
	
	public XMLADimensionInfo getDimensionInternal(String dimName) {
		return (XMLADimensionInfo) dimensionMap.get(dimName);
	}
	
	public XMLADimensionInfo [] getDimensionsInternal() {
		return (XMLADimensionInfo []) dimensionMap.values().
			toArray(new XMLADimensionInfo[0]);
	}
	
	public void setVariables(XMLAVariableInfo [] vars) {
		variables = vars;
	}
	
	private XMLAVariableInfo [] getVariables() {
		return variables;
	}
	
	public String [] getAllKnownPropertyIds(DbConnection con) {
		return new String [] {XMLAConnection.PROPERTY_SAP_VARIABLE_DEFINITION};
	}

	private void addProp(String id, String content, PropertyInfo parent) {
		PropertyInfo property = new PropertyInfoImpl(
				id, content, parent, PropertyInfoImpl.TYPE_STRING, true);
		parent.addChild(property);
	}
	
	private void addElements(PropertyInfo parent, XMLAElementInfo [] elements) {
		if (elements == null) {
			return;
		}
		for (XMLAElementInfo element: elements) {
			PropertyInfo prop = new PropertyInfoImpl(
				element.getId(), element.getName(), parent, PropertyInfoImpl.TYPE_STRING, true);
			addElements(prop, element.getChildrenInternal());
			parent.addChild(prop);
		}		
	}
	
	private void addElementTree(XMLAElementInfo [] elements, PropertyInfo parent) {
		PropertyInfo property = new PropertyInfoImpl(
				XMLAConnection.PROPERTY_SAP_VAR_ELEMENTS,
				"True", parent, PropertyInfoImpl.TYPE_BOOLEAN, true);
		parent.addChild(property);
		for (XMLAElementInfo element: elements) {
			if (element.getParentCount() == 0) {
				PropertyInfo prop = new PropertyInfoImpl(
						element.getId(), element.getName(), property, PropertyInfoImpl.TYPE_STRING, true);
				addElements(prop, element.getChildrenInternal());
				property.addChild(prop);
			}
		}
	}
	
	private void addDefaultSelection(XMLAVariableInfo var, PropertyInfo parent, XMLAElementInfo [] elements) {
		String content = "";
		switch (var.getSelectionType()) {
			case XMLAVariableInfo.VAR_SELECTION_TYPE_VALUE:
			case XMLAVariableInfo.VAR_SELECTION_TYPE_COMPLEX:
				if (elements != null && elements.length > 0) {
					content = elements[0].getId();
				}
				break;
			case XMLAVariableInfo.VAR_SELECTION_TYPE_INTERVAL:
				if (elements != null) {
					if (elements.length > 0) {
						content = elements[0].getId();
						if (elements.length > 1) {
							content += "\n" + elements[1].getId();
						} else {
							content += "\n" + elements[0].getId();
						}
					}
				}
				break;			
		}
		
		PropertyInfo property = new PropertyInfoImpl(
				XMLAConnection.PROPERTY_SAP_VAR_SELECTED_VALUES,
				content, parent, PropertyInfoImpl.TYPE_STRING, false);
		parent.addChild(property);		
	}
	
	public PropertyInfo getProperty(DbConnection con, String id) {
		if (id.equals(XMLAConnection.PROPERTY_SAP_VARIABLE_DEFINITION)) {			
			PropertyInfo info = new PropertyInfoImpl(
					id, "True", null,
					PropertyInfoImpl.TYPE_BOOLEAN, true);
			for (XMLAVariableInfo var: getVariables()) {
				PropertyInfo prop = new PropertyInfoImpl(
						XMLAConnection.PROPERTY_SAP_VARIABLE_INSTANCE,
						"True", info, PropertyInfoImpl.TYPE_BOOLEAN, true);
				info.addChild(prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_ID, var.getId(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_NAME, var.getName(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_UID, var.getUId(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_ORDINAL, var.getOrdinal(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_TYPE, "" + var.getType(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_DATATYPE, var.getDataType(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_CHARMAXLENGTH, var.getCharacterMaximumLength(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_PROCESSINGTYPE, "" + var.getCharacterProcessingType(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_SELECTIONTYPE, "" + var.getSelectionType(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_ENTRYTYPE, "" + var.getInputType(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_REFERENCEDIMENSION, var.getReferenceDimension(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_REFERENCEHIERARCHY, var.getReferenceHierarchy(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_DEFAULTLOW, var.getDefaultLow(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_DEFAULTLOWCAP, var.getDefaultLowCap(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_DEFAULTHIGH, var.getDefaultHigh(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_DEFAULTHIGHCAP, var.getDefaultHighCap(), prop);
				addProp(XMLAConnection.PROPERTY_SAP_VAR_DESCRIPTION, var.getDescription(), prop);

				var.loadVariableElements(xmlaClient, (XMLAConnection) con, database);
				XMLADimensionInfo dimInfo = (XMLADimensionInfo) var.getElementDimension();
				if (dimInfo != null) {
					XMLAElementInfo [] elements = dimInfo.getMembersInternal();
					addElementTree(elements, prop);
					addDefaultSelection(var, prop, elements);
				} else {
					addDefaultSelection(var, prop, null);
				}
			}
			return info;
			
		}
		return null;
	}

	public boolean canBeModified() {
		return true;
	}

	public boolean canCreateChildren() {
		return true;
	}
}
