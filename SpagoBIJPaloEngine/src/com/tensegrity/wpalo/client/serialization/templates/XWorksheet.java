/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client.serialization.templates;

import com.tensegrity.palo.gwt.core.client.models.XObject;

public class XWorksheet extends XObject {
	private XWorkbook workbook;
	
	public XWorksheet() {		
	}
	
	public XWorksheet(String id, String name, XWorkbook workbook) {
		setId(id);
		setName(name);
		this.workbook = workbook;
	}
	
	public String getType() {
		return getClass().getName();
	}
	
	public XWorkbook getWorkbook() {
		return workbook;
	}
			
    public boolean equals(Object o) {
        if(o == null || !(o instanceof XWorksheet))
            return false;
        XWorksheet ws = (XWorksheet) o;
        String id = ws.getId();
        XWorkbook wb = ws.workbook;
        return getId() != null ? getId().equals(id) : id == null &&
        	   workbook != null ? workbook.equals(wb) : wb == null;
    }

    public int hashCode() {
        String id = getId();
        return id == null ? 0 : id.hashCode() + 13 * (workbook == null ? 0 : workbook.hashCode());
    }
}
