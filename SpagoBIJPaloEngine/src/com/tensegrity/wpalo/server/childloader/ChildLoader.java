/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.server.services.UserSession;

public interface ChildLoader {
	boolean accepts(XObject parent);
	XObject [] loadChildren(XObject parent, UserSession userSession);
}
