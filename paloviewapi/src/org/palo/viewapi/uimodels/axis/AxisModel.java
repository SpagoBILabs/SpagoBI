/*
*
* @file AxisModel.java
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
* @version $Id: AxisModel.java,v 1.10 2010/03/11 10:42:19 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis;

import org.palo.api.Hierarchy;
import org.palo.api.utils.ElementPath;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.uimodels.axis.events.AxisModelListener;

/**
 * <code>AxisModel</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisModel.java,v 1.10 2010/03/11 10:42:19 PhilippBouillon Exp $
 **/
public interface AxisModel {

	public Axis getAxis();
	
	public void addListener(AxisModelListener listener);
	public void removeListener(AxisModelListener listener);

	//TODO remove this? better in axis itself?
	public void add(AxisHierarchy hierarchy);
	public void add(int index, AxisHierarchy hierarchy);
	public void remove(AxisHierarchy hierarchy);
	public void removeAll();
	public int getAxisHierarchyCount();
	public AxisHierarchy[] getAxisHierarchies();
	public AxisHierarchy getAxisHierarchy(String id);
	//end remove comment
	
	public void swap(AxisHierarchy hierarchy1, AxisHierarchy hierarchy2);
	
	/** convenience */
	public Hierarchy [] getHierarchies();
	
	
	public AxisItem[] getRoots();

//	public Subset2 getSubset(Hierarchy hierarchy);
	
	/**
	 * Returns the {@link AxisItem} which is referenced by the given 
	 * {@link ElementPath} or <code>null</code> if no such item could be found 
	 * in current model state. <b>Note:</b> if several items apply to the given 
	 * path only the first matching one is returned.
	 * 
	 * @param path an <code>ElementPath</code>
	 * @return the corresponding referenced <code>AxisItem</code> or 
	 * <code>null</code> if no matching item could be found
	 */	
	public AxisItem getItem(ElementPath path);
	public AxisItem getItem(String path);	
	public AxisItem getItem(String path, int index);
	
//	public AxisItem findItemAfter(AxisItem lastItem, ElementPath path);
//	public AxisItem findItemAfter(AxisItem lastItem, String path);
//	public AxisItem findItemAfter(AxisItem lastItem, String pathRegEx, String hierarchyId);
	/**
	 * Looks for an {@link AxisItem} which matches the specified path regex. 
	 * Since path ids are only unique within their corresponding 
	 * {@link Hierarchy}, the hierarchy id must be specified too. 
	 * If no matching item is found <code>null</code> is returned. 
	 * @param pathRegEx a regular expression for a path, e.g. .*23,19
	 * @param hierarchyId id of the hierarchy to which the item belongs 
	 * @return the matching <code>AxisItem</code> or <code>null</code>
	 * 
	 */
	public AxisItem findItem(String pathRegEx, String hierarchyId);
	public AxisItem findItem(String pathRegEx, int index, String hierarchyId);
	
	public int getMaximumLevel(Hierarchy hierarchy);

	/**
	 * Returns the next sibling of the given {@link AxisItem} or 
	 * <code>null</code> if the specified item ist last sibling
	 * @param item the {@link AxisItem} to get the next sibling for
	 * @return the next sibling item or <code>null</code>
	 */
	public AxisItem getNextSibling(AxisItem item);
	public AxisItem getFirstSibling(AxisItem item);
	public AxisItem getLastSibling(AxisItem item);	
	public AxisItem getLastChildInNextHierarchy(AxisItem item);
	public AxisItem getFirstChildInNextHierarchy(AxisItem item);
	public AxisItem[] getSiblings(AxisItem item);

	public void expand(AxisItem item);
	public void collapse(AxisItem item); 	
	public void showAllParents(boolean toggle);
	
	public void refresh();
}
