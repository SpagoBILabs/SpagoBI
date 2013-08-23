/*
*
* @file DbConnection.java
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
* @version $Id: DbConnection.java,v 1.40 2010/02/22 11:38:54 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved.
 */
package com.tensegrity.palojava;

import com.tensegrity.palojava.events.ServerListener;
import com.tensegrity.palojava.loader.CubeLoader;
import com.tensegrity.palojava.loader.DatabaseLoader;
import com.tensegrity.palojava.loader.DimensionLoader;
import com.tensegrity.palojava.loader.ElementLoader;
import com.tensegrity.palojava.loader.FunctionLoader;
import com.tensegrity.palojava.loader.HierarchyLoader;
import com.tensegrity.palojava.loader.PropertyLoader;
import com.tensegrity.palojava.loader.RuleLoader;

/**
 * The <code>DbConnection</code> interface defines all calls to the palo 
 * database which are currently possible for the api.
 * 
 * @author ArndHouben
 * @version $Id: DbConnection.java,v 1.40 2010/02/22 11:38:54 PhilippBouillon Exp $
 */
public interface DbConnection {

	public DatabaseLoader getDatabaseLoader();
	public FunctionLoader getFunctionLoader();
	public CubeLoader getCubeLoader(DatabaseInfo database);
	public DimensionLoader getDimensionLoader(DatabaseInfo database);
	public ElementLoader getElementLoader(DimensionInfo dimension);
	public ElementLoader getElementLoader(HierarchyInfo hierarchy);
	public RuleLoader getRuleLoader(CubeInfo cube);
	public HierarchyLoader getHierarchyLoader(DimensionInfo dimension);

	public PropertyLoader getPropertyLoader();
	public PropertyLoader getTypedPropertyLoader(PaloInfo element);
	
	public PropertyInfo getProperty(String id);
	
	public boolean supportsRules();
	PropertyInfo createNewProperty(String id, String value, PropertyInfo parent,
			   int type, boolean readOnly);

	
	/**
	 * Returns a {@link ConnectionInfo} object which provides more information
	 * about this connection instance
	 * @return a {@link ConnectionInfo} object
	 */
	public ConnectionInfo getInfo();
	
	/**
	 * Returns <code>true</code> if a connection still exists, 
	 * <code>false</code> otherwise
	 * @return <code>true</code> if connection is still available, 
	 * <code>false</code> otherwise
	 */
	public boolean isConnected();
	
//	/**
//	 * Disconnects from the palo server
//	 */
//	public void disconnect();

	/**
	 * Performs a login to the palo server with the given name and password
	 * @param user login name
	 * @param password login password
	 * @return <code>true</code> if login was successful, 
	 * <code>false</code> otherwise
	 */
	public boolean login(String user, String password);
	
    /**
     * Tests if the palo server is still reachable
     * @throws PaloException if palo server is not reachable anymore 
     */
    public void ping() throws PaloException;
	
	/**
	 * Returns the {@link ServerInfo} object which provides more information
	 * about the used palo server
	 * @return {@link ServerInfo} object
	 */
    public ServerInfo getServerInfo();

    /**
     * Adds a {@link ServerListener} listener to this connection
     * @param listener {@link ServerListener} to register
     */
	public void addServerListener(ServerListener listener);
	/**
	 * Removes the given {@link ServerListener}
	 * @param listener
	 */
	public void removeServerListener(ServerListener listener);

	/**
	 * Returns <code>DatabaseInfo</code> instances to all known databases
	 * @return representations of all known databases
	 */
	public DatabaseInfo[] getDatabases();

	/**
	 * Returns <code>DatabaseInfo</code> instance to the given database id
	 * @return <code>DatabaseInfo</code>
	 */
	public DatabaseInfo getDatabase(String id);
	
//	/**
//	 * Returns all known system databases, i.e. the type of the returned 
//	 * databases is {@link PaloConstants#TYPE_SYSTEM}
//	 * @return representations of all known system databases
//	 */
//	public DatabaseInfo[] getSystemDatabases();
//	/**
//	 * Returns all known normal databases, i.e. the type of the returned 
//	 * databases is {@link PaloConstants#TYPE_NORMAL}
//	 * @return representations of all known normal databases
//	 */
//	public DatabaseInfo[] getNormalDatabases();
	
