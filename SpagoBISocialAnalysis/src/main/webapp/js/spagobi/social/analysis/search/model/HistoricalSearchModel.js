/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Model representing a Search
 *
 *  @author
 *  Giorgio Federici (giorgio.federici@eng.it)
 */
 
  
Ext.define('Sbi.social.analysis.search.model.HistoricalSearchModel', {
	
	extend: 'Ext.data.Model',
	
	fields: [
	 		{name: 'searchID', type : 'int'},
	 		{name: 'label', type : 'string'},
	 		{name: 'keywords', type : 'string'},
	 		{name: 'lastActivationTime', type: 'date'},
	 		{name: 'loading', type: 'boolean'},
	 		{name: 'accounts', type : 'string'},
	 		{name: 'links', type : 'string'},
	 		{name: 'frequency', type : 'string'},
	 		{name: 'documents', type : 'string'},
	 		{name: 'hasSearchScheduler', type: 'boolean'},
	 		{name: 'hasMonitorScheduler', type: 'boolean'},
	 		{name: 'isFailed', type: 'boolean'}
	 	],
	 
	 proxy: {
	        type: 'rest',
	        url: 'restful-services/historicalSearch',
	        reader: {
	            type: 'json',
	            root: 'search'
	        },
	       
	    }    
	
});