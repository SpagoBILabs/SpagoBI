/*
*
* @file AxisModelListener.java
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
* @version $Id: AxisModelListener.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis.events;



/**
 * <code>AxisTreeListener</code> 
 * <p>
 * Classes which implement this interface provide methods that deal with 
 * axis tree actions like expanding or collapsing of tree branches. These 
 * methods are called whenever the corresponding event happens. 
 * </p>
 * 
 * @version $Id: AxisModelListener.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $ 
 */
public interface AxisModelListener {
	
	/**
	 * Called whenever a tree branch is about to be expanded. The receiver can
	 * vote against this operation by setting {@link AxisModelEvent#doit} to
	 * <code>false</code>.
	 * @param e an event containing information about the tree operation
	 */
	public void willExpand(AxisModelEvent e);
	/**
	 * Called whenever a tree branch is about to be collapsed. The receiver can
	 * vote against this operation by setting {@link AxisModelEvent#doit} to
	 * <code>false</code>.
	 * @param e an event containing information about the tree operation
	 */
	public void willCollapse(AxisModelEvent e);
	/**
	 * Called when a tree branch is expanded.
	 * @param e an event containing information about the tree operation
	 */
	public void expanded(AxisModelEvent e);
	/**
	 * Called when a tree branch is collapsed.
	 * @param e an event containing information about the tree operation
	 */
	public void collapsed(AxisModelEvent e);
		
	/**
	 * Calls when the structure of the tree (or a branch) is changed.
	 * @param e an event containing information about the tree operation.
	 */
	public void structureChanged(AxisModelEvent e);
	
	//TODO requested:
	//update event => if visualization should change, e.g. an alias was choosen or a format applied
	//detailed structure events: => for typical tree operation like insert, remove...
	
	
	//TODO maybe...	
	//public void changed(AxisTreeEvent e);
	//public void removed(AxisTreeEvent e);
	//public void hidden(AxisTreeEvent e);
	//public void shown(AxisTreeEvent e);
	
	
}
