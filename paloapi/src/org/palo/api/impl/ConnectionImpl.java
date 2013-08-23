/*
*
* @file ConnectionImpl.java
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
* @author Arnd Houben
*
* @version $Id: ConnectionImpl.java,v 1.99 2009/12/16 12:33:25 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

import org.palo.api.Connection;
import org.palo.api.ConnectionContext;
import org.palo.api.ConnectionEvent;
import org.palo.api.ConnectionListener;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.PaloAPIException;
import org.palo.api.Property2;
import org.palo.api.ext.favoriteviews.FavoriteViewTreeNode;
import org.palo.api.ext.favoriteviews.impl.FavoriteViewModel;

import com.tensegrity.palojava.ConnectionInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloServer;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.ServerInfo;
import com.tensegrity.palojava.events.ServerEvent;
import com.tensegrity.palojava.events.ServerListener;
import com.tensegrity.palojava.loader.DatabaseLoader;
import com.tensegrity.palojava.loader.FunctionLoader;
import com.tensegrity.palojava.loader.PropertyLoader;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @version $Id: ConnectionImpl.java,v 1.99 2009/12/16 12:33:25 PhilippBouillon Exp $
 */
public class ConnectionImpl implements Connection, ServerListener {
	
//	// Fix for PR 6732: Jedox Server does not support rules for this version.
//	private final static int MIN_RULES_MAJOR = 1;
//	private final static int MIN_RULES_MINOR = 5;
//	private final static int MIN_RULES_BUILD = 1646;
	static final boolean WPALO = Boolean.getBoolean("wpalo");

//	static final boolean WPALO;
//	static {		
//		WPALO = Boolean.getBoolean("wpalo");
//		System.err.println("requested wpalo: "+WPALO);
//		if(!WPALO)
//			throw new RuntimeException("WPALO IS FALSE!!!");
//	}

	private final DbConnection dbConnection;
	private final LinkedHashSet listeners;
	private final ConnectionInfo connectionInfo;
	private final PaloServer paloServer;
	private final ServerInfo serverInfo;
	//content:
//	private final Map dbInfos;
	private final Map<String, DatabaseImpl> loadedDatabases;
	private final Map <String, Property2Impl> loadedProperties;
	private final DatabaseLoader dbLoader;
	private final FunctionLoader funcLoader;
	private final PropertyLoader propertyLoader;
	private final ConnectionContextImpl context;
	private final String internalID;
	
    ConnectionImpl(PaloServer paloServer) {
    	this.internalID = UUID.randomUUID().toString();
    	this.paloServer = paloServer;
    	this.serverInfo = paloServer.getInfo();
    	this.dbConnection = paloServer.connect();
		this.loadedDatabases = new LinkedHashMap<String, DatabaseImpl>();
		this.loadedProperties = new LinkedHashMap<String, Property2Impl>();
//		this.dbInfos = new LinkedHashMap();
		this.listeners = new LinkedHashSet();
		connectionInfo = dbConnection.getInfo();
		if(!serverInfo.isLegacy())
			dbConnection.addServerListener(this);
//		loadDatabaseInfos();		
		this.dbLoader = dbConnection.getDatabaseLoader();
		this.funcLoader = dbConnection.getFunctionLoader();
		this.propertyLoader = dbConnection.getPropertyLoader();
		
		//context
		this.context = new ConnectionContextImpl(this);
		
		//passing wpalo flag...
		connectionInfo.setData("com.tensegrity.palo.wpalo", new Boolean(WPALO));
	}


    public final boolean login(String username, String password) {
		return dbConnection.login(username,password);
    }
    