	/**
	 * Returns all {@link CubeInfo} representations of all known cubes
	 * @param database info representation
	 * @return representations of all known cubes
	 */
	public CubeInfo[] getCubes(DatabaseInfo database);
	public CubeInfo [] getCubes(DatabaseInfo database, int typeMask);
	public CubeInfo getCube(DatabaseInfo database, String id);
	public int convert(CubeInfo cube, int type);
	
//	/**
//	 * Returns all {@link CubeInfo} representations of all known system cubes
//	 * @param database info representation
//	 * @return representations of all known system cubes
//	 */
//	public CubeInfo[] getSystemCubes(DatabaseInfo database);
//	/**
//	 * Returns all {@link CubeInfo} representations of all known normal cubes
//	 * @param database info representation
//	 * @return representations of all known normal cubes
//	 */
//	public CubeInfo[] getNormalCubes(DatabaseInfo database);
//	/**
//	 * Returns all {@link CubeInfo} representations of all known user info cubes
//	 * @param database info representation
//	 * @return representations of all known user info cubes
//	 */
//	public CubeInfo[] getUserInfoCubes(DatabaseInfo database);
	
	/**
	 * Returns the {@link DimensionInfo} representations of all known dimensions
	 * @param database info representation
	 * @return representations of all known dimensions
	 */
	public DimensionInfo[] getDimensions(DatabaseInfo database);
	public DimensionInfo[] getDimensions(DatabaseInfo database, int typeMask);
	
	public DimensionInfo getDimension(DatabaseInfo database, String id);

	public HierarchyInfo [] getHierarchies(DimensionInfo dimension);
	public HierarchyInfo getHierarchy(DimensionInfo dimension, String id);
	
//	public DimensionInfo [] getDimensions(HierarchyInfo hierarchy);
//	public HierarchyInfo [] getHierarchies(DatabaseInfo database);
//	public HierarchyInfo [] getHierarchies(CubeInfo cube);
//	public HierarchyInfo getHierarchy(DatabaseInfo database, String id);
//	public HierarchyInfo getHierarchy(CubeInfo cube, String id);
	
//	/**
//	 * Returns all {@link DimensionInfo} representations of all known normal
//	 * dimensions, i.e. neither system nor attribute dimensions
//	 * @param database info representation
//	 * @return representations of all known normal dimensions
//	 */
//	public DimensionInfo[] getNormalDimensions(DatabaseInfo database);
//	
//	/**
//	 * Returns all {@link DimensionInfo} representations of all known user info
//	 * dimensions.
//	 * @param database info representation
//	 * @return representations of all known user info dimensions
//	 */
//	public DimensionInfo[] getUserInfoDimensions(DatabaseInfo database);
//	
	/**
	 * Returns the {@link CubeInfo} representation of the attribute cube which
	 * belongs to the given dimension or <code>null</code>
	 * @param dimension info representation
	 * @return representation of its attribute cube
	 */
	public CubeInfo getAttributeCube(DimensionInfo dimension);
	/**
	 * Returns the {@link DimensionInfo} representation of the attribute 
	 * dimension which belongs to the given dimension or <code>null</code>
	 * @param dimension info representation
	 * @return representation of its attribute dimension
	 */
	public DimensionInfo getAttributeDimension(DimensionInfo dimension);
	
	
	/**
	 * Returns all {@link CubeInfo} representations of all cubes which contains 
	 * the specified dimension
	 * @param dimension info representation
	 * @return representations of cubes which use the specified dimension
	 */
	public CubeInfo[] getCubes(DimensionInfo dimension);
//	/**
//	 * Returns all cubes of the given type which use this dimension. The 
//	 * specified type must be one of the type constants defined in 
//	 * {@link PaloConstants}
//	 * @param type the type of cubes to return
//	 * @return all cubes of specified type which use this dimension
//	 */
//	public CubeInfo[] getCubes(DimensionInfo dimension, int type);


