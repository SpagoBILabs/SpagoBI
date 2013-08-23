/*
*
* @file HierarchyImpl.java
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
* @author Philipp Bouillon
*
* @version $Id: HierarchyImpl.java,v 1.34 2010/02/09 11:44:57 PhilippBouillon Exp $
*
*/

package org.palo.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palo.api.Attribute;
import org.palo.api.Connection;
import org.palo.api.ConnectionEvent;
import org.palo.api.Consolidation;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.Subset;
import org.palo.api.persistence.PaloPersistenceException;
import org.palo.api.subsets.SubsetHandler;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.PaloConstants;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.loader.ElementLoader;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Philipp Bouillon
 * @version $Id: HierarchyImpl.java,v 1.34 2010/02/09 11:44:57 PhilippBouillon Exp $
 */
public class HierarchyImpl implements Hierarchy {
    //--------------------------------------------------------------------------
    // FACTORY
	//
    final static HierarchyImpl create(ConnectionImpl connection,
			Dimension dimension, HierarchyInfo hierInfo, boolean doEvents) {
    	HierarchyImpl hier = new HierarchyImpl(connection, dimension, hierInfo);
    	return hier;
	}
    
    private final HierarchyInfo hierInfo;
    private final Dimension dimension;
    private final DbConnection dbConnection;
    private final Map<String, ElementImpl> loadedElements;
    private final ElementLoader elLoader;
    private final ConnectionImpl connection;
    private final Map<Element, Attribute> attributes;
    private final Database database;
    private final CompoundKey key;
    private final SubsetStorageHandler legacySubsetsHandler;
//	private SubsetHandler subsetHandler;
    
	private HierarchyImpl(ConnectionImpl connection, Dimension dimension, 
						  HierarchyInfo hierInfo) {
		this.hierInfo = hierInfo;
		this.dimension = dimension;
		this.connection = connection;		
		this.dbConnection = connection.getConnectionInternal();
    	this.attributes = new HashMap<Element, Attribute>();
    	this.database = dimension.getDatabase();
    	
		if (connection.getType() == Connection.TYPE_XMLA) {
			this.elLoader = dbConnection.getElementLoader(hierInfo);
		} else {
			this.elLoader = dbConnection.getElementLoader(
					((DimensionImpl) dimension).getInfo());
		}
		this.loadedElements = new LinkedHashMap<String, ElementImpl>();
    	this.key = new CompoundKey(new Object[] { HierarchyImpl.class,
    			connection,
				dimension.getDatabase().getId(), // getName(),
				hierInfo.getId() // getName()
			});
    	this.legacySubsetsHandler = 
			((DatabaseImpl)database).getLegacySubsetHandler();
//		this.subsetHandler = new SubsetHandlerImpl(this); 	
	}
	
	public String getId() {
		return hierInfo.getId();
	}

	public String getName() {
		return hierInfo.getName();
	}
	
	public boolean isNormal() {
		return hierInfo.getType() == PaloConstants.TYPE_NORMAL && 
			   !hierInfo.getName().startsWith("#");
	}
	
	public void rename(String name) {
		hierInfo.rename(name);			
	}
	
	public boolean canBeModified() {
		return hierInfo.canBeModified();
	}

	public boolean canCreateChildren() {
		return hierInfo.canCreateChildren();
	}
	
