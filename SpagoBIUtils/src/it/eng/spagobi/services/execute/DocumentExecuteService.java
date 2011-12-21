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
