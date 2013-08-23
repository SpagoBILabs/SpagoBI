/*
*
* @file Database.java
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
* @version $Id: Database.java,v 1.25 2009/10/27 08:33:18 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

import org.palo.api.subsets.SubsetStorageHandler;

import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.ServerInfo;


/**
 * <code>Database</code>
 * 
 * <p>Instances of this class represent a database on a PALO server. Instances
 * are obtained from a parent {@link org.palo.api.Connection} instance.</p>
 * 
 * <p>
 * Different instances of this class may refer to the same logical PALO database. If
 * two different parent connections are  used, the returned database instances
 * are always different, as instances of <code>Database</code> are associated
 * with the {@link org.palo.api.Connection} instance they originate from.
 * A reference to this owner {@link org.palo.api.Connection} is obtained by
 * invoking {@link #getConnection()}.
 * </p>
 * 
 * <p>
 * A <code>Database</code> is a container and namespace for two distinct domain
 * objects, namely {@link org.palo.api.Cube} and {@link org.palo.api.Dimension}.
 * A <code>Database</code> is uniquely identified within a palo-server by its
 * name as returned by {@link #getName()}.
 * </p>
 * 
 * <p>
 * Dimension-information and domain-objects can be retrieved from a database instance by
 * invoking the following methods.
 * <ul>
 * <li><p>{@link #getDimensions()}</p></li>
 * <li><p>{@link #getDimensionCount()}</p></li>
 * <li><p>{@link #getDimensionAt(int)}</p></li>
 * <li><p>{@link #getDimensionByName(String)}</p></li>
 * </ul>
 * </p>
 * 
 * <p>
 * Cube-information and domain-objects can be retrieved from a database instance by
 * invoking the following methods.
 * <ul>
 * <li><p>{@link #getCubes()}</p></li>
 * <li><p>{@link #getCubeCount()}</p></li>
 * <li><p>{@link #getCubeAt(int)}</p></li>
 * <li><p>{@link #getCubeByName(String)}</p></li>
 * </ul>
 * </p>
 * 
 * <p>
 * The returned {@link org.palo.api.Dimension} and {@link org.palo.api.Cube}
 * instances are pooled internally and
 * kept identical throughout a connection lifetime as long as they refer to the 
 * same logical database. Thus these objects can be used inside domain-models
 * during the lifetime of a connection.
 * </p>
 * 
 * <p>
 * In addition to reading information, a database can also be manipulated.
 * {@link org.palo.api.Dimension}s and {@link org.palo.api.Cube}s can
 * be added and removed from the <code>Database</code> by invoking.
 * 
 * <ul>
 * <li><p>{@link #addDimension(String)}</p></li>
 * <li><p>{@link #removeDimension(Dimension)}</p></li>
 * <li><p>{@link #addCube(String, Dimension[])}</p></li>
 * <li><p>{@link #removeCube(Cube)}</p></li>
 * </ul>
 * </p>
 * 
 * <p>
 * All operations of the PALO-API in this package potentially
 * throw the exception {@link org.palo.api.PaloAPIException}
 * or other runtime exceptions.
 * </p>
 * 
 * @author Stepan Rutz
 * @version $ID$
 */
public interface Database extends PaloObject
{    
	/**
     * Returns the name of this <code>Database</code>
     * @return the name of this <code>Database</code>.
     */
    String getName();
    
    /**
     * Returns the parent {@link Connection} of this instance.
     * @return the parent {@link Connection} of this instance.
     */
    Connection getConnection();
    
    /**
     * Reloads all internal objects of the database. Invoking this
     * method can take some time. Afterwards the dimension,
     * cube, element and consolidation objects
     * that can be retrieved from this instance are identical to earlier
     * results if the objects represent the same palo domain objects.
     * This behavior is achieved by caching objects throughout
     * the life-time of the connection. 
     */
    void reload();
    
