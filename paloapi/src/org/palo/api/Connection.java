/*
*
* @file Connection.java
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
* @version $Id: Connection.java,v 1.47 2009/12/14 12:46:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

import org.palo.api.ext.favoriteviews.FavoriteViewTreeNode;

import com.tensegrity.palojava.ServerInfo;

/**
 * <code>Connection</code>
 * 
 * <p>Instances of this class represent a session/connection with a PALO server.
 * Each session is independent from all other sessions.
 * </p>
 * 
 * <p>Invoke {@link #reload()} on a connection to rehash all of its internal datastructes.</p>
 * 
 * <p><code>Connection</code> instances have an equals/hashcode behavior that is identity based.
 * (This means {@link java.lang.Object#hashCode()} and
 * {@link java.lang.Object#equals(java.lang.Object)} are both not overridden).
 * </p>
 * 
 * <p>
 * Instances of this class can only be obtained by means of the
 * {@link org.palo.api.ConnectionFactory} class. 
 * </p>
 * 
 * <p>
 * Here is an example code snippet for retrieving a single datum from
 * a PALO connection. First a connection is created by means of the
 * {@link org.palo.api.ConnectionFactory}. Then this connection is used
 * to get a {@link org.palo.api.Database} instance by its name.
 * Next a {@link org.palo.api.Cube} is selected by name and 
 * a single datum is retrieved in the two possible ways. 
 * <pre>
 *  Connection connection = ConnectionFactory.getInstance().newConnection(
 *      "localhost",
 *      "1234",
 *      "user",
 *      "pass");
 *      
 *  // retrieve Database instance.
 *  Database db = connection.getDatabaseByName("Demo");
 *  
 *  // retrieve Cube instance.
 *  Cube cube = db.getCubeByName("Sales");
 *  
 *  // get single datum from Cube.
 *  Object v0 = cube.getData(new String[] {
 *      "Desktop L",
 *      "Germany",
 *      "Jan",
 *      "2003",
 *      "Budget",
 *      "Units" });
 *  
 *  // alternatively use the Element Objects to retrieve the same datum
 *  // in another fashion.
 *  Object v1 = cube.getData(new Element[] {
 *      cube.getDimensionAt(0).getElementByName("Desktop L"),
 *      cube.getDimensionAt(1).getElementByName("Germany"),
 *      cube.getDimensionAt(2).getElementByName("Jan"),
 *      cube.getDimensionAt(3).getElementByName("2003"),
 *      cube.getDimensionAt(4).getElementByName("Budget"),
 *      cube.getDimensionAt(5).getElementByName("Units"),
 *      });
 *  connection.disconnect();
 *</pre>
 *</p>
 *
 * <p>
 * All operations of the PALO-API in this package potentially
 * throw the exception {@link org.palo.api.PaloAPIException}
 * or other runtime exceptions.
 * </p>
 * 
 * <p>
 * Click here for a <a href="../../../../images/palo_cd.png">class-diagram</a>
 * of the api's core entities.
 * </p> 
 * 
 *
 * @author Stepan Rutz
 * @version $ID$
 * 
 * @see org.palo.api.PaloAPIException
 */
public interface Connection extends Writable
{
	/** connection type legacy */
	public static final int TYPE_LEGACY = 1;
	/** connection type http */
	public static final int TYPE_HTTP = 2;
	/** connection type xmla */
	public static final int TYPE_XMLA = 3;
	/** connection type wss */
	public static final int TYPE_WSS = 4;
	 
	/** the default timeout for connection is 30 seconds*/
	public static final int DEFAULT_TIMEOUT = 30000;
	
    /**
     * Returns the server name.
     * @return the server name.
     */
    String getServer();
    
    /**
     * Returns the service name.
     * @return the service name.
     */
    String getService();
    
    /**
     * Returns the username.
     * @return the username.
     */
    String getUsername();
    
    /**
     * Returns the password used to login. Implementations
     * may choose to return <code>null</code> or the empty string if
     * configured to prevent password-retrieval
     * @return the login password, the empty string or
     * <code>null</code>. 
     */
    String getPassword();

    /**
     * Reloads all internal objects of the connection. Invoking this
     * method can take some time. Afterwards the database, dimension,
     * cube, element and consolidation objects
     * that can be retrieved from this instance are identical to earlier
     * results if the objects represent the same palo domain objects.
     * This behavior is achieved by caching objects throughout
     * the life-time of the connection. 
     */
    void reload();
    
    /**
     * Tries to ping the palo server. Upon success the method returns
     * silenty.
     */
    void ping();
    
    /**
     * Checks if this connection is still connected to a PALO server.
     * @return <code>true</code> if a connection to a PALO server is 
     * established, <code>false</code> otherwise
     */
    boolean isConnected();
    
    /**
     * Disconnects from the PALO server. Afterwards the connection is
     * not functional anymore and should not be used. Reconnecting
     * is not supported. Create a new <code>Connection</code> instance
     * instead.
     */
    void disconnect();
    
    /**
     * Returns the number of databases.
     * @return the number of databases.
     */
    int getDatabaseCount();
    
    /**
     * Returns the database stored at the given index.
     * If the index does not correspond to a legal position
     * in the internally managed array of databases of this
     * instance, then <code>null</code> is returned.
     * @param index the index
     * @return the database stored at the given index
     * or <code>null</code>.
     */
    Database getDatabaseAt(int index);
    
