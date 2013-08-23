/*
*
* @file XView.java
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
* @version $Id: XView.java,v 1.10 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XObject;

public class XView extends XObject {

	public static final String TYPE = XView.class.getName();
//	private XCube cube;
	private String accountId;
	private String databaseId;
	private String cubeId;
	private String definition;
	private String externalId;
	private String ownerId;
	private List <String> roleIds;
	private List <String> roleNames;
	private List <Boolean> displayFlags;
	
	public XView() {		
	}
	
	public XView(String id, String name) {
		setId(id);
		setName(name);
//		this.cube = cube;
	}
	
	public String getType() {
		return TYPE;
	}

	
//	public final XCube getCube() {
//		return cube;
//	}
//

	public final String getDefinition() {
		return definition;
	}

	public final void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getAccountId() {
		return accountId; //cube.getAccountId();
	}
	public void setAccountId(String id) {
		accountId = id;
	}

	public String getCubeId() {
		return cubeId;
	}
	public void setCubeId(String id) {
		cubeId = id;
	}

	public String getDatabaseId() {
		return databaseId; //cube.getDatabaseId();
	}
	public void setDatabaseId(String id) {
		databaseId = id;
	}
	public void setDisplayFlags(List <Boolean> flags) {
		displayFlags = flags;
	}
	public List<Boolean> getDisplayFlags() {
		return displayFlags;
	}
	
	public void setExternalId(String exId) {
		this.externalId = exId;
	}
	
	public String getExternalId() {
		return externalId;
	}
	
	public void setOwnerId(String owner) {
		ownerId = owner;
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setRoleIds(List <String> ids) {
		roleIds = ids;
	}
	
	public void setRoleNames(List <String> roleNames) {
		this.roleNames = roleNames;
	}
	
	public List <String> getRoleIds() {
		return roleIds;
	}
	
	public boolean containsRoleName(String r) {
		return roleNames != null && roleNames.contains(r);
	}
	
	public List <String> getRoleNames() {
		return roleNames;
	}
}
