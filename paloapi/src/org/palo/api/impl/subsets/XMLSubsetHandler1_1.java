/*
*
* @file XMLSubsetHandler1_1.java
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
* @version $Id: XMLSubsetHandler1_1.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.subsets;

import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.xml.sax.Attributes;

import com.tensegrity.palojava.PaloException;

/**
 * <code>XMLSubsetHandler1_1</code>
 * Defines <code>{@link IPaloEndHandler}</code>s and 
 * <code>{@link IPaloEndHandler}</code>s to read subsets which are stored using
 * version 1.1
 *
 * @author ArndHouben
 * @version $Id: XMLSubsetHandler1_1.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
class XMLSubsetHandler1_1 extends XMLSubsetHandler1_0 {

	XMLSubsetHandler1_1(Database database) {
		super(database);
	}

	IPaloStartHandler[] getStartHandlers(final Database database) {
		IPaloStartHandler[] oldStartHandlers = super.getStartHandlers(database);
		int oldLength = oldStartHandlers.length;
		IPaloStartHandler[] newStartHandlers = new IPaloStartHandler[oldLength + 2];
		System.arraycopy(oldStartHandlers, 0, newStartHandlers, 0, oldLength);
		newStartHandlers[oldLength] = new IPaloStartHandler() {
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
				Dimension srcDim = database.getDimensionById(srcDimensionId);
				if (srcDim == null)
					throw new PaloAPIException("Cannot find source dimension '"
							+ srcDimensionId + "'!!");
				Hierarchy srcHier = srcDim.getDefaultHierarchy();
				try {
					// can not save attributes in legacy mode
					
					if (!srcDim.getDatabase().getConnection().isLegacy())
						subsetBuilder.setAlias(getAttributeByName(srcHier,
								attributes.getValue("alias")));
				} catch (PaloException pe) {
					System.err
							.println("SubsetReader: cannot read attributes - "
									+ pe.getMessage());
				}
				subsetBuilder.setSourceHierarchy(srcHier);
			}
		};
		newStartHandlers[oldLength+1] = new IPaloStartHandler() {

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
				Element element = srcHier.getElementById(elementId);
				if(element != null) {
					stateBuilder.addElement(element);
					stateBuilder.setPaths(element, paths);
					stateBuilder.setPositions(element, positions);
				}
			}
		};
		return newStartHandlers;
	}
}
