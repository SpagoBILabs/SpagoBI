/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
