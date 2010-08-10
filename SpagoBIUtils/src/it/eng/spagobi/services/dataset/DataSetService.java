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
package it.eng.spagobi.services.dataset;

import it.eng.spagobi.services.dataset.bo.SpagoBiDataSet;

/**
 * @author Angelo Bernabei
 *         angelo.bernabei@eng.it
 */
public interface DataSetService {

    /**
     * 
     * @param token  String
     * @param user String
     * @param datasetId String
     * @return SpagoBiDataSet
     */
    SpagoBiDataSet getDataSet(String token,String user,String datasetId);
    /**
     * 
     * @param token  String
     * @param user String
     * @param label String
     * @return SpagoBiDataSet
     */
    SpagoBiDataSet getDataSetByLabel(String token,String user,String label);    
    /**
     * 
     * @param token String
     * @param user String
     * @return SpagoBiDataSet[]
     */
    SpagoBiDataSet[] getAllDataSet(String token,String user);
}
