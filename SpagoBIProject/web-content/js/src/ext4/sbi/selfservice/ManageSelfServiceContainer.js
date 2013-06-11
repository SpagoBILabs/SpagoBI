/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * This class is the container for the self service interface 
 *    
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */
 
  
Ext.define('Sbi.selfservice.ManageSelfServiceContainer', {
	extend: 'Ext.panel.Panel',

	congig:{
    	worksheetEngineBaseUrl : '',
        qbeEngineBaseUrl : '',
        user : ''
	},

	/**
	 * @property {Panel} manageSelfService
	 *  Tab panel that contains the datasets and the model views
	 */
    manageSelfService: null,
	
	/**
	 * @property {Panel} documentexecution
	 *  Tab panel that contains the execution of the engine
	 */
    documentexecution: null
	
	, constructor : function(config) {
		this.initConfig(config);
		this.layout =  'card';
		
		this.documentexecution = Ext.create('Sbi.selfservice.SelfServiceExecutionIFrame',{}); 
		this.manageSelfService = Ext.create('Sbi.selfservice.ManageSelfService',{selfServiceContainer : this}); 
		
		this.manageSelfService.on('executeDocument', this.executeDocument ,this);
		
					
		this.items = [ this.manageSelfService, this.documentexecution]
		this.callParent(arguments);

	}

	, executeDocument: function(docType, record){
		var modelName = record.data.name;
		this.documentexecution.load( this.qbeEngineBaseUrl+"&MODEL_NAME="+modelName+"&DATA_SOURCE_LABEL=foodmart");
		this.getLayout().setActiveItem(1);
	}
	

//	, openDataSourceWindow: function(){
//		if(!this.dataSourceWindow){
//			
//			var store = Ext.create('Ext.data.Store', {
//			    model: 'Sbi.tools.datasource.DataSourceModelForFinalUser'
//			});
//			
//			this.dataSourceCombo = Ext.create('Ext.form.ComboBox', {
//			    fieldLabel: 'Choose Store',
//			    store: store,
//			    displayField: 'DATASOURCE_LABEL',
//			    valueField: 'DATASOURCE_LABEL'
//			});
//			
////			var form = Ext.create('Ext.form.Panel',{
////				items:[dataSourceCombo]
////			})
////			
//			this.dataSourceWindow = Ext.create('Ext.window.Window', {
//			    title: 'Select a data source',
//			    height: 200,
//			    width: 400,
//			    layout: 'form',
//			    items: [this.dataSourceCombo],
//			    buttons: [{
//		            text: 'Save',
//		            handler: function() {
//		               // this.up('form').getForm().isValid();
//		            }
//		        },{
//		            text: 'Cancel',
//		            handler: function() {
//		              //  this.up('form').getForm().reset();
//		            }
//		        }]
//			});
//		}
//		this.dataSourceWindow.show();
//	}
//
//    
	
});