/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 app.views.Slider = Ext.extend(Ext.form.Slider,

		{
	    ui: 'light',
	    id: 'mobileSlider',
	    xtype: 'sliderfield',
	    style: 'float: left; width:85%;',
	    increment: 1,
		mySliderTooltip : new Ext.Panel({
				floating: true,
				width: 50,
				height: 30,
				styleHtmlContent: true,
				style: "background-color: #FFF;"
			}),
        tipText: function(thumb){
            return Ext.String.format('<b>{0}% complete</b>', thumb.value);
        },
	    listeners: {
	        change: function(slider, thumb, value, oldvalue) {
		    	if (value && value!= oldvalue) {

		    		var params = {};
		    		var name = slider.name;
		    		params[name]= value;
		    		var items =[];
		    		try{
		    			items = app.views.composed.items.items;
		    		}catch(err){
		    			items = this.ownerCt.items.items;
		    		}

		    		for(i =0; i < items.length; i++){
		    			var panel = items[i];

		    			var executionInstance = panel.executionInstance;
		    			if(executionInstance && executionInstance.PARAMETERS){
		    				app.controllers.composedExecutionController.refreshSubDocument(panel, params);
		    			}

		    		}
	            }
	        }
			,drag: function (theSlider, theThumb, ThumbValue) {

				theSlider.mySliderTooltip.showBy(theThumb);
				theSlider.mySliderTooltip.el.setHTML(ThumbValue);
			},
			dragend: function (theSlider, theThumb, ThumbValue) {
				theSlider.mySliderTooltip.hide();
			},
			scope: this
	    },
	    layout: {
	        type: 'vbox',
	        padding: '5',
	        align: 'bottom'
	    },
	    dockedItems: [],
		initComponent: function ()	{
			console.log('init chart slider');
			var attributes = this.sliderAttributes;
			
			this.maxValue = parseInt(attributes.maxValue);
			this.minValue = parseInt(attributes.minValue);
			this.name = attributes.name;
			if(attributes.value !== undefined){
				this.value = parseInt(attributes.value);
			}
			if(attributes.increment !== undefined){
				this.increment = parseInt(attributes.increment);
			}

			//this.label = attributes.label; //not nice to see...
			
			app.views.Slider.superclass.initComponent.apply(this, arguments);
			
		}

});