	public int getType() {
		return -1;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public int getElementCount() {
		return hierInfo.getElementCount();
	}
	
	public final Element[] getElements() {
		String[] ids = elLoader.getAllElementIds();
		ArrayList<Element> elements= new ArrayList<Element>(); 	//to filter out null databases!! => TODO better thrown an exception here???
		for(String id : ids) {
			ElementInfo info = elLoader.load(id);
			Element element = getElement(info);
			if(element != null)
				elements.add(element);
		}
		return (Element[])elements.toArray(new Element[elements.size()]);
	}
	
	private final Element getElement(ElementInfo elInfo) {
		if(elInfo == null)
			return null;
		Element element = (Element)loadedElements.get(elInfo.getId());
		if(element == null) {
			//not loaded yet...
			element = createElement(elInfo);
		}
		return element;
	}
		
	public final Element getElementAt(int index) {
		ElementInfo elInfo = elLoader.load(index);
		return getElement(elInfo);
	}
	
	public final ElementNode[] getAllElementNodes() {
		final ArrayList allnodes = new ArrayList();
		ElementNodeVisitor visitor = new ElementNodeVisitor() {
			public void visit(ElementNode node, ElementNode parent) {
				allnodes.add(node);
			}
		};
		Element roots[] = getRootElements();
		if (roots != null) {
			for (int i = 0; i < roots.length; ++i) {
				ElementNode rootNode = new ElementNode(roots[i], null);
				DimensionUtil.traverse(rootNode, visitor);
			}
		}
		return (ElementNode[]) allnodes.toArray(new ElementNode[0]);
	}
	
	/**
	 * Creates a new element instance from the given elementinfo and adds it to
	 * the list of all loaded elements
	 * @param elInfo
	 * @return
	 */
	private final ElementImpl createElement(ElementInfo elInfo) {
		ElementImpl element = ElementImpl.create(connection, dimension, elInfo, this);
		loadedElements.put(element.getId(),element);
		return element;
	}
	
	public final Attribute addAttribute(String name) {
		if (isAttributeHierarchy())
			throw new PaloAPIException(
					"Cannot add attributes to an attribute hierarchy!");
		try {
			Hierarchy attrHier = getAttributeHierarchy();
			Element attrElement = attrHier.addElement(name,
					Element.ELEMENTTYPE_STRING);			
			Attribute attribute = addAttribute(attrElement);
			fireAttributesAdded(new Attribute[] { attribute });

			return attribute;
		} catch (PaloException e) {
			throw new PaloAPIException("Attribute " + name + " already in use!!",e);
		}	
	}
	
	public final void removeAllAttributes() {
		Hierarchy attrHier = getAttributeHierarchy();
		if(attrHier == null)
			return;
		Element[] allAttrs = attrHier.getElements();
		for (int i = 0; i < allAttrs.length; ++i)
			attrHier.removeElement(allAttrs[i]);

		// event:
		fireAttributesRemoved(attributes.values().toArray());

		attributes.clear();
	}

	public final void removeAttribute(Attribute attribute) {
		Hierarchy attrHier = getAttributeHierarchy();
		if(attrHier == null)
			return;
		Element attrElement = attrHier.getElementByName(attribute.getName());
		if (attrElement != null) {
			attrHier.removeElement(attrElement);
			attributes.remove(attrElement);
			// event:
			fireAttributesRemoved(new Attribute[] { attribute });
		}
	}	
	
	public final Attribute[] getAttributes() {
		Hierarchy attrHier = getAttributeHierarchy();
		if (attrHier == null) {
			return new Attribute[0];
		}
		Element[] attrElements = attrHier.getElements();
		Attribute[] attributes = new Attribute[attrElements.length];
		for(int i=0;i<attrElements.length;++i)
			attributes[i] = getAttribute(attrElements[i]);
		return attributes;
	}

	public final Attribute getAttribute(String attrId) {
		Hierarchy attrHier = getAttributeHierarchy();
		if (attrHier != null) {
			Element attrElement = attrHier.getElementById(attrId);
//			return (Attribute)attributes.get(attrElement);	
			return getAttribute(attrElement);
		}
		return null;
	}
	
	public final Attribute getAttributeByName(String attrName) {
		Hierarchy attrHier = getAttributeHierarchy();
		if (attrHier != null) {
			Element attrElement = attrHier.getElementByName(attrName);
//			return (Attribute)attributes.get(attrElement);
			return getAttribute(attrElement);
		}
		return null;
	}
	
	public final Object[] getAttributeValues(Attribute[] attributes, Element[] elements) {		
		if (attributes.length != elements.length)
			throw new PaloAPIException("The number of attributes and elements has to be equal!");
		CubeImpl attrCube = (CubeImpl)getAttributeCube();
		if (attrCube == null)
			return new Object[0];
		Element[][] coordinates = getCoordinates(attributes, elements);
		return attrCube.getDataBulk(coordinates);
	}	

	public final Cube getAttributeCube() {
		DimensionInfo dimInfo = ((DimensionImpl) dimension).getInfo();
		String attrId = dimInfo.getAttributeCube();
		//palo server returns sometimes an empty string so:
		if(attrId == null || attrId.length()==0) 
			return database.getCubeByName("#_"+dimInfo.getName());
		return database.getCubeById(attrId);
	}
		
	public final void setAttributeValues(Attribute[] attributes, Element[] elements,
			Object[] values) {
		if (attributes.length == elements.length
				&& attributes.length == values.length) {
			Cube attrCube = getAttributeCube();
			Element[][] coordinates = getCoordinates(attributes, elements);
			attrCube.setDataArray(
					coordinates, values, Cube.SPLASHMODE_DISABLED);
			// event:
			fireAttributesChanged(attributes);
		} else
			throw new PaloAPIException(
					"The number of attributes, elements and values has to be equal!");
	}
	
	public final Hierarchy getAttributeHierarchy() {
		DimensionInfo dimInfo = ((DimensionImpl) dimension).getInfo();
		String attrId = dimInfo.getAttributeDimension();
		//palo server returns sometimes an empty string so:
		Dimension d;
		if (attrId == null || attrId.length()==0) {
			d = database.getDimensionByName("#_" + dimInfo.getName() + "_");
		} else {
			d = database.getDimensionById(attrId);
		}				
		if (d == null) {
			return null;
		}
		return d.getDefaultHierarchy(); 		
	}
	

	public final Element addElement(String name, int type) {
		if (name == null)
			return null;

		Element element = addElementInternal(name, type);
		dbConnection.reload(((DimensionImpl) dimension).getInfo());

		// send add event:
		fireElementsAdded(new Element[] { element });
		return element;
	}
	
	public final void addElements(String[] names, int[] types) {
		if (dbConnection.getServerInfo().getMajor() >= 3) {
			addElements(names, types, new Element[0][0], new double[0][0]);
		} else {
			if (names == null || types == null || names.length != types.length)
				return;
			Element[] _elements = new Element[names.length];
			for (int i = 0; i < names.length; ++i) {
				_elements[i] = addElementInternal(names[i], types[i]);
			}
			dbConnection.reload(((DimensionImpl) dimension).getInfo());
		
			// send add event:
			fireElementsAdded(_elements);
		}
	}
	
	public final void addElements(String[] names, int type,
			Element[][] children, double[][] weights) {
		try {
			// WORKAROUND FOR A BUG IN WEB PALO!!!!
			if (!connection.isLegacy())
				type = ElementImpl.elType2infoType(type);
			ElementInfo[] newElements = elLoader.createBulk(names, type, Util
					.getElementInfos(children), weights);
			List<Element> elements = new ArrayList<Element>();
			for (ElementInfo el : newElements) {
				if (loadedElements.containsKey(el.getId()))
					throw new PaloAPIException("Element '" + el.getName()
							+ "' already exists!!");
				elements.add(createElement(el));
			}
			//have to reload to reflect structural changes!
			dimension.reload(false);
			fireElementsAdded(elements.toArray());
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(), pex);
		}
	}

