/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 

Ext.ns("Sbi.geo");

Sbi.geo.ControlPanel = function(config) {
	
	var defaultSettings = {
		title       : LN('sbi.geo.controlpanel.title'),
		region      : 'east',
		split       : true,
		width       : 315,
		collapsible : true,
		collapsed   : true,
		margins     : '3 0 3 3',
		cmargins    : '3 3 3 3',
		autoScroll	 : true,
		earthPanelConf: {},
		layerPanelConf: {},
		analysisPanelConf: {},
		measurePanelConf: {},
		logoPanelConf: {},
		legendPanelConf: {},
		debugPanelConf: {}
		
	};
	
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.controlPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.controlPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
	
	this.initControls();
	
	c = Ext.apply(c, {
	     items: this.controlPanelItemsConfig 
	});
		
	// constructor
	Sbi.geo.ControlPanel.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.geo.ControlPanel
 * @extends Ext.Panel
 * 
 * ...
 */
Ext.extend(Sbi.geo.ControlPanel, Ext.Panel, {
    
	controlPanelItemsConfig: null
	
	, earthPanelEnabled: false
	, layerPanelEnabled: false
	, analysisPanelEnabled: false
	, measurePanelEnabled: false
	, logoPanelEnabled: false
	, legendPanelEnabled: false
	, debugPanelEnabled: false
	
	, earthPanelConf: null
	, layerPanelConf: null
	, analysisPanelConf: null
	, measurePanelConf: null
	, logoPanelConf: null
	, legendPanelConf: null
	, debugPanelConf: null
   
	, layersControlPanel: null
	, analysisControlPanel: null
	, measureControlPanel: null
	, legendControlPanel: null
	, logoControlPanel: null
	, debugControlPanel: null
	
	, resultsetWindow: null
	, measureCatalogueWindow: null
	, shareMapWindow: null
   
    // public methods
    
    // private methods
	
	, initControls: function() {
		
		this.controlPanelItemsConfig = [];
	
		this.initEarthControlPanel();
		this.initLayersControlPanel();
		this.initMeasureControlPanel();
		this.initAnalysisControlPanel();
		this.initLegendControlPanel();
		this.initLogoControlPanel();
		this.initDebugControlPanel();

	}
	
	, initEarthControlPanel: function() {
		if(this.earthPanelEnabled === true) {
			this.controlPanelItemsConfig.push({
				title: LN('sbi.geo.earthpanel.title'),
				collapsible: false,
				split: true,
				height: 300,
				minSize: 300,
				maxSize: 500,
				html: '<center id="map3dContainer"></center>'
			});
		}
	}

	, initLayersControlPanel: function() {
		
		if(this.layerPanelEnabled === true) {			
			
			this.layersControlPanel = new mapfish.widgets.LayerTree(Ext.apply({
	        	title: LN('sbi.geo.layerpanel.title'),
	            collapsible: true,
	            collapsed: false,
	            autoHeight: true,
	            rootVisible: false,
	            separator: '!',
	            model: this.extractModel(),
	            map: this.map,
	            bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF'
	        }, this.layerPanelConf));
			
			this.map.layerTree = this.layersControlPanel;
			
			this.controlPanelItemsConfig.push(this.layersControlPanel);
		}
	}
	
	, initAnalysisControlPanel: function() {
		
		if(this.analysisPanelEnabled === true) {
			
			this.analysisControlPanel = new Ext.Panel(Ext.apply({
	        	title: LN('sbi.geo.analysispanel.title'),
	            collapsible: true,
	            bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF',
	            items: [this.geostatistic]
	        }, this.analysisPanelConf));
			
			this.geostatistic.on('ready', function(){
				Sbi.debug("[AnalysisControlPanel]: [ready] event fired");
				this.setAnalysisConf( this.geostatistic.analysisConf );
			}, this);
			
			this.controlPanelItemsConfig.push(this.analysisControlPanel);
		}
	}
	
	, setAnalysisConf: function(analysisConf) {
		Sbi.debug("[ControlPanel.setAnalysisConf]: IN");
		
		Sbi.debug("[ControlPanel.setAnalysisConf]: analysisConf = " + Sbi.toSource(analysisConf));
		
		var formState = Ext.apply({}, analysisConf || {});
		
		formState.method = formState.method || 'CLASSIFY_BY_QUANTILS';
		formState.classes =  formState.classes || 5;
		
		formState.fromColor =  formState.fromColor || '#FFFF99';
		formState.toColor =  formState.toColor || '#FF6600';
	
		if(formState.indicator && this.indicatorContainer === 'layer') {
			formState.indicator = formState.indicator.toUpperCase();
		}
		if(!formState.indicator && this.geostatistic.indicators && this.geostatistic.indicators.length > 0) {
			formState.indicator = this.geostatistic.indicators[0][0];
		}
		
		this.geostatistic.setFormState(formState, true);
		
		Sbi.debug("[ControlPanel.setAnalysisConf]: OUT");
	}
	
	, getAnalysisConf: function() {
		return this.geostatistic.getFormState();
	}
	
	, initMeasureControlPanel: function() {
		
		
		if(this.measurePanelEnabled === true) {
			
			this.measureControlPanel = new Ext.Panel(Ext.apply({
				 id: 'mapOutput',
	             title: 'Misurazione',
	             collapsible: true,
	             collapsed: false,
	             height: 50,
	             html: '<center></center>'
			 }, this.measurePanelConf));
				
			this.controlPanelItemsConfig.push(this.measureControlPanel);
		}
	}
	
	, initLegendControlPanel: function() {
		if(this.legendPanelEnabled === true) {
			
			this.legendControlPanel = new Ext.Panel(Ext.apply({
		           title: LN('sbi.geo.legendpanel.title'),
		           collapsible: true,
		           bodyStyle:'padding:6px 6px 6px 6px; background-color:#FFFFFF',
		           height: 180,
		           autoScroll: true,
		           html: '<center id="myChoroplethLegendDiv"></center>'
		     },this.legendPanelConf));
					
			this.controlPanelItemsConfig.push(this.legendControlPanel);
		}
	}
	
	, initLogoControlPanel: function() {
		if(this.logoPanelEnabled === true) {
			
			this.logoControlPanel = new Ext.Panel(Ext.apply({
		           title: 'Logo',
		           collapsible: true,
		           height: 85,
		           html: '<center><img src="/SpagoBIGeoReportEngine/img/georeport.jpg" alt="GeoReport"/></center>'
			 },this.logoPanelConf));
				
			this.controlPanelItemsConfig.push(this.logoControlPanel);
		}
	}
	
	, initDebugControlPanel: function() {
		if(this.debugPanelEnabled === true) {
			
			this.store = this.debugPanelConf.store;
			this.initResultSetWin();
			
			var mapper = this.map;

			
			this.debugControlPanel = new Ext.Panel({
		           title: 'Debug',
		           collapsible: true,
		           height: 120,
		           items: [new Ext.Button({
				    	text: 'Reload dataset',
				        width: 30,
				        handler: function() {
				        	if(this.resultsetWindow != null) {
				        		this.resultsetWindow.show();
					        	var p = Ext.apply({}, this.params, {
					    			//start: this.start
					    			//, limit: this.limit
					    		});
					    		this.store.load({params: p});
				        	} else {
				        		alert("No physical dataset associated to the map. This can happen if the indicatr conatiner is of type layer or if the indictaor container is of type store but the store type is equal to visrtual store");
				        	}
		           		},
		           		scope: this
				    }), new Ext.Button({
				    	text: 'Measure Catalogue',
				        width: 30,
				        handler: function() {
				        	this.showMeasureCatalogueWindow();
		           		},
		           		scope: this
				    }), new Ext.Button({
				    	text: 'Share map as link',
				        width: 30,
				        disabled :(Sbi.config.docLabel=="")?true:false,
				        handler: function() {
				        	this.showShareMapWindow('link');
		           		},
		           		scope: this
				    }), new Ext.Button({
				    	text: 'Share map as html',
				        width: 30,
				        disabled :(Sbi.config.docLabel=="")?true:false,
				        handler: function() {
				        	this.showShareMapWindow('html');
		           		},
		           		scope: this
				    }), new Ext.Button({
				    	text: 'Send Feedback',
				        width: 30,
				        disabled :(Sbi.config.docLabel=="")?true:false,
				        handler: function() {
				        	this.showFeedbackWindow();
		           		},
		           		scope: this
				    }), new Ext.Button({
				    	text: 'Export',
				        width: 30,
				        disabled :(Sbi.config.docLabel=="")?true:false,
				        handler: function() {
				        	var printProvider = new GeoExt.data.PrintProvider({
			                    capabilities: {"scales":[{"name":"1:25.000","value":"25000"},{"name":"1:50.000","value":"50000"},{"name":"1:100.000","value":"100000"},{"name":"1:200.000","value":"200000"},{"name":"1:500.000","value":"500000"},{"name":"1:1.000.000","value":"1000000"},{"name":"1:2.000.000","value":"2000000"},{"name":"1:4.000.000","value":"4000000"}],"dpis":[{"name":"56","value":"56"},{"name":"127","value":"127"},{"name":"190","value":"190"},{"name":"256","value":"256"}],"outputFormats":[{"name":"pdf"}],"layouts":[{"name":"A4 portrait","map":{"width":440,"height":483},"rotation":true}],"printURL":"http://localhost:8080/SpagoBIGeoReportEngine/pdf/print.pdf","createURL":"http://localhost:8080/SpagoBIGeoReportEngine/pdf/create.json"},
			                    customParams: {
			                        mapTitle: "Printing Demo",
			                        title: "Printing Demo",
			                        comment: "This is a simple map printed from GeoExt."
			                    }
				        	});
				        	            var printPage = new GeoExt.data.PrintPage({
				        	                printProvider: printProvider
				        	            });
				        	            printPage.fit(mapper, true);
				        	            printProvider.print(mapper, printPage);
				        	        
				        	    
		           		},
		           		scope: this
				    })
		           ]
		    });
			
			
			this.controlPanelItemsConfig.push(this.debugControlPanel);
			
			
			
			
//			var exportPanel  = new Ext.Window({
//	            autoHeight: true,
//	            width: 350,
//	            items: [new GeoExt.PrintMapPanel({
//	                sourceMap: 	mapper,
//	                printProvider: {
//	                    capabilities: {"scales":[{"name":"1:25,000","value":"25000"},{"name":"1:50,000","value":"50000"},{"name":"1:100,000","value":"100000"},{"name":"1:200,000","value":"200000"},{"name":"1:500,000","value":"500000"},{"name":"1:1,000,000","value":"1000000"},{"name":"1:2,000,000","value":"2000000"},{"name":"1:4,000,000","value":"4000000"}],"dpis":[{"name":"254","value":"254"},{"name":"190","value":"190"}],"layouts":[{"name":"A4 portrait","map":{"width":440,"height":483},"rotation":true},{"name":"Legal","map":{"width":440,"height":483},"rotation":false}],"printURL":"http://localhost:8080/SpagoBIGeoReportEngine/pdf/print.pdf","createURL":"http://localhost:8080/SpagoBIGeoReportEngine/pdf/create.json"},
//	                    customParams: {
//	                        mapTitle: "Printing Demo",
//	                        title: "Printing Demo",
//	                        comment: "This is a simple map printed from GeoExt."
//	                    }
//	                }
//	            })],
//	            bbar: [{
//	                text: "Create PDF",
//	                handler: function() {
//	                	exportPanel.items.get(0).print();
//	                }
//	            }]
//	        });
//			exportPanel.show();
//		}
		}

	}


	
	, showMeasureCatalogueWindow: function(){
		if(this.measureCatalogueWindow==null){
			var measureCatalogue = new Sbi.geo.tools.MeasureCatalogue();
			measureCatalogue.on('storeLoad', this.onStoreLoad, this);
			
			this.measureCatalogueWindow = new Ext.Window({
	            layout      : 'fit',
		        width		: 700,
		        height		: 350,
	            closeAction :'hide',
	            plain       : true,
	            title		: OpenLayers.Lang.translate('sbi.tools.catalogue.measures.window.title'),
	            items       : [measureCatalogue]
			});
		}
		
		
		this.measureCatalogueWindow.show();
	}
	, showFeedbackWindow: function(){
		if(this.feedbackWindow != null){			
			this.feedbackWindow.destroy();
			this.feedbackWindow.close();
		}
		
		/*
		this.subjectField = new Ext.form.TextField({
			fieldLabel: 'Subject',
            name: 'subject'
		});
		*/
		
		this.messageField = new Ext.form.TextArea({
			fieldLabel: 'Message text',
            width: '100%',
            name: 'message',
            maxLength: 2000,
            height: 100,
            autoCreate: {tag: 'textArea', type: 'text',  autocomplete: 'off', maxlength: '2000'}
		});
		
		this.sendButton = new Ext.Button({
			xtype: 'button',
			handler: function() {
				var msgToSend = this.messageField.getValue();
				sendMessage({'label': Sbi.config.docLabel, 'msg': msgToSend},'sendFeedback');
       		},
       		scope: this ,
       		text:'Send',
	        width: '100%'
		});

		
		var feedbackWindowPanel = new Ext.form.FormPanel({
			layout: 'form',
			defaults: {
	            xtype: 'textfield'
	        },

	        items: [this.messageField,this.sendButton]
		});
		
		
		this.feedbackWindow = new Ext.Window({
            layout      : 'fit',
	        width		: 700,
	        height		: 170,
            closeAction :'destroy',
            plain       : true,
            title		: 'Send Feedback',
            items       : [feedbackWindowPanel]
		});
		
		this.feedbackWindow.show();
	}
	
	, showShareMapWindow: function(type){
		if(this.shareMapWindow != null){			
			this.shareMapWindow.destroy();
			this.shareMapWindow.close();
		}
		var shareMap = this.getShareMapContent(type);			
		var shareMapPanel = new Ext.Panel({items:[shareMap]});
		
		this.shareMapWindow = new Ext.Window({
            layout      : 'fit',
	        width		: 700,
	        height		: 350,
            closeAction :'destroy',
            plain       : true,
//	            title		: OpenLayers.Lang.translate('sbi.tools.catalogue.measures.window.title'),
            title		: 'Share map',
            items       : [shareMapPanel]
		});
		
		this.shareMapWindow.show();
	}
	
	, onStoreLoad: function(measureCatalogue, options, store, meta) {
		this.geostatistic.thematizer.setData(store, meta);
		this.geostatistic.storeType = 'virtualStore';
		var s = "";
		for(o in options) s += o + ";"
		Sbi.debug("[ControlPanel.onStoreLoad]: options.url = " + options.url);
		Sbi.debug("[ControlPanel.onStoreLoad]: options.params = " + Sbi.toSource(options.params));
		this.geostatistic.storeConfig = {
			url: options.url
			, params: options.params
		};
	}
	
    // private methods
	
	/**
	 * @method
	 * 
	 * Initialize the window that shows the dataset used to thematize the map
	 */
    , initResultSetWin: function() {
		
    	if(!this.store) {
    		return false;
    	}
    	
    	if(this.resultsetWindow == null) {
    		this.store.on('loadexception', function(store, options, response, e) {
    			Sbi.exception.ExceptionHandler.handleFailure(response, options);
    		});
        	
        	this.store.on('metachange', function( store, meta ) {
        		this.updateMeta( meta );
        	}, this);
        	
        	if(!this.cm){
    			this.cm = new Ext.grid.ColumnModel([
    			   new Ext.grid.RowNumberer(),
    		       {
    		       	  header: "Data",
    		          dataIndex: 'data',
    		          width: 75
    		       }
    		    ]);
        	}
        	
        	var pagingBar = null;
        	pagingBar = new Ext.PagingToolbar({
        	        pageSize: this.limit,
        	        store: this.store,
        	        displayInfo: true,
        	        displayMsg: '', //'Displaying topics {0} - {1} of {2}',
        	        emptyMsg: "No topics to display",
        	        
        	        items:[
        	               '->'
        	               , {
        	            	   text: LN('sbi.lookup.Annulla')
        	            	   , listeners: {
        		           			'click': {
        		                  		fn: this.onCancel,
        		                  		scope: this
        		                	} 
        	               		}
        	               } , {
        	            	   text: LN('sbi.lookup.Confirm')
        	            	   , listeners: {
        		           			'click': {
        		                  		fn: this.onOk,
        		                  		scope: this
        		                	} 
        	               		}
        	               }
        	        ]
        	});
      
    		
        	this.grid = new Ext.grid.GridPanel({
    			store: this.store
       	     	, cm: this.cm
       	     	, frame: false
       	     	, border:false  
       	     	, collapsible:false
       	     	, loadMask: true
       	     	, viewConfig: {
       	        	forceFit:false
       	        	, enableRowBody:true
       	        	, showPreview:true
       	     	}	
    	        //, bbar: pagingBar
    		});
    		
    		this.resultsetWindow = new Ext.Window({
    			title: LN('sbi.lookup.Select') ,   
                layout      : 'fit',
                width       : 580,
                height      : 300,
                closeAction :'hide',
                plain       : true,
                items       : [this.grid]
    		});
    	}
    	
    	return true;
	}
    
    , updateMeta: function(meta) {
    	if(this.grid){		
    		meta.fields[0] = new Ext.grid.RowNumberer();
			this.grid.getColumnModel().setConfig(meta.fields);
		} else {
		   alert('ERROR: store meta changed before grid instatiation')
		}
	}
	
	
	
	
	
	
	, extractModel: function() {
		
		var model = null;
		
		var bLayers = new Array();
		var oLayers = new Array();
	
		var mapLayers = this.map.layers.slice();
		
		for (var i = 0; i < mapLayers.length; i++) {
			 var layer = mapLayers[i];
			 
			 var className = '';
	         if (!layer.displayInLayerSwitcher) {
	        	 className = 'x-hidden';
	         }
	         
	         var layerNode = {
	        	 text: layer.name, // TODO: i18n
                 checked: layer.selected, //layer.getVisibility(),
                 cls: className,
                 layerName: layer.name
             };
	         
	         if(layer.isBaseLayer) {
	        	 layerNode.checked = layer.selected;
	        	 bLayers.push(layerNode);
	         } else {
	        	 layerNode.checked = layer.getVisibility();
	        	 oLayers.push(layerNode);
	         }
		}
		
		model = [
		{
			text: 'Background layers',
		    expanded: true,
		    children: bLayers
		}, {
		    text: 'Overlays',
		    expanded: true,
		    children: oLayers
		}
		];
		
		return model;
	}
	
	, getShareMapContent: function(type){
		var toReturn;
		var url = Sbi.config.serviceRegistry.baseUrl.protocol +'://' + Sbi.config.serviceRegistry.baseUrl.host+':'+
		 		  Sbi.config.serviceRegistry.baseUrl.port+'/SpagoBI/servlet/AdapterHTTP?PAGE=LoginPage&NEW_SESSION=TRUE&'+
		 		  'OBJECT_LABEL='+ Sbi.config.docLabel+'&OBJECT_VERSION=' + Sbi.config.docVersion;
		
		 
		if (type=='link'){
			var toReturn = new Ext.form.TextArea({
		  		  fieldLabel: 'Map link:' 
		  			  , name: 'shareText'
			          , width: 690 
					  , xtype : 'textarea'
					  , hideLabel: false
					  , multiline: true
			          , margin: '0 0 0 0'
			          , readOnly: true
			          , labelStyle:'font-weight:bold;'
			          , value: url
			        });
			 
		}else if (type == 'html'){
			var htmlCode = '<iframe name="htmlMap"  width="100%" height="100%"  src="'+url+'"></iframe>';
			var toReturn = new Ext.form.TextArea({
		  		  fieldLabel: 'Map html:' 
		  			  , name: 'shareText'
			          , width: 690 
					  , xtype : 'textarea'
					  , hideLabel: false
					  , multiline: true
			          , margin: '0 0 0 0'
			          , readOnly: true
			          , labelStyle:'font-weight:bold;'
			          , value: htmlCode
			        });
		}else{
			alert('WARNING: Is possible to share only the link url or the html of the map!');
		}
		return toReturn;
	}
    
   
});