/*
*
* @file DomainObject.java
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
* @version $Id: DomainObject.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;


/**
 * The <code>DomainObject</code> interface is the base class of all domain 
 * objects used in this api. It defines a non globally unique identifier for 
 * each domain object.
 * <p><b>NOTE:</b>
 * The id is only unique within the scope of its domain object type. For example
 * the id of the domain object {@link User} is only unique for any objects of 
 * type <code>User</code> or its subtypes. Therefore it can be possible that a 
 * domain object of type {@link View}, for example, has the same id.
 * </p>

 *
 * @version $Id: DomainObject.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface DomainObject {

	/**
	 * Returns the domain object identifier.
	 * @return the domain object identifier
	 */
	public String getId();

}

