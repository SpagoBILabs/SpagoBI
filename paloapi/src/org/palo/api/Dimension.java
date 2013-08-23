/*
*
* @file Dimension.java
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
* @author Stepan Rutz
*
* @version $Id: Dimension.java,v 1.55 2010/01/12 14:37:04 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

import org.palo.api.subsets.SubsetHandler;

import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ServerInfo;



/**
 * <code>Dimension</code>.
 *
 * <p>Dimensions are defined inside a database. Out of the set of defined
 * dimensions of a database, cubes can be created by associating these
 * dimensions to form a cube. A {@link org.palo.api.Cube} is
 * capable of storing data.
 * </p>
 * 
 * <p>
 * A single dimension can be shared in multiple cubes.
 * </p>
 * 
 * <p>
 * Within the scope of a single parent database, a dimension 
 * is uniquely identified by its name as returned by 
 * {@link #getName()}.
 * </p>
 * 
 * <p>
 * A dimension is made up of distinct {@link org.palo.api.Element}s.
 * Dimension-information and domain-objects can be retrieved from a
 * dimensino instance by
 * invoking the following methods.
 * <ul>
 * <li><p>{@link #getElements()}</p></li>
 * <li><p>{@link #getElementCount()}</p></li>
 * <li><p>{@link #getElementAt(int)}</p></li>
 * <li><p>{@link #getElementByName(String)}</p></li>
 * <li><p>{@link #getRootElements()}</p></li>
 * </ul>
 * </p>
 *
 * @author Stepan Rutz
 * @version $ID$
 * 
 * @see org.palo.api.PaloAPIException
 */
public interface Dimension extends PaloObject
{
    
    /**
     * Constants for dimension-type
     */
    public static final int
        DIMENSIONEXTENDEDTYPE_REGULAR = 0,
        DIMENSIONEXTENDEDTYPE_VIRTUAL = 1;
//    	DIMENSIONTYPE_NORMAL = 1,
//    	DIMENSIONTYPE_SYSTEM = 2,
//    	DIMENSIONTYPE_ATTRIBUTE = 4,
//    	DIMENSIONTYPE_USERINFO = 8;
        
    /**
     * Returns the extended-type of this <code>Dimension</code>.
     * @return the extended-type of this <code>Dimension</code>.
     */
    int getExtendedType();
    
    int getType();
    
    /**
     * Returns the name of this <code>Dimension</code>
     * @return the name of this <code>Dimension</code>.
     */
    String getName();
    
    /**
     * Returns the parent {@link Database} of this instance.
     * @return the parent {@link Database} of this instance.
     */
    Database getDatabase();
    
    /**
     * Returns the number of {@link Element}s of this instance.
     * Note that a single dimension with consolidated elements might
     * consolidate a particular element more than once.
     * 
     * @return the number of {@link Element}s of this instance.
     * @deprecated use {@link Hierarchy#getElementCount()} instead.
     */
    int getElementCount();
    
//    boolean hasElements();
    
    
    /**
     * Returns the {@link Element} stored at the given index.
     * If the index does not correspond to a legal position
     * in the internally managed array of elements of this
     * instance, then <code>null</code> is returned.
     * @param index the index
     * @return the {@link Element} stored at the given index
     * or <code>null</code>.
     * @deprecated use {@link Hierarchy#getElementAt(int)} instead.
     */
    Element getElementAt(int index);
    
    /**
     * Returns an array of {@link Element} instances available
     * for this instance.
     * <p>The returned array is a copy of the internal datastructure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return an array of {@link Element} instances available
     * for this <code>Dimension</code>.
     * @deprecated use {@link Hierarchy#getElements()} instead.
     */
    Element[] getElements();
    
    /**
     * Returns the names of the elements of this <code>Dimension</code>.
     * <p>The returned array is a copy of the internal datastructure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return the names of the {@link Element}s instances available
     * for this <code>Dimension></code>.
     * @deprecated use {@link Hierarchy#getElementNames()} instead.
     */
    String[] getElementNames();
    
    /**
     * Returns the {@link Element} stored under the given name or
     * <code>null</code> if no such {@link Element} exists.
     * @param name the element-name to look-up.
     * @return the {@link Element} stored under the given name or
     * <code>null</code> if no such {@link Element} exists.
     * @deprecated use {@link Hierarchy#getElementByName()} instead.
     */
    Element getElementByName(String name);
    
    /**
     * Returns the {@link Element} stored under the given id or
     * <code>null</code> if no such {@link Element} exists.
     * @param id the element-id to look-up.
     * @return the {@link Element} stored under the given id or
     * <code>null</code> if no such {@link Element} exists.
     * @deprecated use {@link Hierarchy#getElementById()} instead.
     */
    Element getElementById(String id);
    
