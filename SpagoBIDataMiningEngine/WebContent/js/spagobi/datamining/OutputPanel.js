/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 *     
 *  @author
 *  Monica Franceschini
 */
 
  
Ext.define('Sbi.datamining.OutputPanel', {
	extend: 'Ext.panel.Panel',
	layout: {
        type: 'vbox'
        , style:'background: silver;'
    },
	
	config:{
		border: 0
	},
	executeScriptBtn: null,
	dmMask: null,
	command: null,
	output: null,
	mode: 'manual',
	
	constructor : function(config) {
		this.initConfig(config||{});
		
		this.command = config.command;
		this.output = config.output;
		this.mode = config.mode;
		
		this.resultPanel = Ext.create('Sbi.datamining.ResultPanel',{itsParent: this, command: this.command, output: this.output, mode: this.mode}); 
		this.uploadPanel = Ext.create('Sbi.datamining.UploadPanel',{itsParent: this, command: this.command});
		

		
		this.dmMask = new Ext.LoadMask(Ext.getBody(), {msg:LN('sbi.dm.execution.loading')});
		
		this.executeScriptBtn = Ext.create('Ext.Button', {
		    text: LN('sbi.dm.execution.run.text'),
		    scope: this,
		    iconCls: 'run',
		    scale: 'medium',		    
		    handler: function() {
		    	this.dmMask.show();
		        this.resultPanel.getResult();
		    }
		});
		
		this.callParent(arguments);
	},

	initComponent: function() {
		Ext.apply(this, {
			items: [this.uploadPanel, this.resultPanel, this.executeScriptBtn]
		});
	
		//this.isResultReady();
		this.callParent();
	}

//	, isResultReady: function(){
//		
//		var thisPanel = this;
//		
//		
//		var service = Ext.create("Sbi.service.RestService",{
//			url: "result"
//			,subPath: "needsResultAtForstExec"
//		});
//		
//		var functionSuccess = function(response){
//
//			
//			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
//			
//				var res = Ext.decode(response.responseText);
//				if(res.result == Sbi.settings.datamining.execution.ok){
//					this.resultPanel.getResult(Sbi.settings.datamining.execution.auto);
//				}
//				
//			}
//		};
//		service.callService(this, functionSuccess);
//	}
//	
	
});