	public final Database addDatabase(String name) {
		try {
			int infoType = com.tensegrity.palojava.PaloConstants.TYPE_NORMAL;
			DatabaseInfo dbInfo = dbLoader.create(name, infoType);
//			DatabaseInfo dbInfo = dbConnection.addDatabase(name);
//			if (loadedDatabases.containsKey(dbInfo.getId()))
//				throw new PaloAPIException("Database '" + name
//						+ "' already exists!!");
//
//TODO should we really fire an event here...
			Database database = createDatabase(dbInfo,true);
			
			fireEvent(new ConnectionEvent(this, this,
					ConnectionEvent.CONNECTION_EVENT_DATABASES_ADDED,
					new Database[] { database }));

			return database;
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(),pex);
		}
	}

	public final Database addUserInfoDatabase(String name) {
		try {
			int infoType = com.tensegrity.palojava.PaloConstants.TYPE_INFO;			
			DatabaseInfo dbInfo = dbLoader.create(name, infoType);
//			DatabaseInfo dbInfo = dbConnection.addDatabase(name);
//			if (loadedDatabases.containsKey(dbInfo.getId()))
//				throw new PaloAPIException("Database '" + name
//						+ "' already exists!!");
//
//TODO should we really fire an event here...
			Database database = createDatabase(dbInfo,true);
			
			fireEvent(new ConnectionEvent(this, this,
					ConnectionEvent.CONNECTION_EVENT_DATABASES_ADDED,
					new Database[] { database }));

			return database;
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(),pex);
		}		
	}
	public final void disconnect() {
		try {
//			if(!saveAll())
//				System.err.println("saving server status failed...");
			paloServer.disconnect();					
			loadedDatabases.clear();
//			dbInfos.clear();
			dbLoader.reset();
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getLocalizedMessage(), e);
		} finally {
			if (getType() == TYPE_HTTP) {
				((ConnectionFactoryImpl) ConnectionFactoryImpl.
					getInstance()).removePaloConnection(this);
			}
		}
	}

	public final Database getDatabaseAt(int index) {
		DatabaseInfo dbInfo = dbLoader.load(index);
		return getDatabase(dbInfo);
//		int infosCount = dbInfos.size();
//		if (index < infosCount && index >= 0) {
//			try {
//				Iterator it = dbInfos.values().iterator();
//				for (int i = 0; i < index; ++i) {
//					if (it.hasNext())
//						it.next();
//				}
//				return it.hasNext() ? 
//						getDatabase((DatabaseInfo)it.next()): null;
//			} catch (PaloException pex) {
//				throw new PaloAPIException(pex);
//			} catch (RuntimeException e) {
//				throw new PaloAPIException(e.getLocalizedMessage(), e);
//			}
//		}
//		return null; 
	}


	public final Database getDatabaseByName(String name) {
		DatabaseInfo dbInfo = dbLoader.loadByName(name);
		return getDatabase(dbInfo);
//		Iterator it = dbInfos.values().iterator();
//		while(it.hasNext()) {
//			DatabaseInfo dbInfo = (DatabaseInfo)it.next();
//			if(dbInfo.getName().equalsIgnoreCase(name)) 
//				return getDatabase(dbInfo);
//		}
//        return null;
	}
	
	public final Database getDatabaseById(String id) {
		try {
			DatabaseInfo dbInfo= dbLoader.load(id);
			return getDatabase(dbInfo);
		}catch(PaloException pex) {
			/* ignore */
		}
		return null;
//		DatabaseInfo dbInfo = (DatabaseInfo)dbInfos.get(id);
//		if(dbInfo==null)
//			return null;
////			throw new PaloAPIException("Invalid database id! No database not found with id: "+id);
//		return getDatabase(dbInfo);
	}

	public final int getDatabaseCount() {
//		return dbInfos.size();
		try {
			return dbLoader.getDatabaseCount();
		} catch (PaloException e) {
			throw new PaloAPIException(e);
		}
	}

	public final Database[] getDatabases() {
		String[] ids = dbLoader.getAllDatabaseIds();
		ArrayList<Database> databases = new ArrayList<Database>(); 	//to filter out null databases!! => TODO better thrown an exception here???
		for(String id : ids) {
			DatabaseInfo dbInfo = dbLoader.load(id);
			Database db = getDatabase(dbInfo);
			if(db != null)
				databases.add(db);
		}
		return (Database[])databases.toArray(new Database[databases.size()]);
//		ArrayList databases = new ArrayList(); 	//to filter out null databases!! => TODO better thrown an exception here???
//		Iterator it = dbInfos.values().iterator();
//		while(it.hasNext()) {
//			DatabaseInfo dbInfo = (DatabaseInfo)it.next();
//			try {
//				Database db = getDatabase(dbInfo);
//				if (db != null)
//					databases.add(db);
//			} catch (Exception ex) {
//				System.err.println("exception during loading of database: "
//						+ (dbInfo != null ? dbInfo.getName()
//								: "name not available"));
//			}
//		}
//		return (Database[])databases.toArray(new Database[databases.size()]);
	}

	public final String getPassword() {
		return connectionInfo.getPassword();
	}

	public final String getServer() {
		return connectionInfo.getHost();
	}

	public final String getService() {
		return connectionInfo.getPort();
	}

	public final Database[] getSystemDatabases() {
		String[] ids = dbLoader.getAllDatabaseIds();
		ArrayList<Database> sysDbs = new ArrayList<Database>(); 	//to filter out null databases!! => TODO better thrown an exception here???
		for(String id : ids) {
			DatabaseInfo dbInfo = dbLoader.load(id);
			if(dbInfo != null && dbInfo.isSystem()) {
				Database db = getDatabase(dbInfo);
				if(db != null)
					sysDbs.add(db);
			}
		}
		return (Database[])sysDbs.toArray(new Database[sysDbs.size()]);

//		ArrayList sysDBs = new ArrayList();
//		Iterator it = dbInfos.values().iterator();
//		while(it.hasNext()) {
//			DatabaseInfo dbInfo = (DatabaseInfo)it.next();
//			if(dbInfo.getType() == DatabaseInfo.TYPE_SYSTEM) {
//				Database db = getDatabase(dbInfo);
//				if(db != null)
//					sysDBs.add(db);
//			}
//		}
//		return (Database[])sysDBs.toArray(new Database[sysDBs.size()]);
	}

	public final Database [] getUserInfoDatabases() {
		String[] ids = dbLoader.getAllDatabaseIds();
		ArrayList<Database> sysDbs = new ArrayList<Database>(); 	//to filter out null databases!! => TODO better thrown an exception here???
		for(String id : ids) {
			DatabaseInfo dbInfo = dbLoader.load(id);
			if(dbInfo != null && dbInfo.isUserInfo()) {
				Database db = getDatabase(dbInfo);
				if(db != null)
					sysDbs.add(db);
			}
		}
		return (Database[])sysDbs.toArray(new Database[sysDbs.size()]);		
	}
	
	public final String getUsername() {
		return connectionInfo.getUsername();
	}

	public final boolean isLegacy() {
		return serverInfo.isLegacy();
	}
	
	public final int getType() {
		return serverInfo.getType();
	}
	
	public final boolean isConnected() {
		return dbConnection.isConnected();
	}
	
	public final void ping() {
		paloServer.ping();
	}

	public final void reload() {
		//reset all loaders...
		dbLoader.reset();
		if (funcLoader != null)
			funcLoader.reset();
		if (propertyLoader != null)
			propertyLoader.reset();
		
		//NOTE: clearCaches() doesn't fire any events!!
		if(WPALO)
			clearCaches();
		else {
			if (serverInfo.getType() == Connection.TYPE_XMLA) {
				for (Database db: loadedDatabases.values()) {
					((DatabaseImpl) db).init();
				}
			} else {
				reloadAlllDatabases(true);
			}
		}
	}

	public final void clearCache() {
		clearCaches();
	}
	
	public final void removeDatabase(Database database) {
		String dbId = database.getId();
		if (!loadedDatabases.containsKey(dbId))
			return;
		DatabaseInfo dbInfo = ((DatabaseImpl)database).getInfo();
//		if (dbConnection.delete(dbInfo)) {
//			loadedDatabases.remove(dbId);
//			dbLoader.unloadDatabase(dbInfo);			
////			dbInfos.remove(dbId);
//			fireEvent(new ConnectionEvent(this, this,
//					ConnectionEvent.CONNECTION_EVENT_DATABASES_REMOVED,
//					new Database[] { database }));
//		}
		if (dbLoader.delete(dbInfo)) {
			loadedDatabases.remove(dbId);
			fireEvent(new ConnectionEvent(this, this,
					ConnectionEvent.CONNECTION_EVENT_DATABASES_REMOVED,
					new Database[] { database }));
		}
	}

	public final boolean save() {
        return dbConnection.save(serverInfo);
	}
	
