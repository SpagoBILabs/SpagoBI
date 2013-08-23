/*
*
* @file AxisHandler.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: AxisHandler.java,v 1.14 2010/02/12 13:51:05 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io.xml;

import org.palo.api.Attribute;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.PaloObject;
import org.palo.api.subsets.Subset2;
import org.palo.api.utils.ElementPath;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.Property;
import org.palo.viewapi.VirtualElement;
import org.palo.viewapi.internal.LocalFilterImpl;
import org.palo.viewapi.internal.VirtualElementImpl;
import org.palo.viewapi.internal.util.XMLUtil;
import org.xml.sax.Attributes;

/**
 * <code>AxisHandler</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisHandler.java,v 1.14 2010/02/12 13:51:05 PhilippBouillon Exp $
 **/
public class AxisHandler implements IXMLHandler {

	public static final String XPATH = "/view/axis";
		
	private static final String AXIS_HIERARCHY = XPATH+"/axis_hierarchy";
	private static final String AXIS_EXPANDED_PATHS = XPATH+"/expanded_paths";
	private static final String AXIS_PROPERTY = XPATH+"/property";
	
	private static final String AXIS_HIERARCHY_SUBSET = AXIS_HIERARCHY + "/subset";
	private static final String AXIS_HIERARCHY_LOCALFILTER = AXIS_HIERARCHY + "/localfilter";
	private static final String AXIS_HIERARCHY_LOCALFILTER_ELEMENT = AXIS_HIERARCHY + "/localfilter/element";
	private static final String AXIS_HIERARCHY_SELECTED_ELEMENTS = AXIS_HIERARCHY + "/selected_elements";	
	private static final String AXIS_HIERARCHY_PROPERTY = AXIS_HIERARCHY + "/property";

	
	//TODO read/write properties...

	private Axis axis;
	private AxisHierarchy axisHierarchy;
	private ElementNode currentElementNode;
	private final CubeView view;
	
	
	public AxisHandler(CubeView view) {
		this.view = view;
	}

	
	public final void enter(String path, Attributes attributes) {
		if(axis == null && path.equals(XPATH)) {
			//required axis attributes:
			String id = attributes.getValue("id");
			if(id == null || id.equals(""))
				throw new PaloAPIException("AxisHandler: no axis id defined!");

			String name = attributes.getValue("name");
			//add axis to view...
			axis = view.addAxis(id, name);
		} else if(path.equals(AXIS_PROPERTY) && axis != null) {
			String propId = attributes.getValue("id");
			String propValue = attributes.getValue("value");
			if(propId != null && propValue != null) {
				Property<String> property = new Property<String>(propId, propValue);
				axis.addProperty(property);
			}
		} else if(path.equals(AXIS_HIERARCHY) && axisHierarchy == null) {
			//required hierarchy and dimension ids
			String dimId = attributes.getValue("dimension_id");
			String hierId = attributes.getValue("hierarchy_id");
			if (dimId == null || dimId.equals("") || hierId == null
					|| hierId.equals(""))
				throw new PaloAPIException(
						"AxisHandler: no valid axis hierarchy defined for axis '"
								+ axis.getName() + "[" + axis.getId() + "]'!");
			Dimension dimension = view.getCube().getDimensionById(dimId);
			if(dimension == null)
				throw new PaloAPIException("AxisHandler: unkown dimension id '"
						+ dimId + "' in view '" + view.getName()
						+ "' of cube '" + view.getCube().getName() + "'!");
			Hierarchy hierarchy = dimension.getHierarchyById(hierId);
			if(hierarchy == null)
				throw new PaloAPIException("AxisHandler: unkown hierarchy id '"
						+ hierId + "' in view '" + view.getName()
						+ "' of cube '" + view.getCube().getName()
						+ "' and dimension '" + dimension.getName() + "'!");

			axisHierarchy = axis.add(hierarchy); //new AxisHierarchyImpl(hierarchy);
		} else if(path.equals(AXIS_HIERARCHY_PROPERTY)) {
			check(axisHierarchy);
			String propId = attributes.getValue("id");
			//currently we only support alias :(
			if (propId.equals(AxisHierarchy.USE_ALIAS)) {
				String aliasId = attributes.getValue("value");
				Attribute alias = axisHierarchy.getHierarchy().getAttribute(
						aliasId);
				if (alias != null) {
					Property<Attribute> aliasProperty = new Property<Attribute>(
							AxisHierarchy.USE_ALIAS, alias);
					axisHierarchy.addProperty(aliasProperty);
				} else {
					axisHierarchy.setAliasMissing(aliasId);
				}
				
			} else {
				Property prop = new Property(propId, attributes.getValue("value"));
				axisHierarchy.addProperty(prop);
			}
		} else if(path.equals(AXIS_HIERARCHY_LOCALFILTER)) {
			check(axisHierarchy);
			LocalFilter localFilter = new LocalFilterImpl();
			axisHierarchy.setLocalFilter(localFilter);
		} else if(path.startsWith(AXIS_HIERARCHY_LOCALFILTER_ELEMENT)) {
			check(axisHierarchy);			
			String elId = attributes.getValue("id");
			String elName = attributes.getValue("name");
			Element element = (elId != null) ? 
					axisHierarchy.getHierarchy().getElementById(elId) : 
					new VirtualElementImpl(elName,axisHierarchy.getHierarchy());
			try {
				ElementNode elNode = new ElementNode(element);
				if (currentElementNode != null)
					currentElementNode.forceAddChild(elNode);
				currentElementNode = elNode;
			} catch (NullPointerException e) {
				// Element has been deleted. Ignore for now...
			}
		}
	}

