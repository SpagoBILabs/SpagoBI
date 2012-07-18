/*
*
* @file XTemplate.java
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
* @version $Id: XTemplate.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.serialization.templates;

import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.XObject;

public class XTemplate extends XObject {
	private XUser user;
	private XAccount account;
	
	public XTemplate() {		
	}
	
	public XTemplate(String id, String name, XUser user, XAccount account) {
		setId(id);
		setName(name);
		this.user = user;
		this.account = account;
	}
	
	public String getType() {
		return getClass().getName();
	}
	
	public XAccount getAccount() {
		return account;
	}
	
	public void setAccount(XAccount acc) {
		this.account = acc;
	}
	
	public XUser getUser() {
		return user;
	}
	
	public void setUser(XUser user) {
		this.user = user;
	}
	
    public boolean equals(Object o) {
    	if(o == null || !(o instanceof XTemplate)) {
            return false;
    	}
    	XTemplate t = (XTemplate) o;
        String id = t.getId();
        XAccount acc = t.account;
        boolean ret = getId() != null ? getId().equals(id) : id == null &&
        	   account != null ? account.getId().equals(acc.getId()) : t.account == null;
        return ret;
    }

    public int hashCode() {
        String id = getId();
        return id == null ? 0 : id.hashCode() + 3 * (account == null ? 0 : account.hashCode());
    }
}