//TODO check lazy load of favorite views...	
	public final FavoriteViewTreeNode loadFavoriteViews() {
		FavoriteViewModel bm = new FavoriteViewModel();
		return bm.loadFavoriteViews(this);
	}
	
	public synchronized final void storeFavoriteViews(FavoriteViewTreeNode favoriteViews) {
		FavoriteViewModel bm = new FavoriteViewModel();
		bm.storeFavoriteViews(this, favoriteViews);				
	}

    // -------------------------------------------------------------------------
    // EVENTS
	//
	public final void serverStructureChanged(ServerEvent event) {
		int evType = event.getType();		
		//TODO more finer checks e.g. DATABASE_CHANGED...
		if(evType==ServerEvent.SERVER_DOWN) 
			evType = ConnectionEvent.CONNECTION_EVENT_SERVER_DOWN;
		else //if(evType == ServerEvent.SERVER_CHANGED)
			evType = ConnectionEvent.CONNECTION_EVENT_SERVER_STRUCTURE_CHANGED;
			
		fireEvent(new ConnectionEvent(this, this, evType, new Object[0]));
	}

    public final void addConnectionListener(ConnectionListener connectionListener) {
		listeners.add(connectionListener);
	}

	public final void removeConnectionListener(ConnectionListener connectionListener) {
		listeners.remove(connectionListener);
	}

	public final String getFunctions() {
//		if(!supportsRules())
//			return "";
//		//TODO should we cache functions too?
//		return getConnectionInternal().listFunctions();
		return funcLoader.loadAll();
	}
	
	public final boolean equals(Object other) {
		if (other instanceof ConnectionImpl) {
			ConnectionImpl ot = (ConnectionImpl) other;
//			boolean equals = connectionInfo.getHost().equals(
//					ot.connectionInfo.getHost())
//					&& connectionInfo.getPort().equals(
//							ot.connectionInfo.getPort());
//			//user name could not have been set...
//			if (connectionInfo.getUsername() != null)
//				equals = equals
//						&& connectionInfo.getUsername().equals(
//								ot.connectionInfo.getUsername());
//			else
//				equals = equals && (ot.connectionInfo.getUsername() == null);
//			return equals;
			return internalID.equals(ot.internalID) &&
				connectionInfo.equals(ot.connectionInfo);
		}
		return false;
	}

	public final int hashCode() {
		int hc = 87;
		hc += 31 * internalID.hashCode();
		hc += 27 * connectionInfo.hashCode(); 
//		hc += 41 * connectionInfo.getHost().hashCode() 
//				+ 31 * connectionInfo.getPort().hashCode();
//		if (connectionInfo.getUsername() != null)
//			hc += 27 * connectionInfo.getUsername().hashCode();
		return hc;
	}


	public final ConnectionContext getContext() {
		return context;
	}
	
	public final Object getData(String id) {
		return connectionInfo.getData(id);
	}
	
	//-------------------------------------------------------------------------
	// PACKAGE INTERNAL
	//
    final void fireEvent(ConnectionEvent event) {
		// System.err.println ("ConnectionImpl.fireEvent: " + event);
		ArrayList copy = new ArrayList(listeners);
		for (int i = 0; i < copy.size(); ++i) {
			ConnectionListener listener = (ConnectionListener) copy.get(i);
			try {
				listener.connectionChanged(event);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

//    final Map getCache(Object key) {
//		Map cache = (Map) caches.get(key);
//		if (cache == null) {
//			cache = new HashMap();
//			caches.put(key, cache);
//		}
//		return cache;
//	}

    public final DbConnection getConnectionInternal() {
		return dbConnection;
	}

    private final void clearCaches() {
    	//clear all database caches:
    	for(DatabaseImpl db : loadedDatabases.values() ) {
    		db.clearCache();
    	}
    	loadedDatabases.clear();
    	dbLoader.reset();
    	
    	for(Property2Impl property : loadedProperties.values()) {
    		property.clearCache();
    	}
    	loadedProperties.clear();
    	propertyLoader.reset();
    }
    
    final synchronized void reloadAlllDatabases(boolean doEvents) {
        HashMap<String, DatabaseImpl> oldDatabases = 
        	new HashMap<String, DatabaseImpl>(loadedDatabases);
        loadedDatabases.clear();
        dbLoader.reset();
        Exception nestedError = null;
        try {
        	String[] dbIDs = dbLoader.getAllDatabaseIds();
        	for(String id : dbIDs) {
        		try {
					DatabaseImpl db = oldDatabases.get(id);
					if (db == null) {
						DatabaseInfo dbInfo = dbLoader.load(id);
						db = createDatabase(dbInfo, doEvents);
					}
					loadedDatabases.put(id, db);
					// do a reload
					((DatabaseImpl) db).init(doEvents);
				} catch (RuntimeException e) {
					nestedError = e;
				}
			}
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getLocalizedMessage(), e);
		}
        
 
        if (doEvents) {
			LinkedHashSet removedDatabases = 
				new LinkedHashSet(oldDatabases.values());
			removedDatabases.removeAll(loadedDatabases.values());
			if (removedDatabases.size() > 0) {
				fireEvent(new ConnectionEvent(this, this,
						ConnectionEvent.CONNECTION_EVENT_DATABASES_REMOVED,
						removedDatabases.toArray()));
			}
		}

		if (doEvents) {
			LinkedHashSet addedDatabases = 
				new LinkedHashSet(loadedDatabases.values());
			addedDatabases.removeAll(oldDatabases.values());
			if (addedDatabases.size() > 0) {
				fireEvent(new ConnectionEvent(this, this,
						ConnectionEvent.CONNECTION_EVENT_DATABASES_ADDED,
						addedDatabases.toArray()));
			}
		}

		if (nestedError != null) {
			System.err.println("invalid database skipped: "
					+ nestedError.getLocalizedMessage());
		}
    }
//    final synchronized void initDatabases(boolean doEvents) {
//        LinkedHashMap oldDatabases = new LinkedHashMap(loadedDatabases);
//        loadedDatabases.clear();
//        dbInfos.clear();
//        Exception nestedError = null;
//        try {
//			DatabaseInfo[] _dbInfos = dbConnection.getDatabases();
//			for (int i = 0; i < _dbInfos.length; ++i) {
//				try {
//					Database db = 
//						(Database)oldDatabases.get(_dbInfos[i].getId());
//					if(db == null) {
//						db = createDatabase(_dbInfos[i], doEvents);						
//					}
//					loadedDatabases.put(db.getId(), db);
//					dbInfos.put(_dbInfos[i].getId(), _dbInfos[i]);
//					// do a reload
//					((DatabaseImpl)db).init(doEvents);		
//				} catch (RuntimeException e) {
//					nestedError = e;
//				}
//			}
//		} catch (PaloException pex) {
//			throw new PaloAPIException(pex);			
//		} catch (RuntimeException e) {
//			throw new PaloAPIException(e.getLocalizedMessage(), e);
//		}
//        
// 
//        if (doEvents) {
//			LinkedHashSet removedDatabases = 
//				new LinkedHashSet(oldDatabases.values());
//			removedDatabases.removeAll(loadedDatabases.values());
//			if (removedDatabases.size() > 0) {
//				fireEvent(new ConnectionEvent(this, this,
//						ConnectionEvent.CONNECTION_EVENT_DATABASES_REMOVED,
//						removedDatabases.toArray()));
//			}
//		}
//
//		if (doEvents) {
//			LinkedHashSet addedDatabases = 
//				new LinkedHashSet(loadedDatabases.values());
//			addedDatabases.removeAll(oldDatabases.values());
//			if (addedDatabases.size() > 0) {
//				fireEvent(new ConnectionEvent(this, this,
//						ConnectionEvent.CONNECTION_EVENT_DATABASES_ADDED,
//						addedDatabases.toArray()));
//			}
//		}
//
//		if (nestedError != null) {
//			System.err.println("invalid database skipped: "
//					+ nestedError.getLocalizedMessage());
//		}
//    }
    
//    final boolean supportsRules() {
//		ServerInfo server = dbConnection.getServerInfo();
//		if (server.getMajor() < MIN_RULES_MAJOR) {
//			return false;
//		} else if (server.getMajor() == MIN_RULES_MAJOR) {
//			if (server.getMinor() < MIN_RULES_MINOR) {
//				return false;
//			} else if (server.getMinor() == MIN_RULES_MINOR) {
//				if (server.getBuildNumber() <= MIN_RULES_BUILD) {
//					return false;
//				}
//			}
//		}
//		return true;
//    }
    
//	final void loadDatabaseInfos() {
//		DatabaseInfo[] infos = dbConnection.getDatabases();
//		dbInfos.clear();
//		for(int i=0;i<infos.length;++i)
//			dbInfos.put(infos[i].getId(), infos[i]);
//	}

    final boolean supportsRules() {
    	return dbConnection.supportsRules();
    }

	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	//
//	private final void checkSystem(Database database) {
//		if (database.isSystem()) { // dbInfo.getType() ==
//									// DatabaseInfo.TYPE_SYSTEM) {
//			sysDatabases.put(database.getId(), database);
//		} else {
//			// maybe it is one of our system databases
//			if (PaloObjects.isSystemDatabase(database)) {
//				((DatabaseImpl) database).isSystem(true);
//				sysDatabases.put(database.getId(), database);
//			}
//			
//		}
//	}
	
	/**
	 * Checks if the corresponding database instance to the given database info is
	 * already loaded and returns it. If no database instance was created so 
	 * far, this method will do it...
	 * @param dbInfo
	 * @return
	 */
	private final Database getDatabase(DatabaseInfo dbInfo) {
		if(dbInfo == null)
			return null;
		Database database = (Database)loadedDatabases.get(dbInfo.getId());
		if(database == null) {
			//not loaded yet, create it...
			database = createDatabase(dbInfo,true);
		}
		return database;
	}

	/**
	 * Creates a new database instance from the given databasinfo and adds it to
	 * the list of all loaded databases
	 * @param dbInfo
	 * @return
	 */
	private final DatabaseImpl createDatabase(DatabaseInfo dbInfo, boolean fireEvent) {
		try {
			DatabaseImpl database = DatabaseImpl
					.create(this, dbInfo, fireEvent);
			// database.init(fireEvent);
			loadedDatabases.put(database.getId(), database);
			// dbLoader.loaded(dbInfo);
			// dbInfos.put(dbInfo.getId(), dbInfo);
			return database;
		} catch (PaloException pex) {
			pex.printStackTrace();
			System.err.println("failed to load database '" + dbInfo.getName()
					+ "'!! - skipped!! -");
		}
		return null;
	}

	
	public String [] getAllPropertyIds() {
		return propertyLoader.getAllPropertyIds();
	}

	public Property2 getProperty(String id) {
		PropertyInfo propInfo = propertyLoader.load(id);
		if (propInfo == null) {
			return null;
		}
		Property2 property = loadedProperties.get(propInfo.getId());
		if (property == null) {
			property = createProperty(propInfo);
		}

		return property;		
	}
	
	public void addProperty(Property2 property) {
		if (property == null) {
			return;
		}
		Property2Impl _property = (Property2Impl)property;
		propertyLoader.loaded(_property.getPropInfo());
		loadedProperties.put(_property.getId(), _property);
	}
	
	public void removeProperty(String id) {
		Property2 property = getProperty(id); 
		if (property == null) {
			return;
		}
		if (property.isReadOnly()) {
			return;
		}
		loadedProperties.remove(property);		
	}

	private void createProperty(Property2 parent, PropertyInfo kid) {
		Property2 p2Kid = Property2Impl.create(parent, kid);
		parent.addChild(p2Kid);
		for (PropertyInfo kidd : kid.getChildren()) {
			createProperty(p2Kid, kidd);
		}
	}

	private Property2 createProperty(PropertyInfo propInfo) {
		Property2 prop = Property2Impl.create(null, propInfo);
		for (PropertyInfo kid : propInfo.getChildren()) {
			createProperty(prop, kid);
		}
		return prop;
	}


	public boolean canBeModified() {
		return true;
	}


	public boolean canCreateChildren() {
		return getType() != Connection.TYPE_XMLA;
	}
	
	private final boolean saveAll() {
		boolean success = true;
		//save all cubes and databases
		for(Database database : loadedDatabases.values()) {
			Cube[] cubes = database.getCubes();
			for(Cube cube : cubes)
				success &= ((CubeImpl)cube).save();
			//save database:
			success &= database.save(); 
		}
		//finally save this server:
		success &= save();
		return success;
	}
	
	public final ServerInfo getInfo() {
		return serverInfo;
	}
}
