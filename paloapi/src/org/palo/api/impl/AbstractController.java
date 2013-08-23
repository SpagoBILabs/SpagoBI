/*
*
* @file AbstractController.java
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
* @version $Id: AbstractController.java,v 1.7 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl;

import java.util.Collection;
import java.util.Map;

import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Subset;
import org.palo.api.persistence.PaloPersistenceException;

/**
 * <p>
 * Defines an abstract controller class to encapsulate the creating and deleting
 * of palo extension objects, like {@link Subset} and {@link CubeView}.
 * </p> 
 * <p>
 * This class is part of the used palo creation pattern which hides the 
 * implementation details of the {@link Subset} and {@link CubeView} interfaces.
 * This means that no implementing class should be accessibly outside the 
 * <code>org.palo.api.impl</code> package and therefore this class cannot be an 
 * interface.
 * </p>
 * 
 * @author ArndHouben
 * @version $Id: AbstractController.java,v 1.7 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public abstract class AbstractController {

	/**
	 * Returns an instance of the given class parameter.
	 * @param clObject
	 * @param args constructor parameters used to create a class instance 
	 * @return
	 */
	protected abstract Object create(Class clObject, Object[] args);
	
	/**
	 * Deletes the given extension object 
	 * @param obj
	 */
	protected abstract boolean delete(Object obj);
	
	/**
	 * Loads all extension objects from the given database which are assigned to
	 * the palo object specified by the given id
	 */
	protected abstract Object load(Database db, String id)
			throws PaloPersistenceException;
	
	protected abstract void load(Database db, Map id2id, Map views)
			throws PaloPersistenceException;
	
	protected abstract void init(Database db);

}
