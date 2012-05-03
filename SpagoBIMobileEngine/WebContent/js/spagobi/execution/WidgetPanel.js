app.views.WidgetPanel = Ext.extend(Ext.Panel, {
	
	executionInstance : null
	,slider: null
	, dockedItems: []
	                
    ,initComponent: function (options) {

    	app.views.WidgetPanel.superclass.initComponent.apply(this, arguments);
		///to add a slider configuration property
		//this.addSlider();
	}
	
	,
	setExecutionInstance : function (executionInstance) {
		this.executionInstance = executionInstance;
	}
	,showLoadingMask : function(panel){
		this.loadingMask = new Ext.LoadMask(panel.id, {msg:"Loading..."});					
		this.loadingMask.show();
		this.un('afterlayout',this.showLoadingMask,this);
	}
	,
	getExecutionInstance : function () {
		return this.executionInstance;
	}
	, setTargetDocument: function(resp){
		var drill = resp.config.drill;
		var targetDoc = drill.document;
		return targetDoc;
	}
/*	, addSlider: function(){
		var attr = {
			name: 'Slider',
			value: 5,
			minValue: 0,
			maxValue: 10
		};
		this.slider = new app.views.Slider({
			sliderAttributes: attr
		});

		this.dockedItems.items.push(this.slider);
	}*/
});