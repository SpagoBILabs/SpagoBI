/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPIVOT.LICENSE.txt file
 * 
 */
package it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection;

import it.eng.spago.base.SourceBean;

/**
 * @author Andrea Gioia
 *
 */
public class XMLAConnection implements IConnection {
	private String name;
	private int type;
	private String xmlaServerUrl;
	
	public XMLAConnection(SourceBean connSb) {
		name = (String)connSb.getAttribute("name");
		type = XMLA_CONNECTION;
		xmlaServerUrl = (String)connSb.getAttribute("xmlaServerUrl");
	}

	public String getName() {
		return name;
	}

	public String getXmlaServerUrl() {
		return xmlaServerUrl;
	}

	public int getType() {
		return type;
	}
}
