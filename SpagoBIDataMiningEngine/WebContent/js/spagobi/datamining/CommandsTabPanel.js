/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 *     
 *  @author
 *  Monica Franceschini
 */
 
  
Ext.define('Sbi.datamining.CommandsTabPanel', {
	extend: 'Ext.tab.Panel',
	layout: {
        type: 'fit'
    },
    
	//title: 'Data Mining Engine',
    activeTab: 0,
   
	config:{
		border: 0
	},
	
	constructor : function(config) {
		this.initConfig(config||{});
		
		this.callParent(arguments);
	},

	initComponent: function() {		
		this.callParent();
		this.getCommands();
		
	}
	
	, getCommands: function(){
		
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "command"
		});
		
		var functionSuccess = function(response){

			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
			
				var res = Ext.decode(response.responseText);				
				
				if(res && Array.isArray(res)){
					var panelActive =0;
					for (var i=0; i< res.length; i++){						
						var command = res[i];
						var scriptName = command.scriptName;
						var name = command.name;
						var mode = command.mode;
						var label = command.label;
						var outputs = command.outputs;

						var commandTab= Ext.create("Sbi.datamining.OutputsTabPanel",{
					        title: '<span style="color: #28596A;">'+label+'</span>',
					        commandName: name
					    });
						
						thisPanel.add(commandTab);
						if(mode == 'auto'){
							panelActive =i;
							this.setAutoMode(name, i);
						}
					}	
					this.activeTab = panelActive;
					this.setActiveTab(panelActive);
				}

			}

		};
		service.callService(this, functionSuccess);
	}
	, setAutoMode: function(command, activetab){
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "command"
			,pathParams: [command] 
		});
		
		var functionSuccess = function(response){

			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
			
				var res = Ext.decode(response.responseText);		
				
				if(res.result != null && res.result == Sbi.settings.datamining.execution.ok){
					this.setActiveTab(activetab);
					//alert(activetab);
				}
			}

		};
		service.callService(this, functionSuccess);
	}
	
});