	/**
	 * Returns the {@link ElementInfo} representations of all elements the
	 * specified dimension has 
	 * @param dimension info representation
	 * @return representations of all dimension elements 
	 */
	public ElementInfo[] getElements(DimensionInfo dimension);
	public ElementInfo[] getElements(HierarchyInfo hierarchy);
	
	public ElementInfo getElement(DimensionInfo dimension, String id);
	public ElementInfo getElement(HierarchyInfo hierarchy, String id);

	/**
	 * Returns the {@link ElementInfo} representation of the element at the
	 * given position within specified dimension 
	 * @param dimension info representation
	 * @param position the element position
	 * @return element representation
	 */
	public ElementInfo getElementAt(DimensionInfo dimension, int position);
	public ElementInfo getElementAt(HierarchyInfo hierarchy, int position);
	
	
	//-------------------------------------------------------------------------
	// Data
	/**
	 * Returns the {@link CellInfo} representation of the cell which is 
	 * determined by the given coordinate.
	 * The coordinate consists of {@link ElementInfo} representation from the
	 * <code>Dimension</code>s which made up the <code>Cube</code>. It is 
	 * important to specifiy the elements in the same order as the dimensions
	 * are within the cube.
	 * @param cube {@link CubeInfo} representation
	 * @param coordinate {@link ElementInfo} representations
	 * @return cube cell representation
	 */
	public CellInfo getData(CubeInfo cube, ElementInfo[] coordinate);

	/**
	 * Convenient method to receive multiple {@link CellInfo}s at one time.
	 * The specified coordinates determine a cell area (by cartesian product)
	 * within given cube.
	 * @param cube {@link CubeInfo} representation
	 * @param coordinates {@link ElementInfo} representations which specify the
	 * coordinates
	 * @return cube cell representations
	 */
	public CellInfo[] getDataArea(CubeInfo cube, ElementInfo[][] coordinates);

	/**
	 * Convenient method to receive cube data as a stream.
	 * @param cube {@link CubeInfo} representation
	 * @param context export context holder
	 * @return representations of exported cube cells
	 */
	public CellInfo[] getDataExport(CubeInfo cube, ExportContextInfo context);

	/**
	 * Convenient method to receive multiple cell values at once. This method
	 * differs from {@link #getDataArea(CubeInfo, ElementInfo[][])} in that 
	 * way, that it does not compute an area of cells. Instead it will only 
	 * return the cell values for the given coordinates specified by the
	 * provided elements.
	 * @param cube {@link CubeInfo} representation
	 * @param coordinates {@link ElementInfo} representations which specify the
	 * coordinates
	 * @return cube cells representations
	 */
	public CellInfo[] getDataArray(CubeInfo cube, ElementInfo[][] coordinates);
	
	
	/**
	 * Sets the given <code>String</code> value at the specified cell value
	 * @param cube {@link CubeInfo} representation
	 * @param coordinates {@link ElementInfo} representations which specify the
	 * coordinates
	 * @param value the new value
	 */
	public void setDataString(CubeInfo cube, ElementInfo[] coordinate, String value);

	/**
	 * Sets the given <code>double</code> value at the specified cell
	 * @param cube {@link CubeInfo} representation
	 * @param coordinates {@link ElementInfo} representations which specify the
	 * coordinates
	 * @param value the new value
	 */
//	public void setDataNumeric(CubeInfo cube, ElementInfo[] coordinate, double value);

