/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Rossato Luca (luca.rossato@eng.it)
 */
 
  
Ext.define('Sbi.tools.multitenant.MultitenantListDetailPanel', {
	extend: 'Sbi.widgets.compositepannel.ListDetailPanel'

	,config: {
		stripeRows: true,
		modelName: "Sbi.tools.multitenant.MultitenantModel"
	}

	, initServices: function(baseParams){
		this.services["saveTenant"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'multitenant/save'
				, baseParams: baseParams
		});
	}

	, constructor: function(config) {
		this.services = [];
		this.initServices();
		this.detailPanel =  Ext.create('Sbi.tools.multitenant.MultitenantDetailPanel',{});
		this.columns = [{dataIndex:"MULTITENANT_ID", header:LN('sbi.multitenant.id')}, {dataIndex:"MULTITENANT_NAME", header:LN('sbi.generic.name')}];
		this.fields = ["MULTITENANT_ID","MULTITENANT_NAME", "MULTITENANT_THEME"];
		this.detailPanel.on("save",this.onFormSave,this);
		this.filteredProperties = ["MULTITENANT_NAME"];
		this.buttonToolbarConfig = {
				newButton: true
		};
		
		this.buttonColumnsConfig ={
			deletebutton:true
		};
	
		this.callParent(arguments);
	}
	
	, onDeleteRow: function(record){
		
		var thisPanel = this;

		var deleteRecord = function(buttonId, text, config){
			
			var record = config.record;
			
			if(buttonId == 'yes') {			
				
				var recordToDelete = Ext.create("Sbi.tools.multitenant.MultitenantModel",record.data);
				
				if (!this.loadMask) {    		
		    		this.loadMask = new Ext.LoadMask(Ext.getBody(), {msg: "  Wait...  "});
		    	}		   
		    	this.loadMask.show();
		    					
				recordToDelete.destroy({
					success : function(object, response, options) {
					
						if (this.loadMask && this.loadMask != null) {	
				    		this.loadMask.hide();
				    	}
						if(response !== undefined && response.response !== undefined && response.response.responseText !== undefined && response.response.statusText=="OK") {
							response = response.response ;
							if(response.responseText!=null && response.responseText!=undefined){
								if(response.responseText.indexOf("error.mesage.description")>=0){
									Sbi.exception.ExceptionHandler.handleFailure(response);
								}else{
									Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.multitenant.deleted'));
									thisPanel.grid.store.remove(record);
									thisPanel.grid.store.commitChanges();
								}
							}
						} else {
							Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
						}
					},
					failure: function(object, response, options){
						if (this.loadMask && this.loadMask != null) {	
				    		this.loadMask.hide();
				    	}
						Sbi.exception.ExceptionHandler.handleFailure
					},
					scope: this
				});
			} 
		};
		
		Ext.Msg.show({
			   title: LN('sbi.multitenant.delete.title'),   
			   msg: LN('sbi.multitenant.delete.msg'),
			   buttons: Ext.Msg.YESNO,
			   icon: Ext.MessageBox.QUESTION,
			   modal: true,
			   fn: deleteRecord, 
			   record: record
			});

	}

	, onFormSave: function(record){
		
		Ext.Ajax.request({
	        url: this.services['saveTenant'],
	        params: Ext.encode(record),
	        method: 'POST',
	        success: function(response, options) {

			if(response !== undefined && response.responseText != undefined && response.responseText != null && response.statusText=="OK") {
				if(response.responseText.indexOf("error.mesage.description")>=0){
					Sbi.exception.ExceptionHandler.handleFailure(response);
				}else{

					var respoceJSON = Ext.decode(response.responseText);
					if(respoceJSON.MULTITENANT_ID){
						record.MULTITENANT_ID = respoceJSON.MULTITENANT_ID;
					}
					if(respoceJSON.SAVE_TYPE && respoceJSON.SAVE_TYPE == 'INSERT'){
						Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.multitenant.saved') + record.MULTITENANT_NAME+'_admin');
					}else{
						Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.generic.resultMsg'));
					}
								
					var selectedRow = this.grid.getSelectionModel().getSelection();
					selectedRow[0].set("MULTITENANT_ID", record.MULTITENANT_ID);
					selectedRow[0].set("MULTITENANT_NAME", record.MULTITENANT_NAME);
					selectedRow[0].set("MULTITENANT_THEME", record.MULTITENANT_THEME);

					this.grid.store.commitChanges();						
					this.detailPanel.setActiveTab(0);
				}
			} else {
				Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
			}
		},
		failure: Sbi.exception.ExceptionHandler.handleFailure	      		
		,scope: this
		});
	}

	, onGridSelect: function(selectionrowmodel, record, index, eOpts){
		this.detailPanel.setValues(record.data);
		this.detailPanel.enginesStore.getProxy().extraParams  = {'TENANT': record.data.MULTITENANT_NAME, DOMAIN_TYPE:"DIALECT_HIB"};
		this.detailPanel.enginesStore.load();
		this.detailPanel.dsStore.getProxy().extraParams  = {'TENANT': record.data.MULTITENANT_NAME, DOMAIN_TYPE:"DIALECT_HIB"};
		this.detailPanel.dsStore.load();
		this.detailPanel.show();
	}

});
