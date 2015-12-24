/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Danilo Ristovski (danilo.ristovski@mht.net)
 */
 
Ext.define
(
	"Sbi.behavioural.lov.LOVModel", 
		
	{
		extend: 'Ext.data.Model',
			
		fields: 	
		[							 
	         {name: 'LOV_ID',			type: 'number'},
	         {name: 'LOV_LABEL',     	type: 'string'},
	         {name: 'LOV_NAME', 		type: 'string'},
	         {name: 'LOV_DESCRIPTION', 	type: 'string'},	         
	         {name: 'LOV_PROVIDER',    	type: 'string'},
	         {name: 'I_TYPE_CD',     	type: 'string'},
	         {name: 'I_TYPE_ID',      	type: 'string'},	         
	         {name: 'SELECTION_TYPE',   type: 'string'}   
         ],
         
         idProperty: "LOV_ID",
    
         proxy: 
         {
	        type: 'rest',
	        url: Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: 'LOV'}),
	        
	        appendId: false,
	        
	        reader: 
	        {
	            type: 'json'
	        }
         }
	}
);