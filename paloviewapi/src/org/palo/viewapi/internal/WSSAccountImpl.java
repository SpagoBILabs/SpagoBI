/*
*
* @file WSSAccountImpl.java
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
* @version $Id: WSSAccountImpl.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

package org.palo.viewapi.internal;

import org.palo.viewapi.WSSAccount;

public class WSSAccountImpl extends AccountImpl implements WSSAccount {
//	private WSSConnection wssConnection;
	
	WSSAccountImpl(String id, String user) {
		super(id, user);
	}
	
	WSSAccountImpl(Builder builder) {
		super(builder);
	}

	
	public final void logout() {
//		if (wssConnection != null) {
//			wssConnection.logout();
//		}
	}
//
//	public boolean isLoggedIn() {
//		return wssConnection != null;
//	}
//	
//	public WSSConnection login() {
//		if (wssConnection == null) {
//			System.err.println("Logging into WSS Server...");
//			PaloConnection paloConnection = getConnection();
//			if (paloConnection.getType() == PaloConnection.TYPE_WSS) {
//				WSSConnection con = 
//					WSSConnectionFactory.getInstance().newConnection(
//						paloConnection.getHost(),
//						paloConnection.getService());
//				con.login(getLoginName(), getPassword());
//				this.wssConnection = con;				
//			}
//			System.err.println("Logging in done...");
//		}
//		return wssConnection;
//	}
}
