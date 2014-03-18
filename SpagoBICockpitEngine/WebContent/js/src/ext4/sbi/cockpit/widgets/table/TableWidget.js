/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.widgets.table");

Sbi.cockpit.widgets.table.TableWidget = function(config) {	
	Sbi.trace("[TableWidget.constructor]: IN");
	
	var defaultSettings = {
		displayInfo: false,
		pageSize: 50,
		sortable: false,
		sortMode: 'remote', // remote | local | auto
		layout: 'fit',
		timeout: 300000,
		split: true,
		collapsible: false,
		padding: '0 0 0 0',
		autoScroll: false,
		frame: false, 
		border: false,
		sortable: false,
		gridConfig: {
			height: 400,
			clicksToEdit:1,
		    frame: false,
		    border:false,
		    autoScroll: true,
		    collapsible: false,
		    loadMask: true,
		    viewConfig: {
		    	forceFit:false,
		        autoFill: true,
		        enableRowBody:true,
		        showPreview:true
		    },
		    layout: "fit"
		},
		queryLimit: {
			maxRecords: 1000
			, isBlocking: false
		}
	};
			
	var settings = Sbi.getObjectSettings('Sbi.cockpit.widgets.table.TableWidget', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	this.initServices();
	this.init();
	
	this.addEvents('contentloaded');
	
	c = Ext.apply(c, {
		items: [this.grid]
	});

	
	Sbi.cockpit.widgets.table.TableWidget.superclass.constructor.call(this, c);
	
	this.on("afterRender", function(){
		this.getStore().load();
		//this.refresh();
		Sbi.trace("[TableWidget.onRender]: store loaded");
	}, this);
	
	this.on("beforeDestroy", function(){
		this.unboundStore();
		Sbi.trace("[TableWidget.onBeforeDestroy]: store unbounded");
	}, this);
	
	Sbi.trace("[TableWidget.constructor]: OUT");
};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.table.TableWidget, Sbi.cockpit.core.WidgetRuntime, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
	services: null
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    
	, setStoreId: function(storeId, refresh) {
		Sbi.trace("[TableWidget.setStoreId]: IN");
		
		if(storeId == this.getStoreId()) {
			Sbi.trace("[TableWidget.setStoreId]: New store id is equal to the old one. Nothing to update.");
			Sbi.trace("[TableWidget.setStoreId]: OUT");
			return;
		}

		this.unboundStore();
		
		Sbi.cockpit.widgets.table.TableWidget.superclass.setStoreId.call(this, storeId, false);
		
		this.boundStore();
		
		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.RowNumberer(), {
				header : "Data",
				dataIndex : 'data',
				width : 75
		} ]);
		this.grid.reconfigure(this.getStore(), cm);
		
		if(this.rendered === true && refresh !== false) {
			this.refresh();
		}
			
		Sbi.trace("[TableWidget.setStoreId]: OUT");
	}

	, refresh:  function() {  
		Sbi.trace("[TableWidget.refresh]: IN");
		
		var gridStore = this.grid.getStore();
		if(gridStore) {
			gridStore.id
		}
		
		this.getStore().removeAll();
		this.getStore().baseParams = {};
		var requestParameters = {start: 0, limit: this.pageSize};
		this.getStore().load({params: requestParameters});
		this.doLayout();
		Sbi.trace("[TableWidget.refresh]: OUT");
	}

	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	//------------------------------------------------------------------------------------------------------------------
	// utility methods
	// -----------------------------------------------------------------------------------------------------------------
	, onRender: function(ct, position) {	
		Sbi.trace("[TableWidget.onRender]: IN");
		
		this.msg = 'Sono un widget di tipo TABLE';
		
		Sbi.cockpit.widgets.table.TableWidget.superclass.onRender.call(this, ct, position);	
		
		Sbi.trace("[TableWidget.onRender]: OUT");
	}
	
	
	, onStoreLoad: function(store) {
		Sbi.trace("[TableWidget.onStoreLoad]: IN");
		
		this.fireEvent('contentloaded');
		
		var recordsNumber = store.getTotalCount();
     	if(recordsNumber == 0) {
     		Ext.Msg.show({
				   title: LN('sbi.qbe.messagewin.info.title'),
				   msg: LN('sbi.qbe.datastorepanel.grid.emptywarningmsg'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.INFO,
				   modal: false
			});
     	}
     	 
     	if (this.queryLimit.maxRecords !== undefined && recordsNumber > this.queryLimit.maxRecords) {
     		if (this.queryLimit.isBlocking) {
     			Sbi.exception.ExceptionHandler.showErrorMessage(this.warningMessageItem, LN('sbi.qbe.messagewin.error.title'));
     		} else {
     			this.warningMessageItem.show();
     		}
     	} else {
     		this.warningMessageItem.hide();
     	}
     	Sbi.trace("[TableWidget.onStoreLoad]: OUT");		
	}
	
	, onStoreLoadException: function(response, options) {
		this.fireEvent('contentloaded');
		Sbi.exception.ExceptionHandler.handleFailure(response, options);
	}
	
	, onStoreMetaChange: function(store, meta) {
		Sbi.trace("[TableWidget.onStoreMetaChange]: IN");	
		var fields = new Array();
		fields.push(new Ext.grid.RowNumberer());
		
		var columns = [];
		
		for(var i = 0; i < meta.fields.length; i++) {
			this.applyRendererOnField(meta.fields[i]);
			this.applySortableOnField(meta.fields[i]);
			Sbi.trace("[TableWidget.onStoreMetaChange]: checking if field [" + Sbi.toSource(meta.fields[i]) + "] is visible ...");	
			for(var j = 0; j < this.wconf.visibleselectfields.length; j++) {
				if(this.wconf.visibleselectfields[j].id === meta.fields[i].header) {
					Sbi.trace("[TableWidget.onStoreMetaChange]: field [" + meta.fields[i].header + "] is equal to [" + this.wconf.visibleselectfields[j].id + "]");	
					fields.push(meta.fields[i]);
					columns.push(meta.fields[i].header);
					break;
				} else {
					Sbi.trace("[TableWidget.onStoreMetaChange]: field [" + meta.fields[i].header + "] is not equal to [" + this.wconf.visibleselectfields[j].id + "]");	
				}
			}
		}
		Sbi.trace("[TableWidget.onStoreMetaChange]: visible fields are [" + columns.join(",") + "]");
		this.grid.getColumnModel().setConfig(fields);
		Sbi.trace("[TableWidget.onStoreMetaChange]: OUT");	
	}
	
	, applyRendererOnField: function(field) {
		Sbi.trace("[TableWidget.applyRendererOnField]: IN");	
		if(field.type) {
			var t = field.type;
			if (field.format) { // format is applied only to numbers
				var format = Sbi.commons.Format.getFormatFromJavaPattern(field.format);
				var formatDataSet = field.format;
				if((typeof formatDataSet == "string") || (typeof formatDataSet == "String")){
					try {
						formatDataSet =  Ext.decode(field.format);
					} catch(e) {
						formatDataSet = field.format;
					}
				}
				var f = Ext.apply( {}, Sbi.locale.formats[t]);
				f = Ext.apply( f, formatDataSet);
	
				numberFormatterFunction = Sbi.qbe.commons.Format.numberRenderer(f);
			} else {
				numberFormatterFunction = Sbi.locale.formatters[t];
			}	
			
			if (field.measureScaleFactor && (t === 'float' || t ==='int')) { // format is applied only to numbers
			   this.applyScaleRendererOnField(numberFormatterFunction,field);
			} else {
			   field.renderer = numberFormatterFunction;
			}
		}
		
		if(field.subtype && field.subtype === 'html') {
		   field.renderer  =  Sbi.locale.formatters['html'];
		}
		
		if(field.subtype && field.subtype === 'timestamp') {
		   field.renderer  =  Sbi.locale.formatters['timestamp'];
		}
		
		Sbi.trace("[TableWidget.applyRendererOnField]: OUT");	
	}
	
	, applyScaleRendererOnField: function(numberFormatterFunction, field) {
		
		Sbi.trace("[TableWidget.applyScaleRendererOnField]: IN");	
		
		var scaleFactor = field.measureScaleFactor;
		
		if(scaleFactor!=null && scaleFactor!=null && scaleFactor!='NONE'){
			var scaleFactorNumber;
			switch (scaleFactor){
				case 'K':
					scaleFactorNumber=1000;
					break;
				case 'M':
					scaleFactorNumber=1000000;
					break;
				case 'G':
					scaleFactorNumber=1000000000;
					break;
				default:
					scaleFactorNumber=1;
			}
		
			field.renderer = function(v){
				 var scaledValue = v/scaleFactorNumber;
				 return numberFormatterFunction.call(this,scaledValue);	
			};
			
			field.header = field.header +' '+ LN('sbi.worksheet.config.options.measurepresentation.'+scaleFactor);
		} else {
			field.renderer =numberFormatterFunction;
		}
		
		Sbi.trace("[TableWidget.applyScaleRendererOnField]: OUT");	
	}
	
	, applySortableOnField: function(field) {
		Sbi.trace("[TableWidget.applySortableOnField]: IN");	
		if(this.sortable === false) {
		   field.sortable = false;
		} else {
		   if(field.sortable === undefined) { // keep server value if defined
			   field.sortable = true;
		   }
		}
		Sbi.trace("[TableWidget.applySortableOnField]: OUT");	
	}
	
	
   // -----------------------------------------------------------------------------------------------------------------
   // init methods
   // -----------------------------------------------------------------------------------------------------------------
   
	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - none
	 */
	, initServices: function() {
		this.services = this.services || new Array();	
		this.services['loadDataStore'] = this.services['loadDataStore'] || Sbi.config.serviceRegistry.getServiceUrl({
			serviceName : 'api/1.0/dataset/' + this.getStoreId() + '/data'
			, baseParams: new Object()
		});
		
		this.services['exportDataStore'] = this.services['exportDataStore'] || Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXPORT_RESULT_ACTION'
			, baseParams: new Object()
		});
	}


	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		Sbi.trace("[TableWidget.init]: IN");
		this.boundStore();
		this.initGridPanel();
		Sbi.trace("[TableWidget.init]: OUT");
	}
	
	/**
	 * @method 
	 * 
	 * Initialize the store
	 */
	, boundStore: function() {
		Sbi.trace("[TableWidget.boundStore]: IN");		
		this.getStore().on('metachange', this.onStoreMetaChange, this);
		this.getStore().on('load', this.onStoreLoad, this);
		Sbi.trace("[TableWidget.boundStore]: OUT");
	}
	
	, unboundStore: function() {
		Sbi.trace("[TableWidget.unboundStore]: IN");		
		this.getStore().un('metachange', this.onStoreMetaChange, this);
		this.getStore().un('load', this.onStoreLoad, this);
		Sbi.trace("[TableWidget.unboundStore]: OUT");
	}

	/**
	 * @method 
	 * 
	 * Initialize the grid
	 */
	, initGridPanel: function() {
		Sbi.trace("[TableWidget.initGridPanel]: IN");
		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.RowNumberer(), 
			{
				header: "Data",
				dataIndex: 'data',
				width: 75
			}
		]);
		
		
		this.exportTBar = new Ext.Toolbar({
			items: [
			    new Ext.Toolbar.Button({
		            tooltip: LN('sbi.qbe.datastorepanel.button.tt.exportto') + ' pdf',
		            iconCls:'pdf',
		            //handler: this.exportResult.createDelegate(this, ['application/pdf']),
		            handler: function(){Ext.Msg.alert('Message', 'Export to pdf');},
		            scope: this
			    }),
			    new Ext.Toolbar.Button({
		            tooltip:LN('sbi.qbe.datastorepanel.button.tt.exportto') + ' rtf',
		            iconCls:'rtf',
		            //handler: this.exportResult.createDelegate(this, ['application/rtf']),
		            handler: function(){Ext.Msg.alert('Message', 'Export to rtf');},
		            scope: this
			    }),
			    new Ext.Toolbar.Button({
		            tooltip:LN('sbi.qbe.datastorepanel.button.tt.exportto') + ' xls',
		            iconCls:'xls',
		            //handler: this.exportResult.createDelegate(this, ['application/vnd.ms-excel']),
		            handler: function(){Ext.Msg.alert('Message', 'Export to xls');},
		            scope: this
			    }),
			    new Ext.Toolbar.Button({
		            tooltip:LN('sbi.qbe.datastorepanel.button.tt.exportto') + ' csv',
		            iconCls:'csv',
		            //handler: this.exportResult.createDelegate(this, ['text/csv']),
		            handler: function(){Ext.Msg.alert('Message', 'Export to csv');},
		            scope: this
			    }),
			    new Ext.Toolbar.Button({
		            tooltip:LN('sbi.qbe.datastorepanel.button.tt.exportto') + ' jrxml',
		            iconCls:'jrxml',
		            //handler: this.exportResult.createDelegate(this, ['text/jrxml']),
		            handler: function(){Ext.Msg.alert('Message', 'Export to jrxml');},
		            scope: this
			    })
			]
		});
		
		this.warningMessageItem = new Ext.Toolbar.TextItem('<font color="red">' 
				+ LN('sbi.qbe.datastorepanel.grid.beforeoverflow') 
				+ ' [' + this.queryLimit.maxRecords + '] '
				+ LN('sbi.qbe.datastorepanel.grid.afteroverflow') 
				+ '</font>');
		
		
		this.pagingTBar = new Ext.PagingToolbar({
            pageSize: this.pageSize,
            store: this.getStore(),
            displayInfo: this.displayInfo,
            displayMsg: LN('sbi.qbe.datastorepanel.grid.displaymsg'),
            emptyMsg: LN('sbi.qbe.datastorepanel.grid.emptymsg'),
            beforePageText: LN('sbi.qbe.datastorepanel.grid.beforepagetext'),
            afterPageText: LN('sbi.qbe.datastorepanel.grid.afterpagetext'),
            firstText: LN('sbi.qbe.datastorepanel.grid.firsttext'),
            prevText: LN('sbi.qbe.datastorepanel.grid.prevtext'),
            nextText: LN('sbi.qbe.datastorepanel.grid.nexttext'),
            lastText: LN('sbi.qbe.datastorepanel.grid.lasttext'),
            refreshText: LN('sbi.qbe.datastorepanel.grid.refreshtext')
        });
		this.pagingTBar.on('render', function() {
			this.pagingTBar.addItem(this.warningMessageItem);
			this.warningMessageItem.setVisible(false);
		}, this);
		
		var gridConf = {};
		if(this.gridConfig!=null){
			gridConf = this.gridConfig;
		}
		
		// create the Grid
	    this.grid = new Ext.grid.GridPanel(Ext.apply({
	    	store: this.getStore(),
	        cm: cm
	        //tbar:this.exportTBar,
	        //bbar: this.pagingTBar
	    },gridConf));   
	    
	    Sbi.trace("[TableWidget.initGridPanel]: OUT");
	}
});


Sbi.registerWidget('table', {
	name: 'Table'
	, icon: 'js/src/ext/sbi/cockpit/widgets/table/table_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.table.TableWidget'
	, designerClass: 'Sbi.cockpit.widgets.table.TableWidgetDesigner'
	//, designerClass: 'Ext.Panel'
});