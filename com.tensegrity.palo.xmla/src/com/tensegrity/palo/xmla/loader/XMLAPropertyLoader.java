/*
*
* @file XMLAPropertyLoader.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: XMLAPropertyLoader.java,v 1.5 2009/04/29 10:35:38 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.loader;

import java.util.HashSet;
import java.util.Set;

import com.tensegrity.palo.xmla.XMLAConnection;
import com.tensegrity.palo.xmla.XMLAPaloInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.loader.PropertyLoader;

public class XMLAPropertyLoader extends PropertyLoader {
	private final PaloInfo paloObject;
	
	public XMLAPropertyLoader(DbConnection paloConnection) {
		super(paloConnection);
		paloObject = null;
	}
	
	public XMLAPropertyLoader(DbConnection paloConnection, PaloInfo paloObject) {
		super(paloConnection);
		this.paloObject = paloObject;
	}
		
	public String[] getAllPropertyIds() {
		Set <String> allIds = new HashSet <String> ();		
		String [] ids = new String[0];
		if (paloObject == null) {
			ids = ((XMLAConnection) paloConnection).getAllKnownPropertyIds();
		} else {
			if (paloObject instanceof XMLAPaloInfo) {
				ids = ((XMLAPaloInfo) paloObject).getAllKnownPropertyIds(paloConnection);
			}
		}
		allIds.addAll(loadedInfo.keySet());
		for (String id: ids) {
			allIds.add(id);
		}
		
		return allIds.toArray(new String[0]);
	}
	
	public PropertyInfo load(String id) {
		PaloInfo property = loadedInfo.get(id);
		if (property == null) {
			if (paloObject == null) {
				property = paloConnection.getProperty(id);
			} else {
				if (paloObject instanceof XMLAPaloInfo) {
					property = ((XMLAPaloInfo) paloObject).getProperty(paloConnection, id);
				}
			}
			loaded(property);
		}
		return (PropertyInfo) property;
	}	

	protected void reload() {
		// TODO Auto-generated method stub		
	}
}
