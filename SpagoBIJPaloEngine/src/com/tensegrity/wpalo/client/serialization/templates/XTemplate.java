/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
