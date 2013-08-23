/*
*
* @file DimensionImpl.java
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
* @version $Id: DimensionImpl.java,v 1.112 2010/02/22 11:38:55 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.palo.api.Attribute;
import org.palo.api.ConnectionEvent;
import org.palo.api.Consolidation;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;
import org.palo.api.Property2;
import org.palo.api.Subset;
import org.palo.api.persistence.PaloPersistenceException;
import org.palo.api.subsets.SubsetHandler;
import org.palo.api.subsets.impl.SubsetHandlerImpl;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.loader.HierarchyLoader;
import com.tensegrity.palojava.loader.PropertyLoader;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @author Stepan Rutz
 * @version $Id: DimensionImpl.java,v 1.112 2010/02/22 11:38:55 PhilippBouillon Exp $
 */
class DimensionImpl implements Dimension {
	
    //--------------------------------------------------------------------------
    // FACTORY
	//
    final static DimensionImpl create(ConnectionImpl connection,
			Database database, DimensionInfo dimInfo, boolean doEvents) {
    	DimensionImpl dim = new DimensionImpl(connection, database, dimInfo);
    	return dim;
	}
    
    // -------------------------------------------------------------------------
    // INSTANCE
    //
    private final DimensionInfo dimInfo;
    private final ConnectionImpl connection;
    private final DbConnection dbConnection;
//    private final Map elInfos;
//    private final Map<String, ElementImpl> loadedElements;
//    private final Map subsets;
//    private final Map<Element, Attribute> attributes;
    private final Database database;
    private final CompoundKey key; 
    private final SubsetStorageHandler legacySubsetsHandler;
    //private final ElementLoader elLoader;
	private final PropertyLoader propertyLoader;
	private final HierarchyLoader hierarchyLoader;
	private final Map <String, Property2Impl> loadedProperties;
	private final SubsetHandler subsetHandler;
	private final Map <String, HierarchyInfo> hierarchyInfos;
	private final Map <String, HierarchyImpl> loadedHierarchies;
	
    //TODO consolidation cache!!!
    
//    private boolean elementsNeedReload = true;
    
    private DimensionImpl(ConnectionImpl connection, Database database, DimensionInfo dimInfo) {
    	this.dimInfo = dimInfo;
    	this.connection = connection;
    	this.dbConnection = connection.getConnectionInternal();
    	this.database = database;
    	
    	//have to create loaders here since legacy SubsetHandler needs them!!
		this.loadedProperties = new HashMap <String, Property2Impl> ();
		this.propertyLoader = dbConnection.getTypedPropertyLoader(dimInfo);
		this.hierarchyLoader = dbConnection.getHierarchyLoader(dimInfo);

    	this.legacySubsetsHandler = 
    			((DatabaseImpl)database).getLegacySubsetHandler();
   		this.loadedHierarchies = new HashMap<String, HierarchyImpl>();    	
   		this.subsetHandler = new SubsetHandlerImpl(this);
   		this.hierarchyInfos = new HashMap<String, HierarchyInfo>();
   		
    	this.key = new CompoundKey(new Object[] { DimensionImpl.class,
    			connection,
				dimInfo.getDatabase().getId(), // getName(),
				dimInfo.getId() // getName()
				});
	}

//    void setElementInfosInternal(ElementInfo [] _elInfos) {
//    	elInfos.clear();
//		for(int i=0;i<_elInfos.length;++i) {
//			elInfos.put(_elInfos[i].getId(),_elInfos[i]);
//		}
//		elementsNeedReload = false;
//    }

	public final Attribute addAttribute(String name) {
		return getDefaultHierarchy().addAttribute(name);
//		if (isAttributeDimension())
//			throw new PaloAPIException(
//					"Cannot add attributes to an attribute dimension!");
//		try {
//			Dimension attrDim = getAttributeDimension();
//			Cube attrCube = getAttributeCube();
//			Element attrElement = attrDim.addElement(name,
//					Element.ELEMENTTYPE_STRING);
////			ConnectionImpl connection = (ConnectionImpl) getDatabase()
////					.getConnection();
//			Attribute attribute = 
//				AttributeImpl.create(attrElement, attrCube);
//			attributes.put(attrElement, attribute);
//			fireAttributesAdded(new Attribute[] { attribute });
//
//			return attribute;
//		} catch (PaloException e) {
//			throw new PaloAPIException("Attribute " + name + " already in use!!",e);
//		}	
	}
	
	public final Element addElement(String name, int type) {
		return getDefaultHierarchy().addElement(name, type);
//		if (name == null)
//			return null;
//
//		Element element = addElementInternal(name, type);
////		reload(false);
//		dbConnection.reload(dimInfo);
//
//		// send add event:
//		fireElementsAdded(new Element[] { element });
//		return element;
	}