    /**
     * Renames this <code>Dimension</code>.
     * @param name the new name for this <code>Dimension</code>.
     */
    void rename(String name);
    
    /**
     * Batch adds many elements.
     * 
     * @param names the names of the elements to add.
     * @param types the types of the elements to add.
     * @deprecated use {@link Hierarchy#addElements(String [], int []) instead.
     */
    void addElements(String names[], int types[]);
    void addElements(String[] names, int type, Element[][] children, double[][] weights);
    void addElements(String[] names, int [] types, Element[][] children, double[][] weights);
    void updateConsolidations(Consolidation [] consolidations);
    void removeConsolidations(Element [] elements);
    
    //-------------------------------------------------------------------------
    // Element/Consolidation Hierarchies
    
    /**
     * Returns all root-elements (those without parents in the 
     * consolidation-hierarchy).
     * 
     * @return all root elements of the dimension.
     * @deprecated use {@link Hierarchy#getRootElements()} instead.
     */
    Element[] getRootElements();
    
    /**
     * Returns all elements of the dimension in an order that is
     * determined by the consolidation hierarchy. Since elements
     * can be consolidated multiple times, this order is not
     * always the same as a pre-order traversal of the directed
     * acyclic graph of consolidations, but similar. 
     * @return the elements of the dimension in order.
     * @deprecated use {@link Hierarchy#getElementsInOrder()} instead.
     */
    Element[] getElementsInOrder();
    
    /**
     * Returns the root-nodes of the element-tree.
     * @return the root-nodes of the element-tree.
     * @deprecated use {@link Hierarchy#getElementsTree()} instead.
     */
    ElementNode[] getElementsTree();
    
    /**
     * Visits the element-tree of this dimension.
     * @param visitor the visitor-callback to invoke
     * during traversal.
     * @deprecated use {@link Hierarchy#visitElementTree()} instead.
     */
    void visitElementTree(ElementNodeVisitor visitor);
    
    /**
     * Returns an array of all element-nodes of the 
     * consolidation-hierarchy of this dimension.
     * @return an array of all element-nodes of this dimension.
     * @deprecated use {@link Hierarchy#getAllElementNodes()} instead.
     */
    ElementNode[] getAllElementNodes();
    
    /**
     * Debug only method. Provides some unspecified feedback to std-err.
     * @deprecated for internal use only. Please do not use.
     */
    void dumpElementsTree();
    
    //-------------------------------------------------------------------------
    // Admin
    /**
     * Adds an {@link Element} to this <code>Dimension</code>.
     * @param name the name of the element to add.
     * @param type the type of the element to add as defined by the constants
     * in the {@link Element} class.
     * @deprecated use {@link Hierarchy#addElement(String, int)} instead.
     */
    Element addElement(String name, int type);
    
    /**
     * Removes the given {@link Element} from this <code>Dimension</code>
     * @param element the {@link Element} to remove.
     * @deprecated use {@link Hierarchy#removeElement(Element)} instead.
     */
    void removeElement(Element element);
    /**
     * Removes the given {@link Element}s from this <code>Dimension</code>
     * @param elements {@link Element}s to remove.
     * @deprecated use {@link Hierarchy#removeElements(Element [])} instead.
     */
    void removeElements(Element[] elements);
    
    /**
     * Renames given <code>Element</code>.
     * @param element the <code>Element</code> to rename.
     * @param newName the new name for this <code>Element</code>.
     * @deprecated use {@link Hierarchy#renameElement(Element, String)} instead.
     */
    void renameElement(Element element, String newName);
    
    /**
     * Creates a {@link Consolidation} for later use in this dimension.
     * @param element the {@link Element} to consolidate.
     * @param parent the parent-{@link Element} of the consolidation.
     * @param weight the consolidation weight.
     * @return the created {@link Consolidation} object.
     * @deprecated use {@link Hierarchy#newConsolidation(Element, Element, double)}
     * instead.
     */
    Consolidation newConsolidation(Element element, Element parent, double weight);

    //--------------------------------------------------------------------------
    //PALO 1.5 - PART OF ATTRIBUTE API...
    //
    /**
     * Checks if this <code>Dimension</code> is an attribute dimension, i.e.
     * its elements represent <code>Attribute</code>s.
     * @return true if dimension is an attribute dimension, false otherwise
     */
    boolean isAttributeDimension();
    
    /**
     * Creates a new {@link Attribute} and adds it to the dimension. 
     * <p>
     * <b>Note:</b> adding an attribute to a dimension which is itself of type 
     * attribute dimension is prohibited and will result in a 
     * {@link PaloAPIException}.
     * </p> 
     * @param name the name of the new attribute
     * @return the newly created <code>Attribute</code>
     * @deprecated use {@link Hierarchy#addAttribute(String)} instead.
     */
    Attribute addAttribute(String name);
    
