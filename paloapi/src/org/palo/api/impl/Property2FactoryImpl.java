/*
*
* @file Property2FactoryImpl.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: Property2FactoryImpl.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

package org.palo.api.impl;

import org.palo.api.Connection;
import org.palo.api.Property2;
import org.palo.api.Property2Factory;

public class Property2FactoryImpl extends Property2Factory {
	public final Property2 newProperty(Connection con, String id, String value) {
		return createProperty(con, id, value, null, Property2.TYPE_STRING, false);
	}

	public final Property2 newProperty(Connection con, String id, String value, Property2 parent) {		
		return createProperty(con, id, value, parent, Property2.TYPE_STRING, false);
	}

	public final Property2 newProperty(Connection con, String id, String value, int type) {
		return createProperty(con, id, value, null, type, false);
	}

	public final Property2 newProperty(Connection con, String id, String value, Property2 parent,
			int type) {
		return createProperty(con, id, value, parent, type, false);
	}

	public final Property2 newReadOnlyProperty(Connection con, String id, String value) {
		return createProperty(con, id, value, null, Property2.TYPE_STRING, true);
	}

	public final Property2 newReadOnlyProperty(Connection con, String id, String value,
			Property2 parent) {
		return createProperty(con, id, value, parent, Property2.TYPE_STRING, true);
	}

	public final Property2 newReadOnlyProperty(Connection con, String id, String value, int type) {
		return createProperty(con, id, value, null, type, true);
	}

	public final Property2 newReadOnlyProperty(Connection con, String id, String value,
			Property2 parent, int type) {
		return createProperty(con, id, value, parent, type, true);
	}
	
	private final Property2 createProperty(Connection con, String id, String value, 
			Property2 parent, int type, boolean readOnly) {		
		return Property2Impl.create(con, id, value, parent, type, readOnly);
	}
}
