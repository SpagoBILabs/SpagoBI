/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.serialization.templates;

import com.tensegrity.palo.gwt.core.client.models.account.XAccount;
import com.tensegrity.palo.gwt.core.client.models.XObject;

public class XWorkbook extends XObject {
	private String defaultWorksheet;
	private XApplication application;
	
	public XWorkbook() {		
	}
	
	public XWorkbook(String id, String name, XApplication app, String worksheet) {
		setId(id);
		setName(name);
		application = app;
		defaultWorksheet = worksheet;
	}
	
	public String getType() {
		return getClass().getName();
	}
	
	public String getAppName() {
		return application.getName();
	}
	
	public XApplication getApplication() {
		return application;
	}
	
	public String getWorksheetName() {
		return defaultWorksheet;
	}
	
	public XAccount getAccount() {
		return application.getAccount();
	}
	
    public boolean equals(Object o) {
        if(o == null || !(o instanceof XWorkbook))
            return false;
        XWorkbook wb = (XWorkbook) o;
        String id = wb.getId();
        XApplication app = wb.application;
        return getId() != null ? getId().equals(id) : id == null &&
        	   application != null ? application.getId().equals(app.getId()) : wb.application == null;
    }

    public int hashCode() {
        String id = getId();
        return id == null ? 0 : id.hashCode() + 3 * (application == null ? 0 : application.hashCode());
    }
}
