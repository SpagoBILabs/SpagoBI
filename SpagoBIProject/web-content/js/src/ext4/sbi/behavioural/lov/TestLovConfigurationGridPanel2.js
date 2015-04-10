/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define
(
	'Sbi.behavioural.lov.TestLovConfigurationGridPanel2', 
	
	{
	    extend: 'Ext.grid.Panel',
	
	    config: 
	    {
	      	stripeRows: true,
	      	columns: [],
	      	border: false
	      	//height: 200
	    }, 
	    
	    constructor: function(config) 
	    {
			// I have the semi-filled LOV provider within "config.lovProvider"
			
			this.title =  "Fields";

			this.viewConfig = 
			{
				plugins: 
				{
					ddGroup: 'GridLovDD',
					ptype: 'gridviewdragdrop',
					enableDrop: false
				}
			};
							
//			Sbi.debug('TestLovConfigurationGridPanel costructor IN');
			console.log("[IN] constructor() TestLovConfigurationGridPanel2");
			
			Ext.apply(this,config);
			
			this.flex = 1;
	    	//this.height = 200;
			
			this.store = Ext.create
			(
				'Ext.data.Store', 
				
				{
				    fields: ['name', 'isValue', 'isDescription', 'isVisible'],
				    
				    data: 
			    	[
			    	 	{
			    	 		'name': 'a',
			    	 		'isValue': false, 
			    	 		'isDescription': true, 
			    	 		'isVisible': false 
		    	 		}
		    	 	]
				}
			);
	
			this.columnsDefinition = 
			[
			 	{
		            header: LN('sbi.behavioural.lov.name'),
		            dataIndex: 'name',
		            flex: 1
			 	}
		 	];			
			
			if(config.lovType.indexOf("tree") < 0)
			{
				this.columnsDefinition.push({
		            xtype: 'radiocolumn',
		            header: LN('sbi.behavioural.lov.value'),
		            dataIndex: 'isValue',
		            width: 90
		        });
				this.columnsDefinition.push({
		            xtype: 'radiocolumn',
		            header: LN('sbi.behavioural.lov.description'),
		            dataIndex: 'isDescription',
		            width: 90
		        });
				this.columnsDefinition.push({
		            xtype: 'checkcolumn',
		            header: LN('sbi.behavioural.lov.visible'),
		            dataIndex: 'isVisible',
		            width: 90,
		            editor: {
		                xtype: 'checkbox',
		                cls: 'x-grid-checkheader-editor'
		            }});
			}
			
			this.columns = this.columnsDefinition.slice(0,this.columnsDefinition.length);
			
			// "this.columns" - names of the columns in the upper grid
			
			
			
			this.store.load();			
	    	this.callParent(arguments);
//	    	Sbi.debug('TestLovConfigurationGridPanel costructor OUT');	
	    	console.log("[OUT] constructor() TestLovConfigurationGridPanel2");
	    	
		},
		
		/* This function is called from the outer class (JS file) - e.g "TestLovPanel2.js" 
		 * It is used for populating the lower grid with data. */
		onParentStoreLoad: function()
		{
			//console.log("/////");
			//console.log(this.parentStore);
			
			/* This is something I added in order to prevent error due to 
			 * wrong syntax for defined SQL (query) */
			
			if (this.parentStore.proxy.reader.jsonData.metaData != undefined)
			{
				var fields = this.parentStore.proxy.reader.jsonData.metaData.fields;
				
				//console.log("4444444");
				//console.log(fields);
				
				if(fields!=null && fields!=undefined && fields.length>0){
					//console.log("USAO 1");
					var data = [];
					
	//				for(var i=0; i<fields.length; i++){
	//					var aData = {};
	//					aData.name = fields[i].name;
	//					data.push(aData);
	//				}
					
					// MY CODE...
					/* In case that "recNo" field exists in returning JSON 
					 * (LOV test results), start counting fields from index 1
					 * (because "recNo" is 0). When "recNo" does not exist - 
					 * when filtering, count from 0, because that is the first 
					 * field now. */
					if (fields[0] == "recNo")
					{
						var startIndex = 1;
					}
					else
					{
						var startIndex = 0;
					}
					
					for(var i=startIndex; i<fields.length; i++)
					{
						var aData = {};
						aData.name = fields[i].header;
						data.push(aData);
					}
					
					this.setValues(data);
					
					this.store  = Ext.create
					(
						'Ext.data.Store', 
						
						{
							fields: ['name', 'isValue', 'isDescription', 'isVisible'],
							data : data
						}
					);
					
					this.store.load();
					//console.log("USAO 3");
					this.columns = this.columnsDefinition.slice(0,this.columnsDefinition.length);
					//console.log(this.columns);
					this.reconfigure(this.store);
					//console.log("USAO 4");
				}
			}
			
		}
		
		, getValues: function(){
			var value;
			var description;
			var visible =[]; 
			var data = this.store.data;
			
			console.log("%%$$$%%%$$%%$$");
			console.log(data);
			
			if(data!=null && data!=undefined && data.items!=null && data.items!=undefined ){
				for(var i=0; i<data.items.length; i++){
					var aItem = data.items[i];
					if(aItem.data.isValue){
						value = aItem.data.name;
					}
					if(aItem.data.isDescription){
						description = aItem.data.name;
					}
					if(aItem.data.isVisible){
						visible.push(aItem.data.name);
					}
				}
			}
			
			/* If mandatory information is missing (column that is VALUE and the one that is DESCRIPTION)
			 * show the warning massage that inform user to specify them. */
			
			if (value == undefined || value == null || value == "")
			{
				Sbi.exception.ExceptionHandler.showWarningMessage("You need to specify \"Value\" value");
				return null;
			}
			else if (description == undefined || description == null || description == "")
			{
				Sbi.exception.ExceptionHandler.showWarningMessage("You need to specify \"Description\" value");
				return null;
			}
			else 
			{
				var LOVConfiguration = 
				{
						valueColumnName:value,
						descriptionColumnName:description,
						visibleColumnNames:visible,
						lovType: this.lovType,
						column: this.column
				}				
				
				return LOVConfiguration;
			}
			
		},
	
		setValues: function(data){
			//console.log("SDSSSSS - setValues");
			this.column = [];
			if(data!=null && data!=undefined && this.lovConfig!=null && this.lovConfig!=undefined){
				for(var i=0; i<data.length; i++){
					var aItem = data[i];
					if(aItem.name == this.lovConfig.valueColumnName){
						aItem.isValue = true;
					}else{
						aItem.isValue = false;
					}
					if(aItem.name == this.lovConfig.descriptionColumnName ){
						aItem.isDescription = true;
					}else{
						aItem.isDescription = false;
					}
					if(this.lovConfig.visibleColumnNames.indexOf(aItem.name)>=0){
						aItem.isVisible = true;
					}else{
						aItem.isVisible = false;
					}
					this.column.push(aItem.name);
				}
			}
		}
	
	}
	
);