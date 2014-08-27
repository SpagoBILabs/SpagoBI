/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Container of all the UI of the olap engine.<br>
 * It contains:
 * <ul>
 *		<li>View definition tools</li>
 *		<li>Table/Chart</li>
 *		<li>Options</li>
 *	</ul>
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */
 
  
Ext.define('Sbi.datamining.ResultPanel', {
	extend: 'Ext.panel.Panel',
	layout: {
        type: 'fit'
		,flex: 1
		, minWidth: 500
    },
	
	config:{
		html: ' '
	},
	
	dataminingParentPanel: null,
	
	type: null,
	result: '',
	
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
			
			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
			
				var res = Ext.decode(response.responseText);				
				
				this.type = res.outputType;
				this.result = res.result;
				
				if(this.type == 'plot'){
					thisPanel.update('<div style="font-size:bold; color: red;">Result</div><br/><img alt="Result" src="data:image/jpg;base64,'+this.result+'" />');
				}else{
					thisPanel.update('<div style="font-size:bold; color: red;">Result</div><br/>'+this.result);
				}
			}else{
				thisPanel.update('');
			}
		};
		
		
		
		service.callService(this, functionSuccess);
	}
	
});