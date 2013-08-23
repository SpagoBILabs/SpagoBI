/*
*
* @file ReportMapper.java
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
* @version $Id: ReportMapper.java,v 1.6 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.dbmappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.palo.viewapi.DomainObject;
import org.palo.viewapi.Report;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.internal.DbService;
import org.palo.viewapi.internal.IReportManagement;
import org.palo.viewapi.internal.IReportRoleManagement;
import org.palo.viewapi.internal.IReportViewManagement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.IViewManagement;
import org.palo.viewapi.internal.ReportImpl;
import org.palo.viewapi.internal.ReportImpl.Builder;


/**
 * <code>ReportMapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ReportMapper.java,v 1.6 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class ReportMapper extends AbstractMapper implements IReportManagement {

	private static final String TABLE = DbService.getQuery("Reports.tableName");
	private static final String COLUMNS = DbService.getQuery("Reports.columns");
	private static final String CREATE_TABLE_STMT = DbService.getQuery("Reports.createTable", TABLE);
	private static final String FIND_BY_ID_STMT = DbService.getQuery("Reports.findById", COLUMNS, TABLE);
	private static final String FIND_BY_NAME_STMT = DbService.getQuery("Reports.findByName", COLUMNS, TABLE);
	private static final String FIND_BY_OWNER_STMT = DbService.getQuery("Reports.findByOwner", COLUMNS, TABLE);
	private static final String INSERT_STMT = DbService.getQuery("Reports.insert", TABLE);
	private static final String UPDATE_STMT = DbService.getQuery("Reports.update", TABLE);
	private static final String DELETE_STMT = DbService.getQuery("Reports.delete", TABLE);


	public final List<Report> findReports(Role role) throws SQLException {
		IReportManagement reportMgmt = 
			MapperRegistry.getInstance().getReportManagement();
		IReportRoleManagement rrAssoc = 
			MapperRegistry.getInstance().getReportRoleAssociation();
		List<String> reports = rrAssoc.getReports(role);
		List<Report> allReports = new ArrayList<Report>();
		for(String id : reports) {
			Report report = (Report) reportMgmt.find(id);
			if(report != null && !allReports.contains(report))
				allReports.add(report);
		}
		return allReports;
	}

	public final List<Report> findReports(User owner) throws SQLException {
		//TODO first run through cache?
		List<Report> reports = new ArrayList<Report>();
		PreparedStatement stmt = null;
		ResultSet results = null;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(FIND_BY_OWNER_STMT);
			stmt.setString(1, owner.getId());
			results = stmt.executeQuery();
			while (results.next()) {
				Report report = (Report)load(results);
				if(!reports.contains(report))
					reports.add(report);
			}
			return reports;
		} finally {
			cleanUp(stmt, results);
		}
	}
	
	public final void update(DomainObject obj) throws SQLException {
		PreparedStatement stmt = null;
		Report report = (Report) obj;
		Connection connection = DbService.getConnection();
		try {
			stmt = connection.prepareStatement(UPDATE_STMT);
			stmt.setString(1, report.getName());
			stmt.setString(2, report.getDescription());
			stmt.setString(3, report.getOwner().getId());
			stmt.setString(4, report.getId());
			stmt.execute();
			handleAssociations(report);
		} finally {
			cleanUp(stmt);
		}		
	}

	protected final void doInsert(DomainObject obj, PreparedStatement stmt)
			throws SQLException {
		Report report = (Report) obj;
		stmt.setString(1, report.getName());
		stmt.setString(2, report.getDescription());
		stmt.setString(3, report.getOwner().getId());
		handleAssociations(report);
	}

	protected final DomainObject doLoad(String id, ResultSet result)
			throws SQLException {
		Builder reportBuilder = new ReportImpl.Builder(id);
		reportBuilder.name(result.getString(2));
		reportBuilder.description(result.getString(3));
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		//load owner:
		reportBuilder.owner(
				(User) mapperReg.getUserManagement().find(result.getString(4)));
		//load associated roles: 
		IReportRoleManagement rrAssoc = mapperReg.getReportRoleAssociation();
		reportBuilder.roles(rrAssoc.getRoles(id));
		// load associated views:
		IReportViewManagement rvAssoc = mapperReg.getReportViewAssociation();
		reportBuilder.views(rvAssoc.getViews(id));
		return reportBuilder.build();

		
//		Report report = new Report(id, result.getString(2), connection);
//		report.setDescription(result.getString(3));
//		MapperRegistry mapperReg = MapperRegistry.getInstance(connection);
//		//load associated roles: 
//		IReportRoleManagement rrAssoc = mapperReg.getReportRoleAssociation();
//		report.internalSetRoles(rrAssoc.getRoles(report));
//		// load associated views:
//		IReportViewManagement rvAssoc = mapperReg.getReportViewAssociation();
//		report.internalSetViews(rvAssoc.getViews(report));
//		return report;
	}

	protected final void deleteAssociations(DomainObject obj) throws SQLException {
		Report report = (Report) obj;		
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		
		//delete associated roles
		IReportRoleManagement rrAssoc = mapperReg.getReportRoleAssociation();
		rrAssoc.delete(report);
		
		//delete associated views
		IReportViewManagement rvAssoc = mapperReg.getReportViewAssociation();
		rvAssoc.delete(report);
	}

	protected final String deleteStatement() {
		return DELETE_STMT;
	}

	protected final String findStatement() {
		return FIND_BY_ID_STMT;
	}

	protected final String findByNameStatement() {
		return FIND_BY_NAME_STMT;
	}

	protected final String insertStatement() {
		return INSERT_STMT;
	}
	protected final String createTableStatement() {
		return CREATE_TABLE_STMT;
	}

	protected final String getTableName() {
		return TABLE;
	}

	
	private final void handleAssociations(Report report) throws SQLException {
		//HANDLE ASSOCIATIONS:
		MapperRegistry mapperReg = MapperRegistry.getInstance();
		//insert associated roles: 
		IRoleManagement roleMgmt = mapperReg.getRoleManagement();
		IReportRoleManagement rrAssoc = mapperReg.getReportRoleAssociation();
		// remove any deleted ones:
		List<String> roles = ((ReportImpl)report).getRoleIDs();
		List<String> savedRoles = rrAssoc.getRoles(report);
		for (String id : roles) {
			if (!savedRoles.contains(id))
				rrAssoc.insert(report, roleMgmt.find(id));
		}
		// remove any deleted ones:
		savedRoles.removeAll(roles);
		for (String id : savedRoles)
			rrAssoc.delete(report, roleMgmt.find(id));
		
		
		// insert associated views:
		IViewManagement viewMgmt= mapperReg.getViewManagement();
		IReportViewManagement rvAssoc = mapperReg.getReportViewAssociation();
		// remove any deleted ones:
		List<String> views = ((ReportImpl)report).getViewIDs();
		List<String> savedViews = rvAssoc.getViews(report);
		for (String id : views) {
			if (!savedViews.contains(id))
				rvAssoc.insert(report, viewMgmt.find(id));
		}
		// remove any deleted ones:
		savedViews.removeAll(views);
		for (String id : savedViews)
			rvAssoc.delete(report, viewMgmt.find(id));

	}
}
