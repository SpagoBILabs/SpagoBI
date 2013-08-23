/*
*
* @file XMLSubsetHandler1_0.java
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
* @version $Id: XMLSubsetHandler1_0.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.subsets;

import org.palo.api.Attribute;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.SubsetState;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.xml.sax.Attributes;

import com.tensegrity.palojava.PaloException;

/**
 * <code>XMLSubsetHandler1_0</code>
 * Defines <code>{@link IPaloEndHandler}</code>s and 
 * <code>{@link IPaloEndHandler}</code>s to read subsets which are stored using
 * version 1.0
 *
 * @author ArndHouben
 * @version $Id: XMLSubsetHandler1_0.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
class XMLSubsetHandler1_0 extends XMLSubsetHandler {

	XMLSubsetHandler1_0(Database database) {
		super(database);
	}

	IPaloStartHandler[] getStartHandlers(final Database database) {
		return new IPaloStartHandler[] { new IPaloStartHandler() {
			public String getPath() {
				return "subset";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				subsetBuilder = new SubsetBuilder();
				subsetBuilder.setId(attributes.getValue("id")); //$NON-NLS-1$
				subsetBuilder.setName(attributes.getValue("name"));
				subsetBuilder
						.setDescription(attributes.getValue("description")); //$NON-NLS-1$
				subsetBuilder.setActiveState(attributes
						.getValue("activeStateId")); //$NON-NLS-1$
				String srcDimensionId = attributes
						.getValue("sourceDimensionId"); //$NON-NLS-1$
				Dimension srcDim = database.getDimensionByName(srcDimensionId);			
				if (srcDim == null)
					throw new PaloAPIException("Cannot find source dimension '"
							+ srcDimensionId + "'!!");
				try {
					// can not save attributes in legacy mode
					Hierarchy srcHier = srcDim.getDefaultHierarchy();
					if (!srcDim.getDatabase().getConnection().isLegacy())
						subsetBuilder.setAlias(getAttributeByName(srcHier,
								attributes.getValue("alias")));
				} catch (PaloException pe) {
					System.err
							.println("SubsetReader: cannot read attributes - "
									+ pe.getMessage());
				}
				subsetBuilder.setSourceHierarchy(srcDim.getDefaultHierarchy());
			}
		}, new IPaloStartHandler() {
			public String getPath() {
				return "subset/state";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				// get a fresh builder
				stateBuilder = new SubsetStateBuilder();
				stateBuilder.setId(attributes.getValue("id"));
				stateBuilder.setName(attributes.getValue("name"));
			}
		}, new IPaloStartHandler() {
			public String getPath() {
				return "subset/state/expression";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				if (stateBuilder == null) {
					throw new PaloAPIException(
							"Cannot create SubsetState in node description");
				}
				stateBuilder.setExpression(attributes.getValue("expr"));
			}
		}, new IPaloStartHandler() {
			public String getPath() {
				return "subset/state/search";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				if (stateBuilder == null) {
					throw new PaloAPIException(
							"Cannot create SubsetState in node description");
				}
				Attribute attr = getAttributeByName(subsetBuilder
						.getSourceHierarchy(), attributes.getValue("attribute"));
				if (attr != null)
					stateBuilder.setSearchAttribute(attr);
			}
		}, new IPaloStartHandler() {
			public String getPath() {
				return "subset/state/element";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				if (stateBuilder == null || subsetBuilder == null) {
					throw new PaloAPIException(
							"Cannot create SubsetState in node element");
				}
				String elementId = attributes.getValue("id");
				String paths = attributes.getValue("paths");
				String positions = attributes.getValue("pos");
				Hierarchy srcHier = subsetBuilder.getSourceHierarchy();
				Element element = srcHier.getElementByName(elementId);
				stateBuilder.addElement(element);
				stateBuilder.setPaths(element, paths);
				stateBuilder.setPositions(element, positions);
			}
		} };
	}

	IPaloEndHandler[] getEndHandlers(Database database) {
		return new IPaloEndHandler[] { new IPaloEndHandler() {
			public String getPath() {
				return "subset/state";
			}

			public void endElement(String uri, String localName, String qName) {
				if (subsetBuilder == null || stateBuilder == null) {
					throw new PaloAPIException("Cannot create subset state!!");
				}
				SubsetState state = stateBuilder.createState();
				subsetBuilder.addState(state);
				stateBuilder = null;
			}

		} };
	}


	protected Attribute getAttributeByName(Hierarchy srcHier, String value) {
		if (srcHier == null || srcHier.getDimension().getDatabase().getConnection().isLegacy())
			return null;
		Attribute[] attrs = srcHier.getAttributes();
		for (int i = 0; i < attrs.length; i++) {
			if (attrs[i].getName().equals(value)) {
				return attrs[i];
			}
		}
		return null;
	}
}
