/*
*
* @file InternalService.java
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
* @version $Id: InternalService.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;

/**
 * <code>InternalService</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: InternalService.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
class InternalService {
	
	protected final AuthUserImpl user;
//	protected final Connection connection;
	protected final MapperRegistry mapperReg;
	
	InternalService(AuthUser user) {
		if (user == null)
			throw new NullPointerException("user may not be null");
		this.user = (AuthUserImpl)user;
//		this.connection = this.user.getAuthentication().getConnection();
		mapperReg = MapperRegistry.getInstance();
	}
	
	protected final IAccountManagement getAccountManagement() {
		return mapperReg.getAccountManagement();
	}
	protected final IUserManagement getUserManagement() {
		return mapperReg.getUserManagement();
	}
	
	protected final IGroupManagement getGroupManagement() {
		return mapperReg.getGroupManagement();
	}

	protected final IRoleManagement getRoleManagement() {
		return mapperReg.getRoleManagement();
	}

	protected final IReportManagement getReportManagement() {
		return mapperReg.getReportManagement();
	}
	
	protected final IViewManagement getViewManagement() {
		return mapperReg.getViewManagement();
	}
	
	protected final IFolderManagement getFolderManagement() {
		return mapperReg.getFolderManagement();
	}
	
	protected final IConnectionManagement getConnectionManagement() {
		return mapperReg.getConnectionManagement();
	}
}
