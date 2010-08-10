/*
*
* @file LanguageData.java
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
* @version $Id: LanguageData.java,v 1.1 2010/03/02 08:59:12 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.ui.dialog;

public class LanguageData {
	String name;
	String id;
	
	LanguageData(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	public String toString() {
		return name;
	}
	
	public int hashCode() {
		return id.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof LanguageData)) {
			return false;
		}
		return ((LanguageData) o).id.equals(id);
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
}
