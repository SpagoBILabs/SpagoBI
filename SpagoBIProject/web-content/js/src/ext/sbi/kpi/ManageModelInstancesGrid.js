/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/

/**
 * Object name
 * 
 * [description]
 * 
 * 
 * Public Properties
 * 
 * [list]
 * 
 * 
 * Public Methods
 * 
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageModelInstancesGrid = function(config, ref) { 
	var paramsList = {MESSAGE_DET: "MODELINSTS_LIST"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINSTS_NODE_DELETE"};
	
	this.configurationObject = {};
	
	this.configurationObject.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
			, baseParams: paramsList
		});	
	this.configurationObject.deleteItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
		, baseParams: paramsDel
	});
	this.refTree = ref;
	this.initConfigObject();
	config.configurationObject = this.configurationObject;


	var c = Ext.apply({}, config || {}, {});

	Sbi.kpi.ManageModelInstancesGrid.superclass.constructor.call(this, c);	
	
	this.addEvents('selected');

}

Ext.extend(Sbi.kpi.ManageModelInstancesGrid, Sbi.widgets.ListGridPanel, {
	
	configurationObject: null
	, treeConfigObject: null
	, gridForm:null
	, mainElementsStore:null
	, referencedCmp : null
	, emptyRecord: null
	
	,initConfigObject:function(){
		
		this.configurationObject.idKey = 'modelInstId';
		this.configurationObject.referencedCmp = this.referencedCmp;

	    this.configurationObject.fields = ['modelInstId'
		                     	          , 'name'
		                     	          , 'label'
		                     	          , 'text'
		                     	          , 'description'

		                     	          , 'kpiInstId'
		                     	          , 'kpiName'
		                     	          , 'kpiId'
		                     	          , 'kpiInstThrId'
		                     	          , 'kpiInstThrName'
		                     	          , 'kpiInstTarget'
		                     	          , 'kpiInstWeight'
		                     	          , 'kpiInstChartTypeId'
		                     	          , 'modelUuid'
		                     	          
		                     	          , 'kpiInstPeriodicity'
		                     	          
		                     	          , 'modelId'
		                     	          , 'modelText'
			                     	      , 'modelCode'
			                     	      , 'modelName'
			                     	      , 'modelDescr'
			                     	      , 'modelType'
			                     	      , 'modelTypeDescr'
			                     	      , 'resourceName'
			                     	      , 'resourceCode'
			                     	      , 'resourceType'
			                     	      , 'resourceId'
		                    	          ];
		
		this.configurationObject.gridColItems = [
		                                         {id:'modelInstId',	header: LN('sbi.generic.name'), width: 240, sortable: true, locked:false, dataIndex: 'name'}
		                                        ];
		
		this.configurationObject.panelTitle = LN('sbi.modelinstances.panelTitle');
		this.configurationObject.listTitle = LN('sbi.modelinstances.listTitle');

    }
	
    //OVERRIDING save method
	,save : function() {
		alert('Save');
    }


	, deleteSelectedItem: function(itemId, index) {
		Ext.MessageBox.confirm(
				LN('sbi.generic.pleaseConfirm'),
				LN('sbi.generic.confirmDelete'),            
	            function(btn, text) {
	                if (btn=='yes') {
	                	if (itemId != null) {	
							Ext.Ajax.request({
					            url: this.services['deleteItemService'],
					            params: {'modelInstId': itemId},
					            method: 'GET',
					            success: function(response, options) {
									if (response !== undefined) {
										var deleteRow = this.rowselModel.getSelected();
										this.mainElementsStore.remove(deleteRow);
										this.mainElementsStore.commitChanges();
										if(this.mainElementsStore.getCount()>0){
											this.rowselModel.selectRow(0);
										}else{
											this.addNewItem();
										}
									} else {
										Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.deletingItemError'), LN('sbi.generic.serviceError'));
									}
					            },
					            failure: function() {
					                Ext.MessageBox.show({
					                    title: LN('sbi.generic.error'),
					                    msg: LN('sbi.generic.deletingItemError'),
					                    width: 150,
					                    buttons: Ext.MessageBox.OK
					               });
					            }
					            ,scope: this
				
							});
						} else {
							Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.generic.error.msg'),LN('sbi.generic.warning'));
						}
	                }
	            },
	            this
			);
	}
	, launchAddModelInstWindow : function() {

		var conf = {};
		conf.notDraggable = true;
		conf.readonly = true;
		
		var manageModels = new Sbi.kpi.ManageAddModelPanel(conf);

		this.modelsWin = new Ext.Window({
			title: LN('sbi.lookup.Select') ,   
            layout      : 'fit',
            width       : 800,
            y:			20,
            closeAction :'close',
            modal 		: true,
            plain       : true,
            scope		: this,
            items       : [manageModels]
		});
		
		manageModels.modelsGrid.on('selected', function(rec){
							this.modelsWin.close();
							this.addModelInstanceRecord(rec);
							}, this);

		this.modelsWin.show();
		this.modelsWin.doLayout();

	}
	, addModelInstanceRecord: function(rec){
		this.mainElementsStore.add(rec);
		this.mainElementsStore.commitChanges();
		this.rowselModel.selectRecords([rec]);
		//fills node detail and tabs by rowclick
		this.fireEvent('rowclick', this);
	}
	, addNewItem : function(){

		this.launchAddModelInstWindow();


	}

});