    /**
     * Removes the given <code>Attribute</code> from the dimension
     * @param attribute the attribute instance to remove
     * @deprecated use {@link Hierarchy#removeAttribute(Attribute)} instead.
     */
    void removeAttribute(Attribute attribute);

    /**
     * Removes all attributes from the dimension
     * @deprecated use {@link Hierarchy#removeAllAttributes()} instead.
     */
    void removeAllAttributes();
    
    /**
     * Returns all <code>Attribute</code>s this dimension has.
     * @return the <code>Attribute</code>s of this dimension 
     * @deprecated use {@link Hierarchy#getAttributes()} instead.
     */
    Attribute[] getAttributes();

    /**
     * Returns the <code>Attribute</code> which corresponds to the given id or
     * <code>null</code> if no <code>Attribute</code> with this id exists
     * @param id identifier of the <code>Attribute</code> to get 
     * @return the corresponding <code>Attribute</code> or <code>null</code>
     * @deprecated use {@link Hierarchy#getAttribute(String)} instead.
     */
    Attribute getAttribute(String id);
    
    /**
     * Returns the <code>Attribute</code> which corresponds to the given name or
     * <code>null</code> if no <code>Attribute</code> with this name exists
     * @param name the name of the <code>Attribute</code> to get 
     * @return the corresponding <code>Attribute</code> or <code>null</code>
     * @deprecated use {@link Hierarchy#getAttributeByName(String)} instead.
     */
    Attribute getAttributeByName(String name);
    
//    /**
//     * Convenient method to set the value for the given <code>Attribute</code> 
//     * and given <code>Element</code> 
//     * @param attribute the attribute to set the value for
//     * @param element the element which is effected
//     * @param value the new attribute value
//     */
//	void setAttributeValue(Attribute attribute, Element element, Object value);
//
//	/**
//	 * Convenient method to receive the value for the given 
//	 * <code>Attribute</code> and <code>Element</code>
//	 * @param attribute the attribute to get the value from
//	 * @param element the effected element
//	 * @return the attribute value
//	 */
//	Object getAttributeValue(Attribute attribute, Element element);

	/**
	 * Convenient method to set the values for several <code>Attribute</code>s
	 * at once, i.e. the i.th value is assigned to the i.th attribute for the 
	 * i.th element.<br>
	 * <b>Note:</b> if the attributes, elements and values arrays do not have 
	 * same length an {@link PaloAPIException} is thrown.
	 * @param attributes the attributes to set the values for
	 * @param elements the effected elements
	 * @param values the new values
	 * @deprecated use {@link Hierarchy#setAttributeValues(Attribute [], Element [], Object [])}
	 * instead.
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
	 * @deprecated use {@link Hierarchy#getAttributeValues(Attribute [], Element [])}
	 * instead. 
	 */
	Object[] getAttributeValues(Attribute[] attributes, Element[] elements);

    /**
     * Returns the attribute {@link Cube} corresponding to this dimension or 
     * <code>null</code> if the attribute cube couldn't be loaded or does 
     * not exist.
     * <p> 
     * <b>Note:</b> this is a convenient method to provide raw access to the 
     * internal attribute handling. Its usage is not recommended!!
     * </p>
     * @return the attribute cube
     * @deprecated use {@link Hierarchy#getAttributeCube()} instead.
     */
    Cube getAttributeCube();

    /**
     * Returns the corresponding attribute {@link Dimension} or 
     * <code>null</code> if the attribute dimension couldn't be loaded or does 
     * not exist.
     * <p> 
     * <b>Note:</b> this is a convenient method to provide raw access to the 
     * internal attribute handling. Its usage is not recommended!!
     * </p>
     * @return the attribute dimension
     * @deprecated use {@link Hierarchy#getAttributeHierarchy()} instead.
     */
    Dimension getAttributeDimension();	
    
    //--------------------------------------------------------------------------
    // SUBSET SUPPORT
    //
    /**
     * Checks if this <code>Dimension</code> is a subset dimension, i.e.
     * its elements represent <code>Subset</code>s.
     * @return true if dimension is a subset dimension, false otherwise
     * @deprecated use {@link Hierarchy#isSubsetHierarchy()} instead.
     */
    boolean isSubsetDimension();

    /**
	 * Adds a new {@link Subset} with the given name to the dimension.
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
     * Returns all subsets currently registered with this dimension
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
    
	/**
	 * Returns whether this dimension is a system dimension.
	 *  
	 * @return true if this dimension is a system dimension, false otherwise.
	 */
    public boolean isSystemDimension();
	
