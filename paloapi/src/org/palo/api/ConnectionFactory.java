/*
*
* @file ConnectionFactory.java
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
* @version $Id: ConnectionFactory.java,v 1.22 2010/02/09 11:44:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

import java.io.InputStream;
import java.util.Properties;

/**
 * <code>ConnectionFactory</code>
 * 
 * <p>An instance of <code>ConnectionFactory</code> is obtained with the 
 * {@link #getInstance()} method. Subsequently a connection to a PALO server
 * can be created by invoking the
 * {@link #newConnection(String, String, String, String)} method.
 * </p>
 * 
 * <p>Example:
 * <pre>
        Connection c = ConnectionFactory.getInstance().newConnection(
            &quot;localhost&quot;,
            &quot;1234&quot;,
            &quot;user&quot;,
            &quot;pass&quot;);
            
         // use the connection here
         c.disconnect();
 * </pre>
 * </p>
 *
 * @author Stepan Rutz
 * @version $Id: ConnectionFactory.java,v 1.22 2010/02/09 11:44:57 PhilippBouillon Exp $
 * 
 * @see org.palo.api.PaloAPIException
 */
public abstract class ConnectionFactory
{
    private static ConnectionFactory instance;
    
    static
    {
        try
        {
        	//load palo properties:
        	Properties props = new Properties(System.getProperties());
			InputStream propsStream = ConnectionFactory.class
					.getResourceAsStream("/paloapi.properties");			
			if (propsStream != null) {
				props.load(propsStream);
				// Overwrite wpalo and xmla_ignoreVariableCubes, no matter
				// what was in those keys before.
				if (props.containsKey("wpalo")) 
					System.setProperty("wpalo", props.getProperty("wpalo"));
				if (props.containsKey("xmla_ignoreVariableCubes")) 
					System.setProperty("xmla_ignoreVariableCubes", props.getProperty("xmla_ignoreVariableCubes"));				

				// Only add the other properties if they have not been set
				// before. That is to ensure that sql properties already set
				// by config.ini values are not overwritten by settings in
				// paloapi.properties. (The sql settings are also in
				// paloapi.properties, so that we can test them).
				Properties sysProps = System.getProperties();
				for (Object key: props.keySet()) {
					if (!sysProps.containsKey(key)) {
						sysProps.put(key, props.get(key));
					}
				}
				System.setProperties(sysProps);
			} 
	    	
            instance = (ConnectionFactory)
                Class.forName("org.palo.api.impl.ConnectionFactoryImpl").newInstance();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static ConnectionFactory getInstance()
    {
        return instance;
    }
    
    
    //-------------------------------------------------------------------------
    /**
     * Creates a new {@link Connection} which is connected to the specified
     * palo-server. The default connection is of type <code>HTTP</code> and no
	 * load on demand is activated. To
     * define the connection type and to use load on demand please call
     * {@link #newConnection(String, String, String, String, boolean, int)}
     * 
     * @param server the server to connect to.
     * @param service the service to use (corresponds to port numbers given as a string)
     * @param user the username to use for authentication
     * @param pass the password to use for authentication
     * @return the palo-server connection upon success
     * @throws PaloAPIException thrown if connecting failed.
     * @deprecated please use {@link #newConnection(ConnectionConfiguration)
     */
    public abstract Connection newConnection(
        String server,
        String service,
        String user,
        String pass);
    
    /**
     * Creates a new {@link Connection} which is connected to the specified
     * palo-server using specified type (legacy of HTTP). Load on demand can
	 * be used. In this case the API tries to load only the information which
	 * is currently required.
     *
     * @param server the server to connect to.
     * @param service the service to use (corresponds to port numbers given as a string)
     * @param user the username to use for authentication
     * @param pass the password to use for authentication
	 * @param doLoadOnDemand activate load on demand
     * @param type palo server type to be used. Please use one of the defined
     * constants {@link Connection#TYPE_LEGACY} or {@link Connection#TYPE_HTTP}
     * @return the palo-server connection upon success
     * @throws PaloAPIException thrown if connecting failed.
     * @deprecated please use {@link #newConnection(ConnectionConfiguration)
     */
    public abstract Connection newConnection(
        String server,
        String service,
        String user,
        String pass,
        boolean doLoadOnDemand,
        int type);    
    
    /**
     * Creates a new {@link ConnectionConfiguration} instance. Only the name
     * of the palo server host and its service are set. All other fields
     * have their default values.
     * @param host host which runs the palo server
     * @param service the service which handles palo requests
     * @return new {@link ConnectionConfiguration} instance
     */
    public abstract ConnectionConfiguration getConfiguration(String host, String service);
    /**
     * Creates a new {@link ConnectionConfiguration} instance with the specified
     * settings for host, service, user and password.
     * @param host host which runs the palo server
     * @param service the service which handles palo requests
     * @param user the login name
     * @param password the login password
     * @return {@link ConnectionConfiguration} instance
     */
    public abstract ConnectionConfiguration getConfiguration(String host, String service, String user, String password);
    /**
     * Creates a new {@link Connection} using the connection settings from the
     * given {@link ConnectionConfiguration}
     * @param cfg {@link ConnectionConfiguration} containg connection settings
     * @return the palo-server connection upon success
     */
    public abstract Connection newConnection(ConnectionConfiguration cfg);
}
