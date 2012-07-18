/*
*
* @file XApplication.java
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
* @version $Id: XApplication.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.serialization.templates;

import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.XObject;

public class XApplication extends XObject {
	private XAccount account;
	private boolean loadWBs;
	private boolean loadTemps;
	
	public XApplication() {		
	}
	
	public XApplication(XUser user, XAccount account, String id, boolean loadWBs, boolean loadTemps) {
//		super(user, XConstants.TYPE_WSS_APPLICATION);
		setId(id);
		this.account = account;		
		this.loadWBs = loadWBs;
		this.loadTemps = loadTemps;
	}
	
	public XAccount getAccount() {
		return account;
	}	
	
//	public XUser getUser() {
//		return account.getUser();
//	}
	
	public boolean loadWorkbooks() {
		return loadWBs;
	}
	
	public boolean loadTemplates() {
		return loadTemps;
	}
	
	public String getType() {
		return getClass().getName();
	}
	
	public int hashCode() {
		return 7 * (getId() == null ? 1 : getId().hashCode()) + 13 * getType().hashCode() + 11 * account.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof XApplication)) {
			return false;
		}
		XApplication a = (XApplication) o;
		if (getId() == null) {
			boolean ret =a.getId() == null && 
		       getType().equals(a.getType()) &&
		       account.equals(a.account);
			return ret;
		}
		
		boolean ret = getId().equals(a.getId()) &&
		getType().equals(a.getType()) &&
		account.equals(a.account);
		return ret;
	}		
}
