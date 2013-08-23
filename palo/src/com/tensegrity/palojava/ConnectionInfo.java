/*
*
* @file ConnectionInfo.java
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
* @version $Id: ConnectionInfo.java,v 1.3 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 */
package com.tensegrity.palojava;

/**
 * The <code>ConnectionInfo</code> interface defines methods which provides
 * detailed information about a connection to palo server. 
 * 
 * @author ArndHouben
 * @version $Id: ConnectionInfo.java,v 1.3 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public interface ConnectionInfo {

	/**
	 * The name or ip adress of the host computer which runs the palo server 
	 * @return host name or ip
	 */
	public String getHost();
	
    /**
     * Returns the port number under which the palo server listens
     * @return port number
     */
	public String getPort();

	/**
	 * Returns the login name
	 * @return login name
	 */
	public String getUsername();
    
	/**
	 * Returns the login password
	 * @return login password
	 */
    public String getPassword();
    
    public void setData(String id, Object data);
    public Object getData(String id);

}
