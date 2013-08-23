/*
*
* @file ReportService.java
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
* @version $Id: ReportService.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.services;

import java.util.List;

import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Report;
import org.palo.viewapi.Right;
import org.palo.viewapi.Role;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.exceptions.OperationFailedException;

/**
 * The <code>ReportService</code> interface defines methods to create and change
 * {@link Report} instances.
 *
 * @version $Id: ReportService.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface ReportService extends Service {

	/** 
	 * Creates a new {@link Report} with the given name. Note that the name 
	 * must be unique, i.e. there must be no other report with the same name. 
	 * @param name the report name
	 * @return the new <code>Report</code> instance
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to create a new report
	 * @throws OperationFailedException if the creation of the new report fails
	 */
	public Report createReport(String name) throws OperationFailedException;
	/** 
	 * Returns the {@link Report} with the given id or <code>null</code> if 
	 * none exists.
	 * @return the corresponding <code>Report</code> or <code>null</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any reports
	 */
	public Report getReport(String id);
	/** 
	 * Returns the {@link Report} with the given name or <code>null</code> if 
	 * none exists.
	 * @return the corresponding <code>Report</code> or <code>null</code>
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any reports
	 */
	public Report getReportByName(String name);	
	//TODO really provide this? how can we check that a report with a certain 
	//name exists already? boolean reportExists(String withName) ??
	
	
	/** 
	 * Returns all {@link Report}s which can be accessed by the current user of
	 * this service. <b>Note:</b> reports which failed to load are simply 
	 * ignored and therefore not within returned list.
	 * @return all <code>Report</code> which can be accessed by the current user
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to read any reports
	 */
	public List<Report> getReports();
	//TODO should we pass an observer which get notified about failed report??
	
	
	/**
	 * Saves the given {@link Report}.
	 * @param report the <code>Report</code> to save
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given report
	 * @throws OperationFailedException if saving of given report fails
	 */
	public void save(Report report) throws OperationFailedException;
	/**
	 * Deletes the given {@link Report}.
	 * @param report the <code>Report</code> to delete
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to delete the given report
	 * @throws OperationFailedException if deletion of given report fails
	 */
	public void delete(Report report) throws OperationFailedException;
	

	//UPDATES
	/**
	 * Sets the new name of the given {@link Report}.
	 * <b>Note:</b> the change is not persistent until the report is saved
	 * @param name the new report name
	 * @param ofReport the report to change
	 */
	public void setName(String name, Report ofReport);
	/**
	 * Sets the new name of the given {@link Report}.
	 * <b>Note:</b> the change is not persistent until the report is saved
	 * @param description the new report description
	 * @param ofReport the report to change
	 */
	public void setDescription(String description, Report ofReport);
	/**
	 * Sets the new owner of the given {@link Report}.
	 * <b>Note:</b> the change is not persistent until the report is saved
	 * @param owner the new report owner
	 * @param ofReport the report to change
	 */
	public void setOwner(User owner, Report ofReport);
	
	//VIEW-REPORT ASSOCIATION:
	/**
	 * Adds the given {@link View} to the specified {@link Report}. If the view
	 * was already added before calling this method has no effect.
	 * @param view the view to add
	 * @param toReport the report to change
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given report
	 * @throws OperationFailedException if modification of given report fails
	 */
	public void add(View view, Report toReport) throws OperationFailedException;
	/**
	 * Removes the given {@link View} from the specified {@link Report}. If the 
	 * report does not contain the given view calling this method has no effect.
	 * @param view the view to remove
	 * @param fromReport the report to change
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given report
	 * @throws OperationFailedException if modification of given report fails
	 */
	public void remove(View view, Report fromReport) throws OperationFailedException;
	
	//REPORT-ROLE ASSOCIATION:
	/**
	 * Adds the given {@link Role} to the specified {@link Report}. If the role
	 * was already added before calling this method has no effect.
	 * @param role the role to add
	 * @param toReport the report to change
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given report
	 * @throws OperationFailedException if modification of given report fails
	 */
	public void add(Role role, Report toReport) throws OperationFailedException;
	/**
	 * Removes the given {@link Role} from the specified {@link Report}. If the 
	 * report does not contain the given role calling this method has no effect.
	 * @param role the role to remove
	 * @param fromReport the report to change
	 * @throws NoPermissionException if the calling {@link AuthUser} has not 
	 * enough {@link Right}s to modify the given report
	 * @throws OperationFailedException if modification of given report fails
	 */
	public void remove(Role role, Report fromReport) throws OperationFailedException;

}
