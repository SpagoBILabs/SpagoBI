/*
*
* @file Element.java
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
* @version $Id: Element.java,v 1.28 2009/10/27 08:33:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.ServerInfo;


/**
 * <code>Element</code>. <p>Elements belong to {@link org.palo.api.Dimension}s.
 * A dimension is the parent of several distinct <code>Element</code>s. HOwever
 * if an <code>Element</code> is consolidated more than once in a specific
 * dimension than traversing the children of all elements will encounter
 * a given element multiple times. 
 * </p>
 * <p>
 * Within the scope of its single parent {@link org.palo.api.Dimension}, a dimension 
 * is uniquely identified by its name as returned by 
 * {@link #getName()}.
 * </p>
 *
 * @author Stepan Rutz
 * @version $ID$
 * 
 * @see org.palo.api.PaloAPIException
 */
public interface Element extends PaloObject {
	
//	BACK TO OLD VALUES TO FIX A BUG IN WEB PALO!!!!		
    /**
     * Identifier as returned by {@link #getType()} that
     * identifies numeric elements.
     */
    int ELEMENTTYPE_NUMERIC = 0; //ElementInfo.TYPE_NUMERIC;
    
    /**
     * Identifier as returned by {@link #getType()} that
     * identifies string elements.
     */
    int ELEMENTTYPE_STRING = 1; //ElementInfo.TYPE_STRING;
    
    /**
     * Identifier as returned by {@link #getType()} that
     * identifies consolidated elements.
     */
    int ELEMENTTYPE_CONSOLIDATED = 2; //ElementInfo.TYPE_CONSOLIDATED;
    
    /**
     * Identifier as returned by {@link #getType()} that
     * identifies rule elements.
     */
    int ELEMENTTYPE_RULE = 3; //ElementInfo.TYPE_RULE;
    
    /**
     * String constant mapped to the element-type {@link #ELEMENTTYPE_NUMERIC}.
     */
    String ELEMENTTYPE_NUMERIC_STRING = "Numeric";
    
    /**
     * String constant mapped to the element-type {@link #ELEMENTTYPE_STRING}.
     */
    String ELEMENTTYPE_STRING_STRING = "String";
    
    /**
     * String constant mapped to the element-type {@link #ELEMENTTYPE_CONSOLIDATED}.
     */
    String ELEMENTTYPE_CONSOLIDATED_STRING = "Consolidated";
    
    /**
     * String constant mapped to the element-type {@link #ELEMENTTYPE_RULE}.
     */
    String ELEMENTTYPE_RULE_STRING = "Rule";
    
    /**
     * Returns the name of this <code>Element</code>
     * @return the name of this <code>Element</code>.
     */
    String getName();
    
    /**
     * Returns the parent {@link Dimension} of this instance.
     * @return the parent {@link Dimension} of this instance.
     * @deprecated use {@link Element#getHierarchy()} instead.
     */
    Dimension getDimension();
    
    /**
     * Returns the parent {@link Hierarchy} of this instance.
     * @return the parent {@link Hierarchy} of this instance.
     */
    Hierarchy getHierarchy();
    
    /**
     * Returns the type of this <code>Element</code>, one of
     * the following is returned:
     * <ul>
     * <li><p>{@link #ELEMENTTYPE_NUMERIC}</p></li>
     * <li><p>{@link #ELEMENTTYPE_STRING}</p></li>
     * <li><p>{@link #ELEMENTTYPE_CONSOLIDATED}</p></li>
     * <li><p>{@link #ELEMENTTYPE_RULE}</p></li>
     * </ul>
     * @return the type of this <code>Element</code>.
     */
    int getType();
    
    void setType(int type);
    
    /**
     * Returns a string description of the <code>Element</code>'s type.
     * The returned value is a mere description only and is not to be
     * used as a basis for comparisions and the like.
     * @return a string description of the <code>Element</code>'s type.
     */
    String getTypeAsString();

    /**
     * The depth describes how deep the element is located inside its dimension
     * hierarchy related to its root elements, i.e. the depth is the length of
     * the longest path which starts from the root elements and ends in this 
     * element. A root element is an element without any parents and its depth 
     * is 0 by definition.
     * @return element depth
     */
    int getDepth();
    
