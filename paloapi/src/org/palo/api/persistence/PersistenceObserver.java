/*
*
* @file PersistenceObserver.java
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
* @version $Id: PersistenceObserver.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.persistence;

/**
 * A <code>PersistenceObserver</code> is used to monitor the loading and saving
 * of persistence palo objects like <code>{@link Subset}</code>s and 
 * <code>{@link CubeView}</code>s. Currently the API tries to load or save a 
 * persistence palo object completely and calls one of the defined callback 
 * methods afterwards.
 *
 * @author ArndHouben
 * @version $Id: PersistenceObserver.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
public interface PersistenceObserver {
	
	/**
	 * Called when the loading of the palo object failed
	 * @param sourceId the id of failed palo object
	 * @param errors additional information about the reasons
	 */
	void loadFailed(String sourceId, PersistenceError[] errors);
	
	/**
	 * Called when the palo object could not be loaded completely. 
	 * @param source an instance of the loaded palo object
	 * @param errors additional information about the reasons
	 */
	void loadIncomplete(Object source, PersistenceError[] errors);
	
	/**
	 * Called when the palo object could be loaded successfully
	 * @param source instance of loaded palo object
	 */
	void loadComplete(Object source);

	/**
	 * Called when the saving of the given palo object failed
	 * @param source palo object which could not be saved
	 * @param errors additional information about the reasons
	 */
	void saveFailed(Object source, PersistenceError[] errors);
	
	/**
	 * Called when the palo object could not be saved completely
	 * @param source palo object which could not be completely saved
	 * @param errors additional information about the reasons
	 */
	void saveIncomplete(Object source, PersistenceError[] errors);
	
	/**
	 * Called when the saving of the given palo object was successful
	 * @param source palo object which was saved
	 */
	void saveComplete(Object source);

}
