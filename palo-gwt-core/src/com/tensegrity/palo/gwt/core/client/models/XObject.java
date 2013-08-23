/*
*
* @file XObject.java
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
* @version $Id: XObject.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.client.models;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <code>Xobject</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XObject.java,v 1.4 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
public abstract class XObject implements IsSerializable {
	private String id;
	private String name;
	private boolean hasKids;
	
	/**
	 * <b>Convention:</b> should return <code>XObject.class.getName()</code>
	 */
	public abstract String getType();
		
	public String getId() {
		return id;
	}
	
	public void setId(String newId) {
		id = newId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public void setHasChildren(boolean hasKids) {
		this.hasKids = hasKids;
	}
	
	public boolean hasChildren() {
		return hasKids;
	}
	
	public String toString() {
		return "ng XObject [" + id + ", " + name + "]";
	}	
	
	public int hashCode() {
		return 7 * (id == null ? 0 : id.hashCode()) + 
		       13 * (getType() == null ? 0 : getType().hashCode());
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof XObject) {
			XObject ot = (XObject) o;
			boolean eq = (id == null ? ot.id == null : id.equals(ot.id));
			eq &= (getType() == null ? ot.getType() == null : getType().equals(ot.getType()));
			return eq;
		}
		return false;
	}	
}
