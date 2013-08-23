/*
*
* @file HttpElementLoader.java
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
* @version $Id: HttpElementLoader.java,v 1.8 2010/02/26 10:10:31 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.http.loader;

import java.util.ArrayList;
import java.util.Collection;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.loader.ElementLoader;

/**
 * <code>HttpElementInfoLoader</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: HttpElementLoader.java,v 1.8 2010/02/26 10:10:31 PhilippBouillon Exp $
 **/
public class HttpElementLoader extends ElementLoader {

	public HttpElementLoader(DbConnection paloConnection,
			HierarchyInfo hierarchy) {
		super(paloConnection, hierarchy);
	}

	public String[] getAllElementIds() {
		if(!loaded) {			
			reload();
			loaded = true;
		}
		return getLoadedIds();
	}
	
	public final ElementInfo load(int index) {
		//TODO rewrite since palo supports getElementAt(position)!!!
		String[] elIds = getAllElementIds();
		if (index < 0 || index > elIds.length - 1)
			return null;
		return load(elIds[index]);
	}


	public ElementInfo loadByName(String name) {
		//first check if we have it loaded already
		ElementInfo elInfo = findElement(name);
		if(elInfo == null) {
			//if not, we have to ask server...
			reload();
			elInfo = findElement(name);
		}
		return elInfo;
	}

	public final ElementInfo[] getElementsAtDepth(int depth) {
		String[] ids = getAllElementIds();
		ArrayList<ElementInfo> lvlElements = new ArrayList<ElementInfo>(); 
		for (String id : ids) {
			ElementInfo info = load(id);
			if (info != null && info.getDepth() == depth) {
				lvlElements.add(info);
			}
		}
		return (ElementInfo[]) lvlElements.toArray(new ElementInfo[lvlElements
				.size()]);
	}
	
	public final ElementInfo[] getChildren(ElementInfo el) {
		String[] children = el.getChildren();
		// Workaround for a bug in Palo: Sometimes el.getChildren() 
		// returns too many children (if the user does not have access
		// rights).
		ArrayList <ElementInfo> _children = new ArrayList<ElementInfo>();
		ArrayList <String> newChildren = new ArrayList<String>();
		for(int i=0;i<children.length;++i) {
			try {
				ElementInfo ei = load(children[i]);
				if (ei == el) {
					return new ElementInfo[0];
				}
				_children.add(ei);
				newChildren.add(children[i]);
			} catch (Throwable t) {				
			}
		}
		el.update(newChildren.toArray(new String[0]));
		return _children.toArray(new ElementInfo[0]);
		
//		ElementInfo[] _children = new ElementInfo[children.length];
//		for(int i=0;i<children.length;++i) {
//			_children[i] = load(children[i]);
//			if(_children[i] == el) {
//				return new ElementInfo[0];
//			}
//		}
//		return _children;
	}

	protected final void reload() {
		reset();
		ElementInfo[] elInfos = paloConnection.getElements(hierarchy);
		for (ElementInfo elInfo : elInfos) {
			loaded(elInfo);
		}
	}
	
	private final ElementInfo findElement(String name) {
		Collection<PaloInfo> infos = getLoaded();
		for(PaloInfo info : infos) {
			if (info instanceof ElementInfo) {
				ElementInfo elInfo = (ElementInfo) info;
				//PALO IS NOT CASESENSETIVE!!
				if (elInfo.getName().equalsIgnoreCase(name))	
					return elInfo;
			}
		}
		return null;
	}
}
