/*
*
* @file ServerInfoBuilder.java
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
* @version $Id: ServerInfoBuilder.java,v 1.3 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.http.builders;

import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.ServerInfo;
import com.tensegrity.palojava.impl.ServerInfoImpl;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @version $Id: ServerInfoBuilder.java,v 1.3 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class ServerInfoBuilder {

	ServerInfoBuilder() {
		//package visibility only...
	}

	public ServerInfo create(PaloInfo parent, String[] response) throws PaloException {
		if(response.length<4)
			throw new PaloException("Not enough information to create ServerInfo!!");
		
		try {
			int major = Integer.parseInt(response[0]);
			int minor = Integer.parseInt(response[1]);
			int bugfix = Integer.parseInt(response[2]);
			int build = Integer.parseInt(response[3]);			
			int encryption = 2; 			
			int httpsPort = 0;
			if (response.length > 4) encryption = Integer.parseInt(response[4]);
			if (response.length > 5) httpsPort = Integer.parseInt(response[5]);
			return new ServerInfoImpl(build,bugfix,major,minor,httpsPort,encryption,false);
		}catch(Exception e) {
			throw new PaloException(e.getLocalizedMessage(),e);
		}
	}

}
