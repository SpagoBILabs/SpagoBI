/*
*
* @file ServerEvent.java
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
* @version $Id: ServerEvent.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/**
 * (c) Copyright 2006 Tensegrity Software
 * All rights reserved.
 */
package com.tensegrity.palojava.events;

/**
 * {@<describe>}
 * <p>
 * TODO: DOCUMENT ME
 * </p>
 * {@</describe>}
 *
 * @author ArndHouben
 * @version $Id: ServerEvent.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public interface ServerEvent {

	//define the valid types:
	public static final int SERVER_CHANGED = 0;
	public static final int DATABASE_CHANGED = 1;
	public static final int DIMENSION_CHANGED = 2;
	public static final int CUBE_CHANGED = 4;
	public static final int SERVER_DOWN = 8;
	
	/**
	 * Returns one of the defined event types
	 * @return
	 */
	public int getType();
}

