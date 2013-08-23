/*
*
* @file CubeViewStorageHandler.java
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
* @version $Id: CubeViewStorageHandler.java,v 1.12 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

package org.palo.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.PaloAPIException;
import org.palo.api.Property;
import org.palo.api.exceptions.PaloObjectNotFoundException;
import org.palo.api.persistence.PaloPersistenceException;
import org.palo.api.persistence.PersistenceError;



/**
 * A <code>CubeViewStorageHandler</code> encapsulates the lazy loading of cube views. 
 * <b>NOTE:</b> This class is only temporarely and will be removed when palo 
 * server supports cube views natively!!
 *  
 * @author ArndHouben
 * @version $Id: CubeViewStorageHandler.java,v 1.12 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
class CubeViewStorageHandler {

	private final Database database;
	private final Map<String, CubeView> loadedViews;
	private final Map<String, List<PersistenceError>> failedViews;	
	private final ApiExtensionController viewController = 
			ApiExtensionController.getInstance();
	
	/** key: cube id   value: a set of all its defined view ids*/ 
	private final HashMap cubeId2viewId;
	
	private final HashMap<String, CubeView> allViews;
	
	CubeViewStorageHandler(Database database) {
		this.database = database;
		this.cubeId2viewId = new HashMap();
		this.loadedViews = new HashMap<String, CubeView>();
		this.failedViews = new HashMap<String, List<PersistenceError>>();
		this.allViews = new HashMap<String, CubeView>();
//		reload();
	}

	final void initStorage() {
		if(database.isSystem())
			return;
		try {
//			viewController.init(database);
			reload();
		}catch(PaloAPIException e) {
			e.printStackTrace();
			System.err.println("Cannot add view dimension to database '"+database.getName()+"'!!");
		}
	}
	/**
	 * @param cube
	 * @return
	 */
	final int getViewCount(Cube cube) {
		Set knownViewIds = getKnownViewIds(cube);
		return knownViewIds.size();		
	}

	
	final String getViewName(String id) {		
		CubeView view = allViews.get(id);
		if(view != null)
			return view.getName();
		return null;
	}
	
	/**
	 * Returns all known view ids for the specified cube. If the cube has no
	 * views the returned array is empty.
	 * 
	 */
	final String[] getViewIds(Cube cube) {
		Set knownViewIds = getKnownViewIds(cube);
		return (String[])knownViewIds.toArray(new String[knownViewIds.size()]);
	}
	
	
	final CubeView addCubeView(Cube cube, String id, String name, Property[] properties) {
		Set knownViewIds = getKnownViewIds(cube);		
		if(knownViewIds.contains(id))
			throw new PaloAPIException("CubeView already exists!");
		
		CubeView view = viewController.createCubeView(id, name, cube, properties);
		loadedViews.put(view.getId(),view);
		allViews.put(view.getId(),view);
		knownViewIds.add(id);
		return view;
	}
	
	final CubeView addCubeView(Cube cube, String name, Property[] properties) {
		Set knownViewIds = getKnownViewIds(cube);
		String id = Long.toString(System.currentTimeMillis());
		while(knownViewIds.contains(id)) {
			long lg = Long.parseLong(id);
			lg++;
			id = Long.toString(lg);
		}
		return addCubeView(cube, id, name, properties);
	}
	
	final void removeCubeView(Cube cube, CubeView view) {
		try {
			if (view != null && view.getCube().equals(cube)) {
				Set knownViewIds = getKnownViewIds(cube);
				loadedViews.remove(view.getId());
				allViews.remove(view.getId());
				knownViewIds.remove(view.getId());
				ApiExtensionController.getInstance().delete(view);
				// reloadViewIDs();
			}
		} catch (PaloAPIException ex) {
			String errCode = ex.getErrorCode();
			if (errCode != null) {
				if (errCode.equals("2001"))
					throw new PaloObjectNotFoundException("Database not found",
							ex);
				else if (errCode.equals("3002"))
					throw new PaloObjectNotFoundException(
							"Dimension not found", ex);
				else if (errCode.equals("4004"))
					throw new PaloObjectNotFoundException("Element not found",
							ex);

			} else
				throw new PaloAPIException("Couldn't remove cube view '"
						+ view.getName() + "'", ex);
		}
	}
	
	final CubeView getCubeView(Cube cube, String id) throws PaloPersistenceException {
		if (!failed(id)) {
			Set knownViewIds = getKnownViewIds(cube);
			if (knownViewIds.contains(id)) {
				CubeView view = getCubeView(id);
				return view;
			}
		}
		return null;
	}

	final void removeLoadedViews(Cube cube) {
		Set knownViewIds = getKnownViewIds(cube);
		Iterator allViews = knownViewIds.iterator();
		while(allViews.hasNext()) {
			String viewId = (String)allViews.next();
			loadedViews.remove(viewId);
		}
	}
	
	final void reload() {
		loadedViews.clear();
		cubeId2viewId.clear();
		failedViews.clear();
		allViews.clear();
		try {
			ApiExtensionController.getInstance().loadViews(
					database, cubeId2viewId,loadedViews);
		} catch (PaloPersistenceException pex) {
			//store any errors which occur...
//			//run through all errors and distribute them to their affected cubes:
			PersistenceError[] errors = pex.getErrors();
			for(int i=0;i<errors.length;++i) {
				Object view = errors[i].getSource();
				if (view instanceof CubeView) {					
					String viewId = errors[i].getSourceId();
					Cube srcCube = ((CubeView)view).getCube();
					addFailed(viewId,errors[i]);
					allViews.put(viewId,(CubeView)view);
					ensureIsContained(srcCube.getId(),viewId);
				} else {
					//we add it to failedViews since we could not assign it
					//to a special view...
					addFailed(errors[i].getSourceId(),errors[i]);
					allViews.put(errors[i].getSourceId(),null);
				}
			}
		}
		allViews.putAll(loadedViews);
	}
	
	
	private final CubeView getCubeView(String id) throws PaloPersistenceException {
		if(failed(id))
			return null;
		
		CubeView view = (CubeView) loadedViews.get(id);
		if(view == null) {
			//load it
			view = viewController.loadView(database, id);
			loadedViews.put(view.getId(),view);
		}
		return view;	
	}
	
	
	/**
	 * Returns a set of all known views for specified cube. 
	 * @param cube
	 * @return
	 */
	private final Set getKnownViewIds(Cube cube) {
		return getViewIdsSet(cube.getId());
	}
	
	private final void ensureIsContained(String cubeId, String viewId) {
		Set viewIds = getViewIdsSet(cubeId);
		viewIds.add(viewId);
	}
	
	private final Set getViewIdsSet(String cubeId) {
		Set viewIds = (Set)cubeId2viewId.get(cubeId);
		if(viewIds == null) {
			viewIds = new LinkedHashSet();
			cubeId2viewId.put(cubeId, viewIds);
		}
		return viewIds;
	}
	
	private final void addFailed(String viewId,PersistenceError error) {
		List errors = (List)failedViews.get(viewId);
		if(errors == null) {
			errors = new ArrayList();
			failedViews.put(viewId,errors);
		}
		errors.add(error);		
	}
	
	private final boolean failed(String viewId) throws PaloPersistenceException {
		if (failedViews.containsKey(viewId)) {
			List errors = (List) failedViews.get(viewId);
			failedViews.remove(viewId);
			throw new PaloPersistenceException((PersistenceError[]) errors
					.toArray(new PersistenceError[errors.size()]),
					"Exception during cube loading");
		}
		return false;
	}
}
