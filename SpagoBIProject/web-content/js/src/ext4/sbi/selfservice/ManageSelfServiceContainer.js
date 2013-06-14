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
					
		this.items = [ this.manageSelfService, this.documentexecution]
		this.callParent(arguments);
		this.addEvents(
		        /**
		         * @event event1
		         * Execute the qbe clicking in the model/dataset
				 * @param {Object} docType engine to execute 'QBE'/'WORKSHEET'
				 * @param {Object} inputType 'DATASET'/'MODEL'
				 * @param {Object} record the record that contains all the information of the metamodel/dataset
		         */
		        'executeDocument'
				);
		this.manageSelfService.on('executeDocument', this.executeDocument ,this);

	}

	, executeDocument: function(docType,inputType, record){
		if(docType=='QBE'){
			this.executeQbe(inputType, record);
		}else{
			this.executeWorksheet(inputType, record);
		}
		this.getLayout().setActiveItem(1);	
	}
	
	, executeQbe: function(inputType, record){
		if(inputType == "MODEL"){
			var modelName = record.data.name;
			var dataSourceLabel = record.data.data_source_label;
			var url = this.qbeEngineBaseUrl+"&MODEL_NAME="+modelName;
			if(dataSourceLabel || dataSourceLabel!=""){
				url = url+ 
				'&DATA_SOURCE_LABEL=' + dataSourceLabel;
			}
			this.documentexecution.modelName = modelName;
			this.documentexecution.load(url);
		}
	}
	
	,executeWorksheet: function(inputType, record){
		if(inputType == "DATASET"){
			var datasetLabel = record.data.label;
			var datasourceLabel = record.data.dataSource;
			var url =  this.worksheetEngineBaseUrl+ '&dataset_label=' + datasetLabel ;
			if(datasourceLabel || datasourceLabel!=""){
				url = url+ '&datasource_label=' + datasourceLabel;
			}
			this.documentexecution.load(url);
		}
	}
	
   
	
});