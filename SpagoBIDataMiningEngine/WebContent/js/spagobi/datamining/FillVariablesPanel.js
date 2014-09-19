/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 *     
 *  @author
 *  Monica Franceschini
 */
 
  
Ext.define('Sbi.datamining.FillVariablesPanel', {
	extend: 'Ext.panel.Panel',
	layout: {
        type: 'fit',
        align: 'left'
    },
	
	config:{
		padding: 5
		, border: 0
	},
	
	variablesForm: null,
	caller: 'command',
	callerName: null,
	
	constructor : function(config) {
		
		this.initConfig(config||{});
		this.caller = config.caller;
		this.callerName = config.callerName;
		var buttonAlign = 'bottom';
		var wd = 500;
		if(this.caller == 'output'){
			this.border=1;
			this.style="background: #fff0aa;";
			buttonAlign = 'right';
			wd = 800;
		}
		
		this.variablesForm = Ext.create('Ext.form.Panel', {
		    bodyPadding: 5,
		    width: wd,
		    // Fields will be arranged vertically, stretched to full width
		    layout: 'anchor',
		    defaults: {
		        anchor: '100%'		        
		    },
		    border: 0,
		    // The fields
		    defaultType: 'textfield',
		    items: [],
		    dockedItems: [{
		        xtype: 'toolbar',
		        dock: 'top',
		        items:  [{
			        text: LN('sbi.dm.execution.reset.btn'),
			        handler: function() {
			            this.up('form').getForm().reset();
			        }
			    }, {
			        text: LN('sbi.dm.execution.load.btn'),
			        formBind: true, //only enabled once the form is valid
			        disabled: true,	
			        scale: 'medium',
			        iconCls:'variables_ok',

			        handler: function() {
			        	//this.setVariables(this.variablesForm.getForm(),dataset.name, i)		        	
			        },
			        listeners:{
			        	click:{
			        		fn: function(){
			        			//this.refreshUploadButtons();								        			
			        		}
			        	},scope: this
			        },
			        scope: this
			    }]
		    }],
            scope: this
		});
		this.addEvents('hasVariables');
		this.callParent(arguments);
	},

	initComponent: function() {
		Ext.apply(this, {
			items: [this.variablesForm]
		});
		this.getVariablesFileds();		
		this.callParent();
	}
	,getVariablesFileds: function(){
		
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: this.caller
			,subPath: "getVariables"
			,pathParams: [this.callerName]
		});
		
		service.callService(this);
		
		var functionSuccess = function(response){
			var thisPanel = this;
			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
				var res = Ext.decode(response.responseText);
				
				if(res && Array.isArray(res)){

					for (var i=0; i< res.length; i++){
						var variable = res[i];
						
						var varField= Ext.create("Ext.form.field.Text",{
					        value: variable.defaultVal,
					        name: variable.name,
					        fieldLabel: variable.name,
					        labelWidth: 150,
					        msgTarget: 'side',
					        allowBlank: false,
					        anchor: '100%',
					        border: 0
					    });
						this.variablesForm.add(varField);
					}
					this.fireEvent('hasVariables', true);
				}
			}
			else{
				if(this.caller =='command'){
					var emptyField =Ext.create("Ext.form.field.Display", {
				        xtype: 'displayfield',
				        fieldLabel: 'SpagoBI Dataset label',
				        labelStyle: 'font-weight: bold; color: #28596A;',
				        labelWidth: 150,
				        name: dataset.label,
				        value: 'no variables to display'
				    });
					this.variablesForm.add(emptyField);
					this.add(this.variablesForm);
				}
			}
			
		};
		service.callService(this, functionSuccess);
	}
	
});