Ext.define('Sbi.tools.dataset.DataSetsBrowser', {
	extend : 'Ext.Panel'

	,
	config : {
		autoLoad : true,
		modelName : "Sbi.tools.dataset.DataSetModel",
		dataView : null,
		tbar : null,
		height: 600
	}

	,
	constructor : function(config) {
		
		this.initServices();
		this.initStore();
		this.initToolbar();
		this.initViewPanel();
		this.layout='fit';
		
		this.callParent(arguments);
		this.addListener('cancel', this.closeWin, this);
		this.addListener('navigate', this.navigate, this);
	}

	,
	initServices : function(baseParams) {
		this.services = [];
		this.services["list"] = Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName : 'selfservicedataset',
			baseParams : baseParams
		});
	}
	
	,
	initStore : function(baseParams) {
		this.columns = [ {
			dataIndex : "label",
			header : "Name"
		}, {
			dataIndex : "description",
			header : "Description"
		} ];
	
		// set the proxy of the model.. Is STATIC
		var model = Ext.ModelMgr.getModel(this.getModelName());
		model.setProxy({
			type : 'rest',
			url : Sbi.config.serviceRegistry.getRestServiceUrl({
				serviceName : 'selfservicedataset'
			}),
			reader : {
				type : 'json',
				root : 'root'
			}
		});
		
		this.fields = [ "id", "label", "name", "description" ];
		this.filteredProperties = [ "label", "name" ];
		
		Sbi.debug('DataViewPanel bulding the store...');

		this.storeConfig = Ext.apply({
			model : this.getModelName(),
			filteredProperties : [ "label", "name" ]
		}, {});

		// creates and returns the store
		Sbi.debug('DataViewPanel store built.');

		this.store = Ext.create('Sbi.widgets.store.InMemoryFilteredStore', this.storeConfig);
				
		this.store.load({});
	}
	
	, initToolbar: function() {
		var newDatasetButton = new Ext.button.Button({
	    	tooltip: LN('sbi.generic.add'),
			iconCls:'icon-add',
			width:50,
			listeners: {
				'click': {
	          		fn: this.addNewDataset,
	          		scope: this
	        	} 
			}
	    });
	     
		var toolbar =  Ext.create('Ext.toolbar.Toolbar',{renderTo: Ext.getBody(),height:30});
		toolbar.add('->');
		toolbar.add(' ');
		toolbar.add(newDatasetButton);
	
		this.tbar = toolbar;
	}
	
	,
	initViewPanel: function() {
		var config = {};
		config.services = this.services;
		config.store = this.store;
		this.viewPanel = Ext.create('Sbi.tools.dataset.DataSetsView', config);
	}

	,
	onRender : function(obj, opt) {
		Sbi.widgets.dataview.DataViewPanel.superclass.onRender.call(this, opt);
	}

	,
	addNewDataset : function() {		 
		this.wizardWin =  Ext.create('Sbi.tools.dataset.DataSetsWizard',{});		
    	this.wizardWin.show();
    	this.wizardWin.on('navigate', function(panel, direction) {
			alert("navigate!!");
			this.navigate(panel, direction);
		}, this);
		this.wizardWin.on('closeWin', function() {
			alert("closeWin!!");
			this.wizardWin.hide();
		}, this);
	}
	
	
	, navigate: function(panel, direction){		
		alert("navigate! " + panel + " - " + direction);
		
        // This routine could contain business logic required to manage the navigation steps.
        // It would call setActiveItem as needed, manage navigation button state, handle any
         // branching logic that might be required, handle alternate actions like cancellation
         // or finalization, etc.  A complete wizard implementation could get pretty
         // sophisticated depending on the complexity required, and should probably be
         // done as a subclass of CardLayout in a real-world implementation.
		 var layout = panel.getLayout();
		 layout[direction]();
		 Ext.getCmp('move-prev').setDisabled(!layout.getPrev());
		 Ext.getCmp('move-next').setDisabled(!layout.getNext());
	}
	
	, closeWin: function(){
		alert("close WIn");
	}

});
