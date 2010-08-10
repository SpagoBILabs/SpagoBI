/**
 * 
 * LICENSE: see LICENSE.html file
 * 
 */
package it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection;

import it.eng.spago.base.SourceBean;

/**
 * @author Andrea Gioia
 * 
 * 
 *
 */
public class JNDIConnection implements IConnection {
	private String name;
	private int type;
	private String initialContext;
	private String resourceName;
	
	public JNDIConnection(SourceBean connSb) {
		name = (String)connSb.getAttribute("name");
		type = JNDI_CONNECTION;
		initialContext = (String)connSb.getAttribute("initialContext");
		resourceName = (String)connSb.getAttribute("resourceName");
	}

	public String getName() {
		return name;
	}
	
	public String getInitialContext() {
		return initialContext;
	}

	public String getResourceName() {
		return resourceName;
	}

	public int getType() {
		return type;
	}
}