	public final void addElements(String[] names, int[] types) {
		getDefaultHierarchy().addElements(names, types);
//		if (names == null || types == null || names.length != types.length)
//			return;
//		Element[] _elements = new Element[names.length];
//		for (int i = 0; i < names.length; ++i) {
//			_elements[i] = addElementInternal(names[i], types[i]);
//		}
//		dbConnection.reload(dimInfo);
//		
//		// send add event:
//		fireElementsAdded(_elements);
	}

	public final void addElements(String[] names, int type, Element[][] children, double[][] weights) {
		getDefaultHierarchy().addElements(names, type, children, weights);
	}
	
	public final void addElements(String[] names, int [] types, Element[][] children, double[][] weights) {
		getDefaultHierarchy().addElements(names, types, children, weights);
	}
	
	public final void updateConsolidations(Consolidation [] consolidations) {
		getDefaultHierarchy().updateConsolidations(consolidations);
	}
	
	public final void removeConsolidations(Element [] elements) {
		getDefaultHierarchy().removeConsolidations(elements);
	}
	
//	public final Subset addSubset(String id, String name) {
//		return subsetStorageHandler.addSubset(this, id, name);
////		if(subsets.containsKey(id))
////			throw new PaloAPIException("Subset already exists!");
////		ApiExtensionController controller = ApiExtensionController.getInstance();
////		Subset subset = controller.createSubset(id,name,this);
////		subsets.put(subset.getId(),subset);
////		return subset;
//	}

	public final Subset addSubset(String name) {
		return legacySubsetsHandler.addSubset(this, name);
//		String id = Long.toString(System.currentTimeMillis());
//		while(subsets.containsKey(id)) {
//			long lg = Long.parseLong(id);
//			lg++;
//			id = Long.toString(lg);
//		}
//		return addSubset(id,name);
	}

		
	public final void dumpElementsTree() {
		System.err.println("elementsTree for \"" + getName() + "\" ...");
		ElementNode roots[] = getElementsTree();
		for (int i = 0; i < roots.length; ++i) {
			DimensionUtil.traverse(roots[i], new ElementNodeVisitor() {
				public void visit(ElementNode node, ElementNode parent) {
					int depth = node.getDepth();
					System.err.print("  ");
					for (int j = 0; j < depth; ++j)
						System.err.print("  ");
					System.err.println(node.getElement().getName());
				}
			});
		}
	}

	public final ElementNode[] getAllElementNodes() {
		return getDefaultHierarchy().getAllElementNodes();
//		final ArrayList allnodes = new ArrayList();
//		ElementNodeVisitor visitor = new ElementNodeVisitor() {
//			public void visit(ElementNode node, ElementNode parent) {
//				allnodes.add(node);
//			}
//		};
//		Element roots[] = getRootElements();
//		if (roots != null) {
//			for (int i = 0; i < roots.length; ++i) {
//				ElementNode rootNode = new ElementNode(roots[i], null);
//				DimensionUtil.traverse(rootNode, visitor);
//			}
//		}
//		return (ElementNode[]) allnodes.toArray(new ElementNode[0]);
	}

	public final Cube getAttributeCube() {
		return getDefaultHierarchy().getAttributeCube();
//		String attrId = dimInfo.getAttributeCube();
//		//palo server returns sometimes an empty string so:
//		if(attrId == null || attrId.length()==0)
//			return database.getCubeByName("#_"+dimInfo.getName());
//		return database.getCubeById(attrId);
	}

	public final Dimension getAttributeDimension() {
		Hierarchy attHier = getDefaultHierarchy().getAttributeHierarchy();
		if (attHier == null) {
			return null;
		}
		return attHier.getDimension();
//		String attrId = dimInfo.getAttributeDimension();
//		//palo server returns sometimes an empty string so:
//		if(attrId == null || attrId.length()==0)
//			return database.getDimensionByName("#_"+dimInfo.getName()+"_");
//		return database.getDimensionById(attrId);
	}

	public final Object[] getAttributeValues(Attribute[] attributes, Element[] elements) {
		return getDefaultHierarchy().getAttributeValues(attributes, elements);
//		if(attributes.length != elements.length)
//			throw new PaloAPIException("The number of attributes and elements has to be equal!");
//		CubeImpl attrCube = (CubeImpl)getAttributeCube();
//		if(attrCube == null)
//			return new Object[0];
//		Element[][] coordinates = getCoordinates(attributes, elements);
//		return attrCube.getDataBulk(coordinates);
//		//TODO what is missing here is a different getDataArray().
//		//we have a big, big problem inside our api!!!! getDataArray() behaves
//		//very different from setDataArray()!!!
//		//=> think what to do...
	}

	public final Attribute[] getAttributes() {
		return getDefaultHierarchy().getAttributes();
//		Dimension attrDim = getAttributeDimension();
//		if (attrDim == null) {
//			return new Attribute[0];
//		}
//		Element[] attrElements = attrDim.getElements();
//		Attribute[] attributes = new Attribute[attrElements.length];
//		for(int i=0;i<attrElements.length;++i)
//			attributes[i] = getAttribute(attrElements[i]);
//		return attributes;
	}

