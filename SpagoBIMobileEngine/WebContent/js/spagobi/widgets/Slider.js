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
		    tooltip: null,
		    listeners: {
		        change: function( me, sl, thumb, newValue, oldValue, eOpts) {
			    	if (newValue && newValue!= oldValue) {
	
			    		var params = {};
			    		var name = me.getName();
			    		params.name= name;
			    		params.value = newValue;

			    		this.composedDoc.propagateCrossNavigationEventForSlider([params]);

		            }
		        }

				, dragend: function( me, sl, thumb, value, e, eOpts ){
					this.tooltip.setHtml(value);
					this.tooltip.showBy(thumb);
					// executes hide after 2 seconds:					
					Ext.Function.defer(function(){
						this.tooltip.hide();
						this.tooltip.setHtml("");
					}, 2000, this);
					
				}
		    }
		},
	    constructor: function(config){
	    	Ext.apply(this,config||{});	    	
	    	this.callParent(arguments);
	    },
	    initialize: function ()	{
			console.log('init chart slider');
			this.tooltip = Ext.create('Ext.Panel', {
			     html: 'Floating Panel',
			     left: 0,
			     padding: 10
			 });
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