/*
*
* @file AttributeImpl.java
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
* @version $Id: AttributeImpl.java,v 1.19 2009/09/22 09:43:17 PhilippBouillon Exp $
*
*/

package org.palo.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.palo.api.Attribute;
import org.palo.api.ConnectionEvent;
import org.palo.api.Consolidation;
import org.palo.api.Cube;
import org.palo.api.Dimension;
import org.palo.api.Hierarchy;
//import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.PaloAPIException;

class AttributeImpl implements Attribute {
	
    //-------------------------------------------------------------------------
    // FACTORY
	//
//    static Attribute getInstance(
//    		ConnectionImpl connection, 
//    		Element attrElement, 
//    		Cube attrCube) {
//		Map cache = connection.getCache(AttributeImpl.class);
//
//		AttributeImpl attribute = new AttributeImpl(attrElement,attrCube);
//		CompoundKey k = attribute.createKey();
//		AttributeImpl cached;
//		if ((cached = (AttributeImpl) cache.get(k)) != null) {
//			return cached;
//		}
//		cache.put(k, attribute);
//		return attribute;
//	}
	final static Attribute create(Element attrElement, Cube attrCube) {
		return new AttributeImpl(attrElement, attrCube);
	}

//	private final String id;
	private final Cube attrCube;
	private final Element[] coordinates = new Element[2];
	
	private AttributeImpl(Element attrElement, Cube attrCube) {
//		this.id = id;
		this.attrCube = attrCube;
		coordinates[0] = attrElement;
//        //workaround for attributes 'til we have IDs
//		if(attrElement instanceof ElementImpl)
//			((ElementImpl)attrElement).setId(id);
	}
	
	//--------------------------------------------------------------------------
	// INTERNAL API
	//
	final Element getAttributeElement() {
		return coordinates[0];
	}
	
	//--------------------------------------------------------------------------
	// PUBLIC API
	//
	public final void setChildren(Attribute[] attributes) {
		if(attributes == null) {
			attributes = new Attribute[0];
		}
		//create the consolidation hierarchy:
		Element attrElement = coordinates[0];
		Hierarchy attrHier = attrElement.getHierarchy();
		Consolidation[] consolidations = new Consolidation[attributes.length];
		for(int i=0;i<consolidations.length;++i) {
			Element attrChild = 
				((AttributeImpl)attributes[i]).getAttributeElement();
			consolidations[i] =
				attrHier.newConsolidation(attrChild, attrElement, 1);
		}
		attrElement.updateConsolidations(consolidations);
		attrElement.getDimension().reload(false);
	}

	public final void removeChildren(Attribute[] attributes) {
		if(attributes == null)
			return;
		List removeChildren = new ArrayList(Arrays.asList(attributes));
		List children = new ArrayList(Arrays.asList(getChildren()));
		if(children.removeAll(removeChildren)) {
			Attribute[] newChildren = 
				(Attribute[])children.toArray(new Attribute[children.size()]); 
			setChildren(newChildren);
		}
	}
	
	
	public final Attribute[] getChildren() {
		Element attrElement = coordinates[0];
		//get attribute hierarchy
		HierarchyImpl attrHier = (HierarchyImpl)attrElement.getHierarchy();
		//get corresponding source hierarchy 
		//(note: this is the attribute hierarchy of the attribute hierarchy ;) ):
		HierarchyImpl srcHier = (HierarchyImpl)attrHier.getAttributeHierarchy(); 
		if(srcHier == null)
			return new Attribute[0];
		Element[] childElements = attrElement.getChildren();
		Attribute[] attrChildren = new Attribute[childElements.length];
		for(int i=0;i<childElements.length;++i) {
			attrChildren[i] = srcHier.getAttribute(childElements[i]);
		}
		//TODO should we clone here?
		return attrChildren;
	}

	public final String getId() {
		return coordinates[0].getId();
	}

	public final String getName() {
		return getAttributeElement().getName();
	}

