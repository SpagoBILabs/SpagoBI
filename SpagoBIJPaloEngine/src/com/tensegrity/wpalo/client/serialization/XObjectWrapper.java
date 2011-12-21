/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.wpalo.client.serialization;

import com.tensegrity.palo.gwt.core.client.models.XObject;

/**
 * <code>XObjectWrapper</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: XObjectWrapper.java,v 1.2 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class XObjectWrapper extends XObject {
	
	//wrapped object:
	private XObject xObject;
	private String type;
	
	public XObjectWrapper() {
	}

	public XObjectWrapper(XObject xObject) {
		this.xObject = xObject;
		setId(xObject.getId());
		setName(xObject.getName());
	}
	
	public final XObject getXObject() {
		return xObject;
	}
	public final String getType() {
		return type;
	}
	public final void setType(String type) {
		this.type = type;
	}

}
