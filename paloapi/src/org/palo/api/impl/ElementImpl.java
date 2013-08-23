/*
*
* @file ElementImpl.java
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
* @author Arnd Houben
*
* @version $Id: ElementImpl.java,v 1.65 2009/10/27 08:33:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.palo.api.Attribute;
import org.palo.api.ConnectionEvent;
import org.palo.api.Consolidation;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.Property2;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.loader.ElementLoader;
import com.tensegrity.palojava.loader.PropertyLoader;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @author Stepan Rutz
 * @version $Id: ElementImpl.java,v 1.65 2009/10/27 08:33:18 PhilippBouillon Exp $
 */
class ElementImpl implements Element {

    //--------------------------------------------------------------------------
    // FACTORY
	//
    final static ElementImpl create(ConnectionImpl connection,
			Dimension dimension, ElementInfo elInfo) {
		return new ElementImpl(connection, dimension, elInfo, null);
	}
    
    final static ElementImpl create(ConnectionImpl connection,
			Dimension dimension, ElementInfo elInfo, Hierarchy hier) {
		return new ElementImpl(connection, dimension, elInfo, hier);
	}

    // -------------------------------------------------------------------------
    // INSTANCE
    private final Dimension dimension;
    private final Hierarchy hierarchy;
    private final ConnectionImpl connection;
    private final DbConnection dbConnection;       
    private final ElementInfo elInfo;
    private final CompoundKey key;
	private final PropertyLoader propertyLoader;
	private final Map <String, Property2Impl> loadedProperties;
 
    private ElementImpl(ConnectionImpl connection, Dimension dimension,
    		ElementInfo elInfo, Hierarchy hier) {
    	this.elInfo = elInfo;
    	this.dimension = dimension;    	
        this.connection = connection;
        this.dbConnection = connection.getConnectionInternal();
        this.key = new CompoundKey(new Object[] { ElementImpl.class,
				connection, dimension.getDatabase().getId(), dimension.getId(),
				elInfo.getId() });
		this.loadedProperties = new HashMap <String, Property2Impl> ();
		this.propertyLoader = dbConnection.getTypedPropertyLoader(elInfo);
		if (hier == null) {
			hierarchy = dimension.getDefaultHierarchy();
		} else {
			hierarchy = hier;
		}
    }
    

	public final Object getAttributeValue(Attribute attribute) {
		if(attribute == null)
			return null;
		return attribute.getValue(this);
	}

	public final Object[] getAttributeValues() {
		Attribute[] attributes = getHierarchy().getAttributes();
		Object[] values = new Object[attributes.length];
		for(int i=0;i<values.length;++i)
			values[i] = attributes[i].getValue(this);
		return values;
	}

	public final int getChildCount() {
		return elInfo.getChildrenCount();
	}

	public final Element[] getChildren() {
		ElementLoader loader = ((HierarchyImpl) hierarchy).getElementLoader();
		ElementInfo[] children = loader.getChildren(elInfo);
//		String[] children = elInfo.getChildren();
//		Element[] _children = new Element[children.length];
//		for(int i=1;i<children.length;++i) {
//			_children[i] = dimension.getElementById(children[i].getId());
//			if(_children[i] == this) {
//				return new Element[0];
//			}
//		}
		ArrayList<Element> _children = new ArrayList<Element>();
		for(int i=0;i<children.length;++i) {
			Element child = hierarchy.getElementById(children[i].getId());
			if(!child.equals(this)) {
				_children.add(child);
			}
		}
		return _children.toArray(new Element[0]);
	}


	public final Consolidation getConsolidationAt(int index) {
		if (getChildCount() == 0)
			return null;
		String childId = elInfo.getChildren()[index];
		double weight = elInfo.getWeights()[index];
		return ConsolidationImpl.create(connection, this, hierarchy
				.getElementById(childId), weight);
	}

	public final int getConsolidationCount() {
		return getChildCount();
	}

	public final Consolidation[] getConsolidations() {
		String[] childrenIds = elInfo.getChildren();
		double[] weights = elInfo.getWeights();
		Consolidation[] consolidations = new Consolidation[childrenIds.length];
		for(int i=0;i<childrenIds.length;++i) {
			consolidations[i] = 
				ConsolidationImpl.create(connection,this,hierarchy.getElementById(childrenIds[i]),weights[i]);
		}
		return consolidations;
	}

