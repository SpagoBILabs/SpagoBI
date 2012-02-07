app.views.TableExecution = Ext.extend(Ext.ux.TouchGridPanel,

		{
		fullscreen  : true,
	    plugins    : [new Ext.ux.touch.PagingToolbar()],
	    
		multiSelect : false,
		dockedItems : [{
			xtype : "toolbar",
			dock  : "top",
			title : null,
			style:  null
		}],
		conditions  : null,
		colModel    : null
		
		, initComponent: function (options)	{

			console.log('init table execution');
		    
			app.views.TableExecution.superclass.initComponent.apply(this, arguments);
			
		}

		
});