/*
*
* @file XConnection.java
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
* @version $Id: XConnection.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models.account;

import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>XConnection</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XConnection.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public class XConnection extends XObject {

	public static final String TYPE = XConnection.class.getName();
	
	/** connection type legacy @deprecated NOT SUPPORTED ANYMORE */
	public static final int TYPE_LEGACY = 1;	
	/** connection type http */
	public static final int TYPE_HTTP = 2;	//TODO better change to palo?
	/** connection type xmla */
	public static final int TYPE_XMLA = 3;
	/** connection type wss */
	public static final int TYPE_WSS = 4;

	private String host;
	private String service;
	private String description;
	private int connectionType;	
	
	public XConnection() {		
	}
	
	public XConnection(String id, String name, int connectionType) {
		setId(id);
		setName(name);
		this.connectionType = connectionType;
	}
	
	
	public final String getType() {
		return TYPE;
	}


	public final int getConnectionType() {
		return connectionType;
	}


	public final void setConnectionType(int connectionType) {
		this.connectionType = connectionType;
	}


	public final String getHost() {
		return host;
	}


	public final void setHost(String host) {
		this.host = host;
	}


	public final String getService() {
		return service;
	}


	public final void setService(String service) {
		this.service = service;
	}


	public final String getDescription() {
		return description;
	}


	public final void setDescription(String description) {
		this.description = description;
	}
	
}
