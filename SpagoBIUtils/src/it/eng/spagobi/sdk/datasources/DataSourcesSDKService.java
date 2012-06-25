/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.sdk.datasources;

import it.eng.spagobi.sdk.datasources.bo.SDKDataSource;
import it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;


public interface DataSourcesSDKService {
	
	SDKDataSource[] getDataSources() throws NotAllowedOperationException;
	
	SDKDataSource getDataSource(Integer dataSourceId) throws NotAllowedOperationException;
		
}