	public final void addElements(String[] names, int [] types,
			Element[][] children, double[][] weights) {
		try {
			// WORKAROUND FOR A BUG IN WEB PALO!!!!
			if (!connection.isLegacy()) {
				for (int i = 0, n = types.length; i < n; i++) {
					types[i] = ElementImpl.elType2infoType(types[i]);
				}
			}
			ElementInfo[] newElements = elLoader.createBulk(names, types, Util
					.getElementInfos(children), weights);
			List<Element> elements = new ArrayList<Element>();
			for (ElementInfo el : newElements) {
				if (loadedElements.containsKey(el.getId()))
					throw new PaloAPIException("Element '" + el.getName()
							+ "' already exists!!");
				elements.add(createElement(el));
			}
			//have to reload to reflect structural changes!
			dimension.reload(false);
			fireElementsAdded(elements.toArray());
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(), pex);
		}
	}
	
	public final void updateConsolidations(Consolidation [] consolidations) {
		try {
			// WORKAROUND FOR A BUG IN WEB PALO!!!!
			int type = Element.ELEMENTTYPE_CONSOLIDATED;
			if (!connection.isLegacy())
				type = ElementImpl.elType2infoType(type);
			
			LinkedHashMap <Element, List <Element>> childMap = new LinkedHashMap<Element, List<Element>>();
			LinkedHashMap <Element, List <Double>> weightMap = new LinkedHashMap<Element, List<Double>>();
			for (Consolidation c: consolidations) {
				if (!childMap.containsKey(c.getParent())) {
					childMap.put(c.getParent(), new ArrayList<Element>());
				}
				childMap.get(c.getParent()).add(c.getChild());
				if (!weightMap.containsKey(c.getParent())) {
					weightMap.put(c.getParent(), new ArrayList<Double>());
				}
				weightMap.get(c.getParent()).add(c.getWeight());
			}
			Element [] elements = childMap.keySet().toArray(new Element[0]);
			Element [][] children = new Element[childMap.size()][];
			int counter = 0;
			for (Element parent: childMap.keySet()) {
				children[counter++] = childMap.get(parent).toArray(new Element[0]);
			}
						
			Double [][] weights = new Double[weightMap.size()][];
			counter = 0;
			for (Element parent: weightMap.keySet()) {
				weights[counter++] = weightMap.get(parent).toArray(new Double[0]);
			}

			elLoader.replaceBulk(
					Util.getElementInfos(elements), type,
					Util.getElementInfos(children), weights);
			dimension.reload(false);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(), pex);
		}
	}

	public final void removeConsolidations(Element [] elements) {
		try {
			// WORKAROUND FOR A BUG IN WEB PALO!!!!
			int type = Element.ELEMENTTYPE_NUMERIC;
			if (!connection.isLegacy())
				type = ElementImpl.elType2infoType(type);
			elLoader.replaceBulk(
					Util.getElementInfos(elements), type,
					null, null);
			dimension.reload(false);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(), pex);
		}		
	}
	
	public final ElementNode[] getElementsTree() {
		Element roots[] = getRootElements();
		if(Boolean.parseBoolean("wpalo")) {
			ElementNode[] rootNodes = new ElementNode[roots.length];
			for(int i=0;i<roots.length;i++)
				rootNodes[i]= new ElementNode2(roots[i]);
			return rootNodes;
		} 
		
		//org:
		final ArrayList rootnodes = new ArrayList();
		ElementNodeVisitor visitor = new ElementNodeVisitor() {
			public void visit(ElementNode node, ElementNode parent) {
				if (parent == null)
					rootnodes.add(node);
			}
		};		
		if (roots != null) {
			for (int i = 0; i < roots.length; ++i) {
				ElementNode rootNode = new ElementNode(roots[i], null);
				DimensionUtil.forceTraverse(rootNode, visitor);
			}
		}
		ElementNode [] result = (ElementNode[]) rootnodes.toArray(new ElementNode[0]);
		return result;
	}
	
	public final Element[] getRootElements() {
//		if (ConnectionImpl.WPALO) {
//			ElementInfo[] elInfos = elLoader.getElementsAtDepth(0);
//			ArrayList<Element> roots = new ArrayList<Element>();
//			for (ElementInfo root : elInfos) {
//				Element element = getElement(root);
//				if (element != null)
//					roots.add(element);
//			}
//			return (Element[]) roots.toArray(new Element[roots.size()]);
//		}
		
		//org:
		String[] ids = elLoader.getAllElementIds();
		ArrayList<Element> roots = new ArrayList<Element>(); 	//to filter out null databases!! => TODO better thrown an exception here???
		for(String id : ids) {
			ElementInfo info = elLoader.load(id);
			// TODO this is a hack to circumvent the Mondrian bug. As soon
			// as parent-child hierarchies are fully supported by Mondrian,
			// remove the "if info == null continue" statement.
			if (info == null) {
				continue;
			}
			if(info.getParentCount() == 0) {
				Element element = getElement(info);
				if(element != null)
					roots.add(element);
			}
		}
		return (Element[])roots.toArray(new Element[roots.size()]);
	}
	
	public final void visitElementTree(ElementNodeVisitor visitor) {
        Element roots[] = getRootElements();
        if (roots != null) {
			for (int i = 0; i < roots.length; ++i) {
				ElementNode rootNode = new ElementNode(roots[i], null);
				DimensionUtil.traverse(rootNode, visitor);
			}
		}
	}
			
	public final void removeElements(Element[] elements) {
		//ArrayList removedElements = new ArrayList();
		/*for (int i = 0; i < elements.length; ++i) {
			if (removeElementInternal(elements[i]))
				removedElements.add(elements[i]);
		}*/
		//if (!removedElements.isEmpty()) {
		removeElementsInternal(elements);
			dbConnection.reload(((DimensionImpl) dimension).getInfo());
			fireElementsRemoved(elements);
		//}
	}
	
	public final Element getElementById(String id) {
		try {
			ElementInfo elInfo = elLoader.load(id);
			return getElement(elInfo);
		} catch (PaloException pex) {
			/* ignore */
		}
		return null;
	}

	public final Element getElementByName(String name) {
		ElementInfo elInfo = elLoader.loadByName(name);
		return getElement(elInfo);
	}
	
	public final String[] getElementNames() {
		String[] ids = elLoader.getAllElementIds();
		ArrayList<String> names = new ArrayList<String>();
		for(int i=0; i<ids.length; ++i) {
			try {
				ElementInfo info = elLoader.load(ids[i]);
				names.add(info.getName());
			}catch(PaloException pex) {
				/* ignore */
			}
		}
		return names.toArray(new String[0]);
	}	
	
	public final void renameElement(Element element, String newName) {
		element.rename(newName);
	}
	
	public final Element[] getElementsInOrder() {
		final ArrayList result = new ArrayList();
		DimensionUtil.ElementVisitor visitor = 
			new DimensionUtil.ElementVisitor() {
				public void visit(Element element, Element parent) {
					result.add(element);
				}
			};
		Element roots[] = getRootElements();
		if (roots != null) {
			for (int i = 0; i < roots.length; ++i)
				DimensionUtil.traverse(roots[i], visitor);
		}
		return (Element[]) result.toArray(new Element[0]);
	}

	public final Consolidation newConsolidation(Element element, Element parent,
			double weight) {
		return ConsolidationImpl.create(connection, parent, element, weight);
	}
	
	public final void removeElement(Element element) {
		if (removeElementInternal(element)) {
			dbConnection.reload(((DimensionImpl) getDimension()).getInfo());
			fireElementsRemoved(new Element[]{element});
		}
	}

	public final int getMaxDepth() {
		return hierInfo.getMaxDepth();
	}

	public final int getMaxLevel() {
		return hierInfo.getMaxLevel();
	}
	
    private final boolean removeElementInternal(Element element) {		
    	if (loadedElements.containsKey(element.getId())) {
			ElementImpl _element = (ElementImpl) element;
			if(elLoader.delete(_element.getInfo())) {
				// reload its children and parents:
				Element[] children = element.getChildren();
				for (int i = 0; i < children.length; ++i)
					((ElementImpl) children[i]).reload(false);
				Element[] parents = element.getParents();
				for (int i = 0; i < parents.length; ++i)
					((ElementImpl) parents[i]).reload(false);

				// finally remove it from dimension...
				loadedElements.remove(element.getId());
				return true;
			}
		}
		return false;
    }    
	
    private final boolean removeElementsInternal(Element[] elements) {
    	ArrayList <ElementInfo> elInfos = new ArrayList<ElementInfo>();
    	HashMap<Element, Element[]> children = new HashMap<Element, Element[]>();
    	HashMap<Element, Element[]> parents = new HashMap<Element, Element[]>();
    	
    	for (Element e: elements) {
    		elInfos.add(((ElementImpl) e).getInfo());
    		children.put(e, e.getChildren());
    		parents.put(e, e.getParents());
    	}
    	if (elLoader.delete(elInfos.toArray(new ElementInfo[0]))) {
    		//update element structure...
    		Set<Element> deleted = children.keySet();
        	for (Element e: elements) {        		
//        		reload(e, deleted);
				for(Element child : children.get(e)) //e.getChildren())
					reload(child, deleted);
				for(Element parent : parents.get(e)) //e.getParents())
					reload(parent, deleted);
				//finally
				loadedElements.remove(e.getId());
        	}
    		return true;
    	}
    	return false;
    }
    
    private final void reload(Element element, Set<Element> deleted) {
		if (element != null && !deleted.contains(element))
			((ElementImpl) element).reload(false);
	}
    
    // TODO check connection event... They operate on Dimensions; not on
    // hierarchies.
    private final void fireElementsAdded(Object[] elements) {
		connection.fireEvent(new ConnectionEvent(connection, this,
				ConnectionEvent.CONNECTION_EVENT_ELEMENTS_ADDED, elements));
	}
    
    private final void fireElementsRemoved(Object[] elements) {
		connection.fireEvent(new ConnectionEvent(connection, this,
				ConnectionEvent.CONNECTION_EVENT_ELEMENTS_REMOVED, elements));
	}

    private final Element addElementInternal(String name, int type) {
		try {
			// WORKAROUND FOR A BUG IN WEB PALO!!!!
			if (!connection.isLegacy())
				type = ElementImpl.elType2infoType(type);
			ElementInfo elInfo = 
				elLoader.create(name, type, new ElementInfo[0], new double[0]);
			if (loadedElements.containsKey(elInfo.getId()))
				throw new PaloAPIException("Element '" + name
						+ "' already exists!!");
			Element element = createElement(elInfo);
			return element;
		} catch (PaloException pex) {
			pex.printStackTrace();
			throw new PaloAPIException(pex.getMessage(), pex);
		}		
	}    
    
	private final void fireAttributesAdded(Object[] attributes) {
		connection.fireEvent(new ConnectionEvent(connection, this,
				ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_ADDED, attributes));
	}
    
    private final void fireAttributesChanged(Object[] attributes) {
		connection.fireEvent(new ConnectionEvent(
				connection, this,
				ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_CHANGED,
				attributes));
    }
    
    private final void fireAttributesRemoved(Object[] attributes) {
		connection.fireEvent(new ConnectionEvent(
				connection, this,
				ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_REMOVED,
				attributes));
	}    
    
    private final Element[][] getCoordinates(Attribute[] attributes,Element[] elements) {
		// determine the coordinates:
		Element[][] coordinates = new Element[attributes.length][];
		for (int i = 0; i < attributes.length; ++i) {
			coordinates[i] = new Element[2];
			coordinates[i][0] = 
					((AttributeImpl) attributes[i]).getAttributeElement();
			coordinates[i][1] = elements[i];
		}
		return coordinates;
    }    
    
	final Attribute getAttribute(Element attrElement) {
    	Object attribute = attributes.get(attrElement);
    	if(attribute == null) {
    		attribute = addAttribute(attrElement);
    	}
    	return (Attribute)attribute;
    }

	private final Attribute addAttribute(Element attrElement) {
		if(attrElement == null)
			return null;
		
		Cube attrCube = getAttributeCube();
		Attribute attribute = AttributeImpl.create(attrElement, attrCube);
		attributes.put(attrElement, attribute);
		return attribute;
	}
	
	public final boolean isAttributeHierarchy() {
		return ((DimensionImpl) dimension).getInfo().getType() == 
			DimensionInfo.TYPE_ATTRIBUTE;
	}
	
	public final boolean equals(Object other) {
		if(other instanceof HierarchyImpl) {
			return key.equals(((HierarchyImpl) other).key);
		}
		return false;
	}
	
	public final int hashCode() {
		return key.hashCode();
	}	
	
	public final void reload(boolean doEvents) {
		//reload dimension:
		LinkedHashMap oldElements = new LinkedHashMap(loadedElements);
		ArrayList addedElements = new ArrayList();
		
		//reload from server:		
		dbConnection.reload(((DimensionImpl) dimension).getInfo());		

		loadedElements.clear();
		elLoader.reset();
		
		//read in new elements
    	String[] elIDs = elLoader.getAllElementIds();
    	for (String id : elIDs) {
			ElementImpl element = (ElementImpl) oldElements.get(id);
			if (element == null) {
				ElementInfo info = elLoader.load(id);
				element = createElement(info);
			} else {
				// remove reused element...
				oldElements.remove(id);
				loadedElements.put(id, element);
			}
			element.reload(doEvents);
		}
				
		if (doEvents) {			
			if (!oldElements.isEmpty()) {
				fireElementsRemoved(oldElements.values().toArray());
			}
			if (!addedElements.isEmpty()) {
				fireElementsAdded(addedElements.toArray());
			}
		}
	}	
	
	final void clearCache() {
		for(ElementImpl element : loadedElements.values()) {
			element.clearCache();
		}
		loadedElements.clear();
		elLoader.reset();
				
		attributes.clear();		
	}	
	
	final ElementLoader getElementLoader() {
		return elLoader;
	}

