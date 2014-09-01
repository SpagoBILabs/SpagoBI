/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 *   
 *  @author
 *  Monica Franceschini
 */
 
  
Ext.define('Sbi.datamining.ResultPanel', {
	extend: 'Ext.panel.Panel',
	layout: {
        type: 'fit'
		, flex: 1
		, border: 0
    },
	
	config:{
		 autoScroll: true
		, border:0
		, padding: 3
		, width: 800
		, style: 'margin-bottom: 5px; '
	},
	
	dataminingParentPanel: null,
	
	type: null,
	result: '',
	resultTitleStyle: 'font-weight: bold; color: grey; padding-bottom: 2px;',
	plotStyle: 'font-weight: normal; padding:5px;',
	videoStyle: 'font-weight: normal; padding:5px; border: 1px solid #d7e0ea; width:100%;',
	
	constructor : function(config) {
		this.initConfig(config||{});
		
		this.dataminingParentPanel = config.itsParent;
		
		this.callParent(arguments);
	},

	initComponent: function() {
		this.callParent();

	}

	
	, getResult: function(){
		
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "result"
		});
		
		var functionSuccess = function(response){
			
			if(thisPanel.dataminingParentPanel.dmMask !== undefined && thisPanel.dataminingParentPanel.dmMask != null){
				thisPanel.dataminingParentPanel.dmMask.hide();
			}
			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
			
				var res = Ext.decode(response.responseText);				
				
				if(res && Array.isArray(res)){
					var html ='';
					for (var i=0; i< res.length; i++){
						var output = res[i];
						var type = output.outputType;
						var result = output.result;
						var varName = output.variablename;
						var plotName = output.plotName;
						
						if(type == 'plot'){
							html+='<div style="'+this.resultTitleStyle+'">'+plotName+' : </div><br/><p style="'+this.plotStyle+'"><img alt="Result for '+plotName+'" src="data:image/jpg;base64,'+result+'" /></p><br/><br/><br/>';
						}else{
							html+='<div style="'+this.resultTitleStyle+'">'+varName+' : </div><br/><p style="'+this.videoStyle+'">'+result+'</p><br/><br/><br/>';
						}
					}
					thisPanel.update(html);
				}

			}else{
				thisPanel.update('');
			}
		};
		
		
		
		service.callService(this, functionSuccess);
	}
	
});