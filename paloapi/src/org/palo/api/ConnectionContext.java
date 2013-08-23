/*
*
* @file ConnectionContext.java
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
* @version $Id: ConnectionContext.java,v 1.5 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api;

/**
 * <code>ConnectionContext</code>
 * The context provides methods to access additional information about the
 * currently used connection.
 *
 * @author ArndHouben
 * @version $Id: ConnectionContext.java,v 1.5 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public interface ConnectionContext {

	/** @deprecated Please don't use! For internal usage only */
	public Object getData(String key);
	/** @deprecated Please don't use! For internal usage only */
	public void setData(String key, Object data);

	
	/**
	 * @deprecated PLEASE DON'T USE! SUBJECT TO CHANGE
	 */
	public boolean doSupportSubset2();
	/**
	 * Returns the {@link Rights} instance which is associated with this 
	 * connection.
	 * @return the <code>Right</code> instance associated to this connection
	 */
	public Rights getRights();
	
	/**
     * Returns the {@link ServerInfo} object associated with this connection to 
     * get more information about the underlying server.
     * 
     * @return the <code>ServerInfo</code> object associated with this connection.
     */
    ServerInfo getServerInfo();
}