	public final String getXPath() {
		return XPATH;
	}

	public final void leave(String path, String value) {
		if(axis == null)
			throw new PaloAPIException("AxisHandler: no axis created!");
		value = XMLUtil.dequote(value);
		if(path.equals(AXIS_HIERARCHY_SUBSET)) {
			check(axisHierarchy);
			Subset2 subset=
				axisHierarchy.getHierarchy().getDimension().getSubsetHandler().getSubset(value, Subset2.TYPE_GLOBAL);
			if(subset == null) {
				axisHierarchy.setSubsetMissing(value);
			}
//				throw new PaloAPIException("AxisHandler: unkown subset id '"
//							+ value + "' in view '" + view.getName()
//							+ "' of cube '" + view.getCube().getName() + "'!");				
			axisHierarchy.setSubset(subset);
		} else if(path.equals(AXIS_HIERARCHY_SELECTED_ELEMENTS)) {
			check(axisHierarchy);
			Hierarchy hierarchy = axisHierarchy.getHierarchy();
			//selected elements are separated by ',' per elements 
			String[] elIDs = value.split(",");
			for(int i=0;i<elIDs.length;i++) {
				Element el = hierarchy.getElementById(elIDs[i]);
				if(el==null)
					System.err.println("AxisHandler: unknown element id '"
							+ elIDs[i] + "' in view '" + view.getName()
							+ "' of cube '" + view.getCube().getName() + "'!");
				axisHierarchy.addSelectedElement(el);
			}
		} else if(path.startsWith(AXIS_HIERARCHY_LOCALFILTER_ELEMENT)) {			
			if(currentElementNode != null) {
				check(axisHierarchy);
				LocalFilter localFilter = axisHierarchy.getLocalFilter();
				ElementNode parentNode = currentElementNode.getParent();
				if(parentNode == null)
					localFilter.addVisibleElement(currentElementNode);
				currentElementNode = parentNode; 
			}
		} 
		else if(path.equals(AXIS_EXPANDED_PATHS)) {
			//expanded paths are separated by ',' for elements and ':' for dims
			axis.addExpanded(buildPath(value));
		} else if(path.equals(AXIS_HIERARCHY )) {
			axis.add(axisHierarchy);
			axisHierarchy = null;
		} else if(path.equals(XPATH))
			axis = null;
	}

	public static final String getPersistenceString(Axis axis) {
		StringBuffer xml = new StringBuffer();
		//general
		xml.append("<axis id=\"");
		xml.append(axis.getId());
		xml.append("\" name=\"");
		xml.append(XMLUtil.printQuoted(axis.getName()));
		xml.append("\" >\r\n");
				
		writeAxisHierarchies(xml, axis);
		writeExpandedPaths(xml, axis);
		writeProperties(xml, axis);
		
		xml.append("</axis>\r\n");
		return xml.toString();
	}

	//TODO we currently support only simple properties like String, int,... CHANGE IT!!!
	private static final void writeProperties(StringBuffer xml, Axis axis) {
		for(Property<?> property : axis.getProperties()) {
			xml.append("<property id=\"");
			xml.append(property.getId());
			xml.append("\" value=\"");
			xml.append(property.getValue().toString());
			xml.append("\" />\r\n");
		}
	}
	private final String[] getParts(String txt, String delimiter) {
		return txt.split(delimiter);
	}
	
	private final ElementPath buildPath(String xmlValue) {
		Hierarchy[] hierarchies = axis.getHierarchies();
		String[] elsPerDims = getParts(xmlValue, ":");
		ElementPath elPath = new ElementPath();
		for(int i=0;i<elsPerDims.length;i++) {				
			String[] elIds = getParts(elsPerDims[i], ",");
			Element[] elPart = new Element[elIds.length];
			for(int e=0; e<elIds.length; e++) {
				elPart[e] = hierarchies[i].getElementById(elIds[e]);
			}
			elPath.addPart(hierarchies[i], elPart);				
		}
		return elPath;
	}
	
