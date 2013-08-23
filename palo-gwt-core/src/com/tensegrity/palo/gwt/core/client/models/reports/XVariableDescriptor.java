/*
*
* @file XVariableDescriptor.java
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
* @version $Id: XVariableDescriptor.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.models.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XHierarchy;
import com.tensegrity.palo.gwt.core.client.models.subsets.XSubset;

public class XVariableDescriptor implements IsSerializable{
	private List <String> reportVariables;
	private HashMap <XObject, String> variableMapping;
	
	public XVariableDescriptor() {
		reportVariables = new ArrayList<String>();
		variableMapping = new HashMap<XObject, String>();	
	}
	
	public List <String> getReportVariables() {
		return reportVariables;
	}
	
	public HashMap<XObject, String> getVariableMapping() {
		return variableMapping;
	}
	
	public void addMapping(XHierarchy key, String value) {
		variableMapping.put(key, value);
	}
	
	public void addMapping(XSubset key, String value) {
		variableMapping.put(key, value);
	}

	public void addVariable(String var) {
		reportVariables.add(var);
	}
	
	public void setMapping(XObject [] keys, String [] values) {
		for (int i = 0; i < keys.length; i++) {
			variableMapping.put(keys[i], values[i]);
		}
	}
	
	public void setVariables(String [] vars) {
		for (String s: vars) {
			reportVariables.add(s);
		}
	}
	
	public void clearVariables() {
		reportVariables.clear();
	}
	
	public void clearVariableMapping() {
		variableMapping.clear();
	}	
}