	public final Attribute getAttribute(String attrId) {
		return getDefaultHierarchy().getAttribute(attrId);
//		Dimension attrDim = getAttributeDimension();
//		if (attrDim != null) {
//			Element attrElement = attrDim.getElementById(attrId);
//			return (Attribute)attributes.get(attrElement);	
//		}
//		return null;
	}
	
	public final Attribute getAttributeByName(String attrName) {
		return getDefaultHierarchy().getAttributeByName(attrName);
//		Dimension attrDim = getAttributeDimension();
//		if (attrDim != null) {
//			Element attrElement = attrDim.getElementByName(attrName);
//			return (Attribute)attributes.get(attrElement);	
//		}
//		return null;
	}
	
	public final Database getDatabase() {
		return database;
//		return connection.getDatabaseById(dimInfo.getDatabase().getId());
	}

	public final Cube[] getCubes() {
		return ((DatabaseImpl)database).getCubes(this);
	}
	
//	public final Cube[] getCubes(int type) {
//		return ((DatabaseImpl)database).getCubes(this,type);
//	}
	
	
	public final Element getElementAt(int index) {
		return getDefaultHierarchy().getElementAt(index);
//		ElementInfo elInfo = elLoader.load(index);
//		return getElement(elInfo);
////
////		if (elementsNeedReload) {
////			reloadElementInfos();
////			elementsNeedReload = false;
////		}
////		int infosCount = elInfos.size();
////		if (index < infosCount && index >= 0) {
////			try {
////				Iterator it = elInfos.values().iterator();
////				for (int i = 0; i < index; ++i) {
////					if (it.hasNext())
////						it.next();
////				}
////				return it.hasNext() ? 
////						getElement((ElementInfo) it.next()): null;
////			} catch (PaloException pex) {
////				throw new PaloAPIException(pex);						
////			} catch (RuntimeException e) {
////				throw new PaloAPIException(e.getLocalizedMessage(), e);
////			}
////		}
////		return null; // new PaloAPIException("Element index is out of
////						// range!!");
	}

	public final Element getElementById(String id) {
		return getDefaultHierarchy().getElementById(id);
//		try {
//			ElementInfo elInfo = elLoader.load(id);
//			return getElement(elInfo);
//		} catch (PaloException pex) {
//			/* ignore */
//		}
//		return null;
//		
////		if (elementsNeedReload) {
////			reloadElementInfos();
////			elementsNeedReload = false;
////		}
////		return getElement((ElementInfo)elInfos.get(id));
	}
	
//	final Element getElementById(String id, Hierarchy hierarchy) {
//		try {
//			ElementInfo elInfo = elLoader.load(id);
//			return getElement(elInfo, hierarchy);
//		} catch (PaloException pex) {
//			/* ignore */
//		}
//		return null;		
//	}

	public final Element getElementByName(String name) {
		return getDefaultHierarchy().getElementByName(name);
//		ElementInfo elInfo = elLoader.loadByName(name);
//		return getElement(elInfo);
//
////		if (elementsNeedReload) {
////			reloadElementInfos();
////			elementsNeedReload = false;
////		}
////		Iterator it = elInfos.values().iterator();
////		while(it.hasNext()) {
////			ElementInfo elInfo = (ElementInfo)it.next();
////			if(elInfo.getName().equalsIgnoreCase(name)) 
////				return getElement(elInfo);
////		}
////        return null;
////
	}

	public final int getElementCount() {
		return getDefaultHierarchy().getElementCount();
//		return dimInfo.getElementCount();
//		/*if (elementsNeedReload) {
//			reloadElementInfos();
//			elementsNeedReload = false;
//		}
//		return elInfos.size();*/
	}

	public final String[] getElementNames() {
		return getDefaultHierarchy().getElementNames();
////		if (elementsNeedReload) {
////			reloadElementInfos();
////			elementsNeedReload = false;
////		}
////		String[] names = new String[elInfos.size()];
////		Iterator it = elInfos.values().iterator();
////		for(int i=0;i<names.length;++i) {
////			ElementInfo elInfo = (ElementInfo)it.next();
////			names[i] = elInfo.getName();
////		}
////		return names;
//		String[] ids = elLoader.getAllElementIds();
////		String[] names = new String[ids.length];
//		ArrayList<String> names = new ArrayList<String>();
//		for(int i=0; i<ids.length; ++i) {
//			try {
//			ElementInfo info = elLoader.load(ids[i]);
//			names.add(info.getName());
//			}catch(PaloException pex) {
//				/* ignore */
//			}
//		}
//		return names.toArray(new String[0]);
	}

