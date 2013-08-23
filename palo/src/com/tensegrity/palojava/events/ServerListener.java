/*
*
* @file ServerListener.java
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
* @version $Id: ServerListener.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/**
 * (c) Copyright 2006 Tensegrity Software
 * All rights reserved.
 */
package com.tensegrity.palojava.events;

/**
 * <p>
 * This listener gets notified whenever structural changes within the palo 
 * server are detected. Structural changes are e.g the deletion or creation
 * of cubes, dimensions, databases or elements. However, the listeners does
 * not get notified on cell value changes.
 * </p>
 *
 * @author ArndHouben
 * @version $Id: ServerListener.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public interface ServerListener {

	/**
	 * Called whenever the header detects that something within the structure 
	 * of the server changed, e.g. a dimension was added or deleted 
	 * @param event
	 */
	public void serverStructureChanged(ServerEvent event);
}

