/*
*
* @file ReportServiceImpl.java
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
* @version $Id: ReportServiceImpl.java,v 1.6 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Report;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.services.ReportService;

/**
 * <code>ReportService</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ReportServiceImpl.java,v 1.6 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
final class ReportServiceImpl extends InternalService implements ReportService {

	ReportServiceImpl(AuthUser user) {
		super(user);
	}
	
	public final Report createReport(String name)
			throws OperationFailedException {
		AccessController.checkPermission(Right.CREATE, user);
		try {
			ReportImpl report = new ReportImpl(null);
			report.setName(name);
			report.setOwner(user);
			getReportManagement().insert(report);
			return report;
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to create report", e);
		}
	}

	public final void delete(Report report) throws OperationFailedException {
		AccessController.checkPermission(Right.DELETE, report, user);
		try {
			getReportManagement().delete(report);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to delete report", e);
		}
	}

	public final boolean doesReportExist(String name) {
		return getReportByName(name) != null;
	}
	
	public final void save(Report report) throws OperationFailedException {
		AccessController.checkPermission(Right.WRITE, report, user);		
		try {
			getReportManagement().update(report);
		} catch (SQLException e) {
			throw new OperationFailedException("Failed to save report", e);
		}
	}

	public final List<Report> getReports() {
		AccessController.checkPermission(Right.READ, user);
		IReportManagement reportMgmt = getReportManagement();
		Set<Report> reports = new HashSet<Report>();
		List<Role> roles = user.getRoles();
		for (Role role : roles) {
			try {
				// check role has at least read right...
				if (role.hasPermission(Right.READ))
					reports.addAll(reportMgmt.findReports(role));
			} catch (SQLException e) { /* ignore */
			}
		}
		//and we add all reports the user owns:
		try {
			reports.addAll(reportMgmt.findReports(user));
		} catch (SQLException e) { /* ignore */
		}
		return new ArrayList<Report>(reports);
	}

	public final Report getReport(String id) {
		AccessController.checkPermission(Right.READ, user);		
		try {
			return (Report) getReportManagement().find(id);
		} catch (SQLException e) {
			/* ignore, simply return null */
		}
		return null;
	}

	public final Report getReportByName(String name) {
		AccessController.checkPermission(Right.READ, user);
		try {
			return (Report) getReportManagement().findByName(name);
		} catch (SQLException e) {
			/* ignore, simply return null */
		}
		return null;
	}

	//ASSOCIATIONS:
	//view - report
	public final void add(View view, Report toReport)
			throws OperationFailedException {
		if (!toReport.contains(view)) {
			AccessController.checkPermission(Right.WRITE, toReport, user);
			ReportImpl report = (ReportImpl) toReport;
			report.add(view);
			save(report);
		}
	}

	public final void remove(View view, Report fromReport)
			throws OperationFailedException {
		if (fromReport.contains(view)) {
			AccessController.checkPermission(Right.WRITE, fromReport, user);
			ReportImpl report = (ReportImpl) fromReport;
			report.remove(view);
			save(report);
		}
	}
	//report - role
	public final void add(Role role, Report toReport)
			throws OperationFailedException {
		if (!toReport.hasRole(role)) {
			AccessController.checkPermission(Right.WRITE, toReport, user);
			ReportImpl report = (ReportImpl) toReport;
			report.add(role);
			save(report);
		}
	}
	public final void remove(Role role, Report fromReport)
			throws OperationFailedException {
		if (fromReport.hasRole(role)) {
			AccessController.checkPermission(Right.WRITE, fromReport, user);
			ReportImpl report = (ReportImpl) fromReport;
			report.remove(role);
			save(report);
		}
	}

//	protected final void doReset() {
//		getReportManagement().reset();
//	}

	public final void setDescription(String description, Report ofReport) {
		AccessController.checkPermission(Right.WRITE, ofReport, user);
		ReportImpl report = (ReportImpl) ofReport;
		report.setDescription(description);
	}

	public final void setName(String name, Report ofReport) {
		AccessController.checkPermission(Right.WRITE, ofReport, user);
		ReportImpl report = (ReportImpl) ofReport;
		report.setName(name);
	}

	public final void setOwner(User owner, Report ofReport) {
		AccessController.checkPermission(Right.WRITE, ofReport, user);
		ReportImpl report = (ReportImpl) ofReport;
		report.setOwner(owner);
	}
}
