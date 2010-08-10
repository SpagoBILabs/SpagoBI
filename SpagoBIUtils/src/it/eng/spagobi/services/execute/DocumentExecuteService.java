/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.services.execute;

import java.util.HashMap;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public interface DocumentExecuteService {
    /**
     *  Return the IMAGE of a chart
     *  The primary goal is to integrate a chart in a JasperReport
     * @param token
     * @param user
     * @param document
     * @param parameters
     * @return
     */
    byte[] executeChart(String token,String user,String document,HashMap parameters);
    
    /**
     *  Returns the XML DATA of the Kpi value with id kpiValueID
     * @param token
     * @param user
     * @param kpiValueID
     * @return
     */
    String getKpiValueXML(String token, String user,Integer kpiValueID);

}
