/*
*
* @file PropertyLoader.java
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
* @version $Id: PropertyLoader.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.loader;

import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.PropertyInfo;

/**
 * <code>VariableInfoLoader</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: PropertyLoader.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public abstract class PropertyLoader extends PaloInfoLoader {
	public PropertyLoader(DbConnection paloConnection) {
		super(paloConnection);
	}

	/**
	 * Returns the identifiers of all properties currently known to the palo 
	 * server.
	 * @return ids of all known properties
	 */
	public abstract String[] getAllPropertyIds();

	public abstract PropertyInfo load(String id);

//	public final PropertyInfo load(String id) {
//		PaloInfo property = loadedInfo.get(id);
//		if (property == null) {
//			property = paloConnection.getProperty(id);
//			loaded(property);
//		}
//		return (PropertyInfo) property;
//	}
}
