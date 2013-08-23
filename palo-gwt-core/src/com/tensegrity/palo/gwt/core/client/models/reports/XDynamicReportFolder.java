/*
*
* @file XDynamicReportFolder.java
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
* @version $Id: XDynamicReportFolder.java,v 1.2 2009/12/17 16:14:30 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.models.reports;

import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.palo.XHierarchy;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;

public class XDynamicReportFolder extends XReportFolder {
	
	public static final String TYPE = XDynamicReportFolder.class.getName();
	
	private XHierarchy hierarchy;
	private XSubset subset;
	private XUser user;
	
	public XDynamicReportFolder() {		
	}
	
	public XDynamicReportFolder(String name, String id, boolean hasKids, boolean calculated, XHierarchy hierarchy, XSubset subset, XUser user) {
		setName(name);
		setId(id);
		setHasChildren(hasKids);
		returnComputedKids = calculated;
		this.hierarchy = hierarchy;
		this.subset = subset;
		this.user = user;
	}
		
	public String getType() {
		return TYPE;
	}
	
	public XSubset getSourceSubset() {
		return subset;
	}
	
	public void setSourceSubset(XSubset subset) {
		this.subset = subset;
	}
	
	public XHierarchy getSourceHierarchy() {
		return hierarchy;
	}
	
	public void setSourceHierarchy(XHierarchy hier) {
		hierarchy = hier;
	}
	
	public XUser getUser() {
		return user;
	}
	
	public void setUser(XUser user) {
		this.user = user;
	}
}
