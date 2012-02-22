app.views.WidgetPanel = Ext.extend(Ext.Panel, {
	
	executionInstance : null

    ,
    initComponent: function (options) {
    	app.views.WidgetPanel.superclass.initComponent.apply(this, arguments);
	}
	
	,
	setExecutionInstance : function (executionInstance) {
		this.executionInstance = executionInstance;
	}
	
	,
	getExecutionInstance : function () {
		return this.executionInstance;
	}

});