    /**
     * Starts the batch update mode for better performance
     * when adding or removing many palo objects in a single go.
     * 
     * <p>
     * Note that while batch mode is active some methods
     * will return null instead of returning the palo
     * domain objects. (like {@link Dimension#addElement(String, int)}).
     * </p>
     * 
     * <p>
     * Batch mode MUST be ended with a call to {@link #endBatchUpdate()} before
     * proceeding with regular API usage.
     * </p>
     */
    void startBatchUpdate();
    
    /**
     * Ends the batch update mode.
     * 
     * <p>
     * Note that while batch mode is active some methods
     * will return null instead of returning the palo
     * domain objects. (like {@link Dimension#addElement(String, int)}).
     * </p>
     * 
     */
    void endBatchUpdate();

    
    /**
     * Returns the number of {@link Dimension}s of this instance.
     * @return the number of {@link Dimension}s of this instance.
     */
    int getDimensionCount();
    
    /**
     * Returns the {@link Dimension} stored at the given index.
     * If the index does not correspond to a legal position
     * in the internally managed array of dimensions of this
     * instance, then <code>null</code> is returned.
     * @param index the index
     * @return the {@link Dimension} stored at the given index
     * or <code>null</code>.
     */
    Dimension getDimensionAt(int index);
        
    /**
     * Returns an array of {@link Dimension} instances available
     * for this instance.
     * <p>The returned array is a copy of the internal datastructure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return an array of {@link Dimension} instances available
     * for this connection.
     */
    Dimension[] getDimensions();
    
    /**
     * Returns all dimensions that are of one of the types specified in the type 
     * mask. The type mask is a bitwise-or of all possible types as defined
     * in the {@link Dimension} interface.
     * 
     * @param typeMask the mask representing all types of dimensions that are to
     * be returned.
     * @return all dimensions of the specified type(s).
     */
    Dimension[] getDimensions(int typeMask);

    /**
     * Returns the {@link Dimension} stored under the given name or
     * <code>null</code> if no such {@link Dimension} exists.
     * @param name the dimension-name to look-up.
     * @return the {@link Dimension} stored under the given name or
     * <code>null</code> if no such {@link Dimension} exists.
     */
    Dimension getDimensionByName(String name);
    
    Dimension getDimensionById(String id);
           
    /**
     * Returns the number of {@link Cube}s of this instance.
     * @return the number of {@link Cube}s of this instance.
     */
    int getCubeCount();
    
    /**
     * Returns the {@link Cube} stored at the given index.
     * If the index does not correspond to a legal position
     * in the internally managed array of cubes of this
     * instance, then <code>null</code> is returned.
     * @param index the index
     * @return the {@link Cube} stored at the given index
     * or <code>null</code>.
     */
    Cube getCubeAt(int index);
    
    /**
     * Returns an array of {@link Cube} instances available
     * for this instance.
     * <p>The returned array is a copy of the internal datastructure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return an array of {@link Cube} instances available
     * for this connection.
     */
    Cube[] getCubes();
    
    /**
     * Returns all cubes that are of one of the types specified in the type 
     * mask. The type mask is a bitwise-or of all possible types as defined
     * in the {@link Cube} interface.
     * 
     * @param typeMask the mask representing all types of cubes that are to
     * be returned.
     * @return all cubes of the specified type(s).
     */
    Cube[] getCubes(int typeMask);
    
    /**
     * Returns the {@link Cube} stored under the given name or
     * <code>null</code> if no such {@link Cube} exists.
     * @param name the cube-name to look-up.
     * @return the {@link Cube} stored under the given name or
     * <code>null</code> if no such {@link Cube} exists.
     */
    Cube getCubeByName(String name);
    
    Cube getCubeById(String id);
    
    /**
     * Adds a new dimension with the given name to this
     * <code>Database</code>. This operation fails
     * if a dimension with the same name exists already.
     * 
     * @param name the name of the new {@link org.palo.api.Dimension}.
     * @return the created {@link org.palo.api.Dimension}.
     */
    Dimension addDimension(String name);
    
