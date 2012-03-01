app.views.CrossExecutionView = Ext.extend(Ext.Panel,
	{
	fullscreen: true,
	layout: 'card',	
    sortable: false,
	cardSwitchAnimation: 'slide'
	,ui : 'dark'
	,breadCrumbs: new Array()

	,initComponent: function() 	  {
	
		this.docHome = {
		    title: 'Home',    		    
		    iconCls: 'reply',			    
		    text: 'Home',
            handler: function () {
        		Ext.dispatch({
                    controller: app.controllers.mobileController,
                    action: 'backToBrowser',
                    fromCross: true
        		});

            }};
		this.toolbarForCross = new Ext.Toolbar({xtype: 'toolbar',
	        dock: 'bottom',
	        defaults: {
	            ui: 'plain',
	            iconMask: true
	        },
	        scroll: 'horizontal',
	        layout: {
	            pack: 'center'
	        }
	        ,items:[this.docHome]
		});
		this.dockedItems=[this.toolbarForCross];
	    app.views.CrossExecutionView.superclass.initComponent.apply(this, arguments);

	}
	, setBreadCrumb: function(objectLabel, 
								objectId,
								typeCode,
								parameters){
		if(this.breadCrumbs.indexOf(objectLabel) == -1){

			this.breadCrumbs.push(objectLabel);
			var pos = this.breadCrumbs.length;
			this.toolbarForCross.insert(pos,{
				title: objectLabel,    		    
			    iconCls: 'arrow_left',			    
			    text: objectLabel,
	            handler: function () {
	  			Ext.dispatch({
					  controller: app.controllers.mobileController,
					  action: 'getRoles',
					  label: objectLabel, 
					  id: objectId,
					  typeCode: typeCode,
					  parameters: parameters,
					  isFromCross: true
				});
	
	            }
			});
			this.toolbarForCross.doLayout();
			this.doLayout();
		}
	}
});
		