//	public final void reloadElements() {
//		String[] elIDs = elLoader.getAllElementIds();
//		for(String id : elIDs) {
//			ElementInfo elInfo = elLoader.load(id);
//			dbConnection.reload(elInfo);
//		}
//	}
	
	public Subset addSubset(String name) {
		return legacySubsetsHandler.addSubset(this, name);
	}

	public Subset getSubset(String id) {
		try {
			return legacySubsetsHandler.getSubset(this, id);
		} catch (PaloPersistenceException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SubsetHandler getSubsetHandler() {
		return dimension.getSubsetHandler();
	}

	public Subset[] getSubsets() {
		try {
			return legacySubsetsHandler.getSubsets(this);
		} catch(PaloPersistenceException e) {
			e.printStackTrace();			
		}
		return new Subset[0];
	}

	public void removeSubset(Subset subset) {
		legacySubsetsHandler.removeSubset(this, subset);
	}
	
	public final boolean isSubsetHierarchy() {
		return ((DatabaseImpl)database).isSubsetDimension(this.getDimension());
	}	
	public final String toString() {
		StringBuffer str = new StringBuffer();
		str.append("Hierarchy(\"");
		str.append(getName());
		str.append("\")[");
		str.append(hashCode()); //getId());
		str.append("]");
		return str.toString();
	}
	
	/**
	 * simply reset elements cache
	 * @param fromPosition
	 */
	final void resetElementsCache() {
		elLoader.reset();
		loadedElements.clear();
//		String[] ids = elLoader.getAllElementIds();
//		for(int i=startPosition; i<ids.length; i++) {
//			ElementInfo info = elLoader.load(ids[i]);
//			Element element = getElement(info);
//			((ElementImpl) element).reload(false);
//		}
	}
}