    Dimension addUserInfoDimension(String name);

//    /**
//     * Adds a new dimension with the given name and the specified type
//     * to this <code>Database</code>. This operation fails
//     * if a dimension with the same name exists already.
//     * 
//     * @param name the name of the new {@link org.palo.api.Dimension}.
//     * @param type the type of the new {@link org.palo.api.Dimension}. Must
//     *        be one of the type constants defined in 
//     *        {@link org.palo.api.Dimension}. Currently, only normal and user
//     *        info type dimensions are supported.
//     * @return the created {@link org.palo.api.Dimension}.
//     */
//    Dimension addDimension(String name, int type);
    
    /**
     * Removes a dimension from this <code>Database</code>.
     * 
     * @param dimension the {@link org.palo.api.Dimension} to
     * remove from this <code>Database</code>.
     * 
     */
    void removeDimension(Dimension dimension);
    
    /**
     * Adds a new cube with the given name to this
     * <code>Database</code>. This operation fails
     * if a cube with the same name exists already.
     * The cube is created with the specified dimensions, which
     * must refer to dimensions of this <code>Database</code>.
     * 
     * @param name the name of the new {@link Cube}.
     * @param dimensions the {@link Dimension}s of the {@link Cube}.
     * @return the created {@link Cube}.
     */
    Cube addCube(String name, Dimension dimensions[]);
    
    Cube addUserInfoCube(String name, Dimension dimensions[]);
    
//    /**
//     * Adds a new cube with the given name to this
//     * <code>Database</code>. This operation fails
//     * if a cube with the same name exists already.
//     * The cube is created with the specified dimensions, which
//     * must refer to dimensions of this <code>Database</code>.
//     * The type of the new cube must be one of the type constants defined in
//     * {@link Cube} and either create a normal cube or a user info cube.
//     * 
//     * @param name the name of the new {@link Cube}.
//     * @param dimensions the {@link Dimension}s of the {@link Cube}.
//     * @param type of the cube, must be one of the constants defined in 
//     * 		  {@link Cube}.
//     * @return the created {@link Cube}.
//     */
//    Cube addCube(String name, Dimension dimensions[], int type);
    
    /**
     * Removes a cube from this <code>Database</code>.
     * 
     * @param cube the {@link org.palo.api.Cube} to
     * remove from this <code>Database</code>.
     * 
     */
    void removeCube(Cube cube);
    
    /**
     * Tells the Palo-Server to save the database.
   	 * @return <code>true</code> if saving was successful, <code>false</code>
	 * otherwise
     */
    boolean save();
    
    //-------------------------------------------------------------------------
    Cube addCube(VirtualCubeDefinition definition);
    
    //void removeVirtualCubeDefinition(VirtualCubeDefinition definition);

    /**
     * Parses the given rule definition for the given <code>Cube</code>
     * <b>NOTE:</b> INTERNAL USAGE ONLY!! WILL CHANGE WITH FUTURE VERSIONS!!!
     * @param cube
     * @param definition
     * @param functions
     */
    String parseRule(Cube cube,String definition, String functions);
    
	public boolean isSystem();
	
	/**
	 * Returns the storage handler for loading and storing <code>Subset2</code>s
	 * <p><b>NOTE: API INTERNAL METHOD </b></p>
	 * @return
	 */
	public SubsetStorageHandler getSubsetStorageHandler();
	
	/**
	 * Checks if new subsets are supported by this database. 
	 * @return <code>true</code> if new subsets are supported, 
	 * <code>false</code> otherwise
	 */
	public boolean supportsNewSubsets();
	
	/**
	 * @deprecated Subject to change, please don't use.
	 */
	public Rights getRights();

	/**
     * Returns additional information about the database.
     * 
     * @return additional information about the database.
     */
    DatabaseInfo getInfo();	
	
    /**
	 * Renames this <code>Database</code>.
	 * @param newName the new database name.
	 * @throws a {@link PaloAPIException} if the new database name is already in use.
	 */
	void rename(String newName);
}
