/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.writeback4j;

import org.olap4j.metadata.Member;

import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.writeback4j.mondrian.MondrianSchemaRetriver;
import it.eng.spagobi.writeback4j.sql.QueryBuilder;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 * Manager of the writeback.. 
 * Provides the methods to update the values in the db 
 */
public class WriteBackManager {
	QueryBuilder queryBulder;

	public WriteBackManager(String editCubeName, String olapSchema, IDataSource dataSource) throws SpagoBIEngineException {
		ISchemaRetriver retriver = new MondrianSchemaRetriver( olapSchema,  editCubeName);
		queryBulder = new QueryBuilder(retriver, dataSource);
		
	}
	
	public void executeProportionalUpdate(Member[] members, double prop) throws SpagoBIEngineException{
		queryBulder.executeProportionalUpdate(members, prop);
	} 
	
	
	
	
	
}
