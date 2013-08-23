/*
*
* @file SubsetFilterHandler.java
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
* @version $Id: SubsetFilterHandler.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io.xml;

import org.palo.api.Dimension;
import org.palo.api.subsets.SubsetFilter;

/**
 * <code>SubsetFilterHandler</code>
 * <p><b>- API  INTERNAL -</b></p>
 * This interface describes an xml handler for reading <code>SubsetFilter</code>s
 * from xml.
 * 
 * @author ArndHouben
 * @version $Id: SubsetFilterHandler.java,v 1.5 2009/04/29 10:21:58 PhilippBouillon Exp $
 * @see {@link SubsetXMLHandler}
 **/
public interface SubsetFilterHandler {

	/**
	 * Returns the xml path for this subset filter
	 * @return the xpath
	 */
	public String getXPath();
	/**
	 * Called when given xml path is entered
	 * @param path xml path
	 */
	public void enter(String path);
	
	/**
	 * Called when given xml path is leaved
	 * @param path xml path
	 * @param value the value of this path
	 */
	public void leave(String path, String value);
		
	/**
	 * Creates a new <code>SubsetFilter</code> for the given dimension
	 * @param dimension the dimension to create the filter for
	 * @return the newly created subset filter
	 */
	public SubsetFilter createFilter(Dimension dimension);
	
	/**
	 * Specifies the subset version to use
	 * @param version the subset version
	 */
	public void setSubsetVersion(String version);
}
