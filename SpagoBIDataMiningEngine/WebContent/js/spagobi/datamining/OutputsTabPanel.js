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
    tosetactive:0,
	config:{
		border: 0
		
	},
	dmMask: null,
	command: null,
	constructor : function(config) {
		this.initConfig(config||{});		
		
		this.command= config.commandName;
	
		this.callParent(arguments);
		
	},

	initComponent: function() {
		this.callParent();

		this.getOuputs();
		this.setActiveTab(this.tosetactive);
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

					for (var i=0; i< res.length; i++){						
						var output = res[i];
						var outputName= output.outputName;

						var ouputLabel= output.ouputLabel;
						var outputMode= output.outputMode;

						var outputTab= Ext.create("Sbi.datamining.OutputPanel",{
					        title: ouputLabel,
	                        bodyStyle: 'padding:10px;',
	                        command: thisPanel.command,
	                        output: outputName,
	                        mode: outputMode
					    });
						
						thisPanel.add(outputTab);
						if(outputMode == 'auto' || res.length == 1){
							this.setActiveTab(i);
						}
					}	
				}

			}

		};
		service.callService(this, functionSuccess);
	}
	, setOutputAutoMode: function(output){
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
					var items = thisPanel.items.items;
					for(var i=0; i< items.length; i++){
						var outpanel = items[i];
						if((outpanel.command == thisPanel.command) &&(outpanel.output == output)){
							//found the one to activete
							outpanel.dmMask.show();
							outpanel.resultPanel.getResult();
							thisPanel.setActiveTab(i);

						}
					}
					
				}
			}

		};
		service.callService(this, functionSuccess);
	}
});