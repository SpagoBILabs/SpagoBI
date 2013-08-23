/*
*
* @file SubsetHandler.java
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
* @version $Id: SubsetHandler.java,v 1.11 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets;
import org.palo.api.Dimension;
import org.palo.api.Hierarchy;
import org.palo.api.exceptions.InsufficientRightsException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.persistence.SubsetLoadObserver;

/**
 * <code>SubsetHandler</code>
 * <p>This interface defines all methods to handle <code>Subset2</code> related
 *  functionalities.</p>
 *
 * @author ArndHouben
 * @version $Id: SubsetHandler.java,v 1.11 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public interface SubsetHandler {

	/**
	 * Returns the <code>Dimension</code> to which this 
	 * <code>SubsetHandler</code> is registered.
	 * @return the <code>Dimension</code> of this <code>SubsetHandler</code>.
	 * @deprecated use {@link SubsetHandler#getHierarchy()} instead.
	 */
	Dimension getDimension();
	
	/**
	 * Returns the <code>Hierarchy</code> to which this 
	 * <code>SubsetHandler</code> is registered.
	 * @return the <code>Hierarchy</code> of this <code>SubsetHandler</code>.
	 */
	Hierarchy getHierarchy();

	/**
	 * Resets this <code>SubsetHandler</code>. This will clear all internally
	 * used caches too.
	 */
	void reset();
	
	/**
	 * Checks if a subset with the given name and type already exists for
	 * this hierarchy and returns its id.
	 * @param name a subset name
	 * @param type one of the defined subset type constants
	 * @return the id of the specified subset if it exists for this hierarchy 
	 * or <code>null</code> otherwise
	 */
	String getSubsetId(String name, int type);
	
    /**
     * Adds a new {@link Subset2} with the given name and type to this hierarchy
     * @param name the name of the subset
     * @param type one of the defined subset type constants
     * @return the new subset instance
     * @throws InsufficientRightsException 
     */
    Subset2 addSubset(String name, int type) ;
    
    /**
     * Returns all {@link Subset2}s which are currently registered with this
     * hierarchy. This method tries to load all locally and globally defined 
     * subsets. Subsets which cannot be loaded are ignored. 
     * @return the registered subsets
     */
    Subset2[] getSubsets(); 

    /**
     * Returns all {@link Subset2}s of the given type which are registered with
     * this hierarchy. Subsets which cannot be loaded are ignored. 
     * @param type one of the defined subset type constants
     * @return the registered subsets
     */
	Subset2[] getSubsets(int type);
	
//    /**
//     * Returns all {@link Subset2}s which are currently registered with this
//     * hierarchy. This method tries to load all locally and globally defined 
//     * subsets. Subsets which cannot be loaded are ignored. To observe the
//     * loading process please use {@link #getSubsets(SubsetLoadObserver)}
//     * @return the registered subsets
//     */
//    Subset2[] getSubsets(); 
//
//
//    /**
//     * Tries to load all locally and globally defined {@link Subset2}s which 
//     * are currently registered with this dimension. This methods calls the 
//     * provided {@link SubsetLoadObserver} to track status and progress of 
//     * subset loading 
//     * @deprecated NOT YET OFFICIAL - SUBJECT TO CHANGE
//     */
//    void getSubsets(SubsetLoadObserver observer);
//    
//    /**
//     * Returns all {@link Subset2}s of the given type which are registered with
//     * this hierarchy. Subsets which cannot be loaded are ignored. To observe 
//     * the loading process please use {@link #getSubsets(int, SubsetLoadObserver)}
//     * @param type one of the defined subset type constants
//     * @return the registered subsets
//     */
//	Subset2[] getSubsets(int type);
//
//    /**
//     * Tries to load all {@link Subset2}s of the given type which are registered 
//     * with this hierarchy. This methods calls the provided 
//     * {@link SubsetLoadObserver} to track status and progress of subset loading 
//     * @deprecated NOT YET OFFICIAL - SUBJECT TO CHANGE
//     */
//	void getSubsets(int type, SubsetLoadObserver observer);
	
    /**
     * Returns the <code>Subset2</code> instance which is registered with the 
     * given id and for the given type or <code>null</code> if no subset with 
     * this id could be found or new subsets are not supported
     * @param id the subset id
     * @param type one of the defined subset type constants
     * @return the corresponding subset instance
     * @throws PaloIOException
     */
    Subset2 getSubset(String id, int type);
    
    /**
     * Returns the IDs of all locally and globally registered 
     * <code>Subset2</code>s 
     * @return the IDs of all locally and globally registered subsets
     */
    String[] getSubsetIDs();
    
    /**
	 * Returns the names of all locally and globally registered
	 * <code>Subset2</code>s
	 * @return the names of all locally and globally registered subsets
	 */
	String[] getSubsetNames();
	
    /**
     * Returns the names of all registered <code>Subset2</code>s of given type
	 * @param type one of the defined subset type constants
	 * @return the names of all registered subsets of given type
	 * @throws InsufficientRightsException
	 */
	String[] getSubsetNames(int type);
    
    /**
     * Returns the IDs of all registered <code>Subset2</code>s of given type
	 * @param type one of the defined subset type constants
	 * @return the IDs of all registered subsets of given type
	 * @throws InsufficientRightsException
	 */
    String[] getSubsetIDs(int type);
    
    /**
     * Returns the name of the subset which is registered for the given id
     * or <code>null</code>
     * @param id the subset id
     * @return <code>null</code> if no name exists to given id or if new 
     * subsets are not supported
     */
    String getSubsetName(String id);
    
    /**
     * Checks if there are any subsets of the given type
     * @param type one of the defined subset type constants
     * @return <code>true</code> if there are subsets of given type registered 
     * or <code>false</code> if not
     */
    boolean hasSubsets(int type);
    
    /**
     * Removes the given subset
     * @param subset the subset to remove
     * @throws InsufficientRightsException
     */
    void remove(Subset2 subset);
    
    /**
     * Removes the subset specified by the given id and type.
     * @param id the subset id
     * @param type the subset type
     * @throws InsufficientRightsException
     */
    void remove(String id, int type);
    
    /**
     * Saves the given subset
     * @param subset the subset to save
     * @throws InsufficientRightsException
     */
    void save(Subset2 subset);

    
    /**
     * Checks if the user is allowed to write subsets of given type 
     * @param type one of the predefined subset type constants.
     * @return <code>true</code> if user is allowed to write subsets of given
     * type, <code>false</code> otherwise
     */
    boolean canWrite(int type);
    
    /**
     * Checks if the user is allowed to read subsets of given type  
     * @param type one of the predefined subset type constants.
     * @return <code>true</code> if user is allowed to read subsets of given 
     * type, <code>false</code> otherwise
     */
    boolean canRead(int type);
    
}
