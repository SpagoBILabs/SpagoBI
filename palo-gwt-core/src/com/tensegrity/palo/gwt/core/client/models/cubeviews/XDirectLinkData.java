/*
*
* @file XDirectLinkData.java
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
* @version $Id: XDirectLinkData.java,v 1.5 2010/04/12 11:14:14 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.models.cubeviews;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.tensegrity.palo.gwt.core.client.models.account.XAccount;

public class XDirectLinkData implements IsSerializable {
	private XView [] views;
	private List <Boolean> globalDisplayFlags;
	private List <Boolean> displayFlags;
	private List <String> errors = new ArrayList<String>();
	private boolean authenticated;
	private String userPassword;
	private String paloSuiteId;
	private XAccount xAccount;

	public XDirectLinkData() {
		authenticated = false;
	}
	
	public XDirectLinkData(XView [] views, List <Boolean> globalFlags) {
		this.views = views;
		this.globalDisplayFlags = globalFlags;
		this.authenticated = true;
	}
	
	public void setViews(XView [] views) {
		this.views = views;
	}
	
	public XView [] getViews() {
		return views;
	}
	
	public void setGlobalDisplayFlags(List <Boolean> flags) {
		globalDisplayFlags = flags;
	}
	
	public List <Boolean> getGlobalDisplayFlags() {
		return globalDisplayFlags;
	}
	
	public String [] getErrors() {
		return errors.toArray(new String[0]);
	}
	
	public void clearErrors() {
		errors.clear();
	}
	
	public void addError(String error) {
		errors.add(error);
	}
	
	public void setAuthenticated(boolean value) {
		authenticated = value;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public String getUserPassword() {
		return userPassword;
	}
	
	public void setUserPassword(String pass) {
		userPassword = pass;
	}
	
	public String getPaloSuiteViewId() {
		return paloSuiteId;
	}
	
	public void setPaloSuiteViewId(String id) {
		paloSuiteId = id;
	}
	
	public void setXAccount(XAccount account) {
		xAccount = account;
	}
	
	public XAccount getAccount() {
		return xAccount;
	}
	
	public void setDisplayFlags(List <Boolean> df) {
		this.displayFlags = df;
	}
	
	public List <Boolean> getDisplayFlags() {
		return displayFlags;
	}
}
