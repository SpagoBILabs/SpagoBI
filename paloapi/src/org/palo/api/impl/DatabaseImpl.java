/*
*
* @file DatabaseImpl.java
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
* @version $Id: DatabaseImpl.java,v 1.99 2010/02/09 11:44:57 PhilippBouillon Exp $
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

import org.palo.api.Connection;
import org.palo.api.ConnectionEvent;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.PaloAPIException;
import org.palo.api.PaloConstants;
import org.palo.api.Property2;
import org.palo.api.Rights;
import org.palo.api.Subset;
import org.palo.api.VirtualCubeDefinition;
import org.palo.api.impl.views.CubeViewManager;
import org.palo.api.subsets.SubsetStorageHandler;
import org.palo.api.subsets.io.SubsetIOHandler;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.loader.CubeLoader;
import com.tensegrity.palojava.loader.DimensionLoader;
import com.tensegrity.palojava.loader.PropertyLoader;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @author Stepan Rutz
 * @version $Id: DatabaseImpl.java,v 1.99 2010/02/09 11:44:57 PhilippBouillon Exp $
 */
public class DatabaseImpl extends AbstractPaloObject implements Database {
	
	//--------------------------------------------------------------------------
	// FACTORY 
	//
	final static DatabaseImpl create(ConnectionImpl connection,
			DatabaseInfo dbInfo, boolean doEvents) {
		return new DatabaseImpl(connection, dbInfo);
	}

	// --------------------------------------------------------------------------
	// INSTANCE 
	//
    private final DatabaseInfo dbInfo;
    private final ConnectionImpl connection;
    private final DbConnection dbConnection;
    private final LinkedHashSet<Cube> virtualCubes;
//    private final Map dimInfos;
    private final Map<String, DimensionImpl> loadedDimensions;
//    private final Map hierInfos;
//    private final Map<String, HierarchyImpl> loadedHierarchies;
//    private final Map cubeInfos;
    private final Map<String, CubeImpl> loadedCubes;
//    private final Map failedViews;
    private final CompoundKey key;
    private final org.palo.api.impl.SubsetStorageHandler legacySubsetHandler;
    private final CubeViewStorageHandler viewStorageHandler;
    private final CubeLoader cubeLoader;
    private final DimensionLoader dimLoader;
//    private final HierarchyLoader hierLoader;
	private final PropertyLoader propertyLoader;
	private final Map <String, Property2Impl> loadedProperties;

	private final SubsetIOHandler subsetIOHandler;

    private boolean batchMode;
    private boolean isSystem;

    
    private DatabaseImpl(ConnectionImpl connection, DatabaseInfo dbInfo) {
		this.dbInfo = dbInfo;
		this.connection = connection;
		this.dbConnection = connection.getConnectionInternal();
		this.virtualCubes = new LinkedHashSet<Cube>();
//		this.dimInfos = new LinkedHashMap();
		this.loadedDimensions = new LinkedHashMap<String, DimensionImpl>();
//		this.hierInfos = new LinkedHashMap();
//		this.loadedHierarchies = new LinkedHashMap<String, HierarchyImpl>();
//		this.cubeInfos = new LinkedHashMap();
		this.loadedCubes = new LinkedHashMap<String, CubeImpl>();
//		this.failedViews = new HashMap();
		
		//LOADER:
	    this.cubeLoader = dbConnection.getCubeLoader(dbInfo);
	    this.dimLoader = dbConnection.getDimensionLoader(dbInfo);
//		this.hierLoader = dbConnection.getHierarchyLoader(dbInfo);
	    
		//SUBSETS:
	    this.legacySubsetHandler = 
	    		new org.palo.api.impl.SubsetStorageHandler(this); //for handling legacy subsets
	    this.viewStorageHandler = new CubeViewStorageHandler(this); 
	    //NEW SUSBETS:
	    this.subsetIOHandler = new SubsetIOHandler(this); //handles new subsets...
	    
		this.key = new CompoundKey(new Object[] { DatabaseImpl.class,
				connection, dbInfo.getId()});
		isSystem = dbInfo.getType() == DatabaseInfo.TYPE_SYSTEM ||
		           dbInfo.getName().equals(PaloConstants.PALO_CLIENT_SYSTEM_DATABASE);
		this.loadedProperties = new HashMap <String, Property2Impl> ();
		this.propertyLoader = dbConnection.getTypedPropertyLoader(dbInfo);
		
//		loadInfos();
		init();		
	}

	public final Cube addCube(String name, Dimension[] dimensions) {
		return addCubeInternal(name, dimensions, TYPE_NORMAL);
	}

	public final Cube addUserInfoCube(String name, Dimension[] dimensions) {
		return addCubeInternal(name, dimensions, TYPE_USER_INFO);
	}
	