	private final void check(AxisHierarchy ah) {
		if (ah == null)
			throw new PaloAPIException(
					"AxisHandler: no AxisHierarchy created for view '"
							+ view.getName() + "' of cube '"
							+ view.getCube().getName() + "!");
	}
	
	private static final void writeAxisHierarchies(StringBuffer xml, Axis axis) {
		AxisHierarchy[] hierarchies = axis.getAxisHierarchies();
		for(AxisHierarchy axisHierarchy : hierarchies) {
			writeAxisHierarchy(xml, axisHierarchy);
		}
	}
	
	private static final void writeAxisHierarchy(StringBuffer xml,
			AxisHierarchy axisHierarchy) {
		Hierarchy hierarchy = axisHierarchy.getHierarchy();
		xml.append("<axis_hierarchy dimension_id=\"");
		xml.append(hierarchy.getDimension().getId());
		xml.append("\" hierarchy_id=\"");
		xml.append(hierarchy.getId());
		xml.append("\" >\r\n");
		//subset:
		Subset2 subset = axisHierarchy.getSubset();
		if(subset != null) {
			xml.append("<subset>");
			xml.append(subset.getId());
			xml.append("</subset>\r\n");
		}
		//localfilter:
		writeLocalFilter(xml, axisHierarchy.getLocalFilter());
		//selected elements:
		if(axisHierarchy.hasSelectedElements()) {
			xml.append("<selected_elements>");
			Element[] elements = axisHierarchy.getSelectedElements();
			xml.append(createIdString(elements, ","));			
			xml.append("</selected_elements>\r\n");
		}		
		//write properties: currently we only support alias :(
		Property<?>[] properties = axisHierarchy.getProperties();
		for(Property<?> prop : properties) {
			if (prop.getId().equals(AxisHierarchy.USE_ALIAS)) {
				Attribute attr = (Attribute) prop.getValue();
				if(attr != null) {
					xml.append("<property id=\""); 
					xml.append(prop.getId());xml.append("\"");
					xml.append(" value=\"");xml.append(attr.getId());
					xml.append("\"/>\r\n");
				}
			} else {
				Object o = prop.getValue();
				String propValue = o == null ? "" : o.toString();
				xml.append("<property id=\""); 
				xml.append(prop.getId());xml.append("\"");
				xml.append(" value=\"");xml.append(propValue);
				xml.append("\"/>\r\n");				
			}
		}
		
		xml.append("</axis_hierarchy>\r\n");
	}

	private static final void writeExpandedPaths(StringBuffer xml, Axis axis) {
		ElementPath[] paths = axis.getExpandedPaths();
		if(paths.length > 0) {
			for(ElementPath path : paths) {
				xml.append("<expanded_paths>");			
				writePath(xml, path);
				xml.append("</expanded_paths>\r\n");
			}
			
		}
	}

	private static final String createIdString(PaloObject[] pObjs, String delimiter) {
		StringBuffer str = new StringBuffer();
		int lastObj = pObjs.length - 1;
		for(int i=0;i<pObjs.length; i++) {
			str.append(XMLUtil.printQuoted(pObjs[i].getId()));
			if(i<lastObj)
				str.append(delimiter);
		}
		return str.toString();
	}
	
	private static final void writePath(StringBuffer xml, ElementPath path) {
		Dimension[] dimensions = path.getDimensions();
		int lastDim = dimensions.length - 1;
		for(int i=0;i<dimensions.length; i++) {
			Element[] elements = path.getPart(dimensions[i]);
			xml.append(createIdString(elements, ","));
			if(i<lastDim)
				xml.append(":");
		}
	}
	
	private static final void writeLocalFilter(StringBuffer xml, LocalFilter localFilter) {
		if(localFilter == null)
			return;		
		xml.append("<localfilter>");
		ElementNode[] roots = localFilter.getVisibleElements();
		writeElementHierarchy(xml, roots);
		xml.append("</localfilter>\r\n");
	}
	private static final void writeElementHierarchy(StringBuffer xml, ElementNode[] nodes) {
		for(ElementNode node : nodes) {
			xml.append("<element");			
			Element element = node.getElement();			
			if(!(element instanceof VirtualElement)) {
				xml.append(" id=");
				xml.append(XMLUtil.quote(element.getId()));
			}
			xml.append(" name=");
			xml.append(XMLUtil.quote(element.getName()));
			xml.append(" >");
			if(node.hasChildren())
				writeElementHierarchy(xml, node.getChildren());
			xml.append("</element>");
		}
	}
}
