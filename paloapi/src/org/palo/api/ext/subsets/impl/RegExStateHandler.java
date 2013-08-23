/*
*
* @file RegExStateHandler.java
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
* @version $Id: RegExStateHandler.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.subsets.impl;

import org.palo.api.Dimension;
import org.palo.api.DimensionFilter;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.HierarchyFilter;
import org.palo.api.ext.subsets.states.RegExState;

/**
 * The <code>RegExStateHandler</code> is a default implementation to handle
 * the {@link RegExState} which uses a regular expression to determine the
 * visible elements.
 * 
 * @author ArndHouben
 * @version $Id: RegExStateHandler.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
class RegExStateHandler extends AbstractStateHandler {

	public final boolean isFlat() {
		return false;
	}

	public final DimensionFilter createDimensionFilter(Dimension dimension) {
		return new DimensionFilter() {
			public void init(Dimension dimension) {
			}

			public boolean acceptElement(Element element) {
				if (subsetState.getSearchAttribute() != null) {
					return element.getAttributeValue(
							subsetState.getSearchAttribute()).toString()
							.matches(subsetState.getExpression());
				} else {
					return element.getName().matches(
							subsetState.getExpression());
				}
			}

			public boolean isFlat() {
				return false;
			}

			public ElementNode[] postprocessRootNodes(ElementNode[] rootNodes) {
				return null;
			}
		};
	}

	public HierarchyFilter createHierarchyFilter(Hierarchy hierarchy) {
		return new HierarchyFilter() {
			public void init(Hierarchy hierarchy) {
			}

			public boolean acceptElement(Element element) {
				if (subsetState.getSearchAttribute() != null) {
					return element.getAttributeValue(
							subsetState.getSearchAttribute()).toString()
							.matches(subsetState.getExpression());
				} else {
					return element.getName().matches(
							subsetState.getExpression());
				}
			}

			public boolean isFlat() {
				return false;
			}

			public ElementNode[] postprocessRootNodes(ElementNode[] rootNodes) {
				return null;
			}
		};
	}

}
