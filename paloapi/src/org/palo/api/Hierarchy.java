/*
*
* @file Hierarchy.java
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
* @version $Id: Hierarchy.java,v 1.17 2010/01/12 14:37:04 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.api;

import org.palo.api.subsets.SubsetHandler;

/**
 * <code>Hierarchy</code>
 * A hierarchy organizes dimensions with different element consolidations in a
 * cube. For Palo, different consolidations in a dimension are not allowed and
 * thus each hierarchy maps to exactly one dimension and they have identical
 * ids. If a dimension is removed, renamed or added, the mapping stays
 * consistent.
 * In xmla, a dimension can contain several hierarchies. Since
 * XMLA is currently read-only, hierarchies cannot be added or removed.
 *
 * @author Philipp Bouillon
 * @version $Id: Hierarchy.java,v 1.17 2010/01/12 14:37:04 PhilippBouillon Exp $
 **/
public interface Hierarchy extends PaloObject {
	/**
	 * Returns the parent dimension of this hierarchy.
	 * @return the parent dimension of this hierarchy.
	 */
	Dimension getDimension();
	    
    /**
     * Returns if this hierarchy is a normal hierarchy (as opposed to being
     * a system hierarchy). A hierarchy is a system hierarchy, when its
     * dimension is a system dimension. 
     * 
     * @return true if the hierarchy is a non-system hierarchy, false otherwise.
     */
	boolean isNormal();
    
	/**
	 * Renames this hierarchy.
	 * 
	 * @param name the the name for the hierarchy.
	 */
	void rename(String name);
		
    /**
     * Returns the number of {@link Element}s of this instance.
     * Note that a single hierarchy with consolidated elements might
     * consolidate a particular element more than once.
     * 
     * @return the number of {@link Element}s of this instance.
     */
	int getElementCount();
	
    /**
     * Returns an array of {@link Element} instances available
     * for this instance.
     * <p>The returned array is a copy of the internal data structure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return an array of {@link Element} instances available
     * for this <code>Hierarchy</code>.
     */	
	Element [] getElements();
	
    /**
     * Visits the element-tree of this hierarchy.
     * @param visitor the visitor-callback to invoke
     * during traversal.
     */	
	void visitElementTree(ElementNodeVisitor visitor);

    /**
     * Returns all root-elements (those without parents in the 
     * consolidation-hierarchy).
     * 
     * @return all root elements of the hierarchy.
     */
	Element[] getRootElements();
	
    /**
     * Returns the root-nodes of the element-tree.
     * @return the root-nodes of the element-tree.
     */
	ElementNode[] getElementsTree();
	
    /**
     * Returns the {@link Element} stored at the given index.
     * If the index does not correspond to a legal position
     * in the internally managed array of elements of this
     * instance, then <code>null</code> is returned.
     * @param index the index
     * @return the {@link Element} stored at the given index
     * or <code>null</code>.
     */
	Element getElementAt(int index);
	
    /**
     * Returns the names of the elements of this <code>Hierarchy</code>.
     * <p>The returned array is a copy of the internal data structure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return the names of the {@link Element}s instances available
     * for this <code>Dimension></code>.
     */
	String [] getElementNames();
	
    /**
     * Returns the {@link Element} stored under the given name or
     * <code>null</code> if no such {@link Element} exists.
     * @param name the element-name to look-up.
     * @return the {@link Element} stored under the given name or
     * <code>null</code> if no such {@link Element} exists.
     */
	Element getElementByName(String name);
	
    /**
     * Returns the {@link Element} stored under the given id or
     * <code>null</code> if no such {@link Element} exists.
     * @param id the element-id to look-up.
     * @return the {@link Element} stored under the given id or
     * <code>null</code> if no such {@link Element} exists.
     */
    Element getElementById(String id);
    
    /**
     * Batch adds many elements.
     * 
     * @param names the names of the elements to add.
     * @param types the types of the elements to add.
     */
    void addElements(String names[], int types[]);   
    
    void addElements(String[] names, int type, Element[][] children, double[][] weights);
    