	public final Element[] getElements() {
		return getDefaultHierarchy().getElements();
//		String[] ids = elLoader.getAllElementIds();
//		ArrayList<Element> elements= new ArrayList<Element>(); 	//to filter out null databases!! => TODO better thrown an exception here???
//		for(String id : ids) {
//			ElementInfo info = elLoader.load(id);
//			Element element = getElement(info);
//			if(element != null)
//				elements.add(element);
//		}
//		return (Element[])elements.toArray(new Element[elements.size()]);

//		if (elementsNeedReload) {
//			reloadElementInfos();
//			elementsNeedReload = false;
//		}
//		ArrayList elements = new ArrayList(); 	//to filter out null elements!!	
//		Iterator it = elInfos.values().iterator();
//		while(it.hasNext()) {
//			Element element = getElement((ElementInfo)it.next());
//			if(element != null)
//				elements.add(element);
//		}
//		return (Element[])elements.toArray(new Element[elements.size()]);
	}

	public final Element[] getElementsInOrder() {
		return getDefaultHierarchy().getElementsInOrder();
//		final ArrayList result = new ArrayList();
//		DimensionUtil.ElementVisitor visitor = 
//			new DimensionUtil.ElementVisitor() {
//				public void visit(Element element, Element parent) {
//					result.add(element);
//				}
//			};
//		Element roots[] = getRootElements();
//		if (roots != null) {
//			for (int i = 0; i < roots.length; ++i)
//				DimensionUtil.traverse(roots[i], visitor);
//		}
//		return (Element[]) result.toArray(new Element[0]);
	}

	public final ElementNode[] getElementsTree() {
		return getDefaultHierarchy().getElementsTree();
//		Element roots[] = getRootElements();
//		
//		if(ConnectionImpl.WPALO) {
//			ElementNode[] rootNodes = new ElementNode[roots.length];
//			for(int i=0;i<roots.length;i++)
//				rootNodes[i]= new ElementNode2(roots[i]);
//			return rootNodes;
//		} 
//		
//		//org:
//		final ArrayList rootnodes = new ArrayList();
//		ElementNodeVisitor visitor = new ElementNodeVisitor() {
//			public void visit(ElementNode node, ElementNode parent) {
//				if (parent == null)
//					rootnodes.add(node);
//			}
//		};		
//		if (roots != null) {
//			for (int i = 0; i < roots.length; ++i) {
//				ElementNode rootNode = new ElementNode(roots[i], null);
//				DimensionUtil.traverse(rootNode, visitor);
//			}
//		}
//		return (ElementNode[]) rootnodes.toArray(new ElementNode[0]);
	}

	public final int getExtendedType() {
		return DIMENSIONEXTENDEDTYPE_REGULAR;
	}

	public final String getName() {
		return dimInfo.getName();
	}

	public final Element[] getRootElements() {
		return getDefaultHierarchy().getRootElements();
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
//		
//		//org:
//		String[] ids = elLoader.getAllElementIds();
//		ArrayList<Element> roots = new ArrayList<Element>(); 	//to filter out null databases!! => TODO better thrown an exception here???
//		for(String id : ids) {
//			ElementInfo info = elLoader.load(id);
//			// TODO this is a hack to circumvent the Mondrian bug. As soon
//			// as parent-child hierarchies are fully supported by Mondrian,
//			// remove the "if info == null continue" statement.
//			if (info == null) {
//				continue;
//			}
//			if(info.getParentCount() == 0) {
//				Element element = getElement(info);
//				if(element != null)
//					roots.add(element);
//			}
//		}
//		return (Element[])roots.toArray(new Element[roots.size()]);

//		if (elementsNeedReload) {
//			reloadElementInfos();
//			elementsNeedReload = false;
//		}
//		ArrayList roots = new ArrayList();
//		Iterator it = elInfos.values().iterator();
//		while(it.hasNext()) {
//			ElementInfo elInfo = (ElementInfo)it.next();
//			if(elInfo.getParentCount()==0) {
//				Element rootElement = getElement(elInfo);
//				if(rootElement != null)
//					roots.add(rootElement);
//			}			
//		}
//		return (Element[]) roots.toArray(new Element[0]);
	}

	public final Subset getSubset(String id) {
		try {
			return legacySubsetsHandler.getSubset(this, id);
		} catch (PaloPersistenceException e) {
			e.printStackTrace();
		}
		return null;

//		return (Subset)subsets.get(id);
	}

	public final Subset[] getSubsets() {
		try {
			return legacySubsetsHandler.getSubsets(this);
		} catch(PaloPersistenceException e) {
			e.printStackTrace();			
		}
		return new Subset[0];
		
//		return (Subset[])subsets.values().toArray(new Subset[subsets.size()]);
	}