	public final void setName(String name) {
		((ElementImpl) getAttributeElement()).renameInternal(name, false);
		// event:
		ConnectionImpl connection = (ConnectionImpl) attrCube.getDatabase()
				.getConnection();
		((ConnectionImpl) connection).fireEvent(new ConnectionEvent(connection,
				this, ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_CHANGED,
				new Attribute[] { this }));
	}
	
	public final Attribute[] getParents() {
		DimensionImpl sysDim = (DimensionImpl)coordinates[0].getDimension();
		String srcDim = PaloObjects.getLeafName(sysDim.getName());
		Dimension attrDim = 
			(DimensionImpl) sysDim.getDatabase().getDimensionByName(srcDim);
		HierarchyImpl attrHier = (HierarchyImpl) attrDim.getDefaultHierarchy(); 
		Element[] parents = coordinates[0].getParents();
		int parentCount = parents == null ? 0 : parents.length;
		Attribute[] attrParents = new Attribute[parentCount]; 
		for(int i=0;i<parentCount;++i) {
			attrParents[i] = attrHier.getAttribute(parents[i]);
		}
		return attrParents;
//
//		HierarchyImpl sysHier = (HierarchyImpl)coordinates[0].getHierarchy();
//		String srcHier = PaloObjects.getLeafName(sysHier.getName());
//		HierarchyImpl attrHier = (HierarchyImpl) sysHier.getDimension().
//			getHierarchyByName(srcHier);
//		Element[] parents = coordinates[0].getParents();
//		int parentCount = parents == null ? 0 : parents.length;
//		Attribute[] attrParents = new Attribute[parentCount]; 
//		for(int i=0;i<parentCount;++i) {
//			attrParents[i] = attrHier.getAttribute(parents[i]);
//		}
//		return attrParents;
	}

	public final Object getValue(Element element) {
//TODO we can cache the value: init()-> read value, setValue()->store new value		
		coordinates[1] = element;
		return attrCube.getData(coordinates);
	}

	public final boolean hasChildren() {
		return coordinates[0].getChildCount() > 0;
	}

	public final void setValue(Element element, Object value) {
		coordinates[1] = element;
		attrCube.setData(coordinates, value);
		//event:
		ConnectionImpl connection = 
			(ConnectionImpl) attrCube.getDatabase().getConnection();
		((ConnectionImpl) connection).fireEvent(new ConnectionEvent(
				connection, this,
				ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_CHANGED,
				new Attribute[]{this}));
	}

	public final void setValues(Element[] elements, Object[] values) {
		if(elements.length != values.length)
			throw new PaloAPIException("The number of elements and values has to be equal!");
		Element[][] coords = new Element[elements.length][];
		for(int i=0;i<coords.length;++i) {
			coords[i] = new Element[2];
			coords[i][0] = coordinates[0];
			coords[i][1] = elements[i];
		}
		attrCube.setDataArray(coords, values, Cube.SPLASHMODE_DISABLED);
		//event:
		ConnectionImpl connection = 
			(ConnectionImpl) attrCube.getDatabase().getConnection();
		((ConnectionImpl) connection).fireEvent(new ConnectionEvent(
				connection, this,
				ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_CHANGED,
				new Attribute[]{this}));
	}
	
	public final Object[] getValues(Element[] elements) {
		Element[][] coords = new Element[elements.length][];
		for(int i=0;i<coords.length;++i) {
			coords[i] = new Element[2];
			coords[i][0] = coordinates[0];
			coords[i][1] = elements[i];
		}
		return ((CubeImpl)attrCube).getDataBulk(coords);
	}
	
	public final int getType() {
		int attrElType = coordinates[0].getType();
		switch(attrElType) {
		case Element.ELEMENTTYPE_NUMERIC: return TYPE_NUMERIC;
		case Element.ELEMENTTYPE_STRING: return TYPE_STRING;
		}
		return -1;
	}
	
	public final void setType(int type) {
		coordinates[0].setType(type);
	}
	
//    private final CompoundKey createKey() {
//		return new CompoundKey(new Object[] { 
//				AttributeImpl.class,
//				this.getId(), 
//				this.getName(),
//				this.getAttributeElement().getDimension().getName() });
//	}
//
}
