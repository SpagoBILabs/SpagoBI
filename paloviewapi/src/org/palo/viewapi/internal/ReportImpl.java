/*
*
* @file ReportImpl.java
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
* @version $Id: ReportImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
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

import org.palo.viewapi.Report;
import org.palo.viewapi.User;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;

/**
 * <code>Report</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ReportImpl.java,v 1.4 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public final class ReportImpl extends GuardedObjectImpl implements Report {

	private String name;
	private String description;
	private final Set<String> views = new HashSet<String>();
	
	ReportImpl(String id) {
		this(id,null);
	}

	ReportImpl(String id, String name) {
		super(id);
		this.name = name;		
	}

	private ReportImpl(Builder builder) {
		super(builder.id);
		name = builder.name;
		description = builder.description;
		owner = builder.owner;
		views.addAll(builder.views);
		setRoles(builder.roles);
	}
	

	public final String getDescription() {
		return description;
	}

	public final String getName() {
		return name;
	}
	
	public final List<View> getViews() {
		IViewManagement viewMgmt = 
			MapperRegistry.getInstance().getViewManagement();
		List<View> views = new ArrayList<View>();
		for(String id : this.views) {
			try {
				View view = (View) viewMgmt.find(id);
				if (view != null && !views.contains(view))
					views.add(view);
			} catch (SQLException e) {
				/* ignore it */
			}
		}
		return views;
	}
	public final List<String> getViewIDs() {
		return new ArrayList<String>(views);
	}
	public final boolean contains(View view) {
		return views.contains(view.getId());
	}
	

	//--------------------------------------------------------------------------
	// INTERNAL API
	//
	final void setDescription(String description) {
		this.description = description;
	}
	final void add(View view) {
		views.add(view.getId());
	}
	final void remove(View view) {
		views.remove(view.getId());
	}
	final void setName(String name) {
		this.name = name;
	}
	final void setViews(List<String> views) {
		this.views.clear();
		if(views != null) {
			this.views.addAll(views);
		}
	}

	/** 
	 * static builder class
	 * <b>NOTE: not for external usage! </b>
	 */
	public static final class Builder {
		private final String id;
		private String name;
		private String description;
		private User owner;
		private final Set<String> roles = new HashSet<String>();
		private final Set<String> views = new HashSet<String>();
		
		public Builder(String id) {
			AccessController.checkAccess(Report.class);
			this.id = id;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		public Builder owner(User owner) {
			this.owner = owner;
			return this;
		}
		public Builder roles(List<String> roles) {
			this.roles.clear();
			if(roles != null)
				this.roles.addAll(roles);
			return this;
		}
		public Builder views(List<String> views) {
			this.views.clear();
			if(views != null)
				this.views.addAll(views);
			return this;
		}
		public Report build() {
			return new ReportImpl(this);
		}
	}

}
