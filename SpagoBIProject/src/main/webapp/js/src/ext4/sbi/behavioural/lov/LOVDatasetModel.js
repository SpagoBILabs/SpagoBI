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
	"Sbi.behavioural.lov.DatasetModel", 
		
	{
		extend: 'Ext.data.Model',
		
		fields: 	
		[							 
	         {name: 'id',			type: 'number'},
	         {name: 'name',     	type: 'string'},
	         {name: 'description', 	type: 'string'},
	         {name: 'label', 		type: 'string'} 
         ],
         
         idProperty: "id",
    
         proxy: 
         {
	        type: 'rest',
	        
	        url: 	Sbi.config.serviceRegistry.getRestServiceUrl({serviceName: '1.0/datasets'}),
	        
	        appendId: false,
	        
	        reader: 
	        {
	            type: 'json',
	            root: "root"
	        }
         }
	}
);