	public final Cube addCube(VirtualCubeDefinition definition) {
    	//overwritten equals() and hashCode() of VirtualCubeImpl 
    	//=> virtualCubes will not grow...
        VirtualCubeImpl impl = new VirtualCubeImpl(definition); 
        //PR 6567:
        if(virtualCubes.contains(impl)) 
        	virtualCubes.remove(impl);
        virtualCubes.add(impl);
        fireCubesAdded(new Cube[]{impl});
//TODO in case of virtual cubes, can we really save the reloadDatabase(true) call??!!       
//      currently we have to reload complete db to read in changed system objects, like #_CUBE_ dimension... 
//      later, when we support load on demand, this will be superfluous and a simple fireEvent will do!!			
//        reloadDatabase(true);
        return impl; //getCubeByName(impl.getName());
	}

	public final Dimension addDimension(String name) {
		return addDimensionInternal(name, TYPE_NORMAL);
	}
	public final Dimension addUserInfoDimension(String name) {
		return addDimensionInternal(name, TYPE_USER_INFO);
	}
	
	public final synchronized void endBatchUpdate() {
        batchMode = false;
        reloadInternal(true);
	}

	public final Connection getConnection() {
		return connection;
	}

	public final Cube getCubeAt(int index) {
		CubeInfo cube = cubeLoader.load(index);
		return getCube(cube);
//		int infosCount = cubeInfos.size();
//		if (index < infosCount && index >= 0) {
//			try {
//				Iterator it = cubeInfos.values().iterator();
//				for (int i = 0; i < index; ++i) {
//					if (it.hasNext())
//						it.next();
//				}
//				return it.hasNext() ? 
//						getCube((CubeInfo)it.next()): null;
//			} catch (PaloException pex) {
//				throw new PaloAPIException(pex);						
//			} catch (RuntimeException e) {
//				throw new PaloAPIException(e.getLocalizedMessage(), e);
//			}
//		}
//		return null;
	}

	public final Cube getCubeByName(String name) {
		if(name.indexOf("@@")>0) {
			//virtual cube !!!
			for(Cube cube : virtualCubes) {
				if(cube.getName().equalsIgnoreCase(name))
					return cube;
			}
		} else {
			CubeInfo cube = cubeLoader.loadByName(name);
			return getCube(cube);
		}
		return null;
//		else 
//			it = cubeInfos.values().iterator();
//		while(it.hasNext()) {
//			CubeInfo cubeInfo = (CubeInfo)it.next();
//			if (cubeInfo.getName().equalsIgnoreCase(name))
//				return getCube(cubeInfo);
//		}
//        return null;
//        
	}

	public final Cube getCubeById(String id) {
		try {
			CubeInfo cube = cubeLoader.load(id);
			return getCube(cube);
		}catch(PaloException pex) {
			/* ignore */
		}
		return null;
//		CubeInfo cubeInfo = (CubeInfo)cubeInfos.get(id);
//		return getCube(cubeInfo);
	}
	
	public final int getCubeCount() {		
//		return cubeInfos.size();
		return dbInfo.getCubeCount();
	}

	public final Cube[] getCubes() {
		String[] cubeIds = cubeLoader.getAllCubeIds();
		return loadCubes(cubeIds);
//		ArrayList<Cube> cubes = new ArrayList<Cube>(); 	//to filter out null databases!! => TODO better thrown an exception here???
//		for(String id : ids) {
//			CubeInfo info = cubeLoader.load(id);
//			Cube cube = getCube(info);
//			if(cube != null)
//				cubes.add(cube);
//		}
//		return (Cube[])cubes.toArray(new Cube[cubes.size()]);
//
//		ArrayList cubes = new ArrayList(); 	//to filter out null cubes!!
//		Iterator it = cubeInfos.values().iterator();
//		while(it.hasNext()) {
//			CubeInfo info = (CubeInfo)it.next();
//			Cube cube= getCube(info);
//			if(cube != null)
//				cubes.add(cube);
//		}
//		return (Cube[])cubes.toArray(new Cube[cubes.size()]);
	}

	public final Cube [] getCubes(int typeMask) {
		String [] cubeIds = cubeLoader.getCubeIds(typeMask);
		return loadCubes(cubeIds);
	}
	
	final Cube[] getCubes(Dimension dimension) {
		if(!(dimension instanceof DimensionImpl))
			return new Cube[0];
		DimensionInfo dimInfo = ((DimensionImpl)dimension).getInfo();
		String[] cubeIds = cubeLoader.getCubeIds(dimInfo);
		return loadCubes(cubeIds);
	}

//	final Cube[] getCubes(Dimension dimension, int type) {
//		if(!(dimension instanceof DimensionImpl))
//			return new Cube[0];
//		DimensionInfo dimInfo = ((DimensionImpl)dimension).getInfo();
//		String[] cubeIds = cubeLoader.getCubeIds(dimInfo, type);
//		return loadCubes(cubeIds);
//	}

	public final Dimension getDimensionAt(int index) {
		DimensionInfo dim = dimLoader.load(index);
		return getDimension(dim);

//		int infosCount = dimInfos.size();
//		if (index < infosCount && index >= 0) {
//			try {
//				Iterator it = dimInfos.values().iterator();
//				for (int i = 0; i < index; ++i) {
//					if (it.hasNext())
//						it.next();
//				}
//				return it.hasNext() ? getDimension((DimensionInfo) it.next())
//						: null;
//			} catch (PaloException pex) {
//				throw new PaloAPIException(pex);				
//			} catch (RuntimeException e) {
//				throw new PaloAPIException(e.getLocalizedMessage(), e);
//			}
//		}
//		return null;
	}