    /**
     * The level describes how high the element is located inside its dimension
     * hierarchy related to its base-elements. Base-elements are elements 
     * without any children. Therefore the level is the length of the longest 
     * path which starts from the base-elements and ends in this element. The 
     * level of base-elements is 0 by definition.
     * @return element level
     */
    int getLevel();
    
//    /**
//     * The indent is almost the same as the depth with the excepting that only
//     * the first parent of an element is taken into account
//     * @return
//     */
//    int getIndent();
    
    /**
     * Renames this <code>Element</code>.
     * @param name the new name for this <code>Element</code>.
     */
    void rename(String name);
    
    /**
     * Returns the number of {@link Consolidation}s of this instance.
     * If this <code>Element</code> is not consolidated as returned
     * by {@link #getType()} then 0 is returned.
     * 
     * @return the number of {@link Consolidation}s of this instance.
     */
    int getConsolidationCount();
    
    /**
     * Returns the {@link Consolidation} stored at the given index.
     * If the index does not correspond to a legal position
     * in the internally managed array of consolidations of this
     * instance, then <code>null</code> is returned.
     * @param index the index
     * @return the {@link Consolidation} stored at the given index
     * or <code>null</code>.
     */
    Consolidation getConsolidationAt(int index);
    
    /**
     * Returns an array of {@link Consolidation} instances available
     * for this instance.
     * If this <code>Element</code> is not consolidated as returned
     * by {@link #getType()} then 0 is returned.
     * <p>The returned array is a copy of the internal datastructure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return an array of {@link Consolidation} instances available
     * for this connection.
     */
    Consolidation[] getConsolidations();
    
    /**
     * Updates the consolidation-infos of this <code>Element</code>.</br>
     * <b>Note:</b> it is required to call {@link Dimension#reload(boolean)} or
     * {@link Hierarchy#reload(boolean)} afterwards to reflect the structure 
     * changes. This separation is done to allow multiple updates of element 
     * consolidations and to reload structure only once.
     * 
     * @param consolidations the consolidations to use for this
     * <code>Element</code>.
     */
    void updateConsolidations(Consolidation consolidations[]);
    
    /**
     * Returns the number of parent-elements in the consolidation-hierarchy.
     * @return the number of parent-elements in the consolidation-hierarchy.
     */
    int getParentCount();
    
    /**
     * Returns the parent-elements in the consolidation-hierarchy.
     * @return the parent-elements in the consolidation-hierarchy.
     */
    Element[] getParents();
    
    /**
     * Returns the number of child-elements in the consolidation-hierarchy.
     * @return the number of child-elements in the consolidation-hierarchy.
     */
    int getChildCount();
    
    /**
     * Returns the child-elements in the consolidation-hierarchy.
     * @return the child-elements in the consolidation-hierarchy.
     */
    Element[] getChildren();
    
    /**
     * Returns the position of this element within its dimension
     * @return the element position
     */
    int getPosition();
    
    /**
     * Moves this element to the given position in its dimension
     * @param newPosition
     */
    void move(int newPosition);
    
    /**
     * Returns additional information about the element.
     * @return additional information about the element.
     */
    ElementInfo getInfo();
    
    //--------------------------------------------------------------------------
    //PALO 1.5 - PART OF ATTRIBUTE API...
    //
    
    /**
     * Returns the value of the specified {@link Attribute} 
     * @param attribute the <code>Attribute</code> instance to get the value from
     * @return the attribute value
     */
    Object getAttributeValue(Attribute attribute);
    
    /**
     * Returns the values of all {@link Attribute}s currently registered with
     * this element 
     * @return an array of attribute values
     */
    Object[] getAttributeValues();
    
    /**
     * Sets the value for the given <code>Attribute</code> instance
     * @param attribute the <code>Attribute</code> to set the value of
     * @param value the new attribute value
     */
    void setAttributeValue(Attribute attribute, Object value);
    
    /**
     * Sets the values for the given <code>Attribute</code> instances
     * @param attributes the <code>Attribute</code>s to set the values of 
     * @param values the new attribute values
     */
    void setAttributeValues(Attribute[] attributes, Object[] values);
}
