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
	
	dmMask: null,
	command: null,
	output: null,
	mode: 'manual',
	fillVarPanel: null,
	executeScriptBtn: null,
	
	constructor : function(config) {
		this.initConfig(config||{});
		
		this.command = config.command;
		this.output = config.output;
		this.mode = config.mode;
		
		this.actionsPanel = Ext.create('Ext.panel.Panel',{itsParent: this, 
														command: this.command, 
														output: this.output, 
														mode: this.mode,
														border: 0,
														layout: {
													        type: 'hbox'
													    }
														}); 
		this.resultPanel = Ext.create('Sbi.datamining.ResultPanel',{itsParent: this, command: this.command, output: this.output, mode: this.mode}); 
		this.uploadPanel = Ext.create('Sbi.datamining.UploadPanel',{itsParent: this, command: this.command});
		
		
		this.executeScriptBtn = Ext.create('Ext.Button', {
		    text: LN('sbi.dm.execution.run.text'),
		    scope: this,
		    iconCls: 'run',
		    scale: 'medium',	
		    margin: 5,
		    style: {
	            background: '#fff0aa;'
	        },
		    handler: function() {
		    	this.dmMask.show();
		        this.resultPanel.getResult(true);
		    }
		});
		
		this.dmMask = new Ext.LoadMask(Ext.getBody(), {msg:LN('sbi.dm.execution.loading')});
		
		this.callParent(arguments);
	},

	initComponent: function() {
		
		this.executeScriptBtn.hide();
		this.actionsPanel.add(this.executeScriptBtn, this.uploadPanel);
		
		Ext.apply(this, {
			items: [this.actionsPanel, this.resultPanel]
		});
		
		this.callParent();
		this.addVariables();
	}

	, addVariables: function(){
		this.fillVarPanel = Ext.create('Sbi.datamining.FillVariablesPanel',{
										callerName : [this.command, this.output], 
										caller: 'output',
										itsParent: this});
		this.fillVarPanel.on('hasVariables',  function(hasVars) {
			if(hasVars){
				this.insert(0,this.fillVarPanel);	
				this.doLayout();
			}
		}, this);
	}
	
});