	public final int getDepth() {
		return elInfo.getDepth();
	}
	
	public final int getLevel() {
		return elInfo.getLevel();
	}
	
	public final int getIndent() {
		return elInfo.getIndent();
	}

	public final Dimension getDimension() {
		return dimension;
	}
	
	public final Hierarchy getHierarchy() {
		return hierarchy;
	}

	public final String getName() {
		return elInfo.getName();
	}

	public final int getParentCount() {
		return elInfo.getParentCount();
	}

	public final Element[] getParents() {
		String[] parentIds = elInfo.getParents();
		Element[] parents = new Element[parentIds.length];
		for(int i=0;i<parents.length;++i)
			parents[i] = hierarchy.getElementById(parentIds[i]);
//		return (Element[]) parents.toArray(new Element[parents.size()]);
		return parents;
	}

	public final int getType() {
//	  THIS IS NEEDED AS A WORKAROUND FOR A BUG IN WEB PALO!!!!		
		if(connection.isLegacy())
			return elInfo.getType();
		else
			return infoType2elType(elInfo.getType());
	}

	public final String getTypeAsString() {
        switch (getType()) {
		default:
		case Element.ELEMENTTYPE_NUMERIC:
			return Element.ELEMENTTYPE_NUMERIC_STRING;
		case Element.ELEMENTTYPE_STRING:
			return Element.ELEMENTTYPE_STRING_STRING;
		case Element.ELEMENTTYPE_CONSOLIDATED:
			return Element.ELEMENTTYPE_CONSOLIDATED_STRING;
		case Element.ELEMENTTYPE_RULE:
			return Element.ELEMENTTYPE_RULE_STRING;
		}
	}

	public final void rename(String name) {
		renameInternal(name,true);
	}

	public final void setAttributeValue(Attribute attribute, Object value) {
		attribute.setValue(this, value);
	}

	public final void setAttributeValues(Attribute[] attributes, Object[] values) {
		for(int i=0;i<attributes.length;++i)
			attributes[i].setValue(this, values[i]);
	}

	public final void setType(int type) {
		setTypeInternal(type);
	}	

	public final void updateConsolidations(Consolidation[] consolidations) {
		updateConsolidationsInternal(consolidations,true);
	}

	public final String getId() {
		return elInfo.getId();
	}

	public final int getPosition() {
		return elInfo.getPosition();
	}

	public final void move(int newPosition) {
		dbConnection.move(elInfo, newPosition);
		((HierarchyImpl) hierarchy).resetElementsCache();
	}

	public final boolean equals(Object other) {
		if(other instanceof ElementImpl) {
			return key.equals(((ElementImpl)other).key);
		}
		return false;
	}
	
	public final int hashCode() {
		return key.hashCode();
	}

	public final String toString() {
		StringBuffer str = new StringBuffer();
		str.append("Element(\"");
		str.append(getName());
		str.append("\")[");
		str.append(getId());
		str.append("]");
		return str.toString();
	}
	
	public final ElementInfo getInfo() {
		return elInfo;
	}
	
	// --------------------------------------------------------------------------
	// PACKAGE INTERNAL
	//	
	/**
	 * 
	 * @param name
	 * @param doEvents
	 * @param commit to database, i.e. propagate rename to palo server
	 */
	final void renameInternal(String name, boolean doEvents) {
		String oldName = getName();
		if(name.equals(oldName))
			return;
		dbConnection.rename(elInfo, name);
		
		//get dimension and reload rules:
		((DimensionImpl)getDimension()).reloadRules();
		
		// create and fire connection event:
		if (doEvents) {
			fireElementsRenamed(new Element[] { this }, oldName);
		}
	}
	
	final void reload(boolean doEvents) {
		//preserve all states:
		String oldName = getName();
		int oldType = getType();
		Consolidation[] oldConsolidations = getConsolidations();
		
		//reload from server:
		dbConnection.reload(elInfo);
		
		if(doEvents) {
			Element[] affetecedElements = new Element[] { this };
			//compare and raise events...
			if(!getName().equals(oldName))
				fireElementsRenamed(affetecedElements, oldName);
			if(getType() != oldType)
				fireElementsTypeChanged(affetecedElements, oldType);
			//consolidations:
			compareConsolidations(oldConsolidations, getConsolidations(),doEvents);			
		}
	}
	
