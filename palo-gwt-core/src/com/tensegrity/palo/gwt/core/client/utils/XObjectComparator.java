/*
*
* @file XObjectComparator.java
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
* @version $Id: XObjectComparator.java,v 1.2 2009/12/17 16:14:30 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.client.utils;

import java.util.Comparator;

import com.tensegrity.palo.gwt.core.client.models.XObject;

public class XObjectComparator implements Comparator <XObject> {
	private boolean ignoreCase;
	
	public XObjectComparator() {
		this(true);
	}
	
	public XObjectComparator(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	
	public int compare(XObject o1, XObject o2) {
		if (o1 == null) {
			return o2 == null ? 0 : -1;
		}
		if (o2 == null) {
			return 1;
		}
		if (ignoreCase) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		} else {
			return o1.getName().compareTo(o2.getName());
		}
	}
}
