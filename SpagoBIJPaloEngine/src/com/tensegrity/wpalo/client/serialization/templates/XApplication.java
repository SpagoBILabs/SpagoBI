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