    /**
     * Available for Palo Server version 3 and above.
     * @param names
     * @param types
     * @param children
     * @param weights
     */
    void addElements(String[] names, int [] types, Element[][] children, double[][] weights);
    
    /**
     * Available for Palo Server version 3 and above.
     * @param elements
     * @param consolidations
     */
    void updateConsolidations(Consolidation [] consolidations);
    
    /**
     * Available for Palo Server version 3 and above.
     * @param elements
     */
    void removeConsolidations(Element [] elements);
    
    /**
     * Returns all elements of the hierarchy in an order that is
     * determined by the consolidation hierarchy. Since elements
     * can be consolidated multiple times, this order is not
     * always the same as a pre-order traversal of the directed
     * acyclic graph of consolidations, but similar. 
     * @return the elements of the hierarchy in order.
     */
    Element[] getElementsInOrder();
    
    /**
     * Returns an array of all element nodes of the 
     * consolidation hierarchy of this hierarchy.
     * @return an array of all element nodes of this hierarchy.
     */
    ElementNode[] getAllElementNodes();
    
    /**
     * Adds an {@link Element} to this <code>Hierarchy</code>.
     * @param name the name of the element to add.
     * @param type the type of the element to add as defined by the constants
     * in the {@link Element} class.
     */
    Element addElement(String name, int type);
    
    /**
     * Removes the given {@link Element} from this <code>Hierarchy</code>
     * @param element the {@link Element} to remove.
     */
    void removeElement(Element element);
    
    /**
     * Removes the given {@link Element}s from this <code>Hierarchy</code>
     * @param elements {@link Element}s to remove.
     */
    void removeElements(Element[] elements);
    
    /**
     * Renames given <code>Element</code>.
     * @param element the <code>Element</code> to rename.
     * @param newName the new name for this <code>Element</code>.
     */
    void renameElement(Element element, String newName);
    
    /**
     * Creates a {@link Consolidation} for later use in this hierarchy.
     * @param element the {@link Element} to consolidate.
     * @param parent the parent-{@link Element} of the consolidation.
     * @param weight the consolidation weight.
     * @return the created {@link Consolidation} object.
     */
    Consolidation newConsolidation(Element element, Element parent, double weight);    

    /**
	 * Returns the maximum level of this hierarchy. Please refer to 
	 * {@link Element#getLevel()} for an explanation of how the level is 
	 * determined 
	 * @return maximum level of this hierarchy
	 */
	public int getMaxLevel();
    
	/**
	 * Returns the maximum depth of this hierarchy. Please refer to 
	 * {@link Element#getDepth()} for an explanation of how the depth is
	 * determined
	 * @return maximum depth of this hierarchy
	 */
	public int getMaxDepth();
	
    /**
     * Checks if this <code>Hierarchy</code> is an attribute hierarchy, i.e.
     * its elements represent <code>Attribute</code>s.
     * @return true if this hierarchy is an attribute hierarchy, false otherwise
     */
    boolean isAttributeHierarchy();
    
    /**
     * Checks if this <code>Hierarchy</code> is a subset hierarchy, i.e.
     * its elements represent <code>Subset</code>s.
     * @return true if hierarchy is a subset hierarchy, false otherwise
     */
    boolean isSubsetHierarchy();

    /**
     * Creates a new {@link Attribute} and adds it to the hierarchy. 
     * <p>
     * <b>Note:</b> adding an attribute to a hierarchy which is itself of type 
     * attribute hierarchy is prohibited and will result in a 
     * {@link PaloAPIException}.
     * </p> 
     * @param name the name of the new attribute
     * @return the newly created <code>Attribute</code>
     */
    Attribute addAttribute(String name);
    
    /**
     * Removes the given <code>Attribute</code> from the hierarchy
     * @param attribute the attribute instance to remove
     */
    void removeAttribute(Attribute attribute);

    /**
     * Removes all attributes from the hierarchy
     */
    void removeAllAttributes();
    
    /**
     * Returns all <code>Attribute</code>s this hierarchy has.
     * @return the <code>Attribute</code>s of this hierarchy 
     */
    Attribute[] getAttributes();

