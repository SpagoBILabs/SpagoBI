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
			
			this.triggersData = null;

			this.initFields();
			this.items=[this.activityLabel , this.documentsGrid, this.schedulationsGrid];
			this.tbar = Sbi.widget.toolbar.StaticToolbarBuilder.buildToolbar({items:[]},this);
			this.callParent(arguments);
			this.on("render",function(){this.hide()},this);

		}
		

		
		//initialize fields of the gui
		, initFields: function(){
			
			this.activityLabel = Ext.create("Ext.form.Label",{
				text: LN('sbi.scheduler.overview'),
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
			    title: LN('sbi.scheduler.documents'),
			    store: this.documentsGridStore,
			    columns: [
			        { text: LN('sbi.scheduler.name'),  dataIndex: 'name' },
			        { text: LN('sbi.scheduler.parameters'), dataIndex: 'condensedParameters', flex: 1,
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
				fields:['jobName','jobGroup','triggerName','triggerGroup','triggerDescription' ,'triggerChronString','triggerStartDate','triggerStartTime','triggerEndDate','triggerEndTime'],
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
			    title: LN('sbi.scheduler.schedulations'),
			    store: this.schedulationsGridStore,
			    columns: [
			        { text: LN('sbi.scheduler.name'),  dataIndex: 'triggerName', flex: 1,
			          renderer : 
			        		function(value, metadata, record, rowIndex, colIndex) {
			        			var triggerDescription = record.get('triggerDescription');
			        			metadata.tdAttr = 'data-qtip="<b>Name:</b> ' + value +'</br> <b>Description:</b> '+triggerDescription+'"';

			        		return value;
			        	} 
			        },
			      //  { text: 'Generation', dataIndex: 'generation' },
			        { text: LN('sbi.scheduler.type'), dataIndex: 'triggerChronString' },
			        { text: LN('sbi.scheduler.startdate'), dataIndex: 'triggerStartDate' },
			        { text: LN('sbi.scheduler.starttime'), dataIndex: 'triggerStartTime' },
			        { text: LN('sbi.scheduler.enddate'), dataIndex: 'triggerEndDate' },
			        { text: LN('sbi.scheduler.endtime'), dataIndex: 'triggerEndTime' },
					{
						//STATE BUTTON
			        	menuDisabled: true,
						sortable: false,
						xtype: 'actioncolumn',
						width: 20,
						columnType: "decorated",
						items: [{
							iconCls   : 'button-select',  
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
							iconCls   : 'button-remove',  
							handler: function(grid, rowIndex, colIndex) {								
								var selectedRecord =  grid.store.getAt(rowIndex);
								thisPanel.onDeleteSchedulation(selectedRecord);
							}
						}]
					}


			    ],
			    height: 300,
			    width: '100%',
			    margin: '5 0 0 0',
			    tbar: [{
			    	text: LN('sbi.generic.add'),
			        iconCls: 'icon-add',
			    	scope: this,
			    	tooltip: LN('sbi.scheduler.addschedulation'),
			    	handler: this.onAddClick
			    }]
			});
			
			
		}
		
		, onDeleteSchedulation: function(record){
			
			var values = {}
			values.jobName = record.data.jobName;
			values.jobGroup = record.data.jobGroup;
			values.triggerName = record.data.triggerName;
			values.triggerGroup = record.data.triggerGroup;

			
			Ext.MessageBox.confirm(
					LN('sbi.generic.pleaseConfirm'),
					LN('sbi.generic.confirmDelete'),
					function(btn, text){

						if (btn=='yes') {
							//perform Ajax Request

							Ext.Ajax.request({
								url: this.services["deleteTrigger"],
								params: values,
								success : function(response, options) {
									if(response !== undefined  && response.responseText !== undefined && response.statusText=="OK") {
										if(response.responseText!=null && response.responseText!=undefined){
											if(response.responseText.indexOf("error.mesage.description")>=0){
												Sbi.exception.ExceptionHandler.handleFailure(response);
											}else{						
												Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.scheduler.schedulation.deleted'));
												thisPanel.schedulationsGrid.store.remove(record);
												thisPanel.schedulationsGrid.store.commitChanges();
												
												if ((thisPanel.triggersData != null) && (thisPanel.triggersData !== undefined)){
													//remove the trigger also from the original json data
													var index = -1;
													for (var i = 0; i < thisPanel.triggersData.length; i++) {
														var element = thisPanel.triggersData[i];
														if ( (element.jobName == record.get('jobName')) &&
														     (element.jobGroup == record.get('jobGroup')) &&
														     (element.triggerGroup == record.get('triggerGroup')) &&
														     (element.triggerName == record.get('triggerName')) ){		
															
															index = i;
															break;
														}
 
													}
													if (index != -1){
														thisPanel.triggersData.splice(index, 1);
													}
												}

											}
										}
									} else {
										Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
									}
								},
								scope: this,
								failure: Sbi.exception.ExceptionHandler.handleFailure      
							})
						}
						
					},
					this
				);
		}
		
		, onAddClick: function(){
			this.fireEvent('addSchedulation');
		}
		
		, setFormState: function(values){
			this.activityLabel.setText( LN('sbi.scheduler.overview')+values.jobName );
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
			//original data for permanent delete (not only on local store)
			this.triggersData = values.triggers

		}
    	
});    	