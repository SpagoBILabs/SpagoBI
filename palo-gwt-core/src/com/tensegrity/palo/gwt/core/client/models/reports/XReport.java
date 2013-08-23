/*
*
* @file XReport.java
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
* @version $Id: XReport.java,v 1.2 2009/12/17 16:14:30 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.models.reports;

import java.util.ArrayList;
import java.util.List;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.palo.XElement;

public class XReport extends XObject {
	
	public static final String TYPE = XReport.class.getName();
	
	private String receiverType;
	private List <XElement> elements;
	
	public XReport() {		
	}
	
	public XReport(String name, String id, String receiverType, XElement [] elements) {
		setName(name);
		setId(id);
		setHasChildren(false);
		this.receiverType = receiverType;
		this.elements = new ArrayList<XElement>();
		if (elements != null) {
			for (XElement e: elements) {
				this.elements.add(e);
			}
		}
	}

	public String getType() {
		return TYPE;
	}
	
	public String getReceiverType() {
		return receiverType;
	}	
	
	public XElement [] getElements() {
		return elements.toArray(new XElement[0]);
	}
}