	public final SubsetHandler getSubsetHandler() {
		return subsetHandler;
	}
//	public final String[] getSubset2Ids() {
//		return new String[0];
////		Subset2StorageHandler subsetHandler = 
////			((DatabaseImpl)database).getSubsetHandler();		
////		try {
////			return subsetHandler.getAllSubsetIds(this);
////		}catch(PaloIOException pio) {
////			throw new PaloAPIException("Failed to load subset ids for this dimension!",pio);
////		}
//	}
//	
//	public final Subset2 getSubset2(String id) {
//		Subset2StorageHandler subsetHandler = 
//			((DatabaseImpl)database).getSubsetHandler();
//		return subsetHandler.getSubset(id, this);
////		try {
////			return subsetHandler.getSubset(id, this);
////		}catch(PaloIOException pio) {
////			throw new PaloAPIException("Failed to load subset for this dimension!",pio);
////		}catch(PaloException pex) {
////			return null;
////		}
//	}
	
	public final boolean isAttributeDimension() {
		return dimInfo.getType() == DimensionInfo.TYPE_ATTRIBUTE;
	}

	public final boolean isSubsetDimension() {
		return ((DatabaseImpl)database).isSubsetDimension(this);
	}

	public final Consolidation newConsolidation(Element element, Element parent,
			double weight) {
		return getDefaultHierarchy().newConsolidation(element, parent, weight);
//		return ConsolidationImpl.create((ConnectionImpl) getDatabase()
//				.getConnection(), parent, element, weight);
	}

	public final void removeAllAttributes() {
		getDefaultHierarchy().removeAllAttributes();
//		Dimension attrDim = getAttributeDimension();
//		if(attrDim == null)
//			return;
//		Element[] allAttrs = attrDim.getElements();
//		for (int i = 0; i < allAttrs.length; ++i)
//			attrDim.removeElement(allAttrs[i]);
//
//		// event:
//		fireAttributesRemoved(attributes.values().toArray());
//
//		attributes.clear();
	}

	public final void removeAttribute(Attribute attribute) {
		getDefaultHierarchy().removeAttribute(attribute);
//		Dimension attrDim = getAttributeDimension();
//		if(attrDim == null)
//			return;
//		Element attrElement = attrDim.getElementByName(attribute.getName());
//		if (attrElement != null) {
//			attrDim.removeElement(attrElement);
//			attributes.remove(attrElement);
//			// event:
//			fireAttributesRemoved(new Attribute[] { attribute });
//		}
	}

	public final void removeElement(Element element) {
		getDefaultHierarchy().removeElement(element);
//		if(removeElementInternal(element)) {
//			dbConnection.reload(dimInfo);
//			fireElementsRemoved(new Element[]{element});
//		}
	}
	
	public final void removeElements(Element[] elements) {
		getDefaultHierarchy().removeElements(elements);
//		ArrayList removedElements = new ArrayList();
//		for (int i = 0; i < elements.length; ++i) {
//			if (removeElementInternal(elements[i]))
//				removedElements.add(elements[i]);
//		}
//		if (!removedElements.isEmpty()) {
//			dbConnection.reload(dimInfo);
//			fireElementsRemoved((Element[]) removedElements
//					.toArray(new Element[removedElements.size()]));
//		}
	}

	public final void removeSubset(Subset subset) {
		legacySubsetsHandler.removeSubset(this, subset);
//		//PR 6520: first remove it from db then remove it from cache!! not vice versa...
//		ApiExtensionController.getInstance().delete(subset);
//		subsets.remove(subset.getId());
	}

	public final void rename(String name) {
    	String oldName = getName();
    	Dimension attrDim = getAttributeDimension();
    	String oldAttrDimName = attrDim != null ? attrDim.getName() : null;
    		
    	dbConnection.rename(dimInfo,name);

    	dimInfo.setName(name);
    	
    	Hierarchy hier = getHierarchyById(dimInfo.getId());
    	if (hier != null) {
    		hier.rename(name);
    	}
    	 
    	reloadRules();
        //create event:
    	fireDimensionsRenamed(new Dimension[] { this, attrDim }, new String[] {
				oldName, oldAttrDimName });
	}

	public final void renameElement(Element element, String newName) {
		getDefaultHierarchy().renameElement(element, newName);
	}

	public final void setAttributeValues(Attribute[] attributes, Element[] elements,
			Object[] values) {
		getDefaultHierarchy().setAttributeValues(attributes, elements, values);
//		if (attributes.length == elements.length
//				&& attributes.length == values.length) {
//			Cube attrCube = getAttributeCube();
//			Element[][] coordinates = getCoordinates(attributes, elements);
//			attrCube.setDataArray(
//					coordinates, values, Cube.SPLASHMODE_DISABLED);
//			// event:
//			fireAttributesChanged(attributes);
//
//		} else
//			throw new PaloAPIException(
//					"The number of attributes, elements and values has to be equal!");
	}

	public final void visitElementTree(ElementNodeVisitor visitor) {
        getDefaultHierarchy().visitElementTree(visitor);
//		Element roots[] = getRootElements();
//        if (roots != null) {
//			for (int i = 0; i < roots.length; ++i) {
//				ElementNode rootNode = new ElementNode(roots[i], null);
//				DimensionUtil.traverse(rootNode, visitor);
//			}
//		}
	}

