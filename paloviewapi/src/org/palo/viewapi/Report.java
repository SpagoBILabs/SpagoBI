/*
*
* @file Report.java
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
* @version $Id: Report.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

import java.util.List;


/**
 * <code>Report</code>
 * <p>
 * A report is simply a combination of one or more {@link View}s.
 * </p>
 *
 * @version $Id: Report.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public interface Report extends GuardedObject {

	/**
	 * Returns the name of this report. 
	 * @return the report name
	 */
	public String getName();
	/**
	 * Returns the report description or <code>null</code> if no description
	 * was added.
	 * @return the report description
	 */
	public String getDescription();
	/**
	 * Returns all {@link View}s which belongs to this report.
	 * @return a list of all views this report consists of
	 */
	public List<View> getViews();
	/**
	 * Checks if the given {@link View} is part of this report.
	 * @param view a view instance
	 * @return <code>true</code> if the given view is part of this report,
	 * <code>false</code> otherwsie
	 */
	public boolean contains(View view);
}