	/**
	 * Sets the given <code>double</code> value at the specified cell.
	 * The splashMode paramater is only important for consolidated cells and
	 * determines how the value is scattered among the consolidated elements.
	 * Please use the defined class constants for valid values. Although more
	 * modes are currently defined only three are supported, namely:
	 * SPLASH_MODE_DEFAULT, SPLASH_MODE_BASE_SET and SPLASH_MODE_BASE_ADD
	 * @param cube {@link CubeInfo} representation
	 * @param coordinates {@link ElementInfo} representations which specify the
	 * coordinates
	 * @param value the new value
	 * @param splashMode the splash mode, use defined class constants
	 */
	public void setDataNumericSplashed(CubeInfo cube, ElementInfo[] coordinate, double value, int splashMode);

	/**
	 * Convenient method to set multiple cell values at one time.
	 * @param cube {@link CubeInfo} representation
	 * @param coordinates {@link ElementInfo} representations which specify the
	 * coordinates
	 * @param value the new values
	 * @param splashMode the splash mode, use defined class constants
	 */
	public void setDataArray(CubeInfo cube, ElementInfo[][] coordinates, Object[] values, boolean add, int splashMode, boolean notifyEventProcessors);
	
	//-------------------------------------------------------------------------
	// Administration
	/**
	 * Adds the given database. 
	 * Note: the database name has to be unique
	 * @param database name of the new database.
	 * @return @return a newly created database representation
	 */
	public DatabaseInfo addDatabase(String database, int type);

	/**
	 * Adds a new cube specified by given dimensions and name to the given
	 * database
	 * @param database {@link DatabaseInfo} representation
	 * @param name unsused cube name 
	 * @param dimensions {@link DimensionInfo} representations which made up the
	 * cube
	 * @return a newly created cube representation
	 */
	public CubeInfo addCube(DatabaseInfo database, String name, DimensionInfo[] dimensions);

	/**
	 * Adds a new cube specified by given dimensions, name, and type to the
	 * given database
	 * @param database {@link DatabaseInfo} representation
	 * @param name unused cube name 
	 * @param dimensions {@link DimensionInfo} representations which made up the
	 * cube
	 * @param type type of the new cube. Either user info or normal.
	 * @return a newly created cube representation
	 */
	public CubeInfo addCube(DatabaseInfo database, String name, DimensionInfo[] dimensions, int type);

	/**
	 * Adds a new dimension with the given name to the specified database 
	 * @param database {@link DatabaseInfo} representation
	 * @param name unused dimension name
	 * @return a newly created dimension representation
	 */
	public DimensionInfo addDimension(DatabaseInfo database, String name);

	/**
	 * Adds a new dimension with the given name and type to the 
     * specified database.
	 * @param database {@link DatabaseInfo} representation
	 * @param name unused dimension name
     * @param type type of the new dimension. Either user info or normal.
	 * @return a newly created dimension representation
	 */
	public DimensionInfo addDimension(DatabaseInfo database, String name, int type);
	
	/**
	 * Adds a new element with given name, type and children to the specified
	 * dimension
	 * @param dimension {@link DimensionInfo} representation
	 * @param name unused element name
	 * @param type element type
	 * @param children {@link ElementInfo} representations of its children
	 * @param weights corresponding weight factors
	 * @return a newly created element representation 
	 */
	public ElementInfo addElement(DimensionInfo dimension, String name, int type, ElementInfo[] children, double[] weights);
	public ElementInfo addElement(HierarchyInfo hierarchy, String name, int type, ElementInfo[] children, double[] weights);
	public boolean addElements(DimensionInfo dimension, String[] names, int type, ElementInfo[][] children, double[][] weights);
	public boolean addElements(DimensionInfo dimension, String[] names, int [] types, ElementInfo[][] children, double[][] weights);
	
	/**
	 * Adds the given children elements to the exsisting children of the 
	 * specified element.
	 * @param element {@link ElementInfo} representation of the parent element
	 * @param children {@link ElementInfo} representations the children to add
	 * @param weights corresponding weight factors
	 */
	public void addConsolidations(ElementInfo element, ElementInfo[] children,double[] weights);

