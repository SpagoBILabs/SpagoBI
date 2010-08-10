/*
*
* @file XNode.java
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
* @version $Id: XNode.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.client.serialization;

import java.util.LinkedHashSet;
import java.util.Set;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;

/**
 * <code>XNode</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XNode.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class XNode extends XObject {

	private XUser user;
	private String type;
	
	private Set<XObject> children = new LinkedHashSet<XObject>();
	
	public XNode() {		
	}
	
	public XNode(XUser user, String type) {
		this.user = user;
		this.type = type;
	}
	
	
	public void addChild(XObject obj) {
		children.add(obj);
	}

	public XObject[] getChildren() {
		return children.toArray(new XObject[children.size()]);
	}
	
	public final String getType() {
		return type;
	}

	public final XUser getUser() {
		return user;
	}
	
	public void removeChild(XObject obj) {
		children.remove(obj);
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public final void setUser(XUser user) {
		this.user = user;
	}
	
	public int hashCode() {
		int hc = super.hashCode();
		hc += 17 * type.hashCode();
		if(user != null)
			hc += 23 * user.getId().hashCode();
		return hc;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof XNode) || o == null) {
			return false;
		}
		XNode n = (XNode) o;
		boolean equal = n.type.equals(type);
		if( user!= null)
			equal = equal && user.equals(n.user);
		else
			equal = equal && n.user == null;
		return equal;
	}
}
