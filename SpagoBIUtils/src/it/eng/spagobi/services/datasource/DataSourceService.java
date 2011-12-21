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
package it.eng.spagobi.services.datasource;

import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;

/**
 * @author Bernabei Angelo
 *
 */
public interface DataSourceService {

    /**
     * 
     * @param token  String
     * @param user String
     * @param documentId String
     * @return SpagoBiDataSource
     */
    SpagoBiDataSource getDataSource(String token,String user,String documentId);
    /**
     * 
     * @param token  String
     * @param user String
     * @param label String
     * @return SpagoBiDataSource
     */
    SpagoBiDataSource getDataSourceByLabel(String token,String user,String label);    
    /**
     * 
     * @param token String
     * @param user String
     * @return SpagoBiDataSource[]
     */
    SpagoBiDataSource[] getAllDataSource(String token,String user);
}