	/**
	 * Clears the specified dimension. This means that all its elements
	 * are removed and that all associated cubes are cleared too.
	 * @param dimension {@link DimensionInfo} representation
	 */
	public void clear(DimensionInfo dimension);

	/**
	 * Clears the specified cube completely. This means that all cell values
	 * are cleared
	 * @param cube {@link CubeInfo} instance
	 */
	public void clear(CubeInfo cube);
	/**
	 * Clears the specified cube area. The area is determined by the cartesian
	 * product of the given element coordinates.
	 * @param cube {@link CubeInfo} instance
	 * @param area the cube area to clear
	 */
	public void clear(CubeInfo cube, ElementInfo[][] area);

	
	/**
	 * Deletes the specified element from its dimension
	 * @param element {@link ElementInfo} representation
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public boolean delete(ElementInfo element);
    public boolean delete(ElementInfo[] elements);

	/**
	 * Deletes the given cube.
	 * @param cube {@link CubeInfo} representation
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public boolean delete(CubeInfo cube);

	/**
	 * Deletes the given database. 
	 * @param database {@link DatabaseInfo} representation
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public boolean delete(DatabaseInfo database);

	/**
	 * Deletes the specified dimension
	 * @param dimension {@link DimensionInfo} representation
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public boolean delete(DimensionInfo dimension);

	/**
	 * Moves the given element to the specified position within its dimension.
	 * @param element {@link ElementInfo} representation of the element which 
	 * should be moved
	 * @param newPosition the new element position
	 */
	public void move(ElementInfo element, int newPosition);

	/**
	 * Loads the cube data into memory.
	 * @param cube {@link CubeInfo} representation
	 */
	public void load(CubeInfo cube);

	/**
	 * Loads the given database. 
	 * @param database {@link DatabaseInfo} representation
	 */
	public void load(DatabaseInfo database);

	/**
	 * Renames the given database
	 * @param database {@link DatabaseInfo} representation
	 * @param newName new database name
	 */
	public void rename(DatabaseInfo database, String newName);
	/**
	 * Renames the given element
	 * @param element {@link ElementInfo} representation
	 * @param newName new element name
	 */
	public void rename(ElementInfo element, String newName);

	/**
	 * Renames a dimension
	 * @param dimension {@link DimensionInfo} representation
	 * @param newName new dimension name
	 */
	public void rename(DimensionInfo dimension, String newName);

	/**
	 * Renames a cube
	 * @param cube {@link CubeInfo} representation
	 * @param newName new cube name
	 */
	public void rename(CubeInfo cube, String newName);
	
	/**
	 * Saves the specified database.
	 * @param database {@link DatabaseInfo} representation
	 * @return <code>true</code> if saving was successful, <code>false</code>
	 * otherwise
	 */
	public boolean save(DatabaseInfo database);

	/**
	 * Saves the server data, i.e. its database names
	 * @param server {@link ServerInfo} representation
	 * @return <code>true</code> if saving was successful, <code>false</code>
	 * otherwise
	 */
	public boolean save(ServerInfo server);

	/**
	 * Save the specified cube
	 * @param cube {@link CubeInfo} representation
	 * @return <code>true</code> if saving was successful, <code>false</code>
	 * otherwise
	 */
	public boolean save(CubeInfo cube);
	
	
	/**
	 * Unloads the cube data from memory.
	 * @param cube {@link CubeInfo} representation
	 */
	public void unload(CubeInfo cube);

	/**
	 * Updates the specified element, i.e. the type, children and consolidation
	 * factors of the given element are overriden. Afterwards the 
	 * {@link ElementInfo} representation is up to date. 
	 * @param element {@link ElementInfo} representation which should be updated
	 * @param type new element type
	 * @param childrenIds identifiers of new children
	 * @param weights new consolidation factors
	 * @param serverInfo information about the currently used server
	 */
	public void update(ElementInfo element, int type, String[] childrenIds,
			double[] weights, ServerInfo serverInfo);
	
