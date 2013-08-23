/*
*
* @file FunctionLoader.java
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
* @version $Id: FunctionLoader.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.loader;

import com.tensegrity.palojava.DbConnection;

/**
 * <p><code>FunctionInfoLoader</code><7p>
 * This abstract base class manages the loading of functions.
 *
 * @author ArndHouben
 * @version $Id: FunctionLoader.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public abstract class FunctionLoader extends PaloInfoLoader {

	/**
	 * Creates a new loader instance.
	 * @param paloConnection
	 */
	public FunctionLoader(DbConnection paloConnection) {
		super(paloConnection);
	}

	/**
	 * Returns the xml string which contains all defined functions
	 * @return functions as xml 
	 */
	public abstract String loadAll();
	
}
