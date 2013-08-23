/*
*
* @file PaloInfoLoader.java
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
* @version $Id: PaloInfoLoader.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.loader;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PaloInfo;

/**
 * The <code>PaloInfoLoader</code> is the base class for managing the loading 
 * of palo info objects. It does this in a lazy way, i.e. info objects are only
 * loaded if and when they are needed. 
 * 
 *
 * @author ArndHouben
 * @version $Id: PaloInfoLoader.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public abstract class PaloInfoLoader {
	
	/** the connection to the palo server */
	protected final DbConnection paloConnection;
	/** contains currently loaded info objects */ 
	protected final Map<String, PaloInfo> loadedInfo;
	
	protected boolean loaded = false;
		
	//TODO add abstract methods for creating, deleting and reloading and add
	//		new public template methods!!
	//	=> required to keep track of all possible collection changes...
	//	=> make these methods protected
	//	=> only the template methods are public and should therefore be used
	//		by any clients...
	// make loadedInfo map private!!
	// force reload() to be use: => template method: reload() internal abstract method reloadInternal() (ugly name...)

	
	/**
	 * Creates a new loader instance for the given palo connection.
	 * @param paloConnection the palo server connection to user for loading 
	 * requests
	 */
	public PaloInfoLoader(DbConnection paloConnection) {
		this.paloConnection = paloConnection;
		loadedInfo = new LinkedHashMap<String, PaloInfo>();
	}
	
	
	/**
	 * Resets the loader, i.e. all loaded info objects are removed.
	 */
	public final void reset() {
		loaded = false;
		loadedInfo.clear();
	}
		
	/**
	 * Called after loading given palo info object
	 * @param info the loaded palo info object
	 */
	public final void loaded(PaloInfo info) {
		if (info != null) {
			loadedInfo.put(info.getId(),info);
		}
	}
	
	/**
	 * Resets and reloads all info object
	 */
	protected abstract void reload();
	

	/**
	 * Removes the palo info object form the internal cache which corresponds
	 * to the given id
	 * @param id the identifier of the palo object to remove
	 */
	public final void removed(String id) {
		loadedInfo.remove(id);
	}

	/**
	 * Removes the given palo info object from the internal cache
	 * @param info the palo info object to remove
	 */
	protected final void removed(PaloInfo info) {
		removed(info.getId());
	}
	
	
	/**
	 * Returns the identifiers of all currently loaded palo info objects 
	 * @return the ids of all currently loaded info objects
	 */
	protected final String[] getLoadedIds() {
		return loadedInfo.keySet().toArray(new String[loadedInfo.size()]);
	}
	
	/**
	 * Returns a collection of all currently loaded palo info objects
	 * @return a collection of all currently loaded palo info objects
	 */
	protected final Collection<PaloInfo> getLoaded() {
		return loadedInfo.values();
	}
	
	protected final boolean hasType(int typeMask, int type) {
		return (typeMask & type) > 0;
	}
}
