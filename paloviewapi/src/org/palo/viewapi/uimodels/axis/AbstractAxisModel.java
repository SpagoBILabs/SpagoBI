/*
*
* @file AbstractAxisModel.java
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
* @version $Id: AbstractAxisModel.java,v 1.5 2010/03/11 10:42:19 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis;

import java.util.HashSet;

import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.uimodels.axis.events.AxisModelEvent;
import org.palo.viewapi.uimodels.axis.events.AxisModelListener;


/**
 * <code>AbstractAxisModel</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AbstractAxisModel.java,v 1.5 2010/03/11 10:42:19 PhilippBouillon Exp $
 **/
abstract class AbstractAxisModel implements AxisModel {
	
	protected final Axis axis; //the model
	private final HashSet<AxisModelListener> listeners;
	
	
	AbstractAxisModel(Axis axis) {
		this.axis = axis;
		this.listeners = new HashSet<AxisModelListener>();
	}
	
	public final Axis getAxis() {
		return axis;
	}
	
	public void addListener(AxisModelListener listener) {
		listeners.add(listener);
	}
	public void removeListener(AxisModelListener listener) {
		listeners.remove(listener);
	}

	protected abstract void init();
	
	//--------------------------------------------------------------------------
	// AXIS HIERARCHY HANDLING:
	//
	public final void add(AxisHierarchy hierarchy) {
		axis.add(hierarchy);		
		init();
		notifyStructureChange();
	}
	
	
	public final void add(int index, AxisHierarchy hierarchy) {
		AxisHierarchy[] hierarchies = axis.getAxisHierarchies();
		index = check(index, hierarchies.length);
		clear(axis);
		for(int i=0;i<index;++i)
			axis.add(hierarchies[i]);
		axis.add(hierarchy);
		for(int i=index; i<hierarchies.length;++i)
			axis.add(hierarchies[i]);
		//finally
		init();
		notifyStructureChange();
	}
	public final void remove(AxisHierarchy hierarchy) {
		axis.remove(hierarchy);
		init();
		notifyStructureChange();
	}
	public final void removeAll() {
		axis.removeAll();
		init();
		notifyStructureChange();
	}
	public final void swap(AxisHierarchy hierarchy1, AxisHierarchy hierarchy2) {
		AxisHierarchy[] hierarchies = axis.getAxisHierarchies();
		int index1 = -1;
		int index2 = -1;
		for (int i = 0; i < hierarchies.length; ++i) {
			if (index1 >= 0 && index2 >= 0)
				break;
			if (index1 < 0)
				index1 = hierarchies[i].equals(hierarchy1) ? i : -1;
			if (index2 < 0)
				index2 = hierarchies[i].equals(hierarchy2) ? i : -1;
		}
		if (index1 < 0 || index2 < 0)
			return;
		//swap
		AxisHierarchy tmp = hierarchies[index1];
		hierarchies[index1] = hierarchies[index2];
		hierarchies[index2] = tmp;
		clear(axis);
		for (int i = 0; i < hierarchies.length; ++i)
			axis.add(hierarchies[i]);

		// finally
		init();
		notifyStructureChange();
	}
	public final AxisHierarchy[] getAxisHierarchies() {
		return axis.getAxisHierarchies();
	}

	public final AxisHierarchy getAxisHierarchy(String id) {
		return axis.getAxisHierarchy(id);
	}
	
	protected AxisModelEvent notifyWillExpand(AxisItem source) {
		AxisModelEvent event = new AxisModelEvent(source);
//		event.setItemsCount(itemsCount);
		for(AxisModelListener listener : listeners) {
			listener.willExpand(event);
		}
		return event;
	}
	protected AxisModelEvent notifyWillCollapse(AxisItem source) {
		AxisModelEvent event = new AxisModelEvent(source);
//		event.setItemsCount(itemsCount);
		for(AxisModelListener listener : listeners) {
			listener.willCollapse(event);
		}
		return event;
	}
	
	protected void notifyExpanded(AxisItem source, AxisItem[][] items) {
		AxisModelEvent event = new AxisModelEvent(source);
		event.setItems(items);
//		event.setItems(items);
		for(AxisModelListener listener : listeners) {
			listener.expanded(event);
		}
	}
	
	protected void notifyCollapsed(AxisItem source, AxisItem[][] items) {
		AxisModelEvent event = new AxisModelEvent(source);
		event.setItems(items);
//		event.setItems(items);
		for(AxisModelListener listener : listeners) {
			listener.collapsed(event);
		}
	}

	//TODO how to notify hide/show <=> insert/remove
	protected void notifyStructureChange() {
		AxisModelEvent event = new AxisModelEvent(null);
//		event.setItems(items);
		for(AxisModelListener listener : listeners) {
			listener.structureChanged(event);
		}
	}
	
	private final void clear(Axis axis) {
		AxisHierarchy[] hierarchies = axis.getAxisHierarchies();
		for(AxisHierarchy hierarchy : hierarchies)
			axis.remove(hierarchy);
	}
	
	private final int check(int index, int max) {
		if(index < 0)
			index = 0;
		else if(index > max)
			index = max;
		return index;
	}
}
