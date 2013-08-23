/*
*
* @file PaloConnectionFactory.java
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
* @version $Id: PaloConnectionFactory.java,v 1.3 2010/02/12 13:51:05 PhilippBouillon Exp $
*
*/

package org.palo.viewapi.internal;

import org.palo.api.Connection;
import org.palo.api.ConnectionConfiguration;
import org.palo.api.ConnectionFactory;

public class PaloConnectionFactory implements IConnectionFactory {

	/**
	 * {@inheritDoc}
	 */
	public Connection createConnection(String host, String service, String login,
			String password, String provider) {
		ConnectionConfiguration cc = new ConnectionConfiguration(host, service);
		cc.setUser(login);
		cc.setPassword(password);
		int type = provider.equalsIgnoreCase("xmla")? Connection.TYPE_XMLA : Connection.TYPE_HTTP;
		cc.setType(type);
		cc.setLoadOnDemand(true);
		cc.setTimeout(120000);
		Connection r = ConnectionFactory.getInstance().newConnection(cc);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(PaloConfiguration cfg) {
		//ignore
	}

}