	final void clearCache() {
		for(Property2Impl property : loadedProperties.values())
			property.clearCache();
		
		loadedProperties.clear();
		propertyLoader.reset();
	}
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final void updateConsolidationsInternal(
			Consolidation[] newConsolidations, boolean doEvents) {
		Consolidation[] oldConsolidations = getConsolidations();

		String[] children = new String[newConsolidations.length];
		double[] weights = new double[newConsolidations.length];
		for (int i = 0; i < newConsolidations.length; ++i) {
			ElementImpl child = (ElementImpl) newConsolidations[i].getChild();
			weights[i] = newConsolidations[i].getWeight();
			children[i] = child.getId();
		}
		// WORKAROUND FOR BUG IN WEB PALO:
		int elType = ELEMENTTYPE_CONSOLIDATED;
		if (!connection.isLegacy())
			elType = elType2infoType(elType);		
		dbConnection.update(elInfo, elType, children, weights, dbConnection.getServerInfo());
//TEMP. REMOVED FOR JEDOX 
		// HAVE TO UPDATE DIMENSION TO, SINCE IT IS A STRUCTURAL CHANGE AND
		// CAN HAVE INFLUENCE ON (E.G.) MAXDEPTH...
//		DimensionImpl dimImpl = (DimensionImpl)getDimension();
//		dimImpl.reloadAll();
// END JEDOX		
		compareConsolidations(oldConsolidations, newConsolidations, doEvents);
	}
	
    private final void setTypeInternal(int type) {
		int oldType = getType();
		if(oldType == type)
			return;
		//check type:
        boolean typeOk = false;
        switch (type) {
		case Element.ELEMENTTYPE_NUMERIC:
			typeOk = true;
			break;
		case Element.ELEMENTTYPE_STRING:
			typeOk = true;
			break;
		case Element.ELEMENTTYPE_CONSOLIDATED:
			typeOk = true;
			break;
		case Element.ELEMENTTYPE_RULE:
			typeOk = true;
			break;
		default:
			typeOk = false;
		}
        if (!typeOk)
            return;
        
//      THIS IS NEEDED AS A WORKAROUND FOR A BUG IN WEB PALO!!!!            
        if(!connection.isLegacy())
        	type = elType2infoType(type);
        
        dbConnection.update(
        		elInfo, type,elInfo.getChildren(),elInfo.getWeights(),dbConnection.getServerInfo());
		// create and fire connection event:
        fireElementsTypeChanged(new Element[] { this },oldType); 
    }
    
    private final void compareConsolidations(Consolidation[] oldConsolidations,
			Consolidation[] newConsolidations,boolean doEvents) {
		Set oldCons = new LinkedHashSet(Arrays.asList(oldConsolidations));
		Set newCons = new LinkedHashSet(Arrays.asList(newConsolidations));
		Set removed = new LinkedHashSet(oldCons);
		Set added = new LinkedHashSet(newCons);

		removed.removeAll(newCons);
		added.removeAll(oldCons);

		if (!removed.isEmpty()) {
			for (Iterator it = removed.iterator(); it.hasNext();) {
				Consolidation consolidation = (Consolidation) it.next();
				Element child = consolidation.getChild();
				if(child != null)
					((ElementImpl) child).reload(false);
			}
			if(doEvents)
				fireConsolidationsRemoved(removed.toArray());
		}

		if (!added.isEmpty()) {
			for (Iterator it = added.iterator(); it.hasNext();) {
				Consolidation consolidation = (Consolidation) it.next();
				Element child = consolidation.getChild();
				if(child != null)
					((ElementImpl) child).reload(false);

			}
			if(doEvents)
				fireConsolidationsAdded(added.toArray());
		}
	}
    
    
    private final void fireElementsRenamed(Object[] elements,String oldName) {
		ConnectionEvent ev = new ConnectionEvent(connection,
				getDimension(),
				ConnectionEvent.CONNECTION_EVENT_ELEMENTS_RENAMED,
				elements);
		ev.oldValue = oldName;
		connection.fireEvent(ev);
    }
    
    private final void fireElementsTypeChanged(Object[] elements, int oldType) {
		ConnectionEvent ev = new ConnectionEvent(
				connection,
				getDimension(),
				ConnectionEvent.CONNECTION_EVENT_ELEMENTS_TYPE_CHANGED,
				elements);
		ev.oldValue = new Integer(oldType); 
		connection.fireEvent(ev);
    }
    
