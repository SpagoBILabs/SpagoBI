/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
  

/**
 * Object name 
 * 
 * 
 * Public Properties
 * 
  * MANDATORY PARAMETERS: serviceUrl: the url for the ajax request
  * OPTIONAL:
  * 	pagingConfig:{} Object. If this object is defined the paging toollbar will be displayed
 * 
 * 
 * Public Methods
 * 
 *  [list]
 * 
 * 
 * Public Events
 * 
 *  [list]
 * 
 * Authors
 * 
 * - Alberto Ghedin (alberto.ghedin@eng.it)
 */

Ext.define('Sbi.behavioural.lov.TestLovConfigurationGridPanel', {
    extend: 'Ext.grid.Panel'

    ,config: {
      	stripeRows: true
    }

	, constructor: function(config) {
		this.title =  "Fields";
		//this.border = false;
		this.viewConfig = {
			plugins: {
				ddGroup: 'GridLovDD',
				ptype: 'gridviewdragdrop',
				enableDrop: false
			}
		};
			
		
		console.log('TestLovConfigurationGridPanel costructor IN');
		Ext.apply(this,config);
		
    	//this.width = 200;
		this.flex = 1;
    	this.height = 200;
		
		this.store  = Ext.create('Ext.data.Store', {
		    fields: ['name', 'isValue', 'isDescription', 'isVisible'],
		    data : [{'name':'a','isValue':true, 'isDescription':true, 'isVisible':true }]
	
		});

		this.columnsDefinition = [{
            header: 'Name',
            dataIndex: 'name',
            flex: 1
        }];
		
		if(!config.treeLov){
			this.columnsDefinition.push({
	            xtype: 'radiocolumn',
	            header: 'Value',
	            dataIndex: 'isValue',
	            width: 60
	        });
			this.columnsDefinition.push({
	            xtype: 'radiocolumn',
	            header: 'Description',
	            dataIndex: 'isDescription',
	            width: 60
	        });
			this.columnsDefinition.push({
	            xtype: 'checkcolumn',
	            header: 'Visible',
	            dataIndex: 'isVisible',
	            width: 60,
	            editor: {
	                xtype: 'checkbox',
	                cls: 'x-grid-checkheader-editor'
	            }});
		}
		
		this.columns = this.columnsDefinition.slice(0,this.columnsDefinition.length);
		
		this.store.load();
    	this.callParent(arguments);
    	console.log('TestLovConfigurationGridPanel costructor OUT');
    


	}
	
	,onParentStroreLoad: function(){
		var fields = this.parentStore.proxy.reader.jsonData.metaData.fields;
		if(fields!=null && fields!=undefined && fields.length>0){
			var data = [];
			for(var i=0; i<fields.length; i++){
				var aData = {};
				aData.name = fields[i].name;
				aData.isValue = fields[i].name==this.valueColumnName;
				aData.isDescription = fields[i].name==this.descriptionColumnName;
				aData.isVisible = true;//visibleColumnNames
				data.push(aData);
			}
			this.store  = Ext.create('Ext.data.Store', {
				 fields: ['name', 'isValue', 'isDescription', 'isVisible'],
			    data : data
			});
			
			this.columns = this.columnsDefinition.slice(0,this.columnsDefinition.length);
			this.reconfigure(this.store, this.columns);
		}

		
	}
	
	
	
	
});

