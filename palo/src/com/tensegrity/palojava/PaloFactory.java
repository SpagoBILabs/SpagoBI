/*
*
* @file PaloFactory.java
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
* @author Arnd Houben
*
* @version $Id: PaloFactory.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava;

import com.tensegrity.palojava.http.HttpPaloServer;

/**
 * The <code>PaloFactory</code> class is used to create a {@link PaloServer}
 * instance.
 * 
 * @author Arnd Houben
 * @version $Id: PaloFactory.java,v 1.4 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class PaloFactory {

	//--------------------------------------------------------------------------
	// FACTORY
	//
	/** sole factory instance */
	private static final PaloFactory instance = new PaloFactory();
	/**
	 * Returns the sole factory instance
	 * @return 
	 */
	public static final PaloFactory getInstance() {
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private PaloFactory() {		
	}
	
	/**
	 * Creates a {@link PaloServer} of the given type which is connected to
	 * a palo server on specified host at specified port
	 * @param host the host which runs the palo server
	 * @param port the port on which the palo server listens
	 * @param srvType the server type, either {@link PaloServer#TYPE_LEGACY} or
	 * {@link PaloServer#TYPE_HTTP}
	 * @return the {@link PaloServer} connection
	 */
	public final PaloServer createServerConnection(String host, String port, int srvType, int timeout) {
		switch(srvType) {
		case PaloServer.TYPE_LEGACY:
			throw new PaloException("Legacy server is not supported anymore!");
		case PaloServer.TYPE_HTTP:
			return  new HttpPaloServer(host,port,timeout);
		default: throw new PaloException("Unknown server type!");
		}
	}
}
