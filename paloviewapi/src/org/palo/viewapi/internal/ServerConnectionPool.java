/*
*
* @file ServerConnectionPool.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: ServerConnectionPool.java,v 1.14 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

package org.palo.viewapi.internal;

import org.palo.api.Connection;
import org.palo.api.ConnectionContext;
import org.palo.api.ConnectionListener;
import org.palo.api.Database;
import org.palo.api.Property2;
import org.palo.api.ext.favoriteviews.FavoriteViewTreeNode;

import com.tensegrity.palojava.ServerInfo;

public class ServerConnectionPool {
    private final ConnectionWrapper[] connections;

    private final IConnectionFactory factory;
    private final String host;
    private final String service;
    private final String login;
    private final String password;
	private final String provider;
    private final Object mutex = new Object();

    public ServerConnectionPool(IConnectionFactory factory, String host, String service, String login, String password, int capacity, String provider){
    	this.factory = factory;
        this.host = host;
        this.service = service;
        this.login = login;
        this.password = password;
        this.provider = provider;
        connections = new ConnectionWrapper[capacity];
        synchronized (mutex) {
        	getConnection("ServerConnectionPool").disconnect();//load one connection for fast access;
//        	System.err.println("Disconnect ServerConnectionPool");
        }
    }
    
    /**
     * Marks all connection as need reload.
     * So on the next request of the connection it will be reloaded.
     */
    public void markNeedReload() {
    	for (int i = 0; i < connections.length; i++) {
    		if(connections[i] != null)
    			connections[i].setNeedReload();
		}
    }

    private ConnectionWrapper openConnection(int index){
        synchronized (mutex) {
//            System.err.println("openConnection("+index+")");
            Connection nativeConnection = factory.createConnection(host, service, login, password, provider);
            ConnectionWrapper conn = new ConnectionWrapper(nativeConnection, index);
            return conn;
        }
    }

    /**
     * Gives free connection.
     * connection must be freed by calling {@link Connection#disconnect()}; 
     * can throw {@link RuntimeException} that "no more connections available"
     */
    public Connection getConnection(String from){
    	ConnectionWrapper result = null;
//        System.err.println("getConnection " + from);
        synchronized (mutex) {
            for(int i = 0; (i < connections.length) && (result == null); ++i){
            	result = tryGetConnection(i);
            }
        }
//        if(result == null) 
//        	throw new RuntimeException("no more connections available");

        if(result == null) {
//        	System.err.println("-- Whoops! Failed... Trying again...");
        	shutdown();
            synchronized (mutex) {
                for(int i = 0; (i < connections.length) && (result == null); ++i){
                	result = tryGetConnection(i);
                }
            }        	
        }
        
        if (result == null) 
    	    throw new RuntimeException("no more connections available");
        
        return result;
    }

	private ConnectionWrapper tryGetConnection(int i) {
		ConnectionWrapper result = null;
		if(connections[i] == null)
			connections[i] = openConnection(i);
		if(!connections[i].isBusy()){
			result = connections[i];
			result.setBusy();
			result.reloadOnDemand();
		}
		return result;
	}

    /**
     * Shutdow all connections in the pool.
     */
    public void shutdown(){
        synchronized (mutex) {
            for (int i = 0; i < connections.length; i++) {
            	if(connections[i] != null)
            		connections[i].disconnect();
            }
        }
    }

    public void disconnectAll(){
        synchronized (mutex) {
            for (int i = 0; i < connections.length; i++) {
            	if(connections[i] != null) {
            		connections[i].clearCache();
//            		connections[i].quit();
            	}
            	connections[i] = null;
            }
        }
    }

    private class ConnectionWrapper implements Connection{
        private final Connection conn;
        private final int index;
        private boolean busy = false;
        private boolean needReload = false;
        private final Object mutex = new Object();


        public void setBusy() {
        	synchronized (mutex) {
        		if(!isBusy()){
        			busy = true;
//        			System.err.println(this + " -> busy");
        		}
//        		else {
////        			System.err.println("Someone tried to grab a busy " + this + "!");
//        		}
			}
        }

        public boolean isBusy() {
            return busy;
        }

        public void setNeedReload() {
        	synchronized (mutex) {
        		if(!isNeedReload()) {
                	needReload = true;
//                	System.err.println(this + " -> need reload");
            	}
			}

        }

        public boolean isNeedReload() {
            return needReload;
        }

        public ConnectionWrapper(final Connection conn, final int index){
            this.conn = conn;
            this.index = index;
            /*conn.addConnectionListener(new ConnectionListener(){
                public void connectionChanged(ConnectionEvent arg0) {
                    log.debug(this + ": changed!");
                    setNeedReload();
                }

            });
            */
        }

        public void reloadOnDemand(){
            if(isNeedReload()) {
                reload();
            }
        }

        public void forceDisconnect(){
//        	System.err.println("conn");
            conn.disconnect();
        }

        public void addConnectionListener(ConnectionListener listener) {
            conn.addConnectionListener(listener);
        }

        public Database addDatabase(String db) {
            return conn.addDatabase(db);
        }
        
        public Database addUserInfoDatabase(String db) {
        	return conn.addUserInfoDatabase(db);
        }

        public void disconnect() {
//        	System.err.println(this.mutex + ".disconnect()");
            synchronized (mutex) {
                if(isBusy()){
//                	System.err.println("connection["+index+"] -> available");
                    busy = false;
                }
//                else {
////                	System.err.println("Someone tried to disconnect free connection["+index+"]");
//                }
            }
        }

        public void quit() {
        	synchronized (mutex) {
                if(isBusy()){
                    busy = false;
                }
                conn.disconnect();
            }
        }

        public Database getDatabaseAt(int index) {
            return conn.getDatabaseAt(index);
        }

        public Database getDatabaseByName(String name) {
            return conn.getDatabaseByName(name);
        }

        public int getDatabaseCount() {
            return conn.getDatabaseCount();
        }

        public Database[] getDatabases() {
            return conn.getDatabases();
        }

        public String getPassword() {
            return conn.getPassword();
        }

        public String getServer() {
            return conn.getServer();
        }

        public String getService() {
            return conn.getService();
        }

        public Database[] getSystemDatabases() {
            return conn.getSystemDatabases();
        }

        public String getUsername() {
            return conn.getUsername();
        }

        public void ping() {
            conn.ping();
        }

        public void reload() {
        	synchronized (mutex) {
//        		System.err.println(this+".reloading");
            	conn.reload();
            	needReload = false;
			}
        }

        public void removeConnectionListener(ConnectionListener listner) {
            conn.removeConnectionListener(listner);
        }

        public void removeDatabase(Database db) {
            conn.removeDatabase(db);
        }

        public boolean save() {
            return conn.save();
        }

        public boolean isLegacy() {
            return conn.isLegacy();
        }

        public String toString() {
            return "connection["+index+"]";
        }

		public String getFunctions() {
			return conn.getFunctions();
		}

		public boolean isConnected() {
			return conn.isConnected();
		}

		public boolean login(String arg0, String arg1) {
			return conn.login(arg0, arg1);
		}

		public FavoriteViewTreeNode loadFavoriteViews() {
			return conn.loadFavoriteViews();
		}

		public void storeFavoriteViews(FavoriteViewTreeNode node) {
			conn.storeFavoriteViews(node);
		}

		public Database getDatabaseById(String id) {
			return conn.getDatabaseById(id);
		}

		public int getType() {
			return conn.getType();
		}

		public void addProperty(Property2 arg0) {
			conn.addProperty(arg0);
		}

		public String[] getAllPropertyIds() {
			return conn.getAllPropertyIds();
		}

		public ConnectionContext getContext() {
			return conn.getContext();
		}

		public Property2 getProperty(String arg0) {
			return conn.getProperty(arg0);
		}

		public void removeProperty(String arg0) {
			conn.removeProperty(arg0);
		}

		public boolean canBeModified() {
			return conn.canBeModified();
		}

		public boolean canCreateChildren() {
			return conn.canCreateChildren();
		}

		public Object getData(String id) {
			// TODO Auto-generated method stub
			return null;
		}
		
		public void clearCache() {
			conn.clearCache();
		}

		public ServerInfo getInfo() {
			return conn.getInfo();
		}

		public Database[] getUserInfoDatabases() {
			// TODO Auto-generated method stub
			return null;
		}
    }

}