	public boolean replaceBulk(DimensionInfo dimInfo, ElementInfo [] elements, int type, ElementInfo [][] children, Double [][] weights);
	
//	/**
//	 * Sets the given children elements as the new children for the specified
//	 * element.
//	 * @param element {@link ElementInfo} representation of the parent element
//	 * @param children {@link ElementInfo} representations the children to add
//	 * @param weights corresponding weight factors
//	 */
//	public void setConsolidations(ElementInfo element, ElementInfo[] children,double[] weights);
//
//	/**
//	 * Changes the type of the specified element 
//	 * @param element {@link ElementInfo} representation of the parent element
//	 * @param type new element type
//	 */
//	public void setType(ElementInfo element, int type);
	
	/**
	 * Reloads the specified cube from the server. After that the given
	 * </code>{@link CubeInfo}</code> representation is up to date
	 * @param cube {@link CubeInfo} representation to reload
	 */
	public void reload(CubeInfo cube);

	/**
	 * Reloads the specified database from the server. After that the given
	 * {@link DatabaseInfo} representation is up to date
	 * @param database {@link DatabaseInfo} representation to reload
	 */
	public void reload(DatabaseInfo database);
	
	/**
	 * Reloads the specified dimension from the server. After that the given
	 * </code>{@link DimensionInfo}</code> representation is up to date
	 * @param dimension {@link DimensionInfo} representation to reload
	 */
	public void reload(DimensionInfo dimension);
	
	/**
	 * Reloads the specified element from the server. After that the given
	 * </code>{@link ElementInfo}</code> representation is up to date
	 * @param element {@link ElementInfo} representation to reload
	 */
	public void reload(ElementInfo element);
	
	
//PLEASE REFER TO http#ServerHandler for more information why this is not suppported...	
//	/**
//	 * <p>
//	 * Requests an exclusive server lock. If requesting the lock was successful,
//	 * the complete server will block any subsequent requests for any other
//	 * session until the lock is released. Please note that this method will
//	 * block until a lock is acquired.</p> 
//	 * The given string message is used for logging, i.e. all requests between 
//	 * the lock request and lock release are logged by the palo server with 
//	 * this string as event. 
//	 * @param msg the log message or <code>null</code> if none should be logged
//	 * @return <code>true</code> if requesting the lock was successful, 
//	 * <code>false</code> otherwise
//	 */
//	public boolean requestLock(String msg); 
//	
//	/**
//	 * Releases the exclusive server lock.
//	 * @return <code>true</code> if lock release was successful, 
//	 * <code>false</code> otherwise
//	 */
//	public boolean releaseLock();

	
	//--------------------------------------------------------------------------
	// RULE API
	//
	/**
	 * Returns an xml string with the parsed rule 
	 * @param cube {@link CubeInfo} representation
	 * @param definition the rule definition
	 * @param functions the function names to use separated by comma
	 * @return
	 */
	public String parseRule(CubeInfo cube,String ruleDefinition,String functions);

	/**
	 * Returns all known functions which are registered to this palo server
	 * @return a textual representation of all existing functions
	 */
	public String listFunctions();
	
	/**
	 * Creates a new <code>Rule</code> for the specified <code>Cube</code> 
	 * using the given rule definition
	 * @param cube {@link CubeInfo} representation
	 * @param definition a rule definition
	 * @return a newly created rule representation
	 */
	public RuleInfo createRule(CubeInfo cube, String definition);
	
	/**
	 * Creates a new <code>Rule</code> for the specified <code>Cube</code> 
	 * using the given rule definition, external identifier and comment
	 * @param cube {@link CubeInfo} representation
	 * @param definition a rule definition
	 * @param externalIdentifier an optional external identifier
	 * @param useIt set to <code>true</code> to use external identifier, otherwise to <code>false</code>
	 * @param comment an optional comment
	 * @param activate specifies if the rule is active or not
	 * @return a newly created rule representation
	 */
	public RuleInfo createRule(CubeInfo cube, String definition, String externalIdentifier, boolean useIt, String comment, boolean activate);

