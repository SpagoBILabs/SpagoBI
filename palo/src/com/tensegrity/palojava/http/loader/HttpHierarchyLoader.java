/*
*
* @file HttpHierarchyLoader.java
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
* @version $Id: HttpHierarchyLoader.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.http.loader;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.loader.HierarchyLoader;

/**
 * <code>HttpHierarchyInfoLoader</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: HttpHierarchyLoader.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public class HttpHierarchyLoader extends HierarchyLoader {
	
	public HttpHierarchyLoader(DbConnection paloConnection,
			DimensionInfo dimension) {
		super(paloConnection, dimension);
	}
	
	public String[] getAllHierarchyIds() {
		if(!loaded) {			
			reload();
			loaded = true;
		}
		return getLoadedIds();
	}

	public int getHierarchyCount() {
		reload();
		return getLoaded().size();
	}

	protected final void reload() {
		reset();
		HierarchyInfo[] hierInfos = paloConnection.getHierarchies(dimension);
		for (HierarchyInfo hierInfo : hierInfos) {
			loaded(hierInfo);
		}
	}

}
