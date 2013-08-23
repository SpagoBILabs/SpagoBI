/*
*
* @file AxisModelEvent.java
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
* @version $Id: AxisModelEvent.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis.events;

import org.palo.viewapi.uimodels.axis.AxisItem;


/**
 * <code>AxisTreeEvent</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisModelEvent.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class AxisModelEvent {

	/**
	 * Indicates if the current operation should proceed. Setting this field 
	 * to <code>false</code> will cancel the current operation.
	 */
	public boolean doit = true;
	
	/**
	 * The source item issuing the event.
	 */
	private final AxisItem source;
	
//	private int itemsCount;
	
	/**
	 * The affected AxisItems.
	 */
	private AxisItem[][] items;
			
    /**
     * Constructs a new <code>AxisTreeEvent</code> with the given source
     * {@link AxisItem} on which the event was raised.
     * @param source the event source
     */
	public AxisModelEvent(AxisItem source) {
		this.source = source;
	}
	
	/**
	 * Returns the source {@link AxisItem} on which this event was raised
	 * @return the event source
	 */
	public final AxisItem getSource() {
		return source;
	}
	
    /**
     * Returns the affected {@link AxisItem}s of the event or <code>null</code> 
     * @return the affected {@link AxisItem}s.
     */
	public final AxisItem[][] getItems() {
		return items == null ? new AxisItem[0][] : items;
	}
	
	/**
	 * Sets the affected items for this event.
	 * @param items the affected items for this event.
	 */
	public final void setItems(AxisItem[][] items) {
		this.items = items == null ? new AxisItem[0][] : items.clone();
//		itemsCount = this.items.length;
	}
		
//	/**
//	 * An estimated count of the possible affected items.
//	 * @return
//	 * @deprecated topic to remove
//	 */
//	public final int getItemsCount() {
//		return itemsCount;
//	}
//	
//	/**
//	 * @deprecated topic to remove
//	 */
//	public final void setItemsCount(int itemsCount) {
//		this.itemsCount = itemsCount;
//	}
}
