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
    tosetactive:0,
   
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
		this.setActiveTab(this.tosetactive);
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

					for (var i=0; i< res.length; i++){						
						var command = res[i];

						var name = command.name;
						var mode = command.mode;
						var label = command.label;


						var outputsTab= Ext.create("Sbi.datamining.OutputsTabPanel",{
					        title: '<span style="color: #28596A;">'+label+'</span>',
					        iconCls: 'tab-icon',
					        tabPosition: 'left',
							listeners: {
					            'tabchange': function (tabPanel, tab) {
					               //alert("outputs "+tabPanel.id + ' ' + tab.id);
					               tabPanel.setAutoMode(tab.output);
					            }
					        },
					        commandName: name
					    });
						
						thisPanel.add(outputsTab);
						if(mode == 'auto'){
							thisPanel.setAutoMode(name, i);
							thisPanel.tosetactive=i;
						}
					}	
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
				
				if(res != null && res !== undefined){
					var autooutput = res.outputName;
					this.setActiveTab(activetab);
				}
			}

		};
		service.callService(this, functionSuccess);
	}
	
});