	public final Dimension getDimensionByName(String name) {
		DimensionInfo dim = dimLoader.loadByName(name);
		return getDimension(dim);

//		Iterator it = dimInfos.values().iterator();
//		while(it.hasNext()) {
//			DimensionInfo dimInfo = (DimensionInfo)it.next();
//			if(dimInfo.getName().equalsIgnoreCase(name))
//				return getDimension(dimInfo);
//		}
//        return null;
	}
	
	public final Dimension getDimensionById(String id) {
		try {
			DimensionInfo dim = dimLoader.load(id);
			return getDimension(dim);
		}catch(PaloException pex) {
			/* ignore */
		}
		return null;
//		DimensionInfo dimInfo = (DimensionInfo)dimInfos.get(id);
//		return getDimension(dimInfo);
	}

	public final int getDimensionCount() {
//		return dimInfos.size();
		return dbInfo.getDimensionCount();
	}

	public final Dimension[] getDimensions() {
		String[] ids = dimLoader.getAllDimensionIds();
		ArrayList<Dimension> dims= new ArrayList<Dimension>(); 	//to filter out null databases!! => TODO better thrown an exception here???
		for(String id : ids) {
			DimensionInfo info = dimLoader.load(id);
			Dimension dim = getDimension(info);
			if(dim != null)
				dims.add(dim);
		}
		return dims.toArray(new Dimension[dims.size()]);
		
//		ArrayList dims = new ArrayList(); 	//to filter out null dimensions!!
//		Iterator it = dimInfos.values().iterator();
//		while(it.hasNext()) {
//			DimensionInfo info = (DimensionInfo)it.next();
//			Dimension dimension = getDimension(info);
//			if(dimension != null)
//				dims.add(dimension);
//		}
//		return (Dimension[])dims.toArray(new Dimension[dims.size()]);
	}

	public final Dimension [] getDimensions(int typeMask) {
		String [] ids = dimLoader.getDimensionIds(typeMask);
		ArrayList<Dimension> dims= new ArrayList<Dimension>(); 	//to filter out null databases!! => TODO better thrown an exception here???
		for(String id : ids) {
			DimensionInfo info = dimLoader.load(id);
			Dimension dim = getDimension(info);
			if(dim != null)
				dims.add(dim);
		}
		return (Dimension[])dims.toArray(new Dimension[dims.size()]);
	}

	public final String getName() {
		return dbInfo.getName();
	}

	/**
	 * Finds a Palo Database which supports rules from all currently active
	 * connections. If no Palo connection is active or no active connection
	 * supports rules, null is returned.
	 *  
	 * @return an active Palo database which supports rules or null if no such
	 * database is currently active.
	 */
	private Database findPaloDatabase() {
		Connection [] cons = ((ConnectionFactoryImpl) ConnectionFactoryImpl.
				getInstance()).getActiveConnections();
		int length = cons.length;
		if (length == 0) {
			return null;
		}
		for (int i = 0; i < length; i++) {
			if (cons[i].getDatabaseCount() > 0) {				
				if (cons[i].getDatabaseAt(0).getCubeCount() > 0) {					
					return cons[i].getDatabaseAt(0);
				}
			}
		}
		return null;
	}
		
