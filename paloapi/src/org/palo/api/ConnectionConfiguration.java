/*
*
* @file ConnectionConfiguration.java
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
* @version $Id: ConnectionConfiguration.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api;


/**
 * <code>ConnectionConfiguration</code>
 * <p>
 * A simple data class for holding connection concerned settings. 
 * A <code>ConnectionConfiguration</code> is only used to configure a connection 
 * to a palo server. After the connection is established it is no longer used. 
 * </p>
 * @author ArndHouben
 * @version $Id: ConnectionConfiguration.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class ConnectionConfiguration {

	/** the host which runs the palo server */
	private final String host;
	/** the service (e.g. a port number) which handles the requests */
	private final String service;
	/** the login name */
	private String name;
	/** the login password */
	private String password;

	/** the server type. currently {@link Connection#TYPE_HTTP}, 
	 * {@link Connection#TYPE_XMLA} and {@link Connection#TYPE_LEGACY} are
	 * supported*/
	private int type = Connection.TYPE_HTTP;
	/** connection timeout in milliseconds */
	private int timeout = Connection.DEFAULT_TIMEOUT;
	/** flag to indicate usage of load on demand */
	private boolean loadOnDemand = false;;
	
	/**
	 * Create a new <code>ConnectionConfiguration</code> instance with the 
	 * specified host name and port number. The user name and password are not
	 * set. The type is {@link Connection#TYPE_HTTP}, load on demand is disabled
	 * and the timeout is {@link Connection#DEFAULT_TIMEOUT}
	 * @param host
	 * @param port
	 */
	public ConnectionConfiguration(String host, String service) {
		this.host = host;
		this.service = service;
	}
	
	/**
	 * Returns <code>true</code> if load on demand is activated, 
	 * <code>false</code> otherwise
	 * @return <code>true</code> if load on demand is used, 
	 * <code>false</code> otherwise
	 */
	public final boolean doLoadOnDemand() {
		return loadOnDemand;
	}

	/**
	 * Returns the host which runs the palo server
	 * @return host name or ip address 
	 */
	public final String getHost() {
		return host;
	}
		
	/**
	 * Returns the login password in plaintext
	 * @return login password
	 */
	public final String getPassword() {
		return password;
	}
	
	/**
	 * Returns the port number at which the palo server is listening for requests
	 * @return port number of palo server
	 * @deprecated please use {@link #getService()} instead
	 */
	public final String getPort() {
		return getService();
	}

	/**
	 * Returns the service as string representation which handles palo requests.
	 * @return the service for handling palo requests
	 */
	public final String getService() {
		return service;
	}
	
	/**
	 * Returns the timeout setting in milliseconds after which a request is
	 * interrupted.
	 * @return request timeout in milliseconds
	 */
	public final int getTimeout() {
		return timeout;
	}
	
	/**
	 * Returns the connection type. Currently only {@link Connection#TYPE_HTTP}, 
	 * {@link Connection#TYPE_XMLA} and {@link Connection#TYPE_LEGACY} are
	 * supported
	 * @return connection type constant
	 */
	public final int getType() {
		return type;
	}
	
	/**
	 * Returns the login name
	 * @return login name
	 */
	public final String getUser() {
		return name;
	}
	
	/**
	 * En- or disables the load on demand behaviour. Usually load on demand
	 * leads to more faster behaviour of the connection, but the requesting
	 * client should act appropriately to get this benefit.
	 * Specify <code>true</code> to activate load on demand behaviour for this
	 * connection, specify <code>false</code> to deactivate it. 
	 * @param loadOnDemand specify <code>true</code> to activate it, 
	 * <code>false</code> otherwise
	 */
	public final void setLoadOnDemand(boolean loadOnDemand) {
		this.loadOnDemand = loadOnDemand;
	}
	
	/**
	 * Sets the password in plaintext
	 * @param password login password
	 */
	public final void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Specifies connection timeout in milliseconds after which a request is
	 * interrupted. Note that the timeout must be > 0 and a timeout of zero 
	 * is interpreted as an infinite timeout
	 * @param timeout the timeout in milliseconds
	 */
	public final void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * Specifies the connection type. Currently only {@link Connection#TYPE_HTTP}, 
	 * {@link Connection#TYPE_XMLA} and {@link Connection#TYPE_LEGACY} are
	 * supported
	 * @param type connection type
	 */
	public final void setType(int type) {
		this.type = type;
	}
	
	/**
	 * Sets the user login name.
	 * @param name login name
	 */
	public final void setUser(String name) {
		this.name = name;
	}

	
	
//IF WE SWITCH TO PROPERTIES LATER...
//	/** constant key for type property */
//	public static final STRING TYPE = "connectionType";
//	/** constant key for timeout property */
//	public static final STRING TIMEOUT = "connectionTimeout";
//	/** constant key for load on demand property */
//	public static final STRING LOAD_ON_DEMAND = "loadOnDemand";
//	
//
//	/** list of all known, i.e. valid, property ids */
//	private final Set knownProperties;
//	/** mapping of property ids to their values */
//	private final Map properties;
//	
//	public ConnectionConfiguration() {
//		knownProperties = new HashSet();
//		properties = new HashMap();
//	}
//	
//	public final void addProperty(String propertyId) {
//		knownProperties.add(propertyId);
//	}
//	
//	public final void addProperties(String[] propertyIds) {
//		int idCount = propertyIds != null ? propertyIds.length : 0;
//		for (int i = 0; i < idCount; ++i)
//			knownProperties.add(propertyIds);
//	}
//	
//	public final void setProperty(String property, Object value) {
//		//do we support it?
//		check(property);
//		properties.put(property,value);
//	}
//	
//	public final Object getProperty(String propertyId) {
//		Object propertyValue = properties.get(propertyId);
//        if (propertyValue == null) {
//            check(propertyId);
//        }
//
//        return propertyValue;
//	}
//	/** 
//	 * Checks if a property is support by the underlying server. The
//	 * property to check is described by the given property constant.
//	 * @param property the constant of the property to check
//	 * @return <code>true</code> if the property is supported <code>false</code>
//	 * otherwise
//	 */
//	public abstract boolean isSupported(String propertyId);
//
//	private final void check(String propertyId) {
//		if(!isSupported(propertyId))
//			throw new RuntimeException("Unsupported property '"+propertyId+"'!!");
//	}
}