	/**
	 * Deletes the specified rule
	 * @param rule {@link RuleInfo} representation
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public boolean delete(RuleInfo rule);

	/**
	 * Deletes the rule which corresponds to the given id. This method is 
	 * useful to delete a rule on the server which could not be loaded. However,
	 * the preferred way to delete a rule is to use {@link #delete(RuleInfo)}.
	 * @param ruleId the identifier of the rule to delete
	 * @param cube the cube representation which contains this rule
	 * @return <code>true</code> if deletion was successful, <code>false</code>
	 * otherwise
	 */
	public boolean delete(String ruleId, CubeInfo cube);
	
	/**
	 * Returns all rules which the specified cube contains
	 * @param cube {@link CubeInfo} representation
	 * @return all rule repsresentations which the specified cube has
	 */
	public RuleInfo[] getRules(CubeInfo cube);	
	
	public RuleInfo getRule(CubeInfo cube, String id);
	
	/**
	 * Returns the id of the {@link RuleInfo} which determines the value of the 
	 * cell specified by the given coordinate. If no rule was defined for this 
	 * cell <code>null</code> is returned.
	 * @param cube {@link CubeInfo} representation
	 * @param coordinate {@link ElementInfo} representations
	 * @return the rule identifier or <code>null</code>
	 */
	public String getRule(CubeInfo cube, ElementInfo[] coordinate);
	
	/**
	 * Updates the given rule with the specified params.
	 * @param rule the rule to update
	 * @param definition the rule definition
	 * @param externalIdentifier the external rule identifier
	 * @param useIt specifies if the external identifier should be used
	 * @param comment an optional comment
	 * @param activate specifies if the rule is active or not
	 */
	public void update(RuleInfo rule, String definition, String externalIdentifier,boolean useIt, String comment, boolean activate);

	
	/**
	 * Returns all locks for the given cube.
	 * @param cube the cube to list the locks for
	 * @return an array of all currently set locks
	 */
	public LockInfo[] getLocks(CubeInfo cube);
	/**
	 * Requests a lock for the specified area of cells. The area is determined
	 * by the cartesian product of the given elements coordinates. To release
	 * a lock use either {@link #commit(CubeInfo, LockInfo)} or 
	 * {@link #rollback(CubeInfo, LockInfo, int)}
	 * @param cube the cube to request the lock for
	 * @param area the cell area to lock
	 * @return <code>LockInfo</code> instance which represents the lock or
	 * <code>null</code> if requesting lock failed.
	 * @see #commit(CubeInfo, LockInfo)
	 * @see #rollback(CubeInfo, LockInfo, int)
	 */
	public LockInfo requestLock(CubeInfo cube, ElementInfo[][] area);
	/**
	 * Commits the changes of a locked cube area and releases the lock 
	 * afterwards.
	 * @param cube the cube to commit lock changes for
	 * @param lock the lock to release
	 * @return <code>true</code> if commit was successful, <code>false</code>
	 * otherwise
	 */
	public boolean commit(CubeInfo cube, LockInfo lock);
	/**
	 * Rollback changes of a locked cube area and and releases the lock 
	 * afterwards. The <code>int</code> parameter specifies the number of steps 
	 * to rollback. If it is negative a complete rollback is performed. 
	 * @param cube the cube to rollback
	 * @param lock the locked area
	 * @param steps the number of steps to rollback. If the steps value is
	 * negative a complete rollback is performed  
	 * @return <code>true</code> if rollback was successful, <code>false</code>
	 * otherwise
	 */
	public boolean rollback(CubeInfo cube, LockInfo lock, int steps);

//	/**
//	 * Copies the given source cell to the specified target cell. If copying was
//	 * successful <code>true</code> is returned. 
//	 * @param src the source cell to copy
//	 * @param target the target cell
//	 * @return
//	 */
//	public boolean copy(CellInfo src, CellInfo target);
}
