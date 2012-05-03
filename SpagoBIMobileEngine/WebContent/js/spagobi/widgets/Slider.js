app.views.Slider = Ext.extend(Ext.form.Slider,

		{
	    ui: 'light',
	    id: 'mobileSlider',
	    xtype: 'sliderfield',
	    name: 'year',
	    maxValue : 2009,
	    style: 'float: left; width:100%;',
	    minValue : 1960,
	    value: 2009
	    ,listeners: {
	        change: function(slider, thumb, value) {
		 /* if (value) {
	            	alert('ciao');
	            }*/
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
			
			this.maxValue = attributes.maxValue;
			this.minValue = attributes.minValue;
			this.name = attributes.name;
			this.value = attributes.value;
			
			app.views.Slider.superclass.initComponent.apply(this, arguments);
			
		}

});