	public final String parseRule(Cube cube, String definition, String functions) {
        try {
        	CubeImpl _cube = (CubeImpl)cube;
        	String rule = "";
        	if (connection.getType() == Connection.TYPE_XMLA) {
        		Database paloDatabase = findPaloDatabase();
        		if (paloDatabase == null) {
        			return "";
        		}
        			// This is an XMLA connection, so "parseRule" will transform
           			// the XMLA rule to a rule-string readable by Palo. The
           			// returned string is _not_ xml, but the source is syntax
           			// conform to the palo input for rule.
           			definition = dbConnection.parseRule(
           					_cube.getInfo(), definition, functions);
           			
           			// Now let Palo do the parsing (findPaloDatabase only 
           			// returns a database, if it has at least one cube.
           			// TODO This is really not necessary. The RuleHandler
           			// gets the database by accessing the _cube_ info, so we
           			// only need to pass a valid cube here, because otherwise
           			// we cannot see a database. It would be better if we
           			// could omit the cube (i.e. pass null)).           			           			
           			rule = paloDatabase.parseRule(paloDatabase.getCubeAt(0),
           					definition, functions.toLowerCase());    
        	} else {
           		rule = dbConnection.parseRule(_cube.getInfo(), definition, functions);
        	}
        	return rule;
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);        	
        } catch(RuntimeException ex) {
        	throw new PaloAPIException(ex.getLocalizedMessage(),ex);
        }
	}
	
	public final Rights getRights() {
		return connection.getContext().getRights();
	}
	
	public final void reload() {
		//reset all loaders:
		resetAllLoaders();
		
		//NOTE: clearCaches() doesn't fire any events!!
		if(ConnectionImpl.WPALO)
			clearCache();
		else
			reloadInternal(true);
	}

	public final void removeCube(Cube cube) {
		if (!loadedCubes.containsKey(cube.getId()))
			return;

		// delete all views from cube:
		CubeView[] views = cube.getCubeViews();
		for (int i = 0; i < views.length; ++i)
			cube.removeCubeView(views[i]);

		if (cube.getExtendedType() == Cube.CUBEEXTENDEDTYPE_VIRTUAL) {
			loadedCubes.remove(cube.getId());
			virtualCubes.remove(cube);
		} else {
			CubeImpl _cube = (CubeImpl) cube;
			if (cubeLoader.delete(_cube.getInfo())) {
				loadedCubes.remove(cube.getId());
			}
	    	//update dbInfo:
	    	dbConnection.reload(dbInfo);
			fireCubesRemoved(new Cube[] { cube });
		}
	}

	public final void removeDimension(Dimension dimension) {
        if (!loadedDimensions.containsKey(dimension.getId()))
            return;
        
        DimensionImpl _dimension = (DimensionImpl)dimension;
//        if(dbConnection.delete(_dimension.getInfo())) {
//        	//delete all subsets from dimension:
//            Subset[] subsets = dimension.getSubsets();
//            for(int i=0;i<subsets.length;++i)
//            	dimension.removeSubset(subsets[i]);
//            
//        	//have to remove attribute dimensions and cubes too...
//        	Dimension attrDim = dimension.getAttributeDimension();
//        	loadedDimensions.remove(attrDim.getId());
//        	dimInfos.remove(attrDim.getId());
//        	
//        	Cube attrCube = dimension.getAttributeCube();
//        	loadedCubes.remove(attrCube.getId());
//        	cubeInfos.remove(attrCube.getId());
//        	
//        	//finally delete dimension itself...
//        	loadedDimensions.remove(dimension.getId());
//        	dimInfos.remove(dimension.getId());
//        	fireDimensionsRemoved(new Dimension[]{dimension,attrDim});
//        	
////			//NOT NEEDED HERE, IS IT? -> reloadInternal(true);
//        }
        
        if(dimLoader.delete(_dimension.getInfo())) {
        	//delete all subsets from dimension:
            Subset[] subsets = dimension.getSubsets();
            for(int i=0;i<subsets.length;++i)
            	dimension.removeSubset(subsets[i]);
            
        	//have to remove attribute dimensions and cubes too...
        	Dimension attrDim = dimension.getAttributeDimension();
        	if(attrDim != null) {
        		loadedDimensions.remove(attrDim.getId());
        		dimLoader.removed(attrDim.getId());
        	}
        	Cube attrCube = dimension.getAttributeCube();
        	if (attrCube != null) {
        		loadedCubes.remove(attrCube.getId());
        		cubeLoader.removed(attrCube.getId());
        	}
        	
        	//finally delete dimension itself...
        	loadedDimensions.remove(dimension.getId());
        	
	    	//update dbInfo:
	    	dbConnection.reload(dbInfo);
        	
        	fireDimensionsRemoved(new Dimension[]{dimension,attrDim});
        }

	}

	public final void rename(String newName) {		
		String oldName = getName();
		if(newName.equals(oldName))
			return;
		try {
			dbConnection.rename(dbInfo, newName);
			//fire rename event:
			fireDatabaseRenamed(new Database[] { this }, oldName);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(), pex);
		}
	}
    private final void fireDatabaseRenamed(Object[] dbs,String oldName) {
		ConnectionEvent ev = new ConnectionEvent(connection, null,
				ConnectionEvent.CONNECTION_EVENT_DATABASES_RENAMED,
				dbs);
		ev.oldValue = oldName;
		connection.fireEvent(ev);
    }

	public final boolean save() {
        return dbConnection.save(dbInfo);
	}

	public final synchronized void startBatchUpdate() {
		batchMode = true;
	}

	public final String getId() {
		return dbInfo.getId();
	}

	public final DatabaseInfo getInfo() {
		return dbInfo;
	}

	public final boolean isSystem() {
		return isSystem;
	}

//	public final boolean hasLegacySubsets() {
//		return subsetStorageHandler.hasSubsets(); 
//	}

	public final boolean equals(Object other) {
		if(other instanceof DatabaseImpl) {
			return key.equals(((DatabaseImpl)other).key);
		}
		return false;
	}
	
	public final int hashCode() {
		return key.hashCode();
	}

	public final SubsetStorageHandler getSubsetStorageHandler() {
		return subsetIOHandler;
	}

	public final boolean supportsNewSubsets() {
		return subsetIOHandler.supportsNewSubsets(this);
	}
	

	public final int getType() {
		return getType(dbInfo);
	}
	
	// --------------------------------------------------------------------------
	// PACKAGE INTERNAL
	//	
	final boolean isSubsetDimension(Dimension dimension) {
		return subsetIOHandler.isSubsetDimension(dimension);
	}
	
	final void isSystem(boolean b) {
		isSystem = b;
	}
	
    /**
     * Initiliazes this database. It also creates needed system dimensions
     * and cubes for managing subsets and cubeviews.
     */
    final void init(boolean doEvents) {
    	reloadInternal(doEvents);
    	
    	//we cannot add views and subsets to system database, so we skip it then
    	if(dbInfo.getType() == DatabaseInfo.TYPE_SYSTEM)
    		return;
    	
    	if (getConnection().getType() == Connection.TYPE_HTTP ||
    		getConnection().getType() == Connection.TYPE_LEGACY) {    		
       	    //subsets:
   		 	//SubsetManager.getInstance().init(this);
//    		subsetStorageHandler.initStorage();
   		 	//views:
//   		 	CubeViewManager.getInstance().init(this);
    		viewStorageHandler.initStorage();
    	}
    }
    