    /**
     * Returns an array of {@link Database} instances available
     * for this connection.
     * <p>The returned array is a copy of the internal datastructure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return an array of {@link Database} instances available
     * for this connection.
     */
    Database[] getDatabases();
    
    /**
     * Returns an array of system {@link Database} instances available
     * for this connection.
     * <p>The returned array is a copy of the internal datastructure.
     * Changing the returned array does not change this instance.
     * </p>
     * 
     * @return an array of system {@link Database} instances available
     * for this connection.
     */
    Database[] getSystemDatabases();
    
    Database[] getUserInfoDatabases();
    
    /**
     * Returns the database stored under the given name or
     * <code>null</code> if no such database exists.
     * @param name the database name to look-up.
     * @return the database stored under the given name or
     * <code>null</code> if no such database exists.
     */
    Database getDatabaseByName(String name);

    /**
     * Returns the database stored under the given id or
     * <code>null</code> if no such database exists.
     * @param id identifier of the database to look-up.
     * @return the database stored under the given name or
     * <code>null</code> if no such database exists.
     */
    Database getDatabaseById(String id);

    /**
     * Adds a new database with the given name to this
     * <code>Connection</code>. This operation fails
     * if a database with the same name exists already.
     * 
     * @param name the name of the new {@link Database}.
     * @return the created {@link Database}.
     */
    Database addDatabase(String name);
    Database addUserInfoDatabase(String name);
    
    /**
     * Removes a database from this <code>Connection</code>.
     * 
     * @param database the {@link Database} to
     * remove from this <code>Connection</code>.
     * 
     */
    void removeDatabase(Database database);
    
    /**
     * Tells the Palo-Server to save everything on the server-side.
     * Effects are up to the palo-server.
   	 * @return <code>true</code> if saving was successful, <code>false</code>
	 * otherwise
     */
    boolean save();
    
    /**
     * This method adds a {@link ConnectionListener} to this
     * connection.
     * 
     * Note that connection-listeners are limited and do not
     * proactively report changes made on the palo server.
     * 
     * @param connectionListener the {@link ConnectionListener} to add.
     */
    void addConnectionListener (ConnectionListener connectionListener);
    
    /**
     * This method removes a {@link ConnectionListener} from this
     * connection.
     * 
     * Note that connection-listeners are limited and do not
     * proactively report changes made on the palo server.
     * 
     * @param connectionListener the {@link ConnectionListener} to remove.
     */
    void removeConnectionListener (ConnectionListener connectionListener);
    
    /**
     * Checks if connection uses the legacy palo server or not.
     * @return true if the connection is bind to the legacy palo server, false
     * otherwise
     */
    boolean isLegacy();
    
    /**
     * Returns the connection type for this connection. The return value will be
     * one of the TYPE_xxx constants defined in this interface.
     * @return the connection type
     */
    int getType();
    
    /**
     * Returns all available <code>Function</code>s which are defined for this
     * connection. 
     * @return all functions
     */
    String getFunctions();
    
    /**
     * Logs into this connection with the specified user name and password. If
     * the connection could not be established, false is returned. If the
     * connection can successfully be established, true is returned.
     *  
     * @param username
     * @param password
     * @return true if the connection could be established, false otherwise.
     */
    boolean login(String username, String password);
    
    /**
     * Loads all favorite views stored with this connection and returns the
     * root of the favorite view tree representing those views or null, if no
     * favorite views were stored in this connection.
     * 
     * Note that this method will throw a PaloAPIException if the user
     * does not have sufficient rights to store favorite views (and thus cannot
     * have any favorite views to load).
     * 
     * @return the root of the favorite views tree attached to this connection
     * or null if no favorite views were saved.
     * 
     */
    FavoriteViewTreeNode loadFavoriteViews();
    
    /**
     * Stores the favorite views for this connection. Any old favorite views
     * that have been stored in this connection will be overwritten.
     * 
     * Note that this method will throw a PaloAPIException if the user does not
     * have sufficient rights to store favorite views.
     * 
     * @param favoriteViews the root of a favorite views tree.
     */
    void storeFavoriteViews(FavoriteViewTreeNode favoriteViews);
    
    /**
     * Returns all ids of properties that can be set for this connection. If no
     * properties are known to this connection, an empty array is returned.
     * 
     * @return all property ids that are understood by this connection.
     */
    String [] getAllPropertyIds();

    /**
     * Returns the property identified by the given id. All valid ids can be
     * requested by a call to getAllPropertyIds.
     * 
     * @param id the id of the property to read.
     * @return the property for the given id.
     */
    Property2 getProperty(String id);

    /**
     * Adds the given property to the list of properties for this connection.
     * 
     * @param property the property to add.
     */
    void addProperty(Property2 property);

    /**
     * Removes the given property from the list of properties for this
     * connection. If the specified id was not set, the call is ignored.
     * 
     * @param id the id of the property which is to be cleared.
     */    
    void removeProperty(String id);
    
    /**
     * Returns additional information about the palo server.
     * 
     * @return additional information about the palo server.
     */
    ServerInfo getInfo();
    
    /**
     * Returns the {@link ConnectionContext} which allows to retrieve further 
     * information about current connection
     * @return the {@link ConnectionContext} of current connection.
     */
    ConnectionContext getContext();
    
    /** <b> API INTERNAL </b> */
    Object getData(String id);
    
    void clearCache();
}
