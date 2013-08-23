/*
*
* @file DomainObjectCache.java
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
* @version $Id: DomainObjectCache.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package org.palo.viewapi.internal.dbmappers;

import java.util.HashMap;
import java.util.Map;

import org.palo.viewapi.DomainObject;

/**
 * <code>DomainObjectCache</code>
 * TODO DOCUMENT ME
 * A very simple cache class...
 *
 * @version $Id: DomainObjectCache.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
class DomainObjectCache {

	private static final boolean USE_CACHE = !true;
	
	protected final Map<String, DomainObject> cache = new HashMap<String, DomainObject>();

	
	public final void clear() {
		cache.clear();
	}
	public final boolean contains(String id) {
		return USE_CACHE && cache.containsKey(id);
	}
	public final DomainObject get(String id) {
		if(USE_CACHE)
			return cache.get(id);
		return null;
	}
	public final void add(DomainObject domObj) {
		if(USE_CACHE)
			cache.put(domObj.getId(), domObj);
	}
	public final void remove(DomainObject domObj) {
		cache.remove(domObj.getId());
	}


}
