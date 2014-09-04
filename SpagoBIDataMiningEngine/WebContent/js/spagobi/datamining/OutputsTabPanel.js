/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 *     
 *  @author
 *  Monica Franceschini
 */
 
  
Ext.define('Sbi.datamining.OutputsTabPanel', {
	extend: 'Ext.tab.Panel',
	layout: {
        type: 'fit'        
    },
    activeTab: 0,
	config:{
		border: 0
	},
	command: null,
	constructor : function(config) {
		this.initConfig(config||{});		
		
		this.command= config.commandName;
			
		this.callParent(arguments);
	},

	initComponent: function() {
		this.callParent();
		this.getOuputs();
	}
	,getOuputs: function(){
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "output" 
			,pathParams: [thisPanel.command]
		});
		
		var functionSuccess = function(response){

			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
			
				var res = Ext.decode(response.responseText);				
				
				if(res && Array.isArray(res)){
					thisPanel.activeTab=0;
					for (var i=0; i< res.length; i++){						
						var output = res[i];
						var outputName= output.outputName;

						var ouputLabel= output.ouputLabel;
						var outputMode= output.outputMode;

						if(outputMode == 'auto'){
							thisPanel.activeTab = i+1;
							this.setAutoMode(name, i);
						}
						
						var outputTab= Ext.create("Sbi.datamining.OutputPanel",{
					        title: ouputLabel,
	                        bodyStyle: 'padding:10px;',
	                        command: thisPanel.command,
	                        output: outputName
					    });
						
						thisPanel.add(outputTab);
					}	
				}

			}

		};
		service.callService(this, functionSuccess);
	}
	, setAutoMode: function(output, activetab){
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "output"
			,subPath: "setAutoMode"
			,pathParams: [output] 
		});
		
		var functionSuccess = function(response){

			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
			
				var res = Ext.decode(response.responseText);		
				
				if(res.result != null && res.result == Sbi.settings.datamining.execution.ok){
					this.setActiveTab(activetab);
				}
			}

		};
		service.callService(this, functionSuccess);
	}
});