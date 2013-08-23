/*
*
* @file SubsetBuilder.java
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
* @version $Id: SubsetBuilder.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2006. All rights reserved.
 */
package org.palo.api.impl.subsets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.palo.api.Attribute;
import org.palo.api.Dimension;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.Subset;
import org.palo.api.SubsetState;

/**
 * An internally used builder to create {@link Subset}s. The main usage of this
 * builder is during the restore of persistent subsets. Therefore it provides
 * a setter method for each subset field.
 * 
 * @author ArndHouben
 * @version $Id: SubsetBuilder.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
class SubsetBuilder {
	
	//required fields:
	private String id;	
	private String name;
	private Attribute alias;
	private String activeStateId;
	private Hierarchy srcHierarchy;
	private Set states = new HashSet();
	//optional fields
	private String description; 
	
	
	final void setId(String id) {
		this.id = id;
	}
	
	final void setName(String name) {
		this.name = name;
	}
	
	final void setDescription(String description) {
		this.description = description;
	}
	
	final void setActiveState(String  activeStateId) {
		this.activeStateId = activeStateId;
	}
	
	final void setSourceHierarchy(Hierarchy srcHierarchy) {
		this.srcHierarchy = srcHierarchy;
	}
	
	final void setAlias(Attribute alias)
	{
		this.alias = alias;
	}
	
	final Hierarchy getSourceHierarchy() {
		return srcHierarchy;
	}
	
	final void addState(SubsetState state) {		
		states.add(state);
	}
	
	final Subset createSubset() {
		if (id == null || name == null || activeStateId == null
				|| srcHierarchy == null)
			throw new PaloAPIException(
					"Cannot create subset, insufficient information");
		// create subset:
		Subset subset = new SubsetImpl(id, name, srcHierarchy);
//		Object[] params = new Object[] { id, name, srcDimension };
//		Subset subset = (Subset) SubsetPersistence.getInstance().create(
//				Subset.class, params);
		// reset subset to perform an update:
		((SubsetImpl) subset).reset();

		// add da states:
		for (Iterator it = states.iterator(); it.hasNext();)
			subset.addState((SubsetState) it.next());
		((SubsetImpl) subset).setActiveState(activeStateId);
		if (description != null)
			subset.setDescription(description);
		if (alias != null)
			subset.setAlias(alias);
		return subset;
	}

}