//    final boolean hasFailedViews() {
//    	return !failedViews.isEmpty();
//    }
//    
//    final boolean hasFailedView(String viewId) {
//    	return failedViews.containsKey(viewId);
//    }
//    
//    final Map getFailedViews() {
//    	return failedViews;
//    }

    final org.palo.api.impl.SubsetStorageHandler getLegacySubsetHandler() {
    	return legacySubsetHandler;
    }
    
    final CubeViewStorageHandler getViewStorageHandler() {
    	return viewStorageHandler;
    }

    public final void clearCache() {
    	//TODO virtual cubes?
    	
    	for(DimensionImpl dimension : loadedDimensions.values()) {
    		dimension.clearCache();
    	}
    	loadedDimensions.clear();
    	dimLoader.reset();
    	
//    	for(HierarchyImpl hierarchy : loadedHierarchies.values()) {
//    		hierarchy.clearCache();
//    	}
//    	loadedHierarchies.clear();
//    	hierLoader.reset();
//    	
    	for(CubeImpl cube : loadedCubes.values()) {
    		cube.clearCache();
    	}
    	loadedCubes.clear();
    	cubeLoader.reset();
    	
    	for(Property2Impl property : loadedProperties.values()) {
    		property.clearCache();
    	}
    	loadedProperties.clear();
    	propertyLoader.reset();
    	
    	//subsets:
    	subsetIOHandler.reset();
    }
    
	// -------------------------------------------------------------------------
	// PRIVATE METHODS
	//
//    private final void loadInfos() {
//    	loadCubeInfos();
//    	loadDimensionInfos();
//    	loadHierarchyInfos();
//    }
    
    public final void init() {
//    	loadCubeInfos();
//    	loadDimensionInfos();
//    	loadHierarchyInfos();
//    	subsetStorageHandler.initStorage();
    	if(isSystem())
    		return; //neither views nor subsets...
//    	SubsetManager.getInstance().init(this);
    	if (getConnection().getType() != Connection.TYPE_XMLA) {
    		CubeViewManager.getInstance().init(this);
    	}   		
   		viewStorageHandler.initStorage();

    }
    
//	private final void loadDimensionInfos() {
//		DimensionInfo[] infos = dbConnection.getDimensions(dbInfo);
//		dimInfos.clear();
//		for(int i=0;i<infos.length;++i)
//			dimInfos.put(infos[i].getId(), infos[i]);
//	}
	
//	private final void loadHierarchyInfos() {
//		HierarchyInfo [] infos = dbConnection.getHierarchies(dbInfo);
//		hierInfos.clear();
//		for (int i = 0; i < infos.length; ++i)
//			hierInfos.put(infos[i].getId(), infos[i]);		
//	}
	
