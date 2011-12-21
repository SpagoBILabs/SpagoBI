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
package com.tensegrity.wpalo.server.childloader;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.server.services.UserSession;
import com.tensegrity.wpalo.client.serialization.XConstants;
import com.tensegrity.wpalo.client.serialization.XNode;

/**
 * <code>NodeChildLoader</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: NodeChildLoader.java,v 1.6 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class NodeChildLoader implements ChildLoader {

	public boolean accepts(XObject parent) {
		return parent.getType().equals(XConstants.TYPE_ROOT_NODE);
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {
		XNode node = (XNode) parent;
		return node.getChildren();
	}

}
