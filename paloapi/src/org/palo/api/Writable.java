/*
*
* @file Writable.java
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
* @author PhilippBouillon
*
* @version $Id: Writable.java,v 1.3 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

package org.palo.api;

/**
 * Describes if an object can be modified and if it can add new children to
 * itself. All objects belonging to a palo database can be modified and
 * extended, whereas XMLA objects cannot.
 * 
 * @author PhilippBouillon
 * @deprecated subject to change, please do not use.
 */
public interface Writable {
	/**
	 * Returns true if this object can create child objects, false otherwise.
	 * @return true if this object can create child objects, false otherwise.
	 */
	boolean canCreateChildren();
	
	/**
	 * Returns true if this object can be modified (renamed, deleted, ...),
	 * false otherwise.
	 * @return true if this object can be modified, false otherwise.
	 */
	boolean canBeModified();
}