	public final String getId() {
		return dimInfo.getId();
	}

	public final DimensionInfo getInfo() {
		return dimInfo;
	}
	
	public final boolean isSystemDimension() {
		return dimInfo.getType() == DimensionInfo.TYPE_SYSTEM;
	}

	public final boolean isUserInfoDimension() {
		return dimInfo.getType() == DimensionInfo.TYPE_INFO;
	}
	
	public final int getMaxDepth() {
		return getDefaultHierarchy().getMaxDepth();
		//return dimInfo.getMaxDepth();
	}


	public final int getMaxIndent() {		
		return dimInfo.getMaxIndent();
	}


	public final int getMaxLevel() {
		return getDefaultHierarchy().getMaxLevel();
		//return dimInfo.getMaxLevel();
	}


	public final boolean equals(Object other) {
		if(other instanceof DimensionImpl) {
			return key.equals(((DimensionImpl)other).key);
		}
		return false;
	}
	
	public final int hashCode() {
		return key.hashCode();
	}

	//--------------------------------------------------------------------------
	// PACKAGE INTERNAL
	//
    /**
     * Internal method.
     * <p>Returns the <code>Attribute</code> which is defined by the given
     * attribute <code>Element</code>.
     * </p> 
     */
//	final Attribute getAttribute(Element attrElement) {
//    	Object attribute = attributes.get(attrElement);
//    	if(attribute == null) {
//    		attribute = addAttribute(attrElement);
//    	}
//    	return (Attribute)attribute;
//    }

//	/**
//	 * Internal method, used during reloading of database
//	 */
//	final void removeAllSubsets() {
//		//do not remove subsets on palo server, cause then the updates are gone...
//		subsets.clear(); 
//	}

	public final void reload(boolean doEvents) {
		//loaders:
		propertyLoader.reset();
		hierarchyLoader.reset();
		
		//subsets:
		subsetHandler.reset();		
		((HierarchyImpl) getDefaultHierarchy()).reload(doEvents);

//		//reload dimension:
//		LinkedHashMap oldElements = new LinkedHashMap(loadedElements);
//		ArrayList addedElements = new ArrayList();
//		
//		//reload from server:		
//		dbConnection.reload(dimInfo);		
//
//		loadedElements.clear();
////		elementsNeedReload = true;
//		
//		//read in new elements
////		ElementInfo elementInfos[] = dbConnection.getElements(dimInfo);
////		for (int i = 0; i < elementInfos.length; ++i) {
////			ElementInfo elInfo = elementInfos[i];
////			ElementImpl element = (ElementImpl) oldElements.get(elInfo.getId());
////			if(element == null) {				
////				//have to create new element...
////				element = createElement(elInfo);
////			} else {
////				String id = element.getId();
////				//remove reused element...
////				oldElements.remove(id);
////				loadedElements.put(id,element);
////				elInfos.put(id,elInfo);
////			}
////			element.reload(doEvents);
////		}
//    	String[] elIDs = elLoader.getAllElementIds();
//    	for (String id : elIDs) {
//			ElementImpl element = (ElementImpl) oldElements.get(id);
//			if (element == null) {
//				ElementInfo info = elLoader.load(id);
//				element = createElement(info);
//			} else {
//				// remove reused element...
//				oldElements.remove(id);
//				loadedElements.put(id, element);
//			}
//			element.reload(doEvents);
//		}
//		
////		//have to reload after creation, since a consolidation can refer a
////		//children which is not loaded at this time 
////		//TODO we can do reload in one go, i.e. one for-loop above, when we support lazy loading...
////		for (int i = 0; i < elementInfos.length; ++i) {
////			ElementInfo elInfo = elementInfos[i];
////			ElementImpl element = (ElementImpl) elements.get(elInfo.getId());
////			if(element != null) 
////				element.reload(doEvents);
////		}
//		
//		if (doEvents) {			
////PR 6793: Collection#removeAll() is very slow for big collections of huge size		
////			LinkedHashSet removedElements = new LinkedHashSet(oldElements.values());
////			removedElements.removeAll(elements.values());
////			if (removedElements.size() > 0) {
////				fireElementsRemoved(removedElements.toArray());
////			}
////			LinkedHashSet addedElements = new LinkedHashSet(elements.values());
////			addedElements.removeAll(oldElements.values());
////			if (addedElements.size() > 0) {
////				fireElementsAdded(addedElements.toArray());
////			}
//			if (!oldElements.isEmpty()) {
//				fireElementsRemoved(oldElements.values().toArray());
//			}
//			if (!addedElements.isEmpty()) {
//				fireElementsAdded(addedElements.toArray());
//			}
//
//		}
	}

