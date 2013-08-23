/*
*
* @file XFilterFactory.java
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
* @version $Id: XFilterFactory.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import org.palo.api.Attribute;
import org.palo.api.subsets.Subset2;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAlias;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubsetType;

/**
 * <code>XFilterFactory</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XFilterFactory.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
class XFilterFactory {

	static final XSubset createX(Subset2 subset) { // , XHierarchy xHierarchy) {
		XSubsetType type = subset.getType() == Subset2.TYPE_GLOBAL ?
									XSubsetType.GLOBAL : XSubsetType.LOCAL;
		XSubset xSubset = new XSubset(subset.getId(), subset.getName(), type);
		// xSubset.setHierarchy(xHierarchy);
		return xSubset;
	}
	static final XAlias createX(Attribute attribute, String hierarchyId) {
		XAlias xAlias = new XAlias(attribute.getId(), attribute.getName());
		xAlias.setHierarchyId(hierarchyId);
		return xAlias;		
	}

}
