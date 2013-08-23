/*
*
* @file ExportDataset.java
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
* @author Axel Kiselev
*
* @version $Id: ExportDataset.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api;

/**
 * <code>ExportDataset</code>
 *
 * Allows to get access to data export results
 *
 * <p>
 * Instances of that class are received by 
 * {@link org.palo.api.Cube#getDataExport()} calls.
 * </p>
 *
 * <p>
 * Here is an example code snippet for walking through data export results
 * <pre>
 *      ...
 *     ExportDataset dataset= cube.getDataExport();
 *     while(dataset.hasNextCell())
 *     {
 *         Cell cell = dataset.getNextRow();
 *         ...
 *     }
 *      ...
 *</pre>
 *</p>
 *
 * <p>
 * See also {@link org.palo.api.ExportContext} and {@link Cell}.
 * </p>
 *
 * @author Axel Kiselev
 * @author ArndHouben
 * @version $Id: ExportDataset.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public interface ExportDataset {
	
//	/**
//	 * Gets next row from the dataset
//	 * @return next row. Objects in that row represent path (element names, {@link java.lang.String})
//	 * and the value ({@link java.lang.String} or ({@link java.lang.Double})
//	 */
//	Object[] getNextRow();
//
//	/**
//	 * Checks whether there are at least one more row
//	 * @return <code>true</code> if dataset contains at least one more row, <code>false</code> otherwise
//	 */
//	boolean hasNextRow();
	
	/**
	 * Gets the next cell from dataset
	 * @return next cell
	 */
	Cell getNextCell();

	/**
	 * Checks whether there is at least one more cell.
	 * @return <code>true</code> if dataset contains at least one more cell, 
	 * <code>false</code> otherwise
	 */
	boolean hasNextCell();

}
