/*
*
* @file IPaloXMLHandler.java
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
* @version $Id: IPaloXMLHandler.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.xml;

/**
 * <code>IPaloXMLHandler</code>
 * To use in conjunction with <code>{@link BaseXMLHandler}</code> and
 * <code>{@link IPaloStartHandler}</code> and <code>{@link IPaloEndHandler}</code>.
 *
 * @author ArndHouben
 * @version $Id: IPaloXMLHandler.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
interface IPaloXMLHandler {

	/**
	 * Returns the xml path used to register an xml handler 
	 * @return
	 */
	public String getPath();
}
