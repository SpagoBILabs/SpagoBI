/*
*
* @file DomainObjectImpl.java
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
* @version $Id: DomainObjectImpl.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import org.palo.viewapi.DomainObject;


/**
 * <code>DomainObject</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: DomainObjectImpl.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $ 
 **/
public class DomainObjectImpl implements DomainObject {

	//common superclass/supertyp for id handling of domain objects like user, reports...
	
	private String id;
//	protected final Connection connection;
	
	public DomainObjectImpl(String id) {
		this.id = id;
//		this.connection = connection;
	}
	
	public final String getId() {
		return id;
	}
	
	public final void setId(String id) {
		if(id == null || id.length()<1)
			throw new IllegalArgumentException("An id cannot be null or empty");
		this.id = id;
	}
}