	final void clearCache() {
		((HierarchyImpl) getDefaultHierarchy()).clearCache();
		
		for(Property2Impl property : loadedProperties.values()) {
			property.clearCache();
		}
		loadedProperties.clear();
		propertyLoader.reset();
		
		//subsets:
		subsetHandler.reset();
	}

	/**only called on own rename or element rename*/  
	final void reloadRules() {
    	for(Cube cube : getCubes())
    		((CubeImpl)cube).reloadRuleInfos(false);
	}
//	final ElementLoader getElementLoader() {
//		return elLoader;
//	}
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
//	private final void reloadElementInfos() {		
//		ElementInfo[] _elInfos = dbConnection.getElements(dimInfo);
//		elInfos.clear();
//		for(int i=0;i<_elInfos.length;++i) {
//			elInfos.put(_elInfos[i].getId(),_elInfos[i]);
//		}
//	}

//	private final Attribute addAttribute(Element attrElement) {
////		Dimension attrDim = getAttributeDimension();
//		Cube attrCube = getAttributeCube(); //attrDim.getName());
//		Attribute attribute = AttributeImpl.create(attrElement, attrCube);
//		attributes.put(attrElement, attribute);
//		return attribute;
//	}

//    private final Element addElementInternal(String name, int type) {
//		try {
//			// WORKAROUND FOR A BUG IN WEB PALO!!!!
//			if (!connection.isLegacy())
//				type = ElementImpl.elType2infoType(type);
////			ElementInfo elInfo = dbConnection.addElement(dimInfo, name, type,
////					new ElementInfo[0], new double[0]);
//			ElementInfo elInfo = 
//				elLoader.create(name, type, new ElementInfo[0], new double[0]);
//			if (loadedElements.containsKey(elInfo.getId()))
//				throw new PaloAPIException("Element '" + name
//						+ "' already exists!!");
//			Element element = createElement(elInfo);
//			return element;
//			// TODO change element type in legacy connection implementation!
//		} catch (PaloException pex) {
//			throw new PaloAPIException(pex.getMessage(), pex);
//		}		
//	}

//    private final boolean removeElementInternal(Element element) {
//		if (loadedElements.containsKey(element.getId())) {
//			ElementImpl _element = (ElementImpl) element;
////			if (dbConnection.delete(_element.getInfo())) {
////				// reload its children and parents:
////				Element[] children = element.getChildren();
////				for (int i = 0; i < children.length; ++i)
////					((ElementImpl) children[i]).reload(false);
////				Element[] parents = element.getParents();
////				for (int i = 0; i < parents.length; ++i)
////					((ElementImpl) parents[i]).reload(false);
////
////				// finally remove it from dimension...
////				loadedElements.remove(element.getId());
////				elInfos.remove(element.getId());
////				return true;
////			}
//			if(elLoader.delete(_element.getInfo())) {
//				// reload its children and parents:
//				Element[] children = element.getChildren();
//				for (int i = 0; i < children.length; ++i)
//					((ElementImpl) children[i]).reload(false);
//				Element[] parents = element.getParents();
//				for (int i = 0; i < parents.length; ++i)
//					((ElementImpl) parents[i]).reload(false);
//
//				// finally remove it from dimension...
//				loadedElements.remove(element.getId());
////				reload(false);
//				return true;
//			}
//		}
//		return false;
//    }
    
//    private final Element[][] getCoordinates(Attribute[] attributes,Element[] elements) {
//		// determine the coordinates:
//		Element[][] coordinates = new Element[attributes.length][];
//		for (int i = 0; i < attributes.length; ++i) {
//			coordinates[i] = new Element[2];
//			coordinates[i][0] = 
//					((AttributeImpl) attributes[i]).getAttributeElement();
//			coordinates[i][1] = elements[i];
//		}
//		return coordinates;
//    }
    
	/**
	 * Checks if the corresponding element instance to the given elementinfo
	 * was already loaded and returns it. If no element instance was created 
	 * so far, this method will do it...
	 * @param elInfo
	 * @return
	 */
//	private final Element getElement(ElementInfo elInfo) {
//		if(elInfo == null)
//			return null;
//		Element element = (Element)loadedElements.get(elInfo.getId());
//		if(element == null) {
//			//not loaded yet...
//			element = createElement(elInfo);
//		}
//		return element;
//	}
	
//	private final Element getElement(ElementInfo elInfo, Hierarchy hier) {
//		if(elInfo == null)
//			return null;
//		Element element = (Element)loadedElements.get(elInfo.getId());
//		if(element == null) {
//			//not loaded yet...
//			element = createElement(elInfo, hier);
//		}
//		return element;
//	}

