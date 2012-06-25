/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.jpivotaddins.engines.jpivotxmla.connection;

/**
 * @author Andrea Gioia
 *
 */
public interface IConnection {
	public static int JNDI_CONNECTION = 1;
	public static int JDBC_CONNECTION = 2;
	public static int XMLA_CONNECTION = 3;
	
	public String getName();
	public int getType();
}
