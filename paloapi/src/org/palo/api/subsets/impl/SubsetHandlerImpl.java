/*
*
* @file SubsetHandlerImpl.java
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
* @version $Id: SubsetHandlerImpl.java,v 1.16 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.impl;

import java.util.HashMap;
import java.util.LinkedHashSet;

import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.InsufficientRightsException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.persistence.SubsetLoadObserver;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetHandler;

/**
 * <code>SubsetHandlerImpl</code>
 * <p><b>- API INTERNAL CLASS -</b></p>
 *
 * @author ArndHouben
 * @version $Id: SubsetHandlerImpl.java,v 1.16 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class SubsetHandlerImpl implements SubsetHandler {

	private final Hierarchy hierarchy;
	private final boolean newSubsetsSupported;
	private final SubsetStorageHandlerImpl storageHandler;
	//-- cache --
	private HashMap<String, Subset2> localSubsets;
	private HashMap<String, Subset2> globalSubsets;
	

	/**
	 * Creates a new <code>SubsetHandlerImpl</code> instance for the given
	 * dimension.
	 * @param dimension the dimension to create the subset handler for
	 * @deprecated use hierarchy constructor instead. 
	 */
	public SubsetHandlerImpl(Dimension dimension) {
		this(dimension.getDefaultHierarchy());
//		this.localSubsets = new HashMap<String, Subset2>();
//		this.globalSubsets = new HashMap<String, Subset2>();
//		Database database = dimension.getDatabase();
//		this.storageHandler = 
//			(SubsetStorageHandlerImpl) database.getSubsetStorageHandler();
//		if(storageHandler == null)
//			throw new NullPointerException("storage handler is null!!");
//		newSubsetsSupported = !dimension.isSystemDimension()
//				&& !dimension.isAttributeDimension()
//				&& !dimension.isSubsetDimension()
//				&& database.supportsNewSubsets();
	}
	
	private SubsetHandlerImpl(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
		this.localSubsets = new HashMap<String, Subset2>();
		this.globalSubsets = new HashMap<String, Subset2>();
		Database database = hierarchy.getDimension().getDatabase();
		this.storageHandler = 
			(SubsetStorageHandlerImpl) database.getSubsetStorageHandler();
		if(storageHandler == null)
			throw new NullPointerException("storage handler is null!!");
		newSubsetsSupported = !hierarchy.getDimension().isSystemDimension()
				&& !hierarchy.isAttributeHierarchy()
				&& !hierarchy.isSubsetHierarchy()
				&& database.supportsNewSubsets();
	}
	
	
	public final boolean canRead(int type) {
		return storageHandler.canRead(type);
	}

	public final boolean canWrite(int type) {
		return storageHandler.canWrite(type);
	}

	public final Dimension getDimension() {
		return hierarchy.getDimension();
	}

	public final Hierarchy getHierarchy() {
		return hierarchy;
	}

	public final void reset() {
		localSubsets.clear();
		globalSubsets.clear();
		storageHandler.reset();
	}
	
	
	public final Subset2 addSubset(String name, int type) {
		if (!newSubsetsSupported)
			throw new PaloAPIException(
					"New subsets are not supported by database '"
							+ hierarchy.getDimension().getDatabase().getName() + "'!");
		if (!canWrite(type)) {
			throw new InsufficientRightsException("Cannot add subset '" + name
					+ "' to " + getTypeStr(type)
					+ " subsets!\nNot enough rights!");
		}
		try {
			String id = storageHandler.newSubsetCell(name, hierarchy, type);
			if(id == null)
				throw new PaloAPIException("Adding a subset to a system dimension is not allowed!");
			Subset2 subset = new Subset2Impl(id, name, hierarchy, type);
			storageHandler.save(subset);
			register(subset);
			return subset;
		} catch (PaloIOException e) {
			throw new PaloAPIException("The subset '" + name
					+ "' exists already!", e);
		}
	}

	public final Subset2 getSubset(String id, int type) {
		if (!newSubsetsSupported || id == null)
			return null;
		HashMap<String, Subset2> cache = getCache(type);
		Subset2 subset = cache.get(id);
		if (subset == null) {
			// not loaded yet, load it...
			try {
				subset = storageHandler.load(id, hierarchy, type, this);
				register(subset);
			} catch (PaloIOException e) {
				throw new PaloAPIException("Loading subset '"
						+ getSubsetName(id) + "' of hierarchy '"
						+ hierarchy.getName() + "' failed!!", e);
			}
		}
		return subset;
	}

	public final void getSubsets(SubsetLoadObserver observer) {
		if(observer == null || !newSubsetsSupported)
			return;
		String[] subIds = storageHandler.getSubsetIDs(hierarchy);
		for(String id : subIds) {
			loadSubset(id, Subset2.TYPE_GLOBAL, hierarchy, observer);
			loadSubset(id, Subset2.TYPE_LOCAL, hierarchy, observer);
		}
	}
	
	public final Subset2[] getSubsets() {
		if (!newSubsetsSupported)
			return new Subset2[0];
		String[] subIds = storageHandler.getSubsetIDs(hierarchy);
		LinkedHashSet<Subset2> subsets = new LinkedHashSet<Subset2>();
		for (String id : subIds) {
			Subset2 subset = loadSubset(id, Subset2.TYPE_GLOBAL);
			if (subset != null)
				subsets.add(subset);

			subset = loadSubset(id, Subset2.TYPE_LOCAL);
			if (subset != null)
				subsets.add(subset);
		}
		return subsets.toArray(new Subset2[subsets.size()]);
	}
	
	public final void getSubsets(int type, SubsetLoadObserver observer) {
		if(observer == null || !newSubsetsSupported)
			return;
		String[] subIds = storageHandler.getSubsetIDs(hierarchy);
		for(String id : subIds) {
			loadSubset(id, type, hierarchy, observer);
		}
	}

	public final Subset2[] getSubsets(int type) {
		if(!newSubsetsSupported)
			return new Subset2[0];
		String[] subIds = storageHandler.getSubsetIDs(hierarchy,type);
		LinkedHashSet<Subset2> subsets = new LinkedHashSet<Subset2>();
		for(String id : subIds) {
			Subset2 subset = loadSubset(id, type);
			if(subset != null)
				subsets.add(subset);
		}
		return subsets.toArray(new Subset2[subsets.size()]);
	}

	public final String getSubsetId(String name, int type) {
		if(!newSubsetsSupported)
			return null;
		return storageHandler.getSubsetId(hierarchy, name, type);
	}

	public final String[] getSubsetIDs() {
		if(!newSubsetsSupported)
			return new String[0];

		if(hierarchy.getDimension().isSystemDimension())
			return new String[0];
		return storageHandler.getSubsetIDs(hierarchy);
	}

	public String[] getSubsetIDs(int type) {
		if(!newSubsetsSupported)
			return new String[0];

		if(hierarchy.getDimension().isSystemDimension())
			return new String[0];
		return storageHandler.getSubsetIDs(hierarchy, type);
	}

	public String[] getSubsetNames() {
		if(!newSubsetsSupported)
			return new String[0];

		if(hierarchy.getDimension().isSystemDimension())
			return new String[0];
		return storageHandler.getSubsetNames(hierarchy);
	}

	public String[] getSubsetNames(int type) {
		if (!newSubsetsSupported
				|| hierarchy.getDimension().isSystemDimension())
			return new String[0];
		return storageHandler.getSubsetNames(hierarchy, type);
	}

	public final String getSubsetName(String id) {		
		if(!newSubsetsSupported)
			return null;

		return storageHandler.getSubsetName(id);
	}

	public final boolean hasSubsets(int type) {
		if(!newSubsetsSupported)
			return false;

		if(hierarchy.getDimension().isSystemDimension())
			return false;
		return storageHandler.hasSubsets(hierarchy, type);
	}
	
	public void remove(Subset2 subset) {
		if(isRegistered(subset)) {
			storageHandler.remove(subset);
			unregister(subset);
		}
	}
	
	public void remove(String id, int type) {
		// since this method is used to remove failed subsets corresponding
		// subset cannot be registered => so don't check it ;)
		HashMap<String, Subset2> cache = getCache(type);
		storageHandler.remove(id, type, hierarchy);
		cache.remove(id);
	}
	
	public final void save(Subset2 subset) {
		if (isRegistered(subset)) {
			try {
				storageHandler.save(subset);
			} catch (PaloIOException e) {
				throw new PaloAPIException("Saving subset '" + subset.getName()
						+ "' of hierarchy '" + hierarchy.getName()
						+ "' failed!", e);
			}
		}
	}

    /**
     * <p><b>- API INTERNAL -</b></p> 
     */
	public final Subset2 create(String id, String name, Hierarchy hierarchy, int type) {
		if(!newSubsetsSupported)
			throw new PaloAPIException(
					"New subsets are not supported by database '"
							+ hierarchy.getDimension().getDatabase().getName() + "'!");

		if(hierarchy.equals(this.hierarchy)) {
			Subset2 subset = new Subset2Impl(id, name, hierarchy, type);
			register(subset);
			return subset;
		}
		return null;
	}
	
	//-------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final void register(Subset2 subset) {
		if(subset == null)
			return;
		HashMap<String, Subset2> cache = getCache(subset.getType());
		cache.put(subset.getId(), subset);
	}
	
	private final void unregister(Subset2 subset) {
		if(subset == null)
			return;
		HashMap<String, Subset2> cache = getCache(subset.getType());
		cache.remove(subset.getId());
	}
	
	private final boolean isRegistered(Subset2 subset) {
		HashMap<String, Subset2> cache = getCache(subset.getType());
		return cache.containsKey(subset.getId());
	}
	
	private final String getTypeStr(int type) {
		return type == Subset2.TYPE_GLOBAL ? "global" : "local";
	}
	
	private final HashMap<String, Subset2> getCache(int type) {
		return type == Subset2.TYPE_GLOBAL ? globalSubsets : localSubsets;
	}
	

	private final Subset2 loadSubset(String id, int type) {
		try {
			return getSubset(id, type);
		} catch (PaloAPIException pex) {
			/* ignore */
			// System.err.println("Hierarchy '"+hierarchy.getName()+"': skip
			// failed subset(" + id
			// + ")!");
		}
		return null;
	}
	
	private final void loadSubset(String id, int type, Hierarchy hierarchy,
			SubsetLoadObserver observer) {
		try {
			Subset2 subset = getSubset(id, type);
			if (subset != null) {
				observer.loadComplete(subset);
			} 
//subset == null means no subset definition for this type!!			
//			else
//				observer.loadFailed(id, getSubsetName(id), type, hierarchy);
		} catch (PaloAPIException pex) {
			observer.loadFailed(id, getSubsetName(id), type, hierarchy);
		}
	}
}
