/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
Ext.define('app.views.Slider',{
		extend:'Ext.field.Slider',
		config:{
		    ui: 'light',
		    id: 'mobileSlider',
		    xtype: 'slider',
		    style: 'float: left; width:80%; height: 30px;',
		    left:'10%',
		    fullscreen: true,
		    increment: 1,
		    bottom: 0,
		    listeners: {
		        change: function( me, sl, thumb, newValue, oldValue, eOpts) {
			    	if (newValue && newValue!= oldValue) {
	
			    		var params = {};
			    		var name = me.getName();
			    		params[name]= newValue;
			    		var subdocs =this.composedDoc.getSubdocuments();
	
			    		for(i =0; i < subdocs.length; i++){
			    			var panel = subdocs[i];
			    			
			    			var executionInstance = panel.executionInstance;
			    			if(executionInstance && executionInstance.PARAMETERS){
			    				app.controllers.composedExecutionController.refreshSubDocument(panel, this.composedDoc, params);
			    			}
	
			    		}
		            }
		        }
		    }
		},
	    constructor: function(config){
	    	Ext.apply(this,config||{});
	    	this.callParent(arguments);
	    },
	    initialize: function ()	{
			console.log('init chart slider');
			var attributes = this.config.sliderAttributes;
			this.composedDoc = this.config.composedDoc;
			
			this.setMaxValue(parseInt(attributes.maxValue));
			this.setMinValue(parseInt(attributes.minValue));
			this.setName(attributes.name);
			if(attributes.value !== undefined){
				this.setValue(parseInt(attributes.value));
				//this.originalValue = parseInt(attributes.value);
			}
			if(attributes.increment !== undefined){
				this.setIncrement(parseInt(attributes.increment));
			}

			//this.label = attributes.label; //not nice to see...
			
			this.callParent(arguments);
			
		}

});