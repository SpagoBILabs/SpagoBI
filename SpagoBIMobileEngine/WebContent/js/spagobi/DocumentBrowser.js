/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
Ext.define('app.views.DocumentBrowser',{
		extend:'Ext.NestedList',
		
		config:{
		    scroll: 'vertical',
		    flex:1,
		    title: 'Document Browser',
		    store: new Ext.data.TreeStore({
			    model: 'browserItems',
			    proxy: {
			    	type: 'ajax',
			    	reader:{
						type: 'json',
						rootProperty: 'samples'
			    	},
					url: Sbi.config.serviceRegistry.getServiceUrl({
						serviceName: 'DOCUMENT_BROWSER_ACTION'
							, baseParams: {LIGHT_NAVIGATOR_DISABLED: 'TRUE'}
						})
			           

			    }
		    })
//,
//			getItemTextTpl: function(node) {
//				var tplTxt = '<tpl if="typeCode == \'' + Sbi.constants.documenttype.report + '\'">'+
//		        '<div class="table-item">{name}</div>'+
//	    	    '</tpl>'+
//	    	    '<tpl if="typeCode == \'' + Sbi.constants.documenttype.chart + '\'">'+
//		        	'<div class="chart-item">{name}</div>'+
//		        '</tpl>'+
//	    	    '<tpl if="typeCode == \'' + Sbi.constants.documenttype.cockpit + '\'">'+
//			        '<div class="composed-item">{name}</div>'+
//			    '</tpl>'+
//			    '<tpl if="typeCode == undefined || typeCode == null || typeCode ==\'\'">'+
//			        '<div class="navigate">{name}</div>'+
//			    '</tpl>';
//			    return tplTxt;
//			}
//		    getDetailCard: function( record, parentRecord ){
//				Ext.dispatch(
//	            {
//	              controller: app.controllers.mobileController,
//	              action: 'showDetail',
//	              record: record
//	              
//	            });
//				//direct execution: no preview
//				var rec = record.attributes.record.data;
//				  Ext.dispatch({
//					  controller: app.controllers.mobileController,
//					  action: 'getRoles',
//					  id: rec.id,
//					  label: rec.label, 
//					  engine: rec.engine, 
//					  typeCode: rec.typeCode
//				  });
//
//	        }
		},



	reloadPanel: function(){
		var store = this.getStore();
		store.load();
		
	},
		
        getTitleTextTpl: function() {
            return '<tpl><div>{name}</div></tpl>';
        },
        
		getItemTextTpl: function(node) {
		var tplTxt = '<tpl if="typeCode == \'' + Sbi.constants.documenttype.report + '\'">'+
	        '<div class="table-item">{name}</div>'+
		    '</tpl>'+
		    '<tpl if="typeCode == \'' + Sbi.constants.documenttype.chart + '\'">'+
	        	'<div class="chart-item">{name}</div>'+
	        '</tpl>'+
		    '<tpl if="typeCode == \'' + Sbi.constants.documenttype.cockpit + '\'">'+
		        '<div class="composed-item">{name}</div>'+
		    '</tpl>'+
		    '<tpl if="typeCode == undefined || typeCode == null || typeCode ==\'\'">'+
		        '<div class="navigate">{name}</div>'+
		    '</tpl>';
		    return tplTxt;
		}



		
});