    /**
     * Returns the <code>Attribute</code> which corresponds to the given id or
     * <code>null</code> if no <code>Attribute</code> with this id exists
     * @param id identifier of the <code>Attribute</code> to get 
     * @return the corresponding <code>Attribute</code> or <code>null</code>
     */
    Attribute getAttribute(String id);

    /**
     * Returns the <code>Attribute</code> which corresponds to the given name or
     * <code>null</code> if no <code>Attribute</code> with this name exists
     * @param name the name of the <code>Attribute</code> to get 
     * @return the corresponding <code>Attribute</code> or <code>null</code>
     */
    Attribute getAttributeByName(String name);

	/**
	 * Convenient method to set the values for several <code>Attribute</code>s
	 * at once, i.e. the i.th value is assigned to the i.th attribute for the 
	 * i.th element.<br>
	 * <b>Note:</b> if the attributes, elements and values arrays do not have 
	 * same length an {@link PaloAPIException} is thrown.
	 * @param attributes the attributes to set the values for
	 * @param elements the effected elements
	 * @param values the new values
	 */
	void setAttributeValues(Attribute[] attributes, Element[] elements,
			Object[] values);

	/**
	 * Convenient method to receive the values from several 
	 * <code>Attribute</code>s in one go, i.e. the i.th object in the returned
	 * array is the i.th value of the i.th attribute for the i.th element.<br>
	 * <b>Note:</b> if the attributes and elements arrays do not have same 
	 * length an {@link PaloAPIException} is thrown.
	 * @param attributes the attributes to get the values from
	 * @param elements the effected elements
	 * @return the attribute values
	 */
	Object[] getAttributeValues(Attribute[] attributes, Element[] elements);

    /**
     * Returns the attribute {@link Cube} corresponding to this hierarchy or 
     * <code>null</code> if the attribute cube couldn't be loaded or does 
     * not exist.
     * <p> 
     * <b>Note:</b> this is a convenient method to provide raw access to the 
     * internal attribute handling. Its usage is not recommended!!
     * </p>
     * @return the attribute cube
     */
    Cube getAttributeCube();

    /**
     * Returns the corresponding attribute {@link Hierarchy} or 
     * <code>null</code> if the attribute hierarchy couldn't be loaded or does 
     * not exist.
     * <p> 
     * <b>Note:</b> this is a convenient method to provide raw access to the 
     * internal attribute handling. Its usage is not recommended!!
     * </p>
     * @return the attribute hierarchy
     */
    Hierarchy getAttributeHierarchy();	
    
    //--------------------------------------------------------------------------
    // SUBSET SUPPORT
    //

    /**
	 * Adds a new {@link Subset} with the given name to the hierarchy.
	 * @param name the name of the subset
	 * @return the new subset
	 * @deprecated old subsets are not supported anymore, please instead add
	 *             subsets via {@link #getSubsetHandler()} and its appropriate
	 *             methods
	 */
    Subset addSubset(String name);
    
    /**
     * Removes the given subset from the dimension
     * @param subset the subset to remove
     */
    void removeSubset(Subset subset);
    
    /**
     * Returns all subsets currently registered with this hierarchy
     * @return the registered subsets
     */    
    Subset[] getSubsets();
    
    /**
     * Returns the subset which is registered with the given id or null if no
     * subset with this id could be found
     * @param id the subset id
     * @return the corresponding subset instance
     */
    Subset getSubset(String id);
    				
	//-------------------------------------------------------------------------
	// NEW SUBSET API
	//
	/**
	 * Returns the <code>SubsetHandler</code> for managing the new subsets.
	 * Please use {@link Database#supportsNewSubsets()} to check if new 
	 * subsets are supported.
	 * @return the <code>SubsetHandler</code> for managing new subsets
	 */
	SubsetHandler getSubsetHandler();
	
	
	/**
	 * Reloads the internal hierarchy structure from database.
	 * @param fireEvents specify <code>true</code> to get event notification on
	 * hierarchy changes or <code>false</code> otherwise 
	 */
	public void reload(boolean fireEvents);
}