//	private final void loadCubeInfos() {
//		CubeInfo[] infos = dbConnection.getCubes(dbInfo);
//		cubeInfos.clear();
//		for(int i=0;i<infos.length;++i)
//			cubeInfos.put(infos[i].getId(), infos[i]);
//	}
    
    private final void reloadInternal(boolean doEvents) {
		synchronized (this) {
			if (batchMode)
				return;
		}
		
		legacySubsetHandler.reload();
		subsetIOHandler.reset();
		viewStorageHandler.reload();
		reloadDatabase(doEvents);
		
//		reloadInternal(doEvents, false);

//		//------------------- PERSISTENCE -----------------------
//		// load subsets and views:
//		ApiExtensionController extController = 
//				ApiExtensionController.getInstance();
//		if(dbInfo.getType() != DatabaseInfo.TYPE_SYSTEM) {
//			failedViews.clear();
//			// subsets:
//			try {
//				extController.clearSubsets(this);
//				extController.loadSubsets(this);
//			} catch(PaloPersistenceException pex) {
//				//we currently ignore...
//				//TODO to be handled later.
//			}
//			// views:
//			try {
//				extController.clearViews(this);
//				extController.loadViews(this);
//			} catch(PaloPersistenceException pex) {
//				//run through all errors and distribute them to their affected cubes:
//				PersistenceError[] errors = pex.getErrors();
//				for(int i=0;i<errors.length;++i) {
//					Object view = errors[i].getSource();
//					if (view instanceof CubeView) {
//						Cube cube = ((CubeView)view).getCube();
//						String viewId = errors[i].getSourceId();
//						if (cube instanceof CubeImpl) {
//							((CubeImpl) cube).addViewError(viewId, errors[i]);
//						}
//					} else {
//						//we add it to failedViews since we could not assign it
//						//to a special view...
//						failedViews.put(errors[i].getSourceId(),errors[i]);
//					}
//				}
//			}
//		}
	}

    /**
     * Performs a complete reload, i.e. all dimensions and cubes are complete
     * reload with fresh data from palo server
     * @param doEvents notify listeners about any changes
     */
    private final void reloadDatabase(boolean doEvents) {
    	//preserve old dimensions and cubes:
    	LinkedHashMap<String, Dimension> oldDimensions = 
    			new LinkedHashMap<String, Dimension>(loadedDimensions);
		LinkedHashMap<String, Cube> oldCubes = 
				new LinkedHashMap<String, Cube>(loadedCubes);
    	
    	//reload from server:
    	dbConnection.reload(dbInfo);
    	
		//have to create dimensions and cubes first and do a reload afterward,
		//because an element can refer to its attributes (dimension & cube) 
		//which may not be loaded before...		
		//1 Step: create new dimensions:
		loadedDimensions.clear();
		dimLoader.reset();
//		loadDimensionInfos(); //TODO can we save this call, since it will run through al dims twice!!!!
//		DimensionInfo[] _dimInfos = dbConnection.getDimensions(dbInfo);
//		Iterator it = dimInfos.values().iterator();
//		while(it.hasNext()) {
//			DimensionInfo dimInfo = (DimensionInfo)it.next();
//			DimensionImpl d = (DimensionImpl) oldDimensions.get(dimInfo.getId());
//			if (d == null) 
//				d = createDimension(dimInfo,doEvents); //DimensionImpl.create(connection, this, dimInfos[i],doEvents);
//			else
//				loadedDimensions.put(d.getId(), d);
//			d.reload(doEvents);
//		}
    	String[] dimIDs = dimLoader.getAllDimensionIds();
    	for (String id : dimIDs) {
			DimensionImpl dim = (DimensionImpl) oldDimensions.get(id);
			if (dim == null) {
				DimensionInfo info = dimLoader.load(id);
				dim = createDimension(info, doEvents);
			} else
				loadedDimensions.put(id, dim);
			// do a reload
			dim.reload(doEvents);
		}

		
		//2 Step: create new cubes:		
		loadedCubes.clear();
		cubeLoader.reset();
//		it = cubeInfos.values().iterator();
//		while(it.hasNext()) {
//			CubeInfo cubeInfo = (CubeInfo)it.next();
//			CubeImpl c = (CubeImpl)oldCubes.get(cubeInfo);
//			if(c == null) 
//				c = createCube(cubeInfo,doEvents); //CubeImpl.create(connection, this, cubeInfos[i]);
//			else
//				loadedCubes.put(c.getId(),c);
//			c.reload(doEvents);
//		}
		String[] cubeIDs = cubeLoader.getAllCubeIds();
		for(String id : cubeIDs) {
			CubeImpl cube = (CubeImpl) oldCubes.get(id);
			if (cube == null) {
				CubeInfo info = cubeLoader.load(id);
				cube = createCube(info, doEvents);
			} else
				loadedCubes.put(id, cube);
			// do a reload
			cube.reload(doEvents);
		}
//		//3 Step: reload dimensions:
//		for (int i = 0; i < _dimInfos.length; ++i) {
//			DimensionInfo dimension = _dimInfos[i];
//			DimensionImpl dim = 
//				(DimensionImpl) dimensions.get(dimension.getId());
//			if(dim != null) 
//				dim.reload(doEvents);
//		}
//		//4 Step: reload cubes:
//		for (int i = 0; i < cubeInfos.length; ++i) {
//			CubeInfo cubeInf = cubeInfos[i];
//			CubeImpl cube = (CubeImpl)cubes.get(cubeInf.getId());
//			if(cube != null) 
//				cube.reload(doEvents);
//		}
		
		//5 Step: doEvents:
		if(doEvents) {
			//dimensions removed
			LinkedHashSet removedDimensions = new LinkedHashSet(
					oldDimensions.values());
			removedDimensions.removeAll(loadedDimensions.values());
			if (removedDimensions.size() > 0) {
				fireDimensionsRemoved(removedDimensions.toArray());
			}
			//dimensions added
			LinkedHashSet addedDimensions = 
				new LinkedHashSet(loadedDimensions.values());
			addedDimensions.removeAll(oldDimensions.values());
			if (addedDimensions.size() > 0) {
				fireDimensionsAdded(addedDimensions.toArray());
			}
			//cubes removed
			LinkedHashSet removedCubes = new LinkedHashSet(oldCubes.values());
			removedCubes.removeAll(loadedCubes.values());
			if (removedCubes.size() > 0) {
				fireCubesRemoved(removedCubes.toArray());
			}
			//cubes added
			LinkedHashSet addedCubes = new LinkedHashSet(loadedCubes.values());
			addedCubes.removeAll(oldCubes.values());
			if (addedCubes.size() > 0) {
				fireCubesAdded(addedCubes.toArray());
			}
		}
	}
    
    private final void fireDimensionsAdded(Object[] dimensions) {
		connection.fireEvent(new ConnectionEvent(
				getConnection(), this,
				ConnectionEvent.CONNECTION_EVENT_DIMENSIONS_ADDED,
				dimensions));
    }
    private final void fireDimensionsRemoved(Object[] dimensions) {
		connection.fireEvent(new ConnectionEvent(
				getConnection(), this,
				ConnectionEvent.CONNECTION_EVENT_DIMENSIONS_REMOVED,
				dimensions));
    }

    private final void fireCubesAdded(Object[] cubes) {
		connection.fireEvent(new ConnectionEvent(
				getConnection(), this,
				ConnectionEvent.CONNECTION_EVENT_CUBES_ADDED,
				cubes));
    }
    private final void fireCubesRemoved(Object[] cubes) {
		connection.fireEvent(new ConnectionEvent(
				getConnection(), this,
				ConnectionEvent.CONNECTION_EVENT_CUBES_REMOVED,
				cubes));
    }
    
	private final CubeImpl getCube(CubeInfo cubeInfo) {
		if(cubeInfo == null)
			return null;
		CubeImpl cube = (CubeImpl)loadedCubes.get(cubeInfo.getId());
		if(cube == null) {
			//not loaded yet...
			cube = createCube(cubeInfo,true);
		}
		return cube;
	}
	
	private final CubeImpl createCube(CubeInfo cubeInfo, boolean fireEvent) {
		CubeImpl cube = CubeImpl.create(connection, this, cubeInfo);
		loadedCubes.put(cube.getId(), cube);
		return cube;
//		CubeImpl cube = CubeImpl.create(connection, this, cubeInfo);
//		loadedCubes.put(cube.getId(),cube);
//		cubeInfos.put(cubeInfo.getId(),cubeInfo);
//		return cube;
		
	}
	
	/**
	 * Checks if the corresponding dimension instance to the given dimensioninfo
	 * was already loaded and returns it. If no dimension instance was created 
	 * so far, this method will do it...
	 * @param dimInfo
	 * @return
	 */
	private final DimensionImpl getDimension(DimensionInfo dimInfo) {
		if (dimInfo == null) {
			return null;
		}
		DimensionImpl dimension = (DimensionImpl)loadedDimensions.get(dimInfo.getId());
		if(dimension== null) {
			//not loaded yet...
			dimension = createDimension(dimInfo,true);
		}
		return dimension;
	}
	
	/**
	 * Creates a new dimension instance from the given dimensioninfo and adds 
	 * it to the list of all loaded dimensions
	 * @param dimInfo
	 * @return
	 */
	private final DimensionImpl createDimension(DimensionInfo dimInfo, boolean fireEvent) {
		DimensionImpl dimension = DimensionImpl.create(connection, this, dimInfo, fireEvent);
		loadedDimensions.put(dimension.getId(),dimension);
		return dimension;

//		DimensionImpl dimension = DimensionImpl.create(connection, this, dimInfo, fireEvent);
//		loadedDimensions.put(dimension.getId(),dimension);
//		dimInfos.put(dimInfo.getId(), dimInfo);
//		return dimension;
	}


