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
 
  
Ext.define('Sbi.social.analysis.search.model.StreamingSearchModel', {
	
	extend: 'Ext.data.Model',
	
	fields: [
	 		{name: 'searchID', type : 'int'},
	 		{name: 'label', type : 'string'},
	 		{name: 'keywords', type : 'string'},
	 		{name: 'lastActivationTime', type: 'date'},
	 		{name: 'loading', type: 'boolean'},
	 		{name: 'accounts', type : 'string'},
	 		{name: 'links', type : 'string'},
	 		{name: 'hasMonitorScheduler', type: 'boolean'},
	 		{name: 'documents', type : 'string'}
	 	],
	 
	 proxy: {
	        type: 'rest',
	        url: 'restful-services/streamingSearch',
	        reader: {
	            type: 'json',
	            root: 'search'
	        }
	    }    
	
});