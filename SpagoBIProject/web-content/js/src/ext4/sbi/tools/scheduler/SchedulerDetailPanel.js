/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define('Sbi.tools.scheduler.SchedulerDetailPanel', {
    extend: 'Ext.form.Panel'
    	
        , config: {
        	//frame: true,
        	bodyPadding: '5 5 0',
        	defaults: {
                width: 400
            },        
            fieldDefaults: {
                labelAlign: 'right',
                msgTarget: 'side'
            },
            border: false,
            services:[]
        }

		, constructor: function(config) {
			
			this.initConfig(config);
			this.addEvents('addSchedulation');
			
			thisPanel = this;


			this.initFields();
			this.items=[this.activityLabel , this.documentsGrid, this.schedulationsGrid];
			this.tbar = Sbi.widget.toolbar.StaticToolbarBuilder.buildToolbar({items:[]},this);
			this.callParent(arguments);
			this.on("render",function(){this.hide()},this);

		}
		
		//initialize fields of the gui
		, initFields: function(){
			
			this.activityLabel = Ext.create("Ext.form.Label",{
				text: 'Overview Activity',
			});
			
			this.documentsGridStore = Ext.create('Ext.data.Store', {
				fields:['name', 'condensedParameters'],
				data:{},
				proxy: {
					type: 'memory',
					reader: {
						type: 'json',
						root: 'documents'
					}
				}
			});
			
			this.documentsGrid = Ext.create('Ext.grid.Panel', {
			    title: 'Documents',
			    store: this.documentsGridStore,
			    columns: [
			        { text: 'Name',  dataIndex: 'name' },
			        { text: 'Parameters', dataIndex: 'condensedParameters', flex: 1,
				          renderer : 
				        		function(value, metadata, record, rowIndex, colIndex) {
				        			metadata.tdAttr = 'data-qtip="' + value +'"';

				        		return value;
				        	}
			        },

			    ],
			    height: 300,
			    width: '100%',
			    margin: '5 0 0 0'

			});
			
			this.schedulationsGridStore = Ext.create('Ext.data.Store', {
				fields:['jobName','jobGroup','triggerName','triggerDescription' ,'triggerChronString','triggerStartDate','triggerStartTime','triggerEndDate','triggerEndTime'],
				data:{},
				proxy: {
					type: 'memory',
					reader: {
						type: 'json',
						root: 'triggers'
					}
				}
			});
			
			this.schedulationsGrid = Ext.create('Ext.grid.Panel', {
			    title: 'Schedulations',
			    store: this.schedulationsGridStore,
			    columns: [
			        { text: 'Name',  dataIndex: 'triggerName', flex: 1,
			          renderer : 
			        		function(value, metadata, record, rowIndex, colIndex) {
			        			var triggerDescription = record.get('triggerDescription');
			        			metadata.tdAttr = 'data-qtip="<b>Name:</b> ' + value +'</br> <b>Description:</b> '+triggerDescription+'"';

			        		return value;
			        	} 
			        },
			        { text: 'Generation', dataIndex: 'generation' },
			        { text: 'Type', dataIndex: 'triggerChronString' },
			        { text: 'Start Date ', dataIndex: 'triggerStartDate' },
			        { text: 'Start Time ', dataIndex: 'triggerStartTime' },
			        { text: 'End Date ', dataIndex: 'triggerEndDate' },
			        { text: 'End Time ', dataIndex: 'triggerEndTime' },
					{
						//STATE BUTTON
			        	menuDisabled: true,
						sortable: false,
						xtype: 'actioncolumn',
						width: 20,
						columnType: "decorated",
						items: [{
							iconCls   : 'button-select',  // Use a URL in the icon config
							handler: function(grid, rowIndex, colIndex) {
								var selectedRecord =  grid.store.getAt(rowIndex);
								//TODO
								alert("TODO: pause / resume schedulation "+selectedRecord.get('triggerName'));
							}
						}]
					},
					{
						//DELETE BUTTON
			        	menuDisabled: true,
						sortable: false,
						xtype: 'actioncolumn',
						width: 20,
						columnType: "decorated",
						items: [{
							iconCls   : 'button-remove',  // Use a URL in the icon config
							handler: function(grid, rowIndex, colIndex) {
								var selectedRecord =  grid.store.getAt(rowIndex);
								//TODO
								alert("TODO: delete single schedulation "+selectedRecord.get('triggerName'));
							}
						}]
					}


			    ],
			    height: 300,
			    width: '100%',
			    margin: '5 0 0 0',
			    tbar: [{
			    	text: 'Add',
			        iconCls: 'icon-add',
			    	scope: this,
			    	tooltip: 'Add schedulation',
			    	handler: this.onAddClick
			    }]
			});
			
			
			//Sbi.widget.grid.StaticGridDecorator.addDeleteColumn(this.schedulationsGrid.columns, this.schedulationsGrid) ;
		}
		
		, onAddClick: function(){
			this.fireEvent('addSchedulation');
		}
		
		, setFormState: function(values){
			this.activityLabel.setText( 'Overview Activity '+values.jobName );
			this.documentsGridStore.loadData(values.documents,false);
			
			if ((values != undefined) && (values.triggers != undefined)){
				//iterate store to modify CronExpression (get only the type part).
				var typeValue;
				for (var i = 0; i < values.triggers.length; i++) {
					var element = values.triggers[i];
					var indFirstBra = element.triggerChronString.indexOf("{");
					if (indFirstBra !== -1){
						element.triggerChronString = element.triggerChronString.substring(0, indFirstBra);
					}
				}
			}
			this.schedulationsGridStore.loadData(values.triggers,false);

		}
    	
});    	