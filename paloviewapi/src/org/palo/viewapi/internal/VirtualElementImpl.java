/*
*
* @file VirtualElementImpl.java
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
* @version $Id: VirtualElementImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package org.palo.viewapi.internal;

import org.palo.api.Attribute;
import org.palo.api.Consolidation;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.viewapi.VirtualElement;

import com.tensegrity.palojava.ElementInfo;

/**
 * <code>VirtualElementImpl</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: VirtualElementImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class VirtualElementImpl implements VirtualElement {

	private String name;
	private final Hierarchy hierarchy;
	
	
	public VirtualElementImpl(String name, Hierarchy hierarchy) {
		this.name = name;
		this.hierarchy = hierarchy;
	}
	
	public Object getAttributeValue(Attribute attribute) {
		return null;
	}

	public Object[] getAttributeValues() {
		return null;
	}

	public int getChildCount() {
		return 0;
	}

	public Element[] getChildren() {
		return null;
	}

	public Consolidation getConsolidationAt(int index) {
		return null;
	}

	public int getConsolidationCount() {
		return 0;
	}

	public Consolidation[] getConsolidations() {
		return null;
	}

	public int getDepth() {
		return 0;
	}

	public Dimension getDimension() {
		return hierarchy.getDimension();
	}

	public Hierarchy getHierarchy() {
		return hierarchy;
	}

	public int getLevel() {
		return 0;
	}

	public String getName() {
		return name;
	}

	public int getParentCount() {
		return 0;
	}

	public Element[] getParents() {
		return null;
	}

	public int getPosition() {
		return 0;
	}

	public int getType() {
		return ELEMENTTYPE_VIRTUAL;
	}

	public String getTypeAsString() {
		return ELEMENTTYPE_VIRTUAL_STRING;
	}

	public void move(int newPosition) {
	}

	public void rename(String name) {
		this.name = name;
	}

	public void setAttributeValue(Attribute attribute, Object value) {
	}

	public void setAttributeValues(Attribute[] attributes, Object[] values) {
	}

	public void setType(int type) {
	}

	public void updateConsolidations(Consolidation[] consolidations) {
	}

	public String getId() {
		return "VE:"+Integer.toString(hashCode()); 
	}

	public boolean canBeModified() {
		return false;
	}

	public boolean canCreateChildren() {
		return false;
	}

	public ElementInfo getInfo() {		
		return null;
	}

}
