app.views.Slider = Ext.extend(Ext.form.Slider,

		{
	    ui: 'light',
	    id: 'mobileSlider',
	    xtype: 'sliderfield',
	    style: 'float: left; width:85%;',
	    increment: 1,
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