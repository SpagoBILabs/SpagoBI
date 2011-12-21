/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