    private final void fireConsolidationsAdded(Object[] consolidations) {
		connection.fireEvent(new ConnectionEvent(connection, this,
				ConnectionEvent.CONNECTION_EVENT_CONSOLIDATIONS_ADDED,
				consolidations));
	}
    
    private final void fireConsolidationsRemoved(Object[] consolidations) {
		connection.fireEvent(new ConnectionEvent(connection, this,
				ConnectionEvent.CONNECTION_EVENT_CONSOLIDATIONS_REMOVED,
				consolidations));
    }

//  THIS IS NEEDED AS A WORKAROUND FOR A BUG IN WEB PALO!!!!    
    private final int infoType2elType(int type) {
    	switch(type) {
    	case ElementInfo.TYPE_NUMERIC: return Element.ELEMENTTYPE_NUMERIC;
    	case ElementInfo.TYPE_STRING: return Element.ELEMENTTYPE_STRING;
    	case ElementInfo.TYPE_CONSOLIDATED: return Element.ELEMENTTYPE_CONSOLIDATED;
    	case ElementInfo.TYPE_RULE: return Element.ELEMENTTYPE_RULE;
    	}
    	return -1;
    }
    
    public static final int elType2infoType(int type) {
    	switch(type) {
    	case Element.ELEMENTTYPE_NUMERIC: return ElementInfo.TYPE_NUMERIC;
    	case Element.ELEMENTTYPE_STRING: return ElementInfo.TYPE_STRING;
    	case Element.ELEMENTTYPE_CONSOLIDATED: return ElementInfo.TYPE_CONSOLIDATED;
    	case Element.ELEMENTTYPE_RULE: return ElementInfo.TYPE_RULE;
    	}
    	return -1;
    }
    
	public String[] getAllPropertyIds() {
		return propertyLoader.getAllPropertyIds();
	}

	public Property2 getProperty(String id) {
		PropertyInfo propInfo = propertyLoader.load(id);
		if (propInfo == null) {
			return null;
		}
		Property2 property = loadedProperties.get(propInfo.getId());
		if (property == null) {
			property = createProperty(propInfo);
		}

		return property;
	}
	
	public void addProperty(Property2 property) {
		if (property == null) {
			return;
		}
		Property2Impl _property = (Property2Impl)property;
		propertyLoader.loaded(_property.getPropInfo());
		loadedProperties.put(_property.getId(), _property);
	}
	
	public void removeProperty(String id) {
		Property2 property = getProperty(id); 
		if (property == null) {
			return;
		}
		if (property.isReadOnly()) {
			return;
		}
		loadedProperties.remove(property);
	}

	private void createProperty(Property2 parent, PropertyInfo kid) {
		Property2 p2Kid = Property2Impl.create(parent, kid);
		parent.addChild(p2Kid);		
		for (PropertyInfo kidd: kid.getChildren()) {
			createProperty(p2Kid, kidd);
		}
	}
	
	private Property2 createProperty(PropertyInfo propInfo) {
		Property2 prop = Property2Impl.create(null, propInfo);
		for (PropertyInfo kid: propInfo.getChildren()) {
			createProperty(prop, kid);
		}
		return prop;
	}


	public boolean canBeModified() {
		return elInfo.canBeModified();
	}


	public boolean canCreateChildren() {
		return elInfo.canCreateChildren();
	} 
	
//	private final void reloadAllParentsAndChildren() {
//		String[] children = elInfo.getChildren();
//		for(String child : children)
//			reloadChild(child);
//		
//		String[] parents = elInfo.getParents();
//		for(String parent : parents)
//			reloadParent(parent);
//	}
//	private final void reloadChild(String id) {
//		ElementInfo el = ((ElementImpl)dimension.getElementById(id)).getInfo();
//		dbConnection.reload(el);
//		String[] children = el.getChildren();
//		for(String child : children)
//			reloadChild(child);
//	}
//	private final void reloadParent(String id) {
//		ElementInfo el = ((ElementImpl)dimension.getElementById(id)).getInfo();
//		dbConnection.reload(el);
//		String[] children = el.getChildren();
//		for(String child : children)
//			reloadChild(child);
//	}
//
}