//	public final Hierarchy getHierarchyAt(int index) {
//		HierarchyInfo hierarchy = hierLoader.load(index);
//		return getHierarchy(hierarchy);
//
////		int infosCount = hierInfos.size();
////		if (index < infosCount && index >= 0) {
////			try {
////				Iterator it = hierInfos.values().iterator();
////				for (int i = 0; i < index; ++i) {
////					if (it.hasNext())
////						it.next();
////				}
////				return it.hasNext() ? getHierarchy((HierarchyInfo) it.next())
////						: null;
////			} catch (PaloException pex) {
////				throw new PaloAPIException(pex);				
////			} catch (RuntimeException e) {
////				throw new PaloAPIException(e.getLocalizedMessage(), e);
////			}
////		}
////		return null;
//	}
//	
//	public final Hierarchy getHierarchyById(String id) {
//		HierarchyInfo hierInfo = hierLoader.load(id);
//		return getHierarchy(hierInfo);
////		HierarchyInfo hierInfo = (HierarchyInfo) hierInfos.get(id);
////		return getHierarchy(hierInfo);
//	}
//
//	public final int getHierarchyCount() {
//		return hierLoader.getHierarchyCount();
////		return hierInfos.size();
//	}
//
//	public final Hierarchy [] getHierarchies() {
//		String[] ids = hierLoader.getAllHierarchyIds();
//		ArrayList<Hierarchy> hierarchies= new ArrayList<Hierarchy>(); 	//to filter out null hierarchies!! => TODO better thrown an exception here???
//		for(String id : ids) {
//			HierarchyInfo info = hierLoader.load(id);
//			Hierarchy hierarchy = getHierarchy(info);
//			if(hierarchy != null)
//				hierarchies.add(hierarchy);
//		}
//		return (Hierarchy[])hierarchies.toArray(new Hierarchy[hierarchies.size()]);
//
////		ArrayList hiers = new ArrayList(); 	//to filter out null hierarchies!!
////		Iterator it = hierInfos.values().iterator();
////		while(it.hasNext()) {
////			HierarchyInfo info = (HierarchyInfo) it.next();
////			Hierarchy hierarchy = getHierarchy(info);
////			if(hierarchy != null)
////				hiers.add(hierarchy);
////		}
////		return (Hierarchy []) hiers.toArray(new Hierarchy[hiers.size()]);
//	}
//	
//	/**
//	 * Checks if the corresponding dimension instance to the given dimensioninfo
//	 * was already loaded and returns it. If no dimension instance was created 
//	 * so far, this method will do it...
//	 * @param dimInfo
//	 * @return
//	 */
//	private final HierarchyImpl getHierarchy(HierarchyInfo hierInfo) {
//		if (hierInfo == null) {
//			return null;
//		}
//		HierarchyImpl hier = (HierarchyImpl) loadedHierarchies.get(hierInfo.getId());
//		if(hier== null) {
//			//not loaded yet...
//			hier = createHierarchy(hierInfo,true);
//		}
//		return hier;
//	}
//	
//	/**
//	 * Creates a new dimension instance from the given dimensioninfo and adds 
//	 * it to the list of all loaded dimensions
//	 * @param dimInfo
//	 * @return
//	 */
//	private final HierarchyImpl createHierarchy(HierarchyInfo hierInfo, boolean fireEvent) {
//		HierarchyImpl hier = HierarchyImpl.create(connection, this, hierInfo, fireEvent);
//		loadedHierarchies.put(hier.getId(), hier);
////		hierInfos.put(hierInfo.getId(), hierInfo);
//		return hier;
//	}
	
	public String[] getAllPropertyIds() {
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
		for (PropertyInfo kidd: kid.getChildren()) {
			createProperty(p2Kid, kidd);
		}
	}
	
	private Property2 createProperty(PropertyInfo propInfo) {
		Property2 prop = Property2Impl.create(null, propInfo);
		for (PropertyInfo kid: propInfo.getChildren()) {
			createProperty(prop, kid);
		}
		return prop;
	}	
	
	private final Cube[] loadCubes(String[] cubeIds) {
		ArrayList<Cube> cubes = new ArrayList<Cube>(); 	//to filter out null databases!! => TODO better thrown an exception here???
		for(String id : cubeIds) {
			CubeInfo info = cubeLoader.load(id);
			Cube cube = getCube(info);
			if(cube != null)
				cubes.add(cube);
		}
		return (Cube[])cubes.toArray(new Cube[cubes.size()]);

	}


	public boolean canBeModified() {
		return dbInfo.canBeModified();
	}

	public boolean canCreateChildren() {
		return dbInfo.canCreateChildren();
	}
	
	private final Cube addCubeInternal(String name, Dimension[] dimensions, int type) {
		try {
			if (dimensions == null || name == null)
				return null;

			int infoType = getInfoType(type);
			if(infoType == -1)
				infoType = com.tensegrity.palojava.PaloConstants.TYPE_NORMAL;
			
			DimensionInfo[] dimInfos = new DimensionInfo[dimensions.length];
			for (int i = 0; i < dimInfos.length; ++i) {
				dimInfos[i] = ((DimensionImpl) dimensions[i]).getInfo();
			}

			CubeInfo cubeInfo = cubeLoader.create(name, dimInfos, infoType);
			// TODO should we really fire an event here...
			Cube cube = createCube(cubeInfo, true);
	    	//update dbInfo:
	    	dbConnection.reload(dbInfo);

			fireCubesAdded(new Cube[] { cube });
			// reloadInternal(true);
			return cube;
			// CubeInfo cubeInfo = dbConnection.addCube(dbInfo, name, dimInfos);
			// // TODO should we really fire an event here...
			// Cube cube = createCube(cubeInfo,true);
			//	
			// //TODO check do we really need this here? DON'T THINK SO...
			// reloadDatabase(false);
			// fireCubesAdded(new Cube[] { cube });
			// // reloadInternal(true);
			// return cube; // getCubeById(cubeInfo.getId());
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(), pex);
		}
	}

	private final Dimension addDimensionInternal(String name, int type) {
		try {
			int infoType = getInfoType(type);
			if(infoType == -1)
				infoType = com.tensegrity.palojava.PaloConstants.TYPE_NORMAL;

			DimensionInfo dimInfo = dimLoader.create(name, infoType);
			Dimension dimension = createDimension(dimInfo,true);

			//load attribute dimension and cube too...
			DimensionInfo attrDimInfo = 
					dbConnection.getAttributeDimension(dimInfo);
			if(attrDimInfo != null) {
				dimLoader.loaded(attrDimInfo);
				createDimension(attrDimInfo,false);
			}
			CubeInfo attrCubeInfo = dbConnection.getAttributeCube(dimInfo);
			if(attrCubeInfo != null) {
				createCube(attrCubeInfo,false);
				cubeLoader.loaded(attrCubeInfo);
			}
			
			dbConnection.reload(dbInfo);
			fireDimensionsAdded(new Dimension[] { dimension });
			return dimension;
		} catch (PaloException pex) {
			throw new PaloAPIException(pex.getMessage(), pex);
		}
	}

	private final void resetAllLoaders() {
		dimLoader.reset();
	    cubeLoader.reset();
		propertyLoader.reset();
	}
}