	/**
	 * Creates a new element instance from the given elementinfo and adds it to
	 * the list of all loaded elements
	 * @param elInfo
	 * @return
	 */
//	private final ElementImpl createElement(ElementInfo elInfo) {
//		ElementImpl element = ElementImpl.create(connection, this, elInfo);
//		loadedElements.put(element.getId(),element);
////		elInfos.put(elInfo.getId(),elInfo);
//		return element;
//	}

//	private final ElementImpl createElement(ElementInfo elInfo, Hierarchy hier) {
//		ElementImpl element = ElementImpl.create(connection, this, elInfo, hier);
//		loadedElements.put(element.getId(),element);
////		elInfos.put(elInfo.getId(),elInfo);
//		return element;
//	}

//	private final void fireAttributesAdded(Object[] attributes) {
//		connection.fireEvent(new ConnectionEvent(connection, this,
//				ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_ADDED, attributes));
//	}
//    
//    private final void fireAttributesChanged(Object[] attributes) {
//		connection.fireEvent(new ConnectionEvent(
//				connection, this,
//				ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_CHANGED,
//				attributes));
//    }
//    
//    private final void fireAttributesRemoved(Object[] attributes) {
//		connection.fireEvent(new ConnectionEvent(
//				connection, this,
//				ConnectionEvent.CONNECTION_EVENT_ATTRIBUTES_REMOVED,
//				attributes));
//	}
    
    private final void fireDimensionsRenamed(Object[] dimensions,
			Object oldValue) {
		ConnectionEvent ev = new ConnectionEvent(getDatabase().getConnection(),
				getDatabase(),
				ConnectionEvent.CONNECTION_EVENT_DIMENSIONS_RENAMED, dimensions);

		ev.oldValue = oldValue;
		connection.fireEvent(ev);
	}
    
//    private final void fireElementsAdded(Object[] elements) {
//		connection.fireEvent(new ConnectionEvent(connection, this,
//				ConnectionEvent.CONNECTION_EVENT_ELEMENTS_ADDED, elements));
//	}
//    
//    private final void fireElementsRemoved(Object[] elements) {
//		connection.fireEvent(new ConnectionEvent(connection, this,
//				ConnectionEvent.CONNECTION_EVENT_ELEMENTS_REMOVED, elements));
//	}
    
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
		return dimInfo.canBeModified();
	}

	public boolean canCreateChildren() {
		return dimInfo.canCreateChildren();
	}

	public int getType() {
		if (dimInfo.getType() == DimensionInfo.TYPE_NORMAL) {
			return TYPE_NORMAL;
		} else if (dimInfo.getType() == DimensionInfo.TYPE_ATTRIBUTE) {
			return TYPE_ATTRIBUTE;
		} else if (dimInfo.getType() == DimensionInfo.TYPE_SYSTEM) {
			return TYPE_SYSTEM;
		} else if (dimInfo.getType() == DimensionInfo.TYPE_INFO) {
			return TYPE_USER_INFO;
		} 
		return -1;
	}
	
	public Hierarchy getDefaultHierarchy() {
		if (dimInfo == null || dimInfo.getDefaultHierarchy() == null) {
			return null;
		}
		return getHierarchyById(dimInfo.getDefaultHierarchy().getId());
	}

	public Hierarchy[] getHierarchies() {
		ArrayList <Hierarchy> hierarchies = new ArrayList<Hierarchy>();
		for (String hier: getHierarchiesIds()) {
			hierarchies.add(getHierarchyById(hier));
		}
		return hierarchies.toArray(new Hierarchy[0]);
	}

	public String[] getHierarchiesIds() {		
		String [] ids = new String[dimInfo.getHierarchies().length];
		int counter = 0;
		for (HierarchyInfo hier: dimInfo.getHierarchies()) {
			ids[counter++] = hier.getId();
		}
		return ids;
	}

	public Hierarchy getHierarchyAt(int index) {
		return getHierarchies()[index];
	}

	public Hierarchy getHierarchyById(String id) {
		if (!loadedHierarchies.containsKey(id)) { 
			HierarchyInfo hierInfo = hierarchyLoader.load(id);		
			HierarchyImpl hier =	
				HierarchyImpl.create(connection, this, hierInfo, true);
			loadedHierarchies.put(id, hier);
		}
		return loadedHierarchies.get(id);
	}
	
	public final Hierarchy getHierarchyByName(String name) {
		Hierarchy [] hiers = getHierarchies();
		for (Hierarchy hier: hiers) {
			if (hier.getName().equals(name)) {
				return hier;
			}
		}
		return null;
	}
	
//	final void reloadAll() {
//		dbConnection.reload(dimInfo);
//		((HierarchyImpl)getDefaultHierarchy()).reloadAllElementInfos();
//	}
	
	final void reloadHierarchyInfos() {		
		HierarchyInfo [] _hierInfos = 
			dbConnection.getHierarchies(dimInfo);
		hierarchyInfos.clear();
		for (int i = 0; i < _hierInfos.length; ++i) {
			hierarchyInfos.put(_hierInfos[i].getId(), _hierInfos[i]);
		}
	}	
	
	public int getHierarchyCount() {
		return hierarchyLoader.getHierarchyCount();		
	}
}
