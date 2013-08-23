/*
*
* @file CubeViewHandler1_0.java
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
* @version $Id: CubeViewHandler1_0.java,v 1.10 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.views;

import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.Subset;
import org.palo.api.impl.xml.EndHandler;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.impl.xml.StartHandler;
import org.palo.api.impl.xml.XMLUtil;
import org.palo.api.persistence.PersistenceError;
import org.xml.sax.Attributes;


/**
 * <code>CubeViewHandler1_0</code>
 * Defines <code>{@link StartHandler}</code>s and 
 * <code>{@link EndHandler}</code>s to read cube views which are stored using
 * version 1.0
 *
 * @author ArndHouben
 * @version $Id: CubeViewHandler1_0.java,v 1.10 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class CubeViewHandler1_0 extends CubeViewHandler {
    
    CubeViewHandler1_0(Database database) {
    	super(database);
    }
    
    protected void registerEndHandlers() {
		registerEndHandler(new IPaloEndHandler() {
			public String getPath() {
				return "view/axis";
			}

			public void endElement(String uri, String localName, String qName) {
				currAxis = null;
			}
		});
	}
    protected void registerStartHandlers() {
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				CubeViewBuilder viewBuilder = new CubeViewBuilder();
				viewBuilder.setId(attributes.getValue("id"));
				viewBuilder.setName(attributes.getValue("name"));
				viewBuilder.setDescription(XMLUtil.dequoteString(attributes
						.getValue("description")));
				String str = attributes.getValue("hideempty");
				if (str != null && str.equalsIgnoreCase("true"))
					viewBuilder.addProperty(CubeView.PROPERTY_ID_HIDE_EMPTY,
							Boolean.toString(true));
				String cubeId = attributes.getValue("cube");
				if (cubeId != null)
					viewBuilder.setCube(database.getCubeByName(cubeId));
				cubeView = viewBuilder.createView(CubeViewHandler1_0.this);
				//we abort loading if creation failed...
				if(cubeView == null)
					throw new PaloAPIException("CubeView creation failed!");
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String id = attributes.getValue("id");
				String name = attributes.getValue("name");
				if (id == null) {
					addError("CubeViewReader: missing id attribute for axis",
							cubeView.getId(), cubeView, null, null,
							PersistenceError.UNKNOWN_AXIS,
							null, PersistenceError.TARGET_GENERAL);
				}
				currAxis = cubeView.getAxis(id);
				if (currAxis == null)
					currAxis = cubeView.addAxis(id, name);
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/dimension";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String dimId = attributes.getValue("id");
				dimId = CubeViewReader.getLeafName(dimId);
				Dimension dim = database.getDimensionByName(dimId);
				if (dim == null) {
					addError("CubeViewReader: unknown dimension id '" + dimId
							+ "'!!", cubeView.getId(), cubeView, database,
							dimId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_GENERAL);
				}
				currAxis.add(dim);
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/selected";
			}
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String element = attributes.getValue("element");
				String dimId = attributes.getValue("dimension");
				String hierId = attributes.getValue("hierarchy");
				dimId = CubeViewReader.getLeafName(dimId);
				Dimension dim = database.getDimensionByName(dimId);
				if (dim == null) {
					addError("CubeViewReader: unknown dimension id '" + dimId
							+ "'!!", cubeView.getId(), cubeView, database,
							dimId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_GENERAL);
				}
				Hierarchy hier;
				if (hierId == null) {
					hier = dim.getDefaultHierarchy();
				} else {
					hier = dim.getHierarchyById(hierId);
				}
				if (hier == null) {
					addError("CubeViewReader: unknown hierarchy id '" + hierId
							+ "'!!", cubeView.getId(), cubeView, database,
							hierId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_GENERAL);
				}
				Element selected = hier.getElementByName(element);
				if (selected == null) {
					addError("CubeViewReader: unknown element id '" + element
							+ "'!!", cubeView.getId(), cubeView, dim, element,
							PersistenceError.UNKNOWN_ELEMENT,
							currAxis, PersistenceError.TARGET_SELECTED);
				}
				currAxis.setSelectedElement(dim, selected);
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/active";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String subset = attributes.getValue("subset");
				String dimId = attributes.getValue("dimension");
				dimId = CubeViewReader.getLeafName(dimId);
				Dimension dim = database.getDimensionByName(dimId);
				if (dim == null) {
					addError("CubeViewReader: unknown dimension id '" + dimId
							+ "'!!", cubeView.getId(), cubeView, database,
							dimId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_GENERAL);
				}
				Subset activeSub = dim.getSubset(subset);
				if (activeSub == null) {
					addError("CubeViewReader: unknown subset id '" + subset
							+ "'!!", cubeView.getId(), cubeView, dim, subset,
							PersistenceError.UNKNOWN_SUBSET,
							currAxis, PersistenceError.TARGET_SUBSET);
				}
				currAxis.setActiveSubset(dim, activeSub);
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/expanded";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String path = attributes.getValue("path");
				String dimId = attributes.getValue("dimension");
				String hierId = attributes.getValue("hierarchy");
				String reps = attributes.getValue("repetitions");
				dimId = CubeViewReader.getLeafName(dimId);
				Dimension dim = database.getDimensionByName(dimId);
				int[] repetitions = CubeViewReader.getRepetitions(reps);
				if (dim == null) {
					addError("CubeViewReader: unknown dimension id '" + dimId
							+ "'!!", cubeView.getId(), cubeView, database,
							dimId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_EXPANDED_PATH);
				}
				Hierarchy hier;
				if (hierId == null) {
					hier = dim.getDefaultHierarchy();
				} else {
					hier = dim.getHierarchyById(hierId);
				}
				Element[] expPath = CubeViewHandler1_0.this.getPath(path, hier,
						currAxis, PersistenceError.TARGET_EXPANDED_PATH);
				for (int i = 0; i < repetitions.length; ++i)
					currAxis.addExpanded(dim, expPath, repetitions[i]);
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/hidden";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String path = attributes.getValue("path");
				String dimId = attributes.getValue("dimension");
				String hierId = attributes.getValue("hierarchy");
				dimId = CubeViewReader.getLeafName(dimId);
				Dimension dim = database.getDimensionByName(dimId);
				if (dim == null) {
					addError("CubeViewReader: unknown dimension id '" + dimId
							+ "'!!", cubeView.getId(), cubeView, database,
							dimId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_HIDDEN_PATH);
				}
				Hierarchy hier;
				if (hierId == null) {
					hier = dim.getDefaultHierarchy();
				} else {
					hier = dim.getHierarchyById(hierId);
				}
				Element[] hiddenPath = CubeViewHandler1_0.this.getPath(path,
						hier,currAxis,PersistenceError.TARGET_HIDDEN_PATH);
				currAxis.addHidden(dim, hiddenPath);
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/property";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String propId = attributes.getValue("id");
				String propVal = attributes.getValue("value");
				currAxis.addProperty(propId, propVal);
			}
		});
    }
}
