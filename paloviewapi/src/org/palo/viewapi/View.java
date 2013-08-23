/*
*
* @file View.java
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
* @version $Id: View.java,v 1.10 2010/01/13 08:02:42 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

import org.palo.api.Cube;
import org.palo.api.exceptions.PaloIOException;

/**
 * The <code>View</code> interface is used to access a palo {@link CubeView}.  
 * Since the creation of a cube view can consume a lot of time it is delayed 
 * until {@link #createCubeView()} is called explicitly.
 * 
 *
 * @version $Id: View.java,v 1.10 2010/01/13 08:02:42 PhilippBouillon Exp $
 **/
public interface View extends ParameterizedGuardedObject {

	/** 
	 * Returns the view name.
	 * @return the view name
	 */
	public String getName();
	
	/**
	 * Returns the {@link Account} which is used to retrieve the palo 
	 * {@link CubeView}.
	 * @return the view <code>Account</code>
	 */
	public Account getAccount();
	
	/** 
	 * Returns the id of corresponding palo {@link Cube} to which this view
	 * belongs.
	 * @return the palo cube id 
	 */
	public String getCubeId();

	/** 
	 * Returns the id of corresponding palo {@link Database} to which this view
	 * belongs.
	 * @return the palo database id 
	 */
	public String getDatabaseId();
	
	/** 
	 * Returns the raw definition of the internal cube view
	 * @return the palo cube view definition 
	 */
	public String getDefinition(); //TODO rename to getXML()???

	/** 
	 * Creates a new {@link CubeView} from the internal view definition.
	 * @return the <code>CubeView</code>  
	 * @throws PaloIOException 
	 */
	public CubeView createCubeView(AuthUser user, String sessionId) throws PaloIOException; 	
	public CubeView getCubeView();
	public void setCubeView(CubeView cubeView);
}
