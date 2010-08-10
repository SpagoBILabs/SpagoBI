/*
*
* @file XWorksheet.java
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
* @version $Id: XWorksheet.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
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
