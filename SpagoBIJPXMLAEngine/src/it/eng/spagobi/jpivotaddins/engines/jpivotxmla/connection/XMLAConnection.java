/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
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
