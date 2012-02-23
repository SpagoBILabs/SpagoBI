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
	,showLoadingMask : function(panel){
		this.loadingMask = new Ext.LoadMask(panel.id, {msg:"Loading table..."});					
		this.loadingMask.show();
		this.un('afterlayout',this.showLoadingMask,this);
	}
	,
	getExecutionInstance : function () {
		return this.executionInstance;
	}

});