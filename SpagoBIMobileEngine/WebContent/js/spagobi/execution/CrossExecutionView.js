app.views.CrossExecutionView = Ext.extend(Ext.Panel,
	{
	breadCrumbs: null,
	fullscreen: true,
	layout: 'card',
	cardSwitchAnimation: 'slide',
	dockedItems : [{
        dock: 'bottom',
        xtype: 'toolbar',
        scope: this,
        layout: {
            pack: 'center'
        },
        defaults: {
            iconMask: true,
            ui: 'plain'
        },
        items : [{
		    title: 'Back',    		    
		    iconCls: 'arrow_left',			    
		    text: 'Back',
		    scope: this,
            handler: function () {
	        	Ext.dispatch({
	                controller: app.controllers.mobileController,
	                action: 'backToPreviousViewFromCross'
	    		});

            }},
            {
		    title: 'Home',    		    
		    iconCls: 'reply',			    
		    text: 'Home',
            handler: function () {
        		Ext.dispatch({
                    controller: app.controllers.mobileController,
                    action: 'backToBrowser',
                    fromCross: true
        		});

            }}]
    }],
	initComponent: function() 	  {
		this.breadCrumbs = new Array();

	    app.views.CrossExecutionView.superclass.initComponent.apply(this, arguments);

	  }
	, setBreadCrumb: function(crumb){
		this.breadCrumbs.push(crumb);
	}
	});
		