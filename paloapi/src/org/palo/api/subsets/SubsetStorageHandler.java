/*
*
* @file SubsetStorageHandler.java
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
* @version $Id: SubsetStorageHandler.java,v 1.5 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets;

import org.palo.api.Subset;

/**
 * <code>SubsetStorageHandler</code>
 * <p>A simple interface for handling storage related subset functionalities.</p>
 * 
 * @author ArndHouben
 * @version $Id: SubsetStorageHandler.java,v 1.5 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public interface SubsetStorageHandler {
	
	/**
	 * Checks if current user is allowed to read any <code>Subset2</code>s of 
	 * given type. The subset type must be one of the predefined subset type
	 * constants.
	 * @param subsetType a valid subset type constant 
	 * @return <code>true</code> if user is allowed to read subsets of given
	 * type, <code>false</code> otherwise
	 */
	public boolean canRead(int subsetType);

	/**
	 * Checks if current user is allowed to write any <code>Subset2</code>s of 
	 * given type. The subset type must be one of the predefined subset type
	 * constants.
	 * @param subsetType a valid subset type constant 
	 * @return <code>true</code> if user is allowed to write subsets of given
	 * type, <code>false</code> otherwise
	 */
	public boolean canWrite(int subsetType);
	
	/**
	 * Tries to convert the given legacy subsets into new <code>Subset2</code>s
	 * of given type. 
	 * @param legacySubsets
	 * @param type
	 * @param remove
	 */
	public void convert(Subset[] legacySubsets, int type, boolean remove);
	
	/**
	 * Resets this storage handler. This will clear all internal used caches
	 * too.
	 */
	public void reset();
	
}
