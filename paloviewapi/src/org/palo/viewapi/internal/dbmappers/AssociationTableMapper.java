/*
*
* @file AssociationTableMapper.java
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
* @version $Id: AssociationTableMapper.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.dbmappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.palo.viewapi.DomainObject;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.IAssociationManagement;


/**
 * <code>AssociationTableLoader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AssociationTableMapper.java,v 1.5 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
abstract class AssociationTableMapper extends Mapper implements IAssociationManagement {

	
	protected abstract String insertStatement();
	protected abstract String deleteStatement();
	protected abstract String updateStatement();

	
	public void delete(DomainObject obj, DomainObject assoc) throws SQLException {
		Connection connection = DbService.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(deleteStatement());
			stmt.setString(1,obj.getId());
			stmt.setString(2,assoc.getId());
			stmt.execute();
		} finally {
			cleanUp(stmt);
		}
	}

	public void delete(DomainObject obj, String sql) throws SQLException {
		if (obj == null) {
			return;
		}
		Connection connection = DbService.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setString(1,obj.getId());
			stmt.execute();
		} finally {
			cleanUp(stmt);
		}
	}
	
	public void insert(DomainObject obj, DomainObject assoc) throws SQLException {
		Connection connection = DbService.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(insertStatement());
			stmt.setString(1, obj.getId());
			stmt.setString(2, assoc.getId());
			stmt.execute();
			//TODO cache!!
			//loaded.put(obj.getId(), obj);
		} finally {
			cleanUp(stmt);
		}
	}

	public void update(DomainObject obj, DomainObject assoc) throws SQLException {
		Connection connection = DbService.getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(updateStatement());
			stmt.setString(1, obj.getId());
			stmt.setString(2, assoc.getId());
			stmt.execute();
		} finally {
			cleanUp(stmt);
		}
	}
}