	/**
	 * Returns whether this dimension is a user info dimension.
	 *  
	 * @return true if this dimension is a user info dimension, false otherwise.
	 */
	public boolean isUserInfoDimension();
	
	/**
	 * Returns the maximum level of this dimension. Please refer to 
	 * {@link Element#getLevel()} for an explanation of how the level is 
	 * determined 
	 * @return maximum level of this dimension
	 * @deprecated use {@link Hierarchy#getMaxLevel()} instead.
	 */
	public int getMaxLevel();
//	public int getMaxIndent();
	
	/**
	 * Returns the maximum depth of this dimension. Please refer to 
	 * {@link Element#getDepth()} for an explanation of how the depth is
	 * determined
	 * @return maximum depth of this dimension
	 * @deprecated use {@link Hierarchy#getMaxDepth()} instead.
	 */
	public int getMaxDepth();
	
	/**
	 * Returns all cubes which use this dimension. Please note that the 
	 * returned cubes do not contain any attribute, system or info cubes.   
	 * @return all cubes which use this dimension
	 */
	public Cube[] getCubes();
	
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
	
//    /**
//     * Adds a new {@link Subset2} with the given name to this dimension
//     * @param name the name of the subset
//     * @return the new subset
//     * @deprecated PLEASE DO NOT USE! SUBJECT TO CHANGE!!!
//     */
//    Subset2 addSubset2(String name, boolean global);
//	/**
//	 * 
//	 * @deprecated API internal! Please do not use!! For adding subset use 
//	 * {@link #addSubset2(String, boolean)} instead
//	 */
//	public Subset2 addSubset2(String id, String name, boolean global);    
//    /**
//     * Returns all {@link Subset2}s which are currently registered with this
//     * dimension. This method tries to load all locally and globally defined 
//     * subsets.
//     * @return the registered subsets
//     * @deprecated PLEASE DO NOT USE! SUBJECT TO CHANGE!!!
//     */
//    Subset2[] getSubsets2();
//    /**
//     * Returns all globally or locally {@link Subset2}s which are registered with
//     * this dimension. The global flag determines which subsets to load 
//     * @param global set to <code>true</code> to load globally defined subsets,
//     * specify <code>false</code> to load locally defined subsets.
//     * @return
//     */
//	Subset2[] getSubsets2(boolean global) throws InsufficientRightsException;
//	
//    /**
//     * Returns the <code>Subset2</code> instance which is registered with the 
//     * given id or null if no subset with this id could be found
//     * @param id the subset id
//     * @return the corresponding subset instance
//     * @deprecated PLEASE DO NOT USE! SUBJECT TO CHANGE!!!
//     */
//    Subset2 getSubset2(String id);
//    
//    void removeSubset(Subset2 subset);

    /**
     * Returns all hierarchies of this dimension.
     * 
     * @return all hierarchies of this dimension.
     */
	Hierarchy [] getHierarchies();
    
	/**
     * Returns the number of hierarchies in this dimension. For palo
     * connections, the number of hierarchies equals one, for xmla
     * connections, the count may differ as dimensions may have more than one
     * hierarchy.
     * 
     * @return the number of hierarchies for this dimension.
     */
	int getHierarchyCount();

	/**
	 * Returns the ids of all hierarchies of this dimension.
	 * 
	 * @return the ids of all hierarchies of this dimension.
	 */
	String [] getHierarchiesIds();

	/**
     * Returns the hierarchy at the given index.
     * 
     * @param index the index of the hierarchy.
     * @return the hierarchy at the specified index.
     */	
	Hierarchy getHierarchyAt(int index);
	
    /**
     * Returns the hierarchy specified by the given id.
     * @param id the id of the hierarchy. 
     * @return the hierarchy specified by the given id.
     */
	Hierarchy getHierarchyById(String id);
	
	/**
	 * Returns the hierarchy with the specified name or <code>null</code> if
	 * no such hierarchy exists.
	 * 
	 * @param name the name of the hierarchy.
	 * @return the hierarchy with the specified name or <code>null</code> if
	 * no such hierarchy exists.
	 */
	Hierarchy getHierarchyByName(String name);
	
	/**
	 * Returns the default hierarchy of this dimension.
	 * @return the default hierarchy of this dimension.
	 */
	Hierarchy getDefaultHierarchy();
	
    /**
     * Returns additional information about the dimension.
     * @return additional information about the dimension.
     */
	DimensionInfo getInfo();
	
	/**
	 * Reloads the internal dimension structure from database.
	 * @param fireEvents specify <code>true</code> to get event notification on
	 * dimension changes or <code>false</code> otherwise 
	 */
	public void reload(boolean fireEvents);
//	 * @deprecated use {@link Hierarchy#reload(boolean)} instead.
}
